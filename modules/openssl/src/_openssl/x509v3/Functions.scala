package snhttp.experimental.openssl
package _openssl.x509v3

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.asn1.Types.{
  ASN1_BIT_STRING,
  ASN1_OBJECT,
  ASN1_TYPE,
  ASN1_INTEGER,
  ASN1_STRING,
  ASN1_OCTET_STRING,
  ASN1_PRINTABLESTRING,
  ASN1_IA5STRING,
  ASN1_UTF8STRING,
  ASN1_ENUMERATED,
}
import _root_.snhttp.experimental.openssl._openssl.safestack.{
  stack_st_X509,
  stack_st_ADMISSIONS,
  stack_st_ASN1_OBJECT,
  stack_st_ASN1_STRING,
  stack_st_X509_EXTENSION,
  stack_st_CONF_VALUE,
  stack_st_OPENSSL_STRING,
}
import _root_.snhttp.experimental.openssl._openssl.x509.Types.{
  X509_CRL,
  X509_EXTENSION,
  X509_NAME,
  X509_STORE_CTX,
  X509,
  X509_REQ,
}
import _root_.snhttp.experimental.openssl._openssl.bio.Types.BIO
import _root_.snhttp.experimental.openssl._openssl.conf.Types.{
  lhash_st_CONF_VALUE,
  CONF,
  CONF_VALUE,
}
import _root_.snhttp.experimental.openssl._openssl.types.EVP_PKEY
import _root_.snhttp.experimental.openssl._common.{size_t, uint32_t, FILE}

import Types.*

