/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/abcb10f3ac6e829b125cc0f0e5071ce56d511755/include/curl/websockets.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */

package snhttp.experimental._libcurl

import scala.scalanative.unsafe.alloc
import scala.scalanative.unsafe.{CStruct5, Tag, Zone, Ptr, CVoidPtr, CLong, UnsafeRichLong}
import scala.scalanative.unsigned.{UInt, UnsignedRichLong}
import scala.scalanative.libc.stddef.size_t

import snhttp.experimental._libcurl.curl.{Curl, CurlErrCode}
import snhttp.experimental._libcurl.system.CurlOff
import snhttp.experimental._libcurl.internal.{_BindgenEnumInt, _BindgenEnumUInt, _BindgenEnumCLong}

object websockets:

  // known as "curl_ws_frame"
  opaque type CurlWsFrame = CStruct5[
    /** age: zero */
    Int,
    /** flags: See the `CURLWS_*` defines */
    CurlWsFrameFlag,
    /** offset: the offset of this data into the frame */
    CurlOff,
    /** bytesleft: number of pending bytes left of the payload */
    CurlOff,
    /** len:  size of the current data chunk */
    size_t,
  ]
  object CurlWsFrame:
    given Tag[CurlWsFrame] =
      Tag.materializeCStruct5Tag[Int, CurlWsFrameFlag, CurlOff, CurlOff, size_t]

    def apply(age: Int, flags: CurlWsFrameFlag, offset: CurlOff, bytesleft: CurlOff, len: size_t)(
        using Zone,
    ): Ptr[CurlWsFrame] =
      val ptr = alloc[CurlWsFrame](1)
      (!ptr).age = age
      (!ptr).flags = flags
      (!ptr).offset = offset
      (!ptr).bytesleft = bytesleft
      (!ptr).len = len
      ptr

    extension (struct: CurlWsFrame)
      def age: Int = struct._1
      def age_=(value: Int): Unit = !struct.at1 = value
      def flags: CurlWsFrameFlag = struct._2
      def flags_=(value: CurlWsFrameFlag): Unit = !struct.at2 = value
      def offset: CurlOff = struct._3
      def offset_=(value: CurlOff): Unit = !struct.at3 = value
      def bytesleft: CurlOff = struct._4
      def bytesleft_=(value: CurlOff): Unit = !struct.at4 = value
      def len: size_t = struct._5
      def len_=(value: size_t): Unit = !struct.at5 = value

  /**
   * curl websockets frame flag bits
   */
  opaque type CurlWsFrameFlag = Int
  object CurlWsFrameFlag extends _BindgenEnumInt[CurlWsFrameFlag]:
    given Tag[CurlWsFrameFlag] = Tag.Int
    inline def define(inline a: Int): CurlWsFrameFlag = a

    val TEXT = define(1 << 0)
    val BINARY = define(1 << 1)
    val CONT = define(1 << 2)
    val CLOSE = define(1 << 3)
    val PING = define(1 << 4)
    val OFFSET = define(1 << 5)

  /* flags for curl_ws_send() */
  opaque type CurlWsSendFlag = UInt
  object CurlWsSendFlag extends _BindgenEnumUInt[CurlWsSendFlag]:
    given Tag[CurlWsSendFlag] = Tag.UInt

    inline def define(inline a: Long): CurlWsSendFlag = a.toUInt

    val PONG = define(1 << 6)

  /* bits for the CURLOPT_WS_OPTIONS bitmask: */
  opaque type CurlWsOption = CLong
  object CurlWsOption extends _BindgenEnumCLong[CurlWsOption]:
    given Tag[CurlWsOption] = Tag.Size

    inline def define(inline a: Long): CurlWsOption = a.toSize

    val RAW_MODE = define(1L << 0)
    val NOAUTOPONG = define(1L << 1)
