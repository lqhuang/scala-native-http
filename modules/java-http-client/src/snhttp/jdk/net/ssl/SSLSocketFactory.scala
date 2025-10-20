package snhttp.jdk.net.ssl

import java.io.InputStream
import java.net.{InetAddress, Socket}
import java.net.SocketException
import javax.net.ssl.SSLSocketFactory

abstract class DefaultSSLSocketFactory extends SSLSocketFactory {

  // override def createSocket(): Socket = ???

  def createSocket(host: String, port: Int): Socket = ???

  def createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket = ???

  def createSocket(host: InetAddress, port: Int): Socket = ???

  def createSocket(
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): Socket = ???

  def createSocket(
      socket: Socket,
      host: String,
      port: Int,
      autoClose: Boolean,
  ): Socket = ???

  // override def createSocket(
  //     socket: Socket,
  //     consumed: InputStream,
  //     autoClose: Boolean,
  // ): Socket = ???

  def getDefaultCipherSuites(): Array[String] = Array.empty[String]

  def getSupportedCipherSuites(): Array[String] = Array.empty[String]
}
