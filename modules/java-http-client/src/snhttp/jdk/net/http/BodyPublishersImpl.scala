package snhttp.jdk.net.http

import java.io.InputStream
import java.io.FileNotFoundException
import java.net.http.HttpRequest.BodyPublisher
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.{Files, Path}
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.function.Supplier
import java.util.Objects.requireNonNull

import scala.collection.immutable.LazyList
import scala.util.{Try, Failure, Success}

import _root_.snhttp.jdk.internal.PropertyUtils
import _root_.snhttp.jdk.net.http.internal.DelegatePublisher

type BufferSubscriber = Subscriber[? >: ByteBuffer]

class NoBodyPublisher() extends BodyPublisher:

  val delegate: Publisher[ByteBuffer] = DelegatePublisher(Seq(ByteBuffer.allocate(0)))

  def contentLength() = 0

  def subscribe(subscriber: BufferSubscriber) = delegate.subscribe(subscriber)

end NoBodyPublisher

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

  override def subscribe(subscriber: BufferSubscriber): Unit = {
    // val demandLength = length
    val bufs: Seq[ByteBuffer] =
      if length <= bufSize
      then
        val bb = ByteBuffer.allocate(length)
        bb.put(bytes, offset, length)
        bb.flip()
        Seq(bb)
      else {
        val group =
          if length % bufSize == 0
          then length / bufSize
          else length / bufSize + 1

        for i <- 0 until group
        yield
          val start = offset + i * bufSize
          // compute size for the last buffer, which is the min between `bufSize` and remaining bytes
          val size = bufSize.min(bytes.length - start).min(length - i * bufSize)
          val bb = ByteBuffer.allocate(size)
          bb.put(bytes, start, size)
          bb.flip()
          bb
      }

    val delegate = DelegatePublisher(bufs)
    delegate.subscribe(subscriber)
  }

end ByteArrayPublisher

class StringPublisher(
    private val body: String,
    private val charset: Charset,
) extends BodyPublisher:

  requireNonNull(body != null, "body must not be null")
  requireNonNull(charset != null, "charset must not be null")

  private val bytes = body.getBytes(charset)
  private val delegate: ByteArrayPublisher = new ByteArrayPublisher(bytes, 0, bytes.length)

  override def contentLength(): Long =
    delegate.contentLength()

  override def subscribe(subscriber: BufferSubscriber): Unit =
    delegate.subscribe(subscriber)

end StringPublisher

class ByteArraysPublisher(private val iter: Iterable[Array[Byte]]) extends BodyPublisher:

  override def contentLength(): Long = -1

  override def subscribe(subscriber: BufferSubscriber): Unit =
    val delegate = DelegatePublisher(iter.map(ByteBuffer.wrap))
    delegate.subscribe(subscriber)

end ByteArraysPublisher

class InputStreamPublisher(
    supplier: Supplier[? <: InputStream],
    bufSize: Int = PropertyUtils.INTERNAL_BUFFER_SIZE,
) extends BodyPublisher:

  override def contentLength(): Long = -1

  override def subscribe(subscriber: BufferSubscriber): Unit = {
    val is = supplier.get()
    val bufs = LazyList
      .continually {
        val remain = is.available()
        if remain <= 0
        then None
        else {
          val allocSize = remain.min(bufSize)
          val buf = ByteBuffer.wrap(is.readNBytes(allocSize))
          Some(buf)
        }
      }
      .takeWhile(_.isDefined)
      .map(_.get)

    val publisher = DelegatePublisher(bufs)
    publisher.subscribe(subscriber)
  }

end InputStreamPublisher

class FilePublisher(
    path: Path,
    bufSize: Int = PropertyUtils.INTERNAL_BUFFER_SIZE,
) extends BodyPublisher:

  private val file =
    path.toFile()

  private lazy val maybeLength: Try[Long] =
    Try(file.length())

  override def contentLength(): Long =
    maybeLength.getOrElse(-1)

  override def subscribe(subscriber: BufferSubscriber): Unit = {
    val publisher =
      if file.isFile()
      then new InputStreamPublisher(() => Files.newInputStream(path))
      else DelegatePublisher[ByteBuffer](new FileNotFoundException(s"File not found: $path"))
    publisher.subscribe(subscriber)
  }

end FilePublisher

class PublisherWrapper(publisher: Publisher[? <: ByteBuffer], length: Long) extends BodyPublisher:

  override def contentLength(): Long =
    length

  override def subscribe(subscriber: BufferSubscriber): Unit =
    publisher.subscribe(subscriber)

end PublisherWrapper

class ConcatPublisher(val publishers: Seq[BodyPublisher]) extends BodyPublisher:

  override def contentLength(): Long =
    val lengths = publishers.map(_.contentLength())
    val sum = lengths.fold(0L)((x, y) => if x < 0 || y < 0 then -1 else x + y)
    if sum < 0 then -1 else sum

  override def subscribe(subscriber: BufferSubscriber): Unit =
    publishers.foreach(p => p.subscribe(subscriber))

end ConcatPublisher

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

  def ofFile(path: Path): BodyPublisher = new FilePublisher(path)

  def ofByteArrays(iter: Iterable[Array[Byte]]): BodyPublisher =
    new ByteArraysPublisher(iter)

  def noBody(): BodyPublisher = new NoBodyPublisher()

  def concat(publishers: BodyPublisher*): BodyPublisher =
    if publishers.isEmpty
    then noBody()
    else new ConcatPublisher(publishers)

end BodyPublishersImpl
