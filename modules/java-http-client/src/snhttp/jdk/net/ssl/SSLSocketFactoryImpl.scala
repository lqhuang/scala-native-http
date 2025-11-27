package snhttp.jdk.net.ssl

import java.io.InputStream
import java.net.{InetAddress, Socket}
import java.net.SocketException
import javax.net.ssl.SSLSocketFactory

import snhttp.jdk.internal.tls.ParamsData

object SSLSocketFactoryImpl extends SSLSocketFactory:

  override def createSocket(): Socket = SSLSocketImpl()

  def createSocket(host: String, port: Int): Socket = SSLSocketImpl(host, port)

  def createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket =
    SSLSocketImpl(host, port, localHost, localPort)

  def createSocket(host: InetAddress, port: Int): Socket =
    SSLSocketImpl(host, port)

  def createSocket(
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): Socket =
    SSLSocketImpl(address, port, localAddress, localPort)

  def createSocket(
      socket: Socket,
      host: String,
      port: Int,
      autoClose: Boolean,
  ): Socket =
    SSLSocketImpl(socket, host, port, autoClose)

  /// As docs:
  /// Creates a server mode Socket layered over an existing Socket
  ///
  /// Server side TLS is not supported yet
  override def createSocket(
      socket: Socket,
      consumed: InputStream,
      autoClose: Boolean,
  ): Socket = ???

  def getDefaultCipherSuites(): Array[String] =
    ParamsData.DefaultCipherSuites

  def getSupportedCipherSuites(): Array[String] =
    ParamsData.SupportedCipherSuites
