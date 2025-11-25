package javax.net.ssl

class SSLPeerUnverifiedException(msg: String, cuase: Throwable) extends SSLException(msg, cuase):
  def this(reason: String) = this(reason, null)
