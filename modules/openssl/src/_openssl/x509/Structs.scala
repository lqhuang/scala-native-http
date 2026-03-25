package snhttp.experimental.openssl
package _openssl.x509

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.asn1.Types.{
  ASN1_OBJECT,
  ASN1_TYPE,
  ASN1_INTEGER,
  ASN1_BIT_STRING,
  ASN1_OCTET_STRING,
  ASN1_IA5STRING,
  ASN1_TIME,
}
import _root_.snhttp.experimental.openssl._openssl.safestack.stack_st_X509
import _root_.snhttp.experimental.openssl._openssl.types.{EVP_PKEY, EVP_CIPHER}

private[openssl] object Structs:

  /**
   * [bindgen] header: /usr/include/openssl/evp.h
   */
  opaque type EVP_CIPHER_INFO =
    CStruct2[Ptr[EVP_CIPHER], CArray[CUnsignedChar, Nat.Digit2[Nat._1, Nat._6]]]

  object EVP_CIPHER_INFO:
    given _tag: Tag[EVP_CIPHER_INFO] =
      Tag.materializeCStruct2Tag[Ptr[EVP_CIPHER], CArray[CUnsignedChar, Nat.Digit2[Nat._1, Nat._6]]]

    export fields.*
    private[x509] object fields:
      extension (struct: EVP_CIPHER_INFO)
        inline def cipher: Ptr[EVP_CIPHER] = struct._1
        inline def cipher_=(value: Ptr[EVP_CIPHER]): Unit = !struct.at1 = value
        inline def iv: CArray[CUnsignedChar, Nat.Digit2[Nat._1, Nat._6]] = struct._2
        inline def iv_=(value: CArray[CUnsignedChar, Nat.Digit2[Nat._1, Nat._6]]): Unit =
          !struct.at2 = value
      end extension

    // Allocates EVP_CIPHER_INFO on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[EVP_CIPHER_INFO] =
      alloc[EVP_CIPHER_INFO](1)
    def apply(cipher: Ptr[EVP_CIPHER], iv: CArray[CUnsignedChar, Nat.Digit2[Nat._1, Nat._6]])(using
        Zone,
    ): Ptr[EVP_CIPHER_INFO] =
      val ____ptr = apply()
      (!____ptr).cipher = cipher
      (!____ptr).iv = iv
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type NETSCAPE_CERT_SEQUENCE = CStruct2[Ptr[ASN1_OBJECT], Ptr[stack_st_X509]]

  object NETSCAPE_CERT_SEQUENCE:
    given _tag: Tag[NETSCAPE_CERT_SEQUENCE] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[stack_st_X509]]

    export fields.*
    private[x509] object fields:
      extension (struct: NETSCAPE_CERT_SEQUENCE)
        inline def `type`: Ptr[ASN1_OBJECT] = struct._1
        inline def type_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def certs: Ptr[stack_st_X509] = struct._2
        inline def certs_=(value: Ptr[stack_st_X509]): Unit = !struct.at2 = value
      end extension

    // Allocates NETSCAPE_CERT_SEQUENCE on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[NETSCAPE_CERT_SEQUENCE] =
      alloc[NETSCAPE_CERT_SEQUENCE](1)
    def apply(`type`: Ptr[ASN1_OBJECT], certs: Ptr[stack_st_X509])(using
        Zone,
    ): Ptr[NETSCAPE_CERT_SEQUENCE] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).certs = certs
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type NETSCAPE_SPKAC = CStruct2[Ptr[X509_PUBKEY], Ptr[ASN1_IA5STRING]]

  object NETSCAPE_SPKAC:
    given _tag: Tag[NETSCAPE_SPKAC] =
      Tag.materializeCStruct2Tag[Ptr[X509_PUBKEY], Ptr[ASN1_IA5STRING]]

    export fields.*
    private[x509] object fields:
      extension (struct: NETSCAPE_SPKAC)
        inline def pubkey: Ptr[X509_PUBKEY] = struct._1
        inline def pubkey_=(value: Ptr[X509_PUBKEY]): Unit = !struct.at1 = value
        inline def challenge: Ptr[ASN1_IA5STRING] = struct._2
        inline def challenge_=(value: Ptr[ASN1_IA5STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates NETSCAPE_SPKAC on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[NETSCAPE_SPKAC] = alloc[NETSCAPE_SPKAC](1)
    def apply(pubkey: Ptr[X509_PUBKEY], challenge: Ptr[ASN1_IA5STRING])(using
        Zone,
    ): Ptr[NETSCAPE_SPKAC] =
      val ____ptr = apply()
      (!____ptr).pubkey = pubkey
      (!____ptr).challenge = challenge
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type NETSCAPE_SPKI = CStruct3[Ptr[NETSCAPE_SPKAC], X509_ALGOR, Ptr[ASN1_BIT_STRING]]

  object NETSCAPE_SPKI:
    given _tag: Tag[NETSCAPE_SPKI] =
      Tag.materializeCStruct3Tag[Ptr[NETSCAPE_SPKAC], X509_ALGOR, Ptr[ASN1_BIT_STRING]]

    export fields.*
    private[x509] object fields:
      extension (struct: NETSCAPE_SPKI)
        inline def spkac: Ptr[NETSCAPE_SPKAC] = struct._1
        inline def spkac_=(value: Ptr[NETSCAPE_SPKAC]): Unit = !struct.at1 = value
        inline def sig_algor: X509_ALGOR = struct._2
        inline def sig_algor_=(value: X509_ALGOR): Unit = !struct.at2 = value
        inline def signature: Ptr[ASN1_BIT_STRING] = struct._3
        inline def signature_=(value: Ptr[ASN1_BIT_STRING]): Unit = !struct.at3 = value
      end extension

    // Allocates NETSCAPE_SPKI on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[NETSCAPE_SPKI] = alloc[NETSCAPE_SPKI](1)
    def apply(spkac: Ptr[NETSCAPE_SPKAC], sig_algor: X509_ALGOR, signature: Ptr[ASN1_BIT_STRING])(
        using Zone,
    ): Ptr[NETSCAPE_SPKI] =
      val ____ptr = apply()
      (!____ptr).spkac = spkac
      (!____ptr).sig_algor = sig_algor
      (!____ptr).signature = signature
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type Netscape_certificate_sequence = CStruct2[Ptr[ASN1_OBJECT], Ptr[stack_st_X509]]

  object Netscape_certificate_sequence:
    given _tag: Tag[Netscape_certificate_sequence] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[stack_st_X509]]

    export fields.*
    private[x509] object fields:
      extension (struct: Netscape_certificate_sequence)
        inline def `type`: Ptr[ASN1_OBJECT] = struct._1
        inline def type_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def certs: Ptr[stack_st_X509] = struct._2
        inline def certs_=(value: Ptr[stack_st_X509]): Unit = !struct.at2 = value
      end extension

    // Allocates Netscape_certificate_sequence on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[Netscape_certificate_sequence] =
      alloc[Netscape_certificate_sequence](1)
    def apply(`type`: Ptr[ASN1_OBJECT], certs: Ptr[stack_st_X509])(using
        Zone,
    ): Ptr[Netscape_certificate_sequence] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).certs = certs
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type Netscape_spkac_st = CStruct2[Ptr[X509_PUBKEY], Ptr[ASN1_IA5STRING]]

  object Netscape_spkac_st:
    given _tag: Tag[Netscape_spkac_st] =
      Tag.materializeCStruct2Tag[Ptr[X509_PUBKEY], Ptr[ASN1_IA5STRING]]

    export fields.*
    private[x509] object fields:
      extension (struct: Netscape_spkac_st)
        inline def pubkey: Ptr[X509_PUBKEY] = struct._1
        inline def pubkey_=(value: Ptr[X509_PUBKEY]): Unit = !struct.at1 = value
        inline def challenge: Ptr[ASN1_IA5STRING] = struct._2
        inline def challenge_=(value: Ptr[ASN1_IA5STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates Netscape_spkac_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[Netscape_spkac_st] =
      alloc[Netscape_spkac_st](1)
    def apply(pubkey: Ptr[X509_PUBKEY], challenge: Ptr[ASN1_IA5STRING])(using
        Zone,
    ): Ptr[Netscape_spkac_st] =
      val ____ptr = apply()
      (!____ptr).pubkey = pubkey
      (!____ptr).challenge = challenge
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type Netscape_spki_st = CStruct3[Ptr[NETSCAPE_SPKAC], X509_ALGOR, Ptr[ASN1_BIT_STRING]]

  object Netscape_spki_st:
    given _tag: Tag[Netscape_spki_st] =
      Tag.materializeCStruct3Tag[Ptr[NETSCAPE_SPKAC], X509_ALGOR, Ptr[ASN1_BIT_STRING]]

    export fields.*
    private[x509] object fields:
      extension (struct: Netscape_spki_st)
        inline def spkac: Ptr[NETSCAPE_SPKAC] = struct._1
        inline def spkac_=(value: Ptr[NETSCAPE_SPKAC]): Unit = !struct.at1 = value
        inline def sig_algor: X509_ALGOR = struct._2
        inline def sig_algor_=(value: X509_ALGOR): Unit = !struct.at2 = value
        inline def signature: Ptr[ASN1_BIT_STRING] = struct._3
        inline def signature_=(value: Ptr[ASN1_BIT_STRING]): Unit = !struct.at3 = value
      end extension

    // Allocates Netscape_spki_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[Netscape_spki_st] =
      alloc[Netscape_spki_st](1)
    def apply(spkac: Ptr[NETSCAPE_SPKAC], sig_algor: X509_ALGOR, signature: Ptr[ASN1_BIT_STRING])(
        using Zone,
    ): Ptr[Netscape_spki_st] =
      val ____ptr = apply()
      (!____ptr).spkac = spkac
      (!____ptr).sig_algor = sig_algor
      (!____ptr).signature = signature
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type PBE2PARAM = CStruct2[Ptr[X509_ALGOR], Ptr[X509_ALGOR]]

  object PBE2PARAM:
    given _tag: Tag[PBE2PARAM] = Tag.materializeCStruct2Tag[Ptr[X509_ALGOR], Ptr[X509_ALGOR]]

    export fields.*
    private[x509] object fields:
      extension (struct: PBE2PARAM)
        inline def keyfunc: Ptr[X509_ALGOR] = struct._1
        inline def keyfunc_=(value: Ptr[X509_ALGOR]): Unit = !struct.at1 = value
        inline def encryption: Ptr[X509_ALGOR] = struct._2
        inline def encryption_=(value: Ptr[X509_ALGOR]): Unit = !struct.at2 = value
      end extension

    // Allocates PBE2PARAM on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PBE2PARAM] = alloc[PBE2PARAM](1)
    def apply(keyfunc: Ptr[X509_ALGOR], encryption: Ptr[X509_ALGOR])(using Zone): Ptr[PBE2PARAM] =
      val ____ptr = apply()
      (!____ptr).keyfunc = keyfunc
      (!____ptr).encryption = encryption
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type PBE2PARAM_st = CStruct2[Ptr[X509_ALGOR], Ptr[X509_ALGOR]]

  object PBE2PARAM_st:
    given _tag: Tag[PBE2PARAM_st] = Tag.materializeCStruct2Tag[Ptr[X509_ALGOR], Ptr[X509_ALGOR]]

    export fields.*
    private[x509] object fields:
      extension (struct: PBE2PARAM_st)
        inline def keyfunc: Ptr[X509_ALGOR] = struct._1
        inline def keyfunc_=(value: Ptr[X509_ALGOR]): Unit = !struct.at1 = value
        inline def encryption: Ptr[X509_ALGOR] = struct._2
        inline def encryption_=(value: Ptr[X509_ALGOR]): Unit = !struct.at2 = value
      end extension

    // Allocates PBE2PARAM_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PBE2PARAM_st] = alloc[PBE2PARAM_st](1)
    def apply(keyfunc: Ptr[X509_ALGOR], encryption: Ptr[X509_ALGOR])(using
        Zone,
    ): Ptr[PBE2PARAM_st] =
      val ____ptr = apply()
      (!____ptr).keyfunc = keyfunc
      (!____ptr).encryption = encryption
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type PBEPARAM = CStruct2[Ptr[ASN1_OCTET_STRING], Ptr[ASN1_INTEGER]]

  object PBEPARAM:
    given _tag: Tag[PBEPARAM] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OCTET_STRING], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509] object fields:
      extension (struct: PBEPARAM)
        inline def salt: Ptr[ASN1_OCTET_STRING] = struct._1
        inline def salt_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at1 = value
        inline def iter: Ptr[ASN1_INTEGER] = struct._2
        inline def iter_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates PBEPARAM on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PBEPARAM] = alloc[PBEPARAM](1)
    def apply(salt: Ptr[ASN1_OCTET_STRING], iter: Ptr[ASN1_INTEGER])(using Zone): Ptr[PBEPARAM] =
      val ____ptr = apply()
      (!____ptr).salt = salt
      (!____ptr).iter = iter
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type PBEPARAM_st = CStruct2[Ptr[ASN1_OCTET_STRING], Ptr[ASN1_INTEGER]]

  object PBEPARAM_st:
    given _tag: Tag[PBEPARAM_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OCTET_STRING], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509] object fields:
      extension (struct: PBEPARAM_st)
        inline def salt: Ptr[ASN1_OCTET_STRING] = struct._1
        inline def salt_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at1 = value
        inline def iter: Ptr[ASN1_INTEGER] = struct._2
        inline def iter_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates PBEPARAM_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PBEPARAM_st] = alloc[PBEPARAM_st](1)
    def apply(salt: Ptr[ASN1_OCTET_STRING], iter: Ptr[ASN1_INTEGER])(using Zone): Ptr[PBEPARAM_st] =
      val ____ptr = apply()
      (!____ptr).salt = salt
      (!____ptr).iter = iter
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type PBKDF2PARAM =
    CStruct4[Ptr[ASN1_TYPE], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER], Ptr[X509_ALGOR]]

  object PBKDF2PARAM:
    given _tag: Tag[PBKDF2PARAM] = Tag
      .materializeCStruct4Tag[Ptr[ASN1_TYPE], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER], Ptr[X509_ALGOR]]

    export fields.*
    private[x509] object fields:
      extension (struct: PBKDF2PARAM)
        inline def salt: Ptr[ASN1_TYPE] = struct._1
        inline def salt_=(value: Ptr[ASN1_TYPE]): Unit = !struct.at1 = value
        inline def iter: Ptr[ASN1_INTEGER] = struct._2
        inline def iter_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
        inline def keylength: Ptr[ASN1_INTEGER] = struct._3
        inline def keylength_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at3 = value
        inline def prf: Ptr[X509_ALGOR] = struct._4
        inline def prf_=(value: Ptr[X509_ALGOR]): Unit = !struct.at4 = value
      end extension

    // Allocates PBKDF2PARAM on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PBKDF2PARAM] = alloc[PBKDF2PARAM](1)
    def apply(
        salt: Ptr[ASN1_TYPE],
        iter: Ptr[ASN1_INTEGER],
        keylength: Ptr[ASN1_INTEGER],
        prf: Ptr[X509_ALGOR],
    )(using Zone): Ptr[PBKDF2PARAM] =
      val ____ptr = apply()
      (!____ptr).salt = salt
      (!____ptr).iter = iter
      (!____ptr).keylength = keylength
      (!____ptr).prf = prf
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type PBKDF2PARAM_st =
    CStruct4[Ptr[ASN1_TYPE], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER], Ptr[X509_ALGOR]]

  object PBKDF2PARAM_st:
    given _tag: Tag[PBKDF2PARAM_st] = Tag
      .materializeCStruct4Tag[Ptr[ASN1_TYPE], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER], Ptr[X509_ALGOR]]

    export fields.*
    private[x509] object fields:
      extension (struct: PBKDF2PARAM_st)
        inline def salt: Ptr[ASN1_TYPE] = struct._1
        inline def salt_=(value: Ptr[ASN1_TYPE]): Unit = !struct.at1 = value
        inline def iter: Ptr[ASN1_INTEGER] = struct._2
        inline def iter_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
        inline def keylength: Ptr[ASN1_INTEGER] = struct._3
        inline def keylength_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at3 = value
        inline def prf: Ptr[X509_ALGOR] = struct._4
        inline def prf_=(value: Ptr[X509_ALGOR]): Unit = !struct.at4 = value
      end extension

    // Allocates PBKDF2PARAM_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PBKDF2PARAM_st] = alloc[PBKDF2PARAM_st](1)
    def apply(
        salt: Ptr[ASN1_TYPE],
        iter: Ptr[ASN1_INTEGER],
        keylength: Ptr[ASN1_INTEGER],
        prf: Ptr[X509_ALGOR],
    )(using Zone): Ptr[PBKDF2PARAM_st] =
      val ____ptr = apply()
      (!____ptr).salt = salt
      (!____ptr).iter = iter
      (!____ptr).keylength = keylength
      (!____ptr).prf = prf
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type SCRYPT_PARAMS = CStruct5[Ptr[ASN1_OCTET_STRING], Ptr[ASN1_INTEGER], Ptr[
    ASN1_INTEGER,
  ], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

  object SCRYPT_PARAMS:
    given _tag: Tag[SCRYPT_PARAMS] = Tag.materializeCStruct5Tag[Ptr[ASN1_OCTET_STRING], Ptr[
      ASN1_INTEGER,
    ], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509] object fields:
      extension (struct: SCRYPT_PARAMS)
        inline def salt: Ptr[ASN1_OCTET_STRING] = struct._1
        inline def salt_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at1 = value
        inline def costParameter: Ptr[ASN1_INTEGER] = struct._2
        inline def costParameter_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
        inline def blockSize: Ptr[ASN1_INTEGER] = struct._3
        inline def blockSize_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at3 = value
        inline def parallelizationParameter: Ptr[ASN1_INTEGER] = struct._4
        inline def parallelizationParameter_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at4 = value
        inline def keyLength: Ptr[ASN1_INTEGER] = struct._5
        inline def keyLength_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at5 = value
      end extension

    // Allocates SCRYPT_PARAMS on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SCRYPT_PARAMS] = alloc[SCRYPT_PARAMS](1)
    def apply(
        salt: Ptr[ASN1_OCTET_STRING],
        costParameter: Ptr[ASN1_INTEGER],
        blockSize: Ptr[ASN1_INTEGER],
        parallelizationParameter: Ptr[ASN1_INTEGER],
        keyLength: Ptr[ASN1_INTEGER],
    )(using Zone): Ptr[SCRYPT_PARAMS] =
      val ____ptr = apply()
      (!____ptr).salt = salt
      (!____ptr).costParameter = costParameter
      (!____ptr).blockSize = blockSize
      (!____ptr).parallelizationParameter = parallelizationParameter
      (!____ptr).keyLength = keyLength
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type SCRYPT_PARAMS_st = CStruct5[Ptr[ASN1_OCTET_STRING], Ptr[ASN1_INTEGER], Ptr[
    ASN1_INTEGER,
  ], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

  object SCRYPT_PARAMS_st:
    given _tag: Tag[SCRYPT_PARAMS_st] = Tag.materializeCStruct5Tag[Ptr[ASN1_OCTET_STRING], Ptr[
      ASN1_INTEGER,
    ], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509] object fields:
      extension (struct: SCRYPT_PARAMS_st)
        inline def salt: Ptr[ASN1_OCTET_STRING] = struct._1
        inline def salt_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at1 = value
        inline def costParameter: Ptr[ASN1_INTEGER] = struct._2
        inline def costParameter_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
        inline def blockSize: Ptr[ASN1_INTEGER] = struct._3
        inline def blockSize_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at3 = value
        inline def parallelizationParameter: Ptr[ASN1_INTEGER] = struct._4
        inline def parallelizationParameter_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at4 = value
        inline def keyLength: Ptr[ASN1_INTEGER] = struct._5
        inline def keyLength_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at5 = value
      end extension

    // Allocates SCRYPT_PARAMS_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SCRYPT_PARAMS_st] =
      alloc[SCRYPT_PARAMS_st](1)
    def apply(
        salt: Ptr[ASN1_OCTET_STRING],
        costParameter: Ptr[ASN1_INTEGER],
        blockSize: Ptr[ASN1_INTEGER],
        parallelizationParameter: Ptr[ASN1_INTEGER],
        keyLength: Ptr[ASN1_INTEGER],
    )(using Zone): Ptr[SCRYPT_PARAMS_st] =
      val ____ptr = apply()
      (!____ptr).salt = salt
      (!____ptr).costParameter = costParameter
      (!____ptr).blockSize = blockSize
      (!____ptr).parallelizationParameter = parallelizationParameter
      (!____ptr).keyLength = keyLength
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509 = CStruct0

  object X509:
    given _tag: Tag[X509] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_STORE = CStruct0

  object X509_STORE:
    given _tag: Tag[X509_STORE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_STORE_CTX = CStruct0

  object X509_STORE_CTX:
    given _tag: Tag[X509_STORE_CTX] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_ALGOR = CStruct2[Ptr[ASN1_OBJECT], Ptr[ASN1_TYPE]]

  object X509_ALGOR:
    given _tag: Tag[X509_ALGOR] = Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[ASN1_TYPE]]

    export fields.*
    private[x509] object fields:
      extension (struct: X509_ALGOR)
        inline def algorithm: Ptr[ASN1_OBJECT] = struct._1
        inline def algorithm_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def parameter: Ptr[ASN1_TYPE] = struct._2
        inline def parameter_=(value: Ptr[ASN1_TYPE]): Unit = !struct.at2 = value
      end extension

    // Allocates X509_ALGOR on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509_ALGOR] = alloc[X509_ALGOR](1)
    def apply(algorithm: Ptr[ASN1_OBJECT], parameter: Ptr[ASN1_TYPE])(using Zone): Ptr[X509_ALGOR] =
      val ____ptr = apply()
      (!____ptr).algorithm = algorithm
      (!____ptr).parameter = parameter
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  opaque type X509_ALGORS = CStruct0

  object X509_ALGORS:
    given _tag: Tag[X509_ALGORS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_ATTRIBUTE = CStruct0

  object X509_ATTRIBUTE:
    given _tag: Tag[X509_ATTRIBUTE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_CERT_AUX = CStruct0

  object X509_CERT_AUX:
    given _tag: Tag[X509_CERT_AUX] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_CINF = CStruct0

  object X509_CINF:
    given _tag: Tag[X509_CINF] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_CRL = CStruct0

  object X509_CRL:
    given _tag: Tag[X509_CRL] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_CRL_INFO = CStruct0

  object X509_CRL_INFO:
    given _tag: Tag[X509_CRL_INFO] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_CRL_METHOD = CStruct0

  object X509_CRL_METHOD:
    given _tag: Tag[X509_CRL_METHOD] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_EXTENSION = CStruct0

  object X509_EXTENSION:
    given _tag: Tag[X509_EXTENSION] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_EXTENSIONS = CStruct0

  object X509_EXTENSIONS:
    given _tag: Tag[X509_EXTENSIONS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_INFO =
    CStruct6[Ptr[X509], Ptr[X509_CRL], Ptr[X509_PKEY], EVP_CIPHER_INFO, CInt, CString]

  object X509_INFO:
    given _tag: Tag[X509_INFO] = Tag.materializeCStruct6Tag[Ptr[X509], Ptr[X509_CRL], Ptr[
      X509_PKEY,
    ], EVP_CIPHER_INFO, CInt, CString]

    export fields.*
    private[x509] object fields:
      extension (struct: X509_INFO)
        inline def x509: Ptr[X509] = struct._1
        inline def x509_=(value: Ptr[X509]): Unit = !struct.at1 = value
        inline def crl: Ptr[X509_CRL] = struct._2
        inline def crl_=(value: Ptr[X509_CRL]): Unit = !struct.at2 = value
        inline def x_pkey: Ptr[X509_PKEY] = struct._3
        inline def x_pkey_=(value: Ptr[X509_PKEY]): Unit = !struct.at3 = value
        inline def enc_cipher: EVP_CIPHER_INFO = struct._4
        inline def enc_cipher_=(value: EVP_CIPHER_INFO): Unit = !struct.at4 = value
        inline def enc_len: CInt = struct._5
        inline def enc_len_=(value: CInt): Unit = !struct.at5 = value
        inline def enc_data: CString = struct._6
        inline def enc_data_=(value: CString): Unit = !struct.at6 = value
      end extension

    // Allocates X509_INFO on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509_INFO] = alloc[X509_INFO](1)
    def apply(
        x509: Ptr[X509],
        crl: Ptr[X509_CRL],
        x_pkey: Ptr[X509_PKEY],
        enc_cipher: EVP_CIPHER_INFO,
        enc_len: CInt,
        enc_data: CString,
    )(using Zone): Ptr[X509_INFO] =
      val ____ptr = apply()
      (!____ptr).x509 = x509
      (!____ptr).crl = crl
      (!____ptr).x_pkey = x_pkey
      (!____ptr).enc_cipher = enc_cipher
      (!____ptr).enc_len = enc_len
      (!____ptr).enc_data = enc_data
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_NAME = CStruct0

  object X509_NAME:
    given _tag: Tag[X509_NAME] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_NAME_ENTRY = CStruct0

  object X509_NAME_ENTRY:
    given _tag: Tag[X509_NAME_ENTRY] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_PKEY = CStruct8[CInt, Ptr[X509_ALGOR], Ptr[ASN1_OCTET_STRING], Ptr[
    EVP_PKEY,
  ], CInt, CString, CInt, EVP_CIPHER_INFO]

  object X509_PKEY:
    given _tag: Tag[X509_PKEY] = Tag.materializeCStruct8Tag[CInt, Ptr[X509_ALGOR], Ptr[
      ASN1_OCTET_STRING,
    ], Ptr[EVP_PKEY], CInt, CString, CInt, EVP_CIPHER_INFO]

    export fields.*
    private[x509] object fields:
      extension (struct: X509_PKEY)
        inline def version: CInt = struct._1
        inline def version_=(value: CInt): Unit = !struct.at1 = value
        inline def enc_algor: Ptr[X509_ALGOR] = struct._2
        inline def enc_algor_=(value: Ptr[X509_ALGOR]): Unit = !struct.at2 = value
        inline def enc_pkey: Ptr[ASN1_OCTET_STRING] = struct._3
        inline def enc_pkey_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at3 = value
        inline def dec_pkey: Ptr[EVP_PKEY] = struct._4
        inline def dec_pkey_=(value: Ptr[EVP_PKEY]): Unit = !struct.at4 = value
        inline def key_length: CInt = struct._5
        inline def key_length_=(value: CInt): Unit = !struct.at5 = value
        inline def key_data: CString = struct._6
        inline def key_data_=(value: CString): Unit = !struct.at6 = value
        inline def key_free: CInt = struct._7
        inline def key_free_=(value: CInt): Unit = !struct.at7 = value
        inline def cipher: EVP_CIPHER_INFO = struct._8
        inline def cipher_=(value: EVP_CIPHER_INFO): Unit = !struct.at8 = value
      end extension

    // Allocates X509_PKEY on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509_PKEY] = alloc[X509_PKEY](1)
    def apply(
        version: CInt,
        enc_algor: Ptr[X509_ALGOR],
        enc_pkey: Ptr[ASN1_OCTET_STRING],
        dec_pkey: Ptr[EVP_PKEY],
        key_length: CInt,
        key_data: CString,
        key_free: CInt,
        cipher: EVP_CIPHER_INFO,
    )(using Zone): Ptr[X509_PKEY] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).enc_algor = enc_algor
      (!____ptr).enc_pkey = enc_pkey
      (!____ptr).dec_pkey = dec_pkey
      (!____ptr).key_length = key_length
      (!____ptr).key_data = key_data
      (!____ptr).key_free = key_free
      (!____ptr).cipher = cipher
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_PUBKEY = CStruct0

  object X509_PUBKEY:
    given _tag: Tag[X509_PUBKEY] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_REQ = CStruct0

  object X509_REQ:
    given _tag: Tag[X509_REQ] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_REQ_INFO = CStruct0

  object X509_REQ_INFO:
    given _tag: Tag[X509_REQ_INFO] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_REVOKED = CStruct0

  object X509_REVOKED:
    given _tag: Tag[X509_REVOKED] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_SIG = CStruct0

  object X509_SIG:
    given _tag: Tag[X509_SIG] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_SIG_INFO = CStruct0

  object X509_SIG_INFO:
    given _tag: Tag[X509_SIG_INFO] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_VAL = CStruct2[Ptr[ASN1_TIME], Ptr[ASN1_TIME]]

  object X509_VAL:
    given _tag: Tag[X509_VAL] = Tag.materializeCStruct2Tag[Ptr[ASN1_TIME], Ptr[ASN1_TIME]]

    export fields.*
    private[x509] object fields:
      extension (struct: X509_VAL)
        inline def notBefore: Ptr[ASN1_TIME] = struct._1
        inline def notBefore_=(value: Ptr[ASN1_TIME]): Unit = !struct.at1 = value
        inline def notAfter: Ptr[ASN1_TIME] = struct._2
        inline def notAfter_=(value: Ptr[ASN1_TIME]): Unit = !struct.at2 = value
      end extension

    // Allocates X509_VAL on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509_VAL] = alloc[X509_VAL](1)
    def apply(notBefore: Ptr[ASN1_TIME], notAfter: Ptr[ASN1_TIME])(using Zone): Ptr[X509_VAL] =
      val ____ptr = apply()
      (!____ptr).notBefore = notBefore
      (!____ptr).notAfter = notAfter
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_VERIFY_PARAM = CStruct0

  object X509_VERIFY_PARAM:
    given _tag: Tag[X509_VERIFY_PARAM] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_algor_st = CStruct2[Ptr[ASN1_OBJECT], Ptr[ASN1_TYPE]]

  object X509_algor_st:
    given _tag: Tag[X509_algor_st] = Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[ASN1_TYPE]]

    export fields.*
    private[x509] object fields:
      extension (struct: X509_algor_st)
        inline def algorithm: Ptr[ASN1_OBJECT] = struct._1
        inline def algorithm_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def parameter: Ptr[ASN1_TYPE] = struct._2
        inline def parameter_=(value: Ptr[ASN1_TYPE]): Unit = !struct.at2 = value
      end extension

    // Allocates X509_algor_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509_algor_st] = alloc[X509_algor_st](1)
    def apply(algorithm: Ptr[ASN1_OBJECT], parameter: Ptr[ASN1_TYPE])(using
        Zone,
    ): Ptr[X509_algor_st] =
      val ____ptr = apply()
      (!____ptr).algorithm = algorithm
      (!____ptr).parameter = parameter
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_crl_info_st = CStruct0

  object X509_crl_info_st:
    given _tag: Tag[X509_crl_info_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_extension_st = CStruct0

  object X509_extension_st:
    given _tag: Tag[X509_extension_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_info_st =
    CStruct6[Ptr[X509], Ptr[X509_CRL], Ptr[X509_PKEY], EVP_CIPHER_INFO, CInt, CString]

  object X509_info_st:
    given _tag: Tag[X509_info_st] = Tag.materializeCStruct6Tag[Ptr[X509], Ptr[X509_CRL], Ptr[
      X509_PKEY,
    ], EVP_CIPHER_INFO, CInt, CString]

    export fields.*
    private[x509] object fields:
      extension (struct: X509_info_st)
        inline def x509: Ptr[X509] = struct._1
        inline def x509_=(value: Ptr[X509]): Unit = !struct.at1 = value
        inline def crl: Ptr[X509_CRL] = struct._2
        inline def crl_=(value: Ptr[X509_CRL]): Unit = !struct.at2 = value
        inline def x_pkey: Ptr[X509_PKEY] = struct._3
        inline def x_pkey_=(value: Ptr[X509_PKEY]): Unit = !struct.at3 = value
        inline def enc_cipher: EVP_CIPHER_INFO = struct._4
        inline def enc_cipher_=(value: EVP_CIPHER_INFO): Unit = !struct.at4 = value
        inline def enc_len: CInt = struct._5
        inline def enc_len_=(value: CInt): Unit = !struct.at5 = value
        inline def enc_data: CString = struct._6
        inline def enc_data_=(value: CString): Unit = !struct.at6 = value
      end extension

    // Allocates X509_info_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509_info_st] = alloc[X509_info_st](1)
    def apply(
        x509: Ptr[X509],
        crl: Ptr[X509_CRL],
        x_pkey: Ptr[X509_PKEY],
        enc_cipher: EVP_CIPHER_INFO,
        enc_len: CInt,
        enc_data: CString,
    )(using Zone): Ptr[X509_info_st] =
      val ____ptr = apply()
      (!____ptr).x509 = x509
      (!____ptr).crl = crl
      (!____ptr).x_pkey = x_pkey
      (!____ptr).enc_cipher = enc_cipher
      (!____ptr).enc_len = enc_len
      (!____ptr).enc_data = enc_data
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_name_entry_st = CStruct0

  object X509_name_entry_st:
    given _tag: Tag[X509_name_entry_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_req_info_st = CStruct0

  object X509_req_info_st:
    given _tag: Tag[X509_req_info_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_req_st = CStruct0

  object X509_req_st:
    given _tag: Tag[X509_req_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_sig_st = CStruct0

  object X509_sig_st:
    given _tag: Tag[X509_sig_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type X509_val_st = CStruct2[Ptr[ASN1_TIME], Ptr[ASN1_TIME]]

  object X509_val_st:
    given _tag: Tag[X509_val_st] = Tag.materializeCStruct2Tag[Ptr[ASN1_TIME], Ptr[ASN1_TIME]]

    export fields.*
    private[x509] object fields:
      extension (struct: X509_val_st)
        inline def notBefore: Ptr[ASN1_TIME] = struct._1
        inline def notBefore_=(value: Ptr[ASN1_TIME]): Unit = !struct.at1 = value
        inline def notAfter: Ptr[ASN1_TIME] = struct._2
        inline def notAfter_=(value: Ptr[ASN1_TIME]): Unit = !struct.at2 = value
      end extension

    // Allocates X509_val_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509_val_st] = alloc[X509_val_st](1)
    def apply(notBefore: Ptr[ASN1_TIME], notAfter: Ptr[ASN1_TIME])(using Zone): Ptr[X509_val_st] =
      val ____ptr = apply()
      (!____ptr).notBefore = notBefore
      (!____ptr).notAfter = notAfter
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509.h
   */
  opaque type private_key_st = CStruct8[CInt, Ptr[X509_ALGOR], Ptr[ASN1_OCTET_STRING], Ptr[
    EVP_PKEY,
  ], CInt, CString, CInt, EVP_CIPHER_INFO]

  object private_key_st:
    given _tag: Tag[private_key_st] = Tag.materializeCStruct8Tag[CInt, Ptr[X509_ALGOR], Ptr[
      ASN1_OCTET_STRING,
    ], Ptr[EVP_PKEY], CInt, CString, CInt, EVP_CIPHER_INFO]

    export fields.*
    private[x509] object fields:
      extension (struct: private_key_st)
        inline def version: CInt = struct._1
        inline def version_=(value: CInt): Unit = !struct.at1 = value
        inline def enc_algor: Ptr[X509_ALGOR] = struct._2
        inline def enc_algor_=(value: Ptr[X509_ALGOR]): Unit = !struct.at2 = value
        inline def enc_pkey: Ptr[ASN1_OCTET_STRING] = struct._3
        inline def enc_pkey_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at3 = value
        inline def dec_pkey: Ptr[EVP_PKEY] = struct._4
        inline def dec_pkey_=(value: Ptr[EVP_PKEY]): Unit = !struct.at4 = value
        inline def key_length: CInt = struct._5
        inline def key_length_=(value: CInt): Unit = !struct.at5 = value
        inline def key_data: CString = struct._6
        inline def key_data_=(value: CString): Unit = !struct.at6 = value
        inline def key_free: CInt = struct._7
        inline def key_free_=(value: CInt): Unit = !struct.at7 = value
        inline def cipher: EVP_CIPHER_INFO = struct._8
        inline def cipher_=(value: EVP_CIPHER_INFO): Unit = !struct.at8 = value
      end extension

    // Allocates private_key_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[private_key_st] = alloc[private_key_st](1)
    def apply(
        version: CInt,
        enc_algor: Ptr[X509_ALGOR],
        enc_pkey: Ptr[ASN1_OCTET_STRING],
        dec_pkey: Ptr[EVP_PKEY],
        key_length: CInt,
        key_data: CString,
        key_free: CInt,
        cipher: EVP_CIPHER_INFO,
    )(using Zone): Ptr[private_key_st] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).enc_algor = enc_algor
      (!____ptr).enc_pkey = enc_pkey
      (!____ptr).dec_pkey = dec_pkey
      (!____ptr).key_length = key_length
      (!____ptr).key_data = key_data
      (!____ptr).key_free = key_free
      (!____ptr).cipher = cipher
      ____ptr

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509.h
//   // */
//   // opaque type stack_st_X509 = CStruct0

//   // object stack_st_X509:
//   //   given _tag: Tag[stack_st_X509] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509.h
//   // */
//   // opaque type stack_st_X509_ATTRIBUTE = CStruct0

//   // object stack_st_X509_ATTRIBUTE:
//   //   given _tag: Tag[stack_st_X509_ATTRIBUTE] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509.h
//   // */
//   // opaque type stack_st_X509_EXTENSION = CStruct0

//   // object stack_st_X509_EXTENSION:
//   //   given _tag: Tag[stack_st_X509_EXTENSION] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509.h
//   // */
//   // opaque type stack_st_X509_REVOKED = CStruct0

//   // object stack_st_X509_REVOKED:
//   //   given _tag: Tag[stack_st_X509_REVOKED] = Tag.materializeCStruct0Tag

//   /**
//    * [bindgen] header: /usr/include/openssl/x509.h
//   */
//   opaque type x509_attributes_st = CStruct0

//   object x509_attributes_st:
//     given _tag: Tag[x509_attributes_st] = Tag.materializeCStruct0Tag

//   /**
//    * [bindgen] header: /usr/include/openssl/x509.h
//   */
//   opaque type x509_cert_aux_st = CStruct0

//   object x509_cert_aux_st:
//     given _tag: Tag[x509_cert_aux_st] = Tag.materializeCStruct0Tag

//   /**
//    * [bindgen] header: /usr/include/openssl/x509.h
//   */
//   opaque type x509_cinf_st = CStruct0

//   object x509_cinf_st:
//     given _tag: Tag[x509_cinf_st] = Tag.materializeCStruct0Tag
