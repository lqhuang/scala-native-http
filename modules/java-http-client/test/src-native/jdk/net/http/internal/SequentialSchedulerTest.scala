package snhttp.test.jdk.net.http.internal

import java.util.concurrent.{Executor, RejectedExecutionException}

import scala.collection.mutable.{ArrayBuffer, Queue}

import utest.{TestSuite, Tests, assert, assertThrows, test}

import _root_.snhttp.jdk.net.http.internal.SequentialScheduler

class SequentialSchedulerTest extends TestSuite:

  private final class MockExecutor extends Executor:

    private val tasks = Queue.empty[Runnable]

    override def execute(task: Runnable): Unit = tasks.enqueue(task)

    inline def pendingTasks: Int = tasks.size

    inline def runNext(): Unit = tasks.dequeue().run()

  end MockExecutor

  def tests = Tests:

    test("should not block current thread") {
      val executor = new MockExecutor()
      val scheduler = new SequentialScheduler(executor)
      val executionOrder = ArrayBuffer.empty[Int]

      scheduler.execute(() => executionOrder += 1)
      scheduler.execute(() => executionOrder += 2)

      assert(executionOrder.isEmpty)
      assert(executor.pendingTasks == 1)

      executor.runNext()

      assert(executionOrder == Seq(1, 2))
      assert(executor.pendingTasks == 0)
    }

    test("runs tasks in FIFO order without executing them inline") {
      val executor = new MockExecutor()
      val scheduler = new SequentialScheduler(executor)
      val executionOrder = ArrayBuffer.empty[Int]

      scheduler.execute { () =>
        executionOrder += 1
        scheduler.execute(() => executionOrder += 4)
      }
      scheduler.execute(() => executionOrder += 2)
      scheduler.execute(() => executionOrder += 3)

      assert(executionOrder.isEmpty)
      assert(executor.pendingTasks == 1)

      executor.runNext()

      assert(executionOrder == Seq(1, 2, 3, 4))
      assert(executor.pendingTasks == 0)
    }

    test("resubmits work after reaching the task limit for a drain turn") {
      val executor = new MockExecutor()
      val scheduler = new SequentialScheduler(
        executor,
        maxTasksPerTurn = 2,
        maxNanosPerTurn = Long.MaxValue,
      )
      val executionOrder = ArrayBuffer.empty[Int]

      (1 to 5).foreach(value => scheduler.execute(() => executionOrder += value))

      executor.runNext()
      assert(executionOrder == Seq(1, 2))
      assert(executor.pendingTasks == 1)

      executor.runNext()
      assert(executionOrder == Seq(1, 2, 3, 4))
      assert(executor.pendingTasks == 1)

      executor.runNext()
      assert(executionOrder == Seq(1, 2, 3, 4, 5))
      assert(executor.pendingTasks == 0)
    }

    test("resubmits work after reaching the time limit for a drain turn") {
      val executor = new MockExecutor()
      val scheduler = new SequentialScheduler(
        executor,
        maxTasksPerTurn = Int.MaxValue,
        maxNanosPerTurn = 1L,
      )
      val executionOrder = ArrayBuffer.empty[Int]

      scheduler.execute { () =>
        executionOrder += 1
        Thread.sleep(2L)
      }
      scheduler.execute(() => executionOrder += 2)

      executor.runNext()
      assert(executionOrder == Seq(1))
      assert(executor.pendingTasks == 1)

      executor.runNext()
      assert(executionOrder == Seq(1, 2))
      assert(executor.pendingTasks == 0)
    }

    test("reports task failures and continues draining queued tasks") {
      val executor = new MockExecutor()
      val scheduler = new SequentialScheduler(executor)
      val currentThread = Thread.currentThread()
      val previousHandler = currentThread.getUncaughtExceptionHandler()
      val failure = new RuntimeException("task failed")
      var reportedFailure = Option.empty[Throwable]
      var ranAfterFailure = false
      val handler = new Thread.UncaughtExceptionHandler:
        override def uncaughtException(thread: Thread, exception: Throwable): Unit =
          reportedFailure = Some(exception)

      currentThread.setUncaughtExceptionHandler(handler)

      try
        scheduler.execute(() => throw failure)
        scheduler.execute(() => ranAfterFailure = true)
        executor.runNext()
      finally currentThread.setUncaughtExceptionHandler(previousHandler)

      assert(reportedFailure.contains(failure))
      assert(ranAfterFailure)
      assert(executor.pendingTasks == 0)
    }

    test("can retry scheduling after the backing executor rejects a drain") {
      val delegate = new MockExecutor()
      var rejectNext = true
      val executor = new Executor:
        override def execute(task: Runnable): Unit =
          if rejectNext
          then {
            rejectNext = false
            throw new RejectedExecutionException("rejected")
          } else delegate.execute(task)

      val scheduler = new SequentialScheduler(executor)
      val executionOrder = ArrayBuffer.empty[Int]

      val _ = assertThrows[RejectedExecutionException] {
        scheduler.execute(() => executionOrder += 1)
      }
      scheduler.execute(() => executionOrder += 2)

      assert(delegate.pendingTasks == 1)
      delegate.runNext()
      assert(executionOrder == Seq(1, 2))
    }

    test("rejects invalid configuration and null tasks") {
      val executor = new MockExecutor()

      val _ = assertThrows[NullPointerException] {
        val _ = new SequentialScheduler(null)
      }
      val _ = assertThrows[IllegalArgumentException] {
        val _ = new SequentialScheduler(executor, maxTasksPerTurn = 0)
      }
      val _ = assertThrows[IllegalArgumentException] {
        val _ = new SequentialScheduler(executor, maxNanosPerTurn = 0L)
      }

      val scheduler = new SequentialScheduler(executor)
      val _ = assertThrows[NullPointerException] {
        scheduler.execute(null)
      }
    }
