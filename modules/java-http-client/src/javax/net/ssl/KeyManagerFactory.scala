package javax.net.ssl

import java.security.{KeyStore, Provider}
import java.util.Objects.requireNonNull

class KeyManagerFactory protected (
    private val spi: KeyManagerFactorySpi,
    private val provider: Provider,
    private val algorithm: String,
):

  final def getAlgorithm(): String = algorithm

  final def getProvider(): Provider = provider

  final def init(ks: KeyStore, password: Array[Char]): Unit =
    spi.engineInit(ks, password)

  final def init(spec: ManagerFactoryParameters): Unit =
    spi.engineInit(spec)

  final def getKeyManagers(): Array[KeyManager] =
    spi.engineGetKeyManagers()

object KeyManagerFactory:

  final def getDefaultAlgorithm(): String = ???

  final def getInstance(algorithm: String): KeyManagerFactory = {
    requireNonNull(algorithm)
    require(algorithm.nonEmpty)
    ???
  }

  final def getInstance(algorithm: String, provider: String): KeyManagerFactory =
    throw new UnsupportedOperationException("Not supported in Scala Native yet")

  final def getInstance(algorithm: String, provider: Provider): KeyManagerFactory =
    requireNonNull(algorithm)
    requireNonNull(provider)
    require(algorithm.nonEmpty)
    ???

end KeyManagerFactory
