package snhttp.jdk.net.ssl

import java.io.InputStream
import java.net.{InetAddress, Socket}
import java.net.SocketException
import javax.net.ssl.SSLSocketFactory

protected[ssl] class SSLSocketFactoryImpl(ctxSpi: SSLContextSpiImpl) extends SSLSocketFactory:

  def createSocket(host: String, port: Int): ClientSSLSocketImpl =
    ClientSSLSocketImpl(SSLParametersImpl.getDefault(), host, port)

  def createSocket(host: InetAddress, port: Int): ClientSSLSocketImpl =
    ClientSSLSocketImpl(SSLParametersImpl.getDefault(), host, port)

  def createSocket(
      host: String,
      port: Int,
      localHost: InetAddress,
      localPort: Int,
  ): ClientSSLSocketImpl =
    ???

  def createSocket(
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): ClientSSLSocketImpl =
    ???

  // Cannot get socket's fd in java Socket class, mark as unsupported
  def createSocket(
      socket: Socket,
      host: String,
      port: Int,
      autoClose: Boolean,
  ): Socket =
    throw new UnsupportedOperationException(
      "Creating SSLSocket over existing Socket is not supported.",
    )

  /// Recognized as server mode feature which is not supported yet,
  /// postpone the implementation
  override def createSocket(): Socket =
    ???

  /// As docs: Creates a server mode Socket layered over an existing Socket
  ///
  /// server side feature is not supported yet, postpone the implementation
  override def createSocket(
      socket: Socket,
      consumed: InputStream,
      autoClose: Boolean,
  ): Socket =
    ???

  def getDefaultCipherSuites(): Array[String] =
    SSLParametersImpl.getDefaultCipherSuites()

  def getSupportedCipherSuites(): Array[String] =
    SSLParametersImpl.getSupportedCipherSuites()
