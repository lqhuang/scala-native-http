package snhttp.experimental.libcurl

import scala.scalanative.unsafe.Tag
import scala.scalanative.posix.sys.socket

// type SockAddrFamily = AF_INET | AF_INET6 | AF_UNIX | AF_UNSPEC

opaque type SockAddrFamily = Int
object SockAddrFamily:
  inline def define(inline v: Int): SockAddrFamily = v
  given Tag[SockAddrFamily] = Tag.Int
  val AF_INET = define(socket.AF_INET)
  val AF_INET6 = define(socket.AF_INET6)
  val AF_UNIX = define(socket.AF_UNIX)
  val AF_UNSPEC = define(socket.AF_UNSPEC)
