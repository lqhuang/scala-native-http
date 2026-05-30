package snhttp.jdk.net.http

import java.io.InputStream
import java.io.{FileNotFoundException, IOException, UncheckedIOException}
import java.net.http.HttpRequest.BodyPublisher
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.{Files, Path}
import java.util.NoSuchElementException
import java.util.Objects.requireNonNull
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import java.util.function.Supplier
import java.lang.Iterable as JIterable

import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.util.Try
import scala.util.control.NonFatal

import snhttp.jdk.net.http.internal.{ConcatPublisher, PullPublisher, PropertyUtils}

type ByteBufferSubscriber = Subscriber[? >: ByteBuffer]

object BodyPublishersImpl:

  def fromPublisher(publisher: Publisher[? <: ByteBuffer]): BodyPublisher =
    new BodyPublisherWrapper(publisher, -1)

  def fromPublisher(
      publisher: Publisher[? <: ByteBuffer],
      contentLength: Long,
  ): BodyPublisher =
    new BodyPublisherWrapper(publisher, contentLength)

  def ofString(body: String, charset: Charset): BodyPublisher =
    new StringBodyPublisher(body, charset)

  def ofInputStream(streamSupplier: Supplier[? <: InputStream]): BodyPublisher =
    new InputStreamBodyPublisher(streamSupplier)

  def ofByteArray(buf: Array[Byte], offset: Int, length: Int): BodyPublisher =
    new ByteArrayBodyPublisher(buf, offset, length)

  def ofFile(path: Path): BodyPublisher =
    new FileBodyPublisher(path)

  def ofByteArrays(iter: JIterable[Array[Byte]]): BodyPublisher =
    new ByteArraysPublisher(iter)

  def noBody(): BodyPublisher =
    new NoBodyPublisher()

  def concat(publishers: BodyPublisher*): BodyPublisher =
    new ConcatPublisher(publishers)

end BodyPublishersImpl

private[snhttp] class NoBodyPublisher() extends BodyPublisher:

  def contentLength(): Long = 0L

  def subscribe(subscriber: ByteBufferSubscriber): Unit = {
    requireNonNull(subscriber, "subscriber must not be null")
    val publisher = new PullPublisher(Iterator.empty[ByteBuffer])
    publisher.subscribe(subscriber)
  }

end NoBodyPublisher

private[snhttp] class BodyPublisherWrapper(publisher: Publisher[? <: ByteBuffer], length: Long)
    extends BodyPublisher:

  requireNonNull(publisher, "publisher must not be null")
  // require(length > 0, "content length cannot be negative and zero")

  override def contentLength(): Long =
    length

  override def subscribe(subscriber: ByteBufferSubscriber): Unit =
    publisher.subscribe(subscriber)

end BodyPublisherWrapper

private[snhttp] class ByteArrayBodyPublisher(
    bytes: Array[Byte],
    offset: Int,
    length: Int,
    bufSize: Int = PropertyUtils.INTERNAL_BUFFER_SIZE,
) extends BodyPublisher:

  requireNonNull(bytes, "bytes must not be null")
  if (offset < 0 || length < 0 || (bytes.length - offset) < length)
    throw new IndexOutOfBoundsException(s"Invalid offset and length for the given byte array")

  override def contentLength(): Long =
    length

  override def subscribe(subscriber: ByteBufferSubscriber): Unit = {
    val bufs: Iterator[ByteBuffer] =
      if length == 0 then //
        Iterator.empty[ByteBuffer]
      else if length <= bufSize then {
        val bb = ByteBuffer.allocate(length)
        bb.put(bytes, offset, length)
        bb.flip()
        Iterator(bb)
      } //
      else {
        val group =
          if length % bufSize == 0
          then length / bufSize
          else length / bufSize + 1

        Iterator.from(0 until group).map { i =>
          val start = offset + i * bufSize
          // compute size for the last buffer, which is the min between `bufSize` and remaining bytes
          val size = bufSize.min(bytes.length - start).min(length - i * bufSize)
          val bb = ByteBuffer.allocate(size)
          bb.put(bytes, start, size)
          bb.flip()
          bb
        }
      }

    val delegate = new PullPublisher(bufs)
    delegate.subscribe(subscriber)
  }

end ByteArrayBodyPublisher

private[snhttp] class StringBodyPublisher(body: String, charset: Charset) extends BodyPublisher:

  requireNonNull(body, "body must not be null")
  requireNonNull(charset, "charset must not be null")

  lazy val bytes = body.getBytes(charset)
  lazy val delegate: ByteArrayBodyPublisher = new ByteArrayBodyPublisher(bytes, 0, bytes.length)

  override def contentLength(): Long =
    bytes.length

  override def subscribe(subscriber: ByteBufferSubscriber): Unit =
    delegate.subscribe(subscriber)

end StringBodyPublisher

private[snhttp] class ByteArraysPublisher(jiterable: JIterable[Array[Byte]]) extends BodyPublisher:

  requireNonNull(jiterable, "iter must not be null")

  override def contentLength(): Long =
    -1

  override def subscribe(subscriber: ByteBufferSubscriber): Unit =
    val delegate = new PullPublisher(jiterable.asScala.iterator.map(ByteBuffer.wrap))
    delegate.subscribe(subscriber)

end ByteArraysPublisher

private[snhttp] class InputStreamBodyPublisher(
    supplier: Supplier[? <: InputStream],
    bufSize: Int = PropertyUtils.INTERNAL_BUFFER_SIZE,
) extends BodyPublisher:

  requireNonNull(supplier, "supplier must not be null")
  require(bufSize > 0, "bufSize must be positive")

  override def contentLength(): Long =
    -1

  override def subscribe(subscriber: ByteBufferSubscriber): Unit = {
    requireNonNull(subscriber, "subscriber must not be null")

    val stream = supplier.get()
    if stream == null
    then {
      val publisher = new PullPublisher(Iterator.empty[ByteBuffer], () => ())
      publisher.subscribe(subscriber)
      subscriber.onError(new IOException("stream supplier returned null"))
    } else {
      val closeStream = () =>
        try stream.close()
        catch case NonFatal(_) => ()

      val bufs = Iterator
        .unfold(stream) { s =>
          val buffer = ByteBuffer.allocate(bufSize)
          val size = stream.read(buffer.array(), 0, bufSize)

          if size < 0
          then None
          else
            buffer.limit(size)
            Some((buffer, s))
        }
      val publisher = new PullPublisher(bufs, closeStream)
      publisher.subscribe(subscriber)
    }
  }

end InputStreamBodyPublisher

private[snhttp] class FileBodyPublisher(
    path: Path,
    bufSize: Int = PropertyUtils.INTERNAL_BUFFER_SIZE,
) extends BodyPublisher:

  requireNonNull(path, "path must not be null")

  if (!Files.exists(path))
    throw new FileNotFoundException(s"File does not exist: ${path}")

  override def contentLength(): Long =
    Try(Files.size(path)).getOrElse(-1)

  override def subscribe(subscriber: ByteBufferSubscriber): Unit = {
    val publisher =
      if Files.isRegularFile(path) && Files.isReadable(path)
      then {
        println(s"Creating InputStreamBodyPublisher for file: $path")
        val publisher = new InputStreamBodyPublisher(() => Files.newInputStream(path), bufSize)
        publisher.subscribe(subscriber)
      } else {
        val publisher = new PullPublisher(Iterator.empty[ByteBuffer], () => ())
        publisher.subscribe(subscriber)
        subscriber.onError(new FileNotFoundException(s"File not found or is not redable: ${path}"))
      }
  }

end FileBodyPublisher
