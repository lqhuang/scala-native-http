package snhttp.jdk.net

import java.net.{InetAddress, Socket}
import javax.net.SocketFactory

object SocketFactoryImpl extends SocketFactory:

  def createSocket(): Socket =
    new Socket()

  def createSocket(host: String, port: Int): Socket =
    new Socket(host, port)

  def createSocket(
      host: String,
      port: Int,
      localHost: InetAddress,
      localPort: Int,
  ): Socket =
    new Socket(host, port, localHost, localPort)

  def createSocket(
      host: InetAddress,
      port: Int,
  ): Socket =
    new Socket(host, port)

  def createSocket(
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): Socket =
    new Socket(address, port, localAddress, localPort)
