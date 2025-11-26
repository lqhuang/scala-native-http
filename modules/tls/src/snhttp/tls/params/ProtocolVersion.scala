package snhttp.tls.params

/// TLS Protocol Versions
trait ProtocolName(val name: String)

enum ProtocolVersion(val code: Int):
  case SSL_3_0 extends ProtocolVersion(0x0300) with ProtocolName("SSL 3.0")
  case TLS_1_0 extends ProtocolVersion(0x0301) with ProtocolName("TLS 1.0")
  case TLS_1_1 extends ProtocolVersion(0x0302) with ProtocolName("TLS 1.1")
  case TLS_1_2 extends ProtocolVersion(0x0303) with ProtocolName("TLS 1.2")
  case TLS_1_3 extends ProtocolVersion(0x0304) with ProtocolName("TLS 1.3")
