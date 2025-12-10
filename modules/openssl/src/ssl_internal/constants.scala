package snhttp.experimental.openssl.ssl_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object constants:
  val SSL_CT_VALIDATION_PERMISSIVE: CUnsignedInt = 0.toUInt
  val SSL_CT_VALIDATION_STRICT: CUnsignedInt = 1.toUInt
