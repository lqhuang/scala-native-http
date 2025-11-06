package java.net

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URLStreamHandlerFactory.html
trait URLStreamHandlerFactory:
  def createURLStreamHandler(protocol: String): URLStreamHandler
