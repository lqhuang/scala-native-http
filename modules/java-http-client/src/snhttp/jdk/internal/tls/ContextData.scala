package snhttp.jdk.internal.tls

import javax.net.ssl.{SSLParameters, SSLSession}

/// Maintain context parameters for TLS contexts and sessions.
protected[jdk] case class ContextData():

  private val _applicationProtocol = "http/1.1"

  private var _cipherSuites: Set[String] =
    DefaultParams.DefaultCipherSuites
  private var _protocols: Set[String] =
    DefaultParams.DefaultProtocols
  private var _applicationProtocols: Set[String] =
    DefaultParams.DefaultApplicationProtocols
  private val _session: SSLSession = null

  private val _sslParameters: SSLParameters =
    null

  private var _clientMode: Boolean = true

  private var enableSessionCreation = true

  def getEnabledCipherSuites(): Array[String] =
    _cipherSuites.toArray

  def getEnabledProtocols(): Array[String] =
    _protocols.toArray

  def getApplicationProtocol(): String =
    _applicationProtocol

  def getHandshakeApplicationProtocol(): String =
    _applicationProtocol

  def getApplicationProtocols(): Array[String] =
    _applicationProtocols.toArray

  def sslSession: SSLSession =
    _session

  def sslParameters: SSLParameters =
    _sslParameters

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
  protected[jdk] def setSSLParameters(params: SSLParameters): Unit =
    // _sslParameters = params
    ???

  protected[jdk] def setEnabledCipherSuites(_suites: Array[String]): Unit =
    val suites = _suites.toSet
    // any unsupported CipherSuite will cause IllegalArgumentException
    val validSuites =
      DefaultParams.SupportedCipherSuites.map(_.toLowerCase())
        & suites.map(_.toLowerCase())
    _cipherSuites = validSuites

  def setEnabledProtocols(protocols: Array[String]): Unit =
    // any unsupported CipherSuite will cause IllegalArgumentException
    _protocols = null

  def setApplicationProtocols(appProtocols: Array[String]): Unit =
    _applicationProtocols = null

  def getUseClientMode(): Boolean =
    _clientMode

  def setUseClientMode(mode: Boolean): Unit =
    _clientMode = mode

  def setEnableSessionCreation(flag: Boolean): Unit =
    enableSessionCreation = flag

  def getEnableSessionCreation(): Boolean =
    enableSessionCreation

// protected[jdk] object ContextData:

  lazy val defaultCipherSuites: Set[String] =
    DefaultParams.DefaultCipherSuites

  def getDefaultCipherSuites(): Array[String] =
    DefaultParams.DefaultCipherSuites.toArray

  lazy val supportedCipherSuites: Set[String] =
    DefaultParams.SupportedCipherSuites

  def getSupportedCipherSuites(): Array[String] =
    DefaultParams.SupportedCipherSuites.toArray

  lazy val defaultProtocols: Set[String] =
    DefaultParams.DefaultProtocols

  def getDefaultProtocols(): Array[String] =
    DefaultParams.DefaultProtocols.toArray

  lazy val supportedProtocols: Set[String] =
    DefaultParams.SupportedProtocols

  def getSupportedProtocols(): Array[String] =
    DefaultParams.SupportedProtocols.toArray

  lazy val defaultApplicationProtocols: Set[String] =
    DefaultParams.DefaultApplicationProtocols

  def getDefaultApplicationProtocols(): Array[String] =
    DefaultParams.DefaultApplicationProtocols.toArray

  lazy val supportedApplicationProtocols: Set[String] =
    DefaultParams.SupportedApplicationProtocols

  def getSupportedApplicationProtocols: Array[String] =
    DefaultParams.SupportedApplicationProtocols.toArray

end ContextData
