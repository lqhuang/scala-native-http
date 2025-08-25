package javax.net.ssl

import java.security.{SecureRandom, Provider}
import java.security.{KeyManagementException, NoSuchAlgorithmException}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.Objects.requireNonNull

// ref: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/net/ssl/SSLContext.html
class SSLContext protected (
    private val provider: Provider,
    private val protocol: String,
) {

  private val _initialized = new AtomicBoolean(false)

  def getProtocol(): String = protocol

  def getProvider(): Provider = provider

  def init(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      random: SecureRandom,
  ): Unit =
    ???

  def getSocketFactory(): SSLSocketFactory = ???

  def getServerSocketFactory(): SSLServerSocketFactory = ???

  def createSSLEngine(): SSLEngine =
    ???

  def createSSLEngine(peerHost: String, peerPort: Int): SSLEngine =
    ???

  def getServerSessionContext(): SSLSessionContext =
    throw NotImplementedError(
      "SSLContext.getServerSessionContext() is not implemented yet",
    )

  def getClientSessionContext(): SSLSessionContext =
    ???

  def getDefaultSSLParameters(): SSLParameters =
    ???

  def getSupportedSSLParameters(): SSLParameters =
    ???
}

object SSLContext {
  @volatile var defaultContext: SSLContext = SSLContext.getInstance("Default")

  def getDefault(): SSLContext = defaultContext

  def setDefault(context: SSLContext): Unit = {
    requireNonNull(context)
    defaultContext = context
  }

  def getInstance(protocol: String): SSLContext = {
    requireNonNull(protocol)
    ???
  }

  def getInstance(protocol: String, provider: String): SSLContext = {
    requireNonNull(protocol)
    require(
      provider != null && provider != "",
      "Provider cannot be null or empty",
    )
    ???
  }

  def getInstance(protocol: String, provider: Provider): SSLContext = {
    requireNonNull(protocol)
    ???
  }
}
