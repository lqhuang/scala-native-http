/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/d21e75a6ae0cda978e68b26579e5665a0a92ca0d/include/curl/easy.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */
package snhttp.experimental.libcurl

import scala.scalanative.unsafe.{extern, alloc, name}
import scala.scalanative.unsafe.{Ptr, Tag, Zone, CString, CStruct6}
import scala.scalanative.unsigned.*

import core.{Curl, CurlCode, CurlInfo}
import options.{CurlEasyOption, CurlOption}

@extern
object easy:

  //
  // Parts defined in <curl/curl.h>
  //

  /**
   * NAME curl_easy_escape()
   *
   * DESCRIPTION
   *
   * Escapes URL strings (converts all letters consider illegal in URLs to their %XX versions). This
   * function returns a new allocated string or NULL if an error occurred.
   */
  @name("curl_easy_escape")
  def easyEscape(handle: Ptr[Curl], string: CString, length: Int): CString = extern

  /**
   * NAME curl_easy_unescape()
   *
   * DESCRIPTION
   *
   * Unescapes URL encoding in strings (converts all %XX codes to their 8bit versions). This
   * function returns a new allocated string or NULL if an error occurred. Conversion Note: On
   * non-ASCII platforms the ASCII %XX codes are converted into the host encoding.
   */
  @name("curl_easy_unescape")
  def easyUnescape(
      handle: Ptr[Curl],
      string: CString,
      length: Int,
      outlength: Ptr[Int],
  ): CString = extern

  /**
   * NAME curl_easy_strerror()
   *
   * DESCRIPTION
   *
   * The curl_easy_strerror function may be used to turn a CURLcode value into the equivalent human
   * readable error string. This is useful for printing meaningful error messages.
   */
  @name("curl_easy_strerror")
  def easyStrError(code: CurlCode): CString = extern

  /**
   * NAME curl_easy_pause()
   *
   * DESCRIPTION
   *
   * The curl_easy_pause function pauses or unpauses transfers. Select the new state by setting the
   * bitmask, use the convenience defines below.
   */
  @name("curl_easy_pause")
  def easyPause(handle: Ptr[Curl], bitmask: CurlPause): CurlCode = extern

  opaque type CurlPause = UInt
  object CurlPause:
    given Tag[CurlPause] = Tag.UInt

    inline def define(inline a: Long): CurlPause = a.toUInt

    val RECV = define(1 << 0)
    val RECV_CONT = define(0)
    val SEND = define(1 << 2)
    val SEND_CONT = define(0)
    val ALL = RECV | SEND
    val CONT = RECV_CONT | SEND_CONT

    extension (a: CurlPause)
      inline def &(b: CurlPause): CurlPause = a & b
      inline def |(b: CurlPause): CurlPause = a | b
      inline def is(b: CurlPause): Boolean = (a & b) == b

  //
  // include <curl/easy.h>
  //

  @name("curl_easy_init")
  def easyInit(): Ptr[Curl] = extern

  @name("curl_easy_setopt")
  def easySetopt(handle: Ptr[Curl], option: CurlOption, params: Any*): CurlCode = extern

  @name("curl_easy_perform")
  def easyPerform(handle: Ptr[Curl]): CurlCode = extern

  @name("curl_easy_cleanup")
  def easyCleanup(handle: Ptr[Curl]): Unit = extern

  /**
   * NAME curl_easy_getinfo()
   *
   * DESCRIPTION
   *
   * Request internal information from the curl session with this function. The third argument MUST
   * be pointing to the specific type of the used option which is documented in each man page of the
   * option. The data pointed to will be filled in accordingly and can be relied upon only if the
   * function returns CURLE_OK. This function is intended to get used *AFTER* a performed transfer,
   * all results from this function are undefined until the transfer is completed.
   */
  @name("curl_easy_getinfo")
  def easyGetInfo(handle: Ptr[Curl], info: CurlInfo, params: Any*): CurlCode = extern

  /**
   * NAME curl_easy_duphandle()
   *
   * DESCRIPTION
   *
   * Creates a new curl session handle with the same options set for the handle passed in.
   * Duplicating a handle could only be a matter of cloning data and options, internal state info
   * and things like persistent connections cannot be transferred. It is useful in multithreaded
   * applications when you can run curl_easy_duphandle() for each new thread to avoid a series of
   * identical curl_easy_setopt() invokes in every thread.
   */
  @name("curl_easy_duphandle")
  def easyDupHandle(handle: Ptr[Curl]): Ptr[Curl] = extern

  /**
   * NAME curl_easy_reset()
   *
   * DESCRIPTION
   *
   * Re-initializes a CURL handle to the default values. This puts back the handle to the same state
   * as it was in when it was just created.
   *
   * It does keep: live connections, the Session ID cache, the DNS cache and the cookies.
   */
  @name("curl_easy_reset")
  def easyReset(handle: Ptr[Curl]): Unit = extern

  /**
   * NAME curl_easy_recv()
   *
   * DESCRIPTION
   *
   * Receives data from the connected socket. Use after successful curl_easy_perform() with
   * CURLOPT_CONNECT_ONLY option.
   */
  @name("curl_easy_recv")
  def easyRecv(handle: Ptr[Curl], buffer: Ptr[Byte], buflen: USize, n: Ptr[USize]): CurlCode =
    extern

  /**
   * NAME curl_easy_send()
   *
   * DESCRIPTION
   *
   * Sends data over the connected socket. Use after successful curl_easy_perform() with
   * CURLOPT_CONNECT_ONLY option.
   */
  @name("curl_easy_send")
  def easySend(handle: Ptr[Curl], buffer: Ptr[Byte], buflen: USize, n: Ptr[USize]): CurlCode =
    extern

  /**
   * NAME curl_easy_upkeep()
   *
   * DESCRIPTION
   *
   * Performs connection upkeep for the given session handle.
   */
  @name("curl_easy_upkeep")
  def easyUpkeep(handle: Ptr[Curl]): CurlCode = extern

  //
  // include <curl/header.h>
  //

  opaque type CurlHeader = CStruct6[CString, CString, USize, USize, UInt, Ptr[Byte]]
  object CurlHeader:
    given Tag[CurlHeader] =
      Tag.materializeCStruct6Tag[CString, CString, USize, USize, UInt, Ptr[Byte]]

    def apply(
        name: CString,
        value: CString,
        amount: USize,
        index: USize,
        origin: UInt,
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
      def amount: USize = struct._3
      def amount_=(value: USize): Unit = !struct.at3 = value
      def index: USize = struct._4
      def index_=(value: USize): Unit = !struct.at4 = value
      def origin: UInt = struct._5
      def origin_=(value: UInt): Unit = !struct.at5 = value
      def anchor: Ptr[Byte] = struct._6
      def anchor_=(value: Ptr[Byte]): Unit = !struct.at6 = value

  /**
   * define symbols `CURLH_*`, known as 'origin' bits
   */
  opaque type CurlHeaderOrigin = UInt
  object CurlHeaderOrigin:
    given Tag[CurlHeaderOrigin] = Tag.UInt

    inline def define(inline v: Int): CurlHeaderOrigin = v.toUInt

    val HEADER = define(1 << 0) /* plain server header */
    val TRAILER = define(1 << 1) /* trailers */
    val CONNECT = define(1 << 2) /* CONNECT headers */
    val `1XX` = define(1 << 3) /* 1xx headers */
    val PSEUDO = define(1 << 4) /* pseudo headers */

  @name("CURLHcode")
  opaque type CurlHeadderCode = UInt
  object CurlHeadderCode:
    given Tag[CurlHeadderCode] = Tag.UInt

    inline def define(inline a: Long): CurlHeadderCode = a.toUInt

    val OK = define(0)
    val BADINDEX = define(1) /* header exists but not with this index */
    val MISSING = define(2) /* no such header exists */
    val NOHEADERS = define(3) /* no headers at all exist (yet) */
    val NOREQUEST = define(4) /* no request with this number was used */
    val OUT_OF_MEMORY = define(5) /* out of memory while processing */
    val BAD_ARGUMENT = define(6) /* a function argument was not okay */
    val NOT_BUILT_IN = define(7) /* if API was disabled in the build */

    extension (value: CurlHeadderCode)
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
          case _             => throw new IllegalStateException("Unknown CurlHeadderCode: " + value)

    extension (a: CurlHeadderCode)
      inline def &(b: CurlHeadderCode): CurlHeadderCode = a & b
      inline def |(b: CurlHeadderCode): CurlHeadderCode = a | b
      inline def is(b: CurlHeadderCode): Boolean = (a & b) == b

  def easyHeader(
      handle: Ptr[Curl],
      name: CString,
      index: USize,
      origin: CurlHeaderOrigin,
      request: Int,
      hout: Ptr[Ptr[CurlHeader]],
  ): CurlHeadderCode = extern

  def easyNextHeader(
      handle: Ptr[Curl],
      origin: CurlHeaderOrigin,
      request: Int,
      prev: Ptr[CurlHeader],
  ): Ptr[CurlHeader] = extern
