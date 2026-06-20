package snhttp.jdk.net.ssl

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

import scala.scalanative.unsafe.{Ptr, toCString}
import scala.scalanative.unsafe.Zone

import _root_.snhttp.experimental.openssl.libcrypto
import _root_.snhttp.experimental.openssl.libcrypto.{X509_STORE, OsslLibCtxPtr}
import _root_.snhttp.utils.PointerCleaner
import _root_.snhttp.jdk.net.internal.PropertyUtils

private[snhttp] object X509TrustManagerNullImpl:

  def fromDefaultPath(): X509TrustManagerNullImpl =
    Zone.acquire { zone =>
      var ref: Ptr[X509_STORE] = null
      val ret = libcrypto.X509_STORE_load_file_ex(
        ref,
        toCString(PropertyUtils.trustStoreProp)(using zone),
        null.asInstanceOf[OsslLibCtxPtr],
        null,
      )
      if (ret != 1)
        throw new RuntimeException(
          s"failed to load trust store from ${PropertyUtils.trustStoreProp}",
        )
      new X509TrustManagerNullImpl(ref)
    }

private[snhttp] class X509TrustManagerNullImpl(val ref: Ptr[X509_STORE]) extends X509TrustManager:

  PointerCleaner.register(this, ref, ptr => libcrypto.X509_STORE_free(ptr)): Unit

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

end X509TrustManagerNullImpl

private[snhttp] class X509TrustManagerKeyStoreImpl(ks: KeyStore) extends X509TrustManager:

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

end X509TrustManagerKeyStoreImpl
