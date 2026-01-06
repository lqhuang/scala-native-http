package snhttp.experimental.curl

import scala.scalanative.unsafe.{Ptr, Size, CString, toCString, stackalloc, UnsafeRichInt, CVoidPtr}
import scala.scalanative.unsigned.UInt
import scala.scalanative.posix.stddef.size_t
import scala.scalanative.posix.sys.select.fd_set

import _root_.snhttp.experimental.libcurl.{
  Curl as _Curl,
  CurlMulti as _CurlMulti,
  CurlWaitFd as _CurlWaitFd,
  CurlMultiOption,
  CurlMultiCode,
  CurlMsg,
  CurlSocket,
}
import _root_.snhttp.experimental.libcurl

class CurlMulti(ptr: Ptr[_CurlMulti]) extends AnyVal:

  def addCurlEasy(easyPtr: Ptr[_Curl]): CurlMultiCode =
    libcurl.multiAddHandle(ptr, easyPtr)

  def removeCurlEasy(easyPtr: Ptr[_Curl]): CurlMultiCode =
    libcurl.multiRemoveHandle(ptr, easyPtr)

  def fdset(
      readFdSet: Ptr[fd_set],
      writeFdSet: Ptr[fd_set],
      excFdSet: Ptr[fd_set],
      maxFd: Ptr[Int],
  ): CurlMultiCode =
    libcurl.multiFdSet(ptr, readFdSet, writeFdSet, excFdSet, maxFd)

  def wait(
      extraFds: Ptr[_CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiCode =
    libcurl.multiWait(ptr, extraFds, extraNfds, timeoutMs, ret)

  def poll(
      extraFds: Ptr[_CurlWaitFd],
      extraNfds: UInt,
      timeoutMs: Int,
      ret: Ptr[Int],
  ): CurlMultiCode =
    libcurl.multiPoll(ptr, extraFds, extraNfds, timeoutMs, ret)

  def wakeup(): CurlMultiCode =
    libcurl.multiWakeup(ptr)

  def perform(runningHandles: Ptr[Int]): CurlMultiCode =
    libcurl.multiPerform(ptr, runningHandles)

  def cleanup(): CurlMultiCode =
    libcurl.multiCleanup(ptr)

  def infoRead(msgsInQueue: Ptr[Int]): Ptr[CurlMsg] =
    libcurl.multiInfoRead(ptr, msgsInQueue)

  def socketAction(s: CurlSocket, evBitmask: Int, runningHandles: Ptr[Int]): CurlMultiCode =
    libcurl.multiSocketAction(ptr, s, evBitmask, runningHandles)

  def timeout(milliseconds: Int): Int =
    val ms = stackalloc[Size]()
    !ms = milliseconds.toSize
    libcurl.multiTimeout(ptr, ms)

  def setOption(option: CurlMultiOption, value: Size): CurlMultiCode =
    libcurl.multiSetopt(ptr, option, value)

  def setOption(option: CurlMultiOption, value: CString): CurlMultiCode =
    libcurl.multiSetopt(ptr, option, value)

  def assign(sockfd: CurlSocket, sockp: CVoidPtr): CurlMultiCode =
    libcurl.multiAssign(ptr, sockfd, sockp)

  def getHandles(): Ptr[Ptr[_Curl]] =
    libcurl.multiGetHandles(ptr)

object CurlMulti:

  def init(): CurlMulti =
    val ptr = libcurl.multiInit()
    new CurlMulti(libcurl.multiInit())

end CurlMulti
