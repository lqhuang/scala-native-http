package snhttp.experimental.openssl.bio_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object enumerations:
  private[enumerations] trait _BindgenEnumCUnsignedInt[T](using eq: T =:= CUnsignedInt):
    given Tag[T] = Tag.UInt.asInstanceOf[Tag[T]]
    extension (inline t: T)
      inline def value: CUnsignedInt = eq.apply(t)
      inline def int: CInt = eq.apply(t).toInt
      inline def uint: CUnsignedInt = eq.apply(t)

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_hostserv_priorities = CUnsignedInt
  object BIO_hostserv_priorities extends _BindgenEnumCUnsignedInt[BIO_hostserv_priorities]:
    given _tag: Tag[BIO_hostserv_priorities] = Tag.UInt
    inline def define(inline a: Long): BIO_hostserv_priorities = a.toUInt
    val BIO_PARSE_PRIO_HOST = define(0)
    val BIO_PARSE_PRIO_SERV = define(1)
    def getName(value: BIO_hostserv_priorities): Option[String] =
      value match
        case `BIO_PARSE_PRIO_HOST` => Some("BIO_PARSE_PRIO_HOST")
        case `BIO_PARSE_PRIO_SERV` => Some("BIO_PARSE_PRIO_SERV")
        case _                     => _root_.scala.None
    extension (a: BIO_hostserv_priorities)
      inline def &(b: BIO_hostserv_priorities): BIO_hostserv_priorities = a & b
      inline def |(b: BIO_hostserv_priorities): BIO_hostserv_priorities = a | b
      inline def is(b: BIO_hostserv_priorities): Boolean = (a & b) == b

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_lookup_type = CUnsignedInt
  object BIO_lookup_type extends _BindgenEnumCUnsignedInt[BIO_lookup_type]:
    given _tag: Tag[BIO_lookup_type] = Tag.UInt
    inline def define(inline a: Long): BIO_lookup_type = a.toUInt
    val BIO_LOOKUP_CLIENT = define(0)
    val BIO_LOOKUP_SERVER = define(1)
    def getName(value: BIO_lookup_type): Option[String] =
      value match
        case `BIO_LOOKUP_CLIENT` => Some("BIO_LOOKUP_CLIENT")
        case `BIO_LOOKUP_SERVER` => Some("BIO_LOOKUP_SERVER")
        case _                   => _root_.scala.None
    extension (a: BIO_lookup_type)
      inline def &(b: BIO_lookup_type): BIO_lookup_type = a & b
      inline def |(b: BIO_lookup_type): BIO_lookup_type = a | b
      inline def is(b: BIO_lookup_type): Boolean = (a & b) == b

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_sock_info_type = CUnsignedInt
  object BIO_sock_info_type extends _BindgenEnumCUnsignedInt[BIO_sock_info_type]:
    given _tag: Tag[BIO_sock_info_type] = Tag.UInt
    inline def define(inline a: Long): BIO_sock_info_type = a.toUInt
    val BIO_SOCK_INFO_ADDRESS = define(0)
    def getName(value: BIO_sock_info_type): Option[String] =
      value match
        case `BIO_SOCK_INFO_ADDRESS` => Some("BIO_SOCK_INFO_ADDRESS")
        case _                       => _root_.scala.None
    extension (a: BIO_sock_info_type)
      inline def &(b: BIO_sock_info_type): BIO_sock_info_type = a & b
      inline def |(b: BIO_sock_info_type): BIO_sock_info_type = a | b
      inline def is(b: BIO_sock_info_type): Boolean = (a & b) == b
