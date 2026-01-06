package snhttp.experimental._libcurl

import scala.scalanative.unsafe.{Ptr, CString, CVoidPtr, CLong}
import scala.scalanative.unsafe.{alloc, extern, name, link, define}
import scala.scalanative.unsigned.UInt
import scala.scalanative.posix.time.time_t
import scala.scalanative.posix.stddef.size_t
import scala.scalanative.posix.sys.select.fd_set

import snhttp.experimental._libcurl.options.CurlEasyOption
import snhttp.experimental._libcurl.curl.{
  CurlGlobalFlag,
  CurlErrCode,
  CurlOption,
  Curl,
  CurlSslBackendId,
  CurlSslBackend,
  CurlSslSet,
  CurlSlist,
  CurlInfo,
  CurlPause,
  CurlSocket,
}
import snhttp.experimental._libcurl.header.{CurlHeaderOrigin, CurlHeaderErrCode, CurlHeader}
import snhttp.experimental._libcurl.multi.{
  CurlMulti,
  CurlMultiCode,
  CurlWaitFd,
  CurlMsg,
  CurlMultiOption,
  CurlPushHeaders,
}
import snhttp.experimental._libcurl.websockets.{CurlWsFrame, CurlWsSendFlag}
import snhttp.experimental._libcurl.system.CurlOff

