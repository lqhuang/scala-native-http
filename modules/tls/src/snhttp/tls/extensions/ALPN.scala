package snhttp.tls.extensions

trait AlpnByteCodes(val byteCodes: IArray[Byte])

/// TLS Application-Layer Protocol Negotiation (ALPN) Protocol IDs
enum ProtocolId(val name: String):
  // scalafmt: { maxColumn = 150, align.preset = most, align.tokens."+" =  [{ code = "with" }] }
  case HTTP_1_1 extends ProtocolId("http/1.1") with AlpnByteCodes(IArray(0x68, 0x74, 0x74, 0x70, 0x2f, 0x31, 0x2e, 0x31))
  case HTTP_2   extends ProtocolId("h2")       with AlpnByteCodes(IArray(0x68, 0x32))
  case HTTP_2_C extends ProtocolId("h2c")      with AlpnByteCodes(IArray(0x68, 0x32, 0x63))
  case HTTP_3   extends ProtocolId("h3")       with AlpnByteCodes(IArray(0x68, 0x33))
