package snhttp.experimental.curl

import _root_.snhttp.experimental.curl._libcurl.Curl.CurlGlobalFlag

package object curl:

  export _root_.snhttp.experimental.curl._libcurl.Curl.{
    CurlOption,
    CurlErrCode,
    CurlHttpVersion,
    CurlWriteCallback,
    CurlUseSsl,
    CurlFollow,
    CurlDebugCallback,
    CurlInfoType,
    CurlWriteFuncRet,
  }
  export _root_.snhttp.experimental.curl._libcurl.Easy.CurlData
  export _root_.snhttp.experimental.curl._libcurl.Multi.{
    CurlMsgCode,
    CurlMultiCode,
    CurlMultiOption,
  }

  object CurlUtils:

    def version(): String =
      libcurl.curlVersion().toString

    def getStrError(code: CurlErrCode): String =
      libcurl.easyStrError(code).toString

    def getMultiStrError(code: CurlMultiCode): String =
      libcurl.multiStrError(code).toString

  end CurlUtils

  object CurlGlobal:

    def init(flags: CurlGlobalFlag): CurlErrCode =
      libcurl.globalInit(flags)

    def cleanup(): Unit =
      libcurl.globalCleanup()

  end CurlGlobal

end curl
