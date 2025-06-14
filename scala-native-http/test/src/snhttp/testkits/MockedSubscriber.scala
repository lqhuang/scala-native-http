package snhttp.testkits

import java.util.concurrent.Flow
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ListBuffer

class MockedSubscriber[T] extends Flow.Subscriber[T] {
  val received = ListBuffer[T]()
  val completed = new AtomicBoolean(false)
  val hasError = new AtomicBoolean(false)
  var error: Throwable = null
  var subscription: Flow.Subscription = null
  private val completionLatch = new CountDownLatch(1)
  private val errorLatch = new CountDownLatch(1)

  override def onSubscribe(subscription: Flow.Subscription): Unit =
    this.subscription = subscription

  override def onNext(item: T): Unit = synchronized {
    received += item
  }

  override def onError(throwable: Throwable): Unit = {
    error = throwable
    hasError.set(true)
    errorLatch.countDown()
  }

  override def onComplete(): Unit = {
    completed.set(true)
    completionLatch.countDown()
  }

  def waitForCompletion(timeoutMs: Long): Boolean =
    completionLatch.await(timeoutMs, TimeUnit.MILLISECONDS)

  def waitForError(timeoutMs: Long): Boolean =
    errorLatch.await(timeoutMs, TimeUnit.MILLISECONDS)
}
