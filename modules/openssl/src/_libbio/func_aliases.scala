package snhttp.experimental.openssl._libbio

import _root_.scala.scalanative.unsafe.*

object func_aliases:

  import _root_.snhttp.experimental.openssl._libbio.functions.BIO_ctrl
  import _root_.snhttp.experimental.openssl._libbio.enumerations.{BIO_CTRL, BIO_SOCK}
  import _root_.snhttp.experimental.openssl._libbio.structs.BIO

  def BIO_set_conn_mode(
      ptr: Ptr[BIO],
      larg: BIO_SOCK,
  ): CLongInt =
    BIO_ctrl(ptr, BIO_CTRL.SET_CONNECT_MODE, larg.value, null)
