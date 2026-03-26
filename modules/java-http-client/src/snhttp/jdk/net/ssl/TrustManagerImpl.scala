package snhttp.jdk.net.ssl

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.{X509TrustManager, TrustManagerFactory}

class TrustManagerImpl(ks: KeyStore) extends X509TrustManager:

  private val tm: X509TrustManager = ???

  def getAcceptedIssuers(): Array[X509Certificate] =
    tm.getAcceptedIssuers()

  def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit =
    tm.checkClientTrusted(chain, authType)

  def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit =
    tm.checkServerTrusted(chain, authType)
