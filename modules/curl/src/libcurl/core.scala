/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/d21e75a6ae0cda978e68b26579e5665a0a92ca0d/include/curl/curl.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */
package snhttp.experimental.libcurl

import scala.scalanative.unsafe.{alloc, name, link, extern, define}
import scala.scalanative.unsafe.{
  Tag,
  Ptr,
  Zone,
  Size,
  CString,
  CStruct0,
  CStruct2,
  CStruct5,
  CFuncPtr2,
  CFuncPtr3,
  CStruct14,
  CVoidPtr,
  CLong,
}
import scala.scalanative.unsigned.*
import scala.scalanative.posix.sys.socket.{socklen_t, sockaddr}
import scala.scalanative.posix.time.time_t

import snhttp.experimental.libcurl._type._BindgenEnumCLong

import internal.SockAddrFamily

@extern
object core:

  @name("CURL")
  opaque type Curl = Unit // CVoidPtr // curl easy
  object Curl:
    given Tag[Curl] = Tag.Unit

  @name("CURLSH")
  opaque type CurlShare = Unit // CVoidPtr // curl share
  object CurlShare:
    given Tag[CurlShare] = Tag.Unit

  @name("curl_socket_t")
  opaque type CurlSocket = Int
  object CurlSocket:
    given Tag[CurlSocket] = Tag.Int

    inline def define(inline v: Int): CurlSocket = v

    val BAD = define(-1)

  /**
   * enum for the different supported SSL backends
   */
  @name("curl_sslbackend")
  opaque type CurlSslBackendId = UInt
  object CurlSslBackendId:
    given Tag[CurlSslBackendId] = Tag.UInt
    inline def define(inline a: Long): CurlSslBackendId = a.toUInt

    // Doesn't include the following deprecated backends
    // NSS, GSKIT, AXTLS, MESALINK

    val NONE = define(0)
    val OPENSSL = define(1)
    val GNUTLS = define(2)
    val OBSOLETE4 = define(4)
    val WOLFSSL = define(7)
    val SCHANNEL = define(8)
    val SECURETRANSPORT = define(9)
    val MBEDTLS = define(11)
    val BEARSSL = define(13)
    val RUSTLS = define(14)

    val AWSLC = OPENSSL
    val BORINGSSL = OPENSSL
    val LIBRESSL = OPENSSL

    inline def getName(inline value: CurlSslBackendId): Option[String] =
      inline value match
        case NONE            => Some("CURLSSLBACKEND_NONE")
        case OPENSSL         => Some("CURLSSLBACKEND_OPENSSL")
        case GNUTLS          => Some("CURLSSLBACKEND_GNUTLS")
        case OBSOLETE4       => Some("CURLSSLBACKEND_OBSOLETE4")
        case WOLFSSL         => Some("CURLSSLBACKEND_WOLFSSL")
        case SCHANNEL        => Some("CURLSSLBACKEND_SCHANNEL")
        case SECURETRANSPORT => Some("CURLSSLBACKEND_SECURETRANSPORT")
        case MBEDTLS         => Some("CURLSSLBACKEND_MBEDTLS")
        case BEARSSL         => Some("CURLSSLBACKEND_BEARSSL")
        case RUSTLS          => Some("CURLSSLBACKEND_RUSTLS")
        case AWSLC           => Some("CURLSSLBACKEND_AWSLC")
        case BORINGSSL       => Some("CURLSSLBACKEND_BORINGSSL")
        case LIBRESSL        => Some("CURLSSLBACKEND_LIBRESSL")
        case _               => None

    extension (a: CurlSslBackendId)
      inline def &(b: CurlSslBackendId): CurlSslBackendId = a & b
      inline def |(b: CurlSslBackendId): CurlSslBackendId = a | b
      inline def is(b: CurlSslBackendId): Boolean = (a & b) == b

  opaque type CurlFollow = CLong
  object CurlFollow extends _BindgenEnumCLong[CurlFollow]:
    given Tag[CurlFollow] = Tag.Size

    inline def define(inline a: Long): CurlFollow = a.toInt

    /** default:    disabled */
    val DISABLED = define(0L)

    /** bits for the CURLOPT_FOLLOWLOCATION option */
    val ALL = define(1L)

    /**
     * Do not use the custom method in the follow-up request if the HTTP code instructs so (301,
     * 302, 303).
     */
    val OBEYCODE = define(2L)

    /** Only use the custom method in the first request, always reset in the next */
    val FIRSTONLY = define(3L)

    extension (value: CurlFollow)
      inline def getName: String =
        inline value match
          case DISABLED  => "CURLFOLLOW_DISABLED"
          case ALL       => "CURLFOLLOW_ALL"
          case OBEYCODE  => "CURLFOLLOW_OBEYCODE"
          case FIRSTONLY => "CURLFOLLOW_FIRSTONLY"

  @name("curl_httppost")
  opaque type CurlHttpPost = CStruct14[
    /**
     * * next
     *
     * next entry in the list
     */
    Ptr[Byte], // Ptr[CurlHttpPost]
    /**
     * * name
     *
     * pointer to allocated name
     */
    CString,
    /**
     * * namelength
     *
     * length of name length
     */
    Long,
    /**
     * * contents
     *
     * pointer to allocated data contents
     */
    CString,
    /**
     * * contentslength
     *
     * length of contents field, see also CURL_HTTPPOST_LARGE
     */
    Long,
    /**
     * * buffer
     *
     * pointer to allocated buffer contents
     */
    CString,
    /**
     * * bufferlength
     *
     * length of buffer field
     */
    Long,
    /**
     * * contenttype
     *
     * Content-Type
     */
    CString,
    /**
     * * contentheader
     *
     * list of extra headers for this form
     */
    Ptr[Byte], // Ptr[CurlSlist],
    /**
     * * more
     *
     * if one field name has more than one file, this link should link to following files
     */
    Ptr[Byte], // Ptr[CurlHttpPost]
    /**
     * flags
     *
     * curl http post flags
     */
    CurlHttpPostFlag,
    /**
     * * showfilename
     *
     * The filename to show. If not set, the actual filename will be used (if this is a file part)
     */
    CString,
    /**
     * * userp
     *
     * custom pointer used for HTTPPOST_CALLBACK posts
     */
    CVoidPtr,
    /**
     * * contentlen
     *
     * alternative length of contents field. Used if CURL_HTTPPOST_LARGE is set. Added in curl
     * 7.46.0
     */
    CurlOff,
  ]
  object CurlHttpPost:
    given Tag[CurlHttpPost] = Tag.materializeCStruct14Tag[
      /**
       * * just help to split lines by formatter
       */
      Ptr[Byte],
      CString,
      Long,
      CString,
      Long,
      CString,
      Long,
      CString,
      Ptr[Byte], // Ptr[CurlSlist]
      Ptr[Byte], // Ptr[CurlHttpPost]
      CurlHttpPostFlag,
      CString,
      CVoidPtr,
      CurlOff,
    ]

    def apply(
        next: Ptr[CurlHttpPost],
        name: CString,
        namelength: Long,
        contents: CString,
        contentslength: Long,
        buffer: CString,
        bufferlength: Long,
        contenttype: CString,
        contentheader: Ptr[Byte],
        more: Ptr[Byte],
        flags: CurlHttpPostFlag,
        showfilename: CString,
        userp: CVoidPtr,
        contentlen: CurlOff,
    )(using Zone): Ptr[CurlHttpPost] =
      val ptr = alloc[CurlHttpPost](1)
      (!ptr).next = next
      (!ptr).name = name
      (!ptr).namelength = namelength
      (!ptr).contents = contents
      (!ptr).contentslength = contentslength
      (!ptr).buffer = buffer
      (!ptr).bufferlength = bufferlength
      (!ptr).contenttype = contenttype
      (!ptr).contentheader = contentheader
      (!ptr).more = more
      (!ptr).flags = flags
      (!ptr).showfilename = showfilename
      (!ptr).userp = userp
      (!ptr).contentlen = contentlen
      ptr

    extension (struct: CurlHttpPost)
      def next: Ptr[CurlHttpPost] = struct._1.asInstanceOf[Ptr[CurlHttpPost]]
      def next_=(value: Ptr[CurlHttpPost]): Unit = !struct.at1 = value.asInstanceOf[Ptr[Byte]]
      def name: CString = struct._2
      def name_=(value: CString): Unit = !struct.at2 = value
      def namelength: Long = struct._3
      def namelength_=(value: Long): Unit = !struct.at3 = value
      def contents: CString = struct._4
      def contents_=(value: CString): Unit = !struct.at4 = value
      def contentslength: Long = struct._5
      def contentslength_=(value: Long): Unit = !struct.at5 = value
      def buffer: CString = struct._6
      def buffer_=(value: CString): Unit = !struct.at6 = value
      def bufferlength: Long = struct._7
      def bufferlength_=(value: Long): Unit = !struct.at7 = value
      def contenttype: CString = struct._8
      def contenttype_=(value: CString): Unit = !struct.at8 = value
      def contentheader: Ptr[Byte] = struct._9.asInstanceOf[Ptr[Byte]]
      def contentheader_=(value: Ptr[Byte]): Unit = !struct.at9 = value.asInstanceOf[Ptr[Byte]]
      def more: Ptr[Byte] = struct._10.asInstanceOf[Ptr[Byte]]
      def more_=(value: Ptr[Byte]): Unit = !struct.at10 = value.asInstanceOf[Ptr[Byte]]
      def flags: CurlHttpPostFlag = struct._11
      def flags_=(value: CurlHttpPostFlag): Unit = !struct.at11 = value
      def showfilename: CString = struct._12
      def showfilename_=(value: CString): Unit = !struct.at12 = value
      def userp: CVoidPtr = struct._13
      def userp_=(value: CVoidPtr): Unit = !struct.at13 = value
      def contentlen: CurlOff = struct._14
      def contentlen_=(value: CurlOff): Unit = !struct.at14 = value

  /**
   * * define CURL_HTTPPOST_*
   */
  opaque type CurlHttpPostFlag = UInt
  object CurlHttpPostFlag:
    given Tag[CurlHttpPostFlag] = Tag.UInt
    inline def define(inline a: Int): CurlHttpPostFlag = a.toUInt

    /** specified content is a file name */
    val FILENAME = define(1 << 0)

    /** specified content is a file name */
    val READFILE = define(1 << 1)

    /** name is only stored pointer do not free in formfree */
    val PTRNAME = define(1 << 2)

    /** contents is only stored pointer do not free in formfree */
    val PTRCONTENTS = define(1 << 3)

    /** upload file from buffer */
    val BUFFER = define(1 << 4)

    /** upload file from pointer contents */
    val PTRBUFFER = define(1 << 5)

    /**
     * upload file contents by using the regular read callback to get the data and pass the given
     * pointer as custom pointer
     */
    val CALLBACK = define(1 << 6)

    /** use size in 'contentlen', added in 7.46.0 */
    val LARGE = define(1 << 7)

  // val CURL_PROGRESSFUNC_CONTINUE = 0x10000001

  // TODO: add structs
  //
  // 1. curl_progress_callback
  // 2. curl_xferinfo_callback
  // 3. curl_write_callback
  // 4. curl_resolver_start_callback

  // TODO:
  //
  // add enumeration of file types
  // typedef enum {} curlfiletype

  // TODO:
  //
  // 1. add define symbols CURLFINFOFLAG_KNOWN_*
  // 2. add func `curl_fileinfo`

  // TODO:
  //
  // 1. add define symbols `CURL_CHUNK_BGN_FUNC_*`
  // 2. add func `curl_chunk_end_callback`

  // TODO:
  //
  // 1. add defined return codes for FNMATCHFUNCTION
  //    known as `CURL_FNMATCHFUNC_*`
  // 2. add func `curl_fnmatch_callback`

  // TODO:
  //
  // 1. add defined return codes for the seek callbacks
  //    known as `CURL_SEEKFUNC_*`
  // 2. add func `curl_seek_callback`

  // TODO:
  //
  // 1. add defined return code for the read callback
  //    known as `CURL_READFUNC_*`
  // 2. add func `curl_read_callback`

  // TODO:
  //
  // 1. add defined Return code for when the trailing headers callback
  //    known as `CURL_TRAILERFUNC_*`
  // 2. add func `curl_read_callback`

  @name("curlsocktype")
  opaque type CurlSockType = UInt
  object CurlSockType:
    given Tag[CurlSockType] = Tag.UInt
    inline def define(inline a: Long): CurlSockType = a.toUInt

    /** socket created for a specific IP connection */
    val IPCXN = define(0)

    /** socket created by accept() call */
    val ACCEPT = define(1)

    /** never use */
    val LAST = define(2)

    inline def getName(inline value: CurlSockType): Option[String] =
      inline value match
        case IPCXN  => Some("CURLSOCKTYPE_IPCXN")
        case ACCEPT => Some("CURLSOCKTYPE_ACCEPT")
        case LAST   => Some("CURLSOCKTYPE_LAST")
        case _      => None

    extension (a: CurlSockType)
      inline def &(b: CurlSockType): CurlSockType = a & b
      inline def |(b: CurlSockType): CurlSockType = a | b
      inline def is(b: CurlSockType): Boolean = (a & b) == b

  /**
   * The return code from the sockopt_callback can signal information back to libcurl:
   */
  opaque type CurlSockOpt = UInt
  object CurlSockOpt:
    given Tag[CurlSockOpt] = Tag.UInt
    inline def define(inline a: Long): CurlSockOpt = a.toUInt

    val OK = define(0)

    /**
     * causes libcurl to abort and return CURLE_ABORTED_BY_CALLBACK
     */
    val ERROR = define(1)

  @name("curl_sockopt_callback") opaque type CurlSockOptCallback =
    CFuncPtr3[
      /** clientp */
      CVoidPtr,
      /** curlfd */
      CurlSocket,
      /** purpose */
      CurlSockType,
      CurlSockOpt,
    ]
  object CurlSockOptCallback:
    given Tag[CurlSockOptCallback] =
      Tag.materializeCFuncPtr3[CVoidPtr, CurlSocket, CurlSockType, CurlSockOpt]

    inline def apply(
        inline o: CFuncPtr3[CVoidPtr, CurlSocket, CurlSockType, CurlSockOpt],
    ): CurlSockOptCallback = o

  @name("curl_sockaddr") opaque type CurlSockAddr = CStruct5[
    /** family */
    SockAddrFamily,
    /** socktype */
    CurlSockType,
    /** protocol */
    Int,
    /** addrlen */
    socklen_t,
    sockaddr,
  ]
  object CurlSockAddr:
    given Tag[CurlSockAddr] =
      Tag.materializeCStruct5Tag[SockAddrFamily, CurlSockType, Int, socklen_t, sockaddr]

    def apply(
        family: SockAddrFamily,
        socktype: CurlSockType,
        protocol: Int,
        addrlen: UInt,
        addr: sockaddr,
    )(using
        Zone,
    ): Ptr[CurlSockAddr] =
      val ptr = alloc[CurlSockAddr](1)
      (!ptr).family = family
      (!ptr).socktype = socktype
      (!ptr).protocol = protocol
      (!ptr).addrlen = addrlen
      (!ptr).addr = addr
      ptr

    extension (struct: CurlSockAddr)
      def family: SockAddrFamily = struct._1
      def family_=(value: SockAddrFamily): Unit = !struct.at1 = value
      def socktype: CurlSockType = struct._2
      def socktype_=(value: CurlSockType): Unit = !struct.at2 = value
      def protocol: Int = struct._3
      def protocol_=(value: Int): Unit = !struct.at3 = value
      def addrlen: UInt = struct._4
      def addrlen_=(value: UInt): Unit = !struct.at4 = value
      def addr: sockaddr = struct._5
      def addr_=(value: sockaddr): Unit = !struct.at5 = value

  @name("curl_opensocket_callback")
  opaque type CurlOpensocketCallback =
    CFuncPtr3[
      /** clientp */
      CVoidPtr,
      /** purpose */
      CurlSockType,
      /** address */
      Ptr[CurlSockAddr],
      CurlSocket,
    ]
  object CurlOpensocketCallback:
    given Tag[CurlOpensocketCallback] =
      Tag.materializeCFuncPtr3[CVoidPtr, CurlSockType, Ptr[CurlSockAddr], CurlSocket]

    inline def apply(
        inline o: CFuncPtr3[CVoidPtr, CurlSockType, Ptr[CurlSockAddr], CurlSocket],
    ): CurlOpensocketCallback = o

  @name("curl_closesocket_callback")
  opaque type CurlClosesocketCallback = CFuncPtr2[CVoidPtr, CurlSocket, Int]
  object CurlClosesocketCallback:
    given Tag[CurlClosesocketCallback] =
      Tag.materializeCFuncPtr2[CVoidPtr, CurlSocket, Int]

  @name("curlioerr")
  opaque type CurlIoErr = UInt
  object CurlIoErr:
    given Tag[CurlIoErr] = Tag.UInt
    inline def define(inline a: Int): CurlIoErr = a.toUInt

    /** I/O operation successful */
    val OK = define(0)

    /** command was unknown to callback */
    val UNKNOWNCMD = define(1)

    /** failed to restart the read */
    val FAILRESTART = define(2)

    /** never use */
    val LAST = define(3)

    inline def getName(inline value: CurlIoErr): Option[String] =
      inline value match
        case OK          => Some("CURLIOE_OK")
        case UNKNOWNCMD  => Some("CURLIOE_UNKNOWNCMD")
        case FAILRESTART => Some("CURLIOE_FAILRESTART")
        case LAST        => Some("CURLIOE_LAST")
        case _           => None
    extension (a: CurlIoErr)
      inline def &(b: CurlIoErr): CurlIoErr = a & b
      inline def |(b: CurlIoErr): CurlIoErr = a | b
      inline def is(b: CurlIoErr): Boolean = (a & b) == b

  @name("curliocmd")
  opaque type CurlIoCmd = UInt
  object CurlIoCmd:
    given Tag[CurlIoCmd] = Tag.UInt
    inline def define(inline a: Int): CurlIoCmd = a.toUInt

    /** no operation */
    val NOP = define(0)

    /** restart the read stream from start */
    val RESTARTREAD = define(1)

    /** never use */
    val LAST = define(2)

    inline def getName(inline value: CurlIoCmd): Option[String] =
      inline value match
        case NOP         => Some("CURLIOCMD_NOP")
        case RESTARTREAD => Some("CURLIOCMD_RESTARTREAD")
        case LAST        => Some("CURLIOCMD_LAST")
        case _           => None

    extension (a: CurlIoCmd)
      inline def &(b: CurlIoCmd): CurlIoCmd = a & b
      inline def |(b: CurlIoCmd): CurlIoCmd = a | b
      inline def is(b: CurlIoCmd): Boolean = (a & b) == b

  @name("curl_ioctl_callback")
  opaque type CurlIoctlCallback = CFuncPtr3[
    /** handle */
    Ptr[Curl],
    /** cmd */
    CurlIoCmd,
    /** clientp */
    CVoidPtr,
    CurlIoErr,
  ]
  object CurlIoctlCallback:
    given Tag[CurlIoctlCallback] =
      Tag.materializeCFuncPtr3[Ptr[Curl], CurlIoCmd, CVoidPtr, CurlIoErr]

    inline def apply(
        inline o: CFuncPtr3[Ptr[Curl], CurlIoCmd, CVoidPtr, CurlIoErr],
    ): CurlIoctlCallback = o

  // TODO:
  //
  // 1. add the kind of data `curl_infotype` that is passed to information_callback
  // typedef enum {} curl_infotype
  // 2. add func `curl_debug_callback`

  // TODO:
  //
  // 1. add func `curl_prereq_callback`
  // 1. add define symbols CURL_PREREQFUNC_*

  /**
   * All possible error codes from all sorts of curl functions. Future versions may return other
   * values, stay prepared.
   *
   * Always add new return codes last. Never *EVER* remove any. The return codes must remain the
   * same!
   */
  opaque type CurlCode = UInt
  object CurlCode:
    given Tag[CurlCode] = Tag.UInt
    inline def define(inline a: Long): CurlCode = a.toUInt

    val OK = define(0)
    val UNSUPPORTED_PROTOCOL = define(1)
    val FAILED_INIT = define(2)
    val URL_MALFORMAT = define(3)
    val NOT_BUILT_IN = define(4)
    val COULDNT_RESOLVE_PROXY = define(5)
    val COULDNT_RESOLVE_HOST = define(6)
    val COULDNT_CONNECT = define(7)
    val WEIRD_SERVER_REPLY = define(8)
    val REMOTE_ACCESS_DENIED = define(9)
    val FTP_ACCEPT_FAILED = define(10)
    val FTP_WEIRD_PASS_REPLY = define(11)
    val FTP_ACCEPT_TIMEOUT = define(12)
    val FTP_WEIRD_PASV_REPLY = define(13)
    val FTP_WEIRD_227_FORMAT = define(14)
    val FTP_CANT_GET_HOST = define(15)
    val HTTP2 = define(16)
    val FTP_COULDNT_SET_TYPE = define(17)
    val PARTIAL_FILE = define(18)
    val FTP_COULDNT_RETR_FILE = define(19)
    val OBSOLETE20 = define(20)
    val QUOTE_ERROR = define(21)
    val HTTP_RETURNED_ERROR = define(22)
    val WRITE_ERROR = define(23)
    val OBSOLETE24 = define(24)
    val UPLOAD_FAILED = define(25)
    val READ_ERROR = define(26)
    val OUT_OF_MEMORY = define(27)
    val OPERATION_TIMEDOUT = define(28)
    val OBSOLETE29 = define(29)
    val FTP_PORT_FAILED = define(30)
    val FTP_COULDNT_USE_REST = define(31)
    val OBSOLETE32 = define(32)
    val RANGE_ERROR = define(33)
    val HTTP_POST_ERROR = define(34)
    val SSL_CONNECT_ERROR = define(35)
    val BAD_DOWNLOAD_RESUME = define(36)
    val FILE_COULDNT_READ_FILE = define(37)
    val LDAP_CANNOT_BIND = define(38)
    val LDAP_SEARCH_FAILED = define(39)
    val OBSOLETE40 = define(40)
    val FUNCTION_NOT_FOUND = define(41)
    val ABORTED_BY_CALLBACK = define(42)
    val BAD_FUNCTION_ARGUMENT = define(43)
    val OBSOLETE44 = define(44)
    val INTERFACE_FAILED = define(45)
    val OBSOLETE46 = define(46)
    val TOO_MANY_REDIRECTS = define(47)
    val UNKNOWN_OPTION = define(48)
    val SETOPT_OPTION_SYNTAX = define(49)
    val OBSOLETE50 = define(50)
    val OBSOLETE51 = define(51)
    val GOT_NOTHING = define(52)
    val SSL_ENGINE_NOTFOUND = define(53)
    val SSL_ENGINE_SETFAILED = define(54)
    val SEND_ERROR = define(55)
    val RECV_ERROR = define(56)
    val OBSOLETE57 = define(57)
    val SSL_CERTPROBLEM = define(58)
    val SSL_CIPHER = define(59)
    val PEER_FAILED_VERIFICATION = define(60)
    val BAD_CONTENT_ENCODING = define(61)
    val OBSOLETE62 = define(62)
    val FILESIZE_EXCEEDED = define(63)
    val USE_SSL_FAILED = define(64)
    val SEND_FAIL_REWIND = define(65)
    val SSL_ENGINE_INITFAILED = define(66)
    val LOGIN_DENIED = define(67)
    val TFTP_NOTFOUND = define(68)
    val TFTP_PERM = define(69)
    val REMOTE_DISK_FULL = define(70)
    val TFTP_ILLEGAL = define(71)
    val TFTP_UNKNOWNID = define(72)
    val REMOTE_FILE_EXISTS = define(73)
    val TFTP_NOSUCHUSER = define(74)
    val OBSOLETE75 = define(75)
    val OBSOLETE76 = define(76)
    val SSL_CACERT_BADFILE = define(77)
    val REMOTE_FILE_NOT_FOUND = define(78)
    val SSH = define(79)
    val SSL_SHUTDOWN_FAILED = define(80)
    val AGAIN = define(81)
    val SSL_CRL_BADFILE = define(82)
    val SSL_ISSUER_ERROR = define(83)
    val FTP_PRET_FAILED = define(84)
    val RTSP_CSEQ_ERROR = define(85)
    val RTSP_SESSION_ERROR = define(86)
    val FTP_BAD_FILE_LIST = define(87)
    val CHUNK_FAILED = define(88)
    val NO_CONNECTION_AVAILABLE = define(89)
    val SSL_PINNEDPUBKEYNOTMATCH = define(90)
    val SSL_INVALIDCERTSTATUS = define(91)
    val HTTP2_STREAM = define(92)
    val RECURSIVE_API_CALL = define(93)
    val AUTH_ERROR = define(94)
    val HTTP3 = define(95)
    val QUIC_CONNECT_ERROR = define(96)
    val PROXY = define(97)
    val SSL_CLIENTCERT = define(98)
    val UNRECOVERABLE_POLL = define(99)
    val CURL_LAST = define(100)

    inline def getName(inline value: CurlCode): Option[String] =
      inline value match
        case OK                       => Some("CURLE_OK")
        case UNSUPPORTED_PROTOCOL     => Some("CURLE_UNSUPPORTED_PROTOCOL")
        case FAILED_INIT              => Some("CURLE_FAILED_INIT")
        case URL_MALFORMAT            => Some("CURLE_URL_MALFORMAT")
        case NOT_BUILT_IN             => Some("CURLE_NOT_BUILT_IN")
        case COULDNT_RESOLVE_PROXY    => Some("CURLE_COULDNT_RESOLVE_PROXY")
        case COULDNT_RESOLVE_HOST     => Some("CURLE_COULDNT_RESOLVE_HOST")
        case COULDNT_CONNECT          => Some("CURLE_COULDNT_CONNECT")
        case WEIRD_SERVER_REPLY       => Some("CURLE_WEIRD_SERVER_REPLY")
        case REMOTE_ACCESS_DENIED     => Some("CURLE_REMOTE_ACCESS_DENIED")
        case FTP_ACCEPT_FAILED        => Some("CURLE_FTP_ACCEPT_FAILED")
        case FTP_WEIRD_PASS_REPLY     => Some("CURLE_FTP_WEIRD_PASS_REPLY")
        case FTP_ACCEPT_TIMEOUT       => Some("CURLE_FTP_ACCEPT_TIMEOUT")
        case FTP_WEIRD_PASV_REPLY     => Some("CURLE_FTP_WEIRD_PASV_REPLY")
        case FTP_WEIRD_227_FORMAT     => Some("CURLE_FTP_WEIRD_227_FORMAT")
        case FTP_CANT_GET_HOST        => Some("CURLE_FTP_CANT_GET_HOST")
        case HTTP2                    => Some("CURLE_HTTP2")
        case FTP_COULDNT_SET_TYPE     => Some("CURLE_FTP_COULDNT_SET_TYPE")
        case PARTIAL_FILE             => Some("CURLE_PARTIAL_FILE")
        case FTP_COULDNT_RETR_FILE    => Some("CURLE_FTP_COULDNT_RETR_FILE")
        case OBSOLETE20               => Some("CURLE_OBSOLETE20")
        case QUOTE_ERROR              => Some("CURLE_QUOTE_ERROR")
        case HTTP_RETURNED_ERROR      => Some("CURLE_HTTP_RETURNED_ERROR")
        case WRITE_ERROR              => Some("CURLE_WRITE_ERROR")
        case OBSOLETE24               => Some("CURLE_OBSOLETE24")
        case UPLOAD_FAILED            => Some("CURLE_UPLOAD_FAILED")
        case READ_ERROR               => Some("CURLE_READ_ERROR")
        case OUT_OF_MEMORY            => Some("CURLE_OUT_OF_MEMORY")
        case OPERATION_TIMEDOUT       => Some("CURLE_OPERATION_TIMEDOUT")
        case OBSOLETE29               => Some("CURLE_OBSOLETE29")
        case FTP_PORT_FAILED          => Some("CURLE_FTP_PORT_FAILED")
        case FTP_COULDNT_USE_REST     => Some("CURLE_FTP_COULDNT_USE_REST")
        case OBSOLETE32               => Some("CURLE_OBSOLETE32")
        case RANGE_ERROR              => Some("CURLE_RANGE_ERROR")
        case HTTP_POST_ERROR          => Some("CURLE_HTTP_POST_ERROR")
        case SSL_CONNECT_ERROR        => Some("CURLE_SSL_CONNECT_ERROR")
        case BAD_DOWNLOAD_RESUME      => Some("CURLE_BAD_DOWNLOAD_RESUME")
        case FILE_COULDNT_READ_FILE   => Some("CURLE_FILE_COULDNT_READ_FILE")
        case LDAP_CANNOT_BIND         => Some("CURLE_LDAP_CANNOT_BIND")
        case LDAP_SEARCH_FAILED       => Some("CURLE_LDAP_SEARCH_FAILED")
        case OBSOLETE40               => Some("CURLE_OBSOLETE40")
        case FUNCTION_NOT_FOUND       => Some("CURLE_FUNCTION_NOT_FOUND")
        case ABORTED_BY_CALLBACK      => Some("CURLE_ABORTED_BY_CALLBACK")
        case BAD_FUNCTION_ARGUMENT    => Some("CURLE_BAD_FUNCTION_ARGUMENT")
        case OBSOLETE44               => Some("CURLE_OBSOLETE44")
        case INTERFACE_FAILED         => Some("CURLE_INTERFACE_FAILED")
        case OBSOLETE46               => Some("CURLE_OBSOLETE46")
        case TOO_MANY_REDIRECTS       => Some("CURLE_TOO_MANY_REDIRECTS")
        case UNKNOWN_OPTION           => Some("CURLE_UNKNOWN_OPTION")
        case SETOPT_OPTION_SYNTAX     => Some("CURLE_SETOPT_OPTION_SYNTAX")
        case OBSOLETE50               => Some("CURLE_OBSOLETE50")
        case OBSOLETE51               => Some("CURLE_OBSOLETE51")
        case GOT_NOTHING              => Some("CURLE_GOT_NOTHING")
        case SSL_ENGINE_NOTFOUND      => Some("CURLE_SSL_ENGINE_NOTFOUND")
        case SSL_ENGINE_SETFAILED     => Some("CURLE_SSL_ENGINE_SETFAILED")
        case SEND_ERROR               => Some("CURLE_SEND_ERROR")
        case RECV_ERROR               => Some("CURLE_RECV_ERROR")
        case OBSOLETE57               => Some("CURLE_OBSOLETE57")
        case SSL_CERTPROBLEM          => Some("CURLE_SSL_CERTPROBLEM")
        case SSL_CIPHER               => Some("CURLE_SSL_CIPHER")
        case PEER_FAILED_VERIFICATION => Some("CURLE_PEER_FAILED_VERIFICATION")
        case BAD_CONTENT_ENCODING     => Some("CURLE_BAD_CONTENT_ENCODING")
        case OBSOLETE62               => Some("CURLE_OBSOLETE62")
        case FILESIZE_EXCEEDED        => Some("CURLE_FILESIZE_EXCEEDED")
        case USE_SSL_FAILED           => Some("CURLE_USE_SSL_FAILED")
        case SEND_FAIL_REWIND         => Some("CURLE_SEND_FAIL_REWIND")
        case SSL_ENGINE_INITFAILED    => Some("CURLE_SSL_ENGINE_INITFAILED")
        case LOGIN_DENIED             => Some("CURLE_LOGIN_DENIED")
        case TFTP_NOTFOUND            => Some("CURLE_TFTP_NOTFOUND")
        case TFTP_PERM                => Some("CURLE_TFTP_PERM")
        case REMOTE_DISK_FULL         => Some("CURLE_REMOTE_DISK_FULL")
        case TFTP_ILLEGAL             => Some("CURLE_TFTP_ILLEGAL")
        case TFTP_UNKNOWNID           => Some("CURLE_TFTP_UNKNOWNID")
        case REMOTE_FILE_EXISTS       => Some("CURLE_REMOTE_FILE_EXISTS")
        case TFTP_NOSUCHUSER          => Some("CURLE_TFTP_NOSUCHUSER")
        case OBSOLETE75               => Some("CURLE_OBSOLETE75")
        case OBSOLETE76               => Some("CURLE_OBSOLETE76")
        case SSL_CACERT_BADFILE       => Some("CURLE_SSL_CACERT_BADFILE")
        case REMOTE_FILE_NOT_FOUND    => Some("CURLE_REMOTE_FILE_NOT_FOUND")
        case SSH                      => Some("CURLE_SSH")
        case SSL_SHUTDOWN_FAILED      => Some("CURLE_SSL_SHUTDOWN_FAILED")
        case AGAIN                    => Some("CURLE_AGAIN")
        case SSL_CRL_BADFILE          => Some("CURLE_SSL_CRL_BADFILE")
        case SSL_ISSUER_ERROR         => Some("CURLE_SSL_ISSUER_ERROR")
        case FTP_PRET_FAILED          => Some("CURLE_FTP_PRET_FAILED")
        case RTSP_CSEQ_ERROR          => Some("CURLE_RTSP_CSEQ_ERROR")
        case RTSP_SESSION_ERROR       => Some("CURLE_RTSP_SESSION_ERROR")
        case FTP_BAD_FILE_LIST        => Some("CURLE_FTP_BAD_FILE_LIST")
        case CHUNK_FAILED             => Some("CURLE_CHUNK_FAILED")
        case NO_CONNECTION_AVAILABLE  => Some("CURLE_NO_CONNECTION_AVAILABLE")
        case SSL_PINNEDPUBKEYNOTMATCH => Some("CURLE_SSL_PINNEDPUBKEYNOTMATCH")
        case SSL_INVALIDCERTSTATUS    => Some("CURLE_SSL_INVALIDCERTSTATUS")
        case HTTP2_STREAM             => Some("CURLE_HTTP2_STREAM")
        case RECURSIVE_API_CALL       => Some("CURLE_RECURSIVE_API_CALL")
        case AUTH_ERROR               => Some("CURLE_AUTH_ERROR")
        case HTTP3                    => Some("CURLE_HTTP3")
        case QUIC_CONNECT_ERROR       => Some("CURLE_QUIC_CONNECT_ERROR")
        case PROXY                    => Some("CURLE_PROXY")
        case SSL_CLIENTCERT           => Some("CURLE_SSL_CLIENTCERT")
        case UNRECOVERABLE_POLL       => Some("CURLE_UNRECOVERABLE_POLL")
        case CURL_LAST                => Some("CURL_LAST")
        case _                        => None
    extension (a: CurlCode)
      inline def &(b: CurlCode): CurlCode = a & b
      inline def |(b: CurlCode): CurlCode = a | b
      inline def is(b: CurlCode): Boolean = (a & b) == b

  @name("CURLproxycode")
  opaque type CurlRroxyCode = UInt
  object CurlRroxyCode:
    given Tag[CurlRroxyCode] = Tag.UInt

    inline def define(inline a: Long): CurlRroxyCode = a.toUInt

    val OK = define(0)
    val BAD_ADDRESS_TYPE = define(1)
    val BAD_VERSION = define(2)
    val CLOSED = define(3)
    val GSSAPI = define(4)
    val GSSAPI_PERMSG = define(5)
    val GSSAPI_PROTECTION = define(6)
    val IDENTD = define(7)
    val IDENTD_DIFFER = define(8)
    val LONG_HOSTNAME = define(9)
    val LONG_PASSWD = define(10)
    val LONG_USER = define(11)
    val NO_AUTH = define(12)
    val RECV_ADDRESS = define(13)
    val RECV_AUTH = define(14)
    val RECV_CONNECT = define(15)
    val RECV_REQACK = define(16)
    val REPLY_ADDRESS_TYPE_NOT_SUPPORTED = define(17)
    val REPLY_COMMAND_NOT_SUPPORTED = define(18)
    val REPLY_CONNECTION_REFUSED = define(19)
    val REPLY_GENERAL_SERVER_FAILURE = define(20)
    val REPLY_HOST_UNREACHABLE = define(21)
    val REPLY_NETWORK_UNREACHABLE = define(22)
    val REPLY_NOT_ALLOWED = define(23)
    val REPLY_TTL_EXPIRED = define(24)
    val REPLY_UNASSIGNED = define(25)
    val REQUEST_FAILED = define(26)
    val RESOLVE_HOST = define(27)
    val SEND_AUTH = define(28)
    val SEND_CONNECT = define(29)
    val SEND_REQUEST = define(30)
    val UNKNOWN_FAIL = define(31)
    val UNKNOWN_MODE = define(32)
    val USER_REJECTED = define(33)
    val LAST = define(34)

    inline def getName(inline value: CurlRroxyCode): Option[String] =
      inline value match
        case OK                => Some("CURLPX_OK")
        case BAD_ADDRESS_TYPE  => Some("CURLPX_BAD_ADDRESS_TYPE")
        case BAD_VERSION       => Some("CURLPX_BAD_VERSION")
        case CLOSED            => Some("CURLPX_CLOSED")
        case GSSAPI            => Some("CURLPX_GSSAPI")
        case GSSAPI_PERMSG     => Some("CURLPX_GSSAPI_PERMSG")
        case GSSAPI_PROTECTION => Some("CURLPX_GSSAPI_PROTECTION")
        case IDENTD            => Some("CURLPX_IDENTD")
        case IDENTD_DIFFER     => Some("CURLPX_IDENTD_DIFFER")
        case LONG_HOSTNAME     => Some("CURLPX_LONG_HOSTNAME")
        case LONG_PASSWD       => Some("CURLPX_LONG_PASSWD")
        case LONG_USER         => Some("CURLPX_LONG_USER")
        case NO_AUTH           => Some("CURLPX_NO_AUTH")
        case RECV_ADDRESS      => Some("CURLPX_RECV_ADDRESS")
        case RECV_AUTH         => Some("CURLPX_RECV_AUTH")
        case RECV_CONNECT      => Some("CURLPX_RECV_CONNECT")
        case RECV_REQACK       => Some("CURLPX_RECV_REQACK")
        case REPLY_ADDRESS_TYPE_NOT_SUPPORTED =>
          Some("CURLPX_REPLY_ADDRESS_TYPE_NOT_SUPPORTED")
        case REPLY_COMMAND_NOT_SUPPORTED  => Some("CURLPX_REPLY_COMMAND_NOT_SUPPORTED")
        case REPLY_CONNECTION_REFUSED     => Some("CURLPX_REPLY_CONNECTION_REFUSED")
        case REPLY_GENERAL_SERVER_FAILURE => Some("CURLPX_REPLY_GENERAL_SERVER_FAILURE")
        case REPLY_HOST_UNREACHABLE       => Some("CURLPX_REPLY_HOST_UNREACHABLE")
        case REPLY_NETWORK_UNREACHABLE    => Some("CURLPX_REPLY_NETWORK_UNREACHABLE")
        case REPLY_NOT_ALLOWED            => Some("CURLPX_REPLY_NOT_ALLOWED")
        case REPLY_TTL_EXPIRED            => Some("CURLPX_REPLY_TTL_EXPIRED")
        case REPLY_UNASSIGNED             => Some("CURLPX_REPLY_UNASSIGNED")
        case REQUEST_FAILED               => Some("CURLPX_REQUEST_FAILED")
        case RESOLVE_HOST                 => Some("CURLPX_RESOLVE_HOST")
        case SEND_AUTH                    => Some("CURLPX_SEND_AUTH")
        case SEND_CONNECT                 => Some("CURLPX_SEND_CONNECT")
        case SEND_REQUEST                 => Some("CURLPX_SEND_REQUEST")
        case UNKNOWN_FAIL                 => Some("CURLPX_UNKNOWN_FAIL")
        case UNKNOWN_MODE                 => Some("CURLPX_UNKNOWN_MODE")
        case USER_REJECTED                => Some("CURLPX_USER_REJECTED")
        case LAST                         => Some("CURLPX_LAST")
        case _                            => None

    extension (a: CurlRroxyCode)
      inline def &(b: CurlRroxyCode): CurlRroxyCode = a & b
      inline def |(b: CurlRroxyCode): CurlRroxyCode = a | b
      inline def is(b: CurlRroxyCode): Boolean = (a & b) == b

  // TODO: add func `curl_conv_callback``

  // TODO: add func `curl_ssl_ctx_callback``

  @name("curl_proxytype")
  opaque type CurlProxyType = UInt
  object CurlProxyType:
    given Tag[CurlProxyType] = Tag.UInt

    inline def define(inline a: Long): CurlProxyType = a.toUInt

    val HTTP = define(0)
    val HTTP_1_0 = define(1)
    val HTTPS = define(2)
    val HTTPS2 = define(3)
    val SOCKS4 = define(4)
    val SOCKS5 = define(5)
    val SOCKS4A = define(6)
    val SOCKS5_HOSTNAME = define(7)

    inline def getName(inline value: CurlProxyType): Option[String] =
      inline value match
        case HTTP            => Some("CURLPROXY_HTTP")
        case HTTP_1_0        => Some("CURLPROXY_HTTP_1_0")
        case HTTPS           => Some("CURLPROXY_HTTPS")
        case HTTPS2          => Some("CURLPROXY_HTTPS2")
        case SOCKS4          => Some("CURLPROXY_SOCKS4")
        case SOCKS5          => Some("CURLPROXY_SOCKS5")
        case SOCKS4A         => Some("CURLPROXY_SOCKS4A")
        case SOCKS5_HOSTNAME => Some("CURLPROXY_SOCKS5_HOSTNAME")
        case _               => None

    extension (a: CurlProxyType)
      inline def &(b: CurlProxyType): CurlProxyType = a & b
      inline def |(b: CurlProxyType): CurlProxyType = a | b
      inline def is(b: CurlProxyType): Boolean = (a & b) == b

  // TODO:
  //
  // 1. add define symbols `CURLAUTH_*`
  // 2. add enum `curl_khtype`
  // 3. add struct `curl_khkey`
  // 4. add enum `curl_khstat`
  // 5. add enum `curl_khmatch`
  // 6. add func `curl_sshkeycallback`
  // 7. add func `curl_sshhostkeycallback`

  opaque type CurlUseSsl = CLong
  object CurlUseSsl extends _BindgenEnumCLong[CurlUseSsl]:

    inline def define(inline a: Long): CurlUseSsl = a.toInt

    val NONE = define(0) // do not attempt to use SSL
    val TRY = define(1) // try using SSL, proceed anyway otherwise
    val CONTROL = define(2) // SSL for the control connection or fail
    val ALL = define(3) // SSL for all communication or fail
    // val LAST = define(4) // not an option, never use

  // TODO:
  //
  // 2. add define symbols `CURLSSLOPT_*`
  // 3. add typedef enum `curl_ftpccc`
  // 4. add typedef enum `curl_ftpauth`
  // 5. add typedef enum `curl_ftpcreatedir`
  // 6. add typedef enum `curl_ftpmethod`

  /**
   * CURLOPT_HEADEROPT is relocated to [[options]] CurlOpt_HeaderOpt
   */

  /**
   * CURLOPT_ALTSVC_CTRL is relocated to [[options]] CurlOpt_AltSvcCtrl
   */

  // TODO
  //
  // 1. add struct `curl_hstsentry`
  // 2. add struct `curl_index`
  // 3. add typedef enum `CURLSTScode`
  // 4. add func `curl_hstsread_callback`
  // 5. add func `curl_hstswrite_callback`
  // 6. add define symbols `CURLHSTS_*`

  /**
   * CURLoption is relocated to [[options]] CurlOption
   */

  /**
   * CURLOPT_IPRESOLVE is relocated to [[options]]
   */

  // type CurlOpt_RtspHeader = CurlOpt_HttpHeader

  /**
   * CURLOPT_HTTP_VERSION is relocated to [[options]]
   */

  // TODO: add enum `CURL_RTSPREQ_*`

  // TODO: Add enum `CURL_NETRC_OPTION`

  object CurlSslVersion:
    val DEFAULT: UInt = 0.toUInt
    val TLSv1: UInt = 1.toUInt // TLS 1.x
    val SSLv2: UInt = 2.toUInt
    val SSLv3: UInt = 3.toUInt
    val TLSv1_0: UInt = 4.toUInt
    val TLSv1_1: UInt = 5.toUInt
    val TLSv1_2: UInt = 6.toUInt
    val TLSv1_3: UInt = 7.toUInt
    val LAST: UInt = 8.toUInt // never use, keep last

  object CurlSslMaxVersion:
    val MAX_NONE: UInt = 0.toUInt
    val MAX_DEFAULT: UInt = (CurlSslVersion.TLSv1 << 16).toUInt
    val MAX_TLSv1_0: UInt = (CurlSslVersion.TLSv1_0 << 16).toUInt
    val MAX_TLSv1_1: UInt = (CurlSslVersion.TLSv1_1 << 16).toUInt
    val MAX_TLSv1_2: UInt = (CurlSslVersion.TLSv1_2 << 16).toUInt
    val MAX_TLSv1_3: UInt = (CurlSslVersion.TLSv1_3 << 16).toUInt
    val MAX_LAST: UInt = (CurlSslVersion.LAST << 16).toUInt // never use, keep last

  @name("CURL_TLSAUTH")
  opaque type CurlTlsAuth = UInt
  object CurlTlsAuth:
    given Tag[CurlTlsAuth] = Tag.UInt

    inline def define(inline a: Long): CurlTlsAuth = a.toUInt

    val NONE = define(0)
    val SRP = define(1)
    val LAST = define(2)

    inline def getName(inline value: CurlTlsAuth): Option[String] =
      inline value match
        case NONE => Some("CURL_TLSAUTH_NONE")
        case SRP  => Some("CURL_TLSAUTH_SRP")
        case LAST => Some("CURL_TLSAUTH_LAST")
        case _    => None

    extension (a: CurlTlsAuth)
      inline def &(b: CurlTlsAuth): CurlTlsAuth = a & b
      inline def |(b: CurlTlsAuth): CurlTlsAuth = a | b
      inline def is(b: CurlTlsAuth): Boolean = (a & b) == b

  /**
   * symbols to use with CURLOPT_POSTREDIR.
   *
   * CURL_REDIR_POST_301, CURL_REDIR_POST_302 and CURL_REDIR_POST_303
   * can be bitwise ORed so that
   *
   *  CURL_REDIR_POST_301 | CURL_REDIR_POST_302
   *   | CURL_REDIR_POST_303 == CURL_REDIR_POST_ALL
   */
  opaque type CurlOpt_PostRedir = UInt
  object CurlOpt_PostRedir:
    given Tag[CurlOpt_PostRedir] = Tag.UInt

    inline def define(inline a: Long): CurlOpt_PostRedir = a.toUInt

    val GET_ALL = define(0)
    val POST_301 = define(1)
    val POST_302 = define(2)
    val POST_303 = define(4)
    val POST_ALL = POST_301 | POST_302 | POST_303

  // TODO: add typedef enum `curl_TimeCond`

  // TODO:
  //
  // 1. add typedef struct `curl_mime`
  // 2. add typedef struct `curl_mimepart`
  // 3. add define symbol `CURLMIMEOPT_` (CURLOPT_MIME_OPTIONS)
  // 4. add func `curl_mime_init`
  // 5. add func `curl_mime_free`
  // 6. add func `curl_mime_addpart`
  // 7. add func `curl_mime_name`
  // 8. add func `curl_mime_filename`
  // 9. add func `curl_mime_type`
  // 10. add func `curl_mime_encoder`
  // 11. add func `curl_mime_data`
  // 12. add func `curl_mime_filedata`
  // 13. add func `curl_mime_data_cb`
  // 14. add func `curl_mime_subparts`
  // 15. add func `curl_mime_headers`
  // 16. add typedef enum `CURLformoption`
  // 17. add struct `curl_forms`
  // 18. add typedef enum `CURLFORMcode`

  /**
   * NAME curl_version()
   *
   * DESCRIPTION
   *
   * Returns a static ascii string of the libcurl version.
   */
  def curl_version(): CString = extern

  /**
   * curl_easy_escape is relocated to [[easy]] easyEscape
   */

  /**
   * curl_easy_unescape is relocated to [[easy]] easyUnescape
   */

  /**
   * NAME curl_global_init()
   *
   * DESCRIPTION
   *
   * curl_global_init() should be invoked exactly once for each application that uses libcurl and
   * before any call of other libcurl functions.
   *
   * This function is thread-safe if CURL_VERSION_THREADSAFE is set in the
   * curl_version_info_data.features flag (fetch by curl_version_info()).
   */
  def curl_global_init(flags: CURL_GLOBAL): CurlCode = extern

  /**
   * NAME curl_global_init_mem()
   *
   * DESCRIPTION
   *
   * curl_global_init() or curl_global_init_mem() should be invoked exactly once for each
   * application that uses libcurl. This function can be used to initialize libcurl and set user
   * defined memory management callback functions. Users can implement memory management routines to
   * check for memory leaks, check for mis-use of the curl library etc. User registered callback
   * routines will be invoked by this library instead of the system memory management routines like
   * malloc, free etc.
   */

  /**
   * NAME curl_global_cleanup()
   *
   * DESCRIPTION
   *
   * curl_global_cleanup() should be invoked exactly once for each application that uses libcurl
   */
  def curl_global_cleanup(): Unit = extern

  /**
   * NAME curl_global_trace()
   *
   * DESCRIPTION
   *
   * curl_global_trace() can be invoked at application start to configure which components in curl
   * should participate in tracing.
   *
   * This function is thread-safe if CURL_VERSION_THREADSAFE is set in the
   * curl_version_info_data.features flag (fetch by curl_version_info()).
   */
  def curl_global_trace(config: CString): CurlCode = extern

  /** linked-list structure for the CURLOPT_QUOTE option (and other) */
  @name("curl_slist")
  opaque type CurlSlist = CStruct2[
    /** data */
    CString,
    /** next */
    Ptr[Byte],
  ]
  object CurlSlist:
    given Tag[CurlSlist] = Tag.materializeCStruct2Tag[CString, Ptr[Byte]]

    def apply(data: CString, next: Ptr[CurlSlist])(using Zone): Ptr[CurlSlist] =
      val ptr = alloc[CurlSlist](1)
      (!ptr).data = data
      (!ptr).next = next
      ptr

    extension (struct: CurlSlist)
      def data: CString = struct._1
      def data_=(value: CString): Unit = !struct.at1 = value
      def next: Ptr[CurlSlist] = struct._2.asInstanceOf[Ptr[CurlSlist]]
      def next_=(value: Ptr[CurlSlist]): Unit = !struct.at2 = value.asInstanceOf[Ptr[Byte]]

  /**
   * NAME curl_global_sslset()
   *
   * DESCRIPTION
   *
   * When built with multiple SSL backends, curl_global_sslset() allows to choose one. This function
   * can only be called once, and it must be called *before* curl_global_init().
   *
   * The backend can be identified by the id (e.g. CURLSSLBACKEND_OPENSSL). The backend can also be
   * specified via the name parameter (passing -1 as id). If both id and name are specified, the
   * name will be ignored. If neither id nor name are specified, the function will fail with
   * CURLSSLSET_UNKNOWN_BACKEND and set the "avail" pointer to the NULL-terminated list of available
   * backends.
   *
   * Upon success, the function returns CURLSSLSET_OK.
   *
   * If the specified SSL backend is not available, the function returns CURLSSLSET_UNKNOWN_BACKEND
   * and sets the "avail" pointer to a NULL-terminated list of available SSL backends.
   *
   * The SSL backend can be set only once. If it has already been set, a subsequent attempt to
   * change it will result in a CURLSSLSET_TOO_LATE.
   */

  @name("curl_ssl_backend")
  opaque type CurlSslBackend = CStruct2[
    /** id */
    CurlSslBackendId,
    /** name */
    CString,
  ]
  object CurlSslBackend:
    given Tag[CurlSslBackend] = Tag.materializeCStruct2Tag[CurlSslBackendId, CString]

    def apply(id: CurlSslBackendId, name: CString)(using Zone): Ptr[CurlSslBackend] =
      val ptr = alloc[CurlSslBackend](1)
      (!ptr).id = id
      (!ptr).name = name
      ptr

    extension (struct: CurlSslBackend)
      def id: CurlSslBackendId = struct._1
      def id_=(value: CurlSslBackendId): Unit = !struct.at1 = value
      def name: CString = struct._2
      def name_=(value: CString): Unit = !struct.at2 = value

  @name("CURLsslset")
  opaque type CurlSslSet = UInt
  object CurlSslSet:
    given Tag[CurlSslSet] = Tag.UInt
    inline def define(inline a: Long): CurlSslSet = a.toUInt

    val OK = define(0)
    val UNKNOWN_BACKEND = define(1)
    val TOO_LATE = define(2)
    val NO_BACKENDS = define(3) /* libcurl was built without any SSL support */

    inline def getName(inline value: CurlSslSet): Option[String] =
      inline value match
        case OK              => Some("CURLSSLSET_OK")
        case UNKNOWN_BACKEND => Some("CURLSSLSET_UNKNOWN_BACKEND")
        case TOO_LATE        => Some("CURLSSLSET_TOO_LATE")
        case NO_BACKENDS     => Some("CURLSSLSET_NO_BACKENDS")
        case _               => None

    extension (a: CurlSslSet)
      inline def &(b: CurlSslSet): CurlSslSet = a & b
      inline def |(b: CurlSslSet): CurlSslSet = a | b
      inline def is(b: CurlSslSet): Boolean = (a & b) == b

  def curl_global_sslset(
      id: CurlSslBackendId,
      name: CString,
      avail: Ptr[Ptr[Ptr[CurlSslBackend]]],
  ): CurlSslSet = extern

  /**
   * NAME curl_slist_append()
   *
   * DESCRIPTION
   *
   * Appends a string to a linked list. If no list exists, it will be created first. Returns the new
   * list, after appending.
   */
  def curl_slist_append(list: Ptr[CurlSlist], data: CString): Ptr[CurlSlist] = extern

  /**
   * NAME curl_slist_free_all()
   *
   * DESCRIPTION
   *
   * free a previously built curl_slist.
   */
  def curl_slist_free_all(list: Ptr[CurlSlist]): Unit = extern

  /**
   * NAME curl_getdate()
   *
   * DESCRIPTION
   *
   * Returns the time, in seconds since 1 Jan 1970 of the time string given in the first argument.
   * The time argument in the second parameter is unused and should be set to NULL.
   */
  def curl_getdate(p: CString, unused: Ptr[time_t]): time_t = extern

  /**
   * info about the certificate chain, for SSL backends that support it. Asked for with
   * CURLOPT_CERTINFO / CURLINFO_CERTINFO
   */
  @name("curl_certinfo")
  opaque type CurlCertInfo = CStruct2[
    /**
     * num_of_certs
     *
     * number of certificates with information
     */
    Int,
    /**
     * certinfo
     *
     * for each index in this array, there's a linked list with textual information for a
     * certificate in the format "name:content". eg "Subject:foo", "Issuer:bar", etc.
     */
    Ptr[Byte],
  ]
  object CurlCertInfo:
    given Tag[CurlCertInfo] = Tag.materializeCStruct2Tag[Int, Ptr[Byte]]

    def apply(num_of_certs: Int, certinfo: Ptr[Ptr[CurlSlist]])(using Zone): Ptr[CurlCertInfo] =
      val ptr = alloc[CurlCertInfo](1)
      (!ptr).num_of_certs = num_of_certs
      (!ptr).certinfo = certinfo
      ptr

    extension (struct: CurlCertInfo)
      def num_of_certs: Int = struct._1
      def num_of_certs_=(value: Int): Unit = !struct.at1 = value
      def certinfo: Ptr[Ptr[CurlSlist]] = struct._2.asInstanceOf[Ptr[Ptr[CurlSlist]]]
      def certinfo_=(value: Ptr[Ptr[CurlSlist]]): Unit = !struct.at2 = value.asInstanceOf[Ptr[Byte]]

  /**
   * Information about the SSL library used and the respective internal SSL handle, which can be
   * used to obtain further information regarding the connection. Asked for with
   * CURLINFO_TLS_SSL_PTR or CURLINFO_TLS_SESSION.
   */
  @name("curl_tlssessioninfo")
  opaque type CurlTlsSessionInfo = CStruct2[
    /** backend */
    CurlSslBackendId,
    /** internals */
    Ptr[Byte],
  ]
  object CurlTlsSessionInfo:
    given Tag[CurlTlsSessionInfo] = Tag.materializeCStruct2Tag[CurlSslBackendId, Ptr[Byte]]

    def apply(backend: CurlSslBackendId, internals: Ptr[Byte])(using
        Zone,
    ): Ptr[CurlTlsSessionInfo] =
      val ptr = alloc[CurlTlsSessionInfo](1)
      (!ptr).backend = backend
      (!ptr).internals = internals
      ptr

    extension (struct: CurlTlsSessionInfo)
      def backend: CurlSslBackendId = struct._1
      def backend_=(value: CurlSslBackendId): Unit = !struct.at1 = value
      def internals: Ptr[Byte] = struct._2
      def internals_=(value: Ptr[Byte]): Unit = !struct.at2 = value

  @name("CURLINFO")
  opaque type CurlInfo = UInt
  object CurlInfo:
    given Tag[CurlInfo] = Tag.UInt

    inline def define(inline a: Long): CurlInfo = a.toUInt

    // By `#define`
    val STRING = define(0x100000)
    val LONG = define(0x200000)
    val DOUBLE = define(0x300000)
    val SLIST = define(0x400000)
    val PTR = define(0x400000)
    val SOCKET = define(0x500000)
    val OFF_T = define(0x600000)
    val MASK = define(0x0fffff)
    val TYPEMASK = define(0xf00000)

    val NONE = define(0)
    val EFFECTIVE_URL = STRING + 1.toUInt
    val RESPONSE_CODE = LONG + 2.toUInt
    val TOTAL_TIME = DOUBLE + 3.toUInt
    val NAMELOOKUP_TIME = DOUBLE + 4.toUInt
    val CONNECT_TIME = DOUBLE + 5.toUInt
    val PRETRANSFER_TIME = DOUBLE + 6.toUInt
    // val SIZE_UPLOAD = DOUBLE + 7.toUInt   // deprecated since 7.55.0
    val SIZE_UPLOAD_T = OFF_T + 7.toUInt
    // val SIZE_DOWNLOAD = define(3145736)  // deprecated since 7.55.0
    val SIZE_DOWNLOAD_T = define(6291464)
    // val SPEED_DOWNLOAD = define(3145737)  // deprecated since 7.55.0
    val SPEED_DOWNLOAD_T = define(6291465)
    // val SPEED_UPLOAD = define(3145738)  // deprecated since 7.55.0
    val SPEED_UPLOAD_T = define(6291466)
    val HEADER_SIZE = define(2097163)
    val REQUEST_SIZE = define(2097164)
    val SSL_VERIFYRESULT = define(2097165)
    val FILETIME = define(2097166)
    val FILETIME_T = define(6291470)
    // val CONTENT_LENGTH_DOWNLOAD = define(3145743)  // deprecated since 7.55.0
    val CONTENT_LENGTH_DOWNLOAD_T = define(6291471)
    // val CONTENT_LENGTH_UPLOAD = define(3145744)  // deprecated since 7.55.0
    val CONTENT_LENGTH_UPLOAD_T = define(6291472)
    val STARTTRANSFER_TIME = define(3145745)
    val CONTENT_TYPE = define(1048594)
    val REDIRECT_TIME = define(3145747)
    val REDIRECT_COUNT = define(2097172)
    val PRIVATE = define(1048597)
    val HTTP_CONNECTCODE = define(2097174)
    val HTTPAUTH_AVAIL = define(2097175)
    val PROXYAUTH_AVAIL = define(2097176)
    val OS_ERRNO = define(2097177)
    val NUM_CONNECTS = define(2097178)
    val SSL_ENGINES = define(4194331)
    val COOKIELIST = define(4194332)
    // val LASTSOCKET = define(2097181)  // deprecated since 7.55.0
    val FTP_ENTRY_PATH = define(1048606)
    val REDIRECT_URL = define(1048607)
    val PRIMARY_IP = define(1048608)
    val APPCONNECT_TIME = define(3145761)
    val CERTINFO = define(4194338)
    val CONDITION_UNMET = define(2097187)
    val RTSP_SESSION_ID = define(1048612)
    val RTSP_CLIENT_CSEQ = define(2097189)
    val RTSP_SERVER_CSEQ = define(2097190)
    val RTSP_CSEQ_RECV = define(2097191)
    val PRIMARY_PORT = define(2097192)
    val LOCAL_IP = define(1048617)
    val LOCAL_PORT = define(2097194)
    // val TLS_SESSION = define(4194347)  // deprecated since 7.55.0
    val ACTIVESOCKET = define(5242924)
    val TLS_SSL_PTR = define(4194349)
    val HTTP_VERSION = define(2097198)
    val PROXY_SSL_VERIFYRESULT = define(2097199)
    // val PROTOCOL = define(2097200)  // deprecated since 7.55.0
    val SCHEME = define(1048625)
    val TOTAL_TIME_T = define(6291506)
    val NAMELOOKUP_TIME_T = define(6291507)
    val CONNECT_TIME_T = define(6291508)
    val PRETRANSFER_TIME_T = define(6291509)
    val STARTTRANSFER_TIME_T = define(6291510)
    val REDIRECT_TIME_T = define(6291511)
    val APPCONNECT_TIME_T = define(6291512)
    val RETRY_AFTER = define(6291513)
    val EFFECTIVE_METHOD = define(1048634)
    val PROXY_ERROR = define(2097211)
    val REFERER = define(1048636)
    val CAINFO = define(1048637)
    val CAPATH = define(1048638)
    val XFER_ID = define(6291519)
    val CONN_ID = define(6291520)
    val LASTONE = define(64)

    /** alias */
    val HTTP_CODE = RESPONSE_CODE

    inline def getName(inline value: CurlInfo): Option[String] =
      inline value match
        case NONE             => Some("CURLINFO_NONE")
        case EFFECTIVE_URL    => Some("CURLINFO_EFFECTIVE_URL")
        case RESPONSE_CODE    => Some("CURLINFO_RESPONSE_CODE")
        case TOTAL_TIME       => Some("CURLINFO_TOTAL_TIME")
        case NAMELOOKUP_TIME  => Some("CURLINFO_NAMELOOKUP_TIME")
        case CONNECT_TIME     => Some("CURLINFO_CONNECT_TIME")
        case PRETRANSFER_TIME => Some("CURLINFO_PRETRANSFER_TIME")
        // case SIZE_UPLOAD               => Some("CURLINFO_SIZE_UPLOAD")
        case SIZE_UPLOAD_T => Some("CURLINFO_SIZE_UPLOAD_T")
        // case SIZE_DOWNLOAD             => Some("CURLINFO_SIZE_DOWNLOAD")
        case SIZE_DOWNLOAD_T => Some("CURLINFO_SIZE_DOWNLOAD_T")
        // case SPEED_DOWNLOAD            => Some("CURLINFO_SPEED_DOWNLOAD")
        case SPEED_DOWNLOAD_T => Some("CURLINFO_SPEED_DOWNLOAD_T")
        // case SPEED_UPLOAD              => Some("CURLINFO_SPEED_UPLOAD")
        case SPEED_UPLOAD_T   => Some("CURLINFO_SPEED_UPLOAD_T")
        case HEADER_SIZE      => Some("CURLINFO_HEADER_SIZE")
        case REQUEST_SIZE     => Some("CURLINFO_REQUEST_SIZE")
        case SSL_VERIFYRESULT => Some("CURLINFO_SSL_VERIFYRESULT")
        case FILETIME         => Some("CURLINFO_FILETIME")
        case FILETIME_T       => Some("CURLINFO_FILETIME_T")
        // case CONTENT_LENGTH_DOWNLOAD   => Some("CURLINFO_CONTENT_LENGTH_DOWNLOAD")
        case CONTENT_LENGTH_DOWNLOAD_T => Some("CURLINFO_CONTENT_LENGTH_DOWNLOAD_T")
        // case CONTENT_LENGTH_UPLOAD     => Some("CURLINFO_CONTENT_LENGTH_UPLOAD")
        case CONTENT_LENGTH_UPLOAD_T => Some("CURLINFO_CONTENT_LENGTH_UPLOAD_T")
        case STARTTRANSFER_TIME      => Some("CURLINFO_STARTTRANSFER_TIME")
        case CONTENT_TYPE            => Some("CURLINFO_CONTENT_TYPE")
        case REDIRECT_TIME           => Some("CURLINFO_REDIRECT_TIME")
        case REDIRECT_COUNT          => Some("CURLINFO_REDIRECT_COUNT")
        case PRIVATE                 => Some("CURLINFO_PRIVATE")
        case HTTP_CONNECTCODE        => Some("CURLINFO_HTTP_CONNECTCODE")
        case HTTPAUTH_AVAIL          => Some("CURLINFO_HTTPAUTH_AVAIL")
        case PROXYAUTH_AVAIL         => Some("CURLINFO_PROXYAUTH_AVAIL")
        case OS_ERRNO                => Some("CURLINFO_OS_ERRNO")
        case NUM_CONNECTS            => Some("CURLINFO_NUM_CONNECTS")
        case SSL_ENGINES             => Some("CURLINFO_SSL_ENGINES")
        case COOKIELIST              => Some("CURLINFO_COOKIELIST")
        // case LASTSOCKET                => Some("CURLINFO_LASTSOCKET")
        case FTP_ENTRY_PATH   => Some("CURLINFO_FTP_ENTRY_PATH")
        case REDIRECT_URL     => Some("CURLINFO_REDIRECT_URL")
        case PRIMARY_IP       => Some("CURLINFO_PRIMARY_IP")
        case APPCONNECT_TIME  => Some("CURLINFO_APPCONNECT_TIME")
        case CERTINFO         => Some("CURLINFO_CERTINFO")
        case CONDITION_UNMET  => Some("CURLINFO_CONDITION_UNMET")
        case RTSP_SESSION_ID  => Some("CURLINFO_RTSP_SESSION_ID")
        case RTSP_CLIENT_CSEQ => Some("CURLINFO_RTSP_CLIENT_CSEQ")
        case RTSP_SERVER_CSEQ => Some("CURLINFO_RTSP_SERVER_CSEQ")
        case RTSP_CSEQ_RECV   => Some("CURLINFO_RTSP_CSEQ_RECV")
        case PRIMARY_PORT     => Some("CURLINFO_PRIMARY_PORT")
        case LOCAL_IP         => Some("CURLINFO_LOCAL_IP")
        case LOCAL_PORT       => Some("CURLINFO_LOCAL_PORT")
        // case TLS_SESSION               => Some("CURLINFO_TLS_SESSION")
        case ACTIVESOCKET           => Some("CURLINFO_ACTIVESOCKET")
        case TLS_SSL_PTR            => Some("CURLINFO_TLS_SSL_PTR")
        case HTTP_VERSION           => Some("CURLINFO_HTTP_VERSION")
        case PROXY_SSL_VERIFYRESULT => Some("CURLINFO_PROXY_SSL_VERIFYRESULT")
        // case PROTOCOL                  => Some("CURLINFO_PROTOCOL")
        case SCHEME               => Some("CURLINFO_SCHEME")
        case TOTAL_TIME_T         => Some("CURLINFO_TOTAL_TIME_T")
        case NAMELOOKUP_TIME_T    => Some("CURLINFO_NAMELOOKUP_TIME_T")
        case CONNECT_TIME_T       => Some("CURLINFO_CONNECT_TIME_T")
        case PRETRANSFER_TIME_T   => Some("CURLINFO_PRETRANSFER_TIME_T")
        case STARTTRANSFER_TIME_T => Some("CURLINFO_STARTTRANSFER_TIME_T")
        case REDIRECT_TIME_T      => Some("CURLINFO_REDIRECT_TIME_T")
        case APPCONNECT_TIME_T    => Some("CURLINFO_APPCONNECT_TIME_T")
        case RETRY_AFTER          => Some("CURLINFO_RETRY_AFTER")
        case EFFECTIVE_METHOD     => Some("CURLINFO_EFFECTIVE_METHOD")
        case PROXY_ERROR          => Some("CURLINFO_PROXY_ERROR")
        case REFERER              => Some("CURLINFO_REFERER")
        case CAINFO               => Some("CURLINFO_CAINFO")
        case CAPATH               => Some("CURLINFO_CAPATH")
        case XFER_ID              => Some("CURLINFO_XFER_ID")
        case CONN_ID              => Some("CURLINFO_CONN_ID")
        case LASTONE              => Some("CURLINFO_LASTONE")
        case HTTP_CODE            => Some("CURLINFO_RESPONSE_CODE")
        case _                    => None

    extension (a: CurlInfo)
      inline def +(b: CurlInfo): CurlInfo = a + b
      inline def &(b: CurlInfo): CurlInfo = a & b
      inline def |(b: CurlInfo): CurlInfo = a | b
      inline def is(b: CurlInfo): Boolean = (a & b) == b

  @name("curl_closepolicy")
  opaque type CurlClosePolicy = UInt
  object CurlClosePolicy:
    given Tag[CurlClosePolicy] = Tag.UInt

    inline def define(inline a: Long): CurlClosePolicy = a.toUInt

    val NONE = define(0) // no purpose since curl 7.57.0
    val OLDEST = define(1)
    val LEAST_RECENTLY_USED = define(2)
    val LEAST_TRAFFIC = define(3)
    val SLOWEST = define(4)
    val CALLBACK = define(5)
    val LAST = define(6)

    inline def getName(inline value: CurlClosePolicy): Option[String] =
      inline value match
        case NONE                => Some("CURLCLOSEPOLICY_NONE")
        case OLDEST              => Some("CURLCLOSEPOLICY_OLDEST")
        case LEAST_RECENTLY_USED => Some("CURLCLOSEPOLICY_LEAST_RECENTLY_USED")
        case LEAST_TRAFFIC       => Some("CURLCLOSEPOLICY_LEAST_TRAFFIC")
        case SLOWEST             => Some("CURLCLOSEPOLICY_SLOWEST")
        case CALLBACK            => Some("CURLCLOSEPOLICY_CALLBACK")
        case LAST                => Some("CURLCLOSEPOLICY_LAST")
        case _                   => None

    extension (a: CurlClosePolicy)
      inline def &(b: CurlClosePolicy): CurlClosePolicy = a & b
      inline def |(b: CurlClosePolicy): CurlClosePolicy = a | b
      inline def is(b: CurlClosePolicy): Boolean = (a & b) == b

  @name("curl_global_flag")
  opaque type CURL_GLOBAL = Long
  object CURL_GLOBAL:
    given Tag[CURL_GLOBAL] = Tag.Long

    inline def define(inline a: Long): CURL_GLOBAL = a.toLong

    val SSL = define(1 << 0) // no purpose since curl 7.57.0
    val WIN32 = define(1 << 1)
    val ALL = SSL | WIN32
    val NOTHING = define(0)
    val DEFAULT = SSL
    val ACK_EINTR = define(1 << 2)

    extension (a: CURL_GLOBAL)
      inline def &(b: CURL_GLOBAL): CURL_GLOBAL = a & b
      inline def |(b: CURL_GLOBAL): CURL_GLOBAL = a | b
      inline def is(b: CURL_GLOBAL): Boolean = (a & b) == b

  // TODO:
  //
  // 1. add typedef enum `curl_lock_data`
  // 2. add typedef enum `curl_lock_access`
  // 3. add func `curl_lock_function`
  // 4. add func `curl_unlock_function`
  // 5. add typedef enum `CURLSHcode`
  // 6. add typedef enum `CURLSHoption`
  // 7. add func `curl_share_init`
  // 8. add func `curl_share_setopt`
  // 9. add func `curl_share_cleanup`

  // TODO:
  //
  // 1. add typedef enum `CURLversion`
  // 2. add struct `curl_version_info_data`
  // 3. add define symbols `CURL_VERSION_*`
  // 4. add func `curl_version_info`

  /**
   * curl_easy_strerror is relocated to [[easy]] easyStrError
   */

  // TODO: add func `curl_share_strerror`

  /**
   * curl_easy_pause is relocated to [[easy]] easyPause
   */

  /**
   * define symbols `CURLPAUSE_*` is relocated to [[easy]] CurlPause
   */

  //
  // Import data type definitions from `curl/system.h`
  //

  @name("curl_off_t")
  type CurlOff = Size

  @name("curl_socklen_t")
  type CurlSocklen = socklen_t
