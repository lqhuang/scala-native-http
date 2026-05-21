package snhttp.jdk.internal

import java.lang.String.CASE_INSENSITIVE_ORDER
import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}
import java.security.KeyStore
import java.util.function.BiPredicate

import scala.collection.immutable.TreeSet
import scala.math.Ordering.comparatorToOrdering
import scala.util.Properties

import scala.scalanative.unsafe.fromCString

import snhttp.experimental.openssl.libcrypto

/**
 * To check semantics and default value of various JDK properties, see
 * https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/module-summary.html
 */
object PropertyUtils {

  val strCIOrdering: Ordering[String] = comparatorToOrdering(CASE_INSENSITIVE_ORDER)

  val DEFAULT_DISALLOWED_HEADERS: TreeSet[String] =
    TreeSet.from(
      Seq("connection", "content-length", "expect", "host", "upgrade"),
    )(using strCIOrdering)

  def getRestrictedHeaders(): TreeSet[String] =
    Properties
      .propOrNone("jdk.httpclient.allowRestrictedHeaders")
      .flatMap(opts =>
        val hs = opts.split(",").map(_.trim).toSeq
        if hs.isEmpty
        then None
        else Some(DEFAULT_DISALLOWED_HEADERS -- hs),
      )
      .getOrElse(DEFAULT_DISALLOWED_HEADERS)

  val restrictedHeaders = getRestrictedHeaders()
  def allowedHeadersPredicate(name: String, value: String): Boolean =
    !restrictedHeaders.contains(name)

    /**
     * default buffer size to use for HTTP requests and responses.
     */
  final val DEFAULT_INTERNAL_BUFFER_SIZE: Int = 64 * 1024; // 64 KB
  val INTERNAL_BUFFER_SIZE =
    Properties
      .propOrNone("jdk.httpclient.bufsize")
      .flatMap(size => size.trim().toIntOption)
      .filter(s => s >= 16 * 1024 && s <= 2 * 1024 * 1024) // [16 KB, 2 MB] inclusive
      .getOrElse(DEFAULT_INTERNAL_BUFFER_SIZE)
  def newFixedByteBuffer() = ByteBuffer.allocate(INTERNAL_BUFFER_SIZE)

  // https://curl.se/libcurl/c/CURLOPT_BUFFERSIZE.html
  final val DEFAULT_RECEIVE_BUFFER_SIZE: Int = 64 * 1024; // 64 KB, default 16 kb in curl
  val RECEIVE_BUFFER_SIZE =
    Properties
      .propOrNone("jdk.httpclient.receiveBufferSize")
      .flatMap(size => size.trim().toIntOption)
      .filter(s => s >= 1 * 1024 && s <= 10 * 1024 * 1024) // [1 kB, 10 MB] inclusive
      .getOrElse(DEFAULT_RECEIVE_BUFFER_SIZE)

  // https://curl.se/libcurl/c/CURLOPT_UPLOAD_BUFFERSIZE.html
  final val DEFAULT_SEND_BUFFER_SIZE: Int = 64 * 1024; // default 64 kb in curl
  val SEND_BUFFER_SIZE =
    Properties
      .propOrNone("jdk.httpclient.sendBufferSize")
      .flatMap(size => size.trim().toIntOption)
      .filter(s => s >= 16 * 1024 && s <= 2 * 1024 * 1024) // [16 KB, 2 MB] inclusive
      .getOrElse(DEFAULT_SEND_BUFFER_SIZE)

  final val DEFAULT_SESSION_CACHE_SIZE = 20480
  val SESSION_CACHE_SIZE: Int =
    Properties
      .propOrNone("jdk.httpclient.sessionCacheSize")
      .flatMap(size => size.trim().toIntOption)
      .getOrElse(20480)

  // scalafmt: { maxColumn = 150 }

  // inspired by https://github.com/sethmlarson/truststore/blob/main/src/truststore/_openssl.py
  private val _CA_FILE_CANDIDATES = Seq(
    // Alpine, Arch, Fedora 34-43, OpenWRT, RHEL 9-10, BSD, macOS
    "/etc/ssl/cert.pem",
    // Debian, Ubuntu (requires ca-certificates)
    "/etc/ssl/certs/ca-certificates.crt",
    // Fedora 44+, RHEL 11+
    "/etc/pki/ca-trust/extracted/pem/tls-ca-bundle.pem",
    // SUSE
    "/etc/ssl/ca-bundle.pem",
    // Fedora <= 34, RHEL <= 9, CentOS <= 9
    "/etc/pki/tls/cert.pem",
  )

  def findCertFileFromCandidates(): Option[String] =
    _CA_FILE_CANDIDATES.find(path => Files.isRegularFile(Paths.get(path)))

  val defaultX509TrustStore: String = {
    val envName = fromCString(libcrypto.X509_get_default_cert_file_env())
    val certFile = Properties.envOrElse(envName, fromCString(libcrypto.X509_get_default_cert_file()))

    if (Files.isRegularFile(Paths.get(certFile)))
      certFile
    else
      findCertFileFromCandidates().getOrElse(
        throw new RuntimeException(
          s"Failed to find the default CA certificate file. Please specify the path to CA certificates using the `javax.net.ssl.trustStore` system property.",
        ),
      )
  }

  // libcrypto.X509_get_default_cert_dir()
  // libcrypto.X509_get_default_cert_dir_env()

  /*
   *   - `-Djavax.net.ssl.keyStore` specifies the keystore file.
   *   - `-Djavax.net.ssl.keyStorePassword` specifies the passphrase of the keystore.
   *   - `-Djavax.net.ssl.trustStore` specifies the truststore file to use to validate client
   *     certificates.
   *   - `-Djavax.net.ssl.trustStorePassword` specifies the passphrase to access the truststore
   *     file.
   */
  val keyStoreProp = Properties.propOrEmpty("javax.net.ssl.keyStore")
  val keyStoreTypeProp = Properties.propOrElse("javax.net.ssl.keyStoreType", KeyStore.getDefaultType())
  val keyStorePasswdProp = Properties.propOrEmpty("javax.net.ssl.keyStorePassword")

  val trustStoreProp = Properties.propOrElse("javax.net.ssl.trustStore", defaultX509TrustStore)
  val trustStoreTypeProp = Properties.propOrElse("javax.net.ssl.trustStoreType", KeyStore.getDefaultType())
  val trustStorePasswordProp = Properties.propOrEmpty("javax.net.ssl.trustStorePassword")

  // val keyStoreProviderProp = Properties.propOrNone("javax.net.ssl.keyStoreProvider")
  // val trustStoreProviderProp = Properties.propOrNone("javax.net.ssl.trustStoreProvider")

  // scalafmt: { maxColumn = 80 }

}
