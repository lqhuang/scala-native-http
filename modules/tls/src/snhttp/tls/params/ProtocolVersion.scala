package snhttp.tls.params

/// TLS Protocol Versions
trait ProtocolCode(val code: Int)
trait ProtocolName(val name: String)

enum ProtocolVersion(code: Int, name: String) extends ProtocolCode(code) with ProtocolName(name):
  case SSL_3_0 extends ProtocolVersion(0x0300, "SSLv3.0")
  case TLS_1_0 extends ProtocolVersion(0x0301, "TLSv1.0")
  case TLS_1_1 extends ProtocolVersion(0x0302, "TLSv1.1")
  case TLS_1_2 extends ProtocolVersion(0x0303, "TLSv1.2")
  case TLS_1_3 extends ProtocolVersion(0x0304, "TLSv1.3")

object ProtocolVersion:

  val DefaultProtocols: Set[ProtocolVersion] =
    Set(
      TLS_1_2,
      TLS_1_3,
    )

  val SupportedProtocols: Set[ProtocolVersion] =
    Set(
      TLS_1_2,
      TLS_1_3,
    )

end ProtocolVersion
