/**
 * Inspired by
 * https://github.com/codehaus/jsr166-mirror/blob/master/src/main/java/util/concurrent/SubmissionPublisher.java
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166 Expert Group and released to the
 * public domain, as explained at http://creativecommons.org/publicdomain/zero/1.0/
 */
package java.util.concurrent

import java.util.Objects.requireNonNull
import java.util.concurrent.Flow
import java.util.concurrent.{CompletableFuture, TimeUnit, ForkJoinPool, Executor}
import java.util.List as JList
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong}
import java.util.function.{BiConsumer, BiPredicate, Consumer}

import scala.collection.mutable.HashSet
import scala.util.control.NonFatal

/**
 * Reference:
 *
 *   - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/SubmissionPublisher.html
 *
 * since JDK 9
 */
class SubmissionPublisher[T](
    private val executor: Executor,
    private val maxBufferCapacity: Int,
    private val handler: BiConsumer[? >: Subscriber[? >: T], ? >: Throwable],
) extends Publisher[T]
    with AutoCloseable {

  def this() =
    this(ForkJoinPool.commonPool(), Flow.defaultBufferSize(), null)

  def this(executor: Executor, maxBufferCapacity: Int) =
    this(executor, maxBufferCapacity, null)

  requireNonNull(executor, "executor cannot be null")
  require(maxBufferCapacity > 0, "maxBufferCapacity must be positive")

  private val subscribers = HashSet.empty[Subscriber[? >: T]]
  private val closed = new AtomicBoolean(false)
  private val closedException: Throwable = null

  override def subscribe(subscriber: Subscriber[? >: T]): Unit =
    ???

  def submit(item: T): Int =
    requireNonNull(item, "item cannot be null")
    if closed.get() then throw IllegalStateException("Publisher is already closed")
    ???

  def offer(item: T, onDrop: BiPredicate[Subscriber[? >: T], ? >: T]): Int =
    ???

  def offer(
      item: T,
      timeout: Long,
      unit: TimeUnit,
      onDrop: BiPredicate[Flow.Subscriber[? >: T], ? >: T],
  ): Int =
    ???

  def close(): Unit =
    ???

  def closeExceptionally(error: Throwable): Unit =
    ???

  def isClosed(): Boolean =
    closed.get()

  def getClosedException(): Throwable =
    closedException

  def hasSubscribers: Boolean =
    ???

  def getNumberOfSubscribers(): Int =
    ???

  def getExecutor(): Executor =
    executor

  def getMaxBufferCapacity(): Int =
    maxBufferCapacity

  def getSubscribers(): JList[Subscriber[? >: T]] =
    ???

  def isSubscribed(subscriber: Flow.Subscriber[? >: T]): Boolean =
    requireNonNull(subscriber, "subscriber cannot be null")
    ???

  def estimateMinimumDemand(): Long =
    ???

  def estimateMaximumLag(): Long =
    ???

  def consume(consumer: Consumer[? >: T]): CompletableFuture[Unit] =
    requireNonNull(consumer, "consumer cannot be null")
    ???

  /**
   * Private methods and implementation details
   */

}

object SubmissionPublisher {

  /**
   * A task for consuming buffer items and signals, created and executed whenever they become
   * available. A task consumes as many items/signals as possible before terminating, at which point
   * another task is created when needed. The dual Runnable and ForkJoinTask declaration saves
   * overhead when executed by ForkJoinPools, without impacting other kinds of Executors.
   */
  final private class ConsumerTask[T](
      private val consumer: BufferedSubscription[T],
  ) extends ForkJoinTask[Unit]
      with Runnable {
    final override def getRawResult(): Unit = ???

    final override protected[concurrent] def setRawResult(value: Unit): Unit = ???

    final override protected[concurrent] def exec(): Boolean =
      consumer.consume()
      false

    final override def run(): Unit =
      consumer.consume()
  }

