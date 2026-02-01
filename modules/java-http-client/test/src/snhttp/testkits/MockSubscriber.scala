package snhttp.jdk.testkits

import java.nio.ByteBuffer
import java.util.concurrent.Flow
import java.util.function.Consumer

class MockSubscriber[T]() extends Flow.Subscriber[ByteBuffer]:

  var received = List.empty[ByteBuffer]
  @volatile var completed = false
  @volatile var error: Option[Throwable] = None
  @volatile var subscription: Flow.Subscription = null

  override def onSubscribe(subscription: Flow.Subscription): Unit =
    this.subscription = subscription
    subscription.request(1)

  override def onNext(item: ByteBuffer): Unit =
    received = received :+ item
    subscription.request(1)

  override def onError(throwable: Throwable): Unit =
    this.error = Some(throwable)

  override def onComplete(): Unit =
    this.completed = true

object MockSubscriber:

  def concatAll(buffers: Seq[ByteBuffer]) = {
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
