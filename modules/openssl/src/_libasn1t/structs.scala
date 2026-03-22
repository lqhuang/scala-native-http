package snhttp.experimental.openssl._libasn1t

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_BIT_STRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_BIT_STRING:
  given _tag: Tag[ASN1_BIT_STRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_BIT_STRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_BIT_STRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_BIT_STRING] = scala.scalanative.unsafe.alloc[ASN1_BIT_STRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_BIT_STRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_BMPSTRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_BMPSTRING:
  given _tag: Tag[ASN1_BMPSTRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_BMPSTRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_BMPSTRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_BMPSTRING] = scala.scalanative.unsafe.alloc[ASN1_BMPSTRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_BMPSTRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_ENCODING = CStruct3[Ptr[CUnsignedChar], CLongInt, CInt]

object ASN1_ENCODING:
  given _tag: Tag[ASN1_ENCODING] = Tag.materializeCStruct3Tag[Ptr[CUnsignedChar], CLongInt, CInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_ENCODING)
      inline def enc: Ptr[CUnsignedChar] = struct._1
      inline def enc_=(value: Ptr[CUnsignedChar]): Unit = !struct.at1 = value
      inline def len: CLongInt = struct._2
      inline def len_=(value: CLongInt): Unit = !struct.at2 = value
      inline def modified: CInt = struct._3
      inline def modified_=(value: CInt): Unit = !struct.at3 = value
    end extension

  // Allocates ASN1_ENCODING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_ENCODING] = scala.scalanative.unsafe.alloc[ASN1_ENCODING](1)
  def apply(enc: Ptr[CUnsignedChar], len: CLongInt, modified: CInt)(using
      Zone,
  ): Ptr[ASN1_ENCODING] =
    val ____ptr = apply()
    (!____ptr).enc = enc
    (!____ptr).len = len
    (!____ptr).modified = modified
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_ENCODING_st = CStruct3[Ptr[CUnsignedChar], CLongInt, CInt]

object ASN1_ENCODING_st:
  given _tag: Tag[ASN1_ENCODING_st] = Tag.materializeCStruct3Tag[Ptr[CUnsignedChar], CLongInt, CInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_ENCODING_st)
      inline def enc: Ptr[CUnsignedChar] = struct._1
      inline def enc_=(value: Ptr[CUnsignedChar]): Unit = !struct.at1 = value
      inline def len: CLongInt = struct._2
      inline def len_=(value: CLongInt): Unit = !struct.at2 = value
      inline def modified: CInt = struct._3
      inline def modified_=(value: CInt): Unit = !struct.at3 = value
    end extension

  // Allocates ASN1_ENCODING_st on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_ENCODING_st] =
    scala.scalanative.unsafe.alloc[ASN1_ENCODING_st](1)
  def apply(enc: Ptr[CUnsignedChar], len: CLongInt, modified: CInt)(using
      Zone,
  ): Ptr[ASN1_ENCODING_st] =
    val ____ptr = apply()
    (!____ptr).enc = enc
    (!____ptr).len = len
    (!____ptr).modified = modified
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_ENUMERATED = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_ENUMERATED:
  given _tag: Tag[ASN1_ENUMERATED] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_ENUMERATED)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_ENUMERATED on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_ENUMERATED] = scala.scalanative.unsafe.alloc[ASN1_ENUMERATED](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_ENUMERATED] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_GENERALIZEDTIME = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_GENERALIZEDTIME:
  given _tag: Tag[ASN1_GENERALIZEDTIME] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_GENERALIZEDTIME)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_GENERALIZEDTIME on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_GENERALIZEDTIME] =
    scala.scalanative.unsafe.alloc[ASN1_GENERALIZEDTIME](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_GENERALIZEDTIME] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_GENERALSTRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_GENERALSTRING:
  given _tag: Tag[ASN1_GENERALSTRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_GENERALSTRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_GENERALSTRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_GENERALSTRING] =
    scala.scalanative.unsafe.alloc[ASN1_GENERALSTRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_GENERALSTRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_IA5STRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_IA5STRING:
  given _tag: Tag[ASN1_IA5STRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_IA5STRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_IA5STRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_IA5STRING] = scala.scalanative.unsafe.alloc[ASN1_IA5STRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_IA5STRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_INTEGER = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_INTEGER:
  given _tag: Tag[ASN1_INTEGER] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_INTEGER)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_INTEGER on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_INTEGER] = scala.scalanative.unsafe.alloc[ASN1_INTEGER](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_INTEGER] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type ASN1_ITEM = CStruct0

