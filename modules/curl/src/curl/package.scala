package snhttp.experimental

import _root_.snhttp.experimental.libcurl.{CurlErrCode, CurlGlobalFlag, CurlMultiCode}
import _root_.snhttp.experimental.libcurl

package object curl:

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
