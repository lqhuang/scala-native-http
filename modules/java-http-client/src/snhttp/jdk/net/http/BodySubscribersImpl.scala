package snhttp.jdk.net.http

import java.io.IOException
import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.net.http.HttpResponse.BodySubscriber
import java.nio.ByteBuffer
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.channels.FileChannel
import java.nio.file.{Path, OpenOption}
import java.util.{ArrayList, Optional, List as JList}
import java.util.Objects.requireNonNull
import java.util.concurrent.{CompletableFuture, CompletionStage, Executor, Flow}
import java.util.function.{Consumer, Function}
import java.util.concurrent.Flow.{Publisher, Subscription, Subscriber}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Stream

object BodySubscribersImpl:

  def fromSubscriber[S <: Subscriber[? >: JList[ByteBuffer]], R](
      subscriber: S,
      finisher: Function[? >: S, ? <: R],
  ): BodySubscriber[R] =
    new SubscriberWrapper(subscriber, finisher)

  def fromLineSubscriber[S <: Subscriber[? >: String], R](
      subscriber: S,
      finisher: Function[? >: S, ? <: R],
      charset: Charset,
      eol: String,
  ): LineSubscriberWrapper[S, R] =
    new LineSubscriberWrapper(subscriber, finisher, charset, eol)

  def ofByteArray[T](finisher: Array[Byte] => T): BodySubscriber[T] =
    new ByteArraySubscriber(finisher)

  def ofFile(file: Path, openOptions: Array[OpenOption]): PathSubscriber =
    new PathSubscriber(file, openOptions*)

  def ofByteArrayConsumer(consumer: Consumer[Optional[Array[Byte]]]): BodySubscriber[Void] =
    new ConsumerSubscriber(consumer)

  def ofInputStream(): BodySubscriber[InputStream] =
    new InputStreamSubscriber()

  def ofLines(charset: Charset): BodySubscriber[Stream[String]] = {
    requireNonNull(charset)
    val s = new InputStreamSubscriber()
    return new MappingSubscriber[InputStream, Stream[String]](
      s,
      (stream: InputStream) =>
        new BufferedReader(new InputStreamReader(stream, charset))
          .lines()
          .onClose(() => stream.close()),
    )
  }

  def ofPublisher(): BodySubscriber[Publisher[JList[ByteBuffer]]] =
    new PublishingBodySubscriber()

  def replacing[U](value: U): BodySubscriber[U] =
    new NullSubscriber(Optional.ofNullable(value))

  def discarding(): BodySubscriber[Void] =
    new NullSubscriber(Optional.empty())

  def buffering[T](downstream: BodySubscriber[T], bufferSize: Int): BodySubscriber[T] =
    new BufferingSubscriber(downstream, bufferSize)

  def mapping[T, U](
      upstream: BodySubscriber[T],
      mapper: Function[? >: T, ? <: U],
  ): BodySubscriber[U] =
    new MappingSubscriber(upstream, mapper)

  def limiting[T](downstream: BodySubscriber[T], capacity: Long): BodySubscriber[T] =
    new LimitingSubscriber(downstream, capacity)

end BodySubscribersImpl

private[snhttp] class SubscriberWrapper[S <: Subscriber[? >: JList[ByteBuffer]], R](
    subscriber: S,
    finisher: Function[? >: S, ? <: R],
) extends BodySubscriber[R]:

  requireNonNull(subscriber, "subscriber can not be null")
  requireNonNull(finisher, "finisher can not be null")

  private val cf = new CompletableFuture[R]()
  private val subscribed = new AtomicBoolean()
  @volatile
  private var subscription: Subscription = null

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true)
    then subscription.cancel()
    else {
      this.subscription = subscription
      subscriber.onSubscribe(this.subscription)
    }
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    try subscriber.onNext(item)
    catch { case t: Throwable => subscription.cancel(); onError(t) }

  override def onError(throwable: Throwable): Unit =
    try subscriber.onError(throwable)
    finally cf.completeExceptionally(throwable): Unit

  override def onComplete(): Unit =
    try subscriber.onComplete()
    finally
      try cf.completeAsync(() => finisher(subscriber)): Unit
      catch { case t: Throwable => cf.completeExceptionally(t): Unit }

  override def getBody(): CompletionStage[R] = cf

end SubscriberWrapper

