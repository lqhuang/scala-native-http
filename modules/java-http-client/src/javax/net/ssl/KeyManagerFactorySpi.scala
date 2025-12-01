package javax.net.ssl

import java.security.KeyStore
import javax.net.ssl.ManagerFactoryParameters

abstract class KeyManagerFactorySpi():

  protected def engineInit(ks: KeyStore, password: Array[Char]): Unit

  protected def engineInit(spec: ManagerFactoryParameters): Unit

  protected def engineGetKeyManagers(): Array[KeyManager]
