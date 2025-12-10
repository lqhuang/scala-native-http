// scalafmt: { maxColumn = 200, align.preset = most }
package snhttp.experimental.openssl.libssl_internal

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
    given Tag[T] = Tag.Size.asInstanceOf[Tag[T]]
    extension (inline t: T)
      inline def value: Size  = eq.apply(t)
      inline def clong: CLong = eq.apply(t).toCSSize

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type OSSL_HANDSHAKE_STATE = CUnsignedInt
  object OSSL_HANDSHAKE_STATE extends _BindgenEnumCUnsignedInt[OSSL_HANDSHAKE_STATE]:
    given _tag: Tag[OSSL_HANDSHAKE_STATE] = Tag.UInt

    inline def define(inline a: Long): OSSL_HANDSHAKE_STATE = a.toUInt

    val TLS_ST_BEFORE                   = define(0)
    val TLS_ST_OK                       = define(1)
    val DTLS_ST_CR_HELLO_VERIFY_REQUEST = define(2)
    val TLS_ST_CR_SRVR_HELLO            = define(3)
    val TLS_ST_CR_CERT                  = define(4)
    val TLS_ST_CR_COMP_CERT             = define(5)
    val TLS_ST_CR_CERT_STATUS           = define(6)
    val TLS_ST_CR_KEY_EXCH              = define(7)
    val TLS_ST_CR_CERT_REQ              = define(8)
    val TLS_ST_CR_SRVR_DONE             = define(9)
    val TLS_ST_CR_SESSION_TICKET        = define(10)
    val TLS_ST_CR_CHANGE                = define(11)
    val TLS_ST_CR_FINISHED              = define(12)
    val TLS_ST_CW_CLNT_HELLO            = define(13)
    val TLS_ST_CW_CERT                  = define(14)
    val TLS_ST_CW_COMP_CERT             = define(15)
    val TLS_ST_CW_KEY_EXCH              = define(16)
    val TLS_ST_CW_CERT_VRFY             = define(17)
    val TLS_ST_CW_CHANGE                = define(18)
    val TLS_ST_CW_NEXT_PROTO            = define(19)
    val TLS_ST_CW_FINISHED              = define(20)
    val TLS_ST_SW_HELLO_REQ             = define(21)
    val TLS_ST_SR_CLNT_HELLO            = define(22)
    val DTLS_ST_SW_HELLO_VERIFY_REQUEST = define(23)
    val TLS_ST_SW_SRVR_HELLO            = define(24)
    val TLS_ST_SW_CERT                  = define(25)
    val TLS_ST_SW_COMP_CERT             = define(26)
    val TLS_ST_SW_KEY_EXCH              = define(27)
    val TLS_ST_SW_CERT_REQ              = define(28)
    val TLS_ST_SW_SRVR_DONE             = define(29)
    val TLS_ST_SR_CERT                  = define(30)
    val TLS_ST_SR_COMP_CERT             = define(31)
    val TLS_ST_SR_KEY_EXCH              = define(32)
    val TLS_ST_SR_CERT_VRFY             = define(33)
    val TLS_ST_SR_NEXT_PROTO            = define(34)
    val TLS_ST_SR_CHANGE                = define(35)
    val TLS_ST_SR_FINISHED              = define(36)
    val TLS_ST_SW_SESSION_TICKET        = define(37)
    val TLS_ST_SW_CERT_STATUS           = define(38)
    val TLS_ST_SW_CHANGE                = define(39)
    val TLS_ST_SW_FINISHED              = define(40)
    val TLS_ST_SW_ENCRYPTED_EXTENSIONS  = define(41)
    val TLS_ST_CR_ENCRYPTED_EXTENSIONS  = define(42)
    val TLS_ST_CR_CERT_VRFY             = define(43)
    val TLS_ST_SW_CERT_VRFY             = define(44)
    val TLS_ST_CR_HELLO_REQ             = define(45)
    val TLS_ST_SW_KEY_UPDATE            = define(46)
    val TLS_ST_CW_KEY_UPDATE            = define(47)
    val TLS_ST_SR_KEY_UPDATE            = define(48)
    val TLS_ST_CR_KEY_UPDATE            = define(49)
    val TLS_ST_EARLY_DATA               = define(50)
    val TLS_ST_PENDING_EARLY_DATA_END   = define(51)
    val TLS_ST_CW_END_OF_EARLY_DATA     = define(52)
    val TLS_ST_SR_END_OF_EARLY_DATA     = define(53)

    def getName(value: OSSL_HANDSHAKE_STATE): Option[String] =
      value match
        case TLS_ST_BEFORE                   => Some("TLS_ST_BEFORE")
        case TLS_ST_OK                       => Some("TLS_ST_OK")
        case DTLS_ST_CR_HELLO_VERIFY_REQUEST => Some("DTLS_ST_CR_HELLO_VERIFY_REQUEST")
        case TLS_ST_CR_SRVR_HELLO            => Some("TLS_ST_CR_SRVR_HELLO")
        case TLS_ST_CR_CERT                  => Some("TLS_ST_CR_CERT")
        case TLS_ST_CR_COMP_CERT             => Some("TLS_ST_CR_COMP_CERT")
        case TLS_ST_CR_CERT_STATUS           => Some("TLS_ST_CR_CERT_STATUS")
        case TLS_ST_CR_KEY_EXCH              => Some("TLS_ST_CR_KEY_EXCH")
        case TLS_ST_CR_CERT_REQ              => Some("TLS_ST_CR_CERT_REQ")
        case TLS_ST_CR_SRVR_DONE             => Some("TLS_ST_CR_SRVR_DONE")
        case TLS_ST_CR_SESSION_TICKET        => Some("TLS_ST_CR_SESSION_TICKET")
        case TLS_ST_CR_CHANGE                => Some("TLS_ST_CR_CHANGE")
        case TLS_ST_CR_FINISHED              => Some("TLS_ST_CR_FINISHED")
        case TLS_ST_CW_CLNT_HELLO            => Some("TLS_ST_CW_CLNT_HELLO")
        case TLS_ST_CW_CERT                  => Some("TLS_ST_CW_CERT")
        case TLS_ST_CW_COMP_CERT             => Some("TLS_ST_CW_COMP_CERT")
        case TLS_ST_CW_KEY_EXCH              => Some("TLS_ST_CW_KEY_EXCH")
        case TLS_ST_CW_CERT_VRFY             => Some("TLS_ST_CW_CERT_VRFY")
        case TLS_ST_CW_CHANGE                => Some("TLS_ST_CW_CHANGE")
        case TLS_ST_CW_NEXT_PROTO            => Some("TLS_ST_CW_NEXT_PROTO")
        case TLS_ST_CW_FINISHED              => Some("TLS_ST_CW_FINISHED")
        case TLS_ST_SW_HELLO_REQ             => Some("TLS_ST_SW_HELLO_REQ")
        case TLS_ST_SR_CLNT_HELLO            => Some("TLS_ST_SR_CLNT_HELLO")
        case DTLS_ST_SW_HELLO_VERIFY_REQUEST => Some("DTLS_ST_SW_HELLO_VERIFY_REQUEST")
        case TLS_ST_SW_SRVR_HELLO            => Some("TLS_ST_SW_SRVR_HELLO")
        case TLS_ST_SW_CERT                  => Some("TLS_ST_SW_CERT")
        case TLS_ST_SW_COMP_CERT             => Some("TLS_ST_SW_COMP_CERT")
        case TLS_ST_SW_KEY_EXCH              => Some("TLS_ST_SW_KEY_EXCH")
        case TLS_ST_SW_CERT_REQ              => Some("TLS_ST_SW_CERT_REQ")
        case TLS_ST_SW_SRVR_DONE             => Some("TLS_ST_SW_SRVR_DONE")
        case TLS_ST_SR_CERT                  => Some("TLS_ST_SR_CERT")
        case TLS_ST_SR_COMP_CERT             => Some("TLS_ST_SR_COMP_CERT")
        case TLS_ST_SR_KEY_EXCH              => Some("TLS_ST_SR_KEY_EXCH")
        case TLS_ST_SR_CERT_VRFY             => Some("TLS_ST_SR_CERT_VRFY")
        case TLS_ST_SR_NEXT_PROTO            => Some("TLS_ST_SR_NEXT_PROTO")
        case TLS_ST_SR_CHANGE                => Some("TLS_ST_SR_CHANGE")
        case TLS_ST_SR_FINISHED              => Some("TLS_ST_SR_FINISHED")
        case TLS_ST_SW_SESSION_TICKET        => Some("TLS_ST_SW_SESSION_TICKET")
        case TLS_ST_SW_CERT_STATUS           => Some("TLS_ST_SW_CERT_STATUS")
        case TLS_ST_SW_CHANGE                => Some("TLS_ST_SW_CHANGE")
        case TLS_ST_SW_FINISHED              => Some("TLS_ST_SW_FINISHED")
        case TLS_ST_SW_ENCRYPTED_EXTENSIONS  => Some("TLS_ST_SW_ENCRYPTED_EXTENSIONS")
        case TLS_ST_CR_ENCRYPTED_EXTENSIONS  => Some("TLS_ST_CR_ENCRYPTED_EXTENSIONS")
        case TLS_ST_CR_CERT_VRFY             => Some("TLS_ST_CR_CERT_VRFY")
        case TLS_ST_SW_CERT_VRFY             => Some("TLS_ST_SW_CERT_VRFY")
        case TLS_ST_CR_HELLO_REQ             => Some("TLS_ST_CR_HELLO_REQ")
        case TLS_ST_SW_KEY_UPDATE            => Some("TLS_ST_SW_KEY_UPDATE")
        case TLS_ST_CW_KEY_UPDATE            => Some("TLS_ST_CW_KEY_UPDATE")
        case TLS_ST_SR_KEY_UPDATE            => Some("TLS_ST_SR_KEY_UPDATE")
        case TLS_ST_CR_KEY_UPDATE            => Some("TLS_ST_CR_KEY_UPDATE")
        case TLS_ST_EARLY_DATA               => Some("TLS_ST_EARLY_DATA")
        case TLS_ST_PENDING_EARLY_DATA_END   => Some("TLS_ST_PENDING_EARLY_DATA_END")
        case TLS_ST_CW_END_OF_EARLY_DATA     => Some("TLS_ST_CW_END_OF_EARLY_DATA")
        case TLS_ST_SR_END_OF_EARLY_DATA     => Some("TLS_ST_SR_END_OF_EARLY_DATA")
        case _                               => _root_.scala.None
    extension (a: OSSL_HANDSHAKE_STATE)
      inline def &(b: OSSL_HANDSHAKE_STATE): OSSL_HANDSHAKE_STATE = a & b
      inline def |(b: OSSL_HANDSHAKE_STATE): OSSL_HANDSHAKE_STATE = a | b
      inline def is(b: OSSL_HANDSHAKE_STATE): Boolean             = (a & b) == b

  /**
   * Missing from sn-bindgen output
   */
  opaque type SSL_VERIFY = CInt
  object SSL_VERIFY extends _BindgenEnumCInt[SSL_VERIFY]:
    given _tag: Tag[SSL_VERIFY] = Tag.Int

    inline def define(inline a: Int): SSL_VERIFY = a.toInt

    extension (a: SSL_VERIFY)
      inline def &(b: SSL_VERIFY): SSL_VERIFY = a & b
      inline def |(b: SSL_VERIFY): SSL_VERIFY = a | b
      inline def is(b: SSL_VERIFY): Boolean   = (a & b) == b

    val NONE                 = define(0x00)
    val PEER                 = define(0x01)
    val FAIL_IF_NO_PEER_CERT = define(0x02)
    val CLIENT_ONCE          = define(0x04)
    val POST_HANDSHAKE       = define(0x08)

    def getName(value: SSL_VERIFY): Option[String] =
      value match
        case NONE                 => Some("NONE")
        case PEER                 => Some("PEER")
        case FAIL_IF_NO_PEER_CERT => Some("FAIL_IF_NO_PEER_CERT")
        case CLIENT_ONCE          => Some("CLIENT_ONCE")
        case POST_HANDSHAKE       => Some("POST_HANDSHAKE")
        case _                    => _root_.scala.None

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_SESS_CACHE = CLongInt // larg: CLongInt
  object SSL_SESS_CACHE extends _BindgenEnumSize[SSL_SESS_CACHE]:
    given _tag: Tag[SSL_SESS_CACHE] = Tag.Size

    inline def define(inline a: Long): SSL_SESS_CACHE = a.toCSSize

    extension (a: SSL_SESS_CACHE)
      inline def &(b: SSL_SESS_CACHE): SSL_SESS_CACHE = a & b
      inline def |(b: SSL_SESS_CACHE): SSL_SESS_CACHE = a | b
      inline def is(b: SSL_SESS_CACHE): Boolean       = (a & b) == b

    val TLS_ST_BEFORE      = define(0)
    val OFF                = define(0x0000)
    val CLIENT             = define(0x0001)
    val SERVER             = define(0x0002)
    val BOTH               = CLIENT | SERVER
    val NO_AUTO_CLEAR      = define(0x0080)
    val NO_INTERNAL_LOOKUP = define(0x0100)
    val NO_INTERNAL_STORE  = define(0x0200)
    val NO_INTERNAL        = NO_INTERNAL_LOOKUP | NO_INTERNAL_STORE
    val UPDATE_TIME        = define(0x0400)

    def getName(value: SSL_SESS_CACHE): Option[String] =
      value match
        case TLS_ST_BEFORE      => Some("TLS_ST_BEFORE")
        case OFF                => Some("OFF")
        case CLIENT             => Some("CLIENT")
        case SERVER             => Some("SERVER")
        case BOTH               => Some("BOTH")
        case NO_AUTO_CLEAR      => Some("NO_AUTO_CLEAR")
        case NO_INTERNAL_LOOKUP => Some("NO_INTERNAL_LOOKUP")
        case NO_INTERNAL_STORE  => Some("NO_INTERNAL_STORE")
        case NO_INTERNAL        => Some("NO_INTERNAL")
        case UPDATE_TIME        => Some("UPDATE_TIME")
        case _                  => _root_.scala.None

  /**
   * [bindgen] header: /usr/include/openssl/ssl.h
   */
  opaque type SSL_CTRL = CInt
  object SSL_CTRL extends _BindgenEnumCInt[SSL_CTRL]:
    given _tag: Tag[SSL_CTRL] = Tag.Int

    inline def define(inline a: Int): SSL_CTRL = a.toInt

    // val SET_TMP_DH                         = define(3)
    // val SET_TMP_ECDH                       = define(4)
    // val SET_TMP_DH_CB                      = define(6)
    val GET_CLIENT_CERT_REQUEST            = define(9)
    val GET_NUM_RENEGOTIATIONS             = define(10)
    val CLEAR_NUM_RENEGOTIATIONS           = define(11)
    val GET_TOTAL_RENEGOTIATIONS           = define(12)
    val GET_FLAGS                          = define(13)
    val EXTRA_CHAIN_CERT                   = define(14)
    val SET_MSG_CALLBACK                   = define(15)
    val SET_MSG_CALLBACK_ARG               = define(16)
    val SET_MTU                            = define(17)
    val SESS_NUMBER                        = define(20)
    val SESS_CONNECT                       = define(21)
    val SESS_CONNECT_GOOD                  = define(22)
    val SESS_CONNECT_RENEGOTIATE           = define(23)
    val SESS_ACCEPT                        = define(24)
    val SESS_ACCEPT_GOOD                   = define(25)
    val SESS_ACCEPT_RENEGOTIATE            = define(26)
    val SESS_HIT                           = define(27)
    val SESS_CB_HIT                        = define(28)
    val SESS_MISSES                        = define(29)
    val SESS_TIMEOUTS                      = define(30)
    val SESS_CACHE_FULL                    = define(31)
    val MODE                               = define(33)
    val GET_READ_AHEAD                     = define(40)
    val SET_READ_AHEAD                     = define(41)
    val SET_SESS_CACHE_SIZE                = define(42)
    val GET_SESS_CACHE_SIZE                = define(43)
    val SET_SESS_CACHE_MODE                = define(44)
    val GET_SESS_CACHE_MODE                = define(45)
    val GET_MAX_CERT_LIST                  = define(50)
    val SET_MAX_CERT_LIST                  = define(51)
    val SET_MAX_SEND_FRAGMENT              = define(52)
    val SET_TLSEXT_SERVERNAME_CB           = define(53)
    val SET_TLSEXT_SERVERNAME_ARG          = define(54)
    val SET_TLSEXT_HOSTNAME                = define(55)
    val SET_TLSEXT_DEBUG_CB                = define(56)
    val SET_TLSEXT_DEBUG_ARG               = define(57)
    val GET_TLSEXT_TICKET_KEYS             = define(58)
    val SET_TLSEXT_TICKET_KEYS             = define(59)
    val SET_TLSEXT_STATUS_REQ_CB           = define(63)
    val SET_TLSEXT_STATUS_REQ_CB_ARG       = define(64)
    val SET_TLSEXT_STATUS_REQ_TYPE         = define(65)
    val GET_TLSEXT_STATUS_REQ_EXTS         = define(66)
    val SET_TLSEXT_STATUS_REQ_EXTS         = define(67)
    val GET_TLSEXT_STATUS_REQ_IDS          = define(68)
    val SET_TLSEXT_STATUS_REQ_IDS          = define(69)
    val GET_TLSEXT_STATUS_REQ_OCSP_RESP    = define(70)
    val SET_TLSEXT_STATUS_REQ_OCSP_RESP    = define(71)
    val SET_TLS_EXT_SRP_USERNAME_CB        = define(75)
    val SET_SRP_VERIFY_PARAM_CB            = define(76)
    val SET_SRP_GIVE_CLIENT_PWD_CB         = define(77)
    val SET_SRP_ARG                        = define(78)
    val SET_TLS_EXT_SRP_USERNAME           = define(79)
    val SET_TLS_EXT_SRP_STRENGTH           = define(80)
    val SET_TLS_EXT_SRP_PASSWORD           = define(81)
    val GET_RI_SUPPORT                     = define(76)
    val CLEAR_MODE                         = define(78)
    val SET_NOT_RESUMABLE_SESS_CB          = define(79)
    val GET_EXTRA_CHAIN_CERTS              = define(82)
    val CLEAR_EXTRA_CHAIN_CERTS            = define(83)
    val CHAIN                              = define(88)
    val CHAIN_CERT                         = define(89)
    val GET_GROUPS                         = define(90)
    val SET_GROUPS                         = define(91)
    val SET_GROUPS_LIST                    = define(92)
    val GET_SHARED_GROUP                   = define(93)
    val SET_SIGALGS                        = define(97)
    val SET_SIGALGS_LIST                   = define(98)
    val CERT_FLAGS                         = define(99)
    val CLEAR_CERT_FLAGS                   = define(100)
    val SET_CLIENT_SIGALGS                 = define(101)
    val SET_CLIENT_SIGALGS_LIST            = define(102)
    val GET_CLIENT_CERT_TYPES              = define(103)
    val SET_CLIENT_CERT_TYPES              = define(104)
    val BUILD_CERT_CHAIN                   = define(105)
    val SET_VERIFY_CERT_STORE              = define(106)
    val SET_CHAIN_CERT_STORE               = define(107)
    val GET_PEER_SIGNATURE_NID             = define(108)
    val GET_PEER_TMP_KEY                   = define(109)
    val GET_RAW_CIPHERLIST                 = define(110)
    val GET_EC_POINT_FORMATS               = define(111)
    val GET_CHAIN_CERTS                    = define(115)
    val SELECT_CURRENT_CERT                = define(116)
    val SET_CURRENT_CERT                   = define(117)
    val SET_DH_AUTO                        = define(118)
    val GET_EXTMS_SUPPORT                  = define(122)
    val SET_MIN_PROTO_VERSION              = define(123)
    val SET_MAX_PROTO_VERSION              = define(124)
    val SET_SPLIT_SEND_FRAGMENT            = define(125)
    val SET_MAX_PIPELINES                  = define(126)
    val GET_TLSEXT_STATUS_REQ_TYPE         = define(127)
    val GET_TLSEXT_STATUS_REQ_CB           = define(128)
    val GET_TLSEXT_STATUS_REQ_CB_ARG       = define(129)
    val GET_MIN_PROTO_VERSION              = define(130)
    val GET_MAX_PROTO_VERSION              = define(131)
    val GET_SIGNATURE_NID                  = define(132)
    val GET_TMP_KEY                        = define(133)
    val GET_NEGOTIATED_GROUP               = define(134)
    val GET_IANA_GROUPS                    = define(135)
    val SET_RETRY_VERIFY                   = define(136)
    val GET_VERIFY_CERT_STORE              = define(137)
    val GET_CHAIN_CERT_STORE               = define(138)
    val GET0_IMPLEMENTED_GROUPS            = define(139)
    val GET_SIGNATURE_NAME                 = define(140)
    val GET_PEER_SIGNATURE_NAME            = define(141)
    val GET_TLSEXT_STATUS_REQ_OCSP_RESP_EX = define(142)
    val SET_TLSEXT_STATUS_REQ_OCSP_RESP_EX = define(143)

    def getName(value: SSL_CTRL): Option[String] =
      value match
        // case SET_TMP_DH                   => Some("SSL_CTRL_SET_TMP_DH")
        // case SET_TMP_ECDH                 => Some("SSL_CTRL_SET_TMP_ECDH")
        // case SET_TMP_DH_CB                => Some("SSL_CTRL_SET_TMP_DH_CB")
        case GET_CLIENT_CERT_REQUEST            => Some("SSL_CTRL_GET_CLIENT_CERT_REQUEST")
        case GET_NUM_RENEGOTIATIONS             => Some("SSL_CTRL_GET_NUM_RENEGOTIATIONS")
        case CLEAR_NUM_RENEGOTIATIONS           => Some("SSL_CTRL_CLEAR_NUM_RENEGOTIATIONS")
        case GET_TOTAL_RENEGOTIATIONS           => Some("SSL_CTRL_GET_TOTAL_RENEGOTIATIONS")
        case GET_FLAGS                          => Some("SSL_CTRL_GET_FLAGS")
        case EXTRA_CHAIN_CERT                   => Some("SSL_CTRL_EXTRA_CHAIN_CERT")
        case SET_MSG_CALLBACK                   => Some("SSL_CTRL_SET_MSG_CALLBACK")
        case SET_MSG_CALLBACK_ARG               => Some("SSL_CTRL_SET_MSG_CALLBACK_ARG")
        case SET_MTU                            => Some("SSL_CTRL_SET_MTU")
        case SESS_NUMBER                        => Some("SSL_CTRL_SESS_NUMBER")
        case SESS_CONNECT                       => Some("SSL_CTRL_SESS_CONNECT")
        case SESS_CONNECT_GOOD                  => Some("SSL_CTRL_SESS_CONNECT_GOOD")
        case SESS_CONNECT_RENEGOTIATE           => Some("SSL_CTRL_SESS_CONNECT_RENEGOTIATE")
        case SESS_ACCEPT                        => Some("SSL_CTRL_SESS_ACCEPT")
        case SESS_ACCEPT_GOOD                   => Some("SSL_CTRL_SESS_ACCEPT_GOOD")
        case SESS_ACCEPT_RENEGOTIATE            => Some("SSL_CTRL_SESS_ACCEPT_RENEGOTIATE")
        case SESS_HIT                           => Some("SSL_CTRL_SESS_HIT")
        case SESS_CB_HIT                        => Some("SSL_CTRL_SESS_CB_HIT")
        case SESS_MISSES                        => Some("SSL_CTRL_SESS_MISSES")
        case SESS_TIMEOUTS                      => Some("SSL_CTRL_SESS_TIMEOUTS")
        case SESS_CACHE_FULL                    => Some("SSL_CTRL_SESS_CACHE_FULL")
        case MODE                               => Some("SSL_CTRL_MODE")
        case GET_READ_AHEAD                     => Some("SSL_CTRL_GET_READ_AHEAD")
        case SET_READ_AHEAD                     => Some("SSL_CTRL_SET_READ_AHEAD")
        case SET_SESS_CACHE_SIZE                => Some("SSL_CTRL_SET_SESS_CACHE_SIZE")
        case GET_SESS_CACHE_SIZE                => Some("SSL_CTRL_GET_SESS_CACHE_SIZE")
        case SET_SESS_CACHE_MODE                => Some("SSL_CTRL_SET_SESS_CACHE_MODE")
        case GET_SESS_CACHE_MODE                => Some("SSL_CTRL_GET_SESS_CACHE_MODE")
        case GET_MAX_CERT_LIST                  => Some("SSL_CTRL_GET_MAX_CERT_LIST")
        case SET_MAX_CERT_LIST                  => Some("SSL_CTRL_SET_MAX_CERT_LIST")
        case SET_MAX_SEND_FRAGMENT              => Some("SSL_CTRL_SET_MAX_SEND_FRAGMENT")
        case SET_TLSEXT_SERVERNAME_CB           => Some("SSL_CTRL_SET_TLSEXT_SERVERNAME_CB")
        case SET_TLSEXT_SERVERNAME_ARG          => Some("SSL_CTRL_SET_TLSEXT_SERVERNAME_ARG")
        case SET_TLSEXT_HOSTNAME                => Some("SSL_CTRL_SET_TLSEXT_HOSTNAME")
        case SET_TLSEXT_DEBUG_CB                => Some("SSL_CTRL_SET_TLSEXT_DEBUG_CB")
        case SET_TLSEXT_DEBUG_ARG               => Some("SSL_CTRL_SET_TLSEXT_DEBUG_ARG")
        case GET_TLSEXT_TICKET_KEYS             => Some("SSL_CTRL_GET_TLSEXT_TICKET_KEYS")
        case SET_TLSEXT_TICKET_KEYS             => Some("SSL_CTRL_SET_TLSEXT_TICKET_KEYS")
        case SET_TLSEXT_STATUS_REQ_CB           => Some("SSL_CTRL_SET_TLSEXT_STATUS_REQ_CB")
        case SET_TLSEXT_STATUS_REQ_CB_ARG       => Some("SSL_CTRL_SET_TLSEXT_STATUS_REQ_CB_ARG")
        case SET_TLSEXT_STATUS_REQ_TYPE         => Some("SSL_CTRL_SET_TLSEXT_STATUS_REQ_TYPE")
        case GET_TLSEXT_STATUS_REQ_EXTS         => Some("SSL_CTRL_GET_TLSEXT_STATUS_REQ_EXTS")
        case SET_TLSEXT_STATUS_REQ_EXTS         => Some("SSL_CTRL_SET_TLSEXT_STATUS_REQ_EXTS")
        case GET_TLSEXT_STATUS_REQ_IDS          => Some("SSL_CTRL_GET_TLSEXT_STATUS_REQ_IDS")
        case SET_TLSEXT_STATUS_REQ_IDS          => Some("SSL_CTRL_SET_TLSEXT_STATUS_REQ_IDS")
        case GET_TLSEXT_STATUS_REQ_OCSP_RESP    => Some("SSL_CTRL_GET_TLSEXT_STATUS_REQ_OCSP_RESP")
        case SET_TLSEXT_STATUS_REQ_OCSP_RESP    => Some("SSL_CTRL_SET_TLSEXT_STATUS_REQ_OCSP_RESP")
        case SET_TLS_EXT_SRP_USERNAME_CB        => Some("SSL_CTRL_SET_TLS_EXT_SRP_USERNAME_CB")
        case SET_SRP_VERIFY_PARAM_CB            => Some("SSL_CTRL_SET_SRP_VERIFY_PARAM_CB")
        case SET_SRP_GIVE_CLIENT_PWD_CB         => Some("SSL_CTRL_SET_SRP_GIVE_CLIENT_PWD_CB")
        case SET_SRP_ARG                        => Some("SSL_CTRL_SET_SRP_ARG")
        case SET_TLS_EXT_SRP_USERNAME           => Some("SSL_CTRL_SET_TLS_EXT_SRP_USERNAME")
        case SET_TLS_EXT_SRP_STRENGTH           => Some("SSL_CTRL_SET_TLS_EXT_SRP_STRENGTH")
        case SET_TLS_EXT_SRP_PASSWORD           => Some("SSL_CTRL_SET_TLS_EXT_SRP_PASSWORD")
        case GET_RI_SUPPORT                     => Some("SSL_CTRL_GET_RI_SUPPORT")
        case CLEAR_MODE                         => Some("SSL_CTRL_CLEAR_MODE")
        case SET_NOT_RESUMABLE_SESS_CB          => Some("SSL_CTRL_SET_NOT_RESUMABLE_SESS_CB")
        case GET_EXTRA_CHAIN_CERTS              => Some("SSL_CTRL_GET_EXTRA_CHAIN_CERTS")
        case CLEAR_EXTRA_CHAIN_CERTS            => Some("SSL_CTRL_CLEAR_EXTRA_CHAIN_CERTS")
        case CHAIN                              => Some("SSL_CTRL_CHAIN")
        case CHAIN_CERT                         => Some("SSL_CTRL_CHAIN_CERT")
        case GET_GROUPS                         => Some("SSL_CTRL_GET_GROUPS")
        case SET_GROUPS                         => Some("SSL_CTRL_SET_GROUPS")
        case SET_GROUPS_LIST                    => Some("SSL_CTRL_SET_GROUPS_LIST")
        case GET_SHARED_GROUP                   => Some("SSL_CTRL_GET_SHARED_GROUP")
        case SET_SIGALGS                        => Some("SSL_CTRL_SET_SIGALGS")
        case SET_SIGALGS_LIST                   => Some("SSL_CTRL_SET_SIGALGS_LIST")
        case CERT_FLAGS                         => Some("SSL_CTRL_CERT_FLAGS")
        case CLEAR_CERT_FLAGS                   => Some("SSL_CTRL_CLEAR_CERT_FLAGS")
        case SET_CLIENT_SIGALGS                 => Some("SSL_CTRL_SET_CLIENT_SIGALGS")
        case SET_CLIENT_SIGALGS_LIST            => Some("SSL_CTRL_SET_CLIENT_SIGALGS_LIST")
        case GET_CLIENT_CERT_TYPES              => Some("SSL_CTRL_GET_CLIENT_CERT_TYPES")
        case SET_CLIENT_CERT_TYPES              => Some("SSL_CTRL_SET_CLIENT_CERT_TYPES")
        case BUILD_CERT_CHAIN                   => Some("SSL_CTRL_BUILD_CERT_CHAIN")
        case SET_VERIFY_CERT_STORE              => Some("SSL_CTRL_SET_VERIFY_CERT_STORE")
        case SET_CHAIN_CERT_STORE               => Some("SSL_CTRL_SET_CHAIN_CERT_STORE")
        case GET_PEER_SIGNATURE_NID             => Some("SSL_CTRL_GET_PEER_SIGNATURE_NID")
        case GET_PEER_TMP_KEY                   => Some("SSL_CTRL_GET_PEER_TMP_KEY")
        case GET_RAW_CIPHERLIST                 => Some("SSL_CTRL_GET_RAW_CIPHERLIST")
        case GET_EC_POINT_FORMATS               => Some("SSL_CTRL_GET_EC_POINT_FORMATS")
        case GET_CHAIN_CERTS                    => Some("SSL_CTRL_GET_CHAIN_CERTS")
        case SELECT_CURRENT_CERT                => Some("SSL_CTRL_SELECT_CURRENT_CERT")
        case SET_CURRENT_CERT                   => Some("SSL_CTRL_SET_CURRENT_CERT")
        case SET_DH_AUTO                        => Some("SSL_CTRL_SET_DH_AUTO")
        case GET_EXTMS_SUPPORT                  => Some("SSL_CTRL_GET_EXTMS_SUPPORT")
        case SET_MIN_PROTO_VERSION              => Some("SSL_CTRL_SET_MIN_PROTO_VERSION")
        case SET_MAX_PROTO_VERSION              => Some("SSL_CTRL_SET_MAX_PROTO_VERSION")
        case SET_SPLIT_SEND_FRAGMENT            => Some("SSL_CTRL_SET_SPLIT_SEND_FRAGMENT")
        case SET_MAX_PIPELINES                  => Some("SSL_CTRL_SET_MAX_PIPELINES")
        case GET_TLSEXT_STATUS_REQ_TYPE         => Some("SSL_CTRL_GET_TLSEXT_STATUS_REQ_TYPE")
        case GET_TLSEXT_STATUS_REQ_CB           => Some("SSL_CTRL_GET_TLSEXT_STATUS_REQ_CB")
        case GET_TLSEXT_STATUS_REQ_CB_ARG       => Some("SSL_CTRL_GET_TLSEXT_STATUS_REQ_CB_ARG")
        case GET_MIN_PROTO_VERSION              => Some("SSL_CTRL_GET_MIN_PROTO_VERSION")
        case GET_MAX_PROTO_VERSION              => Some("SSL_CTRL_GET_MAX_PROTO_VERSION")
        case GET_SIGNATURE_NID                  => Some("SSL_CTRL_GET_SIGNATURE_NID")
        case GET_TMP_KEY                        => Some("SSL_CTRL_GET_TMP_KEY")
        case GET_NEGOTIATED_GROUP               => Some("SSL_CTRL_GET_NEGOTIATED_GROUP")
        case GET_IANA_GROUPS                    => Some("SSL_CTRL_GET_IANA_GROUPS")
        case SET_RETRY_VERIFY                   => Some("SSL_CTRL_SET_RETRY_VERIFY")
        case GET_VERIFY_CERT_STORE              => Some("SSL_CTRL_GET_VERIFY_CERT_STORE")
        case GET_CHAIN_CERT_STORE               => Some("SSL_CTRL_GET_CHAIN_CERT_STORE")
        case GET0_IMPLEMENTED_GROUPS            => Some("SSL_CTRL_GET0_IMPLEMENTED_GROUPS")
        case GET_SIGNATURE_NAME                 => Some("SSL_CTRL_GET_SIGNATURE_NAME")
        case GET_PEER_SIGNATURE_NAME            => Some("SSL_CTRL_GET_PEER_SIGNATURE_NAME")
        case GET_TLSEXT_STATUS_REQ_OCSP_RESP_EX => Some("SSL_CTRL_GET_TLSEXT_STATUS_REQ_OCSP_RESP_EX")
        case SET_TLSEXT_STATUS_REQ_OCSP_RESP_EX => Some("SSL_CTRL_SET_TLSEXT_STATUS_REQ_OCSP_RESP_EX")
        case _                                  => _root_.scala.None

  opaque type TLS_VERSION = CInt
  object TLS_VERSION extends _BindgenEnumCInt[TLS_VERSION]:
    given _tag: Tag[TLS_VERSION] = Tag.Int

    inline def define(inline a: Int): TLS_VERSION = a.toInt

    val SSL3          = define(0x0300)
    val TLS1          = define(0x0301)
    val TLS1_1        = define(0x0302)
    val TLS1_2        = define(0x0303)
    val TLS1_3        = define(0x0304)
    val DTLS1         = define(0xfeff)
    val DTLS1_2       = define(0xfefd)
    val DTLS1_BAD_VER = define(0x0100)

    def getName(value: TLS_VERSION): Option[String] =
      value match
        case SSL3          => Some("SSL3_VERSION")
        case TLS1          => Some("TLS1_VERSION")
        case TLS1_1        => Some("TLS1_1_VERSION")
        case TLS1_2        => Some("TLS1_2_VERSION")
        case TLS1_3        => Some("TLS1_3_VERSION")
        case DTLS1         => Some("DTLS1_VERSION")
        case DTLS1_2       => Some("DTLS1_2_VERSION")
        case DTLS1_BAD_VER => Some("DTLS1_BAD_VER")
        case _             => _root_.scala.None

  opaque type SSL_SHUTDOWN = CInt
  object SSL_SHUTDOWN extends _BindgenEnumCInt[SSL_SHUTDOWN]:
    given _tag: Tag[SSL_SHUTDOWN] = Tag.Int

    inline def define(inline a: Int): SSL_SHUTDOWN = a.toInt

    val NO_SETTING = define(0x00)
    val SENT       = define(0x01)
    val RECEIVED   = define(0x02)

    def getName(value: SSL_SHUTDOWN): Option[String] =
      value match
        case NO_SETTING => Some("This is not a standard value, represents no shutdown setting has been set")
        case SENT       => Some("SSL_SHUTDOWN_SENT")
        case RECEIVED   => Some("SSL_SHUTDOWN_RECEIVED")
        case _          => _root_.scala.None
