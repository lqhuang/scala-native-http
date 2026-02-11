package snhttp.jdk.net.http.internal

import java.util.NoSuchElementException
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import java.util.concurrent.{Future, Executor, ForkJoinPool}
import java.util.Objects.requireNonNull

import scala.util.{Try, Success, Failure}
import scala.util.control.NonFatal

import scala.collection.mutable.HashMap
import java.util.concurrent.atomic.AtomicInteger
import scala.util.boundary
import scala.util.boundary.break

/**
 * Single publisher multiple subscribers. Sometimes, also known as SPMC (Single Producer Multiple
 * Consumers).
 *
 * TODO: imperfect implementation, needs more testing and improvements.
 */
class DelegatePublisher[T] private (
    iterable: Iterable[T],
    executor: Executor,
) extends Publisher[T]
    with AutoCloseable:

  ??? // NOT finised yet

  import DelegatePublisher.DelegateSubscription

  private val bufferedIter = iterable.iterator.buffered

  // Store of active clients (subscriptions) and its current index in the buffer
  private val clients = HashMap.empty[DelegateSubscription[T], AtomicInteger]

  // Store of items already produced
  private var items = List.empty[T]

  private val count = new AtomicInteger(0)

  /** Run status, updated only within locks */
  @volatile private var closed = false

  /** If non-null, the exception in closeExceptionally */
  @volatile private var closedException: Throwable = null

  override def subscribe(subscriber: Subscriber[? >: T]): Unit = {
    requireNonNull(subscriber, "subscriber cannot be null")
    val sub = DelegateSubscription[T](this, subscriber, executor)

    synchronized {
      subscriber.onSubscribe(sub)

      if isSubscribed(subscriber) then
        subscriber.onError(IllegalStateException("Subscriber has already subscribed"))
      else if closedException != null then // force line break (fmt)
        subscriber.onError(closedException)
      else if closed then // force line break (fmt)
        subscriber.onComplete()
      else // force line break (fmt)
        clients.addOne(sub, new AtomicInteger(count.get())): Unit
        ()
    }
  }

  def close(): Unit =
    if (!closed) {
      synchronized {
        closed = true
        closedException = null
        clients.foreach(kv => kv._1.onComplete())
        clients.clear()
      }
    }

  //
  // Non standard methods
  //

  private[DelegatePublisher] def hasNext: Boolean =
    bufferedIter.hasNext

  // We assume that user will call hasNext before `next()`
  // to avoid NoSuchElementException
  private[DelegatePublisher] def next(slot: DelegateSubscription[T]): T = {
    val currPos = clients
      .getOrElse(
        slot,
        throw new RuntimeException("Unknown subscription, which should be impossible"),
      )
    val currPosValue = currPos.get()

    if currPosValue == count.get()
    then {
      try
        val item = bufferedIter.next()
        count.getAndIncrement()
        items = items :+ item
        item
      catch
        case NonFatal(exc) =>
          closeExceptionally(exc): Unit
          throw exc
    } //
    else {
      val item = items(currPosValue)
      currPos.getAndIncrement(): Unit
      item
    }
  }

  private def isSubscribed(subscriber: Subscriber[? >: T]): Boolean =
    requireNonNull(subscriber, "subscriber cannot be null")
    if closed
    then false
    else clients.exists(kv => kv._1.subscriber.equals(subscriber))

  private def closeExceptionally(t: Throwable): Unit =
    if (!closed) {
      synchronized {
        closed = true
        closedException = t
        clients.foreach(kv => kv._1.onError(t))
        clients.clear()
      }
    }

