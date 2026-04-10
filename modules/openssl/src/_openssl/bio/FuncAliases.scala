package snhttp.experimental.openssl
package _openssl.bio

import scala.scalanative.unsafe.{Ptr, CLongInt}

import _root_.snhttp.experimental.openssl._openssl.bio.Enumerations.{BIO_CTRL, BIO_SOCK}
import _root_.snhttp.experimental.openssl._openssl.bio.Structs.BIO
import _root_.snhttp.experimental.openssl._openssl.bio.Functions

private[openssl] trait FuncAliases(funcs: Functions):

  def BIO_set_conn_mode(
      ptr: Ptr[BIO],
      larg: BIO_SOCK,
  ): CLongInt =
    funcs.BIO_ctrl(ptr, BIO_CTRL.SET_CONNECT_MODE, larg.value, null)
