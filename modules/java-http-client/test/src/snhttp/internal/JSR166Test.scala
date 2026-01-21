/*
 * Written by Doug Lea and Martin Buchholz with assistance from
 * members of JCP JSR-166 Expert Group and released to the public
 * domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 * Other contributors include Andrew Wright, Jeffrey Hayes,
 * Pat Fisher, Mike Judd.
 */
package snhttp.internal

import java.util.concurrent.TimeUnit.NANOSECONDS
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import utest.assert

trait JSR166Test {

  /**
   * The first exception encountered if any threadAssertXXX method fails.
   */
  final private val threadFailure: AtomicReference[Throwable] = new AtomicReference(null)

  /**
   * Records an exception so that it can be rethrown later in the test harness thread, triggering a
   * test case failure. Only the first failure is recorded subsequent calls to this method from
   * within the same test have no effect.
   */
  def threadRecordFailure(t: Throwable) = {
    System.err.println(t)
    if (threadFailure.compareAndSet(null, t)) () // dumpTestThreads()
  }

  /**
   * Just like assertTrue(b), but additionally recording (using threadRecordFailure) any
   * AssertionError thrown, so that the current testcase will fail.
   */
  def threadAssertTrue(pred: => Boolean): Unit =
    try assert(pred == true)
    catch {
      case fail: AssertionError =>
        threadRecordFailure(fail)
        throw fail
    }

  /**
   * Records the given exception using {@link #threadRecordFailure}, then rethrows the exception,
   * wrapping it in an AssertionError if necessary.
   */
  def threadUnexpectedException(t: Throwable): Unit = {
    threadRecordFailure(t)
    // t.printStackTrace()
    t match {
      case t: RuntimeException => throw t
      case t: Error            => throw t
      case t                   => throw new AssertionError(s"unexpected exception: $t", t)
    }
  }

  /**
   * The scaling factor to apply to standard delays used in tests. May be initialized from any of:
   *   - the "jsr166.delay.factor" system property
   *   - the "test.timeout.factor" system property (as used by jtreg) See:
   *     http://openjdk.java.net/jtreg/tag-spec.html
   *   - hard-coded fuzz factor when using a known slowpoke VM
   */
  private val delayFactor = 1.0f

  // Delays for timing-dependent tests, in milliseconds.
  final val SHORT_DELAY_MS = (50 * delayFactor).toLong
  final val SMALL_DELAY_MS = SHORT_DELAY_MS * 5
  final val MEDIUM_DELAY_MS = SHORT_DELAY_MS * 10
  final val LONG_DELAY_MS = SHORT_DELAY_MS * 200

  final private lazy val TIMEOUT_DELAY_MS =
    (12.0 * Math.cbrt(delayFactor)).toLong

  /**
   * Returns a timeout in milliseconds to be used in tests that verify that operations block or time
   * out. We want this to be longer than the OS scheduling quantum, but not too long, so don't scale
   * linearly with delayFactor we use "crazy" cube root instead.
   */
  def timeoutMillis(): Long = TIMEOUT_DELAY_MS

  /**
   * Returns the number of milliseconds since time given by startNanoTime, which must have been
   * previously returned from a call to {@link System#nanoTime()}.
   */
  def millisElapsedSince(startNanoTime: Long): Long =
    NANOSECONDS.toMillis(System.nanoTime() - startNanoTime)

}
