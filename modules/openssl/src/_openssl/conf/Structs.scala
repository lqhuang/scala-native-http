package snhttp.experimental.openssl
package _openssl.conf

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.bio.Types.BIO
import _root_.snhttp.experimental.openssl._openssl.types.Types.OSSL_LIB_CTX

private[openssl] object Structs:

  /**
   * [bindgen] header: /usr/include/openssl/conf.h
   */
  opaque type lhash_st_CONF_VALUE = CStruct0

  object lhash_st_CONF_VALUE:
    given _tag: Tag[lhash_st_CONF_VALUE] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/conftypes.h
   */
  opaque type CONF =
    CStruct7[Ptr[Byte], Ptr[Byte], Ptr[lhash_st_CONF_VALUE], CInt, CInt, CString, Ptr[OSSL_LIB_CTX]]

  object CONF:
    given _tag: Tag[CONF] = Tag.materializeCStruct7Tag[Ptr[Byte], Ptr[Byte], Ptr[
      lhash_st_CONF_VALUE,
    ], CInt, CInt, CString, Ptr[OSSL_LIB_CTX]]

    export fields.*
    private[conf] object fields:
      extension (struct: CONF)
        inline def meth: Ptr[CONF_METHOD] = struct._1.asInstanceOf[Ptr[CONF_METHOD]]
        inline def meth_=(value: Ptr[CONF_METHOD]): Unit = !struct.at1 =
          value.asInstanceOf[Ptr[Byte]]
        inline def meth_data: Ptr[Byte] = struct._2
        inline def meth_data_=(value: Ptr[Byte]): Unit = !struct.at2 = value
        inline def data: Ptr[lhash_st_CONF_VALUE] = struct._3
        inline def data_=(value: Ptr[lhash_st_CONF_VALUE]): Unit = !struct.at3 = value
        inline def flag_dollarid: CInt = struct._4
        inline def flag_dollarid_=(value: CInt): Unit = !struct.at4 = value
        inline def flag_abspath: CInt = struct._5
        inline def flag_abspath_=(value: CInt): Unit = !struct.at5 = value
        inline def includedir: CString = struct._6
        inline def includedir_=(value: CString): Unit = !struct.at6 = value
        inline def libctx: Ptr[OSSL_LIB_CTX] = struct._7
        inline def libctx_=(value: Ptr[OSSL_LIB_CTX]): Unit = !struct.at7 = value
      end extension

    // Allocates CONF on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[CONF] = scala.scalanative.unsafe.alloc[CONF](1)
    def apply(
        meth: Ptr[CONF_METHOD],
        meth_data: Ptr[Byte],
        data: Ptr[lhash_st_CONF_VALUE],
        flag_dollarid: CInt,
        flag_abspath: CInt,
        includedir: CString,
        libctx: Ptr[OSSL_LIB_CTX],
    )(using Zone): Ptr[CONF] =
      val ____ptr = apply()
      (!____ptr).meth = meth
      (!____ptr).meth_data = meth_data
      (!____ptr).data = data
      (!____ptr).flag_dollarid = flag_dollarid
      (!____ptr).flag_abspath = flag_abspath
      (!____ptr).includedir = includedir
      (!____ptr).libctx = libctx
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/conftypes.h
   */
  opaque type CONF_METHOD = CStruct10[
    CString,
    CFuncPtr1[Ptr[Byte], Ptr[Byte]],
    CFuncPtr1[Ptr[Byte], CInt],
    CFuncPtr1[Ptr[Byte], CInt],
    CFuncPtr1[Ptr[Byte], CInt],
    CFuncPtr3[Ptr[Byte], Ptr[BIO], Ptr[CLongInt], CInt],
    CFuncPtr2[Ptr[Byte], Ptr[BIO], CInt],
    CFuncPtr2[Ptr[Byte], CChar, CInt],
    CFuncPtr2[Ptr[Byte], CChar, CInt],
    CFuncPtr3[Ptr[Byte], CString, Ptr[CLongInt], CInt],
  ]

  object CONF_METHOD:
    given _tag: Tag[CONF_METHOD] = Tag.materializeCStruct10Tag[
      CString,
      CFuncPtr1[Ptr[Byte], Ptr[Byte]],
      CFuncPtr1[Ptr[Byte], CInt],
      CFuncPtr1[Ptr[Byte], CInt],
      CFuncPtr1[Ptr[Byte], CInt],
      CFuncPtr3[Ptr[Byte], Ptr[BIO], Ptr[CLongInt], CInt],
      CFuncPtr2[Ptr[Byte], Ptr[BIO], CInt],
      CFuncPtr2[Ptr[Byte], CChar, CInt],
      CFuncPtr2[Ptr[Byte], CChar, CInt],
      CFuncPtr3[Ptr[Byte], CString, Ptr[CLongInt], CInt],
    ]

    export fields.*
    private[conf] object fields:
      extension (struct: CONF_METHOD)
        inline def name: CString = struct._1
        inline def name_=(value: CString): Unit = !struct.at1 = value
        inline def create: CFuncPtr1[Ptr[CONF_METHOD], Ptr[CONF]] =
          struct._2.asInstanceOf[CFuncPtr1[Ptr[CONF_METHOD], Ptr[CONF]]]
        inline def create_=(value: CFuncPtr1[Ptr[CONF_METHOD], Ptr[CONF]]): Unit = !struct.at2 =
          value.asInstanceOf[CFuncPtr1[Ptr[Byte], Ptr[Byte]]]
        inline def init: CFuncPtr1[Ptr[CONF], CInt] =
          struct._3.asInstanceOf[CFuncPtr1[Ptr[CONF], CInt]]
        inline def init_=(value: CFuncPtr1[Ptr[CONF], CInt]): Unit = !struct.at3 =
          value.asInstanceOf[CFuncPtr1[Ptr[Byte], CInt]]
        inline def destroy: CFuncPtr1[Ptr[CONF], CInt] =
          struct._4.asInstanceOf[CFuncPtr1[Ptr[CONF], CInt]]
        inline def destroy_=(value: CFuncPtr1[Ptr[CONF], CInt]): Unit = !struct.at4 =
          value.asInstanceOf[CFuncPtr1[Ptr[Byte], CInt]]
        inline def destroy_data: CFuncPtr1[Ptr[CONF], CInt] =
          struct._5.asInstanceOf[CFuncPtr1[Ptr[CONF], CInt]]
        inline def destroy_data_=(value: CFuncPtr1[Ptr[CONF], CInt]): Unit = !struct.at5 =
          value.asInstanceOf[CFuncPtr1[Ptr[Byte], CInt]]
        inline def load_bio: CFuncPtr3[Ptr[CONF], Ptr[BIO], Ptr[CLongInt], CInt] =
          struct._6.asInstanceOf[CFuncPtr3[Ptr[CONF], Ptr[BIO], Ptr[CLongInt], CInt]]
        inline def load_bio_=(value: CFuncPtr3[Ptr[CONF], Ptr[BIO], Ptr[CLongInt], CInt]): Unit =
          !struct.at6 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], Ptr[BIO], Ptr[CLongInt], CInt]]
        inline def dump: CFuncPtr2[Ptr[CONF], Ptr[BIO], CInt] =
          struct._7.asInstanceOf[CFuncPtr2[Ptr[CONF], Ptr[BIO], CInt]]
        inline def dump_=(value: CFuncPtr2[Ptr[CONF], Ptr[BIO], CInt]): Unit = !struct.at7 =
          value.asInstanceOf[CFuncPtr2[Ptr[Byte], Ptr[BIO], CInt]]
        inline def is_number: CFuncPtr2[Ptr[CONF], CChar, CInt] =
          struct._8.asInstanceOf[CFuncPtr2[Ptr[CONF], CChar, CInt]]
        inline def is_number_=(value: CFuncPtr2[Ptr[CONF], CChar, CInt]): Unit = !struct.at8 =
          value.asInstanceOf[CFuncPtr2[Ptr[Byte], CChar, CInt]]
        inline def to_int: CFuncPtr2[Ptr[CONF], CChar, CInt] =
          struct._9.asInstanceOf[CFuncPtr2[Ptr[CONF], CChar, CInt]]
        inline def to_int_=(value: CFuncPtr2[Ptr[CONF], CChar, CInt]): Unit = !struct.at9 =
          value.asInstanceOf[CFuncPtr2[Ptr[Byte], CChar, CInt]]
        inline def load: CFuncPtr3[Ptr[CONF], CString, Ptr[CLongInt], CInt] =
          struct._10.asInstanceOf[CFuncPtr3[Ptr[CONF], CString, Ptr[CLongInt], CInt]]
        inline def load_=(value: CFuncPtr3[Ptr[CONF], CString, Ptr[CLongInt], CInt]): Unit =
          !struct.at10 = value.asInstanceOf[CFuncPtr3[Ptr[Byte], CString, Ptr[CLongInt], CInt]]
      end extension

    // Allocates CONF_METHOD on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[CONF_METHOD] = scala.scalanative.unsafe.alloc[CONF_METHOD](1)
    def apply(
        name: CString,
        create: CFuncPtr1[Ptr[CONF_METHOD], Ptr[CONF]],
        init: CFuncPtr1[Ptr[CONF], CInt],
        destroy: CFuncPtr1[Ptr[CONF], CInt],
        destroy_data: CFuncPtr1[Ptr[CONF], CInt],
        load_bio: CFuncPtr3[Ptr[CONF], Ptr[BIO], Ptr[CLongInt], CInt],
        dump: CFuncPtr2[Ptr[CONF], Ptr[BIO], CInt],
        is_number: CFuncPtr2[Ptr[CONF], CChar, CInt],
        to_int: CFuncPtr2[Ptr[CONF], CChar, CInt],
        load: CFuncPtr3[Ptr[CONF], CString, Ptr[CLongInt], CInt],
    )(using Zone): Ptr[CONF_METHOD] =
      val ____ptr = apply()
      (!____ptr).name = name
      (!____ptr).create = create
      (!____ptr).init = init
      (!____ptr).destroy = destroy
      (!____ptr).destroy_data = destroy_data
      (!____ptr).load_bio = load_bio
      (!____ptr).dump = dump
      (!____ptr).is_number = is_number
      (!____ptr).to_int = to_int
      (!____ptr).load = load
      ____ptr

  /**
   * [bindgen] header: /usr/include/openssl/conf.h
   */
  opaque type CONF_VALUE = CStruct3[CString, CString, CString]

  object CONF_VALUE:
    given _tag: Tag[CONF_VALUE] = Tag.materializeCStruct3Tag[CString, CString, CString]

    export fields.*
    private[conf] object fields:
      extension (struct: CONF_VALUE)
        inline def section: CString = struct._1
        inline def section_=(value: CString): Unit = !struct.at1 = value
        inline def name: CString = struct._2
        inline def name_=(value: CString): Unit = !struct.at2 = value
        inline def value: CString = struct._3
        inline def value_=(value: CString): Unit = !struct.at3 = value
      end extension

    // Allocates CONF_VALUE on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[CONF_VALUE] = scala.scalanative.unsafe.alloc[CONF_VALUE](1)
    def apply(section: CString, name: CString, value: CString)(using Zone): Ptr[CONF_VALUE] =
      val ____ptr = apply()
      (!____ptr).section = section
      (!____ptr).name = name
      (!____ptr).value = value
      ____ptr
