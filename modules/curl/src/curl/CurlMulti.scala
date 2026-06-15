package snhttp.experimental.curl
package curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{Ptr, Size, CString, stackalloc, CVoidPtr, CLong, CFuncPtr}
import scala.scalanative.unsigned.UInt
import scala.scalanative.libc.stddef.NULL as NullPtr
import scala.scalanative.posix.sys.select.fd_set

import _root_.snhttp.experimental.curl.libcurl
import _root_.snhttp.experimental.curl.libcurl.{
  CurlMultiHandle,
  CurlWaitFd,
  CurlMultiOption,
  CurlMultiErrCode,
  CurlSocket,
  CurlCselect,
}

class CurlMulti(val ptr: Ptr[CurlMultiHandle]) extends AnyVal:

  private type NullPtr = CVoidPtr

  inline def addCurlEasy(easy: CurlEasy): CurlMultiErrCode =
    libcurl.multiAddHandle(ptr, easy.ptr)

  inline def removeCurlEasy(easy: CurlEasy): CurlMultiErrCode =
    libcurl.multiRemoveHandle(ptr, easy.ptr)

  inline def fdset(
      readFdSet: Ptr[fd_set],
      writeFdSet: Ptr[fd_set],
      excFdSet: Ptr[fd_set],
      maxFd: Ptr[Int],
  ): CurlMultiErrCode =
    libcurl.multiFdSet(ptr, readFdSet, writeFdSet, excFdSet, maxFd)

  inline def wait(
      extraFds: Ptr[CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiErrCode =
    libcurl.multiWait(ptr, extraFds, extraNfds, timeoutMs, ret)

  inline def poll(
      extraFds: Ptr[CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiErrCode =
    libcurl.multiPoll(ptr, extraFds, extraNfds, timeoutMs, ret)

  inline def wakeup(): CurlMultiErrCode =
    libcurl.multiWakeup(ptr)

  inline def perform(runningHandles: Ptr[Int]): CurlMultiErrCode =
    libcurl.multiPerform(ptr, runningHandles)

  inline def cleanup(): CurlMultiErrCode =
    libcurl.multiCleanup(ptr)

  inline def infoRead(msgsInQueue: Ptr[Int]): CurlMsg | NullPtr =
    val msg = libcurl.multiInfoRead(ptr, msgsInQueue)
    if msg == NullPtr then NullPtr else CurlMsg(msg)

  inline def socketAction(
      s: CurlSocket,
      evBitmask: CurlCselect,
      runningHandles: Ptr[Int],
  ): CurlMultiErrCode =
    libcurl.multiSocketAction(ptr, s, evBitmask, runningHandles)

  inline def timeout(milliseconds: Int): Int =
    val ms = stackalloc[Size]()
    libcurl.multiTimeout(ptr, ms)

  inline def setCLongOption(option: CurlMultiOption, value: CLong): Unit =
    val ret = libcurl.multiSetopt(ptr, option, value)
    if ret != CurlMultiErrCode.OK then throw new CurlMultiSetOptionException(option, value, ret)

  inline def setCStringOption(option: CurlMultiOption, value: CString): Unit =
    val ret = libcurl.multiSetopt(ptr, option, value)
    if ret != CurlMultiErrCode.OK then throw new CurlMultiSetOptionException(option, value, ret)

  inline def setPtrOption(option: CurlMultiOption, value: Ptr[?]): Unit =
    val ret = libcurl.multiSetopt(ptr, option, value)
    if ret != CurlMultiErrCode.OK then throw new CurlMultiSetOptionException(option, value, ret)

  inline def setFuncPtrOption(option: CurlMultiOption, value: CFuncPtr): Unit =
    val ret = libcurl.multiSetopt(ptr, option, value)
    if ret != CurlMultiErrCode.OK then throw new CurlMultiSetOptionException(option, value, ret)

  inline def assign(sockfd: CurlSocket, sockp: CVoidPtr): CurlMultiErrCode =
    libcurl.multiAssign(ptr, sockfd, sockp)

  // Should we expose this?
  // inline def getHandles(): Ptr[Ptr[_Curl]] =
  //   libcurl.multiGetHandles(ptr)

object CurlMulti:

  given Releasable[CurlMulti] with
    inline def release(multi: CurlMulti): Unit =
      val ret = multi.cleanup()

  def apply(): CurlMulti =
    val ptr = libcurl.multiInit()
    if (ptr == null)
      throw new RuntimeException("Failed to initialize CurlMulti")
    new CurlMulti(ptr)

  def apply(ptr: Ptr[CurlMultiHandle]): CurlMulti =
    new CurlMulti(ptr)

end CurlMulti
