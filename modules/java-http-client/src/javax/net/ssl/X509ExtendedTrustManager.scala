package javax.net.ssl

import java.security.cert.X509Certificate

abstract class X509ExtendedTrustManager extends X509TrustManager:
  def checkClientTrusted(
      chain: Array[X509Certificate],
      authType: String,
      socket: java.net.Socket,
  ): Unit

  def checkServerTrusted(
      chain: Array[X509Certificate],
      authType: String,
      socket: java.net.Socket,
  ): Unit

  def checkClientTrusted(
      chain: Array[X509Certificate],
      authType: String,
      engine: SSLEngine,
  ): Unit

  def checkServerTrusted(
      chain: Array[X509Certificate],
      authType: String,
      engine: SSLEngine,
  ): Unit
