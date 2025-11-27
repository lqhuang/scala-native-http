package javax.net.ssl

import java.security.AlgorithmConstraints
import java.util.List as JList
import java.util.Collection
import java.util.Objects.requireNonNull

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLParameters.html
abstract class SSLParameters(
    private var _cipherSuites: Array[String],
    private var _protocols: Array[String],
):

  final def getCipherSuites(): Array[String] =
    _cipherSuites

  final def setCipherSuites(cipherSuites: Array[String]): Unit =
    _cipherSuites = cipherSuites

  final def getProtocols(): Array[String] =
    _protocols

  final def setProtocols(protocols: Array[String]): Unit =
    _protocols = protocols

  def getWantClientAuth(): Boolean

  def setWantClientAuth(wantClientAuth: Boolean): Unit

  def getNeedClientAuth(): Boolean

  def setNeedClientAuth(needClientAuth: Boolean): Unit

  def getAlgorithmConstraints(): AlgorithmConstraints

  def setAlgorithmConstraints(constraints: AlgorithmConstraints): Unit

  def getEndpointIdentificationAlgorithm(): String

  def setEndpointIdentificationAlgorithm(algorithm: String): Unit

  def setServerNames(serverNames: JList[SNIServerName]): Unit

  def getServerNames(): JList[SNIServerName]

  def setSNIMatchers(matchers: Collection[SNIMatcher]): Unit

  // final
  def getSNIMatchers(): Collection[SNIMatcher]

  // final
  def setUseCipherSuitesOrder(honorOrder: Boolean): Unit

  // final
  def getUseCipherSuitesOrder(): Boolean

  def setEnableRetransmissions(enableRetransmissions: Boolean): Unit

  def getEnableRetransmissions(): Boolean

  def setMaximumPacketSize(maximumPacketSize: Int): Unit

  def getMaximumPacketSize(): Int

  def getApplicationProtocols(): Array[String]

  def setApplicationProtocols(protocols: Array[String]): Unit

  def getSignatureSchemes(): Array[String]

  def setSignatureSchemes(signatureSchemes: Array[String]): Unit

  def getNamedGroups(): Array[String]

  def setNamedGroups(namedGroups: Array[String]): Unit
