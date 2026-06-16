package snhttp.experimental.curl
package curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{
  Ptr,
  Size,
  CString,
  stackalloc,
  CVoidPtr,
  CLong,
  CFuncPtr,
  UnsafeRichInt,
}
import scala.scalanative.unsigned.UInt
import scala.scalanative.libc.stddef.NULL as NullPtr
import scala.scalanative.posix.sys.select.fd_set

import _root_.snhttp.experimental.curl.libcurl.{
  CurlMultiHandle,
  CurlWaitFd,
  CurlMultiOption,
  CurlMultiErrCode,
  CurlSocket,
  CurlCSelect,
}

class CurlMulti(val ref: Ptr[CurlMultiHandle]) extends AnyVal:

  private type NullPtr = CVoidPtr

  private transparent inline def assertErrCode(err: CurlMultiErrCode): Unit =
    if (err != CurlMultiErrCode.OK) throw new CurlMultiException(err)

  inline def addCurlEasy(easy: CurlEasy): Unit =
    assertErrCode(libcurl.multiAddHandle(ref, easy.ref))

  inline def removeCurlEasy(easy: CurlEasy): Unit =
    assertErrCode(libcurl.multiRemoveHandle(ref, easy.ref))

  inline def fdset(
      readFdSet: Ptr[fd_set],
      writeFdSet: Ptr[fd_set],
      excFdSet: Ptr[fd_set],
      maxFd: Ptr[Int],
  ): Unit =
    assertErrCode(libcurl.multiFdSet(ref, readFdSet, writeFdSet, excFdSet, maxFd))

  inline def wait(
      extraFds: Ptr[CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiErrCode =
    libcurl.multiWait(ref, extraFds, extraNfds, timeoutMs, ret)

  inline def poll(
      extraFds: Ptr[CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiErrCode =
    libcurl.multiPoll(ref, extraFds, extraNfds, timeoutMs, ret)

  inline def perform(runningHandle: Ptr[Int]): CurlMultiErrCode =
    libcurl.multiPerform(ref, runningHandle)

  inline def infoRead(msgsInQueue: Ptr[Int]): CurlMsg | NullPtr =
    val msg = libcurl.multiInfoRead(ref, msgsInQueue)
    if msg == NullPtr then NullPtr else CurlMsg(msg)

  inline def socketAction(
      s: CurlSocket,
      evBitmask: CurlCSelect,
      runningHandle: Ptr[Int],
  ): CurlMultiErrCode =
    libcurl.multiSocketAction(ref, s, evBitmask, runningHandle)

  inline def timeout(milliseconds: Int): Int =
    val ms = stackalloc[Size]()
    !ms = milliseconds.toSize
    libcurl.multiTimeout(ref, ms)

  inline def wakeup(): Unit =
    assertErrCode(libcurl.multiWakeup(ref))

  inline def cleanup(): Unit =
    assertErrCode(libcurl.multiCleanup(ref))

  inline def assign(sockfd: CurlSocket, sockp: CVoidPtr): Unit =
    assertErrCode(libcurl.multiAssign(ref, sockfd, sockp))

  inline def setCLongOption(option: CurlMultiOption, value: CLong): Unit =
    val err = libcurl.multiSetOpt(ref, option, value)
    if (err != CurlMultiErrCode.OK) throw new CurlMultiSetOptionException(option, value, err)

  inline def setCStringOption(option: CurlMultiOption, value: CString): Unit =
    val err = libcurl.multiSetOpt(ref, option, value)
    if (err != CurlMultiErrCode.OK) throw new CurlMultiSetOptionException(option, value, err)

  inline def setPtrOption(option: CurlMultiOption, value: Ptr[?]): Unit =
    val err = libcurl.multiSetOpt(ref, option, value)
    if (err != CurlMultiErrCode.OK) throw new CurlMultiSetOptionException(option, value, err)

  inline def setFuncPtrOption(option: CurlMultiOption, value: CFuncPtr): Unit =
    val err = libcurl.multiSetOpt(ref, option, value)
    if (err != CurlMultiErrCode.OK) throw new CurlMultiSetOptionException(option, value, err)

  // Should we expose this?
  // inline def getHandles(): Ptr[Ptr[_Curl]] =
  //   libcurl.multiGetHandles(ptr)

object CurlMulti:

  given Releasable[CurlMulti] with
    def release(multi: CurlMulti): Unit =
      val err = multi.cleanup()

  def apply(): CurlMulti =
    val ptr = libcurl.multiInit()
    if (ptr == null)
      throw new CurlException("Failed to initialize CurlMulti Handle")
    new CurlMulti(ptr)

  def apply(ptr: Ptr[CurlMultiHandle]): CurlMulti =
    new CurlMulti(ptr)

end CurlMulti
