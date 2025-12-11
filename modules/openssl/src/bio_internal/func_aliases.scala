package snhttp.experimental.openssl.bio_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object func_aliases:

  import _root_.snhttp.experimental.openssl.bio_internal.functions.*
  import _root_.snhttp.experimental.openssl.bio_internal.enumerations.*
  import _root_.snhttp.experimental.openssl.bio_internal.structs.*

  def BIO_set_conn_mode(
      ptr: Ptr[BIO],
      larg: BIO_SOCK,
  ): CLongInt =
    BIO_ctrl(ptr, BIO_CTRL.SET_CONNECT_MODE, larg.value, null)
