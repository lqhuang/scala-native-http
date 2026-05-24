package snhttp.java.net.http.utils

import java.net.http.HttpResponse.BodySubscriber
import java.nio.ByteBuffer
import java.util.{List as JList, ArrayList}
import java.util.concurrent.{CompletionStage, CompletableFuture}
import java.util.concurrent.Flow.{Subscriber, Subscription, Publisher}
import java.util.function.Consumer

import scala.collection.mutable.ListBuffer
import scala.util.boundary
import scala.util.boundary.break

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

case class MockSubscription() extends Subscription:

  @volatile var received: Long = 0
  @volatile var cancelled: Boolean = false

  override def request(n: Long): Unit =
    received += n

  override def cancel(): Unit =
    cancelled = true

end MockSubscription

class MockSubscriber[T](
    @volatile var request: Boolean = true,
    @volatile var throwOnCall: Boolean = false,
) extends Subscriber[T]:

  val received = ListBuffer[T]()
  @volatile var count: Int = 0
  @volatile var completed = false
  @volatile var error: Throwable = _
  @volatile var subscription: Subscription = _

  override def onSubscribe(subscription: Subscription): Unit =
    assert(subscription != null)
    this.subscription = subscription
    if (throwOnCall) throw new SPException()
    if (request) subscription.request(1)

  override def onNext(item: T): Unit =
    synchronized {
      count += 1
    }
    received.append(item)
    if (request) subscription.request(1)
    if (throwOnCall) throw new SPException()

  override def onError(throwable: Throwable): Unit =
    assert(!completed)
    assert(error != null)
    synchronized {
      error = throwable
    }

  override def onComplete(): Unit =
    assert(!completed)
    assert(error == null)
    synchronized {
      completed = true
    }

  def awaitComplete(): Unit =
    boundary {
      while (completed == false && error == null)
        synchronized {
          try {
            println("Waiting for completion...")
            wait(3L)
          } catch case ex: Exception => break(())
        }
    }

  def concatReceived() = {
    require(
      completed || (error == null),
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
