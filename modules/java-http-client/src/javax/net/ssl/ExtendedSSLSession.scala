package javax.net.ssl

import javax.crypto.SecretKey

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/ExtendedSSLSession.html
abstract class ExtendedSSLSession extends SSLSession {
  def exportKeyingMaterialData(
      label: String,
      context: Array[Byte],
      length: Int,
  ): Array[Byte]

  def exportKeyingMaterialKey(
      keyAlg: String,
      label: String,
      context: Array[Byte],
      length: Int,
  ): SecretKey

  def getLocalSupportedSignatureAlgorithms(): Array[String]

  def getPeerSupportedSignatureAlgorithms(): Array[String]

  def getRequestedServerNames(): java.util.List[SNIServerName]

  def getStatusResponses(): Array[Array[Byte]]
}
