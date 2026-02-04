package snhttp.experimental.curl

import scala.scalanative.unsafe.Ptr

import _root_.snhttp.experimental.libcurl.{CurlMsg as _CurlMsg, CurlMsgCode, CurlMsgData}

// Public class but cannot be instantiated outside of the curl package
class CurlMsg private[curl] (val ptr: Ptr[_CurlMsg]) extends AnyVal:

  transparent inline def msg: CurlMsgCode =
    (!ptr).msg

  transparent inline def curl: CurlEasy =
    CurlEasy((!ptr).easyHandle)

  transparent inline def data: CurlMsgData =
    (!ptr).data
