package javax.net.ssl

import java.security.{KeyStore, Provider, Security}
import java.util.Objects.requireNonNull

import javax.net.ssl.ManagerFactoryParameters

class KeyManagerFactory protected (
    private val provider: Provider,
    private val algorithm: String,
) {

  def getAlgorithm(): String = algorithm

  def getProvider(): Provider = provider

  def init(ks: KeyStore, password: Array[Char]): Unit = ???

  def init(spec: ManagerFactoryParameters): Unit = ???

  def getKeyManagers(): Array[KeyManager] = ???

}

object KeyManagerFactory {

  def getDefaultAlgorithm(): String = {
    val algorithmType = Security.getProperty("ssl.KeyManagerFactory.algorithm")
    if (algorithmType == null) "SunX509" else algorithmType
  }

  def getInstance(algorithm: String): KeyManagerFactory = {
    requireNonNull(algorithm, "null algorithm name")
    ???
  }

  def getInstance(algorithm: String, provider: String): KeyManagerFactory = {
    requireNonNull(algorithm, "null algorithm name")
    ???
  }

  def getInstance(algorithm: String, provider: Provider): KeyManagerFactory = {
    requireNonNull(algorithm, "null algorithm name")
    ???
  }

}
