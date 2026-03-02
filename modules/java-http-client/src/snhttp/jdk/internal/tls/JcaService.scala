package snhttp.jdk.internal.tls

import java.util.TreeSet

// Java Cryptography Architecture (JCA) Service identifier
// for better type safety
case class JcaService(val name: String) extends AnyVal

object JcaService:

  val SSLContext = JcaService("SSLContext")

  val names: TreeSet[String] = {
    val set = new TreeSet(String.CASE_INSENSITIVE_ORDER)
    set.add(SSLContext.name)
    set
  }

end JcaService
