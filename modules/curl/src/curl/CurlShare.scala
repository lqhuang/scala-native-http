package snhttp.experimental.curl
package curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{Ptr, CInt}
import scala.scalanative.libc.stddef.NULL

import _root_.snhttp.experimental.curl.libcurl.{
  CurlLockData,
  CurlShareOption,
  CurlShareErrCode,
  CurlShareHandle,
}

class CurlShare(val ref: Ptr[CurlShareHandle]) extends AnyVal:

  inline def setOption(option: CurlShareOption, value: CInt): Unit =
    val ret = libcurl.shareSetOpt(ref, option, value)
    if (ret != CurlShareErrCode.OK)
      throw new CurlShareSetOptionException(option, value, ret)

  inline def share(data: CurlLockData): Unit =
    setOption(CurlShareOption.SHARE, data.value)

  inline def unshare(data: CurlLockData): Unit =
    setOption(CurlShareOption.UNSHARE, data.value)

  inline def cleanup(): Unit =
    val ret = libcurl.shareCleanup(ref)
    if (ret != CurlShareErrCode.OK)
      throw new CurlShareException(ret)

object CurlShare:

  given Releasable[CurlShare] with
    def release(share: CurlShare): Unit =
      share.cleanup()

  def apply(shares: CurlLockData*): CurlShare =
    val ptr = libcurl.shareInit()
    if (ptr == NULL)
      throw new CurlException("Failed to initialize CurlShare")
    val share = new CurlShare(ptr)
    try
      shares.foreach(data => share.share(data))
      share
    catch
      case exc: Throwable =>
        libcurl.shareCleanup(ptr): Unit
        throw exc

  def apply(ref: Ptr[CurlShareHandle]): CurlShare =
    new CurlShare(ref)

end CurlShare
