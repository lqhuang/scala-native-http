package javax.net.ssl

import java.io.IOException

// ref: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/net/ssl/SSLException.html
class SSLException(message: String, cause: Throwable) extends IOException(message, cause) {
  def this(reason: String) = this(reason, null)
  def this(cause: Throwable) = this(null, cause)
}
