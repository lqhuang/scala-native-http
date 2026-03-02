package snhttp.jdk.internal.tls

// Java Cryptography Architecture (JCA) Service identifier
// for better type safety
case class JcaService(val name: String) extends AnyVal

object JcaService:

  val SSLContext = JcaService("SSLContext")

  val names: Set[String] = Set(SSLContext.name).map(_.toUpperCase())

end JcaService
