package snhttp.jdk.internal.tls

import snhttp.tls.extensions.ALPNProtocol
import snhttp.tls.params.{CipherSuite, ProtocolVersion, SignatureScheme}

object ParamsData:

  lazy val DefaultProtocols: Array[String] =
    ProtocolVersion.DefaultProtocols
      .map(_.name)
      .toArray

  lazy val SupportedProtocols: Array[String] =
    ProtocolVersion.SupportedProtocols
      .map(_.name)
      .toArray

  lazy val DefaultCipherSuites: Array[String] =
    (
      CipherSuite.TLS_v1_3_ALL_SUPPORTED_CIPHER_SUITES
        & CipherSuite.TLS_v1_2_SUPPORTED_CIPHER_SUITES
    ).map(_.name).toArray

  lazy val SupportedCipherSuites: Array[String] =
    CipherSuite.ALL_SUPPORTED_CIPHER_SUITES.map(_.name).toArray

  lazy val DefaultApplicationProtocols: Array[String] =
    ALPNProtocol.DefaultProtocols
      .map(_.name)
      .toArray

  lazy val supportedApplicationProtocols: Array[String] =
    ALPNProtocol.SupportedProtocols
      .map(_.name)
      .toArray
