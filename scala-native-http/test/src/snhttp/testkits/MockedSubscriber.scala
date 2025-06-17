package snhttp.testkits

import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ArrayBuffer

class MockSubscriber[T] extends Subscriber[T] {
  val _received = ArrayBuffer[T]()
  @volatile var completed = false
  @volatile var hasError = false
  @volatile var error: Throwable = null
  @volatile var subscription: Subscription = null
  private val completionLatch = new CountDownLatch(1)
  private val errorLatch = new CountDownLatch(1)

  def received: List[T] = _received.toList

  override def onSubscribe(subscription: Subscription): Unit =
    this.subscription = subscription

  override def onNext(item: T): Unit = synchronized {
    _received += item
  }

  override def onError(throwable: Throwable): Unit = {
    error = throwable
    hasError = true
    errorLatch.countDown()
  }

  override def onComplete(): Unit = {
    completed = true
    completionLatch.countDown()
  }

  def waitForCompletion(timeoutMs: Long): Boolean =
    completionLatch.await(timeoutMs, TimeUnit.MILLISECONDS)

  def waitForError(timeoutMs: Long): Boolean =
    errorLatch.await(timeoutMs, TimeUnit.MILLISECONDS)
}
object MockSubscriber {
  def apply[T](): MockSubscriber[T] = new MockSubscriber[T]()
}
