package snhttp.jdk.net.http

import java.net.http.{HttpClient, HttpHeaders, HttpResponse}
import java.net.http.HttpResponse.ResponseInfo

case class ResponseInfoImpl(
    _statusCode: Int,
    _headers: HttpHeaders,
    _version: HttpClient.Version,
) extends ResponseInfo:

  def statusCode(): Int = _statusCode

  def headers(): HttpHeaders = _headers

  def version() = _version

object ResponseInfoImpl:

  def from(response: HttpResponse[?]): ResponseInfo =
    ResponseInfoImpl(response.statusCode(), response.headers(), response.version())

end ResponseInfoImpl
