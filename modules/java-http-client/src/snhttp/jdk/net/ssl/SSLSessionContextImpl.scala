package javax.scalanativecrypto.internal

import java.util.Enumeration
import java.util.Objects.requireNonNull

import javax.net.ssl.{SSLSessionContext, SSLSession}

class SSLSessionContextImpl(
    private val sessionId: Array[Byte],
) extends SSLSessionContext {

  // get from "javax.net.ssl.sessionCacheSize" or 20480 by Default
  private var _cacheSize: Int = 20480
  private var _timeout: Int = 86400 // 24 hours by default

  def getSession(sessionId: Array[Byte]): SSLSession = {
    requireNonNull(sessionId)
    ???
  }

  def getIds(): Enumeration[Array[Byte]] = ???

  def setSessionTimeout(seconds: Int): Unit =
    require(seconds >= 0, "Session timeout should be non-negative")
    _timeout = seconds

  def getSessionTimeout(): Int = _timeout

  def setSessionCacheSize(size: Int): Unit =
    require(size >= 0, "Cache size should be non-negative")
    _cacheSize = size

  def getSessionCacheSize(): Int = _cacheSize
}
