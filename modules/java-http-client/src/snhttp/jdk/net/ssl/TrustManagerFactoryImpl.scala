package snhttp.jdk.net.ssl

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.{TrustManagerFactorySpi, TrustManagerFactory}

class TrustManagerFactorySpiImpl extends TrustManagerFactorySpi:

  protected[ssl] def engineInit(ks: KeyStore): Unit =
    ???

  protected[ssl] def engineInit(spec: ManagerFactoryParameters): Unit =
    ???

  protected[ssl] def engineGetTrustManagers(): Array[TrustManager] =
    ???

end TrustManagerFactorySpiImpl

class TrustManagerFactoryImpl(private val provider: Provider, private val algorithm: String)
    extends TrustManagerFactory(new TrustManagerFactorySpiImpl(), provider, algorithm)
