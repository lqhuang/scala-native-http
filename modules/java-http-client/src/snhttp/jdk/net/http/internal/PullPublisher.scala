package snhttp.jdk.net.http.internal

import java.util.Objects.requireNonNull
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicInteger, AtomicBoolean, AtomicLong}
import java.util.concurrent.{Executor, ForkJoinPool}

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
 *   2. Failed with concurrent requests / cancellations
 */
private[snhttp] final class PullPublisher[T] private (
    private[PullPublisher] val iterable: Iterable[T],
    private[PullPublisher] val executor: Executor,
    private[PullPublisher] val closeHandler: () => Unit,
) extends Publisher[T]
    with AutoCloseable:

  import PullPublisher.DelegateSubscription

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

  def apply[T](iterable: Iterable[T]): PullPublisher[T] =
    new PullPublisher(iterable, ASYNC_POOL, () => ())

  def apply[T](iterable: Iterable[T], executor: Executor): PullPublisher[T] =
    new PullPublisher(iterable, executor, () => ())

  def apply[T](
      iterable: Iterable[T],
      closeHandler: () => Unit,
  ): PullPublisher[T] =
    new PullPublisher(iterable, ASYNC_POOL, closeHandler)

  def apply[T](
      iterable: Iterable[T],
      executor: Executor,
      closeHandler: () => Unit,
  ): PullPublisher[T] =
    new PullPublisher(iterable, executor, closeHandler)

  /**
   * Default executor -- ForkJoinPool.commonPool() unless it cannot support parallelism.
   */
  private val ASYNC_POOL: Executor =
    if (ForkJoinPool.getCommonPoolParallelism() > 1)
      ForkJoinPool.commonPool()
    else
      new ThreadPerTaskExecutor()

  /** Fallback if ForkJoinPool.commonPool() cannot support parallelism */
  private[PullPublisher] final class ThreadPerTaskExecutor extends Executor:
    override def execute(r: Runnable): Unit = {
      requireNonNull(r)
      new Thread(r).start()
    }
  end ThreadPerTaskExecutor

  private[PullPublisher] final class DelegateSubscription[T](
      publisher: PullPublisher[T],
      subscriber: Subscriber[? >: T],
  ) extends Subscription:

    private val cancelled = new AtomicBoolean(false)
    private val terminated = new AtomicBoolean(false)
    private val demand = new AtomicLong(0L)
    private val iterator: Iterator[T] = publisher.iterable.iterator

    /**
     * Notes from JDK docs:
     *
     * If `n` is less than or equal to zero, the Subscriber will receive an `onError` signal with an
     * `IllegalArgumentException` argument. Otherwise, the Subscriber will receive up to `n`
     * additional `onNext` invocations (or fewer if terminated).
     */
    override def request(n: Long): Unit =
      if n <= 0
      then //
        signalError(new IllegalArgumentException(s"Non-positive request: ${n}"))
      else {
        if (!terminated.get() && demand.addAndGet(n) > 0)
          publisher.executor.execute { () =>
            while //
              !cancelled.get() && !terminated.get() && demand.get() > 0
            do
              if iterator.hasNext
              then {
                try {
                  subscriber.onNext(iterator.next())
                  demand.decrementAndGet(): Unit
                } //
                catch {
                  case NonFatal(exc) => signalError(exc)
                }
              } //
              else //
                signalComplete()
          }
      }

    override def cancel(): Unit =
      if (!cancelled.compareAndExchange(false, true))
        publisher.close()

    /*
     * Non-public methods
     */

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
