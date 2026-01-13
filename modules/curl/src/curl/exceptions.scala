package snhttp.experimental.curl

import _root_.snhttp.experimental.libcurl.{CurlOption, CurlErrCode, easyStrError}
import _root_.snhttp.experimental.libcurl.CurlErrCode.RichCurlErrCode
import scala.scalanative.unsafe.{CInt, CLong, Ptr, CFuncPtr, fromCString}

class CurlException(message: String, exc: Throwable) extends RuntimeException(message, exc):
  def this(message: String) = this(message, null)
  def this(exc: Throwable) = this(null, exc)

class CurlSetOptionException(
    val option: CurlOption,
    val value: CInt | CLong | String | Ptr[?] | CFuncPtr,
    val code: CurlErrCode,
) extends CurlException(
      s"Failed to set option: ${option} to value: ${value}, error code: ${code} (${code.getname})",
    )

class CurlErrCodeException(val code: CurlErrCode, val detail: Boolean = true)
    extends CurlException(
      if !detail
      then s"CURL operation failed with error code: ${code} (${code.getname})."
      else
        s"CURL operation failed with error code: ${code} (${code.getname}). " ++
          s"Details: ${fromCString(easyStrError(code))}",
    )

class CurlOSException(val code: Int)
    extends CurlException(
      s"CURL operation failed with OS error code: ${code}",
    )
