package javax.net.ssl

import java.security.AlgorithmConstraints
import java.util.List as JList
import java.util.{Collection, Collections}
import java.util.Objects.requireNonNull

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLParameters.html
class SSLParameters(
    private var _cipherSuites: Array[String],
    private var _protocols: Array[String],
) {

  private var _wantClientAuth: Boolean = false
  private var _needClientAuth: Boolean = false
  private var _identificationAlgorithm: String = null
  private var _algorithmConstraints: AlgorithmConstraints = null
  private var _sniNames: JList[SNIServerName] = null
  private var _sniMatchers: Collection[SNIMatcher] = null
  private var _preferLocalCipherSuites: Boolean = false
  private var _enableRetransmissions: Boolean = true
  private var _maximumPacketSize: Int = 0
  private var _applicationProtocols: Array[String] = Array.empty[String]
  private var _signatureSchemes: Array[String] = null
  private var _namedGroups: Array[String] = null

  def getCipherSuites(): Array[String] = _cipherSuites

  def setCipherSuites(cipherSuites: Array[String]): Unit =
    _cipherSuites = cipherSuites

  def getProtocols(): Array[String] = _protocols

  def setProtocols(protocols: Array[String]): Unit = _protocols = protocols

  def getWantClientAuth(): Boolean = _wantClientAuth

  def setWantClientAuth(wantClientAuth: Boolean): Unit = {
    _wantClientAuth = wantClientAuth
    _needClientAuth = false
  }

  def getNeedClientAuth(): Boolean = _needClientAuth

  def setNeedClientAuth(needClientAuth: Boolean): Unit = {
    _wantClientAuth = false
    _needClientAuth = needClientAuth
  }

  def getAlgorithmConstraints(): AlgorithmConstraints = _algorithmConstraints

  def setAlgorithmConstraints(constraints: AlgorithmConstraints): Unit =
    _algorithmConstraints = constraints

  def getEndpointIdentificationAlgorithm(): String = _identificationAlgorithm

  def setEndpointIdentificationAlgorithm(algorithm: String): Unit =
    _identificationAlgorithm = algorithm

  def setServerNames(serverNames: JList[SNIServerName]): Unit = {
    serverNames.forEach { sn =>
      require(
        sn != null && sn.getType() >= 0,
        "Any element of non-empty list must not be null and have a valid type",
      )
    }

    if (serverNames == null)
      _sniNames = null
    else if (serverNames.isEmpty) _sniNames = Collections.emptyList()
    else ???
  }

  def getServerNames(): JList[SNIServerName] = _sniNames

  def setSNIMatchers(matchers: Collection[SNIMatcher]): Unit = {
    requireNonNull(matchers)

    if _sniMatchers == null && matchers != null
    then _sniMatchers = matchers

    ???
  }

  final def getSNIMatchers(): Collection[SNIMatcher] = _sniMatchers

  final def setUseCipherSuitesOrder(honorOrder: Boolean): Unit =
    _preferLocalCipherSuites = honorOrder

  final def getUseCipherSuitesOrder(): Boolean = _preferLocalCipherSuites

  def setEnableRetransmissions(enableRetransmissions: Boolean): Unit =
    _enableRetransmissions = enableRetransmissions

  def getEnableRetransmissions(): Boolean = _enableRetransmissions

  def setMaximumPacketSize(maximumPacketSize: Int): Unit =
    require(maximumPacketSize >= 0, "Maximum packet size must be non-negative")
    _maximumPacketSize = maximumPacketSize

  def getMaximumPacketSize(): Int = _maximumPacketSize

  def getApplicationProtocols(): Array[String] = _applicationProtocols.clone()

  def setApplicationProtocols(protocols: Array[String]): Unit = {
    requireNonNull(protocols)
    require(
      protocols.forall(p => p != null && p.nonEmpty),
      "Any element of non-empty array must not be null or empty string",
    )
    ???
  }

  def getSignatureSchemes(): Array[String] = _signatureSchemes.clone()

  def setSignatureSchemes(signatureSchemes: Array[String]): Unit = ???

  def getNamedGroups(): Array[String] = ???

  def setNamedGroups(namedGroups: Array[String]): Unit = ???
}
