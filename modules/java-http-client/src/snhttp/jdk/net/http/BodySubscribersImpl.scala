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
import java.util.concurrent.{
  ArrayBlockingQueue,
  CompletableFuture,
  CompletionStage,
  LinkedBlockingQueue,
}
import java.util.function.{Consumer, Function}
import java.util.concurrent.Flow.{Publisher, Subscription, Subscriber}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import java.util.regex.Pattern
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
    new ByteArrayBodySubscriber(finisher)

  def ofFile(file: Path, openOptions: Array[OpenOption]): PathBodySubscriber =
    new PathBodySubscriber(file, openOptions*)

  def ofByteArrayConsumer(consumer: Consumer[Optional[Array[Byte]]]): BodySubscriber[Void] =
    new ConsumerBodySubscriber(consumer)

  def ofInputStream(): BodySubscriber[InputStream] =
    new InputStreamBodySubscriber()

  def ofLines(charset: Charset): BodySubscriber[Stream[String]] = {
    requireNonNull(charset)
    val s = new InputStreamBodySubscriber()
    return new MappingBodySubscriber[InputStream, Stream[String]](
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
    new NullBodySubscriber(Optional.ofNullable(value))

  def discarding(): BodySubscriber[Void] =
    new NullBodySubscriber(Optional.empty())

  def buffering[T](downstream: BodySubscriber[T], bufferSize: Int): BodySubscriber[T] =
    new BufferingBoddySubscriber(downstream, bufferSize)

  def mapping[T, U](
      upstream: BodySubscriber[T],
      mapper: Function[? >: T, ? <: U],
  ): BodySubscriber[U] =
    new MappingBodySubscriber(upstream, mapper)

  def limiting[T](downstream: BodySubscriber[T], capacity: Long): BodySubscriber[T] =
    new LimitingBodySubscriber(downstream, capacity)

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
  private var subscription: Subscription = _

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndExchange(false, true)
    then
      this.subscription = subscription
      subscriber.onSubscribe(this.subscription)
    else //
      subscription.cancel()
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    subscriber.onNext(item)

  override def onError(throwable: Throwable): Unit =
    try //
      subscriber.onError(throwable)
    finally
      cf.completeExceptionally(throwable): Unit

  override def onComplete(): Unit =
    try subscriber.onComplete()
    finally
      try cf.complete(finisher(subscriber)): Unit
      catch case t: Throwable => cf.completeExceptionally(t): Unit

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

    if !subscribed.compareAndExchange(false, true)
    then
      this.subscription = subscription
      subscriber.onSubscribe(this.subscription)
    else //
      subscription.cancel()
  }

  override def onNext(item: JList[ByteBuffer]): Unit = {
    val text = item
      .forEach(bb => sbuilder.append(charset.decode(bb.duplicate()).toString))

    val lines = sbuilder.toString().split(Pattern.quote(lineSeparator), -1)
    // Process all complete lines except the last line.
    // Keep the last line in builder, the last line will be processed in `onComplete`.
    // Avoid splitting the last line with incorrect chunks.
    lines.init.foreach(x => subscriber.onNext(x))

    // Clear processed
    sbuilder.clear()
    sbuilder.append(lines.last)
  }

  override def onError(throwable: Throwable): Unit =
    requireNonNull(throwable)
    try subscriber.onError(throwable)
    finally cf.completeExceptionally(throwable): Unit

  override def onComplete(): Unit =
    try {
      if (sbuilder.nonEmpty) subscriber.onNext(sbuilder.toString)
      subscriber.onComplete()
    } //
    finally //
      cf.complete(finisher(subscriber)): Unit

  override def getBody(): CompletionStage[R] =
    cf

end LineSubscriberWrapper

private[snhttp] class ByteArrayBodySubscriber[T](finisher: Array[Byte] => T)
    extends BodySubscriber[T]:

  requireNonNull(finisher, "finisher can not be null")

  private val cf = new CompletableFuture[T]()
  @volatile
  private var subscription: Subscription = _
  private val subscribed = new AtomicBoolean()

  private val result = new ArrayList[Byte]()
  private val received = new ArrayList[ByteBuffer]()

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndExchange(false, true)
    then
      this.subscription = subscription
      this.subscription.request(1L)
    else //
      subscription.cancel()
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
    subscription.request(1L)
  }

  override def onError(throwable: Throwable): Unit =
    requireNonNull(throwable)
    cf.completeExceptionally(throwable): Unit
    received.clear()

  override def onComplete(): Unit =
    try //
      cf.complete(finisher(merge(received))): Unit
    catch //
      case throwable: Throwable => cf.completeExceptionally(throwable): Unit
    finally //
      received.clear()

  override def getBody(): CompletionStage[T] =
    cf

  private inline def merge(item: JList[ByteBuffer]): Array[Byte] = {
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

end ByteArrayBodySubscriber

private[snhttp] class PathBodySubscriber(file: Path, openOptions: OpenOption*)
    extends BodySubscriber[Path]:

  requireNonNull(file, "file can not be null")
  requireNonNull(openOptions, "openOptions can not be null")

  private val cf = new CompletableFuture[Path]()
  private val subscribed = new AtomicBoolean()
  @volatile
  private var subscription: Subscription = _

  @volatile
  private var fh: FileChannel = _

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndExchange(false, true)
    then {
      this.subscription = subscription
      try
        fh = FileChannel.open(file, openOptions*)
        this.subscription.request(1)
      catch //
        case exc: IOException => onError(exc)
    } //
    else //
      subscription.cancel()
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    item.stream().forEach { buf =>
      requireNonNull(buf)
      if (buf.hasRemaining()) fh.write(buf.duplicate()): Unit
    }
    subscription.request(1)

  override def onError(e: Throwable): Unit =
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

end PathBodySubscriber

private[snhttp] class ConsumerBodySubscriber(consumer: Consumer[Optional[Array[Byte]]])
    extends BodySubscriber[Void]:

  requireNonNull(consumer, "consumer can not be null")

  private val cf = new CompletableFuture[Void]()
  @volatile
  private var subscription: Subscription = _
  private val subscribed = new AtomicBoolean()

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndExchange(false, true)
    then
      this.subscription = subscription
      this.subscription.request(1)
    else //
      subscription.cancel()
  }

  override def onNext(item: JList[ByteBuffer]): Unit = {
    requireNonNull(item)
    item.stream().forEach { buf =>
      requireNonNull(buf)
      val data = new Array[Byte](buf.remaining())
      buf.duplicate().get(data)
      consumer.accept(Optional.of(data))
    }
    subscription.request(1)
  }

  override def onError(throwable: Throwable): Unit =
    requireNonNull(throwable)
    cf.completeExceptionally(throwable): Unit

  override def onComplete(): Unit = {
    consumer.accept(Optional.empty())
    cf.complete(null): Unit
  }

  override def getBody(): CompletionStage[Void] =
    cf

