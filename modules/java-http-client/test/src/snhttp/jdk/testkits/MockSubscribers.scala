package snhttp.jdk.testkits

import java.net.http.HttpResponse.BodySubscriber
import java.nio.ByteBuffer
import java.util.{List as JList, ArrayList}
import java.util.concurrent.{CompletionStage, CompletableFuture}
import java.util.concurrent.Flow.{Subscriber, Subscription, Publisher}
import java.util.function.Consumer

import scala.collection.mutable.ListBuffer

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

class MockStringSubscriber() extends Subscriber[String]:

  val received = ListBuffer[String]()
  @volatile var completed = false
  @volatile var error: Option[Throwable] = None
  @volatile var subscription: Subscription = null

  override def onSubscribe(subscription: Subscription): Unit =
    this.subscription = subscription
    subscription.request(1)

  override def onNext(item: String): Unit =
    received.append(item)
    subscription.request(1)

  override def onError(throwable: Throwable): Unit =
    this.error = Some(throwable)

  override def onComplete(): Unit =
    this.completed = true

  def concatReceived() = {
    require(
      completed || error.isDefined,
      "Cannot concatenate received buffers until completion or error",
    )

    received.mkString
  }

end MockStringSubscriber

case class MockByteBufSubscriber() extends Subscriber[ByteBuffer]:

  val received = ListBuffer[ByteBuffer]()
  @volatile var completed = false
  @volatile var error: Option[Throwable] = None
  @volatile var subscription: Subscription = null

  override def onSubscribe(subscription: Subscription): Unit =
    this.subscription = subscription
    subscription.request(1)

  override def onNext(item: ByteBuffer): Unit =
    received.append(item)
    subscription.request(1)

  override def onError(throwable: Throwable): Unit =
    this.error = Some(throwable)

  override def onComplete(): Unit =
    this.completed = true

  def concatReceived() =
    require(
      completed || error.isDefined,
      "Cannot concatenate received buffers until completion or error",
    )
    concatBuffers(received)

end MockByteBufSubscriber

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