private[snhttp] class LineSubscriberWrapper[S <: Subscriber[? >: String], R](
    subscriber: S,
    finisher: Function[? >: S, ? <: R],
    charset: Charset,
    eol: String,
) extends BodySubscriber[R]:

  requireNonNull(subscriber, "subscriber can not be null")
  requireNonNull(finisher, "finisher can not be null")
  requireNonNull(charset, "charset can not be null")

  if (eol != null && eol.isEmpty)
    throw new IllegalArgumentException("Line separator cannot be empty")

  private val cf = new CompletableFuture[R]()
  private val subscribed = new AtomicBoolean()
  private val sbuilder = new StringBuilder()
  private val lineSeparator = if eol == null then System.lineSeparator() else eol

  @volatile
  private var subscription: Subscription = _

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true)
    then subscription.cancel()
    else {
      this.subscription = subscription
      subscriber.onSubscribe(this.subscription)
    }
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    try {
      val text = item
        .stream()
        .map(bb => charset.decode(bb.duplicate()).toString)
        .reduce("", (a, b) => a + b)
      sbuilder.append(text)
      val lines = sbuilder.toString.split(lineSeparator, -1)
      // Process all complete lines except the last line
      lines.init.foreach(subscriber.onNext(_))
      // Keep the last line in builder, the last line will be processed in `onComplete`
      sbuilder.clear()
      sbuilder.append(lines.last)
    } catch {
      case t: Throwable => onError(t)
    }

  override def onComplete(): Unit =
    try {
      if (sbuilder.nonEmpty) subscriber.onNext(sbuilder.toString)
      subscriber.onComplete()
    } finally
      try
        cf.complete(finisher(subscriber)): Unit
      catch {
        case t: Throwable => cf.completeExceptionally(t): Unit
      }

  override def onError(throwable: Throwable): Unit =
    try {
      subscription.cancel()
      subscriber.onError(throwable)
    } finally cf.completeExceptionally(throwable): Unit

  override def getBody(): CompletionStage[R] = cf

end LineSubscriberWrapper

private[snhttp] class ByteArraySubscriber[T](finisher: Array[Byte] => T) extends BodySubscriber[T]:

  requireNonNull(finisher, "finisher can not be null")

  private val cf = new CompletableFuture[T]()
  @volatile private var subscription: Subscription = null
  private val subscribed = new AtomicBoolean()

  private val result = new ArrayList[Byte]()
  private val received = new ArrayList[ByteBuffer]()

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true)
    then subscription.cancel()
    else
      this.subscription = subscription
      try subscription.request(1L)
      catch case t: Throwable => onError(t)
  }

  override def onNext(item: JList[ByteBuffer]): Unit = {
    requireNonNull(item)
    item
      .forEach { buf =>
        requireNonNull(buf)
        if (buf.hasRemaining()) {
          val success = received.add(buf.duplicate())
          if (!success) throw new IllegalStateException("Buffer addition failed")
        }
      }
    this.subscription.request(1L)
  }

  override def onComplete(): Unit =
    try
      cf.complete(finisher(merge(received))): Unit
    catch {
      case t: Throwable => cf.completeExceptionally(t): Unit
    } finally
      received.clear()

  override def onError(throwable: Throwable): Unit =
    cf.completeExceptionally(throwable): Unit
    received.clear()

  override def getBody(): CompletionStage[T] =
    cf

  private def merge(item: JList[ByteBuffer]): Array[Byte] = {
    val size = item.stream().mapToInt(_.remaining()).sum()
    val bytes = new Array[Byte](size)
    var offset = 0

    item.stream().forEach { buf =>
      val left = buf.remaining()
      buf.get(bytes, offset, left)
      offset += left
    }

    bytes
  }

end ByteArraySubscriber

private[snhttp] class PathSubscriber(file: Path, openOptions: OpenOption*)
    extends BodySubscriber[Path]:

  requireNonNull(file, "file can not be null")
  requireNonNull(openOptions, "openOptions can not be null")

  private val cf = new CompletableFuture[Path]()
  private val subscribed = new AtomicBoolean()
  @volatile
  private var subscription: Subscription = null

  @volatile
  private var fh: FileChannel = null

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true)
    then subscription.cancel()
    else {
      this.subscription = subscription
      try
        fh = FileChannel.open(file, openOptions*)
      catch {
        case exc: IOException =>
          subscription.cancel()
          cf.completeExceptionally(exc)
          return
      }
      this.subscription.request(1)
    }
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    try
      item.stream().forEach { buf =>
        requireNonNull(buf)
        if (buf.hasRemaining()) fh.write(buf.duplicate()): Unit
      }
    catch {
      case exc: IOException =>
        closeFileChannel()
        subscription.cancel()
        cf.completeExceptionally(exc)
        return
    }
    subscription.request(1)

  override def onError(e: Throwable): Unit =
    subscription.cancel()
    closeFileChannel()
    cf.completeExceptionally(e): Unit

  override def onComplete(): Unit =
    closeFileChannel()
    cf.complete(file): Unit

  override def getBody(): CompletionStage[Path] =
    cf

  private def closeFileChannel(): Unit =
    if (fh != null) {
      try fh.close()
      catch { case t: Throwable => () }
    }

