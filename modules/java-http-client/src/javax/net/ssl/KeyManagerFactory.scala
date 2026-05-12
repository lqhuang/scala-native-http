package javax.net.ssl

import java.security.{KeyStore, Provider}
import java.util.Objects.requireNonNull

import snhttp.jdk.jsse.provider.OpenSSLProvider

// Refs:
// https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/KeyManagerFactorySpi.html
abstract class KeyManagerFactorySpi:

  protected[ssl] def engineInit(ks: KeyStore, password: Array[Char]): Unit

  protected[ssl] def engineInit(spec: ManagerFactoryParameters): Unit

  protected[ssl] def engineGetKeyManagers(): Array[KeyManager]

end KeyManagerFactorySpi

// Refs:
// https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/KeyManagerFactory.html
class KeyManagerFactory protected (
    spi: KeyManagerFactorySpi,
    provider: Provider,
    algorithm: String,
):

  final def getAlgorithm(): String =
    algorithm

  final def getProvider(): Provider =
    provider

  final def init(ks: KeyStore, password: Array[Char]): Unit =
    spi.engineInit(ks, password)

  final def init(spec: ManagerFactoryParameters): Unit =
    spi.engineInit(spec)

  final def getKeyManagers(): Array[KeyManager] =
    spi.engineGetKeyManagers()

object KeyManagerFactory:

  final def getDefaultAlgorithm(): String =
    "PKIX"

  final def getInstance(algorithm: String): KeyManagerFactory =
    getInstance(algorithm, OpenSSLProvider.defaultInstance)

  final def getInstance(algorithm: String, provider: String): KeyManagerFactory =
    throw new UnsupportedOperationException("Not supported in Scala Native yet")

  final def getInstance(algorithm: String, provider: Provider): KeyManagerFactory = {
    requireNonNull(algorithm)
    requireNonNull(provider)
    require(algorithm.nonEmpty)

    val service = provider.getService("KeyManagerFactory", algorithm)
    service.newInstance(null).asInstanceOf[KeyManagerFactory]
  }

end KeyManagerFactory
