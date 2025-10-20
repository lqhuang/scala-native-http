package javax.net.ssl

import java.security.Principal

abstract class X509ExtendedKeyManager protected () extends X509KeyManager {
  def chooseEngineClientAlias(
      keyType: Array[String],
      issuers: Array[Principal],
      engine: SSLEngine,
  ): String

  def chooseEngineServerAlias(
      keyType: String,
      issuers: Array[Principal],
      engine: SSLEngine,
  ): String
}
