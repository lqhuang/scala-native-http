package snhttp.experimental.libcurl

import scala.scalanative.unsafe.extern
import scala.scalanative.unsafe.Ptr

import snhttp.experimental.libcurl.core.Curl

@extern
object easy:

  def curl_easy_cleanup(curl: Ptr[Curl]): Unit = extern
  // def curl_easy_duphandle(curl: Ptr[Curl]): Ptr[Curl] = extern

  // def curl_easy_getinfo(curl: Ptr[Curl], info: CurlInfo, rest: Any*): CurlCode = extern

  // def curl_easy_header(
  //     easy: Ptr[Curl],
  //     name: CString,
  //     index: USize,
  //     origin: UInt,
  //     request: Int,
  //     hout: Ptr[Ptr[CurlHeader]],
  // ): CurlCode = extern

  // def curl_easy_init(): Ptr[Curl] = extern

  // def curl_easy_nextheader(
  //     easy: Ptr[Curl],
  //     origin: UInt,
  //     request: Int,
  //     prev: Ptr[curl_header],
  // ): Ptr[curl_header] = extern

  // def curl_easy_option_by_id(id: CurlOption): Ptr[curl_easyoption] = extern

  // def curl_easy_option_by_name(name: CString): Ptr[curl_easyoption] = extern

  // def curl_easy_option_next(prev: Ptr[curl_easyoption]): Ptr[curl_easyoption] = extern

  //   def curl_easy_perform(curl: Ptr[Curl]): CurlCode = extern

  //   def curl_easy_recv(curl: Ptr[Curl], buffer: Ptr[Byte], buflen: USize, n: Ptr[USize]): CurlCode =
  //     extern

  //   def curl_easy_reset(curl: Ptr[Curl]): Unit = extern

  //   def curl_easy_send(curl: Ptr[Curl], buffer: Ptr[Byte], buflen: USize, n: Ptr[USize]): CurlCode =
  //     extern

  //   def curl_easy_setopt(curl: Ptr[Curl], option: CurlOption*): CurlCode = extern

  //   def curl_easy_upkeep(curl: Ptr[Curl]): CurlCode = extern
