/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.net.ssl

import java.io.IOException
import java.nio.ByteBuffer
import java.util.List as JList
import java.util.Objects.requireNonNull
import java.util.function.BiFunction
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ssl.{SSLParameters, SSLContextSpi, SSLSession, SSLEngineResult, SSLEngine}

import snhttp.experimental.openssl.libssl
import snhttp.jdk.internal.tls.OpenSSLCtx

/// An SSLEngine is created by calling `SSLParams.createSSLEngine()`.
///
/// Any configuration parameters should be set before making the first call to
/// `wrap()`, `unwrap()`, or `beginHandshake()`
///
/// Implementation Notes:
///
/// SSLEngine will map to `SSL` struct related APIs in OpenSSL library.
class SSLEngineImpl protected[ssl] (ctxSpi: SSLContextSpiImpl, host: String, port: Int)
    extends SSLEngine(host, port):

  val sslCtx: OpenSSLCtx = ???
  val sslContextSession = ctxSpi.clientSessionContext
  val sslSocketFactory = ctxSpi.sslSocketFactory

  @volatile var state: EngineState = EngineState.STATE_NEW

  // Active session
  @volatile var session: SSLSession = null

  private def handshakeStarted =
    state.value < EngineState.STATE_HANDSHAKE_STARTED.value

  private def transitionTo(newState: EngineState): Unit =
    state = newState

  /**
   * Major methods
   */

  def wrap(
      srcs: Array[ByteBuffer],
      offset: Int,
      length: Int,
      dst: ByteBuffer,
  ): SSLEngineResult =
    // handshaked.compareAndExchange(false, true)
    ???

  def unwrap(
      src: ByteBuffer,
      dsts: Array[ByteBuffer],
      offset: Int,
      length: Int,
  ): SSLEngineResult =
    // handshaked.compareAndExchange(false, true)
    ???

  // Not really necessary but included for completeness
  def getDelegatedTask(): Runnable =
    throw new UnsupportedOperationException(
      "Hence getDelegatedTask isn't enforced for full features, it's not implemented yet.",
    )

  def closeInbound(): Unit =
    if (
      state == EngineState.STATE_NEW ||
      state == EngineState.STATE_MODE_SET
    )
      return

    if handshakeStarted
    then {
      state match
        case EngineState.STATE_CLOSED_OUTBOUND => transitionTo(EngineState.STATE_CLOSED)
        case _                                 => transitionTo(EngineState.STATE_CLOSED_INBOUND)
      freeIfDone()
    } else { // Clean up
      closeAllAndFreeResources()
    }

  def isInboundDone(): Boolean =
    (
      state == EngineState.STATE_CLOSED
        || state == EngineState.STATE_CLOSED_INBOUND
        || sslCtx.shutdownReceived
    )
      && sslCtx.pendingReadableBytes == 0

  def closeOutbound(): Unit =
    if (state == EngineState.STATE_CLOSED || state == EngineState.STATE_CLOSED_OUTBOUND)
      return

    if handshakeStarted
    then {
      state match
        case EngineState.STATE_CLOSED_INBOUND => transitionTo(EngineState.STATE_CLOSED)
        case _                                => transitionTo(EngineState.STATE_CLOSED_OUTBOUND)
      freeIfDone()
    } else { // Clean up
      closeAllAndFreeResources()
    }

  def isOutboundDone(): Boolean =
    (
      state == EngineState.STATE_CLOSED
        || state == EngineState.STATE_CLOSED_OUTBOUND
        || sslCtx.shutdownSent
    )
      && sslCtx.pendingWrittenBytes == 0

  def getSupportedCipherSuites(): Array[String] =
    SSLParametersImpl.getSupportedCipherSuites()

  def getEnabledCipherSuites(): Array[String] =
    ???
    // sslParams.getEnabledCipherSuites()

  def setEnabledCipherSuites(suites: Array[String]): Unit =
    throwIfHandshakeStarted()
    requireNonNull(suites)
    if (suites.filter(each => each == null || each.isEmpty()).length > 0)
      throw IllegalArgumentException()
    ???
    // sslParams.setEnabledCipherSuites(suites)

  def getSupportedProtocols(): Array[String] =
    SSLParametersImpl.getSupportedProtocols()

  def getEnabledProtocols(): Array[String] =
    ???
    // sslParams.getEnabledProtocols()

  def setEnabledProtocols(protocols: Array[String]): Unit =
    throwIfHandshakeStarted()
    ???
    // sslParams.setEnabledProtocols(protocols)

  // Implementation Notes:
  //
  //  Unlike SSLSocket.getSession()
  //

  def getSession(): SSLSession =
    state match
      case _ if state.value < EngineState.STATE_HANDSHAKE_COMPLETED.value => SSLNullSessionImpl()
      case EngineState.STATE_CLOSED                                       => SSLNullSessionImpl()
      case _                                                              => session

  override def getHandshakeSession(): SSLSession =
    state match
      case EngineState.STATE_HANDSHAKE_STARTED => session
      case _                                   => null

  def beginHandshake(): Unit = {
    state match
      case EngineState.STATE_NEW =>
        throw new IllegalStateException(
          "SSLEngine: Client/Server mode not set before handshake",
        )
      case (
            EngineState.STATE_CLOSED_INBOUND | EngineState.STATE_CLOSED_OUTBOUND |
            EngineState.STATE_CLOSED
          ) =>
        throw new IllegalStateException(
          "SSLEngine: Engine has already been closed",
        )
      // handshake has been already started, just return
      case (
            EngineState.STATE_HANDSHAKE_STARTED | EngineState.STATE_HANDSHAKE_COMPLETED |
            EngineState.STATE_READY_HANDSHAKE_CUT_THROUGH | EngineState.STATE_READY
          ) =>
        return
      // Go to next step
      case EngineState.STATE_MODE_SET => ()

    transitionTo(EngineState.STATE_HANDSHAKE_STARTED)
    var releaseResources = true
    try {
      // Prepare and init SSL Session
      session = SSLSessionImpl(sslContextSession)

      // For clients, offer to resume a previously cached session to avoid the
      // full TLS handshake.
      if (getUseClientMode()) {
        val cachedClientSession = ???
        if (cachedClientSession != null) sslCtx.resumeFrom(cachedClientSession)
      }

      doHandshake()
      // If we reach here, the handshake was successful, do not release resources
      releaseResources = false
    } catch {
      case e: IOException =>
        closeAllAndFreeResources()
        throw e
    } finally // release resources if handshake failed
      if (releaseResources) closeAllAndFreeResources()
  }

  def getHandshakeStatus(): SSLEngineResult.HandshakeStatus =
    ???

  def setUseClientMode(mode: Boolean): Unit =
    throwIfHandshakeStarted()
    // FIXME: Don't forget to set Client mode
    // sslParams.setUseClientMode(mode)
    transitionTo(EngineState.STATE_MODE_SET)

  def getUseClientMode(): Boolean =
    ???
    // sslParams.getUseClientMode()

  def setNeedClientAuth(need: Boolean): Unit =
    ???
    // sslParams.setNeedClientAuth(need)

  def getNeedClientAuth(): Boolean =
    ???
    // sslParams.getNeedClientAuth()

  def setWantClientAuth(want: Boolean): Unit =
    ???
    // sslParams.setWantClientAuth(want)

  def getWantClientAuth(): Boolean =
    ???
    // sslParams.getWantClientAuth()

  def setEnableSessionCreation(flag: Boolean): Unit =
    throwIfHandshakeStarted()

  def getEnableSessionCreation(): Boolean =
    ???
    // sslParams.getEnableSessionCreation()

  override def getSSLParameters(): SSLParameters =
    ???
    // sslParams.getSSLParameters()

  override def setSSLParameters(params: SSLParameters): Unit =
    throwIfHandshakeStarted()
    ???
    // sslParams.setSSLParameters(params)

  override def getApplicationProtocol(): String =
    ???
    // sslParams.getApplicationProtocol()

  override def getHandshakeApplicationProtocol(): String =
    ???
    // sslParams.getHandshakeApplicationProtocol()

  override def setHandshakeApplicationProtocolSelector(
      selector: BiFunction[SSLEngine, JList[String], String],
  ): Unit =
    throwIfHandshakeStarted()
    throw new NotImplementedError(
      "Not enforcedly required since JDK 9, not implemented yet",
    )

  override def getHandshakeApplicationProtocolSelector()
      : BiFunction[SSLEngine, JList[String], String] =
    throw new NotImplementedError(
      "Not enforcedly required since JDK 9, not implemented yet",
    )

  /**
   * Private helper methods
   */
  private def throwIfHandshakeStarted(): Unit =
    if (handshakeStarted)
      throw new IllegalStateException(
        "Cannot change SSLEngine parameters after handshake has started",
      )

  private def freeIfDone(): Unit =
    if (isInboundDone() && isOutboundDone())
      closeAllAndFreeResources()

  private def closeAllAndFreeResources(): Unit =
    transitionTo(EngineState.STATE_CLOSED)
    // if (ssl != null) {
    //     ssl.close();
    // }
    // if (networkBio != null) {
    //     networkBio.close();
    // }

  private def doHandshake(): Unit =
    ???

  // private def getClientSessionContext(): SSLSessionContext =

