package javax.net

import java.io.IOException
import java.net.{InetAddress, Socket}
import java.net.{SocketException, UnknownHostException}

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/SocketFactory.html
abstract class SocketFactory:

  def createSocket(): Socket

  def createSocket(host: String, port: Int): Socket

  def createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket

  def createSocket(host: InetAddress, port: Int): Socket

  def createSocket(
      address: InetAddress,
      port: Int,
      localAddress: InetAddress,
      localPort: Int,
  ): Socket

object SocketFactory:

  import snhttp.jdk.net.SocketFactoryImpl

  def getDefault(): SocketFactory = SocketFactoryImpl

end SocketFactory
