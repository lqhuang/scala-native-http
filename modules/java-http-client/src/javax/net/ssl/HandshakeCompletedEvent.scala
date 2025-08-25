package javax.net.ssl

import java.security.Principal
import java.security.cert.{Certificate, X509Certificate}
import java.util.EventObject

// ref: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/net/ssl/HandshakeCompletedEvent.html
class HandshakeCompletedEvent(sock: SSLSocket, session: SSLSession) extends EventObject(sock) {

  def getSession(): SSLSession = session

  def getCipherSuite(): String = session.getCipherSuite()

  def getLocalCertificates(): Array[Certificate] = session.getLocalCertificates()

  def getPeerCertificates(): Array[Certificate] = session.getPeerCertificates()

  // `getPeerCertificateChain` deprecated since Java 9
  // def getPeerCertificateChain(): Array[X509Certificate]

  def getPeerPrincipal(): Principal =
    try session.getPeerPrincipal()
    catch {
      case _: AbstractMethodError =>
        val certs = getPeerCertificates()
        certs(0).asInstanceOf[X509Certificate].getSubjectX500Principal()
    }

  def getLocalPrincipal(): Principal =
    try session.getLocalPrincipal()
    catch {
      case _: AbstractMethodError =>
        val certs = getLocalCertificates()
        certs(0).asInstanceOf[X509Certificate].getSubjectX500Principal()
    }

  def getSocket(): SSLSocket = getSource().asInstanceOf[SSLSocket]
}