end PathSubscriber

private[snhttp] class ConsumerSubscriber(consumer: Consumer[Optional[Array[Byte]]])
    extends BodySubscriber[Void]:

  private val cf = new CompletableFuture[Void]()
  @volatile
  private var subscription: Subscription = null
  private val subscribed = new AtomicBoolean()

  override def getBody(): CompletionStage[Void] = cf

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true)
    then subscription.cancel()
    else
      this.subscription = subscription
      this.subscription.request(1)
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    requireNonNull(item)
    item.stream().forEach { buf =>
      requireNonNull(buf)
      val data = new Array[Byte](buf.remaining())
      buf.duplicate().get(data)
      try
        consumer.accept(Optional.of(data))
      catch {
        case t: Throwable =>
          subscription.cancel()
          onError(t)
      }
    }
    subscription.request(1)

  override def onError(throwable: Throwable): Unit =
    requireNonNull(throwable)
    cf.completeExceptionally(throwable): Unit

  override def onComplete(): Unit =
    try
      consumer.accept(Optional.empty())
    catch {
      case t: Throwable =>
        subscription.cancel()
        onError(t)
    }
    cf.complete(null): Unit

end ConsumerSubscriber

private[snhttp] class InputStreamSubscriber extends InputStream with BodySubscriber[InputStream]:

  private val cf = new CompletableFuture[InputStream]()
  @volatile private var subscription: Subscription = null
  private val subscribed = new AtomicBoolean(false)
  // TODO: Improvable by using RingBuffer or ArrayBlockingQueue
  private val buffer = new ArrayList[ByteBuffer]()
  private var current: ByteBuffer = null
  private val streamClosed = new AtomicBoolean(false)

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true) || streamClosed.get()
    then {
      subscription.cancel()
      cf.complete(this): Unit
    } else {
      this.subscription = subscription
      try {
        this.subscription.request(1)
        cf.complete(this): Unit
      } catch
        case exc: Throwable =>
          try close()
          catch case _: IOException => ()
          finally {
            onError(exc)
            subscription.cancel()
          }
    }
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    if (!streamClosed.get()) {
      buffer.addAll(item)
      subscription.request(1)
    }

  override def onComplete(): Unit =
    streamClosed.compareAndExchange(false, true): Unit
    cf.complete(this): Unit

  override def onError(throwable: Throwable): Unit =
    streamClosed.compareAndExchange(false, true)
    cf.completeExceptionally(throwable): Unit

  override def read(): Int = {
    if (streamClosed.get()) return -1

    if (current == null || !current.hasRemaining()) {
      current =
        if buffer.isEmpty
        then null
        else buffer.remove(0)
      if (current == null) return -1
    }

    current.get() & 0xff
  }

  override def getBody(): CompletionStage[InputStream] = cf

  override def close(): Unit =
    streamClosed.compareAndExchange(false, true)
    if (subscribed.get())
      subscription.cancel()

end InputStreamSubscriber

private[snhttp] class PublishingBodySubscriber extends BodySubscriber[Publisher[JList[ByteBuffer]]]:

  private val cf = new CompletableFuture[Publisher[JList[ByteBuffer]]]()
  private val subscribed = new AtomicBoolean()
  @volatile private var subscription: Subscription = null

  private var publisher: Publisher[JList[ByteBuffer]] = null
  private var subscriber: Subscriber[? >: JList[ByteBuffer]] = null

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true)
    then subscription.cancel()
    else {
      this.subscription = subscription
      this.publisher = new Publisher[JList[ByteBuffer]] {
        override def subscribe(s: Subscriber[? >: JList[ByteBuffer]]): Unit = {
          subscriber = s
          s.onSubscribe(subscription)
        }
      }
      cf.complete(publisher): Unit
    }
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    if (subscriber != null) subscriber.onNext(item)

  override def onComplete(): Unit =
    if (subscriber != null) subscriber.onComplete()

  override def onError(throwable: Throwable): Unit =
    if (subscriber != null) subscriber.onError(throwable)
    cf.completeExceptionally(throwable): Unit

  override def getBody(): CompletionStage[Publisher[JList[ByteBuffer]]] =
    cf

