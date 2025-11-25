package snhttp.tls.params

/// TLS SignatureScheme
enum SignatureScheme(val code: Int, val name: String):
  case RSA_PSS_PSS_SHA512 extends SignatureScheme(0x080b, "rsa_pss_pss_sha512")
  case RSA_PSS_PSS_SHA384 extends SignatureScheme(0x080a, "rsa_pss_pss_sha384")
  case RSA_PSS_PSS_SHA256 extends SignatureScheme(0x0809, "rsa_pss_pss_sha256")
  case ED448 extends SignatureScheme(0x0808, "ed448")
  case ED25519 extends SignatureScheme(0x0807, "ed25519")
  case RSA_PSS_RSAE_SHA512 extends SignatureScheme(0x0806, "rsa_pss_rsae_sha512")
  case RSA_PSS_RSAE_SHA384 extends SignatureScheme(0x0805, "rsa_pss_rsae_sha384")
  case RSA_PSS_RSAE_SHA256 extends SignatureScheme(0x0804, "rsa_pss_rsae_sha256")
  case ECDSA_SECP521R1_SHA512 extends SignatureScheme(0x0603, "ecdsa_secp521r1_sha512")
  case RSA_PKCS1_SHA512 extends SignatureScheme(0x0601, "rsa_pkcs1_sha512")
  case ECDSA_SECP384R1_SHA384 extends SignatureScheme(0x0503, "ecdsa_secp384r1_sha384")
  case RSA_PKCS1_SHA384 extends SignatureScheme(0x0501, "rsa_pkcs1_sha384")
  case ECDSA_SECP256R1_SHA256 extends SignatureScheme(0x0403, "ecdsa_secp256r1_sha256")
  case RSA_PKCS1_SHA256 extends SignatureScheme(0x0401, "rsa_pkcs1_sha256")
