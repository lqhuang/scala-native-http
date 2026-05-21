package snhttp.experimental.openssl
package _openssl.conf

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.types.Types.{OsslLibCtxPtr, ConfPtr}

@extern
private[openssl] trait Functions:

  def CONF_get1_default_config_file(): CString = extern

  def CONF_modules_load_file_ex(
      libctx: OsslLibCtxPtr,
      filename: CString,
      appname: CString,
      flags: CUnsignedLong,
  ): CInt = extern

  def CONF_modules_load_file(filename: CString, appname: CString, flags: CUnsignedLong): CInt =
    extern

  def CONF_modules_load(cnf: ConfPtr, appname: CString, flags: CUnsignedLong): CInt = extern
