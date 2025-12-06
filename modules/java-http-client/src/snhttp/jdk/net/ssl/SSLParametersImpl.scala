/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.net.ssl

import java.security.{AlgorithmConstraints, SecureRandom}
import java.util.Objects.requireNonNull
import javax.net.ssl.{
  SSLParameters,
  KeyManager,
  X509KeyManager,
  X509TrustManager,
  SSLSessionContext,
}

import snhttp.jdk.internal.tls.DefaultParams

/// Inspired from <https://github.com/google/conscrypt/blob/master/common/src/main/java/org/conscrypt/SSLParametersImpl.java>
class SSLParametersImpl protected[ssl] (
    // km: X509KeyManager,
    // tm: X509TrustManager,
    // sr: SecureRandom,
    // sc: SSLSessionContext,
    private var _cipherSuites: Array[String],
    private var _protocols: Array[String],
) extends SSLParameters(_cipherSuites, _protocols):

  // if the peer with this parameters tuned to work in client mode
  private var _useClientMode: Boolean = true
  // if the peer with this parameters tuned to require client authentication
  private var _wantClientAuth: Boolean = false
  private var _needClientAuth: Boolean = false

  // if the peer with this parameters allowed to cteate new SSL session
  private var _enableSessionCreation: Boolean = true
  // Endpoint identification algorithm (e.g., HTTPS)
  private var _identificationAlgorithm: String = null
  private var _algorithmConstraints: AlgorithmConstraints = null

  private var _preferLocalCipherSuites: Boolean = false
  private var _enableRetransmissions: Boolean = true
  private var _maximumPacketSize: Int = 0
  private var _applicationProtocols: Array[String] =
    DefaultParams.DefaultApplicationProtocols.toArray
  private var _signatureSchemes: Array[String] = null
  private var _namedGroups: Array[String] = null

  private var _alpn: String = _applicationProtocols.head

  override def getWantClientAuth(): Boolean =
    _wantClientAuth

  /// Note from JDK docs:
  /// Calling this method clears the `needClientAuth` flag
  override def setWantClientAuth(wantClientAuth: Boolean): Unit =
    requireNonNull(wantClientAuth)
    _wantClientAuth = wantClientAuth
    _needClientAuth = false

  override def getNeedClientAuth(): Boolean =
    _needClientAuth

  /// Note from JDK docs:
  /// Calling this method clears the `wantClientAuth` flag.
  override def setNeedClientAuth(needClientAuth: Boolean): Unit =
    requireNonNull(needClientAuth)
    _needClientAuth = needClientAuth
    _wantClientAuth = false

  override def getAlgorithmConstraints(): AlgorithmConstraints =
    _algorithmConstraints

  override def setAlgorithmConstraints(constraints: AlgorithmConstraints): Unit =
    requireNonNull(constraints)
    _algorithmConstraints = constraints

  override def getEndpointIdentificationAlgorithm(): String =
    _identificationAlgorithm

  override def setEndpointIdentificationAlgorithm(algorithm: String): Unit =
    requireNonNull(algorithm)
    require(!algorithm.isBlank() && !algorithm.isEmpty())
    _identificationAlgorithm = algorithm

  override def setEnableRetransmissions(enableRetransmissions: Boolean): Unit =
    requireNonNull(enableRetransmissions)
    _enableRetransmissions = enableRetransmissions

  override def getEnableRetransmissions(): Boolean =
    _enableRetransmissions

  override def setMaximumPacketSize(maximumPacketSize: Int): Unit =
    require(maximumPacketSize >= 0, "Maximum packet size must be non-negative")
    _maximumPacketSize = maximumPacketSize

  override def getMaximumPacketSize(): Int =
    _maximumPacketSize

  /// Note from JDK docs:
  /// This method will return a new array each time it is invoked.
  override def getApplicationProtocols(): Array[String] =
    if _applicationProtocols == null
    then null
    else _applicationProtocols.clone()

  /// Implementation Requirements
  /// This method will make a copy of the protocols array.
  override def setApplicationProtocols(protocols: Array[String]): Unit =
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
  /// SunJSSE provider to override the provider-specific default signature schemes.
  ///
  /// since JDK 19
  override def getSignatureSchemes(): Array[String] =
    if _signatureSchemes == null
    then null
    else _signatureSchemes.clone()

  override def setSignatureSchemes(schemes: Array[String]): Unit =
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
  /// override the provider-specific default named groups.
  ///
  /// since JDK 20
  override def getNamedGroups(): Array[String] =
    if _namedGroups == null
    then null
    else _namedGroups.clone()

  override def setNamedGroups(namedGroups: Array[String]): Unit =
    if namedGroups == null
    then _namedGroups = null
    else
      requireNonNull(namedGroups)
      require(
        namedGroups.forall(each => each != null && !each.isEmpty()),
        "Any element of non-empty array must not be null or empty string",
      )
      _namedGroups = namedGroups.clone()

  /**
   * Extended methods beyond standard SSLParameters
   */

  def getEnabledCipherSuites(): Array[String] =
    _cipherSuites.clone()

  def setEnabledCipherSuites(suites: Array[String]): Unit =
    requireNonNull(suites)
    require(
      suites.forall(each => each != null && !each.isEmpty()),
      "Any element of non-empty array must not be null or empty string",
    )

    // any unsupported CipherSuite will cause IllegalArgumentException
    val validSuites =
      DefaultParams.SupportedCipherSuites.map(_.toLowerCase())
        & Set(suites.map(_.toLowerCase())*)
    if (validSuites.size != suites.length)
      throw new IllegalArgumentException("Some of the cipher suites are not supported")

    _cipherSuites = suites.clone()

  def getEnabledProtocols(): Array[String] =
    _protocols.clone()

  def setEnabledProtocols(protocols: Array[String]): Unit =
    requireNonNull(protocols)
    require(
      protocols.forall(each => each != null && !each.isEmpty()),
      "Any element of non-empty array must not be null or empty string",
    )

    // any unsupported Protocol will cause IllegalArgumentException
    val validProtocols =
      DefaultParams.SupportedProtocols.map(_.toLowerCase())
        & Set(protocols.map(_.toLowerCase())*)
    if (validProtocols.size != protocols.length)
      throw new IllegalArgumentException("Some of the protocols are not supported")

    _protocols = protocols.clone()

  def getSSLParameters(): SSLParameters =
    this

  /// Notes from JDK docs:
  ///
  /// If params.getCipherSuites() is non-null, setEnabledCipherSuites() is called with that value.
  /// If params.getProtocols() is non-null, setEnabledProtocols() is called with that value.
  /// If params.getNeedClientAuth() or params.getWantClientAuth() return true,
  //    setNeedClientAuth(true) and setWantClientAuth(true) are called, respectively;
  //    otherwise setWantClientAuth(false) is called.
  /// If params.getServerNames() is non-null, the engine will configure its server names with that value.
  /// If params.getSNIMatchers() is non-null, the engine will configure its SNI matchers with that value.
  ///
  /// Throws `IllegalArumentException` if the setEnabledCipherSuites() or the setEnabledProtocols() call fails
  def setSSLParameters(params: SSLParameters): Unit =
    requireNonNull(params)

    // // copy over all supported parameters
    // setEnabledCipherSuites(params.getCipherSuites())
    // setEnabledProtocols(params.getProtocols())
    // setWantClientAuth(params.getWantClientAuth())
    // setNeedClientAuth(params.getNeedClientAuth())
    // setEnableSessionCreation(params.getEnableSessionCreation())
    // setEndpointIdentificationAlgorithm(
    //   params.getEndpointIdentificationAlgorithm(),
    // )
    // setAlgorithmConstraints(params.getAlgorithmConstraints())
    // setApplicationProtocols(params.getApplicationProtocols())
    ???

  def getApplicationProtocol(): String =
    _alpn

  def getHandshakeApplicationProtocol(): String =
    ???

  def getUseClientMode(): Boolean =
    _useClientMode

  def setUseClientMode(mode: Boolean): Unit =
    requireNonNull(mode)
    _useClientMode = mode

  def getEnableSessionCreation(): Boolean =
    _enableSessionCreation

  def setEnableSessionCreation(flag: Boolean): Unit =
    requireNonNull(flag)
    _enableSessionCreation = flag

object SSLParametersImpl:

  // protected[ssl]
  def getDefault(): SSLParametersImpl =
    new SSLParametersImpl(
      DefaultParams.getDefaultCipherSuites(),
      DefaultParams.getDefaultProtocols(),
    )

  protected[ssl] def getSupported(): SSLParametersImpl =
    new SSLParametersImpl(
      DefaultParams.getSupportedCipherSuites(),
      DefaultParams.getSupportedProtocols(),
    )

  def getDefaultCipherSuites(): Array[String] =
    DefaultParams.getDefaultCipherSuites()

  def getSupportedCipherSuites(): Array[String] =
    DefaultParams.getSupportedCipherSuites()

  def getDefaultProtocols(): Array[String] =
    DefaultParams.getDefaultProtocols()

  def getSupportedProtocols(): Array[String] =
    DefaultParams.getSupportedProtocols()

end SSLParametersImpl
