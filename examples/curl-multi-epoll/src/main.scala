import scala.scalanative.unsafe.{
  CStruct2,
  CStruct4,
  CLong,
  CSize,
  Ptr,
  stackalloc,
  alloc,
  Zone,
  CQuote,
  sizeof,
  CVoidPtr,
  Tag,
}
import scala.scalanative.unsigned.{USize, UnsignedRichInt, UInt, ULong}
import scala.scalanative.libc.stddef.NULL
import scala.scalanative.libc.string.memcpy
import scala.scalanative.linux.epoll
import scala.scalanative.linux.epoll.{epoll_create1, epoll_ctl, epoll_wait}
import scala.scalanative.posix.{unistd, poll, errno}
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
}
import snhttp.experimental.curl.curl.CurlErrCode.RichCurlErrCode
import snhttp.experimental.curl.curl.CurlMultiErrCode.RichCurlMultiErrCode
import snhttp.experimental.curl.curl.CurlPoll
import snhttp.experimental.curl.libcurl
import snhttp.experimental.curl.libcurl.CurlHandle
import snhttp.experimental.curl.libcurl.CurlMultiHandle

object App:

  /** Used by CURLOPT_WRITEDATA and more */
  type CurlWriteData = CStruct2[
    /** variables to track data */
    Ptr[Byte],
    /** function */
    USize,
  ]

  /** Used as share data among easy and multi handle */
  type MultiContext = CStruct4[
    Ptr[CurlMultiHandle],
    Int,
    Ptr[Int],
    CLong,
  ]
  extension (inline ctx: MultiContext)
    inline def multiRef: Ptr[CurlMultiHandle] = ctx._1
    inline def multiRef_=(value: Ptr[CurlMultiHandle]): Unit = !ctx.at1 = value
    inline def epfd: Int = ctx._2
    inline def epfd_=(value: Int): Unit = !ctx.at2 = value
    inline def runningHandle: Ptr[Int] = ctx._3
    inline def runningHandle_=(value: Ptr[Int]): Unit = !ctx.at3 = value
    inline def timeout: CLong = ctx._4
    inline def timeout_=(value: CLong): Unit = !ctx.at4 = value

  type FileDescriptor = Int
  type EpollData = CVoidPtr | FileDescriptor | UInt | ULong
  given Tag[EpollData] = Tag.ULong.asInstanceOf[Tag[EpollData]]
  type EpollEvent = CStruct2[
    /** events */
    Int,
    /** data */
    EpollData,
  ]
  extension (inline event: EpollEvent)
    inline def events: Int = event._1
    inline def events_=(value: Int): Unit = !event.at1 = value
    inline def data: CVoidPtr | Int | UInt | ULong = event._2
    inline def data_=(value: CVoidPtr | Int | UInt | ULong): Unit = !event.at2 = value

  given zone: Zone = Zone.open()

  val writeDataCallback = CurlWriteCallback.fromScalaFunction {
    (payload: Ptr[Byte], nmemb: CSize, size: CSize, outstream: CVoidPtr) =>
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

      var earlyReturn = false

      val events = what match {
        case CurlPoll.IN     => epoll.EPOLLIN
        case CurlPoll.OUT    => epoll.EPOLLOUT
        case CurlPoll.INOUT  => epoll.EPOLLIN | epoll.EPOLLOUT
        case CurlPoll.REMOVE => epoll.EPOLL_CTL_DEL
      }

      val epollEvent = stackalloc[EpollEvent]()
      epollEvent.events = events
      epollEvent.data = socket.asInt

      val op = if socketp == NULL then epoll.EPOLL_CTL_ADD else epoll.EPOLL_CTL_MOD
      epoll_ctl(ctxptr.epfd, op, socket.asInt, epollEvent)
      libcurl.multiAssign(ctxptr.multiRef, socket, socketp)

      0
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
    if curlSocketActionTimeout < 0 then //
      -1
    else if curlSocketActionTimeout == 0 then //
      0
    else if curlSocketActionTimeout > Int.MaxValue.toCSSize then //
      Int.MaxValue
    else //
      curlSocketActionTimeout.toInt

  @main
  def main(): Unit = {

    println("Curl multi epoll example started")

    val writeData = stackalloc[CurlWriteData]()
    (!writeData)._1 = stackalloc[Byte](8192)
    (!writeData)._2 = 0.toUSize

    Using.resource(CurlMulti()) { multi =>

      val ctxptr = stackalloc[MultiContext]()
      (!ctxptr).multiRef = multi.ref
      (!ctxptr).epfd = {
        val fd = epoll_create1(epoll.EPOLL_CLOEXEC)
        if (fd < 0)
          throw new RuntimeException(s"Failed to create epoll instance")
        fd
      }
      (!ctxptr).runningHandle = stackalloc[Int]()
      (!ctxptr).timeout = 0

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
        var recvEvents = 0
        var err = CurlMultiErrCode.OK

        while {
          val msgCount = stackalloc[Int]()
          val msg = multi.infoRead(msgCount)

          val timeoutMs = toEpollTimeout(ctxptr.timeout)
          recvEvents = epoll_wait(ctxptr.epfd, buffer, bufferSize, timeoutMs)
          println(s"epoll_wait returns with ${recvEvents} events, timeout was ${timeoutMs} ms")

          !ctxptr.runningHandle > 0 && recvEvents > 0 && err == CurlMultiErrCode.OK
        }
        do
          for i <- 0 until recvEvents do {
            val event = buffer(i)

            val action: CurlCSelect = event.events match {
              case e if (e & epoll.EPOLLIN) != 0                     => CurlCSelect.IN
              case e if (e & epoll.EPOLLOUT) != 0                    => CurlCSelect.OUT
              case e if (e & (epoll.EPOLLERR | epoll.EPOLLHUP)) != 0 => CurlCSelect.ERR
              case _                                                 => CurlCSelect.ERR
            }
            val epfd = event.data.asInstanceOf[FileDescriptor]
            println(
              s"Received epoll event with events ${event.events}, then action is ${action}",
            )

            err = action match {
              case CurlCSelect.IN | CurlCSelect.OUT | CurlCSelect.NONE =>
                multi.socketAction(
                  CurlSocket.fromFileDescriptor(epfd),
                  action,
                  ctxptr.runningHandle,
                )
              case CurlCSelect.ERR =>
                CurlMultiErrCode.UNRECOVERABLE_POLL
            }
          }

        val respCode = curl1.info.responseCode
        println(s"Response code of first curl handle is ${respCode}")

        val headers2 = curl2.info.headers
        println(s"Response headers of second curl handle are ${headers2}")

        multi.removeCurlEasy(curl1)
        multi.removeCurlEasy(curl2)
      }

      unistd.close(ctxptr.epfd)

    }

    zone.close()
  }
