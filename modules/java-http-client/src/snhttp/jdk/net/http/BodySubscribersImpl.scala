package snhttp.jdk.net.http

import java.io.IOException
import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.net.http.HttpResponse.BodySubscriber
import java.nio.ByteBuffer
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.channels.FileChannel
import java.nio.file.{Path, OpenOption}
import java.util.{ArrayList, Optional}
import java.util.List as JList
import java.util.Objects.requireNonNull
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.Flow
import java.util.concurrent.Flow.{Publisher, Subscription, Subscriber}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.{Consumer, Function}
import java.util.stream.Stream

import scala.io.Source

object BodySubscribersImpl {

  final class SubscriberAdapter[S <: Subscriber[? >: JList[ByteBuffer]], R](
      private val subscriber: S,
      private val finisher: Function[? >: S, ? <: R],
  ) extends BodySubscriber[R] {
    private val cf = new CompletableFuture[R]()
    private var subscription: Subscription = _
    private val subscribed = new AtomicBoolean()

    override def onSubscribe(subscription: Subscription): Unit =
      if !subscribed.compareAndSet(false, true)
      then subscription.cancel()
      else {
        this.subscription = subscription
        subscriber.onSubscribe(this.subscription)
      }

    override def onNext(item: JList[ByteBuffer]): Unit =
      try subscriber.onNext(item)
      catch { case t: Throwable => subscription.cancel(); onError(t) }

    override def onError(throwable: Throwable): Unit =
      try subscriber.onError(throwable)
      finally cf.completeExceptionally(throwable)

    override def onComplete(): Unit =
      try subscriber.onComplete()
      finally
        try cf.completeAsync(() => finisher(subscriber))
        catch { case t: Throwable => cf.completeExceptionally(t) }

    override def getBody(): CompletionStage[R] = cf
  }

  def fromSubscriber[S <: Subscriber[? >: JList[ByteBuffer]], R](
      subscriber: S,
      finisher: Function[? >: S, ? <: R],
  ): BodySubscriber[R] =
    requireNonNull(subscriber, "subscriber can not be null")
    requireNonNull(finisher, "finisher can not be null")
    new SubscriberAdapter(subscriber, finisher)

  final class LineSubscriberAdapter[S <: Subscriber[? >: String], R](
      subscriber: S,
      finisher: Function[? >: S, ? <: R],
      charset: Charset,
      eol: String,
  ) extends BodySubscriber[R] {
    private val cf = new CompletableFuture[R]()
    private var subscription: Subscription = _
    private val subscribed = new AtomicBoolean()
    private val buffer = new StringBuilder()
    private val lineSeparator = if (eol == null) System.lineSeparator() else eol

    override def onSubscribe(subscription: Subscription): Unit =
      if !subscribed.compareAndSet(false, true)
      then subscription.cancel()
      else {
        this.subscription = subscription
        subscriber.onSubscribe(this.subscription)
      }

    override def onNext(item: JList[ByteBuffer]): Unit =
      try {
        val text = item
          .stream()
          .map(bb => charset.decode(bb).toString)
          .reduce("", (a, b) => a + b)
        buffer.append(text)
        val lines = buffer.toString.split(lineSeparator, -1)
        // Process all complete lines except the last part
        lines.init.foreach(subscriber.onNext(_))
        // Keep the last part (incomplete line) in buffer.
        // The last part will be processed in onComplete
        buffer.clear()
        buffer.append(lines.last)
      } catch {
        case t: Throwable => onError(t)
      }

    override def onComplete(): Unit =
      try {
        // Send any remaining content as the last line
        if (buffer.nonEmpty) subscriber.onNext(buffer.toString)
        subscriber.onComplete()
      } finally
        try cf.complete(finisher(subscriber))
        catch { case t: Throwable => cf.completeExceptionally(t) }

    override def onError(throwable: Throwable): Unit =
      try {
        subscription.cancel()
        subscriber.onError(throwable)
      } finally cf.completeExceptionally(throwable)

    override def getBody(): CompletionStage[R] = cf
  }

  def fromLineSubscriber[S <: Subscriber[? >: String], R](
      subscriber: S,
      finisher: Function[? >: S, ? <: R],
      charset: Charset,
      eol: String,
  ): LineSubscriberAdapter[S, R] =
    if (eol != null && eol.isEmpty)
      throw new IllegalArgumentException("Line separator cannot be empty")
    new LineSubscriberAdapter(subscriber, finisher, charset, eol)

  class ByteArraySubscriber[T](finisher: Array[Byte] => T) extends BodySubscriber[T] {
    private val cf = new CompletableFuture[T]()
    private var subscription: Subscription = _
    private val subscribed = new AtomicBoolean()

