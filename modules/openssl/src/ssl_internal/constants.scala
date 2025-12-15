package snhttp.experimental.openssl.ssl_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object constants:
  val SSL_CT_VALIDATION_PERMISSIVE: CUnsignedInt = 0.toUInt
  val SSL_CT_VALIDATION_STRICT: CUnsignedInt = 1.toUInt

  /* Maximum plaintext length: defined by SSL/TLS standards */
  val SSL3_RT_MAX_PLAIN_LENGTH = 16384
  /* Maximum compression overhead: defined by SSL/TLS standards */
  val SSL3_RT_MAX_COMPRESSED_OVERHEAD = 1024
  /* Maximum block size used in all ciphersuites. Currently 16 for AES.   */
  val SSL_RT_MAX_CIPHER_BLOCK_SIZE = 16

  /**
   * This is the maximum MAC (digest) size used by the SSL library. Currently maximum of 20 is used
   * by SHA1, but we reserve for future extension for 512-bit hashes.
   */
  val SSL3_RT_MAX_MD_SIZE = 64

  /**
   * The standards give a maximum encryption overhead of 1024 bytes. In practice the value is lower
   * than this. The overhead is the maximum number of padding bytes (256) plus the mac size.
   */
  val SSL3_RT_MAX_ENCRYPTED_OVERHEAD = 256 + SSL3_RT_MAX_MD_SIZE

  val SSL3_RT_HEADER_LENGTH = 5
  val SSL3_RT_MAX_COMPRESSED_LENGTH = SSL3_RT_MAX_PLAIN_LENGTH
  val SSL3_RT_MAX_ENCRYPTED_LENGTH = SSL3_RT_MAX_ENCRYPTED_OVERHEAD + SSL3_RT_MAX_COMPRESSED_LENGTH
  val SSL3_RT_MAX_PACKET_SIZE = SSL3_RT_MAX_ENCRYPTED_LENGTH + SSL3_RT_HEADER_LENGTH
  val SSL3_RT_SEND_MAX_ENCRYPTED_OVERHEAD = SSL_RT_MAX_CIPHER_BLOCK_SIZE + SSL3_RT_MAX_MD_SIZE
