package snhttp.tls.params

/// TLS ContentType
enum ContentType(val code: Short, val name: String):
  // 0-19 Unassigned (Requires coordination; see [RFC9443])
  case ChangeCipherSpec extends ContentType(20, "change_cipher_spec")
  case Alert extends ContentType(21, "alert")
  case Handshake extends ContentType(22, "handshake")
  case ApplicationData extends ContentType(23, "application_data")
  case Heartbeat extends ContentType(24, "heartbeat")
  case TLS12CID extends ContentType(25, "tls12_cid")
  case ACK extends ContentType(26, "ack")
  case ReturnRoutabilityCheck extends ContentType(27, "return_routability_check")
  // 28-31 Unassigned
  // 32-63 Reserved
  // 64-255 Unassigned (Requires coordination; see [RFC9443])
