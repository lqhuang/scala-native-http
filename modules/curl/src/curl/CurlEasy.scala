package snhttp.experimental.curl
package curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{Ptr, CString, CInt, CLong, CFuncPtr}
import scala.scalanative.posix.stddef.size_t
import scala.scalanative.libc.stddef.NULL

import _root_.snhttp.experimental.curl.libcurl.{
  CurlOption,
  CurlErrCode,
  CurlHandle,
  CurlPause,
  CurlBlob as _CurlBlob,
}

class CurlEasy(val ref: Ptr[CurlHandle]) extends AnyVal derives CanEqual:

  inline def info: CurlInfo =
    CurlInfo(ref)

  inline def setCIntOption(option: CurlOption, value: CInt): Unit =
    val ret = libcurl.easySetOpt(ref, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  inline def setCLongOption(option: CurlOption, value: CLong): Unit =
    val ret = libcurl.easySetOpt(ref, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  inline def setCStringOption(option: CurlOption, value: CString): Unit =
    val ret = libcurl.easySetOpt(ref, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  inline def setPtrOption(option: CurlOption, value: Ptr[?]): Unit =
    val ret = libcurl.easySetOpt(ref, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  inline def setSlistOption(option: CurlOption, value: CurlSlist): Unit =
    val ret = libcurl.easySetOpt(ref, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value.ref, ret)

  inline def setBlobOption(option: CurlOption, value: Ptr[_CurlBlob]): Unit =
    val ret = libcurl.easySetOpt(ref, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  inline def setFuncPtrOption(option: CurlOption, value: CFuncPtr): Unit =
    val ret = libcurl.easySetOpt(ref, option, value)
    if ret != CurlErrCode.OK then throw new CurlSetOptionException(option, value, ret)

  inline def perform(): CurlErrCode =
    libcurl.easyPerform(ref)

  inline def pause(bitmask: CurlPause): CurlErrCode =
    libcurl.easyPause(ref, bitmask)

  inline def cleanup(): Unit =
    libcurl.easyCleanup(ref)

  inline def reset(): Unit =
    libcurl.easyReset(ref)

  inline def recv(buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    libcurl.easyRecv(ref, buffer, buflen, n)

  inline def send(buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    libcurl.easySend(ref, buffer, buflen, n)

  inline def upkeep(): CurlErrCode =
    libcurl.easyUpkeep(ref)

object CurlEasy:

  given Releasable[CurlEasy] with
    inline def release(easy: CurlEasy): Unit =
      easy.cleanup()

  def apply(): CurlEasy =
    val ptr = libcurl.easyInit()
    if (ptr == NULL)
      throw new RuntimeException("Failed to initialize CurlEasy")
    new CurlEasy(ptr)

  def apply(ptr: Ptr[CurlHandle]): CurlEasy =
    new CurlEasy(ptr)

end CurlEasy
