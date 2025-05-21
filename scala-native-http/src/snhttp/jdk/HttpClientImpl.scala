package snhttp.jdk

import java.io.{IOException, UncheckedIOException}
import java.net.{Authenticator, CookieHandler, InetAddress, InetSocketAddress, Proxy, ProxySelector}
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpClient.{Redirect, Version}
import java.net.http.HttpResponse.{BodyHandler, PushPromiseHandler}
import java.time.Duration
import java.util.{List, Optional}
import java.util.Objects.requireNonNull
import java.util.concurrent.{CompletableFuture, Executor}
import javax.net.ssl.{SSLContext, SSLParameters}
import java.net.http.WebSocket.Builder

class HttpClientBuilderImpl extends HttpClient.Builder {
  private var cookieHandler: Option[CookieHandler] = None
  private var connectTimeout: Option[Duration] = None
  private var followRedirects: Redirect = Redirect.NORMAL
  private var proxy: Option[ProxySelector] = None
  private var authenticator: Option[Authenticator] = None
  private var version: Version = Version.HTTP_1_1
  private var executor: Option[Executor] = None
  private var sslContext: Option[SSLContext] = None
  private var sslParams: Option[SSLParameters] = None
  private var priority: Int = -1
  private var localAddr: Option[InetAddress] = None

  def cookieHandler(cookieHandler: CookieHandler): HttpClient.Builder = {
    requireNonNull(cookieHandler)
    this.cookieHandler = Some(cookieHandler)
    this
  }

  def connectTimeout(duration: Duration): HttpClient.Builder = {
    requireNonNull(duration)
    require(!duration.isNegative && !duration.isZero, "duration must be positive")
    this.connectTimeout = Some(duration)
    this
  }

  def sslContext(sslContext: SSLContext): HttpClient.Builder = {
    requireNonNull(sslContext)
    this.sslContext = Some(sslContext)
    this
  }

  def sslParameters(sslParams: SSLParameters): HttpClient.Builder = {
    requireNonNull(sslParams)
    this.sslParams = Some(sslParams)
    this
  }

  def executor(executor: Executor): HttpClient.Builder = {
    requireNonNull(executor)
    this.executor = Some(executor)
    this
  }

  def followRedirects(redirect: Redirect): HttpClient.Builder = {
    requireNonNull(redirect)
    this.followRedirects = redirect
    this
  }

  def version(version: Version): HttpClient.Builder = {
    requireNonNull(version)
    this.version = version
    this
  }

  def priority(priority: Int): HttpClient.Builder = {
    require(priority >= 1 && priority <= 256, "priority must be between 1 and 256 (inclusive)")
    this.priority = priority
    this
  }

  def proxy(proxy: ProxySelector): HttpClient.Builder = {
    requireNonNull(proxy)
    this.proxy = Some(proxy)
    this
  }

  def authenticator(authenticator: Authenticator): HttpClient.Builder = {
    requireNonNull(authenticator)
    this.authenticator = Some(authenticator)
    this
  }

  def localAddress(localAddr: InetAddress): HttpClient.Builder = {
    requireNonNull(localAddr)
    this.localAddr = Some(localAddr)
    this
  }

  def build(): HttpClient = return new HttpClientImpl()

}

class HttpClientImpl extends HttpClient {

  override def cookieHandler(): Optional[CookieHandler] = ???

  override def connectTimeout(): Optional[Duration] = ???

  override def followRedirects(): Redirect = ???

  override def proxy(): Optional[ProxySelector] = ???

  override def sslContext(): SSLContext = ???

  override def sslParameters(): SSLParameters = ???

  override def authenticator(): Optional[Authenticator] = ???

  override def version(): Version = ???

  override def executor(): Optional[Executor] = ???

  override def send[T](request: HttpRequest, responseBodyHandler: BodyHandler[T]): HttpResponse[T] =
    ???

  override def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
  ): CompletableFuture[HttpResponse[T]] = ???

  override def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
      pushPromiseHandler: PushPromiseHandler[T],
  ): CompletableFuture[HttpResponse[T]] = ???

  override def shutdown(): Unit = ???

  override def awaitTermination(duration: Duration): Boolean = ???

  override def isTerminated(): Boolean = ???

  override def shutdownNow(): Unit = ???

  override def newWebSocketBuilder(): Builder = ???

}
