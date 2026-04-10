package javax.net.ssl

import java.security.KeyStore
import java.util.List as JList
import java.util.Collections
import java.util.Objects.requireNonNull

// Ref:
// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/KeyStoreBuilderParameters.html
class KeyStoreBuilderParameters(params: JList[KeyStore.Builder]) extends ManagerFactoryParameters:

  requireNonNull(params, "params must not be null")
  require(!params.isEmpty(), "params must not be empty")

  def this(builder: KeyStore.Builder) = this({
    requireNonNull(builder, "builder must not be null")
    JList.of(builder)
  })

  def getParameters(): JList[KeyStore.Builder] =
    Collections.unmodifiableList(params)
