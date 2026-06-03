package snhttp.jdk.net.ssl

import java.net.Socket
import java.security.{KeyStore, Principal, PrivateKey}
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.Objects.requireNonNull
import javax.net.ssl.X509KeyManager

import scala.jdk.CollectionConverters.ListHasAsScala
import scala.scalanative.unsafe.{Ptr, fromCString}

import com.github.lolgab.scalanativecrypto.crypto.OpenSSLKeyStore

import snhttp.experimental.openssl.libssl.SSL_CTX
import snhttp.experimental.openssl.libcrypto.{X509, EVP_PKEY, stack_st_X509}
import snhttp.experimental.openssl.{libssl, libcrypto}

private[snhttp] class X509KeyManagerImpl(ks: OpenSSLKeyStore, password: Array[Char])
    extends X509KeyManager:

  private val _passwd = password.clone()

  def getClientAliases(keyType: String, issuers: Array[Principal]): Array[String] =
    getAliases(keyType, issuers)

  def chooseClientAlias(keyType: Array[String], issuers: Array[Principal], socket: Socket): String =
    ???

  def getServerAliases(keyType: String, issuers: Array[Principal]): Array[String] =
    getAliases(keyType, issuers)

  def chooseServerAlias(keyType: String, issuers: Array[Principal], socket: Socket): String =
    ???

  def getCertificateChain(alias: String): Array[X509Certificate] =
    ks.getCertificateChain(alias).map(_.asInstanceOf[X509Certificate])

  def getPrivateKey(alias: String): PrivateKey =
    ks.getKey(alias, _passwd).asInstanceOf[PrivateKey]

  /*
   * Extra methods only expose to snhttp
   */

  def registerToSSLContext(sslCtxPtr: Ptr[SSL_CTX]): Unit = {
    val ret = libssl.SSL_CTX_use_cert_and_key(
      sslCtxPtr,
      ks.cert.asInstanceOf[Ptr[X509]],
      ks.pkey.asInstanceOf[Ptr[EVP_PKEY]],
      ks.stackOfCA.asInstanceOf[Ptr[stack_st_X509]],
      0,
    )
    if (ret != 1)
      val code = libcrypto.ERR_get_error()
      val msg = fromCString(libcrypto.ERR_error_string(code, null))
      throw new RuntimeException(s"Failed to register X509KeyManager to SSL_CTX: ${msg}")
  }

  /*
   * private helper methods
   */

  private final inline def getAliases(keyType: String, issuers: Array[Principal]): Array[String] = {
    requireNonNull(keyType)
    require(keyType.nonEmpty)

    val matched = Collections
      .list(ks.aliases())
      .stream()
      .filter(alias =>
        if issuers == null || issuers.isEmpty
        then true
        else {
          val cert = ks.getCertificate(alias)
          val principal = cert.asInstanceOf[X509Certificate].getIssuerX500Principal()
          issuers.find(p => p.getName() == principal.getName()).isDefined
        },
      )
      .toList()

    if matched.isEmpty
    then null
    else matched.asScala.toArray
  }