object ASN1_ITEM:
  given _tag: Tag[ASN1_ITEM] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type ASN1_OBJECT = CStruct0

object ASN1_OBJECT:
  given _tag: Tag[ASN1_OBJECT] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_OCTET_STRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_OCTET_STRING:
  given _tag: Tag[ASN1_OCTET_STRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_OCTET_STRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_OCTET_STRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_OCTET_STRING] =
    scala.scalanative.unsafe.alloc[ASN1_OCTET_STRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_OCTET_STRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type ASN1_PCTX = CStruct0

object ASN1_PCTX:
  given _tag: Tag[ASN1_PCTX] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_PRINTABLESTRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_PRINTABLESTRING:
  given _tag: Tag[ASN1_PRINTABLESTRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_PRINTABLESTRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_PRINTABLESTRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_PRINTABLESTRING] =
    scala.scalanative.unsafe.alloc[ASN1_PRINTABLESTRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_PRINTABLESTRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type ASN1_SCTX = CStruct0

object ASN1_SCTX:
  given _tag: Tag[ASN1_SCTX] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_SEQUENCE_ANY = CStruct0

object ASN1_SEQUENCE_ANY:
  given _tag: Tag[ASN1_SEQUENCE_ANY] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_STRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_STRING:
  given _tag: Tag[ASN1_STRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_STRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_STRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_STRING] = scala.scalanative.unsafe.alloc[ASN1_STRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_STRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_STRING_TABLE =
  CStruct5[CInt, CLongInt, CLongInt, CUnsignedLongInt, CUnsignedLongInt]

object ASN1_STRING_TABLE:
  given _tag: Tag[ASN1_STRING_TABLE] =
    Tag.materializeCStruct5Tag[CInt, CLongInt, CLongInt, CUnsignedLongInt, CUnsignedLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_STRING_TABLE)
      inline def nid: CInt = struct._1
      inline def nid_=(value: CInt): Unit = !struct.at1 = value
      inline def minsize: CLongInt = struct._2
      inline def minsize_=(value: CLongInt): Unit = !struct.at2 = value
      inline def maxsize: CLongInt = struct._3
      inline def maxsize_=(value: CLongInt): Unit = !struct.at3 = value
      inline def mask: CUnsignedLongInt = struct._4
      inline def mask_=(value: CUnsignedLongInt): Unit = !struct.at4 = value
      inline def flags: CUnsignedLongInt = struct._5
      inline def flags_=(value: CUnsignedLongInt): Unit = !struct.at5 = value
    end extension

  // Allocates ASN1_STRING_TABLE on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_STRING_TABLE] =
    scala.scalanative.unsafe.alloc[ASN1_STRING_TABLE](1)
  def apply(
      nid: CInt,
      minsize: CLongInt,
      maxsize: CLongInt,
      mask: CUnsignedLongInt,
      flags: CUnsignedLongInt,
  )(using Zone): Ptr[ASN1_STRING_TABLE] =
    val ____ptr = apply()
    (!____ptr).nid = nid
    (!____ptr).minsize = minsize
    (!____ptr).maxsize = maxsize
    (!____ptr).mask = mask
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_T61STRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_T61STRING:
  given _tag: Tag[ASN1_T61STRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_T61STRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_T61STRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_T61STRING] = scala.scalanative.unsafe.alloc[ASN1_T61STRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_T61STRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_TEMPLATE = CStruct0

object ASN1_TEMPLATE:
  given _tag: Tag[ASN1_TEMPLATE] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_TEMPLATE_st = CStruct0

object ASN1_TEMPLATE_st:
  given _tag: Tag[ASN1_TEMPLATE_st] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_TIME = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_TIME:
  given _tag: Tag[ASN1_TIME] = Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_TIME)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_TIME on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_TIME] = scala.scalanative.unsafe.alloc[ASN1_TIME](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_TIME] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_TLC = CStruct0

object ASN1_TLC:
  given _tag: Tag[ASN1_TLC] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_TLC_st = CStruct0

object ASN1_TLC_st:
  given _tag: Tag[ASN1_TLC_st] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_TYPE = CStruct2[CInt, ASN1_TYPE_Value]

object ASN1_TYPE:
  given _tag: Tag[ASN1_TYPE] = Tag.materializeCStruct2Tag[CInt, ASN1_TYPE_Value]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_TYPE)
      inline def `type`: CInt = struct._1
      inline def type_=(value: CInt): Unit = !struct.at1 = value
      inline def value: ASN1_TYPE_Value = struct._2
      inline def value_=(value: ASN1_TYPE_Value): Unit = !struct.at2 = value
    end extension

  // Allocates ASN1_TYPE on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_TYPE] = scala.scalanative.unsafe.alloc[ASN1_TYPE](1)
  def apply(`type`: CInt, value: ASN1_TYPE_Value)(using Zone): Ptr[ASN1_TYPE] =
    val ____ptr = apply()
    (!____ptr).`type` = `type`
    (!____ptr).value = value
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_TYPE_Value = CArray[Byte, Nat._8]
object ASN1_TYPE_Value:
  given _tag: Tag[ASN1_TYPE_Value] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

  def apply()(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    ___ptr

  @scala.annotation.targetName("apply_ptr")
  def apply(ptr: CString)(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[CString]].update(0, ptr)
    ___ptr

  @scala.annotation.targetName("apply_boolean")
  def apply(boolean: ASN1_BOOLEAN)(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[ASN1_BOOLEAN]].update(0, boolean)
    ___ptr

  @scala.annotation.targetName("apply_asn1_string")
  def apply(asn1_string: Ptr[ASN1_STRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]].update(0, asn1_string)
    ___ptr

  @scala.annotation.targetName("apply_object")
  def apply(`object`: Ptr[ASN1_OBJECT])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]].update(0, `object`)
    ___ptr

  @scala.annotation.targetName("apply_integer")
  def apply(integer: Ptr[ASN1_INTEGER])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]].update(0, integer)
    ___ptr

  @scala.annotation.targetName("apply_enumerated")
  def apply(enumerated: Ptr[ASN1_ENUMERATED])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_ENUMERATED]]].update(0, enumerated)
    ___ptr

  @scala.annotation.targetName("apply_bit_string")
  def apply(bit_string: Ptr[ASN1_BIT_STRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]].update(0, bit_string)
    ___ptr

  @scala.annotation.targetName("apply_octet_string")
  def apply(octet_string: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]].update(0, octet_string)
    ___ptr

  @scala.annotation.targetName("apply_printablestring")
  def apply(printablestring: Ptr[ASN1_PRINTABLESTRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_PRINTABLESTRING]]].update(0, printablestring)
    ___ptr

  @scala.annotation.targetName("apply_t61string")
  def apply(t61string: Ptr[ASN1_T61STRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_T61STRING]]].update(0, t61string)
    ___ptr

  @scala.annotation.targetName("apply_ia5string")
  def apply(ia5string: Ptr[ASN1_IA5STRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, ia5string)
    ___ptr

  @scala.annotation.targetName("apply_generalstring")
  def apply(generalstring: Ptr[ASN1_GENERALSTRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALSTRING]]].update(0, generalstring)
    ___ptr

  @scala.annotation.targetName("apply_bmpstring")
  def apply(bmpstring: Ptr[ASN1_BMPSTRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_BMPSTRING]]].update(0, bmpstring)
    ___ptr

  @scala.annotation.targetName("apply_universalstring")
  def apply(universalstring: Ptr[ASN1_UNIVERSALSTRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_UNIVERSALSTRING]]].update(0, universalstring)
    ___ptr

  @scala.annotation.targetName("apply_utctime")
  def apply(utctime: Ptr[ASN1_UTCTIME])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTCTIME]]].update(0, utctime)
    ___ptr

  @scala.annotation.targetName("apply_generalizedtime")
  def apply(generalizedtime: Ptr[ASN1_GENERALIZEDTIME])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALIZEDTIME]]].update(0, generalizedtime)
    ___ptr

  @scala.annotation.targetName("apply_visiblestring")
  def apply(visiblestring: Ptr[ASN1_VISIBLESTRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_VISIBLESTRING]]].update(0, visiblestring)
    ___ptr

  @scala.annotation.targetName("apply_utf8string")
  def apply(utf8string: Ptr[ASN1_UTF8STRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTF8STRING]]].update(0, utf8string)
    ___ptr

  @scala.annotation.targetName("apply_set")
  def apply(set: Ptr[ASN1_STRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]].update(0, set)
    ___ptr

  @scala.annotation.targetName("apply_sequence")
  def apply(sequence: Ptr[ASN1_STRING])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]].update(0, sequence)
    ___ptr

  @scala.annotation.targetName("apply_asn1_value")
  def apply(asn1_value: Ptr[ASN1_VALUE])(using Zone): Ptr[ASN1_TYPE_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[ASN1_TYPE_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_VALUE]]].update(0, asn1_value)
    ___ptr

  extension (struct: ASN1_TYPE_Value)
    inline def ptr: CString = !struct.at(0).asInstanceOf[Ptr[CString]]
    inline def ptr_=(value: CString): Unit = !struct.at(0).asInstanceOf[Ptr[CString]] = value
    inline def boolean: ASN1_BOOLEAN = !struct.at(0).asInstanceOf[Ptr[ASN1_BOOLEAN]]
    inline def boolean_=(value: ASN1_BOOLEAN): Unit =
      !struct.at(0).asInstanceOf[Ptr[ASN1_BOOLEAN]] = value
    inline def asn1_string: Ptr[ASN1_STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]]
    inline def asn1_string_=(value: Ptr[ASN1_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]] = value
    inline def `object`: Ptr[ASN1_OBJECT] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]]
    inline def object_=(value: Ptr[ASN1_OBJECT]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]] = value
    inline def integer: Ptr[ASN1_INTEGER] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]]
    inline def integer_=(value: Ptr[ASN1_INTEGER]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]] = value
    inline def enumerated: Ptr[ASN1_ENUMERATED] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_ENUMERATED]]]
    inline def enumerated_=(value: Ptr[ASN1_ENUMERATED]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_ENUMERATED]]] = value
    inline def bit_string: Ptr[ASN1_BIT_STRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]]
    inline def bit_string_=(value: Ptr[ASN1_BIT_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]] = value
    inline def octet_string: Ptr[ASN1_OCTET_STRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]]
    inline def octet_string_=(value: Ptr[ASN1_OCTET_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]] = value
    inline def printablestring: Ptr[ASN1_PRINTABLESTRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_PRINTABLESTRING]]]
    inline def printablestring_=(value: Ptr[ASN1_PRINTABLESTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_PRINTABLESTRING]]] = value
    inline def t61string: Ptr[ASN1_T61STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_T61STRING]]]
    inline def t61string_=(value: Ptr[ASN1_T61STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_T61STRING]]] = value
    inline def ia5string: Ptr[ASN1_IA5STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
    inline def ia5string_=(value: Ptr[ASN1_IA5STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
    inline def generalstring: Ptr[ASN1_GENERALSTRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALSTRING]]]
    inline def generalstring_=(value: Ptr[ASN1_GENERALSTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALSTRING]]] = value
    inline def bmpstring: Ptr[ASN1_BMPSTRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BMPSTRING]]]
    inline def bmpstring_=(value: Ptr[ASN1_BMPSTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BMPSTRING]]] = value
    inline def universalstring: Ptr[ASN1_UNIVERSALSTRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UNIVERSALSTRING]]]
    inline def universalstring_=(value: Ptr[ASN1_UNIVERSALSTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UNIVERSALSTRING]]] = value
    inline def utctime: Ptr[ASN1_UTCTIME] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTCTIME]]]
    inline def utctime_=(value: Ptr[ASN1_UTCTIME]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTCTIME]]] = value
    inline def generalizedtime: Ptr[ASN1_GENERALIZEDTIME] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALIZEDTIME]]]
    inline def generalizedtime_=(value: Ptr[ASN1_GENERALIZEDTIME]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALIZEDTIME]]] = value
    inline def visiblestring: Ptr[ASN1_VISIBLESTRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_VISIBLESTRING]]]
    inline def visiblestring_=(value: Ptr[ASN1_VISIBLESTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_VISIBLESTRING]]] = value
    inline def utf8string: Ptr[ASN1_UTF8STRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTF8STRING]]]
    inline def utf8string_=(value: Ptr[ASN1_UTF8STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTF8STRING]]] = value
    inline def set: Ptr[ASN1_STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]]
    inline def set_=(value: Ptr[ASN1_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]] = value
    inline def sequence: Ptr[ASN1_STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]]
    inline def sequence_=(value: Ptr[ASN1_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]] = value
    inline def asn1_value: Ptr[ASN1_VALUE] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_VALUE]]]
    inline def asn1_value_=(value: Ptr[ASN1_VALUE]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_VALUE]]] = value

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_UNIVERSALSTRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_UNIVERSALSTRING:
  given _tag: Tag[ASN1_UNIVERSALSTRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_UNIVERSALSTRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_UNIVERSALSTRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_UNIVERSALSTRING] =
    scala.scalanative.unsafe.alloc[ASN1_UNIVERSALSTRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_UNIVERSALSTRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_UTCTIME = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_UTCTIME:
  given _tag: Tag[ASN1_UTCTIME] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_UTCTIME)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_UTCTIME on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_UTCTIME] = scala.scalanative.unsafe.alloc[ASN1_UTCTIME](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_UTCTIME] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_UTF8STRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_UTF8STRING:
  given _tag: Tag[ASN1_UTF8STRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_UTF8STRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_UTF8STRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_UTF8STRING] = scala.scalanative.unsafe.alloc[ASN1_UTF8STRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_UTF8STRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_VALUE = CStruct0

object ASN1_VALUE:
  given _tag: Tag[ASN1_VALUE] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_VALUE_st = CStruct0

object ASN1_VALUE_st:
  given _tag: Tag[ASN1_VALUE_st] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type ASN1_VISIBLESTRING = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object ASN1_VISIBLESTRING:
  given _tag: Tag[ASN1_VISIBLESTRING] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: ASN1_VISIBLESTRING)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates ASN1_VISIBLESTRING on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[ASN1_VISIBLESTRING] =
    scala.scalanative.unsafe.alloc[ASN1_VISIBLESTRING](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[ASN1_VISIBLESTRING] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type BIGNUM = CStruct0

object BIGNUM:
  given _tag: Tag[BIGNUM] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type BIO = CStruct0

object BIO:
  given _tag: Tag[BIO] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/bio.h
 */
opaque type BIO_METHOD = CStruct0

object BIO_METHOD:
  given _tag: Tag[BIO_METHOD] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type BIT_STRING_BITNAME = CStruct3[CInt, CString, CString]

object BIT_STRING_BITNAME:
  given _tag: Tag[BIT_STRING_BITNAME] = Tag.materializeCStruct3Tag[CInt, CString, CString]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: BIT_STRING_BITNAME)
      inline def bitnum: CInt = struct._1
      inline def bitnum_=(value: CInt): Unit = !struct.at1 = value
      inline def lname: CString = struct._2
      inline def lname_=(value: CString): Unit = !struct.at2 = value
      inline def sname: CString = struct._3
      inline def sname_=(value: CString): Unit = !struct.at3 = value
    end extension

  // Allocates BIT_STRING_BITNAME on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[BIT_STRING_BITNAME] =
    scala.scalanative.unsafe.alloc[BIT_STRING_BITNAME](1)
  def apply(bitnum: CInt, lname: CString, sname: CString)(using Zone): Ptr[BIT_STRING_BITNAME] =
    val ____ptr = apply()
    (!____ptr).bitnum = bitnum
    (!____ptr).lname = lname
    (!____ptr).sname = sname
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type BIT_STRING_BITNAME_st = CStruct3[CInt, CString, CString]

