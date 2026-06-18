import scala.scalanative.unsafe.{CStruct2, CStruct4, CLong, CSize, Ptr, Zone, CQuote, CVoidPtr, Tag}
import scala.scalanative.unsafe.{stackalloc, alloc}
import scala.scalanative.unsigned.{USize, UnsignedRichInt, UInt, ULong}
import scala.scalanative.libc.stddef.NULL
import scala.scalanative.libc.string.memcpy
import scala.scalanative.linux.epoll
import scala.scalanative.linux.epoll.{epoll_create1, epoll_ctl, epoll_wait}
import scala.scalanative.posix.{unistd, errno}
import scala.scalanative.posix.timer.timer_create
import scala.scalanative.unsafe.UnsafeRichInt
import scala.util.Using

import snhttp.experimental.curl.curl.{
  CurlEasy,
  CurlMulti,
  CurlMsg,
  CurlOption,
  CurlErrCode,
  CurlMultiErrCode,
  CurlMultiOption,
  CurlWriteCallback,
  CurlSocketCallback,
  CurlSocket,
  CurlCSelect,
  CurlException,
  CurlPoll,
}
import snhttp.experimental.curl.curl.CurlErrCode.RichCurlErrCode
import snhttp.experimental.curl.curl.CurlMultiErrCode.RichCurlMultiErrCode
import snhttp.experimental.curl.libcurl
import snhttp.experimental.curl.libcurl.{CurlHandle, CurlMultiHandle}

