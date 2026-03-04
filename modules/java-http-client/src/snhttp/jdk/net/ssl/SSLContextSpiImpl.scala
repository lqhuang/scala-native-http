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

import scala.scalanative.unsafe.{Ptr, fromCString}

import snhttp.experimental.openssl.libssl
import snhttp.experimental.openssl._libssl.enumerations.{SSL_CTRL, SSL_VERIFY, TLS_VERSION}
import snhttp.utils.PointerCleaner

private[snhttp] class SSLContextImpl(
    private val provider: Provider,
    private val protocol: String,
) extends SSLContext(new SSLContextSpiImpl(protocol), provider, protocol)

private[snhttp] class SSLContextSpiImpl(protocol: String) extends SSLContextSpi():

  private val supportedProtocols = Array("TLSv1.2", "TLSv1.3")

  private val tlsVersionPtr =
    if (protocol.equalsIgnoreCase("TLSv1.2")) {
      libssl.TLSv1_2_method()
    } else if (protocol.equalsIgnoreCase("TLSv1.3")) {
      libssl.TLS_method()
    } else
      throw new NoSuchAlgorithmException(
        s"Unsupported protocol: ${protocol}. Only TLSv1.2 and TLSv1.3 are supported.",
      )
  private[ssl] val ptr: Ptr[libssl.SSL_CTX] = libssl.SSL_CTX_new(tlsVersionPtr)
  if (ptr == null)
    throw new RuntimeException("Failed to create SSL_CTX for SSLContextSpiImpl")
  PointerCleaner.register(this, ptr, _ptr => libssl.SSL_CTX_free(_ptr)): Unit

  if (protocol.equalsIgnoreCase("TLSv1.3")) {
    val ret = libssl.SSL_CTX_set_min_proto_version(ptr, TLS_VERSION.TLS1_3)
    if (ret != 1)
      throw new RuntimeException(
        "Failed to set minimum protocol version to TLSv1.3 for SSLContextSpiImpl when protocol is TLSv1.3",
      )
  }

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

      sslParameters = new SSLParametersImpl(cipherSuites, supportedProtocols)
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

    new SSLParametersImpl(cipherSuites, supportedProtocols)
  }

end SSLContextSpiImpl
