package snhttp.jdk.net.ssl

import java.security.{KeyStore, Provider}
import java.security.cert.X509Certificate
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.{
  ManagerFactoryParameters,
  TrustManager,
  X509TrustManager,
  TrustManagerFactory,
  TrustManagerFactorySpi,
}
import java.security.InvalidAlgorithmParameterException

import com.github.lolgab.scalanativecrypto.crypto.OpenSSLKeyStore

private[snhttp] class TrustManagerFactoryImpl(provider: Provider, algorithm: String)
    extends TrustManagerFactory(new TrustManagerFactorySpiImpl(), provider, algorithm)

private[snhttp] class TrustManagerFactorySpiImpl extends TrustManagerFactorySpi:

  var _tm: X509TrustManager = _
  val _initialized = new AtomicBoolean(false)

  def engineInit(ks: KeyStore): Unit =
    if !_initialized.compareAndExchange(false, true)
    then
      _tm =
        if ks == null
        then X509TrustManagerNullImpl.fromDefaultPath()
        else {
          if (ks.isInstanceOf[OpenSSLKeyStore])
            new X509TrustManagerKeyStoreImpl(ks.asInstanceOf[OpenSSLKeyStore])
          else
            throw new IllegalArgumentException(
              s"Unsupported KeyStore type: ${ks.getClass()}. Only OpenSSLKeyStore is supported.",
            )
        }
    else //
      throw new IllegalStateException("TrustManagerFactory is already initialized")

  def engineInit(spec: ManagerFactoryParameters): Unit =
    if (spec != null) {
      throw new InvalidAlgorithmParameterException(
        "ManagerFactoryParameters is not supported for current TrustManagerFactorySpiImpl",
      )
    }

  def engineGetTrustManagers(): Array[TrustManager] =
    if _initialized.get()
    then Array(_tm)
    else throw new IllegalStateException("TrustManagerFactory is not initialized")

end TrustManagerFactorySpiImpl
