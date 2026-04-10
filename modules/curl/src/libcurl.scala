package snhttp.experimental.curl
package libcurl

import scala.scalanative.meta.LinktimeInfo.isWindows
import scala.scalanative.unsafe.{extern, link}

import _root_.snhttp.experimental.curl._libcurl.Functions

export _root_.snhttp.experimental.curl._libcurl.Curl.*
export _root_.snhttp.experimental.curl._libcurl.Easy.*
export _root_.snhttp.experimental.curl._libcurl.Header.*
export _root_.snhttp.experimental.curl._libcurl.Multi.*
export _root_.snhttp.experimental.curl._libcurl.Options.*
export _root_.snhttp.experimental.curl._libcurl.System.*
export _root_.snhttp.experimental.curl._libcurl.Websockets.*

@link("libcurl")
@link("Crypt32")
@link("Secur32") // required after curl 8.15.0
@link("Iphlpapi") // required after curl 8.15.0
@link("zlib")
@link("libcrypto")
@link("libssl")
@extern
private object CurlFunctionsWindows extends Functions

@link("curl")
@extern
private object CurlFunctionsUnix extends Functions

private val Funcs = if isWindows then CurlFunctionsWindows else CurlFunctionsUnix

export Funcs.*
