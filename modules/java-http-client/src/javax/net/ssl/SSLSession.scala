package javax.net.ssl

import java.security.Principal
import java.security.cert.Certificate
import java.nio.ByteBuffer

// ref: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/net/ssl/SSLSession.html
trait SSLSession {
  def getId(): Array[Byte]

  def getSessionContext(): SSLSessionContext

  def getCreationTime(): Long

  def getLastAccessedTime(): Long

  def invalidate(): Unit

  def isValid(): Boolean

  def putValue(name: String, value: Object): Unit

  def getValue(name: String): Object

  def removeValue(name: String): Unit

  def getValueNames(): Array[String]

  def getPeerCertificates(): Array[Certificate]

  def getLocalCertificates(): Array[Certificate]

  // @deprecated since JDK 9
  // def getPeerCertificateChain(): Array[javax.security.cert.X509Certificate] =

  def getPeerPrincipal(): Principal

  def getLocalPrincipal(): Principal

  def getCipherSuite(): String

  def getProtocol(): String

  def getPeerHost(): String

  def getPeerPort(): Int

  def getPacketBufferSize(): Int

  def getApplicationBufferSize(): Int
}
