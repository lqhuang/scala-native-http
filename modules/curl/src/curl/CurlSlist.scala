package snhttp.experimental.curl
package curl

import scala.scalanative.unsafe.{Ptr, CString}
import scala.util.Using.Releasable

import _root_.snhttp.experimental.curl.libcurl.CurlSlist as _CurlSlist

class CurlSlist private (val ref: Ptr[_CurlSlist]) extends AnyVal:

  // private var _curr: Ptr[_CurlSlist] = null
  // private var _last: Ptr[_CurlSlist] = null

  // def append(data: CString): Unit =
  //   _curr = libcurl.slistAppend(_curr, data)

  def freeAll(): Unit =
    libcurl.slistFreeAll(ref)

  // def ptr: Ptr[_CurlSlist] = _curr

object CurlSlist:

  given Releasable[CurlSlist] with
    def release(slist: CurlSlist): Unit =
      slist.freeAll()

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
