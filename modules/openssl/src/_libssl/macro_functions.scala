package snhttp.experimental.openssl._libssl

import scala.scalanative.meta.LinktimeInfo.isWindows
import scala.scalanative.unsafe.{link, extern, Ptr, CInt}
import scala.scalanative.unsafe.CString

object macro_functions:

  @link("libcrypto")
  @link("libssl")
  @extern
  private object SSLMacroFunctionsWindows extends macro_functions

  @link("ssl")
  @link("crypto")
  @extern
  private object SSLMacroFunctionsUnix extends macro_functions

  private val _macro_funcs =
    if isWindows then SSLMacroFunctionsWindows else SSLMacroFunctionsUnix
  export _macro_funcs.*

@extern
trait macro_functions:

  import _root_.snhttp.experimental.openssl._libssl.structs.{SSL_CIPHER, stack_st_SSL_CIPHER}

  /**
   * Error msg: undefined reference to `sk_SSL_CIPHER_num'
   *
   * Not very sure why `ld` cannot find the symbol `sk_SSL_CIPHER_num` and `sk_SSL_CIPHER_value` *
   * when linking
   *
   * We have to forward the `sk_SSL_CIPHER_num` and `sk_SSL_CIPHER_value` via our own C functions
   * `snhttp_ossl_sk_SSL_CIPHER_num` and `snhttp_ossl_sk_SSL_CIPHER_value` respectively,
   *
   * Check [modules/openssl/resources/scala-native/stack_st_ssl_cipher_ops.c] for the implementation
   * of these two C functions.
   */

  // def sk_SSL_CIPHER_num(sk: Ptr[stack_st_SSL_CIPHER]): CInt =
  //   extern

  // def sk_SSL_CIPHER_value(sk: Ptr[stack_st_SSL_CIPHER], idx: CInt): Ptr[SSL_CIPHER] =
  //   extern

  def snhttp_ossl_sk_SSL_CIPHER_num(sk: Ptr[stack_st_SSL_CIPHER]): CInt =
    extern

  def snhttp_ossl_sk_SSL_CIPHER_value(sk: Ptr[stack_st_SSL_CIPHER], idx: CInt): Ptr[SSL_CIPHER] =
    extern

  def snhttp_ossl_sk_SSL_CIPHER_free(sk: Ptr[stack_st_SSL_CIPHER]): Unit =
    extern

end macro_functions
