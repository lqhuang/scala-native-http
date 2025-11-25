package javax.net.ssl

import java.security.{SecureRandom, Provider}
import java.util.Objects.requireNonNull
import java.util.concurrent.atomic.AtomicBoolean

trait SSLContextSpi

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLContext.html
abstract class SSLContext(
    private val spi: SSLContextSpi,
    private val provider: Provider,
    private val protocol: String,
):

  final def getProtocol(): String = protocol

  final def getProvider(): Provider = provider

  def init(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      random: SecureRandom,
  ): Unit

  def getSocketFactory(): SSLSocketFactory

  def getServerSocketFactory(): SSLServerSocketFactory

  def createSSLEngine(): SSLEngine

  def createSSLEngine(peerHost: String, peerPort: Int): SSLEngine

  def getServerSessionContext(): SSLSessionContext

  def getClientSessionContext(): SSLSessionContext

  def getDefaultSSLParameters(): SSLParameters

  def getSupportedSSLParameters(): SSLParameters

object SSLContext:

  @volatile private var defaultContext: SSLContext = SSLContext.getInstance("TLSv1.2")

  def getDefault(): SSLContext =
    defaultContext

  def setDefault(context: SSLContext): Unit =
    requireNonNull(context)
    defaultContext = context

  def getInstance(protocol: String): SSLContext =
    requireNonNull(protocol)
    require(protocol.nonEmpty)
    ???

  def getInstance(protocol: String, provider: String): SSLContext =
    throw new UnsupportedOperationException("Not supported in Scala Native yet")

  def getInstance(protocol: String, provider: Provider): SSLContext =
    requireNonNull(protocol)
    requireNonNull(provider)
    require(protocol.nonEmpty)
    ???

end SSLContext
