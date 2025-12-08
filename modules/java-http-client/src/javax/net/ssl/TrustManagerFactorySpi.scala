package javax.net.ssl

import java.security.{Provider, KeyStore}
import java.util.Objects.requireNonNull

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/TrustManagerFactorySpi.html
abstract class TrustManagerFactorySpi:

  protected[ssl] def engineInit(ks: KeyStore): Unit

  protected[ssl] def engineInit(spec: ManagerFactoryParameters): Unit

  protected[ssl] def engineGetTrustManagers(): Array[TrustManager]
