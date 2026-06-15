package snhttp.experimental.curl
package curl

import scala.scalanative.unsafe.{CInt, CLong, Ptr, CFuncPtr, fromCString}

import _root_.snhttp.experimental.curl.libcurl.{
  CurlOption,
  CurlErrCode,
  CurlMultiOption,
  CurlMultiErrCode,
  CurlShareErrCode,
  CurlShareOption,
}
import _root_.snhttp.experimental.curl.libcurl.{easyStrError, multiStrError}
import _root_.snhttp.experimental.curl.libcurl.CurlErrCode.RichCurlErrCode
import _root_.snhttp.experimental.curl.libcurl.CurlMultiErrCode.RichCurlMultiErrCode

class CurlException(message: String, exc: Throwable) extends RuntimeException(message, exc):
  def this(message: String) = this(message, null)
  def this(exc: Throwable) = this(null, exc)

class CurlSetOptionException(
    option: CurlOption,
    value: CInt | CLong | String | Ptr[?] | CFuncPtr,
    code: CurlErrCode,
) extends CurlException(
      s"Failed to set option: ${option} to value: ${value}, error code: ${code} (${code.getname})",
    )

class CurlMultiSetOptionException(
    option: CurlMultiOption,
    value: CInt | CLong | String | Ptr[?] | CFuncPtr,
    code: CurlMultiErrCode,
) extends CurlException(
      s"Failed to set option: ${option} to value: ${value}, error code: ${code} (${RichCurlMultiErrCode(code).getname})",
    )

class CurlShareSetOptionException(
    option: CurlShareOption,
    value: CInt,
    code: CurlShareErrCode,
) extends CurlException(
      s"Failed to set option: ${option} to value: ${value}, error code: ${code}",
    )

class CurlErrCodeException(val code: CurlErrCode, val detail: Boolean = true)
    extends CurlException(
      if !detail
      then s"CURL operation failed with error code: ${code} (${code.getname})."
      else
        s"CURL operation failed with error code: ${code} (${code.getname}). " ++
          s"Details: ${fromCString(easyStrError(code))}",
    )

class CurlMultiException(val code: CurlMultiErrCode, val detail: Boolean = true)
    extends CurlException(
      if !detail
      then s"CURL multi operation failed with error code: ${code} (${code.getname})."
      else
        s"CURL multi operation failed with error code: ${code} (${code.getname}). " ++
          s"Details: ${fromCString(multiStrError(code))}",
    )

// class CurlShareException(val code: CurlShareErrCode, val detail: Boolean = true)
//     extends CurlException(
//       if !detail
//       then s"CURL share operation failed with error code: ${code} (${code.getname})."
//       else
//         s"CURL share operation failed with error code: ${code} (${code.getname}). " ++
//           s"Details: ${fromCStrin(code))}",
//     )

class CurlOSException(val code: Int)
    extends CurlException(
      s"CURL operation failed with OS error code: ${code}",
    )