end PublishingBodySubscriber

private[snhttp] class NullSubscriber[T](other: Optional[T]) extends BodySubscriber[T]:

  private val cf = new CompletableFuture[T]()
  private val subscribed = new AtomicBoolean()
  @volatile private var subscription: Subscription = null

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if (!subscribed.compareAndSet(false, true)) subscription.cancel()
    else
      this.subscription = subscription
      try subscription.request(1L)
      catch case t: Throwable => onError(t)
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    try subscription.request(1L)
    catch case t: Throwable => onError(t)

  override def onError(throwable: Throwable): Unit =
    cf.completeExceptionally(throwable): Unit

  override def onComplete(): Unit =
    if other.isPresent()
    then cf.complete(other.get()): Unit
    else cf.complete(null.asInstanceOf[T]): Unit

  override def getBody(): CompletionStage[T] = cf

end NullSubscriber

private[snhttp] class BufferingSubscriber[T](
    downstream: BodySubscriber[T],
    bufferSize: Int,
) extends BodySubscriber[T] {

  require(bufferSize >= 0, "bufferSize can not be negative or zero")

  @volatile
  private var subscription: Subscription = null
  private val subscribed = new AtomicBoolean()

  // TODO: Improvable by using RingBuffer or ArrayBlockingQueue
  private val buffer = new ArrayList[ByteBuffer]()
  private var bufferedBytes = 0

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true)
    then subscription.cancel()
    else
      this.subscription = subscription
      downstream.onSubscribe(subscription)
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    writeBuffer(item)
    if (bufferedBytes >= bufferSize) flushBuffer()

  override def onComplete(): Unit =
    flushBuffer()
    downstream.onComplete()

  override def onError(throwable: Throwable): Unit =
    downstream.onError(throwable)

  override def getBody(): CompletionStage[T] =
    downstream.getBody()

  private inline def writeBuffer(item: JList[ByteBuffer]): Unit =
    item
      .stream()
      .forEach { buf =>
        buffer.add(buf.duplicate())
        bufferedBytes += buf.remaining()
      }

  private inline def flushBuffer(): Unit =
    if (!buffer.isEmpty)
      try downstream.onNext(buffer)
      finally {
        buffer.clear()
        bufferedBytes = 0
      }

}

private[snhttp] class MappingSubscriber[T, U](
    upstream: BodySubscriber[T],
    mapper: Function[? >: T, ? <: U],
) extends BodySubscriber[U]:

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)
    upstream.onSubscribe(subscription)
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    upstream.onNext(item)

  override def onError(throwable: Throwable): Unit =
    upstream.onError(throwable)

  override def onComplete(): Unit =
    upstream.onComplete()

  override def getBody(): CompletionStage[U] =
    upstream.getBody().thenApply(mapper)

end MappingSubscriber

private[snhttp] class LimitingSubscriber[T](
    subscriber: BodySubscriber[T],
    capacity: Long,
) extends BodySubscriber[T]:

  requireNonNull(subscriber, "subscriber can not be null")
  require(capacity >= 0, "capacity must not be negative")

  @volatile
  private var subscription: Subscription = null
  private val subscribed = new AtomicBoolean()
  private var bytesReceived: Long = 0L

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndSet(false, true)
    then subscription.cancel()
    else
      this.subscription = subscription
      subscriber.onSubscribe(subscription)
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    var itemBytes = 0L
    item.forEach(buf => itemBytes += buf.remaining())
    bytesReceived += itemBytes
    if bytesReceived > capacity
    then
      subscription.cancel()
      subscriber.onError(
        new IOException(s"Body exceeds capacity limit of $capacity bytes"),
      )
    else subscriber.onNext(item)

  override def onComplete(): Unit =
    subscriber.onComplete()

  override def onError(throwable: Throwable): Unit =
    subscriber.onError(throwable)

  override def getBody(): CompletionStage[T] =
    subscriber.getBody()

end LimitingSubscriber
