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

import com.github.lolgab.scalanativecrypto.crypto
import snhttp.jdk.internal.tls.DefaultParams

class SSLContextSpiImpl() extends SSLContextSpi():

  def engineInit(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      sr: SecureRandom,
  ): Unit = ???

  def engineCreateSSLEngine(): SSLEngine =
    SSLEngineImpl(new SSLParametersImpl(???, ???), null, 0)

  def engineCreateSSLEngine(host: String, port: Int): SSLEngine =
    SSLEngineImpl(new SSLParametersImpl(???, ???), host, port)

  /// server side feature is not supported yet
  def engineGetServerSocketFactory(): SSLServerSocketFactory = ???

  /// server side feature is not supported yet
  def engineGetServerSessionContext(): SSLSessionContext = ???

  def engineGetSocketFactory(): SSLSocketFactory =
    SSLSocketFactoryImpl

  def engineGetClientSessionContext(): SSLSessionContext =
    SSLSessionContextImpl()

  /// Since JDK 1.6
  override def engineGetDefaultSSLParameters(): SSLParameters =
    SSLParametersImpl.getDefault()

  /// Since JDK 1.6
  override def engineGetSupportedSSLParameters(): SSLParameters =
    SSLParametersImpl.getSupported()
