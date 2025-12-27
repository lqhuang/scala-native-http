package snhttp.experimental.libcurl

import _root_.scala.scalanative.unsafe.{Tag, Size, CInt, CLong, UnsafeRichLong}
import _root_.scala.scalanative.unsafe.name
import _root_.scala.scalanative.unsigned.UInt
import _root_.scala.scalanative.posix.sys.socket.{socklen_t, sockaddr}
import _root_.scala.scalanative.posix.time.time_t

private[libcurl] object internal:
  trait _BindgenEnumUInt[T](using eq: T =:= UInt):
    extension (inline t: T)
      inline def value: UInt = eq.apply(t)
      inline def long: Long = eq.apply(t).toLong
      inline def uint: UInt = eq.apply(t)

  trait _BindgenEnumInt[T](using eq: T =:= Int):
    extension (inline t: T)
      inline def value: Int = eq.apply(t)
      inline def int: Int = eq.apply(t).toInt
      inline def long: Long = eq.apply(t).toLong

  trait _BindgenEnumCLong[T](using eq: T =:= CLong):
    extension (inline t: T)
      inline def value: CLong = eq.apply(t)
      inline def int: Int = eq.apply(t).toInt

  trait _BindgenEnumLong[T](using eq: T =:= Long):
    extension (inline t: T)
      inline def value: Long = eq.apply(t)
      inline def long: Long = eq.apply(t).toLong

  trait _BindgenEnumSize[T](using eq: T =:= Size):
    extension (inline t: T)
      inline def value: Size = eq.apply(t)
      inline def long: Long = eq.apply(t).toLong
