package javax.net.ssl

import java.security.{SecureRandom, Provider}
import java.security.NoSuchAlgorithmException
import java.util.Objects.requireNonNull

import snhttp.jdk.internal.tls.OpenSSLProvider

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

  @volatile private var defaultContext: SSLContext = {
    val ssl = SSLContext.getInstance("TLS")
    ssl.init(null, null, null)
    ssl
  }

  def getDefault(): SSLContext =
    defaultContext

  def setDefault(context: SSLContext): Unit =
    requireNonNull(context)
    defaultContext = context

  def getInstance(protocol: String): SSLContext =
    requireNonNull(protocol)
    getInstance(protocol, OpenSSLProvider.defaultInstance)

  def getInstance(protocol: String, provider: String): SSLContext =
    throw new UnsupportedOperationException("Not supported in Scala Native yet")

  def getInstance(protocol: String, provider: Provider): SSLContext = {
    requireNonNull(protocol)
    requireNonNull(provider)
    require(protocol.nonEmpty)

    provider.getService("SSLContext", protocol) match
      case null =>
        throw new NoSuchAlgorithmException(
          s"Protocol ${protocol} for SSLContext service is not found in provider ${provider.getName()}",
        )
      case service =>
        service.newInstance(null) match
          case sslContext: SSLContext => sslContext
          case _ =>
            throw new NoSuchAlgorithmException(
              s"Service provider ${service.getClassName()} returned an object that is not an instance of SSLContext",
            )
  }

end SSLContext
