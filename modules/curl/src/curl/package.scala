package snhttp.experimental.curl

import scala.scalanative.unsafe.fromCString

import _root_.snhttp.experimental.curl._libcurl.Curl.CurlGlobalFlag

package object curl:

  export _root_.snhttp.experimental.curl._libcurl.System.CurlOff
  export _root_.snhttp.experimental.curl._libcurl.Curl.{
    CurlOption,
    CurlErrCode,
    CurlShareErrCode,
    CurlHttpVersion,
    CurlUseSsl,
    CurlFollow,
    CurlRedir,
    CurlInfoType,
    CurlReadFunc,
    CurlWriteFunc,
    CurlSeekFunc,
    CurlSocket,
    CurlGlobalFlag,
    CurlPause,
    CURL_MAX_READ_SIZE,
    CURL_MAX_WRITE_SIZE,
    CURL_MAX_HTTP_HEADER,
  }
  export _root_.snhttp.experimental.curl._libcurl.Curl.{
    CurlSeekCallback,
    CurlReadCallback,
    CurlWriteCallback,
    CurlTrailerCallback,
    CurlDebugCallback,
    CurlXferInfoCallback,
    CurlSockOptCallback,
    CurlOpenSocketCallback,
    CurlCloseSocketCallback,
    CurlSslCtxCallback,
    CurlIoCtlCallback,
    CurlHeaderCallback,
  }
  export _root_.snhttp.experimental.curl._libcurl.Multi.{
    CurlWaitFd,
    CurlMsgCode,
    CurlMsgData,
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
