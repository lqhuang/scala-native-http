package snhttp.experimental.curl

import scala.scalanative.unsafe.{Ptr, Size, CString, toCString, CLong}
import scala.scalanative.posix.stddef.size_t

import _root_.snhttp.experimental.libcurl.{
  CurlOption,
  CurlErrCode,
  Curl as _Curl,
  CurlPause,
  CurlInfo,
  CurlSlist as _CurlSlist,
}
import _root_.snhttp.experimental.libcurl
import snhttp.experimental._libcurl.multi.CurlWait

class CurlEasy(ptr: Ptr[_Curl]) extends AnyVal:

  def setOption(option: CurlOption, value: CLong): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)

  def setOption(option: CurlOption, value: CString): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)

  // def setOption(option: CurlOption, value: Unit): Unit =
  //   val ret = libcurl.easySetopt(ptr, option, value)

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
