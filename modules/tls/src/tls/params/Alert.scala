package snhttp.experimental.tls.params

/// TLS Alerts
enum Alert(val code: Short, val name: String):
  case CloseNotify extends Alert(0, "close_notify")
  // 1-9 Unassigned
  case UnexpectedMessage extends Alert(10, "unexpected_message")
  // 11-19 Unassigned
  case BadRecordMAC extends Alert(20, "bad_record_mac")
  case DecryptionFailed extends Alert(21, "decryption_failed_RESERVED")
  case RecordOverflow extends Alert(22, "record_overflow")
  // 23-29 Unassigned
  case DecompressionFailure extends Alert(30, "decompression_failure_RESERVED")
  // 31-39 Unassigned
  case HandshakeFailure extends Alert(40, "handshake_failure")
  case NoCertificate extends Alert(41, "no_certificate_RESERVED")
  case BadCertificate extends Alert(42, "bad_certificate")
  case UnsupportedCertificate extends Alert(43, "unsupported_certificate")
  case CertificateRevoked extends Alert(44, "certificate_revoked")
  case CertificateExpired extends Alert(45, "certificate_expired")
  case CertificateUnknown extends Alert(46, "certificate_unknown")
  case IllegalParameter extends Alert(47, "illegal_parameter")
  case UnknownCA extends Alert(48, "unknown_ca")
  case AccessDenied extends Alert(49, "access_denied")
  case DecodeError extends Alert(50, "decode_error")
  case DecryptError extends Alert(51, "decrypt_error")
  case TooManyCIDsRequested extends Alert(52, "too_many_cids_requested")
  // 53-59 Unassigned
  case ExportRestriction extends Alert(60, "export_restriction_RESERVED")
  // 61-69 Unassigned
  case ProtocolVersion extends Alert(70, "protocol_version")
  case InsufficientSecurity extends Alert(71, "insufficient_security")
  // 72-79 Unassigned
  case InternalError extends Alert(80, "internal_error")
  // 81-85 Unassigned
  case InappropriateFallback extends Alert(86, "inappropriate_fallback")
  // 87-89 Unassigned
  case UserCanceled extends Alert(90, "user_canceled")
  // 91-99 Unassigned
  case NoRenegotiation extends Alert(100, "no_renegotiation_RESERVED")
  // 101-108 Unassigned
  case MissingExtension extends Alert(109, "missing_extension")
  case UnsupportedExtension extends Alert(110, "unsupported_extension")
  case CertificateUnobtainable extends Alert(111, "certificate_unobtainable_RESERVED")
  case UnrecognizedName extends Alert(112, "unrecognized_name")
  case BadCertificateStatusResponse extends Alert(113, "bad_certificate_status_response")
  case BadCertificateHashValue extends Alert(114, "bad_certificate_hash_value_RESERVED")
  case UnknownPSKIdentity extends Alert(115, "unknown_psk_identity")
  case CertificateRequired extends Alert(116, "certificate_required")
  case GeneralError extends Alert(117, "general_error")
  // 118-119 Unassigned
  case NoApplicationProtocol extends Alert(120, "no_application_protocol")
  case ECHRequired extends Alert(121, "ech_required")
  // 122-255 Unassigned
