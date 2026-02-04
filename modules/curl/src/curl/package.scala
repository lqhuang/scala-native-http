package snhttp.experimental

package object curl:

  export _root_.snhttp.experimental.libcurl.CurlGlobalFlag
  export _root_.snhttp.experimental.libcurl.{
    CurlOption,
    CurlErrCode,
    CurlHttpVersion,
    CurlWriteCallback,
    CurlData,
    CurlUseSsl,
    CurlFollow,
    CurlMsgCode,
    CurlMultiCode,
    CurlMultiOption,
    CurlDebugCallback,
    CurlInfoType,
    CurlWriteFuncRet,
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
