package snhttp.experimental.curl
package curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{Ptr, CString, CLong, CFuncPtr}
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

  private transparent inline def assertErrCode(err: CurlErrCode): Unit =
    if (err != CurlErrCode.OK) throw new CurlErrCodeException(err)

  inline def info: CurlInfo =
    if ref == NULL
    then //
      throw new IllegalStateException("CurlHandle is null or already cleaned up, cannot get info.")
    else //
      CurlInfo(ref)

  inline def setCLongOption(option: CurlOption, value: CLong): Unit =
    val err = libcurl.easySetOpt(ref, option, value)
    if (err != CurlErrCode.OK)
      throw new CurlSetOptionException(option, value, err)

  inline def setCStringOption(option: CurlOption, value: CString): Unit =
    val err = libcurl.easySetOpt(ref, option, value)
    if (err != CurlErrCode.OK)
      throw new CurlSetOptionException(option, value, err)

  inline def setPtrOption(option: CurlOption, value: Ptr[?]): Unit =
    val err = libcurl.easySetOpt(ref, option, value)
    if (err != CurlErrCode.OK)
      throw new CurlSetOptionException(option, value, err)

  inline def setSlistOption(option: CurlOption, value: CurlSList): Unit =
    val err = libcurl.easySetOpt(ref, option, value.ref)
    if (err != CurlErrCode.OK)
      throw new CurlSetOptionException(option, value.ref, err)

  inline def setBlobOption(option: CurlOption, value: Ptr[_CurlBlob]): Unit =
    val err = libcurl.easySetOpt(ref, option, value)
    if (err != CurlErrCode.OK)
      throw new CurlSetOptionException(option, value, err)

  inline def setFuncPtrOption(option: CurlOption, value: CFuncPtr): Unit =
    val err = libcurl.easySetOpt(ref, option, value)
    if (err != CurlErrCode.OK)
      throw new CurlSetOptionException(option, value, err)

  inline def perform(): CurlErrCode =
    libcurl.easyPerform(ref)

  inline def recv(buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    libcurl.easyRecv(ref, buffer, buflen, n)

  inline def send(buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    libcurl.easySend(ref, buffer, buflen, n)

  inline def pause(bitmask: CurlPause): Unit =
    assertErrCode(libcurl.easyPause(ref, bitmask))

  inline def upkeep(): Unit =
    assertErrCode(libcurl.easyUpkeep(ref))

  inline def cleanup(): Unit =
    libcurl.easyCleanup(ref)

  inline def reset(): Unit =
    libcurl.easyReset(ref)

object CurlEasy:

  given Releasable[CurlEasy] with
    def release(easy: CurlEasy): Unit =
      easy.cleanup()

  def apply(): CurlEasy =
    val ptr = libcurl.easyInit()
    if (ptr == NULL)
      throw new CurlException("Failed to initialize CurlEasy Handle")
    new CurlEasy(ptr)

  def apply(ptr: Ptr[CurlHandle]): CurlEasy =
    new CurlEasy(ptr)

end CurlEasy
