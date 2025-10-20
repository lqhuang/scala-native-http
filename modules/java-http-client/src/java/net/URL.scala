package java.net

import java.io.{IOException, InputStream, ObjectInputStream, ObjectOutputStream, Serializable}

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URL.html
final class URL private (
    uri: URI,
    handler: URLStreamHandler,
) extends Serializable {
  def of(uri: URI, handler: URLStreamHandler): URL = new URL(uri, handler)

  def getQuery(): String = ???

  def getPath(): String = ???

  def getUserInfo(): String = ???

  def getAuthority(): String = ???

  def getPort(): Int = ???

  def getDefaultPort(): Int = ???

  def getProtocol(): String = ???

  def getHost(): String = ???

  def getFile(): String = ???

  def getRef(): String = ???

  override def equals(obj: Any): Boolean = ???

  override def hashCode(): Int = ???

  def sameFile(other: URL): Boolean = ???

  override def toString(): String = ???

  def toExternalForm(): String = ???

  def toURI(): URI = ???

  def openConnection(): URLConnection = ???

  def openConnection(proxy: Proxy): URLConnection = ???

  def openStream(): InputStream = ???

  def getContent(): Object = ???

  def getContent(classes: Array[Class[_]]): Object = ???
}

object URL {
  private var handlerFactory: URLStreamHandlerFactory = null

  def setURLStreamHandlerFactory(factory: URLStreamHandlerFactory): Unit =
    handlerFactory = factory
}