object BIT_STRING_BITNAME_st:
  given _tag: Tag[BIT_STRING_BITNAME_st] = Tag.materializeCStruct3Tag[CInt, CString, CString]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: BIT_STRING_BITNAME_st)
      inline def bitnum: CInt = struct._1
      inline def bitnum_=(value: CInt): Unit = !struct.at1 = value
      inline def lname: CString = struct._2
      inline def lname_=(value: CString): Unit = !struct.at2 = value
      inline def sname: CString = struct._3
      inline def sname_=(value: CString): Unit = !struct.at3 = value
    end extension

  // Allocates BIT_STRING_BITNAME_st on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[BIT_STRING_BITNAME_st] =
    scala.scalanative.unsafe.alloc[BIT_STRING_BITNAME_st](1)
  def apply(bitnum: CInt, lname: CString, sname: CString)(using Zone): Ptr[BIT_STRING_BITNAME_st] =
    val ____ptr = apply()
    (!____ptr).bitnum = bitnum
    (!____ptr).lname = lname
    (!____ptr).sname = sname
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type CONF = CStruct0

object CONF:
  given _tag: Tag[CONF] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type EVP_MD = CStruct0

object EVP_MD:
  given _tag: Tag[EVP_MD] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type EVP_PKEY = CStruct0