object DelegatePublisher:

  def apply[T](iterable: Iterable[T]): DelegatePublisher[T] =
    new DelegatePublisher(iterable, ASYNC_POOL)

  def apply[T](iterable: Iterable[T], executor: Executor): DelegatePublisher[T] =
    new DelegatePublisher(iterable, executor)

  /**
   * Default executor -- ForkJoinPool.commonPool() unless it cannot support parallelism.
   */
  private val ASYNC_POOL: Executor =
    if (ForkJoinPool.getCommonPoolParallelism() > 1)
      ForkJoinPool.commonPool()
    else
      new ThreadPerTaskExecutor()

  /** Fallback if ForkJoinPool.commonPool() cannot support parallelism */
  private[DelegatePublisher] final class ThreadPerTaskExecutor extends Executor:
    override def execute(r: Runnable): Unit = {
      requireNonNull(r)
      new Thread(r).start()
    }
  end ThreadPerTaskExecutor

  /** ctl bit values */
  private[DelegatePublisher] object CtlFlag:
    val CLOSED = 0x01 // if set, other bits ignored
    val ACTIVE = 0x02 // keep-alive for task
    val REQS = 0x04 // (possibly) nonzero demand
    val ERROR = 0x08 // issues onError when noticed
    val COMPLETE = 0x10 // issues onComplete when done
    val RUN = 0x20 // task is or will be running
    val OPEN = 0x40 // true after subscribe
  end CtlFlag

  private[DelegatePublisher] final class DelegateSubscription[T](
      pub: DelegatePublisher[T],
      val subscriber: Subscriber[? >: T],
      executor: Executor,
  ) extends Subscription:

    private val ctl = new AtomicInteger(0)

    /**
     * Notes from JDK docs:
     *
     * If `n` is less than or equal to zero, the Subscriber will receive an `onError` signal with an
     * `IllegalArgumentException` argument. Otherwise, the Subscriber will receive up to `n`
     * additional `onNext` invocations (or fewer if terminated).
     */
    override def request(n: Long): Unit =
      if ((ctl.get() & CtlFlag.CLOSED) == CtlFlag.CLOSED) return ()

      if n < 0 then
        executor.execute { () =>
          if ((ctl.getAndUpdate(c => c | CtlFlag.ERROR | CtlFlag.CLOSED) & CtlFlag.CLOSED) != 0)
            subscriber.onError(new IllegalArgumentException(s"n must be positive, but get ${n}"))
        }
      else if !pub.hasNext then
        executor.execute { () =>
          if ((ctl.getAndUpdate(c => c | CtlFlag.COMPLETE | CtlFlag.CLOSED) & CtlFlag.CLOSED) != 0)
            subscriber.onComplete()
        }
      else
        executor.execute { () =>
          var _demand = n

          val doneWithError = boundary {
            while !((ctl.get() & CtlFlag.CLOSED) == 0) && _demand > 0 && pub.hasNext do
              try
                subscriber.onNext(pub.next(this))
                _demand -= 1
              catch
                case NonFatal(exc) =>
                  if (
                    (ctl.getAndUpdate(c => c | CtlFlag.ERROR | CtlFlag.CLOSED)
                      & CtlFlag.CLOSED) != 0
                  )
                    subscriber.onError(exc)
                  break(true)
            false
          }

          if (_demand != 0 && !doneWithError && !pub.hasNext)
            if (
              (ctl.getAndUpdate(c => c | CtlFlag.COMPLETE | CtlFlag.CLOSED)
                & CtlFlag.CLOSED) != 0
            )
              subscriber.onComplete()
        }

    override def cancel(): Unit =
      ctl.getAndUpdate(c => c | CtlFlag.CLOSED): Unit

    override def hashCode(): Int =
      subscriber.hashCode()

    //
    // Non-public methods
    //
    private[DelegatePublisher] def onComplete(): Unit =
      if ((ctl.getAndUpdate(c => c | CtlFlag.CLOSED) & CtlFlag.CLOSED) != 0)
        subscriber.onComplete()

    private[DelegatePublisher] def onError(t: Throwable): Unit =
      if (ctl.getAndUpdate(c => c | CtlFlag.ERROR | CtlFlag.CLOSED) != 0)
        subscriber.onError(t)

  end DelegateSubscription

end DelegatePublisher
