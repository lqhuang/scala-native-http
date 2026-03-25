package snhttp.experimental._libcurl

import scala.scalanative.unsafe.{Size, CInt, CLong, CSize}
import scala.scalanative.unsigned.UInt

private[_libcurl] object internal:
  trait _BindgenEnumUInt[T](using eq: T =:= UInt):
    extension (inline t: T)
      inline def value: UInt = eq.apply(t)
      inline def long: Long = value.toLong

  trait _BindgenEnumCInt[T](using eq: T =:= CInt):
    extension (inline t: T)
      inline def value: CInt = eq.apply(t)
      inline def long: Long = value.toLong

  trait _BindgenEnumCLong[T](using eq: T =:= CLong):
    extension (inline t: T)
      inline def value: CLong = eq.apply(t)
      inline def int: Int = value.toInt

  trait _BindgenEnumCSize[T](using eq: T =:= CSize):
    extension (inline t: T)
      inline def value: CSize = eq.apply(t)
      inline def int: Int = value.toInt
      inline def long: Long = value.toLong

  trait _BindgenEnumLong[T](using eq: T =:= Long):
    extension (inline t: T) inline def value: Long = eq.apply(t)

  trait _BindgenEnumSize[T](using eq: T =:= Size):
    extension (inline t: T)
      inline def value: Size = eq.apply(t)
      inline def long: Long = value.toLong
