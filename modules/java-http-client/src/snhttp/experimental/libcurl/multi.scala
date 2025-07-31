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
  CSize,
  CArray,
  CChar,
  Nat,
}
import scalanative.unsigned.*
import scalanative.posix.sys.select.fd_set

import snhttp.experimental.libcurl.curl.{Curl, CurlCode, CurlSocket}

@link("curl/multi")
@extern
object multi:
  @name("CURLM") opaque type CurlMulti = Byte

  @name("CURLMcode") opaque type CurlMultiCode = Int
  object CurlMultiCode:
    given Tag[CurlMultiCode] = Tag.Int
    inline def define(inline a: Int): CurlMultiCode = a

    /** please call curl_multi_perform() or curl_multi_socket*() soon */
    val CURLM_CALL_MULTI_PERFORM = define(-1)
    val CURLM_OK = define(0)

    /** the passed-in handle is not a valid CURLM handle */
    val CURLM_BAD_HANDLE = define(1)

    /** an easy handle was not good/valid */
    val CURLM_BAD_EASY_HANDLE = define(2)

    /** if you ever get this, you're in deep sh*t */
    val CURLM_OUT_OF_MEMORY = define(3)

    /** this is a libcurl bug */
    val CURLM_INTERNAL_ERROR = define(4)

    /** the passed in socket argument did not match */
    val CURLM_BAD_SOCKET = define(5)

    /** curl_multi_setopt() with unsupported option */
    val CURLM_UNKNOWN_OPTION = define(6)

    /**
     * an easy handle already added to a multi handle was attempted to get added - again
     */
    val CURLM_ADDED_ALREADY = define(7)

    /**
     * an api function was called from inside a callback
     */
    val CURLM_RECURSIVE_API_CALL = define(8)

    /** wakeup is unavailable or failed */
    val CURLM_WAKEUP_FAILURE = define(9)

    /** function called with a bad parameter */
    val CURLM_BAD_FUNCTION_ARGUMENT = define(10)
    val CURLM_ABORTED_BY_CALLBACK = define(11)
    val CURLM_UNRECOVERABLE_POLL = define(12)
    val CURLM_LAST = define(13)

    inline def getName(inline value: CurlMultiCode): Option[String] =
      inline value match
        case CURLM_CALL_MULTI_PERFORM    => Some("CURLM_CALL_MULTI_PERFORM")
        case CURLM_OK                    => Some("CURLM_OK")
        case CURLM_BAD_HANDLE            => Some("CURLM_BAD_HANDLE")
        case CURLM_BAD_EASY_HANDLE       => Some("CURLM_BAD_EASY_HANDLE")
        case CURLM_OUT_OF_MEMORY         => Some("CURLM_OUT_OF_MEMORY")
        case CURLM_INTERNAL_ERROR        => Some("CURLM_INTERNAL_ERROR")
        case CURLM_BAD_SOCKET            => Some("CURLM_BAD_SOCKET")
        case CURLM_UNKNOWN_OPTION        => Some("CURLM_UNKNOWN_OPTION")
        case CURLM_ADDED_ALREADY         => Some("CURLM_ADDED_ALREADY")
        case CURLM_RECURSIVE_API_CALL    => Some("CURLM_RECURSIVE_API_CALL")
        case CURLM_WAKEUP_FAILURE        => Some("CURLM_WAKEUP_FAILURE")
        case CURLM_BAD_FUNCTION_ARGUMENT => Some("CURLM_BAD_FUNCTION_ARGUMENT")
        case CURLM_ABORTED_BY_CALLBACK   => Some("CURLM_ABORTED_BY_CALLBACK")
        case CURLM_UNRECOVERABLE_POLL    => Some("CURLM_UNRECOVERABLE_POLL")
        case CURLM_LAST                  => Some("CURLM_LAST")
        case _                           => None
    extension (a: CurlMultiCode)
      inline def &(b: CurlMultiCode): CurlMultiCode = a & b
      inline def |(b: CurlMultiCode): CurlMultiCode = a | b
      inline def is(b: CurlMultiCode): Boolean = (a & b) == b

  /** enum CURLMSG */
  @name("CURLMSG") opaque type CurlMsgCode = UInt
  object CurlMsgCode:
    given Tag[CurlMsgCode] = Tag.UInt

    inline def define(inline a: Long): CurlMsgCode = a.toUInt

    /** first, not used */
    val CURLMSG_NONE = define(0)

    /**
     * This easy handle has completed. 'result' contains the CurlCode of the transfer
     */
    val CURLMSG_DONE = define(1)

    /** last, not used */
    val CURLMSG_LAST = define(2)

    inline def getName(inline value: CurlMsgCode): Option[String] =
      inline value match
        case CURLMSG_NONE => Some("CURLMSG_NONE")
        case CURLMSG_DONE => Some("CURLMSG_DONE")
        case CURLMSG_LAST => Some("CURLMSG_LAST")
        case _            => None

    extension (a: CurlMsgCode)
      inline def &(b: CurlMsgCode): CurlMsgCode = a & b
      inline def |(b: CurlMsgCode): CurlMsgCode = a | b
      inline def is(b: CurlMsgCode): Boolean = (a & b) == b

  @name("CURLMsg") opaque type CurlMsg = CStruct3[
    /**
     * msg
     *
     * what this message means
     */
    CurlMsgCode,
    /**
     * easy_handle
     *
     * the handle it concerns
     */
    Ptr[Curl],
    /**
     * data
     *
     * message-specific data
     */
    CurlMsg.Data,
  ]
  object CurlMsg:
    type Data = Byte | CurlCode
    // object Data:
    //   given _tag: Tag[Data] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    //   def apply(whatever: Ptr[Byte])(using Zone): Ptr[Data] =
    //     whatever.asInstanceOf[Ptr[Data]]

    //   def apply(result: CurlCode)(using Zone): Ptr[Data] =
    //     val ptr = alloc[Data](1)
    //     !ptr = result
    //     ptr

    //   extension (struct: Data)
    //     def whatever: Ptr[Byte] = !struct.at(0).asInstanceOf[Ptr[Ptr[Byte]]]
    //     def whatever_=(value: Ptr[Byte]): Unit = !struct.at(0).asInstanceOf[Ptr[Ptr[Byte]]] = value
    //     def result: CurlCode = !struct.at(0).asInstanceOf[Ptr[CurlCode]]
    //     def result_=(value: CurlCode): Unit = !struct.at(0).asInstanceOf[Ptr[CurlCode]] = value

    // given Tag[CurlMsg] = Tag.materializeCStruct3Tag[CurlMsgCode, Curl, Data]

    // def apply(msg: CurlMsgCode, easy_handle: Ptr[Curl], data: CurlMsg.Data)(using
    //     Zone,
    // ): Ptr[CurlMsg] =
    //   val ptr = alloc[CurlMsg](1)
    //   (!ptr)._1 = msg
    //   (!ptr)._2 = easy_handle
    //   (!ptr)._3 = data
    //   ptr

    // extension (struct: CurlMsg)
    //   def msg: CurlMsgCode = struct._1
    //   def msg_=(value: CurlMsgCode): Unit = !struct.at1 = value
    //   def easy_handle: Ptr[Curl] = struct._2
    //   def easy_handle_=(value: Ptr[Curl]): Unit = !struct.at2 = value
    //   def data: CurlMsg.Data = struct._3
    //   def data_=(value: CurlMsg.Data): Unit = !struct.at3 = value

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
   * Name: curl_multi_init()
   *
   * Desc: initialize multi-style curl usage
   *
   * Returns: a new CURLM handle to use in all 'curl_multi' functions.
   */
  @name("curl_multi_init")
  def init(): CurlMulti = extern

  /**
   * Name: curl_multi_add_handle()
   *
   * Desc: add a standard curl handle to the multi stack
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_add_handle")
  def addHandle(multiHandle: Ptr[CurlMulti], curl_handle: Ptr[Curl]): CurlMultiCode = extern

  /**
   * Name: curl_multi_remove_handle()
   *
   * Desc: removes a curl handle from the multi stack again
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_remove_handle")
  def removeHandle(
      multiHandle: Ptr[CurlMulti],
      curl_handle: Ptr[Curl],
  ): CurlMultiCode = extern

  /**
   * Name: curl_multi_fdset()
   *
   * Desc: Ask curl for its fd_set sets. The app can use these to select() or poll() on. We want
   * curl_multi_perform() called as soon as one of them are ready.
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_fdset")
  def fdset(
      multiHandle: Ptr[CurlMulti],
      read_fd_set: Ptr[fd_set],
      write_fd_set: Ptr[fd_set],
      exc_fd_set: Ptr[fd_set],
      max_fd: Ptr[Int],
  ): CurlMultiCode = extern

  /**
   * Name: curl_multi_wait()
   *
   * Desc: Poll on all fds within a CURLM set as well as any additional fds passed to the function.
   *
   * Returns: CURLMcode type, general multi error code.
   */
  @name("curl_multi_wait")
  def wait(
      multiHandle: Ptr[CurlMulti],
      extra_fds: Ptr[CurlWaitFd],
      extra_nfds: UInt,
      timeout_ms: Int,
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
  def poll(
      multiHandle: Ptr[CurlMulti],
      extra_fds: Ptr[CurlWaitFd],
      extra_nfds: UInt,
      timeout_ms: Int,
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
  def wakeup(multiHandle: Ptr[CurlMulti]): CurlMultiCode = extern

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
  def perform(multiHandle: Ptr[CurlMulti], running_handles: Ptr[Int]): CurlMultiCode = extern

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
  def cleanup(multiHandle: Ptr[CurlMulti]): CurlMultiCode = extern

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
  def infoRead(multiHandle: Ptr[CurlMulti], msgs_in_queue: Ptr[Int]): Ptr[CurlMsg] = extern

  /**
   * Name: curl_multi_strerror()
   *
   * Desc: The curl_multi_strerror function may be used to turn a CURLMcode value into the
   * equivalent human readable error string. This is useful for printing meaningful error messages.
   *
   * Returns: A pointer to a null-terminated error message.
   */
  @name("curl_multi_strerror")
  def strerror(code: CurlMultiCode): CString = extern

  /**
   * Name: curl_socket_callback
   *
   * Desc:
   *
   * Returns:
   */
  @name("curl_socket_callback") opaque type CurlSocketCallback =
    CFuncPtr5[Ptr[Curl], CurlSocket, Int, Ptr[Byte], Ptr[Byte], Int]
  object CurlSocketCallback:
    given Tag[CurlSocketCallback] =
      Tag.materializeCFuncPtr5[Ptr[Curl], CurlSocket, Int, Ptr[Byte], Ptr[Byte], Int]

    inline def apply(
        inline o: CFuncPtr5[Ptr[Curl], CurlSocket, Int, Ptr[Byte], Ptr[Byte], Int],
    ): CurlSocketCallback = o

    // inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): CurlSocketCallback =
    //   CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    //
    // extension (v: CurlSocketCallback)
    //   inline def value: CFuncPtr5[Ptr[Curl], CurlSocket, Int, Ptr[Byte], Ptr[Byte], Int] = v
    //
    //   inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  /**
   * Name: curl_multi_timer_callback
   *
   * Desc: Called by libcurl whenever the library detects a change in the maximum number of
   * milliseconds the app is allowed to wait before curl_multi_socket() or curl_multi_perform() must
   * be called (to allow libcurl's timed events to take place).
   *
   * Returns: The callback should return zero.
   */
  @name("curl_multi_timer_callback") opaque type CurlMultiTimerCallback =
    CFuncPtr3[Ptr[CurlMulti], CLongInt, Ptr[Byte], Int]

  object CurlMultiTimerCallback:
    given Tag[CurlMultiTimerCallback] =
      Tag.materializeCFuncPtr3[Ptr[CurlMulti], CLongInt, Ptr[Byte], Int]

    inline def apply(
        inline o: CFuncPtr3[Ptr[CurlMulti], CLongInt, Ptr[Byte], Int],
    ): CurlMultiTimerCallback = o

    // inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): CurlMultiTimerCallback =
    //   CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])

    // extension (v: CurlMultiTimerCallback)
    //   inline def value: CFuncPtr3[Ptr[CurlMulti], CLongInt, Ptr[Byte], Int] = v

    //   inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)

  // func `curl_multi_socket` is DEPRECATED since curl 7.19.5

  @name("curl_multi_socket_action")
  def socket_action(
      multiHandle: Ptr[CurlMulti],
      s: CurlSocket,
      ev_bitmask: Int,
      running_handles: Ptr[Int],
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
  def timeout(multiHandle: Ptr[CurlMulti], milliseconds: Ptr[CLongInt]): CurlMultiCode =
    extern

  @name("CURLMoption") opaque type CurlMultiOption = UInt
  object CurlMultiOption:
    given Tag[CurlMultiOption] = Tag.UInt

    inline def define(inline a: Long): CurlMultiOption = a.toUInt

    /** This is the socket callback function pointer */
    val CURLMOPT_SOCKETFUNCTION = define(20001)

    /** This is the argument passed to the socket callback */
    val CURLMOPT_SOCKETDATA = define(10002)

    /** set to 1 to enable pipelining for this multi handle */
    val CURLMOPT_PIPELINING = define(3)

    /** This is the timer callback function pointer */
    val CURLMOPT_TIMERFUNCTION = define(20004)

    /** This is the argument passed to the timer callback */
    val CURLMOPT_TIMERDATA = define(10005)

    /** maximum number of entries in the connection cache */
    val CURLMOPT_MAXCONNECTS = define(6)

    /** maximum number of (pipelining) connections to one host */
    val CURLMOPT_MAX_HOST_CONNECTIONS = define(7)

    /** maximum number of requests in a pipeline */
    val CURLMOPT_MAX_PIPELINE_LENGTH = define(8)

    /**
     * a connection with a content-length longer than this will not be considered for pipelining
     */
    val CURLMOPT_CONTENT_LENGTH_PENALTY_SIZE = define(30009)

    /**
     * a connection with a chunk length longer than this will not be considered for pipelining
     */
    val CURLMOPT_CHUNK_LENGTH_PENALTY_SIZE = define(30010)

    /** a list of site names(+port) that are blocked from pipelining */
    val CURLMOPT_PIPELINING_SITE_BL = define(10011)

    /** a list of server types that are blocked from pipelining */
    val CURLMOPT_PIPELINING_SERVER_BL = define(10012)

    /** maximum number of open connections in total */
    val CURLMOPT_MAX_TOTAL_CONNECTIONS = define(13)

    /** This is the server push callback function pointer */
    val CURLMOPT_PUSHFUNCTION = define(20014)

    /** This is the argument passed to the server push callback */
    val CURLMOPT_PUSHDATA = define(10015)

    /** maximum number of concurrent streams to support on a connection */
    val CURLMOPT_MAX_CONCURRENT_STREAMS = define(16)

    /** the last unused */
    val CURLMOPT_LASTENTRY = define(17)

    inline def getName(inline value: CurlMultiOption): Option[String] =
      inline value match
        case CURLMOPT_SOCKETFUNCTION              => Some("CURLMOPT_SOCKETFUNCTION")
        case CURLMOPT_SOCKETDATA                  => Some("CURLMOPT_SOCKETDATA")
        case CURLMOPT_PIPELINING                  => Some("CURLMOPT_PIPELINING")
        case CURLMOPT_TIMERFUNCTION               => Some("CURLMOPT_TIMERFUNCTION")
        case CURLMOPT_TIMERDATA                   => Some("CURLMOPT_TIMERDATA")
        case CURLMOPT_MAXCONNECTS                 => Some("CURLMOPT_MAXCONNECTS")
        case CURLMOPT_MAX_HOST_CONNECTIONS        => Some("CURLMOPT_MAX_HOST_CONNECTIONS")
        case CURLMOPT_MAX_PIPELINE_LENGTH         => Some("CURLMOPT_MAX_PIPELINE_LENGTH")
        case CURLMOPT_CONTENT_LENGTH_PENALTY_SIZE => Some("CURLMOPT_CONTENT_LENGTH_PENALTY_SIZE")
        case CURLMOPT_CHUNK_LENGTH_PENALTY_SIZE   => Some("CURLMOPT_CHUNK_LENGTH_PENALTY_SIZE")
        case CURLMOPT_PIPELINING_SITE_BL          => Some("CURLMOPT_PIPELINING_SITE_BL")
        case CURLMOPT_PIPELINING_SERVER_BL        => Some("CURLMOPT_PIPELINING_SERVER_BL")
        case CURLMOPT_MAX_TOTAL_CONNECTIONS       => Some("CURLMOPT_MAX_TOTAL_CONNECTIONS")
        case CURLMOPT_PUSHFUNCTION                => Some("CURLMOPT_PUSHFUNCTION")
        case CURLMOPT_PUSHDATA                    => Some("CURLMOPT_PUSHDATA")
        case CURLMOPT_MAX_CONCURRENT_STREAMS      => Some("CURLMOPT_MAX_CONCURRENT_STREAMS")
        case CURLMOPT_LASTENTRY                   => Some("CURLMOPT_LASTENTRY")
        case _                                    => None

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
  def setopt(
      multiHandle: Ptr[CurlMulti],
      option: CurlMultiOption*,
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
  def assign(
      multiHandle: Ptr[CurlMulti],
      sockfd: CurlSocket,
      sockp: Ptr[Byte],
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
  def getHandles(multiHandle: Ptr[CurlMulti]): Ptr[Ptr[Curl]] = extern

  /**
   * Name: curl_push_callback
   *
   * Desc: This callback gets called when a new stream is being pushed by the server. It approves or
   * denies the new stream. It can also decide to completely fail the connection.
   *
   * Returns: CURL_PUSH_OK, CURL_PUSH_DENY or CURL_PUSH_ERROROUT
   */

  opaque type CurlPush = Int
  object CurlPush:
    given Tag[CurlPush] = Tag.Int
    inline def define(inline a: Int): CurlPush = a
    val OK = define(0)
    val DENY = define(1)
    val ERROROUT = define(2)

  /** forward declaration only */
  @name("curl_pushheaders") opaque type CurlPushHeaders = CStruct0
  object CurlPushHeaders:
    given Tag[CurlPushHeaders] = Tag.materializeCStruct0Tag

  @name("curl_pushheader_bynum")
  def CurlPushHeaderByNum(h: Ptr[CurlPushHeaders], num: USize): CString = extern

  @name("curl_pushheader_byname")
  def CurlPushHeaderByName(h: Ptr[CurlPushHeaders], name: CString): CString = extern

  @name("curl_push_callback") opaque type CurlPushCallback =
    CFuncPtr5[Ptr[Curl], Ptr[Curl], USize, Ptr[CurlPushHeaders], Ptr[Byte], Int]
  object CurlPushCallback:
    given _tag: Tag[CurlPushCallback] =
      Tag.materializeCFuncPtr5[Ptr[Curl], Ptr[Curl], USize, Ptr[CurlPushHeaders], Ptr[Byte], Int]
    inline def fromPtr(ptr: Ptr[Byte] | CVoidPtr): CurlPushCallback =
      CFuncPtr.fromPtr(ptr.asInstanceOf[Ptr[Byte]])
    inline def apply(
        inline o: CFuncPtr5[Ptr[Curl], Ptr[Curl], USize, Ptr[CurlPushHeaders], Ptr[Byte], Int],
    ): CurlPushCallback = o
    extension (v: CurlPushCallback)
      inline def value
          : CFuncPtr5[Ptr[Curl], Ptr[Curl], USize, Ptr[CurlPushHeaders], Ptr[Byte], Int] = v
      inline def toPtr: CVoidPtr = CFuncPtr.toPtr(v)
