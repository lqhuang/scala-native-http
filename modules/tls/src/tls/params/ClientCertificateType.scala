package snhttp.experimental.tls.params

/// TLS ClientCertificateType Identifiers
enum ClientCertificateType(val code: Short, val name: String):
  // 0 Unassigned
  case RSA_Sign extends ClientCertificateType(1, "rsa_sign")
  case DSS_Sign extends ClientCertificateType(2, "dss_sign")
  case RSA_Fixed_DH extends ClientCertificateType(3, "rsa_fixed_dh")
  case DSS_Fixed_DH extends ClientCertificateType(4, "dss_fixed_dh")
  case RSA_Ephemeral_DH extends ClientCertificateType(5, "rsa_ephemeral_dh_RESERVED")
  case DSS_Ephemeral_DH extends ClientCertificateType(6, "dss_ephemeral_dh_RESERVED")
  // 7-19 Unassigned
  case Fortezza_DMS extends ClientCertificateType(20, "fortezza_dms_RESERVED")
  // 21-63 Unassigned
  case ECDSA_Sign extends ClientCertificateType(64, "ecdsa_sign")
  case RSA_Fixed_ECDH extends ClientCertificateType(65, "rsa_fixed_ecdh")
  case ECDSA_Fixed_ECDH extends ClientCertificateType(66, "ecdsa_fixed_ecdh")
  case gost_sign256 extends ClientCertificateType(67, "gost_sign256")
  case gost_sign512 extends ClientCertificateType(68, "gost_sign512")
  // 69-223 Unassigned
  // 224-255 Reserved for Private Use
