package snhttp.jdk.net.ssl

import java.net.{InetAddress, ServerSocket}
import java.net.SocketException

import javax.net.ssl.SSLServerSocketFactory
import java.net.Socket

class SSLServerSocketFactoryImpl extends SSLServerSocketFactory {

  def createSocket(): Socket = ???

  def createSocket(host: String, port: Int): Socket = ???

  def createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket = ???

  def createSocket(host: InetAddress, port: Int): Socket = ???

  def createSocket(
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): Socket = ???

  def createServerSocket(): ServerSocket = ???

  def createServerSocket(port: Int): ServerSocket = ???

  def createServerSocket(port: Int, backlog: Int): ServerSocket = ???

  def createServerSocket(port: Int, backlog: Int, ifAddress: InetAddress): ServerSocket =
    ???

  def getDefaultCipherSuites(): Array[String] = Array.empty[String]

  def getSupportedCipherSuites(): Array[String] = Array.empty[String]
}
