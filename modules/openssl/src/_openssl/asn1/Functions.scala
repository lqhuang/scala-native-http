package snhttp.experimental.openssl
package _openssl.asn1

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._common.{size_t, int64_t, FILE, tm, uint64_t, time_t}
import _root_.snhttp.experimental.openssl._openssl.bio.Structs.{BIO, BIO_METHOD}
import _root_.snhttp.experimental.openssl._openssl.types.Types.{BIGNUM, EVP_PKEY, OSSL_LIB_CTX, EVP_MD}
import _root_.snhttp.experimental.openssl._openssl.safestack.stack_st_X509_ALGOR
import _root_.snhttp.experimental.openssl._openssl.conf.Types.CONF
import _root_.snhttp.experimental.openssl._openssl.x509.Types.X509_ALGOR
import _root_.snhttp.experimental.openssl._openssl.x509v3.Types.X509V3_CTX
import _root_.snhttp.experimental.openssl._openssl.pkcs12.Types.PKCS8_PRIV_KEY_INFO

import Types.*

@extern
private[openssl] trait Functions:

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_BIT_STRING_check(
      a: Ptr[ASN1_BIT_STRING],
      flags: Ptr[CUnsignedChar],
      flags_len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_BIT_STRING_get_bit(a: Ptr[ASN1_BIT_STRING], n: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_BIT_STRING_name_print(
      out: Ptr[BIO],
      bs: Ptr[ASN1_BIT_STRING],
      tbl: Ptr[BIT_STRING_BITNAME],
      indent: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_BIT_STRING_num_asc(name: CString, tbl: Ptr[BIT_STRING_BITNAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_BIT_STRING_set(
      a: Ptr[ASN1_BIT_STRING],
      d: Ptr[CUnsignedChar],
      length: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_BIT_STRING_set_asc(
      bs: Ptr[ASN1_BIT_STRING],
      name: CString,
      value: CInt,
      tbl: Ptr[BIT_STRING_BITNAME],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_BIT_STRING_set_bit(a: Ptr[ASN1_BIT_STRING], n: CInt, value: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_ENUMERATED_get(a: Ptr[ASN1_ENUMERATED]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_ENUMERATED_get_int64(pr: Ptr[int64_t], a: Ptr[ASN1_ENUMERATED]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_ENUMERATED_set(a: Ptr[ASN1_ENUMERATED], v: CLongInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_ENUMERATED_set_int64(a: Ptr[ASN1_ENUMERATED], r: int64_t): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_ENUMERATED_to_BN(ai: Ptr[ASN1_ENUMERATED], bn: Ptr[BIGNUM]): Ptr[BIGNUM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_GENERALIZEDTIME_adj(
      s: Ptr[ASN1_GENERALIZEDTIME],
      t: time_t,
      offset_day: CInt,
      offset_sec: CLongInt,
  ): Ptr[ASN1_GENERALIZEDTIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_GENERALIZEDTIME_check(a: Ptr[ASN1_GENERALIZEDTIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_GENERALIZEDTIME_print(fp: Ptr[BIO], a: Ptr[ASN1_GENERALIZEDTIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_GENERALIZEDTIME_set(
      s: Ptr[ASN1_GENERALIZEDTIME],
      t: time_t,
  ): Ptr[ASN1_GENERALIZEDTIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_GENERALIZEDTIME_set_string(s: Ptr[ASN1_GENERALIZEDTIME], str: CString): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_INTEGER_cmp(x: Ptr[ASN1_INTEGER], y: Ptr[ASN1_INTEGER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_INTEGER_get(a: Ptr[ASN1_INTEGER]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_INTEGER_get_int64(pr: Ptr[int64_t], a: Ptr[ASN1_INTEGER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_INTEGER_get_uint64(pr: Ptr[uint64_t], a: Ptr[ASN1_INTEGER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_INTEGER_set(a: Ptr[ASN1_INTEGER], v: CLongInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_INTEGER_set_int64(a: Ptr[ASN1_INTEGER], r: int64_t): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_INTEGER_set_uint64(a: Ptr[ASN1_INTEGER], r: uint64_t): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_INTEGER_to_BN(ai: Ptr[ASN1_INTEGER], bn: Ptr[BIGNUM]): Ptr[BIGNUM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_ITEM_get(i: size_t): Ptr[ASN1_ITEM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_ITEM_lookup(name: CString): Ptr[ASN1_ITEM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_OBJECT_create(
      nid: CInt,
      data: Ptr[CUnsignedChar],
      len: CInt,
      sn: CString,
      ln: CString,
  ): Ptr[ASN1_OBJECT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_OCTET_STRING_cmp(a: Ptr[ASN1_OCTET_STRING], b: Ptr[ASN1_OCTET_STRING]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_OCTET_STRING_set(
      str: Ptr[ASN1_OCTET_STRING],
      data: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_free(p: Ptr[ASN1_PCTX]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_get_cert_flags(p: Ptr[ASN1_PCTX]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_get_flags(p: Ptr[ASN1_PCTX]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_get_nm_flags(p: Ptr[ASN1_PCTX]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_get_oid_flags(p: Ptr[ASN1_PCTX]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_get_str_flags(p: Ptr[ASN1_PCTX]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_new(): Ptr[ASN1_PCTX] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_set_cert_flags(p: Ptr[ASN1_PCTX], flags: CUnsignedLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_set_flags(p: Ptr[ASN1_PCTX], flags: CUnsignedLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_set_nm_flags(p: Ptr[ASN1_PCTX], flags: CUnsignedLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_set_oid_flags(p: Ptr[ASN1_PCTX], flags: CUnsignedLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PCTX_set_str_flags(p: Ptr[ASN1_PCTX], flags: CUnsignedLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_PRINTABLE_type(s: Ptr[CUnsignedChar], max: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_SCTX_free(p: Ptr[ASN1_SCTX]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_SCTX_get_app_data(p: Ptr[ASN1_SCTX]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_SCTX_get_flags(p: Ptr[ASN1_SCTX]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_SCTX_get_item(p: Ptr[ASN1_SCTX]): Ptr[ASN1_ITEM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_SCTX_get_template(p: Ptr[ASN1_SCTX]): Ptr[ASN1_TEMPLATE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_SCTX_new(scan_cb: CFuncPtr1[Ptr[ASN1_SCTX], CInt]): Ptr[ASN1_SCTX] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_SCTX_set_app_data(p: Ptr[ASN1_SCTX], data: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_TABLE_add(
      _0: CInt,
      _1: CLongInt,
      _2: CLongInt,
      _3: CUnsignedLongInt,
      _4: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_TABLE_cleanup(): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_TABLE_get(nid: CInt): Ptr[ASN1_STRING_TABLE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_clear_free(a: Ptr[ASN1_STRING]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_cmp(a: Ptr[ASN1_STRING], b: Ptr[ASN1_STRING]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_copy(dst: Ptr[ASN1_STRING], str: Ptr[ASN1_STRING]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_data(x: Ptr[ASN1_STRING]): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_free(a: Ptr[ASN1_STRING]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_get0_data(x: Ptr[ASN1_STRING]): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_get_default_mask(): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_length(x: Ptr[ASN1_STRING]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_length_set(x: Ptr[ASN1_STRING], n: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_new(): Ptr[ASN1_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_print(bp: Ptr[BIO], v: Ptr[ASN1_STRING]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_print_ex(
      out: Ptr[BIO],
      str: Ptr[ASN1_STRING],
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_print_ex_fp(
      fp: Ptr[FILE],
      str: Ptr[ASN1_STRING],
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_set(str: Ptr[ASN1_STRING], data: Ptr[Byte], len: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_set0(str: Ptr[ASN1_STRING], data: Ptr[Byte], len: CInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_set_by_NID(
      out: Ptr[Ptr[ASN1_STRING]],
      in: Ptr[CUnsignedChar],
      inlen: CInt,
      inform: CInt,
      nid: CInt,
  ): Ptr[ASN1_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_set_default_mask(mask: CUnsignedLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_set_default_mask_asc(p: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_to_UTF8(out: Ptr[Ptr[CUnsignedChar]], in: Ptr[ASN1_STRING]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_type(x: Ptr[ASN1_STRING]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_STRING_type_new(`type`: CInt): Ptr[ASN1_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_adj(
      s: Ptr[ASN1_TIME],
      t: time_t,
      offset_day: CInt,
      offset_sec: CLongInt,
  ): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_check(t: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_cmp_time_t(s: Ptr[ASN1_TIME], t: time_t): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_compare(a: Ptr[ASN1_TIME], b: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_diff(
      pday: Ptr[CInt],
      psec: Ptr[CInt],
      from: Ptr[ASN1_TIME],
      to: Ptr[ASN1_TIME],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_normalize(s: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_print(bp: Ptr[BIO], tm: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_print_ex(bp: Ptr[BIO], tm: Ptr[ASN1_TIME], flags: CUnsignedLongInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_set(s: Ptr[ASN1_TIME], t: time_t): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_set_string(s: Ptr[ASN1_TIME], str: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_set_string_X509(s: Ptr[ASN1_TIME], str: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_to_generalizedtime(
      t: Ptr[ASN1_TIME],
      out: Ptr[Ptr[ASN1_GENERALIZEDTIME]],
  ): Ptr[ASN1_GENERALIZEDTIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TIME_to_tm(s: Ptr[ASN1_TIME], tm: Ptr[tm]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_cmp(a: Ptr[ASN1_TYPE], b: Ptr[ASN1_TYPE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_get(a: Ptr[ASN1_TYPE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_get_int_octetstring(
      a: Ptr[ASN1_TYPE],
      num: Ptr[CLongInt],
      data: Ptr[CUnsignedChar],
      max_len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_get_octetstring(
      a: Ptr[ASN1_TYPE],
      data: Ptr[CUnsignedChar],
      max_len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_pack_sequence(
      it: Ptr[ASN1_ITEM],
      s: Ptr[Byte],
      t: Ptr[Ptr[ASN1_TYPE]],
  ): Ptr[ASN1_TYPE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_set(a: Ptr[ASN1_TYPE], `type`: CInt, value: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_set1(a: Ptr[ASN1_TYPE], `type`: CInt, value: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_set_int_octetstring(
      a: Ptr[ASN1_TYPE],
      num: CLongInt,
      data: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_set_octetstring(
      a: Ptr[ASN1_TYPE],
      data: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_TYPE_unpack_sequence(it: Ptr[ASN1_ITEM], t: Ptr[ASN1_TYPE]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_UNIVERSALSTRING_to_string(s: Ptr[ASN1_UNIVERSALSTRING]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_UTCTIME_adj(
      s: Ptr[ASN1_UTCTIME],
      t: time_t,
      offset_day: CInt,
      offset_sec: CLongInt,
  ): Ptr[ASN1_UTCTIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_UTCTIME_check(a: Ptr[ASN1_UTCTIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_UTCTIME_cmp_time_t(s: Ptr[ASN1_UTCTIME], t: time_t): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_UTCTIME_print(fp: Ptr[BIO], a: Ptr[ASN1_UTCTIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_UTCTIME_set(s: Ptr[ASN1_UTCTIME], t: time_t): Ptr[ASN1_UTCTIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_UTCTIME_set_string(s: Ptr[ASN1_UTCTIME], str: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_add_oid_module(): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_add_stable_module(): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_bn_print(
      bp: Ptr[BIO],
      number: CString,
      num: Ptr[BIGNUM],
      buf: Ptr[CUnsignedChar],
      off: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_buf_print(
      bp: Ptr[BIO],
      buf: Ptr[CUnsignedChar],
      buflen: size_t,
      off: CInt,
  ): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_check_infinite_end(p: Ptr[Ptr[CUnsignedChar]], len: CLongInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_const_check_infinite_end(p: Ptr[Ptr[CUnsignedChar]], len: CLongInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_d2i_bio(
      xnew: CFuncPtr0[Ptr[Byte]],
      d2i: Ptr[d2i_of_void],
      in: Ptr[BIO],
      x: Ptr[Ptr[Byte]],
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_d2i_fp(
      xnew: CFuncPtr0[Ptr[Byte]],
      d2i: Ptr[d2i_of_void],
      in: Ptr[FILE],
      x: Ptr[Ptr[Byte]],
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_dup(i2d: Ptr[i2d_of_void], d2i: Ptr[d2i_of_void], x: Ptr[Byte]): Ptr[Byte] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_generate_nconf(str: CString, nconf: Ptr[CONF]): Ptr[ASN1_TYPE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_generate_v3(str: CString, cnf: Ptr[X509V3_CTX]): Ptr[ASN1_TYPE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_get_object(
      pp: Ptr[Ptr[CUnsignedChar]],
      plength: Ptr[CLongInt],
      ptag: Ptr[CInt],
      pclass: Ptr[CInt],
      omax: CLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_i2d_bio(i2d: Ptr[i2d_of_void], out: Ptr[BIO], x: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_i2d_fp(i2d: Ptr[i2d_of_void], out: Ptr[FILE], x: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_d2i(
      `val`: Ptr[Ptr[ASN1_VALUE]],
      in: Ptr[Ptr[CUnsignedChar]],
      len: CLongInt,
      it: Ptr[ASN1_ITEM],
  ): Ptr[ASN1_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_d2i_bio(it: Ptr[ASN1_ITEM], in: Ptr[BIO], pval: Ptr[Byte]): Ptr[Byte] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_d2i_bio_ex(
      it: Ptr[ASN1_ITEM],
      in: Ptr[BIO],
      pval: Ptr[Byte],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_d2i_ex(
      `val`: Ptr[Ptr[ASN1_VALUE]],
      in: Ptr[Ptr[CUnsignedChar]],
      len: CLongInt,
      it: Ptr[ASN1_ITEM],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[ASN1_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_d2i_fp(it: Ptr[ASN1_ITEM], in: Ptr[FILE], x: Ptr[Byte]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_d2i_fp_ex(
      it: Ptr[ASN1_ITEM],
      in: Ptr[FILE],
      x: Ptr[Byte],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_dup(it: Ptr[ASN1_ITEM], x: Ptr[Byte]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_free(`val`: Ptr[ASN1_VALUE], it: Ptr[ASN1_ITEM]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_i2d(
      `val`: Ptr[ASN1_VALUE],
      out: Ptr[Ptr[CUnsignedChar]],
      it: Ptr[ASN1_ITEM],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_i2d_bio(it: Ptr[ASN1_ITEM], out: Ptr[BIO], x: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_i2d_fp(it: Ptr[ASN1_ITEM], out: Ptr[FILE], x: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_i2d_mem_bio(it: Ptr[ASN1_ITEM], `val`: Ptr[ASN1_VALUE]): Ptr[BIO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_ndef_i2d(
      `val`: Ptr[ASN1_VALUE],
      out: Ptr[Ptr[CUnsignedChar]],
      it: Ptr[ASN1_ITEM],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_new(it: Ptr[ASN1_ITEM]): Ptr[ASN1_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_new_ex(
      it: Ptr[ASN1_ITEM],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[ASN1_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_pack(
      obj: Ptr[Byte],
      it: Ptr[ASN1_ITEM],
      oct: Ptr[Ptr[ASN1_OCTET_STRING]],
  ): Ptr[ASN1_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_print(
      out: Ptr[BIO],
      ifld: Ptr[ASN1_VALUE],
      indent: CInt,
      it: Ptr[ASN1_ITEM],
      pctx: Ptr[ASN1_PCTX],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_sign_ex(
      it: Ptr[ASN1_ITEM],
      algor1: Ptr[X509_ALGOR],
      algor2: Ptr[X509_ALGOR],
      signature: Ptr[ASN1_BIT_STRING],
      data: Ptr[Byte],
      id: Ptr[ASN1_OCTET_STRING],
      pkey: Ptr[EVP_PKEY],
      md: Ptr[EVP_MD],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_unpack(oct: Ptr[ASN1_STRING], it: Ptr[ASN1_ITEM]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_item_verify_ex(
      it: Ptr[ASN1_ITEM],
      alg: Ptr[X509_ALGOR],
      signature: Ptr[ASN1_BIT_STRING],
      data: Ptr[Byte],
      id: Ptr[ASN1_OCTET_STRING],
      pkey: Ptr[EVP_PKEY],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_mbstring_copy(
      out: Ptr[Ptr[ASN1_STRING]],
      in: Ptr[CUnsignedChar],
      len: CInt,
      inform: CInt,
      mask: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_mbstring_ncopy(
      out: Ptr[Ptr[ASN1_STRING]],
      in: Ptr[CUnsignedChar],
      len: CInt,
      inform: CInt,
      mask: CUnsignedLongInt,
      minsize: CLongInt,
      maxsize: CLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_object_size(constructed: CInt, length: CInt, tag: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_parse(bp: Ptr[BIO], pp: Ptr[CUnsignedChar], len: CLongInt, indent: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_parse_dump(
      bp: Ptr[BIO],
      pp: Ptr[CUnsignedChar],
      len: CLongInt,
      indent: CInt,
      dump: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_put_eoc(pp: Ptr[Ptr[CUnsignedChar]]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_put_object(
      pp: Ptr[Ptr[CUnsignedChar]],
      constructed: CInt,
      length: CInt,
      tag: CInt,
      xclass: CInt,
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_str2mask(str: CString, pmask: Ptr[CUnsignedLongInt]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_tag2bit(tag: CInt): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def ASN1_tag2str(tag: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def BIO_f_asn1(): Ptr[BIO_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def BIO_new_NDEF(out: Ptr[BIO], `val`: Ptr[ASN1_VALUE], it: Ptr[ASN1_ITEM]): Ptr[BIO] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def BN_to_ASN1_ENUMERATED(
      bn: Ptr[BIGNUM],
      ai: Ptr[ASN1_ENUMERATED],
  ): Ptr[ASN1_ENUMERATED] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def BN_to_ASN1_INTEGER(bn: Ptr[BIGNUM], ai: Ptr[ASN1_INTEGER]): Ptr[ASN1_INTEGER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def PEM_write_bio_ASN1_stream(
      out: Ptr[BIO],
      `val`: Ptr[ASN1_VALUE],
      in: Ptr[BIO],
      flags: CInt,
      hdr: CString,
      it: Ptr[ASN1_ITEM],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def SMIME_crlf_copy(in: Ptr[BIO], out: Ptr[BIO], flags: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def SMIME_read_ASN1(
      bio: Ptr[BIO],
      bcont: Ptr[Ptr[BIO]],
      it: Ptr[ASN1_ITEM],
  ): Ptr[ASN1_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def SMIME_read_ASN1_ex(
      bio: Ptr[BIO],
      flags: CInt,
      bcont: Ptr[Ptr[BIO]],
      it: Ptr[ASN1_ITEM],
      x: Ptr[Ptr[ASN1_VALUE]],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[ASN1_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def SMIME_text(in: Ptr[BIO], out: Ptr[BIO]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def SMIME_write_ASN1(
      bio: Ptr[BIO],
      `val`: Ptr[ASN1_VALUE],
      data: Ptr[BIO],
      flags: CInt,
      ctype_nid: CInt,
      econt_nid: CInt,
      mdalgs: Ptr[stack_st_X509_ALGOR],
      it: Ptr[ASN1_ITEM],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def SMIME_write_ASN1_ex(
      bio: Ptr[BIO],
      `val`: Ptr[ASN1_VALUE],
      data: Ptr[BIO],
      flags: CInt,
      ctype_nid: CInt,
      econt_nid: CInt,
      mdalgs: Ptr[stack_st_X509_ALGOR],
      it: Ptr[ASN1_ITEM],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def UTF8_getc(str: Ptr[CUnsignedChar], len: CInt, `val`: Ptr[CUnsignedLongInt]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def UTF8_putc(str: Ptr[CUnsignedChar], len: CInt, value: CUnsignedLongInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def a2d_ASN1_OBJECT(out: Ptr[CUnsignedChar], olen: CInt, buf: CString, num: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def a2i_ASN1_ENUMERATED(
      bp: Ptr[BIO],
      bs: Ptr[ASN1_ENUMERATED],
      buf: CString,
      size: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def a2i_ASN1_INTEGER(
      bp: Ptr[BIO],
      bs: Ptr[ASN1_INTEGER],
      buf: CString,
      size: CInt,
  ): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def a2i_ASN1_STRING(bp: Ptr[BIO], bs: Ptr[ASN1_STRING], buf: CString, size: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def d2i_ASN1_UINTEGER(
      a: Ptr[Ptr[ASN1_INTEGER]],
      pp: Ptr[Ptr[CUnsignedChar]],
      length: CLongInt,
  ): Ptr[ASN1_INTEGER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def i2a_ASN1_ENUMERATED(bp: Ptr[BIO], a: Ptr[ASN1_ENUMERATED]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def i2a_ASN1_INTEGER(bp: Ptr[BIO], a: Ptr[ASN1_INTEGER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def i2a_ASN1_OBJECT(bp: Ptr[BIO], a: Ptr[ASN1_OBJECT]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def i2a_ASN1_STRING(bp: Ptr[BIO], a: Ptr[ASN1_STRING], `type`: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def i2d_ASN1_bio_stream(
      out: Ptr[BIO],
      `val`: Ptr[ASN1_VALUE],
      in: Ptr[BIO],
      flags: CInt,
      it: Ptr[ASN1_ITEM],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  @extern def i2t_ASN1_OBJECT(buf: CString, buf_len: CInt, a: Ptr[ASN1_OBJECT]): CInt = extern

  // Generated template functions via macros in `asn1t.h`.
  // These are not generated by `sn-bindgen`, so we have to write them by hand.
  //
  // Docs reference:
  //
  // - https://docs.openssl.org/master/man3/X509_dup/

  @extern def PKCS8_PRIV_KEY_INFO_new(): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  @extern def PKCS8_PRIV_KEY_INFO_free(a: Ptr[PKCS8_PRIV_KEY_INFO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/bn.h
   */
  @extern def BN_bin2bn(s: Ptr[CUnsignedChar], len: CInt, ret: Ptr[BIGNUM]): Ptr[BIGNUM] = extern

  /**
   * [bindgen] header: /usr/include/openssl/bn.h
   */
  @extern def BN_free(a: Ptr[BIGNUM]): Unit = extern
