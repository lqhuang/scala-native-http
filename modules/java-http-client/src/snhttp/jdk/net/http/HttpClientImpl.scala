package snhttp.jdk.net.http

import java.io.IOException
import java.net.{Authenticator, CookieHandler, InetAddress, Proxy, ProxySelector, URI}
import java.net.http.{HttpClient, HttpRequest, HttpResponse, HttpHeaders, WebSocket}
import java.net.http.HttpClient.{Builder, Redirect, Version}
import java.net.http.HttpResponse.{BodyHandler, PushPromiseHandler}
import java.time.Duration
import java.util.{ArrayList, Optional, TreeMap}
import java.util.List as JList
import java.util.Map as JMap
import java.util.Objects.requireNonNull
import java.util.concurrent.{CompletableFuture, Executor}
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.{SSLContext, SSLParameters}

import scala.collection.mutable.{LinkedHashSet, HashMap}
import scala.concurrent.ExecutionContext
import scala.scalanative.unsafe.{Size, Ptr, Zone, toCString, stackalloc, Tag, CLong}

import snhttp.experimental.libcurl
import snhttp.experimental.libcurl.core.{CurlSlist, CurlInfo}
import snhttp.experimental.libcurl.multi.{CurlMsgCode, CurlMsg}
import snhttp.experimental.libcurl.options.CurlHttpVersion
import snhttp.experimental.libcurl.header.{CurlHeader, CURLH}
import snhttp.utils.PointerFinalizer

class HttpClientBuilderImpl() extends Builder:

  protected[http] var _cookieHandler: Option[CookieHandler] = None
  protected[http] var _connectTimeout: Option[Duration] = None
  protected[http] var _redirect: Redirect = Redirect.NORMAL
  protected[http] var _proxy: Option[ProxySelector] = None
  protected[http] var _authenticator: Option[Authenticator] = None
  protected[http] var _version: Version = Version.HTTP_1_1
  protected[http] var _executor: Option[Executor] = None
  protected[http] var _sslContext: Option[SSLContext] = None
  protected[http] var _sslParams: Option[SSLParameters] = None
  protected[http] var _priority: Int = -1
  protected[http] var _localAddr: Option[InetAddress] = None

  def cookieHandler(cookieHandler: CookieHandler): Builder =
    requireNonNull(cookieHandler)
    this._cookieHandler = Some(cookieHandler)
    this

  def connectTimeout(duration: Duration): Builder =
    requireNonNull(duration)
    require(!duration.isNegative && !duration.isZero, "duration must be positive")
    this._connectTimeout = Some(duration)
    this

  def sslContext(sslContext: SSLContext): Builder =
    requireNonNull(sslContext)
    this._sslContext = Some(sslContext)
    this

  def sslParameters(sslParams: SSLParameters): Builder =
    requireNonNull(sslParams)
    this._sslParams = Some(sslParams)
    this

  def executor(executor: Executor): Builder =
    requireNonNull(executor)
    this._executor = Some(executor)
    this

  def followRedirects(redirect: Redirect): Builder =
    requireNonNull(redirect)
    this._redirect = redirect
    this

  def version(version: Version): Builder =
    requireNonNull(version)
    this._version = version
    this

  def priority(priority: Int): Builder =
    require(priority >= 1 && priority <= 256, "priority must be between 1 and 256 (inclusive)")
    this._priority = priority
    this

  def proxy(proxy: ProxySelector): Builder =
    requireNonNull(proxy)
    this._proxy = Some(proxy)
    this

  def authenticator(authenticator: Authenticator): Builder =
    requireNonNull(authenticator)
    this._authenticator = Some(authenticator)
    this

  override def localAddress(localAddr: InetAddress): Builder =
    requireNonNull(localAddr)
    this._localAddr = Some(localAddr)
    this

  def build(): HttpClient =
    new HttpClientImpl(this)

end HttpClientBuilderImpl

