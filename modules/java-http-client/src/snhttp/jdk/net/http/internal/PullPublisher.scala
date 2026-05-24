package snhttp.jdk.net.http.internal

import java.util.NoSuchElementException
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.{Future, Executor, ForkJoinPool}
import java.util.Objects.requireNonNull

import scala.collection.BufferedIterator
import scala.util.control.NonFatal
import scala.scalanative.annotation.alwaysinline

import scala.collection.mutable.HashMap
import java.util.concurrent.atomic.AtomicInteger
import scala.util.boundary
import scala.util.boundary.break

/**
 * Pull-based Single publisher single subscribers. Also known as SPSC (Single Producer Single
 * Consumers).
 *
 * Produces items only when requested by the subscriber. It is designed to be used in the
 * implementation of HttpRequest.BodyPublisher.
 *
 * TODO: imperfect implementation, needs more testing and improvements.
 */
private[snhttp] class PullPublisher[T] private (
    private[PullPublisher] val iterable: Iterable[T],
    private[PullPublisher] val executor: Executor,
) extends Publisher[T]
    with AutoCloseable:

  import PullPublisher.DelegateSubscription

  /** Set true on first call to subscribe, to initialize possible owner */
  private val subscribed = new AtomicBoolean(false)

  /** Run status, updated only within locks */
  @volatile
  private var closed = false

  /** If non-null, the exception in closeExceptionally */
  @volatile
  private var error: Throwable = _

  /** A lock for operations that need to be synchronous */
  private final val lock = new ReentrantLock()

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
    if (!closed) {
      lock.lock()
      try closed = true
      finally lock.unlock()
    }

  private[PullPublisher] def closeExceptionally(exc: Throwable): Unit =
    if (!closed) {
      lock.lock()
      try { closed = true; error = exc }
      finally lock.unlock()
    }

object PullPublisher:

  def apply[T](iterable: Iterable[T]): PullPublisher[T] =
    new PullPublisher(iterable, ASYNC_POOL)

  def apply[T](iterable: Iterable[T], executor: Executor): PullPublisher[T] =
    new PullPublisher(iterable, executor)

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

  /** ctl bit values */
  private[PullPublisher] object Ctl:
    val ACTIVE = 0x01
    val CLOSED = 0x02
    val REQS = 0x04
    val ERROR = 0x08
    val COMPLETE = 0x16
    val CANCELLED = 0x32
  end Ctl

  private[PullPublisher] final class DelegateSubscription[T](
      publisher: PullPublisher[T],
      subscriber: Subscriber[? >: T],
  ) extends Subscription:

    private var error: Throwable = _
    private val ctl = new AtomicInteger(0)
    private val demand = new AtomicLong(0)
    private val buffIter: BufferedIterator[T] = publisher.iterable.iterator.buffered

    private lazy val task = new Runnable {
      override def run(): Unit = {
        if ((ctl.get() & Ctl.CLOSED & Ctl.CANCELLED) > 0)
          return ()

        if (error != null) {
          if ((ctl.getAndUpdate(c => c | Ctl.ERROR | Ctl.CLOSED) & Ctl.CLOSED) != 0)
            subscriber.onError(error)
          return ()
        }

        while (ctl.get() & Ctl.CLOSED) != Ctl.CLOSED
          && (ctl.get() & Ctl.CANCELLED) != Ctl.CANCELLED
          && buffIter.hasNext
          && (demand.get() > 0)
        do
          try {
            demand.decrementAndGet(): Unit
            subscriber.onNext(buffIter.next())
          } catch case NonFatal(exc) => onError(exc): Unit

        if (error == null && !buffIter.hasNext)
          onComplete(): Unit
      }
    }

    /**
     * Notes from JDK docs:
     *
     * If `n` is less than or equal to zero, the Subscriber will receive an `onError` signal with an
     * `IllegalArgumentException` argument. Otherwise, the Subscriber will receive up to `n`
     * additional `onNext` invocations (or fewer if terminated).
     */
    override def request(n: Long): Unit = {
      if (
        (ctl.get() & Ctl.CANCELLED) == Ctl.CANCELLED
        || (ctl.get() & Ctl.CLOSED) == Ctl.CLOSED
      ) return ()

      if n < 0 //
      then //
        if ((ctl.getAndUpdate(c => c | Ctl.ERROR) & Ctl.ERROR) != 0)
          error = new IllegalArgumentException(s"Non-positive request: ${n}")
      else //
        demand.addAndGet(n): Unit

      if ((ctl.get() | Ctl.ACTIVE) != Ctl.ACTIVE) {
        ctl.updateAndGet(c => c | Ctl.ACTIVE): Unit
        println(s"Scheduling task with demand ${demand.get()}")
        publisher.executor.execute(task)
      }
    }

    override def cancel(): Unit =
      ctl.getAndUpdate(c => c | Ctl.CANCELLED): Unit

    //
    // Non-public methods
    //

    private inline def onError(exc: Throwable): Unit =
      if (ctl.getAndUpdate(c => c | Ctl.ERROR | Ctl.CLOSED) != Ctl.CLOSED)
        publisher.closeExceptionally(exc)
        subscriber.onError(exc)

    private inline def onComplete(): Unit =
      if (ctl.getAndUpdate(c => c | Ctl.COMPLETE | Ctl.CLOSED) != Ctl.CLOSED)
        publisher.close()
        subscriber.onComplete()

    // private[PullPublisher] def onSubscribe(): Unit =
    //   if (ctl.getAndUpdate(c => c | Ctl.SUBSCRIBED) != 0)
    //     subscriber.onSubscribe(this)

  end DelegateSubscription

end PullPublisher
