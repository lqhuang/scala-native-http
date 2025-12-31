package snhttp.jdk.testkits

import java.net.http.HttpResponse.BodySubscriber
import java.nio.ByteBuffer
import java.util.List as JList
import java.util.ArrayList
import java.util.concurrent.{CompletionStage, CompletableFuture}
import java.util.concurrent.Flow.{Subscriber, Subscription, Publisher}
import java.util.function.Consumer

import scala.collection.mutable.ListBuffer

class MockBodySubscriber[T] extends BodySubscriber[T]:
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

object MockBodySubscriber:

  def concatAll(buffers: ListBuffer[ByteBuffer]) = {
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

end MockBodySubscriber
