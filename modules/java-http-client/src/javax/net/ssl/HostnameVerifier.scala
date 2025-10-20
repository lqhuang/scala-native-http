package javax.net.ssl

trait HostnameVerifier {
  def verify(hostname: String, session: SSLSession): Boolean
}
