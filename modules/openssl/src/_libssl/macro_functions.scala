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

  // def sk_SSL_CIPHER_num(sk: Ptr[stack_st_SSL_CIPHER]): CInt =
  //   extern

  // def sk_SSL_CIPHER_value(sk: Ptr[stack_st_SSL_CIPHER], idx: CInt): Ptr[SSL_CIPHER] =
  //   extern

  def snhttp_ossl_sk_SSL_CIPHER_num(sk: Ptr[stack_st_SSL_CIPHER]): CInt =
    extern

  def snhttp_ossl_sk_SSL_CIPHER_value(sk: Ptr[stack_st_SSL_CIPHER], idx: CInt): Ptr[SSL_CIPHER] =
    extern

end macro_functions
