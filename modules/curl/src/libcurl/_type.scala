package snhttp.experimental.libcurl

import scala.scalanative.unsafe.{Tag, Size, CInt, CLongLong, CLong}
import scala.scalanative.unsigned.UInt

private object _type:
  private[libcurl] trait _BindgenEnumUInt[T](using eq: T =:= UInt):
    given Tag[T] = Tag.UInt.asInstanceOf[Tag[T]]

    extension (inline t: T)
      inline def value: UInt = eq.apply(t)
      inline def long: Long = eq.apply(t).toLong
      inline def uint: UInt = eq.apply(t)

  private[libcurl] trait _BindgenEnumInt[T](using eq: T =:= Int):
    given Tag[T] = Tag.Int.asInstanceOf[Tag[T]]
    extension (inline t: T)
      inline def value: Int = eq.apply(t)
      inline def int: Int = eq.apply(t).toInt
      inline def long: Long = eq.apply(t).toLong

  private[libcurl] trait _BindgenEnumCLong[T](using eq: T =:= CLong):
    given Tag[T] = Tag.Size.asInstanceOf[Tag[T]]
    extension (inline t: T) inline def value: CLong = eq.apply(t)
    extension (inline t: T) inline def int: Int = eq.apply(t).toInt

  private[libcurl] trait _BindgenEnumLong[T](using eq: T =:= Long):
    given Tag[T] = Tag.Long.asInstanceOf[Tag[T]]
    extension (inline t: T)
      inline def value: Long = eq.apply(t)
      inline def long: Long = eq.apply(t).toLong

  private[libcurl] trait _BindgenEnumSize[T](using eq: T =:= Size):
    given Tag[T] = Tag.Int.asInstanceOf[Tag[T]]
    extension (inline t: T) inline def value: Size = eq.apply(t)
    extension (inline t: T) inline def long: Long = eq.apply(t).toLong
