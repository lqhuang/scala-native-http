/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.jsse.provider

import java.security.NoSuchAlgorithmException
import java.security.Provider
import java.util.{List as JList, Map as JMap}
import javax.net.ssl.SSLContext

import snhttp.jdk.net.ssl.{
  SSLContextImpl,
  SSLContextSpiImpl,
  KeyManagerFactoryImpl,
  TrustManagerFactoryImpl,
}

private[snhttp] class OpenSSLProvService(
    provider: Provider,
    svcType: JSSEServiceType,
    algorithm: String,
    className: String,
    aliases: JList[String],
    attributes: JMap[String, String],
) extends Provider.Service(
      provider,
      svcType,
      algorithm,
      className,
      aliases,
      attributes,
    ):

  def getAliases(): JList[String] =
    aliases

  override def supportsParameter(parameter: Object): Boolean =
    if parameter == null then true else false

  override def newInstance(constructorParameter: Object): Object =
    svcType match
      case "SSLContext" =>
        val spi = new SSLContextSpiImpl(algorithm)
        val ins = new SSLContextImpl(spi, provider, algorithm)
        if (algorithm.equalsIgnoreCase("Default"))
          ins.init(null, null, null)
        ins
      case "KeyManagerFactory" =>
        new KeyManagerFactoryImpl(provider, algorithm)
      case "TrustManagerFactory" =>
        new TrustManagerFactoryImpl(provider, algorithm)
      case _ =>
        throw new NoSuchAlgorithmException(
          s"Unsupported service type: ${svcType}",
        )
