package snhttp.experimental.openssl
package _internal

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

private[openssl] trait _BindgenEnumCUnsignedInt[T](using eq: T =:= CUnsignedInt):
  given Tag[T] = Tag.UInt.asInstanceOf[Tag[T]]
  extension (inline t: T)
    inline def value: CUnsignedInt = eq.apply(t)
    inline def int: CInt = value.toInt
    inline def uint: CUnsignedInt = value

private[openssl] trait _BindgenEnumCInt[T](using eq: T =:= CInt):
  given Tag[T] = Tag.Int.asInstanceOf[Tag[T]]
  extension (inline t: T)
    inline def value: CInt = eq.apply(t)
    inline def int: CInt = eq.apply(t).toInt

private[openssl] trait _BindgenEnumSize[T](using eq: T =:= Size):
  given Tag[T] = Tag.Size.asInstanceOf[Tag[T]]
  extension (inline t: T)
    inline def value: Size = eq.apply(t)
    inline def long: Long = eq.apply(t).toLong

private[openssl] trait _BindgenEnumULong[T](using eq: T =:= ULong):
  given Tag[T] = Tag.ULong.asInstanceOf[Tag[T]]
  extension (inline t: T) inline def value: ULong = eq.apply(t)
