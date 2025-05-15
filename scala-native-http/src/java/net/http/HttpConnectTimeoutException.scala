package java.net.http

/** @since 11 */
class HttpConnectTimeoutException(s: String) extends HttpTimeoutException(s)

object HttpConnectTimeoutException {
  final private val serialVersionUID: Long = 321L + 11L
}
