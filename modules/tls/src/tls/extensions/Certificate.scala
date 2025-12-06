package snhttp.experimental.tls.extensions

/// https://www.iana.org/assignments/tls-extensiontype-values/tls-extensiontype-values.xhtml

/// TLS Certificate Types
enum CertificateType(val code: Short, val name: String):
  case X509 extends CertificateType(0, "X509")
  case OpenPGP extends CertificateType(1, "OpenPGP_RESERVED")
  case RawPublicKey extends CertificateType(2, "Raw Public Key")
  case IEEE1609Dot2 extends CertificateType(3, "1609Dot2")
  // 4-223 Unassigned
  // 224-255 Reserved for Private Use [RFC6091]
end CertificateType

/// TLS Certificate Status Types
enum CertificateStatus(val code: Short, val name: String):
  // 0 reserved
  case OCSP extends CertificateStatus(1, "ocsp")
  case OCSPMulti extends CertificateStatus(2, "ocsp_multi_RESERVED")
  // 3-255 Unassigned
end CertificateStatus

case class BaseCertCompressAlgorithm(val code: Int)

/// TLS Certificate Compression Algorithm IDs
enum CertCompressAlgorithm(val code: Int, val name: String):
  /// 0          reserved
  case Zlib extends CertCompressAlgorithm(1, "zlib")
  case Brotli extends CertCompressAlgorithm(2, "brotli")
  case Zstd extends CertCompressAlgorithm(3, "zstd")
  // 4-16383     Unassigned
  // 16384-65535 Reserved for Experimental Use
end CertCompressAlgorithm
