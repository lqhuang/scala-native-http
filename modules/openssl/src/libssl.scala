/// Header bindings for OpenSSL <openssl/ssl.h>
///
/// References:
///
/// 1. https://github.com/openssl/openssl/blob/master/include/openssl/ssl.h.in
package snhttp.experimental.openssl
package libssl

import scala.scalanative.meta.LinktimeInfo.isWindows
import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.bio.Functions as BIOFunctions
import _root_.snhttp.experimental.openssl._openssl.ssl.Functions as SSLFunctions

export _root_.snhttp.experimental.openssl._openssl.bio.Types.*
export _root_.snhttp.experimental.openssl._openssl.ssl.Types.*

private[libssl] object Functions:

  @extern
  trait Functions extends BIOFunctions with SSLFunctions

  @link("libssl")
  @link("libcrypto")
  @extern
  private object FunctionsWindows extends Functions

  @link("ssl")
  @link("crypto")
  @extern
  private object FunctionsUnix extends Functions

  val funcs = if isWindows then FunctionsWindows else FunctionsUnix

end Functions

export Functions.funcs.*
