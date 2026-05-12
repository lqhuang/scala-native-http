package javax.net.ssl

import java.security.{Provider, KeyStore}
import java.util.Objects.requireNonNull

import snhttp.jdk.jsse.provider.OpenSSLProvider

// Refs:
// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/TrustManagerFactorySpi.html
abstract class TrustManagerFactorySpi:

  protected[ssl] def engineInit(ks: KeyStore): Unit

  protected[ssl] def engineInit(spec: ManagerFactoryParameters): Unit

  protected[ssl] def engineGetTrustManagers(): Array[TrustManager]

end TrustManagerFactorySpi

// Refs:
// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/TrustManagerFactory.html
class TrustManagerFactory protected (
    private val spi: TrustManagerFactorySpi,
    private val provider: Provider,
    private val algorithm: String,
):

  final def getAlgorithm(): String = algorithm

  final def getProvider(): Provider = provider

  final def init(ks: KeyStore): Unit =
    spi.engineInit(ks)

  final def init(spec: ManagerFactoryParameters): Unit =
    spi.engineInit(spec)

  final def getTrustManagers(): Array[TrustManager] =
    spi.engineGetTrustManagers()

object TrustManagerFactory:

  final inline def getDefaultAlgorithm(): String =
    "PKIX"

  final inline def getInstance(algorithm: String): TrustManagerFactory =
    getInstance(algorithm, OpenSSLProvider.defaultInstance)

  final inline def getInstance(algorithm: String, provider: String): TrustManagerFactory =
    throw new UnsupportedOperationException("Not supported by Scala Native")

  final def getInstance(
      algorithm: String,
      provider: Provider,
  ): TrustManagerFactory = {
    requireNonNull(algorithm)
    requireNonNull(provider)
    require(algorithm.nonEmpty)

    val service = provider.getService("TrustManagerFactory", algorithm)
    service.newInstance(null).asInstanceOf[TrustManagerFactory]
  }

end TrustManagerFactory
