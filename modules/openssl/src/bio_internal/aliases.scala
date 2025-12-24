package snhttp.experimental.openssl.bio_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object aliases:

  import _root_.snhttp.experimental.openssl.bio_internal.structs.*

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_callback_fn =
    CFuncPtr6[Ptr[BIO], CInt, CString, CInt, CLongInt, CLongInt, CLongInt]
  object BIO_callback_fn:
    given _tag: Tag[BIO_callback_fn] =
      Tag.materializeCFuncPtr6[Ptr[BIO], CInt, CString, CInt, CLongInt, CLongInt, CLongInt]
    inline def fromPtr(ptr: Ptr[Byte] | Ptr[?]): BIO_callback_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr6[Ptr[BIO], CInt, CString, CInt, CLongInt, CLongInt, CLongInt],
    ): BIO_callback_fn = o
    extension (v: BIO_callback_fn)
      inline def value: CFuncPtr6[Ptr[BIO], CInt, CString, CInt, CLongInt, CLongInt, CLongInt] = v
      inline def toPtr: Ptr[?] = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_callback_fn_ex =
    CFuncPtr8[Ptr[BIO], CInt, CString, size_t, CInt, CLongInt, CInt, Ptr[size_t], CLongInt]
  object BIO_callback_fn_ex:
    given _tag: Tag[BIO_callback_fn_ex] = Tag.materializeCFuncPtr8[Ptr[
      BIO,
    ], CInt, CString, size_t, CInt, CLongInt, CInt, Ptr[size_t], CLongInt]
    inline def fromPtr(ptr: Ptr[Byte] | Ptr[?]): BIO_callback_fn_ex =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr8[Ptr[BIO], CInt, CString, size_t, CInt, CLongInt, CInt, Ptr[
          size_t,
        ], CLongInt],
    ): BIO_callback_fn_ex = o
    extension (v: BIO_callback_fn_ex)
      inline def value: CFuncPtr8[Ptr[BIO], CInt, CString, size_t, CInt, CLongInt, CInt, Ptr[
        size_t,
      ], CLongInt] = v
      inline def toPtr: Ptr[?] = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_dgram_sctp_notification_handler_fn =
    CFuncPtr3[Ptr[BIO], Ptr[Byte], Ptr[Byte], Unit]
  object BIO_dgram_sctp_notification_handler_fn:
    given _tag: Tag[BIO_dgram_sctp_notification_handler_fn] =
      Tag.materializeCFuncPtr3[Ptr[BIO], Ptr[Byte], Ptr[Byte], Unit]
    inline def fromPtr(ptr: Ptr[Byte] | Ptr[?]): BIO_dgram_sctp_notification_handler_fn =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr3[Ptr[BIO], Ptr[Byte], Ptr[Byte], Unit],
    ): BIO_dgram_sctp_notification_handler_fn = o
    extension (v: BIO_dgram_sctp_notification_handler_fn)
      inline def value: CFuncPtr3[Ptr[BIO], Ptr[Byte], Ptr[Byte], Unit] = v
      inline def toPtr: Ptr[?] = CFuncPtr.toPtr(v)

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  type BIO_info_cb = CFuncPtr3[Ptr[BIO], CInt, CInt, CInt]
  object BIO_info_cb:
    given _tag: Tag[BIO_info_cb] = Tag.materializeCFuncPtr3[Ptr[BIO], CInt, CInt, CInt]
    inline def apply(inline o: CFuncPtr3[Ptr[BIO], CInt, CInt, CInt]): BIO_info_cb = o
    extension (v: BIO_info_cb) inline def value: CFuncPtr3[Ptr[BIO], CInt, CInt, CInt] = v

  type FILE = libc.stdio.FILE
  object FILE:
    val _tag: Tag[FILE] = summon[Tag[libc.stdio.FILE]]
    inline def apply(inline o: libc.stdio.FILE): FILE = o
    extension (v: FILE) inline def value: libc.stdio.FILE = v

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  type asn1_ps_func = CFuncPtr4[Ptr[BIO], Ptr[Ptr[CUnsignedChar]], Ptr[CInt], Ptr[Byte], CInt]
  object asn1_ps_func:
    given _tag: Tag[asn1_ps_func] =
      Tag.materializeCFuncPtr4[Ptr[BIO], Ptr[Ptr[CUnsignedChar]], Ptr[CInt], Ptr[Byte], CInt]
    inline def apply(
        inline o: CFuncPtr4[Ptr[BIO], Ptr[Ptr[CUnsignedChar]], Ptr[CInt], Ptr[Byte], CInt],
    ): asn1_ps_func = o
    extension (v: asn1_ps_func)
      inline def value: CFuncPtr4[Ptr[BIO], Ptr[Ptr[CUnsignedChar]], Ptr[CInt], Ptr[Byte], CInt] = v

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

  type uint32_t = scala.scalanative.unsigned.UInt
  object uint32_t:
    val _tag: Tag[uint32_t] = summon[Tag[scala.scalanative.unsigned.UInt]]
    inline def apply(inline o: scala.scalanative.unsigned.UInt): uint32_t = o
    extension (v: uint32_t) inline def value: scala.scalanative.unsigned.UInt = v

  type uint64_t = scala.scalanative.unsigned.ULong
  object uint64_t:
    val _tag: Tag[uint64_t] = summon[Tag[scala.scalanative.unsigned.ULong]]
    inline def apply(inline o: scala.scalanative.unsigned.ULong): uint64_t = o
    extension (v: uint64_t) inline def value: scala.scalanative.unsigned.ULong = v

  /**
   * [bindgen] header: /usr/include/stdint.h
   */
  opaque type uintptr_t = CUnsignedLongInt
  object uintptr_t:
    given _tag: Tag[uintptr_t] = Tag.USize
    inline def apply(inline o: CUnsignedLongInt): uintptr_t = o
    extension (v: uintptr_t) inline def value: CUnsignedLongInt = v
