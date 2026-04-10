package snhttp.experimental.openssl
package _openssl.pkcs12

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.types.Types.{
  EVP_CIPHER_CTX,
  EVP_CIPHER,
  EVP_MD,
  EVP_PKEY,
  OSSL_LIB_CTX,
}
import _root_.snhttp.experimental.openssl._openssl.asn1.Types.{
  ASN1_TYPE,
  ASN1_INTEGER,
  ASN1_ITEM,
  ASN1_OBJECT,
  ASN1_OCTET_STRING,
}
import _root_.snhttp.experimental.openssl._openssl.x509.Types.{X509, X509_CRL, X509_ALGOR, X509_SIG}
import _root_.snhttp.experimental.openssl._openssl.safestack.{
  stack_st_PKCS7,
  stack_st_PKCS12_SAFEBAG,
  stack_st_X509_ATTRIBUTE,
  stack_st_X509,
}
import _root_.snhttp.experimental.openssl._common.FILE
import _root_.snhttp.experimental.openssl._openssl.bio.Types.BIO

import Structs.{PKCS8_PRIV_KEY_INFO, PKCS12_SAFEBAG, PKCS12, PKCS7}

@extern
private[openssl] trait Functions:

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def OPENSSL_asc2uni(
      asc: CString,
      asclen: CInt,
      uni: Ptr[Ptr[CUnsignedChar]],
      unilen: Ptr[CInt],
  ): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def OPENSSL_uni2asc(uni: Ptr[CUnsignedChar], unilen: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def OPENSSL_uni2utf8(uni: Ptr[CUnsignedChar], unilen: CInt): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def OPENSSL_utf82uni(
      asc: CString,
      asclen: CInt,
      uni: Ptr[Ptr[CUnsignedChar]],
      unilen: Ptr[CInt],
  ): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_PBE_add(): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_PBE_keyivgen(
      ctx: Ptr[EVP_CIPHER_CTX],
      pass: CString,
      passlen: CInt,
      param: Ptr[ASN1_TYPE],
      cipher: Ptr[EVP_CIPHER],
      md_type: Ptr[EVP_MD],
      en_de: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_PBE_keyivgen_ex(
      ctx: Ptr[EVP_CIPHER_CTX],
      pass: CString,
      passlen: CInt,
      param: Ptr[ASN1_TYPE],
      cipher: Ptr[EVP_CIPHER],
      md_type: Ptr[EVP_MD],
      en_de: CInt,
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_create0_p8inf(p8: Ptr[PKCS8_PRIV_KEY_INFO]): Ptr[PKCS12_SAFEBAG] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_create0_pkcs8(p8: Ptr[X509_SIG]): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_create_cert(x509: Ptr[X509]): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_create_crl(crl: Ptr[X509_CRL]): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_create_pkcs8_encrypt(
      pbe_nid: CInt,
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      iter: CInt,
      p8inf: Ptr[PKCS8_PRIV_KEY_INFO],
  ): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_create_pkcs8_encrypt_ex(
      pbe_nid: CInt,
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      iter: CInt,
      p8inf: Ptr[PKCS8_PRIV_KEY_INFO],
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_create_secret(
      `type`: CInt,
      vtype: CInt,
      value: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get0_attr(bag: Ptr[PKCS12_SAFEBAG], attr_nid: CInt): Ptr[ASN1_TYPE] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get0_attrs(bag: Ptr[PKCS12_SAFEBAG]): Ptr[stack_st_X509_ATTRIBUTE] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get0_bag_obj(bag: Ptr[PKCS12_SAFEBAG]): Ptr[ASN1_TYPE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get0_bag_type(bag: Ptr[PKCS12_SAFEBAG]): Ptr[ASN1_OBJECT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get0_p8inf(bag: Ptr[PKCS12_SAFEBAG]): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get0_pkcs8(bag: Ptr[PKCS12_SAFEBAG]): Ptr[X509_SIG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get0_safes(bag: Ptr[PKCS12_SAFEBAG]): Ptr[stack_st_PKCS12_SAFEBAG] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get0_type(bag: Ptr[PKCS12_SAFEBAG]): Ptr[ASN1_OBJECT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get1_cert(bag: Ptr[PKCS12_SAFEBAG]): Ptr[X509] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get1_crl(bag: Ptr[PKCS12_SAFEBAG]): Ptr[X509_CRL] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get_bag_nid(bag: Ptr[PKCS12_SAFEBAG]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_SAFEBAG_get_nid(bag: Ptr[PKCS12_SAFEBAG]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add1_attr_by_NID(
      bag: Ptr[PKCS12_SAFEBAG],
      nid: CInt,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add1_attr_by_txt(
      bag: Ptr[PKCS12_SAFEBAG],
      attrname: CString,
      `type`: CInt,
      bytes: Ptr[CUnsignedChar],
      len: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_CSPName_asc(bag: Ptr[PKCS12_SAFEBAG], name: CString, namelen: CInt): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_cert(
      pbags: Ptr[Ptr[stack_st_PKCS12_SAFEBAG]],
      cert: Ptr[X509],
  ): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_friendlyname_asc(
      bag: Ptr[PKCS12_SAFEBAG],
      name: CString,
      namelen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_friendlyname_uni(
      bag: Ptr[PKCS12_SAFEBAG],
      name: Ptr[CUnsignedChar],
      namelen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_friendlyname_utf8(
      bag: Ptr[PKCS12_SAFEBAG],
      name: CString,
      namelen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_key(
      pbags: Ptr[Ptr[stack_st_PKCS12_SAFEBAG]],
      key: Ptr[EVP_PKEY],
      key_usage: CInt,
      iter: CInt,
      key_nid: CInt,
      pass: CString,
  ): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_key_ex(
      pbags: Ptr[Ptr[stack_st_PKCS12_SAFEBAG]],
      key: Ptr[EVP_PKEY],
      key_usage: CInt,
      iter: CInt,
      key_nid: CInt,
      pass: CString,
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_localkeyid(
      bag: Ptr[PKCS12_SAFEBAG],
      name: Ptr[CUnsignedChar],
      namelen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_safe(
      psafes: Ptr[Ptr[stack_st_PKCS7]],
      bags: Ptr[stack_st_PKCS12_SAFEBAG],
      safe_nid: CInt,
      iter: CInt,
      pass: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_safe_ex(
      psafes: Ptr[Ptr[stack_st_PKCS7]],
      bags: Ptr[stack_st_PKCS12_SAFEBAG],
      safe_nid: CInt,
      iter: CInt,
      pass: CString,
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_safes(safes: Ptr[stack_st_PKCS7], p7_nid: CInt): Ptr[PKCS12] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_safes_ex(
      safes: Ptr[stack_st_PKCS7],
      p7_nid: CInt,
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[PKCS12] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_add_secret(
      pbags: Ptr[Ptr[stack_st_PKCS12_SAFEBAG]],
      nid_type: CInt,
      value: Ptr[CUnsignedChar],
      len: CInt,
  ): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_create(
      pass: CString,
      name: CString,
      pkey: Ptr[EVP_PKEY],
      cert: Ptr[X509],
      ca: Ptr[stack_st_X509],
      nid_key: CInt,
      nid_cert: CInt,
      iter: CInt,
      mac_iter: CInt,
      keytype: CInt,
  ): Ptr[PKCS12] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_create_ex(
      pass: CString,
      name: CString,
      pkey: Ptr[EVP_PKEY],
      cert: Ptr[X509],
      ca: Ptr[stack_st_X509],
      nid_key: CInt,
      nid_cert: CInt,
      iter: CInt,
      mac_iter: CInt,
      keytype: CInt,
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[PKCS12] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_decrypt_skey(
      bag: Ptr[PKCS12_SAFEBAG],
      pass: CString,
      passlen: CInt,
  ): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_decrypt_skey_ex(
      bag: Ptr[PKCS12_SAFEBAG],
      pass: CString,
      passlen: CInt,
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_gen_mac(
      p12: Ptr[PKCS12],
      pass: CString,
      passlen: CInt,
      mac: Ptr[CUnsignedChar],
      maclen: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_get0_mac(
      pmac: Ptr[Ptr[ASN1_OCTET_STRING]],
      pmacalg: Ptr[Ptr[X509_ALGOR]],
      psalt: Ptr[Ptr[ASN1_OCTET_STRING]],
      piter: Ptr[Ptr[ASN1_INTEGER]],
      p12: Ptr[PKCS12],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_get_attr(bag: Ptr[PKCS12_SAFEBAG], attr_nid: CInt): Ptr[ASN1_TYPE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_get_attr_gen(
      attrs: Ptr[stack_st_X509_ATTRIBUTE],
      attr_nid: CInt,
  ): Ptr[ASN1_TYPE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_get_friendlyname(bag: Ptr[PKCS12_SAFEBAG]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_init(mode: CInt): Ptr[PKCS12] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_init_ex(mode: CInt, ctx: Ptr[OSSL_LIB_CTX], propq: CString): Ptr[PKCS12] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_item_decrypt_d2i(
      algor: Ptr[X509_ALGOR],
      it: Ptr[ASN1_ITEM],
      pass: CString,
      passlen: CInt,
      oct: Ptr[ASN1_OCTET_STRING],
      zbuf: CInt,
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_item_decrypt_d2i_ex(
      algor: Ptr[X509_ALGOR],
      it: Ptr[ASN1_ITEM],
      pass: CString,
      passlen: CInt,
      oct: Ptr[ASN1_OCTET_STRING],
      zbuf: CInt,
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_item_i2d_encrypt(
      algor: Ptr[X509_ALGOR],
      it: Ptr[ASN1_ITEM],
      pass: CString,
      passlen: CInt,
      obj: Ptr[Byte],
      zbuf: CInt,
  ): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_item_i2d_encrypt_ex(
      algor: Ptr[X509_ALGOR],
      it: Ptr[ASN1_ITEM],
      pass: CString,
      passlen: CInt,
      obj: Ptr[Byte],
      zbuf: CInt,
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_item_pack_safebag(
      obj: Ptr[Byte],
      it: Ptr[ASN1_ITEM],
      nid1: CInt,
      nid2: CInt,
  ): Ptr[PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_key_gen_asc(
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      id: CInt,
      iter: CInt,
      n: CInt,
      out: Ptr[CUnsignedChar],
      md_type: Ptr[EVP_MD],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_key_gen_asc_ex(
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      id: CInt,
      iter: CInt,
      n: CInt,
      out: Ptr[CUnsignedChar],
      md_type: Ptr[EVP_MD],
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_key_gen_uni(
      pass: Ptr[CUnsignedChar],
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      id: CInt,
      iter: CInt,
      n: CInt,
      out: Ptr[CUnsignedChar],
      md_type: Ptr[EVP_MD],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_key_gen_uni_ex(
      pass: Ptr[CUnsignedChar],
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      id: CInt,
      iter: CInt,
      n: CInt,
      out: Ptr[CUnsignedChar],
      md_type: Ptr[EVP_MD],
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_key_gen_utf8(
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      id: CInt,
      iter: CInt,
      n: CInt,
      out: Ptr[CUnsignedChar],
      md_type: Ptr[EVP_MD],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_key_gen_utf8_ex(
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      id: CInt,
      iter: CInt,
      n: CInt,
      out: Ptr[CUnsignedChar],
      md_type: Ptr[EVP_MD],
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_mac_present(p12: Ptr[PKCS12]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_newpass(p12: Ptr[PKCS12], oldpass: CString, newpass: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_pack_authsafes(p12: Ptr[PKCS12], safes: Ptr[stack_st_PKCS7]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_pack_p7data(sk: Ptr[stack_st_PKCS12_SAFEBAG]): Ptr[PKCS7] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_pack_p7encdata(
      pbe_nid: CInt,
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      iter: CInt,
      bags: Ptr[stack_st_PKCS12_SAFEBAG],
  ): Ptr[PKCS7] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_pack_p7encdata_ex(
      pbe_nid: CInt,
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      iter: CInt,
      bags: Ptr[stack_st_PKCS12_SAFEBAG],
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[PKCS7] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_parse(
      p12: Ptr[PKCS12],
      pass: CString,
      pkey: Ptr[Ptr[EVP_PKEY]],
      cert: Ptr[Ptr[X509]],
      ca: Ptr[Ptr[stack_st_X509]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_pbe_crypt(
      algor: Ptr[X509_ALGOR],
      pass: CString,
      passlen: CInt,
      in: Ptr[CUnsignedChar],
      inlen: CInt,
      data: Ptr[Ptr[CUnsignedChar]],
      datalen: Ptr[CInt],
      en_de: CInt,
  ): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_pbe_crypt_ex(
      algor: Ptr[X509_ALGOR],
      pass: CString,
      passlen: CInt,
      in: Ptr[CUnsignedChar],
      inlen: CInt,
      data: Ptr[Ptr[CUnsignedChar]],
      datalen: Ptr[CInt],
      en_de: CInt,
      libctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[CUnsignedChar] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_set_mac(
      p12: Ptr[PKCS12],
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      iter: CInt,
      md_type: Ptr[EVP_MD],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_setup_mac(
      p12: Ptr[PKCS12],
      iter: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      md_type: Ptr[EVP_MD],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_unpack_authsafes(p12: Ptr[PKCS12]): Ptr[stack_st_PKCS7] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_unpack_p7data(p7: Ptr[PKCS7]): Ptr[stack_st_PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_unpack_p7encdata(
      p7: Ptr[PKCS7],
      pass: CString,
      passlen: CInt,
  ): Ptr[stack_st_PKCS12_SAFEBAG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS12_verify_mac(p12: Ptr[PKCS12], pass: CString, passlen: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS8_add_keyusage(p8: Ptr[PKCS8_PRIV_KEY_INFO], usage: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS8_decrypt(
      p8: Ptr[X509_SIG],
      pass: CString,
      passlen: CInt,
  ): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS8_decrypt_ex(
      p8: Ptr[X509_SIG],
      pass: CString,
      passlen: CInt,
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[PKCS8_PRIV_KEY_INFO] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS8_encrypt(
      pbe_nid: CInt,
      cipher: Ptr[EVP_CIPHER],
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      iter: CInt,
      p8: Ptr[PKCS8_PRIV_KEY_INFO],
  ): Ptr[X509_SIG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS8_encrypt_ex(
      pbe_nid: CInt,
      cipher: Ptr[EVP_CIPHER],
      pass: CString,
      passlen: CInt,
      salt: Ptr[CUnsignedChar],
      saltlen: CInt,
      iter: CInt,
      p8: Ptr[PKCS8_PRIV_KEY_INFO],
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[X509_SIG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS8_get_attr(p8: Ptr[PKCS8_PRIV_KEY_INFO], attr_nid: CInt): Ptr[ASN1_TYPE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS8_set0_pbe(
      pass: CString,
      passlen: CInt,
      p8inf: Ptr[PKCS8_PRIV_KEY_INFO],
      pbe: Ptr[X509_ALGOR],
  ): Ptr[X509_SIG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def PKCS8_set0_pbe_ex(
      pass: CString,
      passlen: CInt,
      p8inf: Ptr[PKCS8_PRIV_KEY_INFO],
      pbe: Ptr[X509_ALGOR],
      ctx: Ptr[OSSL_LIB_CTX],
      propq: CString,
  ): Ptr[X509_SIG] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def d2i_PKCS12_bio(bp: Ptr[BIO], p12: Ptr[Ptr[PKCS12]]): Ptr[PKCS12] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def d2i_PKCS12_fp(fp: Ptr[FILE], p12: Ptr[Ptr[PKCS12]]): Ptr[PKCS12] = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def i2d_PKCS12_bio(bp: Ptr[BIO], p12: Ptr[PKCS12]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  def i2d_PKCS12_fp(fp: Ptr[FILE], p12: Ptr[PKCS12]): CInt = extern
