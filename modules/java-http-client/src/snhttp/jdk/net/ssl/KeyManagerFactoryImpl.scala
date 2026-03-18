package snhttp.jdk.net.ssl

import java.security.KeyStore
import javax.net.ssl.{ManagerFactoryParameters, KeyManagerFactory, KeyManagerFactorySpi, KeyManager}

class KeyManagerFactorySpiImpl() extends KeyManagerFactorySpi():

  def engineInit(ks: KeyStore, password: Array[Char]): Unit =
    ???

  def engineInit(spec: ManagerFactoryParameters): Unit =
    ???

  def engineGetKeyManagers(): Array[KeyManager] =
    ???

class KeyManagerFactoryImpl(
    spi: KeyManagerFactorySpi,
    provider: java.security.Provider,
    algorithm: String,
) extends KeyManagerFactory(spi, provider, algorithm)
