package snhttp.experimental.curl

import scala.util.Using.Releasable

import scala.scalanative.unsafe.{Ptr, Size, CString, stackalloc, CVoidPtr, CLong}
import scala.scalanative.unsigned.UInt
import scala.scalanative.libc.stddef.NULL
import scala.scalanative.posix.sys.select.fd_set

import _root_.snhttp.experimental.libcurl.{
  CurlMulti as _CurlMulti,
  CurlWaitFd as _CurlWaitFd,
  CurlMultiOption,
  CurlMultiCode,
  CurlSocket,
}
import _root_.snhttp.experimental.libcurl

class CurlMulti(val ptr: Ptr[_CurlMulti]) extends AnyVal:

  transparent inline def addCurlEasy(easy: CurlEasy): CurlMultiCode =
    libcurl.multiAddHandle(ptr, easy.ptr)

  transparent inline def removeCurlEasy(easy: CurlEasy): CurlMultiCode =
    libcurl.multiRemoveHandle(ptr, easy.ptr)

  transparent inline def fdset(
      readFdSet: Ptr[fd_set],
      writeFdSet: Ptr[fd_set],
      excFdSet: Ptr[fd_set],
      maxFd: Ptr[Int],
  ): CurlMultiCode =
    libcurl.multiFdSet(ptr, readFdSet, writeFdSet, excFdSet, maxFd)

  transparent inline def wait(
      extraFds: Ptr[_CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiCode =
    libcurl.multiWait(ptr, extraFds, extraNfds, timeoutMs, ret)

  transparent inline def poll(
      extraFds: Ptr[_CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiCode =
    libcurl.multiPoll(ptr, extraFds, extraNfds, timeoutMs, ret)

  transparent inline def wakeup(): CurlMultiCode =
    libcurl.multiWakeup(ptr)

  transparent inline def perform(runningHandles: Ptr[Int]): CurlMultiCode =
    libcurl.multiPerform(ptr, runningHandles)

  transparent inline def cleanup(): CurlMultiCode =
    libcurl.multiCleanup(ptr)

  // Implementation restriction: cannot use private constructors in inline methods
  def infoRead(msgsInQueue: Ptr[Int]): Option[CurlMsg] =
    val msg = libcurl.multiInfoRead(ptr, msgsInQueue)
    if msg == NULL then None else Some(CurlMsg(msg))

  transparent inline def socketAction(
      s: CurlSocket,
      evBitmask: Int,
      runningHandles: Ptr[Int],
  ): CurlMultiCode =
    libcurl.multiSocketAction(ptr, s, evBitmask, runningHandles)

  transparent inline def timeout(milliseconds: Int): Int =
    val ms = stackalloc[Size]()
    libcurl.multiTimeout(ptr, ms)

  transparent inline def setCLongOption(option: CurlMultiOption, value: CLong): Unit =
    val ret = libcurl.multiSetopt(ptr, option, value)

  transparent inline def setCStringOption(option: CurlMultiOption, value: CString): Unit =
    val ret = libcurl.multiSetopt(ptr, option, value)

  transparent inline def setPtrOption(option: CurlMultiOption, value: Ptr[?]): Unit =
    val ret = libcurl.multiSetopt(ptr, option, value)

  transparent inline def assign(sockfd: CurlSocket, sockp: CVoidPtr): CurlMultiCode =
    libcurl.multiAssign(ptr, sockfd, sockp)

  // Should we expose this?
  // transparent inline def getHandles(): Ptr[Ptr[_Curl]] =
  //   libcurl.multiGetHandles(ptr)

object CurlMulti:

  given Releasable[CurlMulti] with
    transparent inline def release(multi: CurlMulti): Unit =
      val ret = multi.cleanup()

  def apply(): CurlMulti =
    val ptr = libcurl.multiInit()
    if (ptr == null)
      throw new RuntimeException("Failed to initialize CurlMulti")
    new CurlMulti(ptr)

  def apply(ptr: Ptr[_CurlMulti]): CurlMulti =
    new CurlMulti(ptr)

end CurlMulti
