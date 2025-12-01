/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.net.ssl

import java.util.Enumeration
import java.util.Objects.requireNonNull
import javax.net.ssl.{SSLSessionContext, SSLSession}
import java.util.{Collections, Collection}

import scala.collection.mutable.{LinkedHashMap, Map as MutableMap}
import scala.collection.JavaConverters.asJavaCollection

import snhttp.jdk.internal.PropertyUtils

case class SSLSessionContextImpl() extends SSLSessionContext:

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
