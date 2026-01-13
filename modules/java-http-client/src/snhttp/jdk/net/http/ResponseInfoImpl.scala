package snhttp.jdk.net.http

import java.net.http.{HttpClient, HttpHeaders, HttpResponse}
import java.net.http.HttpResponse.ResponseInfo

final class ResponseInfoImpl(
    _statusCode: Int,
    _version: HttpClient.Version,
    _headers: HttpHeaders,
) extends ResponseInfo:

  def statusCode(): Int = _statusCode

  def headers(): HttpHeaders = _headers

  def version() = _version

object ResponseInfoImpl:

  def from(response: HttpResponse[?]): ResponseInfo =
    ResponseInfoImpl(response.statusCode(), response.version(), response.headers())

end ResponseInfoImpl
