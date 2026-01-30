package snhttp.jdk.net.http.internal

import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import java.util.concurrent.{Future, Executor, ForkJoinPool}

import scala.util.{Try, Success, Failure}
import scala.util.control.NonFatal

class DelegatePublisher[T] private (
    // private val iter: Iterable[T],
    // private val error: Throwable,
    iterable: Try[Iterable[T]],
    executor: Executor,
) extends Publisher[T]
    with AutoCloseable:

  import DelegatePublisher.SimpleSubscription

  private val subscribed = new AtomicBoolean(false)

  override def subscribe(subscriber: Subscriber[? >: T]): Unit =
    iterable match
      case Failure(exc) =>
        subscriber.onError(exc)
      case Success(it) =>
        if !subscribed.compareAndSet(false, true)
        then
          subscriber.onError(
            IllegalStateException("This Publisher allows only a single Subscriber"),
          )
        else
          val sub = SimpleSubscription[T](subscriber, it, executor)
          subscriber.onSubscribe(sub)

object DelegatePublisher:

  def apply[T](iterable: Iterable[T]): DelegatePublisher[T] =
    new DelegatePublisher(Success(iterable), ASYNC_POOL)

  def apply[T](t: Throwable): DelegatePublisher[T] =
    new DelegatePublisher(Failure(t), ASYNC_POOL)

  def apply[T](iterable: Try[Iterable[T]], executor: Executor): DelegatePublisher[T] =
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

  private[DelegatePublisher] class SimpleSubscription[T](
      subscriber: Subscriber[? >: T],
      iterable: Iterable[T],
      executor: ExecutorService,
  ) extends Subscription:

    private val bufferedIter = iterable.iterator.buffered
    private val errored = new AtomicBoolean(false)
    private var future: Future[Unit] = null

    /**
     * Notes from JDK docs:
     *
     * If `n` is less than or equal to zero, the Subscriber will receive an `onError` signal with an
     * `IllegalArgumentException` argument. Otherwise, the Subscriber will receive up to `n`
     * additional `onNext` invocations (or fewer if terminated).
     */
    override def request(n: Long): Unit =
      if (futureIsDone) return ()

      if n < 0
      then
        executor.execute(() =>
          errored.set(true)
          subscriber.onError(IllegalArgumentException(s"n must be positive, but get ${n}")),
        )
      else
        future = executor.submit { () =>
          val demand = new AtomicLong(n)
          while demand.get() > 0 && bufferedIter.hasNext && !futureIsCancelled
          do
            try
              val next = bufferedIter.next()
              subscriber.onNext(next)
              demand.decrementAndGet(): Unit
            catch
              case NonFatal(exc) =>
                if (errored.compareAndSet(false, true))
                  subscriber.onError(exc)
                demand.set(0)
        }

    override def cancel(): Unit =
      if (isCancellable)
        if (future != null)
          future.cancel(false): Unit

    private def futureIsCancelled: Boolean =
      future != null && future.isCancelled()

    private def futureIsDone: Boolean =
      future != null && future.isDone()

    private def isCancellable: Boolean =
      !(errored.get() || futureIsDone)

  end SimpleSubscription

end DelegatePublisher
