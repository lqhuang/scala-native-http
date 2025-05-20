package java.net.http

import java.io.{Closeable, InputStream}
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Path
import java.time.Duration
import java.util.Optional
import java.util.concurrent.Flow
import java.util.function.{BiPredicate, Supplier}
import java.util.stream.Stream

import snhttp.jdk.HttpRequestBuilderImpl
import snhttp.jdk.ResponseBodyHandlers

trait HttpRequest extends Closeable {
  import HttpRequest.BodyPublisher

  def bodyPublisher(): Optional[BodyPublisher]

  def method(): String

  def timeout(): Optional[Duration]

  def expectContinue(): Boolean

  def uri(): URI

  def version(): Optional[HttpClient.Version]

  def headers(): HttpHeaders

  final override def equals(that: Any): Boolean = that match {
    case other: HttpRequest =>
      this.uri() == other.uri() &&
      this.method() == other.method() &&
      this.timeout() == other.timeout() &&
      this.expectContinue() == other.expectContinue() &&
      this.version() == other.version() &&
      this.headers() == (other.headers())
    case _ => false
  }

  final override def hashCode(): Int = {
    val uriHash = uri().hashCode
    val methodHash = method().hashCode
    val timeoutHash = timeout().hashCode
    val expectContinueHash = expectContinue().hashCode
    val versionHash = version().hashCode
    val headersHash = headers().hashCode

    uriHash ^ methodHash ^ timeoutHash ^ expectContinueHash ^ versionHash ^ headersHash
  }
}

object HttpRequest {

  def newBuilder(): Builder = new HttpRequestBuilderImpl()

  def newBuilder(uri: URI): Builder = new HttpRequestBuilderImpl(uri)

  def newBuilder(request: HttpRequest, filter: BiPredicate[String, String]): Builder = ???

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

  import snhttp.jdk.BodyPublishers
  trait BodyPublishers {
    def fromPublisher(publisher: Flow.Publisher[? <: ByteBuffer]): BodyPublisher

    def fromPublisher(
        publisher: Flow.Publisher[? <: ByteBuffer],
        contentLength: Long,
    ): BodyPublisher

    def ofInputStream(streamSupplier: Supplier[? <: InputStream]): BodyPublisher

    def ofByteArray(buf: Array[Byte]): BodyPublisher

    def ofByteArray(buf: Array[Byte], offset: Int, length: Int): BodyPublisher

    def ofFile(path: Path): BodyPublisher

    def ofByteArrays(iter: Iterable[Array[Byte]]): BodyPublisher

    def noBody(): BodyPublisher

    def concat(publishers: BodyPublisher*): BodyPublisher
  }

}
