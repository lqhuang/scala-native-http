package snhttp.experimental.openssl.bio_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

@link("openssl/bio")
@extern
object functions:

  import _root_.snhttp.experimental.openssl.bio_internal.aliases.*
  import _root_.snhttp.experimental.openssl.bio_internal.structs.*
  import _root_.snhttp.experimental.openssl.bio_internal.enumerations.*
  import _root_.snhttp.experimental.openssl.bio_internal.unions.*

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDRINFO_address(bai: Ptr[BIO_ADDRINFO]): Ptr[BIO_ADDR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDRINFO_family(bai: Ptr[BIO_ADDRINFO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDRINFO_free(bai: Ptr[BIO_ADDRINFO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDRINFO_next(bai: Ptr[BIO_ADDRINFO]): Ptr[BIO_ADDRINFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDRINFO_protocol(bai: Ptr[BIO_ADDRINFO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDRINFO_socktype(bai: Ptr[BIO_ADDRINFO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_clear(ap: Ptr[BIO_ADDR]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_copy(dst: Ptr[BIO_ADDR], src: Ptr[BIO_ADDR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_dup(ap: Ptr[BIO_ADDR]): Ptr[BIO_ADDR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_family(ap: Ptr[BIO_ADDR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_free(_0: Ptr[BIO_ADDR]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_hostname_string(ap: Ptr[BIO_ADDR], numeric: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_new(): Ptr[BIO_ADDR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_path_string(ap: Ptr[BIO_ADDR]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_rawaddress(ap: Ptr[BIO_ADDR], p: Ptr[Byte], l: Ptr[size_t]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_rawmake(
      ap: Ptr[BIO_ADDR],
      family: CInt,
      where: Ptr[Byte],
      wherelen: size_t,
      port: CUnsignedShort,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_rawport(ap: Ptr[BIO_ADDR]): CUnsignedShort = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ADDR_service_string(ap: Ptr[BIO_ADDR], numeric: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_accept(sock: CInt, ip_port: Ptr[CString]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_accept_ex(accept_sock: CInt, addr: Ptr[BIO_ADDR], options: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_asn1_get_prefix(
      b: Ptr[BIO],
      pprefix: Ptr[Ptr[asn1_ps_func]],
      pprefix_free: Ptr[Ptr[asn1_ps_func]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_asn1_get_suffix(
      b: Ptr[BIO],
      psuffix: Ptr[Ptr[asn1_ps_func]],
      psuffix_free: Ptr[Ptr[asn1_ps_func]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_asn1_set_prefix(
      b: Ptr[BIO],
      prefix: Ptr[asn1_ps_func],
      prefix_free: Ptr[asn1_ps_func],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_asn1_set_suffix(
      b: Ptr[BIO],
      suffix: Ptr[asn1_ps_func],
      suffix_free: Ptr[asn1_ps_func],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_bind(sock: CInt, addr: Ptr[BIO_ADDR], options: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_callback_ctrl(b: Ptr[BIO], cmd: CInt, fp: Ptr[BIO_info_cb]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_clear_flags(b: Ptr[BIO], flags: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_closesocket(sock: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_connect(sock: CInt, addr: Ptr[BIO_ADDR], options: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_copy_next_retry(b: Ptr[BIO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ctrl(bp: Ptr[BIO], cmd: BIO_CTRL, larg: Size, parg: Ptr[Byte]): Size =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ctrl_get_read_request(b: Ptr[BIO]): size_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ctrl_get_write_guarantee(b: Ptr[BIO]): size_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ctrl_pending(b: Ptr[BIO]): size_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ctrl_reset_read_request(b: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ctrl_wpending(b: Ptr[BIO]): size_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_debug_callback(
      bio: Ptr[BIO],
      cmd: CInt,
      argp: CString,
      argi: CInt,
      argl: CLongInt,
      ret: CLongInt,
  ): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_debug_callback_ex(
      bio: Ptr[BIO],
      oper: CInt,
      argp: CString,
      len: size_t,
      argi: CInt,
      argl: CLongInt,
      ret: CInt,
      processed: Ptr[size_t],
  ): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_dgram_non_fatal_error(error: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_do_connect_retry(bio: Ptr[BIO], timeout: CInt, nap_milliseconds: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_dump(b: Ptr[BIO], bytes: Ptr[Byte], len: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_dump_cb(
      cb: CFuncPtr3[Ptr[Byte], size_t, Ptr[Byte], CInt],
      u: Ptr[Byte],
      s: Ptr[Byte],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_dump_fp(fp: Ptr[FILE], s: Ptr[Byte], len: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_dump_indent(b: Ptr[BIO], bytes: Ptr[Byte], len: CInt, indent: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_dump_indent_cb(
      cb: CFuncPtr3[Ptr[Byte], size_t, Ptr[Byte], CInt],
      u: Ptr[Byte],
      s: Ptr[Byte],
      len: CInt,
      indent: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_dump_indent_fp(fp: Ptr[FILE], s: Ptr[Byte], len: CInt, indent: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_dup_chain(in: Ptr[BIO]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_err_is_non_fatal(errcode: CUnsignedInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_f_buffer(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_f_linebuffer(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_f_nbio_test(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_f_null(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_f_prefix(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_f_readbuffer(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_fd_non_fatal_error(error: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_fd_should_retry(i: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_find_type(b: Ptr[BIO], bio_type: CInt): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_free(a: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_free_all(a: Ptr[BIO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_accept_socket(host_port: CString, mode: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_callback(b: Ptr[BIO]): BIO_callback_fn = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_callback_arg(b: Ptr[BIO]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_callback_ex(b: Ptr[BIO]): BIO_callback_fn_ex = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_data(a: Ptr[BIO]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_ex_data(bio: Ptr[BIO], idx: CInt): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_host_ip(str: CString, ip: Ptr[CUnsignedChar]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_init(a: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_line(bio: Ptr[BIO], buf: CString, size: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_new_index(): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_port(str: CString, port_ptr: Ptr[CUnsignedShort]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_retry_BIO(bio: Ptr[BIO], reason: Ptr[CInt]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_retry_reason(bio: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_rpoll_descriptor(b: Ptr[BIO], desc: Ptr[BIO_POLL_DESCRIPTOR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_shutdown(a: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_get_wpoll_descriptor(b: Ptr[BIO], desc: Ptr[BIO_POLL_DESCRIPTOR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_gethostbyname(name: CString): Ptr[hostent] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_gets(bp: Ptr[BIO], buf: CString, size: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_hex_string(
      out: Ptr[BIO],
      indent: CInt,
      width: CInt,
      data: Ptr[Byte],
      datalen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_indent(b: Ptr[BIO], indent: CInt, max: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_int_ctrl(bp: Ptr[BIO], cmd: CInt, larg: CLongInt, iarg: CInt): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_listen(sock: CInt, addr: Ptr[BIO_ADDR], options: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_lookup(
      host: CString,
      service: CString,
      lookup_type: BIO_lookup_type,
      family: CInt,
      socktype: CInt,
      res: Ptr[Ptr[BIO_ADDRINFO]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_lookup_ex(
      host: CString,
      service: CString,
      lookup_type: CInt,
      family: CInt,
      socktype: CInt,
      protocol: CInt,
      res: Ptr[Ptr[BIO_ADDRINFO]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_free(biom: Ptr[BIO_METHOD]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_callback_ctrl(
      _0: Ptr[BIO],
  ): CFuncPtr3[Ptr[BIO], CInt, Ptr[BIO_info_cb], CLongInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_create(_0: Ptr[BIO]): CFuncPtr1[Ptr[BIO], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_ctrl(
      _0: Ptr[BIO],
  ): CFuncPtr4[Ptr[BIO], CInt, CLongInt, Ptr[Byte], CLongInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_destroy(_0: Ptr[BIO]): CFuncPtr1[Ptr[BIO], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_gets(_0: Ptr[BIO]): CFuncPtr3[Ptr[BIO], CString, CInt, CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_puts(_0: Ptr[BIO]): CFuncPtr2[Ptr[BIO], CString, CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_read(_0: Ptr[BIO]): CFuncPtr3[Ptr[BIO], CString, CInt, CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_read_ex(
      _0: Ptr[BIO],
  ): CFuncPtr4[Ptr[BIO], CString, size_t, Ptr[size_t], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_recvmmsg(
      _0: Ptr[BIO],
  ): CFuncPtr6[Ptr[BIO], Ptr[BIO_MSG], size_t, size_t, uint64_t, Ptr[size_t], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_sendmmsg(
      _0: Ptr[BIO],
  ): CFuncPtr6[Ptr[BIO], Ptr[BIO_MSG], size_t, size_t, uint64_t, Ptr[size_t], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_write(_0: Ptr[BIO]): CFuncPtr3[Ptr[BIO], CString, CInt, CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_get_write_ex(
      _0: Ptr[BIO],
  ): CFuncPtr4[Ptr[BIO], CString, size_t, Ptr[size_t], CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_new(`type`: CInt, name: CString): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_callback_ctrl(
      biom: Ptr[BIO_METHOD],
      callback_ctrl: CFuncPtr3[Ptr[BIO], CInt, Ptr[BIO_info_cb], CLongInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_create(biom: Ptr[BIO_METHOD], create: CFuncPtr1[Ptr[BIO], CInt]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_ctrl(
      biom: Ptr[BIO_METHOD],
      ctrl: CFuncPtr4[Ptr[BIO], CInt, CLongInt, Ptr[Byte], CLongInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_destroy(
      biom: Ptr[BIO_METHOD],
      destroy: CFuncPtr1[Ptr[BIO], CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_gets(
      biom: Ptr[BIO_METHOD],
      ossl_gets: CFuncPtr3[Ptr[BIO], CString, CInt, CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_puts(
      biom: Ptr[BIO_METHOD],
      puts: CFuncPtr2[Ptr[BIO], CString, CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_read(
      biom: Ptr[BIO_METHOD],
      read: CFuncPtr3[Ptr[BIO], CString, CInt, CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_read_ex(
      biom: Ptr[BIO_METHOD],
      bread: CFuncPtr4[Ptr[BIO], CString, size_t, Ptr[size_t], CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_recvmmsg(
      biom: Ptr[BIO_METHOD],
      f: CFuncPtr6[Ptr[BIO], Ptr[BIO_MSG], size_t, size_t, uint64_t, Ptr[size_t], CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_sendmmsg(
      biom: Ptr[BIO_METHOD],
      f: CFuncPtr6[Ptr[BIO], Ptr[BIO_MSG], size_t, size_t, uint64_t, Ptr[size_t], CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_write(
      biom: Ptr[BIO_METHOD],
      write: CFuncPtr3[Ptr[BIO], CString, CInt, CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_meth_set_write_ex(
      biom: Ptr[BIO_METHOD],
      bwrite: CFuncPtr4[Ptr[BIO], CString, size_t, Ptr[size_t], CInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_method_name(b: Ptr[BIO]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_method_type(b: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new(`type`: Ptr[BIO_METHOD]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_accept(host_port: CString): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_bio_dgram_pair(
      bio1: Ptr[Ptr[BIO]],
      writebuf1: size_t,
      bio2: Ptr[Ptr[BIO]],
      writebuf2: size_t,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_bio_pair(
      bio1: Ptr[Ptr[BIO]],
      writebuf1: size_t,
      bio2: Ptr[Ptr[BIO]],
      writebuf2: size_t,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_connect(host_port: CString): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_dgram(fd: CInt, close_flag: CInt): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_ex(libctx: Ptr[OSSL_LIB_CTX], method: Ptr[BIO_METHOD]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_fd(fd: CInt, close_flag: CInt): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_file(filename: CString, mode: CString): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_fp(stream: Ptr[FILE], close_flag: CInt): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_from_core_bio(
      libctx: Ptr[OSSL_LIB_CTX],
      corebio: Ptr[OSSL_CORE_BIO],
  ): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_mem_buf(buf: Ptr[Byte], len: CInt): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_new_socket(sock: CInt, close_flag: CInt): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_next(b: Ptr[BIO]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_nread(bio: Ptr[BIO], buf: Ptr[CString], num: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_nread0(bio: Ptr[BIO], buf: Ptr[CString]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_number_read(bio: Ptr[BIO]): uint64_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_number_written(bio: Ptr[BIO]): uint64_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_nwrite(bio: Ptr[BIO], buf: Ptr[CString], num: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_nwrite0(bio: Ptr[BIO], buf: Ptr[CString]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_parse_hostserv(
      hostserv: CString,
      host: Ptr[CString],
      service: Ptr[CString],
      hostserv_prio: BIO_hostserv_priorities,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_pop(b: Ptr[BIO]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_printf(
      bio: Ptr[BIO],
      format: CString,
      rest: Any*, // CVarArgList
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_ptr_ctrl(bp: Ptr[BIO], cmd: CInt, larg: CLongInt): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_push(b: Ptr[BIO], append: Ptr[BIO]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_puts(bp: Ptr[BIO], buf: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_read(b: Ptr[BIO], data: Ptr[Byte], dlen: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_read_ex(
      b: Ptr[BIO],
      data: Ptr[Byte],
      dlen: size_t,
      readbytes: Ptr[size_t],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_recvmmsg(
      b: Ptr[BIO],
      msg: Ptr[BIO_MSG],
      stride: size_t,
      num_msg: size_t,
      flags: uint64_t,
      msgs_processed: Ptr[size_t],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_accept(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_bio(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_connect(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_core(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_datagram(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_dgram_mem(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_dgram_pair(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_fd(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_file(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_log(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_mem(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_null(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_secmem(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_s_socket(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_sendmmsg(
      b: Ptr[BIO],
      msg: Ptr[BIO_MSG],
      stride: size_t,
      num_msg: size_t,
      flags: uint64_t,
      msgs_processed: Ptr[size_t],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_callback(b: Ptr[BIO], callback: BIO_callback_fn): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_callback_arg(b: Ptr[BIO], arg: CString): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_callback_ex(b: Ptr[BIO], callback: BIO_callback_fn_ex): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_data(a: Ptr[BIO], ptr: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_ex_data(bio: Ptr[BIO], idx: CInt, data: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_flags(b: Ptr[BIO], flags: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_init(a: Ptr[BIO], init: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_next(b: Ptr[BIO], next: Ptr[BIO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_retry_reason(bio: Ptr[BIO], reason: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_shutdown(a: Ptr[BIO], shut: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_set_tcp_ndelay(sock: CInt, turn_on: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_snprintf(buf: CString, n: size_t, format: CString, rest: CVarArgList): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_sock_error(sock: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_sock_info(
      sock: CInt,
      `type`: BIO_sock_info_type,
      info: Ptr[BIO_sock_info_u],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_sock_init(): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_sock_non_fatal_error(error: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_sock_should_retry(i: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_socket(domain: CInt, socktype: CInt, protocol: CInt, options: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_socket_ioctl(fd: CInt, `type`: CLongInt, arg: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_socket_nbio(fd: CInt, mode: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_socket_wait(fd: CInt, for_read: CInt, max_time: time_t): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_test_flags(b: Ptr[BIO], flags: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_up_ref(a: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_vfree(a: Ptr[BIO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_vprintf(bio: Ptr[BIO], format: CString, args: va_list): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_vsnprintf(buf: CString, n: size_t, format: CString, args: va_list): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_wait(bio: Ptr[BIO], max_time: time_t, nap_milliseconds: CUnsignedInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_write(b: Ptr[BIO], data: Ptr[Byte], dlen: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  @extern def BIO_write_ex(b: Ptr[BIO], data: Ptr[Byte], dlen: size_t, written: Ptr[size_t]): CInt =
    extern
