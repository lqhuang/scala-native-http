package javax.net.ssl

import java.io.IOException

class SSLProtocolException(msg: String, cause: Throwable) extends IOException(msg, cause):
  def this(reason: String) = this(reason, null)
