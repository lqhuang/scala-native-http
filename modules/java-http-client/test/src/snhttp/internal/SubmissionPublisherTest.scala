/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{
  CompletableFuture,
  CountDownLatch,
  Executor,
  Executors,
  Flow,
  ForkJoinPool,
  SubmissionPublisher
}

import scala.util.boundary
import scala.util.boundary.break

import utest.{assert, assertThrows, TestSuite, Tests, test}

import snhttp.internal.JSR166Test

class SubmissionPublisherTest extends TestSuite with JSR166Test:

  class TestSubscriber extends Flow.Subscriber[Integer] {
    @volatile var sn: Flow.Subscription = null
    var last = 0 // Requires that onNexts are in numeric order

    @volatile var nexts = 0
    @volatile var errors = 0
    @volatile var completes = 0
    @volatile var throwOnCall = false
    @volatile var request = true
    @volatile var lastError: Throwable = null

    override def onSubscribe(s: Flow.Subscription): Unit = {
      threadAssertTrue(sn == null)
      sn = s
      notifyAll()
      if (throwOnCall) throw new SPException()
      if (request) sn.request(1L)
    }

    override def onNext(t: Integer): Unit = {
      nexts += 1
      notifyAll()
      val current = t.intValue()
      threadAssertTrue(current >= last)
      last = current
      if (request) sn.request(1L)
      if (throwOnCall) throw new SPException()
    }

    override def onError(t: Throwable): Unit = {
      threadAssertTrue(completes == 0)
      threadAssertTrue(errors == 0)
      lastError = t
      errors += 1
      notifyAll()
    }

    override def onComplete(): Unit = {
      threadAssertTrue(completes == 0)
      completes += 1
      notifyAll()
    }

    def awaitSubscribe(): Unit =
      boundary {
        while (sn == null)
          try wait()
          catch {
            case ex: Exception =>
              threadUnexpectedException(ex)
              break(())
          }
      }

    def awaitNext(n: Int): Unit =
      boundary {
        while (nexts < n)
          try wait()
          catch {
            case ex: Exception =>
              threadUnexpectedException(ex)
              break(())
          }
      }

    def awaitComplete(): Unit =
      boundary {
        while (completes == 0 && errors == 0)
          try wait()
          catch {
            case ex: Exception =>
              threadUnexpectedException(ex)
              break(())
          }
      }

    def awaitError(): Unit =
      boundary {
        while (errors == 0)
          try wait()
          catch {
            case ex: Exception =>
              threadUnexpectedException(ex)
              break(())
          }
      }
  }

  def noopHandle(count: AtomicInteger): Boolean = {
    count.getAndIncrement()
    false
  }

  def reqHandle(count: AtomicInteger, sub: Flow.Subscriber[_]): Boolean = {
    count.getAndIncrement()
    sub.asInstanceOf[TestSubscriber].sn.request(Long.MaxValue)
    true
  }

  def basicPublisher() = new SubmissionPublisher[Integer]()

  class SPException extends RuntimeException {}

  final val basicExecutor = basicPublisher().getExecutor()

  /**
   * A new SubmissionPublisher has no subscribers, a non-null executor, a power-of-two capacity, is
   * not closed, and reports zero demand and lag
   */
  def checkInitialState(p: SubmissionPublisher[_]): Unit = {
    assert(p.hasSubscribers() == false)
    assert(0 == p.getNumberOfSubscribers())
    assert(p.getSubscribers.isEmpty() == true)
    assert(p.isClosed() == false)
    assert(p.getClosedException() == null)
    val n = p.getMaxBufferCapacity()
    assert((n & (n - 1)) == 0) // power of two

    assert(p.getExecutor() != null)
    assert(0L == p.estimateMinimumDemand())
    assert(0 == p.estimateMaximumLag())
  }

  def tests = Tests {

    /**
     * A default-constructed SubmissionPublisher has no subscribers, is not closed, has default
     * buffer size, and uses the defaultExecutor
     */
    test("Constructor1") {
      val p = new SubmissionPublisher[Integer]
      checkInitialState(p)
      assert(p.getMaxBufferCapacity() == Flow.defaultBufferSize())
      val e = p.getExecutor()
      val c = ForkJoinPool.commonPool()
      if (ForkJoinPool.getCommonPoolParallelism() > 1)
        assert(e eq c)
      else
        assert(e ne c)
    }

    /**
     * A new SubmissionPublisher has no subscribers, is not closed, has the given buffer size, and
     * uses the given executor
     */
    test("Constructor2") {
      val e = Executors.newFixedThreadPool(1)
      val p = new SubmissionPublisher[Integer](e, 8)
      checkInitialState(p)
      assert(p.getExecutor() eq e)
      assert(8 == p.getMaxBufferCapacity())
    }

    /**
     * A null Executor argument to SubmissionPublisher constructor throws NullPointerException
     */
    test("Constructor3") {
      assertThrows[NullPointerException] {
        val _ = new SubmissionPublisher[Integer](null, 8)
      }
    }

    /**
     * A negative capacity argument to SubmissionPublisher constructor throws
     * IllegalArgumentException
     */
    test("Constructor4") {
      val e = Executors.newFixedThreadPool(1)
      assertThrows[IllegalArgumentException]:
        val _ = new SubmissionPublisher[Integer](e, -1)
    }

    /**
     * A closed publisher reports isClosed with no closedException and throws IllegalStateException
     * upon attempted submission; a subsequent close or closeExceptionally has no additional effect.
     */
    test("Close") {
      val p = basicPublisher()
      checkInitialState(p)
      p.close()
      assert(p.isClosed() == true)
      assert(p.getClosedException() == null)

      val _ = assertThrows[IllegalStateException] {
        val _ = p.submit(1)
      }

      val ex = new SPException()
      p.closeExceptionally(ex)
      assert(p.isClosed() == true)
      assert(p.getClosedException() == null)
    }

    /**
     * A publisher closedExceptionally reports isClosed with the closedException and throws
     * IllegalStateException upon attempted submission; a subsequent close or closeExceptionally has
     * no additional effect.
     */
    test("CloseExceptionally") {
      val p = basicPublisher()
      checkInitialState(p)
      val ex = new SPException()
      p.closeExceptionally(ex)
      assert(p.isClosed() == true)
      assert(p.getClosedException() eq ex)

      val _ = assertThrows[IllegalStateException] {
        p.submit(1): Unit
      }

      p.close()
      assert(p.isClosed())
      assert(p.getClosedException() eq ex)
    }

    /**
     * Upon subscription, the subscriber's onSubscribe is called, no other Subscriber methods are
     * invoked, the publisher hasSubscribers, isSubscribed is true, and existing subscriptions are
     * unaffected.
     */
    test("Subscribe1") {
      val s = new TestSubscriber()
      val p = basicPublisher()
      p.subscribe(s)
      assert(p.hasSubscribers() == true)
      assert(1 == p.getNumberOfSubscribers())
      assert(p.getSubscribers.contains(s) == true)
      assert(p.isSubscribed(s) == true)
      s.awaitSubscribe()
      assert(s.sn != null)
      assert(0 == s.nexts)
      assert(0 == s.errors)
      assert(0 == s.completes)
      val s2 = new TestSubscriber()
      p.subscribe(s2)
      assert(p.hasSubscribers() == true)
      assert(2 == p.getNumberOfSubscribers())
      assert(p.getSubscribers.contains(s) == true)
      assert(p.getSubscribers.contains(s2) == true)
      assert(p.isSubscribed(s) == true)
      assert(p.isSubscribed(s2) == true)
      s2.awaitSubscribe()
      assert(s2.sn != null)
      assert(0 == s2.nexts)
      assert(0 == s2.errors)
      assert(0 == s2.completes)
      p.close()
    }

    /**
     * If closed, upon subscription, the subscriber's onComplete method is invoked
     */
    test("Subscribe2") {
      val s = new TestSubscriber()
      val p = basicPublisher()
      p.close()
      p.subscribe(s)
      s.awaitComplete()
      assert(0 == s.nexts)
      assert(0 == s.errors)
      assert(1 == s.completes)
    }

    /**
     * If closedExceptionally, upon subscription, the subscriber's onError method is invoked
     */
    test("Subscribe3") {
      val s = new TestSubscriber()
      val p = basicPublisher()
      val ex = new SPException()
      p.closeExceptionally(ex)
      assert(p.isClosed() == true)
      assert(p.getClosedException() eq ex)
      p.subscribe(s)
      s.awaitError()
      assert(0 == s.nexts)
      assert(1 == s.errors)
    }

    /**
     * Upon attempted resubscription, the subscriber's onError is called and the subscription is
     * cancelled.
     */
    test("Subscribe4") {
      val s = new TestSubscriber()
      val p = basicPublisher()
      p.subscribe(s)
      assert(p.hasSubscribers() == true)
      assert(1 == p.getNumberOfSubscribers())
      assert(p.getSubscribers.contains(s) == true)
      assert(p.isSubscribed(s) == true)
      s.awaitSubscribe()
      assert(s.sn != null)
      assert(0 == s.nexts)
      assert(0 == s.errors)
      assert(0 == s.completes)
      p.subscribe(s)
      s.awaitError()
      assert(0 == s.nexts)
      assert(1 == s.errors)
      assert(p.isSubscribed(s) == false)
    }

    /** An exception thrown in onSubscribe causes onError */
    test("Subscribe5") {
      val s = new TestSubscriber()
      val p = basicPublisher()
      s.throwOnCall = true
      p.subscribe(s)
      s.awaitError()
      assert(0 == s.nexts)
      assert(1 == s.errors)
      assert(0 == s.completes)
    }

    /** subscribe(null) throws NPE */
    test("Subscribe6") {
      val p = basicPublisher()
      val _ = assertThrows[NullPointerException] {
        p.subscribe(null)
      }
      checkInitialState(p)
    }

    /**
     * Closing a publisher causes onComplete to subscribers
     */
    test("CloseCompletes") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      p.submit(1)
      p.close()
      assert(p.isClosed() == true)
      assert(p.getClosedException() == null)
      s1.awaitComplete()
      assert(1 == s1.nexts)
      assert(1 == s1.completes)
      s2.awaitComplete()
      assert(1 == s2.nexts)
      assert(1 == s2.completes)
    }

    /**
     * Closing a publisher exceptionally causes onError to subscribers after they are subscribed
     */
    test("CloseExceptionallyError") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      p.submit(1)
      p.closeExceptionally(new SPException())
      assert(p.isClosed() == true)
      s1.awaitSubscribe()
      s1.awaitError()
      assert(s1.nexts <= 1)
      assert(1 == s1.errors)
      s2.awaitSubscribe()
      s2.awaitError()
      assert(s2.nexts <= 1)
      assert(1 == s2.errors)
    }

    /**
     * Cancelling a subscription eventually causes no more onNexts to be issued
     */
    test("Cancel") {
      val p = new SubmissionPublisher[Integer](basicExecutor, 4) // must be < 20

      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      s1.awaitSubscribe()
      p.submit(1)
      s1.sn.cancel()
      for (i <- 2 to 20)
        p.submit(i)
      p.close()
      s2.awaitComplete()
      assert(20 == s2.nexts)
      assert(1 == s2.completes)
      assert(s1.nexts < 20)
      assert(p.isSubscribed(s1) == false)
    }

    /**
     * Throwing an exception in onNext causes onError
     */
    test("ThrowOnNext") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      s1.awaitSubscribe()
      p.submit(1)
      s1.throwOnCall = true
      p.submit(2)
      p.close()
      s2.awaitComplete()
      assert(2 == s2.nexts)
      s1.awaitComplete()
      assert(1 == s1.errors)
    }

    /**
     * If a handler is supplied in constructor, it is invoked when subscriber throws an exception in
     * onNext
     */
    test("ThrowOnNextHandler") {
      val calls = new AtomicInteger()
      val p = new SubmissionPublisher[Integer](
        basicExecutor,
        8,
        (s: Flow.Subscriber[_ >: Integer], e: Throwable) => calls.getAndIncrement(): Unit
      )
      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      s1.awaitSubscribe()
      p.submit(1)
      s1.throwOnCall = true
      p.submit(2)
      p.close()
      s2.awaitComplete()
      assert(2 == s2.nexts)
      assert(1 == s2.completes)
      s1.awaitError()
      assert(1 == s1.errors)
      assert(1 == calls.get())
    }

    /**
     * onNext items are issued in the same order to each subscriber
     */
    test("Order") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      for (i <- 1 to 20)
        p.submit(i)
      p.close()
      s2.awaitComplete()
      s1.awaitComplete()
      assert(20 == s2.nexts)
      assert(1 == s2.completes)
      assert(20 == s1.nexts)
      assert(1 == s1.completes)
    }

    /**
     * onNext is issued only if requested
     */
    test("Request1") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      s1.request = false
      p.subscribe(s1)
      s1.awaitSubscribe()
      assert(0 == p.estimateMinimumDemand())
      val s2 = new TestSubscriber()
      p.subscribe(s2)
      p.submit(1)
      p.submit(2)
      s2.awaitNext(1)
      assert(0 == s1.nexts)
      s1.sn.request(3)
      p.submit(3)
      p.close()
      s2.awaitComplete()
      assert(3 == s2.nexts)
      assert(1 == s2.completes)
      s1.awaitComplete()
      assert(s1.nexts > 0)
      assert(1 == s1.completes)
    }

    /**
     * onNext is not issued when requests become zero
     */
    test("Request2") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      s1.request = false
      p.submit(1)
      p.submit(2)
      p.close()
      s2.awaitComplete()
      assert(2 == s2.nexts)
      assert(1 == s2.completes)
      s1.awaitNext(1)
      assert(1 == s1.nexts)
    }

    /**
     * Non-positive request causes error
     */
    test("Request3") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      val s3 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      p.subscribe(s3)
      s3.awaitSubscribe()
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      s1.sn.request(-1L)
      s3.sn.request(0L)
      p.submit(1)
      p.submit(2)
      p.close()
      s2.awaitComplete()
      assert(2 == s2.nexts)
      assert(1 == s2.completes)
      s1.awaitError()
      assert(1 == s1.errors)
      assert(s1.lastError.isInstanceOf[IllegalArgumentException] == true)
      s3.awaitError()
      assert(1 == s3.errors)
      assert(s3.lastError.isInstanceOf[IllegalArgumentException] == true)
    }

    /**
     * estimateMinimumDemand reports 0 until request, nonzero after request
     */
    test("EstimateMinimumDemand") {
      val s = new TestSubscriber()
      val p = basicPublisher()
      s.request = false
      p.subscribe(s)
      s.awaitSubscribe()
      assert(0 == p.estimateMinimumDemand())
      s.sn.request(1)
      assert(1 == p.estimateMinimumDemand())
    }

    /** submit to a publisher with no subscribers returns lag 0 */
    test("EmptySubmit") {
      val p = basicPublisher()
      assert(0 == p.submit(1))
    }

    /** submit(null) throws NPE */
    test("NullSubmit") {
      val p = basicPublisher()
      assertThrows[NullPointerException] {
        p.submit(null): Unit
      }
    }

    /**
     * submit returns number of lagged items, compatible with result of estimateMaximumLag.
     */
    test("LaggedSubmit") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      assert(1 == p.submit(1))
      assert(p.estimateMaximumLag() >= 1)
      assert(p.submit(2) >= 2)
      assert(p.estimateMaximumLag() >= 2)
      s1.sn.request(4)
      assert(p.submit(3) >= 3)
      assert(p.estimateMaximumLag() >= 3)
      s2.sn.request(4)
      p.submit(4)
      p.close()
      s2.awaitComplete()
      assert(4 == s2.nexts)
      s1.awaitComplete()
      assert(4 == s2.nexts)
    }

    /**
     * submit eventually issues requested items when buffer capacity is 1
     */
    test("Cap1Submit") {
      val p = new SubmissionPublisher[Integer](basicExecutor, 1)
      val s1 = new TestSubscriber()
      val s2 = new TestSubscriber()
      p.subscribe(s1)
      p.subscribe(s2)
      for (i <- 1 to 20)
        assert(p.submit(i) >= 0)
      p.close()
      s2.awaitComplete()
      s1.awaitComplete()
      assert(20 == s2.nexts)
      assert(1 == s2.completes)
      assert(20 == s1.nexts)
      assert(1 == s1.completes)
    }

    /**
     * offer to a publisher with no subscribers returns lag 0
     */
    test("EmptyOffer") {
      val p = basicPublisher()
      assert(0 == p.offer(1, null))
    }

    /**
     * offer(null) throws NPE
     */
    test("NullOffer") {
      val p = basicPublisher()
      assertThrows[NullPointerException] {
        p.offer(null, null): Unit
      }
    }

    /**
     * offer returns number of lagged items if not saturated
     */
    test("LaggedOffer") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      assert(p.offer(1, null) >= 1)
      assert(p.offer(2, null) >= 2)
      s1.sn.request(4)
      assert(p.offer(3, null) >= 3)
      s2.sn.request(4)
      p.offer(4, null)
      p.close()
      s2.awaitComplete()
      assert(4 == s2.nexts)
      s1.awaitComplete()
      assert(4 == s2.nexts)
    }

    /** offer reports drops if saturated */
    test("DroppedOffer") {
      val p = new SubmissionPublisher[Integer](basicExecutor, 4)
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      for (i <- 1 to 4)
        assert(p.offer(i, null) >= 0)
      p.offer(5, null)
      assert(p.offer(6, null) < 0)
      s1.sn.request(64)
      assert(p.offer(7, null) < 0)
      s2.sn.request(64)
      p.close()
      s2.awaitComplete()
      assert(s2.nexts >= 4)
      s1.awaitComplete()
      assert(s1.nexts >= 4)
    }

    /**
     * offer invokes drop handler if saturated
     */
    test("HandledDroppedOffer") {
      val calls = new AtomicInteger()
      val p = new SubmissionPublisher[Integer](basicExecutor, 4)
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      for (i <- 1 to 4)
        assert(
          p.offer(
            i,
            (s: Flow.Subscriber[_ >: Integer], x: Integer) => noopHandle(calls)
          ) >= 0
        )
      p.offer(
        4,
        (s: Flow.Subscriber[_ >: Integer], x: Integer) => noopHandle(calls)
      )
      assert(
        p.offer(
          6,
          (s: Flow.Subscriber[_ >: Integer], x: Integer) => noopHandle(calls)
        ) < 0
      )
      s1.sn.request(64)
      assert(
        p.offer(
          7,
          (s: Flow.Subscriber[_ >: Integer], x: Integer) => noopHandle(calls)
        ) < 0
      )
      s2.sn.request(64)
      p.close()
      s2.awaitComplete()
      s1.awaitComplete()
      assert(calls.get() >= 4)
    }

    /**
     * offer succeeds if drop handler forces request
     */
    test("RecoveredHandledDroppedOffer") {
      val calls = new AtomicInteger()
      val p = new SubmissionPublisher[Integer](basicExecutor, 4)
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      var n = 0
      for (i <- 1 to 8) {
        val d = p.offer(
          i,
          (s: Flow.Subscriber[_ >: Integer], x: Integer) => reqHandle(calls, s)
        )
        n = n + 2 + (if (d < 0) d else 0)
      }
      p.close()
      s2.awaitComplete()
      s1.awaitComplete()
      assert(n == s1.nexts + s2.nexts)
      assert(calls.get() >= 2)
    }

    /**
     * Timed offer to a publisher with no subscribers returns lag 0
     */
    test("EmptyTimedOffer") {
      val p = basicPublisher()
      val startTime = System.nanoTime()
      assert(0 == p.offer(1, LONG_DELAY_MS, MILLISECONDS, null))
      assert(millisElapsedSince(startTime) < LONG_DELAY_MS / 2)
    }

    /**
     * Timed offer with null item or TimeUnit throws NPE
     */
    test("NullTimedOffer") {
      val p = basicPublisher()
      val startTime = System.nanoTime()

      val _ = assertThrows[NullPointerException] {
        p.offer(null, LONG_DELAY_MS, MILLISECONDS, null): Unit
      }
      val _ = assertThrows[NullPointerException] {
        p.offer(1, LONG_DELAY_MS, null, null): Unit
      }
      assert(millisElapsedSince(startTime) < LONG_DELAY_MS / 2)
    }

    /**
     * Timed offer returns number of lagged items if not saturated
     */
    test("LaggedTimedOffer") {
      val p = basicPublisher()
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      val startTime = System.nanoTime()
      assert(p.offer(1, LONG_DELAY_MS, MILLISECONDS, null) >= 1)
      assert(p.offer(2, LONG_DELAY_MS, MILLISECONDS, null) >= 2)
      s1.sn.request(4)
      assert(p.offer(3, LONG_DELAY_MS, MILLISECONDS, null) >= 3)
      s2.sn.request(4)
      p.offer(4, LONG_DELAY_MS, MILLISECONDS, null)
      p.close()
      s2.awaitComplete()
      assert(4 == s2.nexts)
      s1.awaitComplete()
      assert(4 == s2.nexts)
      assert(millisElapsedSince(startTime) < LONG_DELAY_MS / 2)
    }

    /**
     * Timed offer reports drops if saturated
     */
    test("DroppedTimedOffer") {
      val p = new SubmissionPublisher[Integer](basicExecutor, 4)
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      val delay = timeoutMillis()
      for (i <- 1 to 4)
        assert(p.offer(i, delay, MILLISECONDS, null) >= 0)
      val startTime = System.nanoTime()
      assert(p.offer(5, delay, MILLISECONDS, null) < 0)
      s1.sn.request(64)
      assert(p.offer(6, delay, MILLISECONDS, null) < 0)
      // 2 * delay should elapse but check only 1 * delay to allow timer slop
      assert(millisElapsedSince(startTime) >= delay)
      s2.sn.request(64)
      p.close()
      s2.awaitComplete()
      assert(s2.nexts >= 2)
      s1.awaitComplete()
      assert(s1.nexts >= 2)
    }

    /**
     * Timed offer invokes drop handler if saturated
     */
    test("HandledDroppedTimedOffer") {
      val calls = new AtomicInteger()
      val p = new SubmissionPublisher[Integer](basicExecutor, 4)
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      val delay = timeoutMillis()
      for (i <- 1 to 4)
        assert(
          p.offer(
            i,
            delay,
            MILLISECONDS,
            (s: Flow.Subscriber[_ >: Integer], x: Integer) => noopHandle(calls)
          ) >= 0
        )
      val startTime = System.nanoTime()
      assert(
        p.offer(
          5,
          delay,
          MILLISECONDS,
          (s: Flow.Subscriber[_ >: Integer], x: Integer) => noopHandle(calls)
        ) < 0
      )
      s1.sn.request(64)
      assert(
        p.offer(
          6,
          delay,
          MILLISECONDS,
          (s: Flow.Subscriber[_ >: Integer], x: Integer) => noopHandle(calls)
        ) < 0
      )
      assert(millisElapsedSince(startTime) >= delay)
      s2.sn.request(64)
      p.close()
      s2.awaitComplete()
      s1.awaitComplete()
      assert(calls.get() >= 2)
    }

    /**
     * Timed offer succeeds if drop handler forces request
     */
    test("RecoveredHandledDroppedTimedOffer") {
      val calls = new AtomicInteger()
      val p = new SubmissionPublisher[Integer](basicExecutor, 4)
      val s1 = new TestSubscriber()
      s1.request = false
      val s2 = new TestSubscriber()
      s2.request = false
      p.subscribe(s1)
      p.subscribe(s2)
      s2.awaitSubscribe()
      s1.awaitSubscribe()
      var n = 0
      val delay = timeoutMillis()
      val startTime = System.nanoTime()
      for (i <- 1 to 6) {
        val d = p.offer(
          i,
          delay,
          MILLISECONDS,
          (s: Flow.Subscriber[_ >: Integer], x: Integer) => reqHandle(calls, s)
        )
        n = n + 2 + (if (d < 0) d else 0)
      }
      assert(millisElapsedSince(startTime) >= delay)
      p.close()
      s2.awaitComplete()
      s1.awaitComplete()
      assert(n == s1.nexts + s2.nexts)
      assert(calls.get() >= 2)
    }

    /**
     * consume returns a CompletableFuture that is done when publisher completes
     */
    test("Consume") {
      val sum = new AtomicInteger()
      val p = basicPublisher()
      val f = p.consume((x: Integer) => sum.getAndAdd(x.intValue): Unit)
      val n = 20
      for (i <- 1 to n)
        p.submit(i)
      p.close()
      f.join
      assert((n * (n + 1)) / 2 == sum.get())
    }

    /**
     * consume(null) throws NPE
     */
    test("ConsumeNPE") {
      val p = basicPublisher()
      assertThrows[NullPointerException] {
        val f = p.consume(null)
      }
    }

    /**
     * consume eventually stops processing published items if cancelled
     */
    test("CancelledConsume") {
      val count = new AtomicInteger()
      val p = basicPublisher()
      val f = p.consume((x: Integer) => count.getAndIncrement(): Unit)
      f.cancel(true)
      val n = 1000000 // arbitrary limit

      for (i <- 1 to n)
        p.submit(i)
      assert(count.get() < n)
    }
  }
