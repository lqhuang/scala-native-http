/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/532d134767df99fb6ce8bc041afa4e0279b52589/include/curl/header.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */
package snhttp.experimental.libcurl

import scala.scalanative.unsafe.alloc
import scala.scalanative.unsafe.{
  Tag,
  Ptr,
  Zone,
  CInt,
  Size,
  CString,
  CStruct6,
  CVoidPtr,
  CLong,
  CUnsignedInt,
}
import scala.scalanative.unsigned.{UInt, UnsignedRichLong}
import scala.scalanative.posix.sys.socket.{socklen_t, sockaddr}
import scala.scalanative.posix.time.time_t
import scala.scalanative.posix.stddef.size_t

import _root_.snhttp.experimental.libcurl._type.{_BindgenEnumInt, _BindgenEnumUInt}
import _root_.snhttp.experimental.libcurl.curl.Curl

object header:

  // known as "curl_header"
  opaque type CurlHeader = CStruct6[
    /** name */
    CString,
    /** value */
    CString,
    /** amount: number of headers using this name */
    size_t,
    /** index: ... of this instance, 0 or higher */
    size_t,
    /** origin: for specifying which headers to receive */
    CurlHeaderOrigin,
    /** anchor: handle privately used by libcurl */
    Ptr[_],
  ]
  object CurlHeader:

    given _tag: Tag[CurlHeader] =
      Tag.materializeCStruct6Tag[CString, CString, size_t, size_t, CurlHeaderOrigin, Ptr[_]]

    def apply(
        name: CString,
        value: CString,
        amount: size_t,
        index: size_t,
        origin: CurlHeaderOrigin,
        anchor: Ptr[Byte],
    )(using Zone): Ptr[CurlHeader] =
      val ptr = alloc[CurlHeader](1)
      (!ptr).name = name
      (!ptr).value = value
      (!ptr).amount = amount
      (!ptr).index = index
      (!ptr).origin = origin
      (!ptr).anchor = anchor
      ptr

    extension (struct: CurlHeader)
      def name: CString = struct._1
      def name_=(value: CString): Unit = !struct.at1 = value
      def value: CString = struct._2
      def value_=(value: CString): Unit = !struct.at2 = value
      def amount: size_t = struct._3
      def amount_=(value: size_t): Unit = !struct.at3 = value
      def index: size_t = struct._4
      def index_=(value: size_t): Unit = !struct.at4 = value
      def origin: CurlHeaderOrigin = struct._5
      def origin_=(value: CurlHeaderOrigin): Unit = !struct.at5 = value
      def anchor: Ptr[?] = struct._6
      def anchor_=(value: Ptr[?]): Unit = !struct.at6 = value

  end CurlHeader

  /* 'origin' bits */
  // known as "CURLH_*""
  opaque type CurlHeaderOrigin = UInt
  object CurlHeaderOrigin extends _BindgenEnumUInt[CurlHeaderOrigin]:

    given _tag: Tag[CurlHeaderOrigin] = Tag.UInt
    inline def define(inline a: Long): CurlHeaderOrigin = a.toUInt

    val HEADER = define(1 << 0) // plain server header
    val TRAILER = define(1 << 1) // trailers
    val CONNECT = define(1 << 2) // CONNECT headers
    val `1XX` = define(1 << 3) // 1xx headers
    val PSEUDO = define(1 << 4) // pseudo headers

    extension (value: CurlHeaderOrigin)
      inline def getName: String =
        inline value match
          case HEADER  => "HEADER"
          case TRAILER => "TRAILER"
          case CONNECT => "CONNECT"
          case `1XX`   => "1XX"
          case PSEUDO  => "PSEUDO"

  end CurlHeaderOrigin

  // known as enum "CURLHcode"
  opaque type CurlHeaderErrCode = Int
  object CurlHeaderErrCode extends _BindgenEnumInt[CurlHeaderErrCode]:

    given _tag: Tag[CurlHeaderErrCode] = Tag.Int
    inline def define(inline a: Long): CurlHeaderErrCode = a.toInt

    val OK = define(0)
    val BADINDEX = define(1)
    val MISSING = define(2)
    val NOHEADERS = define(3)
    val NOREQUEST = define(4)
    val OUT_OF_MEMORY = define(5)
    val BAD_ARGUMENT = define(6)
    val NOT_BUILT_IN = define(7)

    extension (value: CurlHeaderErrCode)
      def getName: String =
        value match
          case OK            => "CURLHE_OK"
          case BADINDEX      => "CURLHE_BADINDEX"
          case MISSING       => "CURLHE_MISSING"
          case NOHEADERS     => "CURLHE_NOHEADERS"
          case NOREQUEST     => "CURLHE_NOREQUEST"
          case OUT_OF_MEMORY => "CURLHE_OUT_OF_MEMORY"
          case BAD_ARGUMENT  => "CURLHE_BAD_ARGUMENT"
          case NOT_BUILT_IN  => "CURLHE_NOT_BUILT_IN"

  end CurlHeaderErrCode
