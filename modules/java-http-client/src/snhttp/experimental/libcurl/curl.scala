package snhttp.experimental.libcurl
import scala.scalanative.unsafe.{alloc, name, link, extern, define}

@link("curl/curl")
@extern
@define("CURL_NO_OLDIES") // deprecate all outdated
object curl:

  import core.*
  import options.*
  import easy.*
  import multi.*
  import websockets.*