    private val result = new ArrayList[Byte]()
    private val received = new ArrayList[ByteBuffer]()

    override def onSubscribe(subscription: Subscription): Unit =
      if !subscribed.compareAndSet(false, true)
      then subscription.cancel()
      else
        this.subscription = subscription
        this.subscription.request(Long.MaxValue) // read all once

    override def onNext(item: JList[ByteBuffer]): Unit = {
      requireNonNull(item)
      item.stream().forEach(item => item.hasRemaining() && received.add(item))
    }

    private def join(bufs: JList[ByteBuffer]): Array[Byte] = {
      val size = bufs.stream().mapToInt(_.remaining()).sum()
      val res = new Array[Byte](size)
      var from = 0

      bufs.stream().forEach { buf =>
        val left = buf.remaining()
        buf.get(res, from, left)
        from += left
      }

      res
    }

    override def onComplete(): Unit =
      try
        cf.complete(finisher(join(received)))
      catch {
        case t: Throwable => cf.completeExceptionally(t)
      } finally
        received.clear()

    override def onError(throwable: Throwable): Unit = {
      received.clear()
      subscription.cancel()
      cf.completeExceptionally(throwable)
    }

    override def getBody(): CompletionStage[T] = cf
  }

  def ofByteArray[T](finisher: Array[Byte] => T): BodySubscriber[T] =
    new ByteArraySubscriber(finisher)

  class PathSubscriber(private val file: Path, private val openOptions: Seq[OpenOption])
      extends BodySubscriber[Path] {
    private val cf = new CompletableFuture[Path]()
    private var subscription: Subscription = _
    private val subscribed = new AtomicBoolean()

    @volatile private var fh: FileChannel = _

    override def onSubscribe(subscription: Subscription): Unit =
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

    override def onNext(item: JList[ByteBuffer]): Unit = {
      try {
        val buffers = item.toArray(new Array[ByteBuffer](item.size))
        while buffers.exists(_.hasRemaining) do fh.write(buffers)
      } catch {
        case exc: IOException =>
          closeFileChannel()
          subscription.cancel()
          cf.completeExceptionally(exc)
          return
      }

      subscription.request(1)
    }

    override def onError(e: Throwable): Unit = {
      subscription.cancel()
      closeFileChannel()
      cf.completeExceptionally(e)
    }

    override def onComplete(): Unit = {
      closeFileChannel()
      cf.complete(file)
    }

    override def getBody(): CompletionStage[Path] = cf

    private def closeFileChannel(): Unit =
      if (fh != null) {
        try fh.close()
        catch { case t: Throwable => () }
      }
  }

  def ofFile(file: Path, openOptions: Seq[OpenOption]): PathSubscriber =
    new PathSubscriber(file, openOptions)

  class ConsumerSubscriber(private val consumer: Consumer[Optional[Array[Byte]]])
      extends BodySubscriber[Void] {
    private val cf = new CompletableFuture[Void]()
    private var subscription: Subscription = _
    private val subscribed = new AtomicBoolean()

    override def getBody(): CompletionStage[Void] = cf

    override def onSubscribe(subscription: Subscription): Unit =
      if !subscribed.compareAndSet(false, true)
      then subscription.cancel()
      else {
        this.subscription = subscription
        this.subscription.request(1)
      }

    override def onNext(item: JList[ByteBuffer]): Unit = {
      requireNonNull(item)
      item.stream().forEach { item =>
        requireNonNull(item)
        val buf = new Array[Byte](item.remaining())
        item.get(buf)
        consumer.accept(Optional.of(buf))
      }
      subscription.request(1)
    }

    override def onError(throwable: Throwable): Unit = {
      requireNonNull(throwable)
      cf.completeExceptionally(throwable)
    }

    override def onComplete(): Unit = {
      consumer.accept(Optional.empty())
      cf.complete(null)
    }
  }

  def ofByteArrayConsumer(
      consumer: Consumer[Optional[Array[Byte]]],
  ): BodySubscriber[Void] = new ConsumerSubscriber(consumer)

  class InputStreamSubscriber extends InputStream with BodySubscriber[InputStream] {
    private val cf = new CompletableFuture[InputStream]()
    private var subscription: Subscription = _
    private val subscribed = new AtomicBoolean()

    private val buffer = new ArrayList[ByteBuffer]()
    private var current: ByteBuffer = _
    private var closed = false

    override def onSubscribe(subscription: Subscription): Unit =
      if !subscribed.compareAndSet(false, true)
      then subscription.cancel()
      else {
        this.subscription = subscription
        subscription.request(1)
      }

    override def onNext(item: JList[ByteBuffer]): Unit =
      if (!closed) {
        buffer.addAll(item)
        subscription.request(1)
      }

