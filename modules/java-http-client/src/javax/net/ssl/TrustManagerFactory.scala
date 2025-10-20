package javax.net.ssl

import java.security.{Provider, KeyStore}
import java.util.Objects.requireNonNull

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/TrustManagerFactory.html
abstract class TrustManagerFactory(
    private val provider: Provider,
    private val algorithm: String,
) {
  final def getAlgorithm(): String = algorithm

  final def getProvider(): Provider = provider

  def init(ks: KeyStore): Unit

  def init(spec: ManagerFactoryParameters): Unit

  def getTrustManagers(): Array[TrustManager]
}

object TrustManagerFactory {
  def getDefaultAlgorithm(): String = "PKIX"

  def getInstance(algorithm: String): TrustManagerFactory = {
    requireNonNull(algorithm)
    require(algorithm.nonEmpty)
    ???
  }

  def getInstance(algorithm: String, provider: String): TrustManagerFactory =
    throw new UnsupportedOperationException("Not supported by Scala Native")

  def getInstance(
      algorithm: String,
      provider: Provider,
  ): TrustManagerFactory = {
    requireNonNull(algorithm)
    requireNonNull(provider)
    require(algorithm.nonEmpty)
    ???
  }
}
