package snhttp.tls.params

/// TLS KDF Identifiers
///
/// Key Derivation Function Type
enum KDFType(val code: Int, val name: String):
  // 0x0000 Reserved
  case HKDF_SHA256 extends KDFType(0x0001, "HKDF_SHA256")
  case HKDF_SHA384 extends KDFType(0x0002, "HKDF_SHA384")
  // 0x0003-0xfeff Unassigned
  // 0xff00-0xffff Reserved for Private Use
