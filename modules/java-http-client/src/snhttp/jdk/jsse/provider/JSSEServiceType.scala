package snhttp.jdk.jsse.provider

import java.util.TreeSet

// Java Secure Socket Extension (JSSE) identifier
// https://docs.oracle.com/en/java/javase/25/security/java-secure-socket-extension-jsse-reference-guide.html
case class JSSEServiceType private (val name: String) extends AnyVal

object JSSEServiceType:

  val KeyStore = JSSEServiceType("KeyStore")
  val KeyManagerFactory = JSSEServiceType("KeyManagerFactory")
  val TrustManagerFactory = JSSEServiceType("TrustManagerFactory")
  val SSLContext = JSSEServiceType("SSLContext")

  val names: TreeSet[String] = {
    val set = new TreeSet(String.CASE_INSENSITIVE_ORDER)
    set.add(KeyStore.name)
    set.add(KeyManagerFactory.name)
    set.add(SSLContext.name)
    set.add(TrustManagerFactory.name)
    set
  }

  val values: Seq[JSSEServiceType] = Seq(
    KeyStore,
    KeyManagerFactory,
    SSLContext,
    TrustManagerFactory,
  )

  inline def contains(name: String): Boolean =
    names.contains(name)

end JSSEServiceType
