package snhttp.experimental.openssl
package _openssl.x509v3

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.asn1.Types.*
import _root_.snhttp.experimental.openssl._openssl.safestack.{
  stack_st_SXNETID,
  stack_st_CONF_VALUE,
  stack_st_ASN1_INTEGER,
  stack_st_POLICYQUALINFO,
  stack_st_GENERAL_SUBTREE,
  stack_st_X509_NAME_ENTRY,
}
import _root_.snhttp.experimental.openssl._openssl.types.EVP_PKEY
import _root_.snhttp.experimental.openssl._openssl.x509.Types.{X509, X509_CRL, X509_REQ, X509_NAME}

import Aliases.*

object Structs:

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ACCESS_DESCRIPTION = CStruct2[Ptr[ASN1_OBJECT], Ptr[GENERAL_NAME]]

  object ACCESS_DESCRIPTION:
    given _tag: Tag[ACCESS_DESCRIPTION] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[GENERAL_NAME]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ACCESS_DESCRIPTION)
        inline def method: Ptr[ASN1_OBJECT] = struct._1
        inline def method_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def location: Ptr[GENERAL_NAME] = struct._2
        inline def location_=(value: Ptr[GENERAL_NAME]): Unit = !struct.at2 = value
      end extension

    // Allocates ACCESS_DESCRIPTION on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ACCESS_DESCRIPTION] =
      scala.scalanative.unsafe.alloc[ACCESS_DESCRIPTION](1)
    def apply(method: Ptr[ASN1_OBJECT], location: Ptr[GENERAL_NAME])(using
        Zone,
    ): Ptr[ACCESS_DESCRIPTION] =
      val ____ptr = apply()
      (!____ptr).method = method
      (!____ptr).location = location
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ACCESS_DESCRIPTION_st = CStruct2[Ptr[ASN1_OBJECT], Ptr[GENERAL_NAME]]

  object ACCESS_DESCRIPTION_st:
    given _tag: Tag[ACCESS_DESCRIPTION_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[GENERAL_NAME]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ACCESS_DESCRIPTION_st)
        inline def method: Ptr[ASN1_OBJECT] = struct._1
        inline def method_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def location: Ptr[GENERAL_NAME] = struct._2
        inline def location_=(value: Ptr[GENERAL_NAME]): Unit = !struct.at2 = value
      end extension

    // Allocates ACCESS_DESCRIPTION_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ACCESS_DESCRIPTION_st] =
      scala.scalanative.unsafe.alloc[ACCESS_DESCRIPTION_st](1)
    def apply(method: Ptr[ASN1_OBJECT], location: Ptr[GENERAL_NAME])(using
        Zone,
    ): Ptr[ACCESS_DESCRIPTION_st] =
      val ____ptr = apply()
      (!____ptr).method = method
      (!____ptr).location = location
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ADMISSIONS = CStruct0

  object ADMISSIONS:
    given _tag: Tag[ADMISSIONS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ADMISSION_SYNTAX = CStruct0

  object ADMISSION_SYNTAX:
    given _tag: Tag[ADMISSION_SYNTAX] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdOrRange = CStruct2[CInt, ASIdOrRange_U]

  object ASIdOrRange:
    given _tag: Tag[ASIdOrRange] = Tag.materializeCStruct2Tag[CInt, ASIdOrRange_U]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ASIdOrRange)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def u: ASIdOrRange_U = struct._2
        inline def u_=(value: ASIdOrRange_U): Unit = !struct.at2 = value
      end extension

    // Allocates ASIdOrRange on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ASIdOrRange] = scala.scalanative.unsafe.alloc[ASIdOrRange](1)
    def apply(`type`: CInt, u: ASIdOrRange_U)(using Zone): Ptr[ASIdOrRange] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).u = u
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdOrRange_U = CArray[Byte, Nat._8]
  object ASIdOrRange_U:
    given _tag: Tag[ASIdOrRange_U] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[ASIdOrRange_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdOrRange_U](1)
      ___ptr

    @scala.annotation.targetName("apply_id")
    def apply(id: Ptr[ASN1_INTEGER])(using Zone): Ptr[ASIdOrRange_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdOrRange_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]].update(0, id)
      ___ptr

    @scala.annotation.targetName("apply_range")
    def apply(range: Ptr[ASRange])(using Zone): Ptr[ASIdOrRange_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdOrRange_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASRange]]].update(0, range)
      ___ptr

    extension (struct: ASIdOrRange_U)
      inline def id: Ptr[ASN1_INTEGER] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]]
      inline def id_=(value: Ptr[ASN1_INTEGER]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]] = value
      inline def range: Ptr[ASRange] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASRange]]]
      inline def range_=(value: Ptr[ASRange]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASRange]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdOrRange_st = CStruct2[CInt, ASIdOrRange_st_U]

  object ASIdOrRange_st:
    given _tag: Tag[ASIdOrRange_st] = Tag.materializeCStruct2Tag[CInt, ASIdOrRange_st_U]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ASIdOrRange_st)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def u: ASIdOrRange_st_U = struct._2
        inline def u_=(value: ASIdOrRange_st_U): Unit = !struct.at2 = value
      end extension

    // Allocates ASIdOrRange_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ASIdOrRange_st] = scala.scalanative.unsafe.alloc[ASIdOrRange_st](1)
    def apply(`type`: CInt, u: ASIdOrRange_st_U)(using Zone): Ptr[ASIdOrRange_st] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).u = u
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdOrRange_st_U = CArray[Byte, Nat._8]
  object ASIdOrRange_st_U:
    given _tag: Tag[ASIdOrRange_st_U] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[ASIdOrRange_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdOrRange_st_U](1)
      ___ptr

    @scala.annotation.targetName("apply_id")
    def apply(id: Ptr[ASN1_INTEGER])(using Zone): Ptr[ASIdOrRange_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdOrRange_st_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]].update(0, id)
      ___ptr

    @scala.annotation.targetName("apply_range")
    def apply(range: Ptr[ASRange])(using Zone): Ptr[ASIdOrRange_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdOrRange_st_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASRange]]].update(0, range)
      ___ptr

    extension (struct: ASIdOrRange_st_U)
      inline def id: Ptr[ASN1_INTEGER] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]]
      inline def id_=(value: Ptr[ASN1_INTEGER]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]] = value
      inline def range: Ptr[ASRange] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASRange]]]
      inline def range_=(value: Ptr[ASRange]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASRange]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdOrRanges = CStruct0

  object ASIdOrRanges:
    given _tag: Tag[ASIdOrRanges] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdentifierChoice = CStruct2[CInt, ASIdentifierChoice_U]

  object ASIdentifierChoice:
    given _tag: Tag[ASIdentifierChoice] = Tag.materializeCStruct2Tag[CInt, ASIdentifierChoice_U]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ASIdentifierChoice)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def u: ASIdentifierChoice_U = struct._2
        inline def u_=(value: ASIdentifierChoice_U): Unit = !struct.at2 = value
      end extension

    // Allocates ASIdentifierChoice on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ASIdentifierChoice] =
      scala.scalanative.unsafe.alloc[ASIdentifierChoice](1)
    def apply(`type`: CInt, u: ASIdentifierChoice_U)(using Zone): Ptr[ASIdentifierChoice] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).u = u
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdentifierChoice_U = CArray[Byte, Nat._8]
  object ASIdentifierChoice_U:
    given _tag: Tag[ASIdentifierChoice_U] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[ASIdentifierChoice_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdentifierChoice_U](1)
      ___ptr

    @scala.annotation.targetName("apply_inherit")
    def apply(inherit: Ptr[ASN1_NULL])(using Zone): Ptr[ASIdentifierChoice_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdentifierChoice_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]].update(0, inherit)
      ___ptr

    @scala.annotation.targetName("apply_asIdsOrRanges")
    def apply(asIdsOrRanges: Ptr[ASIdOrRanges])(using Zone): Ptr[ASIdentifierChoice_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdentifierChoice_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASIdOrRanges]]].update(0, asIdsOrRanges)
      ___ptr

    extension (struct: ASIdentifierChoice_U)
      inline def inherit: Ptr[ASN1_NULL] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]]
      inline def inherit_=(value: Ptr[ASN1_NULL]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]] = value
      inline def asIdsOrRanges: Ptr[ASIdOrRanges] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASIdOrRanges]]]
      inline def asIdsOrRanges_=(value: Ptr[ASIdOrRanges]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASIdOrRanges]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdentifierChoice_st = CStruct2[CInt, ASIdentifierChoice_st_U]

  object ASIdentifierChoice_st:
    given _tag: Tag[ASIdentifierChoice_st] =
      Tag.materializeCStruct2Tag[CInt, ASIdentifierChoice_st_U]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ASIdentifierChoice_st)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def u: ASIdentifierChoice_st_U = struct._2
        inline def u_=(value: ASIdentifierChoice_st_U): Unit = !struct.at2 = value
      end extension

    // Allocates ASIdentifierChoice_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ASIdentifierChoice_st] =
      scala.scalanative.unsafe.alloc[ASIdentifierChoice_st](1)
    def apply(`type`: CInt, u: ASIdentifierChoice_st_U)(using Zone): Ptr[ASIdentifierChoice_st] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).u = u
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdentifierChoice_st_U = CArray[Byte, Nat._8]
  object ASIdentifierChoice_st_U:
    given _tag: Tag[ASIdentifierChoice_st_U] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[ASIdentifierChoice_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdentifierChoice_st_U](1)
      ___ptr

    @scala.annotation.targetName("apply_inherit")
    def apply(inherit: Ptr[ASN1_NULL])(using Zone): Ptr[ASIdentifierChoice_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdentifierChoice_st_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]].update(0, inherit)
      ___ptr

    @scala.annotation.targetName("apply_asIdsOrRanges")
    def apply(asIdsOrRanges: Ptr[ASIdOrRanges])(using Zone): Ptr[ASIdentifierChoice_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[ASIdentifierChoice_st_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASIdOrRanges]]].update(0, asIdsOrRanges)
      ___ptr

    extension (struct: ASIdentifierChoice_st_U)
      inline def inherit: Ptr[ASN1_NULL] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]]
      inline def inherit_=(value: Ptr[ASN1_NULL]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]] = value
      inline def asIdsOrRanges: Ptr[ASIdOrRanges] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASIdOrRanges]]]
      inline def asIdsOrRanges_=(value: Ptr[ASIdOrRanges]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASIdOrRanges]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdentifiers = CStruct2[Ptr[ASIdentifierChoice], Ptr[ASIdentifierChoice]]

  object ASIdentifiers:
    given _tag: Tag[ASIdentifiers] =
      Tag.materializeCStruct2Tag[Ptr[ASIdentifierChoice], Ptr[ASIdentifierChoice]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ASIdentifiers)
        inline def asnum: Ptr[ASIdentifierChoice] = struct._1
        inline def asnum_=(value: Ptr[ASIdentifierChoice]): Unit = !struct.at1 = value
        inline def rdi: Ptr[ASIdentifierChoice] = struct._2
        inline def rdi_=(value: Ptr[ASIdentifierChoice]): Unit = !struct.at2 = value
      end extension

    // Allocates ASIdentifiers on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ASIdentifiers] = scala.scalanative.unsafe.alloc[ASIdentifiers](1)
    def apply(asnum: Ptr[ASIdentifierChoice], rdi: Ptr[ASIdentifierChoice])(using
        Zone,
    ): Ptr[ASIdentifiers] =
      val ____ptr = apply()
      (!____ptr).asnum = asnum
      (!____ptr).rdi = rdi
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASIdentifiers_st = CStruct2[Ptr[ASIdentifierChoice], Ptr[ASIdentifierChoice]]

  object ASIdentifiers_st:
    given _tag: Tag[ASIdentifiers_st] =
      Tag.materializeCStruct2Tag[Ptr[ASIdentifierChoice], Ptr[ASIdentifierChoice]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ASIdentifiers_st)
        inline def asnum: Ptr[ASIdentifierChoice] = struct._1
        inline def asnum_=(value: Ptr[ASIdentifierChoice]): Unit = !struct.at1 = value
        inline def rdi: Ptr[ASIdentifierChoice] = struct._2
        inline def rdi_=(value: Ptr[ASIdentifierChoice]): Unit = !struct.at2 = value
      end extension

    // Allocates ASIdentifiers_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ASIdentifiers_st] =
      scala.scalanative.unsafe.alloc[ASIdentifiers_st](1)
    def apply(asnum: Ptr[ASIdentifierChoice], rdi: Ptr[ASIdentifierChoice])(using
        Zone,
    ): Ptr[ASIdentifiers_st] =
      val ____ptr = apply()
      (!____ptr).asnum = asnum
      (!____ptr).rdi = rdi
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASRange = CStruct2[Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

  object ASRange:
    given _tag: Tag[ASRange] = Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ASRange)
        inline def min: Ptr[ASN1_INTEGER] = struct._1
        inline def min_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def max: Ptr[ASN1_INTEGER] = struct._2
        inline def max_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates ASRange on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ASRange] = scala.scalanative.unsafe.alloc[ASRange](1)
    def apply(min: Ptr[ASN1_INTEGER], max: Ptr[ASN1_INTEGER])(using Zone): Ptr[ASRange] =
      val ____ptr = apply()
      (!____ptr).min = min
      (!____ptr).max = max
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ASRange_st = CStruct2[Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

  object ASRange_st:
    given _tag: Tag[ASRange_st] = Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ASRange_st)
        inline def min: Ptr[ASN1_INTEGER] = struct._1
        inline def min_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def max: Ptr[ASN1_INTEGER] = struct._2
        inline def max_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates ASRange_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ASRange_st] = scala.scalanative.unsafe.alloc[ASRange_st](1)
    def apply(min: Ptr[ASN1_INTEGER], max: Ptr[ASN1_INTEGER])(using Zone): Ptr[ASRange_st] =
      val ____ptr = apply()
      (!____ptr).min = min
      (!____ptr).max = max
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type AUTHORITY_INFO_ACCESS = CStruct0

  object AUTHORITY_INFO_ACCESS:
    given _tag: Tag[AUTHORITY_INFO_ACCESS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type AUTHORITY_KEYID =
    CStruct3[Ptr[ASN1_OCTET_STRING], Ptr[GENERAL_NAMES], Ptr[ASN1_INTEGER]]

  object AUTHORITY_KEYID:
    given _tag: Tag[AUTHORITY_KEYID] =
      Tag.materializeCStruct3Tag[Ptr[ASN1_OCTET_STRING], Ptr[GENERAL_NAMES], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: AUTHORITY_KEYID)
        inline def keyid: Ptr[ASN1_OCTET_STRING] = struct._1
        inline def keyid_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at1 = value
        inline def issuer: Ptr[GENERAL_NAMES] = struct._2
        inline def issuer_=(value: Ptr[GENERAL_NAMES]): Unit = !struct.at2 = value
        inline def serial: Ptr[ASN1_INTEGER] = struct._3
        inline def serial_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at3 = value
      end extension

    // Allocates AUTHORITY_KEYID on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[AUTHORITY_KEYID] =
      scala.scalanative.unsafe.alloc[AUTHORITY_KEYID](1)
    def apply(keyid: Ptr[ASN1_OCTET_STRING], issuer: Ptr[GENERAL_NAMES], serial: Ptr[ASN1_INTEGER])(
        using Zone,
    ): Ptr[AUTHORITY_KEYID] =
      val ____ptr = apply()
      (!____ptr).keyid = keyid
      (!____ptr).issuer = issuer
      (!____ptr).serial = serial
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type AUTHORITY_KEYID_st =
    CStruct3[Ptr[ASN1_OCTET_STRING], Ptr[GENERAL_NAMES], Ptr[ASN1_INTEGER]]

  object AUTHORITY_KEYID_st:
    given _tag: Tag[AUTHORITY_KEYID_st] =
      Tag.materializeCStruct3Tag[Ptr[ASN1_OCTET_STRING], Ptr[GENERAL_NAMES], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: AUTHORITY_KEYID_st)
        inline def keyid: Ptr[ASN1_OCTET_STRING] = struct._1
        inline def keyid_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at1 = value
        inline def issuer: Ptr[GENERAL_NAMES] = struct._2
        inline def issuer_=(value: Ptr[GENERAL_NAMES]): Unit = !struct.at2 = value
        inline def serial: Ptr[ASN1_INTEGER] = struct._3
        inline def serial_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at3 = value
      end extension

    // Allocates AUTHORITY_KEYID_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[AUTHORITY_KEYID_st] =
      scala.scalanative.unsafe.alloc[AUTHORITY_KEYID_st](1)
    def apply(keyid: Ptr[ASN1_OCTET_STRING], issuer: Ptr[GENERAL_NAMES], serial: Ptr[ASN1_INTEGER])(
        using Zone,
    ): Ptr[AUTHORITY_KEYID_st] =
      val ____ptr = apply()
      (!____ptr).keyid = keyid
      (!____ptr).issuer = issuer
      (!____ptr).serial = serial
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type AdmissionSyntax_st = CStruct0

  object AdmissionSyntax_st:
    given _tag: Tag[AdmissionSyntax_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type Admissions_st = CStruct0

  object Admissions_st:
    given _tag: Tag[Admissions_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type BASIC_CONSTRAINTS = CStruct2[CInt, Ptr[ASN1_INTEGER]]

  object BASIC_CONSTRAINTS:
    given _tag: Tag[BASIC_CONSTRAINTS] = Tag.materializeCStruct2Tag[CInt, Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: BASIC_CONSTRAINTS)
        inline def ca: CInt = struct._1
        inline def ca_=(value: CInt): Unit = !struct.at1 = value
        inline def pathlen: Ptr[ASN1_INTEGER] = struct._2
        inline def pathlen_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates BASIC_CONSTRAINTS on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[BASIC_CONSTRAINTS] =
      scala.scalanative.unsafe.alloc[BASIC_CONSTRAINTS](1)
    def apply(ca: CInt, pathlen: Ptr[ASN1_INTEGER])(using Zone): Ptr[BASIC_CONSTRAINTS] =
      val ____ptr = apply()
      (!____ptr).ca = ca
      (!____ptr).pathlen = pathlen
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type BASIC_CONSTRAINTS_st = CStruct2[CInt, Ptr[ASN1_INTEGER]]

  object BASIC_CONSTRAINTS_st:
    given _tag: Tag[BASIC_CONSTRAINTS_st] = Tag.materializeCStruct2Tag[CInt, Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: BASIC_CONSTRAINTS_st)
        inline def ca: CInt = struct._1
        inline def ca_=(value: CInt): Unit = !struct.at1 = value
        inline def pathlen: Ptr[ASN1_INTEGER] = struct._2
        inline def pathlen_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates BASIC_CONSTRAINTS_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[BASIC_CONSTRAINTS_st] =
      scala.scalanative.unsafe.alloc[BASIC_CONSTRAINTS_st](1)
    def apply(ca: CInt, pathlen: Ptr[ASN1_INTEGER])(using Zone): Ptr[BASIC_CONSTRAINTS_st] =
      val ____ptr = apply()
      (!____ptr).ca = ca
      (!____ptr).pathlen = pathlen
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type CERTIFICATEPOLICIES = CStruct0

  object CERTIFICATEPOLICIES:
    given _tag: Tag[CERTIFICATEPOLICIES] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type CRL_DIST_POINTS = CStruct0

  object CRL_DIST_POINTS:
    given _tag: Tag[CRL_DIST_POINTS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type DIST_POINT_NAME = CStruct3[CInt, DIST_POINT_NAME_Name, Ptr[X509_NAME]]

  object DIST_POINT_NAME:
    given _tag: Tag[DIST_POINT_NAME] =
      Tag.materializeCStruct3Tag[CInt, DIST_POINT_NAME_Name, Ptr[X509_NAME]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: DIST_POINT_NAME)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def name: DIST_POINT_NAME_Name = struct._2
        inline def name_=(value: DIST_POINT_NAME_Name): Unit = !struct.at2 = value
        inline def dpname: Ptr[X509_NAME] = struct._3
        inline def dpname_=(value: Ptr[X509_NAME]): Unit = !struct.at3 = value
      end extension

    // Allocates DIST_POINT_NAME on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[DIST_POINT_NAME] =
      scala.scalanative.unsafe.alloc[DIST_POINT_NAME](1)
    def apply(`type`: CInt, name: DIST_POINT_NAME_Name, dpname: Ptr[X509_NAME])(using
        Zone,
    ): Ptr[DIST_POINT_NAME] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).name = name
      (!____ptr).dpname = dpname
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type DIST_POINT_NAME_Name = CArray[Byte, Nat._8]
  object DIST_POINT_NAME_Name:
    given _tag: Tag[DIST_POINT_NAME_Name] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[DIST_POINT_NAME_Name] =
      val ___ptr = scala.scalanative.unsafe.alloc[DIST_POINT_NAME_Name](1)
      ___ptr

    @scala.annotation.targetName("apply_fullname")
    def apply(fullname: Ptr[GENERAL_NAMES])(using Zone): Ptr[DIST_POINT_NAME_Name] =
      val ___ptr = scala.scalanative.unsafe.alloc[DIST_POINT_NAME_Name](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[GENERAL_NAMES]]].update(0, fullname)
      ___ptr

    @scala.annotation.targetName("apply_relativename")
    def apply(relativename: Ptr[stack_st_X509_NAME_ENTRY])(using Zone): Ptr[DIST_POINT_NAME_Name] =
      val ___ptr = scala.scalanative.unsafe.alloc[DIST_POINT_NAME_Name](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[stack_st_X509_NAME_ENTRY]]].update(0, relativename)
      ___ptr

    extension (struct: DIST_POINT_NAME_Name)
      inline def fullname: Ptr[GENERAL_NAMES] = !struct.at(0).asInstanceOf[Ptr[Ptr[GENERAL_NAMES]]]
      inline def fullname_=(value: Ptr[GENERAL_NAMES]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[GENERAL_NAMES]]] = value
      inline def relativename: Ptr[stack_st_X509_NAME_ENTRY] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[stack_st_X509_NAME_ENTRY]]]
      inline def relativename_=(value: Ptr[stack_st_X509_NAME_ENTRY]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[stack_st_X509_NAME_ENTRY]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type DIST_POINT_NAME_st = CStruct3[CInt, DIST_POINT_NAME_st_Name, Ptr[X509_NAME]]

  object DIST_POINT_NAME_st:
    given _tag: Tag[DIST_POINT_NAME_st] =
      Tag.materializeCStruct3Tag[CInt, DIST_POINT_NAME_st_Name, Ptr[X509_NAME]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: DIST_POINT_NAME_st)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def name: DIST_POINT_NAME_st_Name = struct._2
        inline def name_=(value: DIST_POINT_NAME_st_Name): Unit = !struct.at2 = value
        inline def dpname: Ptr[X509_NAME] = struct._3
        inline def dpname_=(value: Ptr[X509_NAME]): Unit = !struct.at3 = value
      end extension

    // Allocates DIST_POINT_NAME_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[DIST_POINT_NAME_st] =
      scala.scalanative.unsafe.alloc[DIST_POINT_NAME_st](1)
    def apply(`type`: CInt, name: DIST_POINT_NAME_st_Name, dpname: Ptr[X509_NAME])(using
        Zone,
    ): Ptr[DIST_POINT_NAME_st] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).name = name
      (!____ptr).dpname = dpname
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type DIST_POINT_NAME_st_Name = CArray[Byte, Nat._8]
  object DIST_POINT_NAME_st_Name:
    given _tag: Tag[DIST_POINT_NAME_st_Name] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[DIST_POINT_NAME_st_Name] =
      val ___ptr = scala.scalanative.unsafe.alloc[DIST_POINT_NAME_st_Name](1)
      ___ptr

    @scala.annotation.targetName("apply_fullname")
    def apply(fullname: Ptr[GENERAL_NAMES])(using Zone): Ptr[DIST_POINT_NAME_st_Name] =
      val ___ptr = scala.scalanative.unsafe.alloc[DIST_POINT_NAME_st_Name](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[GENERAL_NAMES]]].update(0, fullname)
      ___ptr

    @scala.annotation.targetName("apply_relativename")
    def apply(relativename: Ptr[stack_st_X509_NAME_ENTRY])(using
        Zone,
    ): Ptr[DIST_POINT_NAME_st_Name] =
      val ___ptr = scala.scalanative.unsafe.alloc[DIST_POINT_NAME_st_Name](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[stack_st_X509_NAME_ENTRY]]].update(0, relativename)
      ___ptr

    extension (struct: DIST_POINT_NAME_st_Name)
      inline def fullname: Ptr[GENERAL_NAMES] = !struct.at(0).asInstanceOf[Ptr[Ptr[GENERAL_NAMES]]]
      inline def fullname_=(value: Ptr[GENERAL_NAMES]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[GENERAL_NAMES]]] = value
      inline def relativename: Ptr[stack_st_X509_NAME_ENTRY] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[stack_st_X509_NAME_ENTRY]]]
      inline def relativename_=(value: Ptr[stack_st_X509_NAME_ENTRY]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[stack_st_X509_NAME_ENTRY]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type DIST_POINT_st =
    CStruct4[Ptr[DIST_POINT_NAME], Ptr[ASN1_BIT_STRING], Ptr[GENERAL_NAMES], CInt]

  object DIST_POINT_st:
    given _tag: Tag[DIST_POINT_st] = Tag
      .materializeCStruct4Tag[Ptr[DIST_POINT_NAME], Ptr[ASN1_BIT_STRING], Ptr[GENERAL_NAMES], CInt]

    export fields.*
    private[x509v3] object fields:
      extension (struct: DIST_POINT_st)
        inline def distpoint: Ptr[DIST_POINT_NAME] = struct._1
        inline def distpoint_=(value: Ptr[DIST_POINT_NAME]): Unit = !struct.at1 = value
        inline def reasons: Ptr[ASN1_BIT_STRING] = struct._2
        inline def reasons_=(value: Ptr[ASN1_BIT_STRING]): Unit = !struct.at2 = value
        inline def CRLissuer: Ptr[GENERAL_NAMES] = struct._3
        inline def CRLissuer_=(value: Ptr[GENERAL_NAMES]): Unit = !struct.at3 = value
        inline def dp_reasons: CInt = struct._4
        inline def dp_reasons_=(value: CInt): Unit = !struct.at4 = value
      end extension

    // Allocates DIST_POINT_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[DIST_POINT_st] = scala.scalanative.unsafe.alloc[DIST_POINT_st](1)
    def apply(
        distpoint: Ptr[DIST_POINT_NAME],
        reasons: Ptr[ASN1_BIT_STRING],
        CRLissuer: Ptr[GENERAL_NAMES],
        dp_reasons: CInt,
    )(using Zone): Ptr[DIST_POINT_st] =
      val ____ptr = apply()
      (!____ptr).distpoint = distpoint
      (!____ptr).reasons = reasons
      (!____ptr).CRLissuer = CRLissuer
      (!____ptr).dp_reasons = dp_reasons
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type EDIPARTYNAME = CStruct2[Ptr[ASN1_STRING], Ptr[ASN1_STRING]]

  object EDIPARTYNAME:
    given _tag: Tag[EDIPARTYNAME] = Tag.materializeCStruct2Tag[Ptr[ASN1_STRING], Ptr[ASN1_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: EDIPARTYNAME)
        inline def nameAssigner: Ptr[ASN1_STRING] = struct._1
        inline def nameAssigner_=(value: Ptr[ASN1_STRING]): Unit = !struct.at1 = value
        inline def partyName: Ptr[ASN1_STRING] = struct._2
        inline def partyName_=(value: Ptr[ASN1_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates EDIPARTYNAME on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[EDIPARTYNAME] = scala.scalanative.unsafe.alloc[EDIPARTYNAME](1)
    def apply(nameAssigner: Ptr[ASN1_STRING], partyName: Ptr[ASN1_STRING])(using
        Zone,
    ): Ptr[EDIPARTYNAME] =
      val ____ptr = apply()
      (!____ptr).nameAssigner = nameAssigner
      (!____ptr).partyName = partyName
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type EDIPartyName_st = CStruct2[Ptr[ASN1_STRING], Ptr[ASN1_STRING]]

  object EDIPartyName_st:
    given _tag: Tag[EDIPartyName_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_STRING], Ptr[ASN1_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: EDIPartyName_st)
        inline def nameAssigner: Ptr[ASN1_STRING] = struct._1
        inline def nameAssigner_=(value: Ptr[ASN1_STRING]): Unit = !struct.at1 = value
        inline def partyName: Ptr[ASN1_STRING] = struct._2
        inline def partyName_=(value: Ptr[ASN1_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates EDIPartyName_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[EDIPartyName_st] =
      scala.scalanative.unsafe.alloc[EDIPartyName_st](1)
    def apply(nameAssigner: Ptr[ASN1_STRING], partyName: Ptr[ASN1_STRING])(using
        Zone,
    ): Ptr[EDIPartyName_st] =
      val ____ptr = apply()
      (!____ptr).nameAssigner = nameAssigner
      (!____ptr).partyName = partyName
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  opaque type EXTENDED_KEY_USAGE = CStruct0

  object EXTENDED_KEY_USAGE:
    given _tag: Tag[EXTENDED_KEY_USAGE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type GENERAL_NAME = CStruct2[CInt, GENERAL_NAME_D]

  object GENERAL_NAME:
    given _tag: Tag[GENERAL_NAME] = Tag.materializeCStruct2Tag[CInt, GENERAL_NAME_D]

    export fields.*
    private[x509v3] object fields:
      extension (struct: GENERAL_NAME)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def d: GENERAL_NAME_D = struct._2
        inline def d_=(value: GENERAL_NAME_D): Unit = !struct.at2 = value
      end extension

    // Allocates GENERAL_NAME on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[GENERAL_NAME] = scala.scalanative.unsafe.alloc[GENERAL_NAME](1)
    def apply(`type`: CInt, d: GENERAL_NAME_D)(using Zone): Ptr[GENERAL_NAME] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).d = d
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type GENERAL_NAME_D = CArray[Byte, Nat._8]
  object GENERAL_NAME_D:
    given _tag: Tag[GENERAL_NAME_D] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      ___ptr

    @scala.annotation.targetName("apply_ptr")
    def apply(ptr: CString)(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[CString]].update(0, ptr)
      ___ptr

    @scala.annotation.targetName("apply_otherName")
    def apply(otherName: Ptr[OTHERNAME])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[OTHERNAME]]].update(0, otherName)
      ___ptr

    @scala.annotation.targetName("apply_rfc822Name")
    def apply(rfc822Name: Ptr[ASN1_IA5STRING])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, rfc822Name)
      ___ptr

    @scala.annotation.targetName("apply_dNSName")
    def apply(dNSName: Ptr[ASN1_IA5STRING])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, dNSName)
      ___ptr

    @scala.annotation.targetName("apply_x400Address")
    def apply(x400Address: Ptr[ASN1_STRING])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]].update(0, x400Address)
      ___ptr

    @scala.annotation.targetName("apply_directoryName")
    def apply(directoryName: Ptr[X509_NAME])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]].update(0, directoryName)
      ___ptr

    @scala.annotation.targetName("apply_ediPartyName")
    def apply(ediPartyName: Ptr[EDIPARTYNAME])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[EDIPARTYNAME]]].update(0, ediPartyName)
      ___ptr

    @scala.annotation.targetName("apply_uniformResourceIdentifier")
    def apply(uniformResourceIdentifier: Ptr[ASN1_IA5STRING])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, uniformResourceIdentifier)
      ___ptr

    @scala.annotation.targetName("apply_iPAddress")
    def apply(iPAddress: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]].update(0, iPAddress)
      ___ptr

    @scala.annotation.targetName("apply_registeredID")
    def apply(registeredID: Ptr[ASN1_OBJECT])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]].update(0, registeredID)
      ___ptr

    @scala.annotation.targetName("apply_ip")
    def apply(ip: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]].update(0, ip)
      ___ptr

    @scala.annotation.targetName("apply_dirn")
    def apply(dirn: Ptr[X509_NAME])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]].update(0, dirn)
      ___ptr

    @scala.annotation.targetName("apply_ia5")
    def apply(ia5: Ptr[ASN1_IA5STRING])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, ia5)
      ___ptr

    @scala.annotation.targetName("apply_rid")
    def apply(rid: Ptr[ASN1_OBJECT])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]].update(0, rid)
      ___ptr

    @scala.annotation.targetName("apply_other")
    def apply(other: Ptr[ASN1_TYPE])(using Zone): Ptr[GENERAL_NAME_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]].update(0, other)
      ___ptr

    extension (struct: GENERAL_NAME_D)
      inline def ptr: CString = !struct.at(0).asInstanceOf[Ptr[CString]]
      inline def ptr_=(value: CString): Unit = !struct.at(0).asInstanceOf[Ptr[CString]] = value
      inline def otherName: Ptr[OTHERNAME] = !struct.at(0).asInstanceOf[Ptr[Ptr[OTHERNAME]]]
      inline def otherName_=(value: Ptr[OTHERNAME]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[OTHERNAME]]] = value
      inline def rfc822Name: Ptr[ASN1_IA5STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def rfc822Name_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def dNSName: Ptr[ASN1_IA5STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def dNSName_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def x400Address: Ptr[ASN1_STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]]
      inline def x400Address_=(value: Ptr[ASN1_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]] = value
      inline def directoryName: Ptr[X509_NAME] = !struct.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]]
      inline def directoryName_=(value: Ptr[X509_NAME]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]] = value
      inline def ediPartyName: Ptr[EDIPARTYNAME] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[EDIPARTYNAME]]]
      inline def ediPartyName_=(value: Ptr[EDIPARTYNAME]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[EDIPARTYNAME]]] = value
      inline def uniformResourceIdentifier: Ptr[ASN1_IA5STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def uniformResourceIdentifier_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def iPAddress: Ptr[ASN1_OCTET_STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]]
      inline def iPAddress_=(value: Ptr[ASN1_OCTET_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]] = value
      inline def registeredID: Ptr[ASN1_OBJECT] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]]
      inline def registeredID_=(value: Ptr[ASN1_OBJECT]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]] = value
      inline def ip: Ptr[ASN1_OCTET_STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]]
      inline def ip_=(value: Ptr[ASN1_OCTET_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]] = value
      inline def dirn: Ptr[X509_NAME] = !struct.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]]
      inline def dirn_=(value: Ptr[X509_NAME]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]] = value
      inline def ia5: Ptr[ASN1_IA5STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def ia5_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def rid: Ptr[ASN1_OBJECT] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]]
      inline def rid_=(value: Ptr[ASN1_OBJECT]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]] = value
      inline def other: Ptr[ASN1_TYPE] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]]
      inline def other_=(value: Ptr[ASN1_TYPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type GENERAL_NAMES = CStruct0

  object GENERAL_NAMES:
    given _tag: Tag[GENERAL_NAMES] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type GENERAL_NAME_st = CStruct2[CInt, GENERAL_NAME_st_D]

  object GENERAL_NAME_st:
    given _tag: Tag[GENERAL_NAME_st] = Tag.materializeCStruct2Tag[CInt, GENERAL_NAME_st_D]

    export fields.*
    private[x509v3] object fields:
      extension (struct: GENERAL_NAME_st)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def d: GENERAL_NAME_st_D = struct._2
        inline def d_=(value: GENERAL_NAME_st_D): Unit = !struct.at2 = value
      end extension

    // Allocates GENERAL_NAME_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[GENERAL_NAME_st] =
      scala.scalanative.unsafe.alloc[GENERAL_NAME_st](1)
    def apply(`type`: CInt, d: GENERAL_NAME_st_D)(using Zone): Ptr[GENERAL_NAME_st] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).d = d
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type GENERAL_NAME_st_D = CArray[Byte, Nat._8]
  object GENERAL_NAME_st_D:
    given _tag: Tag[GENERAL_NAME_st_D] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      ___ptr

    @scala.annotation.targetName("apply_ptr")
    def apply(ptr: CString)(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[CString]].update(0, ptr)
      ___ptr

    @scala.annotation.targetName("apply_otherName")
    def apply(otherName: Ptr[OTHERNAME])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[OTHERNAME]]].update(0, otherName)
      ___ptr

    @scala.annotation.targetName("apply_rfc822Name")
    def apply(rfc822Name: Ptr[ASN1_IA5STRING])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, rfc822Name)
      ___ptr

    @scala.annotation.targetName("apply_dNSName")
    def apply(dNSName: Ptr[ASN1_IA5STRING])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, dNSName)
      ___ptr

    @scala.annotation.targetName("apply_x400Address")
    def apply(x400Address: Ptr[ASN1_STRING])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]].update(0, x400Address)
      ___ptr

    @scala.annotation.targetName("apply_directoryName")
    def apply(directoryName: Ptr[X509_NAME])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]].update(0, directoryName)
      ___ptr

    @scala.annotation.targetName("apply_ediPartyName")
    def apply(ediPartyName: Ptr[EDIPARTYNAME])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[EDIPARTYNAME]]].update(0, ediPartyName)
      ___ptr

    @scala.annotation.targetName("apply_uniformResourceIdentifier")
    def apply(uniformResourceIdentifier: Ptr[ASN1_IA5STRING])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, uniformResourceIdentifier)
      ___ptr

    @scala.annotation.targetName("apply_iPAddress")
    def apply(iPAddress: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]].update(0, iPAddress)
      ___ptr

    @scala.annotation.targetName("apply_registeredID")
    def apply(registeredID: Ptr[ASN1_OBJECT])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]].update(0, registeredID)
      ___ptr

    @scala.annotation.targetName("apply_ip")
    def apply(ip: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]].update(0, ip)
      ___ptr

    @scala.annotation.targetName("apply_dirn")
    def apply(dirn: Ptr[X509_NAME])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]].update(0, dirn)
      ___ptr

    @scala.annotation.targetName("apply_ia5")
    def apply(ia5: Ptr[ASN1_IA5STRING])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, ia5)
      ___ptr

    @scala.annotation.targetName("apply_rid")
    def apply(rid: Ptr[ASN1_OBJECT])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]].update(0, rid)
      ___ptr

    @scala.annotation.targetName("apply_other")
    def apply(other: Ptr[ASN1_TYPE])(using Zone): Ptr[GENERAL_NAME_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[GENERAL_NAME_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]].update(0, other)
      ___ptr

    extension (struct: GENERAL_NAME_st_D)
      inline def ptr: CString = !struct.at(0).asInstanceOf[Ptr[CString]]
      inline def ptr_=(value: CString): Unit = !struct.at(0).asInstanceOf[Ptr[CString]] = value
      inline def otherName: Ptr[OTHERNAME] = !struct.at(0).asInstanceOf[Ptr[Ptr[OTHERNAME]]]
      inline def otherName_=(value: Ptr[OTHERNAME]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[OTHERNAME]]] = value
      inline def rfc822Name: Ptr[ASN1_IA5STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def rfc822Name_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def dNSName: Ptr[ASN1_IA5STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def dNSName_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def x400Address: Ptr[ASN1_STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]]
      inline def x400Address_=(value: Ptr[ASN1_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]] = value
      inline def directoryName: Ptr[X509_NAME] = !struct.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]]
      inline def directoryName_=(value: Ptr[X509_NAME]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]] = value
      inline def ediPartyName: Ptr[EDIPARTYNAME] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[EDIPARTYNAME]]]
      inline def ediPartyName_=(value: Ptr[EDIPARTYNAME]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[EDIPARTYNAME]]] = value
      inline def uniformResourceIdentifier: Ptr[ASN1_IA5STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def uniformResourceIdentifier_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def iPAddress: Ptr[ASN1_OCTET_STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]]
      inline def iPAddress_=(value: Ptr[ASN1_OCTET_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]] = value
      inline def registeredID: Ptr[ASN1_OBJECT] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]]
      inline def registeredID_=(value: Ptr[ASN1_OBJECT]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]] = value
      inline def ip: Ptr[ASN1_OCTET_STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]]
      inline def ip_=(value: Ptr[ASN1_OCTET_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]] = value
      inline def dirn: Ptr[X509_NAME] = !struct.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]]
      inline def dirn_=(value: Ptr[X509_NAME]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[X509_NAME]]] = value
      inline def ia5: Ptr[ASN1_IA5STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def ia5_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def rid: Ptr[ASN1_OBJECT] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]]
      inline def rid_=(value: Ptr[ASN1_OBJECT]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]] = value
      inline def other: Ptr[ASN1_TYPE] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]]
      inline def other_=(value: Ptr[ASN1_TYPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type GENERAL_SUBTREE = CStruct3[Ptr[GENERAL_NAME], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

  object GENERAL_SUBTREE:
    given _tag: Tag[GENERAL_SUBTREE] =
      Tag.materializeCStruct3Tag[Ptr[GENERAL_NAME], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: GENERAL_SUBTREE)
        inline def base: Ptr[GENERAL_NAME] = struct._1
        inline def base_=(value: Ptr[GENERAL_NAME]): Unit = !struct.at1 = value
        inline def minimum: Ptr[ASN1_INTEGER] = struct._2
        inline def minimum_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
        inline def maximum: Ptr[ASN1_INTEGER] = struct._3
        inline def maximum_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at3 = value
      end extension

    // Allocates GENERAL_SUBTREE on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[GENERAL_SUBTREE] =
      scala.scalanative.unsafe.alloc[GENERAL_SUBTREE](1)
    def apply(base: Ptr[GENERAL_NAME], minimum: Ptr[ASN1_INTEGER], maximum: Ptr[ASN1_INTEGER])(using
        Zone,
    ): Ptr[GENERAL_SUBTREE] =
      val ____ptr = apply()
      (!____ptr).base = base
      (!____ptr).minimum = minimum
      (!____ptr).maximum = maximum
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type GENERAL_SUBTREE_st = CStruct3[Ptr[GENERAL_NAME], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

  object GENERAL_SUBTREE_st:
    given _tag: Tag[GENERAL_SUBTREE_st] =
      Tag.materializeCStruct3Tag[Ptr[GENERAL_NAME], Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: GENERAL_SUBTREE_st)
        inline def base: Ptr[GENERAL_NAME] = struct._1
        inline def base_=(value: Ptr[GENERAL_NAME]): Unit = !struct.at1 = value
        inline def minimum: Ptr[ASN1_INTEGER] = struct._2
        inline def minimum_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
        inline def maximum: Ptr[ASN1_INTEGER] = struct._3
        inline def maximum_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at3 = value
      end extension

    // Allocates GENERAL_SUBTREE_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[GENERAL_SUBTREE_st] =
      scala.scalanative.unsafe.alloc[GENERAL_SUBTREE_st](1)
    def apply(base: Ptr[GENERAL_NAME], minimum: Ptr[ASN1_INTEGER], maximum: Ptr[ASN1_INTEGER])(using
        Zone,
    ): Ptr[GENERAL_SUBTREE_st] =
      val ____ptr = apply()
      (!____ptr).base = base
      (!____ptr).minimum = minimum
      (!____ptr).maximum = maximum
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddrBlocks = CStruct0

  object IPAddrBlocks:
    given _tag: Tag[IPAddrBlocks] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressChoice = CStruct2[CInt, IPAddressChoice_U]

  object IPAddressChoice:
    given _tag: Tag[IPAddressChoice] = Tag.materializeCStruct2Tag[CInt, IPAddressChoice_U]

    export fields.*
    private[x509v3] object fields:
      extension (struct: IPAddressChoice)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def u: IPAddressChoice_U = struct._2
        inline def u_=(value: IPAddressChoice_U): Unit = !struct.at2 = value
      end extension

    // Allocates IPAddressChoice on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[IPAddressChoice] =
      scala.scalanative.unsafe.alloc[IPAddressChoice](1)
    def apply(`type`: CInt, u: IPAddressChoice_U)(using Zone): Ptr[IPAddressChoice] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).u = u
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressChoice_U = CArray[Byte, Nat._8]
  object IPAddressChoice_U:
    given _tag: Tag[IPAddressChoice_U] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[IPAddressChoice_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressChoice_U](1)
      ___ptr

    @scala.annotation.targetName("apply_inherit")
    def apply(inherit: Ptr[ASN1_NULL])(using Zone): Ptr[IPAddressChoice_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressChoice_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]].update(0, inherit)
      ___ptr

    @scala.annotation.targetName("apply_addressesOrRanges")
    def apply(addressesOrRanges: Ptr[IPAddressOrRanges])(using Zone): Ptr[IPAddressChoice_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressChoice_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[IPAddressOrRanges]]].update(0, addressesOrRanges)
      ___ptr

    extension (struct: IPAddressChoice_U)
      inline def inherit: Ptr[ASN1_NULL] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]]
      inline def inherit_=(value: Ptr[ASN1_NULL]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]] = value
      inline def addressesOrRanges: Ptr[IPAddressOrRanges] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[IPAddressOrRanges]]]
      inline def addressesOrRanges_=(value: Ptr[IPAddressOrRanges]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[IPAddressOrRanges]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressChoice_st = CStruct2[CInt, IPAddressChoice_st_U]

  object IPAddressChoice_st:
    given _tag: Tag[IPAddressChoice_st] = Tag.materializeCStruct2Tag[CInt, IPAddressChoice_st_U]

    export fields.*
    private[x509v3] object fields:
      extension (struct: IPAddressChoice_st)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def u: IPAddressChoice_st_U = struct._2
        inline def u_=(value: IPAddressChoice_st_U): Unit = !struct.at2 = value
      end extension

    // Allocates IPAddressChoice_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[IPAddressChoice_st] =
      scala.scalanative.unsafe.alloc[IPAddressChoice_st](1)
    def apply(`type`: CInt, u: IPAddressChoice_st_U)(using Zone): Ptr[IPAddressChoice_st] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).u = u
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressChoice_st_U = CArray[Byte, Nat._8]
  object IPAddressChoice_st_U:
    given _tag: Tag[IPAddressChoice_st_U] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[IPAddressChoice_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressChoice_st_U](1)
      ___ptr

    @scala.annotation.targetName("apply_inherit")
    def apply(inherit: Ptr[ASN1_NULL])(using Zone): Ptr[IPAddressChoice_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressChoice_st_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]].update(0, inherit)
      ___ptr

    @scala.annotation.targetName("apply_addressesOrRanges")
    def apply(addressesOrRanges: Ptr[IPAddressOrRanges])(using Zone): Ptr[IPAddressChoice_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressChoice_st_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[IPAddressOrRanges]]].update(0, addressesOrRanges)
      ___ptr

    extension (struct: IPAddressChoice_st_U)
      inline def inherit: Ptr[ASN1_NULL] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]]
      inline def inherit_=(value: Ptr[ASN1_NULL]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_NULL]]] = value
      inline def addressesOrRanges: Ptr[IPAddressOrRanges] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[IPAddressOrRanges]]]
      inline def addressesOrRanges_=(value: Ptr[IPAddressOrRanges]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[IPAddressOrRanges]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressFamily = CStruct2[Ptr[ASN1_OCTET_STRING], Ptr[IPAddressChoice]]

  object IPAddressFamily:
    given _tag: Tag[IPAddressFamily] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OCTET_STRING], Ptr[IPAddressChoice]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: IPAddressFamily)
        inline def addressFamily: Ptr[ASN1_OCTET_STRING] = struct._1
        inline def addressFamily_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at1 = value
        inline def ipAddressChoice: Ptr[IPAddressChoice] = struct._2
        inline def ipAddressChoice_=(value: Ptr[IPAddressChoice]): Unit = !struct.at2 = value
      end extension

    // Allocates IPAddressFamily on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[IPAddressFamily] =
      scala.scalanative.unsafe.alloc[IPAddressFamily](1)
    def apply(addressFamily: Ptr[ASN1_OCTET_STRING], ipAddressChoice: Ptr[IPAddressChoice])(using
        Zone,
    ): Ptr[IPAddressFamily] =
      val ____ptr = apply()
      (!____ptr).addressFamily = addressFamily
      (!____ptr).ipAddressChoice = ipAddressChoice
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressFamily_st = CStruct2[Ptr[ASN1_OCTET_STRING], Ptr[IPAddressChoice]]

  object IPAddressFamily_st:
    given _tag: Tag[IPAddressFamily_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OCTET_STRING], Ptr[IPAddressChoice]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: IPAddressFamily_st)
        inline def addressFamily: Ptr[ASN1_OCTET_STRING] = struct._1
        inline def addressFamily_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at1 = value
        inline def ipAddressChoice: Ptr[IPAddressChoice] = struct._2
        inline def ipAddressChoice_=(value: Ptr[IPAddressChoice]): Unit = !struct.at2 = value
      end extension

    // Allocates IPAddressFamily_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[IPAddressFamily_st] =
      scala.scalanative.unsafe.alloc[IPAddressFamily_st](1)
    def apply(addressFamily: Ptr[ASN1_OCTET_STRING], ipAddressChoice: Ptr[IPAddressChoice])(using
        Zone,
    ): Ptr[IPAddressFamily_st] =
      val ____ptr = apply()
      (!____ptr).addressFamily = addressFamily
      (!____ptr).ipAddressChoice = ipAddressChoice
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressOrRange = CStruct2[CInt, IPAddressOrRange_U]

  object IPAddressOrRange:
    given _tag: Tag[IPAddressOrRange] = Tag.materializeCStruct2Tag[CInt, IPAddressOrRange_U]

    export fields.*
    private[x509v3] object fields:
      extension (struct: IPAddressOrRange)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def u: IPAddressOrRange_U = struct._2
        inline def u_=(value: IPAddressOrRange_U): Unit = !struct.at2 = value
      end extension

    // Allocates IPAddressOrRange on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[IPAddressOrRange] =
      scala.scalanative.unsafe.alloc[IPAddressOrRange](1)
    def apply(`type`: CInt, u: IPAddressOrRange_U)(using Zone): Ptr[IPAddressOrRange] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).u = u
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressOrRange_U = CArray[Byte, Nat._8]
  object IPAddressOrRange_U:
    given _tag: Tag[IPAddressOrRange_U] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[IPAddressOrRange_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressOrRange_U](1)
      ___ptr

    @scala.annotation.targetName("apply_addressPrefix")
    def apply(addressPrefix: Ptr[ASN1_BIT_STRING])(using Zone): Ptr[IPAddressOrRange_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressOrRange_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]].update(0, addressPrefix)
      ___ptr

    @scala.annotation.targetName("apply_addressRange")
    def apply(addressRange: Ptr[IPAddressRange])(using Zone): Ptr[IPAddressOrRange_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressOrRange_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[IPAddressRange]]].update(0, addressRange)
      ___ptr

    extension (struct: IPAddressOrRange_U)
      inline def addressPrefix: Ptr[ASN1_BIT_STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]]
      inline def addressPrefix_=(value: Ptr[ASN1_BIT_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]] = value
      inline def addressRange: Ptr[IPAddressRange] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[IPAddressRange]]]
      inline def addressRange_=(value: Ptr[IPAddressRange]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[IPAddressRange]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressOrRange_st = CStruct2[CInt, IPAddressOrRange_st_U]

  object IPAddressOrRange_st:
    given _tag: Tag[IPAddressOrRange_st] = Tag.materializeCStruct2Tag[CInt, IPAddressOrRange_st_U]

    export fields.*
    private[x509v3] object fields:
      extension (struct: IPAddressOrRange_st)
        inline def `type`: CInt = struct._1
        inline def type_=(value: CInt): Unit = !struct.at1 = value
        inline def u: IPAddressOrRange_st_U = struct._2
        inline def u_=(value: IPAddressOrRange_st_U): Unit = !struct.at2 = value
      end extension

    // Allocates IPAddressOrRange_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[IPAddressOrRange_st] =
      scala.scalanative.unsafe.alloc[IPAddressOrRange_st](1)
    def apply(`type`: CInt, u: IPAddressOrRange_st_U)(using Zone): Ptr[IPAddressOrRange_st] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).u = u
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressOrRange_st_U = CArray[Byte, Nat._8]
  object IPAddressOrRange_st_U:
    given _tag: Tag[IPAddressOrRange_st_U] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[IPAddressOrRange_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressOrRange_st_U](1)
      ___ptr

    @scala.annotation.targetName("apply_addressPrefix")
    def apply(addressPrefix: Ptr[ASN1_BIT_STRING])(using Zone): Ptr[IPAddressOrRange_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressOrRange_st_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]].update(0, addressPrefix)
      ___ptr

    @scala.annotation.targetName("apply_addressRange")
    def apply(addressRange: Ptr[IPAddressRange])(using Zone): Ptr[IPAddressOrRange_st_U] =
      val ___ptr = scala.scalanative.unsafe.alloc[IPAddressOrRange_st_U](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[IPAddressRange]]].update(0, addressRange)
      ___ptr

    extension (struct: IPAddressOrRange_st_U)
      inline def addressPrefix: Ptr[ASN1_BIT_STRING] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]]
      inline def addressPrefix_=(value: Ptr[ASN1_BIT_STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]] = value
      inline def addressRange: Ptr[IPAddressRange] =
        !struct.at(0).asInstanceOf[Ptr[Ptr[IPAddressRange]]]
      inline def addressRange_=(value: Ptr[IPAddressRange]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[IPAddressRange]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressOrRanges = CStruct0

  object IPAddressOrRanges:
    given _tag: Tag[IPAddressOrRanges] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressRange = CStruct2[Ptr[ASN1_BIT_STRING], Ptr[ASN1_BIT_STRING]]

  object IPAddressRange:
    given _tag: Tag[IPAddressRange] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_BIT_STRING], Ptr[ASN1_BIT_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: IPAddressRange)
        inline def min: Ptr[ASN1_BIT_STRING] = struct._1
        inline def min_=(value: Ptr[ASN1_BIT_STRING]): Unit = !struct.at1 = value
        inline def max: Ptr[ASN1_BIT_STRING] = struct._2
        inline def max_=(value: Ptr[ASN1_BIT_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates IPAddressRange on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[IPAddressRange] = scala.scalanative.unsafe.alloc[IPAddressRange](1)
    def apply(min: Ptr[ASN1_BIT_STRING], max: Ptr[ASN1_BIT_STRING])(using
        Zone,
    ): Ptr[IPAddressRange] =
      val ____ptr = apply()
      (!____ptr).min = min
      (!____ptr).max = max
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type IPAddressRange_st = CStruct2[Ptr[ASN1_BIT_STRING], Ptr[ASN1_BIT_STRING]]

  object IPAddressRange_st:
    given _tag: Tag[IPAddressRange_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_BIT_STRING], Ptr[ASN1_BIT_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: IPAddressRange_st)
        inline def min: Ptr[ASN1_BIT_STRING] = struct._1
        inline def min_=(value: Ptr[ASN1_BIT_STRING]): Unit = !struct.at1 = value
        inline def max: Ptr[ASN1_BIT_STRING] = struct._2
        inline def max_=(value: Ptr[ASN1_BIT_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates IPAddressRange_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[IPAddressRange_st] =
      scala.scalanative.unsafe.alloc[IPAddressRange_st](1)
    def apply(min: Ptr[ASN1_BIT_STRING], max: Ptr[ASN1_BIT_STRING])(using
        Zone,
    ): Ptr[IPAddressRange_st] =
      val ____ptr = apply()
      (!____ptr).min = min
      (!____ptr).max = max
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ISSUER_SIGN_TOOL =
    CStruct4[Ptr[ASN1_UTF8STRING], Ptr[ASN1_UTF8STRING], Ptr[ASN1_UTF8STRING], Ptr[ASN1_UTF8STRING]]

  object ISSUER_SIGN_TOOL:
    given _tag: Tag[ISSUER_SIGN_TOOL] = Tag.materializeCStruct4Tag[Ptr[ASN1_UTF8STRING], Ptr[
      ASN1_UTF8STRING,
    ], Ptr[ASN1_UTF8STRING], Ptr[ASN1_UTF8STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ISSUER_SIGN_TOOL)
        inline def signTool: Ptr[ASN1_UTF8STRING] = struct._1
        inline def signTool_=(value: Ptr[ASN1_UTF8STRING]): Unit = !struct.at1 = value
        inline def cATool: Ptr[ASN1_UTF8STRING] = struct._2
        inline def cATool_=(value: Ptr[ASN1_UTF8STRING]): Unit = !struct.at2 = value
        inline def signToolCert: Ptr[ASN1_UTF8STRING] = struct._3
        inline def signToolCert_=(value: Ptr[ASN1_UTF8STRING]): Unit = !struct.at3 = value
        inline def cAToolCert: Ptr[ASN1_UTF8STRING] = struct._4
        inline def cAToolCert_=(value: Ptr[ASN1_UTF8STRING]): Unit = !struct.at4 = value
      end extension

    // Allocates ISSUER_SIGN_TOOL on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ISSUER_SIGN_TOOL] =
      scala.scalanative.unsafe.alloc[ISSUER_SIGN_TOOL](1)
    def apply(
        signTool: Ptr[ASN1_UTF8STRING],
        cATool: Ptr[ASN1_UTF8STRING],
        signToolCert: Ptr[ASN1_UTF8STRING],
        cAToolCert: Ptr[ASN1_UTF8STRING],
    )(using Zone): Ptr[ISSUER_SIGN_TOOL] =
      val ____ptr = apply()
      (!____ptr).signTool = signTool
      (!____ptr).cATool = cATool
      (!____ptr).signToolCert = signToolCert
      (!____ptr).cAToolCert = cAToolCert
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ISSUER_SIGN_TOOL_st =
    CStruct4[Ptr[ASN1_UTF8STRING], Ptr[ASN1_UTF8STRING], Ptr[ASN1_UTF8STRING], Ptr[ASN1_UTF8STRING]]

  object ISSUER_SIGN_TOOL_st:
    given _tag: Tag[ISSUER_SIGN_TOOL_st] = Tag.materializeCStruct4Tag[Ptr[ASN1_UTF8STRING], Ptr[
      ASN1_UTF8STRING,
    ], Ptr[ASN1_UTF8STRING], Ptr[ASN1_UTF8STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ISSUER_SIGN_TOOL_st)
        inline def signTool: Ptr[ASN1_UTF8STRING] = struct._1
        inline def signTool_=(value: Ptr[ASN1_UTF8STRING]): Unit = !struct.at1 = value
        inline def cATool: Ptr[ASN1_UTF8STRING] = struct._2
        inline def cATool_=(value: Ptr[ASN1_UTF8STRING]): Unit = !struct.at2 = value
        inline def signToolCert: Ptr[ASN1_UTF8STRING] = struct._3
        inline def signToolCert_=(value: Ptr[ASN1_UTF8STRING]): Unit = !struct.at3 = value
        inline def cAToolCert: Ptr[ASN1_UTF8STRING] = struct._4
        inline def cAToolCert_=(value: Ptr[ASN1_UTF8STRING]): Unit = !struct.at4 = value
      end extension

    // Allocates ISSUER_SIGN_TOOL_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ISSUER_SIGN_TOOL_st] =
      scala.scalanative.unsafe.alloc[ISSUER_SIGN_TOOL_st](1)
    def apply(
        signTool: Ptr[ASN1_UTF8STRING],
        cATool: Ptr[ASN1_UTF8STRING],
        signToolCert: Ptr[ASN1_UTF8STRING],
        cAToolCert: Ptr[ASN1_UTF8STRING],
    )(using Zone): Ptr[ISSUER_SIGN_TOOL_st] =
      val ____ptr = apply()
      (!____ptr).signTool = signTool
      (!____ptr).cATool = cATool
      (!____ptr).signToolCert = signToolCert
      (!____ptr).cAToolCert = cAToolCert
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ISSUING_DIST_POINT_st =
    CStruct6[Ptr[DIST_POINT_NAME], CInt, CInt, Ptr[ASN1_BIT_STRING], CInt, CInt]

  object ISSUING_DIST_POINT_st:
    given _tag: Tag[ISSUING_DIST_POINT_st] =
      Tag.materializeCStruct6Tag[Ptr[DIST_POINT_NAME], CInt, CInt, Ptr[ASN1_BIT_STRING], CInt, CInt]

    export fields.*
    private[x509v3] object fields:
      extension (struct: ISSUING_DIST_POINT_st)
        inline def distpoint: Ptr[DIST_POINT_NAME] = struct._1
        inline def distpoint_=(value: Ptr[DIST_POINT_NAME]): Unit = !struct.at1 = value
        inline def onlyuser: CInt = struct._2
        inline def onlyuser_=(value: CInt): Unit = !struct.at2 = value
        inline def onlyCA: CInt = struct._3
        inline def onlyCA_=(value: CInt): Unit = !struct.at3 = value
        inline def onlysomereasons: Ptr[ASN1_BIT_STRING] = struct._4
        inline def onlysomereasons_=(value: Ptr[ASN1_BIT_STRING]): Unit = !struct.at4 = value
        inline def indirectCRL: CInt = struct._5
        inline def indirectCRL_=(value: CInt): Unit = !struct.at5 = value
        inline def onlyattr: CInt = struct._6
        inline def onlyattr_=(value: CInt): Unit = !struct.at6 = value
      end extension

    // Allocates ISSUING_DIST_POINT_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[ISSUING_DIST_POINT_st] =
      scala.scalanative.unsafe.alloc[ISSUING_DIST_POINT_st](1)
    def apply(
        distpoint: Ptr[DIST_POINT_NAME],
        onlyuser: CInt,
        onlyCA: CInt,
        onlysomereasons: Ptr[ASN1_BIT_STRING],
        indirectCRL: CInt,
        onlyattr: CInt,
    )(using Zone): Ptr[ISSUING_DIST_POINT_st] =
      val ____ptr = apply()
      (!____ptr).distpoint = distpoint
      (!____ptr).onlyuser = onlyuser
      (!____ptr).onlyCA = onlyCA
      (!____ptr).onlysomereasons = onlysomereasons
      (!____ptr).indirectCRL = indirectCRL
      (!____ptr).onlyattr = onlyattr
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type NAME_CONSTRAINTS =
    CStruct2[Ptr[stack_st_GENERAL_SUBTREE], Ptr[stack_st_GENERAL_SUBTREE]]

  object NAME_CONSTRAINTS:
    given _tag: Tag[NAME_CONSTRAINTS] =
      Tag.materializeCStruct2Tag[Ptr[stack_st_GENERAL_SUBTREE], Ptr[stack_st_GENERAL_SUBTREE]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: NAME_CONSTRAINTS)
        inline def permittedSubtrees: Ptr[stack_st_GENERAL_SUBTREE] = struct._1
        inline def permittedSubtrees_=(value: Ptr[stack_st_GENERAL_SUBTREE]): Unit = !struct.at1 =
          value
        inline def excludedSubtrees: Ptr[stack_st_GENERAL_SUBTREE] = struct._2
        inline def excludedSubtrees_=(value: Ptr[stack_st_GENERAL_SUBTREE]): Unit = !struct.at2 =
          value
      end extension

    // Allocates NAME_CONSTRAINTS on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[NAME_CONSTRAINTS] =
      scala.scalanative.unsafe.alloc[NAME_CONSTRAINTS](1)
    def apply(
        permittedSubtrees: Ptr[stack_st_GENERAL_SUBTREE],
        excludedSubtrees: Ptr[stack_st_GENERAL_SUBTREE],
    )(using Zone): Ptr[NAME_CONSTRAINTS] =
      val ____ptr = apply()
      (!____ptr).permittedSubtrees = permittedSubtrees
      (!____ptr).excludedSubtrees = excludedSubtrees
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type NAME_CONSTRAINTS_st =
    CStruct2[Ptr[stack_st_GENERAL_SUBTREE], Ptr[stack_st_GENERAL_SUBTREE]]

  object NAME_CONSTRAINTS_st:
    given _tag: Tag[NAME_CONSTRAINTS_st] =
      Tag.materializeCStruct2Tag[Ptr[stack_st_GENERAL_SUBTREE], Ptr[stack_st_GENERAL_SUBTREE]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: NAME_CONSTRAINTS_st)
        inline def permittedSubtrees: Ptr[stack_st_GENERAL_SUBTREE] = struct._1
        inline def permittedSubtrees_=(value: Ptr[stack_st_GENERAL_SUBTREE]): Unit = !struct.at1 =
          value
        inline def excludedSubtrees: Ptr[stack_st_GENERAL_SUBTREE] = struct._2
        inline def excludedSubtrees_=(value: Ptr[stack_st_GENERAL_SUBTREE]): Unit = !struct.at2 =
          value
      end extension

    // Allocates NAME_CONSTRAINTS_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[NAME_CONSTRAINTS_st] =
      scala.scalanative.unsafe.alloc[NAME_CONSTRAINTS_st](1)
    def apply(
        permittedSubtrees: Ptr[stack_st_GENERAL_SUBTREE],
        excludedSubtrees: Ptr[stack_st_GENERAL_SUBTREE],
    )(using Zone): Ptr[NAME_CONSTRAINTS_st] =
      val ____ptr = apply()
      (!____ptr).permittedSubtrees = permittedSubtrees
      (!____ptr).excludedSubtrees = excludedSubtrees
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type NAMING_AUTHORITY = CStruct0

  object NAMING_AUTHORITY:
    given _tag: Tag[NAMING_AUTHORITY] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type NOTICEREF = CStruct2[Ptr[ASN1_STRING], Ptr[stack_st_ASN1_INTEGER]]

  object NOTICEREF:
    given _tag: Tag[NOTICEREF] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_STRING], Ptr[stack_st_ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: NOTICEREF)
        inline def organization: Ptr[ASN1_STRING] = struct._1
        inline def organization_=(value: Ptr[ASN1_STRING]): Unit = !struct.at1 = value
        inline def noticenos: Ptr[stack_st_ASN1_INTEGER] = struct._2
        inline def noticenos_=(value: Ptr[stack_st_ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates NOTICEREF on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[NOTICEREF] = scala.scalanative.unsafe.alloc[NOTICEREF](1)
    def apply(organization: Ptr[ASN1_STRING], noticenos: Ptr[stack_st_ASN1_INTEGER])(using
        Zone,
    ): Ptr[NOTICEREF] =
      val ____ptr = apply()
      (!____ptr).organization = organization
      (!____ptr).noticenos = noticenos
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type NOTICEREF_st = CStruct2[Ptr[ASN1_STRING], Ptr[stack_st_ASN1_INTEGER]]

  object NOTICEREF_st:
    given _tag: Tag[NOTICEREF_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_STRING], Ptr[stack_st_ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: NOTICEREF_st)
        inline def organization: Ptr[ASN1_STRING] = struct._1
        inline def organization_=(value: Ptr[ASN1_STRING]): Unit = !struct.at1 = value
        inline def noticenos: Ptr[stack_st_ASN1_INTEGER] = struct._2
        inline def noticenos_=(value: Ptr[stack_st_ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates NOTICEREF_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[NOTICEREF_st] = scala.scalanative.unsafe.alloc[NOTICEREF_st](1)
    def apply(organization: Ptr[ASN1_STRING], noticenos: Ptr[stack_st_ASN1_INTEGER])(using
        Zone,
    ): Ptr[NOTICEREF_st] =
      val ____ptr = apply()
      (!____ptr).organization = organization
      (!____ptr).noticenos = noticenos
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type NamingAuthority_st = CStruct0

  object NamingAuthority_st:
    given _tag: Tag[NamingAuthority_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type OTHERNAME = CStruct2[Ptr[ASN1_OBJECT], Ptr[ASN1_TYPE]]

  object OTHERNAME:
    given _tag: Tag[OTHERNAME] = Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[ASN1_TYPE]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: OTHERNAME)
        inline def type_id: Ptr[ASN1_OBJECT] = struct._1
        inline def type_id_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def value: Ptr[ASN1_TYPE] = struct._2
        inline def value_=(value: Ptr[ASN1_TYPE]): Unit = !struct.at2 = value
      end extension

    // Allocates OTHERNAME on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[OTHERNAME] = scala.scalanative.unsafe.alloc[OTHERNAME](1)
    def apply(type_id: Ptr[ASN1_OBJECT], value: Ptr[ASN1_TYPE])(using Zone): Ptr[OTHERNAME] =
      val ____ptr = apply()
      (!____ptr).type_id = type_id
      (!____ptr).value = value
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type PKEY_USAGE_PERIOD = CStruct2[Ptr[ASN1_GENERALIZEDTIME], Ptr[ASN1_GENERALIZEDTIME]]

  object PKEY_USAGE_PERIOD:
    given _tag: Tag[PKEY_USAGE_PERIOD] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_GENERALIZEDTIME], Ptr[ASN1_GENERALIZEDTIME]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: PKEY_USAGE_PERIOD)
        inline def notBefore: Ptr[ASN1_GENERALIZEDTIME] = struct._1
        inline def notBefore_=(value: Ptr[ASN1_GENERALIZEDTIME]): Unit = !struct.at1 = value
        inline def notAfter: Ptr[ASN1_GENERALIZEDTIME] = struct._2
        inline def notAfter_=(value: Ptr[ASN1_GENERALIZEDTIME]): Unit = !struct.at2 = value
      end extension

    // Allocates PKEY_USAGE_PERIOD on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKEY_USAGE_PERIOD] =
      scala.scalanative.unsafe.alloc[PKEY_USAGE_PERIOD](1)
    def apply(notBefore: Ptr[ASN1_GENERALIZEDTIME], notAfter: Ptr[ASN1_GENERALIZEDTIME])(using
        Zone,
    ): Ptr[PKEY_USAGE_PERIOD] =
      val ____ptr = apply()
      (!____ptr).notBefore = notBefore
      (!____ptr).notAfter = notAfter
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type PKEY_USAGE_PERIOD_st = CStruct2[Ptr[ASN1_GENERALIZEDTIME], Ptr[ASN1_GENERALIZEDTIME]]

  object PKEY_USAGE_PERIOD_st:
    given _tag: Tag[PKEY_USAGE_PERIOD_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_GENERALIZEDTIME], Ptr[ASN1_GENERALIZEDTIME]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: PKEY_USAGE_PERIOD_st)
        inline def notBefore: Ptr[ASN1_GENERALIZEDTIME] = struct._1
        inline def notBefore_=(value: Ptr[ASN1_GENERALIZEDTIME]): Unit = !struct.at1 = value
        inline def notAfter: Ptr[ASN1_GENERALIZEDTIME] = struct._2
        inline def notAfter_=(value: Ptr[ASN1_GENERALIZEDTIME]): Unit = !struct.at2 = value
      end extension

    // Allocates PKEY_USAGE_PERIOD_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PKEY_USAGE_PERIOD_st] =
      scala.scalanative.unsafe.alloc[PKEY_USAGE_PERIOD_st](1)
    def apply(notBefore: Ptr[ASN1_GENERALIZEDTIME], notAfter: Ptr[ASN1_GENERALIZEDTIME])(using
        Zone,
    ): Ptr[PKEY_USAGE_PERIOD_st] =
      val ____ptr = apply()
      (!____ptr).notBefore = notBefore
      (!____ptr).notAfter = notAfter
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICYINFO = CStruct2[Ptr[ASN1_OBJECT], Ptr[stack_st_POLICYQUALINFO]]

  object POLICYINFO:
    given _tag: Tag[POLICYINFO] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[stack_st_POLICYQUALINFO]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: POLICYINFO)
        inline def policyid: Ptr[ASN1_OBJECT] = struct._1
        inline def policyid_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def qualifiers: Ptr[stack_st_POLICYQUALINFO] = struct._2
        inline def qualifiers_=(value: Ptr[stack_st_POLICYQUALINFO]): Unit = !struct.at2 = value
      end extension

    // Allocates POLICYINFO on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[POLICYINFO] = scala.scalanative.unsafe.alloc[POLICYINFO](1)
    def apply(policyid: Ptr[ASN1_OBJECT], qualifiers: Ptr[stack_st_POLICYQUALINFO])(using
        Zone,
    ): Ptr[POLICYINFO] =
      val ____ptr = apply()
      (!____ptr).policyid = policyid
      (!____ptr).qualifiers = qualifiers
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICYINFO_st = CStruct2[Ptr[ASN1_OBJECT], Ptr[stack_st_POLICYQUALINFO]]

  object POLICYINFO_st:
    given _tag: Tag[POLICYINFO_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[stack_st_POLICYQUALINFO]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: POLICYINFO_st)
        inline def policyid: Ptr[ASN1_OBJECT] = struct._1
        inline def policyid_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def qualifiers: Ptr[stack_st_POLICYQUALINFO] = struct._2
        inline def qualifiers_=(value: Ptr[stack_st_POLICYQUALINFO]): Unit = !struct.at2 = value
      end extension

    // Allocates POLICYINFO_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[POLICYINFO_st] = scala.scalanative.unsafe.alloc[POLICYINFO_st](1)
    def apply(policyid: Ptr[ASN1_OBJECT], qualifiers: Ptr[stack_st_POLICYQUALINFO])(using
        Zone,
    ): Ptr[POLICYINFO_st] =
      val ____ptr = apply()
      (!____ptr).policyid = policyid
      (!____ptr).qualifiers = qualifiers
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICYQUALINFO = CStruct2[Ptr[ASN1_OBJECT], POLICYQUALINFO_D]

  object POLICYQUALINFO:
    given _tag: Tag[POLICYQUALINFO] = Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], POLICYQUALINFO_D]

    export fields.*
    private[x509v3] object fields:
      extension (struct: POLICYQUALINFO)
        inline def pqualid: Ptr[ASN1_OBJECT] = struct._1
        inline def pqualid_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def d: POLICYQUALINFO_D = struct._2
        inline def d_=(value: POLICYQUALINFO_D): Unit = !struct.at2 = value
      end extension

    // Allocates POLICYQUALINFO on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[POLICYQUALINFO] = scala.scalanative.unsafe.alloc[POLICYQUALINFO](1)
    def apply(pqualid: Ptr[ASN1_OBJECT], d: POLICYQUALINFO_D)(using Zone): Ptr[POLICYQUALINFO] =
      val ____ptr = apply()
      (!____ptr).pqualid = pqualid
      (!____ptr).d = d
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICYQUALINFO_D = CArray[Byte, Nat._8]
  object POLICYQUALINFO_D:
    given _tag: Tag[POLICYQUALINFO_D] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[POLICYQUALINFO_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[POLICYQUALINFO_D](1)
      ___ptr

    @scala.annotation.targetName("apply_cpsuri")
    def apply(cpsuri: Ptr[ASN1_IA5STRING])(using Zone): Ptr[POLICYQUALINFO_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[POLICYQUALINFO_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, cpsuri)
      ___ptr

    @scala.annotation.targetName("apply_usernotice")
    def apply(usernotice: Ptr[USERNOTICE])(using Zone): Ptr[POLICYQUALINFO_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[POLICYQUALINFO_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[USERNOTICE]]].update(0, usernotice)
      ___ptr

    @scala.annotation.targetName("apply_other")
    def apply(other: Ptr[ASN1_TYPE])(using Zone): Ptr[POLICYQUALINFO_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[POLICYQUALINFO_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]].update(0, other)
      ___ptr

    extension (struct: POLICYQUALINFO_D)
      inline def cpsuri: Ptr[ASN1_IA5STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def cpsuri_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def usernotice: Ptr[USERNOTICE] = !struct.at(0).asInstanceOf[Ptr[Ptr[USERNOTICE]]]
      inline def usernotice_=(value: Ptr[USERNOTICE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[USERNOTICE]]] = value
      inline def other: Ptr[ASN1_TYPE] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]]
      inline def other_=(value: Ptr[ASN1_TYPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICYQUALINFO_st = CStruct2[Ptr[ASN1_OBJECT], POLICYQUALINFO_st_D]

  object POLICYQUALINFO_st:
    given _tag: Tag[POLICYQUALINFO_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], POLICYQUALINFO_st_D]

    export fields.*
    private[x509v3] object fields:
      extension (struct: POLICYQUALINFO_st)
        inline def pqualid: Ptr[ASN1_OBJECT] = struct._1
        inline def pqualid_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def d: POLICYQUALINFO_st_D = struct._2
        inline def d_=(value: POLICYQUALINFO_st_D): Unit = !struct.at2 = value
      end extension

    // Allocates POLICYQUALINFO_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[POLICYQUALINFO_st] =
      scala.scalanative.unsafe.alloc[POLICYQUALINFO_st](1)
    def apply(pqualid: Ptr[ASN1_OBJECT], d: POLICYQUALINFO_st_D)(using
        Zone,
    ): Ptr[POLICYQUALINFO_st] =
      val ____ptr = apply()
      (!____ptr).pqualid = pqualid
      (!____ptr).d = d
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICYQUALINFO_st_D = CArray[Byte, Nat._8]
  object POLICYQUALINFO_st_D:
    given _tag: Tag[POLICYQUALINFO_st_D] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[POLICYQUALINFO_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[POLICYQUALINFO_st_D](1)
      ___ptr

    @scala.annotation.targetName("apply_cpsuri")
    def apply(cpsuri: Ptr[ASN1_IA5STRING])(using Zone): Ptr[POLICYQUALINFO_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[POLICYQUALINFO_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, cpsuri)
      ___ptr

    @scala.annotation.targetName("apply_usernotice")
    def apply(usernotice: Ptr[USERNOTICE])(using Zone): Ptr[POLICYQUALINFO_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[POLICYQUALINFO_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[USERNOTICE]]].update(0, usernotice)
      ___ptr

    @scala.annotation.targetName("apply_other")
    def apply(other: Ptr[ASN1_TYPE])(using Zone): Ptr[POLICYQUALINFO_st_D] =
      val ___ptr = scala.scalanative.unsafe.alloc[POLICYQUALINFO_st_D](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]].update(0, other)
      ___ptr

    extension (struct: POLICYQUALINFO_st_D)
      inline def cpsuri: Ptr[ASN1_IA5STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
      inline def cpsuri_=(value: Ptr[ASN1_IA5STRING]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
      inline def usernotice: Ptr[USERNOTICE] = !struct.at(0).asInstanceOf[Ptr[Ptr[USERNOTICE]]]
      inline def usernotice_=(value: Ptr[USERNOTICE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[USERNOTICE]]] = value
      inline def other: Ptr[ASN1_TYPE] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]]
      inline def other_=(value: Ptr[ASN1_TYPE]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_TYPE]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICY_CONSTRAINTS = CStruct2[Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

  object POLICY_CONSTRAINTS:
    given _tag: Tag[POLICY_CONSTRAINTS] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: POLICY_CONSTRAINTS)
        inline def requireExplicitPolicy: Ptr[ASN1_INTEGER] = struct._1
        inline def requireExplicitPolicy_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def inhibitPolicyMapping: Ptr[ASN1_INTEGER] = struct._2
        inline def inhibitPolicyMapping_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates POLICY_CONSTRAINTS on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[POLICY_CONSTRAINTS] =
      scala.scalanative.unsafe.alloc[POLICY_CONSTRAINTS](1)
    def apply(requireExplicitPolicy: Ptr[ASN1_INTEGER], inhibitPolicyMapping: Ptr[ASN1_INTEGER])(
        using Zone,
    ): Ptr[POLICY_CONSTRAINTS] =
      val ____ptr = apply()
      (!____ptr).requireExplicitPolicy = requireExplicitPolicy
      (!____ptr).inhibitPolicyMapping = inhibitPolicyMapping
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICY_CONSTRAINTS_st = CStruct2[Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

  object POLICY_CONSTRAINTS_st:
    given _tag: Tag[POLICY_CONSTRAINTS_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[ASN1_INTEGER]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: POLICY_CONSTRAINTS_st)
        inline def requireExplicitPolicy: Ptr[ASN1_INTEGER] = struct._1
        inline def requireExplicitPolicy_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def inhibitPolicyMapping: Ptr[ASN1_INTEGER] = struct._2
        inline def inhibitPolicyMapping_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at2 = value
      end extension

    // Allocates POLICY_CONSTRAINTS_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[POLICY_CONSTRAINTS_st] =
      scala.scalanative.unsafe.alloc[POLICY_CONSTRAINTS_st](1)
    def apply(requireExplicitPolicy: Ptr[ASN1_INTEGER], inhibitPolicyMapping: Ptr[ASN1_INTEGER])(
        using Zone,
    ): Ptr[POLICY_CONSTRAINTS_st] =
      val ____ptr = apply()
      (!____ptr).requireExplicitPolicy = requireExplicitPolicy
      (!____ptr).inhibitPolicyMapping = inhibitPolicyMapping
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICY_MAPPING = CStruct2[Ptr[ASN1_OBJECT], Ptr[ASN1_OBJECT]]

  object POLICY_MAPPING:
    given _tag: Tag[POLICY_MAPPING] = Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[ASN1_OBJECT]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: POLICY_MAPPING)
        inline def issuerDomainPolicy: Ptr[ASN1_OBJECT] = struct._1
        inline def issuerDomainPolicy_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def subjectDomainPolicy: Ptr[ASN1_OBJECT] = struct._2
        inline def subjectDomainPolicy_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at2 = value
      end extension

    // Allocates POLICY_MAPPING on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[POLICY_MAPPING] = scala.scalanative.unsafe.alloc[POLICY_MAPPING](1)
    def apply(issuerDomainPolicy: Ptr[ASN1_OBJECT], subjectDomainPolicy: Ptr[ASN1_OBJECT])(using
        Zone,
    ): Ptr[POLICY_MAPPING] =
      val ____ptr = apply()
      (!____ptr).issuerDomainPolicy = issuerDomainPolicy
      (!____ptr).subjectDomainPolicy = subjectDomainPolicy
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICY_MAPPINGS = CStruct0

  object POLICY_MAPPINGS:
    given _tag: Tag[POLICY_MAPPINGS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type POLICY_MAPPING_st = CStruct2[Ptr[ASN1_OBJECT], Ptr[ASN1_OBJECT]]

  object POLICY_MAPPING_st:
    given _tag: Tag[POLICY_MAPPING_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[ASN1_OBJECT]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: POLICY_MAPPING_st)
        inline def issuerDomainPolicy: Ptr[ASN1_OBJECT] = struct._1
        inline def issuerDomainPolicy_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def subjectDomainPolicy: Ptr[ASN1_OBJECT] = struct._2
        inline def subjectDomainPolicy_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at2 = value
      end extension

    // Allocates POLICY_MAPPING_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[POLICY_MAPPING_st] =
      scala.scalanative.unsafe.alloc[POLICY_MAPPING_st](1)
    def apply(issuerDomainPolicy: Ptr[ASN1_OBJECT], subjectDomainPolicy: Ptr[ASN1_OBJECT])(using
        Zone,
    ): Ptr[POLICY_MAPPING_st] =
      val ____ptr = apply()
      (!____ptr).issuerDomainPolicy = issuerDomainPolicy
      (!____ptr).subjectDomainPolicy = subjectDomainPolicy
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type PROFESSION_INFO = CStruct0

  object PROFESSION_INFO:
    given _tag: Tag[PROFESSION_INFO] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type PROFESSION_INFOS = CStruct0

  object PROFESSION_INFOS:
    given _tag: Tag[PROFESSION_INFOS] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type PROXY_CERT_INFO_EXTENSION = CStruct2[Ptr[ASN1_INTEGER], Ptr[PROXY_POLICY]]

  object PROXY_CERT_INFO_EXTENSION:
    given _tag: Tag[PROXY_CERT_INFO_EXTENSION] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[PROXY_POLICY]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: PROXY_CERT_INFO_EXTENSION)
        inline def pcPathLengthConstraint: Ptr[ASN1_INTEGER] = struct._1
        inline def pcPathLengthConstraint_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def proxyPolicy: Ptr[PROXY_POLICY] = struct._2
        inline def proxyPolicy_=(value: Ptr[PROXY_POLICY]): Unit = !struct.at2 = value
      end extension

    // Allocates PROXY_CERT_INFO_EXTENSION on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PROXY_CERT_INFO_EXTENSION] =
      scala.scalanative.unsafe.alloc[PROXY_CERT_INFO_EXTENSION](1)
    def apply(pcPathLengthConstraint: Ptr[ASN1_INTEGER], proxyPolicy: Ptr[PROXY_POLICY])(using
        Zone,
    ): Ptr[PROXY_CERT_INFO_EXTENSION] =
      val ____ptr = apply()
      (!____ptr).pcPathLengthConstraint = pcPathLengthConstraint
      (!____ptr).proxyPolicy = proxyPolicy
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type PROXY_CERT_INFO_EXTENSION_st = CStruct2[Ptr[ASN1_INTEGER], Ptr[PROXY_POLICY]]

  object PROXY_CERT_INFO_EXTENSION_st:
    given _tag: Tag[PROXY_CERT_INFO_EXTENSION_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[PROXY_POLICY]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: PROXY_CERT_INFO_EXTENSION_st)
        inline def pcPathLengthConstraint: Ptr[ASN1_INTEGER] = struct._1
        inline def pcPathLengthConstraint_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def proxyPolicy: Ptr[PROXY_POLICY] = struct._2
        inline def proxyPolicy_=(value: Ptr[PROXY_POLICY]): Unit = !struct.at2 = value
      end extension

    // Allocates PROXY_CERT_INFO_EXTENSION_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PROXY_CERT_INFO_EXTENSION_st] =
      scala.scalanative.unsafe.alloc[PROXY_CERT_INFO_EXTENSION_st](1)
    def apply(pcPathLengthConstraint: Ptr[ASN1_INTEGER], proxyPolicy: Ptr[PROXY_POLICY])(using
        Zone,
    ): Ptr[PROXY_CERT_INFO_EXTENSION_st] =
      val ____ptr = apply()
      (!____ptr).pcPathLengthConstraint = pcPathLengthConstraint
      (!____ptr).proxyPolicy = proxyPolicy
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type PROXY_POLICY = CStruct2[Ptr[ASN1_OBJECT], Ptr[ASN1_OCTET_STRING]]

  object PROXY_POLICY:
    given _tag: Tag[PROXY_POLICY] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[ASN1_OCTET_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: PROXY_POLICY)
        inline def policyLanguage: Ptr[ASN1_OBJECT] = struct._1
        inline def policyLanguage_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def policy: Ptr[ASN1_OCTET_STRING] = struct._2
        inline def policy_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates PROXY_POLICY on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PROXY_POLICY] = scala.scalanative.unsafe.alloc[PROXY_POLICY](1)
    def apply(policyLanguage: Ptr[ASN1_OBJECT], policy: Ptr[ASN1_OCTET_STRING])(using
        Zone,
    ): Ptr[PROXY_POLICY] =
      val ____ptr = apply()
      (!____ptr).policyLanguage = policyLanguage
      (!____ptr).policy = policy
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type PROXY_POLICY_st = CStruct2[Ptr[ASN1_OBJECT], Ptr[ASN1_OCTET_STRING]]

  object PROXY_POLICY_st:
    given _tag: Tag[PROXY_POLICY_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[ASN1_OCTET_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: PROXY_POLICY_st)
        inline def policyLanguage: Ptr[ASN1_OBJECT] = struct._1
        inline def policyLanguage_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def policy: Ptr[ASN1_OCTET_STRING] = struct._2
        inline def policy_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates PROXY_POLICY_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[PROXY_POLICY_st] =
      scala.scalanative.unsafe.alloc[PROXY_POLICY_st](1)
    def apply(policyLanguage: Ptr[ASN1_OBJECT], policy: Ptr[ASN1_OCTET_STRING])(using
        Zone,
    ): Ptr[PROXY_POLICY_st] =
      val ____ptr = apply()
      (!____ptr).policyLanguage = policyLanguage
      (!____ptr).policy = policy
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type ProfessionInfo_st = CStruct0

  object ProfessionInfo_st:
    given _tag: Tag[ProfessionInfo_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type SXNET = CStruct2[Ptr[ASN1_INTEGER], Ptr[stack_st_SXNETID]]

  object SXNET:
    given _tag: Tag[SXNET] = Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[stack_st_SXNETID]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: SXNET)
        inline def version: Ptr[ASN1_INTEGER] = struct._1
        inline def version_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def ids: Ptr[stack_st_SXNETID] = struct._2
        inline def ids_=(value: Ptr[stack_st_SXNETID]): Unit = !struct.at2 = value
      end extension

    // Allocates SXNET on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SXNET] = scala.scalanative.unsafe.alloc[SXNET](1)
    def apply(version: Ptr[ASN1_INTEGER], ids: Ptr[stack_st_SXNETID])(using Zone): Ptr[SXNET] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).ids = ids
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type SXNETID = CStruct2[Ptr[ASN1_INTEGER], Ptr[ASN1_OCTET_STRING]]

  object SXNETID:
    given _tag: Tag[SXNETID] = Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[ASN1_OCTET_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: SXNETID)
        inline def zone: Ptr[ASN1_INTEGER] = struct._1
        inline def zone_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def user: Ptr[ASN1_OCTET_STRING] = struct._2
        inline def user_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates SXNETID on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SXNETID] = scala.scalanative.unsafe.alloc[SXNETID](1)
    def apply(zone: Ptr[ASN1_INTEGER], user: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[SXNETID] =
      val ____ptr = apply()
      (!____ptr).zone = zone
      (!____ptr).user = user
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type SXNET_ID_st = CStruct2[Ptr[ASN1_INTEGER], Ptr[ASN1_OCTET_STRING]]

  object SXNET_ID_st:
    given _tag: Tag[SXNET_ID_st] =
      Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[ASN1_OCTET_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: SXNET_ID_st)
        inline def zone: Ptr[ASN1_INTEGER] = struct._1
        inline def zone_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def user: Ptr[ASN1_OCTET_STRING] = struct._2
        inline def user_=(value: Ptr[ASN1_OCTET_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates SXNET_ID_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SXNET_ID_st] = scala.scalanative.unsafe.alloc[SXNET_ID_st](1)
    def apply(zone: Ptr[ASN1_INTEGER], user: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[SXNET_ID_st] =
      val ____ptr = apply()
      (!____ptr).zone = zone
      (!____ptr).user = user
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type SXNET_st = CStruct2[Ptr[ASN1_INTEGER], Ptr[stack_st_SXNETID]]

  object SXNET_st:
    given _tag: Tag[SXNET_st] = Tag.materializeCStruct2Tag[Ptr[ASN1_INTEGER], Ptr[stack_st_SXNETID]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: SXNET_st)
        inline def version: Ptr[ASN1_INTEGER] = struct._1
        inline def version_=(value: Ptr[ASN1_INTEGER]): Unit = !struct.at1 = value
        inline def ids: Ptr[stack_st_SXNETID] = struct._2
        inline def ids_=(value: Ptr[stack_st_SXNETID]): Unit = !struct.at2 = value
      end extension

    // Allocates SXNET_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[SXNET_st] = scala.scalanative.unsafe.alloc[SXNET_st](1)
    def apply(version: Ptr[ASN1_INTEGER], ids: Ptr[stack_st_SXNETID])(using Zone): Ptr[SXNET_st] =
      val ____ptr = apply()
      (!____ptr).version = version
      (!____ptr).ids = ids
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/asn1.h
   */
  opaque type TLS_FEATURE = CStruct0

  object TLS_FEATURE:
    given _tag: Tag[TLS_FEATURE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type USERNOTICE = CStruct2[Ptr[NOTICEREF], Ptr[ASN1_STRING]]

  object USERNOTICE:
    given _tag: Tag[USERNOTICE] = Tag.materializeCStruct2Tag[Ptr[NOTICEREF], Ptr[ASN1_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: USERNOTICE)
        inline def noticeref: Ptr[NOTICEREF] = struct._1
        inline def noticeref_=(value: Ptr[NOTICEREF]): Unit = !struct.at1 = value
        inline def exptext: Ptr[ASN1_STRING] = struct._2
        inline def exptext_=(value: Ptr[ASN1_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates USERNOTICE on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[USERNOTICE] = scala.scalanative.unsafe.alloc[USERNOTICE](1)
    def apply(noticeref: Ptr[NOTICEREF], exptext: Ptr[ASN1_STRING])(using Zone): Ptr[USERNOTICE] =
      val ____ptr = apply()
      (!____ptr).noticeref = noticeref
      (!____ptr).exptext = exptext
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type USERNOTICE_st = CStruct2[Ptr[NOTICEREF], Ptr[ASN1_STRING]]

  object USERNOTICE_st:
    given _tag: Tag[USERNOTICE_st] = Tag.materializeCStruct2Tag[Ptr[NOTICEREF], Ptr[ASN1_STRING]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: USERNOTICE_st)
        inline def noticeref: Ptr[NOTICEREF] = struct._1
        inline def noticeref_=(value: Ptr[NOTICEREF]): Unit = !struct.at1 = value
        inline def exptext: Ptr[ASN1_STRING] = struct._2
        inline def exptext_=(value: Ptr[ASN1_STRING]): Unit = !struct.at2 = value
      end extension

    // Allocates USERNOTICE_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[USERNOTICE_st] = scala.scalanative.unsafe.alloc[USERNOTICE_st](1)
    def apply(noticeref: Ptr[NOTICEREF], exptext: Ptr[ASN1_STRING])(using
        Zone,
    ): Ptr[USERNOTICE_st] =
      val ____ptr = apply()
      (!____ptr).noticeref = noticeref
      (!____ptr).exptext = exptext
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_CONF_METHOD = CStruct4[
    CFuncPtr3[Ptr[Byte], CString, CString, CString],
    CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]],
    CFuncPtr2[Ptr[Byte], CString, Unit],
    CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit],
  ]

  object X509V3_CONF_METHOD:
    given _tag: Tag[X509V3_CONF_METHOD] = Tag.materializeCStruct4Tag[
      CFuncPtr3[Ptr[Byte], CString, CString, CString],
      CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]],
      CFuncPtr2[Ptr[Byte], CString, Unit],
      CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit],
    ]

    export fields.*
    private[x509v3] object fields:
      extension (struct: X509V3_CONF_METHOD)
        inline def get_string: CFuncPtr3[Ptr[Byte], CString, CString, CString] = struct._1
        inline def get_string_=(value: CFuncPtr3[Ptr[Byte], CString, CString, CString]): Unit =
          !struct.at1 = value
        inline def get_section: CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]] = struct._2
        inline def get_section_=(
            value: CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]],
        ): Unit = !struct.at2 = value
        inline def free_string: CFuncPtr2[Ptr[Byte], CString, Unit] = struct._3
        inline def free_string_=(value: CFuncPtr2[Ptr[Byte], CString, Unit]): Unit = !struct.at3 =
          value
        inline def free_section: CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit] = struct._4
        inline def free_section_=(
            value: CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit],
        ): Unit = !struct.at4 = value
      end extension

    // Allocates X509V3_CONF_METHOD on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509V3_CONF_METHOD] =
      scala.scalanative.unsafe.alloc[X509V3_CONF_METHOD](1)
    def apply(
        get_string: CFuncPtr3[Ptr[Byte], CString, CString, CString],
        get_section: CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]],
        free_string: CFuncPtr2[Ptr[Byte], CString, Unit],
        free_section: CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit],
    )(using Zone): Ptr[X509V3_CONF_METHOD] =
      val ____ptr = apply()
      (!____ptr).get_string = get_string
      (!____ptr).get_section = get_section
      (!____ptr).free_string = free_string
      (!____ptr).free_section = free_section
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_CONF_METHOD_st = CStruct4[
    CFuncPtr3[Ptr[Byte], CString, CString, CString],
    CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]],
    CFuncPtr2[Ptr[Byte], CString, Unit],
    CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit],
  ]

  object X509V3_CONF_METHOD_st:
    given _tag: Tag[X509V3_CONF_METHOD_st] = Tag.materializeCStruct4Tag[
      CFuncPtr3[Ptr[Byte], CString, CString, CString],
      CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]],
      CFuncPtr2[Ptr[Byte], CString, Unit],
      CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit],
    ]

    export fields.*
    private[x509v3] object fields:
      extension (struct: X509V3_CONF_METHOD_st)
        inline def get_string: CFuncPtr3[Ptr[Byte], CString, CString, CString] = struct._1
        inline def get_string_=(value: CFuncPtr3[Ptr[Byte], CString, CString, CString]): Unit =
          !struct.at1 = value
        inline def get_section: CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]] = struct._2
        inline def get_section_=(
            value: CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]],
        ): Unit = !struct.at2 = value
        inline def free_string: CFuncPtr2[Ptr[Byte], CString, Unit] = struct._3
        inline def free_string_=(value: CFuncPtr2[Ptr[Byte], CString, Unit]): Unit = !struct.at3 =
          value
        inline def free_section: CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit] = struct._4
        inline def free_section_=(
            value: CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit],
        ): Unit = !struct.at4 = value
      end extension

    // Allocates X509V3_CONF_METHOD_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509V3_CONF_METHOD_st] =
      scala.scalanative.unsafe.alloc[X509V3_CONF_METHOD_st](1)
    def apply(
        get_string: CFuncPtr3[Ptr[Byte], CString, CString, CString],
        get_section: CFuncPtr2[Ptr[Byte], CString, Ptr[stack_st_CONF_VALUE]],
        free_string: CFuncPtr2[Ptr[Byte], CString, Unit],
        free_section: CFuncPtr2[Ptr[Byte], Ptr[stack_st_CONF_VALUE], Unit],
    )(using Zone): Ptr[X509V3_CONF_METHOD_st] =
      val ____ptr = apply()
      (!____ptr).get_string = get_string
      (!____ptr).get_section = get_section
      (!____ptr).free_string = free_string
      (!____ptr).free_section = free_section
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_CTX = CStruct8[CInt, Ptr[X509], Ptr[X509], Ptr[X509_REQ], Ptr[X509_CRL], Ptr[
    X509V3_CONF_METHOD,
  ], Ptr[Byte], Ptr[EVP_PKEY]]

  object X509V3_CTX:
    given _tag: Tag[X509V3_CTX] = Tag.materializeCStruct8Tag[CInt, Ptr[X509], Ptr[X509], Ptr[
      X509_REQ,
    ], Ptr[X509_CRL], Ptr[X509V3_CONF_METHOD], Ptr[Byte], Ptr[EVP_PKEY]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: X509V3_CTX)
        inline def flags: CInt = struct._1
        inline def flags_=(value: CInt): Unit = !struct.at1 = value
        inline def issuer_cert: Ptr[X509] = struct._2
        inline def issuer_cert_=(value: Ptr[X509]): Unit = !struct.at2 = value
        inline def subject_cert: Ptr[X509] = struct._3
        inline def subject_cert_=(value: Ptr[X509]): Unit = !struct.at3 = value
        inline def subject_req: Ptr[X509_REQ] = struct._4
        inline def subject_req_=(value: Ptr[X509_REQ]): Unit = !struct.at4 = value
        inline def crl: Ptr[X509_CRL] = struct._5
        inline def crl_=(value: Ptr[X509_CRL]): Unit = !struct.at5 = value
        inline def db_meth: Ptr[X509V3_CONF_METHOD] = struct._6
        inline def db_meth_=(value: Ptr[X509V3_CONF_METHOD]): Unit = !struct.at6 = value
        inline def db: Ptr[Byte] = struct._7
        inline def db_=(value: Ptr[Byte]): Unit = !struct.at7 = value
        inline def issuer_pkey: Ptr[EVP_PKEY] = struct._8
        inline def issuer_pkey_=(value: Ptr[EVP_PKEY]): Unit = !struct.at8 = value
      end extension

    // Allocates X509V3_CTX on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509V3_CTX] = scala.scalanative.unsafe.alloc[X509V3_CTX](1)
    def apply(
        flags: CInt,
        issuer_cert: Ptr[X509],
        subject_cert: Ptr[X509],
        subject_req: Ptr[X509_REQ],
        crl: Ptr[X509_CRL],
        db_meth: Ptr[X509V3_CONF_METHOD],
        db: Ptr[Byte],
        issuer_pkey: Ptr[EVP_PKEY],
    )(using Zone): Ptr[X509V3_CTX] =
      val ____ptr = apply()
      (!____ptr).flags = flags
      (!____ptr).issuer_cert = issuer_cert
      (!____ptr).subject_cert = subject_cert
      (!____ptr).subject_req = subject_req
      (!____ptr).crl = crl
      (!____ptr).db_meth = db_meth
      (!____ptr).db = db
      (!____ptr).issuer_pkey = issuer_pkey
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_METHOD = CStruct14[
    CInt,
    CInt,
    Ptr[ASN1_ITEM_EXP],
    X509V3_EXT_NEW,
    X509V3_EXT_FREE,
    X509V3_EXT_D2I,
    X509V3_EXT_I2D,
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
  ]

  object X509V3_EXT_METHOD:
    given _tag: Tag[X509V3_EXT_METHOD] = Tag.materializeCStruct14Tag[
      CInt,
      CInt,
      Ptr[ASN1_ITEM_EXP],
      X509V3_EXT_NEW,
      X509V3_EXT_FREE,
      X509V3_EXT_D2I,
      X509V3_EXT_I2D,
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
    ]

    export fields.*
    private[x509v3] object fields:
      extension (struct: X509V3_EXT_METHOD)
        inline def ext_nid: CInt = struct._1
        inline def ext_nid_=(value: CInt): Unit = !struct.at1 = value
        inline def ext_flags: CInt = struct._2
        inline def ext_flags_=(value: CInt): Unit = !struct.at2 = value
        inline def it: Ptr[ASN1_ITEM_EXP] = struct._3
        inline def it_=(value: Ptr[ASN1_ITEM_EXP]): Unit = !struct.at3 = value
        inline def ext_new: X509V3_EXT_NEW = struct._4
        inline def ext_new_=(value: X509V3_EXT_NEW): Unit = !struct.at4 = value
        inline def ext_free: X509V3_EXT_FREE = struct._5
        inline def ext_free_=(value: X509V3_EXT_FREE): Unit = !struct.at5 = value
        inline def d2i: X509V3_EXT_D2I = struct._6
        inline def d2i_=(value: X509V3_EXT_D2I): Unit = !struct.at6 = value
        inline def i2d: X509V3_EXT_I2D = struct._7
        inline def i2d_=(value: X509V3_EXT_I2D): Unit = !struct.at7 = value
        inline def i2s: X509V3_EXT_I2S = struct._8.asInstanceOf[X509V3_EXT_I2S]
        inline def i2s_=(value: X509V3_EXT_I2S): Unit = !struct.at8 = value.asInstanceOf[Ptr[Byte]]
        inline def s2i: X509V3_EXT_S2I = struct._9.asInstanceOf[X509V3_EXT_S2I]
        inline def s2i_=(value: X509V3_EXT_S2I): Unit = !struct.at9 = value.asInstanceOf[Ptr[Byte]]
        inline def i2v: X509V3_EXT_I2V = struct._10.asInstanceOf[X509V3_EXT_I2V]
        inline def i2v_=(value: X509V3_EXT_I2V): Unit = !struct.at10 = value.asInstanceOf[Ptr[Byte]]
        inline def v2i: X509V3_EXT_V2I = struct._11.asInstanceOf[X509V3_EXT_V2I]
        inline def v2i_=(value: X509V3_EXT_V2I): Unit = !struct.at11 = value.asInstanceOf[Ptr[Byte]]
        inline def i2r: X509V3_EXT_I2R = struct._12.asInstanceOf[X509V3_EXT_I2R]
        inline def i2r_=(value: X509V3_EXT_I2R): Unit = !struct.at12 = value.asInstanceOf[Ptr[Byte]]
        inline def r2i: X509V3_EXT_R2I = struct._13.asInstanceOf[X509V3_EXT_R2I]
        inline def r2i_=(value: X509V3_EXT_R2I): Unit = !struct.at13 = value.asInstanceOf[Ptr[Byte]]
        inline def usr_data: Ptr[Byte] = struct._14
        inline def usr_data_=(value: Ptr[Byte]): Unit = !struct.at14 = value
      end extension

    // Allocates X509V3_EXT_METHOD on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509V3_EXT_METHOD] =
      scala.scalanative.unsafe.alloc[X509V3_EXT_METHOD](1)
    def apply(
        ext_nid: CInt,
        ext_flags: CInt,
        it: Ptr[ASN1_ITEM_EXP],
        ext_new: X509V3_EXT_NEW,
        ext_free: X509V3_EXT_FREE,
        d2i: X509V3_EXT_D2I,
        i2d: X509V3_EXT_I2D,
        i2s: X509V3_EXT_I2S,
        s2i: X509V3_EXT_S2I,
        i2v: X509V3_EXT_I2V,
        v2i: X509V3_EXT_V2I,
        i2r: X509V3_EXT_I2R,
        r2i: X509V3_EXT_R2I,
        usr_data: Ptr[Byte],
    )(using Zone): Ptr[X509V3_EXT_METHOD] =
      val ____ptr = apply()
      (!____ptr).ext_nid = ext_nid
      (!____ptr).ext_flags = ext_flags
      (!____ptr).it = it
      (!____ptr).ext_new = ext_new
      (!____ptr).ext_free = ext_free
      (!____ptr).d2i = d2i
      (!____ptr).i2d = i2d
      (!____ptr).i2s = i2s
      (!____ptr).s2i = s2i
      (!____ptr).i2v = i2v
      (!____ptr).v2i = v2i
      (!____ptr).i2r = i2r
      (!____ptr).r2i = r2i
      (!____ptr).usr_data = usr_data
      ____ptr

  // /**
  //  * [bindgen] header: /usr/include/openssl/x509.h
  //  */
  // opaque type X509_EXTENSION = CStruct0

  // object X509_EXTENSION:
  //   given _tag: Tag[X509_EXTENSION] = Tag.materializeCStruct0Tag

  // /**
  //  * [bindgen] header: /usr/include/openssl/types.h
  //  */
  // opaque type X509_NAME = CStruct0

  // object X509_NAME:
  //   given _tag: Tag[X509_NAME] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type X509_POLICY_NODE = CStruct0

  object X509_POLICY_NODE:
    given _tag: Tag[X509_POLICY_NODE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509_PURPOSE =
    CStruct7[CInt, CInt, CInt, CFuncPtr3[Ptr[Byte], Ptr[X509], CInt, CInt], CString, CString, Ptr[
      Byte,
    ]]

  object X509_PURPOSE:
    given _tag: Tag[X509_PURPOSE] = Tag.materializeCStruct7Tag[CInt, CInt, CInt, CFuncPtr3[Ptr[
      Byte,
    ], Ptr[X509], CInt, CInt], CString, CString, Ptr[Byte]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: X509_PURPOSE)
        inline def purpose: CInt = struct._1
        inline def purpose_=(value: CInt): Unit = !struct.at1 = value
        inline def trust: CInt = struct._2
        inline def trust_=(value: CInt): Unit = !struct.at2 = value
        inline def flags: CInt = struct._3
        inline def flags_=(value: CInt): Unit = !struct.at3 = value
        inline def check_purpose: CFuncPtr3[Ptr[x509_purpose_st], Ptr[X509], CInt, CInt] =
          struct._4.asInstanceOf[CFuncPtr3[Ptr[x509_purpose_st], Ptr[X509], CInt, CInt]]
        inline def check_purpose_=(
            value: CFuncPtr3[Ptr[x509_purpose_st], Ptr[X509], CInt, CInt],
        ): Unit = !struct.at4 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], Ptr[X509], CInt, CInt]]
        inline def name: CString = struct._5
        inline def name_=(value: CString): Unit = !struct.at5 = value
        inline def sname: CString = struct._6
        inline def sname_=(value: CString): Unit = !struct.at6 = value
        inline def usr_data: Ptr[Byte] = struct._7
        inline def usr_data_=(value: Ptr[Byte]): Unit = !struct.at7 = value
      end extension

    // Allocates X509_PURPOSE on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[X509_PURPOSE] = scala.scalanative.unsafe.alloc[X509_PURPOSE](1)
    def apply(
        purpose: CInt,
        trust: CInt,
        flags: CInt,
        check_purpose: CFuncPtr3[Ptr[x509_purpose_st], Ptr[X509], CInt, CInt],
        name: CString,
        sname: CString,
        usr_data: Ptr[Byte],
    )(using Zone): Ptr[X509_PURPOSE] =
      val ____ptr = apply()
      (!____ptr).purpose = purpose
      (!____ptr).trust = trust
      (!____ptr).flags = flags
      (!____ptr).check_purpose = check_purpose
      (!____ptr).name = name
      (!____ptr).sname = sname
      (!____ptr).usr_data = usr_data
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type otherName_st = CStruct2[Ptr[ASN1_OBJECT], Ptr[ASN1_TYPE]]

  object otherName_st:
    given _tag: Tag[otherName_st] = Tag.materializeCStruct2Tag[Ptr[ASN1_OBJECT], Ptr[ASN1_TYPE]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: otherName_st)
        inline def type_id: Ptr[ASN1_OBJECT] = struct._1
        inline def type_id_=(value: Ptr[ASN1_OBJECT]): Unit = !struct.at1 = value
        inline def value: Ptr[ASN1_TYPE] = struct._2
        inline def value_=(value: Ptr[ASN1_TYPE]): Unit = !struct.at2 = value
      end extension

    // Allocates otherName_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[otherName_st] = scala.scalanative.unsafe.alloc[otherName_st](1)
    def apply(type_id: Ptr[ASN1_OBJECT], value: Ptr[ASN1_TYPE])(using Zone): Ptr[otherName_st] =
      val ____ptr = apply()
      (!____ptr).type_id = type_id
      (!____ptr).value = value
      ____ptr

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/conf.h
//   // */
//   // opaque type stack_st_CONF_VALUE = CStruct0

