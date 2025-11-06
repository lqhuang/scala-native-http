package java.net

import java.nio.charset.Charset

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URLEncoder.html
object URLEncoder:

  @deprecated
  def decode(s: String): String = throw new UnsupportedOperationException("Deprecated")

  def decode(s: String, enc: String): String = ???

  // Since JDK 10
  def decode(s: String, charset: Charset): String = ???
