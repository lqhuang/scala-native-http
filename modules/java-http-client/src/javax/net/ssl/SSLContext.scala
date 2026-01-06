package javax.net.ssl

import java.security.{SecureRandom, Provider}
import java.security.NoSuchAlgorithmException
import java.util.Objects.requireNonNull
import java.util.concurrent.atomic.AtomicBoolean

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLContext.html
class SSLContext protected (
    private val spi: SSLContextSpi,
    private val provider: Provider,
    private val protocol: String,
):

  requireNonNull(spi)
  requireNonNull(provider)
  requireNonNull(protocol)
  require(protocol.nonEmpty)

  final def getProtocol(): String =
    protocol

  final def getProvider(): Provider =
    provider

  final def init(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      random: SecureRandom,
  ): Unit =
    spi.engineInit(km, tm, random)

  final def getSocketFactory(): SSLSocketFactory =
    spi.engineGetSocketFactory()

  final def getServerSocketFactory(): SSLServerSocketFactory =
    spi.engineGetServerSocketFactory()

  final def createSSLEngine(): SSLEngine =
    spi.engineCreateSSLEngine()

  final def createSSLEngine(host: String, port: Int): SSLEngine =
    spi.engineCreateSSLEngine(host, port)

  final def getServerSessionContext(): SSLSessionContext =
    spi.engineGetServerSessionContext()

  final def getClientSessionContext(): SSLSessionContext =
    spi.engineGetClientSessionContext()

  final def getDefaultSSLParameters(): SSLParameters =
    spi.engineGetDefaultSSLParameters()

  final def getSupportedSSLParameters(): SSLParameters =
    spi.engineGetSupportedSSLParameters()

object SSLContext:

  import snhttp.jdk.internal.tls.DefaultParams

  @volatile private var defaultContext: SSLContext = SSLContext.getInstance("TLSv1.3")

  def getDefault(): SSLContext =
    // println("DEBUG: SSLContext.getDefault() called")
    defaultContext

  def setDefault(context: SSLContext): Unit =
    requireNonNull(context)
    defaultContext = context

  def getInstance(protocol: String): SSLContext =
    requireNonNull(protocol)
    if (!DefaultParams.SupportedProtocols.map(_.toLowerCase()).contains(protocol.toLowerCase()))
      throw new NoSuchAlgorithmException(s"Protocol ${protocol} not supported")
    SSLContext.getInstance(protocol)

  def getInstance(protocol: String, provider: String): SSLContext =
    throw new UnsupportedOperationException("Not supported in Scala Native yet")

  def getInstance(protocol: String, provider: Provider): SSLContext =
    requireNonNull(protocol)
    requireNonNull(provider)
    // require(protocol.nonEmpty)
    ???

end SSLContext
