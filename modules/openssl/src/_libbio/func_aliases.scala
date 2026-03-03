package snhttp.experimental.openssl._libbio

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object func_aliases:

  import _root_.snhttp.experimental.openssl._libbio.functions.*
  import _root_.snhttp.experimental.openssl._libbio.enumerations.*
  import _root_.snhttp.experimental.openssl._libbio.structs.*

  def BIO_set_conn_mode(
      ptr: Ptr[BIO],
      larg: BIO_SOCK,
  ): CLongInt =
    BIO_ctrl(ptr, BIO_CTRL.SET_CONNECT_MODE, larg.value, null)
