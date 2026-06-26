package snhttp.experimental.curl
package curl

import java.nio.charset.StandardCharsets

import scala.collection.mutable.{TreeMap, ListBuffer}
import scala.math.Ordering.comparatorToOrdering
import scala.scalanative.unsafe.{Ptr, CLong}
import scala.scalanative.unsafe.{stackalloc, fromCString}
import scala.scalanative.libc.stddef.NULL as NullPtr

import _root_.snhttp.experimental.curl.libcurl.{
  CurlInfo as _CurlInfo,
  CurlHandle,
  CurlHttpVersion,
  CurlHeader,
  CurlHeaderOrigin,
  CurlErrCode,
}

class CurlInfo(ref: Ptr[CurlHandle]) extends AnyVal:

  inline def osErrNo: Int =
    val _osErrno = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.OS_ERRNO, _osErrno))
    (!_osErrno).toInt

  inline def responseCode: Int =
    val _respCode = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.RESPONSE_CODE, _respCode))
    (!_respCode).toInt

  inline def requestSize: Long =
    val _reqSize = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.REQUEST_SIZE, _reqSize))
    (!_reqSize).toLong

  inline def version: CurlHttpVersion =
    val _version = stackalloc[CurlHttpVersion]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.HTTP_VERSION, _version))
    !_version // TODO: Copy?

  inline def numConnects: Long =
    val _numConnects = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.NUM_CONNECTS, _numConnects))
    (!_numConnects).toLong

  inline def connID: Long =
    val _connId = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.CONN_ID, _connId))
    (!_connId).toLong

  inline def headerSize: Long =
    val _hs = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.HEADER_SIZE, _hs))
    (!_hs).toLong

  inline def httpConnectCode: Int =
    val _httpConnectCode = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.HTTP_CONNECTCODE, _httpConnectCode))
    (!_httpConnectCode).toInt

  inline def proxyAuthAvail: Long =
    val _proxyAuthAvail = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.PROXYAUTH_AVAIL, _proxyAuthAvail))
    (!_proxyAuthAvail).toLong

  inline def proxyAuthUsed: Long =
    val _proxyAuthUsed = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.PROXYAUTH_USED, _proxyAuthUsed))
    (!_proxyAuthUsed).toLong

  inline def proxyError: Int =
    val _proxyError = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.PROXY_ERROR, _proxyError))
    (!_proxyError).toInt

  inline def proxySSLVerifyResult: Int =
    val _proxySSLVerifyResult = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.PROXY_SSL_VERIFYRESULT, _proxySSLVerifyResult))
    (!_proxySSLVerifyResult).toInt

  inline def redirectCount: Int =
    val _redirectCount = stackalloc[CLong]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.REDIRECT_COUNT, _redirectCount))
    (!_redirectCount).toInt

  inline def redirectURL: String =
    val _redirectUrlPtr = stackalloc[Ptr[Byte]]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.REDIRECT_URL, _redirectUrlPtr))
    fromCString(!_redirectUrlPtr)

  inline def redirectTime: Long =
    val _redirectTime = stackalloc[Long]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.REDIRECT_TIME_T, _redirectTime))
    !_redirectTime

  inline def effectiveURL: String =
    var buffer = stackalloc[Ptr[Byte]]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.EFFECTIVE_URL, buffer))
    fromCString(!buffer)

  inline def effectiveMethod: String =
    var buffer = stackalloc[Ptr[Byte]]()
    assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.EFFECTIVE_METHOD, buffer))
    fromCString(!buffer)

  // inline def tlsSession: Ptr[?] =
  //   val _tlsSession = stackalloc[Ptr[?]]()
  //   assertErrCode(libcurl.easyGetInfo(ref, _CurlInfo.TLS_SSL_PTR, _tlsSession))
  //   !_tlsSession

  inline def headers: Map[String, List[String]] = {
    var headerPtr: Ptr[CurlHeader] = null
    var prevHeaderPtr: Ptr[CurlHeader] = null

    val _map: TreeMap[String, ListBuffer[String]] =
      TreeMap.empty(using comparatorToOrdering(String.CASE_INSENSITIVE_ORDER))

    while
      headerPtr = libcurl.easyNextHeader(ref, CurlHeaderOrigin.HEADER, -1, prevHeaderPtr)
      headerPtr != NullPtr
    do {
      val name = fromCString((!headerPtr).name, StandardCharsets.UTF_8)
      val value = fromCString((!headerPtr).value, StandardCharsets.UTF_8)

      _map.get(name) match
        case Some(xs) =>
          xs.addOne(value): Unit
        case None =>
          val xs = ListBuffer(value)
          _map.addOne((name, xs)): Unit

      prevHeaderPtr = headerPtr
    }

    _map.view.mapValues(_.toList).toMap
  }

  private transparent inline def assertErrCode(err: CurlErrCode): Unit =
    if (err != CurlErrCode.OK)
      throw new CurlErrCodeException(err)