object SSLEngineImpl:

  def apply(ctxSpi: SSLContextSpiImpl): SSLEngineImpl =
    throw new NotImplementedError("Not supported and no plan to support currently")

  def apply(
      ctxSpi: SSLContextSpiImpl,
      host: String,
      port: Int,
  ): SSLEngineImpl =
    new SSLEngineImpl(ctxSpi, host, port)

end SSLEngineImpl

/**
 * States for SSL engines.
 */
enum EngineState(code: Int):
  val value = code

  /**
   * The engine is constructed, but the initial handshake hasn't been started
   */
  case STATE_NEW extends EngineState(0)

  /**
   * The client/server mode of the engine has been set.
   */
  case STATE_MODE_SET extends EngineState(1)

  /**
   * The handshake has been started
   */
  case STATE_HANDSHAKE_STARTED extends EngineState(2)

  /**
   * Listeners of the handshake have been notified of completion but the handshake call hasn't
   * returned.
   */
  case STATE_HANDSHAKE_COMPLETED extends EngineState(3)

  /**
   * The handshake call returned but the listeners have not yet been notified. This is expected
   * behaviour in cut-through mode, where SSL_do_handshake returns before the handshake is complete.
   * We can now start writing data to the socket.
   */
  case STATE_READY_HANDSHAKE_CUT_THROUGH extends EngineState(4)

  /**
   * The handshake call has returned and the listeners have been notified. Ready to begin writing
   * data.
   */
  case STATE_READY extends EngineState(5)

  /**
   * The inbound direction of the engine has been closed.
   */
  case STATE_CLOSED_INBOUND extends EngineState(6)

  /**
   * The outbound direction of the engine has been closed.
   */
  case STATE_CLOSED_OUTBOUND extends EngineState(7)

  /**
   * The engine has been closed.
   */
  case STATE_CLOSED extends EngineState(8)
