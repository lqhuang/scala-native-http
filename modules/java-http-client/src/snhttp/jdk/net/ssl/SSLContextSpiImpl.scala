package snhttp.jdk.net.ssl

import java.security.{SecureRandom, Provider}
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.{
  SSLContext,
  SSLContextSpi,
  SSLParameters,
  KeyManager,
  SSLSessionContext,
  SSLSocketFactory,
  SSLEngine,
  TrustManager,
  SSLServerSocketFactory,
}

private[snhttp] class SSLContextImpl(
    private val provider: Provider,
    private val protocol: String,
) extends SSLContext(new SSLContextSpiImpl(), provider, protocol)

private[snhttp] class SSLContextSpiImpl() extends SSLContextSpi():

  // Client session context bind to current SSLContext
  // only has one ClientSessionContext instance per SSLContext instance
  protected[ssl] val clientSessionContext = ClientSessionContextImpl(this)
  protected[ssl] val sslSocketFactory = SSLSocketFactoryImpl(this)

  private val inited: AtomicBoolean = new AtomicBoolean(false)

  def engineInit(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      sr: SecureRandom,
  ): Unit =
    if (!inited.compareAndExchange(false, true)) {
      // Do nothing now
      ()
    } else {
      throw new IllegalStateException("SSLContext already initialized")
    }

  def engineCreateSSLEngine(): SSLEngine =
    throw new NotImplementedError("server side feature is not supported yet")

  def engineCreateSSLEngine(host: String, port: Int): SSLEngine =
    ClientSSLEngineImpl(this, host, port)

  def engineGetServerSocketFactory(): SSLServerSocketFactory =
    throw new NotImplementedError("server side feature is not supported yet")

  def engineGetServerSessionContext(): SSLSessionContext =
    throw new NotImplementedError("server side feature is not supported yet")

  def engineGetSocketFactory(): SSLSocketFactory =
    sslSocketFactory

  def engineGetClientSessionContext(): SSLSessionContext =
    clientSessionContext

  // @since JDK 1.6
  override def engineGetDefaultSSLParameters(): SSLParameters =
    SSLParametersImpl.getDefault()

  // @since JDK 1.6
  override def engineGetSupportedSSLParameters(): SSLParameters =
    SSLParametersImpl.getSupported()
