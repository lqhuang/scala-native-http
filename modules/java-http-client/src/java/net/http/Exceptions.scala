package java.net.http

import java.io.IOException

/// @since 11
class HttpTimeoutException(s: String, e: Throwable) extends IOException(s, e):
  def this(s: String) = this(s, null)

/// @since 11
class HttpConnectTimeoutException(s: String) extends HttpTimeoutException(s)

/// @since 11
class WebSocketHandshakeException(response: HttpResponse[?]) extends IOException:
  def getResponse(): HttpResponse[?] = response
