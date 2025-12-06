package snhttp.experimental.tls.params

/// TLS HashAlgorithm
///
/// References:
///
/// - https://www.iana.org/assignments/tls-parameters/tls-parameters.xhtml#tls-parameters-18
enum HashAlgorithm(val code: Short, val name: String):
  case None extends HashAlgorithm(0, "none")
  case MD5 extends HashAlgorithm(1, "md5")
  case SHA1 extends HashAlgorithm(2, "sha1")
  case SHA224 extends HashAlgorithm(3, "sha224")
  case SHA256 extends HashAlgorithm(4, "sha256")
  case SHA384 extends HashAlgorithm(5, "sha384")
  case SHA512 extends HashAlgorithm(6, "sha512")
  case Intrinsic extends HashAlgorithm(8, "Intrinsic")

object HashAlgorithm:
  lazy val recommendedHashAlgorithms: Set[HashAlgorithm] = Set(
    HashAlgorithm.None,
    HashAlgorithm.SHA256,
    HashAlgorithm.SHA384,
    HashAlgorithm.SHA512,
    HashAlgorithm.Intrinsic,
  )
