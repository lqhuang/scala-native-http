package snhttp.jdk.net.ssl

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

private[snhttp] class X509TrustManagerImpl(ks: KeyStore) extends X509TrustManager:

  def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit =
    checkTrusted(chain, authType)

  def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit =
    checkTrusted(chain, authType)

  def getAcceptedIssuers(): Array[X509Certificate] =
    ???

  /*
   * private helper methods
   */

  private final inline def checkTrusted(chain: Array[X509Certificate], authType: String): Unit = {
    require(
      chain != null && chain.nonEmpty,
      "Certificate chain must not be null or empty",
    )
    require(
      authType != null && authType.nonEmpty,
      "Authentication type must not be null or empty",
    )

    ???
  }
