package snhttp.jdk.net.http.internal

import java.util.Objects.requireNonNull
import java.util.concurrent.{ConcurrentLinkedQueue, Executor, ForkJoinPool}
import java.util.concurrent.atomic.AtomicBoolean

import scala.util.control.NonFatal

/**
 * Runs submitted tasks in FIFO order without owning a thread.
 *
 * A drain turn is deliberately bounded. If work remains, it is submitted to the backing executor
 * again so unrelated work can use the worker in between turns.
 */
private[snhttp] final class SequentialScheduler(
    executor: Executor = ForkJoinPool.commonPool(),
    maxTasksPerTurn: Int = 64,
    maxNanosPerTurn: Long = 1_000_000L,
):

  requireNonNull(executor, "executor cannot be null")
  require(maxTasksPerTurn > 0, "maxTasksPerTurn must be positive")
  require(maxNanosPerTurn > 0L, "maxNanosPerTurn must be positive")

  private val tasks = new ConcurrentLinkedQueue[Runnable]()
  private val drainScheduled = new AtomicBoolean(false)
  private val drainTask: Runnable = () => drainTurn()

  def execute(task: Runnable): Unit = {
    requireNonNull(task, "task cannot be null")
    tasks.offer(task): Unit
    scheduleDrain()
  }

  private def scheduleDrain(): Unit =
    if (!drainScheduled.compareAndExchange(false, true))
      try executor.execute(drainTask)
      catch {
        case exc: Throwable =>
          drainScheduled.set(false)
          throw exc
      }

  private def drainTurn(): Unit =
    val startedAt = System.nanoTime()
    var executed = 0
    var continue = true

    try
      while continue
      do
        if executed >= maxTasksPerTurn
          || (executed > 0 && (System.nanoTime() - startedAt >= maxNanosPerTurn))
        then //
          continue = false
        else {
          val task = tasks.poll()
          if task == null
          then continue = false
          else {
            try task.run()
            catch case NonFatal(exc) => reportFailure(exc)
            executed += 1
          }
        }
    finally {
      drainScheduled.set(false)
      if (!tasks.isEmpty())
        scheduleDrain()
    }

  private def reportFailure(exc: Throwable): Unit = {
    val thread = Thread.currentThread()
    val handler = thread.getUncaughtExceptionHandler()
    if (handler != null)
      handler.uncaughtException(thread, exc)
  }

end SequentialScheduler
