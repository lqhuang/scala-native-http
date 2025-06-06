package snhttp.jdk

import java.net.URI
import java.time.Duration
import java.util.function.BiPredicate
import java.util.{Optional, Locale}
import java.util.List as JList
import java.util.Map as JMap
import java.util.Objects.requireNonNull
import java.io.Closeable
import java.net.http.HttpClient
import java.net.http.HttpClient.Version
import java.net.http.HttpHeaders
import java.net.http.HttpRequest
import java.net.http.HttpRequest.{Builder, BodyPublisher, BodyPublishers}
import java.util.TreeMap

import snhttp.jdk.PropertyUtils
import snhttp.Method

class HttpRequestBuilderImpl(
    var uri: Option[URI] = None,
    var expectContinue: Boolean = false,
    var method: String = Method.GET.value,
    var version: Option[Version] = None,
    var timeout: Option[Duration] = None,
    var bodyPublisher: Option[BodyPublisher] = None,
    val headerMap: TreeMap[String, JList[String]] = new TreeMap(String.CASE_INSENSITIVE_ORDER),
) extends HttpRequest.Builder {

  def uri(uri: URI): Builder = {
    requireNonNull(uri)
    val scheme = uri.getScheme().toLowerCase(Locale.US)
    require(scheme == "http" || scheme == "https", s"invalid URI scheme ${scheme}")
    val host = uri.getHost()
    requireNonNull(host)

    this.uri = Some(uri)
    this
  }

  def expectContinue(enable: Boolean): Builder = {
    requireNonNull(enable)
    this.expectContinue = enable
    this
  }

  def version(version: HttpClient.Version): Builder = {
    requireNonNull(version)
    this.version = Some(version)
    this
  }

  private def checkHeader(name: String, value: String): (String, String) =
    requireNonNull(name, "Header key can not be null")
    requireNonNull(value, "Header value can not be null")
    val key = name.trim()
    require(!key.isEmpty, s"invalid header name ${name}")
    val valueTrimmed = value.trim()
    require(!valueTrimmed.isEmpty, s"invalid header value ${value}")
    return (key, valueTrimmed)

  /// Add a header to the request.
  /// If the header already exists, the value is appended to the existing values.
  def header(name: String, value: String): Builder = {
    val (key, valueTrimmed) = checkHeader(name, value)

    if headerMap.containsKey(key)
    then headerMap.get(key).add(valueTrimmed)
    else headerMap.put(key, JList.of(valueTrimmed))

    this
  }

  /// Set a header to the request.
  /// If the header already exists, the original value is replaced.
  def setHeader(name: String, value: String): Builder = {
    val (key, valueTrimmed) = checkHeader(name, value)
    // if (headerMap.containsKey(key))
    //   headerMap.remove(key)
    headerMap.put(key, JList.of(valueTrimmed))
    this
  }

  def headers(headers: String*): Builder = {
    require(headers.length % 2 == 0, "Headers must be supplied in name-value pairs")
    headers.grouped(2).foreach(pair => header(pair(0), pair(1)))
    this
  }

  def timeout(duration: Duration): Builder = {
    requireNonNull(duration)
    require(!duration.isNegative && !duration.isZero, "Duration must be positive")
    this.timeout = Some(duration)
    this
  }

  def method(method: String, bodyPublisher: BodyPublisher): Builder = {
    requireNonNull(method)
    requireNonNull(bodyPublisher)

    Method(method.trim().toUpperCase()) match {
      case None =>
        throw new IllegalArgumentException(s"Invalid HTTP method: ${method}")
      case Some(m) =>
        this.method = m.value
        this.bodyPublisher = Some(bodyPublisher)
    }

    this
  }

  def GET(): Builder = {
    this.method = Method.GET.value
    this.bodyPublisher = None
    this
  }

  def POST(bodyPublisher: BodyPublisher): Builder = {
    this.method = Method.POST.value
    this.bodyPublisher = Some(bodyPublisher)
    this
  }

  def PUT(bodyPublisher: BodyPublisher): Builder = {
    this.method = Method.PUT.value
    this.bodyPublisher = Some(bodyPublisher)
    this
  }

  def DELETE(): Builder = {
    this.method = Method.DELETE.value
    this.bodyPublisher = None
    this
  }

  def HEAD(): Builder = {
    this.method = Method.HEAD.value
    this.bodyPublisher = None
    this
  }

  def build(): HttpRequest = {
    require(!this.uri.isEmpty, "URI must be set")
    HttpRequestImpl(this)
  }

  private def deepcloneHeaderMap(): TreeMap[String, JList[String]] =
    /// deep copy for headerMap
    val newHeaderMap: TreeMap[String, JList[String]] = new TreeMap(String.CASE_INSENSITIVE_ORDER)
    val entries = this.headerMap
      .entrySet()
      .forEach { entry =>
        val key = entry.getKey
        val values = entry.getValue
        newHeaderMap.put(key, JList.copyOf(values))
      }
    newHeaderMap

  def copy(): Builder =
    new HttpRequestBuilderImpl(
      uri = this.uri,
      expectContinue = this.expectContinue,
      version = this.version,
      method = this.method,
      timeout = this.timeout,
      bodyPublisher = this.bodyPublisher,
      headerMap = deepcloneHeaderMap(),
    )
}

case class HttpRequestImpl(private val builder: HttpRequestBuilderImpl) extends HttpRequest {

  def close(): Unit = ()

  override def bodyPublisher(): Optional[BodyPublisher] =
    builder.bodyPublisher match {
      case Some(publisher) => Optional.ofNullable(publisher)
      case None            => Optional.empty()
    }

  override def method(): String = builder.method

  override def timeout(): Optional[Duration] =
    builder.timeout match {
      case Some(duration) => Optional.ofNullable(duration)
      case None           => Optional.empty()
    }

  override def expectContinue(): Boolean = builder.expectContinue

  override def uri(): URI =
    builder.uri match {
      case Some(uri) => uri
      case None => throw new IllegalStateException("Unreachable. URI must be set in the builder")
    }

  override def version(): Optional[Version] =
    builder.version match {
      case Some(version) => Optional.ofNullable(version)
      case None          => Optional.empty()
    }

  override def headers(): HttpHeaders =
    HttpHeaders.of(builder.headerMap, PropertyUtils.allowedHeadersPredicate)
}
