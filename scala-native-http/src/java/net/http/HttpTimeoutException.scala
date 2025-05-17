package java.net.http

import java.io.IOException;

/// @since 11
class HttpTimeoutException(s: String, e: Throwable) extends IOException(s, e) {
  def this(s: String) = this(s, null)
}

object HttpTimeoutException {
  final private val serialVersionUID: Long = 981344271622632951L
}
