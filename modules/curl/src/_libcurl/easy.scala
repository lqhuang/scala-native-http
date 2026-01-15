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
package snhttp.experimental._libcurl

import scala.scalanative.unsafe.alloc
import scala.scalanative.unsafe.{Tag, CStruct2, CStruct3, Ptr}
import scala.scalanative.unsigned.{UInt, UnsignedRichLong}
import scala.scalanative.libc.stddef.size_t

import snhttp.experimental._libcurl.internal._BindgenEnumUInt

object easy:

  opaque type CurlBlobFlag = UInt
  given Tag[CurlBlobFlag] = Tag.UInt

  object CurlBlobFlag extends _BindgenEnumUInt[CurlBlobFlag]:
    inline def define(inline a: Long): CurlBlobFlag = a.toUInt
    val Copy: CurlBlobFlag = define(0)
    val Nocopy: CurlBlobFlag = define(1)

  type CurlBlob = CStruct3[
    /** data */
    Unit, // CVoidPtr,
    /** length */
    size_t,
    /** flags */
    CurlBlobFlag,
  ]

  /** Used by CURLOPT_WRITEDATA and more */
  type CurlData = CStruct2[
    /** memory */
    Ptr[Byte],
    /** size */
    size_t,
  ]
