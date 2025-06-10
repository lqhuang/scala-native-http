package snhttp.jdk

import java.util.concurrent.Flow
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

import scala.util.control.NonFatal
import scala.concurrent.Promise
import scala.concurrent.Promise

class PullPublisher[T] private (
    private val iterable: Iterable[T],
    private val throwable: Throwable,
) extends Flow.Publisher[T] {
  def this(iterable: Iterable[T]) = this(iterable, null)

  override def subscribe(subscriber: Flow.Subscriber[? >: T]): Unit = {
    val sub =
      if throwable != null
      then new Subscription(subscriber, null, throwable)
      else new Subscription(subscriber, iterable.iterator, null)

    subscriber.onSubscribe(sub)
    if throwable != null then sub.pullScheduler.runOrSchedule()
  }

  private class Subscription(
      private val subscriber: Flow.Subscriber[? >: T],
      private val iter: Iterator[T],
      @volatile private var error: Throwable,
  ) extends Flow.Subscription {

    @volatile private var completed: Boolean = false
    @volatile private var cancelled: Boolean = false
    private val demand = new AtomicLong(0)
    private val running = new AtomicBoolean(false)

    val future = Promise[Unit]()

    val pullScheduler = new SimpleScheduler(() => pullTask())

    private def pullTask(): Unit = {
      if completed || cancelled then return

      val t = error
      if t != null then
        completed = true
        pullScheduler.stop()
        subscriber.onError(t)
        return

      var continue = true
      while continue && decDemand() && !cancelled do
        try
          if iter == null || !iter.hasNext
          then continue = false
          else
            val next = iter.next()
            subscriber.onNext(next)
        catch {
          case NonFatal(t1) =>
            completed = true
            pullScheduler.stop()
            subscriber.onError(t1)
            return
        }

      if iter != null && !iter.hasNext && !cancelled then
        completed = true
        pullScheduler.stop()
        subscriber.onComplete()
    }

    private def decDemand(): Boolean =
      demand.updateAndGet(current => if current > 0 then current - 1 else 0) > 0

    override def request(n: Long): Unit = {
      if cancelled then return

      if n <= 0
      then error = new IllegalArgumentException(s"non-positive subscription request: $n")
      else demand.addAndGet(n)

      pullScheduler.runOrSchedule()
    }

    override def cancel(): Unit =
      cancelled = true
  }
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
