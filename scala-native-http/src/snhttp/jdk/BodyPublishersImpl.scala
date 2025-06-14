package snhttp.jdk

import java.io.{Closeable, InputStream, IOException, UncheckedIOException}
import java.net.URI
import java.net.http.HttpRequest.BodyPublisher
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Files, Path}
import java.util.Optional
import java.util.concurrent.Flow
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.function.{BiPredicate, Supplier}
import java.util.stream.Stream

import scala.util.{Try, Success, Failure}

class NoBodyPublisher() extends BodyPublisher {
  val delegate: Publisher[ByteBuffer] = new PullPublisher(Iterable(ByteBuffer.allocate(0)))

  def contentLength() = 0

  def subscribe(subscriber: Subscriber[? >: ByteBuffer]) =
    delegate.subscribe(subscriber)
}

class StringPublisher(
    private val body: String,
    private val charset: Charset,
) extends BodyPublisher {
  private val bytes = body.getBytes(charset)
  private val bufSize = 8192

  override def contentLength(): Long = bytes.length

  override def subscribe(subscriber: Subscriber[? >: ByteBuffer]): Unit = {
    val bufs = scala.collection.mutable.ListBuffer.empty[ByteBuffer]
    var off = 0
    var remain = bytes.length
    while (remain > 0) {
      val b = ByteBuffer.allocate(Math.min(bufSize, remain))
      val tocopy = Math.min(b.capacity(), remain)
      b.put(bytes, off, tocopy)
      off += tocopy
      remain -= tocopy
      b.flip()
      bufs += b
    }
    val delegate = new PullPublisher(bufs)
    delegate.subscribe(subscriber)
  }
}

class InputStreamPublisher(private val streamSupplier: Supplier[? <: InputStream])
    extends BodyPublisher {
  override def contentLength(): Long = -1
  // override def subscribe(subscriber: Subscriber[? >: ByteBuffer]): Unit = {
  //   val is = streamSupplier.get()
  //   val publisher =
  //     if (is == null)
  //       new PullPublisher[ByteBuffer](
  //         List.empty[ByteBuffer],
  //         new IOException("streamSupplier returned null"),
  //       )
  //     else
  //       new PullPublisher[ByteBuffer](new Iterable[ByteBuffer] {
  //         def iterator: Iterator[ByteBuffer] = new StreamIterator(is)
  //       })
  //   publisher.subscribe(subscriber)
  // }
}

class ByteArrayPublisher(buf: Array[Byte], offset: Int, length: Int) extends BodyPublisher {
  override def contentLength(): Long = length
  override def subscribe(subscriber: Subscriber[? >: ByteBuffer]): Unit = {
    val bufs = scala.collection.mutable.ListBuffer.empty[ByteBuffer]
    var off = offset
    var remain = length
    val bufSize = 8192
    while (remain > 0) {
      val b = ByteBuffer.allocate(Math.min(bufSize, remain))
      val tocopy = Math.min(b.capacity(), remain)
      b.put(buf, off, tocopy)
      off += tocopy
      remain -= tocopy
      b.flip()
      bufs += b
    }
    val delegate = new PullPublisher(bufs)
    delegate.subscribe(subscriber)
  }
}

class FilePublisher(private val path: Path, private val bufSize: Int = 8192) extends BodyPublisher {

  private lazy val maybeLength: Option[Long] =
    Try(Files.size(path)) match {
      case Success(size)           => Some(size)
      case Failure(_: IOException) => None
      case Failure(e) =>
        throw new UncheckedIOException(new IOException(s"Error reading file size for $path", e))
    }

  override def contentLength(): Long = maybeLength.getOrElse(-1)

  override def subscribe(subscriber: Subscriber[? >: ByteBuffer]): Unit = {
    val publisher =
      try {
        val is = Files.newInputStream(path)
        new PullPublisher[ByteBuffer](new Iterable[ByteBuffer] {
          def iterator: Iterator[ByteBuffer] = new StreamIterator(is, bufSize)
        })
      } catch {
        case e: Throwable => new PullPublisher[ByteBuffer](List.empty[ByteBuffer], e)
      }
    publisher.subscribe(subscriber)
  }
}

