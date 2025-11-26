package snhttp.tls.params

enum SignatureAlgorithm(val code: Short, val name: String):
  case Anonymous extends SignatureAlgorithm(0, "anonymous")
  case RSA extends SignatureAlgorithm(1, "rsa")
  case DSA extends SignatureAlgorithm(2, "dsa")
  case ECDSA extends SignatureAlgorithm(3, "ecdsa")
  // 4-6 reserved
  case ED25519 extends SignatureAlgorithm(7, "ed25519")
  case ED448 extends SignatureAlgorithm(8, "ed448")
  // 9-63 Reserved
  case GOSTR34102012_256 extends SignatureAlgorithm(64, "gostr34102012_256")
  case GOSTR34102012_512 extends SignatureAlgorithm(65, "gostr34102012_512")
