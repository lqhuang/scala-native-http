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
}

class CurlInfo(ptr: Ptr[CurlHandle]) extends AnyVal:

  inline def osErrNo: Int =
    val _osErrno = stackalloc[CLong]()
    val ret = libcurl.easyGetInfo(ptr, _CurlInfo.OS_ERRNO, _osErrno)
    if ret != libcurl.CurlErrCode.OK then
      throw new RuntimeException(s"Failed to get OS_ERRNO: ${ret}")
    (!_osErrno).toInt

  inline def responseCode: Int =
    val _respCode = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(
      ptr,
      _CurlInfo.RESPONSE_CODE,
      _respCode,
    )
    (!_respCode).toInt

  inline def requestSize: Long =
    val _reqSize = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.REQUEST_SIZE, _reqSize)
    (!_reqSize).toLong

  inline def version: CurlHttpVersion =
    val _version = stackalloc[CurlHttpVersion]()
    val _ = libcurl.easyGetInfo(
      ptr,
      _CurlInfo.HTTP_VERSION,
      _version,
    )
    !_version // TODO: Copy?

  inline def numConnects: Long =
    val _numConnects = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.NUM_CONNECTS, _numConnects)
    (!_numConnects).toLong

  inline def connID: Long =
    val _connId = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(
      ptr,
      _CurlInfo.CONN_ID,
      _connId,
    )
    (!_connId).toLong

  inline def headerSize: Long =
    val _hs = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.HEADER_SIZE, _hs)
    (!_hs).toLong

  inline def httpConnectCode: Int =
    val _httpConnectCode = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.HTTP_CONNECTCODE, _httpConnectCode)
    (!_httpConnectCode).toInt

  inline def proxyAuthAvail: Long =
    val _proxyAuthAvail = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.PROXYAUTH_AVAIL, _proxyAuthAvail)
    (!_proxyAuthAvail).toLong

  inline def proxyAuthUsed: Long =
    val _proxyAuthUsed = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.PROXYAUTH_USED, _proxyAuthUsed)
    (!_proxyAuthUsed).toLong

  inline def proxyError: Int =
    val _proxyError = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.PROXY_ERROR, _proxyError)
    (!_proxyError).toInt

  inline def proxySSLVerifyResult: Int =
    val _proxySSLVerifyResult = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.PROXY_SSL_VERIFYRESULT, _proxySSLVerifyResult)
    (!_proxySSLVerifyResult).toInt

  inline def redirectCount: Int =
    val _redirectCount = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.REDIRECT_COUNT, _redirectCount)
    (!_redirectCount).toInt

  inline def redirectURL: String =
    val _redirectUrlPtr = stackalloc[Ptr[Byte]]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.REDIRECT_URL, _redirectUrlPtr)
    fromCString(!_redirectUrlPtr)

  inline def redirectTime: Long =
    val _redirectTime = stackalloc[Long]()
    val _ = libcurl.easyGetInfo(ptr, _CurlInfo.REDIRECT_TIME_T, _redirectTime)
    !_redirectTime

  inline def headers: Map[String, List[String]] = {
    var headerPtr: Ptr[CurlHeader] = null
    var prevHeaderPtr: Ptr[CurlHeader] = null

    val _map: TreeMap[String, ListBuffer[String]] =
      TreeMap.empty(using comparatorToOrdering(String.CASE_INSENSITIVE_ORDER))

    while
      headerPtr = libcurl.easyNextHeader(ptr, CurlHeaderOrigin.HEADER, -1, prevHeaderPtr)
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
