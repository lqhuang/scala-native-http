package snhttp.experimental.openssl
package _openssl.ssl

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._common.size_t
import _root_.snhttp.experimental.openssl._openssl.types.Types.EVP_MD
import _root_.snhttp.experimental.openssl._openssl.safestack.{stack_st_SCT, stack_st_SSL_CIPHER}
import _root_.snhttp.experimental.openssl._openssl.x509.Types.{X509, X509_STORE_CTX}

import Structs.{ssl_st, SSL, SSL_CIPHER, SSL_CTX, SSL_SESSION, CT_POLICY_EVAL_CTX}

private[openssl] object Aliases:

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type DTLS_timer_cb = CFuncPtr2[Ptr[SSL], CUnsignedInt, CUnsignedInt]
  object DTLS_timer_cb:
    given _tag: Tag[DTLS_timer_cb] = Tag.materializeCFuncPtr2[Ptr[SSL], CUnsignedInt, CUnsignedInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): DTLS_timer_cb =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(inline o: CFuncPtr2[Ptr[SSL], CUnsignedInt, CUnsignedInt]): DTLS_timer_cb = o
    extension (v: DTLS_timer_cb)
      inline def value: CFuncPtr2[Ptr[SSL], CUnsignedInt, CUnsignedInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type GEN_SESSION_CB = CFuncPtr3[Ptr[SSL], Ptr[CUnsignedChar], Ptr[CUnsignedInt], CInt]
  object GEN_SESSION_CB:
    given _tag: Tag[GEN_SESSION_CB] =
      Tag.materializeCFuncPtr3[Ptr[SSL], Ptr[CUnsignedChar], Ptr[CUnsignedInt], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): GEN_SESSION_CB =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[SSL], Ptr[CUnsignedChar], Ptr[CUnsignedInt], CInt],
    ): GEN_SESSION_CB = o
    extension (v: GEN_SESSION_CB)
      inline def value: CFuncPtr3[Ptr[SSL], Ptr[CUnsignedChar], Ptr[CUnsignedInt], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CTX_alpn_select_cb_func = CFuncPtr6[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[
    CUnsignedChar,
  ], Ptr[CUnsignedChar], CUnsignedInt, Ptr[Byte], CInt]
  object SSL_CTX_alpn_select_cb_func:
    given _tag: Tag[SSL_CTX_alpn_select_cb_func] = Tag.materializeCFuncPtr6[Ptr[SSL], Ptr[
      Ptr[CUnsignedChar],
    ], Ptr[CUnsignedChar], Ptr[CUnsignedChar], CUnsignedInt, Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_CTX_alpn_select_cb_func =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr6[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[CUnsignedChar], Ptr[
          CUnsignedChar,
        ], CUnsignedInt, Ptr[Byte], CInt],
    ): SSL_CTX_alpn_select_cb_func = o
    extension (v: SSL_CTX_alpn_select_cb_func)
      inline def value: CFuncPtr6[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[CUnsignedChar], Ptr[
        CUnsignedChar,
      ], CUnsignedInt, Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CTX_decrypt_session_ticket_fn = CFuncPtr6[Ptr[SSL], Ptr[SSL_SESSION], Ptr[
    CUnsignedChar,
  ], size_t, SSL_TICKET_STATUS, Ptr[Byte], SSL_TICKET_RETURN]
  object SSL_CTX_decrypt_session_ticket_fn:
    given _tag: Tag[SSL_CTX_decrypt_session_ticket_fn] = Tag.materializeCFuncPtr6[Ptr[SSL], Ptr[
      SSL_SESSION,
    ], Ptr[CUnsignedChar], size_t, SSL_TICKET_STATUS, Ptr[Byte], SSL_TICKET_RETURN]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_CTX_decrypt_session_ticket_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr6[Ptr[SSL], Ptr[SSL_SESSION], Ptr[
          CUnsignedChar,
        ], size_t, SSL_TICKET_STATUS, Ptr[Byte], SSL_TICKET_RETURN],
    ): SSL_CTX_decrypt_session_ticket_fn = o
    extension (v: SSL_CTX_decrypt_session_ticket_fn)
      inline def value: CFuncPtr6[Ptr[SSL], Ptr[SSL_SESSION], Ptr[
        CUnsignedChar,
      ], size_t, SSL_TICKET_STATUS, Ptr[Byte], SSL_TICKET_RETURN] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CTX_generate_session_ticket_fn = CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt]
  object SSL_CTX_generate_session_ticket_fn:
    given _tag: Tag[SSL_CTX_generate_session_ticket_fn] =
      Tag.materializeCFuncPtr2[Ptr[SSL], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_CTX_generate_session_ticket_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt],
    ): SSL_CTX_generate_session_ticket_fn = o
    extension (v: SSL_CTX_generate_session_ticket_fn)
      inline def value: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CTX_keylog_cb_func = CFuncPtr2[Ptr[SSL], CString, Unit]
  object SSL_CTX_keylog_cb_func:
    given _tag: Tag[SSL_CTX_keylog_cb_func] = Tag.materializeCFuncPtr2[Ptr[SSL], CString, Unit]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_CTX_keylog_cb_func =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(inline o: CFuncPtr2[Ptr[SSL], CString, Unit]): SSL_CTX_keylog_cb_func = o
    extension (v: SSL_CTX_keylog_cb_func)
      inline def value: CFuncPtr2[Ptr[SSL], CString, Unit] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CTX_npn_advertised_cb_func =
    CFuncPtr4[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[CUnsignedInt], Ptr[Byte], CInt]
  object SSL_CTX_npn_advertised_cb_func:
    given _tag: Tag[SSL_CTX_npn_advertised_cb_func] =
      Tag
        .materializeCFuncPtr4[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[CUnsignedInt], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_CTX_npn_advertised_cb_func =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr4[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[CUnsignedInt], Ptr[Byte], CInt],
    ): SSL_CTX_npn_advertised_cb_func = o
    extension (v: SSL_CTX_npn_advertised_cb_func)
      inline def value
          : CFuncPtr4[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[CUnsignedInt], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CTX_npn_select_cb_func = CFuncPtr6[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[
    CUnsignedChar,
  ], Ptr[CUnsignedChar], CUnsignedInt, Ptr[Byte], CInt]
  object SSL_CTX_npn_select_cb_func:
    given _tag: Tag[SSL_CTX_npn_select_cb_func] = Tag.materializeCFuncPtr6[Ptr[SSL], Ptr[
      Ptr[CUnsignedChar],
    ], Ptr[CUnsignedChar], Ptr[CUnsignedChar], CUnsignedInt, Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_CTX_npn_select_cb_func =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr6[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[CUnsignedChar], Ptr[
          CUnsignedChar,
        ], CUnsignedInt, Ptr[Byte], CInt],
    ): SSL_CTX_npn_select_cb_func = o
    extension (v: SSL_CTX_npn_select_cb_func)
      inline def value: CFuncPtr6[Ptr[SSL], Ptr[Ptr[CUnsignedChar]], Ptr[CUnsignedChar], Ptr[
        CUnsignedChar,
      ], CUnsignedInt, Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_TICKET_RETURN = CInt
  object SSL_TICKET_RETURN:
    given _tag: Tag[SSL_TICKET_RETURN] = Tag.Int
    inline def apply(inline o: CInt): SSL_TICKET_RETURN = o
    extension (v: SSL_TICKET_RETURN) inline def value: CInt = v

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_TICKET_STATUS = CInt
  object SSL_TICKET_STATUS:
    given _tag: Tag[SSL_TICKET_STATUS] = Tag.Int
    inline def apply(inline o: CInt): SSL_TICKET_STATUS = o
    extension (v: SSL_TICKET_STATUS) inline def value: CInt = v

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_allow_early_data_cb_fn = CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt]
  object SSL_allow_early_data_cb_fn:
    given _tag: Tag[SSL_allow_early_data_cb_fn] =
      Tag.materializeCFuncPtr2[Ptr[SSL], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_allow_early_data_cb_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(inline o: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt]): SSL_allow_early_data_cb_fn = o
    extension (v: SSL_allow_early_data_cb_fn)
      inline def value: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_async_callback_fn = CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt]
  object SSL_async_callback_fn:
    given _tag: Tag[SSL_async_callback_fn] = Tag.materializeCFuncPtr2[Ptr[SSL], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_async_callback_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(inline o: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt]): SSL_async_callback_fn = o
    extension (v: SSL_async_callback_fn)
      inline def value: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_client_hello_cb_fn = CFuncPtr3[Ptr[SSL], Ptr[CInt], Ptr[Byte], CInt]
  object SSL_client_hello_cb_fn:
    given _tag: Tag[SSL_client_hello_cb_fn] =
      Tag.materializeCFuncPtr3[Ptr[SSL], Ptr[CInt], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_client_hello_cb_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[SSL], Ptr[CInt], Ptr[Byte], CInt],
    ): SSL_client_hello_cb_fn = o
    extension (v: SSL_client_hello_cb_fn)
      inline def value: CFuncPtr3[Ptr[SSL], Ptr[CInt], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_custom_ext_add_cb_ex = CFuncPtr9[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[
    Ptr[CUnsignedChar],
  ], Ptr[size_t], Ptr[X509], size_t, Ptr[CInt], Ptr[Byte], CInt]
  object SSL_custom_ext_add_cb_ex:
    given _tag: Tag[SSL_custom_ext_add_cb_ex] =
      Tag.materializeCFuncPtr9[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[Ptr[CUnsignedChar]], Ptr[
        size_t,
      ], Ptr[X509], size_t, Ptr[CInt], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_custom_ext_add_cb_ex =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr9[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[Ptr[CUnsignedChar]], Ptr[
          size_t,
        ], Ptr[X509], size_t, Ptr[CInt], Ptr[Byte], CInt],
    ): SSL_custom_ext_add_cb_ex = o
    extension (v: SSL_custom_ext_add_cb_ex)
      inline def value
          : CFuncPtr9[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[Ptr[CUnsignedChar]], Ptr[
            size_t,
          ], Ptr[X509], size_t, Ptr[CInt], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_custom_ext_free_cb_ex =
    CFuncPtr5[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[CUnsignedChar], Ptr[Byte], Unit]
  object SSL_custom_ext_free_cb_ex:
    given _tag: Tag[SSL_custom_ext_free_cb_ex] = Tag
      .materializeCFuncPtr5[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[CUnsignedChar], Ptr[
        Byte,
      ], Unit]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_custom_ext_free_cb_ex =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr5[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[CUnsignedChar], Ptr[
          Byte,
        ], Unit],
    ): SSL_custom_ext_free_cb_ex = o
    extension (v: SSL_custom_ext_free_cb_ex)
      inline def value
          : CFuncPtr5[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[CUnsignedChar], Ptr[Byte], Unit] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_custom_ext_parse_cb_ex = CFuncPtr9[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[
    CUnsignedChar,
  ], size_t, Ptr[X509], size_t, Ptr[CInt], Ptr[Byte], CInt]
  object SSL_custom_ext_parse_cb_ex:
    given _tag: Tag[SSL_custom_ext_parse_cb_ex] =
      Tag
        .materializeCFuncPtr9[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[CUnsignedChar], size_t, Ptr[
          X509,
        ], size_t, Ptr[CInt], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_custom_ext_parse_cb_ex =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr9[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[CUnsignedChar], size_t, Ptr[
          X509,
        ], size_t, Ptr[CInt], Ptr[Byte], CInt],
    ): SSL_custom_ext_parse_cb_ex = o
    extension (v: SSL_custom_ext_parse_cb_ex)
      inline def value: CFuncPtr9[Ptr[SSL], CUnsignedInt, CUnsignedInt, Ptr[
        CUnsignedChar,
      ], size_t, Ptr[X509], size_t, Ptr[CInt], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_new_pending_conn_cb_fn = CFuncPtr3[Ptr[SSL_CTX], Ptr[SSL], Ptr[Byte], CInt]
  object SSL_new_pending_conn_cb_fn:
    given _tag: Tag[SSL_new_pending_conn_cb_fn] =
      Tag.materializeCFuncPtr3[Ptr[SSL_CTX], Ptr[SSL], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_new_pending_conn_cb_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[SSL_CTX], Ptr[SSL], Ptr[Byte], CInt],
    ): SSL_new_pending_conn_cb_fn = o
    extension (v: SSL_new_pending_conn_cb_fn)
      inline def value: CFuncPtr3[Ptr[SSL_CTX], Ptr[SSL], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_psk_client_cb_func = CFuncPtr6[Ptr[SSL], CString, CString, CUnsignedInt, Ptr[
    CUnsignedChar,
  ], CUnsignedInt, CUnsignedInt]
  object SSL_psk_client_cb_func:
    given _tag: Tag[SSL_psk_client_cb_func] = Tag.materializeCFuncPtr6[Ptr[
      SSL,
    ], CString, CString, CUnsignedInt, Ptr[CUnsignedChar], CUnsignedInt, CUnsignedInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_psk_client_cb_func =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr6[Ptr[SSL], CString, CString, CUnsignedInt, Ptr[
          CUnsignedChar,
        ], CUnsignedInt, CUnsignedInt],
    ): SSL_psk_client_cb_func = o
    extension (v: SSL_psk_client_cb_func)
      inline def value: CFuncPtr6[Ptr[SSL], CString, CString, CUnsignedInt, Ptr[
        CUnsignedChar,
      ], CUnsignedInt, CUnsignedInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_psk_find_session_cb_func =
    CFuncPtr4[Ptr[SSL], Ptr[CUnsignedChar], size_t, Ptr[Ptr[SSL_SESSION]], CInt]
  object SSL_psk_find_session_cb_func:
    given _tag: Tag[SSL_psk_find_session_cb_func] =
      Tag.materializeCFuncPtr4[Ptr[SSL], Ptr[CUnsignedChar], size_t, Ptr[Ptr[SSL_SESSION]], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_psk_find_session_cb_func =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr4[Ptr[SSL], Ptr[CUnsignedChar], size_t, Ptr[Ptr[SSL_SESSION]], CInt],
    ): SSL_psk_find_session_cb_func = o
    extension (v: SSL_psk_find_session_cb_func)
      inline def value
          : CFuncPtr4[Ptr[SSL], Ptr[CUnsignedChar], size_t, Ptr[Ptr[SSL_SESSION]], CInt] =
        v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_psk_server_cb_func =
    CFuncPtr4[Ptr[SSL], CString, Ptr[CUnsignedChar], CUnsignedInt, CUnsignedInt]
  object SSL_psk_server_cb_func:
    given _tag: Tag[SSL_psk_server_cb_func] =
      Tag.materializeCFuncPtr4[Ptr[SSL], CString, Ptr[CUnsignedChar], CUnsignedInt, CUnsignedInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_psk_server_cb_func =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr4[Ptr[SSL], CString, Ptr[CUnsignedChar], CUnsignedInt, CUnsignedInt],
    ): SSL_psk_server_cb_func = o
    extension (v: SSL_psk_server_cb_func)
      inline def value
          : CFuncPtr4[Ptr[SSL], CString, Ptr[CUnsignedChar], CUnsignedInt, CUnsignedInt] =
        v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_psk_use_session_cb_func = CFuncPtr5[Ptr[SSL], Ptr[EVP_MD], Ptr[
    Ptr[CUnsignedChar],
  ], Ptr[size_t], Ptr[Ptr[SSL_SESSION]], CInt]
  object SSL_psk_use_session_cb_func:
    given _tag: Tag[SSL_psk_use_session_cb_func] = Tag.materializeCFuncPtr5[Ptr[SSL], Ptr[
      EVP_MD,
    ], Ptr[Ptr[CUnsignedChar]], Ptr[size_t], Ptr[Ptr[SSL_SESSION]], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_psk_use_session_cb_func =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr5[Ptr[SSL], Ptr[EVP_MD], Ptr[Ptr[CUnsignedChar]], Ptr[size_t], Ptr[
          Ptr[SSL_SESSION],
        ], CInt],
    ): SSL_psk_use_session_cb_func = o
    extension (v: SSL_psk_use_session_cb_func)
      inline def value: CFuncPtr5[Ptr[SSL], Ptr[EVP_MD], Ptr[Ptr[CUnsignedChar]], Ptr[size_t], Ptr[
        Ptr[SSL_SESSION],
      ], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_verify_cb = CFuncPtr2[CInt, Ptr[X509_STORE_CTX], CInt]
  object SSL_verify_cb:
    given _tag: Tag[SSL_verify_cb] = Tag.materializeCFuncPtr2[CInt, Ptr[X509_STORE_CTX], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): SSL_verify_cb =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(inline o: CFuncPtr2[CInt, Ptr[X509_STORE_CTX], CInt]): SSL_verify_cb = o
    extension (v: SSL_verify_cb)
      inline def value: CFuncPtr2[CInt, Ptr[X509_STORE_CTX], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type custom_ext_add_cb = CFuncPtr6[Ptr[SSL], CUnsignedInt, Ptr[Ptr[CUnsignedChar]], Ptr[
    size_t,
  ], Ptr[CInt], Ptr[Byte], CInt]
  object custom_ext_add_cb:
    given _tag: Tag[custom_ext_add_cb] = Tag.materializeCFuncPtr6[Ptr[SSL], CUnsignedInt, Ptr[
      Ptr[CUnsignedChar],
    ], Ptr[size_t], Ptr[CInt], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): custom_ext_add_cb =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr6[Ptr[SSL], CUnsignedInt, Ptr[Ptr[CUnsignedChar]], Ptr[size_t], Ptr[
          CInt,
        ], Ptr[Byte], CInt],
    ): custom_ext_add_cb = o
    extension (v: custom_ext_add_cb)
      inline def value: CFuncPtr6[Ptr[SSL], CUnsignedInt, Ptr[Ptr[CUnsignedChar]], Ptr[size_t], Ptr[
        CInt,
      ], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type custom_ext_free_cb =
    CFuncPtr4[Ptr[SSL], CUnsignedInt, Ptr[CUnsignedChar], Ptr[Byte], Unit]
  object custom_ext_free_cb:
    given _tag: Tag[custom_ext_free_cb] =
      Tag.materializeCFuncPtr4[Ptr[SSL], CUnsignedInt, Ptr[CUnsignedChar], Ptr[Byte], Unit]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): custom_ext_free_cb =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr4[Ptr[SSL], CUnsignedInt, Ptr[CUnsignedChar], Ptr[Byte], Unit],
    ): custom_ext_free_cb = o
    extension (v: custom_ext_free_cb)
      inline def value: CFuncPtr4[Ptr[SSL], CUnsignedInt, Ptr[CUnsignedChar], Ptr[Byte], Unit] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type custom_ext_parse_cb =
    CFuncPtr6[Ptr[SSL], CUnsignedInt, Ptr[CUnsignedChar], size_t, Ptr[CInt], Ptr[Byte], CInt]
  object custom_ext_parse_cb:
    given _tag: Tag[custom_ext_parse_cb] = Tag.materializeCFuncPtr6[Ptr[SSL], CUnsignedInt, Ptr[
      CUnsignedChar,
    ], size_t, Ptr[CInt], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): custom_ext_parse_cb =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr6[Ptr[SSL], CUnsignedInt, Ptr[CUnsignedChar], size_t, Ptr[CInt], Ptr[
          Byte,
        ], CInt],
    ): custom_ext_parse_cb = o
    extension (v: custom_ext_parse_cb)
      inline def value
          : CFuncPtr6[Ptr[SSL], CUnsignedInt, Ptr[CUnsignedChar], size_t, Ptr[CInt], Ptr[
            Byte,
          ], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  type pem_password_cb = CFuncPtr4[CString, CInt, CInt, Ptr[Byte], CInt]
  object pem_password_cb:
    given _tag: Tag[pem_password_cb] =
      Tag.materializeCFuncPtr4[CString, CInt, CInt, Ptr[Byte], CInt]
    inline def apply(inline o: CFuncPtr4[CString, CInt, CInt, Ptr[Byte], CInt]): pem_password_cb = o
    extension (v: pem_password_cb)
      inline def value: CFuncPtr4[CString, CInt, CInt, Ptr[Byte], CInt] = v

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_crock_st = Ptr[ssl_st]
  object ssl_crock_st:
    given _tag: Tag[ssl_crock_st] = Tag.Ptr[ssl_st](ssl_st._tag)
    inline def apply(inline o: Ptr[ssl_st]): ssl_crock_st = o
    extension (v: ssl_crock_st) inline def value: Ptr[ssl_st] = v

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type ssl_ct_validation_cb =
    CFuncPtr3[Ptr[CT_POLICY_EVAL_CTX], Ptr[stack_st_SCT], Ptr[Byte], CInt]
  object ssl_ct_validation_cb:
    given _tag: Tag[ssl_ct_validation_cb] =
      Tag.materializeCFuncPtr3[Ptr[CT_POLICY_EVAL_CTX], Ptr[stack_st_SCT], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): ssl_ct_validation_cb =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[CT_POLICY_EVAL_CTX], Ptr[stack_st_SCT], Ptr[Byte], CInt],
    ): ssl_ct_validation_cb = o
    extension (v: ssl_ct_validation_cb)
      inline def value: CFuncPtr3[Ptr[CT_POLICY_EVAL_CTX], Ptr[stack_st_SCT], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type tls_session_secret_cb_fn = CFuncPtr6[Ptr[SSL], Ptr[Byte], Ptr[CInt], Ptr[
    stack_st_SSL_CIPHER,
  ], Ptr[Ptr[SSL_CIPHER]], Ptr[Byte], CInt]
  object tls_session_secret_cb_fn:
    given _tag: Tag[tls_session_secret_cb_fn] = Tag.materializeCFuncPtr6[Ptr[SSL], Ptr[Byte], Ptr[
      CInt,
    ], Ptr[stack_st_SSL_CIPHER], Ptr[Ptr[SSL_CIPHER]], Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): tls_session_secret_cb_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr6[Ptr[SSL], Ptr[Byte], Ptr[CInt], Ptr[stack_st_SSL_CIPHER], Ptr[
          Ptr[SSL_CIPHER],
        ], Ptr[Byte], CInt],
    ): tls_session_secret_cb_fn = o
    extension (v: tls_session_secret_cb_fn)
      inline def value: CFuncPtr6[Ptr[SSL], Ptr[Byte], Ptr[CInt], Ptr[stack_st_SSL_CIPHER], Ptr[
        Ptr[SSL_CIPHER],
      ], Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type tls_session_ticket_ext_cb_fn =
    CFuncPtr4[Ptr[SSL], Ptr[CUnsignedChar], CInt, Ptr[Byte], CInt]
  object tls_session_ticket_ext_cb_fn:
    given _tag: Tag[tls_session_ticket_ext_cb_fn] =
      Tag.materializeCFuncPtr4[Ptr[SSL], Ptr[CUnsignedChar], CInt, Ptr[Byte], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): tls_session_ticket_ext_cb_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr4[Ptr[SSL], Ptr[CUnsignedChar], CInt, Ptr[Byte], CInt],
    ): tls_session_ticket_ext_cb_fn = o
    extension (v: tls_session_ticket_ext_cb_fn)
      inline def value: CFuncPtr4[Ptr[SSL], Ptr[CUnsignedChar], CInt, Ptr[Byte], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)
