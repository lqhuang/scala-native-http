package snhttp.experimental.openssl
package _openssl.ssl

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

import _root_.snhttp.experimental.openssl._openssl.bio.Types.BIO_POLL_DESCRIPTOR

private[openssl] object Structs:

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type COMP_METHOD = CStruct0

  object COMP_METHOD:
    given _tag: Tag[COMP_METHOD] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type CTLOG_STORE = CStruct0

  object CTLOG_STORE:
    given _tag: Tag[CTLOG_STORE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type CT_POLICY_EVAL_CTX = CStruct0

  object CT_POLICY_EVAL_CTX:
    given _tag: Tag[CT_POLICY_EVAL_CTX] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type DH = CStruct0

  object DH:
    given _tag: Tag[DH] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type ENGINE = CStruct0

  object ENGINE:
    given _tag: Tag[ENGINE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type EVP_MD = CStruct0

  object EVP_MD:
    given _tag: Tag[EVP_MD] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type EVP_PKEY = CStruct0

  object EVP_PKEY:
    given _tag: Tag[EVP_PKEY] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type OPENSSL_INIT_SETTINGS = CStruct0

  object OPENSSL_INIT_SETTINGS:
    given _tag: Tag[OPENSSL_INIT_SETTINGS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/core.h
   */
  opaque type OSSL_DISPATCH = CStruct2[CInt, CFuncPtr0[Unit]]

  object OSSL_DISPATCH:
    given _tag: Tag[OSSL_DISPATCH] = Tag.materializeCStruct2Tag[CInt, CFuncPtr0[Unit]]

    export fields.*
    private[ssl] object fields:
      extension (struct: OSSL_DISPATCH)
        def function_id: CInt = struct._1
        def function_id_=(value: CInt): Unit = !struct.at1 = value
        def function: CFuncPtr0[Unit] = struct._2
        def function_=(value: CFuncPtr0[Unit]): Unit = !struct.at2 = value
      end extension

    // Allocates OSSL_DISPATCH on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[OSSL_DISPATCH] = scala.scalanative.unsafe.alloc[OSSL_DISPATCH](1)
    def apply(function_id: CInt, function: CFuncPtr0[Unit])(using Zone): Ptr[OSSL_DISPATCH] =
      val ____ptr = apply()
      (!____ptr).function_id = function_id
      (!____ptr).function = function
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SRTP_PROTECTION_PROFILE = CStruct2[CString, CUnsignedLongInt]

  object SRTP_PROTECTION_PROFILE:
    given _tag: Tag[SRTP_PROTECTION_PROFILE] = Tag.materializeCStruct2Tag[CString, CUnsignedLongInt]

    export fields.*
    private[ssl] object fields:
      extension (struct: SRTP_PROTECTION_PROFILE)
        inline def name: CString = struct._1
        inline def name_=(value: CString): Unit = !struct.at1 = value
        inline def id: CUnsignedLongInt = struct._2
        inline def id_=(value: CUnsignedLongInt): Unit = !struct.at2 = value
      end extension

    // Allocates SRTP_PROTECTION_PROFILE on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SRTP_PROTECTION_PROFILE] =
      scala.scalanative.unsafe.alloc[SRTP_PROTECTION_PROFILE](1)
    def apply(name: CString, id: CUnsignedLongInt)(using Zone): Ptr[SRTP_PROTECTION_PROFILE] =
      val ____ptr = apply()
      (!____ptr).name = name
      (!____ptr).id = id
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type SSL = CStruct0

  object SSL:
    given _tag: Tag[SSL] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CIPHER = CStruct0

  object SSL_CIPHER:
    given _tag: Tag[SSL_CIPHER] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/comp.h
   */
  opaque type SSL_COMP = CStruct0

  object SSL_COMP:
    given _tag: Tag[SSL_COMP] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CONF_CTX = CStruct0

  object SSL_CONF_CTX:
    given _tag: Tag[SSL_CONF_CTX] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CONN_CLOSE_INFO = CStruct5[ULong, ULong, CString, USize, UInt]

  object SSL_CONN_CLOSE_INFO:
    given _tag: Tag[SSL_CONN_CLOSE_INFO] =
      Tag.materializeCStruct5Tag[ULong, ULong, CString, USize, UInt]

    // Allocates SSL_CONN_CLOSE_INFO on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SSL_CONN_CLOSE_INFO] =
      scala.scalanative.unsafe.alloc[SSL_CONN_CLOSE_INFO](1)
    def apply(
        error_code: ULong,
        frame_type: ULong,
        reason: CString,
        reason_len: USize,
        flags: UInt,
    )(using Zone): Ptr[SSL_CONN_CLOSE_INFO] =
      val ____ptr = apply()
      (!____ptr).error_code = error_code
      (!____ptr).frame_type = frame_type
      (!____ptr).reason = reason
      (!____ptr).reason_len = reason_len
      (!____ptr).flags = flags
      ____ptr

    extension (struct: SSL_CONN_CLOSE_INFO)
      def error_code: ULong = struct._1
      def error_code_=(value: ULong): Unit = !struct.at1 = value
      def frame_type: ULong = struct._2
      def frame_type_=(value: ULong): Unit = !struct.at2 = value
      def reason: CString = struct._3
      def reason_=(value: CString): Unit = !struct.at3 = value
      def reason_len: USize = struct._4
      def reason_len_=(value: USize): Unit = !struct.at4 = value
      def flags: UInt = struct._5
      def flags_=(value: UInt): Unit = !struct.at5 = value

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type SSL_CTX = CStruct0

  object SSL_CTX:
    given _tag: Tag[SSL_CTX] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type SSL_DANE = CStruct0

  object SSL_DANE:
    given _tag: Tag[SSL_DANE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_METHOD = CStruct0

  object SSL_METHOD:
    given _tag: Tag[SSL_METHOD] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_POLL_ITEM = CStruct3[BIO_POLL_DESCRIPTOR, ULong, ULong]

  object SSL_POLL_ITEM:
    given _tag: Tag[SSL_POLL_ITEM] =
      Tag.materializeCStruct3Tag[BIO_POLL_DESCRIPTOR, ULong, ULong]

    // Allocates SSL_POLL_ITEM on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SSL_POLL_ITEM] = scala.scalanative.unsafe.alloc[SSL_POLL_ITEM](1)
    def apply(desc: BIO_POLL_DESCRIPTOR, events: ULong, revents: ULong)(using
        Zone,
    ): Ptr[SSL_POLL_ITEM] =
      val ____ptr = apply()
      (!____ptr).desc = desc
      (!____ptr).events = events
      (!____ptr).revents = revents
      ____ptr

    extension (struct: SSL_POLL_ITEM)
      def desc: BIO_POLL_DESCRIPTOR = struct._1
      def desc_=(value: BIO_POLL_DESCRIPTOR): Unit = !struct.at1 = value
      def events: ULong = struct._2
      def events_=(value: ULong): Unit = !struct.at2 = value
      def revents: ULong = struct._3
      def revents_=(value: ULong): Unit = !struct.at3 = value

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_SESSION = CStruct0

  object SSL_SESSION:
    given _tag: Tag[SSL_SESSION] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_SHUTDOWN_EX_ARGS = CStruct2[ULong, CString]

  object SSL_SHUTDOWN_EX_ARGS:
    given _tag: Tag[SSL_SHUTDOWN_EX_ARGS] = Tag.materializeCStruct2Tag[ULong, CString]

    // Allocates SSL_SHUTDOWN_EX_ARGS on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SSL_SHUTDOWN_EX_ARGS] =
      scala.scalanative.unsafe.alloc[SSL_SHUTDOWN_EX_ARGS](1)
    def apply(quic_error_code: ULong, quic_reason: CString)(using
        Zone,
    ): Ptr[SSL_SHUTDOWN_EX_ARGS] =
      val ____ptr = apply()
      (!____ptr).quic_error_code = quic_error_code
      (!____ptr).quic_reason = quic_reason
      ____ptr

    extension (struct: SSL_SHUTDOWN_EX_ARGS)
      def quic_error_code: ULong = struct._1
      def quic_error_code_=(value: ULong): Unit = !struct.at1 = value
      def quic_reason: CString = struct._2
      def quic_reason_=(value: CString): Unit = !struct.at2 = value

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_STREAM_RESET_ARGS = CStruct1[ULong]

  object SSL_STREAM_RESET_ARGS:
    given _tag: Tag[SSL_STREAM_RESET_ARGS] = Tag.materializeCStruct1Tag[ULong]

    // Allocates SSL_STREAM_RESET_ARGS on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SSL_STREAM_RESET_ARGS] =
      scala.scalanative.unsafe.alloc[SSL_STREAM_RESET_ARGS](1)
    def apply(quic_error_code: ULong)(using Zone): Ptr[SSL_STREAM_RESET_ARGS] =
      val ____ptr = apply()
      (!____ptr).quic_error_code = quic_error_code
      ____ptr

    extension (struct: SSL_STREAM_RESET_ARGS)
      def quic_error_code: ULong = struct._1
      def quic_error_code_=(value: ULong): Unit = !struct.at1 = value

  /**
   * [bindgen] header: /usr/include/openssl/tls1.h
   */
  opaque type TLS_SESSION_TICKET_EXT = CStruct2[CUnsignedShort, Ptr[Byte]]

  object TLS_SESSION_TICKET_EXT:
    given _tag: Tag[TLS_SESSION_TICKET_EXT] = Tag.materializeCStruct2Tag[CUnsignedShort, Ptr[Byte]]

    export fields.*
    private[_openssl] object fields:
      extension (struct: TLS_SESSION_TICKET_EXT)
        inline def length: CUnsignedShort = struct._1
        inline def length_=(value: CUnsignedShort): Unit = !struct.at1 = value
        inline def data: Ptr[Byte] = struct._2
        inline def data_=(value: Ptr[Byte]): Unit = !struct.at2 = value
      end extension

    // Allocates TLS_SESSION_TICKET_EXT on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[TLS_SESSION_TICKET_EXT] =
      scala.scalanative.unsafe.alloc[TLS_SESSION_TICKET_EXT](1)
    def apply(length: CUnsignedShort, data: Ptr[Byte])(using Zone): Ptr[TLS_SESSION_TICKET_EXT] =
      val ____ptr = apply()
      (!____ptr).length = length
      (!____ptr).data = data
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type TLS_SIGALGS = CStruct0

  object TLS_SIGALGS:
    given _tag: Tag[TLS_SIGALGS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type evp_pkey_st = CStruct0

  object evp_pkey_st:
    given _tag: Tag[evp_pkey_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type lhash_st_SSL_SESSION = CStruct0

  object lhash_st_SSL_SESSION:
    given _tag: Tag[lhash_st_SSL_SESSION] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type openssl_ssl_test_functions = CStruct0

  object openssl_ssl_test_functions:
    given _tag: Tag[openssl_ssl_test_functions] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type srtp_protection_profile_st = CStruct2[CString, CUnsignedLongInt]

  object srtp_protection_profile_st:
    given _tag: Tag[srtp_protection_profile_st] =
      Tag.materializeCStruct2Tag[CString, CUnsignedLongInt]

    export fields.*
    private[_openssl] object fields:
      extension (struct: srtp_protection_profile_st)
        inline def name: CString = struct._1
        inline def name_=(value: CString): Unit = !struct.at1 = value
        inline def id: CUnsignedLongInt = struct._2
        inline def id_=(value: CUnsignedLongInt): Unit = !struct.at2 = value
      end extension

    // Allocates srtp_protection_profile_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[srtp_protection_profile_st] =
      scala.scalanative.unsafe.alloc[srtp_protection_profile_st](1)
    def apply(name: CString, id: CUnsignedLongInt)(using Zone): Ptr[srtp_protection_profile_st] =
      val ____ptr = apply()
      (!____ptr).name = name
      (!____ptr).id = id
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_cipher_st = CStruct0

  object ssl_cipher_st:
    given _tag: Tag[ssl_cipher_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_comp_st = CStruct0

  object ssl_comp_st:
    given _tag: Tag[ssl_comp_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_conf_ctx_st = CStruct0

  object ssl_conf_ctx_st:
    given _tag: Tag[ssl_conf_ctx_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_conn_close_info_st = CStruct5[ULong, ULong, CString, USize, UInt]

  object ssl_conn_close_info_st:
    given _tag: Tag[ssl_conn_close_info_st] =
      Tag.materializeCStruct5Tag[ULong, ULong, CString, USize, UInt]

    // Allocates ssl_conn_close_info_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ssl_conn_close_info_st] =
      scala.scalanative.unsafe.alloc[ssl_conn_close_info_st](1)
    def apply(
        error_code: ULong,
        frame_type: ULong,
        reason: CString,
        reason_len: USize,
        flags: UInt,
    )(using Zone): Ptr[ssl_conn_close_info_st] =
      val ____ptr = apply()
      (!____ptr).error_code = error_code
      (!____ptr).frame_type = frame_type
      (!____ptr).reason = reason
      (!____ptr).reason_len = reason_len
      (!____ptr).flags = flags
      ____ptr

    extension (struct: ssl_conn_close_info_st)
      def error_code: ULong = struct._1
      def error_code_=(value: ULong): Unit = !struct.at1 = value
      def frame_type: ULong = struct._2
      def frame_type_=(value: ULong): Unit = !struct.at2 = value
      def reason: CString = struct._3
      def reason_=(value: CString): Unit = !struct.at3 = value
      def reason_len: USize = struct._4
      def reason_len_=(value: USize): Unit = !struct.at4 = value
      def flags: UInt = struct._5
      def flags_=(value: UInt): Unit = !struct.at5 = value

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type ssl_ctx_st = CStruct0

  object ssl_ctx_st:
    given _tag: Tag[ssl_ctx_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_method_st = CStruct0

  object ssl_method_st:
    given _tag: Tag[ssl_method_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_poll_item_st = CStruct3[BIO_POLL_DESCRIPTOR, ULong, ULong]

  object ssl_poll_item_st:
    given _tag: Tag[ssl_poll_item_st] =
      Tag.materializeCStruct3Tag[BIO_POLL_DESCRIPTOR, ULong, ULong]

    // Allocates ssl_poll_item_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ssl_poll_item_st] =
      scala.scalanative.unsafe.alloc[ssl_poll_item_st](1)
    def apply(desc: BIO_POLL_DESCRIPTOR, events: ULong, revents: ULong)(using
        Zone,
    ): Ptr[ssl_poll_item_st] =
      val ____ptr = apply()
      (!____ptr).desc = desc
      (!____ptr).events = events
      (!____ptr).revents = revents
      ____ptr

    extension (struct: ssl_poll_item_st)
      def desc: BIO_POLL_DESCRIPTOR = struct._1
      def desc_=(value: BIO_POLL_DESCRIPTOR): Unit = !struct.at1 = value
      def events: ULong = struct._2
      def events_=(value: ULong): Unit = !struct.at2 = value
      def revents: ULong = struct._3
      def revents_=(value: ULong): Unit = !struct.at3 = value

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_session_st = CStruct0

  object ssl_session_st:
    given _tag: Tag[ssl_session_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_shutdown_ex_args_st = CStruct2[ULong, CString]

  object ssl_shutdown_ex_args_st:
    given _tag: Tag[ssl_shutdown_ex_args_st] = Tag.materializeCStruct2Tag[ULong, CString]

    // Allocates ssl_shutdown_ex_args_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ssl_shutdown_ex_args_st] =
      scala.scalanative.unsafe.alloc[ssl_shutdown_ex_args_st](1)
    def apply(quic_error_code: ULong, quic_reason: CString)(using
        Zone,
    ): Ptr[ssl_shutdown_ex_args_st] =
      val ____ptr = apply()
      (!____ptr).quic_error_code = quic_error_code
      (!____ptr).quic_reason = quic_reason
      ____ptr

    extension (struct: ssl_shutdown_ex_args_st)
      def quic_error_code: ULong = struct._1
      def quic_error_code_=(value: ULong): Unit = !struct.at1 = value
      def quic_reason: CString = struct._2
      def quic_reason_=(value: CString): Unit = !struct.at2 = value

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type ssl_st = CStruct0

  object ssl_st:
    given _tag: Tag[ssl_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_stream_reset_args_st = CStruct1[ULong]

  object ssl_stream_reset_args_st:
    given _tag: Tag[ssl_stream_reset_args_st] = Tag.materializeCStruct1Tag[ULong]

    // Allocates ssl_stream_reset_args_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ssl_stream_reset_args_st] =
      scala.scalanative.unsafe.alloc[ssl_stream_reset_args_st](1)
    def apply(quic_error_code: ULong)(using Zone): Ptr[ssl_stream_reset_args_st] =
      val ____ptr = apply()
      (!____ptr).quic_error_code = quic_error_code
      ____ptr

    extension (struct: ssl_stream_reset_args_st)
      def quic_error_code: ULong = struct._1
      def quic_error_code_=(value: ULong): Unit = !struct.at1 = value

  // /**
  //  * [bindgen] header: /usr/include/openssl/ct.h
  //  */
  // opaque type stack_st_SCT = CStruct0

  // object stack_st_SCT:
  //   given _tag: Tag[stack_st_SCT] = Tag.materializeCStruct0Tag

  // /**
  //  * [bindgen] header: /usr/include/openssl/ssl.h
  //  */
  // opaque type stack_st_SSL_CIPHER = CStruct0

  // object stack_st_SSL_CIPHER:
  //   given _tag: Tag[stack_st_SSL_CIPHER] = Tag.materializeCStruct0Tag

  // /**
  //  * [bindgen] header: /usr/include/openssl/comp.h
  //  */
  // opaque type stack_st_SSL_COMP = CStruct0

  // object stack_st_SSL_COMP:
  //   given _tag: Tag[stack_st_SSL_COMP] = Tag.materializeCStruct0Tag

  // /**
  //  * [bindgen] header: /usr/include/openssl/x509.h
  //  */
  // opaque type stack_st_X509 = CStruct0

  // object stack_st_X509:
  //   given _tag: Tag[stack_st_X509] = Tag.materializeCStruct0Tag

  // /**
  //  * [bindgen] header: /usr/include/openssl/x509.h
  //  */
  // opaque type stack_st_X509_NAME = CStruct0

  // object stack_st_X509_NAME:
  //   given _tag: Tag[stack_st_X509_NAME] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type tls_sigalgs_st = CStruct0

  object tls_sigalgs_st:
    given _tag: Tag[tls_sigalgs_st] = Tag.materializeCStruct0Tag
