/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.net.ssl

import java.security.{SecureRandom, Provider}
import java.security.{KeyManagementException, NoSuchAlgorithmException}
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

import scala.collection.immutable.TreeSet
import scala.math.Ordering.comparatorToOrdering
import scala.scalanative.unsafe.{Ptr, fromCString}

import snhttp.experimental.openssl.libssl
import snhttp.experimental.openssl.libssl.{SSL_CTRL, SSL_VERIFY, TLS_VERSION, SSL_OP}
import snhttp.utils.PointerCleaner

private[snhttp] class SSLContextImpl(
    spi: SSLContextSpiImpl,
    provider: Provider,
    protocol: String,
) extends SSLContext(spi, provider, protocol):
  private[snhttp] val ptr = spi.ptr
end SSLContextImpl

private[snhttp] class SSLContextSpiImpl(protocol: String) extends SSLContextSpi():

  import SSLContextSpiImpl.{
    SUPPORTED_PROTOCOLS,
    ALL_SSL_CONTEXT_PROTOCOLS,
    SSL_OP_PROTOCOL_MASK_MAP,
  }

  private val tlsVersionPtr =
    if (SUPPORTED_PROTOCOLS.contains(protocol)) {
      libssl.TLS_method()
    } else
      throw new NoSuchAlgorithmException(
        s"Unsupported protocol: ${protocol}. Only TLSv1.2 and TLSv1.3 are supported.",
      )
  private[ssl] val ptr: Ptr[libssl.SSL_CTX] = libssl.SSL_CTX_new(tlsVersionPtr)
  if (ptr == null)
    throw new RuntimeException("Failed to create SSL_CTX for SSLContextSpiImpl")
  PointerCleaner.register(this, ptr, _ptr => libssl.SSL_CTX_free(_ptr)): Unit

  val minProtoRet = libssl.SSL_CTX_set_min_proto_version(ptr, TLS_VERSION.TLS1_2)
  if (minProtoRet != 1)
    throw new RuntimeException(
      "Failed to set minimul protocol version to TLSv1.2 for SSLContextSpiImpl when required protocol is TLSv1.2",
    )
  if (protocol.equalsIgnoreCase("TLSv1.2")) {
    val ret = libssl.SSL_CTX_set_max_proto_version(ptr, TLS_VERSION.TLS1_2)
    if (ret != 1)
      throw new RuntimeException(
        "Failed to set maximum protocol version to TLSv1.2 for SSLContextSpiImpl when required protocol is TLSv1.2",
      )
  }

  /**
   * Security level set to 192 bits of security. As a result RSA, DSA and DH keys shorter than 7680
   * bits and ECC keys shorter than 384 bits are prohibited. Cipher suites using SHA1 for the MAC
   * are prohibited.
   */
  if (libssl.SSL_CTX_get_security_level(ptr) < 4)
    libssl.SSL_CTX_set_security_level(ptr, 4)

  // Client session context bind to current SSLContext
  // only has one ClientSessionContext instance per SSLContext instance
  protected[ssl] val clientSessionContext = ClientSessionContextImpl(this)
  protected[ssl] val sslSocketFactory = SSLSocketFactoryImpl(this)

  private val inited: AtomicBoolean = new AtomicBoolean(false)

  /**
   * Notes from JDK docs:
   *
   *   1. Either of the first two parameters may be null in which case the installed security
   *      providers will be searched for the highest priority implementation of the appropriate
   *      factory.
   *   2. Likewise, the secure random parameter may be null in which case the default implementation
   *      will be used.
   *   3. Only the first instance of a particular key and/or trust manager implementation type in
   *      the array is used.
   */
  def engineInit(
      km: Array[KeyManager],
      tm: Array[TrustManager],
      sr: SecureRandom,
  ): Unit =
    if (!inited.compareAndExchange(false, true)) {
      // FIXME: Do nothing now
      ()
    } else {
      throw new KeyManagementException("SSLContext already initialized")
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
  override def engineGetDefaultSSLParameters(): SSLParameters = {
    // Mimic a SSL to get the default SSL parameters via `SSL_get1_supported_ciphers`.
    // https://docs.openssl.org/3.3/man3/SSL_get_ciphers/
    //
    // Don't forget also:
    //
    // The stack returned by `SSL_get1_supported_ciphers()` should be freed using `sk_SSL_CIPHER_free()`.
    val ssl = libssl.SSL_new(ptr)
    if (ssl == null)
      throw new RuntimeException(
        "Failed to create SSL for SSLContextSpiImpl when getting default SSL parameters",
      )

    val stackOfCiphers = libssl.SSL_get1_supported_ciphers(ssl)
    if (stackOfCiphers == null)
      throw new RuntimeException(
        "Failed to get default SSL parameters for SSLContextSpiImpl: `SSL_get1_supported_ciphers` returned null",
      )
    val numCiphers = libssl.snhttp_ossl_sk_SSL_CIPHER_num(stackOfCiphers)

    var sslParameters: SSLParameters = null
    try {
      val cipherSuites = Array(
        (0 until numCiphers).map { i =>
          val cipher = libssl.snhttp_ossl_sk_SSL_CIPHER_value(stackOfCiphers, i)
          fromCString(libssl.SSL_CIPHER_standard_name(cipher))
        }*,
      )

      sslParameters = new SSLParametersImpl(cipherSuites, getDefaultProtocols())
    } finally {
      libssl.snhttp_ossl_sk_SSL_CIPHER_free(stackOfCiphers)
      libssl.SSL_free(ssl)
    }

    sslParameters
  }

  // @since JDK 1.6
  override def engineGetSupportedSSLParameters(): SSLParameters = {
    val stackOfCiphers = libssl.SSL_CTX_get_ciphers(ptr)
    if (stackOfCiphers == null)
      throw new RuntimeException(
        "Failed to get supported SSL parameters for SSLContextSpiImpl: `SSL_CTX_get_ciphers` returned null",
      )
    val numCiphers = libssl.snhttp_ossl_sk_SSL_CIPHER_num(stackOfCiphers)

    val cipherSuites = Array(
      (0 until numCiphers).map { i =>
        val cipher = libssl.snhttp_ossl_sk_SSL_CIPHER_value(stackOfCiphers, i)
        fromCString(libssl.SSL_CIPHER_standard_name(cipher))
      }*,
    )

    new SSLParametersImpl(cipherSuites, getSupportedProtocols())
  }

  private def getDefaultProtocols(): Array[String] = {
    val _min = libssl.SSL_CTX_get_min_proto_version(ptr).toInt
    val _max = libssl.SSL_CTX_get_max_proto_version(ptr).toInt
    val min = if _min == 0 then 0x0300 else _min
    val max = if _max == 0 then 0x0400 else _max

    val supported = ALL_SSL_CONTEXT_PROTOCOLS
      .filter(v => v.value >= min && v.value <= max)
      .map(TLS_VERSION.getJavaStandardName(_))
      .toArray

    supported
  }

  private def getSupportedProtocols(): Array[String] =
    getEnabledProtocols()
      .map(TLS_VERSION.getJavaStandardName(_))
      .toArray

  private def getEnabledProtocols(): Seq[TLS_VERSION] = {
    val mask = libssl.SSL_CTX_get_options(ptr)
    val enabled =
      ALL_SSL_CONTEXT_PROTOCOLS
        .filter(v => (mask.value & SSL_OP_PROTOCOL_MASK_MAP(v).value) == 0)
    enabled
  }

object SSLContextSpiImpl:

  final val SUPPORTED_PROTOCOLS = TreeSet(
    "TLSv1.2",
    "TLSv1.3",
    "TLS",
    "Default",
  )(using comparatorToOrdering(String.CASE_INSENSITIVE_ORDER))

  final val ALL_SSL_CONTEXT_PROTOCOLS = Seq(
    TLS_VERSION.SSL3,
    TLS_VERSION.TLS1,
    TLS_VERSION.TLS1_1,
    TLS_VERSION.TLS1_2,
    TLS_VERSION.TLS1_3,
  )
  final val SSL_OP_PROTOCOL_MASK_MAP = Map(
    TLS_VERSION.SSL3 -> SSL_OP.NO_SSLv3,
    TLS_VERSION.TLS1 -> SSL_OP.NO_TLSv1,
    TLS_VERSION.TLS1_1 -> SSL_OP.NO_TLSv1_1,
    TLS_VERSION.TLS1_2 -> SSL_OP.NO_TLSv1_2,
    TLS_VERSION.TLS1_3 -> SSL_OP.NO_TLSv1_3,
  )

end SSLContextSpiImpl
