package snhttp.jdk.net.http.internal

import java.util.Objects.requireNonNull
import java.util.concurrent.Flow
import java.util.concurrent.{CompletableFuture, TimeUnit, ForkJoinPool, Executor}
import java.util.List as JList
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
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
    with AutoCloseable:

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
