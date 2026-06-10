package snhttp.experimental.curl

import scala.scalanative.unsafe.fromCString

import _root_.snhttp.experimental.curl._libcurl.Curl.CurlGlobalFlag

package object curl:

  export _root_.snhttp.experimental.curl._libcurl.Curl.{
    CurlOption,
    CurlErrCode,
    CurlShareErrCode,
    CurlHttpVersion,
    CurlWriteCallback,
    CurlUseSSL,
    CurlFollow,
    CurlDebugCallback,
    CurlInfoType,
    CurlWriteFuncRet,
  }
  export _root_.snhttp.experimental.curl._libcurl.Easy.CurlData
  export _root_.snhttp.experimental.curl._libcurl.Multi.{
    CurlMsgCode,
    CurlMultiErrCode,
    CurlMultiOption,
  }

  object CurlUtils:

    def version(): String =
      fromCString(libcurl.curlVersion())

    def getStrError(code: CurlErrCode): String =
      fromCString(libcurl.easyStrError(code))

    def getMultiStrError(code: CurlMultiErrCode): String =
      fromCString(libcurl.multiStrError(code))

  end CurlUtils

  object CurlGlobal:

    def init(flags: CurlGlobalFlag): CurlErrCode =
      libcurl.globalInit(flags)

    def cleanup(): Unit =
      libcurl.globalCleanup()

  end CurlGlobal

end curl