@extern
trait Functions:

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSIONS_get0_admissionAuthority(a: Ptr[ADMISSIONS]): Ptr[GENERAL_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSIONS_get0_namingAuthority(a: Ptr[ADMISSIONS]): Ptr[NAMING_AUTHORITY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSIONS_get0_professionInfos(a: Ptr[ADMISSIONS]): Ptr[PROFESSION_INFOS] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSIONS_set0_admissionAuthority(a: Ptr[ADMISSIONS], aa: Ptr[GENERAL_NAME]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSIONS_set0_namingAuthority(a: Ptr[ADMISSIONS], na: Ptr[NAMING_AUTHORITY]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSIONS_set0_professionInfos(a: Ptr[ADMISSIONS], pi: Ptr[PROFESSION_INFOS]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSION_SYNTAX_get0_admissionAuthority(
      as: Ptr[ADMISSION_SYNTAX],
  ): Ptr[GENERAL_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSION_SYNTAX_get0_contentsOfAdmissions(
      as: Ptr[ADMISSION_SYNTAX],
  ): Ptr[stack_st_ADMISSIONS] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSION_SYNTAX_set0_admissionAuthority(
      as: Ptr[ADMISSION_SYNTAX],
      aa: Ptr[GENERAL_NAME],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def ADMISSION_SYNTAX_set0_contentsOfAdmissions(
      as: Ptr[ADMISSION_SYNTAX],
      a: Ptr[stack_st_ADMISSIONS],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def DIST_POINT_set_dpname(dpn: Ptr[DIST_POINT_NAME], iname: Ptr[X509_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def GENERAL_NAME_cmp(a: Ptr[GENERAL_NAME], b: Ptr[GENERAL_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def GENERAL_NAME_get0_otherName(
      gen: Ptr[GENERAL_NAME],
      poid: Ptr[Ptr[ASN1_OBJECT]],
      pvalue: Ptr[Ptr[ASN1_TYPE]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def GENERAL_NAME_get0_value(a: Ptr[GENERAL_NAME], ptype: Ptr[CInt]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def GENERAL_NAME_print(out: Ptr[BIO], gen: Ptr[GENERAL_NAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def GENERAL_NAME_set0_othername(
      gen: Ptr[GENERAL_NAME],
      oid: Ptr[ASN1_OBJECT],
      value: Ptr[ASN1_TYPE],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def GENERAL_NAME_set0_value(a: Ptr[GENERAL_NAME], `type`: CInt, value: Ptr[Byte]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def NAME_CONSTRAINTS_check(x: Ptr[X509], nc: Ptr[NAME_CONSTRAINTS]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def NAME_CONSTRAINTS_check_CN(x: Ptr[X509], nc: Ptr[NAME_CONSTRAINTS]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def NAMING_AUTHORITY_get0_authorityId(n: Ptr[NAMING_AUTHORITY]): Ptr[ASN1_OBJECT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def NAMING_AUTHORITY_get0_authorityText(n: Ptr[NAMING_AUTHORITY]): Ptr[ASN1_STRING] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def NAMING_AUTHORITY_get0_authorityURL(n: Ptr[NAMING_AUTHORITY]): Ptr[ASN1_IA5STRING] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def NAMING_AUTHORITY_set0_authorityId(
      n: Ptr[NAMING_AUTHORITY],
      namingAuthorityId: Ptr[ASN1_OBJECT],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def NAMING_AUTHORITY_set0_authorityText(
      n: Ptr[NAMING_AUTHORITY],
      namingAuthorityText: Ptr[ASN1_STRING],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def NAMING_AUTHORITY_set0_authorityURL(
      n: Ptr[NAMING_AUTHORITY],
      namingAuthorityUrl: Ptr[ASN1_IA5STRING],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def OTHERNAME_cmp(a: Ptr[OTHERNAME], b: Ptr[OTHERNAME]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_get0_addProfessionInfo(
      pi: Ptr[PROFESSION_INFO],
  ): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_get0_namingAuthority(
      pi: Ptr[PROFESSION_INFO],
  ): Ptr[NAMING_AUTHORITY] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_get0_professionItems(
      pi: Ptr[PROFESSION_INFO],
  ): Ptr[stack_st_ASN1_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_get0_professionOIDs(
      pi: Ptr[PROFESSION_INFO],
  ): Ptr[stack_st_ASN1_OBJECT] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_get0_registrationNumber(
      pi: Ptr[PROFESSION_INFO],
  ): Ptr[ASN1_PRINTABLESTRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_set0_addProfessionInfo(
      pi: Ptr[PROFESSION_INFO],
      aos: Ptr[ASN1_OCTET_STRING],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_set0_namingAuthority(
      pi: Ptr[PROFESSION_INFO],
      na: Ptr[NAMING_AUTHORITY],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_set0_professionItems(
      pi: Ptr[PROFESSION_INFO],
      as: Ptr[stack_st_ASN1_STRING],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_set0_professionOIDs(
      pi: Ptr[PROFESSION_INFO],
      po: Ptr[stack_st_ASN1_OBJECT],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def PROFESSION_INFO_set0_registrationNumber(
      pi: Ptr[PROFESSION_INFO],
      rn: Ptr[ASN1_PRINTABLESTRING],
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def SXNET_add_id_INTEGER(
      psx: Ptr[Ptr[SXNET]],
      izone: Ptr[ASN1_INTEGER],
      user: CString,
      userlen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def SXNET_add_id_asc(
      psx: Ptr[Ptr[SXNET]],
      zone: CString,
      user: CString,
      userlen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def SXNET_add_id_ulong(
      psx: Ptr[Ptr[SXNET]],
      lzone: CUnsignedLongInt,
      user: CString,
      userlen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def SXNET_get_id_INTEGER(
      sx: Ptr[SXNET],
      zone: Ptr[ASN1_INTEGER],
  ): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def SXNET_get_id_asc(sx: Ptr[SXNET], zone: CString): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def SXNET_get_id_ulong(sx: Ptr[SXNET], lzone: CUnsignedLongInt): Ptr[ASN1_OCTET_STRING] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_CRL_add_conf(
      conf: Ptr[lhash_st_CONF_VALUE],
      ctx: Ptr[X509V3_CTX],
      section: CString,
      crl: Ptr[X509_CRL],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_CRL_add_nconf(
      conf: Ptr[CONF],
      ctx: Ptr[X509V3_CTX],
      section: CString,
      crl: Ptr[X509_CRL],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_REQ_add_conf(
      conf: Ptr[lhash_st_CONF_VALUE],
      ctx: Ptr[X509V3_CTX],
      section: CString,
      req: Ptr[X509_REQ],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_REQ_add_nconf(
      conf: Ptr[CONF],
      ctx: Ptr[X509V3_CTX],
      section: CString,
      req: Ptr[X509_REQ],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_add(ext: Ptr[X509V3_EXT_METHOD]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_add_alias(nid_to: CInt, nid_from: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_add_conf(
      conf: Ptr[lhash_st_CONF_VALUE],
      ctx: Ptr[X509V3_CTX],
      section: CString,
      cert: Ptr[X509],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_add_list(extlist: Ptr[X509V3_EXT_METHOD]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_add_nconf(
      conf: Ptr[CONF],
      ctx: Ptr[X509V3_CTX],
      section: CString,
      cert: Ptr[X509],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_add_nconf_sk(
      conf: Ptr[CONF],
      ctx: Ptr[X509V3_CTX],
      section: CString,
      sk: Ptr[Ptr[stack_st_X509_EXTENSION]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_cleanup(): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_conf(
      conf: Ptr[lhash_st_CONF_VALUE],
      ctx: Ptr[X509V3_CTX],
      name: CString,
      value: CString,
  ): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_conf_nid(
      conf: Ptr[lhash_st_CONF_VALUE],
      ctx: Ptr[X509V3_CTX],
      ext_nid: CInt,
      value: CString,
  ): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_d2i(ext: Ptr[X509_EXTENSION]): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_get(ext: Ptr[X509_EXTENSION]): Ptr[X509V3_EXT_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_get_nid(nid: CInt): Ptr[X509V3_EXT_METHOD] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_i2d(ext_nid: CInt, crit: CInt, ext_struc: Ptr[Byte]): Ptr[X509_EXTENSION] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_nconf(
      conf: Ptr[CONF],
      ctx: Ptr[X509V3_CTX],
      name: CString,
      value: CString,
  ): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_nconf_nid(
      conf: Ptr[CONF],
      ctx: Ptr[X509V3_CTX],
      ext_nid: CInt,
      value: CString,
  ): Ptr[X509_EXTENSION] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_print(
      out: Ptr[BIO],
      ext: Ptr[X509_EXTENSION],
      flag: CUnsignedLongInt,
      indent: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_print_fp(
      out: Ptr[FILE],
      ext: Ptr[X509_EXTENSION],
      flag: CInt,
      indent: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_EXT_val_prn(
      out: Ptr[BIO],
      `val`: Ptr[stack_st_CONF_VALUE],
      indent: CInt,
      ml: CInt,
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_NAME_from_section(
      nm: Ptr[X509_NAME],
      dn_sk: Ptr[stack_st_CONF_VALUE],
      chtype: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_add1_i2d(
      x: Ptr[Ptr[stack_st_X509_EXTENSION]],
      nid: CInt,
      value: Ptr[Byte],
      crit: CInt,
      flags: CUnsignedLongInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_add_standard_extensions(): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_add_value(
      name: CString,
      value: CString,
      extlist: Ptr[Ptr[stack_st_CONF_VALUE]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_add_value_bool(
      name: CString,
      asn1_bool: CInt,
      extlist: Ptr[Ptr[stack_st_CONF_VALUE]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_add_value_bool_nf(
      name: CString,
      asn1_bool: CInt,
      extlist: Ptr[Ptr[stack_st_CONF_VALUE]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_add_value_int(
      name: CString,
      aint: Ptr[ASN1_INTEGER],
      extlist: Ptr[Ptr[stack_st_CONF_VALUE]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_add_value_uchar(
      name: CString,
      value: Ptr[CUnsignedChar],
      extlist: Ptr[Ptr[stack_st_CONF_VALUE]],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_conf_free(`val`: Ptr[CONF_VALUE]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_extensions_print(
      out: Ptr[BIO],
      title: CString,
      exts: Ptr[stack_st_X509_EXTENSION],
      flag: CUnsignedLongInt,
      indent: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_get_d2i(
      x: Ptr[stack_st_X509_EXTENSION],
      nid: CInt,
      crit: Ptr[CInt],
      idx: Ptr[CInt],
  ): Ptr[Byte] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_get_section(ctx: Ptr[X509V3_CTX], section: CString): Ptr[stack_st_CONF_VALUE] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_get_string(ctx: Ptr[X509V3_CTX], name: CString, section: CString): CString =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_get_value_bool(value: Ptr[CONF_VALUE], asn1_bool: Ptr[CInt]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_get_value_int(value: Ptr[CONF_VALUE], aint: Ptr[Ptr[ASN1_INTEGER]]): CInt =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_parse_list(line: CString): Ptr[stack_st_CONF_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_section_free(ctx: Ptr[X509V3_CTX], section: Ptr[stack_st_CONF_VALUE]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_set_conf_lhash(ctx: Ptr[X509V3_CTX], lhash: Ptr[lhash_st_CONF_VALUE]): Unit =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_set_ctx(
      ctx: Ptr[X509V3_CTX],
      issuer: Ptr[X509],
      subject: Ptr[X509],
      req: Ptr[X509_REQ],
      crl: Ptr[X509_CRL],
      flags: CInt,
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_set_issuer_pkey(ctx: Ptr[X509V3_CTX], pkey: Ptr[EVP_PKEY]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_set_nconf(ctx: Ptr[X509V3_CTX], conf: Ptr[CONF]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509V3_string_free(ctx: Ptr[X509V3_CTX], str: CString): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_POLICY_NODE_print(
      out: Ptr[BIO],
      node: Ptr[X509_POLICY_NODE],
      indent: CInt,
  ): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_add(
      id: CInt,
      trust: CInt,
      flags: CInt,
      ck: CFuncPtr3[Ptr[X509_PURPOSE], Ptr[X509], CInt, CInt],
      name: CString,
      sname: CString,
      arg: Ptr[Byte],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_cleanup(): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_get0(idx: CInt): Ptr[X509_PURPOSE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_get0_name(xp: Ptr[X509_PURPOSE]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_get0_sname(xp: Ptr[X509_PURPOSE]): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_get_by_id(id: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_get_by_sname(sname: CString): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_get_count(): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_get_id(_0: Ptr[X509_PURPOSE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_get_trust(xp: Ptr[X509_PURPOSE]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_PURPOSE_set(p: Ptr[CInt], purpose: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_REQ_get1_email(x: Ptr[X509_REQ]): Ptr[stack_st_OPENSSL_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_check_akid(issuer: Ptr[X509], akid: Ptr[AUTHORITY_KEYID]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_check_ca(x: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_check_email(
      x: Ptr[X509],
      chk: CString,
      chklen: size_t,
      flags: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_check_host(
      x: Ptr[X509],
      chk: CString,
      chklen: size_t,
      flags: CUnsignedInt,
      peername: Ptr[CString],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_check_ip(
      x: Ptr[X509],
      chk: Ptr[CUnsignedChar],
      chklen: size_t,
      flags: CUnsignedInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_check_ip_asc(x: Ptr[X509], ipasc: CString, flags: CUnsignedInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_check_issued(issuer: Ptr[X509], subject: Ptr[X509]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_check_purpose(x: Ptr[X509], id: CInt, ca: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_email_free(sk: Ptr[stack_st_OPENSSL_STRING]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get0_authority_issuer(x: Ptr[X509]): Ptr[GENERAL_NAMES] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get0_authority_key_id(x: Ptr[X509]): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get0_authority_serial(x: Ptr[X509]): Ptr[ASN1_INTEGER] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get0_subject_key_id(x: Ptr[X509]): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get1_email(x: Ptr[X509]): Ptr[stack_st_OPENSSL_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get1_ocsp(x: Ptr[X509]): Ptr[stack_st_OPENSSL_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get_extended_key_usage(x: Ptr[X509]): uint32_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get_extension_flags(x: Ptr[X509]): uint32_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get_key_usage(x: Ptr[X509]): uint32_t = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_get_proxy_pathlen(x: Ptr[X509]): CLongInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_set_proxy_flag(x: Ptr[X509]): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_set_proxy_pathlen(x: Ptr[X509], l: CLongInt): Unit = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509_supported_extension(ex: Ptr[X509_EXTENSION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_add_inherit(
      addr: Ptr[IPAddrBlocks],
      afi: CUnsignedInt,
      safi: Ptr[CUnsignedInt],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_add_prefix(
      addr: Ptr[IPAddrBlocks],
      afi: CUnsignedInt,
      safi: Ptr[CUnsignedInt],
      a: Ptr[CUnsignedChar],
      prefixlen: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_add_range(
      addr: Ptr[IPAddrBlocks],
      afi: CUnsignedInt,
      safi: Ptr[CUnsignedInt],
      min: Ptr[CUnsignedChar],
      max: Ptr[CUnsignedChar],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_canonize(addr: Ptr[IPAddrBlocks]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_get_afi(f: Ptr[IPAddressFamily]): CUnsignedInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_get_range(
      aor: Ptr[IPAddressOrRange],
      afi: CUnsignedInt,
      min: Ptr[CUnsignedChar],
      max: Ptr[CUnsignedChar],
      length: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_inherits(addr: Ptr[IPAddrBlocks]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_is_canonical(addr: Ptr[IPAddrBlocks]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_subset(a: Ptr[IPAddrBlocks], b: Ptr[IPAddrBlocks]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_validate_path(_0: Ptr[X509_STORE_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_addr_validate_resource_set(
      chain: Ptr[stack_st_X509],
      ext: Ptr[IPAddrBlocks],
      allow_inheritance: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_asid_add_id_or_range(
      asid: Ptr[ASIdentifiers],
      which: CInt,
      min: Ptr[ASN1_INTEGER],
      max: Ptr[ASN1_INTEGER],
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_asid_add_inherit(asid: Ptr[ASIdentifiers], which: CInt): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_asid_canonize(asid: Ptr[ASIdentifiers]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_asid_inherits(asid: Ptr[ASIdentifiers]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_asid_is_canonical(asid: Ptr[ASIdentifiers]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_asid_subset(a: Ptr[ASIdentifiers], b: Ptr[ASIdentifiers]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_asid_validate_path(_0: Ptr[X509_STORE_CTX]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def X509v3_asid_validate_resource_set(
      chain: Ptr[stack_st_X509],
      ext: Ptr[ASIdentifiers],
      allow_inheritance: CInt,
  ): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def a2i_GENERAL_NAME(
      out: Ptr[GENERAL_NAME],
      method: Ptr[X509V3_EXT_METHOD],
      ctx: Ptr[X509V3_CTX],
      gen_type: CInt,
      value: CString,
      is_nc: CInt,
  ): Ptr[GENERAL_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def a2i_IPADDRESS(ipasc: CString): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def a2i_IPADDRESS_NC(ipasc: CString): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2a_ACCESS_DESCRIPTION(bp: Ptr[BIO], a: Ptr[ACCESS_DESCRIPTION]): CInt = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2s_ASN1_ENUMERATED(
      meth: Ptr[X509V3_EXT_METHOD],
      aint: Ptr[ASN1_ENUMERATED],
  ): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2s_ASN1_ENUMERATED_TABLE(
      meth: Ptr[X509V3_EXT_METHOD],
      aint: Ptr[ASN1_ENUMERATED],
  ): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2s_ASN1_IA5STRING(
      method: Ptr[X509V3_EXT_METHOD],
      ia5: Ptr[ASN1_IA5STRING],
  ): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2s_ASN1_INTEGER(meth: Ptr[X509V3_EXT_METHOD], aint: Ptr[ASN1_INTEGER]): CString =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2s_ASN1_OCTET_STRING(
      method: Ptr[X509V3_EXT_METHOD],
      ia5: Ptr[ASN1_OCTET_STRING],
  ): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2s_ASN1_UTF8STRING(
      method: Ptr[X509V3_EXT_METHOD],
      utf8: Ptr[ASN1_UTF8STRING],
  ): CString = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2v_ASN1_BIT_STRING(
      method: Ptr[X509V3_EXT_METHOD],
      bits: Ptr[ASN1_BIT_STRING],
      extlist: Ptr[stack_st_CONF_VALUE],
  ): Ptr[stack_st_CONF_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2v_GENERAL_NAME(
      method: Ptr[X509V3_EXT_METHOD],
      gen: Ptr[GENERAL_NAME],
      ret: Ptr[stack_st_CONF_VALUE],
  ): Ptr[stack_st_CONF_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def i2v_GENERAL_NAMES(
      method: Ptr[X509V3_EXT_METHOD],
      gen: Ptr[GENERAL_NAMES],
      extlist: Ptr[stack_st_CONF_VALUE],
  ): Ptr[stack_st_CONF_VALUE] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def s2i_ASN1_IA5STRING(
      method: Ptr[X509V3_EXT_METHOD],
      ctx: Ptr[X509V3_CTX],
      str: CString,
  ): Ptr[ASN1_IA5STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def s2i_ASN1_INTEGER(meth: Ptr[X509V3_EXT_METHOD], value: CString): Ptr[ASN1_INTEGER] =
    extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def s2i_ASN1_OCTET_STRING(
      method: Ptr[X509V3_EXT_METHOD],
      ctx: Ptr[X509V3_CTX],
      str: CString,
  ): Ptr[ASN1_OCTET_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def s2i_ASN1_UTF8STRING(
      method: Ptr[X509V3_EXT_METHOD],
      ctx: Ptr[X509V3_CTX],
      str: CString,
  ): Ptr[ASN1_UTF8STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def v2i_ASN1_BIT_STRING(
      method: Ptr[X509V3_EXT_METHOD],
      ctx: Ptr[X509V3_CTX],
      nval: Ptr[stack_st_CONF_VALUE],
  ): Ptr[ASN1_BIT_STRING] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def v2i_GENERAL_NAME(
      method: Ptr[X509V3_EXT_METHOD],
      ctx: Ptr[X509V3_CTX],
      cnf: Ptr[CONF_VALUE],
  ): Ptr[GENERAL_NAME] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def v2i_GENERAL_NAMES(
      method: Ptr[X509V3_EXT_METHOD],
      ctx: Ptr[X509V3_CTX],
      nval: Ptr[stack_st_CONF_VALUE],
  ): Ptr[GENERAL_NAMES] = extern

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  @extern def v2i_GENERAL_NAME_ex(
      out: Ptr[GENERAL_NAME],
      method: Ptr[X509V3_EXT_METHOD],
      ctx: Ptr[X509V3_CTX],
      cnf: Ptr[CONF_VALUE],
      is_nc: CInt,
  ): Ptr[GENERAL_NAME] = extern
