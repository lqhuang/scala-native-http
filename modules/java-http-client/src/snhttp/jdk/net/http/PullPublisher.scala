package snhttp.jdk.net.http

import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

import scala.util.control.NonFatal
import scala.util.{Try, Success, Failure}

private class PullSubscription[T](
    private val subscriber: Subscriber[? >: T],
    private val iter: Try[Iterable[T]],
) extends Subscription {

  private val completed = new AtomicBoolean(false)
  private val cancelled = new AtomicBoolean(false)
  private val running = new AtomicBoolean(false)
  private val demand = Demand()

  // private val pullScheduler = new SimpleScheduler(() => pullTask())

  override def request(n: Long): Unit = {
    require(n >= 0, s"subscription request number ${n} can not be zero or negative")

    if (cancelled.getOpaque()) return
    demand.increase(n): Unit
    // pullScheduler.runOrSchedule()
  }

  override def cancel(): Unit =
    cancelled.compareAndSet(false, true): Unit

  // private def pullTask(): Unit = {
  //   if (completed.get() || cancelled.get()) return

  //   val t = error
  //   if t != null then
  //     completed.set(true)
  //     pullScheduler.stop()
  //     subscriber.onError(t)
  //     return

  //   var continue = true
  //   while continue && decDemand() && !cancelled.get() do
  //     try
  //       if iter == null || !iter.hasNext
  //       then continue = false
  //       else
  //         val next = iter.next()
  //         subscriber.onNext(next)
  //     catch {
  //       case NonFatal(t1) =>
  //         completed.compareAndSet(false, true)
  //         pullScheduler.stop()
  //         subscriber.onError(t1)
  //         return
  //     }

  //   if iter != null && !iter.hasNext && !cancelled.get() then
  //     completed.compareAndSet(false, true)
  //     pullScheduler.stop()
  //     subscriber.onComplete()
  // }

  // private def decDemand(): Boolean =
  //   demand.updateAndGet(current => if current > 0 then current - 1 else 0) > 0

  private[jdk] def runOrSchedule(): Unit = ???
  //   if (completed.get() || cancelled.get()) return
  //   if (throwable != null) {
  //     completed.compareAndSet(false, true)
  //     subscriber.onError(throwable)
  //     return
  //   }

  //   if (running.compareAndSet(false, true)) {
  //     try
  //       while demand.get() > 0 && iterator.hasNext && !cancelled.get() do {
  //         subscriber.onNext(iterator.next())
  //         // demand.decrease()
  //         if (!iterator.hasNext && !cancelled.get()) {
  //           completed.set(true)
  //           subscriber.onComplete()
  //         }
  //       }
  //     catch {
  //       case NonFatal(t) =>
  //         completed.set(true)
  //         subscriber.onError(t)
  //     } finally
  //       running.set(false)
  //   }

}

class PullPublisher[T] private[jdk] (
    // private val iter: Iterable[T],
    // private val error: Throwable,
    private val iter: Try[Iterable[T]],
) extends Publisher[T] {
  override def subscribe(subscriber: Subscriber[? >: T]): Unit =
    val sub = PullSubscription[T](subscriber, iter)
    subscriber.onSubscribe(sub)
    iter.map(_ => sub.runOrSchedule()): Unit
}
object PullPublisher {
  def apply[T](iterable: Iterable[T]): PullPublisher[T] =
    new PullPublisher(Success(iterable))
    // new PullPublisher(iterable, null)
  def apply[T](t: Throwable): PullPublisher[T] =
    // new PullPublisher(null, t)
    new PullPublisher(Failure(t))
}

private class SimpleScheduler(task: () => Unit) {
  private val running = new AtomicBoolean(false)
  @volatile private var stopped = false

  def runOrSchedule(): Unit =
    if !stopped && running.compareAndSet(false, true) then
      try
        task()
      finally
        running.set(false)

  def stop(): Unit =
    stopped = true
}