end ConsumerBodySubscriber

private[snhttp] class InputStreamBodySubscriber(capacity: Int = 8)
    extends InputStream
    with BodySubscriber[InputStream]:

  require(capacity > 0, "capacity must be positive")

  private var subscription: Subscription = _
  private var error: Throwable = _
  private val subscribed = new AtomicBoolean(false)

  private val queue = new ArrayBlockingQueue[ByteBuffer](roundToPowerOfTwo(capacity))
  private val closed = new AtomicBoolean(false)

  // sentinel buffer to hold the last buffer (0 byte) until `onComplete` or `onError` is called
  private val lastBuffer: ByteBuffer = ByteBuffer.allocate(0)
  private val currBufferLock = new ReentrantLock()
  @volatile
  private var currBuffer: ByteBuffer = _

  /*
   * Act as a BodySubscriber
   */

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndExchange(false, true) && !closed.get()
    then {
      this.subscription = subscription
      this.subscription.request(1)
    } //
    else //
      subscription.cancel()
  }

  override def onNext(item: JList[ByteBuffer]): Unit = {
    requireNonNull(item)
    item.forEach(buf => requireNonNull(buf): Unit)

    if (!closed.get()) {
      val iter = item.iterator()
      while //
        iter.hasNext()
      do
        queue.put(iter.next()); ()

      subscription.request(Math.max(1, queue.remainingCapacity() - 1))
    }
  }

  override def onComplete(): Unit = {
    if (subscription != null)
      subscription = null
    queue.offer(lastBuffer)
    ()
  }

  override def onError(throwable: Throwable): Unit = {
    if (subscription != null)
      subscription = null

    requireNonNull(throwable)
    error = throwable

    queue.offer(lastBuffer)
    ()
  }

  override def getBody(): CompletionStage[InputStream] =
    CompletableFuture.completedStage(this)

  /*
   * Act as an InputStream
   */

  override def available(): Int =
    if error != null then //
      throw new IOException("Error occurred in InputStreamBodySubscriber", error)
    else if closed.get() then //
      0
    else //
      queue.stream().mapToInt(_.remaining()).sum()

  /*
   * Implementation notes from JDK:
   *
   * 1. The value byte is returned as an int in the range 0 to 255
   * 2. This method blocks until input data is available, the end of the stream is detected, or an exception is thrown.
   */
  override def read(): Int =
    if (error != null)
      throw new IOException("Error occurred in InputStreamBodySubscriber", error)
    if (closed.get()) //
      throw new IOException("InputStream is closed")
    else { //
      currBufferLock.lock()
      try
        if (currBuffer == lastBuffer) //
          -1
        else {
          if (currBuffer == null || !currBuffer.hasRemaining())
            currBuffer = queue.take()

          if currBuffer == lastBuffer
          then -1
          else currBuffer.get().toInt
        }
      finally //
        currBufferLock.unlock()
    }

  override def close(): Unit =
    if (!closed.compareAndExchange(false, true)) {
      if (subscription != null)
        subscription.cancel()

      queue.clear()

      if (error != null) throw new IOException("Error occurred in InputStreamBodySubscriber", error)
    }

  /*
   * Helpers
   */

  private inline def roundToPowerOfTwo(cap: Int): Int = {
    var n = cap - 1
    n |= n >>> 1
    n |= n >>> 2
    n |= n >>> 4
    n |= n >>> 8
    n |= n >>> 16

    if (n <= 1)
      2 // minimum capacity is 2 (1 current, 1 last)
    else if (n >= (1 << 30))
      1 << 30
    else
      n + 1
  }

