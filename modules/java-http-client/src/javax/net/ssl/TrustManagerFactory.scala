package javax.net.ssl

import java.security.{Provider, KeyStore}
import java.util.Objects.requireNonNull

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/TrustManagerFactory.html
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

  final def getDefaultAlgorithm(): String = "PKIX"

  final def getInstance(algorithm: String): TrustManagerFactory = {
    requireNonNull(algorithm)
    require(algorithm.nonEmpty)
    ???
  }

  final def getInstance(algorithm: String, provider: String): TrustManagerFactory =
    throw new UnsupportedOperationException("Not supported by Scala Native")

  final def getInstance(
      algorithm: String,
      provider: Provider,
  ): TrustManagerFactory = {
    requireNonNull(algorithm)
    requireNonNull(provider)
    require(algorithm.nonEmpty)
    ???
  }

end TrustManagerFactory
