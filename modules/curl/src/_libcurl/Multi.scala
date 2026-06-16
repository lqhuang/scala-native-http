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
package snhttp.experimental.curl
package _libcurl

import scalanative.unsafe.alloc
import scalanative.unsafe.{
  Zone,
  Tag,
  Ptr,
  CLong,
  CVoidPtr,
  CStruct0,
  CStruct3,
  CFuncPtr3,
  CFuncPtr5,
  UnsafeRichLong,
}
import scalanative.posix.stddef.size_t

import _root_.snhttp.experimental.curl._libcurl.Curl.{CurlHandle, CurlErrCode, CurlSocket}
import _root_.snhttp.experimental.curl._internal.{_BindgenEnumCInt, _BindgenEnumCLong}

private[curl] object Multi:

  // known as "CURLM"
  opaque type CurlMultiHandle = CVoidPtr
  object CurlMultiHandle:
    given Tag[CurlMultiHandle] = Tag.materializePtrWildcard
  end CurlMultiHandle

  // known as "CURLMcode"
  type CurlMultiErrCode = Int
  object CurlMultiErrCode extends _BindgenEnumCInt[CurlMultiErrCode]:

    given Tag[CurlMultiErrCode] = Tag.Int
    private inline def define(a: Int): CurlMultiErrCode = a

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

    implicit class RichCurlMultiErrCode(value: CurlMultiErrCode) extends AnyVal:
      inline def getname: String =
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

  end CurlMultiErrCode

  opaque type CurlPipe = CLong
  object CurlPipe extends _BindgenEnumCLong[CurlPipe]:

    given Tag[CurlPipe] = Tag.Size
    private inline def define(a: Long): CurlPipe = a.toSize

    val NOTHING = define(0L)
    val HTTP1 = define(1L)
    val MULTIPLEX = define(2L)

  /** enum CURLMSG */
  // known as "CURLMSG"
  type CurlMsgCode = Int
  object CurlMsgCode extends _BindgenEnumCInt[CurlMsgCode]:

    given Tag[CurlMsgCode] = Tag.Int
    private inline def define(a: Int): CurlMsgCode = a

    /* first, not used */
    final val NONE = define(0)
    /* This easy handle has completed. 'result' contains the CurlErrCode of the transfer */
    final val DONE = define(1)
    /* last, not used */
    final val LAST = define(2)

    extension (value: CurlMsgCode)
      def getname: String =
        value match
          case NONE => "CURLMSG_NONE"
          case DONE => "CURLMSG_DONE"
          case LAST => "CURLMSG_LAST"

  end CurlMsgCode

  // type CurlMsgData = CurlErrCode
  type CurlMsgData = CurlErrCode | CVoidPtr // ??? successed in cli compile, failed in IDE
  object CurlMsgData:
    /**
     * its size must be the max of the two sizes, its alignment the max of the two alignments
     */
    given Tag[CurlMsgData] = Tag.materializePtrWildcard.asInstanceOf[Tag[CurlMsgData]]

  end CurlMsgData

  // known as "CURLMsg"
  opaque type CurlMsg = CStruct3[
    /** msg: what this message means */
    CurlMsgCode,
    /** easy_handle: the handle it concerns */
    Ptr[CurlHandle],
    /** data: message-specific data or return code for transfer */
    CurlMsgData,
  ]
  object CurlMsg:
    import CurlMsgData.given_Tag_CurlMsgData
    given Tag[CurlMsg] =
      Tag.materializeCStruct3Tag[CurlMsgCode, CurlHandle, CurlMsgData].asInstanceOf[Tag[CurlMsg]]

    extension (struct: CurlMsg)
      inline def msg: CurlMsgCode = !struct.at1
      inline def msg_=(value: CurlMsgCode): Unit = !struct.at1 = value
      inline def easyHandle: Ptr[CurlHandle] = !struct.at2
      inline def easyHandle_=(value: Ptr[CurlHandle]): Unit = !struct.at2 = value
      inline def data: CurlMsgData = !struct.at3
      inline def data_=(value: CurlMsgData): Unit = !struct.at3 = value

  end CurlMsg

  /**
   * Based on poll(2) structure and values. We don't use pollfd and POLL* constants explicitly to
   * cover platforms without poll().
   */
  opaque type CurlWait = Int
  object CurlWait extends _BindgenEnumCInt[CurlWait]:

    given Tag[CurlWait] = Tag.Int
    private inline def define(a: Int): CurlWait = a

    val POLLNONE = define(0x0000)
    val POLLIN = define(0x0001)
    val POLLPRI = define(0x0002)
    val POLLOUT = define(0x0004)

  end CurlWait

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
      inline def fd: CurlSocket = struct._1
      inline def fd_=(value: CurlSocket): Unit = !struct.at1 = value
      inline def events: Short = struct._2
      inline def events_=(value: Short): Unit = !struct.at2 = value
      inline def revents: Short = struct._3
      inline def revents_=(value: Short): Unit = !struct.at3 = value

  end CurlWaitFd

  /**
   * Name: curl_multi_socket() and curl_multi_socket_all()
   *
   * Desc: An alternative version of curl_multi_perform() that allows the application to pass in one
   * of the file descriptors that have been detected to have "action" on them and let libcurl
   * perform. See manpage for details.
   */
  opaque type CurlPoll = Int
  object CurlPoll extends _BindgenEnumCInt[CurlPoll]:

    given Tag[CurlPoll] = Tag.Int
    private inline def define(a: Int): CurlPoll = a

    val NONE = define(0)
    val IN = define(1)
    val OUT = define(2)
    val INOUT = define(3)
    val REMOVE = define(4)

    def unapply(value: CurlPoll): Option[CurlPoll] =
      value match
        case NONE   => Some(NONE)
        case IN     => Some(IN)
        case OUT    => Some(OUT)
        case INOUT  => Some(INOUT)
        case REMOVE => Some(REMOVE)
        case _      => None

    extension (a: CurlPoll)
      inline def &(b: CurlPoll): CurlPoll = a & b
      inline def |(b: CurlPoll): CurlPoll = a | b
      inline infix def is(b: CurlPoll): Boolean = (a & b) == b

  end CurlPoll

  // CURL_SOCKET_TIMEOUT = CURL_SOCKET_BAD

  opaque type CurlCselect = Int
  object CurlCselect:

    given Tag[CurlCselect] = Tag.Int

    val NONE: CurlCselect = 0x00
    val IN: CurlCselect = 0x01
    val OUT: CurlCselect = 0x02
    val ERR: CurlCselect = 0x04

    extension (a: CurlCselect)
      inline def &(b: CurlCselect): CurlCselect = a & b
      inline def |(b: CurlCselect): CurlCselect = a | b
      inline def is(b: CurlCselect): Boolean = (a & b) == b

  end CurlCselect

  // known as "curl_socket_callback"
  opaque type CurlSocketCallback =
    CFuncPtr5[
      /** easy: easy handle */
      Ptr[CurlHandle],
      /** s: socket */
      CurlSocket,
      /** what: CurlPoll */
      CurlPoll,
      /** userp: private callback pointer */
      CVoidPtr,
      /** socketp private socket pointer */
      CVoidPtr,
      /** return */
      Int,
    ]
  object CurlSocketCallback:

    given Tag[CurlSocketCallback] =
      Tag.materializeCFuncPtr5[Ptr[CurlHandle], CurlSocket, CurlPoll, CVoidPtr, CVoidPtr, Int]

    inline def fromScalaFunction(
        inline func: (Ptr[CurlHandle], CurlSocket, CurlPoll, CVoidPtr, CVoidPtr) => Int,
    ): CurlSocketCallback =
      CFuncPtr5.fromScalaFunction[Ptr[CurlHandle], CurlSocket, CurlPoll, CVoidPtr, CVoidPtr, Int](
        func,
      )

    extension (callback: CurlSocketCallback)
      inline def asFuncPtr
          : CFuncPtr5[Ptr[CurlHandle], CurlSocket, CurlPoll, CVoidPtr, CVoidPtr, Int] =
        callback

  end CurlSocketCallback

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
      Ptr[CurlMultiHandle],
      /** timeout_ms: see above */
      CLong,
      /** userp: private callback pointer */
      Ptr[Byte],
      /** Returns: The callback should return zero */
      Int,
    ]
  object CurlMultiTimerCallback:

    given Tag[CurlMultiTimerCallback] =
      Tag.materializeCFuncPtr3[Ptr[CurlMultiHandle], CLong, Ptr[Byte], Int]

    inline def fromScalaFunction(
        inline func: (Ptr[CurlMultiHandle], CLong, Ptr[Byte]) => Int,
    ): CurlMultiTimerCallback =
      CFuncPtr3.fromScalaFunction(func)

    extension (inline cb: CurlMultiTimerCallback)
      inline def asFuncPtr: CFuncPtr3[Ptr[CurlMultiHandle], CLong, Ptr[Byte], Int] =
        cb

  end CurlMultiTimerCallback

  // known as "CURLMoption"
  opaque type CurlMultiOption = Int
  object CurlMultiOption extends _BindgenEnumCInt[CurlMultiOption]:

    given Tag[CurlMultiOption] = Tag.Int
    private inline def define(a: Int): CurlMultiOption = a

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

    implicit class RichCurlMultiOption(value: CurlMultiOption) extends AnyVal:
      inline def getname(value: CurlMultiOption): String =
        value match
          case SOCKETFUNCTION              => "CURLMOPT_SOCKETFUNCTION"
          case SOCKETDATA                  => "CURLMOPT_SOCKETDATA"
          case PIPELINING                  => "CURLMOPT_PIPELINING"
          case TIMERFUNCTION               => "CURLMOPT_TIMERFUNCTION"
          case TIMERDATA                   => "CURLMOPT_TIMERDATA"
          case MAXCONNECTS                 => "CURLMOPT_MAXCONNECTS"
          case MAX_HOST_CONNECTIONS        => "CURLMOPT_MAX_HOST_CONNECTIONS"
          case MAX_PIPELINE_LENGTH         => "CURLMOPT_MAX_PIPELINE_LENGTH"
          case CONTENT_LENGTH_PENALTY_SIZE => "CURLMOPT_CONTENT_LENGTH_PENALTY_SIZE"
          case CHUNK_LENGTH_PENALTY_SIZE   => "CURLMOPT_CHUNK_LENGTH_PENALTY_SIZE"
          case PIPELINING_SITE_BL          => "CURLMOPT_PIPELINING_SITE_BL"
          case PIPELINING_SERVER_BL        => "CURLMOPT_PIPELINING_SERVER_BL"
          case MAX_TOTAL_CONNECTIONS       => "CURLMOPT_MAX_TOTAL_CONNECTIONS"
          case PUSHFUNCTION                => "CURLMOPT_PUSHFUNCTION"
          case PUSHDATA                    => "CURLMOPT_PUSHDATA"
          case MAX_CONCURRENT_STREAMS      => "CURLMOPT_MAX_CONCURRENT_STREAMS"
          case LASTENTRY                   => "CURLMOPT_LASTENTRY"

  end CurlMultiOption

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

  end CurlPush

  /** forward declaration only */
  // known as "curl_pushheaders"
  opaque type CurlPushHeaders = CStruct0
  object CurlPushHeaders:
    given Tag[CurlPushHeaders] = Tag.materializeCStruct0Tag
  end CurlPushHeaders

  // known as "curl_push_callback"
  opaque type CurlPushCallback =
    CFuncPtr5[
      /** parent */
      Ptr[CurlHandle],
      /** easy */
      Ptr[CurlHandle],
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
      Ptr[CurlHandle],
      Ptr[CurlHandle],
      size_t,
      Ptr[CurlPushHeaders],
      CVoidPtr,
      CurlPush,
    ]

  end CurlPushCallback
