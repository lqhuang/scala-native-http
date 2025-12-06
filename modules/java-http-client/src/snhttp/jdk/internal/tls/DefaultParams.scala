package snhttp.jdk.internal.tls

import snhttp.experimental.tls.extensions.ALPNProtocol
import snhttp.experimental.tls.params.{CipherSuite, ProtocolVersion, SignatureScheme}
import snhttp.experimental.tls.extensions.ALPNProtocol

object DefaultParams:

  lazy val DefaultProtocols: Set[String] =
    ProtocolVersion.DefaultProtocols
      .map(_.name)

  lazy val SupportedProtocols: Set[String] =
    ProtocolVersion.SupportedProtocols
      .map(_.name)

  lazy val DefaultCipherSuites: Set[String] =
    (
      CipherSuite.TLS_v1_3_ALL_SUPPORTED_CIPHER_SUITES
        & CipherSuite.TLS_v1_2_SUPPORTED_CIPHER_SUITES
    ).map(_.name)

  lazy val SupportedCipherSuites: Set[String] =
    CipherSuite.ALL_SUPPORTED_CIPHER_SUITES.map(_.name)

  lazy val DefaultApplicationProtocols: List[String] =
    ALPNProtocol.DefaultProtocols.map(_.name)

  lazy val SupportedApplicationProtocols: Set[String] =
    ALPNProtocol.SupportedProtocols.map(_.name)

  def getDefaultCipherSuites(): Array[String] =
    DefaultParams.DefaultCipherSuites.toArray

  def getSupportedCipherSuites(): Array[String] =
    DefaultParams.SupportedCipherSuites.toArray

  def getDefaultProtocols(): Array[String] =
    DefaultParams.DefaultProtocols.toArray

  def getSupportedProtocols(): Array[String] =
    DefaultParams.SupportedProtocols.toArray

  def getDefaultApplicationProtocols(): Array[String] =
    DefaultParams.DefaultApplicationProtocols.toArray

  def getSupportedApplicationProtocols: Array[String] =
    DefaultParams.SupportedApplicationProtocols.toArray
