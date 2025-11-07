package java.net.http

import java.io.{IOException, UncheckedIOException}
import java.net.{InetAddress, InetSocketAddress}
import java.net.{CookieHandler, Authenticator}
import java.net.{Proxy, ProxySelector}
import java.time.Duration
import java.util.Optional
import java.util.concurrent.{CompletableFuture, Executor}
import javax.net.ssl.{SSLContext, SSLParameters}

import snhttp.jdk.net.http.HttpClientBuilderImpl

/// @since 11
abstract class HttpClient extends AutoCloseable {
  import HttpClient.{Redirect, Version}

  def cookieHandler(): Optional[CookieHandler]

  def connectTimeout(): Optional[Duration]

  def followRedirects(): Redirect

  def proxy(): Optional[ProxySelector]

  def sslContext(): SSLContext

  def sslParameters(): SSLParameters

  def authenticator(): Optional[Authenticator]

  def version(): Version

  def executor(): Optional[Executor]

  def send[T](
      request: HttpRequest,
      responseBodyHandler: HttpResponse.BodyHandler[T],
  ): HttpResponse[T]

  def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: HttpResponse.BodyHandler[T],
  ): CompletableFuture[HttpResponse[T]]

  def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: HttpResponse.BodyHandler[T],
      pushPromiseHandler: HttpResponse.PushPromiseHandler[T],
  ): CompletableFuture[HttpResponse[T]]

  def newWebSocketBuilder(): WebSocket.Builder

  def shutdown(): Unit

  def awaitTermination(duration: Duration): Boolean

  def isTerminated(): Boolean

  def shutdownNow(): Unit

  override def close(): Unit = {
    @volatile var interrupted = false
    while !isTerminated() do {
      shutdown()
      try
        awaitTermination(Duration.ofSeconds(10L))
      catch {
        case e: InterruptedException =>
          if (!interrupted) {
            interrupted = true
            shutdownNow()
          }
      }
    }
    if (interrupted) Thread.currentThread().interrupt()
  }
}

object HttpClient {
  enum Version extends Enum[Version] {
    case HTTP_1_1, HTTP_2
  }

  enum Redirect extends Enum[Redirect] {
    case NEVER, ALWAYS, NORMAL
  }

  /// @since 11
  abstract class Builder {
    // def cookieHandler(cookieHandler: CookieHandler): Builder

    def connectTimeout(duration: Duration): Builder

    def sslContext(sslContext: SSLContext): Builder

    def sslParameters(sslParameters: SSLParameters): Builder

    def executor(executor: Executor): Builder

    def followRedirects(policy: Redirect): Builder

    def version(version: HttpClient.Version): Builder

    def priority(priority: Int): Builder

    def proxy(proxySelector: ProxySelector): Builder

    // def authenticator(authenticator: Authenticator): Builder

    def localAddress(localAddr: InetAddress): Builder

    def build(): HttpClient
  }
  object Builder {
    final val NO_PROXY: ProxySelector = ProxySelector.of(null)
  }

  def newHttpClient(): HttpClient = newBuilder().build()

  def newBuilder(): Builder = new HttpClientBuilderImpl()
}
