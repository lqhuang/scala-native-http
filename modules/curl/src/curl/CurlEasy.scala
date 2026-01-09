package snhttp.experimental.curl

import scala.scalanative.unsafe.{Ptr, Size, CString, toCString, CLong, Zone}
import scala.scalanative.posix.stddef.size_t

import _root_.snhttp.experimental.libcurl.{
  CurlOption,
  CurlErrCode,
  Curl as _Curl,
  CurlPause,
  CurlInfo,
  CurlSlist as _CurlSlist,
  CurlBlob as _CurlBlob,
}
import _root_.snhttp.experimental.libcurl
import snhttp.experimental._libcurl.multi.CurlWait

class CurlEasy(ptr: Ptr[_Curl]) extends AnyVal:

  def setCLongOption(option: CurlOption, value: CLong): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)

  def setStringOption(using zone: Zone)(option: CurlOption, value: String): Unit =
    val ret = libcurl.easySetopt(ptr, option, toCString(value))

  def setPtrOption(option: CurlOption, value: Ptr[?]): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)

  def setSlistOption(option: CurlOption, value: _CurlSlist): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)

  def setBlobOption(option: CurlOption, value: _CurlBlob): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)

  def perform(): CurlErrCode =
    libcurl.easyPerform(ptr)

  def pause(bitmask: CurlPause): CurlErrCode =
    libcurl.easyPause(ptr, bitmask)

  def cleanup(): Unit =
    libcurl.easyCleanup(ptr)

  def getInfo(info: CurlInfo, value: Size): Unit =
    val ret = libcurl.easyGetInfo(ptr, info, value)

  def reset(): Unit =
    libcurl.easyReset(ptr)

  def recv(buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    libcurl.easyRecv(ptr, buffer, buflen, n)

  def send(buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    libcurl.easySend(ptr, buffer, buflen, n)

  def upkeep(): CurlErrCode =
    libcurl.easyUpkeep(ptr)

object CurlEasy:

  def apply(): CurlEasy =
    val cptr = libcurl.easyInit()
    new CurlEasy(cptr)

end CurlEasy