//   // object stack_st_CONF_VALUE:
//   //   given _tag: Tag[stack_st_CONF_VALUE] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509v3.h
//   // */
//   // opaque type stack_st_ADMISSIONS = CStruct0

//   // object stack_st_ADMISSIONS:
//   //   given _tag: Tag[stack_st_ADMISSIONS] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509v3.h
//   // */
//   // opaque type stack_st_ASN1_STRING = CStruct0

//   // object stack_st_ASN1_STRING:
//   //   given _tag: Tag[stack_st_ASN1_STRING] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509v3.h
//   // */
//   // opaque type stack_st_GENERAL_SUBTREE = CStruct0

//   // object stack_st_GENERAL_SUBTREE:
//   //   given _tag: Tag[stack_st_GENERAL_SUBTREE] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/safestack.h
//   // */
//   // opaque type stack_st_OPENSSL_STRING = CStruct0

//   // object stack_st_OPENSSL_STRING:
//   //   given _tag: Tag[stack_st_OPENSSL_STRING] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509v3.h
//   // */
//   // opaque type stack_st_POLICYQUALINFO = CStruct0

//   // object stack_st_POLICYQUALINFO:
//   //   given _tag: Tag[stack_st_POLICYQUALINFO] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509v3.h
//   // */
//   // opaque type stack_st_SXNETID = CStruct0

