package snhttp.jdk.net.http.internal

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpResponse.BodySubscriber
import java.util.List as JList
import java.util.concurrent.CompletionStage
import java.util.concurrent.Flow.{Subscriber, Subscription, Publisher}
import java.util.concurrent.atomic.AtomicBoolean

class CurlBodySubscriber[T](wrapped: BodySubscriber[T]) extends Subscriber[ByteBuffer]:

  override def onSubscribe(sub: Subscription): Unit =
    wrapped.onSubscribe(sub)

  override def onNext(item: ByteBuffer): Unit =
    wrapped.onNext(JList.of(item))

  override def onError(t: Throwable): Unit =
    wrapped.onError(t)

  override def onComplete(): Unit =
    wrapped.onComplete()

  def getBody(): CompletionStage[T] =
    wrapped.getBody()

end CurlBodySubscriber
