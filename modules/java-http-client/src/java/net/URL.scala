package java.net

import java.io.IOException
import java.io.{InputStream, ObjectInputStream, ObjectOutputStream, Serializable}
import java.util.Objects.requireNonNull

import scala.collection.immutable.HashSet

import snhttp.jdk.net.URLStreamHandlerFactoryImpl

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URL.html
final class URL private (
    uri: URI,
    handler: Option[URLStreamHandler],
) extends Serializable {

  @deprecated(
    "Use URI.toURL() to construct an instance of URL.",
    since = "20",
  )
  def this(protocol: String, host: String, port: Int, path: String) =
    this(URI.create(s"${protocol}://${host}:${port}/${path}"), None)

  @deprecated(
    "Use URI.toURL() to construct an instance of URL.",
    since = "20",
  )
  def this(protocol: String, host: String, path: String) =
    this(URI.create(s"${protocol}://${host}/${path}"), None)

  @deprecated(
    "Use URL.of() to construct an instance of URL with URLStreamHandler.",
    since = "20",
  )
  def this(protocol: String, host: String, port: Int, path: String, handler: URLStreamHandler) =
    this(
      URI.create(s"$protocol://$host:$port/$path"),
      if handler != null then Some(handler) else None,
    )

  @deprecated(
    "Use URI.toURL() to construct an instance of URL.",
    since = "20",
  )
  def this(spec: String) = this(URI.create(spec), None)

  @deprecated(
    "Use URI.toURL() to construct an instance of URL.",
    since = "20",
  )
  def this(context: URL, spec: String) = this(URI.create(spec), None)

  // From docs:
  //
  // Creates a URL by parsing the given spec with the specified handler within
  // a specified context. If the handler is null, the parsing occurs as with the
  // two argument constructor.
  @deprecated(
    "Use URL.of() to construct an instance of URL with URLStreamHandler.",
    since = "20",
  )
  def this(context: URL, spec: String, handler: URLStreamHandler) =
    this(URI.create(spec), if handler != null then Some(handler) else None)

  def getQuery(): String = uri.getQuery()

  def getPath(): String = uri.getPath()

  def getUserInfo(): String = uri.getUserInfo()

  def getAuthority(): String = uri.getAuthority()

  def getPort(): Int = uri.getPort()

  def getDefaultPort(): Int = ???

  def getProtocol(): String = uri.getScheme()

  def getHost(): String = uri.getHost()

  def getFile(): String = ???

  def getRef(): String = ???

  def sameFile(other: URL): Boolean = ???

  def toExternalForm(): String = ???

  def toURI(): URI = uri

  def openConnection(): URLConnection = handler match {
    case Some(h) => h.openConnection(this)
    case None =>
      val protocol = getProtocol()
      val handlerInstance = URL.handlerFactory.createURLStreamHandler(protocol)

      if handlerInstance != null
      then handlerInstance.openConnection(this)
      else throw new IOException(s"No handler found for protocol: $protocol")
  }

  def openConnection(proxy: Proxy): URLConnection = handler match {
    case Some(h) => h.openConnection(this, proxy)
    case None =>
      val protocol = getProtocol()
      val handlerInstance = URL.handlerFactory.createURLStreamHandler(protocol)

      if handlerInstance != null
      then handlerInstance.openConnection(this, proxy)
      else throw new IOException(s"No handler found for protocol: $protocol")
  }

  def openStream(): InputStream = ???

  def getContent(): AnyRef = ???

  def getContent(classes: Array[Class[_]]): AnyRef = ???

  override def equals(obj: Any): Boolean = ???

  override def hashCode(): Int = ???

  override def toString(): String = ???
}

object URL {

  def of(uri: URI, handler: URLStreamHandler): URL = {
    requireNonNull(uri, "URI cannot be null")
    val scheme = uri.getScheme()
    require(scheme != null && scheme.nonEmpty)
    require(handler != null)

    try
      new URL(uri, Some(handler))
    catch {
      case e: IOException => throw new MalformedURLException(e.getMessage())
    }
  }

  val handlerFactory: URLStreamHandlerFactory = URLStreamHandlerFactoryImpl

  def setURLStreamHandlerFactory(factory: URLStreamHandlerFactory): Unit =
    throw new Error("factory already defined")

}
