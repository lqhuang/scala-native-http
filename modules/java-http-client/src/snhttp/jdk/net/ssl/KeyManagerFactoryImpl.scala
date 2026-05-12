package snhttp.jdk.net.ssl

import java.security.{KeyStore, Provider}
import javax.net.ssl.{ManagerFactoryParameters, KeyManagerFactory, KeyManagerFactorySpi, KeyManager}

private[snhttp] class KeyManagerFactorySpiImpl extends KeyManagerFactorySpi:

  def engineInit(ks: KeyStore, password: Array[Char]): Unit =
    ???

  def engineInit(spec: ManagerFactoryParameters): Unit =
    ???

  def engineGetKeyManagers(): Array[KeyManager] =
    ???

private[snhttp] class KeyManagerFactoryImpl(provider: Provider, algorithm: String)
    extends KeyManagerFactory(KeyManagerFactorySpiImpl(), provider, algorithm)
