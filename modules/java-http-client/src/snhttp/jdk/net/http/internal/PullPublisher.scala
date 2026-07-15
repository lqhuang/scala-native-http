package snhttp.jdk.net.http.internal

import java.io.{IOException, UncheckedIOException}
import java.util.Objects.requireNonNull
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

import scala.util.control.NonFatal

/**
 * Pull-based Single publisher single subscribers. Also known as SPSC (Single Producer Single
 * Consumers).
 *
 * Produces items only when requested by the subscriber. It is designed to be used in the
 * implementation of HttpRequest.BodyPublisher.
 *
 * TODO:
 *   1. Imperfect implementation, needs more testing and improvements.
 *   2. Failed with concurrent requests / cancellations.
 */
private[snhttp] final class PullPublisher[T](
    private[PullPublisher] val iterator: Iterator[T],
    private[PullPublisher] val closeHandler: () => Unit,
) extends Publisher[T]
    with AutoCloseable:

  requireNonNull(iterator)

  import PullPublisher.DelegateSubscription

  def this(iterator: Iterator[T]) = this(iterator, () => ())

  private val subscribed = new AtomicBoolean(false)
  private val closed = new AtomicBoolean(false)

  override def subscribe(subscriber: Subscriber[? >: T]): Unit = {
    requireNonNull(subscriber, "subscriber cannot be null")

    if (!subscribed.compareAndExchange(false, true)) {
      val subscription = DelegateSubscription[T](this, subscriber)
      subscriber.onSubscribe(subscription)
    } else {
      subscriber.onSubscribe(new Subscription {
        override def request(n: Long): Unit = ()
        override def cancel(): Unit = ()
      })
      subscriber.onError(new IllegalStateException("Only one subscriber allowed"))
    }
  }

  def close(): Unit =
    if (!closed.compareAndExchange(false, true))
      closeHandler()

object PullPublisher:

  private[PullPublisher] final class DelegateSubscription[T](
      publisher: PullPublisher[T],
      subscriber: Subscriber[? >: T],
  ) extends Subscription:

    private val cancelled = new AtomicBoolean(false)
    private val terminated = new AtomicBoolean(false)

    private val scheduler = new SequentialScheduler(maxTasksPerTurn = 4)
    private val iterator: Iterator[T] = publisher.iterator

    private val demand = new AtomicLong(0L)
    private val draining = new AtomicBoolean(false) // indicates whether a drain turn is in progress

    /**
     * Notes from JDK docs:
     *
     * If `n` is less than or equal to zero, the Subscriber will receive an `onError` signal with an
     * `IllegalArgumentException` argument. Otherwise, the Subscriber will receive up to `n`
     * additional `onNext` invocations (or fewer if terminated).
     */
    override def request(n: Long): Unit =
      if (n <= 0)
        signalError(new IllegalArgumentException(s"Non-positive request: ${n}"))
      scheduler.execute { () =>
        demand.updateAndGet { curr =>
          val residual = Long.MaxValue - curr
          if residual < n then Long.MaxValue else curr + n
        }
        scheduleDrain()
      }

    override def cancel(): Unit =
      if (!cancelled.compareAndExchange(false, true))
        scheduler.execute(() => publisher.close())

    /*
     * Non-public methods
     */

    private def scheduleDrain(): Unit =
      if (!draining.compareAndExchange(false, true) && !cancelled.get() && !terminated.get())
        scheduler.execute(() => drainDemand())

    private def drainDemand(): Unit = {
      try
        while //
          !cancelled.get() && !terminated.get()
          && demand.get() > 0L
        do
          if iterator.hasNext
          then {
            val item = iterator.next()
            demand.decrementAndGet()
            subscriber.onNext(item)
          } //
          else //
            signalComplete()

        if (!cancelled.get() && !terminated.get() && !iterator.hasNext)
          signalComplete()
      catch {
        case exc: IOException => signalError(UncheckedIOException(exc))
        case NonFatal(exc)    => signalError(exc)
      }

      draining.compareAndExchange(true, false): Unit
    }

    private inline def signalError(exc: Throwable): Unit =
      if (!terminated.compareAndExchange(false, true))
        subscriber.onError(exc)
        publisher.close()

    private inline def signalComplete(): Unit =
      if (!terminated.compareAndExchange(false, true))
        subscriber.onComplete()
        publisher.close()

  end DelegateSubscription

end PullPublisher
