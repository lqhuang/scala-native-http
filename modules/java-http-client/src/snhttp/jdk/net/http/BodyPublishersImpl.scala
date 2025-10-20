package snhttp.jdk.net.http

import java.io.InputStream
import java.io.FileNotFoundException
import java.net.http.HttpRequest.BodyPublisher
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.{Files, Path}
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.function.Supplier

import scala.collection.immutable.LazyList
import scala.util.{Try, Failure, Success}

import snhttp.jdk.PropertyUtils

type BufferSubscriber = Subscriber[? >: ByteBuffer]

class NoBodyPublisher() extends BodyPublisher {
  val delegate: Publisher[ByteBuffer] = PullPublisher(Seq(ByteBuffer.allocate(0)))

  def contentLength() = 0

  def subscribe(subscriber: BufferSubscriber) = delegate.subscribe(subscriber)
}

class ByteArrayPublisher(
    bytes: Array[Byte],
    offset: Int,
    length: Int,
    bufSize: Int = PropertyUtils.DEFAULT_BUFSIZE,
) extends BodyPublisher {

  override def contentLength(): Long = length

  override def subscribe(subscriber: BufferSubscriber): Unit = {
    val bufs =
      if length <= bufSize
      then {
        val bb = ByteBuffer.allocate(length)
        bb.put(bytes)
        Seq()
      } else
        for i <- 0 until length by bufSize
        yield {
          val end = math.min(i + bufSize, offset + length)
          val size = end - (offset + i)
          val bb = ByteBuffer.allocate(size)
          bb.put(bytes, offset + i, size)
          bb
        }

    val delegate = PullPublisher(bufs)
    delegate.subscribe(subscriber)
  }
}

class StringPublisher(
    private val body: String,
    private val charset: Charset,
) extends BodyPublisher {
  private val bytes = body.getBytes(charset)
  private val delegate: ByteArrayPublisher = new ByteArrayPublisher(bytes, 0, bytes.length)

  override def contentLength(): Long =
    delegate.contentLength()

  override def subscribe(subscriber: BufferSubscriber): Unit =
    delegate.subscribe(subscriber)
}

class ByteArraysPublisher(private val iter: Iterable[Array[Byte]]) extends BodyPublisher {
  override def contentLength(): Long = -1

  override def subscribe(subscriber: BufferSubscriber): Unit = {
    val delegate = PullPublisher(iter.map(ByteBuffer.wrap))
    delegate.subscribe(subscriber)
  }
}

class InputStreamPublisher(
    private val supplier: Supplier[? <: InputStream],
    private val bufSize: Int = PropertyUtils.DEFAULT_BUFSIZE,
) extends BodyPublisher {
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
    val publisher = PullPublisher(bufs)
    publisher.subscribe(subscriber)
  }
}

class FilePublisher(
    private val path: Path,
    private val bufSize: Int = PropertyUtils.DEFAULT_BUFSIZE,
) extends BodyPublisher {

  private val file = path.toFile()

  private lazy val maybeLength: Try[Long] =
    Try(file.length())

  override def contentLength(): Long = maybeLength.getOrElse(-1)

  override def subscribe(subscriber: BufferSubscriber): Unit = {
    val publisher =
      if file.isFile()
      then new InputStreamPublisher(() => Files.newInputStream(path))
      else PullPublisher[ByteBuffer](new FileNotFoundException(s"File not found: $path"))
    publisher.subscribe(subscriber)
  }
}

class PublisherAdapter(publisher: Publisher[? <: ByteBuffer], length: Long) extends BodyPublisher {
  override def contentLength(): Long = length

  override def subscribe(subscriber: BufferSubscriber): Unit =
    publisher.subscribe(subscriber)
}

class ConcatPublisher(val publishers: Seq[BodyPublisher]) extends BodyPublisher {
  override def contentLength(): Long =
    val lengths = publishers.map(_.contentLength())
    val sum = lengths.fold(0L)((x, y) => if x < 0 || y < 0 then -1 else x + y)
    if sum < 0 then -1 else sum

  override def subscribe(subscriber: BufferSubscriber): Unit =
    for publisher <- publishers yield publisher.subscribe(subscriber)
}

object BodyPublishersImpl {

  def fromPublisher(publisher: Publisher[? <: ByteBuffer]): BodyPublisher =
    new PublisherAdapter(publisher, -1)

  def fromPublisher(
      publisher: Publisher[? <: ByteBuffer],
      contentLength: Long,
  ): BodyPublisher =
    new PublisherAdapter(publisher, contentLength)

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

}
