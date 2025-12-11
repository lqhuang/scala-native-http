// scalafmt: { maxColumn = 200, align.preset = most }
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
      inline def int: CInt           = eq.apply(t).toInt
      inline def uint: CUnsignedInt  = eq.apply(t)

  private[enumerations] trait _BindgenEnumCInt[T](using eq: T =:= CInt):
    given Tag[T] = Tag.Int.asInstanceOf[Tag[T]]
    extension (inline t: T)
      inline def value: CInt = eq.apply(t)
      inline def int: CInt   = eq.apply(t).toInt

  private[enumerations] trait _BindgenEnumSize[T](using eq: T =:= Size):
    given Tag[T] = Tag.Int.asInstanceOf[Tag[T]]
    extension (inline t: T)
      inline def value: Size = eq.apply(t)
      inline def int: CInt   = eq.apply(t).toInt

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
        case BIO_PARSE_PRIO_HOST => Some("BIO_PARSE_PRIO_HOST")
        case BIO_PARSE_PRIO_SERV => Some("BIO_PARSE_PRIO_SERV")
        case _                   => _root_.scala.None

    extension (a: BIO_hostserv_priorities)
      inline def &(b: BIO_hostserv_priorities): BIO_hostserv_priorities = a & b
      inline def |(b: BIO_hostserv_priorities): BIO_hostserv_priorities = a | b
      inline def is(b: BIO_hostserv_priorities): Boolean                = (a & b) == b

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
        case BIO_LOOKUP_CLIENT => Some("BIO_LOOKUP_CLIENT")
        case BIO_LOOKUP_SERVER => Some("BIO_LOOKUP_SERVER")
        case _                 => _root_.scala.None

    extension (a: BIO_lookup_type)
      inline def &(b: BIO_lookup_type): BIO_lookup_type = a & b
      inline def |(b: BIO_lookup_type): BIO_lookup_type = a | b
      inline def is(b: BIO_lookup_type): Boolean        = (a & b) == b

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
        case BIO_SOCK_INFO_ADDRESS => Some("BIO_SOCK_INFO_ADDRESS")
        case _                     => _root_.scala.None

    extension (a: BIO_sock_info_type)
      inline def &(b: BIO_sock_info_type): BIO_sock_info_type = a & b
      inline def |(b: BIO_sock_info_type): BIO_sock_info_type = a | b
      inline def is(b: BIO_sock_info_type): Boolean           = (a & b) == b

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_CTRL = CInt
  object BIO_CTRL extends _BindgenEnumCInt[BIO_CTRL]:
    given _tag: Tag[BIO_CTRL] = Tag.Int

    inline def define(inline a: Int): BIO_CTRL = a.toInt

    val SET_CONNECT: BIO_CTRL                 = define(100)
    val DO_STATE_MACHINE: BIO_CTRL            = define(101)
    val SET_NBIO: BIO_CTRL                    = define(102)
    val SET_FD: BIO_CTRL                      = define(104)
    val GET_FD: BIO_CTRL                      = define(105)
    val SET_FILE_PTR: BIO_CTRL                = define(106)
    val GET_FILE_PTR: BIO_CTRL                = define(107)
    val SET_FILENAME: BIO_CTRL                = define(108)
    val SET_SSL: BIO_CTRL                     = define(109)
    val GET_SSL: BIO_CTRL                     = define(110)
    val SET_MD: BIO_CTRL                      = define(111)
    val GET_MD: BIO_CTRL                      = define(112)
    val GET_CIPHER_STATUS: BIO_CTRL           = define(113)
    val SET_BUF_MEM: BIO_CTRL                 = define(114)
    val GET_BUF_MEM_PTR: BIO_CTRL             = define(115)
    val GET_BUFF_NUM_LINES: BIO_CTRL          = define(116)
    val SET_BUFF_SIZE: BIO_CTRL               = define(117)
    val SET_ACCEPT: BIO_CTRL                  = define(118)
    val SSL_MODE: BIO_CTRL                    = define(119)
    val GET_MD_CTX: BIO_CTRL                  = define(120)
    val SET_BUFF_READ_DATA: BIO_CTRL          = define(122)
    val GET_CONNECT: BIO_CTRL                 = define(123)
    val GET_ACCEPT: BIO_CTRL                  = define(124)
    val SET_SSL_RENEGOTIATE_BYTES: BIO_CTRL   = define(125)
    val GET_SSL_NUM_RENEGOTIATES: BIO_CTRL    = define(126)
    val SET_SSL_RENEGOTIATE_TIMEOUT: BIO_CTRL = define(127)
    val FILE_SEEK: BIO_CTRL                   = define(128)
    val GET_CIPHER_CTX: BIO_CTRL              = define(129)
    val SET_BUF_MEM_EOF_RETURN: BIO_CTRL      = define(130)
    val SET_BIND_MODE: BIO_CTRL               = define(131)
    val GET_BIND_MODE: BIO_CTRL               = define(132)
    val FILE_TELL: BIO_CTRL                   = define(133)
    val GET_SOCKS: BIO_CTRL                   = define(134)
    val SET_SOCKS: BIO_CTRL                   = define(135)
    val SET_WRITE_BUF_SIZE: BIO_CTRL          = define(136)
    val GET_WRITE_BUF_SIZE: BIO_CTRL          = define(137)
    val MAKE_BIO_PAIR: BIO_CTRL               = define(138)
    val DESTROY_BIO_PAIR: BIO_CTRL            = define(139)
    val GET_WRITE_GUARANTEE: BIO_CTRL         = define(140)
    val GET_READ_REQUEST: BIO_CTRL            = define(141)
    val SHUTDOWN_WR: BIO_CTRL                 = define(142)
    val NREAD0: BIO_CTRL                      = define(143)
    val NREAD: BIO_CTRL                       = define(144)
    val NWRITE0: BIO_CTRL                     = define(145)
    val NWRITE: BIO_CTRL                      = define(146)
    val RESET_READ_REQUEST: BIO_CTRL          = define(147)
    val SET_MD_CTX: BIO_CTRL                  = define(148)
    val SET_PREFIX: BIO_CTRL                  = define(149)
    val GET_PREFIX: BIO_CTRL                  = define(150)
    val SET_SUFFIX: BIO_CTRL                  = define(151)
    val GET_SUFFIX: BIO_CTRL                  = define(152)
    val SET_EX_ARG: BIO_CTRL                  = define(153)
    val GET_EX_ARG: BIO_CTRL                  = define(154)
    val SET_CONNECT_MODE: BIO_CTRL            = define(155)
    val SET_TFO: BIO_CTRL                     = define(156)
    val SET_SOCK_TYPE: BIO_CTRL               = define(157)
    val GET_SOCK_TYPE: BIO_CTRL               = define(158)
    val GET_DGRAM_BIO: BIO_CTRL               = define(159)

    def getName(value: BIO_CTRL): Option[String] =
      value match
        case SET_CONNECT                 => Some("BIO_C_SET_CONNECT")
        case DO_STATE_MACHINE            => Some("BIO_C_DO_STATE_MACHINE")
        case SET_NBIO                    => Some("BIO_C_SET_NBIO")
        case SET_FD                      => Some("BIO_C_SET_FD")
        case GET_FD                      => Some("BIO_C_GET_FD")
        case SET_FILE_PTR                => Some("BIO_C_SET_FILE_PTR")
        case GET_FILE_PTR                => Some("BIO_C_GET_FILE_PTR")
        case SET_FILENAME                => Some("BIO_C_SET_FILENAME")
        case SET_SSL                     => Some("BIO_C_SET_SSL")
        case GET_SSL                     => Some("BIO_C_GET_SSL")
        case SET_MD                      => Some("BIO_C_SET_MD")
        case GET_MD                      => Some("BIO_C_GET_MD")
        case GET_CIPHER_STATUS           => Some("BIO_C_GET_CIPHER_STATUS")
        case SET_BUF_MEM                 => Some("BIO_C_SET_BUF_MEM")
        case GET_BUF_MEM_PTR             => Some("BIO_C_GET_BUF_MEM_PTR")
        case GET_BUFF_NUM_LINES          => Some("BIO_C_GET_BUFF_NUM_LINES")
        case SET_BUFF_SIZE               => Some("BIO_C_SET_BUFF_SIZE")
        case SET_ACCEPT                  => Some("BIO_C_SET_ACCEPT")
        case SSL_MODE                    => Some("BIO_C_SSL_MODE")
        case GET_MD_CTX                  => Some("BIO_C_GET_MD_CTX")
        case SET_BUFF_READ_DATA          => Some("BIO_C_SET_BUFF_READ_DATA")
        case GET_CONNECT                 => Some("BIO_C_GET_CONNECT")
        case GET_ACCEPT                  => Some("BIO_C_GET_ACCEPT")
        case SET_SSL_RENEGOTIATE_BYTES   => Some("BIO_C_SET_SSL_RENEGOTIATE_BYTES")
        case GET_SSL_NUM_RENEGOTIATES    => Some("BIO_C_GET_SSL_NUM_RENEGOTIATES")
        case SET_SSL_RENEGOTIATE_TIMEOUT => Some("BIO_C_SET_SSL_RENEGOTIATE_TIMEOUT")
        case FILE_SEEK                   => Some("BIO_C_FILE_SEEK")
        case GET_CIPHER_CTX              => Some("BIO_C_GET_CIPHER_CTX")
        case SET_BUF_MEM_EOF_RETURN      => Some("BIO_C_SET_BUF_MEM_EOF_RETURN")
        case SET_BIND_MODE               => Some("BIO_C_SET_BIND_MODE")
        case GET_BIND_MODE               => Some("BIO_C_GET_BIND_MODE")
        case FILE_TELL                   => Some("BIO_C_FILE_TELL")
        case GET_SOCKS                   => Some("BIO_C_GET_SOCKS")
        case SET_SOCKS                   => Some("BIO_C_SET_SOCKS")
        case SET_WRITE_BUF_SIZE          => Some("BIO_C_SET_WRITE_BUF_SIZE")
        case GET_WRITE_BUF_SIZE          => Some("BIO_C_GET_WRITE_BUF_SIZE")
        case MAKE_BIO_PAIR               => Some("BIO_C_MAKE_BIO_PAIR")
        case DESTROY_BIO_PAIR            => Some("BIO_C_DESTROY_BIO_PAIR")
        case GET_WRITE_GUARANTEE         => Some("BIO_C_GET_WRITE_GUARANTEE")
        case GET_READ_REQUEST            => Some("BIO_C_GET_READ_REQUEST")
        case SHUTDOWN_WR                 => Some("BIO_C_SHUTDOWN_WR")
        case NREAD0                      => Some("BIO_C_NREAD0")
        case NREAD                       => Some("BIO_C_NREAD")
        case NWRITE0                     => Some("BIO_C_NWRITE0")
        case NWRITE                      => Some("BIO_C_NWRITE")
        case RESET_READ_REQUEST          => Some("BIO_C_RESET_READ_REQUEST")
        case SET_MD_CTX                  => Some("BIO_C_SET_MD_CTX")
        case SET_PREFIX                  => Some("BIO_C_SET_PREFIX")
        case GET_PREFIX                  => Some("BIO_C_GET_PREFIX")
        case SET_SUFFIX                  => Some("BIO_C_SET_SUFFIX")
        case GET_SUFFIX                  => Some("BIO_C_GET_SUFFIX")
        case SET_EX_ARG                  => Some("BIO_C_SET_EX_ARG")
        case GET_EX_ARG                  => Some("BIO_C_GET_EX_ARG")
        case SET_CONNECT_MODE            => Some("BIO_C_SET_CONNECT_MODE")
        case SET_TFO                     => Some("BIO_C_SET_TFO")
        case SET_SOCK_TYPE               => Some("BIO_C_SET_SOCK_TYPE")
        case GET_SOCK_TYPE               => Some("BIO_C_GET_SOCK_TYPE")
        case GET_DGRAM_BIO               => Some("BIO_C_GET_DGRAM_BIO")
        case _                           => _root_.scala.None

  opaque type BIO_SOCK = Size
  object BIO_SOCK extends _BindgenEnumSize[BIO_SOCK]:
    given _tag: Tag[BIO_SOCK] = Tag.Size

    inline def define(inline a: Long): BIO_SOCK = a.toSize

    val REUSEADDR: BIO_SOCK = define(0x01)
    val V6_ONLY: BIO_SOCK   = define(0x02)
    val KEEPALIVE: BIO_SOCK = define(0x04)
    val NONBLOCK: BIO_SOCK  = define(0x08)
    val NODELAY: BIO_SOCK   = define(0x10)
    val TFO: BIO_SOCK       = define(0x20)

    def getName(value: BIO_SOCK): Option[String] =
      value match
        case REUSEADDR => Some("BIO_SOCK_REUSEADDR")
        case V6_ONLY   => Some("BIO_SOCK_V6_ONLY")
        case KEEPALIVE => Some("BIO_SOCK_KEEPALIVE")
        case NONBLOCK  => Some("BIO_SOCK_NONBLOCK")
        case NODELAY   => Some("BIO_SOCK_NODELAY")
        case TFO       => Some("BIO_SOCK_TFO")
        case _         => _root_.scala.None
