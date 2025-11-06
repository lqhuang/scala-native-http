package java.net

import java.io.IOException

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/ContentHandler.html
abstract class ContentHandler:
  def getContent(urlc: URLConnection): AnyRef

  def getContent(urlc: URLConnection, classes: Array[Class[?]]): AnyRef
