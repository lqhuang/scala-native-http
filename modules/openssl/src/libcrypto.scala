/// Header bindings for OpenSSL libcrypto
package snhttp.experimental.openssl
package libcrypto

import scala.scalanative.meta.LinktimeInfo.isWindows
import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.asn1.Functions as ASN1Functions
import _root_.snhttp.experimental.openssl._openssl.bio.{
  Functions as BIOFunctions,
  FuncAliases as BIOFuncAliases,
}

import _root_.snhttp.experimental.openssl._openssl.pkcs12.Functions as PKCS12Functions
import _root_.snhttp.experimental.openssl._openssl.x509.Functions as X509Functions
import _root_.snhttp.experimental.openssl._openssl.x509v3.Functions as X509v3Functions

export _root_.snhttp.experimental.openssl._openssl.types.Types.*
export _root_.snhttp.experimental.openssl._openssl.asn1.Types.*
export _root_.snhttp.experimental.openssl._openssl.bio.Types.*
export _root_.snhttp.experimental.openssl._openssl.pkcs12.Types.*
export _root_.snhttp.experimental.openssl._openssl.x509.Types.*
export _root_.snhttp.experimental.openssl._openssl.x509v3.Types.*

@extern
private trait Functions
    extends ASN1Functions
    with BIOFunctions
    with PKCS12Functions
    with X509Functions
    with X509v3Functions

@link("libcrypto")
@extern
private object FunctionsWindows extends Functions

@link("crypto")
@extern
private object FunctionsUnix extends Functions

private final val Funcs = if isWindows then FunctionsWindows else FunctionsUnix

private object FuncAliases extends BIOFuncAliases(Funcs)

export Funcs.*
export FuncAliases.*
