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

  private var _serverNames: JList[SNIServerName] = null
  private var _honorOrder: Boolean = false

  def getCipherSuites(): Array[String] =
    _cipherSuites

  def setCipherSuites(cipherSuites: Array[String]): Unit =
    _cipherSuites = cipherSuites

  def getProtocols(): Array[String] =
    _protocols

  def setProtocols(protocols: Array[String]): Unit =
    _protocols = protocols

  def getWantClientAuth(): Boolean

  def setWantClientAuth(wantClientAuth: Boolean): Unit

  def getNeedClientAuth(): Boolean

  def setNeedClientAuth(needClientAuth: Boolean): Unit

  def getAlgorithmConstraints(): AlgorithmConstraints

  def setAlgorithmConstraints(constraints: AlgorithmConstraints): Unit

  def getEndpointIdentificationAlgorithm(): String

  def setEndpointIdentificationAlgorithm(algorithm: String): Unit

  final def setServerNames(serverNames: JList[SNIServerName]): Unit =
    if serverNames == null
    then _serverNames = null
    else
      // `serverNames` can be null but if not null, validate its elements
      if (serverNames.stream().allMatch(sn => sn != null && sn.getType() >= 0))
        throw new NullPointerException(
          "'serverNames' contains null element",
        )
      val uniqueCountOfNames = serverNames.stream().map(_.getType()).distinct().count()
      if (uniqueCountOfNames != serverNames.size())
        throw new IllegalArgumentException(
          "'serverNames' contains element with invalid type",
        )
      _serverNames = serverNames.stream().map(sn => sn.clone()).toList()

  final def getServerNames(): JList[SNIServerName] =
    _serverNames

  /// Since JDK doc says:
  /// This method is only useful to SSLSockets or SSLEngines operating in server mode.
  ///
  /// We currently focus on client mode, so we leave its implementation blank for now.
  /// Welcome to contribute.
  final def setSNIMatchers(matchers: Collection[SNIMatcher]): Unit =
    ???

  /// Since JDK doc says:
  /// This method is only useful to SSLSockets or SSLEngines operating in server mode.
  ///
  /// We currently focus on client mode, so we leave its implementation blank for now.
  /// Welcome to contribute.
  final def getSNIMatchers(): Collection[SNIMatcher] =
    ???

  final def setUseCipherSuitesOrder(honorOrder: Boolean): Unit =
    _honorOrder = honorOrder

  final def getUseCipherSuitesOrder(): Boolean =
    _honorOrder

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
