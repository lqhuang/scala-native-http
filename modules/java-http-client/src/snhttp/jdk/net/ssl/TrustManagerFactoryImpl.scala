package snhttp.jdk.net.ssl

import java.security.{KeyStore, Provider}
import java.security.cert.X509Certificate
import javax.net.ssl.{
  ManagerFactoryParameters,
  TrustManager,
  TrustManagerFactory,
  TrustManagerFactorySpi,
}

private[snhttp] class TrustManagerFactorySpiImpl extends TrustManagerFactorySpi:

  def engineInit(ks: KeyStore): Unit =
    ???

  def engineInit(spec: ManagerFactoryParameters): Unit =
    ???

  def engineGetTrustManagers(): Array[TrustManager] =
    ???

end TrustManagerFactorySpiImpl

private[snhttp] class TrustManagerFactoryImpl(provider: Provider, algorithm: String)
    extends TrustManagerFactory(new TrustManagerFactorySpiImpl(), provider, algorithm)
