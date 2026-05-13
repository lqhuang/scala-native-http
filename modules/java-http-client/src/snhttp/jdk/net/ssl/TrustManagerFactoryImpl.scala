package snhttp.jdk.net.ssl

import java.security.{KeyStore, Provider}
import java.security.cert.X509Certificate
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.{
  ManagerFactoryParameters,
  TrustManager,
  TrustManagerFactory,
  TrustManagerFactorySpi,
}

private[snhttp] class TrustManagerFactorySpiImpl extends TrustManagerFactorySpi:

  var _tm: X509TrustManagerImpl = _
  val _initialized = new AtomicBoolean(false)

  def engineInit(ks: KeyStore): Unit = {
    require(ks != null, "KeyStore must not be null")

    if !_initialized.compareAndExchange(false, true)
    then _tm = X509TrustManagerImpl(ks)
    else throw new IllegalStateException("TrustManagerFactory is already initialized")
  }

  def engineInit(spec: ManagerFactoryParameters): Unit =
    ???

  def engineGetTrustManagers(): Array[TrustManager] =
    if _initialized.get()
    then Array(_tm)
    else throw new IllegalStateException("TrustManagerFactory is not initialized")

end TrustManagerFactorySpiImpl

private[snhttp] class TrustManagerFactoryImpl(provider: Provider, algorithm: String)
    extends TrustManagerFactory(new TrustManagerFactorySpiImpl(), provider, algorithm)
