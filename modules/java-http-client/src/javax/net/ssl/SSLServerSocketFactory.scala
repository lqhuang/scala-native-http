package javax.net.ssl

import java.net.InetAddress
import javax.net.ServerSocketFactory

abstract class SSLServerSocketFactory extends ServerSocketFactory:

  def getDefaultCipherSuites(): Array[String]

  def getSupportedCipherSuites(): Array[String]

object SSLServerSocketFactory:

  private val defaultFactory: ServerSocketFactory = ???

  def getDefault(): ServerSocketFactory = defaultFactory

end SSLServerSocketFactory