object App:

  /** Used by CURLOPT_WRITEDATA and more */
  type CurlWriteData = CStruct2[
    /** variables to track data */
    Ptr[Byte],
    /** function */
    USize,
  ]

  /** Used as share data for multi callback functions */
  type MultiContext = CStruct4[
    Ptr[CurlMultiHandle],
    Int,
    Int,
    CLong,
  ]
  extension (inline ctx: MultiContext)
    inline def multiRef: Ptr[CurlMultiHandle] = ctx._1
    inline def multiRef_=(value: Ptr[CurlMultiHandle]): Unit = !ctx.at1 = value
    inline def epfd: Int = ctx._2
    inline def epfd_=(value: Int): Unit = !ctx.at2 = value
    inline def running: Int = ctx._3
    inline def running_=(value: Int): Unit = !ctx.at3 = value
    inline def runningPtr: Ptr[Int] = ctx.at3 // low level pointer access
    inline def timeout: CLong = ctx._4
    inline def timeout_=(value: CLong): Unit = !ctx.at4 = value

  type FileDescriptor = Int
  type EpollData = FileDescriptor // | CVoidPtr | ULong | UInt
  given Tag[EpollData] = Tag.Int
  // Tag.materializePtrWildcard.asInstanceOf[Tag[EpollData]]
  // Tag.ULong.asInstanceOf[Tag[EpollData]]
  type EpollEvent = CStruct2[
    /** events */
    Int,
    /** data */
    EpollData,
  ]
  extension (inline event: EpollEvent)
    inline def events: Int = event._1
    inline def events_=(value: Int): Unit = !event.at1 = value
    inline def data: EpollData = event._2
    inline def data_=(value: EpollData): Unit = !event.at2 = value

  given zone: Zone = Zone.open()

  val writeDataCallback = CurlWriteCallback.fromScalaFunction {
    (payload: Ptr[Byte], size: CSize, nmemb: CSize, outstream: CVoidPtr) =>
      val userdata = outstream.asInstanceOf[Ptr[CurlWriteData]]
      val recvSize = size * nmemb

      val processed: CSize =
        if recvSize >= 8192.toUSize
        then
          val _ = memcpy((!userdata)._1, payload, 8192.toUSize)
          (!userdata)._2 = 8192.toUSize
          8192.toUSize
        else
          val _ = memcpy((!userdata)._1, payload, recvSize)
          (!userdata)._2 = recvSize
          recvSize

      processed
  }

  /**
   * Callback informed about what to wait for
   *
   * ref: https://curl.se/libcurl/c/CURLMOPT_SOCKETFUNCTION.html
   *
   * inform the application about updates in the socket (file descriptor) status by doing none, one,
   * or multiple calls.
   *
   *   - `clientp` is set with `CURLMOPT_SOCKETDATA`.
   *   - `socketp` is set with `curl_multi_assign` or `NULL`.
   */
  val socketCallback = CurlSocketCallback.fromScalaFunction {
    (easy: Ptr[CurlHandle], socket: CurlSocket, what: CurlPoll, clientp: Ptr[?], socketp: Ptr[?]) =>
      val ctxptr = clientp.asInstanceOf[Ptr[MultiContext]]

      if what == CurlPoll.REMOVE
      then { // Early return
        epoll_ctl(ctxptr.epfd, epoll.EPOLL_CTL_DEL, socket.asInt, NULL)
        libcurl.multiAssign(ctxptr.multiRef, socket, NULL)
        0
      } //
      else {
        val events = what match
          case CurlPoll.IN     => epoll.EPOLLIN
          case CurlPoll.OUT    => epoll.EPOLLOUT
          case CurlPoll.INOUT  => epoll.EPOLLIN | epoll.EPOLLOUT
          case CurlPoll.REMOVE => -1 // Unreachable

        if events == -1
        then //
          -1
        else {
          val epollEvent = stackalloc[EpollEvent]()
          epollEvent.events = events
          epollEvent.data = socket.value

          val op = if socketp == NULL then epoll.EPOLL_CTL_ADD else epoll.EPOLL_CTL_MOD
          val rc = epoll_ctl(ctxptr.epfd, op, socket.value, epollEvent)
          if rc < 0
          then
            // epoll_ctl failed, return error to libcurl,
            // libcurl will then call the callback again with `CURL_POLL_REMOVE`
            -1
          else
            libcurl.multiAssign(ctxptr.multiRef, socket, ctxptr)
            0
        }
      }
  }

  /**
   * Callback to receive timeout values
   *
   * ref: https://curl.se/libcurl/c/CURLMOPT_TIMERFUNCTION.html
   *
   *   - timeout_ms >= 0 : valid expire times in number of milliseconds
   *   - timeout_ms < 0 : the main program should delete the timer
   */
  val timerCallback = libcurl.CurlMultiTimerCallback.fromScalaFunction {
    (multi: Ptr[CurlMultiHandle], timeoutMs: CLong, clientp: CVoidPtr) =>
      val ctxptr = !clientp.asInstanceOf[Ptr[MultiContext]]
      ctxptr.timeout = timeoutMs
      0
  }

  def toEpollTimeout(curlSocketActionTimeout: CLong): Int =
    Math.clamp(curlSocketActionTimeout.toInt, -1, Int.MaxValue)

  @main
  def main(): Unit = {
    println("Curl multi epoll example started")

    val writeData = alloc[CurlWriteData]()
    (!writeData)._1 = alloc[Byte](8192)
    (!writeData)._2 = 0.toUSize

    val globalEpollFd = epoll_create1(epoll.EPOLL_CLOEXEC)
    if (globalEpollFd < 0)
      throw new RuntimeException(s"Failed to create epoll instance")

    Using.resource(CurlMulti()) { multi =>

      val ctxptr = alloc[MultiContext]()
      ctxptr.multiRef = multi.ref
      ctxptr.epfd = globalEpollFd
      ctxptr.running = 0
      ctxptr.timeout = -1

      multi.setPtrOption(CurlMultiOption.TIMERDATA, ctxptr)
      multi.setFuncPtrOption(CurlMultiOption.TIMERFUNCTION, timerCallback.asFuncPtr)

      multi.setPtrOption(CurlMultiOption.SOCKETDATA, ctxptr)
      multi.setFuncPtrOption(CurlMultiOption.SOCKETFUNCTION, socketCallback.asFuncPtr)

      Using.resources(CurlEasy(), CurlEasy()) { (curl1, curl2) =>
        curl1.setCStringOption(CurlOption.URL, c"https://httpbingo.org/get")
        curl1.setPtrOption(CurlOption.WRITEDATA, writeData)
        curl1.setFuncPtrOption(CurlOption.WRITEFUNCTION, writeDataCallback.asFuncPtr)

        curl2.setCStringOption(CurlOption.URL, c"https://example.com/")
        multi.addCurlEasy(curl1)
        multi.addCurlEasy(curl2)

        /* kickstart the multi epoll event */
        multi.socketAction(CurlSocket.TIMEOUT, CurlCSelect.NONE, ctxptr.runningHandle)

        val bufferSize = 64
        val buffer = stackalloc[EpollEvent](bufferSize)
        var err = CurlMultiErrCode.OK

        while ctxptr.running > 0 && err == CurlMultiErrCode.OK
        do {
          val timeoutMs = toEpollTimeout(ctxptr.timeout)
          val recvEvents = epoll_wait(ctxptr.epfd, buffer, bufferSize, timeoutMs)

          if (recvEvents < 0)
            if errno.errno == errno.EINTR
            then () // Retry if interrupted by signal
            else throw new CurlException(s"epoll_wait failed with error ${errno.errno}")
          else if (recvEvents == 0) // timeout expired, simply kick the multi socket action again
            ctxptr.timeout = -1
            err = multi.socketAction(CurlSocket.TIMEOUT, CurlCSelect.NONE, ctxptr.runningPtr)
          else
            for i <- 0 until recvEvents do {
              val event = buffer(i)

              var action: CurlCSelect = CurlCSelect.NONE
              if ((event.events & epoll.EPOLLIN) != 0)
                action |= CurlCSelect.IN
              if ((event.events & epoll.EPOLLOUT) != 0)
                action |= CurlCSelect.OUT
              if ((event.events & (epoll.EPOLLERR | epoll.EPOLLHUP)) != 0)
                action |= CurlCSelect.ERR

              val epfd = event.data.asInstanceOf[FileDescriptor]
              err = multi.socketAction(
                CurlSocket.fromFileDescriptor(epfd),
                action,
                ctxptr.runningPtr,
              )
            }
        }

        val respCode = curl1.info.responseCode
        println(s"Response code of first curl handle is ${respCode}")

        val headers2 = curl2.info.headers
        println(s"Response headers of second curl handle are ${headers2}")

        multi.removeCurlEasy(curl1)
        multi.removeCurlEasy(curl2)
      }

    }

    /**
     * Clean up the global epoll instance after all multi handles are closed, otherwise the process
     * will leak file descriptors.
     *
     * As curl doc suggests:
     *
     * The `socket_callback` may get invoked at anytime, may even happen after all transfers are
     * done and is likely to happen during a call to `curl_multi_cleanup` when cached connections
     * are shut down.
     */
    unistd.close(globalEpollFd)

    zone.close()
  }
