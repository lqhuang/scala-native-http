package snhttp.jdk.net.http

import java.io.InputStream
import java.io.{FileNotFoundException, IOException}
import java.net.http.HttpRequest.BodyPublisher
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.{Files, Path}
import java.util.NoSuchElementException
import java.util.Objects.requireNonNull
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import java.util.function.Supplier

import scala.util.Try
import scala.util.control.NonFatal

import snhttp.jdk.net.http.internal.{ConcatPublisher, PullPublisher, PropertyUtils}

type ByteBufferSubscriber = Subscriber[? >: ByteBuffer]

object BodyPublishersImpl:

  def fromPublisher(publisher: Publisher[? <: ByteBuffer]): BodyPublisher =
    new PublisherWrapper(publisher, -1)

  def fromPublisher(
      publisher: Publisher[? <: ByteBuffer],
      contentLength: Long,
  ): BodyPublisher =
    new PublisherWrapper(publisher, contentLength)

  def ofString(body: String, charset: Charset): BodyPublisher =
    new StringPublisher(body, charset)

  def ofInputStream(streamSupplier: Supplier[? <: InputStream]): BodyPublisher =
    new InputStreamPublisher(streamSupplier)

  def ofByteArray(buf: Array[Byte], offset: Int, length: Int): BodyPublisher =
    new ByteArrayPublisher(buf, offset, length)

  def ofFile(path: Path): BodyPublisher =
    new FilePublisher(path)

  def ofByteArrays(iter: Iterable[Array[Byte]]): BodyPublisher =
    new ByteArraysPublisher(iter)

  def noBody(): BodyPublisher =
    new NoBodyPublisher()

  def concat(publishers: BodyPublisher*): BodyPublisher =
    new ConcatPublisher(publishers)

end BodyPublishersImpl

class NoBodyPublisher() extends BodyPublisher:

  def contentLength(): Long = 0L

  def subscribe(subscriber: ByteBufferSubscriber): Unit = {
    requireNonNull(subscriber, "subscriber must not be null")
    val publisher = PullPublisher(Seq.empty[ByteBuffer])
    publisher.subscribe(subscriber)
  }

end NoBodyPublisher

class PublisherWrapper(publisher: Publisher[? <: ByteBuffer], length: Long) extends BodyPublisher:

  override def contentLength(): Long =
    length

  override def subscribe(subscriber: ByteBufferSubscriber): Unit =
    publisher.subscribe(subscriber)

end PublisherWrapper

class ByteArrayPublisher(
    bytes: Array[Byte],
    offset: Int,
    length: Int,
    bufSize: Int = PropertyUtils.INTERNAL_BUFFER_SIZE,
) extends BodyPublisher:

  requireNonNull(bytes, "bytes must not be null")
  require(
    offset >= 0 && length >= 0 && (bytes.length - offset) >= length,
    "invalid offset and length for the given byte array",
  )

  override def contentLength(): Long =
    length

  override def subscribe(subscriber: ByteBufferSubscriber): Unit = {
    val bufs: Seq[ByteBuffer] =
      if length <= bufSize
      then {
        val bb = ByteBuffer.allocate(length)
        bb.put(bytes, offset, length)
        bb.flip()
        Seq(bb)
      } //
      else {
        val group =
          if length % bufSize == 0
          then length / bufSize
          else length / bufSize + 1

        for i <- 0 until group
        yield {
          val start = offset + i * bufSize
          // compute size for the last buffer, which is the min between `bufSize` and remaining bytes
          val size = bufSize.min(bytes.length - start).min(length - i * bufSize)
          val bb = ByteBuffer.allocate(size)
          bb.put(bytes, start, size)
          bb.flip()
          bb
        }
      }

    val delegate = PullPublisher(bufs)
    delegate.subscribe(subscriber)
  }

end ByteArrayPublisher

class StringPublisher(
    body: String,
    charset: Charset,
) extends BodyPublisher:

  requireNonNull(body, "body must not be null")
  requireNonNull(charset, "charset must not be null")

  lazy val bytes = body.getBytes(charset)
  lazy val delegate: ByteArrayPublisher = new ByteArrayPublisher(bytes, 0, bytes.length)

  override def contentLength(): Long =
    delegate.contentLength()

  override def subscribe(subscriber: ByteBufferSubscriber): Unit =
    delegate.subscribe(subscriber)

end StringPublisher

class ByteArraysPublisher(iter: Iterable[Array[Byte]]) extends BodyPublisher:

  requireNonNull(iter, "iter must not be null")

  override def contentLength(): Long =
    -1

  override def subscribe(subscriber: ByteBufferSubscriber): Unit =
    val delegate = PullPublisher(iter.map(ByteBuffer.wrap))
    delegate.subscribe(subscriber)

end ByteArraysPublisher

class InputStreamPublisher(
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
      val publisher = PullPublisher(Seq.empty[ByteBuffer], () => ())
      publisher.subscribe(subscriber)
      subscriber.onError(new IOException("stream supplier returned null"))
    } else {
      val closeStream = () =>
        try stream.close()
        catch case NonFatal(_) => ()

      val bufs = Iterable
        .unfold(stream) { s =>
          val buffer = ByteBuffer.allocate(bufSize)
          val size = stream.read(buffer.array(), 0, bufSize)

          if size < 0
          then None
          else
            buffer.limit(size)
            Some((buffer, s))
        }

      val publisher = PullPublisher(bufs, closeStream)
      publisher.subscribe(subscriber)
    }
  }

end InputStreamPublisher

class FilePublisher(
    path: Path,
    bufSize: Int = PropertyUtils.INTERNAL_BUFFER_SIZE,
) extends BodyPublisher:

  requireNonNull(path, "path must not be null")

  private lazy val maybeLength: Try[Long] =
    Try(Files.size(path))

  override def contentLength(): Long =
    maybeLength.getOrElse(-1)

  override def subscribe(subscriber: ByteBufferSubscriber): Unit = {
    val publisher =
      if Files.isRegularFile(path)
      then {
        val publisher = new InputStreamPublisher(() => Files.newInputStream(path), bufSize)
        publisher.subscribe(subscriber)
      } else {
        val publisher = PullPublisher(Seq.empty[ByteBuffer], () => ())
        publisher.subscribe(subscriber)
        subscriber.onError(new FileNotFoundException(s"File not found: $path"))
      }
  }

end FilePublisher
