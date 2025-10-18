package javax.net.ssl

import java.security.Principal
import java.security.cert.{Certificate, X509Certificate}
import java.util.EventObject

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/HandshakeCompletedEvent.html
class HandshakeCompletedEvent(sock: SSLSocket, session: SSLSession) extends EventObject(sock) {

  def getSession(): SSLSession = session

  def getCipherSuite(): String = session.getCipherSuite()

  def getLocalCertificates(): Array[Certificate] = session.getLocalCertificates()

  def getPeerCertificates(): Array[Certificate] = session.getPeerCertificates()

  @deprecated // since Java 9
  def getPeerCertificateChain(): Array[X509Certificate] = throw new UnsupportedOperationException(
    "getPeerCertificateChain is deprecated since JDK 9 and not implemented",
  )

  def getPeerPrincipal(): Principal = ???

  def getLocalPrincipal(): Principal = ???

  def getSocket(): SSLSocket = getSource().asInstanceOf[SSLSocket]
}
