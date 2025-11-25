package snhttp.tls.params

/// TLS Supplemental Data Formats (SupplementalDataType)
enum SupplementalDataType(val code: Int, val name: String):
  case UserMappingData extends SupplementalDataType(0, "user_mapping_data")
  // 1-16385 Unassigned
  case AuthzData extends SupplementalDataType(16386, "authz_data")
  // 16387-65279 Unassigned
  // 65280-65535 Reserved for Private Use
