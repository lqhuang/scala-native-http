package snhttp.experimental.tls.params

/// TLS HandshakeType
enum HandshakeType(val code: Short, val name: String):
  case HelloRequest extends HandshakeType(0, "hello_request_RESERVED")
  case ClientHello extends HandshakeType(1, "client_hello")
  case ServerHello extends HandshakeType(2, "server_hello")
  case HelloVerifyRequest extends HandshakeType(3, "hello_verify_request_RESERVED")
  case NewSessionTicket extends HandshakeType(4, "new_session_ticket")
  case EndOfEarlyData extends HandshakeType(5, "end_of_early_data")
  case HelloRetryRequest extends HandshakeType(6, "hello_retry_request_RESERVED")
  // 7 Unassigned
  case EncryptedExtensions extends HandshakeType(8, "encrypted_extensions")
  case RequestConnectionId extends HandshakeType(9, "request_connection_id")
  case NewConnectionId extends HandshakeType(10, "new_connection_id")
  case Certificate extends HandshakeType(11, "certificate")
  case ServerKeyExchange extends HandshakeType(12, "server_key_exchange_RESERVED")
  case CertificateRequest extends HandshakeType(13, "certificate_request")
  case ServerHelloDone extends HandshakeType(14, "server_hello_done_RESERVED")
  case CertificateVerify extends HandshakeType(15, "certificate_verify")
  case ClientKeyExchange extends HandshakeType(16, "client_key_exchange_RESERVED")
  case ClientCertificateRequest extends HandshakeType(17, "client_certificate_request")
  // 18-19 Unassigned
  case Finished extends HandshakeType(20, "finished")
  case CertificateUrl extends HandshakeType(21, "certificate_url_RESERVED")
  case CertificateStatus extends HandshakeType(22, "certificate_status_RESERVED")
  case SupplementalData extends HandshakeType(23, "supplemental_data_RESERVED")
  case KeyUpdate extends HandshakeType(24, "key_update")
  case CompressedCertificate extends HandshakeType(25, "compressed_certificate")
  case EKTKey extends HandshakeType(26, "ekt_key") // Encrypted Key Transport
  // 27-253 Unassigned
  case MessageHash extends HandshakeType(254, "message_hash")
  // 255 Unassigned
