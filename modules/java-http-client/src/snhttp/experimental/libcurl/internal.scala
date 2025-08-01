package snhttp.experimental.libcurl

import scala.scalanative.unsafe.{Tag, CSize}
import scala.scalanative.posix.sys.socket

// type SockAddrFamily = AF_INET | AF_INET6 | AF_UNIX | AF_UNSPEC

object internal:
  opaque type SockAddrFamily = Int
  object SockAddrFamily:
    given Tag[SockAddrFamily] = Tag.Int

    inline def define(inline v: Int): SockAddrFamily = v

    val AF_INET = define(socket.AF_INET)
    val AF_INET6 = define(socket.AF_INET6)
    val AF_UNIX = define(socket.AF_UNIX)
    val AF_UNSPEC = define(socket.AF_UNSPEC)
