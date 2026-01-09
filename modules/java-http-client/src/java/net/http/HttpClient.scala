package java.net.http

import java.lang._Enum

import java.io.{IOException, UncheckedIOException}
import java.net.{InetAddress, InetSocketAddress}
import java.net.{CookieHandler, Authenticator}
import java.net.{Proxy, ProxySelector}
import java.time.Duration
import java.util.Optional
import java.util.concurrent.{CompletableFuture, Executor}
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.{SSLContext, SSLParameters}

import snhttp.jdk.net.http.HttpClientBuilderImpl

/**
 * An HTTP Client.
 *
 * References:
 *
 *   - https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/java/net/http/HttpClient.html
 *
 * @since 11
 */
abstract class HttpClient extends AutoCloseable:

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

  def close(): Unit

object HttpClient:

  sealed class Version private (name: String, ordinal: Int) extends _Enum[Version](name, ordinal)
  object Version:
    final val HTTP_1_1 = new Version("HTTP_1_1", 0)
    final val HTTP_2 = new Version("HTTP_2", 1)
    final val HTTP_3 = new Version("HTTP_3", 2)

    def values(): Array[Version] = Array(HTTP_1_1, HTTP_2)

    def valueOf(name: String): Version =
      name match
        case "HTTP_1_1" => HTTP_1_1
        case "HTTP_2"   => HTTP_2
        case "HTTP_3"   => HTTP_3
        case _ => throw new IllegalArgumentException(s"No enum constant HttpClient.Version.$name")
  end Version

  sealed class Redirect private (name: String, ordinal: Int) extends _Enum[Redirect](name, ordinal)
  object Redirect:
    final val NEVER = new Redirect("NEVER", 0)
    final val ALWAYS = new Redirect("ALWAYS", 1)
    final val NORMAL = new Redirect("NORMAL", 2)

    def values(): Array[Redirect] = Array(NEVER, ALWAYS, NORMAL)

    def valueOf(name: String): Redirect =
      name match
        case "NEVER"  => NEVER
        case "ALWAYS" => ALWAYS
        case "NORMAL" => NORMAL
        case _ => throw new IllegalArgumentException(s"No enum constant HttpClient.Redirect.$name")
  end Redirect

  /// @since 11
  trait Builder:

    def cookieHandler(cookieHandler: CookieHandler): Builder

    def connectTimeout(duration: Duration): Builder

    def sslContext(sslContext: SSLContext): Builder

    def sslParameters(sslParameters: SSLParameters): Builder

    def executor(executor: Executor): Builder

    def followRedirects(policy: Redirect): Builder

    def version(version: HttpClient.Version): Builder

    def priority(priority: Int): Builder

    def proxy(proxySelector: ProxySelector): Builder

    def authenticator(authenticator: Authenticator): Builder

    def localAddress(localAddr: InetAddress): Builder

    def build(): HttpClient

  object Builder:

    final val NO_PROXY: ProxySelector = ProxySelector.of(null)

  end Builder

  def newHttpClient(): HttpClient =
    newBuilder().build()

  def newBuilder(): Builder =
    new HttpClientBuilderImpl()

end HttpClient
