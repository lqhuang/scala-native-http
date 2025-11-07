package snhttp.jdk.net.http

import java.io.{IOException, UncheckedIOException}
import java.net.{
  Authenticator,
  CookieHandler,
  InetAddress,
  InetSocketAddress,
  Proxy,
  ProxySelector,
  URI,
}
import java.net.http.{HttpClient, HttpRequest, HttpResponse, HttpHeaders, WebSocket}
import java.net.http.HttpClient.{Builder, Redirect, Version}
import java.net.http.HttpResponse.{BodyHandler, PushPromiseHandler}
import java.time.Duration
import java.util.{List, Optional}
import java.util.Objects.requireNonNull
import java.util.concurrent.{CompletableFuture, Executor}
import java.util.concurrent.ExecutionException
import javax.net.ssl.{SSLContext, SSLParameters}

import scala.concurrent.ExecutionContext

class HttpClientBuilderImpl extends Builder {

  private var _cookieHandler: Option[CookieHandler] = None
  private var _timeout: Option[Duration] = None
  private var _redirect: Redirect = Redirect.NORMAL
  private var _proxy: Option[ProxySelector] = None
  private var _authenticator: Option[Authenticator] = None
  private var _version: Version = Version.HTTP_1_1
  private var _executor: Option[Executor] = None
  private var _sslContext: Option[SSLContext] = None
  private var _sslParams: Option[SSLParameters] = None
  private var _priority: Int = -1
  private var _localAddr: Option[InetAddress] = None

  def cookieHandler(cookieHandler: CookieHandler): Builder =
    requireNonNull(cookieHandler)
    this._cookieHandler = Some(cookieHandler)
    this

  def connectTimeout(duration: Duration): Builder =
    requireNonNull(duration)
    require(!duration.isNegative && !duration.isZero, "duration must be positive")
    this._timeout = Some(duration)
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
    new HttpClientImpl(
      _timeout = _timeout,
      _redirect = _redirect,
      _proxy = _proxy,
      _httpVersion = _version,
      _executor = _executor,
      _priority = _priority,
      _localAddr = _localAddr,
      _sslContext = _sslContext,
      _sslParams = _sslParams,
      _authenticator = _authenticator,
      _cookieHandler = _cookieHandler,
    )

}

class HttpClientImpl(
    _timeout: Option[Duration] = None,
    _redirect: Redirect = Redirect.NORMAL,
    _proxy: Option[ProxySelector] = None,
    _httpVersion: Version = Version.HTTP_1_1,
    _executor: Option[Executor] = None,
    _priority: Int = -1,
    _localAddr: Option[InetAddress] = None,
    _sslContext: Option[SSLContext] = None,
    _sslParams: Option[SSLParameters] = None,
    _authenticator: Option[Authenticator] = None,
    _cookieHandler: Option[CookieHandler] = None,
) extends HttpClient {

  @volatile private var _terminated = false
  @volatile private var _shutdown = false

  private val defaultExecutor: Executor =
    _executor.getOrElse(ExecutionContext.global)

  def cookieHandler(): Optional[CookieHandler] =
    _cookieHandler.map(Optional.of).getOrElse(Optional.empty())

  def connectTimeout(): Optional[Duration] =
    _timeout.map(Optional.of).getOrElse(Optional.empty())

  def followRedirects(): Redirect =
    _redirect

  def proxy(): Optional[ProxySelector] =
    _proxy.map(Optional.of).getOrElse(Optional.empty())

  def sslContext(): SSLContext =
    _sslContext.getOrElse(SSLContext.getDefault())

  def sslParameters(): SSLParameters =
    sslContext().getDefaultSSLParameters()

  def authenticator(): Optional[Authenticator] =
    _authenticator.map(Optional.of).getOrElse(Optional.empty())

  def version(): Version =
    _httpVersion

  def executor(): Optional[Executor] =
    _executor.map(Optional.of).getOrElse(Optional.empty())

  def send[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
  ): HttpResponse[T] = {
    requireNonNull(request, "request cannot be null")
    requireNonNull(responseBodyHandler, "responseBodyHandler cannot be null")

    if (_shutdown) {
      throw new IllegalStateException("HttpClient has been shut down")
    }

    try
      sendAsync(request, responseBodyHandler).get()
    catch {
      case e: ExecutionException =>
        val cause = e.getCause
        cause match {
          case io: IOException           => throw io
          case runtime: RuntimeException => throw runtime
          case _ => throw new IOException("Unexpected error during HTTP request", cause)
        }
      case e: InterruptedException =>
        Thread.currentThread().interrupt()
        throw new IOException("HTTP request was interrupted", e)
    }
  }

  def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
  ): CompletableFuture[HttpResponse[T]] = {
    requireNonNull(request, "request cannot be null")
    requireNonNull(responseBodyHandler, "responseBodyHandler cannot be null")

    if (_shutdown) {
      val future = new CompletableFuture[HttpResponse[T]]()
      future.completeExceptionally(new IllegalStateException("HttpClient has been shut down"))
      return future
    }

    // Create a CompletableFuture that will complete with the response
    val responseFuture = new CompletableFuture[HttpResponse[T]]()

    // Submit the request processing to the executor
    defaultExecutor.execute(() =>
      try
        // This is a simplified implementation
        // In a real implementation, this would:
        // 1. Resolve the URI and establish connection
        // 2. Send the HTTP request
        // 3. Receive and parse the HTTP response
        // 4. Handle redirects according to policy
        // 5. Process the response body with the provided handler

        // For now, create a mock response to satisfy the interface
        // val mockResponse = createMockResponse(request, responseBodyHandler)
        // responseFuture.complete(mockResponse)
        responseFuture.complete(???)
      catch {
        case e: Exception =>
          responseFuture.completeExceptionally(e)
      },
    )

    responseFuture
  }

  def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
      pushPromiseHandler: PushPromiseHandler[T],
  ): CompletableFuture[HttpResponse[T]] = {
    requireNonNull(pushPromiseHandler, "pushPromiseHandler cannot be null")

    // For HTTP/1.1, push promises are not supported, so just delegate to the two-parameter version
    if (_httpVersion == Version.HTTP_1_1) {
      sendAsync(request, responseBodyHandler)
    } else {
      // For HTTP/2, in a real implementation this would handle server push
      // For now, just delegate to the two-parameter version
      sendAsync(request, responseBodyHandler)
    }
  }

  override def shutdown(): Unit =
    _shutdown = true
    // In a real implementation, this would:
    // 1. Stop accepting new requests
    // 2. Allow existing requests to complete
    // 3. Close idle connections

  override def awaitTermination(duration: Duration): Boolean = {
    requireNonNull(duration, "duration cannot be null")

    val start = System.nanoTime()
    val timeout = duration.toNanos

    while (!_terminated && (System.nanoTime() - start) < timeout)
      try Thread.sleep(1) // Small sleep to avoid busy waiting
      catch
        case _: InterruptedException =>
          Thread.currentThread().interrupt()
          return false

    _terminated
  }

  override def isTerminated(): Boolean =
    _terminated

  override def shutdownNow(): Unit =
    // In a real implementation, this would:
    // 1. Cancel all pending requests
    // 2. Close all connections immediately
    // 3. Shutdown executor services
    _shutdown = true
    _terminated = true

  override def newWebSocketBuilder(): WebSocket.Builder = {
    if (_shutdown) {
      throw new IllegalStateException("HttpClient has been shut down")
    }

    ???
  }

}
