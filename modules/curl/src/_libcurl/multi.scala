/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/4108d11008030c5c25adb28bf73ba392f80708a8/include/curl/multi.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */
package snhttp.experimental._libcurl

import scalanative.unsafe.alloc
import scalanative.unsafe.{
  Zone,
  Tag,
  Ptr,
  CLong,
  CVoidPtr,
  Size,
  CStruct0,
  CStruct3,
  CFuncPtr3,
  CFuncPtr5,
  CVarArgList,
  UnsafeRichLong,
}
import scalanative.posix.stddef.size_t

import snhttp.experimental._libcurl.curl.{Curl, CurlErrCode, CurlSocket}
import snhttp.experimental._libcurl.internal.{_BindgenEnumInt, _BindgenEnumCLong}

object multi:

  // known as "CURLM"
  opaque type CurlMulti = Unit // CVoidPtr
  object CurlMulti:
    given Tag[CurlMulti] = Tag.Unit

  // known as "CURLMcode"
  type CurlMultiCode = Int
  object CurlMultiCode extends _BindgenEnumInt[CurlMultiCode]:
    given Tag[CurlMultiCode] = Tag.Int

    inline def define(inline a: Int): CurlMultiCode = a

    /* please call curl_multi_perform() or curl_multi_socket*() soon */
    val CALL_MULTI_PERFORM = define(-1)
    val OK = define(0)
    /* the passed-in handle is not a valid CURLM handle */
    val BAD_HANDLE = define(1)
    /* an easy handle was not good/valid */
    val BAD_EASY_HANDLE = define(2)
    /* if you ever get this, you're in deep sh*t */
    val OUT_OF_MEMORY = define(3)
    /* this is a libcurl bug */
    val INTERNAL_ERROR = define(4)
    /* the passed in socket argument did not match */
    val BAD_SOCKET = define(5)
    /* curl_multi_setopt() with unsupported option */
    val UNKNOWN_OPTION = define(6)
    /* an easy handle already added to a multi handle was attempted to get added - again */
    val ADDED_ALREADY = define(7)
    /* an api function was called from inside a callback */
    val RECURSIVE_API_CALL = define(8)
    /* wakeup is unavailable or failed */
    val WAKEUP_FAILURE = define(9)
    /* function called with a bad parameter */
    val BAD_FUNCTION_ARGUMENT = define(10)
    val ABORTED_BY_CALLBACK = define(11)
    val UNRECOVERABLE_POLL = define(12)
    val LAST = define(13)

    implicit class RichCurlMultiCode(value: CurlMultiCode) extends AnyVal:
      def getName: String =
        value match
          case CALL_MULTI_PERFORM    => "CURLM_CALL_MULTI_PERFORM"
          case OK                    => "CURLM_OK"
          case BAD_HANDLE            => "CURLM_BAD_HANDLE"
          case BAD_EASY_HANDLE       => "CURLM_BAD_EASY_HANDLE"
          case OUT_OF_MEMORY         => "CURLM_OUT_OF_MEMORY"
          case INTERNAL_ERROR        => "CURLM_INTERNAL_ERROR"
          case BAD_SOCKET            => "CURLM_BAD_SOCKET"
          case UNKNOWN_OPTION        => "CURLM_UNKNOWN_OPTION"
          case ADDED_ALREADY         => "CURLM_ADDED_ALREADY"
          case RECURSIVE_API_CALL    => "CURLM_RECURSIVE_API_CALL"
          case WAKEUP_FAILURE        => "CURLM_WAKEUP_FAILURE"
          case BAD_FUNCTION_ARGUMENT => "CURLM_BAD_FUNCTION_ARGUMENT"
          case ABORTED_BY_CALLBACK   => "CURLM_ABORTED_BY_CALLBACK"
          case UNRECOVERABLE_POLL    => "CURLM_UNRECOVERABLE_POLL"
          case LAST                  => "CURLM_LAST"

  opaque type CurlPipe = CLong
  object CurlPipe extends _BindgenEnumCLong[CurlPipe]:
    given Tag[CurlPipe] = Tag.Size

    inline def define(inline a: Long): CurlPipe = a.toSize

    val NOTHING = define(0L)
    val HTTP1 = define(1L)
    val MULTIPLEX = define(2L)

  /** enum CURLMSG */
  // known as "CURLMSG"
  opaque type CurlMsgCode = Size
  object CurlMsgCode:
    given Tag[CurlMsgCode] = Tag.Size

    inline def define(inline a: Long): CurlMsgCode = a.toSize

    /* first, not used */
    val NONE = define(0)
    /* This easy handle has completed. 'result' contains the CurlErrCode of the transfer */
    val DONE = define(1)
    /* last, not used */
    val LAST = define(2)

    extension (value: CurlMsgCode)
      inline def getName: String =
        inline value match
          case NONE => "CURLMSG_NONE"
          case DONE => "CURLMSG_DONE"
          case LAST => "CURLMSG_LAST"

  type CurlMsgData = CVoidPtr | CurlErrCode
  object CurlMsgData:
    /**
     * its size must be the max of the two sizes, its alignment the max of the two alignments
     */
    given tagCurMsgData: Tag[CurlMsgData] = {
      val tagPtr = summon[Tag[CVoidPtr]]
      val tagCode = summon[Tag[CurlErrCode]]

      if tagPtr.size >= tagCode.size
      then tagPtr.asInstanceOf[Tag[CurlMsgData]]
      else tagCode.asInstanceOf[Tag[CurlMsgData]]
    }

  // known as "CURLMsg"
  opaque type CurlMsg = CStruct3[
    /** msg: what this message means */
    CurlMsgCode,
    /** easy_handle: the handle it concerns */
    Ptr[Curl],
    /** data: message-specific data or return code for transfer */
    CurlMsgData,
  ]
  object CurlMsg:
    import CurlMsgData.tagCurMsgData

    given tagCurMsg: Tag[CurlMsg] = Tag
      .materializeCStruct3Tag[CurlMsgCode, Curl, CurlMsgData]
      .asInstanceOf[Tag[CurlMsg]]

    extension (struct: CurlMsg)
      def msg: CurlMsgCode = struct._1
      def msg_=(value: CurlMsgCode): Unit = !struct.at1 = value
      def easyHandle: Ptr[Curl] = struct._2
      def easyHandle_=(value: Ptr[Curl]): Unit = !struct.at2 = value
      def data: CurlMsgData = struct._3
      def data_=(value: CurlMsgData): Unit = !struct.at3 = value

  /**
   * Based on poll(2) structure and values. We don't use pollfd and POLL* constants explicitly to
   * cover platforms without poll().
   */
  opaque type CurlWait = Int
  object CurlWait:
    given Tag[CurlWait] = Tag.Int

    inline def define(inline a: Int): CurlWait = a

    val POLLIN = define(0x0001)
    val POLLPRI = define(0x0002)
    val POLLOUT = define(0x0004)

  opaque type CurlWaitFd = CStruct3[CurlSocket, Short, Short]
  object CurlWaitFd:
    given Tag[CurlWaitFd] = Tag.materializeCStruct3Tag[CurlSocket, Short, Short]

    def apply(fd: CurlSocket, events: Short, revents: Short)(using Zone): Ptr[CurlWaitFd] =
      val ptr = alloc[CurlWaitFd](1)
      (!ptr).fd = fd
      (!ptr).events = events
      (!ptr).revents = revents
      ptr

    extension (struct: CurlWaitFd)
      def fd: CurlSocket = struct._1
      def fd_=(value: CurlSocket): Unit = !struct.at1 = value
      def events: Short = struct._2
      def events_=(value: Short): Unit = !struct.at2 = value
      def revents: Short = struct._3
      def revents_=(value: Short): Unit = !struct.at3 = value

  /**
   * Name: curl_multi_socket() and curl_multi_socket_all()
   *
   * Desc: An alternative version of curl_multi_perform() that allows the application to pass in one
   * of the file descriptors that have been detected to have "action" on them and let libcurl
   * perform. See manpage for details.
   */
  opaque type CurlPoll = Int
  object CurlPoll:
    given Tag[CurlPoll] = Tag.Int

    val NONE = 0
    val IN = 1
    val OUT = 2
    val INOUT = 3
    val REMOVE = 4

  // CURL_SOCKET_TIMEOUT = CURL_SOCKET_BAD

  opaque type CurlCSelect = Int
  object CurlCSelect:
    given Tag[CurlCSelect] = Tag.Int

    val IN: CurlCSelect = 0x01
    val OUT: CurlCSelect = 0x02
    val ERR: CurlCSelect = 0x04

  // known as "curl_socket_callback"
  opaque type CurlSocketCallback =
    CFuncPtr5[
      /** easy: easy handle */
      Ptr[Curl],
      /** s: socket */
      CurlSocket,
      /** what: CurlPoll */
      CurlPoll,
      /** userp: private callback pointer */
      CVoidPtr,
      /** socketp private socket pointer */
      CVoidPtr,
      Int,
    ]
  object CurlSocketCallback:
    given Tag[CurlSocketCallback] =
      Tag.materializeCFuncPtr5[Ptr[Curl], CurlSocket, Int, CVoidPtr, CVoidPtr, Int]

    inline def apply(
        inline o: CFuncPtr5[Ptr[Curl], CurlSocket, Int, CVoidPtr, CVoidPtr, Int],
    ): CurlSocketCallback = o

  /**
   * Name: curl_multi_timer_callback
   *
   * Desc: Called by libcurl whenever the library detects a change in the maximum number of
   * milliseconds the app is allowed to wait before curl_multi_socket() or curl_multi_perform() must
   * be called (to allow libcurl's timed events to take place).
   *
   * Returns: The callback should return zero.
   */
  // known as "curl_multi_timer_callback"
  opaque type CurlMultiTimerCallback =
    CFuncPtr3[
      /** multi: multi handle */
      Ptr[CurlMulti],
      /** timeout_ms: see above */
      Long,
      /** userp: private callback pointer */
      Ptr[Byte],
      Int,
    ]
  object CurlMultiTimerCallback:
    given Tag[CurlMultiTimerCallback] =
      Tag.materializeCFuncPtr3[Ptr[CurlMulti], Long, Ptr[Byte], Int]

    inline def apply(
        inline o: CFuncPtr3[Ptr[CurlMulti], Long, Ptr[Byte], Int],
    ): CurlMultiTimerCallback = o

  // known as "CURLMoption"
  opaque type CurlMultiOption = Int
  object CurlMultiOption:
    given Tag[CurlMultiOption] = Tag.Int

    inline def define(inline a: Int): CurlMultiOption = a.toInt

    /* This is the socket callback function pointer */
    val SOCKETFUNCTION = define(20001)
    /* This is the argument passed to the socket callback */
    val SOCKETDATA = define(10002)
    /* set to 1 to enable pipelining for this multi handle */
    val PIPELINING = define(3)
    /* This is the timer callback function pointer */
    val TIMERFUNCTION = define(20004)
    /* This is the argument passed to the timer callback */
    val TIMERDATA = define(10005)
    /* maximum number of entries in the connection cache */
    val MAXCONNECTS = define(6)
    /* maximum number of (pipelining) connections to one host */
    val MAX_HOST_CONNECTIONS = define(7)
    /* maximum number of requests in a pipeline */
    val MAX_PIPELINE_LENGTH = define(8)
    /* a connection with a content-length longer than this will not be considered for pipelining */
    val CONTENT_LENGTH_PENALTY_SIZE = define(30009)
    /* a connection with a chunk length longer than this will not be considered for pipelining */
    val CHUNK_LENGTH_PENALTY_SIZE = define(30010)
    /* a list of site names(+port) that are blocked from pipelining */
    val PIPELINING_SITE_BL = define(10011)
    /* a list of server types that are blocked from pipelining */
    val PIPELINING_SERVER_BL = define(10012)
    /* maximum number of open connections in total */
    val MAX_TOTAL_CONNECTIONS = define(13)
    /* This is the server push callback function pointer */
    val PUSHFUNCTION = define(20014)
    /* This is the argument passed to the server push callback */
    val PUSHDATA = define(10015)
    /* maximum number of concurrent streams to support on a connection */
    val MAX_CONCURRENT_STREAMS = define(16)
    /* the last unused */
    val LASTENTRY = define(17)

    inline def getName(inline value: CurlMultiOption): Option[String] =
      inline value match
        case SOCKETFUNCTION              => Some("CURLMOPT_SOCKETFUNCTION")
        case SOCKETDATA                  => Some("CURLMOPT_SOCKETDATA")
        case PIPELINING                  => Some("CURLMOPT_PIPELINING")
        case TIMERFUNCTION               => Some("CURLMOPT_TIMERFUNCTION")
        case TIMERDATA                   => Some("CURLMOPT_TIMERDATA")
        case MAXCONNECTS                 => Some("CURLMOPT_MAXCONNECTS")
        case MAX_HOST_CONNECTIONS        => Some("CURLMOPT_MAX_HOST_CONNECTIONS")
        case MAX_PIPELINE_LENGTH         => Some("CURLMOPT_MAX_PIPELINE_LENGTH")
        case CONTENT_LENGTH_PENALTY_SIZE => Some("CURLMOPT_CONTENT_LENGTH_PENALTY_SIZE")
        case CHUNK_LENGTH_PENALTY_SIZE   => Some("CURLMOPT_CHUNK_LENGTH_PENALTY_SIZE")
        case PIPELINING_SITE_BL          => Some("CURLMOPT_PIPELINING_SITE_BL")
        case PIPELINING_SERVER_BL        => Some("CURLMOPT_PIPELINING_SERVER_BL")
        case MAX_TOTAL_CONNECTIONS       => Some("CURLMOPT_MAX_TOTAL_CONNECTIONS")
        case PUSHFUNCTION                => Some("CURLMOPT_PUSHFUNCTION")
        case PUSHDATA                    => Some("CURLMOPT_PUSHDATA")
        case MAX_CONCURRENT_STREAMS      => Some("CURLMOPT_MAX_CONCURRENT_STREAMS")
        case LASTENTRY                   => Some("CURLMOPT_LASTENTRY")
        case _                           => None

    extension (a: CurlMultiOption)
      inline def &(b: CurlMultiOption): CurlMultiOption = a & b
      inline def |(b: CurlMultiOption): CurlMultiOption = a | b
      inline def is(b: CurlMultiOption): Boolean = (a & b) == b

  // TODO: add CURLMinfo_offt
  // known as "CURLMinfo_offt"

  // typedef enum {
  //   CURLMINFO_NONE, /* first, never use this */
  //   /* The number of easy handles currently managed by the multi handle,
  //   * e.g. have been added but not yet removed. */
  //   CURLMINFO_XFERS_CURRENT = 1,
  //   /* The number of easy handles running, e.g. not done and not queueing. */
  //   CURLMINFO_XFERS_RUNNING = 2,
  //   /* The number of easy handles waiting to start, e.g. for a connection
  //   * to become available due to limits on parallelism, max connections
  //   * or other factors. */
  //   CURLMINFO_XFERS_PENDING = 3,
  //   /* The number of easy handles finished, waiting for their results to
  //   * be read via `curl_multi_info_read()`. */
  //   CURLMINFO_XFERS_DONE = 4,
  //   /* The total number of easy handles added to the multi handle, ever. */
  //   CURLMINFO_XFERS_ADDED = 5,
  //
  //   CURLMINFO_LASTENTRY /* the last unused */
  // } CURLMinfo_offt;

  /**
   * Name: curl_push_callback
   *
   * Desc: This callback gets called when a new stream is being pushed by the server. It approves or
   * denies the new stream. It can also decide to completely fail the connection.
   *
   * Returns: CURL_PUSH_OK, CURL_PUSH_DENY or CURL_PUSH_ERROROUT
   */

  // known as "curl_push"
  opaque type CurlPush = Int
  object CurlPush:
    given Tag[CurlPush] = Tag.Int
    val OK: Int = 0
    val DENY: Int = 1
    val ERROROUT: Int = 2

  /** forward declaration only */
  // known as "curl_pushheaders"
  opaque type CurlPushHeaders = CStruct0
  object CurlPushHeaders:
    given Tag[CurlPushHeaders] = Tag.materializeCStruct0Tag

  // known as "curl_push_callback"
  opaque type CurlPushCallback =
    CFuncPtr5[
      /** parent */
      Ptr[Curl],
      /** easy */
      Ptr[Curl],
      /** number_headers */
      size_t,
      /** headers */
      Ptr[CurlPushHeaders],
      /** userp */
      CVoidPtr,
      CurlPush,
    ]
  object CurlPushCallback:
    given Tag[CurlPushCallback] = Tag.materializeCFuncPtr5[
      Ptr[Curl],
      Ptr[Curl],
      size_t,
      Ptr[CurlPushHeaders],
      CVoidPtr,
      CurlPush,
    ]

    inline def apply(
        inline o: CFuncPtr5[
          Ptr[Curl],
          Ptr[Curl],
          size_t,
          Ptr[CurlPushHeaders],
          CVoidPtr,
          CurlPush,
        ],
    ): CurlPushCallback = o
