package javax.net.ssl

import java.security.cert.X509Certificate

trait X509TrustManager extends TrustManager:
  def checkClientTrusted(
      chain: Array[X509Certificate],
      authType: String,
  ): Unit

  def checkServerTrusted(
      chain: Array[X509Certificate],
      authType: String,
  ): Unit

  def getAcceptedIssuers(): Array[X509Certificate]