@link("curl")
@extern
@define("CURL_NO_OLDIES") // deprecate all outdated
object functions:

  /**
   * <curl/curl.h>
   */

  /**
   * NAME curl_version()
   *
   * DESCRIPTION
   *
   * Returns a static ascii string of the libcurl version.
   */
  @name("curl_version")
  def curlVersion(): CString = extern

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
  @name("curl_global_init")
  def globalInit(flags: CurlGlobalFlag): CurlErrCode = extern

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
  @name("curl_global_cleanup")
  def globalCleanup(): Unit = extern

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
  @name("curl_global_trace")
  def globalTrace(config: CString): CurlErrCode = extern

  @name("curl_global_sslset")
  def globalSslset(
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
  @name("curl_slist_append")
  def slistAppend(list: Ptr[CurlSlist], data: CString): Ptr[CurlSlist] = extern

  /**
   * NAME curl_slist_free_all()
   *
   * DESCRIPTION
   *
   * free a previously built curl_slist.
   */
  @name("curl_slist_free_all")
  def slistFreeAll(list: Ptr[CurlSlist]): Unit = extern

  /**
   * NAME curl_getdate()
   *
   * DESCRIPTION
   *
   * Returns the time, in seconds since 1 Jan 1970 of the time string given in the first argument.
   * The time argument in the second parameter is unused and should be set to NULL.
   */
  @name("curl_getdate")
  def curlGetdate(p: CString, unused: Ptr[time_t]): time_t = extern

  /**
   * NAME curl_easy_escape()
   *
   * DESCRIPTION
   *
   * Escapes URL strings (converts all letters consider illegal in URLs to their %XX versions). This
   * function returns a new allocated string or NULL if an error occurred.
   */
  @name("curl_easy_escape")
  def easyEscape(handle: Ptr[Curl], string: CString, length: Int): CString = extern

  /**
   * NAME curl_easy_unescape()
   *
   * DESCRIPTION
   *
   * Unescapes URL encoding in strings (converts all %XX codes to their 8bit versions). This
   * function returns a new allocated string or NULL if an error occurred. Conversion Note: On
   * non-ASCII platforms the ASCII %XX codes are converted into the host encoding.
   */
  @name("curl_easy_unescape")
  def easyUnescape(
      handle: Ptr[Curl],
      string: CString,
      length: Int,
      outlength: Ptr[Int],
  ): CString = extern

  /**
   * NAME curl_easy_strerror()
   *
   * DESCRIPTION
   *
   * The curl_easy_strerror function may be used to turn a CURLcode value into the equivalent human
   * readable error string. This is useful for printing meaningful error messages.
   */
  @name("curl_easy_strerror")
  def easyStrError(code: CurlErrCode): CString = extern

  /**
   * NAME curl_easy_pause()
   *
   * DESCRIPTION
   *
   * The curl_easy_pause function pauses or unpauses transfers. Select the new state by setting the
   * bitmask, use the convenience defines below.
   */
  @name("curl_easy_pause")
  def easyPause(handle: Ptr[Curl], bitmask: CurlPause): CurlErrCode = extern

  /**
   * <curl/easy.h>
   */

  @name("curl_easy_init")
  def easyInit(): Ptr[Curl] = extern

  @name("curl_easy_setopt")
  def easySetopt(
      handle: Ptr[Curl],
      option: CurlOption,
      params: Any*, // CVarArgList
  ): CurlErrCode =
    extern

  @name("curl_easy_perform")
  def easyPerform(handle: Ptr[Curl]): CurlErrCode =
    extern

  @name("curl_easy_cleanup")
  def easyCleanup(handle: Ptr[Curl]): Unit =
    extern

  /**
   * NAME curl_easy_getinfo()
   *
   * DESCRIPTION
   *
   * Request internal information from the curl session with this function. The third argument MUST
   * be pointing to the specific type of the used option which is documented in each man page of the
   * option. The data pointed to will be filled in accordingly and can be relied upon only if the
   * function returns CURLE_OK. This function is intended to get used *AFTER* a performed transfer,
   * all results from this function are undefined until the transfer is completed.
   */
  @name("curl_easy_getinfo")
  def easyGetInfo(
      handle: Ptr[Curl],
      info: CurlInfo,
      params: Any*, // CVarArgList
  ): CurlErrCode =
    extern

  /**
   * NAME curl_easy_duphandle()
   *
   * DESCRIPTION
   *
   * Creates a new curl session handle with the same options set for the handle passed in.
   * Duplicating a handle could only be a matter of cloning data and options, internal state info
   * and things like persistent connections cannot be transferred. It is useful in multithreaded
   * applications when you can run curl_easy_duphandle() for each new thread to avoid a series of
   * identical curl_easy_setopt() invokes in every thread.
   */
  @name("curl_easy_duphandle")
  def easyDupHandle(handle: Ptr[Curl]): Ptr[Curl] =
    extern

  /**
   * NAME curl_easy_reset()
   *
   * DESCRIPTION
   *
   * Re-initializes a CURL handle to the default values. This puts back the handle to the same state
   * as it was in when it was just created.
   *
   * It does keep: live connections, the Session ID cache, the DNS cache and the cookies.
   */
  @name("curl_easy_reset")
  def easyReset(handle: Ptr[Curl]): Unit =
    extern

  /**
   * NAME curl_easy_recv()
   *
   * DESCRIPTION
   *
   * Receives data from the connected socket. Use after successful curl_easy_perform() with
   * CURLOPT_CONNECT_ONLY option.
   */
  @name("curl_easy_recv")
  def easyRecv(handle: Ptr[Curl], buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    extern

  /**
   * NAME curl_easy_send()
   *
   * DESCRIPTION
   *
   * Sends data over the connected socket. Use after successful curl_easy_perform() with
   * CURLOPT_CONNECT_ONLY option.
   */
  @name("curl_easy_send")
  def easySend(handle: Ptr[Curl], buffer: Ptr[Byte], buflen: size_t, n: Ptr[size_t]): CurlErrCode =
    extern

  /**
   * NAME curl_easy_upkeep()
   *
   * DESCRIPTION
   *
   * Performs connection upkeep for the given session handle.
   */
  @name("curl_easy_upkeep")
  def easyUpkeep(handle: Ptr[Curl]): CurlErrCode =
    extern

  @name("curl_easy_header")
  def easyHeader(
      easy: Ptr[Curl],
      name: CString,
      index: size_t,
      origin: CurlHeaderOrigin,
      request: Int,
      hout: Ptr[Ptr[CurlHeader]],
  ): CurlHeaderErrCode = extern

  @name("curl_easy_nextheader")
  def easyNextHeader(
      easy: Ptr[Curl],
      origin: CurlHeaderOrigin,
      request: Int,
      prev: Ptr[CurlHeader],
  ): Ptr[CurlHeader] = extern

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
  def multiTimeout(handle: Ptr[CurlMulti], milliseconds: Ptr[CLong]): CurlMultiCode =
    extern

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

  @name("curl_pushheader_bynum")
  def pushHeaderByNum(handle: Ptr[CurlPushHeaders], num: size_t): CString = extern

  @name("curl_pushheader_byname")
  def pushHeaderByName(handle: Ptr[CurlPushHeaders], name: CString): CString = extern

  /**
   * <curl/options.h>
   */

  @name("curl_easy_option_by_id")
  def easyOptionById(id: CurlOption): Ptr[CurlEasyOption] = extern

  @name("curl_easy_option_by_name")
  def easyOptionByName(name: CString): Ptr[CurlEasyOption] = extern

  @name("curl_easy_option_next")
  def easyOptionNext(prev: Ptr[CurlEasyOption]): Ptr[CurlEasyOption] = extern

  /**
   * <curl/websockets.h>
   */

  /**
   * NAME curl_ws_recv()
   *
   * DESCRIPTION
   *
   * Receives data from the websocket connection. Use after successful curl_easy_perform() with
   * CURLOPT_CONNECT_ONLY option.
   */
  @name("curl_ws_recv")
  def wsRecv(
      curl: Ptr[Curl],
      buffer: CVoidPtr,
      buflen: size_t,
      recv: Ptr[size_t],
      metap: Ptr[Ptr[CurlWsFrame]],
  ): CurlErrCode = extern

  /**
   * NAME curl_ws_send()
   *
   * DESCRIPTION
   *
   * Sends data over the websocket connection. Use after successful curl_easy_perform() with
   * CURLOPT_CONNECT_ONLY option.
   */
  @name("curl_ws_send")
  def wsSend(
      curl: Ptr[Curl],
      buffer: CVoidPtr,
      buflen: size_t,
      sent: Ptr[size_t],
      fragsize: CurlOff,
      flags: CurlWsSendFlag,
  ): CurlErrCode = extern

  @name("curl_ws_meta")
  def wsMeta(curl: Ptr[Curl]): Ptr[CurlWsFrame] = extern
