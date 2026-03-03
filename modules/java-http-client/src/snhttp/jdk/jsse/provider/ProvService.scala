package snhttp.jdk.jsse.provider

import java.security.Provider
import java.util.{List as JList, Map as JMap}
import javax.net.ssl.SSLContext

import snhttp.jdk.net.ssl.SSLContextImpl

/* OpenSSLProviderService */
private class ProvService private[provider] (
    provider: Provider,
    svcType: String,
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

  override def supportsParameter(parameter: Object): Boolean =
    if parameter == null then true else false

object ProvService:

  def apply(
      provider: Provider,
      svcType: JSSEServiceType,
      algorithm: String,
      className: String,
      aliases: JList[String],
      attributes: JMap[String, String],
  ): ProvService = new ProvService(
    provider,
    svcType.name,
    algorithm,
    className,
    aliases,
    attributes,
  )

end ProvService
