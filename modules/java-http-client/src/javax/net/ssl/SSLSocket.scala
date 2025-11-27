package javax.net.ssl

import java.net.{Socket, InetAddress}
import java.util.List as JList
import java.util.function.BiFunction
import javax.net.ssl.HandshakeCompletedListener

/// ## Refs
///
/// https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLSocket.html
abstract class SSLSocket(
    // address: InetAddress,
    // port: Int,
    // clientAddress: InetAddress,
    // clientPort: Int,
    // autoClose: Boolean = true,
) extends Socket
    with AutoCloseable:

  // def this() =
  //   this(null.asInstanceOf[InetAddress], -1, null, 0)

  // def this(host: String, port: Int) =
  //   this(InetAddress.getByName(host), port, null, 0)

  // def this(address: InetAddress, port: Int) =
  //   this(address, port, null, 0)

  // def this(host: String, port: Int, localHost: InetAddress, localPort: Int) =
  //   this(InetAddress.getByName(host), port, localHost, localPort)

  def getSupportedCipherSuites(): Array[String]

  def getEnabledCipherSuites(): Array[String]

  def setEnabledCipherSuites(suites: Array[String]): Unit

  def getSupportedProtocols(): Array[String]

  def getEnabledProtocols(): Array[String]

  def setEnabledProtocols(protocols: Array[String]): Unit

  def getSession(): SSLSession

  def getHandshakeSession(): SSLSession

  def addHandshakeCompletedListener(listener: HandshakeCompletedListener): Unit

  def removeHandshakeCompletedListener(
      listener: HandshakeCompletedListener,
  ): Unit

  def startHandshake(): Unit

  def setUseClientMode(mode: Boolean): Unit

  def getUseClientMode(): Boolean

  def setNeedClientAuth(need: Boolean): Unit

  def getNeedClientAuth(): Boolean

  def setWantClientAuth(want: Boolean): Unit

  def getWantClientAuth(): Boolean

  def setEnableSessionCreation(flag: Boolean): Unit

  def getEnableSessionCreation(): Boolean

  def getSSLParameters(): SSLParameters

  def setSSLParameters(params: SSLParameters): Unit

  def getApplicationProtocol(): String

  def getHandshakeApplicationProtocol(): String

  def setHandshakeApplicationProtocolSelector(
      selector: BiFunction[SSLSocket, JList[String], String],
  ): Unit

  def getHandshakeApplicationProtocolSelector(): BiFunction[SSLSocket, JList[String], String]