object EVP_PKEY:
  given _tag: Tag[EVP_PKEY] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type OSSL_LIB_CTX = CStruct0

object OSSL_LIB_CTX:
  given _tag: Tag[OSSL_LIB_CTX] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type X509V3_CTX = CStruct0

object X509V3_CTX:
  given _tag: Tag[X509V3_CTX] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type X509_ALGOR = CStruct0

object X509_ALGOR:
  given _tag: Tag[X509_ALGOR] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type asn1_string_st = CStruct4[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

object asn1_string_st:
  given _tag: Tag[asn1_string_st] =
    Tag.materializeCStruct4Tag[CInt, CInt, Ptr[CUnsignedChar], CLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: asn1_string_st)
      inline def length: CInt = struct._1
      inline def length_=(value: CInt): Unit = !struct.at1 = value
      inline def `type`: CInt = struct._2
      inline def type_=(value: CInt): Unit = !struct.at2 = value
      inline def data: Ptr[CUnsignedChar] = struct._3
      inline def data_=(value: Ptr[CUnsignedChar]): Unit = !struct.at3 = value
      inline def flags: CLongInt = struct._4
      inline def flags_=(value: CLongInt): Unit = !struct.at4 = value
    end extension

  // Allocates asn1_string_st on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[asn1_string_st] = scala.scalanative.unsafe.alloc[asn1_string_st](1)
  def apply(length: CInt, `type`: CInt, data: Ptr[CUnsignedChar], flags: CLongInt)(using
      Zone,
  ): Ptr[asn1_string_st] =
    val ____ptr = apply()
    (!____ptr).length = length
    (!____ptr).`type` = `type`
    (!____ptr).data = data
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type asn1_string_table_st =
  CStruct5[CInt, CLongInt, CLongInt, CUnsignedLongInt, CUnsignedLongInt]

object asn1_string_table_st:
  given _tag: Tag[asn1_string_table_st] =
    Tag.materializeCStruct5Tag[CInt, CLongInt, CLongInt, CUnsignedLongInt, CUnsignedLongInt]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: asn1_string_table_st)
      inline def nid: CInt = struct._1
      inline def nid_=(value: CInt): Unit = !struct.at1 = value
      inline def minsize: CLongInt = struct._2
      inline def minsize_=(value: CLongInt): Unit = !struct.at2 = value
      inline def maxsize: CLongInt = struct._3
      inline def maxsize_=(value: CLongInt): Unit = !struct.at3 = value
      inline def mask: CUnsignedLongInt = struct._4
      inline def mask_=(value: CUnsignedLongInt): Unit = !struct.at4 = value
      inline def flags: CUnsignedLongInt = struct._5
      inline def flags_=(value: CUnsignedLongInt): Unit = !struct.at5 = value
    end extension

  // Allocates asn1_string_table_st on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[asn1_string_table_st] =
    scala.scalanative.unsafe.alloc[asn1_string_table_st](1)
  def apply(
      nid: CInt,
      minsize: CLongInt,
      maxsize: CLongInt,
      mask: CUnsignedLongInt,
      flags: CUnsignedLongInt,
  )(using Zone): Ptr[asn1_string_table_st] =
    val ____ptr = apply()
    (!____ptr).nid = nid
    (!____ptr).minsize = minsize
    (!____ptr).maxsize = maxsize
    (!____ptr).mask = mask
    (!____ptr).flags = flags
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type asn1_type_st = CStruct2[CInt, asn1_type_st_Value]

