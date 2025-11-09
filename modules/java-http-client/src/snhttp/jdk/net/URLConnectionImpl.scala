package snhttp.jdk.net

import java.io.{IOException, InputStream, OutputStream}
import java.net.{
  URL,
  URLConnection,
  FileNameMap,
  ContentHandler,
  ContentHandlerFactory,
  URLStreamHandler,
}
import java.net.http.HttpHeaders
import java.util.{List as JList, Map as JMap}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.Objects.requireNonNull

trait URLConnectionImplMixin(
    private val url: URL,
    private val factory: ContentHandlerFactory,
    @volatile private var _connTimeout: Int = 0,
    @volatile private var _readTimeout: Int = 0,
    @volatile private var _doInput: Boolean = true,
    @volatile private var _doOutput: Boolean = false,
    @volatile private var _allowUserInteraction: Boolean = false,
    @volatile private var _useCaches: Option[Boolean] = None,
    @volatile private var _ifModifiedSince: Long = 0,
    @volatile private var _contentLengthLong: Long = -1,
    @volatile private var _contentType: Option[String] = None,
    @volatile private var _contentEncoding: Option[String] = None,
) extends URLConnection {

  val _connected: AtomicBoolean = new AtomicBoolean(false)

  def connect(): Unit = ???

  def setConnectTimeout(timeout: Int): Unit =
    require(timeout >= 0, "Connect timeout cannot be negative")
    _connTimeout = timeout

  def getConnectTimeout(): Int =
    _connTimeout

  def setReadTimeout(timeout: Int): Unit =
    require(timeout >= 0, "Read timeout cannot be negative")
    _readTimeout = timeout

  def getReadTimeout(): Int =
    _readTimeout

  def getURL(): URL =
    url

  def getContentLength(): Int =
    _contentLengthLong > Int.MaxValue.toLong match
      case true  => -1
      case false => _contentLengthLong.toInt

  def getContentLengthLong(): Long =
    _contentLengthLong

  def getContentType(): String = _contentType.getOrElse(null)

  def getContentEncoding(): String = _contentEncoding.getOrElse(null)

  /// This method first determines the content type of the object by calling
  /// the `getContentType` method. If this is the first time that the
  /// application has seen that specific content type, a content handler for
  /// that content type is created.
  ///
  ///
  def getContent(): Any =
    val contentType = getContentType()
    factory.createContentHandler(contentType) match
      case null    => null
      case handler => handler.getContent(this)

  def getContent(classes: Array[Class[_]]): Any = ???

  /// API Note:
  ///
  /// The `InputStream` returned by this method can wrap an `InflaterInputStream`,
  /// whose `read(byte[], int, int)` method can modify any element of the output buffer.
  def getInputStream(): InputStream =
    ???

  def getOutputStream(): OutputStream =
    ???

  def setDoInput(doInput: Boolean): Unit =
    requireUnconnected()
    _doInput = doInput

  def getDoInput(): Boolean =
    _doInput

  def setDoOutput(doOutput: Boolean): Unit =
    requireUnconnected()
    _doOutput = doOutput

  def getDoOutput(): Boolean =
    _doOutput

  def setAllowUserInteraction(allowUserInteraction: Boolean): Unit =
    requireUnconnected()
    _allowUserInteraction = allowUserInteraction

  def getAllowUserInteraction(): Boolean =
    _allowUserInteraction

  def setUseCaches(useCaches: Boolean): Unit =
    requireUnconnected()
    _useCaches = Some(useCaches)

  def getUseCaches(): Boolean =
    _useCaches.getOrElse(getDefaultUseCaches())

  def getDefaultUseCaches(): Boolean =
    URLConnection.getDefaultUseCaches(url.getProtocol())

  def setDefaultUseCaches(defaultVal: Boolean): Unit =
    URLConnection.setDefaultUseCaches(url.getProtocol(), defaultVal)

  def setRequestProperty(key: String, value: String): Unit =
    requireNonNull(key)
    requireUnconnected()
    ???

  def addRequestProperty(key: String, value: String): Unit =
    requireNonNull(key)
    requireUnconnected()
    ???

  def getRequestProperty(key: String): String =
    requireUnconnected()
    ???

  def getRequestProperties(): JMap[String, JList[String]] =
    requireUnconnected()
    ???

  protected def requireUnconnected(): Unit =
    if (_connected.get()) throw new IllegalStateException("Already connected")

}

class URLConnectionImpl protected (url: URL, factory: ContentHandlerFactory)
    extends URLConnection(url)
    with URLConnectionImplMixin(url, factory) {
  override def toString(): String = ???
}
