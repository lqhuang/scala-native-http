package snhttp.jdk.net.http.internal

import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.List as JList
import java.util.Objects.requireNonNull
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.concurrent.{ArrayBlockingQueue, CompletableFuture, CompletionStage}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock

import _root_.snhttp.jdk.net.http.InputStreamBodySubscriber

class CurlBodyUploader(maxCachedBytes: Long) extends Subscriber[ByteBuffer]:

  private val stream = new InputStreamBodySubscriber()

  override def onSubscribe(sub: Subscription): Unit =
    stream.onSubscribe(sub)

  override def onNext(item: ByteBuffer): Unit =
    stream.onNext(JList.of(item))

  override def onError(t: Throwable): Unit =
    stream.onError(t)

  override def onComplete(): Unit =
    stream.onComplete()

  def getBody(): CompletionStage[SeekableInputStream] =
    CompletableFuture.completedStage(DelegateSeekableInputStream(maxCachedBytes, stream))
