package snhttp.experimental.curl
package curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{Ptr, CString, CInt, CLong, CFuncPtr}
import scala.scalanative.posix.stddef.size_t
import scala.scalanative.libc.stddef.NULL

import _root_.snhttp.experimental.curl.libcurl.{
  CurlShareOption,
  CurlShareErrCode,
  CurlShare as _CurlShare,
}
import _root_.snhttp.experimental.curl.libcurl

class CurlShare(ref: Ptr[_CurlShare]) extends AnyVal:

  inline def setOption(option: CurlShareOption, value: CInt): Unit =
    val ret = libcurl.shareSetopt(ref, option, value)
    if ret != CurlShareErrCode.OK then throw new CurlShareSetOptionException(option, value, ret)

  inline def cleanup(): Unit =
    libcurl.shareCleanup(ref): Unit

object CurlShare:

  given Releasable[CurlShare] with
    inline def release(easy: CurlShare): Unit =
      easy.cleanup()

  def apply(): CurlShare =
    val ptr = libcurl.shareInit()
    if (ptr == NULL)
      throw new RuntimeException("Failed to initialize CurlShare")
    new CurlShare(ptr)

  def apply(ref: Ptr[_CurlShare]): CurlShare =
    new CurlShare(ref)

end CurlShare
