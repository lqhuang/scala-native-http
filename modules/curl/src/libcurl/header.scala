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

import scala.scalanative.unsafe.{alloc, name, link, extern, define}
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
import scala.scalanative.unsigned.*
import scala.scalanative.posix.sys.socket.{socklen_t, sockaddr}
import scala.scalanative.posix.time.time_t
import scala.scalanative.posix.stddef.size_t

import snhttp.experimental.libcurl._type._BindgenEnumCLong

import internal.SockAddrFamily

import _type.{_BindgenEnumInt, _BindgenEnumUInt}
import core.Curl
import scala.annotation.targetName

@extern
object header:

  @name("curl_header")
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
    CURLH,
    /** anchor: handle privately used by libcurl */
    Ptr[Byte],
  ]
  object CurlHeader:
    given _tag: Tag[CurlHeader] =
      Tag.materializeCStruct6Tag[CString, CString, size_t, size_t, CURLH, Ptr[Byte]]

    def apply(
        name: CString,
        value: CString,
        amount: size_t,
        index: size_t,
        origin: CURLH,
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
      def origin: CUnsignedInt = struct._5
      def origin_=(value: CUnsignedInt): Unit = !struct.at5 = value
      def anchor: Ptr[Byte] = struct._6
      def anchor_=(value: Ptr[Byte]): Unit = !struct.at6 = value

  end CurlHeader

  /* 'origin' bits */
  opaque type CURLH = CUnsignedInt
  object CURLH extends _BindgenEnumUInt[CURLH]:

    given _tag: Tag[CURLH] = Tag.UInt
    inline def define(inline a: Long): CURLH = a.toUInt

    val HEADER = define(1 << 0) // plain server header
    val TRAILER = define(1 << 1) // trailers
    val CONNECT = define(1 << 2) // CONNECT headers
    val `1XX` = define(1 << 3) // 1xx headers
    val PSEUDO = define(1 << 4) // pseudo headers

    extension (value: CURLH)
      inline def getName: String =
        inline value match
          case HEADER  => "HEADER"
          case TRAILER => "TRAILER"
          case CONNECT => "CONNECT"
          case `1XX`   => "1XX"
          case PSEUDO  => "PSEUDO"

    extension (a: CURLH)
      inline def &(b: CURLH): CURLH = a & b
      inline def |(b: CURLH): CURLH = a | b
      inline def is(b: CURLH): Boolean = (a & b) == b

  end CURLH

  opaque type CURLHcode = Int
  object CURLHcode extends _BindgenEnumInt[CURLHcode]:

    given _tag: Tag[CURLHcode] = Tag.Int
    inline def define(inline a: Long): CURLHcode = a.toInt

    val OK = define(0)
    val BADINDEX = define(1)
    val MISSING = define(2)
    val NOHEADERS = define(3)
    val NOREQUEST = define(4)
    val OUT_OF_MEMORY = define(5)
    val BAD_ARGUMENT = define(6)
    val NOT_BUILT_IN = define(7)

    extension (value: CURLHcode)
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

    extension (a: CURLHcode)
      inline def &(b: CURLHcode): CURLHcode = a & b
      inline def |(b: CURLHcode): CURLHcode = a | b
      inline def is(b: CURLHcode): Boolean = (a & b) == b

  end CURLHcode

  @name("curl_easy_header")
  def easyHeader(
      easy: Ptr[Curl],
      name: CString,
      index: size_t,
      origin: CURLH,
      request: CInt,
      hout: Ptr[Ptr[CurlHeader]],
  ): CURLHcode = extern

  @name("curl_easy_nextheader")
  def easyNextHeader(
      easy: Ptr[Curl],
      origin: CURLH,
      request: CInt,
      prev: Ptr[CurlHeader],
  ): Ptr[CurlHeader] = extern
