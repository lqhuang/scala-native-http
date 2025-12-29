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

  protected[http] var _cookieHandler: Optional[CookieHandler] = Optional.empty()
  protected[http] var _connectTimeout: Optional[Duration] = Optional.empty()
  protected[http] var _redirect: Redirect = Redirect.NORMAL
  protected[http] var _proxy: Optional[ProxySelector] = Optional.empty()
  protected[http] var _authenticator: Optional[Authenticator] = Optional.empty()
  protected[http] var _version: Version = Version.HTTP_1_1
  protected[http] var _executor: Optional[Executor] = Optional.empty()
  protected[http] var _sslContext: Optional[SSLContext] = Optional.empty()
  protected[http] var _sslParams: Optional[SSLParameters] = Optional.empty()
  protected[http] var _priority: Int = -1
  protected[http] var _localAddr: Optional[InetAddress] = Optional.empty()

  def cookieHandler(cookieHandler: CookieHandler): Builder =
    requireNonNull(cookieHandler)
    this._cookieHandler = Optional.of(cookieHandler)
    this

  def connectTimeout(duration: Duration): Builder =
    requireNonNull(duration)
    require(!duration.isNegative && !duration.isZero, "duration must be positive")
    this._connectTimeout = Optional.of(duration)
    this

  def sslContext(sslContext: SSLContext): Builder =
    requireNonNull(sslContext)
    this._sslContext = Optional.of(sslContext)
    this

  def sslParameters(sslParams: SSLParameters): Builder =
    requireNonNull(sslParams)
    this._sslParams = Optional.of(sslParams)
    this

  def executor(executor: Executor): Builder =
    requireNonNull(executor)
    this._executor = Optional.of(executor)
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
    this._proxy = Optional.of(proxy)
    this

  def authenticator(authenticator: Authenticator): Builder =
    requireNonNull(authenticator)
    this._authenticator = Optional.of(authenticator)
    this

  override def localAddress(localAddr: InetAddress): Builder =
    requireNonNull(localAddr)
    this._localAddr = Optional.of(localAddr)
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
    case exc: RuntimeException =>
      // if null ptr, globalCleanup still needs to be called
      libcurl.globalCleanup()
      throw exc
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
  private val _terminable = new AtomicBoolean(false)
  private val _shutdown = new AtomicBoolean(false)

  protected[http] lazy val _sslContext =
    builder._sslContext.orElse(SSLContext.getDefault())
  protected[http] lazy val _sslParams =
    builder._sslParams.orElse(_sslContext.getDefaultSSLParameters())
  protected[http] lazy val _executor: Executor =
    builder._executor.orElse(ExecutionContext.global)

  /**
   * Main methods of HttpClient
   */

  def send[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
  ): HttpResponse[T] = {
    requireNonNull(request, "request cannot be null")
    requireNonNull(responseBodyHandler, "responseBodyHandler cannot be null")

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

  /** Equivalent to: `sendAsync(request, responseBodyHandler, null)`. */
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
    requireNonShutdown()

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
    _terminable.compareAndExchange(false, true): Unit
    _shutdown.compareAndExchange(false, true): Unit

  /**
   * Note from JDK docs:
   *
   * Blocks until all operations have completed execution after a shutdown request, or the duration
   * elapses, or the current thread is interrupted, whichever happens first. Operations are any
   * tasks required to run a request previously submitted with send or sendAsync to completion.
   *
   * This method does not wait if the duration to wait is less than or equal to zero. In this case,
   * the method just tests if the thread has terminated.
   */
  override def awaitTermination(duration: Duration): Boolean = {
    requireNonNull(duration, "duration cannot be null")

    if (duration.isNegative() || duration.isZero())
      return isTerminated()

    ???
  }

  /**
   * Note from JDK docs:
   *
   * Note that isTerminated is never true unless either `shutdown` or `shutdownNow` was called
   * first.
   */
  override def isTerminated(): Boolean =
    _terminated.get()

  override def shutdownNow(): Unit =
    shutdown()

  override def close(): Unit =
    val interrupted = new AtomicBoolean(false)
    while !isTerminated() do {
      shutdown()
      try
        awaitTermination(Duration.ofSeconds(5L)): Unit
      catch {
        case e: InterruptedException =>
          if (interrupted.compareAndSet(false, true))
            shutdownNow()
      }
    }
    if (interrupted.get())
      Thread.currentThread().interrupt()

  /**
   * Getters for HttpClient properties
   */

  def cookieHandler(): Optional[CookieHandler] =
    builder._cookieHandler

  def connectTimeout(): Optional[Duration] =
    builder._connectTimeout

  def followRedirects(): Redirect =
    builder._redirect

  def proxy(): Optional[ProxySelector] =
    builder._proxy

  /**
   * Note from JDK docs:
   *
   * If no `SSLContext` was set in this client's builder, then the default context is returned.
   */
  def sslContext(): SSLContext =
    _sslContext

  /**
   * Note from JDK docs:
   *
   * If no SSLParameters were set in the client's builder, then an implementation specific default
   * set of parameters, that the client will use, is returned.
   */
  def sslParameters(): SSLParameters =
    _sslParams

  def authenticator(): Optional[Authenticator] =
    builder._authenticator

  def version(): Version =
    builder._version

  def executor(): Optional[Executor] =
    builder._executor

  override def newWebSocketBuilder(): WebSocket.Builder =
    requireNonShutdown()
    ???

  /**
   * Private helpers
   */

  protected def requireNonShutdown(): Unit =
    if (_shutdown.get())
      throw new IllegalStateException(
        "HttpClient has been shutdown, no new request will be accepted.",
      )

end HttpClientImpl
