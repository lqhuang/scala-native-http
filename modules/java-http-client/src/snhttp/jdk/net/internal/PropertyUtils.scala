package snhttp.jdk.net.internal

import java.nio.file.{Files, Paths}
import java.security.KeyStore

import scala.util.Properties

import scala.scalanative.unsafe.fromCString

import snhttp.experimental.openssl.libcrypto

private[snhttp] object PropertyUtils {

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
