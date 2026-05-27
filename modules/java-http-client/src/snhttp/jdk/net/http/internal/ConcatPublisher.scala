package snhttp.jdk.net.http.internal

import java.net.http.HttpRequest.BodyPublisher
import java.nio.ByteBuffer
import java.util.Objects.requireNonNull
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription, Processor}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import java.util.concurrent.locks.ReentrantLock

import scala.util.control.NonFatal

/**
 * Processor that concatenates multiple BodyPublishers into a single BodyPublisher. It subscribes to
 * each publisher in sequence, and forwards the items to the downstream subscriber. It also handles
 * backpressure by requesting items from the current publisher based on the demand from the
 * downstream subscriber.
 *
 * @param publishers
 */
class ConcatPublisher[T](val publishers: Seq[BodyPublisher]) extends BodyPublisher:

  requireNonNull(publishers, "publishers must not be null")
  publishers.foreach(p => requireNonNull(p, "publishers cannot contain null elements"))

  private val subscribed = new AtomicBoolean(false)

  override def contentLength(): Long =
    val lengths = publishers.map(_.contentLength())
    val sum = lengths.fold(0L)((x, y) => if x < 0 || y < 0 then -1 else x + y)
    if sum < 0 then -1 else sum

  override def subscribe(subscriber: Subscriber[? >: ByteBuffer]): Unit =
    requireNonNull(subscriber, "subscriber must not be null")
    subscriber.onSubscribe(new ConcatSubscription(publishers.iterator, subscriber))

  /**
   * ConcatSubscription
   */
  private final class ConcatSubscription(
      _publishers: Iterator[BodyPublisher],
      _subscriber: Subscriber[? >: ByteBuffer],
  ) extends Subscription
      with Subscriber[ByteBuffer]:

    private val cancelled = new AtomicBoolean(false)
    private val terminated = new AtomicBoolean(false)

    private val subLock = new ReentrantLock()
    private val currFillLength = new AtomicLong(0L)
    @volatile
    private var currPublisher: BodyPublisher = _
    @volatile
    private var currPubLength = 0L
    @volatile
    private var currSubscription: Subscription = _

    try subscribeNextOrComplete()
    catch case NonFatal(exc) => signalError(exc)

    override def request(n: Long): Unit =
      if n <= 0
      then signalError(new IllegalArgumentException(s"Cannot use non-positive request ${n}"))
      else {
        var remaining = n

        while //
          !cancelled.get() && !terminated.get()
          && currSubscription != null && remaining > 0
        do {
          val beforeFill = currFillLength.get()
          try {
            currSubscription.request(1)
            remaining -= (currFillLength.get() - beforeFill)
          } //
          catch //
            case NonFatal(exc) => signalError(exc)
        }
      }

    override def cancel(): Unit =
      if (!cancelled.compareAndExchange(false, true) && currSubscription != null)
        currSubscription.cancel()

    /*
     * As a Subscriber[ByteBuffer]
     */

    override def onSubscribe(subscription: Subscription): Unit = {
      requireNonNull(subscription, "subscription must not be null")

      if !cancelled.get() && !terminated.get()
      then {
        subLock.lock()
        try
          currSubscription = subscription
        finally
          subLock.unlock()
      } //
      else //
        subscription.cancel()
    }

    override def onNext(item: ByteBuffer): Unit =
      if (!cancelled.get() && !terminated.get())
        currFillLength.addAndGet(item.capacity())
        try _subscriber.onNext(item)
        catch case NonFatal(exc) => onError(exc)

    override def onError(throwable: Throwable): Unit =
      requireNonNull(throwable)
      cancel()
      signalError(throwable)

    override def onComplete(): Unit =
      subscribeNextOrComplete()

    /*
     * Private helpers
     */

    private def subscribeNextOrComplete(): Unit =
      if (!cancelled.get() && !terminated.get()) {
        if _publishers.hasNext
        then {
          subLock.lock()
          try {
            val prevLength = currPubLength
            currPublisher = _publishers.next()
            currPubLength = currPublisher.contentLength()
            currFillLength.set(0L)
            currPublisher.subscribe(this)
          } //
          catch //
            case NonFatal(exc) => signalError(exc)
          finally //
            subLock.unlock()
        } //
        else //
          signalComplete()
      }

    private inline def signalError(exc: Throwable): Unit =
      if (!terminated.compareAndExchange(false, true))
        _subscriber.onError(exc)

    private inline def signalComplete(): Unit =
      if (!terminated.compareAndExchange(false, true))
        _subscriber.onComplete()

  end ConcatSubscription

end ConcatPublisher
