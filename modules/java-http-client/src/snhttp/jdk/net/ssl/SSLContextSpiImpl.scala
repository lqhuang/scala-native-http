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

class SSLContextSpiImpl() extends SSLContextSpi():

  def engineGetServerSocketFactory(): SSLServerSocketFactory = ???

  def engineInit(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      sr: SecureRandom,
  ): Unit = ???

  def engineCreateSSLEngine(): SSLEngine = ???

  def engineCreateSSLEngine(host: String, port: Int): SSLEngine = ???

  def engineGetServerSessionContext(): SSLSessionContext = ???

  def engineGetSocketFactory(): SSLSocketFactory = ???

  def engineGetClientSessionContext(): SSLSessionContext = ???

  def engineGetDefaultSSLParameters(): SSLParameters = ???

  def engineGetSupportedSSLParameters(): SSLParameters = ???
