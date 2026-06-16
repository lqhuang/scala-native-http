package snhttp.experimental.curl
package curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{Ptr, CInt}
import scala.scalanative.libc.stddef.NULL

import _root_.snhttp.experimental.curl.libcurl.{CurlShareOption, CurlShareErrCode, CurlShareHandle}

class CurlShare(ref: Ptr[CurlShareHandle]) extends AnyVal:

  inline def setOption(option: CurlShareOption, value: CInt): Unit =
    val ret = libcurl.shareSetOpt(ref, option, value)
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

  def apply(ref: Ptr[CurlShareHandle]): CurlShare =
    new CurlShare(ref)

end CurlShare
