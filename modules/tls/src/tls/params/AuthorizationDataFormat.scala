package snhttp.experimental.tls.params

/// TLS Authorization Data Formats
enum AuthorizationDataFormat(val code: Short, val name: String):
  case X509AttrCert extends AuthorizationDataFormat(0, "x509_attr_cert")
  case SAMLAssertion extends AuthorizationDataFormat(1, "saml_assertion")
  case X509AttrCertURL extends AuthorizationDataFormat(2, "x509_attr_cert_url")
  case SAMLAssertionURL extends AuthorizationDataFormat(3, "saml_assertion_url")
  // 4-63 Unassigned
  case KeynoteAssertionList extends AuthorizationDataFormat(64, "keynote_assertion_list")
  case KeynoteAssertionListURL extends AuthorizationDataFormat(65, "keynote_assertion_list_url")
  case DTCPAuthorization extends AuthorizationDataFormat(66, "dtcp_authorization")
  // 67-223 Unassigned
  // 224-255 Reserved for Private Use [RFC5878]
