package snhttp.jdk.net.http

import java.io.IOException
import java.net.{Authenticator, CookieHandler, InetAddress, ProxySelector}
import java.net.ConnectException
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
import scala.scalanative.unsafe.{Ptr, stackalloc}
import scala.scalanative.unsigned.UnsignedRichInt

import _root_.snhttp.experimental.curl.CurlErrCodeException
import _root_.snhttp.experimental.curl.CurlMulti as CurlMultiWrapper
import _root_.snhttp.experimental.libcurl
import _root_.snhttp.experimental.libcurl.{
  Curl,
  CurlSlist,
  CurlInfo,
  CurlMsgCode,
  CurlMsg,
  CurlHeader,
  CurlGlobalFlag,
  CurlErrCode,
  CurlMulti,
  CurlMultiCode,
}
import _root_.snhttp.experimental.libcurl.CurlErrCode.RichCurlErrCode
import _root_.snhttp.experimental.libcurl.CurlMultiCode.RichCurlMultiCode
import _root_.snhttp.jdk.net.http.internal.HttpConnection
import _root_.snhttp.utils.PointerFinalizer

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

final class HttpClientImpl(
    private[http] val builder: HttpClientBuilderImpl,
) extends HttpClient:

  private[http] val ptr: Ptr[CurlMulti] = libcurl.multiInit()
  if (ptr == null)
    throw new RuntimeException("Failed to initialize CURLM pointer")
  PointerFinalizer(
    this,
    ptr,
    _ptr => {
      val ret = libcurl.multiCleanup(_ptr)
      if (ret != 0)
        throw new RuntimeException(s"Failed to cleanup CURLM pointer: error code ${ret}")
    },
  ): Unit

  private val _multi = CurlMultiWrapper(ptr)
  private val _runningCounter = stackalloc[Int]()

  // private val _started = new AtomicBoolean(false)
  private val _alive = new AtomicBoolean(false)
  private val _terminated = new AtomicBoolean(false)
  private val _terminable = new AtomicBoolean(false)
  private val _shutdown = new AtomicBoolean(false)

  private val _connections = HashMap.empty[Ptr[Curl], HttpConnection[?]]

  private[http] lazy val _sslContext =
    builder._sslContext.orElse(SSLContext.getDefault())
  private[http] lazy val _sslParams =
    builder._sslParams.orElse(_sslContext.getDefaultSSLParameters())
  private[http] lazy val _executor: Executor =
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
          case e: ConnectException => throw e
          case _                   => throw exc
        }
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

    def _task() = {
      val conn = HttpConnection(request, responseBodyHandler, this)
      _connections.put(conn.ptr, conn): Unit

      try {
        perform()
        conn.waitUntilDoneReceived()
        val response = conn.buildResponse()
        response
      } catch {
        case exc: CurlErrCodeException =>
          if exc.code.value.toInt == 5 || exc.code.value.toInt == 6 || exc.code.value.toInt == 7
          then throw new ConnectException(s"HTTP request failed: ${exc.getMessage()}")
          else throw exc
      } finally unregisterConnection(conn)
    }

    CompletableFuture.supplyAsync(() => _task(), _executor)
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

    val ret = libcurl.multiCleanup(ptr)
    if ret == CurlMultiCode.OK
    then _terminated.compareAndExchange(false, true)
    else throw new RuntimeException(s"Failed to cleanup CURLM pointer: error code ${ret}")
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
        awaitTermination(Duration.ofSeconds(3L)): Unit
      catch {
        case e: InterruptedException =>
          interrupted.compareAndSet(false, true): Unit
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
   * Non-JDK public methods
   */

  private[http] def registerConnection(conn: HttpConnection[?]): Unit =
    val ret = _multi.addCurlEasy(conn.ptr)
    if (ret != CurlMultiCode.OK)
      conn.close()
      throw new RuntimeException(s"CURLM add easy failed: error code ${ret} (${ret.getname})")
    // register connection only after added to multi handle successfully
    _connections.put(conn.ptr, conn): Unit

  private[http] def running: Boolean =
    !_runningCounter != 0

  private[http] def performAndPoll(timeoutMs: Int): Unit =
    perform()
    poll(timeoutMs)
    collectInfo()

  /**
   * Private helpers
   */

  private def requireNonShutdown(): Unit =
    if (_shutdown.get())
      throw new IllegalStateException(
        "HttpClient has been shutdown, no new request will be accepted.",
      )

  private def perform(): Unit =
    // /* set started flag for http client */
    // _started.compareAndExchange(false, true): Unit
    val ret = _multi.perform(_runningCounter)
    println(s"DEBUG: CURL multi perform result: ${ret.getname}")
    if (ret != CurlMultiCode.OK)
      throw new IOException(s"CURLM perform failed: error code ${ret} (${ret.getname})")

  private def poll(timeoutMs: Int): Unit =
    val ret = _multi.poll(null, 0.toUInt, timeoutMs, null)
    println(s"DEBUG: CURL multi poll result: ${ret.getname}")
    if (ret != CurlMultiCode.OK)
      throw new IOException(s"CURLM poll failed: error code ${ret} (${ret.getname})")

  private def collectInfo(): Unit =
    var msg: Ptr[CurlMsg] = null
    while
      val msgCount = stackalloc[Int]()
      msg = _multi.infoRead(msgCount)
      msg != null
    do
      val ptr = (!msg).easyHandle
      val conn = _connections.getOrElse(
        ptr,
        throw new IllegalStateException(
          s"Failed to find HttpConnection for CURL easy handle pointer: ${ptr}",
        ),
      )
      conn.assignCurlMsg(msg)

  private def unregisterConnection(conn: HttpConnection[?]): Unit =
    conn.close()
    _connections.remove(conn.ptr): Unit

end HttpClientImpl
