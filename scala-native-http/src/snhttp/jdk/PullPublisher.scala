package snhttp.jdk

import java.util.concurrent.Flow
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

import scala.concurrent.Promise
import scala.util.control.NonFatal

import snhttp.jdk.Demand
import scala.util.{Try, Success, Failure}

private class PullSubscription[T](
    private val subscriber: Flow.Subscriber[? >: T],
    private val mayIter: Try[Iterator[T]],
) extends Flow.Subscription {

  private val completed = new AtomicBoolean(false)
  private val cancelled = new AtomicBoolean(false)
  private val running = new AtomicBoolean(false)
  private val demand = Demand()

  private val future = Promise[Unit]()
  // private val pullScheduler = new SimpleScheduler(() => pullTask())

  override def request(n: Long): Unit = {
    require(n >= 0, s"subscription request number ${n} can not be zero or negative")
    if (cancelled.get()) return
    demand.increase(n)
    // pullScheduler.runOrSchedule()
  }

  override def cancel(): Unit =
    cancelled.compareAndSet(false, true)

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

  private[jdk] def runOrSchedule(): Unit =
    if (running.compareAndSet(false, true)) {
      try
        mayIter match {
          case scala.util.Success(iter) =>
            while (demand.get() > 0 && iter.hasNext && !cancelled.get())
              subscriber.onNext(iter.next())
              // demand.decrease()
            if (!iter.hasNext && !cancelled.get()) {
              completed.set(true)
              subscriber.onComplete()
            }
          case scala.util.Failure(t) =>
            completed.set(true)
            subscriber.onError(t)
        }
      catch {
        case NonFatal(t) =>
          completed.set(true)
          subscriber.onError(t)
      } finally
        running.set(false)
    }

}

class PullPublisher[T] private[jdk] (
    private val mayIterable: Try[Iterable[T]],
) extends Flow.Publisher[T] {

  override def subscribe(subscriber: Flow.Subscriber[? >: T]): Unit = {
    val sub = new PullSubscription(subscriber, mayIterable.map(_.iterator))
    subscriber.onSubscribe(sub)
    mayIterable.map(_ => sub.runOrSchedule())
  }
}
object PullPublisher {
  def apply[T](iterable: Iterable[T]): PullPublisher[T] =
    new PullPublisher(Success(iterable))
  def apply[T](error: Throwable): PullPublisher[T] =
    new PullPublisher(Failure(error))
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
