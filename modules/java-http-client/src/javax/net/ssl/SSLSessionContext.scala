package javax.net.ssl

import java.util.Enumeration

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLSessionContext.html
trait SSLSessionContext {
  def getSession(sessionId: Array[Byte]): SSLSession

  def getIds(): Enumeration[Array[Byte]]

  def setSessionTimeout(seconds: Int): Unit

  def getSessionTimeout(): Int

  def setSessionCacheSize(size: Int): Unit

  def getSessionCacheSize(): Int
}
