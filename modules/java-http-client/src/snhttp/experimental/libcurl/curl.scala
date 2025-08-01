package snhttp.experimental.libcurl
import scala.scalanative.unsafe.{alloc, name, link, extern, define}

@link("curl/curl")
@extern
@define("CURL_NO_OLDIES") // deprecate all outdated
object curl:
  import snhttp.experimental.libcurl

  import libcurl.core.*
  import libcurl.options.*
  import libcurl.easy.*
  import libcurl.multi.*
  import libcurl.websockets.*
