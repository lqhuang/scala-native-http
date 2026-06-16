package snhttp.experimental.curl
package curl

import scala.scalanative.unsafe.Ptr

import _root_.snhttp.experimental.curl.libcurl.{CurlMsg as _CurlMsg, CurlMsgCode, CurlMsgData}

class CurlMsg(val ptr: Ptr[_CurlMsg]) extends AnyVal:

  import _CurlMsg.*

  inline def msg: CurlMsgCode =
    (!ptr).msg

  inline def curl: CurlEasy =
    CurlEasy((!ptr).easyHandle)

  inline def data: CurlMsgData =
    (!ptr).data
