package javax.net.ssl

import java.net.{HttpURLConnection, URL}
import java.security.Principal
import java.security.cert.Certificate
import java.util.Optional

abstract class HttpsURLConnection(url: URL) extends HttpURLConnection(url) {
  def getCipherSuite(): String

  def getLocalCertificates(): Array[Certificate]

  def getServerCertificates(): Array[Certificate]

  def getPeerPrincipal(): Principal

  def getLocalPrincipal(): Principal

  def setHostnameVerifier(verifier: HostnameVerifier): Unit

  def getHostnameVerifier(): HostnameVerifier

  def setSSLSocketFactory(factory: SSLSocketFactory): Unit

  def getSSLSocketFactory(): SSLSocketFactory

  // Since JDK 12
  def getSSLSession(): Optional[SSLSession]
}

object HttpsURLConnection {
  private var defaultHostnameVerifier: HostnameVerifier = ???
  private var defaultSSLSocketFactory: SSLSocketFactory = ???

  def getDefaultHostnameVerifier(): HostnameVerifier = ???

  def setDefaultHostnameVerifier(verifier: HostnameVerifier): Unit = ???

  def getDefaultSSLSocketFactory(): SSLSocketFactory = ???

  def setDefaultSSLSocketFactory(factor: SSLSocketFactory): Unit = ???
}
