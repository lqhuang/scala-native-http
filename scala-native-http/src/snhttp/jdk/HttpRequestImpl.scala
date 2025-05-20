package snhttp.jdk

import java.net.URI
import java.time.Duration
import java.util.function.BiPredicate
import java.io.Closeable
import java.net.http.HttpClient
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest
import java.net.http.HttpRequest.{Builder, BodyPublisher, BodyPublishers}

import scala.collection.mutable.Map

import snhttp.constants.Method
import java.util.Locale

class HttpRequestBuilderImpl(
    private var uri: Option[URI] = None,
    private var expectContinue: Boolean = false,
    private var version: Version = Version.HTTP_1_1,
    private var timeout: Option[Duration] = None,
    private var method: String = Method.GET,
    private var bodyPublisher: BodyPublisher = BodyPublishers.noBody(),
    private val headerMap: Map[String, List[String]] = Map.empty,
) extends HttpRequest.Builder {

  def uri(uri: URI): Builder = {
    require(uri != null, "uri must be non-null")
    val scheme = uri.getScheme().toLowerCase(Locale.US)
    require(scheme == "http" || scheme == "https", s"invalid URI scheme ${scheme}")
    val host = uri.getHost()
    require(host != null, s"invalid URI host ${host}")

    this.uri = Some(uri)
    this
  }

  def expectContinue(enable: Boolean): Builder = {
    this.expectContinue = enable
    this
  }

  def version(version: HttpClient.Version): Builder = {
    this.version = version
    this
  }

  private def checkHeader(name: String, value: String): (String, String) =
    val key = name.trim()
    require(!key.isEmpty, s"invalid header name ${name}")
    val valueTrimmed = value.trim()
    require(!valueTrimmed.isEmpty, s"invalid header value ${value}")
    return (key, valueTrimmed)

  /// Add a header to the request.
  /// If the header already exists, the value is appended to the existing values.
  def header(name: String, value: String): Builder = {
    val (key, valueTrimmed) = checkHeader(name, value)
    val values = headerMap.getOrElse(key, Nil)
    headerMap += (key -> (values :+ valueTrimmed))
    this
  }

  /// Set a header to the request.
  /// If the header already exists, the original value is replaced.
  def setHeader(name: String, value: String): Builder = {
    val (key, valueTrimmed) = checkHeader(name, value)
    headerMap(key) = valueTrimmed :: Nil
    this
  }

  def headers(headers: String*): Builder = {
    require(headers.length % 2 == 0, "Headers must be supplied in name-value pairs")
    headers.grouped(2).foreach(pair => header(pair(0), pair(1)))
    this
  }

  def timeout(duration: Duration): Builder = {
    require(!duration.isNegative && !duration.isZero, "Duration must be positive")
    this.timeout = Some(duration)
    this
  }

  def method(method: String, bodyPublisher: BodyPublisher): Builder = {
    require(method != null && !method.isEmpty, "Method must not be null or empty")
    this.method = method
    this.bodyPublisher = bodyPublisher
    this
  }

  def GET(): Builder = {
    this.method = Method.GET
    this.bodyPublisher = BodyPublishers.noBody()
    this
  }

  def POST(bodyPublisher: BodyPublisher): Builder = {
    this.method = Method.POST
    this.bodyPublisher = bodyPublisher
    this
  }

  def PUT(bodyPublisher: BodyPublisher): Builder = {
    this.method = Method.PUT
    this.bodyPublisher = bodyPublisher
    this
  }

  def DELETE(): Builder = {
    this.method = Method.DELETE
    this.bodyPublisher = BodyPublishers.noBody()
    this
  }

  def build(): HttpRequest = {
    if (uri == null) throw new IllegalStateException("URI must be set")

    new HttpRequestImpl(
      // uri,
      // method,
      // headers,
      // bodyPublisher,
      // expectContinue,
      // version,
      // timeout,
    )
  }

  def copy(): Builder =
    return new HttpRequestBuilderImpl(
      uri = this.uri,
      expectContinue = this.expectContinue,
      version = this.version,
      timeout = this.timeout,
      method = this.method,
      bodyPublisher = this.bodyPublisher,
      headerMap = this.headerMap.clone(),
    )
}

class HttpRequestImpl extends HttpRequest {}
object HttpRequestImpl {}
