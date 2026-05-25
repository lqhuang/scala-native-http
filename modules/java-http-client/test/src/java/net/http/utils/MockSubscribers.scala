package snhttp.java.net.http.utils

import java.net.http.HttpResponse.BodySubscriber
import java.nio.ByteBuffer
import java.util.{List as JList, ArrayList}
import java.util.concurrent.{CompletionStage, CompletableFuture}
import java.util.concurrent.Flow.{Subscriber, Subscription, Publisher}
import java.util.function.Consumer

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.*

import utest.assert

class SPException extends RuntimeException("Test exception")

private def concatBuffers(buffers: Iterable[ByteBuffer]): Array[Byte] = {
  val size = buffers.map(b => b.remaining()).sum
  val bytes = new Array[Byte](size)
  var offset = 0

  buffers.foreach { buffer =>
    val total = buffer.limit()
    buffer.get(bytes, offset, total)
    offset += total
  }

  bytes
}

class MockSubscription() extends Subscription:

  @volatile var received: Long = 0
  @volatile var cancelled: Boolean = false

  override def request(n: Long): Unit =
    received += n

  override def cancel(): Unit =
    cancelled = true

end MockSubscription

class MockSubscriber[T](
    @volatile var request: Boolean = false,
    @volatile var throwOnCall: Boolean = false,
) extends Subscriber[T]:

  val received = ListBuffer[T]()
  @volatile var nexts: Int = 0
  @volatile var last: Int = 0 // Requires that onNexts are in numeric order
  @volatile var completes: Int = 0
  @volatile var errors: Int = 0
  @volatile var lastError: Throwable = _
  @volatile var sub: Subscription = _

  def threadUnexpectedException(t: Throwable): Unit =
    t match {
      case t: RuntimeException => throw t
      case t: Error            => throw t
      case t                   => throw new AssertionError(s"unexpected exception: $t", t)
    }

  override def onSubscribe(subscription: Subscription): Unit = {
    assert(subscription != null)
    assert(sub == null)
    sub = subscription
    if (throwOnCall) throw new SPException()
    if (request) subscription.request(1)
  }

  override def onNext(item: T): Unit = synchronized {
    assert(item != null)
    nexts += 1
    received.append(item)
    assert(last + 1 == nexts)
    last = nexts
    notifyAll()
    if (request) sub.request(1)
    if (throwOnCall) throw new SPException()
  }

  override def onError(throwable: Throwable): Unit = synchronized {
    assert(completes == 0)
    assert(errors == 0)
    errors += 1
    lastError = throwable
    notifyAll()
  }

  override def onComplete(): Unit = synchronized {
    assert(completes == 0)
    assert(errors == 0)
    completes += 1
    notifyAll()
  }

  // def awaitComplete(assertFailTimeouts: Duration = 3.seconds): Unit = synchronized {

  //   while completed == false && error == null
  //   do {
  //     System.err.println("Waiting for completion...")
  //     // if (System.currentTimeMillis() > deadline) {
  //     //   throw new AssertionError(
  //     //     s"Timeout waiting for completion or error after ${assertFailTimeouts.toSeconds}s",
  //     //   )
  //     // }
  //     try wait(500L)
  //     catch
  //       case _: InterruptedException         => ()
  //       case _: IllegalMonitorStateException => ()
  //   }
  // }

  def awaitSubscribe(): Unit = synchronized {
    if (sub == null) {
      while ({
        try wait(300L)
        catch case ex: Exception => threadUnexpectedException(ex)

        sub == null
      }) {}
    }
  }

  def awaitNext(n: Int): Unit = synchronized {
    if (nexts < n) {
      while ({
        try wait(300L)
        catch case ex: Exception => threadUnexpectedException(ex)

        nexts < n
      }) {}
    }
  }

  def awaitComplete(): Unit = synchronized {
    if (completes == 0 && errors == 0) {
      while ({
        try wait(300L)
        catch case ex: Exception => threadUnexpectedException(ex)

        completes == 0 && errors == 0
      }) {}
    }
  }

  def awaitError(): Unit = synchronized {
    if (errors == 0) {
      while ({
        try wait(300L)
        catch case ex: Exception => threadUnexpectedException(ex)

        errors == 0
      }) {}
    }
  }

  def concatReceived() = {
    require(
      completes > 0 || errors > 0,
      "Cannot concatenate received buffers until completion or error",
    )

    received.mkString
  }

end MockSubscriber

case class MockBodySubscriber[T]() extends BodySubscriber[T]:
  val cf = new CompletableFuture[T]()
  val received = new ListBuffer[ByteBuffer]()
  @volatile var completed = false
  @volatile var error: Option[Throwable] = None
  @volatile var subscription: Subscription = null

  override def onSubscribe(subscription: Subscription): Unit =
    this.subscription = subscription

  override def onNext(item: JList[ByteBuffer]): Unit =
    item.forEach(buffer => received.append(buffer)): Unit

  override def onError(throwable: Throwable): Unit =
    this.error = Some(throwable)

  override def onComplete(): Unit =
    this.completed = true

  override def getBody(): CompletionStage[T] =
    cf

  def concatReceived() = {
    require(
      completed || error.isDefined,
      "Cannot concatenate received buffers until completion or error",
    )
    concatBuffers(received)
  }

end MockBodySubscriber
