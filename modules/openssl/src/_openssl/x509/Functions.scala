package snhttp.experimental.openssl
package _openssl.x509

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._common.{size_t, time_t, uint32_t, uint64_t, FILE}
import _root_.snhttp.experimental.openssl._openssl.asn1.Types.{
  i2d_of_void,
  ASN1_ITEM,
  ASN1_BIT_STRING,
  ASN1_OBJECT,
  ASN1_TIME,
  ASN1_TYPE,
  ASN1_INTEGER,
  ASN1_STRING,
  ASN1_OCTET_STRING,
}
import _root_.snhttp.experimental.openssl._openssl.bio.Types.BIO
import _root_.snhttp.experimental.openssl._openssl.types.{
  EVP_MD,
  EVP_CIPHER,
  EC_KEY,
  EVP_PKEY,
  EVP_MD_CTX,
  OSSL_LIB_CTX,
  DSA,
  RSA,
}
import _root_.snhttp.experimental.openssl._openssl.pkcs12.Types.PKCS8_PRIV_KEY_INFO
import _root_.snhttp.experimental.openssl._openssl.x509.Types.{
  X509_ALGOR,
  X509_CRL_METHOD,
  X509_CRL,
  X509_EXTENSION,
  X509_INFO,
  X509_NAME,
  X509_NAME_ENTRY,
  X509_REVOKED,
  X509,
}
import _root_.snhttp.experimental.openssl._openssl.safestack.{
  stack_st_X509,
  stack_st_X509_ATTRIBUTE,
  stack_st_X509_REVOKED,
  stack_st_X509_EXTENSION,
}

import Structs.*

