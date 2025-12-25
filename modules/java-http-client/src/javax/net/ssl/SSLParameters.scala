package javax.net.ssl

import java.security.AlgorithmConstraints
import java.util.List as JList
import java.util.Collection
import java.util.Objects.requireNonNull

import snhttp.jdk.internal.tls.DefaultParams

/**
 * Refs
 *
 *   - <https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLParameters.html>
 */
class SSLParameters(cipherSuites: Array[String], protocols: Array[String]):

  def this() = this(null, null)

  def this(cipherSuites: Array[String]) =
    this(cipherSuites, null)

  cipherSuites.find(cs => cs == null || cs.isEmpty()) match
    case Some(value) =>
      throw new IllegalArgumentException(
        "Any element of non-empty array must not be null or empty string",
      )
    case None => ()
  protocols.find(p => p == null || p.isEmpty()) match
    case Some(value) =>
      throw new IllegalArgumentException(
        "Any element of non-empty array must not be null or empty string",
      )
    case None => ()

  private var _cipherSuites: Array[String] = cipherSuites.clone()
  private var _protocols: Array[String] = protocols.clone()

  private var _serverNames: JList[SNIServerName] = null
  private var _honorOrder: Boolean = false

  // if the peer with this parameters tuned to require client authentication
  private var _wantClientAuth: Boolean = false
  private var _needClientAuth: Boolean = false

  // Endpoint identification algorithm (e.g., HTTPS)
  private var _identificationAlgorithm: String = null
  private var _algorithmConstraints: AlgorithmConstraints = null

  private var _enableRetransmissions: Boolean = true
  private var _maximumPacketSize: Int = 0
  private var _applicationProtocols: Array[String] =
    DefaultParams.DefaultApplicationProtocols.toArray
  private var _signatureSchemes: Array[String] = null
  private var _namedGroups: Array[String] = null

  def getCipherSuites(): Array[String] =
    _cipherSuites

  def setCipherSuites(cipherSuites: Array[String]): Unit =
    _cipherSuites = cipherSuites

  def getProtocols(): Array[String] =
    _protocols

  def setProtocols(protocols: Array[String]): Unit =
    _protocols = protocols

  def getWantClientAuth(): Boolean =
    _wantClientAuth

  /// Note from JDK docs:
  /// Calling this method clears the `needClientAuth` flag
  def setWantClientAuth(wantClientAuth: Boolean): Unit =
    requireNonNull(wantClientAuth)
    _wantClientAuth = wantClientAuth
    _needClientAuth = false

  def getNeedClientAuth(): Boolean =
    _needClientAuth

  /// Note from JDK docs:
  /// Calling this method clears the `wantClientAuth` flag.
  def setNeedClientAuth(needClientAuth: Boolean): Unit =
    requireNonNull(needClientAuth)
    _needClientAuth = needClientAuth
    _wantClientAuth = false

  def getAlgorithmConstraints(): AlgorithmConstraints =
    _algorithmConstraints

  def setAlgorithmConstraints(constraints: AlgorithmConstraints): Unit =
    requireNonNull(constraints)
    _algorithmConstraints = constraints

  def getEndpointIdentificationAlgorithm(): String =
    _identificationAlgorithm

  def setEndpointIdentificationAlgorithm(algorithm: String): Unit =
    requireNonNull(algorithm)
    require(!algorithm.isBlank() && !algorithm.isEmpty())
    _identificationAlgorithm = algorithm

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
      _serverNames = JList.copyOf(serverNames)

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

  def setEnableRetransmissions(enableRetransmissions: Boolean): Unit =
    requireNonNull(enableRetransmissions)
    _enableRetransmissions = enableRetransmissions

  def getEnableRetransmissions(): Boolean =
    _enableRetransmissions

  def setMaximumPacketSize(maximumPacketSize: Int): Unit =
    require(maximumPacketSize >= 0, "Maximum packet size must be non-negative")
    _maximumPacketSize = maximumPacketSize

  def getMaximumPacketSize(): Int =
    _maximumPacketSize

  /// Note from JDK docs:
  /// This method will return a new array each time it is invoked.
  def getApplicationProtocols(): Array[String] =
    if _applicationProtocols == null
    then null
    else _applicationProtocols.clone()

  /// Implementation Requirements
  /// This method will make a copy of the protocols array.
  def setApplicationProtocols(protocols: Array[String]): Unit =
    requireNonNull(protocols)
    require(
      protocols.forall(p => p != null && !p.isEmpty()),
      "Any element of non-empty array must not be null or empty string",
    )
    _applicationProtocols = protocols.clone()

  /// API Note:
  ///
  /// Note that a provider may not have been updated to support this method and
  /// in that case may return `null` instead of the default named groups for
  /// connection populated objects.
  ///
  /// Implementation Note:
  ///
  /// The SunJSSE provider supports this method.
  /// Note that applications may use the `jdk.tls.client.SignatureSchemes`
  /// and/or `jdk.tls.server.SignatureSchemes` system properties with the
  /// SunJSSE provider to the provider-specific default signature schemes.
  ///
  /// since JDK 19
  def getSignatureSchemes(): Array[String] =
    if _signatureSchemes == null
    then null
    else _signatureSchemes.clone()

  def setSignatureSchemes(schemes: Array[String]): Unit =
    if schemes == null
    then _signatureSchemes = null
    else
      require(
        schemes.forall(each => each != null && !each.isEmpty()),
        "Any element of non-empty array must not be null or empty string",
      )
      _signatureSchemes = schemes.clone()

  /// API Note:
  ///
  /// Note that a provider may not have been updated to support this method and
  /// in that case may return `null` instead of the default named groups for
  /// connection populated objects.
  ///
  /// Implementation Note:
  ///
  /// The SunJSSE provider supports this method. Note that applications may use
  /// the `jdk.tls.namedGroups` system property with the SunJSSE provider to
  /// the provider-specific default named groups.
  ///
  /// since JDK 20
  def getNamedGroups(): Array[String] =
    if _namedGroups == null
    then null
    else _namedGroups.clone()

  def setNamedGroups(namedGroups: Array[String]): Unit =
    if namedGroups == null
    then _namedGroups = null
    else
      requireNonNull(namedGroups)
      require(
        namedGroups.forall(each => each != null && !each.isEmpty()),
        "Any element of non-empty array must not be null or empty string",
      )
      _namedGroups = namedGroups.clone()
