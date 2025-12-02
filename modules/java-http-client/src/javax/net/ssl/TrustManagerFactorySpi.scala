package javax.net.ssl

import java.security.{Provider, KeyStore}
import java.util.Objects.requireNonNull

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/TrustManagerFactorySpi.html
abstract class TrustManagerFactorySpi():

  def engineInit(ks: KeyStore): Unit

  def engineInit(spec: ManagerFactoryParameters): Unit

  def engineGetTrustManagers(): Array[TrustManager]
