package snhttp.jdk.internal

import snhttp.tls.params.CipherSuite

case class CipherSuites()

object CipherSuites {

  lazy val defaultCipherSuites: Set[String] =
    (
      CipherSuite.TLS_v1_3_ALL_SUPPORTED_CIPHER_SUITES
        & CipherSuite.TLS_v1_2_SUPPORTED_CIPHER_SUITES
    ).map(_.name)

  lazy val supportedCipherSuites: Set[String] =
    CipherSuite.ALL_SUPPORTED_CIPHER_SUITES.map(_.name)

}
