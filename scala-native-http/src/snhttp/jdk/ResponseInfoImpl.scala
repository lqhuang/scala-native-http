package snhttp.jdk

import java.net.http.HttpResponse
import java.net.http.HttpResponse.ResponseInfo
import java.net.http.HttpHeaders
import java.net.http.HttpClient

case class ResponseInfoImpl(
    private val _statusCode: Int,
    private val _headers: HttpHeaders,
    private val _version: HttpClient.Version,
) extends ResponseInfo {
  def statusCode(): Int = _statusCode

  def headers(): HttpHeaders = _headers

  def version() = _version
}

object ResponseInfoImpl {
  def from(response: HttpResponse[?]): ResponseInfo =
    ResponseInfoImpl(response.statusCode(), response.headers(), response.version())
}
