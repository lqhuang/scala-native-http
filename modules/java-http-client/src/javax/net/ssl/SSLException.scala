package javax.net.ssl

import java.io.IOException

class SSLException(message: String, cause: Throwable) extends IOException(message, cause):
  def this(reason: String) = this(reason, null)
  def this(cause: Throwable) = this(null, cause)
