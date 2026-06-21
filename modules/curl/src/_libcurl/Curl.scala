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
package snhttp.experimental.curl
package _libcurl

import scala.scalanative.unsafe.alloc
import scala.scalanative.unsafe.{
  CFuncPtr2,
  CFuncPtr3,
  CFuncPtr4,
  CFuncPtr5,
  CInt,
  CLong,
  CSize,
  CString,
  CStruct2,
  CStruct5,
  CStruct14,
  CVoidPtr,
  Ptr,
  Tag,
  UnsafeRichInt,
  UnsafeRichLong,
  Zone,
}
import scala.scalanative.unsigned.{UInt, UnsignedRichLong}
import scala.scalanative.posix.sys.socket
import scala.scalanative.posix.sys.socket.{socklen_t, sockaddr}

import _root_.snhttp.experimental.curl._internal.{
  _BindgenEnumCInt,
  _BindgenEnumCLong,
  _BindgenEnumCSize,
}
import _root_.snhttp.experimental.curl._libcurl.System.CurlOff

// scalafmt: { maxColumn = 120, align.preset = most }
private[curl] object Curl:

  // known as "CURL"
  opaque type CurlHandle = CVoidPtr // curl easy handle
  object CurlHandle:
    given Tag[CurlHandle] = Tag.materializePtrWildcard
  end CurlHandle

  // known as "CURLSH"
  opaque type CurlShareHandle = CVoidPtr // curl share handle
  object CurlShareHandle:
    given Tag[CurlShareHandle] = Tag.materializePtrWildcard
  end CurlShareHandle

  // known as "curl_socket_t"
  opaque type CurlSocket = Int
  object CurlSocket extends _BindgenEnumCInt[CurlSocket]:

    given Tag[CurlSocket] = Tag.Int

    private inline def define(inline v: Int): CurlSocket = v

    final val BAD     = define(-1) // FIXME: should be extern in windows platform
    final val TIMEOUT = CurlSocket.BAD

    inline def fromSocket(s: CInt): CurlSocket         = define(s)
    inline def fromFileDescriptor(s: CInt): CurlSocket = define(s)

  end CurlSocket

  /**
   * enum for the different supported SSL backends
   */
  // known as "curl_sslbackend"
  opaque type CurlSslBackendId = Int
  object CurlSslBackendId extends _BindgenEnumCInt[CurlSslBackendId]:

    given Tag[CurlSslBackendId] = Tag.Int

    private inline def define(inline a: Int): CurlSslBackendId = a

    // Doesn't include the following deprecated backends
    // NSS, GSKIT, AXTLS, MESALINK

    val NONE            = define(0)
    val OPENSSL         = define(1)
    val GNUTLS          = define(2)
    val NSS             = define(3)  // deprecated 8.3.0
    val OBSOLETE4       = define(4)  // obsoleted
    val GSKIT           = define(5)  // deprecated 8.3.0
    val POLARSSL        = define(6)  // deprecated 7.69.0
    val WOLFSSL         = define(7)
    val SCHANNEL        = define(8)
    val SECURETRANSPORT = define(9)  // deprecated 8.15.0
    val AXTLS           = define(10) // deprecated 7.61.0
    val MBEDTLS         = define(11)
    val MESALINK        = define(12) // deprecated 7.82.0
    val BEARSSL         = define(13) // deprecated 8.15.0
    val RUSTLS          = define(14)

    val AWSLC     = OPENSSL
    val BORINGSSL = OPENSSL
    val LIBRESSL  = OPENSSL

    def getname(value: CurlSslBackendId): String =
      value match
        case NONE            => "CURLSSLBACKEND_NONE"
        case OPENSSL         => "CURLSSLBACKEND_OPENSSL"
        case GNUTLS          => "CURLSSLBACKEND_GNUTLS"
        case WOLFSSL         => "CURLSSLBACKEND_WOLFSSL"
        case SCHANNEL        => "CURLSSLBACKEND_SCHANNEL"
        case SECURETRANSPORT => "CURLSSLBACKEND_SECURETRANSPORT"
        case MBEDTLS         => "CURLSSLBACKEND_MBEDTLS"
        case BEARSSL         => "CURLSSLBACKEND_BEARSSL"
        case RUSTLS          => "CURLSSLBACKEND_RUSTLS"
        case AWSLC           => "CURLSSLBACKEND_AWSLC"
        case BORINGSSL       => "CURLSSLBACKEND_BORINGSSL"
        case LIBRESSL        => "CURLSSLBACKEND_LIBRESSL"

    extension (a: CurlSslBackendId)
      inline def &(b: CurlSslBackendId): CurlSslBackendId = a & b
      inline def |(b: CurlSslBackendId): CurlSslBackendId = a | b
      inline def is(b: CurlSslBackendId): Boolean         = (a & b) == b

  end CurlSslBackendId

  opaque type CurlFollow = CLong
  object CurlFollow extends _BindgenEnumCLong[CurlFollow]:

    given Tag[CurlFollow] = Tag.Size

    private inline def define(inline a: Long): CurlFollow = a.toSize

    /* default:    disabled */
    val DISABLED = define(0L)
    /* bits for the CURLOPT_FOLLOWLOCATION option */
    val ALL = define(1L)
    /* Do not use the custom method in the follow-up request if the HTTP code instructs so (301, 302, 303). */
    val OBEYCODE = define(2L)
    /* Only use the custom method in the first request, always reset in the next */
    val FIRSTONLY = define(3L)

    implicit class RichCurlFollow(value: CurlFollow) extends AnyVal:
      inline def getname: String =
        value match
          case DISABLED  => "CURLFOLLOW_DISABLED"
          case ALL       => "CURLFOLLOW_ALL"
          case OBEYCODE  => "CURLFOLLOW_OBEYCODE"
          case FIRSTONLY => "CURLFOLLOW_FIRSTONLY"

  end CurlFollow

  // known as "curl_httppost"
  opaque type CurlHttpPost = CStruct14[
    /**
     * next: next entry in the list
     */
    CVoidPtr, // Ptr[CurlHttpPost],
    /**
     * name: pointer to allocated name
     */
    CString,
    /**
     * namelength: length of name length
     */
    CLong,
    /**
     * contents: pointer to allocated data contents
     */
    CString,
    /**
     * contentslength: length of contents field, see also `CURL_HTTPPOST_LARGE`
     */
    CLong,
    /**
     * buffer: pointer to allocated buffer contents
     */
    CString,
    /**
     * bufferlength: length of buffer field
     */
    CLong,
    /**
     * contenttype: Content-Type
     */
    CString,
    /**
     * contentheader: list of extra headers for this form
     */
    Ptr[CurlSlist],
    /**
     * more: if one field name has more than one file, this link should link to following files
     */
    CVoidPtr, // Ptr[CurlHttpPost],
    /**
     * flags: curl http post flags
     */
    CurlHttpPostFlag,
    /**
     * showfilename: The filename to show. If not set, the actual filename will be used (if this is a file part)
     */
    CString,
    /**
     * userp: custom pointer used for HTTPPOST_CALLBACK posts
     */
    CVoidPtr,
    /**
     * contentlen: alternative length of contents field. Used if `CURL_HTTPPOST_LARGE` is set. Added in curl 7.46.0
     */
    CurlOff,
  ]
  object CurlHttpPost:

    given Tag[CVoidPtr]     = Tag.materializePtrWildcard
    given Tag[CurlHttpPost] = Tag.materializeCStruct14Tag[
      /**
       * just help to split lines by formatter
       */
      CVoidPtr, // Ptr[CurlHttpPost],
      CString,
      CLong,
      CString,
      CLong,
      CString,
      CLong,
      CString,
      Ptr[CurlSlist],
      CVoidPtr, // Ptr[CurlHttpPost],
      CurlHttpPostFlag,
      CString,
      CVoidPtr,
      CurlOff,
    ]

    // def apply(
    //     next: Ptr[CurlHttpPost],
    //     name: CString,
    //     namelength: CLong,
    //     contents: CString,
    //     contentslength: CLong,
    //     buffer: CString,
    //     bufferlength: CLong,
    //     contenttype: CString,
    //     contentheader: Ptr[Byte],
    //     more: Ptr[Byte],
    //     flags: CurlHttpPostFlag,
    //     showfilename: CString,
    //     userp: CVoidPtr,
    //     contentlen: CurlOff,
    // )(using Zone): Ptr[CurlHttpPost] =
    //   val ptr = alloc[CurlHttpPost](1)
    //   (!ptr).next = next
    //   (!ptr).name = name
    //   (!ptr).namelength = namelength
    //   (!ptr).contents = contents
    //   (!ptr).contentslength = contentslength
    //   (!ptr).buffer = buffer
    //   (!ptr).bufferlength = bufferlength
    //   (!ptr).contenttype = contenttype
    //   (!ptr).contentheader = contentheader
    //   (!ptr).more = more
    //   (!ptr).flags = flags
    //   (!ptr).showfilename = showfilename
    //   (!ptr).userp = userp
    //   (!ptr).contentlen = contentlen
    //   ptr

    // extension (struct: CurlHttpPost)
    //   inline def next: Ptr[CurlHttpPost] = struct._1.asInstanceOf[Ptr[CurlHttpPost]]
    //   inline def next_=(value: Ptr[CurlHttpPost]): Unit = !struct.at1 = value.asInstanceOf[Ptr[Byte]]
    //   inline def name: CString = struct._2
    //   inline def name_=(value: CString): Unit = !struct.at2 = value
    //   inline def namelength: CLong = struct._3
    //   inline def namelength_=(value: CLong): Unit = !struct.at3 = value
    //   inline def contents: CString = struct._4
    //   inline def contents_=(value: CString): Unit = !struct.at4 = value
    //   inline def contentslength: CLong = struct._5
    //   inline def contentslength_=(value: CLong): Unit = !struct.at5 = value
    //   inline def buffer: CString = struct._6
    //   inline def buffer_=(value: CString): Unit = !struct.at6 = value
    //   inline def bufferlength: CLong = struct._7
    //   inline def bufferlength_=(value: CLong): Unit = !struct.at7 = value
    //   inline def contenttype: CString = struct._8
    //   inline def contenttype_=(value: CString): Unit = !struct.at8 = value
    //   inline def contentheader: Ptr[Byte] = struct._9.asInstanceOf[Ptr[Byte]]
    //   inline def contentheader_=(value: Ptr[Byte]): Unit = !struct.at9 = value.asInstanceOf[Ptr[Byte]]
    //   inline def more: Ptr[Byte] = struct._10.asInstanceOf[Ptr[Byte]]
    //   inline def more_=(value: Ptr[Byte]): Unit = !struct.at10 = value.asInstanceOf[Ptr[Byte]]
    //   inline def flags: CurlHttpPostFlag = struct._11
    //   inline def flags_=(value: CurlHttpPostFlag): Unit = !struct.at11 = value
    //   inline def showfilename: CString = struct._12
    //   inline def showfilename_=(value: CString): Unit = !struct.at12 = value
    //   inline def userp: CVoidPtr = struct._13
    //   inline def userp_=(value: CVoidPtr): Unit = !struct.at13 = value
    //   inline def contentlen: CurlOff = struct._14
    //   inline def contentlen_=(value: CurlOff): Unit = !struct.at14 = value

  end CurlHttpPost

  /**
   * known as defined `CURL_HTTPPOST_*` symbols
   */
  opaque type CurlHttpPostFlag = Int
  object CurlHttpPostFlag extends _BindgenEnumCInt[CurlHttpPostFlag]:

    given Tag[CurlHttpPostFlag] = Tag.Int

    private inline def define(inline a: Int): CurlHttpPostFlag = a

    /* specified content is a file name */
    val FILENAME = define(1 << 0)
    /* specified content is a file name */
    val READFILE = define(1 << 1)
    /* name is only stored pointer do not free in formfree */
    val PTRNAME = define(1 << 2)
    /* contents is only stored pointer do not free in formfree */
    val PTRCONTENTS = define(1 << 3)
    /* upload file from buffer */
    val BUFFER = define(1 << 4)
    /* upload file from pointer contents */
    val PTRBUFFER = define(1 << 5)
    /* upload file contents by using the regular read callback to get the data
     * and pass the given pointer as custom pointer */
    val CALLBACK = define(1 << 6)
    /* use size in 'contentlen', added in 7.46.0 */
    val LARGE = define(1 << 7)

  end CurlHttpPostFlag

  // val CURL_PROGRESSFUNC_CONTINUE = 0x10000001

  opaque type CurlXferInfoCallback = CFuncPtr5[
    /** clientp */
    CVoidPtr,
    /** dltotal */
    CurlOff,
    /** dlnow */
    CurlOff,
    /** ultotal */
    CurlOff,
    /** ulnow */
    CurlOff,
    /** return */
    CInt,
  ]

  // TODO: Fix with `extern` mark
  val CURL_MAX_READ_SIZE   = (10 * 1024 * 1024).toSize
  val CURL_MAX_WRITE_SIZE  = (16 * 1024).toSize
  val CURL_MAX_HTTP_HEADER = (100 * 1024).toSize

  /**
   * known as "CURL_WRITEFUNC_*"
   *
   * This is a magic return code for the write callback
   */
  type CurlWriteFuncRet = CSize
  object CurlWriteFuncRet extends _BindgenEnumCSize[CurlWriteFuncRet]:

    given Tag[CurlWriteFuncRet] = Tag.USize

    private inline def define(inline a: Long): CurlWriteFuncRet = a.toUSize

    /* pause receiving on the current transfer. */
    val PAUSE = define(0x10000001)
    /* signal an error from the callback. */
    val ERROR = define(0xffffffff)

  end CurlWriteFuncRet

  /** known as "curl_write_callback" */
  opaque type CurlWriteCallback = CFuncPtr4[
    /** buffer */
    Ptr[Byte],
    /** size */
    CSize,
    /** nitems */
    CSize,
    /** outstream */
    CVoidPtr,
    /** return */
    CurlWriteFuncRet,
  ]
  object CurlWriteCallback:

    given Tag[CurlWriteCallback] =
      Tag.materializeCFuncPtr4[Ptr[Byte], CSize, CSize, CVoidPtr, CSize]

    inline def fromScalaFunction(
        inline func: (Ptr[Byte], CSize, CSize, CVoidPtr) => CurlWriteFuncRet,
    ): CurlWriteCallback =
      CFuncPtr4.fromScalaFunction(func)

    extension (func: CurlWriteCallback)
      inline def asFuncPtr: CFuncPtr4[Ptr[Byte], CSize, CSize, CVoidPtr, CurlWriteFuncRet] =
        func

  end CurlWriteCallback

  /** known as "curl_header_callback" */
  opaque type CurlHeaderCallback = CFuncPtr4[
    /** buffer */
    Ptr[Byte],
    /** size */
    CSize,
    /** nitems */
    CSize,
    /** outstream */
    CVoidPtr,
    /** return */
    CurlWriteFuncRet,
  ]
  object CurlHeaderCallback:

    given Tag[CurlHeaderCallback] =
      Tag.materializeCFuncPtr4[Ptr[Byte], CSize, CSize, CVoidPtr, CSize]

    inline def fromScalaFunction(
        inline func: (Ptr[Byte], CSize, CSize, CVoidPtr) => CurlWriteFuncRet,
    ): CurlWriteCallback =
      CFuncPtr4.fromScalaFunction(func)

    extension (func: CurlWriteCallback)
      inline def asFuncPtr: CFuncPtr4[Ptr[Byte], CSize, CSize, CVoidPtr, CurlWriteFuncRet] =
        func

  end CurlHeaderCallback

  /** This callback will be called when a new resolver request is made */
  opaque type CurlResolverStartCallback = CFuncPtr3[
    /** resolver_state */
    CVoidPtr,
    /** hostname */
    CString,
    /** port */
    CInt,
    /** return */
    CInt,
  ]
  object CurlResolverStartCallback:

    given Tag[CurlResolverStartCallback] =
      Tag.materializeCFuncPtr3[CVoidPtr, CString, CInt, CInt]

    inline def fromScalaFunction(
        inline func: (CVoidPtr, CString, CInt) => CInt,
    ): CurlResolverStartCallback =
      CFuncPtr3.fromScalaFunction(func)

    extension (func: CurlResolverStartCallback) //
      inline def asFuncPtr: CFuncPtr3[CVoidPtr, CString, CInt, CInt] = func

  end CurlResolverStartCallback

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

  /**
   * This is a return code for the read callback that, when returned, will signal libcurl to immediately abort the
   * current transfer.
   */
  val CURL_READFUNC_ABORT = 0x10000000

  /**
   * This is a return code for the read callback that, when returned, will signal libcurl to pause sending data on the
   * current transfer.
   */
  val CURL_READFUNC_PAUSE = 0x10000001

  /**
   * Return code for when the trailing headers' callback has terminated without any errors
   */
  val CURL_TRAILERFUNC_OK = 0

  /**
   * Return code for when was an error in the trailing header's list and we want to abort the request
   */
  val CURL_TRAILERFUNC_ABORT = 1

  // known as "curl_read_callback"
  opaque type CurlReadCallback = CFuncPtr4[
    /** buffer */
    CString,
    /** size */
    CSize,
    /** nitems */
    CSize,
    /** instream */
    CVoidPtr,
    /** return */
    CSize,
  ]
  object CurlReadCallback:

    given Tag[CurlReadCallback] =
      Tag.materializeCFuncPtr4[CString, CSize, CSize, CVoidPtr, CSize]

    inline def fromScalaFunction(
        inline func: (CString, CSize, CSize, CVoidPtr) => CSize,
    ): CurlReadCallback =
      CFuncPtr4.fromScalaFunction(func)

    extension (func: CurlReadCallback)
      inline def asFuncPtr: CFuncPtr4[CString, CSize, CSize, CVoidPtr, CSize] =
        func

  end CurlReadCallback

  // known as "curl_trailer_callback"
  opaque type CurlTrailerCallback = CFuncPtr2[
    /** instream */
    Ptr[CurlSlist],
    /** userdata */
    CVoidPtr,
    /** return */
    CInt,
  ]

  // known as enum "curlsocktype"
  opaque type CurlSockType = Int
  object CurlSockType extends _BindgenEnumCInt[CurlSockType]:

    given Tag[CurlSockType] = Tag.Int

    private inline def define(inline a: Int): CurlSockType = a

    /* socket created for a specific IP connection */
    val IPCXN = define(0)
    /* socket created by accept() call */
    val ACCEPT = define(1)
    /* never use */
    val LAST = define(2)

    def getname(value: CurlSockType): Option[String] =
      value match
        case IPCXN  => Some("CURLSOCKTYPE_IPCXN")
        case ACCEPT => Some("CURLSOCKTYPE_ACCEPT")
        case LAST   => Some("CURLSOCKTYPE_LAST")
        case _      => None

    extension (a: CurlSockType)
      inline def &(b: CurlSockType): CurlSockType = a & b
      inline def |(b: CurlSockType): CurlSockType = a | b
      inline def is(b: CurlSockType): Boolean     = (a & b) == b

  end CurlSockType

  /**
   * The return code from the sockopt_callback can signal information back to libcurl:
   */
  opaque type CurlSockOpt = Int
  object CurlSockOpt extends _BindgenEnumCInt[CurlSockOpt]:

    given Tag[CurlSockOpt] = Tag.Int

    private inline def define(inline a: Int): CurlSockOpt = a

    val OK = define(0)

    /** causes libcurl to abort and return CURLE_ABORTED_BY_CALLBACK */
    val ERROR = define(1)

  end CurlSockOpt

  // known as "curl_sockopt_callback"
  opaque type CurlSockOptCallback =
    CFuncPtr3[
      /** clientp */
      CVoidPtr,
      /** curlfd */
      CurlSocket,
      /** purpose */
      CurlSockType,
      /** return */
      CurlSockOpt,
    ]
  object CurlSockOptCallback:

    given Tag[CurlSockOptCallback] =
      Tag.materializeCFuncPtr3[CVoidPtr, CurlSocket, CurlSockType, CurlSockOpt]

    inline def fromScalaFunction(
        inline func: (CVoidPtr, CurlSocket, CurlSockType) => CurlSockOpt,
    ): CurlSockOptCallback =
      CFuncPtr3.fromScalaFunction(func)

    extension (func: CurlSockOptCallback)
      inline def asFuncPtr: CFuncPtr3[CVoidPtr, CurlSocket, CurlSockType, CurlSockOpt] =
        func

  end CurlSockOptCallback

  // known as "curl_sockaddr"
  opaque type CurlSockAddr = CStruct5[
    /** family */
    SockAddrFamily,
    /** socktype */
    CurlSockType,
    /** protocol */
    Int,
    /** addrlen */
    socklen_t,
    /** ret */
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
    )(using Zone): Ptr[CurlSockAddr] =
      val ptr = alloc[CurlSockAddr](1)
      (!ptr).family = family
      (!ptr).socktype = socktype
      (!ptr).protocol = protocol
      (!ptr).addrlen = addrlen
      (!ptr).addr = addr
      ptr

    extension (struct: CurlSockAddr)
      inline def family: SockAddrFamily                = struct._1
      inline def family_=(value: SockAddrFamily): Unit = !struct.at1 = value
      inline def socktype: CurlSockType                = struct._2
      inline def socktype_=(value: CurlSockType): Unit = !struct.at2 = value
      inline def protocol: Int                         = struct._3
      inline def protocol_=(value: Int): Unit          = !struct.at3 = value
      inline def addrlen: UInt                         = struct._4
      inline def addrlen_=(value: UInt): Unit          = !struct.at4 = value
      inline def addr: sockaddr                        = struct._5
      inline def addr_=(value: sockaddr): Unit         = !struct.at5 = value

  // known as "curl_opensocket_callback"
  opaque type CurlOpenSocketCallback =
    CFuncPtr3[
      /** clientp */
      CVoidPtr,
      /** purpose */
      CurlSockType,
      /** address */
      Ptr[CurlSockAddr],
      /** return */
      CurlSocket,
    ]
  object CurlOpenSocketCallback:

    given Tag[CurlOpenSocketCallback] =
      Tag.materializeCFuncPtr3[CVoidPtr, CurlSockType, Ptr[CurlSockAddr], CurlSocket]

    inline def fromScalaFunction(
        inline func: (CVoidPtr, CurlSockType, Ptr[CurlSockAddr]) => CurlSocket,
    ): CurlOpenSocketCallback =
      CFuncPtr3.fromScalaFunction(func)

    extension (func: CurlOpenSocketCallback) //
      inline def asFuncPtr: CFuncPtr3[CVoidPtr, CurlSockType, Ptr[CurlSockAddr], CurlSocket] =
        func

  end CurlOpenSocketCallback

  // known as "curl_closesocket_callback"
  opaque type CurlCloseSocketCallback = CFuncPtr2[CVoidPtr, CurlSocket, Int]
  object CurlCloseSocketCallback:

    given Tag[CurlCloseSocketCallback] =
      Tag.materializeCFuncPtr2[CVoidPtr, CurlSocket, Int]

    inline def fromScalaFunction(
        inline func: (CVoidPtr, CurlSocket) => Int,
    ): CurlCloseSocketCallback =
      CFuncPtr2.fromScalaFunction(func)

    extension (func: CurlCloseSocketCallback) //
      inline def asFuncPtr: CFuncPtr2[CVoidPtr, CurlSocket, Int] = func

  end CurlCloseSocketCallback

  // known as enum "curlioerr"
  opaque type CurlIoErr = Int
  object CurlIoErr extends _BindgenEnumCInt[CurlIoErr]:

    given Tag[CurlIoErr] = Tag.Int

    private inline def define(inline a: Int): CurlIoErr = a

    /* I/O operation successful */
    val OK = define(0)
    /* command was unknown to callback */
    val UNKNOWNCMD = define(1)
    /* failed to restart the read */
    val FAILRESTART = define(2)
    /* never use */
    val LAST = define(3)

    def getname(value: CurlIoErr): Option[String] =
      value match
        case OK          => Some("CURLIOE_OK")
        case UNKNOWNCMD  => Some("CURLIOE_UNKNOWNCMD")
        case FAILRESTART => Some("CURLIOE_FAILRESTART")
        case LAST        => Some("CURLIOE_LAST")
        case _           => None

    extension (a: CurlIoErr)
      inline def &(b: CurlIoErr): CurlIoErr = a & b
      inline def |(b: CurlIoErr): CurlIoErr = a | b
      inline def is(b: CurlIoErr): Boolean  = (a & b) == b

  end CurlIoErr

  // known as enum "curliocmd"
  opaque type CurlIoCmd = Int
  object CurlIoCmd extends _BindgenEnumCInt[CurlIoCmd]:

    given Tag[CurlIoCmd]                                = Tag.Int
    private inline def define(inline a: Int): CurlIoCmd = a

    /* no operation */
    val NOP = define(0)
    /* restart the read stream from start */
    val RESTARTREAD = define(1)
    /* never use */
    val LAST = define(2)

    def getname(value: CurlIoCmd): Option[String] =
      value match
        case NOP         => Some("CURLIOCMD_NOP")
        case RESTARTREAD => Some("CURLIOCMD_RESTARTREAD")
        case LAST        => Some("CURLIOCMD_LAST")
        case _           => None

    extension (a: CurlIoCmd)
      inline def &(b: CurlIoCmd): CurlIoCmd = a & b
      inline def |(b: CurlIoCmd): CurlIoCmd = a | b
      inline def is(b: CurlIoCmd): Boolean  = (a & b) == b

  end CurlIoCmd

  // known as "curl_ioctl_callback"
  opaque type CurlIoCtlCallback = CFuncPtr3[
    /** handle */
    Ptr[CurlHandle],
    /** cmd */
    CurlIoCmd,
    /** clientp */
    CVoidPtr,
    /** return */
    CurlIoErr,
  ]
  object CurlIoCtlCallback:

    given Tag[CurlIoCtlCallback] =
      Tag.materializeCFuncPtr3[Ptr[CurlHandle], CurlIoCmd, CVoidPtr, CurlIoErr]

    inline def fromScalaFunction(
        inline func: (Ptr[CurlHandle], CurlIoCmd, CVoidPtr) => CurlIoErr,
    ): CurlIoCtlCallback =
      CFuncPtr3.fromScalaFunction(func)

    extension (func: CurlIoCtlCallback) //
      inline def asFuncPtr: CFuncPtr3[Ptr[CurlHandle], CurlIoCmd, CVoidPtr, CurlIoErr] =
        func

  end CurlIoCtlCallback

  // known as enum curl_infotype
  opaque type CurlInfoType = CInt
  object CurlInfoType extends _BindgenEnumCInt[CurlInfoType]:

    given Tag[CurlInfoType] = Tag.Int

    private inline def define(inline a: Int): CurlInfoType = a

    val TEXT         = define(0)
    val HEADER_IN    = define(1)
    val HEADER_OUT   = define(2)
    val DATA_IN      = define(3)
    val DATA_OUT     = define(4)
    val SSL_DATA_IN  = define(5)
    val SSL_DATA_OUT = define(6)
    val END          = define(7)

    implicit class RichCurlInfoType(value: CurlInfoType) extends AnyVal:
      inline def getname: String =
        value match
          case TEXT         => "CURLINFO_TEXT"
          case HEADER_IN    => "CURLINFO_HEADER_IN"
          case HEADER_OUT   => "CURLINFO_HEADER_OUT"
          case DATA_IN      => "CURLINFO_DATA_IN"
          case DATA_OUT     => "CURLINFO_DATA_OUT"
          case SSL_DATA_IN  => "CURLINFO_SSL_DATA_IN"
          case SSL_DATA_OUT => "CURLINFO_SSL_DATA_OUT"
          case END          => "CURLINFO_END"

  end CurlInfoType

  // known as "curl_debug_callback"
  opaque type CurlDebugCallback = CFuncPtr5[
    /** handle */
    Ptr[CurlHandle],
    /** type */
    CurlInfoType,
    /** data */
    CString,
    /** size */
    CSize,
    /** userptr */
    CVoidPtr,
    /** return */
    CInt,
  ]

  /**
   * All possible error codes from all sorts of curl functions. Future versions may return other values, stay prepared.
   *
   * Always add new return codes last. Never *EVER* remove any. The return codes must remain the same!
   */
  // known as enum "CURLcode"
  opaque type CurlErrCode = Int
  object CurlErrCode extends _BindgenEnumCInt[CurlErrCode]:

    given Tag[CurlErrCode]                   = Tag.Int
    given CanEqual[CurlErrCode, CurlErrCode] = CanEqual.derived

    private inline def define(inline a: Int): CurlErrCode = a

    val OK                       = define(0)
    val UNSUPPORTED_PROTOCOL     = define(1)
    val FAILED_INIT              = define(2)
    val URL_MALFORMAT            = define(3)
    val NOT_BUILT_IN             = define(4)
    val COULDNT_RESOLVE_PROXY    = define(5)
    val COULDNT_RESOLVE_HOST     = define(6)
    val COULDNT_CONNECT          = define(7)
    val WEIRD_SERVER_REPLY       = define(8)
    val REMOTE_ACCESS_DENIED     = define(9)
    val FTP_ACCEPT_FAILED        = define(10)
    val FTP_WEIRD_PASS_REPLY     = define(11)
    val FTP_ACCEPT_TIMEOUT       = define(12)
    val FTP_WEIRD_PASV_REPLY     = define(13)
    val FTP_WEIRD_227_FORMAT     = define(14)
    val FTP_CANT_GET_HOST        = define(15)
    val HTTP2                    = define(16)
    val FTP_COULDNT_SET_TYPE     = define(17)
    val PARTIAL_FILE             = define(18)
    val FTP_COULDNT_RETR_FILE    = define(19)
    val OBSOLETE20               = define(20)
    val QUOTE_ERROR              = define(21)
    val HTTP_RETURNED_ERROR      = define(22)
    val WRITE_ERROR              = define(23)
    val OBSOLETE24               = define(24)
    val UPLOAD_FAILED            = define(25)
    val READ_ERROR               = define(26)
    val OUT_OF_MEMORY            = define(27)
    val OPERATION_TIMEDOUT       = define(28)
    val OBSOLETE29               = define(29)
    val FTP_PORT_FAILED          = define(30)
    val FTP_COULDNT_USE_REST     = define(31)
    val OBSOLETE32               = define(32)
    val RANGE_ERROR              = define(33)
    val HTTP_POST_ERROR          = define(34)
    val SSL_CONNECT_ERROR        = define(35)
    val BAD_DOWNLOAD_RESUME      = define(36)
    val FILE_COULDNT_READ_FILE   = define(37)
    val LDAP_CANNOT_BIND         = define(38)
    val LDAP_SEARCH_FAILED       = define(39)
    val OBSOLETE40               = define(40)
    val FUNCTION_NOT_FOUND       = define(41)
    val ABORTED_BY_CALLBACK      = define(42)
    val BAD_FUNCTION_ARGUMENT    = define(43)
    val OBSOLETE44               = define(44)
    val INTERFACE_FAILED         = define(45)
    val OBSOLETE46               = define(46)
    val TOO_MANY_REDIRECTS       = define(47)
    val UNKNOWN_OPTION           = define(48)
    val SETOPT_OPTION_SYNTAX     = define(49)
    val OBSOLETE50               = define(50)
    val OBSOLETE51               = define(51)
    val GOT_NOTHING              = define(52)
    val SSL_ENGINE_NOTFOUND      = define(53)
    val SSL_ENGINE_SETFAILED     = define(54)
    val SEND_ERROR               = define(55)
    val RECV_ERROR               = define(56)
    val OBSOLETE57               = define(57)
    val SSL_CERTPROBLEM          = define(58)
    val SSL_CIPHER               = define(59)
    val PEER_FAILED_VERIFICATION = define(60)
    val BAD_CONTENT_ENCODING     = define(61)
    val OBSOLETE62               = define(62)
    val FILESIZE_EXCEEDED        = define(63)
    val USE_SSL_FAILED           = define(64)
    val SEND_FAIL_REWIND         = define(65)
    val SSL_ENGINE_INITFAILED    = define(66)
    val LOGIN_DENIED             = define(67)
    val TFTP_NOTFOUND            = define(68)
    val TFTP_PERM                = define(69)
    val REMOTE_DISK_FULL         = define(70)
    val TFTP_ILLEGAL             = define(71)
    val TFTP_UNKNOWNID           = define(72)
    val REMOTE_FILE_EXISTS       = define(73)
    val TFTP_NOSUCHUSER          = define(74)
    val OBSOLETE75               = define(75)
    val OBSOLETE76               = define(76)
    val SSL_CACERT_BADFILE       = define(77)
    val REMOTE_FILE_NOT_FOUND    = define(78)
    val SSH                      = define(79)
    val SSL_SHUTDOWN_FAILED      = define(80)
    val AGAIN                    = define(81)
    val SSL_CRL_BADFILE          = define(82)
    val SSL_ISSUER_ERROR         = define(83)
    val FTP_PRET_FAILED          = define(84)
    val RTSP_CSEQ_ERROR          = define(85)
    val RTSP_SESSION_ERROR       = define(86)
    val FTP_BAD_FILE_LIST        = define(87)
    val CHUNK_FAILED             = define(88)
    val NO_CONNECTION_AVAILABLE  = define(89)
    val SSL_PINNEDPUBKEYNOTMATCH = define(90)
    val SSL_INVALIDCERTSTATUS    = define(91)
    val HTTP2_STREAM             = define(92)
    val RECURSIVE_API_CALL       = define(93)
    val AUTH_ERROR               = define(94)
    val HTTP3                    = define(95)
    val QUIC_CONNECT_ERROR       = define(96)
    val PROXY                    = define(97)
    val SSL_CLIENTCERT           = define(98)
    val UNRECOVERABLE_POLL       = define(99)
    val CURL_LAST                = define(100)

    def fromInt(v: Int) = v match
      case 0   => OK
      case 1   => UNSUPPORTED_PROTOCOL
      case 2   => FAILED_INIT
      case 3   => URL_MALFORMAT
      case 4   => NOT_BUILT_IN
      case 5   => COULDNT_RESOLVE_PROXY
      case 6   => COULDNT_RESOLVE_HOST
      case 7   => COULDNT_CONNECT
      case 8   => WEIRD_SERVER_REPLY
      case 9   => REMOTE_ACCESS_DENIED
      case 10  => FTP_ACCEPT_FAILED
      case 11  => FTP_WEIRD_PASS_REPLY
      case 12  => FTP_ACCEPT_TIMEOUT
      case 13  => FTP_WEIRD_PASV_REPLY
      case 14  => FTP_WEIRD_227_FORMAT
      case 15  => FTP_CANT_GET_HOST
      case 16  => HTTP2
      case 17  => FTP_COULDNT_SET_TYPE
      case 18  => PARTIAL_FILE
      case 19  => FTP_COULDNT_RETR_FILE
      case 20  => OBSOLETE20
      case 21  => QUOTE_ERROR
      case 22  => HTTP_RETURNED_ERROR
      case 23  => WRITE_ERROR
      case 24  => OBSOLETE24
      case 25  => UPLOAD_FAILED
      case 26  => READ_ERROR
      case 27  => OUT_OF_MEMORY
      case 28  => OPERATION_TIMEDOUT
      case 29  => OBSOLETE29
      case 30  => FTP_PORT_FAILED
      case 31  => FTP_COULDNT_USE_REST
      case 32  => OBSOLETE32
      case 33  => RANGE_ERROR
      case 34  => HTTP_POST_ERROR
      case 35  => SSL_CONNECT_ERROR
      case 36  => BAD_DOWNLOAD_RESUME
      case 37  => FILE_COULDNT_READ_FILE
      case 38  => LDAP_CANNOT_BIND
      case 39  => LDAP_SEARCH_FAILED
      case 40  => OBSOLETE40
      case 41  => FUNCTION_NOT_FOUND
      case 42  => ABORTED_BY_CALLBACK
      case 43  => BAD_FUNCTION_ARGUMENT
      case 44  => OBSOLETE44
      case 45  => INTERFACE_FAILED
      case 46  => OBSOLETE46
      case 47  => TOO_MANY_REDIRECTS
      case 48  => UNKNOWN_OPTION
      case 49  => SETOPT_OPTION_SYNTAX
      case 50  => OBSOLETE50
      case 51  => OBSOLETE51
      case 52  => GOT_NOTHING
      case 53  => SSL_ENGINE_NOTFOUND
      case 54  => SSL_ENGINE_SETFAILED
      case 55  => SEND_ERROR
      case 56  => RECV_ERROR
      case 57  => OBSOLETE57
      case 58  => SSL_CERTPROBLEM
      case 59  => SSL_CIPHER
      case 60  => PEER_FAILED_VERIFICATION
      case 61  => BAD_CONTENT_ENCODING
      case 62  => OBSOLETE62
      case 63  => FILESIZE_EXCEEDED
      case 64  => USE_SSL_FAILED
      case 65  => SEND_FAIL_REWIND
      case 66  => SSL_ENGINE_INITFAILED
      case 67  => LOGIN_DENIED
      case 68  => TFTP_NOTFOUND
      case 69  => TFTP_PERM
      case 70  => REMOTE_DISK_FULL
      case 71  => TFTP_ILLEGAL
      case 72  => TFTP_UNKNOWNID
      case 73  => REMOTE_FILE_EXISTS
      case 74  => TFTP_NOSUCHUSER
      case 75  => OBSOLETE75
      case 76  => OBSOLETE76
      case 77  => SSL_CACERT_BADFILE
      case 78  => REMOTE_FILE_NOT_FOUND
      case 79  => SSH
      case 80  => SSL_SHUTDOWN_FAILED
      case 81  => AGAIN
      case 82  => SSL_CRL_BADFILE
      case 83  => SSL_ISSUER_ERROR
      case 84  => FTP_PRET_FAILED
      case 85  => RTSP_CSEQ_ERROR
      case 86  => RTSP_SESSION_ERROR
      case 87  => FTP_BAD_FILE_LIST
      case 88  => CHUNK_FAILED
      case 89  => NO_CONNECTION_AVAILABLE
      case 90  => SSL_PINNEDPUBKEYNOTMATCH
      case 91  => SSL_INVALIDCERTSTATUS
      case 92  => HTTP2_STREAM
      case 93  => RECURSIVE_API_CALL
      case 94  => AUTH_ERROR
      case 95  => HTTP3
      case 96  => QUIC_CONNECT_ERROR
      case 97  => PROXY
      case 98  => SSL_CLIENTCERT
      case 99  => UNRECOVERABLE_POLL
      case 100 => CURL_LAST
      case _   => throw new IllegalArgumentException(s"Invalid CurlErrCode: ${v}, probably binding version mismatch")

    implicit class RichCurlErrCode(value: CurlErrCode) extends AnyVal:
      inline def getname: String =
        value match
          case OK                       => "CURLE_OK"
          case UNSUPPORTED_PROTOCOL     => "CURLE_UNSUPPORTED_PROTOCOL"
          case FAILED_INIT              => "CURLE_FAILED_INIT"
          case URL_MALFORMAT            => "CURLE_URL_MALFORMAT"
          case NOT_BUILT_IN             => "CURLE_NOT_BUILT_IN"
          case COULDNT_RESOLVE_PROXY    => "CURLE_COULDNT_RESOLVE_PROXY"
          case COULDNT_RESOLVE_HOST     => "CURLE_COULDNT_RESOLVE_HOST"
          case COULDNT_CONNECT          => "CURLE_COULDNT_CONNECT"
          case WEIRD_SERVER_REPLY       => "CURLE_WEIRD_SERVER_REPLY"
          case REMOTE_ACCESS_DENIED     => "CURLE_REMOTE_ACCESS_DENIED"
          case FTP_ACCEPT_FAILED        => "CURLE_FTP_ACCEPT_FAILED"
          case FTP_WEIRD_PASS_REPLY     => "CURLE_FTP_WEIRD_PASS_REPLY"
          case FTP_ACCEPT_TIMEOUT       => "CURLE_FTP_ACCEPT_TIMEOUT"
          case FTP_WEIRD_PASV_REPLY     => "CURLE_FTP_WEIRD_PASV_REPLY"
          case FTP_WEIRD_227_FORMAT     => "CURLE_FTP_WEIRD_227_FORMAT"
          case FTP_CANT_GET_HOST        => "CURLE_FTP_CANT_GET_HOST"
          case HTTP2                    => "CURLE_HTTP2"
          case FTP_COULDNT_SET_TYPE     => "CURLE_FTP_COULDNT_SET_TYPE"
          case PARTIAL_FILE             => "CURLE_PARTIAL_FILE"
          case FTP_COULDNT_RETR_FILE    => "CURLE_FTP_COULDNT_RETR_FILE"
          case OBSOLETE20               => "CURLE_OBSOLETE20"
          case QUOTE_ERROR              => "CURLE_QUOTE_ERROR"
          case HTTP_RETURNED_ERROR      => "CURLE_HTTP_RETURNED_ERROR"
          case WRITE_ERROR              => "CURLE_WRITE_ERROR"
          case OBSOLETE24               => "CURLE_OBSOLETE24"
          case UPLOAD_FAILED            => "CURLE_UPLOAD_FAILED"
          case READ_ERROR               => "CURLE_READ_ERROR"
          case OUT_OF_MEMORY            => "CURLE_OUT_OF_MEMORY"
          case OPERATION_TIMEDOUT       => "CURLE_OPERATION_TIMEDOUT"
          case OBSOLETE29               => "CURLE_OBSOLETE29"
          case FTP_PORT_FAILED          => "CURLE_FTP_PORT_FAILED"
          case FTP_COULDNT_USE_REST     => "CURLE_FTP_COULDNT_USE_REST"
          case OBSOLETE32               => "CURLE_OBSOLETE32"
          case RANGE_ERROR              => "CURLE_RANGE_ERROR"
          case HTTP_POST_ERROR          => "CURLE_HTTP_POST_ERROR"
          case SSL_CONNECT_ERROR        => "CURLE_SSL_CONNECT_ERROR"
          case BAD_DOWNLOAD_RESUME      => "CURLE_BAD_DOWNLOAD_RESUME"
          case FILE_COULDNT_READ_FILE   => "CURLE_FILE_COULDNT_READ_FILE"
          case LDAP_CANNOT_BIND         => "CURLE_LDAP_CANNOT_BIND"
          case LDAP_SEARCH_FAILED       => "CURLE_LDAP_SEARCH_FAILED"
          case OBSOLETE40               => "CURLE_OBSOLETE40"
          case FUNCTION_NOT_FOUND       => "CURLE_FUNCTION_NOT_FOUND"
          case ABORTED_BY_CALLBACK      => "CURLE_ABORTED_BY_CALLBACK"
          case BAD_FUNCTION_ARGUMENT    => "CURLE_BAD_FUNCTION_ARGUMENT"
          case OBSOLETE44               => "CURLE_OBSOLETE44"
          case INTERFACE_FAILED         => "CURLE_INTERFACE_FAILED"
          case OBSOLETE46               => "CURLE_OBSOLETE46"
          case TOO_MANY_REDIRECTS       => "CURLE_TOO_MANY_REDIRECTS"
          case UNKNOWN_OPTION           => "CURLE_UNKNOWN_OPTION"
          case SETOPT_OPTION_SYNTAX     => "CURLE_SETOPT_OPTION_SYNTAX"
          case OBSOLETE50               => "CURLE_OBSOLETE50"
          case OBSOLETE51               => "CURLE_OBSOLETE51"
          case GOT_NOTHING              => "CURLE_GOT_NOTHING"
          case SSL_ENGINE_NOTFOUND      => "CURLE_SSL_ENGINE_NOTFOUND"
          case SSL_ENGINE_SETFAILED     => "CURLE_SSL_ENGINE_SETFAILED"
          case SEND_ERROR               => "CURLE_SEND_ERROR"
          case RECV_ERROR               => "CURLE_RECV_ERROR"
          case OBSOLETE57               => "CURLE_OBSOLETE57"
          case SSL_CERTPROBLEM          => "CURLE_SSL_CERTPROBLEM"
          case SSL_CIPHER               => "CURLE_SSL_CIPHER"
          case PEER_FAILED_VERIFICATION => "CURLE_PEER_FAILED_VERIFICATION"
          case BAD_CONTENT_ENCODING     => "CURLE_BAD_CONTENT_ENCODING"
          case OBSOLETE62               => "CURLE_OBSOLETE62"
          case FILESIZE_EXCEEDED        => "CURLE_FILESIZE_EXCEEDED"
          case USE_SSL_FAILED           => "CURLE_USE_SSL_FAILED"
          case SEND_FAIL_REWIND         => "CURLE_SEND_FAIL_REWIND"
          case SSL_ENGINE_INITFAILED    => "CURLE_SSL_ENGINE_INITFAILED"
          case LOGIN_DENIED             => "CURLE_LOGIN_DENIED"
          case TFTP_NOTFOUND            => "CURLE_TFTP_NOTFOUND"
          case TFTP_PERM                => "CURLE_TFTP_PERM"
          case REMOTE_DISK_FULL         => "CURLE_REMOTE_DISK_FULL"
          case TFTP_ILLEGAL             => "CURLE_TFTP_ILLEGAL"
          case TFTP_UNKNOWNID           => "CURLE_TFTP_UNKNOWNID"
          case REMOTE_FILE_EXISTS       => "CURLE_REMOTE_FILE_EXISTS"
          case TFTP_NOSUCHUSER          => "CURLE_TFTP_NOSUCHUSER"
          case OBSOLETE75               => "CURLE_OBSOLETE75"
          case OBSOLETE76               => "CURLE_OBSOLETE76"
          case SSL_CACERT_BADFILE       => "CURLE_SSL_CACERT_BADFILE"
          case REMOTE_FILE_NOT_FOUND    => "CURLE_REMOTE_FILE_NOT_FOUND"
          case SSH                      => "CURLE_SSH"
          case SSL_SHUTDOWN_FAILED      => "CURLE_SSL_SHUTDOWN_FAILED"
          case AGAIN                    => "CURLE_AGAIN"
          case SSL_CRL_BADFILE          => "CURLE_SSL_CRL_BADFILE"
          case SSL_ISSUER_ERROR         => "CURLE_SSL_ISSUER_ERROR"
          case FTP_PRET_FAILED          => "CURLE_FTP_PRET_FAILED"
          case RTSP_CSEQ_ERROR          => "CURLE_RTSP_CSEQ_ERROR"
          case RTSP_SESSION_ERROR       => "CURLE_RTSP_SESSION_ERROR"
          case FTP_BAD_FILE_LIST        => "CURLE_FTP_BAD_FILE_LIST"
          case CHUNK_FAILED             => "CURLE_CHUNK_FAILED"
          case NO_CONNECTION_AVAILABLE  => "CURLE_NO_CONNECTION_AVAILABLE"
          case SSL_PINNEDPUBKEYNOTMATCH => "CURLE_SSL_PINNEDPUBKEYNOTMATCH"
          case SSL_INVALIDCERTSTATUS    => "CURLE_SSL_INVALIDCERTSTATUS"
          case HTTP2_STREAM             => "CURLE_HTTP2_STREAM"
          case RECURSIVE_API_CALL       => "CURLE_RECURSIVE_API_CALL"
          case AUTH_ERROR               => "CURLE_AUTH_ERROR"
          case HTTP3                    => "CURLE_HTTP3"
          case QUIC_CONNECT_ERROR       => "CURLE_QUIC_CONNECT_ERROR"
          case PROXY                    => "CURLE_PROXY"
          case SSL_CLIENTCERT           => "CURLE_SSL_CLIENTCERT"
          case UNRECOVERABLE_POLL       => "CURLE_UNRECOVERABLE_POLL"
          case CURL_LAST                => "CURL_LAST"

  end CurlErrCode

  // known as "CURLproxycode"
  opaque type CurlProxyCode = Int
  object CurlProxyCode extends _BindgenEnumCInt[CurlProxyCode]:

    given Tag[CurlProxyCode] = Tag.Int

    private inline def define(inline a: Int): CurlProxyCode = a

    val OK                               = define(0)
    val BAD_ADDRESS_TYPE                 = define(1)
    val BAD_VERSION                      = define(2)
    val CLOSED                           = define(3)
    val GSSAPI                           = define(4)
    val GSSAPI_PERMSG                    = define(5)
    val GSSAPI_PROTECTION                = define(6)
    val IDENTD                           = define(7)
    val IDENTD_DIFFER                    = define(8)
    val LONG_HOSTNAME                    = define(9)
    val LONG_PASSWD                      = define(10)
    val LONG_USER                        = define(11)
    val NO_AUTH                          = define(12)
    val RECV_ADDRESS                     = define(13)
    val RECV_AUTH                        = define(14)
    val RECV_CONNECT                     = define(15)
    val RECV_REQACK                      = define(16)
    val REPLY_ADDRESS_TYPE_NOT_SUPPORTED = define(17)
    val REPLY_COMMAND_NOT_SUPPORTED      = define(18)
    val REPLY_CONNECTION_REFUSED         = define(19)
    val REPLY_GENERAL_SERVER_FAILURE     = define(20)
    val REPLY_HOST_UNREACHABLE           = define(21)
    val REPLY_NETWORK_UNREACHABLE        = define(22)
    val REPLY_NOT_ALLOWED                = define(23)
    val REPLY_TTL_EXPIRED                = define(24)
    val REPLY_UNASSIGNED                 = define(25)
    val REQUEST_FAILED                   = define(26)
    val RESOLVE_HOST                     = define(27)
    val SEND_AUTH                        = define(28)
    val SEND_CONNECT                     = define(29)
    val SEND_REQUEST                     = define(30)
    val UNKNOWN_FAIL                     = define(31)
    val UNKNOWN_MODE                     = define(32)
    val USER_REJECTED                    = define(33)
    val LAST                             = define(34)

    // scalafmt: { maxColumn = 150, align.preset = most }
    def getname(value: CurlProxyCode): Option[String] =
      value match
        case OK                               => Some("CURLPX_OK")
        case BAD_ADDRESS_TYPE                 => Some("CURLPX_BAD_ADDRESS_TYPE")
        case BAD_VERSION                      => Some("CURLPX_BAD_VERSION")
        case CLOSED                           => Some("CURLPX_CLOSED")
        case GSSAPI                           => Some("CURLPX_GSSAPI")
        case GSSAPI_PERMSG                    => Some("CURLPX_GSSAPI_PERMSG")
        case GSSAPI_PROTECTION                => Some("CURLPX_GSSAPI_PROTECTION")
        case IDENTD                           => Some("CURLPX_IDENTD")
        case IDENTD_DIFFER                    => Some("CURLPX_IDENTD_DIFFER")
        case LONG_HOSTNAME                    => Some("CURLPX_LONG_HOSTNAME")
        case LONG_PASSWD                      => Some("CURLPX_LONG_PASSWD")
        case LONG_USER                        => Some("CURLPX_LONG_USER")
        case NO_AUTH                          => Some("CURLPX_NO_AUTH")
        case RECV_ADDRESS                     => Some("CURLPX_RECV_ADDRESS")
        case RECV_AUTH                        => Some("CURLPX_RECV_AUTH")
        case RECV_CONNECT                     => Some("CURLPX_RECV_CONNECT")
        case RECV_REQACK                      => Some("CURLPX_RECV_REQACK")
        case REPLY_ADDRESS_TYPE_NOT_SUPPORTED => Some("CURLPX_REPLY_ADDRESS_TYPE_NOT_SUPPORTED")
        case REPLY_COMMAND_NOT_SUPPORTED      => Some("CURLPX_REPLY_COMMAND_NOT_SUPPORTED")
        case REPLY_CONNECTION_REFUSED         => Some("CURLPX_REPLY_CONNECTION_REFUSED")
        case REPLY_GENERAL_SERVER_FAILURE     => Some("CURLPX_REPLY_GENERAL_SERVER_FAILURE")
        case REPLY_HOST_UNREACHABLE           => Some("CURLPX_REPLY_HOST_UNREACHABLE")
        case REPLY_NETWORK_UNREACHABLE        => Some("CURLPX_REPLY_NETWORK_UNREACHABLE")
        case REPLY_NOT_ALLOWED                => Some("CURLPX_REPLY_NOT_ALLOWED")
        case REPLY_TTL_EXPIRED                => Some("CURLPX_REPLY_TTL_EXPIRED")
        case REPLY_UNASSIGNED                 => Some("CURLPX_REPLY_UNASSIGNED")
        case REQUEST_FAILED                   => Some("CURLPX_REQUEST_FAILED")
        case RESOLVE_HOST                     => Some("CURLPX_RESOLVE_HOST")
        case SEND_AUTH                        => Some("CURLPX_SEND_AUTH")
        case SEND_CONNECT                     => Some("CURLPX_SEND_CONNECT")
        case SEND_REQUEST                     => Some("CURLPX_SEND_REQUEST")
        case UNKNOWN_FAIL                     => Some("CURLPX_UNKNOWN_FAIL")
        case UNKNOWN_MODE                     => Some("CURLPX_UNKNOWN_MODE")
        case USER_REJECTED                    => Some("CURLPX_USER_REJECTED")
        case LAST                             => Some("CURLPX_LAST")
        case _                                => None
    // scalafmt: { maxColumn = 120, align.preset = most }

    extension (a: CurlProxyCode)
      inline def &(b: CurlProxyCode): CurlProxyCode = a & b
      inline def |(b: CurlProxyCode): CurlProxyCode = a | b
      inline def is(b: CurlProxyCode): Boolean      = (a & b) == b

  end CurlProxyCode

  // TODO: add func `curl_conv_callback``

  // TODO: add func `curl_ssl_ctx_callback``

  // known as "CURLPROXY_*"
  opaque type CurlProxyType = CLong
  object CurlProxyType extends _BindgenEnumCLong[CurlProxyType]:
    given Tag[CurlProxyType]                                 = Tag.Size
    private inline def define(inline a: Long): CurlProxyType = a.toSize

    val HTTP            = define(0L)
    val HTTP_1_0        = define(1L)
    val HTTPS           = define(2L)
    val HTTPS2          = define(3L)
    val SOCKS4          = define(4L)
    val SOCKS5          = define(5L)
    val SOCKS4A         = define(6L)
    val SOCKS5_HOSTNAME = define(7L)

    def getname(value: CurlProxyType): String =
      value match
        case HTTP            => "CURLPROXY_HTTP"
        case HTTP_1_0        => "CURLPROXY_HTTP_1_0"
        case HTTPS           => "CURLPROXY_HTTPS"
        case HTTPS2          => "CURLPROXY_HTTPS2"
        case SOCKS4          => "CURLPROXY_SOCKS4"
        case SOCKS5          => "CURLPROXY_SOCKS5"
        case SOCKS4A         => "CURLPROXY_SOCKS4A"
        case SOCKS5_HOSTNAME => "CURLPROXY_SOCKS5_HOSTNAME"

  // TODO:
  //
  // 1. add define symbols `CURLAUTH_*`
  // 2. add enum `curl_khtype`
  // 3. add struct `curl_khkey`
  // 4. add enum `curl_khstat`
  // 5. add enum `curl_khmatch`
  // 6. add func `curl_sshkeycallback`
  // 7. add func `curl_sshhostkeycallback`

  /**
   * known as `CURLUSESSL_*` symbols
   *
   * parameter for the `CURLOPT_USE_SSL` option
   */
  opaque type CurlUseSsl = CLong
  object CurlUseSsl extends _BindgenEnumCLong[CurlUseSsl]:

    given Tag[CurlUseSsl] = Tag.Size

    private inline def define(inline a: Long): CurlUseSsl = a.toSize

    val NONE    = define(0L) // do not attempt to use SSL
    val TRY     = define(1L) // try using SSL, proceed anyway otherwise
    val CONTROL = define(2L) // SSL for the control connection or fail
    val ALL     = define(3L) // SSL for all communication or fail
    val LAST    = define(4L) // not an option, never use

  end CurlUseSsl

  // TODO:
  //
  // 2. add define symbols `CURLSSLOPT_*`
  // 3. add typedef enum `curl_ftpccc`
  // 4. add typedef enum `curl_ftpauth`
  // 5. add typedef enum `curl_ftpcreatedir`
  // 6. add typedef enum `curl_ftpmethod`

  /** bitmask defines for `CURLOPT_HEADEROPT` */
  // known as "CURLHEADER_*"
  opaque type CurlHeaderOpt = CLong
  object CurlHeaderOpt extends _BindgenEnumCLong[CurlHeaderOpt]:

    private inline def define(inline v: Long): CurlHeaderOpt = v.toSize

    val UNIFIED  = define(0L)
    val SEPARATE = define(1L << 0)

  end CurlHeaderOpt

  /* bits for the `CURLOPT_ALTSVC_CTRL` option */
  // known as "CURLALTSVC_*"
  opaque type CurlAltSvc = CLong
  object CurlAltSvc extends _BindgenEnumCLong[CurlAltSvc]:

    given Tag[CurlAltSvc] = Tag.Size

    private inline def define(inline v: Long): CurlAltSvc = v.toSize

    val READONLYFILE = define(1L << 2)
    val H1           = define(1L << 3)
    val H2           = define(1L << 4)
    val H3           = define(1L << 5)

  end CurlAltSvc

  // TODO
  //
  // 1. add struct `curl_hstsentry`
  // 2. add struct `curl_index`
  // 3. add typedef enum `CURLSTScode`
  // 4. add func `curl_hstsread_callback`
  // 5. add func `curl_hstswrite_callback`
  // 6. add define symbols `CURLHSTS_*`

  // known as "CURLoption"
  opaque type CurlOption = Int
  object CurlOption extends _BindgenEnumCInt[CurlOption]:

    given Tag[CurlOption] = Tag.Int

    private inline def define(inline a: Int): CurlOption = a

    // CurlOptType
    private object cot {
      val CLONG         = 0
      val OBJECTPOINT   = 10000
      val FUNCTIONPOINT = 20000
      val OFF_T         = 30000
      val BLOB          = 40000

      // alias
      val STRINGPOINT = OBJECTPOINT
      val SLISTPOINT  = OBJECTPOINT
      val CBPOINT     = OBJECTPOINT
      val VALUES      = CLONG
    }

    val WRITEDATA                  = define(cot.CBPOINT + 1)
    val URL                        = define(cot.STRINGPOINT + 2)
    val PORT                       = define(cot.CLONG + 3)
    val PROXY                      = define(cot.STRINGPOINT + 4)
    val USERPWD                    = define(cot.STRINGPOINT + 5)
    val PROXYUSERPWD               = define(cot.STRINGPOINT + 6)
    val RANGE                      = define(cot.STRINGPOINT + 7)
    val READDATA                   = define(10009)
    val ERRORBUFFER                = define(10010)
    val WRITEFUNCTION              = define(cot.FUNCTIONPOINT + 11)
    val READFUNCTION               = define(cot.FUNCTIONPOINT + 12)
    val TIMEOUT                    = define(cot.CLONG + 13)
    val INFILESIZE                 = define(cot.CLONG + 14)
    val POSTFIELDS                 = define(10015)
    val REFERER                    = define(10016)
    val FTPPORT                    = define(10017)
    val USERAGENT                  = define(10018)
    val LOW_SPEED_LIMIT            = define(cot.CLONG + 19)
    val LOW_SPEED_TIME             = define(cot.CLONG + 20)
    val RESUME_FROM                = define(cot.CLONG + 21)
    val COOKIE                     = define(10022)
    val HTTPHEADER                 = define(10023)
    val HTTPPOST                   = define(10024)
    val SSLCERT                    = define(10025)
    val KEYPASSWD                  = define(10026)
    val CRLF                       = define(cot.CLONG + 27)
    val QUOTE                      = define(10028)
    val HEADERDATA                 = define(10029)
    val COOKIEFILE                 = define(10031)
    val SSLVERSION                 = define(cot.VALUES + 32)
    val TIMECONDITION              = define(cot.VALUES + 33)
    val TIMEVALUE                  = define(cot.CLONG + 34)
    val CUSTOMREQUEST              = define(10036)
    val STDERR                     = define(10037)
    val POSTQUOTE                  = define(10039)
    val WRITEINFO                  = define(10040)          // OBSOLETE40
    val VERBOSE                    = define(cot.CLONG + 41)
    val HEADER                     = define(cot.CLONG + 42)
    val NOPROGRESS                 = define(cot.CLONG + 43)
    val NOBODY                     = define(cot.CLONG + 44) // HTTP HEAD
    val FAILONERROR                = define(cot.CLONG + 45)
    val UPLOAD                     = define(cot.CLONG + 46)
    val POST                       = define(cot.CLONG + 47)
    val DIRLISTONLY                = define(cot.CLONG + 48)
    val APPEND                     = define(cot.CLONG + 50)
    val NETRC                      = define(cot.VALUES + 51)
    val FOLLOWLOCATION             = define(cot.CLONG + 52)
    val TRANSFERTEXT               = define(cot.CLONG + 53)
    val PUT                        = define(cot.CLONG + 54)
    val PROGRESSFUNCTION           = define(cot.FUNCTIONPOINT + 56)
    val XFERINFODATA               = define(10057)
    val AUTOREFERER                = define(cot.CLONG + 58)
    val PROXYPORT                  = define(cot.CLONG + 59)
    val POSTFIELDSIZE              = define(cot.CLONG + 60)
    val HTTPPROXYTUNNEL            = define(cot.CLONG + 61)
    val INTERFACE                  = define(cot.STRINGPOINT + 62)
    val KRBLEVEL                   = define(10063)
    val SSL_VERIFYPEER             = define(cot.CLONG + 64)
    val CAINFO                     = define(10065)
    val MAXREDIRS                  = define(cot.CLONG + 68)
    val FILETIME                   = define(cot.CLONG + 69)
    val TELNETOPTIONS              = define(10070)
    val MAXCONNECTS                = define(cot.CLONG + 71)
    val CLOSEPOLICY                = define(72)             // OBSOLETE72
    val FRESH_CONNECT              = define(cot.CLONG + 74)
    val FORBID_REUSE               = define(cot.CLONG + 75)
    val RANDOM_FILE                = define(10076)
    val EGDSOCKET                  = define(10077)
    val CONNECTTIMEOUT             = define(cot.CLONG + 78)
    val HEADERFUNCTION             = define(cot.FUNCTIONPOINT + 79)
    val HTTPGET                    = define(cot.CLONG + 80)
    val SSL_VERIFYHOST             = define(cot.CLONG + 81)
    val COOKIEJAR                  = define(10082)
    val SSL_CIPHER_LIST            = define(10083)
    val HTTP_VERSION               = define(cot.VALUES + 84)
    val FTP_USE_EPSV               = define(cot.CLONG + 85)
    val SSLCERTTYPE                = define(10086)
    val SSLKEY                     = define(10087)
    val SSLKEYTYPE                 = define(10088)
    val SSLENGINE                  = define(10089)
    val SSLENGINE_DEFAULT          = define(cot.CLONG + 90)
    val DNS_USE_GLOBAL_CACHE       = define(cot.CLONG + 91)
    val DNS_CACHE_TIMEOUT          = define(cot.CLONG + 92)
    val PREQUOTE                   = define(10093)
    val DEBUGFUNCTION              = define(cot.FUNCTIONPOINT + 94)
    val DEBUGDATA                  = define(10095)
    val COOKIESESSION              = define(cot.CLONG + 96)
    val CAPATH                     = define(10097)
    val BUFFERSIZE                 = define(cot.CLONG + 98)
    val NOSIGNAL                   = define(cot.CLONG + 99)
    val SHARE                      = define(10100)
    val PROXYTYPE                  = define(cot.VALUES + 101)
    val ACCEPT_ENCODING            = define(10102)
    val PRIVATE                    = define(10103)
    val HTTP200ALIASES             = define(10104)
    val UNRESTRICTED_AUTH          = define(cot.CLONG + 105)
    val FTP_USE_EPRT               = define(cot.CLONG + 106)
    val HTTPAUTH                   = define(cot.VALUES + 107)
    val SSL_CTX_FUNCTION           = define(cot.FUNCTIONPOINT + 108)
    val SSL_CTX_DATA               = define(10109)
    val FTP_CREATE_MISSING_DIRS    = define(cot.CLONG + 110)
    val PROXYAUTH                  = define(cot.VALUES + 111)
    val SERVER_RESPONSE_TIMEOUT    = define(cot.CLONG + 112)
    val IPRESOLVE                  = define(cot.VALUES + 113)
    val MAXFILESIZE                = define(cot.CLONG + 114)
    val INFILESIZE_LARGE           = define(cot.OFF_T + 115)
    val RESUME_FROM_LARGE          = define(cot.OFF_T + 116)
    val MAXFILESIZE_LARGE          = define(cot.OFF_T + 117)
    val NETRC_FILE                 = define(10118)
    val USE_SSL                    = define(cot.VALUES + 119)
    val POSTFIELDSIZE_LARGE        = define(cot.OFF_T + 120)
    val TCP_NODELAY                = define(cot.CLONG + 121)
    val FTPSSLAUTH                 = define(cot.VALUES + 129)
    val IOCTLFUNCTION              = define(cot.FUNCTIONPOINT + 130)
    val IOCTLDATA                  = define(10131)
    val FTP_ACCOUNT                = define(10134)
    val COOKIELIST                 = define(10135)
    val IGNORE_CONTENT_LENGTH      = define(cot.CLONG + 136)
    val FTP_SKIP_PASV_IP           = define(cot.CLONG + 137)
    val FTP_FILEMETHOD             = define(cot.VALUES + 138)
    val LOCALPORT                  = define(cot.CLONG + 139)
    val LOCALPORTRANGE             = define(cot.CLONG + 140)
    val CONNECT_ONLY               = define(cot.CLONG + 141)
    val CONV_FROM_NETWORK_FUNCTION = define(cot.FUNCTIONPOINT + 142)
    val CONV_TO_NETWORK_FUNCTION   = define(cot.FUNCTIONPOINT + 143)
    val CONV_FROM_UTF8_FUNCTION    = define(cot.FUNCTIONPOINT + 144)
    val MAX_SEND_SPEED_LARGE       = define(cot.OFF_T + 145)
    val MAX_RECV_SPEED_LARGE       = define(cot.OFF_T + 146)
    val FTP_ALTERNATIVE_TO_USER    = define(10147)
    val SOCKOPTFUNCTION            = define(cot.FUNCTIONPOINT + 148)
    val SOCKOPTDATA                = define(10149)
    val SSL_SESSIONID_CACHE        = define(cot.CLONG + 150)
    val SSH_AUTH_TYPES             = define(cot.VALUES + 151)
    val SSH_PUBLIC_KEYFILE         = define(10152)
    val SSH_PRIVATE_KEYFILE        = define(10153)
    val FTP_SSL_CCC                = define(cot.CLONG + 154)
    val TIMEOUT_MS                 = define(cot.CLONG + 155)
    val CONNECTTIMEOUT_MS          = define(cot.CLONG + 156)
    val HTTP_TRANSFER_DECODING     = define(cot.CLONG + 157)
    val HTTP_CONTENT_DECODING      = define(cot.CLONG + 158)
    val NEW_FILE_PERMS             = define(cot.CLONG + 159)
    val NEW_DIRECTORY_PERMS        = define(cot.CLONG + 160)
    val POSTREDIR                  = define(cot.VALUES + 161)
    val SSH_HOST_PUBLIC_KEY_MD5    = define(10162)
    val OPENSOCKETFUNCTION         = define(cot.FUNCTIONPOINT + 163)
    val OPENSOCKETDATA             = define(10164)
    val COPYPOSTFIELDS             = define(10165)
    val PROXY_TRANSFER_MODE        = define(cot.CLONG + 166)
    val SEEKFUNCTION               = define(cot.FUNCTIONPOINT + 167)
    val SEEKDATA                   = define(10168)
    val CRLFILE                    = define(10169)
    val ISSUERCERT                 = define(10170)
    val ADDRESS_SCOPE              = define(cot.CLONG + 171)
    val CERTINFO                   = define(cot.CLONG + 172)
    val USERNAME                   = define(10173)
    val PASSWORD                   = define(10174)
    val PROXYUSERNAME              = define(10175)
    val PROXYPASSWORD              = define(10176)
    val NOPROXY                    = define(10177)
    val TFTP_BLKSIZE               = define(cot.CLONG + 178)
    val SOCKS5_GSSAPI_SERVICE      = define(10179)
    val SOCKS5_GSSAPI_NEC          = define(cot.CLONG + 180)
    val PROTOCOLS                  = define(cot.CLONG + 181)
    val REDIR_PROTOCOLS            = define(cot.CLONG + 182)
    val SSH_KNOWNHOSTS             = define(10183)
    val SSH_KEYFUNCTION            = define(cot.FUNCTIONPOINT + 184)
    val SSH_KEYDATA                = define(10185)
    val MAIL_FROM                  = define(10186)
    val MAIL_RCPT                  = define(10187)
    val FTP_USE_PRET               = define(cot.CLONG + 188)
    val RTSP_REQUEST               = define(cot.VALUES + 189)
    val RTSP_SESSION_ID            = define(10190)
    val RTSP_STREAM_URI            = define(10191)
    val RTSP_TRANSPORT             = define(10192)
    val RTSP_CLIENT_CSEQ           = define(cot.CLONG + 193)
    val RTSP_SERVER_CSEQ           = define(cot.CLONG + 194)
    val INTERLEAVEDATA             = define(10195)
    val INTERLEAVEFUNCTION         = define(cot.FUNCTIONPOINT + 196)
    val WILDCARDMATCH              = define(cot.CLONG + 197)
    val CHUNK_BGN_FUNCTION         = define(cot.FUNCTIONPOINT + 198)
    val CHUNK_END_FUNCTION         = define(cot.FUNCTIONPOINT + 199)
    val FNMATCH_FUNCTION           = define(cot.FUNCTIONPOINT + 200)
    val CHUNK_DATA                 = define(10201)
    val FNMATCH_DATA               = define(10202)
    val RESOLVE                    = define(10203)
    val TLSAUTH_USERNAME           = define(10204)
    val TLSAUTH_PASSWORD           = define(10205)
    val TLSAUTH_TYPE               = define(10206)
    val TRANSFER_ENCODING          = define(cot.CLONG + 207)
    val CLOSESOCKETFUNCTION        = define(cot.FUNCTIONPOINT + 208)
    val CLOSESOCKETDATA            = define(10209)
    val GSSAPI_DELEGATION          = define(cot.VALUES + 210)
    val DNS_SERVERS                = define(10211)
    val ACCEPTTIMEOUT_MS           = define(cot.CLONG + 212)
    val TCP_KEEPALIVE              = define(cot.CLONG + 213)
    val TCP_KEEPIDLE               = define(cot.CLONG + 214)
    val TCP_KEEPINTVL              = define(cot.CLONG + 215)
    val SSL_OPTIONS                = define(cot.VALUES + 216)
    val MAIL_AUTH                  = define(10217)
    val SASL_IR                    = define(cot.CLONG + 218)
    val XFERINFOFUNCTION           = define(cot.FUNCTIONPOINT + 219)
    val XOAUTH2_BEARER             = define(10220)
    val DNS_INTERFACE              = define(10221)
    val DNS_LOCAL_IP4              = define(10222)
    val DNS_LOCAL_IP6              = define(10223)
    val LOGIN_OPTIONS              = define(10224)
    val SSL_ENABLE_NPN             = define(cot.CLONG + 225)
    val SSL_ENABLE_ALPN            = define(cot.CLONG + 226)
    val EXPECT_100_TIMEOUT_MS      = define(cot.CLONG + 227)
    val PROXYHEADER                = define(10228)
    val HEADEROPT                  = define(cot.VALUES + 229)
    val PINNEDPUBLICKEY            = define(10230)
    val UNIX_SOCKET_PATH           = define(10231)
    val SSL_VERIFYSTATUS           = define(cot.CLONG + 232)
    val SSL_FALSESTART             = define(cot.CLONG + 233)
    val PATH_AS_IS                 = define(cot.CLONG + 234)
    val PROXY_SERVICE_NAME         = define(10235)
    val SERVICE_NAME               = define(10236)
    val PIPEWAIT                   = define(cot.CLONG + 237)
    val DEFAULT_PROTOCOL           = define(10238)
    val STREAM_WEIGHT              = define(cot.CLONG + 239)
    val STREAM_DEPENDS             = define(10240)
    val STREAM_DEPENDS_E           = define(10241)
    val TFTP_NO_OPTIONS            = define(cot.CLONG + 242)
    val CONNECT_TO                 = define(10243)
    val TCP_FASTOPEN               = define(cot.CLONG + 244)
    val KEEP_SENDING_ON_ERROR      = define(cot.CLONG + 245)
    val PROXY_CAINFO               = define(10246)
    val PROXY_CAPATH               = define(10247)
    val PROXY_SSL_VERIFYPEER       = define(cot.CLONG + 248)
    val PROXY_SSL_VERIFYHOST       = define(cot.CLONG + 249)
    val PROXY_SSLVERSION           = define(cot.VALUES + 250)
    val PROXY_TLSAUTH_USERNAME     = define(10251)
    val PROXY_TLSAUTH_PASSWORD     = define(10252)
    val PROXY_TLSAUTH_TYPE         = define(10253)
    val PROXY_SSLCERT              = define(10254)
    val PROXY_SSLCERTTYPE          = define(10255)
    val PROXY_SSLKEY               = define(10256)
    val PROXY_SSLKEYTYPE           = define(10257)
    val PROXY_KEYPASSWD            = define(10258)
    val PROXY_SSL_CIPHER_LIST      = define(10259)
    val PROXY_CRLFILE              = define(10260)
    val PROXY_SSL_OPTIONS          = define(cot.CLONG + 261)
    val PRE_PROXY                  = define(10262)
    val PROXY_PINNEDPUBLICKEY      = define(10263)
    val ABSTRACT_UNIX_SOCKET       = define(10264)
    val SUPPRESS_CONNECT_HEADERS   = define(cot.CLONG + 265)
    val REQUEST_TARGET             = define(10266)
    val SOCKS5_AUTH                = define(cot.CLONG + 267)
    val SSH_COMPRESSION            = define(cot.CLONG + 268)
    val MIMEPOST                   = define(10269)
    val TIMEVALUE_LARGE            = define(cot.OFF_T + 270)
    val HAPPY_EYEBALLS_TIMEOUT_MS  = define(cot.CLONG + 271)
    val RESOLVER_START_FUNCTION    = define(cot.FUNCTIONPOINT + 272)
    val RESOLVER_START_DATA        = define(10273)
    val HAPROXYPROTOCOL            = define(cot.CLONG + 274)
    val DNS_SHUFFLE_ADDRESSES      = define(cot.CLONG + 275)
    val TLS13_CIPHERS              = define(10276)
    val PROXY_TLS13_CIPHERS        = define(10277)
    val DISALLOW_USERNAME_IN_URL   = define(cot.CLONG + 278)
    val DOH_URL                    = define(10279)
    val UPLOAD_BUFFERSIZE          = define(cot.CLONG + 280)
    val UPKEEP_INTERVAL_MS         = define(cot.CLONG + 281)
    val CURLU                      = define(10282)
    val TRAILERFUNCTION            = define(cot.FUNCTIONPOINT + 283)
    val TRAILERDATA                = define(10284)
    val HTTP09_ALLOWED             = define(cot.CLONG + 285)
    val ALTSVC_CTRL                = define(cot.CLONG + 286)
    val ALTSVC                     = define(10287)
    val MAXAGE_CONN                = define(cot.CLONG + 288)
    val SASL_AUTHZID               = define(10289)
    val MAIL_RCPT_ALLOWFAILS       = define(cot.CLONG + 290)
    val SSLCERT_BLOB               = define(cot.BLOB + 291)
    val SSLKEY_BLOB                = define(cot.BLOB + 292)
    val PROXY_SSLCERT_BLOB         = define(cot.BLOB + 293)
    val PROXY_SSLKEY_BLOB          = define(cot.BLOB + 294)
    val ISSUERCERT_BLOB            = define(cot.BLOB + 295)
    val PROXY_ISSUERCERT           = define(10296)
    val PROXY_ISSUERCERT_BLOB      = define(cot.BLOB + 297)
    val SSL_EC_CURVES              = define(10298)
    val HSTS_CTRL                  = define(cot.CLONG + 299)
    val HSTS                       = define(10300)
    val HSTSREADFUNCTION           = define(cot.FUNCTIONPOINT + 301)
    val HSTSREADDATA               = define(10302)
    val HSTSWRITEFUNCTION          = define(cot.FUNCTIONPOINT + 303)
    val HSTSWRITEDATA              = define(10304)
    val AWS_SIGV4                  = define(10305)
    val DOH_SSL_VERIFYPEER         = define(cot.CLONG + 306)
    val DOH_SSL_VERIFYHOST         = define(cot.CLONG + 307)
    val DOH_SSL_VERIFYSTATUS       = define(cot.CLONG + 308)
    val CAINFO_BLOB                = define(cot.BLOB + 309)
    val PROXY_CAINFO_BLOB          = define(cot.BLOB + 310)
    val SSH_HOST_PUBLIC_KEY_SHA256 = define(10311)
    val PREREQFUNCTION             = define(cot.FUNCTIONPOINT + 312)
    val PREREQDATA                 = define(10313)
    val MAXLIFETIME_CONN           = define(cot.CLONG + 314)
    val MIME_OPTIONS               = define(cot.CLONG + 315)
    val SSH_HOSTKEYFUNCTION        = define(cot.FUNCTIONPOINT + 316)
    val SSH_HOSTKEYDATA            = define(10317)
    val PROTOCOLS_STR              = define(10318)
    val REDIR_PROTOCOLS_STR        = define(10319)
    val WS_OPTIONS                 = define(cot.CLONG + 320)
    val CA_CACHE_TIMEOUT           = define(cot.CLONG + 321)
    val QUICK_EXIT                 = define(cot.CLONG + 322)
    val HAPROXY_CLIENT_IP          = define(cot.STRINGPOINT + 323)
    val SERVER_RESPONSE_TIMEOUT_MS = define(cot.CLONG + 324)
    val ECH                        = define(cot.STRINGPOINT + 325)
    val TCP_KEEPCNT                = define(cot.CLONG + 326)
    val UPLOAD_FLAGS               = define(cot.CLONG + 327)
    val SSL_SIGNATURE_ALGORITHMS   = define(cot.STRINGPOINT + 328)
    val LASTENTRY                  = define(Int.MaxValue)

    extension (value: CurlOption)
      inline def getname: String =
        value match
          case WRITEDATA                  => "CURLOPT_WRITEDATA"
          case URL                        => "CURLOPT_URL"
          case PORT                       => "CURLOPT_PORT"
          case PROXY                      => "CURLOPT_PROXY"
          case USERPWD                    => "CURLOPT_USERPWD"
          case PROXYUSERPWD               => "CURLOPT_PROXYUSERPWD"
          case RANGE                      => "CURLOPT_RANGE"
          case READDATA                   => "CURLOPT_READDATA"
          case ERRORBUFFER                => "CURLOPT_ERRORBUFFER"
          case WRITEFUNCTION              => "CURLOPT_WRITEFUNCTION"
          case READFUNCTION               => "CURLOPT_READFUNCTION"
          case TIMEOUT                    => "CURLOPT_TIMEOUT"
          case INFILESIZE                 => "CURLOPT_INFILESIZE"
          case POSTFIELDS                 => "CURLOPT_POSTFIELDS"
          case REFERER                    => "CURLOPT_REFERER"
          case FTPPORT                    => "CURLOPT_FTPPORT"
          case USERAGENT                  => "CURLOPT_USERAGENT"
          case LOW_SPEED_LIMIT            => "CURLOPT_LOW_SPEED_LIMIT"
          case LOW_SPEED_TIME             => "CURLOPT_LOW_SPEED_TIME"
          case RESUME_FROM                => "CURLOPT_RESUME_FROM"
          case COOKIE                     => "CURLOPT_COOKIE"
          case HTTPHEADER                 => "CURLOPT_HTTPHEADER"
          case HTTPPOST                   => "CURLOPT_HTTPPOST"
          case SSLCERT                    => "CURLOPT_SSLCERT"
          case KEYPASSWD                  => "CURLOPT_KEYPASSWD"
          case CRLF                       => "CURLOPT_CRLF"
          case QUOTE                      => "CURLOPT_QUOTE"
          case HEADERDATA                 => "CURLOPT_HEADERDATA"
          case COOKIEFILE                 => "CURLOPT_COOKIEFILE"
          case SSLVERSION                 => "CURLOPT_SSLVERSION"
          case TIMECONDITION              => "CURLOPT_TIMECONDITION"
          case TIMEVALUE                  => "CURLOPT_TIMEVALUE"
          case CUSTOMREQUEST              => "CURLOPT_CUSTOMREQUEST"
          case STDERR                     => "CURLOPT_STDERR"
          case POSTQUOTE                  => "CURLOPT_POSTQUOTE"
          case WRITEINFO                  => "CURLOPT_WRITEINFO"
          case VERBOSE                    => "CURLOPT_VERBOSE"
          case HEADER                     => "CURLOPT_HEADER"
          case NOPROGRESS                 => "CURLOPT_NOPROGRESS"
          case NOBODY                     => "CURLOPT_NOBODY"
          case FAILONERROR                => "CURLOPT_FAILONERROR"
          case UPLOAD                     => "CURLOPT_UPLOAD"
          case POST                       => "CURLOPT_POST"
          case DIRLISTONLY                => "CURLOPT_DIRLISTONLY"
          case APPEND                     => "CURLOPT_APPEND"
          case NETRC                      => "CURLOPT_NETRC"
          case FOLLOWLOCATION             => "CURLOPT_FOLLOWLOCATION"
          case TRANSFERTEXT               => "CURLOPT_TRANSFERTEXT"
          case PUT                        => "CURLOPT_PUT"
          case PROGRESSFUNCTION           => "CURLOPT_PROGRESSFUNCTION"
          case XFERINFODATA               => "CURLOPT_XFERINFODATA"
          case AUTOREFERER                => "CURLOPT_AUTOREFERER"
          case PROXYPORT                  => "CURLOPT_PROXYPORT"
          case POSTFIELDSIZE              => "CURLOPT_POSTFIELDSIZE"
          case HTTPPROXYTUNNEL            => "CURLOPT_HTTPPROXYTUNNEL"
          case INTERFACE                  => "CURLOPT_INTERFACE"
          case KRBLEVEL                   => "CURLOPT_KRBLEVEL"
          case SSL_VERIFYPEER             => "CURLOPT_SSL_VERIFYPEER"
          case CAINFO                     => "CURLOPT_CAINFO"
          case MAXREDIRS                  => "CURLOPT_MAXREDIRS"
          case FILETIME                   => "CURLOPT_FILETIME"
          case TELNETOPTIONS              => "CURLOPT_TELNETOPTIONS"
          case MAXCONNECTS                => "CURLOPT_MAXCONNECTS"
          case CLOSEPOLICY                => "CURLOPT_CLOSEPOLICY"
          case FRESH_CONNECT              => "CURLOPT_FRESH_CONNECT"
          case FORBID_REUSE               => "CURLOPT_FORBID_REUSE"
          case RANDOM_FILE                => "CURLOPT_RANDOM_FILE"
          case EGDSOCKET                  => "CURLOPT_EGDSOCKET"
          case CONNECTTIMEOUT             => "CURLOPT_CONNECTTIMEOUT"
          case HEADERFUNCTION             => "CURLOPT_HEADERFUNCTION"
          case HTTPGET                    => "CURLOPT_HTTPGET"
          case SSL_VERIFYHOST             => "CURLOPT_SSL_VERIFYHOST"
          case COOKIEJAR                  => "CURLOPT_COOKIEJAR"
          case SSL_CIPHER_LIST            => "CURLOPT_SSL_CIPHER_LIST"
          case HTTP_VERSION               => "CURLOPT_HTTP_VERSION"
          case FTP_USE_EPSV               => "CURLOPT_FTP_USE_EPSV"
          case SSLCERTTYPE                => "CURLOPT_SSLCERTTYPE"
          case SSLKEY                     => "CURLOPT_SSLKEY"
          case SSLKEYTYPE                 => "CURLOPT_SSLKEYTYPE"
          case SSLENGINE                  => "CURLOPT_SSLENGINE"
          case SSLENGINE_DEFAULT          => "CURLOPT_SSLENGINE_DEFAULT"
          case DNS_USE_GLOBAL_CACHE       => "CURLOPT_DNS_USE_GLOBAL_CACHE"
          case DNS_CACHE_TIMEOUT          => "CURLOPT_DNS_CACHE_TIMEOUT"
          case PREQUOTE                   => "CURLOPT_PREQUOTE"
          case DEBUGFUNCTION              => "CURLOPT_DEBUGFUNCTION"
          case DEBUGDATA                  => "CURLOPT_DEBUGDATA"
          case COOKIESESSION              => "CURLOPT_COOKIESESSION"
          case CAPATH                     => "CURLOPT_CAPATH"
          case BUFFERSIZE                 => "CURLOPT_BUFFERSIZE"
          case NOSIGNAL                   => "CURLOPT_NOSIGNAL"
          case SHARE                      => "CURLOPT_SHARE"
          case PROXYTYPE                  => "CURLOPT_PROXYTYPE"
          case ACCEPT_ENCODING            => "CURLOPT_ACCEPT_ENCODING"
          case PRIVATE                    => "CURLOPT_PRIVATE"
          case HTTP200ALIASES             => "CURLOPT_HTTP200ALIASES"
          case UNRESTRICTED_AUTH          => "CURLOPT_UNRESTRICTED_AUTH"
          case FTP_USE_EPRT               => "CURLOPT_FTP_USE_EPRT"
          case HTTPAUTH                   => "CURLOPT_HTTPAUTH"
          case SSL_CTX_FUNCTION           => "CURLOPT_SSL_CTX_FUNCTION"
          case SSL_CTX_DATA               => "CURLOPT_SSL_CTX_DATA"
          case FTP_CREATE_MISSING_DIRS    => "CURLOPT_FTP_CREATE_MISSING_DIRS"
          case PROXYAUTH                  => "CURLOPT_PROXYAUTH"
          case SERVER_RESPONSE_TIMEOUT    => "CURLOPT_SERVER_RESPONSE_TIMEOUT"
          case IPRESOLVE                  => "CURLOPT_IPRESOLVE"
          case MAXFILESIZE                => "CURLOPT_MAXFILESIZE"
          case INFILESIZE_LARGE           => "CURLOPT_INFILESIZE_LARGE"
          case RESUME_FROM_LARGE          => "CURLOPT_RESUME_FROM_LARGE"
          case MAXFILESIZE_LARGE          => "CURLOPT_MAXFILESIZE_LARGE"
          case NETRC_FILE                 => "CURLOPT_NETRC_FILE"
          case USE_SSL                    => "CURLOPT_USE_SSL"
          case POSTFIELDSIZE_LARGE        => "CURLOPT_POSTFIELDSIZE_LARGE"
          case TCP_NODELAY                => "CURLOPT_TCP_NODELAY"
          case FTPSSLAUTH                 => "CURLOPT_FTPSSLAUTH"
          case IOCTLFUNCTION              => "CURLOPT_IOCTLFUNCTION"
          case IOCTLDATA                  => "CURLOPT_IOCTLDATA"
          case FTP_ACCOUNT                => "CURLOPT_FTP_ACCOUNT"
          case COOKIELIST                 => "CURLOPT_COOKIELIST"
          case IGNORE_CONTENT_LENGTH      => "CURLOPT_IGNORE_CONTENT_LENGTH"
          case FTP_SKIP_PASV_IP           => "CURLOPT_FTP_SKIP_PASV_IP"
          case FTP_FILEMETHOD             => "CURLOPT_FTP_FILEMETHOD"
          case LOCALPORT                  => "CURLOPT_LOCALPORT"
          case LOCALPORTRANGE             => "CURLOPT_LOCALPORTRANGE"
          case CONNECT_ONLY               => "CURLOPT_CONNECT_ONLY"
          case CONV_FROM_NETWORK_FUNCTION => "CURLOPT_CONV_FROM_NETWORK_FUNCTION"
          case CONV_TO_NETWORK_FUNCTION   => "CURLOPT_CONV_TO_NETWORK_FUNCTION"
          case CONV_FROM_UTF8_FUNCTION    => "CURLOPT_CONV_FROM_UTF8_FUNCTION"
          case MAX_SEND_SPEED_LARGE       => "CURLOPT_MAX_SEND_SPEED_LARGE"
          case MAX_RECV_SPEED_LARGE       => "CURLOPT_MAX_RECV_SPEED_LARGE"
          case FTP_ALTERNATIVE_TO_USER    => "CURLOPT_FTP_ALTERNATIVE_TO_USER"
          case SOCKOPTFUNCTION            => "CURLOPT_SOCKOPTFUNCTION"
          case SOCKOPTDATA                => "CURLOPT_SOCKOPTDATA"
          case SSL_SESSIONID_CACHE        => "CURLOPT_SSL_SESSIONID_CACHE"
          case SSH_AUTH_TYPES             => "CURLOPT_SSH_AUTH_TYPES"
          case SSH_PUBLIC_KEYFILE         => "CURLOPT_SSH_PUBLIC_KEYFILE"
          case SSH_PRIVATE_KEYFILE        => "CURLOPT_SSH_PRIVATE_KEYFILE"
          case FTP_SSL_CCC                => "CURLOPT_FTP_SSL_CCC"
          case TIMEOUT_MS                 => "CURLOPT_TIMEOUT_MS"
          case CONNECTTIMEOUT_MS          => "CURLOPT_CONNECTTIMEOUT_MS"
          case HTTP_TRANSFER_DECODING     => "CURLOPT_HTTP_TRANSFER_DECODING"
          case HTTP_CONTENT_DECODING      => "CURLOPT_HTTP_CONTENT_DECODING"
          case NEW_FILE_PERMS             => "CURLOPT_NEW_FILE_PERMS"
          case NEW_DIRECTORY_PERMS        => "CURLOPT_NEW_DIRECTORY_PERMS"
          case POSTREDIR                  => "CURLOPT_POSTREDIR"
          case SSH_HOST_PUBLIC_KEY_MD5    => "CURLOPT_SSH_HOST_PUBLIC_KEY_MD5"
          case OPENSOCKETFUNCTION         => "CURLOPT_OPENSOCKETFUNCTION"
          case OPENSOCKETDATA             => "CURLOPT_OPENSOCKETDATA"
          case COPYPOSTFIELDS             => "CURLOPT_COPYPOSTFIELDS"
          case PROXY_TRANSFER_MODE        => "CURLOPT_PROXY_TRANSFER_MODE"
          case SEEKFUNCTION               => "CURLOPT_SEEKFUNCTION"
          case SEEKDATA                   => "CURLOPT_SEEKDATA"
          case CRLFILE                    => "CURLOPT_CRLFILE"
          case ISSUERCERT                 => "CURLOPT_ISSUERCERT"
          case ADDRESS_SCOPE              => "CURLOPT_ADDRESS_SCOPE"
          case CERTINFO                   => "CURLOPT_CERTINFO"
          case USERNAME                   => "CURLOPT_USERNAME"
          case PASSWORD                   => "CURLOPT_PASSWORD"
          case PROXYUSERNAME              => "CURLOPT_PROXYUSERNAME"
          case PROXYPASSWORD              => "CURLOPT_PROXYPASSWORD"
          case NOPROXY                    => "CURLOPT_NOPROXY"
          case TFTP_BLKSIZE               => "CURLOPT_TFTP_BLKSIZE"
          case SOCKS5_GSSAPI_SERVICE      => "CURLOPT_SOCKS5_GSSAPI_SERVICE"
          case SOCKS5_GSSAPI_NEC          => "CURLOPT_SOCKS5_GSSAPI_NEC"
          case PROTOCOLS                  => "CURLOPT_PROTOCOLS"
          case REDIR_PROTOCOLS            => "CURLOPT_REDIR_PROTOCOLS"
          case SSH_KNOWNHOSTS             => "CURLOPT_SSH_KNOWNHOSTS"
          case SSH_KEYFUNCTION            => "CURLOPT_SSH_KEYFUNCTION"
          case SSH_KEYDATA                => "CURLOPT_SSH_KEYDATA"
          case MAIL_FROM                  => "CURLOPT_MAIL_FROM"
          case MAIL_RCPT                  => "CURLOPT_MAIL_RCPT"
          case FTP_USE_PRET               => "CURLOPT_FTP_USE_PRET"
          case RTSP_REQUEST               => "CURLOPT_RTSP_REQUEST"
          case RTSP_SESSION_ID            => "CURLOPT_RTSP_SESSION_ID"
          case RTSP_STREAM_URI            => "CURLOPT_RTSP_STREAM_URI"
          case RTSP_TRANSPORT             => "CURLOPT_RTSP_TRANSPORT"
          case RTSP_CLIENT_CSEQ           => "CURLOPT_RTSP_CLIENT_CSEQ"
          case RTSP_SERVER_CSEQ           => "CURLOPT_RTSP_SERVER_CSEQ"
          case INTERLEAVEDATA             => "CURLOPT_INTERLEAVEDATA"
          case INTERLEAVEFUNCTION         => "CURLOPT_INTERLEAVEFUNCTION"
          case WILDCARDMATCH              => "CURLOPT_WILDCARDMATCH"
          case CHUNK_BGN_FUNCTION         => "CURLOPT_CHUNK_BGN_FUNCTION"
          case CHUNK_END_FUNCTION         => "CURLOPT_CHUNK_END_FUNCTION"
          case FNMATCH_FUNCTION           => "CURLOPT_FNMATCH_FUNCTION"
          case CHUNK_DATA                 => "CURLOPT_CHUNK_DATA"
          case FNMATCH_DATA               => "CURLOPT_FNMATCH_DATA"
          case RESOLVE                    => "CURLOPT_RESOLVE"
          case TLSAUTH_USERNAME           => "CURLOPT_TLSAUTH_USERNAME"
          case TLSAUTH_PASSWORD           => "CURLOPT_TLSAUTH_PASSWORD"
          case TLSAUTH_TYPE               => "CURLOPT_TLSAUTH_TYPE"
          case TRANSFER_ENCODING          => "CURLOPT_TRANSFER_ENCODING"
          case CLOSESOCKETFUNCTION        => "CURLOPT_CLOSESOCKETFUNCTION"
          case CLOSESOCKETDATA            => "CURLOPT_CLOSESOCKETDATA"
          case GSSAPI_DELEGATION          => "CURLOPT_GSSAPI_DELEGATION"
          case DNS_SERVERS                => "CURLOPT_DNS_SERVERS"
          case ACCEPTTIMEOUT_MS           => "CURLOPT_ACCEPTTIMEOUT_MS"
          case TCP_KEEPALIVE              => "CURLOPT_TCP_KEEPALIVE"
          case TCP_KEEPIDLE               => "CURLOPT_TCP_KEEPIDLE"
          case TCP_KEEPINTVL              => "CURLOPT_TCP_KEEPINTVL"
          case SSL_OPTIONS                => "CURLOPT_SSL_OPTIONS"
          case MAIL_AUTH                  => "CURLOPT_MAIL_AUTH"
          case SASL_IR                    => "CURLOPT_SASL_IR"
          case XFERINFOFUNCTION           => "CURLOPT_XFERINFOFUNCTION"
          case XOAUTH2_BEARER             => "CURLOPT_XOAUTH2_BEARER"
          case DNS_INTERFACE              => "CURLOPT_DNS_INTERFACE"
          case DNS_LOCAL_IP4              => "CURLOPT_DNS_LOCAL_IP4"
          case DNS_LOCAL_IP6              => "CURLOPT_DNS_LOCAL_IP6"
          case LOGIN_OPTIONS              => "CURLOPT_LOGIN_OPTIONS"
          case SSL_ENABLE_NPN             => "CURLOPT_SSL_ENABLE_NPN"
          case SSL_ENABLE_ALPN            => "CURLOPT_SSL_ENABLE_ALPN"
          case EXPECT_100_TIMEOUT_MS      => "CURLOPT_EXPECT_100_TIMEOUT_MS"
          case PROXYHEADER                => "CURLOPT_PROXYHEADER"
          case HEADEROPT                  => "CURLOPT_HEADEROPT"
          case PINNEDPUBLICKEY            => "CURLOPT_PINNEDPUBLICKEY"
          case UNIX_SOCKET_PATH           => "CURLOPT_UNIX_SOCKET_PATH"
          case SSL_VERIFYSTATUS           => "CURLOPT_SSL_VERIFYSTATUS"
          case SSL_FALSESTART             => "CURLOPT_SSL_FALSESTART"
          case PATH_AS_IS                 => "CURLOPT_PATH_AS_IS"
          case PROXY_SERVICE_NAME         => "CURLOPT_PROXY_SERVICE_NAME"
          case SERVICE_NAME               => "CURLOPT_SERVICE_NAME"
          case PIPEWAIT                   => "CURLOPT_PIPEWAIT"
          case DEFAULT_PROTOCOL           => "CURLOPT_DEFAULT_PROTOCOL"
          case STREAM_WEIGHT              => "CURLOPT_STREAM_WEIGHT"
          case STREAM_DEPENDS             => "CURLOPT_STREAM_DEPENDS"
          case STREAM_DEPENDS_E           => "CURLOPT_STREAM_DEPENDS_E"
          case TFTP_NO_OPTIONS            => "CURLOPT_TFTP_NO_OPTIONS"
          case CONNECT_TO                 => "CURLOPT_CONNECT_TO"
          case TCP_FASTOPEN               => "CURLOPT_TCP_FASTOPEN"
          case KEEP_SENDING_ON_ERROR      => "CURLOPT_KEEP_SENDING_ON_ERROR"
          case PROXY_CAINFO               => "CURLOPT_PROXY_CAINFO"
          case PROXY_CAPATH               => "CURLOPT_PROXY_CAPATH"
          case PROXY_SSL_VERIFYPEER       => "CURLOPT_PROXY_SSL_VERIFYPEER"
          case PROXY_SSL_VERIFYHOST       => "CURLOPT_PROXY_SSL_VERIFYHOST"
          case PROXY_SSLVERSION           => "CURLOPT_PROXY_SSLVERSION"
          case PROXY_TLSAUTH_USERNAME     => "CURLOPT_PROXY_TLSAUTH_USERNAME"
          case PROXY_TLSAUTH_PASSWORD     => "CURLOPT_PROXY_TLSAUTH_PASSWORD"
          case PROXY_TLSAUTH_TYPE         => "CURLOPT_PROXY_TLSAUTH_TYPE"
          case PROXY_SSLCERT              => "CURLOPT_PROXY_SSLCERT"
          case PROXY_SSLCERTTYPE          => "CURLOPT_PROXY_SSLCERTTYPE"
          case PROXY_SSLKEY               => "CURLOPT_PROXY_SSLKEY"
          case PROXY_SSLKEYTYPE           => "CURLOPT_PROXY_SSLKEYTYPE"
          case PROXY_KEYPASSWD            => "CURLOPT_PROXY_KEYPASSWD"
          case PROXY_SSL_CIPHER_LIST      => "CURLOPT_PROXY_SSL_CIPHER_LIST"
          case PROXY_CRLFILE              => "CURLOPT_PROXY_CRLFILE"
          case PROXY_SSL_OPTIONS          => "CURLOPT_PROXY_SSL_OPTIONS"
          case PRE_PROXY                  => "CURLOPT_PRE_PROXY"
          case PROXY_PINNEDPUBLICKEY      => "CURLOPT_PROXY_PINNEDPUBLICKEY"
          case ABSTRACT_UNIX_SOCKET       => "CURLOPT_ABSTRACT_UNIX_SOCKET"
          case SUPPRESS_CONNECT_HEADERS   => "CURLOPT_SUPPRESS_CONNECT_HEADERS"
          case REQUEST_TARGET             => "CURLOPT_REQUEST_TARGET"
          case SOCKS5_AUTH                => "CURLOPT_SOCKS5_AUTH"
          case SSH_COMPRESSION            => "CURLOPT_SSH_COMPRESSION"
          case MIMEPOST                   => "CURLOPT_MIMEPOST"
          case TIMEVALUE_LARGE            => "CURLOPT_TIMEVALUE_LARGE"
          case HAPPY_EYEBALLS_TIMEOUT_MS  => "CURLOPT_HAPPY_EYEBALLS_TIMEOUT_MS"
          case RESOLVER_START_FUNCTION    => "CURLOPT_RESOLVER_START_FUNCTION"
          case RESOLVER_START_DATA        => "CURLOPT_RESOLVER_START_DATA"
          case HAPROXYPROTOCOL            => "CURLOPT_HAPROXYPROTOCOL"
          case DNS_SHUFFLE_ADDRESSES      => "CURLOPT_DNS_SHUFFLE_ADDRESSES"
          case TLS13_CIPHERS              => "CURLOPT_TLS13_CIPHERS"
          case PROXY_TLS13_CIPHERS        => "CURLOPT_PROXY_TLS13_CIPHERS"
          case DISALLOW_USERNAME_IN_URL   => "CURLOPT_DISALLOW_USERNAME_IN_URL"
          case DOH_URL                    => "CURLOPT_DOH_URL"
          case UPLOAD_BUFFERSIZE          => "CURLOPT_UPLOAD_BUFFERSIZE"
          case UPKEEP_INTERVAL_MS         => "CURLOPT_UPKEEP_INTERVAL_MS"
          case CURLU                      => "CURLOPT_CURLU"
          case TRAILERFUNCTION            => "CURLOPT_TRAILERFUNCTION"
          case TRAILERDATA                => "CURLOPT_TRAILERDATA"
          case HTTP09_ALLOWED             => "CURLOPT_HTTP09_ALLOWED"
          case ALTSVC_CTRL                => "CURLOPT_ALTSVC_CTRL"
          case ALTSVC                     => "CURLOPT_ALTSVC"
          case MAXAGE_CONN                => "CURLOPT_MAXAGE_CONN"
          case SASL_AUTHZID               => "CURLOPT_SASL_AUTHZID"
          case MAIL_RCPT_ALLOWFAILS       => "CURLOPT_MAIL_RCPT_ALLOWFAILS"
          case SSLCERT_BLOB               => "CURLOPT_SSLCERT_BLOB"
          case SSLKEY_BLOB                => "CURLOPT_SSLKEY_BLOB"
          case PROXY_SSLCERT_BLOB         => "CURLOPT_PROXY_SSLCERT_BLOB"
          case PROXY_SSLKEY_BLOB          => "CURLOPT_PROXY_SSLKEY_BLOB"
          case ISSUERCERT_BLOB            => "CURLOPT_ISSUERCERT_BLOB"
          case PROXY_ISSUERCERT           => "CURLOPT_PROXY_ISSUERCERT"
          case PROXY_ISSUERCERT_BLOB      => "CURLOPT_PROXY_ISSUERCERT_BLOB"
          case SSL_EC_CURVES              => "CURLOPT_SSL_EC_CURVES"
          case HSTS_CTRL                  => "CURLOPT_HSTS_CTRL"
          case HSTS                       => "CURLOPT_HSTS"
          case HSTSREADFUNCTION           => "CURLOPT_HSTSREADFUNCTION"
          case HSTSREADDATA               => "CURLOPT_HSTSREADDATA"
          case HSTSWRITEFUNCTION          => "CURLOPT_HSTSWRITEFUNCTION"
          case HSTSWRITEDATA              => "CURLOPT_HSTSWRITEDATA"
          case AWS_SIGV4                  => "CURLOPT_AWS_SIGV4"
          case DOH_SSL_VERIFYPEER         => "CURLOPT_DOH_SSL_VERIFYPEER"
          case DOH_SSL_VERIFYHOST         => "CURLOPT_DOH_SSL_VERIFYHOST"
          case DOH_SSL_VERIFYSTATUS       => "CURLOPT_DOH_SSL_VERIFYSTATUS"
          case CAINFO_BLOB                => "CURLOPT_CAINFO_BLOB"
          case PROXY_CAINFO_BLOB          => "CURLOPT_PROXY_CAINFO_BLOB"
          case SSH_HOST_PUBLIC_KEY_SHA256 => "CURLOPT_SSH_HOST_PUBLIC_KEY_SHA256"
          case PREREQFUNCTION             => "CURLOPT_PREREQFUNCTION"
          case PREREQDATA                 => "CURLOPT_PREREQDATA"
          case MAXLIFETIME_CONN           => "CURLOPT_MAXLIFETIME_CONN"
          case MIME_OPTIONS               => "CURLOPT_MIME_OPTIONS"
          case SSH_HOSTKEYFUNCTION        => "CURLOPT_SSH_HOSTKEYFUNCTION"
          case SSH_HOSTKEYDATA            => "CURLOPT_SSH_HOSTKEYDATA"
          case PROTOCOLS_STR              => "CURLOPT_PROTOCOLS_STR"
          case REDIR_PROTOCOLS_STR        => "CURLOPT_REDIR_PROTOCOLS_STR"
          case WS_OPTIONS                 => "CURLOPT_WS_OPTIONS"
          case CA_CACHE_TIMEOUT           => "CURLOPT_CA_CACHE_TIMEOUT"
          case QUICK_EXIT                 => "CURLOPT_QUICK_EXIT"
          case HAPROXY_CLIENT_IP          => "CURLOPT_HAPROXY_CLIENT_IP"
          case LASTENTRY                  => "CURLOPT_LASTENTRY"

  end CurlOption

  /**
   * Below here follows defines for the CURLOPT_IPRESOLVE option. If a host name resolves addresses using more than one
   * IP protocol version, this option might be handy to force libcurl to use a specific IP version.
   */
  // known as "CURL_IPRESOLVE_*"
  opaque type CurlIpResolve = CLong
  object CurlIpResolve extends _BindgenEnumCLong[CurlIpResolve]:

    given Tag[CurlIpResolve] = Tag.Size

    private inline def define(inline a: Long): CurlIpResolve = a.toSize

    /* default, uses addresses to all IP versions that your system allows */
    val WHATEVER = define(0L)
    /* uses only IPv4 addresses/connections */
    val V4 = define(1L)
    /* uses only IPv6 addresses/connections */
    val V6 = define(2L)

  end CurlIpResolve

  // type CurlOpt_RtspHeader = CurlOpt_HttpHeader

  // known as "CURLOPT_HTTP_VERSION"
  /** These enums are for use with the `CURLOPT_HTTP_VERSION` option. */
  opaque type CurlHttpVersion = CLong
  object CurlHttpVersion extends _BindgenEnumCLong[CurlHttpVersion]:

    given Tag[CurlHttpVersion] = Tag.Size

    private inline def define(inline a: Long): CurlHttpVersion = a.toSize

    /* setting this means we don't care, and that we'd like the library to
       choose the best possible for us! */
    val VERSION_NONE = define(0L)
    /* please use HTTP 1.0 in the request */
    val VERSION_1_0 = define(1L)
    /* please use HTTP 1.1 in the request */
    val VERSION_1_1 = define(2L)
    /* please use HTTP 2 in the request */
    val VERSION_2_0 = define(3L)
    /* use version 2 for HTTPS, version 1.1 for HTTP */
    val VERSION_2TLS = define(4L)
    /* please use HTTP 2 without HTTP/1.1 Upgrade */
    val VERSION_2_PRIOR_KNOWLEDGE = define(5L)
    /* Use HTTP/3, fallback to HTTP/2 or HTTP/1 if needed. For HTTPS only.
       For HTTP, this option makes libcurl return error. */
    val VERSION_3 = define(30L)
    /* Use HTTP/3 without fallback. For HTTPS only. For HTTP, this makes libcurl return error. */
    val VERSION_3ONLY = define(31L)
    /* ILLEGAL http version */
    val VERSION_LAST = define(32L)

  end CurlHttpVersion

  // TODO: add enum `CURL_RTSPREQ_*`

  // TODO: Add enum `CURL_NETRC_OPTION`

  // known as "CURL_SSLVERSION"
  opaque type CurlSslVersion = CLong
  object CurlSslVersion extends _BindgenEnumCLong[CurlSslVersion]:

    given Tag[CurlSslVersion] = Tag.Size

    private inline def define(inline a: Long): CurlSslVersion = a.toSize

    val DEFAULT = define(0L)
    val TLSv1   = define(1L) // TLS 1.x
    val SSLv2   = define(2L)
    val SSLv3   = define(3L)
    val TLSv1_0 = define(4L)
    val TLSv1_1 = define(5L)
    val TLSv1_2 = define(6L)
    val TLSv1_3 = define(7L)
    val LAST    = define(8L) // never use, keep last

  end CurlSslVersion

  // known as "CURL_SSLVERSION_MAX_*"
  opaque type CurlSslMaxVersion = CLong
  object CurlSslMaxVersion extends _BindgenEnumCLong[CurlSslMaxVersion]:

    given Tag[CurlSslMaxVersion] = Tag.Size

    private inline def define(inline a: Long): CurlSslMaxVersion = a.toSize

    val MAX_NONE    = define(0L)
    val MAX_DEFAULT = define(CurlSslVersion.TLSv1.toLong << 16L)
    val MAX_TLSv1_0 = define(CurlSslVersion.TLSv1_0.toLong << 16L)
    val MAX_TLSv1_1 = define(CurlSslVersion.TLSv1_1.toLong << 16L)
    val MAX_TLSv1_2 = define(CurlSslVersion.TLSv1_2.toLong << 16L)
    val MAX_TLSv1_3 = define(CurlSslVersion.TLSv1_3.toLong << 16L)
    val MAX_LAST    = define(CurlSslVersion.LAST.toLong << 16L) // never use, keep last

  end CurlSslMaxVersion

  // known as "CURL_TLSAUTH"
  opaque type CurlTlsAuth = CLong
  object CurlTlsAuth extends _BindgenEnumCLong[CurlTlsAuth]:

    given Tag[CurlTlsAuth] = Tag.Size

    private inline def define(inline a: Long): CurlTlsAuth = a.toSize

    val NONE = define(0L)
    val SRP  = define(1L)
    /* we set a single member here, just to make sure we still provide the enum,
     * but the values to use are defined above with L suffixes
     */
    val LAST = define(2)

    def getname(value: CurlTlsAuth): Option[String] =
      value match
        case NONE => Some("CURL_TLSAUTH_NONE")
        case SRP  => Some("CURL_TLSAUTH_SRP")
        case LAST => Some("CURL_TLSAUTH_LAST")
        case _    => None

  end CurlTlsAuth

  /**
   * symbols to use with CURLOPT_POSTREDIR.
   *
   * CURL_REDIR_POST_301, CURL_REDIR_POST_302 and CURL_REDIR_POST_303
   * can be bitwise ORed so that
   *
   *  CURL_REDIR_POST_301 | CURL_REDIR_POST_302
   *   | CURL_REDIR_POST_303 == CURL_REDIR_POST_ALL
   */
  // known as "CURL_REDIR_*"
  opaque type CurlRedir = CLong
  object CurlRedir extends _BindgenEnumCLong[CurlRedir]:

    given Tag[CurlRedir] = Tag.Size

    private inline def define(inline a: Long): CurlRedir = a.toSize

    val GET_ALL  = define(0L)
    val POST_301 = define(1L)
    val POST_302 = define(2L)
    val POST_303 = define(4L)
    val POST_ALL = POST_301 | POST_302 | POST_303

  end CurlRedir

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

  // known as "curl_slist"
  /** linked-list structure for the CURLOPT_QUOTE option (and other) */
  opaque type CurlSlist = CStruct2[
    /** data */
    CString,
    /** struct curl_slist* next */
    CVoidPtr,
  ]
  object CurlSlist:

    given Tag[CVoidPtr]  = Tag.materializePtrWildcard
    given Tag[CurlSlist] = Tag.materializeCStruct2Tag[CString, CVoidPtr]

    def apply(data: CString, next: Ptr[CurlSlist])(using Zone): Ptr[CurlSlist] =
      val ptr = alloc[CurlSlist](1)
      (!ptr).data = data
      (!ptr).next = next
      ptr

    extension (struct: CurlSlist)
      inline def data: CString                       = struct._1
      inline def data_=(value: CString): Unit        = !struct.at1 = value
      inline def next: Ptr[CurlSlist]                = struct._2.asInstanceOf[Ptr[CurlSlist]]
      inline def next_=(value: Ptr[CurlSlist]): Unit = !struct.at2 = value.asInstanceOf[Ptr[Byte]]

  end CurlSlist

  /**
   * NAME curl_global_sslset()
   *
   * DESCRIPTION
   *
   * When built with multiple SSL backends, curl_global_sslset() allows to choose one. This function can only be called
   * once, and it must be called *before* curl_global_init().
   *
   * The backend can be identified by the id (e.g. CURLSSLBACKEND_OPENSSL). The backend can also be specified via the
   * name parameter (passing -1 as id). If both id and name are specified, the name will be ignored. If neither id nor
   * name are specified, the function will fail with CURLSSLSET_UNKNOWN_BACKEND and set the "avail" pointer to the
   * NULL-terminated list of available backends.
   *
   * Upon success, the function returns CURLSSLSET_OK.
   *
   * If the specified SSL backend is not available, the function returns CURLSSLSET_UNKNOWN_BACKEND and sets the "avail"
   * pointer to a NULL-terminated list of available SSL backends.
   *
   * The SSL backend can be set only once. If it has already been set, a subsequent attempt to change it will result in
   * a CURLSSLSET_TOO_LATE.
   */
  // known as "curl_ssl_backend"
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
      inline def id: CurlSslBackendId                = struct._1
      inline def id_=(value: CurlSslBackendId): Unit = !struct.at1 = value
      inline def name: CString                       = struct._2
      inline def name_=(value: CString): Unit        = !struct.at2 = value

  end CurlSslBackend

  // known as "CURLSSLSET_*"
  opaque type CurlSslSet = Int
  object CurlSslSet extends _BindgenEnumCInt[CurlSslSet]:

    given Tag[CurlSslSet] = Tag.Int

    private inline def define(inline a: Int): CurlSslSet = a

    val OK              = define(0)
    val UNKNOWN_BACKEND = define(1)
    val TOO_LATE        = define(2)
    val NO_BACKENDS     = define(3) /* libcurl was built without any SSL support */

    def getname(value: CurlSslSet): Option[String] =
      value match
        case OK              => Some("CURLSSLSET_OK")
        case UNKNOWN_BACKEND => Some("CURLSSLSET_UNKNOWN_BACKEND")
        case TOO_LATE        => Some("CURLSSLSET_TOO_LATE")
        case NO_BACKENDS     => Some("CURLSSLSET_NO_BACKENDS")
        case _               => None

    extension (a: CurlSslSet)
      inline def &(b: CurlSslSet): CurlSslSet = a & b
      inline def |(b: CurlSslSet): CurlSslSet = a | b
      inline def is(b: CurlSslSet): Boolean   = (a & b) == b

  end CurlSslSet

  /**
   * info about the certificate chain, for SSL backends that support it. Asked for with CURLOPT_CERTINFO /
   * CURLINFO_CERTINFO
   */
  // known as "curl_certinfo"
  opaque type CurlCertInfo = CStruct2[
    /**
     * num_of_certs: number of certificates with information
     */
    Int,
    /**
     * certinfo: for each index in this array, there's a linked list with textual information for a certificate in the
     * format "name:content". eg "Subject:foo", "Issuer:bar", etc.
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
      inline def num_of_certs: Int                            = struct._1
      inline def num_of_certs_=(value: Int): Unit             = !struct.at1 = value
      inline def certinfo: Ptr[Ptr[CurlSlist]]                = struct._2.asInstanceOf[Ptr[Ptr[CurlSlist]]]
      inline def certinfo_=(value: Ptr[Ptr[CurlSlist]]): Unit = !struct.at2 = value.asInstanceOf[Ptr[Byte]]

  end CurlCertInfo

  /**
   * Information about the SSL library used and the respective internal SSL handle, which can be used to obtain further
   * information regarding the connection. Asked for with CURLINFO_TLS_SSL_PTR or CURLINFO_TLS_SESSION.
   */
  // known as "curl_tlssessioninfo" in C header
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
      inline def backend: CurlSslBackendId                = struct._1
      inline def backend_=(value: CurlSslBackendId): Unit = !struct.at1 = value
      inline def internals: Ptr[Byte]                     = struct._2
      inline def internals_=(value: Ptr[Byte]): Unit      = !struct.at2 = value

  end CurlTlsSessionInfo

  // known as "CURLINFO" in C header
  opaque type CurlInfo = CInt
  object CurlInfo extends _BindgenEnumCInt[CurlInfo]:

    given Tag[CurlInfo] = Tag.Int

    private inline def define(a: Int): CurlInfo = a

    // By `#define`
    private val STRING: CurlInfo   = define(0x100000)
    private val LONG: CurlInfo     = define(0x200000)
    private val DOUBLE: CurlInfo   = define(0x300000)
    private val SLIST: CurlInfo    = define(0x400000)
    private val PTR: CurlInfo      = define(0x400000)
    private val SOCKET: CurlInfo   = define(0x500000)
    private val OFF_T: CurlInfo    = define(0x600000)
    private val MASK: CurlInfo     = define(0x0fffff)
    private val TYPEMASK: CurlInfo = define(0xf00000)

    // By typedef enum
    val NONE             = define(0)
    val EFFECTIVE_URL    = define(STRING + 1)
    val RESPONSE_CODE    = define(LONG + 2)
    val TOTAL_TIME       = define(DOUBLE + 3)
    val NAMELOOKUP_TIME  = define(DOUBLE + 4)
    val CONNECT_TIME     = define(DOUBLE + 5)
    val PRETRANSFER_TIME = define(DOUBLE + 6)

    @deprecated("deprecated since libcurl 7.55.0. Use CurlInfo.SIZE_UPLOAD_T")
    val SIZE_UPLOAD   = define(DOUBLE + 7)
    val SIZE_UPLOAD_T = define(OFF_T + 7)

    @deprecated("deprecated since libcurl 7.55.0. Use CurlInfo.SIZE_DOWNLOAD_T")
    val SIZE_DOWNLOAD   = define(DOUBLE + 8)
    val SIZE_DOWNLOAD_T = define(OFF_T + 8)

    @deprecated("deprecated since libcurl 7.55.0. Use CurlInfo.SPEED_DOWNLOAD_T")
    val SPEED_DOWNLOAD   = define(DOUBLE + 9)
    val SPEED_DOWNLOAD_T = define(OFF_T + 9)

    @deprecated("deprecated since libcurl 7.55.0. Use CurlInfo.SPEED_UPLOAD_T")
    val SPEED_UPLOAD   = define(DOUBLE + 10)
    val SPEED_UPLOAD_T = define(OFF_T + 10)

    val HEADER_SIZE      = define(LONG + 11)
    val REQUEST_SIZE     = define(LONG + 12)
    val SSL_VERIFYRESULT = define(LONG + 13)
    val FILETIME         = define(LONG + 14)
    val FILETIME_T       = define(OFF_T + 14)

    @deprecated("deprecated since libcurl 7.55.0. Use CurlInfo.CONTENT_LENGTH_DOWNLOAD_T")
    val CONTENT_LENGTH_DOWNLOAD   = define(DOUBLE + 15)
    val CONTENT_LENGTH_DOWNLOAD_T = define(OFF_T + 15)

    @deprecated("deprecated since libcurl 7.55.0. Use CurlInfo.CONTENT_LENGTH_UPLOAD_T")
    val CONTENT_LENGTH_UPLOAD   = define(DOUBLE + 16)
    val CONTENT_LENGTH_UPLOAD_T = define(OFF_T + 16)

    val STARTTRANSFER_TIME = define(DOUBLE + 17)
    val CONTENT_TYPE       = define(STRING + 18)
    val REDIRECT_TIME      = define(DOUBLE + 19)
    val REDIRECT_COUNT     = define(LONG + 20)
    val PRIVATE            = define(STRING + 21)
    val HTTP_CONNECTCODE   = define(LONG + 22)
    val HTTPAUTH_AVAIL     = define(LONG + 23)
    val PROXYAUTH_AVAIL    = define(LONG + 24)
    val OS_ERRNO           = define(LONG + 25)
    val NUM_CONNECTS       = define(LONG + 26)
    val SSL_ENGINES        = SLIST + define(27)
    val COOKIELIST         = SLIST + define(28)

    @deprecated("deprecated since libcurl 7.45.0. Use CurlInfo.ACTIVESOCKET")
    val LASTSOCKET = define(LONG + 29)

    val FTP_ENTRY_PATH   = define(STRING + 30)
    val REDIRECT_URL     = define(STRING + 31)
    val PRIMARY_IP       = define(STRING + 32)
    val APPCONNECT_TIME  = define(DOUBLE + 33)
    val CERTINFO         = define(PTR + 34)
    val CONDITION_UNMET  = define(LONG + 35)
    val RTSP_SESSION_ID  = define(STRING + 36)
    val RTSP_CLIENT_CSEQ = define(LONG + 37)
    val RTSP_SERVER_CSEQ = define(LONG + 38)
    val RTSP_CSEQ_RECV   = define(LONG + 39)
    val PRIMARY_PORT     = define(LONG + 40)
    val LOCAL_IP         = define(STRING + 41)
    val LOCAL_PORT       = define(LONG + 42)

    @deprecated("deprecated since libcurl 7.48.0. Use CurlInfo.TLS_SSL_PTR")
    val TLS_SESSION = define(PTR + 43)

    val ACTIVESOCKET           = define(SOCKET + 44)
    val TLS_SSL_PTR            = define(PTR + 45)
    val HTTP_VERSION           = define(LONG + 46)
    val PROXY_SSL_VERIFYRESULT = define(LONG + 47)

    @deprecated("deprecated since libcurl 7.85.0. Use CurlInfo.SCHEME")
    val PROTOCOL = define(STRING + 48)

    val SCHEME               = define(STRING + 49)
    val TOTAL_TIME_T         = define(OFF_T + 50)
    val NAMELOOKUP_TIME_T    = define(OFF_T + 51)
    val CONNECT_TIME_T       = define(OFF_T + 52)
    val PRETRANSFER_TIME_T   = define(OFF_T + 53)
    val STARTTRANSFER_TIME_T = define(OFF_T + 54)
    val REDIRECT_TIME_T      = define(OFF_T + 55)
    val APPCONNECT_TIME_T    = define(OFF_T + 56)
    val RETRY_AFTER          = define(OFF_T + 57)
    val EFFECTIVE_METHOD     = define(STRING + 58)
    val PROXY_ERROR          = define(LONG + 59)
    val REFERER              = define(STRING + 60)
    val CAINFO               = define(STRING + 61)
    val CAPATH               = define(STRING + 62)
    val XFER_ID              = define(OFF_T + 63)
    val CONN_ID              = define(OFF_T + 64)
    val QUEUE_TIME_T         = define(OFF_T + 65)
    val USED_PROXY           = define(LONG + 66)
    val POSTTRANSFER_TIME_T  = define(OFF_T + 67)
    val EARLYDATA_SENT_T     = define(OFF_T + 68)
    val HTTPAUTH_USED        = define(LONG + 69)
    val PROXYAUTH_USED       = define(LONG + 70)
    val LASTONE              = define(70)

    /** alias */
    val HTTP_CODE = RESPONSE_CODE

    extension (value: CurlInfo)
      inline def getname: String =
        value match
          case NONE                      => "CURLINFO_NONE"
          case EFFECTIVE_URL             => "CURLINFO_EFFECTIVE_URL"
          case RESPONSE_CODE             => "CURLINFO_RESPONSE_CODE"
          case TOTAL_TIME                => "CURLINFO_TOTAL_TIME"
          case NAMELOOKUP_TIME           => "CURLINFO_NAMELOOKUP_TIME"
          case CONNECT_TIME              => "CURLINFO_CONNECT_TIME"
          case PRETRANSFER_TIME          => "CURLINFO_PRETRANSFER_TIME"
          case SIZE_UPLOAD               => "CURLINFO_SIZE_UPLOAD"
          case SIZE_UPLOAD_T             => "CURLINFO_SIZE_UPLOAD_T"
          case SIZE_DOWNLOAD             => "CURLINFO_SIZE_DOWNLOAD"
          case SIZE_DOWNLOAD_T           => "CURLINFO_SIZE_DOWNLOAD_T"
          case SPEED_DOWNLOAD            => "CURLINFO_SPEED_DOWNLOAD"
          case SPEED_DOWNLOAD_T          => "CURLINFO_SPEED_DOWNLOAD_T"
          case SPEED_UPLOAD              => "CURLINFO_SPEED_UPLOAD"
          case SPEED_UPLOAD_T            => "CURLINFO_SPEED_UPLOAD_T"
          case HEADER_SIZE               => "CURLINFO_HEADER_SIZE"
          case REQUEST_SIZE              => "CURLINFO_REQUEST_SIZE"
          case SSL_VERIFYRESULT          => "CURLINFO_SSL_VERIFYRESULT"
          case FILETIME                  => "CURLINFO_FILETIME"
          case FILETIME_T                => "CURLINFO_FILETIME_T"
          case CONTENT_LENGTH_DOWNLOAD   => "CURLINFO_CONTENT_LENGTH_DOWNLOAD"
          case CONTENT_LENGTH_DOWNLOAD_T => "CURLINFO_CONTENT_LENGTH_DOWNLOAD_T"
          case CONTENT_LENGTH_UPLOAD     => "CURLINFO_CONTENT_LENGTH_UPLOAD"
          case CONTENT_LENGTH_UPLOAD_T   => "CURLINFO_CONTENT_LENGTH_UPLOAD_T"
          case STARTTRANSFER_TIME        => "CURLINFO_STARTTRANSFER_TIME"
          case CONTENT_TYPE              => "CURLINFO_CONTENT_TYPE"
          case REDIRECT_TIME             => "CURLINFO_REDIRECT_TIME"
          case REDIRECT_COUNT            => "CURLINFO_REDIRECT_COUNT"
          case PRIVATE                   => "CURLINFO_PRIVATE"
          case HTTP_CONNECTCODE          => "CURLINFO_HTTP_CONNECTCODE"
          case HTTPAUTH_AVAIL            => "CURLINFO_HTTPAUTH_AVAIL"
          case PROXYAUTH_AVAIL           => "CURLINFO_PROXYAUTH_AVAIL"
          case OS_ERRNO                  => "CURLINFO_OS_ERRNO"
          case NUM_CONNECTS              => "CURLINFO_NUM_CONNECTS"
          case SSL_ENGINES               => "CURLINFO_SSL_ENGINES"
          case COOKIELIST                => "CURLINFO_COOKIELIST"
          case LASTSOCKET                => "CURLINFO_LASTSOCKET"
          case FTP_ENTRY_PATH            => "CURLINFO_FTP_ENTRY_PATH"
          case REDIRECT_URL              => "CURLINFO_REDIRECT_URL"
          case PRIMARY_IP                => "CURLINFO_PRIMARY_IP"
          case APPCONNECT_TIME           => "CURLINFO_APPCONNECT_TIME"
          case CERTINFO                  => "CURLINFO_CERTINFO"
          case CONDITION_UNMET           => "CURLINFO_CONDITION_UNMET"
          case RTSP_SESSION_ID           => "CURLINFO_RTSP_SESSION_ID"
          case RTSP_CLIENT_CSEQ          => "CURLINFO_RTSP_CLIENT_CSEQ"
          case RTSP_SERVER_CSEQ          => "CURLINFO_RTSP_SERVER_CSEQ"
          case RTSP_CSEQ_RECV            => "CURLINFO_RTSP_CSEQ_RECV"
          case PRIMARY_PORT              => "CURLINFO_PRIMARY_PORT"
          case LOCAL_IP                  => "CURLINFO_LOCAL_IP"
          case LOCAL_PORT                => "CURLINFO_LOCAL_PORT"
          case TLS_SESSION               => "CURLINFO_TLS_SESSION"
          case ACTIVESOCKET              => "CURLINFO_ACTIVESOCKET"
          case TLS_SSL_PTR               => "CURLINFO_TLS_SSL_PTR"
          case HTTP_VERSION              => "CURLINFO_HTTP_VERSION"
          case PROXY_SSL_VERIFYRESULT    => "CURLINFO_PROXY_SSL_VERIFYRESULT"
          case PROTOCOL                  => "CURLINFO_PROTOCOL"
          case SCHEME                    => "CURLINFO_SCHEME"
          case TOTAL_TIME_T              => "CURLINFO_TOTAL_TIME_T"
          case NAMELOOKUP_TIME_T         => "CURLINFO_NAMELOOKUP_TIME_T"
          case CONNECT_TIME_T            => "CURLINFO_CONNECT_TIME_T"
          case PRETRANSFER_TIME_T        => "CURLINFO_PRETRANSFER_TIME_T"
          case STARTTRANSFER_TIME_T      => "CURLINFO_STARTTRANSFER_TIME_T"
          case REDIRECT_TIME_T           => "CURLINFO_REDIRECT_TIME_T"
          case APPCONNECT_TIME_T         => "CURLINFO_APPCONNECT_TIME_T"
          case RETRY_AFTER               => "CURLINFO_RETRY_AFTER"
          case EFFECTIVE_METHOD          => "CURLINFO_EFFECTIVE_METHOD"
          case PROXY_ERROR               => "CURLINFO_PROXY_ERROR"
          case REFERER                   => "CURLINFO_REFERER"
          case CAINFO                    => "CURLINFO_CAINFO"
          case CAPATH                    => "CURLINFO_CAPATH"
          case XFER_ID                   => "CURLINFO_XFER_ID"
          case CONN_ID                   => "CURLINFO_CONN_ID"
          case LASTONE                   => "CURLINFO_LASTONE"
          case HTTP_CODE                 => "CURLINFO_RESPONSE_CODE"

  end CurlInfo

  // known as enum "curl_closepolicy"
  opaque type CurlClosePolicy = Int
  object CurlClosePolicy extends _BindgenEnumCInt[CurlClosePolicy]:

    given Tag[CurlClosePolicy] = Tag.Int

    private inline def define(inline a: Int): CurlClosePolicy = a

    val NONE                = define(0) // no purpose since curl 7.57.0
    val OLDEST              = define(1)
    val LEAST_RECENTLY_USED = define(2)
    val LEAST_TRAFFIC       = define(3)
    val SLOWEST             = define(4)
    val CALLBACK            = define(5)
    val LAST                = define(6)

    def getname(value: CurlClosePolicy): Option[String] =
      value match
        case NONE                => Some("CURLCLOSEPOLICY_NONE")
        case OLDEST              => Some("CURLCLOSEPOLICY_OLDEST")
        case LEAST_RECENTLY_USED => Some("CURLCLOSEPOLICY_LEAST_RECENTLY_USED")
        case LEAST_TRAFFIC       => Some("CURLCLOSEPOLICY_LEAST_TRAFFIC")
        case SLOWEST             => Some("CURLCLOSEPOLICY_SLOWEST")
        case CALLBACK            => Some("CURLCLOSEPOLICY_CALLBACK")
        case LAST                => Some("CURLCLOSEPOLICY_LAST")
        case _                   => None

  end CurlClosePolicy

  // known as "curl_global_flag"
  opaque type CurlGlobalFlag = CLong
  object CurlGlobalFlag extends _BindgenEnumCLong[CurlGlobalFlag]:

    given Tag[CurlGlobalFlag] = Tag.Size

    private inline def define(inline a: Int): CurlGlobalFlag = a.toSize

    val SSL       = define(1 << 0) // no purpose since curl 7.57.0
    val WIN32     = define(1 << 1)
    val ALL       = SSL | WIN32
    val NOTHING   = define(0)
    val DEFAULT   = SSL
    val ACK_EINTR = define(1 << 2)

  end CurlGlobalFlag

  // TODO:
  //
  // 1. add typedef enum `curl_lock_data`
  // 2. add typedef enum `curl_lock_access`
  // 3. add func `curl_lock_function`
  // 4. add func `curl_unlock_function`

  // known as "CURLSHcode" in C header
  opaque type CurlShareErrCode = CInt
  object CurlShareErrCode extends _BindgenEnumCInt[CurlShareErrCode]:

    given Tag[CurlShareErrCode] = Tag.Int

    private inline def define(inline a: Int): CurlShareErrCode = a

    val OK           = define(0)
    val BAD_OPTION   = define(1)
    val IN_USE       = define(2)
    val INVALID      = define(3)
    val NOMEM        = define(4)
    val NOT_BUILT_IN = define(5)
    val LAST         = define(6)

    extension (value: CurlShareErrCode)
      inline def getName: String =
        value match
          case OK           => "CURLSHE_OK"
          case BAD_OPTION   => "CURLSHE_BAD_OPTION"
          case IN_USE       => "CURLSHE_IN_USE"
          case INVALID      => "CURLSHE_INVALID"
          case NOMEM        => "CURLSHE_NOMEM"
          case NOT_BUILT_IN => "CURLSHE_NOT_BUILT_IN"
          case LAST         => "CURLSHE_LAST"

  end CurlShareErrCode

  // known as "CURLSHoption" in C header
  opaque type CurlShareOption = CInt
  object CurlShareOption extends _BindgenEnumCInt[CurlShareOption]:

    given _tag: Tag[CurlShareOption] = Tag.Int

    private inline def define(inline a: Int): CurlShareOption = a

    val CURLSHOPT_NONE       = define(0)
    val CURLSHOPT_SHARE      = define(1)
    val CURLSHOPT_UNSHARE    = define(2)
    val CURLSHOPT_LOCKFUNC   = define(3)
    val CURLSHOPT_UNLOCKFUNC = define(4)
    val CURLSHOPT_USERDATA   = define(5)
    val CURLSHOPT_LAST       = define(6)

    // def getName(value: CurlShareOption): Option[String] =
    //   value match
    //     case `CURLSHOPT_NONE`       => Some("CURLSHOPT_NONE")
    //     case `CURLSHOPT_SHARE`      => Some("CURLSHOPT_SHARE")
    //     case `CURLSHOPT_UNSHARE`    => Some("CURLSHOPT_UNSHARE")
    //     case `CURLSHOPT_LOCKFUNC`   => Some("CURLSHOPT_LOCKFUNC")
    //     case `CURLSHOPT_UNLOCKFUNC` => Some("CURLSHOPT_UNLOCKFUNC")
    //     case `CURLSHOPT_USERDATA`   => Some("CURLSHOPT_USERDATA")
    //     case `CURLSHOPT_LAST`       => Some("CURLSHOPT_LAST")
    //     case _                      => _root_.scala.None
    // extension (a: CurlShareOption)
    //   inline def &(b: CurlShareOption): CurlShareOption = a & b
    //   inline def |(b: CurlShareOption): CurlShareOption = a | b
    //   inline def is(b: CurlShareOption): Boolean = (a & b) == b

  end CurlShareOption

  // TODO:
  //
  // 1. add typedef enum `CURLversion`
  // 2. add struct `curl_version_info_data`
  // 3. add define symbols `CURL_VERSION_*`
  // 4. add func `curl_version_info`

  // TODO: add func `curl_share_strerror`

  // known as "CURLPAUSE_*"
  opaque type CurlPause = Int
  object CurlPause extends _BindgenEnumCInt[CurlPause]:

    given Tag[CurlPause] = Tag.Int

    private inline def define(inline a: Int): CurlPause = a

    val RECV      = define(1 << 0)
    val RECV_CONT = define(0)
    val SEND      = define(1 << 2)
    val SEND_CONT = define(0)
    val ALL       = RECV | SEND
    val CONT      = RECV_CONT | SEND_CONT

  opaque type SockAddrFamily = Int
  object SockAddrFamily:

    given Tag[SockAddrFamily] = Tag.Int

    private inline def define(inline v: Int): SockAddrFamily = v

    val AF_INET   = define(socket.AF_INET)
    val AF_INET6  = define(socket.AF_INET6)
    val AF_UNIX   = define(socket.AF_UNIX)
    val AF_UNSPEC = define(socket.AF_UNSPEC)

  end SockAddrFamily
