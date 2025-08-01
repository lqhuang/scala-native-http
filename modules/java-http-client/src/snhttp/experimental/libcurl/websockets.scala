/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/d21e75a6ae0cda978e68b26579e5665a0a92ca0d/include/curl/websockets.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */

package snhttp.experimental.libcurl

import scala.scalanative.unsafe.{link, extern, alloc, name}
import scala.scalanative.unsafe.{CStruct5, Tag, Zone, Ptr, CVoidPtr}
import scala.scalanative.unsigned.*

import snhttp.experimental.libcurl.core.{Curl, CurlOff, CurlCode}

@extern
object websockets:

  /**
   * CurlWsFrame
   */
  opaque type CurlWsFrame = CStruct5[
    /**
     * age
     *
     * zero
     */
    Int,
    /**
     * flags
     *
     * See the CURLWS_* defines
     */
    Int,
    /**
     * offset
     *
     * the offset of this data into the frame
     */
    CurlOff,
    /**
     * bytesleft
     *
     * number of pending bytes left of the payload
     */
    CurlOff,
    /**
     * len
     *
     * size of the current data chunk
     */
    USize,
  ]
  object CurlWsFrame:
    given Tag[CurlWsFrame] =
      Tag.materializeCStruct5Tag[Int, Int, CurlOff, CurlOff, USize]

    def apply(age: Int, flags: Int, offset: CurlOff, bytesleft: CurlOff, len: USize)(using
        Zone,
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
      def flags: Int = struct._2
      def flags_=(value: Int): Unit = !struct.at2 = value
      def offset: CurlOff = struct._3
      def offset_=(value: CurlOff): Unit = !struct.at3 = value
      def bytesleft: CurlOff = struct._4
      def bytesleft_=(value: CurlOff): Unit = !struct.at4 = value
      def len: USize = struct._5
      def len_=(value: USize): Unit = !struct.at5 = value

  /**
   * curl websockets flag bits
   */
  opaque type CurlWsFlag = UInt
  object CurlWsFlag:
    given Tag[CurlWsFlag] = Tag.UInt
    inline def define(inline a: Int): CurlWsFlag = a.toUInt

    val TEXT = define(1 << 0)
    val BINARY = define(1 << 1)
    val CONT = define(1 << 2)
    val CLOSE = define(1 << 3)
    val PING = define(1 << 4)
    val OFFSET = define(1 << 5)
    val PONG = define(1 << 6)
    val RAW_MODE = define(1 << 0)

  /**
   * NAME curl_ws_recv()
   *
   * DESCRIPTION
   *
   * Receives data from the websocket connection. Use after successful curl_easy_perform() with
   * CURLOPT_CONNECT_ONLY option.
   */
  @name("curl_ws_recv")
  def recv(
      curl: Ptr[Curl],
      buffer: CVoidPtr,
      buflen: USize,
      recv: Ptr[USize],
      metap: Ptr[Ptr[CurlWsFrame]],
  ): CurlCode = extern

  /**
   * NAME curl_ws_send()
   *
   * DESCRIPTION
   *
   * Sends data over the websocket connection. Use after successful curl_easy_perform() with
   * CURLOPT_CONNECT_ONLY option.
   */
  @name("curl_ws_send")
  def send(
      curl: Ptr[Curl],
      buffer: CVoidPtr,
      buflen: USize,
      sent: Ptr[USize],
      fragsize: CurlOff,
      flags: CurlWsFlag,
  ): CurlCode = extern

  @name("curl_ws_meta")
  def meta(curl: Ptr[Curl]): Ptr[CurlWsFrame] = extern
