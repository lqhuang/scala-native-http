package javax.net.ssl

import java.security.{SecureRandom, Provider}
import java.util.Objects.requireNonNull
import java.util.concurrent.atomic.AtomicBoolean

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLContext.html
abstract class SSLContextSpi():

  protected[ssl] def engineInit(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      random: SecureRandom,
  ): Unit

  protected[ssl] def engineGetSocketFactory(): SSLSocketFactory

  protected[ssl] def engineGetServerSocketFactory(): SSLServerSocketFactory

  protected[ssl] def engineCreateSSLEngine(): SSLEngine

  protected[ssl] def engineCreateSSLEngine(host: String, port: Int): SSLEngine

  protected[ssl] def engineGetServerSessionContext(): SSLSessionContext

  protected[ssl] def engineGetClientSessionContext(): SSLSessionContext

  protected[ssl] def engineGetDefaultSSLParameters(): SSLParameters

  protected[ssl] def engineGetSupportedSSLParameters(): SSLParameters
