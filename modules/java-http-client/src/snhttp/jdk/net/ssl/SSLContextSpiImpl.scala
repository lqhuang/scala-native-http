package snhttp.jdk.net.ssl

import java.security.{SecureRandom, Provider}
import javax.net.ssl.{
  SSLContextSpi,
  SSLParameters,
  KeyManager,
  SSLSessionContext,
  SSLSocketFactory,
  SSLEngine,
  TrustManager,
  SSLServerSocketFactory,
}

class SSLContextSpiImpl() extends SSLContextSpi():

  // Client session context bind to current SSLContext
  // only has one ClientSessionContext instance per SSLContext instance
  protected[ssl] val clientSessionContext = ClientSessionContextImpl(this)
  protected[ssl] val sslSocketFactory = SSLSocketFactoryImpl(this)

  def engineInit(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      sr: SecureRandom,
  ): Unit =
    // ...
    ???

  /// server side feature is not supported yet
  def engineCreateSSLEngine(): SSLEngine =
    ???

  def engineCreateSSLEngine(host: String, port: Int): SSLEngine =
    ClientSSLEngineImpl(this, host, port)

  /// server side feature is not supported yet
  def engineGetServerSocketFactory(): SSLServerSocketFactory =
    ???

  /// server side feature is not supported yet
  def engineGetServerSessionContext(): SSLSessionContext =
    ???

  def engineGetSocketFactory(): SSLSocketFactory =
    sslSocketFactory

  def engineGetClientSessionContext(): SSLSessionContext =
    clientSessionContext

  /// Since JDK 1.6
  override def engineGetDefaultSSLParameters(): SSLParameters =
    SSLParametersImpl.getDefault()

  /// Since JDK 1.6
  override def engineGetSupportedSSLParameters(): SSLParameters =
    SSLParametersImpl.getSupported()
