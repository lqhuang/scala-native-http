package snhttp.jdk.net.ssl

import java.io.InputStream
import java.net.{InetAddress, Socket}
import java.net.SocketException
import javax.net.ssl.SSLSocketFactory

import snhttp.jdk.internal.tls.{ContextData, DefaultParams}

protected object SSLSocketFactoryImpl extends SSLSocketFactory:

  override def createSocket(): Socket = SSLSocketImpl(ContextData())

  def createSocket(host: String, port: Int): Socket = SSLSocketImpl(ContextData(), host, port)

  def createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket =
    SSLSocketImpl(ContextData(), host, port, localHost, localPort)

  def createSocket(host: InetAddress, port: Int): Socket =
    SSLSocketImpl(ContextData(), host, port)

  def createSocket(
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): Socket =
    SSLSocketImpl(ContextData(), address, port, localAddress, localPort)

  def createSocket(
      socket: Socket,
      host: String,
      port: Int,
      autoClose: Boolean,
  ): Socket =
    SSLSocketImpl(ContextData(), socket, host, port, autoClose)

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
    DefaultParams.getDefaultCipherSuites()

  def getSupportedCipherSuites(): Array[String] =
    DefaultParams.getSupportedCipherSuites()
