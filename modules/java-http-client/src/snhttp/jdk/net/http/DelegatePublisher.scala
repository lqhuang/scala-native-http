package snhttp.jdk.net.http

import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

import scala.util.control.NonFatal
import scala.util.{Try, Success, Failure}

import scala.concurrent.ExecutionContext.global

private class SimpleSubscription[T](
    subscriber: Subscriber[? >: T],
    iterable: Iterable[T],
) extends Subscription:

  private val errored = new AtomicBoolean(false)
  private val completed = new AtomicBoolean(false)
  private val cancelled = new AtomicBoolean(false)

  private val running = new AtomicBoolean(false)

  private val iter = iterable.iterator.buffered

  /**
   * Notes from JDK docs:
   *
   * If `n` is less than or equal to zero, the Subscriber will receive an `onError` signal with an
   * `IllegalArgumentException` argument. Otherwise, the Subscriber will receive up to `n`
   * additional `onNext` invocations (or fewer if terminated).
   */
  override def request(n: Long): Unit =
    if (n < 0) subscriber.onError(IllegalArgumentException(s"n must be positive, but was $n"))
    else
      var demand = n
      while demand > 0
        && iter.hasNext
        && cancelled.get() == false
      do
        try
          val next = iter.next()
          subscriber.onNext(next)
          demand -= 1
        catch {
          case NonFatal(t) =>
            if (errored.compareAndSet(false, true))
              subscriber.onError(t)
            demand = 0
        }

  override def cancel(): Unit =
    if (!isCancellable)
      cancelled.compareAndExchange(false, true): Unit

  def isCancellable: Boolean =
    !(errored.get() || cancelled.get() || completed.get())

end SimpleSubscription

class DelegatePublisher[T] private (
    // private val iter: Iterable[T],
    // private val error: Throwable,
    iterable: Try[Iterable[T]],
) extends Publisher[T]:

  override def subscribe(subscriber: Subscriber[? >: T]): Unit =
    iterable match
      case Failure(exc) =>
        subscriber.onError(exc)
      case Success(it) =>
        val sub = SimpleSubscription[T](subscriber, it)
        subscriber.onSubscribe(sub)

object DelegatePublisher:

  def apply[T](iterable: Iterable[T]): DelegatePublisher[T] =
    new DelegatePublisher(Success(iterable))

  def apply[T](t: Throwable): DelegatePublisher[T] =
    new DelegatePublisher(Failure(t))

end DelegatePublisher