    override def onComplete(): Unit =
      closed = true

    override def onError(throwable: Throwable): Unit =
      closed = true
      cf.completeExceptionally(throwable)

    override def read(): Int = {
      if (closed) return -1

      if (current == null || !current.hasRemaining) {
        current =
          if buffer.isEmpty
          then null
          else buffer.remove(0)
        if (current == null) return -1
      }

      current.get() & 0xff
    }

    override def getBody(): CompletionStage[InputStream] = cf

    override def close(): Unit = {
      closed = true
      if (subscribed.compareAndExchange(true, false)) {
        subscription.cancel()
      }
    }
  }

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

  class PublishingBodySubscriber extends BodySubscriber[Publisher[JList[ByteBuffer]]] {
    private val cf = new CompletableFuture[Publisher[JList[ByteBuffer]]]()
    private val subscribed = new AtomicBoolean()
    private var subscription: Subscription = _

    private var publisher: Publisher[JList[ByteBuffer]] = _
    private var subscriber: Subscriber[? >: JList[ByteBuffer]] = _

    override def onSubscribe(subscription: Subscription): Unit =
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
        cf.complete(publisher)
      }

    override def onNext(item: JList[ByteBuffer]): Unit =
      if (subscriber != null) subscriber.onNext(item)

    override def onComplete(): Unit =
      if (subscriber != null) subscriber.onComplete()

    override def onError(throwable: Throwable): Unit = {
      if (subscriber != null) subscriber.onError(throwable)

      cf.completeExceptionally(throwable)
    }

    override def getBody(): CompletionStage[Publisher[JList[ByteBuffer]]] = cf
  }

  def ofPublisher(): BodySubscriber[Publisher[JList[ByteBuffer]]] =
    new PublishingBodySubscriber()

  class NullSubscriber[T](private val other: Optional[T]) extends BodySubscriber[T] {
    private val cf = new CompletableFuture[T]()
    private val subscribed = new AtomicBoolean()

    override def onSubscribe(subscription: Subscription): Unit =
      if (!subscribed.compareAndSet(false, true)) subscription.cancel()
      else subscription.request(Long.MaxValue)

    override def onNext(item: JList[ByteBuffer]): Unit = ()

    override def onError(throwable: Throwable): Unit = cf.completeExceptionally(throwable)

    override def onComplete(): Unit =
      if other.isPresent()
      then cf.complete(other.get())
      else cf.complete(null.asInstanceOf[T])

    override def getBody(): CompletionStage[T] = cf
  }

  class BufferingSubscriber[T](
      private val downstream: BodySubscriber[T],
      private val bufferSize: Int,
  ) extends BodySubscriber[T] {
    private var subscription: Subscription = _
    private val subscribed = new AtomicBoolean()

    private val buffer = new ArrayList[ByteBuffer]()
    private var bufferedBytes = 0

    override def onSubscribe(subscription: Subscription): Unit =
      if !subscribed.compareAndSet(false, true)
      then subscription.cancel()
      else {
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

    private def writeBuffer(item: JList[ByteBuffer]): Unit =
      item
        .stream()
        .forEach { buf =>
          buffer.add(buf.duplicate())
          bufferedBytes += buf.remaining()
        }

    private def flushBuffer(): Unit =
      if (!buffer.isEmpty) {
        try downstream.onNext(buffer)
        finally {
          buffer.clear()
          bufferedBytes = 0
        }
      }
  }

  class MappingSubscriber[T, U](
      private val upstream: BodySubscriber[T],
      private val mapper: Function[? >: T, ? <: U],
  ) extends BodySubscriber[U] {
    override def onSubscribe(subscription: Subscription): Unit =
      upstream.onSubscribe(subscription)

    override def onNext(item: JList[ByteBuffer]): Unit =
      upstream.onNext(item)

    override def onError(throwable: Throwable): Unit =
      upstream.onError(throwable)

    override def onComplete(): Unit =
      upstream.onComplete()

    override def getBody(): CompletionStage[U] =
      upstream.getBody().thenApply(mapper)
  }

  def replacing[U](value: U): BodySubscriber[U] =
    new NullSubscriber(Optional.ofNullable(value))

  def discarding(): BodySubscriber[Void] =
    new NullSubscriber(Optional.empty())

  def buffering[T](downstream: BodySubscriber[T], bufferSize: Int): BodySubscriber[T] =
    require(bufferSize > 0, "bufferSize can not be negative or zero")
    new BufferingSubscriber(downstream, bufferSize)

  def mapping[T, U](
      upstream: BodySubscriber[T],
      mapper: Function[? >: T, ? <: U],
  ): BodySubscriber[U] =
    new MappingSubscriber(upstream, mapper)

}
