package snhttp.experimental.openssl._libasn1t

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type ASN1_BOOLEAN = CInt
object ASN1_BOOLEAN:
  given _tag: Tag[ASN1_BOOLEAN] = Tag.Int
  inline def apply(inline o: CInt): ASN1_BOOLEAN = o
  extension (v: ASN1_BOOLEAN) inline def value: CInt = v

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
type ASN1_ITEM_EXP = CFuncPtr0[Ptr[ASN1_ITEM]]
object ASN1_ITEM_EXP:
  given _tag: Tag[ASN1_ITEM_EXP] = Tag.materializeCFuncPtr0[Ptr[ASN1_ITEM]]
  inline def apply(inline o: CFuncPtr0[Ptr[ASN1_ITEM]]): ASN1_ITEM_EXP = o
  extension (v: ASN1_ITEM_EXP) inline def value: CFuncPtr0[Ptr[ASN1_ITEM]] = v

type FILE = libc.stdio.FILE
object FILE:
  val _tag: Tag[FILE] = summon[Tag[libc.stdio.FILE]]
  inline def apply(inline o: libc.stdio.FILE): FILE = o
  extension (v: FILE) inline def value: libc.stdio.FILE = v

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
type d2i_of_void = CFuncPtr3[Ptr[Ptr[Byte]], Ptr[Ptr[CUnsignedChar]], CLongInt, Ptr[Byte]]
object d2i_of_void:
  given _tag: Tag[d2i_of_void] =
    Tag.materializeCFuncPtr3[Ptr[Ptr[Byte]], Ptr[Ptr[CUnsignedChar]], CLongInt, Ptr[Byte]]
  inline def apply(
      inline o: CFuncPtr3[Ptr[Ptr[Byte]], Ptr[Ptr[CUnsignedChar]], CLongInt, Ptr[Byte]],
  ): d2i_of_void = o
  extension (v: d2i_of_void)
    inline def value: CFuncPtr3[Ptr[Ptr[Byte]], Ptr[Ptr[CUnsignedChar]], CLongInt, Ptr[Byte]] = v

/**
 * [bindgen] header: /usr/include/openssl/asn1.h
 */
type i2d_of_void = CFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt]
object i2d_of_void:
  given _tag: Tag[i2d_of_void] = Tag.materializeCFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt]
  inline def apply(inline o: CFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt]): i2d_of_void = o
  extension (v: i2d_of_void)
    inline def value: CFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt] = v

type int64_t = scala.Long
object int64_t:
  val _tag: Tag[int64_t] = summon[Tag[scala.Long]]
  inline def apply(inline o: scala.Long): int64_t = o
  extension (v: int64_t) inline def value: scala.Long = v

type size_t = libc.stddef.size_t
object size_t:
  val _tag: Tag[size_t] = summon[Tag[libc.stddef.size_t]]
  inline def apply(inline o: libc.stddef.size_t): size_t = o
  extension (v: size_t) inline def value: libc.stddef.size_t = v

type time_t = posix.sys.types.time_t
object time_t:
  val _tag: Tag[time_t] = summon[Tag[posix.sys.types.time_t]]
  inline def apply(inline o: posix.sys.types.time_t): time_t = o
  extension (v: time_t) inline def value: posix.sys.types.time_t = v

type tm = posix.time.tm
object tm:
  val _tag: Tag[tm] = summon[Tag[posix.time.tm]]
  inline def apply(inline o: posix.time.tm): tm = o
  extension (v: tm) inline def value: posix.time.tm = v

type uint64_t = scala.scalanative.unsigned.ULong
object uint64_t:
  val _tag: Tag[uint64_t] = summon[Tag[scala.scalanative.unsigned.ULong]]
  inline def apply(inline o: scala.scalanative.unsigned.ULong): uint64_t = o
  extension (v: uint64_t) inline def value: scala.scalanative.unsigned.ULong = v
