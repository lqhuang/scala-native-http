package java.net.http

import java.io.IOException

/// @since 11
class HttpTimeoutException(s: String, e: Throwable) extends IOException(s, e) {
  def this(s: String) = this(s, null)
}

object HttpTimeoutException {
  final private val serialVersionUID: Long = 981344271622632951L
}

/// @since 11
class HttpConnectTimeoutException(s: String) extends HttpTimeoutException(s)

object HttpConnectTimeoutException {
  final private val serialVersionUID: Long = 321L + 11L
}

/// @since 11
class WebSocketHandshakeException(
    @transient
    final private val response: HttpResponse[?],
) extends IOException {
  def getResponse(): HttpResponse[?] = response

  override def initCause(cause: Throwable): Throwable = super.initCause(cause)
}

object WebSocketHandshakeException {
  final private val serialVersionUID: Long = 1L
}
