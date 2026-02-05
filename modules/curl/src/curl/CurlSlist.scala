package snhttp.experimental.curl

import scala.scalanative.unsafe.{Ptr, CString}
import scala.util.Using.Releasable

import _root_.snhttp.experimental.libcurl.CurlSlist as _CurlSlist
import _root_.snhttp.experimental.libcurl

class CurlSlist private (val ptr: Ptr[_CurlSlist]) extends Releasable[CurlSlist]:

  // private var _curr: Ptr[_CurlSlist] = null
  // private var _last: Ptr[_CurlSlist] = null

  // def append(data: CString): Unit =
  //   _curr = libcurl.slistAppend(_curr, data)

  def freeAll(): Unit =
    libcurl.slistFreeAll(ptr)

  // def ptr: Ptr[_CurlSlist] = _curr

  def release(resource: CurlSlist): Unit =
    resource.freeAll()

object CurlSlist:

  def apply(elem: CString*): CurlSlist = {
    var ptr: Ptr[_CurlSlist] = null
    for e <- elem do
      libcurl.slistAppend(ptr, e) match
        case null   => throw new OutOfMemoryError("Failed to allocate CurlSlist")
        case newPtr => ptr = newPtr

    val slist = new CurlSlist(ptr)
    slist
  }

end CurlSlist