  private[concurrent] object BufferedSubscription {
    // ctl field constants
    private[concurrent] val ACTIVE = 0x01 // consumer task active
    private[concurrent] val CONSUME = 0x02 // keep-alive for consumer task
    private[concurrent] val DISABLED = 0x04 // final state
    private[concurrent] val ERROR = 0x08 // signal onError then disable
    private[concurrent] val SUBSCRIBE = 0x10 // signal onSubscribe
    private[concurrent] val COMPLETE = 0x20 // signal onComplete when done
    private[concurrent] val INTERRUPTED = -1L // timeout vs interrupt sentinel

    /**
     * Initial/Minimum buffer capacity. Must be a power of two, at least 2.
     */
    private[concurrent] val MINCAP = 8
  }

  final private class BufferedSubscription[T] private (
      private var subscriber: Flow.Subscriber[? >: T], // null if disabled
      private var executor: Executor, // null if disabled
      private var maxCapacity: Int, // reduced on OOME
  ) extends Flow.Subscription
      with ForkJoinPool.ManagedBlocker {

    // Order-sensitive field declarations
    private var timeout = 0L // > 0 if timed wait
    private val demand = new AtomicLong(0L) // unfilled requests
    private var putStat = 0 // offer result for ManagedBlocker
    private var helpDepth = 0 // nested helping depth (at most 1)
    @volatile private var ctl = 0 // atomic run state flags

    private val head = new AtomicInteger(0) // next position to take
    private val tail = new AtomicInteger(0) // next position to put
    private var array: Array[AnyRef] = null // buffer: null if disabled

    @volatile private var pendingError: Throwable = null // holds until onError issued
    @volatile private var waiter: Thread = null // blocked producer thread
    private var putItem: T = _ // for offer within ManagedBlocker
    private val next: BufferedSubscription[T] = null // used only by publisher
    private val nextRetry: BufferedSubscription[T] = null // used only by publisher

    final def isDisabled() =
      ctl == BufferedSubscription.DISABLED

    /**
     * Returns estimated number of buffered items, or -1 if disabled
     */
    final def estimateLag(): Int =
      if ctl == BufferedSubscription.DISABLED
      then -1
      else
        val n = tail.get() - head.get()
        if n > 0
        then n
        else 0

    /**
     * Tries to add item and start consumer task if necessary.
     *
     * @return
     *   -1 if disabled, 0 if dropped, else estimated lag
     */
    final def offer(item: T): Int =
      ???

    /**
     * Tries to create or expand buffer, then adds item if possible.
     */
    private def growAndAdd(a: Array[AnyRef], item: T): Int =
      ???

    /**
     * Spins/helps/blocks while offer returns 0. Called only if initial offer return 0.
     */
    final def submit(item: T): Int =
      ???

    /**
     * Timeout version; similar to submit
     */
    final def timedOffer(item: T, nanos: Long): Int =
      ???

    /** Version of consume called when helping in submit or timedOffer */
    private def helpConsume(): Unit = {
      helpDepth = 1 // only one level allowed
      consume()
      helpDepth = 0
    }

    /**
     * Tries to start consumer task after offer.
     *
     * @return
     *   -1 if now disabled, else argument
     */
    private def startOnOffer(stat: Int): Int =
      ???

    /**
     * Nulls out most fields, mainly to avoid garbage retention until publisher unsubscribes, but
     * also to help cleanly stop upon error by nulling required components.
     */
    private def detach(): Unit =
      ???

    /**
     * Issues error signal, asynchronously if a task is running, else synchronously.
     */
    final def onError(ex: Throwable): Unit =
      ???

    /**
     * Tries to start consumer task upon a signal or request; disables on failure.
     */
    private def startOrDisable(): Unit =
      ???

    final def onComplete(): Unit =
      ???

    final def onSubscribe(): Unit =
      ???

    /**
     * Causes consumer task to exit if active (without reporting onError unless there is already a
     * pending error), and disables.
     */
    override def cancel(): Unit =
      ???

    /**
     * Adds to demand and possibly starts task.
     */
    override def request(n: Long): Unit =
      ???

    final override def isReleasable(): Boolean = // for ManagedBlocker
      ???

    final override def block(): Boolean = // for ManagedBlocker
      ???

    /**
     * Consumer loop, called from ConsumerTask, or indirectly via helpConsume when helping during
     * submit.
     */
    final def consume(): Unit =
      ???
  }
}