//   // object stack_st_SXNETID:
//   //   given _tag: Tag[stack_st_SXNETID] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509.h
//   // */
//   // opaque type stack_st_X509 = CStruct0

//   // object stack_st_X509:
//   //   given _tag: Tag[stack_st_X509] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509.h
//   // */
//   // opaque type stack_st_X509_EXTENSION = CStruct0

//   // object stack_st_X509_EXTENSION:
//   //   given _tag: Tag[stack_st_X509_EXTENSION] = Tag.materializeCStruct0Tag

//   // /**
//   //  * [bindgen] header: /usr/include/openssl/x509.h
//   // */
//   // opaque type stack_st_X509_NAME_ENTRY = CStruct0

//   // object stack_st_X509_NAME_ENTRY:
//   //   given _tag: Tag[stack_st_X509_NAME_ENTRY] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type v3_ext_ctx = CStruct8[CInt, Ptr[X509], Ptr[X509], Ptr[X509_REQ], Ptr[X509_CRL], Ptr[
    X509V3_CONF_METHOD,
  ], Ptr[Byte], Ptr[EVP_PKEY]]

  object v3_ext_ctx:
    given _tag: Tag[v3_ext_ctx] = Tag.materializeCStruct8Tag[CInt, Ptr[X509], Ptr[X509], Ptr[
      X509_REQ,
    ], Ptr[X509_CRL], Ptr[X509V3_CONF_METHOD], Ptr[Byte], Ptr[EVP_PKEY]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: v3_ext_ctx)
        inline def flags: CInt = struct._1
        inline def flags_=(value: CInt): Unit = !struct.at1 = value
        inline def issuer_cert: Ptr[X509] = struct._2
        inline def issuer_cert_=(value: Ptr[X509]): Unit = !struct.at2 = value
        inline def subject_cert: Ptr[X509] = struct._3
        inline def subject_cert_=(value: Ptr[X509]): Unit = !struct.at3 = value
        inline def subject_req: Ptr[X509_REQ] = struct._4
        inline def subject_req_=(value: Ptr[X509_REQ]): Unit = !struct.at4 = value
        inline def crl: Ptr[X509_CRL] = struct._5
        inline def crl_=(value: Ptr[X509_CRL]): Unit = !struct.at5 = value
        inline def db_meth: Ptr[X509V3_CONF_METHOD] = struct._6
        inline def db_meth_=(value: Ptr[X509V3_CONF_METHOD]): Unit = !struct.at6 = value
        inline def db: Ptr[Byte] = struct._7
        inline def db_=(value: Ptr[Byte]): Unit = !struct.at7 = value
        inline def issuer_pkey: Ptr[EVP_PKEY] = struct._8
        inline def issuer_pkey_=(value: Ptr[EVP_PKEY]): Unit = !struct.at8 = value
      end extension

    // Allocates v3_ext_ctx on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[v3_ext_ctx] = scala.scalanative.unsafe.alloc[v3_ext_ctx](1)
    def apply(
        flags: CInt,
        issuer_cert: Ptr[X509],
        subject_cert: Ptr[X509],
        subject_req: Ptr[X509_REQ],
        crl: Ptr[X509_CRL],
        db_meth: Ptr[X509V3_CONF_METHOD],
        db: Ptr[Byte],
        issuer_pkey: Ptr[EVP_PKEY],
    )(using Zone): Ptr[v3_ext_ctx] =
      val ____ptr = apply()
      (!____ptr).flags = flags
      (!____ptr).issuer_cert = issuer_cert
      (!____ptr).subject_cert = subject_cert
      (!____ptr).subject_req = subject_req
      (!____ptr).crl = crl
      (!____ptr).db_meth = db_meth
      (!____ptr).db = db
      (!____ptr).issuer_pkey = issuer_pkey
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type v3_ext_method = CStruct14[
    CInt,
    CInt,
    Ptr[ASN1_ITEM_EXP],
    X509V3_EXT_NEW,
    X509V3_EXT_FREE,
    X509V3_EXT_D2I,
    X509V3_EXT_I2D,
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
    Ptr[Byte],
  ]

  object v3_ext_method:
    given _tag: Tag[v3_ext_method] = Tag.materializeCStruct14Tag[
      CInt,
      CInt,
      Ptr[ASN1_ITEM_EXP],
      X509V3_EXT_NEW,
      X509V3_EXT_FREE,
      X509V3_EXT_D2I,
      X509V3_EXT_I2D,
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
      Ptr[Byte],
    ]

    export fields.*
    private[x509v3] object fields:
      extension (struct: v3_ext_method)
        inline def ext_nid: CInt = struct._1
        inline def ext_nid_=(value: CInt): Unit = !struct.at1 = value
        inline def ext_flags: CInt = struct._2
        inline def ext_flags_=(value: CInt): Unit = !struct.at2 = value
        inline def it: Ptr[ASN1_ITEM_EXP] = struct._3
        inline def it_=(value: Ptr[ASN1_ITEM_EXP]): Unit = !struct.at3 = value
        inline def ext_new: X509V3_EXT_NEW = struct._4
        inline def ext_new_=(value: X509V3_EXT_NEW): Unit = !struct.at4 = value
        inline def ext_free: X509V3_EXT_FREE = struct._5
        inline def ext_free_=(value: X509V3_EXT_FREE): Unit = !struct.at5 = value
        inline def d2i: X509V3_EXT_D2I = struct._6
        inline def d2i_=(value: X509V3_EXT_D2I): Unit = !struct.at6 = value
        inline def i2d: X509V3_EXT_I2D = struct._7
        inline def i2d_=(value: X509V3_EXT_I2D): Unit = !struct.at7 = value
        inline def i2s: X509V3_EXT_I2S = struct._8.asInstanceOf[X509V3_EXT_I2S]
        inline def i2s_=(value: X509V3_EXT_I2S): Unit = !struct.at8 = value.asInstanceOf[Ptr[Byte]]
        inline def s2i: X509V3_EXT_S2I = struct._9.asInstanceOf[X509V3_EXT_S2I]
        inline def s2i_=(value: X509V3_EXT_S2I): Unit = !struct.at9 = value.asInstanceOf[Ptr[Byte]]
        inline def i2v: X509V3_EXT_I2V = struct._10.asInstanceOf[X509V3_EXT_I2V]
        inline def i2v_=(value: X509V3_EXT_I2V): Unit = !struct.at10 = value.asInstanceOf[Ptr[Byte]]
        inline def v2i: X509V3_EXT_V2I = struct._11.asInstanceOf[X509V3_EXT_V2I]
        inline def v2i_=(value: X509V3_EXT_V2I): Unit = !struct.at11 = value.asInstanceOf[Ptr[Byte]]
        inline def i2r: X509V3_EXT_I2R = struct._12.asInstanceOf[X509V3_EXT_I2R]
        inline def i2r_=(value: X509V3_EXT_I2R): Unit = !struct.at12 = value.asInstanceOf[Ptr[Byte]]
        inline def r2i: X509V3_EXT_R2I = struct._13.asInstanceOf[X509V3_EXT_R2I]
        inline def r2i_=(value: X509V3_EXT_R2I): Unit = !struct.at13 = value.asInstanceOf[Ptr[Byte]]
        inline def usr_data: Ptr[Byte] = struct._14
        inline def usr_data_=(value: Ptr[Byte]): Unit = !struct.at14 = value
      end extension

    // Allocates v3_ext_method on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[v3_ext_method] = scala.scalanative.unsafe.alloc[v3_ext_method](1)
    def apply(
        ext_nid: CInt,
        ext_flags: CInt,
        it: Ptr[ASN1_ITEM_EXP],
        ext_new: X509V3_EXT_NEW,
        ext_free: X509V3_EXT_FREE,
        d2i: X509V3_EXT_D2I,
        i2d: X509V3_EXT_I2D,
        i2s: X509V3_EXT_I2S,
        s2i: X509V3_EXT_S2I,
        i2v: X509V3_EXT_I2V,
        v2i: X509V3_EXT_V2I,
        i2r: X509V3_EXT_I2R,
        r2i: X509V3_EXT_R2I,
        usr_data: Ptr[Byte],
    )(using Zone): Ptr[v3_ext_method] =
      val ____ptr = apply()
      (!____ptr).ext_nid = ext_nid
      (!____ptr).ext_flags = ext_flags
      (!____ptr).it = it
      (!____ptr).ext_new = ext_new
      (!____ptr).ext_free = ext_free
      (!____ptr).d2i = d2i
      (!____ptr).i2d = i2d
      (!____ptr).i2s = i2s
      (!____ptr).s2i = s2i
      (!____ptr).i2v = i2v
      (!____ptr).v2i = v2i
      (!____ptr).i2r = i2r
      (!____ptr).r2i = r2i
      (!____ptr).usr_data = usr_data
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type x509_purpose_st =
    CStruct7[CInt, CInt, CInt, CFuncPtr3[Ptr[Byte], Ptr[X509], CInt, CInt], CString, CString, Ptr[
      Byte,
    ]]

  object x509_purpose_st:
    given _tag: Tag[x509_purpose_st] = Tag.materializeCStruct7Tag[CInt, CInt, CInt, CFuncPtr3[Ptr[
      Byte,
    ], Ptr[X509], CInt, CInt], CString, CString, Ptr[Byte]]

    export fields.*
    private[x509v3] object fields:
      extension (struct: x509_purpose_st)
        inline def purpose: CInt = struct._1
        inline def purpose_=(value: CInt): Unit = !struct.at1 = value
        inline def trust: CInt = struct._2
        inline def trust_=(value: CInt): Unit = !struct.at2 = value
        inline def flags: CInt = struct._3
        inline def flags_=(value: CInt): Unit = !struct.at3 = value
        inline def check_purpose: CFuncPtr3[Ptr[x509_purpose_st], Ptr[X509], CInt, CInt] =
          struct._4.asInstanceOf[CFuncPtr3[Ptr[x509_purpose_st], Ptr[X509], CInt, CInt]]
        inline def check_purpose_=(
            value: CFuncPtr3[Ptr[x509_purpose_st], Ptr[X509], CInt, CInt],
        ): Unit = !struct.at4 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], Ptr[X509], CInt, CInt]]
        inline def name: CString = struct._5
        inline def name_=(value: CString): Unit = !struct.at5 = value
        inline def sname: CString = struct._6
        inline def sname_=(value: CString): Unit = !struct.at6 = value
        inline def usr_data: Ptr[Byte] = struct._7
        inline def usr_data_=(value: Ptr[Byte]): Unit = !struct.at7 = value
      end extension

    // Allocates x509_purpose_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[x509_purpose_st] =
      scala.scalanative.unsafe.alloc[x509_purpose_st](1)
    def apply(
        purpose: CInt,
        trust: CInt,
        flags: CInt,
        check_purpose: CFuncPtr3[Ptr[x509_purpose_st], Ptr[X509], CInt, CInt],
        name: CString,
        sname: CString,
        usr_data: Ptr[Byte],
    )(using Zone): Ptr[x509_purpose_st] =
      val ____ptr = apply()
      (!____ptr).purpose = purpose
      (!____ptr).trust = trust
      (!____ptr).flags = flags
      (!____ptr).check_purpose = check_purpose
      (!____ptr).name = name
      (!____ptr).sname = sname
      (!____ptr).usr_data = usr_data
      ____ptr
