package javax.net.ssl

import java.net.Socket
import java.io.InputStream
import java.util.Locale
import javax.net.SocketFactory

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLSocketFactory.html
abstract class SSLSocketFactory extends SocketFactory:

  def getDefaultCipherSuites(): Array[String]

  def getSupportedCipherSuites(): Array[String]

  def createSocket(
      socket: Socket,
      host: String,
      port: Int,
      autoClose: Boolean,
  ): Socket

  def createSocket(
      socket: Socket,
      consumed: InputStream,
      autoClose: Boolean,
  ): Socket

object SSLSocketFactory:

  private val defaultSocketFactory: SocketFactory = ???

  def getDefault(): SocketFactory = defaultSocketFactory

  def getSecurityProperty(name: String): String = ???

end SSLSocketFactory
