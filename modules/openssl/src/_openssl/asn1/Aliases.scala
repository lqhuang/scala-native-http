package snhttp.experimental.openssl
package _openssl.asn1

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.asn1.Structs.ASN1_ITEM

object Aliases:

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type ASN1_NULL = CInt
  object ASN1_NULL:
    given _tag: Tag[ASN1_NULL] = Tag.Int
    inline def apply(inline o: CInt): ASN1_NULL = o
    extension (v: ASN1_NULL) inline def value: CInt = v

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
    given _tag: Tag[i2d_of_void] =
      Tag.materializeCFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt]
    inline def apply(inline o: CFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt]): i2d_of_void = o
    extension (v: i2d_of_void)
      inline def value: CFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt] = v
