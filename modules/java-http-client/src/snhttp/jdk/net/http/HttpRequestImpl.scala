package snhttp.jdk.net.http

import java.io.Closeable
import java.net.URI
import java.net.http.{HttpClient, HttpHeaders, HttpRequest}
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest.{Builder, BodyPublisher, BodyPublishers}
import java.time.Duration
import java.util.List as JList
import java.util.Map as JMap
import java.util.{ArrayList, Locale, Optional, TreeMap}
import java.util.Objects.requireNonNull
import java.util.function.BiPredicate

import snhttp.core.Method
import snhttp.jdk.net.http.internal.PropertyUtils

case class HttpRequestBuilderImpl(
    protected[http] var _uri: Optional[URI] = Optional.empty(),
    protected[http] var _expectContinue: Boolean = false,
    protected[http] var _method: Method = "GET",
    protected[http] var _version: Optional[Version] = Optional.empty(),
    protected[http] var _timeout: Optional[Duration] = Optional.empty(),
    protected[http] var _bodyPublisher: Optional[BodyPublisher] = Optional.empty(),
    protected[http] val _headerMap: TreeMap[String, JList[String]] = //
      new TreeMap(String.CASE_INSENSITIVE_ORDER),
) extends HttpRequest.Builder:

  def uri(uri: URI): Builder =
    requireNonNull(uri)
    val scheme = uri.getScheme().toLowerCase(Locale.US)
    require(scheme == "http" || scheme == "https", s"invalid URI scheme ${scheme}")
    requireNonNull(uri.getHost())
    this._uri = Optional.of(uri)
    this

  def expectContinue(enable: Boolean): Builder = {
    requireNonNull(enable)
    this._expectContinue = enable
    this
  }

  def version(version: HttpClient.Version): Builder = {
    requireNonNull(version)
    this._version = Optional.of(version)
    this
  }

  /// Add a header to the request.
  /// If the header already exists, the value is appended to the existing values.
  def header(name: String, value: String): Builder = {
    val (key, valueTrimmed) = checkHeader(name, value)

    if _headerMap.containsKey(key)
    then _headerMap.get(key).add(valueTrimmed)
    else _headerMap.put(key, new ArrayList(JList.of(valueTrimmed)))

    this
  }

  /**
   * Set a header to the request. If the header already exists, the original value is replaced.
   */
  def setHeader(name: String, value: String): Builder =
    val (key, valueTrimmed) = checkHeader(name, value)
    this._headerMap.put(key, new ArrayList(JList.of(valueTrimmed)))
    this

  def headers(headers: Array[? <: String]): Builder =
    require(headers.length % 2 == 0, "Headers must be supplied in name-value pairs")
    headers.grouped(2).foreach(pair => header(pair(0), pair(1)))
    this

  def timeout(duration: Duration): Builder =
    requireNonNull(duration)
    require(!duration.isNegative && !duration.isZero, "Duration must be positive")
    this._timeout = Optional.of(duration)
    this

  def method(method: String, bodyPublisher: BodyPublisher): Builder = {
    requireNonNull(method)
    requireNonNull(bodyPublisher)

    Method(method) match {
      case None =>
        throw new IllegalArgumentException(s"Invalid HTTP method: ${method}")
      case Some(m) =>
        this._method = m
        this._bodyPublisher = Optional.of(bodyPublisher)
    }

    this
  }

  def GET(): Builder =
    this._method = "GET"
    this._bodyPublisher = Optional.empty()
    this

  def POST(bodyPublisher: BodyPublisher): Builder =
    this._method = "POST"
    this._bodyPublisher = Optional.of(bodyPublisher)
    this

  def PUT(bodyPublisher: BodyPublisher): Builder =
    this._method = "PUT"
    this._bodyPublisher = Optional.of(bodyPublisher)
    this

  def DELETE(): Builder =
    this._method = "DELETE"
    this._bodyPublisher = Optional.empty()
    this

  override def HEAD(): Builder =
    this._method = "HEAD"
    this._bodyPublisher = Optional.empty()
    this

  def build(): HttpRequest =
    require(!_uri.isEmpty, "URI must be set")
    HttpRequestImpl(this)

  def copy(): Builder =
    new HttpRequestBuilderImpl(
      _uri = _uri,
      _expectContinue = _expectContinue,
      _version = _version,
      _method = _method,
      _timeout = _timeout,
      _bodyPublisher = _bodyPublisher,
      _headerMap = deepcloneHeaderMap(),
    )

  /**
   * Private helpers
   */

  private def checkHeader(name: String, value: String): (String, String) =
    requireNonNull(name, "Header key can not be null")
    requireNonNull(value, "Header value can not be null")
    val key = name.trim()
    require(!key.isEmpty, s"invalid header name ${name}")
    val valueTrimmed = value.trim()
    require(!valueTrimmed.isEmpty, s"invalid header value ${value}")
    return (key, valueTrimmed)

  private def deepcloneHeaderMap(): TreeMap[String, JList[String]] =
    /// deep copy for headerMap
    val newHeaderMap: TreeMap[String, JList[String]] = new TreeMap(String.CASE_INSENSITIVE_ORDER)
    val entries = _headerMap
      .entrySet()
      .forEach { entry =>
        val key = entry.getKey
        val values = entry.getValue
        val _ = newHeaderMap.put(key, JList.copyOf(values))
      }
    newHeaderMap

end HttpRequestBuilderImpl

case class HttpRequestImpl(private val builder: HttpRequestBuilderImpl) extends HttpRequest:

  def close(): Unit = ()

  override def bodyPublisher(): Optional[BodyPublisher] =
    builder._bodyPublisher

  override def method(): String = builder._method

  override def timeout(): Optional[Duration] =
    builder._timeout

  override def expectContinue(): Boolean =
    builder._expectContinue

  override def uri(): URI =
    builder._uri.orElseThrow(() =>
      new IllegalStateException("Unreachable. URI must be set in the builder"),
    )

  override def version(): Optional[Version] =
    builder._version

  override def headers(): HttpHeaders =
    HttpHeaders.of(builder._headerMap, PropertyUtils.allowedHeadersPredicate)

end HttpRequestImpl
