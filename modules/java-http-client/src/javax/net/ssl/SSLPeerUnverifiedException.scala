package javax.net.ssl

// ref: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/net/ssl/SSLPeerUnverifiedException.html
class SSLPeerUnverifiedException(msg: String, cuase: Throwable) extends SSLException(msg, cuase) {
  def this(reason: String) = this(reason, null)
}
