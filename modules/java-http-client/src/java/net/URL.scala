package java.net

import java.io.IOException
import java.io.{InputStream, ObjectInputStream, ObjectOutputStream, Serializable}
import java.net.InetAddress
import java.net.URISyntaxException
import java.util.Optional
import java.util.Objects.requireNonNull

import snhttp.jdk.net.URLStreamHandlerFactoryImpl

/**
 * Refs
 *
 *   - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URL.html
 */
final class URL private (
    uri: URI,
    handler: Optional[URLStreamHandler],
) extends Serializable:

  @deprecated(
    "Use URI.toURL() to construct an instance of URL.",
    since = "20",
  )
  def this(protocol: String, host: String, port: Int, path: String) =
    this(URL.creatURI(s"${protocol}://${host}:${port}/${path}"), Optional.empty())

  @deprecated(
    "Use URI.toURL() to construct an instance of URL.",
    since = "20",
  )
  def this(protocol: String, host: String, path: String) =
    this(URL.creatURI(s"${protocol}://${host}/${path}"), Optional.empty())

  @deprecated(
    "Use URL.of() to construct an instance of URL with URLStreamHandler.",
    since = "20",
  )
  def this(protocol: String, host: String, port: Int, path: String, handler: URLStreamHandler) =
    this(URL.creatURI(s"$protocol://$host:$port/$path"), Optional.ofNullable(handler))

  @deprecated(
    "Use URI.toURL() to construct an instance of URL.",
    since = "20",
  )
  def this(spec: String) =
    this(URL.creatURI(spec), Optional.empty())

  @deprecated(
    "Use URI.toURL() to construct an instance of URL.",
    since = "20",
  )
  def this(context: URL, spec: String) =
    this(URI.create(context.toURI().resolve(spec).toString()), Optional.empty())

  @deprecated(
    "Use URL.of() to construct an instance of URL with URLStreamHandler.",
    since = "20",
  )
  def this(context: URL, spec: String, handler: URLStreamHandler) =
    this(URL.creatURI(spec), Optional.ofNullable(handler))

  def getQuery(): String =
    uri.getQuery()

  def getPath(): String =
    uri.getPath()

  def getUserInfo(): String =
    uri.getUserInfo()

  def getAuthority(): String =
    uri.getAuthority()

  def getPort(): Int =
    uri.getPort()

  /// Gets the default port number of the protocol associated with this URL.
  /// If the URL scheme or the URLStreamHandler for the URL do not define
  /// a default port number, then -1 is returned.
  def getDefaultPort(): Int =
    if getPort() != -1
    then //
      getPort()
    else
      handler
        .map(_.getDefaultPort())
        .orElseGet(() =>
          if (getProtocol() == "http")
            80
          else if (getProtocol() == "https")
            443
          else //
            throw new NotImplementedError(
              s"Default port for protocol ${getProtocol()} is not implemented",
            ),
        )

  def getProtocol(): String =
    uri.getScheme()

  def getHost(): String =
    uri.getHost()

  def getFile(): String =
    uri.getQuery() match
      case null => uri.getPath()
      case q    => s"${uri.getPath()}?${q}"

  def getRef(): String =
    uri.getFragment()

  def sameFile(other: URL): Boolean =
    this.getProtocol() == other.getProtocol()
      && this.getUserInfo() == other.getUserInfo()
      && this.getHost() == other.getHost()
      && this.getPort() == other.getPort()
      && this.getFile() == other.getFile()
      && this.getQuery() == other.getQuery()

  def toExternalForm(): String =
    handler
      .orElseThrow(() => new NotImplementedError())
      .toExternalForm(this)

  def toURI(): URI =
    uri

  def openConnection(): URLConnection =
    throw new NotImplementedError()
    // handler match {
    //   case Some(h) => h.openConnection(this)
    //   case None =>
    //     val protocol = getProtocol()
    //     val handlerInstance = URL.handlerFactory.createURLStreamHandler(protocol)

    //     if handlerInstance != null
    //     then handlerInstance.openConnection(this)
    //     else throw new IOException(s"No handler found for protocol: $protocol")
    // }

  def openConnection(proxy: Proxy): URLConnection =
    throw new NotImplementedError()
    // handler match {
    //   case Some(h) => h.openConnection(this, proxy)
    //   case None =>
    //     val protocol = getProtocol()
    //     val handlerInstance = URL.handlerFactory.createURLStreamHandler(protocol)

    //     if handlerInstance != null
    //     then handlerInstance.openConnection(this, proxy)
    //     else throw new IOException(s"No handler found for protocol: $protocol")
    // }

  def openStream(): InputStream =
    openConnection().getInputStream()

  def getContent(): Any =
    openConnection().getContent()

  def getContent(classes: Array[Class[_]]): Any =
    throw new UnsupportedOperationException()

  /// Based on docs
  ///
  /// Two URL objects are equal if they have
  ///
  /// 1. the same protocol,
  /// 2. reference equivalent hosts,
  /// 3. have the same port number on the host,
  /// 4. and the same file
  /// 5. and fragment of the file.
  ///
  /// Two hosts are considered equivalent if both host names can be resolved into
  /// the same IP addresses; else if either host name can't be resolved, the host
  /// names must be equal without regard to case; or both host names equal to null.
  ///
  /// Since hosts comparison requires name resolution, this operation is a blocking operation.
  ///
  /// Note: The defined behavior for equals is known to be inconsistent with virtual hosting
  /// in HTTP.
  override def equals(obj: Any): Boolean =
    if !obj.isInstanceOf[URL]
    then //
      false
    else {
      val that = obj.asInstanceOf[URL]

      this.getProtocol() == that.getProtocol()
      && this.getHost() == that.getHost()
      && this.getPort() == that.getPort()
      && this.getFile() == that.getFile()
      && this.getRef() == that.getRef()
    }

  override def hashCode(): Int =
    Seq(
      this.getProtocol(),
      this.getHost(),
      this.getPort(),
      this.getFile(),
      this.getRef(),
    )
      .foldLeft[Int](0) { (acc, item) =>
        if item == null
        then acc
        else (acc << 5 | acc >>> 27) + item.hashCode()
      }

  override def toString(): String =
    uri.toString()

object URL:

  private inline def creatURI(spec: String): URI =
    try
      URI.create(spec)
    catch {
      case e: URISyntaxException =>
        throw new MalformedURLException(e.getMessage())
    }

  def of(uri: URI, handler: URLStreamHandler): URL = {
    requireNonNull(uri, "URI cannot be null")
    val scheme = uri.getScheme()
    require(scheme != null && scheme.nonEmpty)

    try
      new URL(uri, Optional.ofNullable(handler))
    catch {
      case e: IOException => throw new MalformedURLException(e.getMessage())
    }
  }

  def setURLStreamHandlerFactory(factory: URLStreamHandlerFactory): Unit =
    throw new NotImplementedError()

end URL
