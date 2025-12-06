/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.net.ssl

import java.util.Enumeration
import java.util.Objects.requireNonNull
import javax.net.ssl.{SSLSessionContext, SSLSession}
import java.util.{Collections, Collection}

import scala.collection.mutable.{LinkedHashMap, Map as MutableMap}
import scala.collection.JavaConverters.asJavaCollection

import snhttp.jdk.internal.PropertyUtils

// Inspired from
//
//
// 1. <https://github.com/google/conscrypt/blob/master/common/src/main/java/org/conscrypt/ClientSessionContext.java>
// 2. <https://github.com/bcgit/bc-java/blob/main/tls/src/main/java/org/bouncycastle/tls/TlsContext.java>
abstract class ClientSessionContext extends SSLSessionContext:

  private var sessionCacheSize: Int = PropertyUtils.SESSION_CACHE_SIZE
  private var timeout: Int = 86400 // 24 hours by default
  private val sessions: MutableMap[Array[Byte], SSLSession] =
    LinkedHashMap.empty[Array[Byte], SSLSession]

  def getSession(sessionId: Array[Byte]): SSLSession =
    requireNonNull(sessionId)
    sessions.get(sessionId) match
      case Some(session) => session
      case None          => null

  def getIds(): Enumeration[Array[Byte]] =
    Collections.enumeration(asJavaCollection(sessions.keys))

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
  def getSession(host: String, port: Int): SSLSession =
    ???

  def putSession(session: SSLSession): Unit =
    ???

  def getSessionSize(): Int =
    sessions.size

  def getCachedSession(host: String, port: Int, sslParams: SSLParametersImpl): SSLSession =
    ???

  def putCachedSession(session: SSLSession): Unit =
    ???

/// Session Context for TLS 1.2 and earlier
///
/// This class provides an implementation for handling client sessions
/// in TLS versions up to 1.2, where session resumption is typically
/// managed through session IDs (host and port based).
class TLSv12ClientSessionContext extends ClientSessionContext:

  override def getCachedSession(
      host: String,
      port: Int,
      sslParams: SSLParametersImpl,
  ): SSLSession =
    ???

/// Session Context for TLS 1.3
///
/// Session resumption in TLS 1.3 is based on Pre-Shared Keys (PSK) established
/// during a full handshake. Therefore, the session management differs from TLS 1.2
/// and earlier versions. This class provides a specialized implementation for
/// handling TLS 1.3 client sessions.
class TLSv13ClientSessionContext extends ClientSessionContext:

  override def getCachedSession(
      host: String,
      port: Int,
      sslParams: SSLParametersImpl,
  ): SSLSession =
    ???
