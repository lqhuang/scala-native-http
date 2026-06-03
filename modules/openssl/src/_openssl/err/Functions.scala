package snhttp.experimental.openssl
package _openssl.err

import scala.scalanative.unsafe.*
import scala.scalanative.posix.stdio.FILE

@extern
private[openssl] trait Functions:

  def ERR_get_error(): CUnsignedLong =
    extern

  def ERR_error_string(errCode: CUnsignedLong, buf: Ptr[Byte]): Ptr[Byte] =
    extern

  def ERR_print_errors_fp(fp: Ptr[FILE]): Unit =
    extern
