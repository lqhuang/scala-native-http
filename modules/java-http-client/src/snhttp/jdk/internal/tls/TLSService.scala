package snhttp.jdk.internal.tls

import java.security.Provider
import java.util.{List as JList, Map as JMap}
import javax.net.ssl.SSLContext

import snhttp.jdk.net.ssl.SSLContextImpl

class TLSService private (
    provider: Provider,
    algorithm: String,
    aliases: JList[String],
    attributes: JMap[String, String],
) extends Provider.Service(
      provider,
      JcaService.SSLContext.name,
      algorithm,
      "snhttp.jdk.internal.tls.TLSService",
      aliases,
      attributes,
    ):

  override def supportsParameter(parameter: Object): Boolean =
    if parameter == null then true else false

  override def newInstance(constructorParameter: Object): SSLContext =
    new SSLContextImpl(provider, algorithm)

object TLSService:

  def apply(
      provider: Provider,
      algorithm: String,
      aliases: JList[String],
      attributes: JMap[String, String],
  ): TLSService = new TLSService(
    provider,
    algorithm,
    aliases,
    attributes,
  )

end TLSService
