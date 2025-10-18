package javax.net.ssl

import java.security.{KeyStore, Provider}
import java.util.Objects.requireNonNull

import javax.net.ssl.ManagerFactoryParameters

abstract class KeyManagerFactory(
    private val provider: Provider,
    private val algorithm: String,
) {
  final def getAlgorithm(): String = algorithm

  final def getProvider(): Provider = provider

  def init(ks: KeyStore, password: Array[Char]): Unit

  def init(spec: ManagerFactoryParameters): Unit

  def getKeyManagers(): Array[KeyManager]
}

object KeyManagerFactory {

  def getDefaultAlgorithm(): String = ???

  def getInstance(algorithm: String): KeyManagerFactory = {
    requireNonNull(algorithm)
    require(algorithm.nonEmpty)
    ???
  }

  def getInstance(algorithm: String, provider: String): KeyManagerFactory =
    throw new UnsupportedOperationException("Not supported in Scala Native yet")

  def getInstance(algorithm: String, provider: Provider): KeyManagerFactory = {
    requireNonNull(algorithm)
    requireNonNull(provider)
    require(algorithm.nonEmpty)
    ???
  }

}