object asn1_type_st:
  given _tag: Tag[asn1_type_st] = Tag.materializeCStruct2Tag[CInt, asn1_type_st_Value]

  export fields.*
  private[_libasn1t] object fields:
    extension (struct: asn1_type_st)
      inline def `type`: CInt = struct._1
      inline def type_=(value: CInt): Unit = !struct.at1 = value
      inline def value: asn1_type_st_Value = struct._2
      inline def value_=(value: asn1_type_st_Value): Unit = !struct.at2 = value
    end extension

  // Allocates asn1_type_st on the heap – fields are not initalised or zeroed out
  def apply()(using Zone): Ptr[asn1_type_st] = scala.scalanative.unsafe.alloc[asn1_type_st](1)
  def apply(`type`: CInt, value: asn1_type_st_Value)(using Zone): Ptr[asn1_type_st] =
    val ____ptr = apply()
    (!____ptr).`type` = `type`
    (!____ptr).value = value
    ____ptr

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type asn1_type_st_Value = CArray[Byte, Nat._8]
object asn1_type_st_Value:
  given _tag: Tag[asn1_type_st_Value] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

  def apply()(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    ___ptr

  @scala.annotation.targetName("apply_ptr")
  def apply(ptr: CString)(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[CString]].update(0, ptr)
    ___ptr

  @scala.annotation.targetName("apply_boolean")
  def apply(boolean: ASN1_BOOLEAN)(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[ASN1_BOOLEAN]].update(0, boolean)
    ___ptr

  @scala.annotation.targetName("apply_asn1_string")
  def apply(asn1_string: Ptr[ASN1_STRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]].update(0, asn1_string)
    ___ptr

  @scala.annotation.targetName("apply_object")
  def apply(`object`: Ptr[ASN1_OBJECT])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]].update(0, `object`)
    ___ptr

  @scala.annotation.targetName("apply_integer")
  def apply(integer: Ptr[ASN1_INTEGER])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]].update(0, integer)
    ___ptr

  @scala.annotation.targetName("apply_enumerated")
  def apply(enumerated: Ptr[ASN1_ENUMERATED])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_ENUMERATED]]].update(0, enumerated)
    ___ptr

  @scala.annotation.targetName("apply_bit_string")
  def apply(bit_string: Ptr[ASN1_BIT_STRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]].update(0, bit_string)
    ___ptr

  @scala.annotation.targetName("apply_octet_string")
  def apply(octet_string: Ptr[ASN1_OCTET_STRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]].update(0, octet_string)
    ___ptr

  @scala.annotation.targetName("apply_printablestring")
  def apply(printablestring: Ptr[ASN1_PRINTABLESTRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_PRINTABLESTRING]]].update(0, printablestring)
    ___ptr

  @scala.annotation.targetName("apply_t61string")
  def apply(t61string: Ptr[ASN1_T61STRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_T61STRING]]].update(0, t61string)
    ___ptr

  @scala.annotation.targetName("apply_ia5string")
  def apply(ia5string: Ptr[ASN1_IA5STRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]].update(0, ia5string)
    ___ptr

  @scala.annotation.targetName("apply_generalstring")
  def apply(generalstring: Ptr[ASN1_GENERALSTRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALSTRING]]].update(0, generalstring)
    ___ptr

  @scala.annotation.targetName("apply_bmpstring")
  def apply(bmpstring: Ptr[ASN1_BMPSTRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_BMPSTRING]]].update(0, bmpstring)
    ___ptr

  @scala.annotation.targetName("apply_universalstring")
  def apply(universalstring: Ptr[ASN1_UNIVERSALSTRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_UNIVERSALSTRING]]].update(0, universalstring)
    ___ptr

  @scala.annotation.targetName("apply_utctime")
  def apply(utctime: Ptr[ASN1_UTCTIME])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTCTIME]]].update(0, utctime)
    ___ptr

  @scala.annotation.targetName("apply_generalizedtime")
  def apply(generalizedtime: Ptr[ASN1_GENERALIZEDTIME])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALIZEDTIME]]].update(0, generalizedtime)
    ___ptr

  @scala.annotation.targetName("apply_visiblestring")
  def apply(visiblestring: Ptr[ASN1_VISIBLESTRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_VISIBLESTRING]]].update(0, visiblestring)
    ___ptr

  @scala.annotation.targetName("apply_utf8string")
  def apply(utf8string: Ptr[ASN1_UTF8STRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTF8STRING]]].update(0, utf8string)
    ___ptr

  @scala.annotation.targetName("apply_set")
  def apply(set: Ptr[ASN1_STRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]].update(0, set)
    ___ptr

  @scala.annotation.targetName("apply_sequence")
  def apply(sequence: Ptr[ASN1_STRING])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]].update(0, sequence)
    ___ptr

  @scala.annotation.targetName("apply_asn1_value")
  def apply(asn1_value: Ptr[ASN1_VALUE])(using Zone): Ptr[asn1_type_st_Value] =
    val ___ptr = _root_.scala.scalanative.unsafe.alloc[asn1_type_st_Value](1)
    val un = !___ptr
    un.at(0).asInstanceOf[Ptr[Ptr[ASN1_VALUE]]].update(0, asn1_value)
    ___ptr

  extension (struct: asn1_type_st_Value)
    inline def ptr: CString = !struct.at(0).asInstanceOf[Ptr[CString]]
    inline def ptr_=(value: CString): Unit = !struct.at(0).asInstanceOf[Ptr[CString]] = value
    inline def boolean: ASN1_BOOLEAN = !struct.at(0).asInstanceOf[Ptr[ASN1_BOOLEAN]]
    inline def boolean_=(value: ASN1_BOOLEAN): Unit =
      !struct.at(0).asInstanceOf[Ptr[ASN1_BOOLEAN]] = value
    inline def asn1_string: Ptr[ASN1_STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]]
    inline def asn1_string_=(value: Ptr[ASN1_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]] = value
    inline def `object`: Ptr[ASN1_OBJECT] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]]
    inline def object_=(value: Ptr[ASN1_OBJECT]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OBJECT]]] = value
    inline def integer: Ptr[ASN1_INTEGER] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]]
    inline def integer_=(value: Ptr[ASN1_INTEGER]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_INTEGER]]] = value
    inline def enumerated: Ptr[ASN1_ENUMERATED] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_ENUMERATED]]]
    inline def enumerated_=(value: Ptr[ASN1_ENUMERATED]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_ENUMERATED]]] = value
    inline def bit_string: Ptr[ASN1_BIT_STRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]]
    inline def bit_string_=(value: Ptr[ASN1_BIT_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BIT_STRING]]] = value
    inline def octet_string: Ptr[ASN1_OCTET_STRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]]
    inline def octet_string_=(value: Ptr[ASN1_OCTET_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_OCTET_STRING]]] = value
    inline def printablestring: Ptr[ASN1_PRINTABLESTRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_PRINTABLESTRING]]]
    inline def printablestring_=(value: Ptr[ASN1_PRINTABLESTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_PRINTABLESTRING]]] = value
    inline def t61string: Ptr[ASN1_T61STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_T61STRING]]]
    inline def t61string_=(value: Ptr[ASN1_T61STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_T61STRING]]] = value
    inline def ia5string: Ptr[ASN1_IA5STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]]
    inline def ia5string_=(value: Ptr[ASN1_IA5STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_IA5STRING]]] = value
    inline def generalstring: Ptr[ASN1_GENERALSTRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALSTRING]]]
    inline def generalstring_=(value: Ptr[ASN1_GENERALSTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALSTRING]]] = value
    inline def bmpstring: Ptr[ASN1_BMPSTRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BMPSTRING]]]
    inline def bmpstring_=(value: Ptr[ASN1_BMPSTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_BMPSTRING]]] = value
    inline def universalstring: Ptr[ASN1_UNIVERSALSTRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UNIVERSALSTRING]]]
    inline def universalstring_=(value: Ptr[ASN1_UNIVERSALSTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UNIVERSALSTRING]]] = value
    inline def utctime: Ptr[ASN1_UTCTIME] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTCTIME]]]
    inline def utctime_=(value: Ptr[ASN1_UTCTIME]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTCTIME]]] = value
    inline def generalizedtime: Ptr[ASN1_GENERALIZEDTIME] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALIZEDTIME]]]
    inline def generalizedtime_=(value: Ptr[ASN1_GENERALIZEDTIME]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_GENERALIZEDTIME]]] = value
    inline def visiblestring: Ptr[ASN1_VISIBLESTRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_VISIBLESTRING]]]
    inline def visiblestring_=(value: Ptr[ASN1_VISIBLESTRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_VISIBLESTRING]]] = value
    inline def utf8string: Ptr[ASN1_UTF8STRING] =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTF8STRING]]]
    inline def utf8string_=(value: Ptr[ASN1_UTF8STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_UTF8STRING]]] = value
    inline def set: Ptr[ASN1_STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]]
    inline def set_=(value: Ptr[ASN1_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]] = value
    inline def sequence: Ptr[ASN1_STRING] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]]
    inline def sequence_=(value: Ptr[ASN1_STRING]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_STRING]]] = value
    inline def asn1_value: Ptr[ASN1_VALUE] = !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_VALUE]]]
    inline def asn1_value_=(value: Ptr[ASN1_VALUE]): Unit =
      !struct.at(0).asInstanceOf[Ptr[Ptr[ASN1_VALUE]]] = value

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
opaque type stack_st_X509_ALGOR = CStruct0

object stack_st_X509_ALGOR:
  given _tag: Tag[stack_st_X509_ALGOR] = Tag.materializeCStruct0Tag
