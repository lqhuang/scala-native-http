package snhttp.experimental._libcurl

import scala.scalanative.unsafe.name
import scala.scalanative.posix.sys.socket.socklen_t
import scala.scalanative.unsafe.Size

object system:

  @name("curl_off_t")
  type CurlOff = Long