@extern
trait Functions:

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def ASN1_digest(
      i2d: Ptr[i2d_of_void],
      `type`: Ptr[EVP_MD],
      data: CString,
      md: Ptr[CUnsignedChar],
      len: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def ASN1_item_digest(
      it: Ptr[ASN1_ITEM],
      `type`: Ptr[EVP_MD],
      data: Ptr[Byte],
      md: Ptr[CUnsignedChar],
      len: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def ASN1_item_sign(
      it: Ptr[ASN1_ITEM],
      algor1: Ptr[X509_ALGOR],
      algor2: Ptr[X509_ALGOR],
      signature: Ptr[ASN1_BIT_STRING],
      data: Ptr[Byte],
      pkey: Ptr[EVP_PKEY],
      md: Ptr[EVP_MD],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def ASN1_item_sign_ctx(
      it: Ptr[ASN1_ITEM],
      algor1: Ptr[X509_ALGOR],
      algor2: Ptr[X509_ALGOR],
      signature: Ptr[ASN1_BIT_STRING],
      data: Ptr[Byte],
      ctx: Ptr[EVP_MD_CTX],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def ASN1_item_verify(
      it: Ptr[ASN1_ITEM],
      alg: Ptr[X509_ALGOR],
      signature: Ptr[ASN1_BIT_STRING],
      data: Ptr[Byte],
      pkey: Ptr[EVP_PKEY],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def ASN1_item_verify_ctx(
      it: Ptr[ASN1_ITEM],
      alg: Ptr[X509_ALGOR],
      signature: Ptr[ASN1_BIT_STRING],
      data: Ptr[Byte],
      ctx: Ptr[EVP_MD_CTX],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def ASN1_sign(
      i2d: Ptr[i2d_of_void],
      algor1: Ptr[X509_ALGOR],
      algor2: Ptr[X509_ALGOR],
      signature: Ptr[ASN1_BIT_STRING],
      data: CString,
      pkey: Ptr[EVP_PKEY],
      `type`: Ptr[EVP_MD],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def ASN1_verify(
      i2d: Ptr[i2d_of_void],
      algor1: Ptr[X509_ALGOR],
      signature: Ptr[ASN1_BIT_STRING],
      data: CString,
      pkey: Ptr[EVP_PKEY],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKCS82PKEY(p8: Ptr[PKCS8_PRIV_KEY_INFO]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKCS82PKEY_ex(
      p8: Ptr[PKCS8_PRIV_KEY_INFO],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY2PKCS8(pkey: Ptr[EVP_PKEY]): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_add1_attr(key: Ptr[EVP_PKEY], attr: Ptr[X509_ATTRIBUTE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_add1_attr_by_NID(
      key: Ptr[EVP_PKEY],
      nid: CInt,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_add1_attr_by_OBJ(
      key: Ptr[EVP_PKEY],
      obj: Ptr[ASN1_OBJECT],
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_add1_attr_by_txt(
      key: Ptr[EVP_PKEY],
      attrname: CString,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_delete_attr(key: Ptr[EVP_PKEY], loc: CInt): Ptr[X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_get_attr(key: Ptr[EVP_PKEY], loc: CInt): Ptr[X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_get_attr_by_NID(key: Ptr[EVP_PKEY], nid: CInt, lastpos: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_get_attr_by_OBJ(
      key: Ptr[EVP_PKEY],
      obj: Ptr[ASN1_OBJECT],
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def EVP_PKEY_get_attr_count(key: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def NETSCAPE_SPKI_b64_decode(str: CString, len: CInt): Ptr[NETSCAPE_SPKI] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def NETSCAPE_SPKI_b64_encode(x: Ptr[NETSCAPE_SPKI]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def NETSCAPE_SPKI_get_pubkey(x: Ptr[NETSCAPE_SPKI]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def NETSCAPE_SPKI_print(out: Ptr[BIO], spki: Ptr[NETSCAPE_SPKI]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def NETSCAPE_SPKI_set_pubkey(x: Ptr[NETSCAPE_SPKI], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def NETSCAPE_SPKI_sign(
      x: Ptr[NETSCAPE_SPKI],
      pkey: Ptr[EVP_PKEY],
      md: Ptr[EVP_MD],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def NETSCAPE_SPKI_verify(a: Ptr[NETSCAPE_SPKI], r: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbe2_set(
      cipher: Ptr[EVP_CIPHER],
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
  ): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbe2_set_iv(
      cipher: Ptr[EVP_CIPHER],
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      aiv: Ptr[CUnsignedChar],
      prf_nid: CInt,
  ): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbe2_set_iv_ex(
      cipher: Ptr[EVP_CIPHER],
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      aiv: Ptr[CUnsignedChar],
      prf_nid: CInt,
      libctx: Ptr[OSSL_LIB_CTX],
  ): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbe2_set_scrypt(
      cipher: Ptr[EVP_CIPHER],
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      aiv: Ptr[CUnsignedChar],
      N: uint64_t,
      r: uint64_t,
      p: uint64_t,
  ): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbe_set(
      alg: CInt,
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
  ): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbe_set0_algor(
      algor: Ptr[X509_ALGOR],
      alg: CInt,
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbe_set0_algor_ex(
      algor: Ptr[X509_ALGOR],
      alg: CInt,
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      libctx: Ptr[OSSL_LIB_CTX],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbe_set_ex(
      alg: CInt,
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      libctx: Ptr[OSSL_LIB_CTX],
  ): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbkdf2_set(
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      prf_nid: CInt,
      keylen: CInt,
  ): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS5_pbkdf2_set_ex(
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      prf_nid: CInt,
      keylen: CInt,
      libctx: Ptr[OSSL_LIB_CTX],
  ): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS8_pkey_add1_attr(p8: Ptr[PKCS8_PRIV_KEY_INFO], attr: Ptr[X509_ATTRIBUTE]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS8_pkey_add1_attr_by_NID(
      p8: Ptr[PKCS8_PRIV_KEY_INFO],
      nid: CInt,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS8_pkey_add1_attr_by_OBJ(
      p8: Ptr[PKCS8_PRIV_KEY_INFO],
      obj: Ptr[ASN1_OBJECT],
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS8_pkey_get0(
      ppkalg: Ptr[Ptr[ASN1_OBJECT]],
      pk: Ptr[Ptr[CUnsignedChar]],
      ppklen: Ptr[CInt],
      pa: Ptr[Ptr[X509_ALGOR]],
      p8: Ptr[PKCS8_PRIV_KEY_INFO],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS8_pkey_get0_attrs(p8: Ptr[PKCS8_PRIV_KEY_INFO]): Ptr[stack_st_X509_ATTRIBUTE] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def PKCS8_pkey_set0(
      priv: Ptr[PKCS8_PRIV_KEY_INFO],
      aobj: Ptr[ASN1_OBJECT],
      version: CInt,
      ptype: CInt,
      pval: Ptr[Byte],
      penc: Ptr[CUnsignedChar],
      penclen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ALGOR_cmp(a: Ptr[X509_ALGOR], b: Ptr[X509_ALGOR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ALGOR_copy(dest: Ptr[X509_ALGOR], src: Ptr[X509_ALGOR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ALGOR_get0(
      paobj: Ptr[Ptr[ASN1_OBJECT]],
      pptype: Ptr[CInt],
      ppval: Ptr[Ptr[Byte]],
      algor: Ptr[X509_ALGOR],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ALGOR_set0(
      alg: Ptr[X509_ALGOR],
      aobj: Ptr[ASN1_OBJECT],
      ptype: CInt,
      pval: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ALGOR_set_md(alg: Ptr[X509_ALGOR], md: Ptr[EVP_MD]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_count(attr: Ptr[X509_ATTRIBUTE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_create(
      nid: CInt,
      atrtype: CInt,
      value: Ptr[Byte],
  ): Ptr[X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_create_by_NID(
      attr: Ptr[Ptr[X509_ATTRIBUTE]],
      nid: CInt,
      atrtype: CInt,
      data: Ptr[Byte],
      len: CInt,
  ): Ptr[X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_create_by_OBJ(
      attr: Ptr[Ptr[X509_ATTRIBUTE]],
      obj: Ptr[ASN1_OBJECT],
      atrtype: CInt,
      data: Ptr[Byte],
      len: CInt,
  ): Ptr[X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_create_by_txt(
      attr: Ptr[Ptr[X509_ATTRIBUTE]],
      atrname: CString,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_get0_data(
      attr: Ptr[X509_ATTRIBUTE],
      idx: CInt,
      atrtype: CInt,
      data: Ptr[Byte],
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_get0_object(attr: Ptr[X509_ATTRIBUTE]): Ptr[ASN1_OBJECT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_get0_type(attr: Ptr[X509_ATTRIBUTE], idx: CInt): Ptr[ASN1_TYPE] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_set1_data(
      attr: Ptr[X509_ATTRIBUTE],
      attrtype: CInt,
      data: Ptr[Byte],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ATTRIBUTE_set1_object(attr: Ptr[X509_ATTRIBUTE], obj: Ptr[ASN1_OBJECT]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_METHOD_free(m: Ptr[X509_CRL_METHOD]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_METHOD_new(
      crl_init: CFuncPtr1[Ptr[X509_CRL], CInt],
      crl_free: CFuncPtr1[Ptr[X509_CRL], CInt],
      crl_lookup: CFuncPtr4[Ptr[X509_CRL], Ptr[Ptr[X509_REVOKED]], Ptr[ASN1_INTEGER], Ptr[
        X509_NAME,
      ], CInt],
      crl_verify: CFuncPtr2[Ptr[X509_CRL], Ptr[EVP_PKEY], CInt],
  ): Ptr[X509_CRL_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_add0_revoked(crl: Ptr[X509_CRL], rev: Ptr[X509_REVOKED]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_add1_ext_i2d(
      x: Ptr[X509_CRL],
      nid: CInt,
      value: Ptr[Byte],
      crit: CInt,
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_add_ext(x: Ptr[X509_CRL], ex: Ptr[X509_EXTENSION], loc: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_check_suiteb(
      crl: Ptr[X509_CRL],
      pk: Ptr[EVP_PKEY],
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_cmp(a: Ptr[X509_CRL], b: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_delete_ext(x: Ptr[X509_CRL], loc: CInt): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_diff(
      base: Ptr[X509_CRL],
      newer: Ptr[X509_CRL],
      skey: Ptr[EVP_PKEY],
      md: Ptr[EVP_MD],
      flags: CUnsignedInt,
  ): Ptr[X509_CRL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_digest(
      data: Ptr[X509_CRL],
      `type`: Ptr[EVP_MD],
      md: Ptr[CUnsignedChar],
      len: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get0_by_cert(
      crl: Ptr[X509_CRL],
      ret: Ptr[Ptr[X509_REVOKED]],
      x: Ptr[X509],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get0_by_serial(
      crl: Ptr[X509_CRL],
      ret: Ptr[Ptr[X509_REVOKED]],
      serial: Ptr[ASN1_INTEGER],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get0_extensions(crl: Ptr[X509_CRL]): Ptr[stack_st_X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get0_lastUpdate(crl: Ptr[X509_CRL]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get0_nextUpdate(crl: Ptr[X509_CRL]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get0_signature(
      crl: Ptr[X509_CRL],
      psig: Ptr[Ptr[ASN1_BIT_STRING]],
      palg: Ptr[Ptr[X509_ALGOR]],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_REVOKED(crl: Ptr[X509_CRL]): Ptr[stack_st_X509_REVOKED] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_ext(x: Ptr[X509_CRL], loc: CInt): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_ext_by_NID(x: Ptr[X509_CRL], nid: CInt, lastpos: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_ext_by_OBJ(
      x: Ptr[X509_CRL],
      obj: Ptr[ASN1_OBJECT],
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_ext_by_critical(x: Ptr[X509_CRL], crit: CInt, lastpos: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_ext_count(x: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_ext_d2i(
      x: Ptr[X509_CRL],
      nid: CInt,
      crit: Ptr[CInt],
      idx: Ptr[CInt],
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_issuer(crl: Ptr[X509_CRL]): Ptr[X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_lastUpdate(crl: Ptr[X509_CRL]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_meth_data(crl: Ptr[X509_CRL]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_nextUpdate(crl: Ptr[X509_CRL]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_signature_nid(crl: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_get_version(crl: Ptr[X509_CRL]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_load_http(
      url: CString,
      bio: Ptr[BIO],
      rbio: Ptr[BIO],
      timeout: CInt,
  ): Ptr[X509_CRL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_match(a: Ptr[X509_CRL], b: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_new_ex(libctx: Ptr[OSSL_LIB_CTX], propq: CString): Ptr[X509_CRL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_print(bp: Ptr[BIO], x: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_print_ex(out: Ptr[BIO], x: Ptr[X509_CRL], nmflag: CUnsignedLongInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_print_fp(bp: Ptr[FILE], x: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_set1_lastUpdate(x: Ptr[X509_CRL], tm: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_set1_nextUpdate(x: Ptr[X509_CRL], tm: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_set_default_method(meth: Ptr[X509_CRL_METHOD]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_set_issuer_name(x: Ptr[X509_CRL], name: Ptr[X509_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_set_meth_data(crl: Ptr[X509_CRL], dat: Ptr[Byte]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_set_version(x: Ptr[X509_CRL], version: CLongInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_sign(x: Ptr[X509_CRL], pkey: Ptr[EVP_PKEY], md: Ptr[EVP_MD]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_sign_ctx(x: Ptr[X509_CRL], ctx: Ptr[EVP_MD_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_sort(crl: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_up_ref(crl: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_CRL_verify(a: Ptr[X509_CRL], r: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_EXTENSION_create_by_NID(
      ex: Ptr[Ptr[X509_EXTENSION]],
      nid: CInt,
      crit: CInt,
      data: Ptr[ASN1_OCTET_STRING],
  ): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_EXTENSION_create_by_OBJ(
      ex: Ptr[Ptr[X509_EXTENSION]],
      obj: Ptr[ASN1_OBJECT],
      crit: CInt,
      data: Ptr[ASN1_OCTET_STRING],
  ): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_EXTENSION_get_critical(ex: Ptr[X509_EXTENSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_EXTENSION_get_data(ne: Ptr[X509_EXTENSION]): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_EXTENSION_get_object(ex: Ptr[X509_EXTENSION]): Ptr[ASN1_OBJECT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_EXTENSION_set_critical(ex: Ptr[X509_EXTENSION], crit: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_EXTENSION_set_data(ex: Ptr[X509_EXTENSION], data: Ptr[ASN1_OCTET_STRING]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_EXTENSION_set_object(ex: Ptr[X509_EXTENSION], obj: Ptr[ASN1_OBJECT]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_INFO_free(a: Ptr[X509_INFO]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_INFO_new(): Ptr[X509_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_ENTRY_create_by_NID(
      ne: Ptr[Ptr[X509_NAME_ENTRY]],
      nid: CInt,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[X509_NAME_ENTRY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_ENTRY_create_by_OBJ(
      ne: Ptr[Ptr[X509_NAME_ENTRY]],
      obj: Ptr[ASN1_OBJECT],
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[X509_NAME_ENTRY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_ENTRY_create_by_txt(
      ne: Ptr[Ptr[X509_NAME_ENTRY]],
      field: CString,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[X509_NAME_ENTRY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_ENTRY_get_data(ne: Ptr[X509_NAME_ENTRY]): Ptr[ASN1_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_ENTRY_get_object(ne: Ptr[X509_NAME_ENTRY]): Ptr[ASN1_OBJECT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_ENTRY_set(ne: Ptr[X509_NAME_ENTRY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_ENTRY_set_data(
      ne: Ptr[X509_NAME_ENTRY],
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_ENTRY_set_object(ne: Ptr[X509_NAME_ENTRY], obj: Ptr[ASN1_OBJECT]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_add_entry(
      name: Ptr[X509_NAME],
      ne: Ptr[X509_NAME_ENTRY],
      loc: CInt,
      set: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_add_entry_by_NID(
      name: Ptr[X509_NAME],
      nid: CInt,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
      loc: CInt,
      set: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_add_entry_by_OBJ(
      name: Ptr[X509_NAME],
      obj: Ptr[ASN1_OBJECT],
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
      loc: CInt,
      set: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_add_entry_by_txt(
      name: Ptr[X509_NAME],
      field: CString,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
      loc: CInt,
      set: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_cmp(a: Ptr[X509_NAME], b: Ptr[X509_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_delete_entry(name: Ptr[X509_NAME], loc: CInt): Ptr[X509_NAME_ENTRY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_digest(
      data: Ptr[X509_NAME],
      `type`: Ptr[EVP_MD],
      md: Ptr[CUnsignedChar],
      len: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_entry_count(name: Ptr[X509_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_get0_der(
      nm: Ptr[X509_NAME],
      pder: Ptr[Ptr[CUnsignedChar]],
      pderlen: Ptr[size_t],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_get_entry(name: Ptr[X509_NAME], loc: CInt): Ptr[X509_NAME_ENTRY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_get_index_by_NID(name: Ptr[X509_NAME], nid: CInt, lastpos: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_get_index_by_OBJ(
      name: Ptr[X509_NAME],
      obj: Ptr[ASN1_OBJECT],
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_get_text_by_NID(
      name: Ptr[X509_NAME],
      nid: CInt,
      buf: CString,
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_get_text_by_OBJ(
      name: Ptr[X509_NAME],
      obj: Ptr[ASN1_OBJECT],
      buf: CString,
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_hash_ex(
      x: Ptr[X509_NAME],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
      ok: Ptr[CInt],
  ): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_hash_old(x: Ptr[X509_NAME]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_oneline(a: Ptr[X509_NAME], buf: CString, size: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_print(bp: Ptr[BIO], name: Ptr[X509_NAME], obase: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_print_ex(
      out: Ptr[BIO],
      nm: Ptr[X509_NAME],
      indent: CInt,
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_print_ex_fp(
      fp: Ptr[FILE],
      nm: Ptr[X509_NAME],
      indent: CInt,
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_NAME_set(xn: Ptr[Ptr[X509_NAME]], name: Ptr[X509_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PKEY_free(a: Ptr[X509_PKEY]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PKEY_new(): Ptr[X509_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PUBKEY_eq(a: Ptr[X509_PUBKEY], b: Ptr[X509_PUBKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PUBKEY_get(key: Ptr[X509_PUBKEY]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PUBKEY_get0(key: Ptr[X509_PUBKEY]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PUBKEY_get0_param(
      ppkalg: Ptr[Ptr[ASN1_OBJECT]],
      pk: Ptr[Ptr[CUnsignedChar]],
      ppklen: Ptr[CInt],
      pa: Ptr[Ptr[X509_ALGOR]],
      pub: Ptr[X509_PUBKEY],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PUBKEY_new_ex(libctx: Ptr[OSSL_LIB_CTX], propq: CString): Ptr[X509_PUBKEY] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PUBKEY_set(x: Ptr[Ptr[X509_PUBKEY]], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_PUBKEY_set0_param(
      pub: Ptr[X509_PUBKEY],
      aobj: Ptr[ASN1_OBJECT],
      ptype: CInt,
      pval: Ptr[Byte],
      penc: Ptr[CUnsignedChar],
      penclen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_add1_attr(req: Ptr[X509_REQ], attr: Ptr[X509_ATTRIBUTE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_add1_attr_by_NID(
      req: Ptr[X509_REQ],
      nid: CInt,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_add1_attr_by_OBJ(
      req: Ptr[X509_REQ],
      obj: Ptr[ASN1_OBJECT],
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_add1_attr_by_txt(
      req: Ptr[X509_REQ],
      attrname: CString,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_add_extensions(req: Ptr[X509_REQ], ext: Ptr[stack_st_X509_EXTENSION]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_add_extensions_nid(
      req: Ptr[X509_REQ],
      exts: Ptr[stack_st_X509_EXTENSION],
      nid: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_check_private_key(x509: Ptr[X509_REQ], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_delete_attr(req: Ptr[X509_REQ], loc: CInt): Ptr[X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_digest(
      data: Ptr[X509_REQ],
      `type`: Ptr[EVP_MD],
      md: Ptr[CUnsignedChar],
      len: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_extension_nid(nid: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get0_distinguishing_id(x: Ptr[X509_REQ]): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get0_pubkey(req: Ptr[X509_REQ]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get0_signature(
      req: Ptr[X509_REQ],
      psig: Ptr[Ptr[ASN1_BIT_STRING]],
      palg: Ptr[Ptr[X509_ALGOR]],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_X509_PUBKEY(req: Ptr[X509_REQ]): Ptr[X509_PUBKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_attr(req: Ptr[X509_REQ], loc: CInt): Ptr[X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_attr_by_NID(req: Ptr[X509_REQ], nid: CInt, lastpos: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_attr_by_OBJ(
      req: Ptr[X509_REQ],
      obj: Ptr[ASN1_OBJECT],
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_attr_count(req: Ptr[X509_REQ]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_extension_nids(): Ptr[CInt] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_extensions(req: Ptr[X509_REQ]): Ptr[stack_st_X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_pubkey(req: Ptr[X509_REQ]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_signature_nid(req: Ptr[X509_REQ]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_subject_name(req: Ptr[X509_REQ]): Ptr[X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_get_version(req: Ptr[X509_REQ]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_new_ex(libctx: Ptr[OSSL_LIB_CTX], propq: CString): Ptr[X509_REQ] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_print(bp: Ptr[BIO], req: Ptr[X509_REQ]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_print_ex(
      bp: Ptr[BIO],
      x: Ptr[X509_REQ],
      nmflag: CUnsignedLongInt,
      cflag: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_print_fp(bp: Ptr[FILE], req: Ptr[X509_REQ]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_set0_distinguishing_id(
      x: Ptr[X509_REQ],
      d_id: Ptr[ASN1_OCTET_STRING],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_set0_signature(req: Ptr[X509_REQ], psig: Ptr[ASN1_BIT_STRING]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_set1_signature_algo(req: Ptr[X509_REQ], palg: Ptr[X509_ALGOR]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_set_extension_nids(nids: Ptr[CInt]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_set_pubkey(x: Ptr[X509_REQ], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_set_subject_name(req: Ptr[X509_REQ], name: Ptr[X509_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_set_version(x: Ptr[X509_REQ], version: CLongInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_sign(x: Ptr[X509_REQ], pkey: Ptr[EVP_PKEY], md: Ptr[EVP_MD]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_sign_ctx(x: Ptr[X509_REQ], ctx: Ptr[EVP_MD_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_to_X509(r: Ptr[X509_REQ], days: CInt, pkey: Ptr[EVP_PKEY]): Ptr[X509] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_verify(a: Ptr[X509_REQ], r: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REQ_verify_ex(
      a: Ptr[X509_REQ],
      r: Ptr[EVP_PKEY],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_add1_ext_i2d(
      x: Ptr[X509_REVOKED],
      nid: CInt,
      value: Ptr[Byte],
      crit: CInt,
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_add_ext(x: Ptr[X509_REVOKED], ex: Ptr[X509_EXTENSION], loc: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_delete_ext(x: Ptr[X509_REVOKED], loc: CInt): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get0_extensions(r: Ptr[X509_REVOKED]): Ptr[stack_st_X509_EXTENSION] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get0_revocationDate(x: Ptr[X509_REVOKED]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get0_serialNumber(x: Ptr[X509_REVOKED]): Ptr[ASN1_INTEGER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get_ext(x: Ptr[X509_REVOKED], loc: CInt): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get_ext_by_NID(x: Ptr[X509_REVOKED], nid: CInt, lastpos: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get_ext_by_OBJ(
      x: Ptr[X509_REVOKED],
      obj: Ptr[ASN1_OBJECT],
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get_ext_by_critical(
      x: Ptr[X509_REVOKED],
      crit: CInt,
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get_ext_count(x: Ptr[X509_REVOKED]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_get_ext_d2i(
      x: Ptr[X509_REVOKED],
      nid: CInt,
      crit: Ptr[CInt],
      idx: Ptr[CInt],
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_set_revocationDate(r: Ptr[X509_REVOKED], tm: Ptr[ASN1_TIME]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_REVOKED_set_serialNumber(x: Ptr[X509_REVOKED], serial: Ptr[ASN1_INTEGER]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_SIG_INFO_get(
      siginf: Ptr[X509_SIG_INFO],
      mdnid: Ptr[CInt],
      pknid: Ptr[CInt],
      secbits: Ptr[CInt],
      flags: Ptr[uint32_t],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_SIG_INFO_set(
      siginf: Ptr[X509_SIG_INFO],
      mdnid: CInt,
      pknid: CInt,
      secbits: CInt,
      flags: uint32_t,
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_SIG_get0(
      sig: Ptr[X509_SIG],
      palg: Ptr[Ptr[X509_ALGOR]],
      pdigest: Ptr[Ptr[ASN1_OCTET_STRING]],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_SIG_getm(
      sig: Ptr[X509_SIG],
      palg: Ptr[Ptr[X509_ALGOR]],
      pdigest: Ptr[Ptr[ASN1_OCTET_STRING]],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_add1_ext_i2d(
      x: Ptr[X509],
      nid: CInt,
      value: Ptr[Byte],
      crit: CInt,
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_add_cert(sk: Ptr[stack_st_X509], cert: Ptr[X509], flags: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_add_certs(sk: Ptr[stack_st_X509], certs: Ptr[stack_st_X509], flags: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_add_ext(x: Ptr[X509], ex: Ptr[X509_EXTENSION], loc: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_alias_get0(x: Ptr[X509], len: Ptr[CInt]): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_alias_set1(x: Ptr[X509], name: Ptr[CUnsignedChar], len: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_aux_print(out: Ptr[BIO], x: Ptr[X509], indent: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_certificate_type(x: Ptr[X509], pubkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_chain_check_suiteb(
      perror_depth: Ptr[CInt],
      x: Ptr[X509],
      chain: Ptr[stack_st_X509],
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_chain_up_ref(chain: Ptr[stack_st_X509]): Ptr[stack_st_X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_check_private_key(x509: Ptr[X509], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_cmp(a: Ptr[X509], b: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_cmp_current_time(s: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_cmp_time(s: Ptr[ASN1_TIME], t: Ptr[time_t]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_cmp_timeframe(
      vpm: Ptr[X509_VERIFY_PARAM],
      start: Ptr[ASN1_TIME],
      end: Ptr[ASN1_TIME],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_delete_ext(x: Ptr[X509], loc: CInt): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_digest(
      data: Ptr[X509],
      `type`: Ptr[EVP_MD],
      md: Ptr[CUnsignedChar],
      len: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_digest_sig(
      cert: Ptr[X509],
      md_used: Ptr[Ptr[EVP_MD]],
      md_is_fallback: Ptr[CInt],
  ): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_find_by_issuer_and_serial(
      sk: Ptr[stack_st_X509],
      name: Ptr[X509_NAME],
      serial: Ptr[ASN1_INTEGER],
  ): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_find_by_subject(sk: Ptr[stack_st_X509], name: Ptr[X509_NAME]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_distinguishing_id(x: Ptr[X509]): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_extensions(x: Ptr[X509]): Ptr[stack_st_X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_notAfter(x: Ptr[X509]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_notBefore(x: Ptr[X509]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_pubkey(x: Ptr[X509]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_pubkey_bitstr(x: Ptr[X509]): Ptr[ASN1_BIT_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_serialNumber(x: Ptr[X509]): Ptr[ASN1_INTEGER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_signature(
      psig: Ptr[Ptr[ASN1_BIT_STRING]],
      palg: Ptr[Ptr[X509_ALGOR]],
      x: Ptr[X509],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_tbs_sigalg(x: Ptr[X509]): Ptr[X509_ALGOR] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get0_uids(
      x: Ptr[X509],
      piuid: Ptr[Ptr[ASN1_BIT_STRING]],
      psuid: Ptr[Ptr[ASN1_BIT_STRING]],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_X509_PUBKEY(x: Ptr[X509]): Ptr[X509_PUBKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_default_cert_area(): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_default_cert_dir(): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_default_cert_dir_env(): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_default_cert_file(): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_default_cert_file_env(): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_default_private_dir(): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_ex_data(r: Ptr[X509], idx: CInt): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_ext(x: Ptr[X509], loc: CInt): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_ext_by_NID(x: Ptr[X509], nid: CInt, lastpos: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_ext_by_OBJ(x: Ptr[X509], obj: Ptr[ASN1_OBJECT], lastpos: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_ext_by_critical(x: Ptr[X509], crit: CInt, lastpos: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_ext_count(x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_ext_d2i(
      x: Ptr[X509],
      nid: CInt,
      crit: Ptr[CInt],
      idx: Ptr[CInt],
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_issuer_name(a: Ptr[X509]): Ptr[X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_pathlen(x: Ptr[X509]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_pubkey(x: Ptr[X509]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_pubkey_parameters(pkey: Ptr[EVP_PKEY], chain: Ptr[stack_st_X509]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_serialNumber(x: Ptr[X509]): Ptr[ASN1_INTEGER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_signature_info(
      x: Ptr[X509],
      mdnid: Ptr[CInt],
      pknid: Ptr[CInt],
      secbits: Ptr[CInt],
      flags: Ptr[uint32_t],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_signature_nid(x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_signature_type(x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_subject_name(a: Ptr[X509]): Ptr[X509_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_get_version(x: Ptr[X509]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_getm_notAfter(x: Ptr[X509]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_getm_notBefore(x: Ptr[X509]): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_gmtime_adj(s: Ptr[ASN1_TIME], adj: CLongInt): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_issuer_and_serial_cmp(a: Ptr[X509], b: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_issuer_and_serial_hash(a: Ptr[X509]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_issuer_name_cmp(a: Ptr[X509], b: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_issuer_name_hash(a: Ptr[X509]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_issuer_name_hash_old(a: Ptr[X509]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_keyid_get0(x: Ptr[X509], len: Ptr[CInt]): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_keyid_set1(x: Ptr[X509], id: Ptr[CUnsignedChar], len: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_load_http(
      url: CString,
      bio: Ptr[BIO],
      rbio: Ptr[BIO],
      timeout: CInt,
  ): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_new_ex(libctx: Ptr[OSSL_LIB_CTX], propq: CString): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_ocspid_print(bp: Ptr[BIO], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_print(bp: Ptr[BIO], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_print_ex(
      bp: Ptr[BIO],
      x: Ptr[X509],
      nmflag: CUnsignedLongInt,
      cflag: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_print_ex_fp(
      bp: Ptr[FILE],
      x: Ptr[X509],
      nmflag: CUnsignedLongInt,
      cflag: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_print_fp(bp: Ptr[FILE], x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_pubkey_digest(
      data: Ptr[X509],
      `type`: Ptr[EVP_MD],
      md: Ptr[CUnsignedChar],
      len: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_self_signed(cert: Ptr[X509], verify_signature: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set0_distinguishing_id(x: Ptr[X509], d_id: Ptr[ASN1_OCTET_STRING]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set1_notAfter(x: Ptr[X509], tm: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set1_notBefore(x: Ptr[X509], tm: Ptr[ASN1_TIME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set_ex_data(r: Ptr[X509], idx: CInt, arg: Ptr[Byte]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set_issuer_name(x: Ptr[X509], name: Ptr[X509_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set_pubkey(x: Ptr[X509], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set_serialNumber(x: Ptr[X509], serial: Ptr[ASN1_INTEGER]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set_subject_name(x: Ptr[X509], name: Ptr[X509_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_set_version(x: Ptr[X509], version: CLongInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_sign(x: Ptr[X509], pkey: Ptr[EVP_PKEY], md: Ptr[EVP_MD]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_sign_ctx(x: Ptr[X509], ctx: Ptr[EVP_MD_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_signature_dump(bp: Ptr[BIO], sig: Ptr[ASN1_STRING], indent: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_signature_print(
      bp: Ptr[BIO],
      alg: Ptr[X509_ALGOR],
      sig: Ptr[ASN1_STRING],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_subject_name_cmp(a: Ptr[X509], b: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_subject_name_hash(x: Ptr[X509]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_subject_name_hash_old(x: Ptr[X509]): CUnsignedLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_time_adj(s: Ptr[ASN1_TIME], adj: CLongInt, t: Ptr[time_t]): Ptr[ASN1_TIME] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_time_adj_ex(
      s: Ptr[ASN1_TIME],
      offset_day: CInt,
      offset_sec: CLongInt,
      t: Ptr[time_t],
  ): Ptr[ASN1_TIME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_to_X509_REQ(x: Ptr[X509], pkey: Ptr[EVP_PKEY], md: Ptr[EVP_MD]): Ptr[X509_REQ] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_up_ref(x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_verify(a: Ptr[X509], r: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509_verify_cert_error_string(n: CLongInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_add1_attr(
      x: Ptr[Ptr[stack_st_X509_ATTRIBUTE]],
      attr: Ptr[X509_ATTRIBUTE],
  ): Ptr[stack_st_X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_add1_attr_by_NID(
      x: Ptr[Ptr[stack_st_X509_ATTRIBUTE]],
      nid: CInt,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[stack_st_X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_add1_attr_by_OBJ(
      x: Ptr[Ptr[stack_st_X509_ATTRIBUTE]],
      obj: Ptr[ASN1_OBJECT],
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[stack_st_X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_add1_attr_by_txt(
      x: Ptr[Ptr[stack_st_X509_ATTRIBUTE]],
      attrname: CString,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[stack_st_X509_ATTRIBUTE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_delete_attr(x: Ptr[stack_st_X509_ATTRIBUTE], loc: CInt): Ptr[X509_ATTRIBUTE] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_get0_data_by_OBJ(
      x: Ptr[stack_st_X509_ATTRIBUTE],
      obj: Ptr[ASN1_OBJECT],
      lastpos: CInt,
      `type`: CInt,
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_get_attr(x: Ptr[stack_st_X509_ATTRIBUTE], loc: CInt): Ptr[X509_ATTRIBUTE] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_get_attr_by_NID(
      x: Ptr[stack_st_X509_ATTRIBUTE],
      nid: CInt,
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_get_attr_by_OBJ(
      sk: Ptr[stack_st_X509_ATTRIBUTE],
      obj: Ptr[ASN1_OBJECT],
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509at_get_attr_count(x: Ptr[stack_st_X509_ATTRIBUTE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509v3_add_ext(
      x: Ptr[Ptr[stack_st_X509_EXTENSION]],
      ex: Ptr[X509_EXTENSION],
      loc: CInt,
  ): Ptr[stack_st_X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509v3_delete_ext(x: Ptr[stack_st_X509_EXTENSION], loc: CInt): Ptr[X509_EXTENSION] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509v3_get_ext(x: Ptr[stack_st_X509_EXTENSION], loc: CInt): Ptr[X509_EXTENSION] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509v3_get_ext_by_NID(
      x: Ptr[stack_st_X509_EXTENSION],
      nid: CInt,
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509v3_get_ext_by_OBJ(
      x: Ptr[stack_st_X509_EXTENSION],
      obj: Ptr[ASN1_OBJECT],
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509v3_get_ext_by_critical(
      x: Ptr[stack_st_X509_EXTENSION],
      crit: CInt,
      lastpos: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def X509v3_get_ext_count(x: Ptr[stack_st_X509_EXTENSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_DSAPrivateKey_bio(bp: Ptr[BIO], dsa: Ptr[Ptr[DSA]]): Ptr[DSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_DSAPrivateKey_fp(fp: Ptr[FILE], dsa: Ptr[Ptr[DSA]]): Ptr[DSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_DSA_PUBKEY_bio(bp: Ptr[BIO], dsa: Ptr[Ptr[DSA]]): Ptr[DSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_DSA_PUBKEY_fp(fp: Ptr[FILE], dsa: Ptr[Ptr[DSA]]): Ptr[DSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_ECPrivateKey_bio(bp: Ptr[BIO], eckey: Ptr[Ptr[EC_KEY]]): Ptr[EC_KEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_ECPrivateKey_fp(fp: Ptr[FILE], eckey: Ptr[Ptr[EC_KEY]]): Ptr[EC_KEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_EC_PUBKEY_bio(bp: Ptr[BIO], eckey: Ptr[Ptr[EC_KEY]]): Ptr[EC_KEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_EC_PUBKEY_fp(fp: Ptr[FILE], eckey: Ptr[Ptr[EC_KEY]]): Ptr[EC_KEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PKCS8_PRIV_KEY_INFO_bio(
      bp: Ptr[BIO],
      p8inf: Ptr[Ptr[PKCS8_PRIV_KEY_INFO]],
  ): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PKCS8_PRIV_KEY_INFO_fp(
      fp: Ptr[FILE],
      p8inf: Ptr[Ptr[PKCS8_PRIV_KEY_INFO]],
  ): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PKCS8_bio(bp: Ptr[BIO], p8: Ptr[Ptr[X509_SIG]]): Ptr[X509_SIG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PKCS8_fp(fp: Ptr[FILE], p8: Ptr[Ptr[X509_SIG]]): Ptr[X509_SIG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PUBKEY_bio(bp: Ptr[BIO], a: Ptr[Ptr[EVP_PKEY]]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PUBKEY_ex(
      a: Ptr[Ptr[EVP_PKEY]],
      pp: Ptr[Ptr[CUnsignedChar]],
      length: CLongInt,
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PUBKEY_fp(fp: Ptr[FILE], a: Ptr[Ptr[EVP_PKEY]]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PrivateKey_bio(bp: Ptr[BIO], a: Ptr[Ptr[EVP_PKEY]]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PrivateKey_ex_bio(
      bp: Ptr[BIO],
      a: Ptr[Ptr[EVP_PKEY]],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PrivateKey_ex_fp(
      fp: Ptr[FILE],
      a: Ptr[Ptr[EVP_PKEY]],
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_PrivateKey_fp(fp: Ptr[FILE], a: Ptr[Ptr[EVP_PKEY]]): Ptr[EVP_PKEY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_RSAPrivateKey_bio(bp: Ptr[BIO], rsa: Ptr[Ptr[RSA]]): Ptr[RSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_RSAPrivateKey_fp(fp: Ptr[FILE], rsa: Ptr[Ptr[RSA]]): Ptr[RSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_RSAPublicKey_bio(bp: Ptr[BIO], rsa: Ptr[Ptr[RSA]]): Ptr[RSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_RSAPublicKey_fp(fp: Ptr[FILE], rsa: Ptr[Ptr[RSA]]): Ptr[RSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_RSA_PUBKEY_bio(bp: Ptr[BIO], rsa: Ptr[Ptr[RSA]]): Ptr[RSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_RSA_PUBKEY_fp(fp: Ptr[FILE], rsa: Ptr[Ptr[RSA]]): Ptr[RSA] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_X509_CRL_bio(bp: Ptr[BIO], crl: Ptr[Ptr[X509_CRL]]): Ptr[X509_CRL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_X509_CRL_fp(fp: Ptr[FILE], crl: Ptr[Ptr[X509_CRL]]): Ptr[X509_CRL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_X509_PUBKEY_bio(bp: Ptr[BIO], xpk: Ptr[Ptr[X509_PUBKEY]]): Ptr[X509_PUBKEY] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_X509_PUBKEY_fp(fp: Ptr[FILE], xpk: Ptr[Ptr[X509_PUBKEY]]): Ptr[X509_PUBKEY] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_X509_REQ_bio(bp: Ptr[BIO], req: Ptr[Ptr[X509_REQ]]): Ptr[X509_REQ] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_X509_REQ_fp(fp: Ptr[FILE], req: Ptr[Ptr[X509_REQ]]): Ptr[X509_REQ] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_X509_bio(bp: Ptr[BIO], x509: Ptr[Ptr[X509]]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def d2i_X509_fp(fp: Ptr[FILE], x509: Ptr[Ptr[X509]]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_DSAPrivateKey_bio(bp: Ptr[BIO], dsa: Ptr[DSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_DSAPrivateKey_fp(fp: Ptr[FILE], dsa: Ptr[DSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_DSA_PUBKEY_bio(bp: Ptr[BIO], dsa: Ptr[DSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_DSA_PUBKEY_fp(fp: Ptr[FILE], dsa: Ptr[DSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_ECPrivateKey_bio(bp: Ptr[BIO], eckey: Ptr[EC_KEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_ECPrivateKey_fp(fp: Ptr[FILE], eckey: Ptr[EC_KEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_EC_PUBKEY_bio(bp: Ptr[BIO], eckey: Ptr[EC_KEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_EC_PUBKEY_fp(fp: Ptr[FILE], eckey: Ptr[EC_KEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PKCS8PrivateKeyInfo_bio(bp: Ptr[BIO], key: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PKCS8PrivateKeyInfo_fp(fp: Ptr[FILE], key: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PKCS8_PRIV_KEY_INFO_bio(bp: Ptr[BIO], p8inf: Ptr[PKCS8_PRIV_KEY_INFO]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PKCS8_PRIV_KEY_INFO_fp(fp: Ptr[FILE], p8inf: Ptr[PKCS8_PRIV_KEY_INFO]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PKCS8_bio(bp: Ptr[BIO], p8: Ptr[X509_SIG]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PKCS8_fp(fp: Ptr[FILE], p8: Ptr[X509_SIG]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PUBKEY_bio(bp: Ptr[BIO], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PUBKEY_fp(fp: Ptr[FILE], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PrivateKey_bio(bp: Ptr[BIO], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_PrivateKey_fp(fp: Ptr[FILE], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_RSAPrivateKey_bio(bp: Ptr[BIO], rsa: Ptr[RSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_RSAPrivateKey_fp(fp: Ptr[FILE], rsa: Ptr[RSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_RSAPublicKey_bio(bp: Ptr[BIO], rsa: Ptr[RSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_RSAPublicKey_fp(fp: Ptr[FILE], rsa: Ptr[RSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_RSA_PUBKEY_bio(bp: Ptr[BIO], rsa: Ptr[RSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_RSA_PUBKEY_fp(fp: Ptr[FILE], rsa: Ptr[RSA]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_X509_CRL_bio(bp: Ptr[BIO], crl: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_X509_CRL_fp(fp: Ptr[FILE], crl: Ptr[X509_CRL]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_X509_PUBKEY_bio(bp: Ptr[BIO], xpk: Ptr[X509_PUBKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_X509_PUBKEY_fp(fp: Ptr[FILE], xpk: Ptr[X509_PUBKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_X509_REQ_bio(bp: Ptr[BIO], req: Ptr[X509_REQ]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_X509_REQ_fp(fp: Ptr[FILE], req: Ptr[X509_REQ]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_X509_bio(bp: Ptr[BIO], x509: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_X509_fp(fp: Ptr[FILE], x509: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_re_X509_CRL_tbs(req: Ptr[X509_CRL], pp: Ptr[Ptr[CUnsignedChar]]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_re_X509_REQ_tbs(req: Ptr[X509_REQ], pp: Ptr[Ptr[CUnsignedChar]]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  @extern def i2d_re_X509_tbs(x: Ptr[X509], pp: Ptr[Ptr[CUnsignedChar]]): CInt = extern
