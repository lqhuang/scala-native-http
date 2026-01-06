/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/d21e75a6ae0cda978e68b26579e5665a0a92ca0d/include/curl/options.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */
package snhttp.experimental._libcurl

import scala.scalanative.posix.sys.socket
import scala.scalanative.unsafe.{Tag, Ptr, Zone, CString, CStruct4, CLong, UnsafeRichLong}
import scala.scalanative.unsafe.alloc
import scala.scalanative.unsigned.UInt

import snhttp.experimental._libcurl.internal._BindgenEnumInt
import snhttp.experimental._libcurl.curl.CurlOption

object options:

  // known as enum "curl_easytype"
  opaque type CurlEasyType = Int
  given Tag[CurlEasyType] = Tag.Int
  object CurlEasyType extends _BindgenEnumInt[CurlEasyType]:
    inline def define(inline a: Int): CurlEasyType = a

    /* long (a range of values) */
    val LONG = define(0)
    /* long (a defined set or bitmask) */
    val VALUES = define(1)
    /* curl_off_t (a range of values) */
    val OFF_T = define(2)
    /* pointer (void *) */
    val OBJECT = define(3)
    /* pointer (char * to null-terminated buffer) */
    val STRING = define(4)
    /* pointer (struct curl_slist *) */
    val SLIST = define(5)
    /* pointer (void * passed as-is to a callback) */
    val CBPTR = define(6)
    /* blob (struct curl_blob *) */
    val BLOB = define(7)
    /* function pointer */
    val FUNCTION = define(8)

    extension (inline value: CurlEasyType)
      inline def getName: String =
        inline value match
          case LONG     => "CURLOT_LONG"
          case VALUES   => "CURLOT_VALUES"
          case OFF_T    => "CURLOT_OFF_T"
          case OBJECT   => "CURLOT_OBJECT"
          case STRING   => "CURLOT_STRING"
          case SLIST    => "CURLOT_SLIST"
          case CBPTR    => "CURLOT_CBPTR"
          case BLOB     => "CURLOT_BLOB"
          case FUNCTION => "CURLOT_FUNCTION"

  opaque type CurlEasyOption = CStruct4[
    /** name */
    CString,
    /** id */
    CurlOption,
    /** type */
    CurlEasyType,
    /** flags */
    UInt,
  ]
  object CurlEasyOption:
    given Tag[CurlEasyOption] =
      Tag.materializeCStruct4Tag[CString, CurlOption, CurlEasyType, UInt]

    def apply(name: CString, id: CurlOption, `type`: CurlEasyType, flags: UInt)(using
        Zone,
    ): Ptr[CurlEasyOption] =
      val ptr = alloc[CurlEasyOption](1)
      (!ptr).name = name
      (!ptr).id = id
      (!ptr).`type` = `type`
      (!ptr).flags = flags
      ptr

    extension (struct: CurlEasyOption)
      def name: CString = struct._1
      def name_=(value: CString): Unit = !struct.at1 = value
      def id: CurlOption = struct._2
      def id_=(value: CurlOption): Unit = !struct.at2 = value
      def `type`: CurlEasyType = struct._3
      def type_=(value: CurlEasyType): Unit = !struct.at3 = value
      def flags: UInt = struct._4
      def flags_=(value: UInt): Unit = !struct.at4 = value
