package snhttp.jdk.net.ssl

import java.io.InputStream
import java.net.{InetAddress, Socket}
import java.net.SocketException
import javax.net.ssl.SSLSocketFactory

protected[ssl] class SSLSocketFactoryImpl(ctxSpi: SSLContextSpiImpl) extends SSLSocketFactory:

  override def createSocket(): Socket =
    SSLSocketImpl(SSLParametersImpl.getDefault())

  def createSocket(host: String, port: Int): Socket =
    SSLSocketImpl(SSLParametersImpl.getDefault(), host, port)

  def createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket =
    SSLSocketImpl(SSLParametersImpl.getDefault(), host, port, localHost, localPort)

  def createSocket(host: InetAddress, port: Int): Socket =
    SSLSocketImpl(SSLParametersImpl.getDefault(), host, port)

  def createSocket(
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): Socket =
    SSLSocketImpl(SSLParametersImpl.getDefault(), address, port, localAddress, localPort)

  def createSocket(
      socket: Socket,
      host: String,
      port: Int,
      autoClose: Boolean,
  ): Socket =
    SSLSocketImpl(SSLParametersImpl.getDefault(), socket, host, port, autoClose)

  /// As docs:
  /// Creates a server mode Socket layered over an existing Socket
  ///
  /// server side feature is not supported yet, postpone the implementation
  override def createSocket(
      socket: Socket,
      consumed: InputStream,
      autoClose: Boolean,
  ): Socket = ???

  def getDefaultCipherSuites(): Array[String] =
    SSLParametersImpl.getDefaultCipherSuites()

  def getSupportedCipherSuites(): Array[String] =
    SSLParametersImpl.getSupportedCipherSuites()
