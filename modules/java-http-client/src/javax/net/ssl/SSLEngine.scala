package javax.net.ssl

import java.nio.ByteBuffer
import java.util.List as JList
import java.util.function.BiFunction

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLEngine.html
abstract class SSLEngine(host: String, port: Int):

  final def getPeerHost(): String = host

  final def getPeerPort(): Int = port

  final def wrap(src: ByteBuffer, dst: ByteBuffer): SSLEngineResult =
    wrap(Array(src), 0, 1, dst)

  final def wrap(srcs: Array[ByteBuffer], dst: ByteBuffer): SSLEngineResult =
    wrap(srcs, 0, srcs.length, dst)

  def wrap(
      srcs: Array[ByteBuffer],
      offset: Int,
      length: Int,
      dst: ByteBuffer,
  ): SSLEngineResult

  final def unwrap(src: ByteBuffer, dst: ByteBuffer): SSLEngineResult =
    unwrap(src, Array(dst), 0, 1)

  final def unwrap(src: ByteBuffer, dsts: Array[ByteBuffer]): SSLEngineResult =
    unwrap(src, dsts, 0, dsts.length)

  def unwrap(
      src: ByteBuffer,
      dsts: Array[ByteBuffer],
      offset: Int,
      length: Int,
  ): SSLEngineResult

  def getDelegatedTask(): Runnable

  def closeInbound(): Unit

  def isInboundDone(): Boolean

  def closeOutbound(): Unit

  def isOutboundDone(): Boolean

  def getSupportedCipherSuites(): Array[String]

  def getEnabledCipherSuites(): Array[String]

  def setEnabledCipherSuites(suites: Array[String]): Unit

  def getSupportedProtocols(): Array[String]

  def getEnabledProtocols(): Array[String]

  def setEnabledProtocols(protocols: Array[String]): Unit

  def getSession(): SSLSession

  def getHandshakeSession(): SSLSession

  def beginHandshake(): Unit

  def getHandshakeStatus(): SSLEngineResult.HandshakeStatus

  def setUseClientMode(mode: Boolean): Unit

  def getUseClientMode(): Boolean

  def setNeedClientAuth(need: Boolean): Unit

  def getNeedClientAuth(): Boolean

  def setWantClientAuth(want: Boolean): Unit

  def getWantClientAuth(): Boolean

  def setEnableSessionCreation(flag: Boolean): Unit

  def getEnableSessionCreation(): Boolean

  def getSSLParameters(): SSLParameters

  def setSSLParameters(params: SSLParameters): Unit

  def getApplicationProtocol(): String

  def getHandshakeApplicationProtocol(): String

  def setHandshakeApplicationProtocolSelector(
      selector: BiFunction[SSLEngine, JList[String], String],
  ): Unit

  def getHandshakeApplicationProtocolSelector(): BiFunction[SSLEngine, JList[String], String]
