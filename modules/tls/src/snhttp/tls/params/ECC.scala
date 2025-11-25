package snhttp.tls.params

/// TLS EC Point Formats
enum ECCPointFormat(val code: Int, val name: String):
  case UNCOMPRESSED extends ECCPointFormat(0, "uncompressed")
  case AnsiX962CompressedPrime extends ECCPointFormat(1, "ansiX962_compressed_prime")
  case AnsiX962CompressedChar2 extends ECCPointFormat(2, "ansiX962_compressed_char2")
  // 3-247 Unassigned
  // 248-255 Reserved for Private Use
end ECCPointFormat

/// TLS EC Curve Types
enum ECCType(val code: Int, val name: String):
  case ExplicitPrime extends ECCType(1, "explicit_prime")
  case ExplicitChar2 extends ECCType(2, "explicit_char2")
  case NamedCurve extends ECCType(3, "named_curve")
  // 4-247 Unassigned
  // 248-255 Reserved for Private Use
end ECCType
