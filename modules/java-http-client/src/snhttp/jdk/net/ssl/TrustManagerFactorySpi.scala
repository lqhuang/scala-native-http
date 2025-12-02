package snhttp.jdk.net.ssl

import java.security.{Provider, KeyStore}
import java.util.Objects.requireNonNull
import javax.net.ssl.{TrustManager, TrustManagerFactorySpi, ManagerFactoryParameters}

class TrustManagerFactorySpiImpl extends TrustManagerFactorySpi:

  def engineInit(ks: KeyStore): Unit = ???

  def engineInit(spec: ManagerFactoryParameters): Unit = ???

  def engineGetTrustManagers(): Array[TrustManager] = ???
