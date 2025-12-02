package snhttp.jdk.net.ssl

import java.security.KeyStore
import javax.net.ssl.{ManagerFactoryParameters, KeyManagerFactorySpi, KeyManager}

class KeyManagerFactorySpiImpl() extends KeyManagerFactorySpi():

  protected def engineInit(ks: KeyStore, password: Array[Char]): Unit =
    ???

  protected def engineInit(spec: ManagerFactoryParameters): Unit =
    ???

  protected def engineGetKeyManagers(): Array[KeyManager] =
    ???