class ByteArrayIterablePublisher(val iter: Iterable[Array[Byte]]) extends BodyPublisher {
  override def contentLength(): Long = -1
  override def subscribe(subscriber: Subscriber[? >: ByteBuffer]): Unit = {
    val byteBufferIter = new Iterable[ByteBuffer] {
      def iterator: Iterator[ByteBuffer] = iter.iterator.map(ByteBuffer.wrap)
    }
    val delegate = new PullPublisher(byteBufferIter)
    delegate.subscribe(subscriber)
  }
}

class PublisherAdapter(publisher: Publisher[? <: ByteBuffer], len: Long) extends BodyPublisher {
  override def contentLength(): Long = len

  override def subscribe(subscriber: Subscriber[? >: ByteBuffer]): Unit =
    publisher.subscribe(subscriber)
}

class ConcatPublisher(val publishers: Seq[BodyPublisher]) extends BodyPublisher {
  override def contentLength(): Long =
    val lengths = publishers.map(_.contentLength())
    if lengths.contains(-1) then -1
    else lengths.sum

  override def subscribe(subscriber: Subscriber[? >: ByteBuffer]): Unit =
    new AggregateSubscription(publishers.toList, subscriber).start()
}

class AggregateSubscription(
    bodies: List[BodyPublisher],
    subscriber: Subscriber[? >: ByteBuffer],
) extends Subscription {
  private val it = bodies.iterator
  private var current: Subscription = null
  private var cancelled = false
  private var requested = 0L
  private var done = false

  def start(): Unit =
    subscribeNext()

  private def subscribeNext(): Unit =
    if (it.hasNext && !cancelled) {
      val nextPublisher = it.next()
      nextPublisher.subscribe(new Subscriber[ByteBuffer] {
        override def onSubscribe(s: Subscription): Unit = {
          current = s
          if (requested > 0) s.request(requested)
        }
        override def onNext(item: ByteBuffer): Unit =
          subscriber.onNext(item)
        override def onError(t: Throwable): Unit =
          subscriber.onError(t)
        override def onComplete(): Unit =
          subscribeNext()
      })
    } else if (!done) {
      done = true
      subscriber.onComplete()
    }

  override def request(n: Long): Unit =
    if (n <= 0)
      subscriber.onError(new IllegalArgumentException("non-positive subscription request: " + n))
    else {
      requested += n
      if (current != null) current.request(n)
    }
  override def cancel(): Unit = {
    cancelled = true
    if (current != null) current.cancel()
  }
}

/**
 * StreamIterator for InputStream -> ByteBuffer
 */
class StreamIterator(is: InputStream, bufSize: Int = 8192) extends Iterator[ByteBuffer] {
  private var eof = false
  private var nextBuf: ByteBuffer = _
  private var needRead = true
  override def hasNext: Boolean = {
    if (eof) return false
    if (needRead) {
      val buf = ByteBuffer.allocate(bufSize)
      val n = is.read(buf.array())
      if (n == -1) {
        eof = true
        is.close()
        false
      } else {
        buf.limit(n)
        buf.position(0)
        nextBuf = buf
        needRead = false
        true
      }
    } else true
  }
  override def next(): ByteBuffer = {
    if (!hasNext) throw new NoSuchElementException()
    needRead = true
    nextBuf
  }
}

object BodyPublishersImpl {

  def fromPublisher(publisher: Flow.Publisher[? <: ByteBuffer]): BodyPublisher =
    new PublisherAdapter(publisher, -1)

  def fromPublisher(
      publisher: Flow.Publisher[? <: ByteBuffer],
      contentLength: Long,
  ): BodyPublisher =
    require(contentLength >= 0, "contentLength can not be zero or negative")
    new PublisherAdapter(publisher, contentLength)

  def ofString(body: String, charset: Charset): BodyPublisher =
    new StringPublisher(body, charset)

  def ofInputStream(streamSupplier: Supplier[? <: InputStream]): BodyPublisher =
    new InputStreamPublisher(streamSupplier)

  def ofByteArray(buf: Array[Byte], offset: Int, length: Int): BodyPublisher =
    new ByteArrayPublisher(buf, offset, length)

  def ofFile(path: Path): BodyPublisher = new FilePublisher(path)

  def ofByteArrays(iter: Iterable[Array[Byte]]): BodyPublisher =
    new ByteArrayIterablePublisher(iter)

  def noBody(): BodyPublisher = new NoBodyPublisher()

  def concat(publishers: BodyPublisher*): BodyPublisher =
    if publishers.isEmpty
    then noBody()
    else new ConcatPublisher(publishers)

}
