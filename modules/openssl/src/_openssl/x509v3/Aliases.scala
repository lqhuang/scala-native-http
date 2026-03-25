package snhttp.experimental.openssl
package _openssl.x509v3

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.asn1.Types.BIT_STRING_BITNAME
import _root_.snhttp.experimental.openssl._openssl.bio.Types.BIO
import _root_.snhttp.experimental.openssl._openssl.safestack.stack_st_CONF_VALUE

import Structs.{v3_ext_ctx, v3_ext_method}

object Aliases:

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  type ENUMERATED_NAMES = BIT_STRING_BITNAME
  object ENUMERATED_NAMES:
    given _tag: Tag[ENUMERATED_NAMES] = BIT_STRING_BITNAME._tag
    inline def apply(inline o: BIT_STRING_BITNAME): ENUMERATED_NAMES = o
    extension (v: ENUMERATED_NAMES) inline def value: BIT_STRING_BITNAME = v

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_D2I = CFuncPtr3[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CLongInt, Ptr[Byte]]
  object X509V3_EXT_D2I:
    given _tag: Tag[X509V3_EXT_D2I] =
      Tag.materializeCFuncPtr3[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CLongInt, Ptr[Byte]]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_D2I =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CLongInt, Ptr[Byte]],
    ): X509V3_EXT_D2I = o
    extension (v: X509V3_EXT_D2I)
      inline def value: CFuncPtr3[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CLongInt, Ptr[Byte]] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_FREE = CFuncPtr1[Ptr[Byte], Unit]
  object X509V3_EXT_FREE:
    given _tag: Tag[X509V3_EXT_FREE] = Tag.materializeCFuncPtr1[Ptr[Byte], Unit]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_FREE =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(inline o: CFuncPtr1[Ptr[Byte], Unit]): X509V3_EXT_FREE = o
    extension (v: X509V3_EXT_FREE)
      inline def value: CFuncPtr1[Ptr[Byte], Unit] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_I2D = CFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt]
  object X509V3_EXT_I2D:
    given _tag: Tag[X509V3_EXT_I2D] =
      Tag.materializeCFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_I2D =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt],
    ): X509V3_EXT_I2D = o
    extension (v: X509V3_EXT_I2D)
      inline def value: CFuncPtr2[Ptr[Byte], Ptr[Ptr[CUnsignedChar]], CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_I2R = CFuncPtr4[Ptr[v3_ext_method], Ptr[Byte], Ptr[BIO], CInt, CInt]
  object X509V3_EXT_I2R:
    given _tag: Tag[X509V3_EXT_I2R] =
      Tag.materializeCFuncPtr4[Ptr[v3_ext_method], Ptr[Byte], Ptr[BIO], CInt, CInt]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_I2R =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr4[Ptr[v3_ext_method], Ptr[Byte], Ptr[BIO], CInt, CInt],
    ): X509V3_EXT_I2R = o
    extension (v: X509V3_EXT_I2R)
      inline def value: CFuncPtr4[Ptr[v3_ext_method], Ptr[Byte], Ptr[BIO], CInt, CInt] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_I2S = CFuncPtr2[Ptr[v3_ext_method], Ptr[Byte], CString]
  object X509V3_EXT_I2S:
    given _tag: Tag[X509V3_EXT_I2S] =
      Tag.materializeCFuncPtr2[Ptr[v3_ext_method], Ptr[Byte], CString]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_I2S =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(inline o: CFuncPtr2[Ptr[v3_ext_method], Ptr[Byte], CString]): X509V3_EXT_I2S =
      o
    extension (v: X509V3_EXT_I2S)
      inline def value: CFuncPtr2[Ptr[v3_ext_method], Ptr[Byte], CString] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_I2V =
    CFuncPtr3[Ptr[v3_ext_method], Ptr[Byte], Ptr[stack_st_CONF_VALUE], Ptr[stack_st_CONF_VALUE]]
  object X509V3_EXT_I2V:
    given _tag: Tag[X509V3_EXT_I2V] = Tag.materializeCFuncPtr3[Ptr[v3_ext_method], Ptr[Byte], Ptr[
      stack_st_CONF_VALUE,
    ], Ptr[stack_st_CONF_VALUE]]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_I2V =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[v3_ext_method], Ptr[Byte], Ptr[stack_st_CONF_VALUE], Ptr[
          stack_st_CONF_VALUE,
        ]],
    ): X509V3_EXT_I2V = o
    extension (v: X509V3_EXT_I2V)
      inline def value: CFuncPtr3[Ptr[v3_ext_method], Ptr[Byte], Ptr[stack_st_CONF_VALUE], Ptr[
        stack_st_CONF_VALUE,
      ]] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_NEW = CFuncPtr0[Ptr[Byte]]
  object X509V3_EXT_NEW:
    given _tag: Tag[X509V3_EXT_NEW] = Tag.materializeCFuncPtr0[Ptr[Byte]]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_NEW =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(inline o: CFuncPtr0[Ptr[Byte]]): X509V3_EXT_NEW = o
    extension (v: X509V3_EXT_NEW)
      inline def value: CFuncPtr0[Ptr[Byte]] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_R2I = CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], CString, Ptr[Byte]]
  object X509V3_EXT_R2I:
    given _tag: Tag[X509V3_EXT_R2I] =
      Tag.materializeCFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], CString, Ptr[Byte]]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_R2I =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], CString, Ptr[Byte]],
    ): X509V3_EXT_R2I = o
    extension (v: X509V3_EXT_R2I)
      inline def value: CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], CString, Ptr[Byte]] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_S2I = CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], CString, Ptr[Byte]]
  object X509V3_EXT_S2I:
    given _tag: Tag[X509V3_EXT_S2I] =
      Tag.materializeCFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], CString, Ptr[Byte]]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_S2I =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], CString, Ptr[Byte]],
    ): X509V3_EXT_S2I = o
    extension (v: X509V3_EXT_S2I)
      inline def value: CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], CString, Ptr[Byte]] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/x509v3.h
   */
  opaque type X509V3_EXT_V2I =
    CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], Ptr[stack_st_CONF_VALUE], Ptr[Byte]]
  object X509V3_EXT_V2I:
    given _tag: Tag[X509V3_EXT_V2I] = Tag.materializeCFuncPtr3[Ptr[v3_ext_method], Ptr[
      v3_ext_ctx,
    ], Ptr[stack_st_CONF_VALUE], Ptr[Byte]]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): X509V3_EXT_V2I =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], Ptr[stack_st_CONF_VALUE], Ptr[
          Byte,
        ]],
    ): X509V3_EXT_V2I = o
    extension (v: X509V3_EXT_V2I)
      inline def value
          : CFuncPtr3[Ptr[v3_ext_method], Ptr[v3_ext_ctx], Ptr[stack_st_CONF_VALUE], Ptr[Byte]] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)