end InputStreamBodySubscriber

private[snhttp] class PublishingBodySubscriber extends BodySubscriber[Publisher[JList[ByteBuffer]]]:

  private val cf = new CompletableFuture[Publisher[JList[ByteBuffer]]]()
  private val subscribed = new AtomicBoolean()

  @volatile
  private var subscriber: Subscriber[? >: JList[ByteBuffer]] = _

  @volatile
  private var subscription: Subscription = _

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndExchange(false, true)
    then {
      val publisher = new Publisher[JList[ByteBuffer]] {
        private val _subscribed = new AtomicBoolean()

        override def subscribe(s: Subscriber[? >: JList[ByteBuffer]]): Unit =
          if !_subscribed.compareAndExchange(false, true)
          then
            subscriber = s
            s.onSubscribe(subscription)
          else //
            s.onError(new IllegalStateException("Only one subscriber allowed")): Unit
      }

      this.subscription = subscription
      cf.complete(publisher): Unit
    } //
    else //
      subscription.cancel()
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

private[snhttp] class NullBodySubscriber[T](other: Optional[T]) extends BodySubscriber[T]:

  private val cf = new CompletableFuture[T]()
  private val subscribed = new AtomicBoolean()
  @volatile
  private var subscription: Subscription = _

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)
    if !subscribed.compareAndExchange(false, true)
    then
      this.subscription = subscription
      this.subscription.request(1L)
    else //
      subscription.cancel()
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    subscription.request(1L)

  override def onError(throwable: Throwable): Unit =
    cf.completeExceptionally(throwable): Unit

  override def onComplete(): Unit =
    if other.isPresent()
    then cf.complete(other.get()): Unit
    else cf.complete(null.asInstanceOf[T]): Unit

  override def getBody(): CompletionStage[T] = cf

