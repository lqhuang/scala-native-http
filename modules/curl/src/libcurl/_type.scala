package snhttp.experimental.libcurl

import scala.scalanative.unsafe.{Tag, Size, CInt, CLong, UnsafeRichLong}
import scala.scalanative.unsafe.name
import scala.scalanative.unsigned.UInt
import scala.scalanative.posix.sys.socket.{socklen_t, sockaddr}
import scala.scalanative.posix.time.time_t

private[libcurl] object _type:
  trait _BindgenEnumUInt[T](using eq: T =:= UInt):
    extension (inline t: T)
      inline def value: UInt = eq.apply(t)
      inline def long: Long = eq.apply(t).toLong
      inline def uint: UInt = eq.apply(t)

    extension (inline a: T)
      inline def &(b: T): T = a & b
      inline def |(b: T): T = a | b
      inline def is(b: T): Boolean = (a & b) == b

  trait _BindgenEnumInt[T](using eq: T =:= Int):
    extension (inline t: T)
      inline def value: Int = eq.apply(t)
      inline def int: Int = eq.apply(t).toInt
      inline def long: Long = eq.apply(t).toLong

    extension (inline a: T)
      inline def &(b: T): T = a & b
      inline def |(b: T): T = a | b
      inline def is(b: T): Boolean = (a & b) == b

  trait _BindgenEnumCLong[T](using eq: T =:= CLong):
    extension (inline t: T)
      inline def value: CLong = eq.apply(t)
      inline def int: Int = eq.apply(t).toInt

    extension (inline a: T)
      inline def &(b: T): T = a & b
      inline def |(b: T): T = a | b
      inline def is(b: T): Boolean = (a & b) == b

  trait _BindgenEnumLong[T](using eq: T =:= Long):
    extension (inline t: T)
      inline def value: Long = eq.apply(t)
      inline def long: Long = eq.apply(t).toLong

    extension (inline a: T)
      inline def &(b: T): T = a & b
      inline def |(b: T): T = a | b
      inline def is(b: T): Boolean = (a & b) == b

  trait _BindgenEnumSize[T](using eq: T =:= Size):
    extension (inline t: T)
      inline def value: Size = eq.apply(t)
      inline def long: Long = eq.apply(t).toLong

    extension (inline a: T)
      inline def &(b: T): T = a & b
      inline def |(b: T): T = a | b
      inline def is(b: T): Boolean = (a & b) == b
