package snhttp.experimental.curl

import scala.scalanative.unsafe.{Ptr, Size, CString, toCString, CInt, CLong, Zone, CFuncPtr}
import scala.scalanative.posix.stddef.size_t

import _root_.snhttp.experimental.libcurl.{
  CurlOption,
  CurlErrCode,
  Curl as _Curl,
  CurlPause,
  CurlInfo as _CurlInfo,
  CurlSlist as _CurlSlist,
  CurlBlob as _CurlBlob,
}
import _root_.snhttp.experimental.libcurl

class CurlEasy(ptr: Ptr[_Curl]) extends AnyVal:

  inline def info: CurlInfo =
    CurlInfo(ptr)

  def setCIntOption(option: CurlOption, value: CInt): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  def setCLongOption(option: CurlOption, value: CLong): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  def setStringOption(option: CurlOption, value: String)(using zone: Zone): Unit =
    val ret = libcurl.easySetopt(ptr, option, toCString(value))
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  def setPtrOption(option: CurlOption, value: Ptr[?]): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  def setSlistOption(option: CurlOption, value: Ptr[_CurlSlist]): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  def setBlobOption(option: CurlOption, value: Ptr[_CurlBlob]): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  def setFuncPtrOption(option: CurlOption, value: CFuncPtr): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  def perform(): CurlErrCode =
    libcurl.easyPerform(ptr)

  def pause(bitmask: CurlPause): CurlErrCode =
    libcurl.easyPause(ptr, bitmask)

  def cleanup(): Unit =
    libcurl.easyCleanup(ptr)

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

  def apply(ptr: Ptr[_Curl]): CurlEasy =
    new CurlEasy(ptr)

end CurlEasy
