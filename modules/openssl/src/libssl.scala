package snhttp.experimental.openssl.ssl

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
import scalanative.posix.sys.select.fd_set

/// Header bindings for OpenSSL <openssl/ssl.h>
///
/// References:
///
/// 1. https://github.com/openssl/openssl/blob/master/include/openssl/ssl.h.in
object libssl:

  import _root_.snhttp.experimental.openssl.libssl.internal.*

  import aliases.*
  import constants.*
  import enumerations.*
  import functions.*
  import structs.*
  import unions.*
