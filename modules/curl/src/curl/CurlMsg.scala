package snhttp.experimental.curl
package curl

import scala.scalanative.unsafe.Ptr

import _root_.snhttp.experimental.curl.libcurl.{CurlMsg as _CurlMsg, CurlMsgCode, CurlMsgData}

class CurlMsg(val ref: Ptr[_CurlMsg]) extends AnyVal:

  import _CurlMsg.*

  inline def msg: CurlMsgCode =
    (!ref).msg

  inline def curl: CurlEasy =
    CurlEasy((!ref).easyHandle)

  inline def data: CurlMsgData =
    (!ref).data
