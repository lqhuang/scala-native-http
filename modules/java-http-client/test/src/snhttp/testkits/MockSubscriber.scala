package snhttp.jdk.testkits

import java.nio.ByteBuffer
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.function.Consumer

import scala.collection.mutable.ListBuffer

class MockSubscriber() extends Subscriber[ByteBuffer]:

  val received = new ListBuffer[ByteBuffer]()
  @volatile var completed = false
  @volatile var error: Option[Throwable] = None
  @volatile var subscription: Subscription = null

  override def onSubscribe(subscription: Subscription): Unit =
    this.subscription = subscription

  override def onNext(item: ByteBuffer): Unit =
    received.append(item): Unit

  override def onError(throwable: Throwable): Unit =
    this.error = Some(throwable)

  override def onComplete(): Unit =
    this.completed = true

object MockSubscriber:

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

end MockSubscriber
