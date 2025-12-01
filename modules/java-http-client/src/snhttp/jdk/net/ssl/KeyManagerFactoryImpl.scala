package snhttp.jdk.net.ssl

import java.security.{KeyStore, Provider, Security}
import java.util.Objects

import javax.net.ssl.{KeyManagerFactory, KeyManager, ManagerFactoryParameters}

class KeyManagerFactoryImpl(
    private val spi: KeyManagerFactorySpiImpl,
    private val provider: Provider,
    private val algorithm: String,
) extends KeyManagerFactory(spi, provider, algorithm):

  def init(ks: KeyStore, password: Array[Char]): Unit = ???

  def init(spec: ManagerFactoryParameters): Unit = ???

  def getKeyManagers(): Array[KeyManager] = ???
