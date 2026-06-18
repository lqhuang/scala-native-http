package snhttp.experimental.curl

import scala.scalanative.unsafe.fromCString

import _root_.snhttp.experimental.curl._libcurl.Curl.CurlGlobalFlag

package object curl:

  export _root_.snhttp.experimental.curl._libcurl.Curl.{
    CurlOption,
    CurlErrCode,
    CurlShareErrCode,
    CurlHttpVersion,
    CurlUseSsl,
    CurlFollow,
    CurlInfoType,
    CurlWriteFuncRet,
    CurlSocket,
    CurlGlobalFlag,
    CurlReadCallback,
    CurlWriteCallback,
    CurlTrailerCallback,
    CurlDebugCallback,
    CurlXferInfoCallback,
    CurlSockOptCallback,
    CurlOpenSocketCallback,
    CurlCloseSocketCallback,
    CurlIoCtlCallback,
  }
  export _root_.snhttp.experimental.curl._libcurl.Multi.{
    CurlWaitFd,
    CurlMsgCode,
    CurlMultiErrCode,
    CurlMultiOption,
    CurlPoll,
    CurlCSelect,
    CurlSocketCallback,
    CurlMultiTimerCallback,
    CurlPushCallback,
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
