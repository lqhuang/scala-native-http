package java.net.http

import java.io.{IOException, UncheckedIOException}
import java.net.{Authenticator, CookieHandler, InetAddress, InetSocketAddress, Proxy, ProxySelector}
import java.util.{Objects, Optional}
import java.util.concurrent.Executor
import java.util.concurrent.CompletableFuture

import javax.net.ssl.{SSLContext, SSLParameters}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

/**
 * An HTTP Client.
 *
 * An `HttpClient` can be used to send [[HttpRequest requests]] and retrieve their
 * [[HttpResponse responses]]. An `HttpClient` is created through a [[HttpClient.Builder]]. The
 * [[HttpClient.newBuilder]] method returns a builder that creates instances of the default
 * `HttpClient` implementation.
 *
 * The builder can be used to configure per-client state, like:
 *   - the preferred protocol version (HTTP/1.1 or HTTP/2)
 *   - whether to follow redirects
 *   - a proxy
 *   - an authenticator, etc.
 *
 * Once built, an `HttpClient` is immutable, and can be used to send multiple requests.
 *
 * An `HttpClient` provides configuration information, and resource sharing, for all requests sent
 * through it. An `HttpClient` instance typically manages its own pools of connections, which it may
 * then reuse as and when necessary.
 *
 * Connection pools are typically not shared between `HttpClient` instances. Creating a new client
 * for each operation, though possible, will usually prevent reusing such connections.
 *
 * A [[BodyHandler]] must be supplied for each [[HttpRequest]] sent. The `BodyHandler` determines
 * how to handle the response body, if any. Once an [[HttpResponse]] is received, the headers,
 * response code, and body (typically) are available.
 *
 * @since 11
 */
trait HttpClient extends AutoCloseable {

  /** HTTP protocol version. */
  object Version extends Enumeration {
    type Version = Value

    /** HTTP version 1.1 */
    val HTTP_1_1,
    /** HTTP version 2 */
    HTTP_2 = Value
  }

  /** Defines the automatic redirection policy. */
  object Redirect extends Enumeration {
    type Version = Value

    /** Never redirect */
    val NEVER
    /** Always redirect */
    , ALWAYS
    /** Always redirect, except from HTTPS URLs to HTTP URLs */
    , NORMAL = Value
  }

  /**
   * A builder of [[HttpClient]].
   *
   * Builders are created by invoking [[HttpClient.newBuilder]]. Each of the setter methods modifies
   * the state of the builder and returns the same instance.
   */
  trait Builder {

    /** A proxy selector that always returns [[Proxy.NO_PROXY]] implying a direct connection. */
    final val NO_PROXY: ProxySelector = ProxySelector.of(null)

    /** Sets a cookie handler. */
    def cookieHandler(cookieHandler: CookieHandler): Builder

    /** Sets the connect timeout duration for this client. */
    def connectTimeout(duration: Duration): Builder

    /** Set an [[SSLContext]]. */
    def sslContext(sslContext: SSLContext): Builder

    /** Sets an [[SSLParameters]]. */
    def sslParameters(sslParameters: SSLParameters): Builder

    /** Sets the executor to be used for asynchronous and dependent tasks. */
    def executor(executor: Executor): Builder

    /** Specifies whether requests will automatically follow redirects issued by the server. */
    def followRedirects(policy: Redirect): Builder

    /** Requests a specific HTTP protocol version where possible. */
    def version(version: HttpClient.Version): Builder

    /**
     * Sets the default priority for any HTTP/2 requests sent from this client. The value provided
     * must be between `1` and `256` (inclusive).
     */
    def priority(priority: Int): Builder

    /** Sets a [[java.net.ProxySelector]]. */
    def proxy(proxySelector: ProxySelector): Builder

    /** Sets an authenticator to use for HTTP authentication. */
    def authenticator(authenticator: Authenticator): Builder

    /** Binds the socket to this local address when creating connections for sending requests. */
    def localAddress(localAddr: InetAddress): Builder = throw UnsupportedOperationException()

    /** a new [[HttpClient]] built from the current state of this builder. */
    def build(): HttpClient
  }

  def cookieHandler(): Optional[CookieHandler]
  def connectTimeout(): Optional[Duration]
  def followRedirects(): Redirect
  def proxy(): Optional[ProxySelector]
  def sslContext(): SSLContext
  def sslParameters(): SSLParameters
  def authenticator(): Optional[Authenticator]
  def version(): HttpClient.Version
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

  def newWebSocketBuilder(): WebSocket.Builder =
    throw UnsupportedOperationException()

  def shutdown(): Unit = ()

  def awaitTermination(duration: Duration): Boolean = {
    Objects.requireNonNull(duration)
    true
  }

  def isTerminated(): Boolean = false

  def shutdownNow(): Unit = shutdown()

  override def close(): Unit = {
    var terminated = isTerminated()
    if (!terminated) {
      shutdown()
      var interrupted = false
      while (!terminated)
        try
          terminated = awaitTermination(Duration.ofDays(1L))
        catch {
          case e: InterruptedException =>
            if (!interrupted) {
              interrupted = true
              shutdownNow()
              if (isTerminated()) {
                terminated = true
              }
            }
        }
      if (interrupted) {
        Thread.currentThread().interrupt()
      }
    }
  }
}

/** Companion object for [[HttpClient]] providing factory methods */
object HttpClient {
  type Version = HttpClient.Version
  type Redirect = HttpClient.Redirect
  type Builder = HttpClient.Builder

  /** Creates an HttpClient with default settings. */
  def newHttpClient(): HttpClient = newBuilder().build()

  /** Creates a new [[HttpClient.Builder]]. */
  def newBuilder(): Builder = new HttpClientBuilderImpl()
}
