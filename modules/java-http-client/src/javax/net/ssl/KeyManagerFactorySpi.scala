package javax.net.ssl

import java.security.KeyStore
import javax.net.ssl.ManagerFactoryParameters

abstract class KeyManagerFactorySpi():

  protected[ssl] def engineInit(ks: KeyStore, password: Array[Char]): Unit

  protected[ssl] def engineInit(spec: ManagerFactoryParameters): Unit

  protected[ssl] def engineGetKeyManagers(): Array[KeyManager]
