package javax.net.ssl

// https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/net/ssl/SSLHandshakeException.html
class SSLHandshakeException(message: String, cause: Throwable)
    extends SSLException(message, cause) {
  def this(reason: String) = this(reason, null)
}
