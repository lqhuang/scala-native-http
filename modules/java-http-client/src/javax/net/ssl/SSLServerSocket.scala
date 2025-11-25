package javax.net.ssl

import java.io.Closeable
import java.net.InetAddress

abstract class SSLServerSocket protected (
    port: Int,
    backlog: Int,
    address: InetAddress,
) extends Closeable:

  def this(port: Int) = this(port, 0, InetAddress.getByName(null))

  def this(port: Int, backlog: Int) = this(port, backlog, InetAddress.getByName(null))

  def getEnabledCipherSuites(): Array[String]

  def setEnabledCipherSuites(suites: Array[String]): Unit

  def getSupportedCipherSuites(): Array[String]

  def getSupportedProtocols(): Array[String]

  def getEnabledProtocols(): Array[String]

  def setEnabledProtocols(protocols: Array[String]): Unit

  def setNeedClientAuth(need: Boolean): Unit

  def getNeedClientAuth(): Unit

  def setWantClientAuth(want: Boolean): Unit

  def getWantClientAuth(): Boolean

  def setUseClientMode(mode: Boolean): Unit

  def getUseClientMode(): Boolean

  def setEnableSessionCreation(flag: Boolean): Unit

  def getEnableSessionCreation(): Boolean

  def getSSLParameters(): SSLParameters

  def setSSLParameters(params: SSLParameters): Unit
