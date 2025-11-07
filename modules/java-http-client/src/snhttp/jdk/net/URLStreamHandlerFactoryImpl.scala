package snhttp.jdk.net

import java.net.{URLStreamHandler, URLStreamHandlerFactory}
import java.util.Objects.requireNonNull

import scala.collection.immutable.HashMap

object URLStreamHandlerFactoryImpl extends URLStreamHandlerFactory:

  // Protocol handlers for the following protocols are guaranteed to exist on the search path
  private val ProtocolHandlerMap: HashMap[String, URLStreamHandler] =
    HashMap(
      "http" -> {
        lazy val handler = HttpURLStreamHandler()
        handler
      },
      "https" -> {
        lazy val handler = HttpsURLStreamHandler()
        handler
      },
      "file" -> {
        lazy val handler = FileURLStreamHandler()
        handler
      },
      "jar" -> {
        throw new NotImplementedError("'URLStreamHandler' for jar protocol is not supported yet")
      },
    )

  // a `URLStreamHandler` for the specific protocol,
  // or `null` if this factory cannot create a handler for the specific protocol
  def createURLStreamHandler(protocol: String): URLStreamHandler =
    requireNonNull(protocol)
    ProtocolHandlerMap.get(protocol.toLowerCase()) match
      case Some(handler) => handler
      case None          => null
