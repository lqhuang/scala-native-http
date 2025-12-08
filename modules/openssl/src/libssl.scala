package snhttp.experimental.openssl

import scalanative.unsafe.{extern, link, name, alloc, define}
import scalanative.unsafe.{
  Zone,
  Tag,
  Ptr,
  CVoidPtr,
  CString,
  CLongInt,
  CStruct0,
  CStruct3,
  CFuncPtr,
  CFuncPtr3,
  CFuncPtr5,
  CSize,
  CArray,
  CChar,
  Nat,
}
import scalanative.unsigned.*

/// Header bindings for OpenSSL <openssl/ssl.h>
///
/// References:
///
/// 1. https://github.com/openssl/openssl/blob/master/include/openssl/ssl.h.in
object libssl:

  import _root_.snhttp.experimental.openssl.libssl_internal.*

  export aliases.*
  export constants.*
  export enumerations.*
  export functions.*
  export structs.*
  export unions.*
