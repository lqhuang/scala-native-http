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
package snhttp.experimental.libcurl

import scalanative.unsafe.{extern, link, name, alloc}
import scalanative.unsafe.{
  Zone,
  Tag,
  Ptr,
  CVoidPtr,
  CString,
  CLongInt,
  CStruct0,
  CStruct3,
  CFuncPtr,
  CFuncPtr3,
  CFuncPtr5,
  CChar,
  CSize,
  CArray,
  CVarArgList,
  Nat,
}
import scalanative.unsigned.*
import scalanative.posix.sys.select.fd_set

import core.{Curl, CurlCode, CurlSocket}

@extern
object multi:

  @name("CURLM")
  opaque type CurlMulti = Unit // CVoidPtr
  object CurlMulti:
    given Tag[CurlMulti] = Tag.Unit

  @name("CURLMcode")
  type CurlMultiCode = Int
  object CurlMultiCode:
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

    extension (value: CurlMultiCode)
      inline def getName: String =
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

    extension (a: CurlMultiCode)
      inline def &(b: CurlMultiCode): CurlMultiCode = a & b
      inline def |(b: CurlMultiCode): CurlMultiCode = a | b
      inline def is(b: CurlMultiCode): Boolean = (a & b) == b

  /** enum CURLMSG */
  @name("CURLMSG")
  opaque type CurlMsgCode = UInt
  object CurlMsgCode:
    given Tag[CurlMsgCode] = Tag.UInt

    inline def define(inline a: Long): CurlMsgCode = a.toUInt

    /* first, not used */
    val NONE = define(0)
    /* This easy handle has completed. 'result' contains the CurlCode of the transfer */
    val DONE = define(1)
    /* last, not used */
    val LAST = define(2)

    extension (value: CurlMsgCode)
      inline def getName: String =
        value match
          case NONE => "CURLMSG_NONE"
          case DONE => "CURLMSG_DONE"
          case LAST => "CURLMSG_LAST"

    extension (a: CurlMsgCode)
      inline def &(b: CurlMsgCode): CurlMsgCode = a & b
      inline def |(b: CurlMsgCode): CurlMsgCode = a | b
      inline def is(b: CurlMsgCode): Boolean = (a & b) == b

  type CurlMsgData = CVoidPtr | CurlCode
  object CurlMsgData:
    /**
     * its size must be the max of the two sizes, its alignment the max of the two alignments
     */
    given tagCurMsgData: Tag[CurlMsgData] = {
      val tagPtr = summon[Tag[CVoidPtr]]
      val tagCode = summon[Tag[CurlCode]]

      if tagPtr.size >= tagCode.size
      then tagPtr.asInstanceOf[Tag[CurlMsgData]]
      else tagCode.asInstanceOf[Tag[CurlMsgData]]
    }

  @name("CURLMsg")
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
  opaque type CurlWait = UInt
  object CurlWait:
    given Tag[CurlWait] = Tag.UInt

    inline def define(inline a: Long): CurlWait = a.toUInt

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
   * Name: curl_multi_init()
   *
   * Desc: initialize multi-style curl usage
   *
   * Returns: a new CURLM handle to use in all 'curl_multi' functions.
   */
  @name("curl_multi_init")
  def multiInit(): Ptr[CurlMulti] = extern

  /**
   * Name: curl_multi_add_handle()
   *
   * Desc: add a standard curl handle to the multi stack
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_add_handle")
  def multiAddHandle(handle: Ptr[CurlMulti], curlHandle: Ptr[Curl]): CurlMultiCode = extern

  /**
   * Name: curl_multi_remove_handle()
   *
   * Desc: removes a curl handle from the multi stack again
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_remove_handle")
  def multiRemoveHandle(handle: Ptr[CurlMulti], curlHandle: Ptr[Curl]): CurlMultiCode = extern

  /**
   * Name: curl_multi_fdset()
   *
   * Desc: Ask curl for its fd_set sets. The app can use these to select() or poll() on. We want
   * curl_multi_perform() called as soon as one of them are ready.
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_fdset")
  def multiFdSet(
      handle: Ptr[CurlMulti],
      readFdSet: Ptr[fd_set],
      writeFdSet: Ptr[fd_set],
      excFdSet: Ptr[fd_set],
      maxFd: Ptr[Int],
  ): CurlMultiCode = extern

  /**
   * Name: curl_multi_wait()
   *
   * Desc: Poll on all fds within a CURLM set as well as any additional fds passed to the function.
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_wait")
  def multiWait(
      handle: Ptr[CurlMulti],
      extraFds: Ptr[CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiCode = extern

  /**
   * Name: curl_multi_poll()
   *
   * Desc: Poll on all fds within a CURLM set as well as any additional fds passed to the function.
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_poll")
  def multiPoll(
      handle: Ptr[CurlMulti],
      extraFds: Ptr[CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiCode = extern

  /**
   * Name: curl_multi_wakeup()
   *
   * Desc: wakes up a sleeping curl_multi_poll call.
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_wakeup")
  def multiWakeup(handle: Ptr[CurlMulti]): CurlMultiCode = extern

  /**
   * Name: curl_multi_perform()
   *
   * Desc: When the app thinks there's data available for curl it calls this function to read/write
   * whatever there is right now. This returns as soon as the reads and writes are done. This
   * function does not require that there actually is data available for reading or that data can be
   * written, it can be called just in case. It returns the number of handles that still transfer
   * data in the second argument's integer-pointer.
   *
   * Returns: CURLMcode type, general multi error code. *NOTE* that this only returns errors etc
   * regarding the whole multi stack. There might still have occurred problems on individual
   * transfers even when this returns OK.
   */
  @name("curl_multi_perform")
  def multiPerform(handle: Ptr[CurlMulti], runningHandles: Ptr[Int]): CurlMultiCode = extern

  /**
   * Name: curl_multi_cleanup()
   *
   * Desc: Cleans up and removes a whole multi stack. It does not free or touch any individual easy
   * handles in any way. We need to define in what state those handles will be if this function is
   * called in the middle of a transfer.
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_cleanup")
  def multiCleanup(handle: Ptr[CurlMulti]): CurlMultiCode = extern

  /**
   * Name: curl_multi_info_read()
   *
   * Desc: Ask the multi handle if there's any messages/informationals from the individual
   * transfers. Messages include informationals such as error code from the transfer or just the
   * fact that a transfer is completed. More details on these should be written down as well.
   *
   * Repeated calls to this function will return a new struct each time, until a special "end of
   * msgs" struct is returned as a signal that there is no more to get at this point.
   *
   * The data the returned pointer points to will not survive calling curl_multi_cleanup().
   *
   * The 'CURLMsg' struct is meant to be very simple and only contain very basic information. If
   * more involved information is wanted, we will provide the particular "transfer handle" in that
   * struct and that should/could/would be used in subsequent curl_easy_getinfo() calls (or
   * similar). The point being that we must never expose complex structs to applications, as then
   * we'll undoubtably get backwards compatibility problems in the future.
   *
   * Returns: A pointer to a filled-in struct, or NULL if it failed or ran out of structs. It also
   * writes the number of messages left in the queue (after this read) in the integer the second
   * argument points to.
   */
  @name("curl_multi_info_read")
  def multiInfoRead(handle: Ptr[CurlMulti], msgsInQueue: Ptr[Int]): Ptr[CurlMsg] = extern

  /**
   * Name: curl_multi_strerror()
   *
   * Desc: The curl_multi_strerror function may be used to turn a CURLMcode value into the
   * equivalent human readable error string. This is useful for printing meaningful error messages.
   *
   * Returns: A pointer to a null-terminated error message.
   */
  @name("curl_multi_strerror")
  def multiStrError(code: CurlMultiCode): CString = extern

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

  opaque type CurlCSelect = UInt
  object CurlCSelect:
    given Tag[CurlCSelect] = Tag.UInt
    val IN = 0x01.toUInt
    val OUT = 0x02.toUInt
    val ERR = 0x04.toUInt

  /**
   * Name: curl_socket_callback
   *
   * Desc:
   *
   * Returns:
   */
  @name("curl_socket_callback")
  opaque type CurlSocketCallback =
    CFuncPtr5[
      /** easy: easy handle */
      Ptr[Curl],
      /** s: socket */
      CurlSocket,
      /** what: see above ????? */
      Int,
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
  @name("curl_multi_timer_callback")
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

  // func `curl_multi_socket` is DEPRECATED since curl 7.19.5

  @name("curl_multi_socket_action")
  def multiSocketAction(
      handle: Ptr[CurlMulti],
      s: CurlSocket,
      evBitmask: Int,
      runningHandles: Ptr[Int],
  ): CurlMultiCode = extern

  // func `curl_multi_socket_all` is DEPRECATED since curl 7.19.5

  /**
   * Name: curl_multi_timeout()
   *
   * Desc: Returns the maximum number of milliseconds the app is allowed to wait before
   * curl_multi_socket() or curl_multi_perform() must be called (to allow libcurl's timed events to
   * take place).
   *
   * Returns: CURLM error code.
   */
  @name("curl_multi_timeout")
  def multiTimeout(handle: Ptr[CurlMulti], milliseconds: Ptr[CLongInt]): CurlMultiCode =
    extern

  @name("CURLMoption")
  opaque type CurlMultiOption = UInt
  object CurlMultiOption:
    given Tag[CurlMultiOption] = Tag.UInt

    inline def define(inline a: Long): CurlMultiOption = a.toUInt

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

  /**
   * Name: curl_multi_setopt()
   *
   * Desc: Sets options for the multi handle.
   *
   * Returns: CURLM error code.
   */
  @name("curl_multi_setopt")
  def multiSetopt(
      handle: Ptr[CurlMulti],
      option: CurlMultiOption,
      params: Any*, // CVarArgList,
  ): CurlMultiCode = extern

  /**
   * Name: curl_multi_assign()
   *
   * Desc: This function sets an association in the multi handle between the given socket and a
   * private pointer of the application. This is (only) useful for curl_multi_socket uses.
   *
   * Returns: CURLM error code.
   */
  @name("curl_multi_assign")
  def multiAssign(
      handle: Ptr[CurlMulti],
      sockfd: CurlSocket,
      sockp: CVoidPtr,
  ): CurlMultiCode = extern

  /**
   * Name: curl_multi_get_handles()
   *
   * Desc: Returns an allocated array holding all handles currently added to the multi handle. Marks
   * the final entry with a NULL pointer. If there is no easy handle added to the multi handle, this
   * function returns an array with the first entry as a NULL pointer.
   *
   * Returns: NULL on failure, otherwise a CURL **array pointer
   */
  @name("curl_multi_get_handles")
  def multiGetHandles(handle: Ptr[CurlMulti]): Ptr[Ptr[Curl]] = extern

  /**
   * Name: curl_push_callback
   *
   * Desc: This callback gets called when a new stream is being pushed by the server. It approves or
   * denies the new stream. It can also decide to completely fail the connection.
   *
   * Returns: CURL_PUSH_OK, CURL_PUSH_DENY or CURL_PUSH_ERROROUT
   */

  @name("curl_push")
  opaque type CurlPush = Int
  object CurlPush:
    given Tag[CurlPush] = Tag.Int
    val OK: Int = 0
    val DENY: Int = 1
    val ERROROUT: Int = 2

  /** forward declaration only */
  @name("curl_pushheaders")
  opaque type CurlPushHeaders = CStruct0
  object CurlPushHeaders:
    given Tag[CurlPushHeaders] = Tag.materializeCStruct0Tag

  @name("curl_pushheader_bynum")
  def pushHeaderByNum(handle: Ptr[CurlPushHeaders], num: USize): CString = extern

  @name("curl_pushheader_byname")
  def pushHeaderByName(handle: Ptr[CurlPushHeaders], name: CString): CString = extern

  @name("curl_push_callback")
  opaque type CurlPushCallback =
    CFuncPtr5[
      /** parent */
      Ptr[Curl],
      /** easy */
      Ptr[Curl],
      /** number_headers */
      USize,
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
      USize,
      Ptr[CurlPushHeaders],
      CVoidPtr,
      CurlPush,
    ]

    inline def apply(
        inline o: CFuncPtr5[
          Ptr[Curl],
          Ptr[Curl],
          USize,
          Ptr[CurlPushHeaders],
          CVoidPtr,
          CurlPush,
        ],
    ): CurlPushCallback = o
