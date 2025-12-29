package snhttp.jdk.net.http

import java.io.IOException
import java.net.{Authenticator, CookieHandler, InetAddress, ProxySelector}
import java.net.http.{HttpClient, HttpRequest, HttpResponse, WebSocket}
import java.net.http.HttpClient.{Builder, Redirect, Version}
import java.net.http.HttpResponse.{BodyHandler, PushPromiseHandler}
import java.time.Duration
import java.util.Optional
import java.util.Objects.requireNonNull
import java.util.concurrent.{CompletableFuture, Executor}
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.{SSLContext, SSLParameters}

import scala.collection.mutable.HashMap
import scala.concurrent.ExecutionContext
import scala.scalanative.unsafe.Ptr

import snhttp.experimental.libcurl
import snhttp.experimental.libcurl.{
  CurlSlist,
  CurlInfo,
  CurlMsgCode,
  CurlMsg,
  CurlHeader,
  CurlGlobalFlag,
  CurlErrCode,
  CurlMulti,
}
import snhttp.jdk.net.http.internal.HttpConnection
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

  val globalInitRet = libcurl.globalInit(CurlGlobalFlag.DEFAULT)
  globalInitRet match
    case CurlErrCode.OK => ()
    case _ =>
      throw new RuntimeException(
        s"Failed to initialize libcurl global state: error code ${globalInitRet}",
      )

  protected[http] var ptr: Ptr[CurlMulti] = null
  try {
    ptr = libcurl.multiInit()
    if (ptr == null)
      throw new RuntimeException("Failed to initialize CURLM pointer")
  } catch {
    case e: RuntimeException =>
      libcurl.globalCleanup()
      throw e
  }
  PointerFinalizer(
    this,
    ptr,
    _ptr => {
      val ret = libcurl.multiCleanup(_ptr)
      libcurl.globalCleanup()
      if (ret != 0)
        throw new RuntimeException(s"Failed to cleanup CURLM pointer: error code ${ret}")
    },
  ): Unit

  private val _started = new AtomicBoolean(false)
  private val _alive = new AtomicBoolean(false)
  private val _terminated = new AtomicBoolean(false)
  private val _shutdown = new AtomicBoolean(false)

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
  // private val connections = HashMap()

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

    CompletableFuture.supplyAsync(
      () =>
        try {
          conn.perform()

          /* set started flag for http client */
          _started.setPlain(true)

          conn.pollUntilDone()

          val response = conn.buildResponse()
          response
        } catch {
          case e: Exception =>
            throw new RuntimeException(s"HTTP request failed: ${e.getMessage()}", e)
        } finally conn.close(),
      _executor,
    )

  }

  override def shutdown(): Unit =
    _shutdown.compareAndExchange(true, false)
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
    // shutdown()
    // while awaitTermination(Duration.ofSeconds(0L)) == false do ()
    ???

  override def newWebSocketBuilder(): WebSocket.Builder = {
    requireNonShutdown()

    ???
  }

  protected def requireNonShutdown(): Unit =
    if (_shutdown.get()) throw new IllegalStateException("HttpClient has been shut down")

end HttpClientImpl
