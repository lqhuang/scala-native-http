package snhttp.jdk.net.http

import java.net.URI
import java.net.http.{HttpRequest, HttpResponse, HttpHeaders, HttpClient}
import java.util.Optional
import javax.net.ssl.SSLSession
import java.io.OutputStream

class HttpResponseImpl[T](
    _request: HttpRequest,
    _version: HttpClient.Version,
    _statusCode: Int,
    _headers: HttpHeaders,
    _body: T,
    _sslSession: Optional[SSLSession] = Optional.empty(),
    _connectionLabel: Optional[String] = Optional.empty(),
    // _previousResponse: Optional[HttpResponse[T]] = Optional.empty(),
) extends HttpResponse[T]:

  def statusCode(): Int = _statusCode

  /// @since 25
  override def connectionLabel(): Optional[String] = _connectionLabel

  def request(): HttpRequest = _request

  def previousResponse(): Optional[HttpResponse[T]] = ??? // _previousResponse

  def headers(): HttpHeaders = _headers

  def body(): T = _body

  def sslSession(): Optional[SSLSession] = _sslSession

  def uri(): URI = _request.uri()

  def version(): HttpClient.Version = _version

// object HttpResponseImpl:

// def apply[T](
//     request: HttpRequest,
//     version: HttpClient.Version,
//     statusCode: Int,
//     headers: HttpHeaders,
//     body: T,
//     sslSession: Optional[SSLSession] = Optional.empty(),
//     connectionLabel: Optional[String] = Optional.empty(),
//     previousResponse: Optional[HttpResponse[T]] = Optional.empty(),
// ): HttpResponseImpl[T] =
//   new HttpResponseImpl[T](
//     request,
//     version,
//     statusCode,
//     headers,
//     body,
//     sslSession,
//     connectionLabel,
//     previousResponse,
//   )

// end HttpResponseImpl
