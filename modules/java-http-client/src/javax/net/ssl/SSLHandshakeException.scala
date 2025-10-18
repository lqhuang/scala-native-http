package javax.net.ssl

class SSLHandshakeException(message: String, cause: Throwable)
    extends SSLException(message, cause) {
  def this(reason: String) = this(reason, null)
}
