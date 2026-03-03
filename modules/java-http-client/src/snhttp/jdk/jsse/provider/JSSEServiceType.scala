package snhttp.jdk.jsse.provider

import java.util.TreeSet

// Java Secure Socket Extension (JSSE) identifier
// https://docs.oracle.com/en/java/javase/25/security/java-secure-socket-extension-jsse-reference-guide.html
type JSSEServiceType = "KeyStore" | "KeyManagerFactory" | "TrustManagerFactory" | "SSLContext"
object JSSEServiceType:

  lazy val names: TreeSet[String] = {
    val set = new TreeSet(String.CASE_INSENSITIVE_ORDER)
    set.add("KeyStore")
    set.add("KeyManagerFactory")
    set.add("SSLContext")
    set.add("TrustManagerFactory")
    set
  }
