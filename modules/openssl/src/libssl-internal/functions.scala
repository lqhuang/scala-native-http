package snhttp.experimental.openssl.libssl_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

@link("openssl/ssl")
@extern
object functions:

  import _root_.snhttp.experimental.openssl.libssl_internal.aliases.*
  import _root_.snhttp.experimental.openssl.libssl_internal.structs.*
  import _root_.snhttp.experimental.openssl.libssl_internal.enumerations.*
  import _root_.snhttp.experimental.openssl.libssl_internal.unions.*

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def BIO_f_ssl(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def BIO_new_buffer_ssl_connect(ctx: Ptr[SSL_CTX]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def BIO_new_ssl(ctx: Ptr[SSL_CTX], client: CInt): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def BIO_new_ssl_connect(ctx: Ptr[SSL_CTX]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def BIO_ssl_copy_session_id(to: Ptr[BIO], from: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def BIO_ssl_shutdown(ssl_bio: Ptr[BIO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLS_client_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLS_get_data_mtu(s: Ptr[SSL]): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLS_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLS_server_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLS_set_timer_cb(s: Ptr[SSL], cb: DTLS_timer_cb): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLSv1_2_client_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLSv1_2_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLSv1_2_server_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLSv1_client_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLSv1_listen(s: Ptr[SSL], client: Ptr[BIO_ADDR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLSv1_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def DTLSv1_server_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def OPENSSL_cipher_name(rfc_name: CString): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def OPENSSL_init_ssl(opts: ULong, settings: Ptr[OPENSSL_INIT_SETTINGS]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def OSSL_default_cipher_list(): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def OSSL_default_ciphersuites(): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SRP_Calc_A_param(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_description(_0: Ptr[SSL_CIPHER], buf: CString, size: CInt): CString =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_find(ssl: Ptr[SSL], ptr: Ptr[CUnsignedChar]): Ptr[SSL_CIPHER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_auth_nid(c: Ptr[SSL_CIPHER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_bits(c: Ptr[SSL_CIPHER], alg_bits: Ptr[CInt]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_cipher_nid(c: Ptr[SSL_CIPHER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_digest_nid(c: Ptr[SSL_CIPHER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_handshake_digest(c: Ptr[SSL_CIPHER]): Ptr[EVP_MD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_id(c: Ptr[SSL_CIPHER]): UInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_kx_nid(c: Ptr[SSL_CIPHER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_name(c: Ptr[SSL_CIPHER]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_protocol_id(c: Ptr[SSL_CIPHER]): UShort = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_get_version(c: Ptr[SSL_CIPHER]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_is_aead(c: Ptr[SSL_CIPHER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CIPHER_standard_name(c: Ptr[SSL_CIPHER]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_COMP_add_compression_method(id: CInt, cm: Ptr[COMP_METHOD]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_COMP_get0_name(comp: Ptr[SSL_COMP]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_COMP_get_compression_methods(): Ptr[stack_st_SSL_COMP] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_COMP_get_id(comp: Ptr[SSL_COMP]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_COMP_get_name(comp: Ptr[COMP_METHOD]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_COMP_set0_compression_methods(
      meths: Ptr[stack_st_SSL_COMP],
  ): Ptr[stack_st_SSL_COMP] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_CTX_clear_flags(cctx: Ptr[SSL_CONF_CTX], flags: CUnsignedInt): CUnsignedInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_CTX_finish(cctx: Ptr[SSL_CONF_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_CTX_free(cctx: Ptr[SSL_CONF_CTX]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_CTX_new(): Ptr[SSL_CONF_CTX] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_CTX_set1_prefix(cctx: Ptr[SSL_CONF_CTX], pre: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_CTX_set_flags(cctx: Ptr[SSL_CONF_CTX], flags: CUnsignedInt): CUnsignedInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_CTX_set_ssl(cctx: Ptr[SSL_CONF_CTX], ssl: Ptr[SSL]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_CTX_set_ssl_ctx(cctx: Ptr[SSL_CONF_CTX], ctx: Ptr[SSL_CTX]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_cmd(cctx: Ptr[SSL_CONF_CTX], cmd: CString, value: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_cmd_argv(
      cctx: Ptr[SSL_CONF_CTX],
      pargc: Ptr[CInt],
      pargv: Ptr[Ptr[CString]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CONF_cmd_value_type(cctx: Ptr[SSL_CONF_CTX], cmd: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_SRP_CTX_free(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_SRP_CTX_init(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_add1_to_CA_list(ctx: Ptr[SSL_CTX], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_add_client_CA(ctx: Ptr[SSL_CTX], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_add_client_custom_ext(
      ctx: Ptr[SSL_CTX],
      ext_type: CUnsignedInt,
      add_cb: custom_ext_add_cb,
      free_cb: custom_ext_free_cb,
      add_arg: Ptr[Byte],
      parse_cb: custom_ext_parse_cb,
      parse_arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_add_custom_ext(
      ctx: Ptr[SSL_CTX],
      ext_type: CUnsignedInt,
      context: CUnsignedInt,
      add_cb: SSL_custom_ext_add_cb_ex,
      free_cb: SSL_custom_ext_free_cb_ex,
      add_arg: Ptr[Byte],
      parse_cb: SSL_custom_ext_parse_cb_ex,
      parse_arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_add_server_custom_ext(
      ctx: Ptr[SSL_CTX],
      ext_type: CUnsignedInt,
      add_cb: custom_ext_add_cb,
      free_cb: custom_ext_free_cb,
      add_arg: Ptr[Byte],
      parse_cb: custom_ext_parse_cb,
      parse_arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_add_session(ctx: Ptr[SSL_CTX], session: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_callback_ctrl(_0: Ptr[SSL_CTX], _1: CInt, _2: CFuncPtr0[Unit]): CLongInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_check_private_key(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_clear_options(ctx: Ptr[SSL_CTX], op: ULong): ULong = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_compress_certs(ctx: Ptr[SSL_CTX], alg: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_config(ctx: Ptr[SSL_CTX], name: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_ct_is_enabled(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_ctrl(
      ctx: Ptr[SSL_CTX],
      cmd: CInt,
      larg: CLongInt,
      parg: Ptr[Byte],
  ): CLongInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_dane_clear_flags(
      ctx: Ptr[SSL_CTX],
      flags: CUnsignedLongInt,
  ): CUnsignedLongInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_dane_enable(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_dane_mtype_set(
      ctx: Ptr[SSL_CTX],
      md: Ptr[EVP_MD],
      mtype: UByte,
      ord: UByte,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_dane_set_flags(ctx: Ptr[SSL_CTX], flags: CUnsignedLongInt): CUnsignedLongInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_enable_ct(ctx: Ptr[SSL_CTX], validation_mode: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_flush_sessions(ctx: Ptr[SSL_CTX], tm: CLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_flush_sessions_ex(ctx: Ptr[SSL_CTX], tm: time_t): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_free(_0: Ptr[SSL_CTX]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get0_CA_list(ctx: Ptr[SSL_CTX]): Ptr[stack_st_X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get0_certificate(ctx: Ptr[SSL_CTX]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get0_client_cert_type(
      ctx: Ptr[SSL_CTX],
      t: Ptr[Ptr[CUnsignedChar]],
      len: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get0_ctlog_store(ctx: Ptr[SSL_CTX]): Ptr[CTLOG_STORE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get0_param(ctx: Ptr[SSL_CTX]): Ptr[X509_VERIFY_PARAM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get0_privatekey(ctx: Ptr[SSL_CTX]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get0_security_ex_data(ctx: Ptr[SSL_CTX]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get0_server_cert_type(
      s: Ptr[SSL_CTX],
      t: Ptr[Ptr[CUnsignedChar]],
      len: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get1_compressed_cert(
      ctx: Ptr[SSL_CTX],
      alg: CInt,
      data: Ptr[Ptr[CUnsignedChar]],
      orig_len: Ptr[USize],
  ): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_cert_store(_0: Ptr[SSL_CTX]): Ptr[X509_STORE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_ciphers(ctx: Ptr[SSL_CTX]): Ptr[stack_st_SSL_CIPHER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_client_CA_list(s: Ptr[SSL_CTX]): Ptr[stack_st_X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_client_cert_cb(
      ssl: Ptr[SSL],
  ): CFuncPtr3[Ptr[SSL], Ptr[Ptr[X509]], Ptr[Ptr[EVP_PKEY]], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_default_passwd_cb(ctx: Ptr[SSL_CTX]): Ptr[pem_password_cb] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_default_passwd_cb_userdata(ctx: Ptr[SSL_CTX]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_domain_flags(ctx: Ptr[SSL_CTX], domain_flags: Ptr[ULong]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_ex_data(ssl: Ptr[SSL_CTX], idx: CInt): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_info_callback(ssl: Ptr[SSL]): CFuncPtr3[Ptr[SSL], CInt, CInt, Unit] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_keylog_callback(ctx: Ptr[SSL_CTX]): SSL_CTX_keylog_cb_func = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_max_early_data(ctx: Ptr[SSL_CTX]): UInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_num_tickets(ctx: Ptr[SSL_CTX]): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_options(ctx: Ptr[SSL_CTX]): ULong = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_quiet_shutdown(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_record_padding_callback_arg(ctx: Ptr[SSL_CTX]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_recv_max_early_data(ctx: Ptr[SSL_CTX]): UInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_security_callback(
      s: Ptr[SSL],
  ): CFuncPtr7[Ptr[SSL], Ptr[SSL_CTX], CInt, CInt, CInt, Ptr[Byte], Ptr[Byte], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_security_level(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_ssl_method(ctx: Ptr[SSL_CTX]): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_timeout(ctx: Ptr[SSL_CTX]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_verify_callback(ctx: Ptr[SSL_CTX]): SSL_verify_cb = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_verify_depth(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_get_verify_mode(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_has_client_custom_ext(ctx: Ptr[SSL_CTX], ext_type: CUnsignedInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_load_verify_dir(ctx: Ptr[SSL_CTX], CApath: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_load_verify_file(ctx: Ptr[SSL_CTX], CAfile: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_load_verify_locations(
      ctx: Ptr[SSL_CTX],
      CAfile: CString,
      CApath: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_load_verify_store(ctx: Ptr[SSL_CTX], CAstore: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_new(meth: Ptr[SSL_METHOD]): Ptr[SSL_CTX] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_new_ex(
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
      meth: Ptr[SSL_METHOD],
  ): Ptr[SSL_CTX] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_remove_session(ctx: Ptr[SSL_CTX], session: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_sess_get_get_cb(
      ssl: Ptr[ssl_st],
  ): CFuncPtr4[Ptr[ssl_st], Ptr[CUnsignedChar], CInt, Ptr[CInt], Ptr[SSL_SESSION]] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_sess_get_new_cb(
      ssl: Ptr[ssl_st],
  ): CFuncPtr2[Ptr[ssl_st], Ptr[SSL_SESSION], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_sess_get_remove_cb(
      ctx: Ptr[ssl_ctx_st],
  ): CFuncPtr2[Ptr[ssl_ctx_st], Ptr[SSL_SESSION], Unit] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_sess_set_get_cb(
      ctx: Ptr[SSL_CTX],
      get_session_cb: CFuncPtr4[Ptr[ssl_st], Ptr[CUnsignedChar], CInt, Ptr[CInt], Ptr[SSL_SESSION]],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_sess_set_new_cb(
      ctx: Ptr[SSL_CTX],
      new_session_cb: CFuncPtr2[Ptr[ssl_st], Ptr[SSL_SESSION], CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_sess_set_remove_cb(
      ctx: Ptr[SSL_CTX],
      remove_session_cb: CFuncPtr2[Ptr[ssl_ctx_st], Ptr[SSL_SESSION], Unit],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_sessions(ctx: Ptr[SSL_CTX]): Ptr[lhash_st_SSL_SESSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set0_CA_list(ctx: Ptr[SSL_CTX], name_list: Ptr[stack_st_X509_NAME]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set0_ctlog_store(ctx: Ptr[SSL_CTX], logs: Ptr[CTLOG_STORE]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set0_security_ex_data(ctx: Ptr[SSL_CTX], ex: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set0_tmp_dh_pkey(ctx: Ptr[SSL_CTX], dhpkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set1_cert_comp_preference(
      ctx: Ptr[SSL_CTX],
      algs: Ptr[CInt],
      len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set1_cert_store(_0: Ptr[SSL_CTX], _1: Ptr[X509_STORE]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set1_client_cert_type(
      ctx: Ptr[SSL_CTX],
      `val`: Ptr[CUnsignedChar],
      len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set1_compressed_cert(
      ctx: Ptr[SSL_CTX],
      algorithm: CInt,
      comp_data: Ptr[CUnsignedChar],
      comp_length: USize,
      orig_length: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set1_param(ctx: Ptr[SSL_CTX], vpm: Ptr[X509_VERIFY_PARAM]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set1_server_cert_type(
      ctx: Ptr[SSL_CTX],
      `val`: Ptr[CUnsignedChar],
      len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_allow_early_data_cb(
      ctx: Ptr[SSL_CTX],
      cb: SSL_allow_early_data_cb_fn,
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_alpn_protos(
      ctx: Ptr[SSL_CTX],
      protos: Ptr[CUnsignedChar],
      protos_len: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_alpn_select_cb(
      ctx: Ptr[SSL_CTX],
      cb: SSL_CTX_alpn_select_cb_func,
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_async_callback(ctx: Ptr[SSL_CTX], callback: SSL_async_callback_fn): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_async_callback_arg(ctx: Ptr[SSL_CTX], arg: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_block_padding(ctx: Ptr[SSL_CTX], block_size: USize): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_block_padding_ex(
      ctx: Ptr[SSL_CTX],
      app_block_size: USize,
      hs_block_size: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_cert_cb(
      c: Ptr[SSL_CTX],
      cb: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt],
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_cert_store(_0: Ptr[SSL_CTX], _1: Ptr[X509_STORE]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_cert_verify_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr2[Ptr[X509_STORE_CTX], Ptr[Byte], CInt],
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_cipher_list(_0: Ptr[SSL_CTX], str: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_ciphersuites(ctx: Ptr[SSL_CTX], str: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_client_CA_list(
      ctx: Ptr[SSL_CTX],
      name_list: Ptr[stack_st_X509_NAME],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_client_cert_cb(
      ctx: Ptr[SSL_CTX],
      client_cert_cb: CFuncPtr3[Ptr[SSL], Ptr[Ptr[X509]], Ptr[Ptr[EVP_PKEY]], CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_client_cert_engine(ctx: Ptr[SSL_CTX], e: Ptr[ENGINE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_client_hello_cb(
      c: Ptr[SSL_CTX],
      cb: SSL_client_hello_cb_fn,
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_cookie_generate_cb(
      ctx: Ptr[SSL_CTX],
      app_gen_cookie_cb: CFuncPtr3[Ptr[SSL], Ptr[CUnsignedChar], Ptr[CUnsignedInt], CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_cookie_verify_cb(
      ctx: Ptr[SSL_CTX],
      app_verify_cookie_cb: CFuncPtr3[Ptr[SSL], Ptr[CUnsignedChar], CUnsignedInt, CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_ct_validation_callback(
      ctx: Ptr[SSL_CTX],
      callback: ssl_ct_validation_cb,
      arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_ctlog_list_file(ctx: Ptr[SSL_CTX], path: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_default_ctlog_list_file(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_default_passwd_cb(ctx: Ptr[SSL_CTX], cb: Ptr[pem_password_cb]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_default_passwd_cb_userdata(ctx: Ptr[SSL_CTX], u: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_default_read_buffer_len(ctx: Ptr[SSL_CTX], len: USize): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_default_verify_dir(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_default_verify_file(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_default_verify_paths(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_default_verify_store(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_domain_flags(ctx: Ptr[SSL_CTX], domain_flags: ULong): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_ex_data(ssl: Ptr[SSL_CTX], idx: CInt, data: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_generate_session_id(ctx: Ptr[SSL_CTX], cb: GEN_SESSION_CB): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_info_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr3[Ptr[SSL], CInt, CInt, Unit],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_keylog_callback(ctx: Ptr[SSL_CTX], cb: SSL_CTX_keylog_cb_func): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_max_early_data(ctx: Ptr[SSL_CTX], max_early_data: UInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_msg_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr7[CInt, CInt, CInt, Ptr[Byte], USize, Ptr[SSL], Ptr[Byte], Unit],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_new_pending_conn_cb(
      c: Ptr[SSL_CTX],
      cb: SSL_new_pending_conn_cb_fn,
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_next_proto_select_cb(
      s: Ptr[SSL_CTX],
      cb: SSL_CTX_npn_select_cb_func,
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_next_protos_advertised_cb(
      s: Ptr[SSL_CTX],
      cb: SSL_CTX_npn_advertised_cb_func,
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_not_resumable_session_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr2[Ptr[SSL], CInt, CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_num_tickets(ctx: Ptr[SSL_CTX], num_tickets: USize): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_options(ctx: Ptr[SSL_CTX], op: ULong): ULong = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_post_handshake_auth(ctx: Ptr[SSL_CTX], `val`: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_psk_client_callback(ctx: Ptr[SSL_CTX], cb: SSL_psk_client_cb_func): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_psk_find_session_callback(
      ctx: Ptr[SSL_CTX],
      cb: SSL_psk_find_session_cb_func,
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_psk_server_callback(ctx: Ptr[SSL_CTX], cb: SSL_psk_server_cb_func): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_psk_use_session_callback(
      ctx: Ptr[SSL_CTX],
      cb: SSL_psk_use_session_cb_func,
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_purpose(ctx: Ptr[SSL_CTX], purpose: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_quiet_shutdown(ctx: Ptr[SSL_CTX], mode: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_record_padding_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr4[Ptr[SSL], CInt, USize, Ptr[Byte], USize],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_record_padding_callback_arg(ctx: Ptr[SSL_CTX], arg: Ptr[Byte]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_recv_max_early_data(
      ctx: Ptr[SSL_CTX],
      recv_max_early_data: UInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_security_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr7[Ptr[SSL], Ptr[SSL_CTX], CInt, CInt, CInt, Ptr[Byte], Ptr[Byte], CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_security_level(ctx: Ptr[SSL_CTX], level: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_session_id_context(
      ctx: Ptr[SSL_CTX],
      sid_ctx: Ptr[CUnsignedChar],
      sid_ctx_len: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_session_ticket_cb(
      ctx: Ptr[SSL_CTX],
      gen_cb: SSL_CTX_generate_session_ticket_fn,
      dec_cb: SSL_CTX_decrypt_session_ticket_fn,
      arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_srp_cb_arg(ctx: Ptr[SSL_CTX], arg: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_srp_client_pwd_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr2[Ptr[SSL], Ptr[Byte], CString],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_srp_password(ctx: Ptr[SSL_CTX], password: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_srp_strength(ctx: Ptr[SSL_CTX], strength: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_srp_username(ctx: Ptr[SSL_CTX], name: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_srp_username_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr3[Ptr[SSL], Ptr[CInt], Ptr[Byte], CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_srp_verify_param_callback(
      ctx: Ptr[SSL_CTX],
      cb: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_ssl_version(ctx: Ptr[SSL_CTX], meth: Ptr[SSL_METHOD]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_stateless_cookie_generate_cb(
      ctx: Ptr[SSL_CTX],
      gen_stateless_cookie_cb: CFuncPtr3[Ptr[SSL], Ptr[CUnsignedChar], Ptr[USize], CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_stateless_cookie_verify_cb(
      ctx: Ptr[SSL_CTX],
      verify_stateless_cookie_cb: CFuncPtr3[Ptr[SSL], Ptr[CUnsignedChar], USize, CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_timeout(ctx: Ptr[SSL_CTX], t: CLongInt): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_tmp_dh_callback(
      ctx: Ptr[SSL_CTX],
      dh: CFuncPtr3[Ptr[SSL], CInt, CInt, Ptr[DH]],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_trust(ctx: Ptr[SSL_CTX], trust: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_verify(ctx: Ptr[SSL_CTX], mode: CInt, callback: SSL_verify_cb): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_set_verify_depth(ctx: Ptr[SSL_CTX], depth: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_up_ref(ctx: Ptr[SSL_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_PrivateKey(ctx: Ptr[SSL_CTX], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_PrivateKey_ASN1(
      pk: CInt,
      ctx: Ptr[SSL_CTX],
      d: Ptr[CUnsignedChar],
      len: CLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_PrivateKey_file(ctx: Ptr[SSL_CTX], file: CString, `type`: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_RSAPrivateKey(ctx: Ptr[SSL_CTX], rsa: Ptr[RSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_RSAPrivateKey_ASN1(
      ctx: Ptr[SSL_CTX],
      d: Ptr[CUnsignedChar],
      len: CLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_RSAPrivateKey_file(ctx: Ptr[SSL_CTX], file: CString, `type`: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_cert_and_key(
      ctx: Ptr[SSL_CTX],
      x509: Ptr[X509],
      privatekey: Ptr[EVP_PKEY],
      chain: Ptr[stack_st_X509],
      `override`: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_certificate(ctx: Ptr[SSL_CTX], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_certificate_ASN1(
      ctx: Ptr[SSL_CTX],
      len: CInt,
      d: Ptr[CUnsignedChar],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_certificate_chain_file(ctx: Ptr[SSL_CTX], file: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_certificate_file(ctx: Ptr[SSL_CTX], file: CString, `type`: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_psk_identity_hint(ctx: Ptr[SSL_CTX], identity_hint: CString): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_serverinfo(
      ctx: Ptr[SSL_CTX],
      serverinfo: Ptr[CUnsignedChar],
      serverinfo_length: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_serverinfo_ex(
      ctx: Ptr[SSL_CTX],
      version: CUnsignedInt,
      serverinfo: Ptr[CUnsignedChar],
      serverinfo_length: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_CTX_use_serverinfo_file(ctx: Ptr[SSL_CTX], file: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_dup(src: Ptr[SSL_SESSION]): Ptr[SSL_SESSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_free(ses: Ptr[SSL_SESSION]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get0_alpn_selected(
      s: Ptr[SSL_SESSION],
      alpn: Ptr[Ptr[CUnsignedChar]],
      len: Ptr[USize],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get0_cipher(s: Ptr[SSL_SESSION]): Ptr[SSL_CIPHER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get0_hostname(s: Ptr[SSL_SESSION]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get0_id_context(
      s: Ptr[SSL_SESSION],
      len: Ptr[CUnsignedInt],
  ): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get0_peer(s: Ptr[SSL_SESSION]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get0_peer_rpk(s: Ptr[SSL_SESSION]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get0_ticket(
      s: Ptr[SSL_SESSION],
      tick: Ptr[Ptr[CUnsignedChar]],
      len: Ptr[USize],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get0_ticket_appdata(
      ss: Ptr[SSL_SESSION],
      data: Ptr[Ptr[Byte]],
      len: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_compress_id(s: Ptr[SSL_SESSION]): CUnsignedInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_ex_data(ss: Ptr[SSL_SESSION], idx: CInt): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_id(s: Ptr[SSL_SESSION], len: Ptr[CUnsignedInt]): Ptr[CUnsignedChar] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_master_key(
      sess: Ptr[SSL_SESSION],
      out: Ptr[CUnsignedChar],
      outlen: USize,
  ): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_max_early_data(s: Ptr[SSL_SESSION]): UInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_max_fragment_length(sess: Ptr[SSL_SESSION]): UByte = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_protocol_version(s: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_ticket_lifetime_hint(s: Ptr[SSL_SESSION]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_time(s: Ptr[SSL_SESSION]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_time_ex(s: Ptr[SSL_SESSION]): time_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_get_timeout(s: Ptr[SSL_SESSION]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_has_ticket(s: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_is_resumable(s: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_new(): Ptr[SSL_SESSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_print(fp: Ptr[BIO], ses: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_print_fp(fp: Ptr[FILE], ses: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_print_keylog(bp: Ptr[BIO], x: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set1_alpn_selected(
      s: Ptr[SSL_SESSION],
      alpn: Ptr[CUnsignedChar],
      len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set1_hostname(s: Ptr[SSL_SESSION], hostname: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set1_id(
      s: Ptr[SSL_SESSION],
      sid: Ptr[CUnsignedChar],
      sid_len: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set1_id_context(
      s: Ptr[SSL_SESSION],
      sid_ctx: Ptr[CUnsignedChar],
      sid_ctx_len: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set1_master_key(
      sess: Ptr[SSL_SESSION],
      in: Ptr[CUnsignedChar],
      len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set1_ticket_appdata(
      ss: Ptr[SSL_SESSION],
      data: Ptr[Byte],
      len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set_cipher(s: Ptr[SSL_SESSION], cipher: Ptr[SSL_CIPHER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set_ex_data(ss: Ptr[SSL_SESSION], idx: CInt, data: Ptr[Byte]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set_max_early_data(s: Ptr[SSL_SESSION], max_early_data: UInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set_protocol_version(s: Ptr[SSL_SESSION], version: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set_time(s: Ptr[SSL_SESSION], t: CLongInt): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set_time_ex(s: Ptr[SSL_SESSION], t: time_t): time_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_set_timeout(s: Ptr[SSL_SESSION], t: CLongInt): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SESSION_up_ref(ses: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SRP_CTX_free(ctx: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_SRP_CTX_init(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_accept(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_accept_connection(ssl: Ptr[SSL], flags: ULong): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_accept_stream(s: Ptr[SSL], flags: ULong): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_add1_host(s: Ptr[SSL], host: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_add1_to_CA_list(ssl: Ptr[SSL], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_add_client_CA(ssl: Ptr[SSL], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_add_dir_cert_subjects_to_stack(
      stackCAs: Ptr[stack_st_X509_NAME],
      dir: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_add_expected_rpk(s: Ptr[SSL], rpk: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_add_file_cert_subjects_to_stack(
      stackCAs: Ptr[stack_st_X509_NAME],
      file: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_add_ssl_module(): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_add_store_cert_subjects_to_stack(
      stackCAs: Ptr[stack_st_X509_NAME],
      uri: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_alert_desc_string(value: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_alert_desc_string_long(value: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_alert_type_string(value: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_alert_type_string_long(value: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_alloc_buffers(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_bytes_to_cipher_list(
      s: Ptr[SSL],
      bytes: Ptr[CUnsignedChar],
      len: USize,
      isv2format: CInt,
      sk: Ptr[Ptr[stack_st_SSL_CIPHER]],
      scsvs: Ptr[Ptr[stack_st_SSL_CIPHER]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_callback_ctrl(_0: Ptr[SSL], _1: CInt, _2: CFuncPtr0[Unit]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_certs_clear(s: Ptr[SSL]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_check_private_key(ctx: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_clear(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_clear_options(s: Ptr[SSL], op: ULong): ULong = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_get0_ciphers(s: Ptr[SSL], out: Ptr[Ptr[CUnsignedChar]]): USize =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_get0_compression_methods(
      s: Ptr[SSL],
      out: Ptr[Ptr[CUnsignedChar]],
  ): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_get0_ext(
      s: Ptr[SSL],
      `type`: CUnsignedInt,
      out: Ptr[Ptr[CUnsignedChar]],
      outlen: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_get0_legacy_version(s: Ptr[SSL]): CUnsignedInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_get0_random(s: Ptr[SSL], out: Ptr[Ptr[CUnsignedChar]]): USize =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_get0_session_id(s: Ptr[SSL], out: Ptr[Ptr[CUnsignedChar]]): USize =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_get1_extensions_present(
      s: Ptr[SSL],
      out: Ptr[Ptr[CInt]],
      outlen: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_get_extension_order(
      s: Ptr[SSL],
      exts: Ptr[UShort],
      num_exts: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_hello_isv2(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_client_version(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_compress_certs(ssl: Ptr[SSL], alg: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_config(s: Ptr[SSL], name: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_connect(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_copy_session_id(to: Ptr[SSL], from: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_ct_is_enabled(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_ctrl(ssl: Ptr[SSL], cmd: CInt, larg: CLongInt, parg: Ptr[Byte]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_dane_clear_flags(ssl: Ptr[SSL], flags: CUnsignedLongInt): CUnsignedLongInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_dane_enable(s: Ptr[SSL], basedomain: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_dane_set_flags(ssl: Ptr[SSL], flags: CUnsignedLongInt): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_dane_tlsa_add(
      s: Ptr[SSL],
      usage: UByte,
      selector: UByte,
      mtype: UByte,
      data: Ptr[CUnsignedChar],
      dlen: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_do_handshake(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_dup(ssl: Ptr[SSL]): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_dup_CA_list(sk: Ptr[stack_st_X509_NAME]): Ptr[stack_st_X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_enable_ct(s: Ptr[SSL], validation_mode: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_extension_supported(ext_type: CUnsignedInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_free(ssl: Ptr[SSL]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_free_buffers(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_CA_list(s: Ptr[SSL]): Ptr[stack_st_X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_alpn_selected(
      ssl: Ptr[SSL],
      data: Ptr[Ptr[CUnsignedChar]],
      len: Ptr[CUnsignedInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_client_cert_type(
      s: Ptr[SSL],
      t: Ptr[Ptr[CUnsignedChar]],
      len: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_connection(s: Ptr[SSL]): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_dane(ssl: Ptr[SSL]): Ptr[SSL_DANE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_dane_authority(
      s: Ptr[SSL],
      mcert: Ptr[Ptr[X509]],
      mspki: Ptr[Ptr[EVP_PKEY]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_dane_tlsa(
      s: Ptr[SSL],
      usage: Ptr[UByte],
      selector: Ptr[UByte],
      mtype: Ptr[UByte],
      data: Ptr[Ptr[CUnsignedChar]],
      dlen: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_domain(s: Ptr[SSL]): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_group_name(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_listener(s: Ptr[SSL]): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_next_proto_negotiated(
      s: Ptr[SSL],
      data: Ptr[Ptr[CUnsignedChar]],
      len: Ptr[CUnsignedInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_param(ssl: Ptr[SSL]): Ptr[X509_VERIFY_PARAM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_peer_CA_list(s: Ptr[SSL]): Ptr[stack_st_X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_peer_certificate(s: Ptr[SSL]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_peer_rpk(s: Ptr[SSL]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_peer_scts(s: Ptr[SSL]): Ptr[stack_st_SCT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_peername(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_security_ex_data(s: Ptr[SSL]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_server_cert_type(
      s: Ptr[SSL],
      t: Ptr[Ptr[CUnsignedChar]],
      len: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get0_verified_chain(s: Ptr[SSL]): Ptr[stack_st_X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get1_compressed_cert(
      ssl: Ptr[SSL],
      alg: CInt,
      data: Ptr[Ptr[CUnsignedChar]],
      orig_len: Ptr[USize],
  ): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get1_peer_certificate(s: Ptr[SSL]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get1_session(ssl: Ptr[SSL]): Ptr[SSL_SESSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get1_supported_ciphers(s: Ptr[SSL]): Ptr[stack_st_SSL_CIPHER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_SSL_CTX(ssl: Ptr[SSL]): Ptr[SSL_CTX] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_accept_connection_queue_len(ssl: Ptr[SSL]): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_accept_stream_queue_len(s: Ptr[SSL]): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_all_async_fds(s: Ptr[SSL], fds: Ptr[CInt], numfds: Ptr[USize]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_async_status(s: Ptr[SSL], status: Ptr[CInt]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_blocking_mode(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_certificate(ssl: Ptr[SSL]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_changed_async_fds(
      s: Ptr[SSL],
      addfd: Ptr[CInt],
      numaddfds: Ptr[USize],
      delfd: Ptr[CInt],
      numdelfds: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_cipher_list(s: Ptr[SSL], n: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_ciphers(s: Ptr[SSL]): Ptr[stack_st_SSL_CIPHER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_client_CA_list(s: Ptr[SSL]): Ptr[stack_st_X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_client_ciphers(s: Ptr[SSL]): Ptr[stack_st_SSL_CIPHER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_client_random(
      ssl: Ptr[SSL],
      out: Ptr[CUnsignedChar],
      outlen: USize,
  ): USize =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_conn_close_info(
      ssl: Ptr[SSL],
      info: Ptr[SSL_CONN_CLOSE_INFO],
      info_len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_current_cipher(s: Ptr[SSL]): Ptr[SSL_CIPHER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_current_compression(s: Ptr[SSL]): Ptr[COMP_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_current_expansion(s: Ptr[SSL]): Ptr[COMP_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_default_passwd_cb(s: Ptr[SSL]): Ptr[pem_password_cb] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_default_passwd_cb_userdata(s: Ptr[SSL]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_default_timeout(s: Ptr[SSL]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_domain_flags(ssl: Ptr[SSL], domain_flags: Ptr[ULong]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_early_data_status(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_error(s: Ptr[SSL], ret_code: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_event_timeout(s: Ptr[SSL], tv: Ptr[timeval], is_infinite: Ptr[CInt]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_ex_data(ssl: Ptr[SSL], idx: CInt): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_ex_data_X509_STORE_CTX_idx(): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_fd(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_finished(s: Ptr[SSL], buf: Ptr[Byte], count: USize): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_handshake_rtt(s: Ptr[SSL], rtt: Ptr[ULong]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_info_callback(ssl: Ptr[SSL]): CFuncPtr3[Ptr[SSL], CInt, CInt, Unit] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_key_update_type(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_max_early_data(s: Ptr[SSL]): UInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_negotiated_client_cert_type(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_negotiated_server_cert_type(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_num_tickets(s: Ptr[SSL]): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_options(s: Ptr[SSL]): ULong = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_peer_cert_chain(s: Ptr[SSL]): Ptr[stack_st_X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_peer_finished(s: Ptr[SSL], buf: Ptr[Byte], count: USize): USize = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_pending_cipher(s: Ptr[SSL]): Ptr[SSL_CIPHER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_privatekey(ssl: Ptr[SSL]): Ptr[evp_pkey_st] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_psk_identity(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_psk_identity_hint(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_quiet_shutdown(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_rbio(s: Ptr[SSL]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_read_ahead(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_record_padding_callback_arg(ssl: Ptr[SSL]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_recv_max_early_data(s: Ptr[SSL]): UInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_rfd(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_rpoll_descriptor(s: Ptr[SSL], desc: Ptr[BIO_POLL_DESCRIPTOR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_security_callback(
      s: Ptr[SSL],
  ): CFuncPtr7[Ptr[SSL], Ptr[SSL_CTX], CInt, CInt, CInt, Ptr[Byte], Ptr[Byte], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_security_level(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_server_random(
      ssl: Ptr[SSL],
      out: Ptr[CUnsignedChar],
      outlen: USize,
  ): USize =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_session(ssl: Ptr[SSL]): Ptr[SSL_SESSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_shared_ciphers(s: Ptr[SSL], buf: CString, size: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_shutdown(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_srp_N(s: Ptr[SSL]): Ptr[BIGNUM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_srp_g(s: Ptr[SSL]): Ptr[BIGNUM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_srp_userinfo(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_srp_username(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_ssl_method(s: Ptr[SSL]): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_state(ssl: Ptr[SSL]): OSSL_HANDSHAKE_STATE = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_stream_id(s: Ptr[SSL]): ULong = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_stream_read_error_code(ssl: Ptr[SSL], app_error_code: Ptr[ULong]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_stream_read_state(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_stream_type(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_stream_write_error_code(ssl: Ptr[SSL], app_error_code: Ptr[ULong]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_stream_write_state(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_value_uint(
      s: Ptr[SSL],
      `class_`: UInt,
      id: UInt,
      v: Ptr[ULong],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_verify_callback(s: Ptr[SSL]): SSL_verify_cb = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_verify_depth(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_verify_mode(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_verify_result(ssl: Ptr[SSL]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_version(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_wbio(s: Ptr[SSL]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_wfd(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_get_wpoll_descriptor(s: Ptr[SSL], desc: Ptr[BIO_POLL_DESCRIPTOR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_group_to_name(s: Ptr[SSL], id: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_handle_events(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_has_matching_session_id(
      s: Ptr[SSL],
      id: Ptr[CUnsignedChar],
      id_len: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_has_pending(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_in_before(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_in_init(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_inject_net_dgram(
      s: Ptr[SSL],
      buf: Ptr[CUnsignedChar],
      buf_len: USize,
      peer: Ptr[BIO_ADDR],
      local: Ptr[BIO_ADDR],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_connection(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_domain(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_dtls(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_init_finished(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_listener(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_quic(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_server(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_stream_local(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_is_tls(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_key_update(s: Ptr[SSL], updatetype: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_listen(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_load_client_CA_file(file: CString): Ptr[stack_st_X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_load_client_CA_file_ex(
      file: CString,
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[stack_st_X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_net_read_desired(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_net_write_desired(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_new(ctx: Ptr[SSL_CTX]): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_new_domain(ctx: Ptr[SSL_CTX], flags: ULong): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_new_from_listener(ssl: Ptr[SSL], flags: ULong): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_new_listener(ctx: Ptr[SSL_CTX], flags: ULong): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_new_listener_from(ssl: Ptr[SSL], flags: ULong): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_new_session_ticket(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_new_stream(s: Ptr[SSL], flags: ULong): Ptr[SSL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_peek(ssl: Ptr[SSL], buf: Ptr[Byte], num: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_peek_ex(
      ssl: Ptr[SSL],
      buf: Ptr[Byte],
      num: USize,
      readbytes: Ptr[USize],
  ): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_pending(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_poll(
      items: Ptr[SSL_POLL_ITEM],
      num_items: USize,
      stride: USize,
      timeout: Ptr[timeval],
      flags: ULong,
      result_count: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_read(ssl: Ptr[SSL], buf: Ptr[Byte], num: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_read_early_data(
      s: Ptr[SSL],
      buf: Ptr[Byte],
      num: USize,
      readbytes: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_read_ex(
      ssl: Ptr[SSL],
      buf: Ptr[Byte],
      num: USize,
      readbytes: Ptr[USize],
  ): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_renegotiate(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_renegotiate_abbreviated(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_renegotiate_pending(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_rstate_string(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_rstate_string_long(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_select_next_proto(
      out: Ptr[Ptr[CUnsignedChar]],
      outlen: Ptr[CUnsignedChar],
      in: Ptr[CUnsignedChar],
      inlen: CUnsignedInt,
      client: Ptr[CUnsignedChar],
      client_len: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_sendfile(
      s: Ptr[SSL],
      fd: CInt,
      offset: Size,
      size: USize,
      flags: CInt,
  ): USize =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_session_reused(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set0_CA_list(s: Ptr[SSL], name_list: Ptr[stack_st_X509_NAME]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set0_rbio(s: Ptr[SSL], rbio: Ptr[BIO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set0_security_ex_data(s: Ptr[SSL], ex: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set0_tmp_dh_pkey(s: Ptr[SSL], dhpkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set0_wbio(s: Ptr[SSL], wbio: Ptr[BIO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set1_cert_comp_preference(ssl: Ptr[SSL], algs: Ptr[CInt], len: USize): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set1_client_cert_type(s: Ptr[SSL], `val`: Ptr[CUnsignedChar], len: USize): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set1_compressed_cert(
      ssl: Ptr[SSL],
      algorithm: CInt,
      comp_data: Ptr[CUnsignedChar],
      comp_length: USize,
      orig_length: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set1_host(s: Ptr[SSL], host: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set1_initial_peer_addr(s: Ptr[SSL], peer_addr: Ptr[BIO_ADDR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set1_param(ssl: Ptr[SSL], vpm: Ptr[X509_VERIFY_PARAM]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set1_server_cert_type(s: Ptr[SSL], `val`: Ptr[CUnsignedChar], len: USize): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_SSL_CTX(ssl: Ptr[SSL], ctx: Ptr[SSL_CTX]): Ptr[SSL_CTX] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_accept_state(s: Ptr[SSL]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_allow_early_data_cb(
      s: Ptr[SSL],
      cb: SSL_allow_early_data_cb_fn,
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_alpn_protos(
      ssl: Ptr[SSL],
      protos: Ptr[CUnsignedChar],
      protos_len: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_async_callback(s: Ptr[SSL], callback: SSL_async_callback_fn): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_async_callback_arg(s: Ptr[SSL], arg: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_bio(s: Ptr[SSL], rbio: Ptr[BIO], wbio: Ptr[BIO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_block_padding(ssl: Ptr[SSL], block_size: USize): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_block_padding_ex(
      ssl: Ptr[SSL],
      app_block_size: USize,
      hs_block_size: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_blocking_mode(s: Ptr[SSL], blocking: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_cert_cb(
      s: Ptr[SSL],
      cb: CFuncPtr2[Ptr[SSL], Ptr[Byte], CInt],
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_cipher_list(s: Ptr[SSL], str: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_ciphersuites(s: Ptr[SSL], str: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_client_CA_list(s: Ptr[SSL], name_list: Ptr[stack_st_X509_NAME]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_connect_state(s: Ptr[SSL]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_ct_validation_callback(
      s: Ptr[SSL],
      callback: ssl_ct_validation_cb,
      arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_debug(s: Ptr[SSL], debug: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_default_passwd_cb(s: Ptr[SSL], cb: Ptr[pem_password_cb]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_default_passwd_cb_userdata(s: Ptr[SSL], u: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_default_read_buffer_len(s: Ptr[SSL], len: USize): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_default_stream_mode(s: Ptr[SSL], mode: UInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_ex_data(ssl: Ptr[SSL], idx: CInt, data: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_fd(s: Ptr[SSL], fd: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_generate_session_id(s: Ptr[SSL], cb: GEN_SESSION_CB): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_hostflags(s: Ptr[SSL], flags: CUnsignedInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_incoming_stream_policy(s: Ptr[SSL], policy: CInt, aec: ULong): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_info_callback(
      ssl: Ptr[SSL],
      cb: CFuncPtr3[Ptr[SSL], CInt, CInt, Unit],
  ): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_max_early_data(s: Ptr[SSL], max_early_data: UInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_msg_callback(
      ssl: Ptr[SSL],
      cb: CFuncPtr7[CInt, CInt, CInt, Ptr[Byte], USize, Ptr[SSL], Ptr[Byte], Unit],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_not_resumable_session_callback(
      ssl: Ptr[SSL],
      cb: CFuncPtr2[Ptr[SSL], CInt, CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_num_tickets(s: Ptr[SSL], num_tickets: USize): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_options(s: Ptr[SSL], op: ULong): ULong = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_post_handshake_auth(s: Ptr[SSL], `val`: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_psk_client_callback(ssl: Ptr[SSL], cb: SSL_psk_client_cb_func): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_psk_find_session_callback(
      s: Ptr[SSL],
      cb: SSL_psk_find_session_cb_func,
  ): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_psk_server_callback(ssl: Ptr[SSL], cb: SSL_psk_server_cb_func): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_psk_use_session_callback(s: Ptr[SSL], cb: SSL_psk_use_session_cb_func): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_purpose(ssl: Ptr[SSL], purpose: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_quic_tls_cbs(s: Ptr[SSL], qtdis: Ptr[OSSL_DISPATCH], arg: Ptr[Byte]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_quic_tls_early_data_enabled(s: Ptr[SSL], enabled: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_quic_tls_transport_params(
      s: Ptr[SSL],
      params: Ptr[CUnsignedChar],
      params_len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_quiet_shutdown(ssl: Ptr[SSL], mode: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_read_ahead(s: Ptr[SSL], yes: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_record_padding_callback(
      ssl: Ptr[SSL],
      cb: CFuncPtr4[Ptr[SSL], CInt, USize, Ptr[Byte], USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_record_padding_callback_arg(ssl: Ptr[SSL], arg: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_recv_max_early_data(s: Ptr[SSL], recv_max_early_data: UInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_rfd(s: Ptr[SSL], fd: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_security_callback(
      s: Ptr[SSL],
      cb: CFuncPtr7[Ptr[SSL], Ptr[SSL_CTX], CInt, CInt, CInt, Ptr[Byte], Ptr[Byte], CInt],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_security_level(s: Ptr[SSL], level: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_session(to: Ptr[SSL], session: Ptr[SSL_SESSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_session_id_context(
      ssl: Ptr[SSL],
      sid_ctx: Ptr[CUnsignedChar],
      sid_ctx_len: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_session_secret_cb(
      s: Ptr[SSL],
      session_secret_cb: tls_session_secret_cb_fn,
      arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_session_ticket_ext(s: Ptr[SSL], ext_data: Ptr[Byte], ext_len: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_session_ticket_ext_cb(
      s: Ptr[SSL],
      cb: tls_session_ticket_ext_cb_fn,
      arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_shutdown(ssl: Ptr[SSL], mode: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_srp_server_param(
      s: Ptr[SSL],
      N: Ptr[BIGNUM],
      g: Ptr[BIGNUM],
      sa: Ptr[BIGNUM],
      v: Ptr[BIGNUM],
      info: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_srp_server_param_pw(
      s: Ptr[SSL],
      user: CString,
      pass: CString,
      grp: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_ssl_method(s: Ptr[SSL], method: Ptr[SSL_METHOD]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_tmp_dh_callback(
      ssl: Ptr[SSL],
      dh: CFuncPtr3[Ptr[SSL], CInt, CInt, Ptr[DH]],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_trust(ssl: Ptr[SSL], trust: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_value_uint(s: Ptr[SSL], `class_`: UInt, id: UInt, v: ULong): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_verify(s: Ptr[SSL], mode: CInt, callback: SSL_verify_cb): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_verify_depth(s: Ptr[SSL], depth: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_verify_result(ssl: Ptr[SSL], v: CLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_set_wfd(s: Ptr[SSL], fd: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_shutdown(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_shutdown_ex(
      ssl: Ptr[SSL],
      flags: ULong,
      args: Ptr[SSL_SHUTDOWN_EX_ARGS],
      args_len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_srp_server_param_with_username(s: Ptr[SSL], ad: Ptr[CInt]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_state_string(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_state_string_long(s: Ptr[SSL]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_stateless(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_stream_conclude(ssl: Ptr[SSL], flags: ULong): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_stream_reset(
      ssl: Ptr[SSL],
      args: Ptr[SSL_STREAM_RESET_ARGS],
      args_len: USize,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_test_functions(): Ptr[openssl_ssl_test_functions] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_trace(
      write_p: CInt,
      version: CInt,
      content_type: CInt,
      buf: Ptr[Byte],
      len: USize,
      ssl: Ptr[SSL],
      arg: Ptr[Byte],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_up_ref(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_PrivateKey(ssl: Ptr[SSL], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_PrivateKey_ASN1(
      pk: CInt,
      ssl: Ptr[SSL],
      d: Ptr[CUnsignedChar],
      len: CLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_PrivateKey_file(ssl: Ptr[SSL], file: CString, `type`: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_RSAPrivateKey(ssl: Ptr[SSL], rsa: Ptr[RSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_RSAPrivateKey_ASN1(
      ssl: Ptr[SSL],
      d: Ptr[CUnsignedChar],
      len: CLongInt,
  ): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_RSAPrivateKey_file(ssl: Ptr[SSL], file: CString, `type`: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_cert_and_key(
      ssl: Ptr[SSL],
      x509: Ptr[X509],
      privatekey: Ptr[EVP_PKEY],
      chain: Ptr[stack_st_X509],
      `override`: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_certificate(ssl: Ptr[SSL], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_certificate_ASN1(ssl: Ptr[SSL], d: Ptr[CUnsignedChar], len: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_certificate_chain_file(ssl: Ptr[SSL], file: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_certificate_file(ssl: Ptr[SSL], file: CString, `type`: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_use_psk_identity_hint(s: Ptr[SSL], identity_hint: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_verify_client_post_handshake(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_version(ssl: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_waiting_for_async(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_want(s: Ptr[SSL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_write(ssl: Ptr[SSL], buf: Ptr[Byte], num: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_write_early_data(
      s: Ptr[SSL],
      buf: Ptr[Byte],
      num: USize,
      written: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_write_ex(s: Ptr[SSL], buf: Ptr[Byte], num: USize, written: Ptr[USize]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def SSL_write_ex2(
      s: Ptr[SSL],
      buf: Ptr[Byte],
      num: USize,
      flags: ULong,
      written: Ptr[USize],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLS_client_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLS_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLS_server_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_1_client_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_1_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_1_server_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_2_client_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_2_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_2_server_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_client_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_method(): Ptr[SSL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def TLSv1_server_method(): Ptr[SSL_METHOD] = extern

  private[libssl_internal] def __sn_wrap_snhttp_experimental_openssl_ssl_SSL_as_poll_descriptor(
      s: Ptr[SSL],
      __return: Ptr[BIO_POLL_DESCRIPTOR],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def d2i_SSL_SESSION(
      a: Ptr[Ptr[SSL_SESSION]],
      pp: Ptr[Ptr[CUnsignedChar]],
      length: CLongInt,
  ): Ptr[SSL_SESSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def d2i_SSL_SESSION_ex(
      a: Ptr[Ptr[SSL_SESSION]],
      pp: Ptr[Ptr[CUnsignedChar]],
      length: CLongInt,
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[SSL_SESSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  def i2d_SSL_SESSION(in: Ptr[SSL_SESSION], pp: Ptr[Ptr[CUnsignedChar]]): CInt = extern

  // /**
  //  * [bindgen] header: /usr/include/openssl/ssl.h
  //  */
  // def SSL_as_poll_descriptor(s: Ptr[SSL])(using Zone): BIO_POLL_DESCRIPTOR =
  // val __ptr_0: Ptr[BIO_POLL_DESCRIPTOR] =
  //     _root_.scala.scalanative.unsafe.alloc[BIO_POLL_DESCRIPTOR](1)
  // __sn_wrap_snhttp_experimental_openssl_ssl_SSL_as_poll_descriptor(s, __ptr_0 + 0)
  // !(__ptr_0 + 0)

  // /**
  //  * [bindgen] header: /usr/include/openssl/ssl.h
  //  */
  // def SSL_as_poll_descriptor(s: Ptr[SSL])(__return: Ptr[BIO_POLL_DESCRIPTOR]): Unit =
  // __sn_wrap_snhttp_experimental_openssl_ssl_SSL_as_poll_descriptor(s, __return)
