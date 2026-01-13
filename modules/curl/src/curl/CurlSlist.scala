package snhttp.experimental.curl

import scala.scalanative.unsafe.{
  Ptr,
  Size,
  CString,
  toCString,
  stackalloc,
  UnsafeRichInt,
  CVoidPtr,
  Zone,
  CLong,
}
import scala.scalanative.unsigned.UInt
import scala.scalanative.posix.sys.select.fd_set

import _root_.snhttp.experimental.libcurl.{
  Curl as _Curl,
  CurlMulti as _CurlMulti,
  CurlWaitFd as _CurlWaitFd,
  CurlMultiOption,
  CurlMultiCode,
  CurlMsg,
  CurlSocket,
  CurlSlist as _CurlSlist,
}
import _root_.snhttp.experimental.libcurl

class CurlSlist private ():

  private var _curr: Ptr[_CurlSlist] = null
  private var _last: Ptr[_CurlSlist] = null

  def append(data: String): Unit =
    _last = _curr
    _curr = libcurl.slistAppend(_curr, toCString(data)(using ???))

  def freeAll(): Unit =
    libcurl.slistFreeAll(_curr)

  def ptr: Ptr[_CurlSlist] = _curr

object CurlSlist:

  def apply(elem: String*): CurlSlist =
    val slist = new CurlSlist()
    for e <- elem do slist.append(e)
    slist

end CurlSlist