class HttpClientImpl(
    private[http] val builder: HttpClientBuilderImpl,
) extends HttpClient:

  val globalInitRet = libcurl.core.curl_global_init(libcurl.core.CURL_GLOBAL.DEFAULT)
  globalInitRet match
    case libcurl.core.CurlCode.OK => ()
    case _ =>
      throw new RuntimeException(
        s"Failed to initialize libcurl global state: error code ${globalInitRet}",
      )

  protected[http] var ptr: Ptr[libcurl.multi.CurlMulti] = null
  try {
    ptr = libcurl.multi.multiInit()
    if (ptr == null)
      throw new RuntimeException("Failed to initialize CURLM pointer")
  } catch {
    case e: RuntimeException =>
      libcurl.core.curl_global_cleanup()
      throw e
  }
  PointerFinalizer(
    this,
    ptr,
    _ptr => {
      val ret = libcurl.multi.multiCleanup(_ptr)
      libcurl.core.curl_global_cleanup()
      if (ret != 0)
        throw new RuntimeException(s"Failed to cleanup CURLM pointer: error code ${ret}")
    },
  ): Unit

  private val _started = new AtomicBoolean(false)
  private val _alive = new AtomicBoolean(false)
  private val _terminated = new AtomicBoolean(false)
  private val _shutdown = new AtomicBoolean(false)

  private val requests = new LinkedHashSet[HttpRequest]()

  private[http] lazy val _sslContext = builder._sslContext match
    case Some(ctx) => ctx
    case None      => SSLContext.getDefault()
  private[http] lazy val _sslParams = builder._sslParams match
    case Some(params) => params
    case None         => _sslContext.getDefaultSSLParameters()

  private val _executor: Executor =
    builder._executor.getOrElse(ExecutionContext.global)

  /**
   * Map of active connections: request -> connection info (curl easy handle pointer)
   */
  private val connections = HashMap()

  def cookieHandler(): Optional[CookieHandler] =
    builder._cookieHandler match
      case Some(cookieHandler) => Optional.of(cookieHandler)
      case None                => Optional.empty()

  def connectTimeout(): Optional[Duration] =
    builder._connectTimeout match
      case Some(duration) => Optional.of(duration)
      case None           => Optional.empty()

  def followRedirects(): Redirect =
    builder._redirect

  def proxy(): Optional[ProxySelector] =
    builder._proxy match
      case Some(proxySelector) => Optional.of(proxySelector)
      case None                => Optional.empty()

  def sslContext(): SSLContext =
    _sslContext

  // TODO: return a copy
  def sslParameters(): SSLParameters =
    _sslParams

  def authenticator(): Optional[Authenticator] =
    builder._authenticator.map(Optional.of).getOrElse(Optional.empty())

  def version(): Version =
    builder._version

  def executor(): Optional[Executor] =
    _executor match
      case null => Optional.empty()
      case exec => Optional.of(exec)

  def send[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
  ): HttpResponse[T] = {
    requireNonNull(request, "request cannot be null")
    requireNonNull(responseBodyHandler, "responseBodyHandler cannot be null")
    requireNonShutdown()

    try
      sendAsync(request, responseBodyHandler).get()
    catch {
      case exc: ExecutionException =>
        val cause = exc.getCause()
        cause match {
          case e: (IOException | RuntimeException) => throw e
          case _ => throw new IOException("Unexpected error during HTTP request", cause)
        }
      case e: InterruptedException =>
        Thread.currentThread().interrupt()
        throw new IOException("HTTP request was interrupted", e)
    }
  }

  /**
   * Equivalent to: `sendAsync(request, responseBodyHandler, null)`.
   */
  def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
  ): CompletableFuture[HttpResponse[T]] =
    sendAsync(request, responseBodyHandler, null)

  def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
      pushPromiseHandler: PushPromiseHandler[T],
  ): CompletableFuture[HttpResponse[T]] = {
    requireNonNull(request, "`request: HttpRequest` cannot be null")
    requireNonNull(responseBodyHandler, "`responseBodyHandler: BodyHandler[T]` cannot be null")

    if (pushPromiseHandler != null)
      throw new NotImplementedError("`PushPromiseHandler` feature is not implemented yet.")

    val conn = HttpConnection(request, responseBodyHandler, this)
    val code = libcurl.multi.multiAddHandle(ptr, conn.ptr)

    CompletableFuture.supplyAsync(
      () => {
        val stillRunningPtr = stackalloc[Int]()
        val _ = libcurl.multi.multiPerform(ptr, stillRunningPtr)

        /**
         * set started flag for http client
         */
        _started.compareAndExchange(false, true)

        val msgQPtr = stackalloc[Int]() // zero-initialized
        var msg: Ptr[CurlMsg] = null

        while {
          msg = libcurl.multi.multiInfoRead(ptr, msgQPtr)
          // Stop
          // if no more messages
          // or if we have catched the message for curr connection (easyHandle)
          msg == null || ((!msg).easyHandle == conn.ptr && (!msg).msg == CurlMsgCode.DONE)
        } do ()

        // We unconfidently assueme here that msg is not null and (!msg).easyHandle == conn.ptr
        if (msg == null || (!msg).easyHandle != conn.ptr)
          throw new IllegalStateException(
            s"Failed to complete HTTP request: no DONE message received for the connection",
          )

        /**
         * Collect response code
         */
        val _respCodePtr = stackalloc[CLong]()
        val _ = libcurl.easy.easyGetInfo(
          conn.ptr,
          CurlInfo.RESPONSE_CODE,
          _respCodePtr,
        )

        /**
         * Collect http version info
         */
        val _versionPtr = stackalloc[CurlHttpVersion]()
        val _ = libcurl.easy.easyGetInfo(
          conn.ptr,
          CurlInfo.HTTP_VERSION,
          _versionPtr,
        )
        val _version = !_versionPtr match
          case CurlHttpVersion.VERSION_1_1               => HttpClient.Version.HTTP_1_1
          case CurlHttpVersion.VERSION_2_0               => HttpClient.Version.HTTP_2
          case CurlHttpVersion.VERSION_2TLS              => HttpClient.Version.HTTP_2
          case CurlHttpVersion.VERSION_2_PRIOR_KNOWLEDGE => HttpClient.Version.HTTP_2
          case _ =>
            throw new RuntimeException(
              s"Unsupported HTTP version response code with libcurl: ${!_versionPtr}",
            )

        /**
         * Collect response headers
         */
        var _headerPtr: Ptr[CurlHeader] = null
        var _prevHeaderPtr: Ptr[CurlHeader] = null
        val _map: TreeMap[String, JList[String]] = new TreeMap(String.CASE_INSENSITIVE_ORDER)
        while {
          _headerPtr = libcurl.header.easyNextHeader(conn.ptr, CURLH.HEADER, -1, _prevHeaderPtr)
          _headerPtr != null
        } do {
          val name = (!_headerPtr).name.toString
          val value = (!_headerPtr).value.toString

          _map.containsKey(name) match
            case true => _map.get(name).add(value)
            case false =>
              val xs = new ArrayList[String]()
              xs.add(value)
              _map.put(name, xs)

          _prevHeaderPtr = _headerPtr
        }
        val respHeaders = HttpHeaders.of(_map, (_, _) => true)

        val response = HttpResponseImpl[T](
          request,
          _version,
          (!_respCodePtr).toInt,
          respHeaders,
          ???,
        )

        // Clean up
        val r = libcurl.multi.multiRemoveHandle(ptr, conn.ptr)
        if (r != 0)
          throw new IOException(
            s"Failed to remove easy handle from multi handle: error code ${r}",
          )
        conn.cleanup()

        response
      },
      _executor,
    )

  }

  override def shutdown(): Unit =
    _shutdown.compareAndExchange(true, false)
    // In a real implementation, this would:
    // 1. Stop accepting new requests
    // 2. Allow existing requests to complete
    // 3. Close idle connections
    ???

  override def awaitTermination(duration: Duration): Boolean = {
    requireNonNull(duration, "duration cannot be null")

    val start = System.nanoTime()
    val timeout = duration.toNanos()

    while (!_terminated.get() && (System.nanoTime() - start) < timeout)
      try Thread.sleep(1) // Small sleep to avoid busy waiting
      catch
        case _: InterruptedException =>
          Thread.currentThread().interrupt()
          return false

    _terminated.get()
  }

  override def isTerminated(): Boolean =
    _terminated.get()

  override def shutdownNow(): Unit =
    // In a real implementation, this would:
    // 1. Cancel all pending requests
    // 2. Close all connections immediately
    // 3. Shutdown executor services
    shutdown()
    while awaitTermination(Duration.ofSeconds(0L)) == false do ()

  override def newWebSocketBuilder(): WebSocket.Builder = {
    requireNonShutdown()

    ???
  }

  protected def requireNonShutdown(): Unit =
    if (_shutdown.get()) throw new IllegalStateException("HttpClient has been shut down")

end HttpClientImpl
