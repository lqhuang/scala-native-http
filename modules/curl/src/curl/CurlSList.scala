package snhttp.experimental.curl
package curl

import scala.scalanative.unsafe.{Ptr, CString}
import scala.util.Using.Releasable

import _root_.snhttp.experimental.curl.libcurl.CurlSList as _CurlSList

class CurlSList private (val ref: Ptr[_CurlSList]) extends AnyVal:

  // private var _curr: Ptr[_CurlSList] = null
  // private var _last: Ptr[_CurlSList] = null

  // def append(data: CString): Unit =
  //   _curr = libcurl.slistAppend(_curr, data)

  def freeAll(): Unit =
    libcurl.slistFreeAll(ref)

  // def ptr: Ptr[_CurlSList] = _curr

object CurlSList:

  given Releasable[CurlSList] with
    def release(slist: CurlSList): Unit =
      slist.freeAll()

  def apply(elem: CString*): CurlSList = {
    var ptr: Ptr[_CurlSList] = null

    for e <- elem do
      libcurl.slistAppend(ptr, e) match
        case null   => throw new OutOfMemoryError("Failed to allocate CurlSList")
        case newPtr => ptr = newPtr

    val slist = new CurlSList(ptr)
    slist
  }

end CurlSList
