package snhttp.experimental.openssl
package libssl

import scala.scalanative.meta.LinktimeInfo.isWindows
import scala.scalanative.unsafe.{extern, link}

import _root_.snhttp.experimental.openssl._openssl.bio.{
  Functions as BIOFunctions,
  FuncAliases as BIOFuncAliases,
}
import _root_.snhttp.experimental.openssl._openssl.ssl.{
  Functions as SSLFunctions,
  FuncAliases as SSLFuncAliases,
}

export _root_.snhttp.experimental.openssl._openssl.bio.Types.*
export _root_.snhttp.experimental.openssl._openssl.ssl.Types.*
export _root_.snhttp.experimental.openssl._openssl.ssl.Constants.*

@extern
private trait Functions extends BIOFunctions with SSLFunctions

@link("libssl")
@link("libcrypto")
@extern
private object FunctionsWindows extends Functions

@link("ssl")
@link("crypto")
@extern
private object FunctionsUnix extends Functions

private final val Funcs = if isWindows then FunctionsWindows else FunctionsUnix

private object FuncAliases extends BIOFuncAliases(Funcs) with SSLFuncAliases(Funcs)

export Funcs.*
export FuncAliases.*
