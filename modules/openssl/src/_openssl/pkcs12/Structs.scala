package snhttp.experimental.openssl
package _openssl.pkcs12

import scala.annotation.targetName
import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.asn1.Types.{
  ASN1_OBJECT,
  ASN1_TYPE,
  ASN1_INTEGER,
  ASN1_OCTET_STRING,
}
import _root_.snhttp.experimental.openssl._openssl.safestack.{
  stack_st_X509,
  stack_st_X509_ALGOR,
  stack_st_X509_CRL,
  stack_st_PKCS7_RECIP_INFO,
  stack_st_PKCS7_SIGNER_INFO,
}
import _root_.snhttp.experimental.openssl._openssl.types.{EVP_CIPHER, OSSL_LIB_CTX}
import _root_.snhttp.experimental.openssl._openssl.x509.Types.X509_ALGOR

object Structs:

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  opaque type PKCS12 = CStruct0

  object PKCS12:
    given _tag: Tag[PKCS12] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  opaque type PKCS12_BAGS = CStruct0

  object PKCS12_BAGS:
    given _tag: Tag[PKCS12_BAGS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  opaque type PKCS12_MAC_DATA = CStruct0

  object PKCS12_MAC_DATA:
    given _tag: Tag[PKCS12_MAC_DATA] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  opaque type PKCS12_MAC_DATA_st = CStruct0

  object PKCS12_MAC_DATA_st:
    given _tag: Tag[PKCS12_MAC_DATA_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  opaque type PKCS12_SAFEBAG = CStruct0

  object PKCS12_SAFEBAG:
    given _tag: Tag[PKCS12_SAFEBAG] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  opaque type PKCS12_SAFEBAG_st = CStruct0

  object PKCS12_SAFEBAG_st:
    given _tag: Tag[PKCS12_SAFEBAG_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  opaque type PKCS12_st = CStruct0

  object PKCS12_st:
    given _tag: Tag[PKCS12_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7 =
    CStruct7[Ptr[CUnsignedChar], CLongInt, CInt, CInt, Ptr[ASN1_OBJECT], PKCS7_D, PKCS7_CTX]

  object PKCS7:
    given _tag: Tag[PKCS7] = Tag.materializeCStruct7Tag[Ptr[
      CUnsignedChar,
    ], CLongInt, CInt, CInt, Ptr[ASN1_OBJECT], PKCS7_D, PKCS7_CTX]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: PKCS7)
        inline def asn1: Ptr[CUnsignedChar] = struct._1
        inline def asn1_=(value: Ptr[CUnsignedChar]): Unit = !struct.at1 = value
        inline def length: CLongInt = struct._2
        inline def length_=(value: CLongInt): Unit = !struct.at2 = value
        inline def state: CInt = struct._3
        inline def state_=(value: CInt): Unit = !struct.at3 = value
        inline def detached: CInt = struct._4
        inline def detached_=(value: CInt): Unit = !struct.at4 = value
        inline def `type`: Ptr[ASN1_OBJECT] = struct._5
        inline def type_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at5 = value
        inline def d: PKCS7_D = struct._6
        inline def d_=(value: PKCS7_D): Unit = !struct.at6 = value
        inline def ctx: PKCS7_CTX = struct._7
        inline def ctx_=(value: PKCS7_CTX): Unit = !struct.at7 = value
      end extension

    // Allocates PKCS7 on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKCS7] = scala.scalanative.unsafe.alloc[PKCS7](1)
    def apply(
        asn1: Ptr[CUnsignedChar],
        length: CLongInt,
        state: CInt,
        detached: CInt,
        `type`: Ptr[ASN1_OBJECT],
        d: PKCS7_D,
        ctx: PKCS7_CTX,
    )(using Zone): Ptr[PKCS7] =
      val ____ptr = apply()
      (!____ptr).asn1 = asn1
      (!____ptr).length = length
      (!____ptr).state = state
      (!____ptr).detached = detached
      (!____ptr).`type` = `type`
      (!____ptr).d = d
      (!____ptr).ctx = ctx
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7_D = CArray[Byte, Nat._8]
  object PKCS7_D:
    given _tag: Tag[PKCS7_D] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      ___ptr

    @targetName("apply_ptr")
    def apply(ptr: CString)(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[CString]].update(0, ptr)
      ___ptr

    @targetName("apply_data")
    def apply(data: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]].update(0, data)
      ___ptr

    @targetName("apply_sign")
    def apply(sign: Ptr[PKCS7_SIGNED])(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGNED]]].update(0, sign)
      ___ptr

    @targetName("apply_enveloped")
    def apply(enveloped: Ptr[PKCS7_ENVELOPE])(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENVELOPE]]].update(0, enveloped)
      ___ptr

    @targetName("apply_signed_and_enveloped")
    def apply(signed_and_enveloped: Ptr[PKCS7_SIGN_ENVELOPE])(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGN_ENVELOPE]]].update(0, signed_and_enveloped)
      ___ptr

    @targetName("apply_digest")
    def apply(digest: Ptr[PKCS7_DIGEST])(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_DIGEST]]].update(0, digest)
      ___ptr

    @targetName("apply_encrypted")
    def apply(encrypted: Ptr[PKCS7_ENCRYPT])(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENCRYPT]]].update(0, encrypted)
      ___ptr

    @targetName("apply_other")
    def apply(other: Ptr[ASN1_TYPE])(using Zone): Ptr[PKCS7_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[PKCS7_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]].update(0, other)
      ___ptr

    extension (struct: PKCS7_D)
      inline def ptr: CString = !struct.at(0).asInstanceOf[Ptr[CString]]
      inline def ptr_=(value: CString): Unit = !struct.at(0).asInstanceOf[Ptr[CString]] = value
      inline def data: Ptr[ASN1_OCTET_STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]]
      inline def data_=(value: Ptr[ASN1_OCTET_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]] = value
      inline def sign: Ptr[PKCS7_SIGNED] = !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGNED]]]
      inline def sign_=(value: Ptr[PKCS7_SIGNED]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGNED]]] = value
      inline def enveloped: Ptr[PKCS7_ENVELOPE] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENVELOPE]]]
      inline def enveloped_=(value: Ptr[PKCS7_ENVELOPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENVELOPE]]] = value
      inline def signed_and_enveloped: Ptr[PKCS7_SIGN_ENVELOPE] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGN_ENVELOPE]]]
      inline def signed_and_enveloped_=(value: Ptr[PKCS7_SIGN_ENVELOPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGN_ENVELOPE]]] = value
      inline def digest: Ptr[PKCS7_DIGEST] = !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_DIGEST]]]
      inline def digest_=(value: Ptr[PKCS7_DIGEST]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_DIGEST]]] = value
      inline def encrypted: Ptr[PKCS7_ENCRYPT] = !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENCRYPT]]]
      inline def encrypted_=(value: Ptr[PKCS7_ENCRYPT]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENCRYPT]]] = value
      inline def other: Ptr[ASN1_TYPE] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]]
      inline def other_=(value: Ptr[ASN1_TYPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7_CTX = CStruct2[Ptr[OSSL_LIB_CTX], CString]

  object PKCS7_CTX:
    given _tag: Tag[PKCS7_CTX] = Tag.materializeCStruct2Tag[Ptr[OSSL_LIB_CTX], CString]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: PKCS7_CTX)
        inline def libctx: Ptr[OSSL_LIB_CTX] = struct._1
        inline def libctx_=(value: Ptr[OSSL_LIB_CTX]): Unit = !struct.at1 = value
        inline def propq: CString = struct._2
        inline def propq_=(value: CString): Unit = !struct.at2 = value
      end extension

    // Allocates PKCS7_CTX on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKCS7_CTX] = scala.scalanative.unsafe.alloc[PKCS7_CTX](1)
    def apply(libctx: Ptr[OSSL_LIB_CTX], propq: CString)(using Zone): Ptr[PKCS7_CTX] =
      val ____ptr = apply()
      (!____ptr).libctx = libctx
      (!____ptr).propq = propq
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7_DIGEST =
    CStruct4[Ptr[ASN1_INTEGER], Ptr[X509_ALGOR], Ptr[pkcs7_st], Ptr[ASN1_OCTET_STRING]]

  object PKCS7_DIGEST:
    given _tag: Tag[PKCS7_DIGEST] = Tag.materializeCStruct4Tag[Ptr[ASN1_INTEGER], Ptr[
      X509_ALGOR,
    ], Ptr[pkcs7_st], Ptr[ASN1_OCTET_STRING]]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: PKCS7_DIGEST)
        inline def version: Ptr[ASN1_INTEGER] = struct._1
        inline def version_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def md: Ptr[X509_ALGOR] = struct._2
        inline def md_=(value: Ptr[X509_ALGOR]): Unit = !struct.at2 = value
        inline def contents: Ptr[pkcs7_st] = struct._3
        inline def contents_=(value: Ptr[pkcs7_st]): Unit = !struct.at3 = value
        inline def digest: Ptr[ASN1_OCTET_STRING] = struct._4
        inline def digest_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at4 = value
      end extension

    // Allocates PKCS7_DIGEST on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKCS7_DIGEST] = scala.scalanative.unsafe.alloc[PKCS7_DIGEST](1)
    def apply(
        version: Ptr[ASN1_INTEGER],
        md: Ptr[X509_ALGOR],
        contents: Ptr[pkcs7_st],
        digest: Ptr[ASN1_OCTET_STRING],
    )(using Zone): Ptr[PKCS7_DIGEST] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).md = md
      (!____ptr).contents = contents
      (!____ptr).digest = digest
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7_ENCRYPT = CStruct2[Ptr[ASN1_INTEGER], Ptr[PKCS7_ENC_CONTENT]]

  object PKCS7_ENCRYPT:
    given _tag: Tag[PKCS7_ENCRYPT] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[PKCS7_ENC_CONTENT]]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: PKCS7_ENCRYPT)
        inline def version: Ptr[ASN1_INTEGER] = struct._1
        inline def version_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def enc_data: Ptr[PKCS7_ENC_CONTENT] = struct._2
        inline def enc_data_=(value: Ptr[PKCS7_ENC_CONTENT]): Unit = !struct.at2 = value
      end extension

    // Allocates PKCS7_ENCRYPT on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKCS7_ENCRYPT] = scala.scalanative.unsafe.alloc[PKCS7_ENCRYPT](1)
    def apply(version: Ptr[ASN1_INTEGER], enc_data: Ptr[PKCS7_ENC_CONTENT])(using
        Zone,
    ): Ptr[PKCS7_ENCRYPT] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).enc_data = enc_data
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7_ENC_CONTENT = CStruct5[Ptr[ASN1_OBJECT], Ptr[X509_ALGOR], Ptr[
    ASN1_OCTET_STRING,
  ], Ptr[EVP_CIPHER], Ptr[PKCS7_CTX]]

  object PKCS7_ENC_CONTENT:
    given _tag: Tag[PKCS7_ENC_CONTENT] = Tag.materializeCStruct5Tag[Ptr[ASN1_OBJECT], Ptr[
      X509_ALGOR,
    ], Ptr[ASN1_OCTET_STRING], Ptr[EVP_CIPHER], Ptr[PKCS7_CTX]]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: PKCS7_ENC_CONTENT)
        inline def content_type: Ptr[ASN1_OBJECT] = struct._1
        inline def content_type_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def algorithm: Ptr[X509_ALGOR] = struct._2
        inline def algorithm_=(value: Ptr[X509_ALGOR]): Unit = !struct.at2 = value
        inline def enc_data: Ptr[ASN1_OCTET_STRING] = struct._3
        inline def enc_data_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at3 = value
        inline def cipher: Ptr[EVP_CIPHER] = struct._4
        inline def cipher_=(value: Ptr[EVP_CIPHER]): Unit = !struct.at4 = value
        inline def ctx: Ptr[PKCS7_CTX] = struct._5
        inline def ctx_=(value: Ptr[PKCS7_CTX]): Unit = !struct.at5 = value
      end extension

    // Allocates PKCS7_ENC_CONTENT on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKCS7_ENC_CONTENT] =
      scala.scalanative.unsafe.alloc[PKCS7_ENC_CONTENT](1)
    def apply(
        content_type: Ptr[ASN1_OBJECT],
        algorithm: Ptr[X509_ALGOR],
        enc_data: Ptr[ASN1_OCTET_STRING],
        cipher: Ptr[EVP_CIPHER],
        ctx: Ptr[PKCS7_CTX],
    )(using Zone): Ptr[PKCS7_ENC_CONTENT] =
      val ____ptr = apply()
      (!____ptr).content_type = content_type
      (!____ptr).algorithm = algorithm
      (!____ptr).enc_data = enc_data
      (!____ptr).cipher = cipher
      (!____ptr).ctx = ctx
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7_ENVELOPE =
    CStruct3[Ptr[ASN1_INTEGER], Ptr[stack_st_PKCS7_RECIP_INFO], Ptr[PKCS7_ENC_CONTENT]]

  object PKCS7_ENVELOPE:
    given _tag: Tag[PKCS7_ENVELOPE] = Tag.materializeCStruct3Tag[Ptr[ASN1_INTEGER], Ptr[
      stack_st_PKCS7_RECIP_INFO,
    ], Ptr[PKCS7_ENC_CONTENT]]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: PKCS7_ENVELOPE)
        inline def version: Ptr[ASN1_INTEGER] = struct._1
        inline def version_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def recipientinfo: Ptr[stack_st_PKCS7_RECIP_INFO] = struct._2
        inline def recipientinfo_=(value: Ptr[stack_st_PKCS7_RECIP_INFO]): Unit = !struct.at2 =
          value
        inline def enc_data: Ptr[PKCS7_ENC_CONTENT] = struct._3
        inline def enc_data_=(value: Ptr[PKCS7_ENC_CONTENT]): Unit = !struct.at3 = value
      end extension

    // Allocates PKCS7_ENVELOPE on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKCS7_ENVELOPE] = scala.scalanative.unsafe.alloc[PKCS7_ENVELOPE](1)
    def apply(
        version: Ptr[ASN1_INTEGER],
        recipientinfo: Ptr[stack_st_PKCS7_RECIP_INFO],
        enc_data: Ptr[PKCS7_ENC_CONTENT],
    )(using Zone): Ptr[PKCS7_ENVELOPE] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).recipientinfo = recipientinfo
      (!____ptr).enc_data = enc_data
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7_SIGNED = CStruct6[Ptr[ASN1_INTEGER], Ptr[stack_st_X509_ALGOR], Ptr[
    stack_st_X509,
  ], Ptr[stack_st_X509_CRL], Ptr[stack_st_PKCS7_SIGNER_INFO], Ptr[pkcs7_st]]

  object PKCS7_SIGNED:
    given _tag: Tag[PKCS7_SIGNED] = Tag.materializeCStruct6Tag[Ptr[ASN1_INTEGER], Ptr[
      stack_st_X509_ALGOR,
    ], Ptr[stack_st_X509], Ptr[stack_st_X509_CRL], Ptr[stack_st_PKCS7_SIGNER_INFO], Ptr[pkcs7_st]]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: PKCS7_SIGNED)
        inline def version: Ptr[ASN1_INTEGER] = struct._1
        inline def version_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def md_algs: Ptr[stack_st_X509_ALGOR] = struct._2
        inline def md_algs_=(value: Ptr[stack_st_X509_ALGOR]): Unit = !struct.at2 = value
        inline def cert: Ptr[stack_st_X509] = struct._3
        inline def cert_=(value: Ptr[stack_st_X509]): Unit = !struct.at3 = value
        inline def crl: Ptr[stack_st_X509_CRL] = struct._4
        inline def crl_=(value: Ptr[stack_st_X509_CRL]): Unit = !struct.at4 = value
        inline def signer_info: Ptr[stack_st_PKCS7_SIGNER_INFO] = struct._5
        inline def signer_info_=(value: Ptr[stack_st_PKCS7_SIGNER_INFO]): Unit = !struct.at5 = value
        inline def contents: Ptr[pkcs7_st] = struct._6
        inline def contents_=(value: Ptr[pkcs7_st]): Unit = !struct.at6 = value
      end extension

    // Allocates PKCS7_SIGNED on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKCS7_SIGNED] = scala.scalanative.unsafe.alloc[PKCS7_SIGNED](1)
    def apply(
        version: Ptr[ASN1_INTEGER],
        md_algs: Ptr[stack_st_X509_ALGOR],
        cert: Ptr[stack_st_X509],
        crl: Ptr[stack_st_X509_CRL],
        signer_info: Ptr[stack_st_PKCS7_SIGNER_INFO],
        contents: Ptr[pkcs7_st],
    )(using Zone): Ptr[PKCS7_SIGNED] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).md_algs = md_algs
      (!____ptr).cert = cert
      (!____ptr).crl = crl
      (!____ptr).signer_info = signer_info
      (!____ptr).contents = contents
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type PKCS7_SIGN_ENVELOPE = CStruct7[
    Ptr[ASN1_INTEGER],
    Ptr[stack_st_X509_ALGOR],
    Ptr[stack_st_X509],
    Ptr[stack_st_X509_CRL],
    Ptr[stack_st_PKCS7_SIGNER_INFO],
    Ptr[PKCS7_ENC_CONTENT],
    Ptr[stack_st_PKCS7_RECIP_INFO],
  ]

  object PKCS7_SIGN_ENVELOPE:
    given _tag: Tag[PKCS7_SIGN_ENVELOPE] = Tag.materializeCStruct7Tag[
      Ptr[ASN1_INTEGER],
      Ptr[stack_st_X509_ALGOR],
      Ptr[stack_st_X509],
      Ptr[stack_st_X509_CRL],
      Ptr[stack_st_PKCS7_SIGNER_INFO],
      Ptr[PKCS7_ENC_CONTENT],
      Ptr[stack_st_PKCS7_RECIP_INFO],
    ]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: PKCS7_SIGN_ENVELOPE)
        inline def version: Ptr[ASN1_INTEGER] = struct._1
        inline def version_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def md_algs: Ptr[stack_st_X509_ALGOR] = struct._2
        inline def md_algs_=(value: Ptr[stack_st_X509_ALGOR]): Unit = !struct.at2 = value
        inline def cert: Ptr[stack_st_X509] = struct._3
        inline def cert_=(value: Ptr[stack_st_X509]): Unit = !struct.at3 = value
        inline def crl: Ptr[stack_st_X509_CRL] = struct._4
        inline def crl_=(value: Ptr[stack_st_X509_CRL]): Unit = !struct.at4 = value
        inline def signer_info: Ptr[stack_st_PKCS7_SIGNER_INFO] = struct._5
        inline def signer_info_=(value: Ptr[stack_st_PKCS7_SIGNER_INFO]): Unit = !struct.at5 = value
        inline def enc_data: Ptr[PKCS7_ENC_CONTENT] = struct._6
        inline def enc_data_=(value: Ptr[PKCS7_ENC_CONTENT]): Unit = !struct.at6 = value
        inline def recipientinfo: Ptr[stack_st_PKCS7_RECIP_INFO] = struct._7
        inline def recipientinfo_=(value: Ptr[stack_st_PKCS7_RECIP_INFO]): Unit = !struct.at7 =
          value
      end extension

    // Allocates PKCS7_SIGN_ENVELOPE on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKCS7_SIGN_ENVELOPE] =
      scala.scalanative.unsafe.alloc[PKCS7_SIGN_ENVELOPE](1)
    def apply(
        version: Ptr[ASN1_INTEGER],
        md_algs: Ptr[stack_st_X509_ALGOR],
        cert: Ptr[stack_st_X509],
        crl: Ptr[stack_st_X509_CRL],
        signer_info: Ptr[stack_st_PKCS7_SIGNER_INFO],
        enc_data: Ptr[PKCS7_ENC_CONTENT],
        recipientinfo: Ptr[stack_st_PKCS7_RECIP_INFO],
    )(using Zone): Ptr[PKCS7_SIGN_ENVELOPE] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).md_algs = md_algs
      (!____ptr).cert = cert
      (!____ptr).crl = crl
      (!____ptr).signer_info = signer_info
      (!____ptr).enc_data = enc_data
      (!____ptr).recipientinfo = recipientinfo
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type PKCS8_PRIV_KEY_INFO = CStruct0

  object PKCS8_PRIV_KEY_INFO:
    given _tag: Tag[PKCS8_PRIV_KEY_INFO] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs12.h
   */
  opaque type pkcs12_bag_st = CStruct0

  object pkcs12_bag_st:
    given _tag: Tag[pkcs12_bag_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type pkcs7_st =
    CStruct7[Ptr[CUnsignedChar], CLongInt, CInt, CInt, Ptr[ASN1_OBJECT], pkcs7_st_D, PKCS7_CTX]

  object pkcs7_st:
    given _tag: Tag[pkcs7_st] = Tag.materializeCStruct7Tag[Ptr[
      CUnsignedChar,
    ], CLongInt, CInt, CInt, Ptr[ASN1_OBJECT], pkcs7_st_D, PKCS7_CTX]

    export fields.*
    private[pkcs12] object fields:
      extension (struct: pkcs7_st)
        inline def asn1: Ptr[CUnsignedChar] = struct._1
        inline def asn1_=(value: Ptr[CUnsignedChar]): Unit = !struct.at1 = value
        inline def length: CLongInt = struct._2
        inline def length_=(value: CLongInt): Unit = !struct.at2 = value
        inline def state: CInt = struct._3
        inline def state_=(value: CInt): Unit = !struct.at3 = value
        inline def detached: CInt = struct._4
        inline def detached_=(value: CInt): Unit = !struct.at4 = value
        inline def `type`: Ptr[ASN1_OBJECT] = struct._5
        inline def type_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at5 = value
        inline def d: pkcs7_st_D = struct._6
        inline def d_=(value: pkcs7_st_D): Unit = !struct.at6 = value
        inline def ctx: PKCS7_CTX = struct._7
        inline def ctx_=(value: PKCS7_CTX): Unit = !struct.at7 = value
      end extension

    // Allocates pkcs7_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[pkcs7_st] = scala.scalanative.unsafe.alloc[pkcs7_st](1)
    def apply(
        asn1: Ptr[CUnsignedChar],
        length: CLongInt,
        state: CInt,
        detached: CInt,
        `type`: Ptr[ASN1_OBJECT],
        d: pkcs7_st_D,
        ctx: PKCS7_CTX,
    )(using Zone): Ptr[pkcs7_st] =
      val ____ptr = apply()
      (!____ptr).asn1 = asn1
      (!____ptr).length = length
      (!____ptr).state = state
      (!____ptr).detached = detached
      (!____ptr).`type` = `type`
      (!____ptr).d = d
      (!____ptr).ctx = ctx
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/pkcs7.h
   */
  opaque type pkcs7_st_D = CArray[Byte, Nat._8]
  object pkcs7_st_D:
    given _tag: Tag[pkcs7_st_D] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      ___ptr

    @targetName("apply_ptr")
    def apply(ptr: CString)(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[CString]].update(0, ptr)
      ___ptr

    @targetName("apply_data")
    def apply(data: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]].update(0, data)
      ___ptr

    @targetName("apply_sign")
    def apply(sign: Ptr[PKCS7_SIGNED])(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGNED]]].update(0, sign)
      ___ptr

    @targetName("apply_enveloped")
    def apply(enveloped: Ptr[PKCS7_ENVELOPE])(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENVELOPE]]].update(0, enveloped)
      ___ptr

    @targetName("apply_signed_and_enveloped")
    def apply(signed_and_enveloped: Ptr[PKCS7_SIGN_ENVELOPE])(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGN_ENVELOPE]]].update(0, signed_and_enveloped)
      ___ptr

    @targetName("apply_digest")
    def apply(digest: Ptr[PKCS7_DIGEST])(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_DIGEST]]].update(0, digest)
      ___ptr

    @targetName("apply_encrypted")
    def apply(encrypted: Ptr[PKCS7_ENCRYPT])(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENCRYPT]]].update(0, encrypted)
      ___ptr

    @targetName("apply_other")
    def apply(other: Ptr[ASN1_TYPE])(using Zone): Ptr[pkcs7_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[pkcs7_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]].update(0, other)
      ___ptr

    extension (struct: pkcs7_st_D)
      inline def ptr: CString = !struct.at(0).asInstanceOf[Ptr[CString]]
      inline def ptr_=(value: CString): Unit = !struct.at(0).asInstanceOf[Ptr[CString]] = value
      inline def data: Ptr[ASN1_OCTET_STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]]
      inline def data_=(value: Ptr[ASN1_OCTET_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]] = value
      inline def sign: Ptr[PKCS7_SIGNED] = !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGNED]]]
      inline def sign_=(value: Ptr[PKCS7_SIGNED]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGNED]]] = value
      inline def enveloped: Ptr[PKCS7_ENVELOPE] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENVELOPE]]]
      inline def enveloped_=(value: Ptr[PKCS7_ENVELOPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENVELOPE]]] = value
      inline def signed_and_enveloped: Ptr[PKCS7_SIGN_ENVELOPE] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGN_ENVELOPE]]]
      inline def signed_and_enveloped_=(value: Ptr[PKCS7_SIGN_ENVELOPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_SIGN_ENVELOPE]]] = value
      inline def digest: Ptr[PKCS7_DIGEST] = !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_DIGEST]]]
      inline def digest_=(value: Ptr[PKCS7_DIGEST]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_DIGEST]]] = value
      inline def encrypted: Ptr[PKCS7_ENCRYPT] = !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENCRYPT]]]
      inline def encrypted_=(value: Ptr[PKCS7_ENCRYPT]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[PKCS7_ENCRYPT]]] = value
      inline def other: Ptr[ASN1_TYPE] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]]
      inline def other_=(value: Ptr[ASN1_TYPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]] = value
