package javax.net.ssl

import java.io.IOException

class SSLException(message: String, cause: Throwable) extends IOException(message, cause):
  def this(reason: String) = this(reason, null)
  def this(cause: Throwable) = this(null, cause)

class SSLHandshakeException(message: String, cause: Throwable) extends SSLException(message, cause):
  def this(reason: String) = this(reason, null)

class SSLKeyException(msg: String, cause: Throwable) extends SSLException(msg, cause):
  def this(reason: String) = this(reason, null)

class SSLPeerUnverifiedException(msg: String, cuase: Throwable) extends SSLException(msg, cuase):
  def this(reason: String) = this(reason, null)

class SSLProtocolException(msg: String, cause: Throwable) extends IOException(msg, cause):
  def this(reason: String) = this(reason, null)
