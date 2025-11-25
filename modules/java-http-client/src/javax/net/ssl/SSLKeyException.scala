package javax.net.ssl

import java.io.IOException

class SSLKeyException(msg: String, cause: Throwable) extends SSLException(msg, cause):
  def this(reason: String) = this(reason, null)
