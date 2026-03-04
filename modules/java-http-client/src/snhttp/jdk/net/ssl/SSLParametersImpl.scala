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

  // if the peer with this parameters allowed to cteate new SSL session
  private var _enableSessionCreation: Boolean = true

  private var _preferLocalCipherSuites: Boolean = false

  // private var _applicationProtocols: Array[String] = ???
  // DefaultParams.DefaultApplicationProtocols.toArray
  // private var _alpn: String = _applicationProtocols.head

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
    // TODO: reimplement
    val validSuites = Set()
    // DefaultParams.SupportedCipherSuites.map(_.toLowerCase())
    //   & Set(suites.map(_.toLowerCase())*)
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
    // TODO: reimplement
    val validProtocols = Set()
    // DefaultParams.SupportedProtocols.map(_.toLowerCase())
    //   & Set(protocols.map(_.toLowerCase())*)

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
    ???
    // _alpn

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
