package snhttp.jdk

import java.io.{Closeable, InputStream}
import java.net.URI
import java.net.http.HttpRequest.BodyPublisher
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Path
import java.time.Duration
import java.util.Optional
import java.util.concurrent.Flow
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.function.{BiPredicate, Supplier}
import java.util.stream.Stream

case class NoBodyPublisher() extends BodyPublisher {
  val delegate: Publisher[ByteBuffer] = new PullPublisher(Iterable(ByteBuffer.allocate(0)))

  def contentLength() = 0

  def subscribe(subscriber: Subscriber[? >: ByteBuffer]) =
    delegate.subscribe(subscriber)
}

object BodyPublishersImpl {

  // def fromPublisher(publisher: Flow.Publisher[? <: ByteBuffer]): BodyPublisher = ???

  // def fromPublisher(
  //     publisher: Flow.Publisher[? <: ByteBuffer],
  //     contentLength: Long,
  // ): BodyPublisher = ???

  // def ofInputStream(streamSupplier: Supplier[? <: InputStream]): BodyPublisher = ???

  // def ofByteArray(buf: Array[Byte]): BodyPublisher = ???

  // def ofByteArray(buf: Array[Byte], offset: Int, length: Int): BodyPublisher = ???

  // def ofFile(path: Path): BodyPublisher = ???

  // def ofByteArrays(iter: Iterable[Array[Byte]]): BodyPublisher = ???

  def noBody(): BodyPublisher = NoBodyPublisher()

  // def concat(publishers: BodyPublisher*): BodyPublisher = ???

}
