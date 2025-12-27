package snhttp.jdk.net.http.internal

import java.net.http.HttpClient.{Version, Redirect}
import java.net.http.HttpHeaders
import java.net.http.HttpResponse.ResponseInfo
import java.nio.charset.StandardCharsets
import java.util.{ArrayList, TreeMap}
import java.util.List as JList

import scala.scalanative.unsafe.{Ptr, CLong, Zone}
import scala.scalanative.unsafe.{stackalloc, fromCString}

import snhttp.utils.PointerFinalizer
import snhttp.experimental.libcurl
import snhttp.experimental.libcurl.{CurlInfo, Curl, CurlHttpVersion, CurlHeader, CurlHeaderOrigin}

class CurlGetInfoHelper(ptr: Ptr[Curl]) extends ResponseInfo with AutoCloseable:

  given zone: Zone = Zone.open()

  def close(): Unit =
    zone.close()

  def statusCode(): Int =
    val _respCodePtr = stackalloc[CLong]()
    val _ = libcurl.easyGetInfo(
      ptr,
      CurlInfo.RESPONSE_CODE,
      _respCodePtr,
    )
    (!_respCodePtr).toInt

  def version(): Version =
    val _versionPtr = stackalloc[CurlHttpVersion]()
    val _ = libcurl.easyGetInfo(
      ptr,
      CurlInfo.HTTP_VERSION,
      _versionPtr,
    )
    !_versionPtr match
      case CurlHttpVersion.VERSION_1_1               => Version.HTTP_1_1
      case CurlHttpVersion.VERSION_2_0               => Version.HTTP_2
      case CurlHttpVersion.VERSION_2TLS              => Version.HTTP_2
      case CurlHttpVersion.VERSION_2_PRIOR_KNOWLEDGE => Version.HTTP_2
      case _ =>
        throw new RuntimeException(
          s"Unsupported HTTP version response code with libcurl: ${!_versionPtr}",
        )

  def headers(): HttpHeaders = {
    var _headerPtr: Ptr[CurlHeader] = null
    var _prevHeaderPtr: Ptr[CurlHeader] = null

    val _map: TreeMap[String, JList[String]] = new TreeMap(String.CASE_INSENSITIVE_ORDER)
    while {
      _headerPtr = libcurl.easyNextHeader(ptr, CurlHeaderOrigin.HEADER, -1, _prevHeaderPtr)
      _headerPtr != null
    } do {
      val name = fromCString((!_headerPtr).name, StandardCharsets.UTF_8)
      val value = fromCString((!_headerPtr).value, StandardCharsets.UTF_8)

      _map.containsKey(name) match
        case true => _map.get(name).add(value)
        case false =>
          val xs = new ArrayList[String]()
          xs.add(value)
          _map.put(name, xs)

      _prevHeaderPtr = _headerPtr
    }

    HttpHeaders.of(_map, (_, _) => true)
  }

object CurlGetInfoHelper:

  def apply(ptr: Ptr[Curl]): CurlGetInfoHelper =
    new CurlGetInfoHelper(ptr)

end CurlGetInfoHelper
