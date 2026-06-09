package snhttp.jdk.net.http

import java.net.URI
import java.net.http.{HttpRequest, HttpResponse, HttpHeaders, HttpClient}
import java.io.OutputStream
import java.util.Optional
import java.util.concurrent.CompletionStage
import javax.net.ssl.SSLSession

class HttpResponseImpl[T] private[http] (
    _request: HttpRequest,
    _responseInfo: ResponseInfoImpl,
    _body: CompletionStage[T],
    _sslSession: Optional[SSLSession] = Optional.empty(),
    _connectionLabel: Optional[String] = Optional.empty(),
    // _previousResponse: Optional[HttpResponse[T]] = Optional.empty(),
) extends HttpResponse[T]:

  def statusCode(): Int = _responseInfo.statusCode()

  /// @since 25
  override def connectionLabel(): Optional[String] = _connectionLabel

  def request(): HttpRequest = _request

  def previousResponse(): Optional[HttpResponse[T]] = ??? // _previousResponse

  def headers(): HttpHeaders = _responseInfo.headers()

  def body(): T = _body.toCompletableFuture().join()

  def sslSession(): Optional[SSLSession] = _sslSession

  def uri(): URI = _request.uri()

  def version(): HttpClient.Version = _responseInfo.version()
