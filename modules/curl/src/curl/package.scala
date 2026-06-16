package snhttp.experimental.curl

import scala.scalanative.unsafe.fromCString

import _root_.snhttp.experimental.curl._libcurl.Curl.CurlGlobalFlag

package object curl:

  export _root_.snhttp.experimental.curl._libcurl.Curl.{
    CurlOption,
    CurlErrCode,
    CurlShareErrCode,
    CurlHttpVersion,
    CurlReadCallback,
    CurlWriteCallback,
    CurlUseSsl,
    CurlFollow,
    CurlDebugCallback,
    CurlInfoType,
    CurlWriteFuncRet,
    CurlSocket,
    CurlGlobalFlag,
  }
  export _root_.snhttp.experimental.curl._libcurl.Multi.{
    CurlMsgCode,
    CurlMultiErrCode,
    CurlMultiOption,
    CurlPoll,
    CurlCSelect,
    CurlSocketCallback,
  }

  def version(): String =
    fromCString(libcurl.curlVersion())

  def getStrError(code: CurlErrCode): String =
    fromCString(libcurl.easyStrError(code))

  def getMultiStrError(code: CurlMultiErrCode): String =
    fromCString(libcurl.multiStrError(code))

  object CurlGlobal:

    def init(flags: CurlGlobalFlag): CurlErrCode =
      libcurl.globalInit(flags)

    def cleanup(): Unit =
      libcurl.globalCleanup()

  end CurlGlobal

end curl
