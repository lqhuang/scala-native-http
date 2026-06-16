/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/d21e75a6ae0cda978e68b26579e5665a0a92ca0d/include/curl/easy.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */
package snhttp.experimental.curl
package _libcurl

import scala.scalanative.unsafe.{Tag, CStruct3, CVoidPtr}
import scala.scalanative.libc.stddef.size_t

import _root_.snhttp.experimental.curl._internal._BindgenEnumCInt

private[curl] object Easy:

  opaque type CurlBlobFlag = Int
  object CurlBlobFlag extends _BindgenEnumCInt[CurlBlobFlag]:

    given Tag[CurlBlobFlag] = Tag.Int
    private inline def define(inline a: Int): CurlBlobFlag = a
    val Copy: CurlBlobFlag = define(0)
    val Nocopy: CurlBlobFlag = define(1)

  end CurlBlobFlag

  type CurlBlob = CStruct3[
    /** data */
    CVoidPtr,
    /** length */
    size_t,
    /** flags */
    CurlBlobFlag,
  ]
