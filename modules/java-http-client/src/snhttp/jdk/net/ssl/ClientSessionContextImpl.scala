/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.net.ssl

import java.util.{Collections, Collection, Enumeration}
import java.util.Objects.requireNonNull
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.{SSLSessionContext, SSLSession}

import scala.collection.mutable.{LinkedHashMap, Map as MutableMap}
import scala.collection.JavaConverters.asJavaCollection
import scala.scalanative.unsafe.Ptr

import snhttp.jdk.internal.PropertyUtils
import snhttp.experimental.openssl.libssl
import snhttp.experimental.openssl.libssl_internal.enumerations.{
  SSL_CTRL,
  SSL_SESS_CACHE,
  SSL_VERIFY,
}
import snhttp.utils.PointerFinalizer

// Inspired from
//
// 1. <https://github.com/google/conscrypt/blob/master/common/src/main/java/org/conscrypt/ClientSessionContext.java>
// 2. <https://github.com/bcgit/bc-java/blob/main/tls/src/main/java/org/bouncycastle/tls/TlsContext.java>
// 3. <https://docs.openssl.org/master/man7/ossl-guide-tls-introduction/>
abstract class ClientSessionContext extends SSLSessionContext:

  private var sessionCacheSize: Int = PropertyUtils.SESSION_CACHE_SIZE
  private var timeout: Int = 86400 // 24 hours by default
  // private val sessions: MutableMap[Array[Byte], SSLSession] =
  //   LinkedHashMap.empty[Array[Byte], SSLSession]

  def getSession(sessionId: Array[Byte]): SSLSession
  // requireNonNull(sessionId)
  // sessions.get(sessionId) match
  //   case Some(session) => session
  //   case None          => null

  def getIds(): Enumeration[Array[Byte]]
  // Collections.enumeration(asJavaCollection(sessions.keys))

  def setSessionTimeout(seconds: Int): Unit =
    requireNonNull(seconds)
    require(seconds >= 0, "Session timeout should be non-negative")
    timeout = seconds

  def getSessionTimeout(): Int =
    timeout

  def setSessionCacheSize(size: Int): Unit =
    requireNonNull(size)
    require(size >= 0, "Cache size should be non-negative")
    sessionCacheSize = size

  def getSessionCacheSize(): Int =
    sessionCacheSize

  /**
   * Extra methods beyond the SSLSessionContext interface
   */
  def getSession(host: String, port: Int): SSLSession

  def putSession(session: SSLSession): Unit

  def getSessionSize(): Int

  def getCachedSession(host: String, port: Int, sslParams: SSLParametersImpl): SSLSession

  def putCachedSession(session: SSLSession): Unit

end ClientSessionContext

/// Default Context for Client Session Context (auto select between TLSv1.2 and TLSv1.3)
class ClientSessionContextImpl(spi: SSLContextSpiImpl) extends ClientSessionContext:

  protected[ssl] val tlsVersionPtr = libssl.TLS_client_method()
  protected[ssl] val ptr: Ptr[libssl.SSL_CTX] = libssl.SSL_CTX_new(tlsVersionPtr)

  if (ptr == null)
    throw new RuntimeException(
      "Failed to create SSL_CTX for ClientSessionContext",
    )

  PointerFinalizer(this, ptr, _ptr => libssl.SSL_CTX_free(_ptr)): Unit

  /**
   * Debug mode now
   */
  libssl.SSL_CTX_set_verify(
    ptr,
    SSL_VERIFY.NONE,
    null.asInstanceOf[libssl.SSL_verify_cb],
  )

  val currCacheMode =
    libssl.SSL_CTX_ctrl(ptr, SSL_CTRL.SET_SESS_CACHE_MODE, SSL_SESS_CACHE.CLIENT.value, null)
  val currCacheSize =
    libssl.SSL_CTX_ctrl(ptr, SSL_CTRL.SET_SESS_CACHE_SIZE, getSessionCacheSize(), null)

  def newSession() =
    SSLSessionImpl(this)

  def putSession(session: SSLSession): Unit =
    ???

  def getSession(sessionId: Array[Byte]): SSLSession = ???

  def getIds(): Enumeration[Array[Byte]] = ???

  def getSession(host: String, port: Int): SSLSession = ???

  def getSessionSize(): Int = ???

  def getCachedSession(host: String, port: Int, sslParams: SSLParametersImpl): SSLSession =
    ???

  def putCachedSession(session: SSLSession): Unit = ???

end ClientSessionContextImpl
