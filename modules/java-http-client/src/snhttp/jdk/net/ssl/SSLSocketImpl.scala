package snhttp.jdk.net.ssl

import java.net.{Socket, InetAddress}
import java.util.List as JList
import java.util.function.BiFunction
import java.util.Objects.requireNonNull
import javax.net.ssl.{SSLParameters, SSLSession, SSLSocket, HandshakeCompletedListener}

import snhttp.jdk.internal.tls.ContextData

protected class SSLSocketImpl(
    context: ContextData,
    host: InetAddress,
    port: Int,
    socket: Socket,
    autoClose: Boolean = true,
) extends SSLSocket:

  import socket.*

  def getSupportedCipherSuites(): Array[String] =
    context.getSupportedCipherSuites()

  def getEnabledCipherSuites(): Array[String] =
    context.getEnabledCipherSuites()

  def setEnabledCipherSuites(suites: Array[String]): Unit =
    validateArrayOfStrings(suites)
    context.setEnabledCipherSuites(suites)

  def getSupportedProtocols(): Array[String] =
    context.getSupportedProtocols()

  def getEnabledProtocols(): Array[String] =
    context.getEnabledProtocols()

  def setEnabledProtocols(protocols: Array[String]): Unit =
    validateArrayOfStrings(protocols)
    context.setEnabledProtocols(protocols)

  /// This method will initiate the initial handshake if necessary and then
  /// block until the handshake has been established.
  ///
  /// If an error occurs during the initial handshake, this method returns
  /// an invalid session object which reports an invalid cipher suite of
  /// "SSL_NULL_WITH_NULL_NULL".
  def getSession(): SSLSession = ???

  override def getHandshakeSession(): SSLSession = ???

  def addHandshakeCompletedListener(listener: HandshakeCompletedListener): Unit = ???

  def removeHandshakeCompletedListener(
      listener: HandshakeCompletedListener,
  ): Unit = ???

  def startHandshake(): Unit = ???

  def setUseClientMode(mode: Boolean): Unit =
    context.setUseClientMode(mode)

  def getUseClientMode(): Boolean =
    context.getUseClientMode()

  /// server side feature is not supported yet
  def setNeedClientAuth(need: Boolean): Unit = ???

  /// server side feature is not supported yet
  def getNeedClientAuth(): Boolean = ???

  /// server side feature is not supported yet
  def setWantClientAuth(want: Boolean): Unit = ???

  /// server side feature is not supported yet
  def getWantClientAuth(): Boolean = ???

  def setEnableSessionCreation(flag: Boolean): Unit =
    context.setEnableSessionCreation(flag)

  def getEnableSessionCreation(): Boolean =
    context.getEnableSessionCreation()

  override def getSSLParameters(): SSLParameters =
    context.sslParameters

  override def setSSLParameters(params: SSLParameters): Unit =
    context.setSSLParameters(params)

  override def getApplicationProtocol(): String =
    context.getApplicationProtocol()

  override def getHandshakeApplicationProtocol(): String =
    context.getHandshakeApplicationProtocol()

  override def setHandshakeApplicationProtocolSelector(
      selector: BiFunction[SSLSocket, JList[String], String],
  ): Unit = ???

  override def getHandshakeApplicationProtocolSelector()
      : BiFunction[SSLSocket, JList[String], String] = ???

  private def validateArrayOfStrings(arr: Array[String]): Unit =
    if (arr == null)
      throw new IllegalArgumentException()
    if (arr.filter(each => each == null || each.isEmpty()).length > 0)
      throw new IllegalArgumentException()

object SSLSocketImpl:

  def apply(spi: ContextData): SSLSocketImpl =
    throw new NotImplementedError("Not implemented yet")

  def apply(spi: ContextData, host: String, port: Int): SSLSocketImpl =
    requireNonNull(host)
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    val socket = new Socket(host, port)
    new SSLSocketImpl(spi, socket.getInetAddress(), socket.getPort(), socket)

  def apply(spi: ContextData, address: InetAddress, port: Int): SSLSocketImpl =
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    val socket = new Socket(address, port)
    new SSLSocketImpl(spi, socket.getInetAddress(), socket.getPort(), socket)

  def apply(
      spi: ContextData,
      host: String,
      port: Int,
      localHost: InetAddress,
      localPort: Int,
  ): SSLSocketImpl =
    requireNonNull(host)
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    val socket = new Socket(host, port, localHost, localPort)
    new SSLSocketImpl(spi, socket.getInetAddress(), socket.getPort(), socket)

  def apply(
      spi: ContextData,
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): SSLSocketImpl =
    requireNonNull(port)
    requireNonNull(localPort)
    require(port > -1 && port <= 65535)
    require(localPort >= 0 && localPort <= 65535)
    val socket = new Socket(address, port, localAddress, localPort)
    new SSLSocketImpl(spi, socket.getInetAddress(), socket.getPort(), socket)

  def apply(
      spi: ContextData,
      socket: Socket,
      host: String,
      port: Int,
      autoClose: Boolean,
  ): SSLSocketImpl =
    requireNonNull(socket)
    requireNonNull(host)
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    if socket.isClosed()
    then {
      val _socket = new Socket(host, port, socket.getLocalAddress(), socket.getLocalPort())
      new SSLSocketImpl(spi, socket.getInetAddress(), socket.getPort(), socket, autoClose)
    } else {
      new SSLSocketImpl(spi, InetAddress.getByName(host), port, socket, autoClose)
    }

end SSLSocketImpl
