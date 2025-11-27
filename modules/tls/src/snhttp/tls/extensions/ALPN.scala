package snhttp.tls.extensions

trait ALPNByteCodes(val bytes: IArray[Byte])

/// TLS Application-Layer Protocol Negotiation (ALPN) Protocol IDs
enum ALPNProtocol(val name: String):
  // scalafmt: { maxColumn = 150, align.preset = most, align.tokens."+" =  [{ code = "with" }] }
  case HTTP_1_1 extends ALPNProtocol("http/1.1") with ALPNByteCodes(IArray(0x68, 0x74, 0x74, 0x70, 0x2f, 0x31, 0x2e, 0x31))
  case HTTP_2   extends ALPNProtocol("h2")       with ALPNByteCodes(IArray(0x68, 0x32))
  case HTTP_2_C extends ALPNProtocol("h2c")      with ALPNByteCodes(IArray(0x68, 0x32, 0x63))
  case HTTP_3   extends ALPNProtocol("h3")       with ALPNByteCodes(IArray(0x68, 0x33))

object ALPNProtocol:

  val DefaultProtocols: Set[ALPNProtocol] =
    Set(HTTP_1_1, HTTP_2)

  val SupportedProtocols: Set[ALPNProtocol] =
    Set(HTTP_1_1, HTTP_2)

end ALPNProtocol
