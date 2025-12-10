package snhttp.jdk.net.ssl

import java.net.{Socket, InetAddress}
import java.util.List as JList
import java.util.function.BiFunction
import java.util.Objects.requireNonNull
import javax.net.ssl.{SSLParameters, SSLSession, SSLSocket, HandshakeCompletedListener}

import snhttp.experimental.openssl.ssl

/**
 * SSL Socket Implementation
 *
 * map to OpenSSL `BIO` with blocking/non-blocking mode
 */
class ClientSSLSocketImpl protected (
    sslParams: SSLParametersImpl,
    host: InetAddress,
    port: Int,
    socket: Socket,
    autoClose: Boolean = true,
) extends SSLSocket:

  import socket.*

  // protected[ssl] val ptr = ssl.BIO_new(socket.getFileno(), autoClose)

  def getSupportedCipherSuites(): Array[String] =
    SSLParametersImpl.getSupportedCipherSuites()

  def getEnabledCipherSuites(): Array[String] =
    sslParams.getEnabledCipherSuites()

  def setEnabledCipherSuites(suites: Array[String]): Unit =
    validateArrayOfStrings(suites)
    sslParams.setEnabledCipherSuites(suites)

  def getSupportedProtocols(): Array[String] =
    SSLParametersImpl.getSupportedProtocols()

  def getEnabledProtocols(): Array[String] =
    sslParams.getEnabledProtocols()

  def setEnabledProtocols(protocols: Array[String]): Unit =
    validateArrayOfStrings(protocols)
    sslParams.setEnabledProtocols(protocols)

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
    sslParams.setUseClientMode(mode)

  def getUseClientMode(): Boolean =
    sslParams.getUseClientMode()

  /// server side feature is not supported yet
  def setNeedClientAuth(need: Boolean): Unit = ???

  /// server side feature is not supported yet
  def getNeedClientAuth(): Boolean = ???

  /// server side feature is not supported yet
  def setWantClientAuth(want: Boolean): Unit = ???

  /// server side feature is not supported yet
  def getWantClientAuth(): Boolean = ???

  def setEnableSessionCreation(flag: Boolean): Unit =
    sslParams.setEnableSessionCreation(flag)

  def getEnableSessionCreation(): Boolean =
    sslParams.getEnableSessionCreation()

  override def getSSLParameters(): SSLParameters =
    sslParams.getSSLParameters()

  override def setSSLParameters(params: SSLParameters): Unit =
    sslParams.setSSLParameters(params)

  override def getApplicationProtocol(): String =
    sslParams.getApplicationProtocol()

  override def getHandshakeApplicationProtocol(): String =
    sslParams.getHandshakeApplicationProtocol()

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

object ClientSSLSocketImpl:

  def apply(sslParams: SSLParametersImpl): ClientSSLSocketImpl =
    throw new NotImplementedError("Not implemented yet")

  def apply(sslParams: SSLParametersImpl, host: String, port: Int): ClientSSLSocketImpl =
    requireNonNull(host)
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    val socket = new Socket(host, port)
    new ClientSSLSocketImpl(sslParams, socket.getInetAddress(), socket.getPort(), socket)

  def apply(sslParams: SSLParametersImpl, address: InetAddress, port: Int): ClientSSLSocketImpl =
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    val socket = new Socket(address, port)
    new ClientSSLSocketImpl(sslParams, socket.getInetAddress(), socket.getPort(), socket)

  def apply(
      sslParams: SSLParametersImpl,
      host: String,
      port: Int,
      localHost: InetAddress,
      localPort: Int,
  ): ClientSSLSocketImpl =
    requireNonNull(host)
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    val socket = new Socket(host, port, localHost, localPort)
    new ClientSSLSocketImpl(sslParams, socket.getInetAddress(), socket.getPort(), socket)

  def apply(
      sslParams: SSLParametersImpl,
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): ClientSSLSocketImpl =
    requireNonNull(port)
    requireNonNull(localPort)
    require(port > -1 && port <= 65535)
    require(localPort >= 0 && localPort <= 65535)
    val socket = new Socket(address, port, localAddress, localPort)
    new ClientSSLSocketImpl(sslParams, socket.getInetAddress(), socket.getPort(), socket)

  def apply(
      sslParams: SSLParametersImpl,
      socket: Socket,
      host: String,
      port: Int,
      autoClose: Boolean,
  ): ClientSSLSocketImpl =
    requireNonNull(socket)
    requireNonNull(host)
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    if socket.isClosed()
    then {
      val _socket = new Socket(host, port, socket.getLocalAddress(), socket.getLocalPort())
      new ClientSSLSocketImpl(
        sslParams,
        socket.getInetAddress(),
        socket.getPort(),
        socket,
        autoClose,
      )
    } else {
      new ClientSSLSocketImpl(sslParams, InetAddress.getByName(host), port, socket, autoClose)
    }

end ClientSSLSocketImpl
