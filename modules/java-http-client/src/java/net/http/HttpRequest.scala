package java.net.http

import java.io.{Closeable, InputStream}
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Path
import java.time.Duration
import java.util.List as JList
import java.util.Optional
import java.util.Objects.requireNonNull
import java.util.concurrent.Flow
import java.util.function.{BiConsumer, BiPredicate, Consumer, Supplier}
import java.util.stream.Stream

import snhttp.jdk.net.http.{BodyPublishersImpl, HttpRequestBuilderImpl}

trait HttpRequest extends Closeable {
  import HttpRequest.BodyPublisher

  def bodyPublisher(): Optional[BodyPublisher]

  def method(): String

  def timeout(): Optional[Duration]

  def expectContinue(): Boolean

  def uri(): URI

  def version(): Optional[HttpClient.Version]

  def headers(): HttpHeaders

  /// Two HTTP requests are equal if their URI, method, and headers fields are all equal.
  final override def equals(that: Any): Boolean = that match {
    case other: HttpRequest =>
      this.uri() == other.uri() &&
      this.method() == other.method() &&
      this.headers() == (other.headers())
    case _ => false
  }

  /// The hash code is based upon the HTTP request's URI, method, and header components
  final override def hashCode(): Int =
    uri().hashCode + method().hashCode + headers().hashCode
}

object HttpRequest {
  def newBuilder(): Builder = new HttpRequestBuilderImpl()

  def newBuilder(uri: URI): Builder = new HttpRequestBuilderImpl(Some(uri))

  /// since 16
  def newBuilder(request: HttpRequest, filter: BiPredicate[String, String]): Builder = {
    requireNonNull(request)
    requireNonNull(filter)

    val builder = newBuilder(request.uri()).expectContinue(request.expectContinue())

    if (request.version().isPresent()) builder.version(request.version().get()): Unit
    if (request.timeout().isPresent()) builder.timeout(request.timeout().get()): Unit

    val publisher =
      if request.bodyPublisher().isPresent() then request.bodyPublisher().get()
      else BodyPublishers.noBody()
    builder.method(request.method(), publisher): Unit

    val newHeaders = HttpHeaders.of(request.headers().map(), filter)
    newHeaders
      .map()
      .forEach((name: String, values: JList[String]) =>
        values.forEach((value: String) => builder.header(name, value): Unit),
      )

    builder
  }

  trait Builder {
    def uri(uri: URI): Builder

    def expectContinue(enable: Boolean): Builder

    def version(version: HttpClient.Version): Builder

    def header(name: String, value: String): Builder

    def headers(headers: String*): Builder

    def timeout(duration: Duration): Builder

    def setHeader(name: String, value: String): Builder

    def GET(): Builder

    def POST(bodyPublisher: BodyPublisher): Builder

    def PUT(bodyPublisher: BodyPublisher): Builder

    def DELETE(): Builder

    def HEAD(): Builder

    def method(method: String, bodyPublisher: BodyPublisher): Builder

    def build(): HttpRequest

    def copy(): Builder
  }

  trait BodyPublisher extends Flow.Publisher[ByteBuffer] {
    def contentLength(): Long
  }

  abstract class BodyPublishers {}
  object BodyPublishers {
    def fromPublisher(publisher: Flow.Publisher[? <: ByteBuffer]): BodyPublisher =
      BodyPublishersImpl.fromPublisher(publisher)

    def fromPublisher(
        publisher: Flow.Publisher[? <: ByteBuffer],
        contentLength: Long,
    ): BodyPublisher =
      BodyPublishersImpl.fromPublisher(publisher, contentLength)

    def ofString(body: String, charset: Charset): BodyPublisher =
      BodyPublishersImpl.ofString(body, charset)

    def ofString(body: String): BodyPublisher = ofString(body, UTF_8)

    def ofInputStream(streamSupplier: Supplier[? <: InputStream]): BodyPublisher =
      BodyPublishersImpl.ofInputStream(streamSupplier)

    def ofByteArray(buf: Array[Byte], offset: Int, length: Int): BodyPublisher =
      BodyPublishersImpl.ofByteArray(buf, offset, length)

    def ofByteArray(buf: Array[Byte]): BodyPublisher =
      ofByteArray(buf, 0, buf.length)

    def ofFile(path: Path): BodyPublisher =
      BodyPublishersImpl.ofFile(path)

    def ofByteArrays(iter: Iterable[Array[Byte]]): BodyPublisher =
      BodyPublishersImpl.ofByteArrays(iter)

    def noBody(): BodyPublisher =
      BodyPublishersImpl.noBody()

    def concat(publishers: BodyPublisher*): BodyPublisher =
      BodyPublishersImpl.concat(publishers*)
  }
}