end NullBodySubscriber

private[snhttp] class BufferingBoddySubscriber[T](
    downstream: BodySubscriber[T],
    bufferSize: Int,
) extends BodySubscriber[T] {

  require(bufferSize >= 0, "bufferSize can not be negative or zero")

  @volatile
  private var subscription: Subscription = _
  private val subscribed = new AtomicBoolean()
  private val buffered = ByteBuffer.allocate(bufferSize)

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)
    if !subscribed.compareAndExchange(false, true)
    then
      this.subscription = subscription
      downstream.onSubscribe(subscription)
    else //
      subscription.cancel()
  }

  override def onNext(item: JList[ByteBuffer]): Unit =
    item
      .stream()
      .forEach { buf =>
        requireNonNull(buf)
        if (buf.hasRemaining()) {
          val remaining = buf.remaining()

          var offset = 0
          while (offset < remaining) {
            val space = buffered.remaining()
            val toWrite = Math.min(space, remaining - offset)

            val duplicate = buf.duplicate()
            duplicate.position(buf.position() + offset).limit(buf.position() + offset + toWrite)
            buffered.put(duplicate)
            buffered.flip()

            offset += toWrite
            flushBuffer()
          }
        }

      }

  override def onComplete(): Unit =
    downstream.onComplete()

  override def onError(throwable: Throwable): Unit =
    requireNonNull(throwable)
    downstream.onError(throwable)

  override def getBody(): CompletionStage[T] =
    downstream.getBody()

  private inline def flushBuffer(): Unit =
    if (buffered.hasRemaining())
      try
        val toSend = ByteBuffer.allocate(bufferSize)
        toSend.put(buffered)
        toSend.flip()
        downstream.onNext(JList.of(toSend))
      catch case t: Throwable => downstream.onError(t)
      finally {
        buffered.clear()
        buffered.position(0): Unit
      }

}

private[snhttp] class MappingBodySubscriber[T, U](
    upstream: BodySubscriber[T],
    mapper: Function[? >: T, ? <: U],
) extends BodySubscriber[U]:

  requireNonNull(upstream, "upstream can not be null")
  requireNonNull(mapper, "mapper can not be null")

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

end MappingBodySubscriber

private[snhttp] class LimitingBodySubscriber[T](
    subscriber: BodySubscriber[T],
    capacity: Long,
) extends BodySubscriber[T]:

  requireNonNull(subscriber, "subscriber can not be null")
  require(capacity >= 0, "capacity must not be negative")

  @volatile
  private var subscription: Subscription = _
  private val subscribed = new AtomicBoolean()
  private var bytesReceived: Long = 0L

  override def onSubscribe(subscription: Subscription): Unit = {
    requireNonNull(subscription)

    if !subscribed.compareAndExchange(false, true)
    then
      this.subscription = subscription
      subscriber.onSubscribe(subscription)
    else //
      subscription.cancel()
  }

  override def onNext(item: JList[ByteBuffer]): Unit = {
    var itemBytes = 0L
    item.forEach(buf => itemBytes += buf.remaining())
    bytesReceived += itemBytes
    if bytesReceived > capacity
    then
      subscription.cancel()
      subscriber.onError(
        new IOException(s"Body exceeds capacity limit of ${capacity} bytes"),
      )
    else //
      subscriber.onNext(item)
  }

  override def onComplete(): Unit =
    subscriber.onComplete()

  override def onError(throwable: Throwable): Unit =
    subscriber.onError(throwable)

  override def getBody(): CompletionStage[T] =
    subscriber.getBody()

end LimitingBodySubscriber
