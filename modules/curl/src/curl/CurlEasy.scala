package snhttp.experimental.curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{Ptr, CString, CInt, CLong, CFuncPtr}
import scala.scalanative.posix.stddef.size_t
import scala.scalanative.libc.stddef.NULL

import _root_.snhttp.experimental.libcurl.{
  CurlOption,
  CurlErrCode,
  Curl as _Curl,
  CurlPause,
  CurlBlob as _CurlBlob,
}
import _root_.snhttp.experimental.libcurl

class CurlEasy(val ptr: Ptr[_Curl]) extends AnyVal:

  transparent inline def info: CurlInfo =
    CurlInfo(ptr)

  transparent inline def setCIntOption(option: CurlOption, value: CInt): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  transparent inline def setCLongOption(option: CurlOption, value: CLong): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  transparent inline def setCStringOption(option: CurlOption, value: CString): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  transparent inline def setPtrOption(option: CurlOption, value: Ptr[?]): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  transparent inline def setSlistOption(option: CurlOption, value: CurlSlist): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value.ptr, ret)

  transparent inline def setBlobOption(option: CurlOption, value: Ptr[_CurlBlob]): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  transparent inline def setFuncPtrOption(option: CurlOption, value: CFuncPtr): Unit =
    val ret = libcurl.easySetopt(ptr, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  transparent inline def perform(): CurlErrCode =
    libcurl.easyPerform(ptr)

  transparent inline def pause(bitmask: CurlPause): CurlErrCode =
    libcurl.easyPause(ptr, bitmask)

  transparent inline def cleanup(): Unit =
    libcurl.easyCleanup(ptr)

  transparent inline def reset(): Unit =
    libcurl.easyReset(ptr)

  transparent inline def recv(buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    libcurl.easyRecv(ptr, buffer, buflen, n)

  transparent inline def send(buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    libcurl.easySend(ptr, buffer, buflen, n)

  transparent inline def upkeep(): CurlErrCode =
    libcurl.easyUpkeep(ptr)

object CurlEasy:

  given Releasable[CurlEasy] with
    transparent inline def release(easy: CurlEasy): Unit =
      easy.cleanup()

  def apply(): CurlEasy =
    val ptr = libcurl.easyInit()
    if (ptr == NULL)
      throw new RuntimeException("Failed to initialize CurlEasy")
    new CurlEasy(ptr)

  def apply(ptr: Ptr[_Curl]): CurlEasy =
    new CurlEasy(ptr)

end CurlEasy
