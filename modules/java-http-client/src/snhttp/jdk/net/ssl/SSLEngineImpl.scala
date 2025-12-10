/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.net.ssl

import java.io.IOException
import java.nio.ByteBuffer
import java.util.List as JList
import java.util.Objects.requireNonNull
import java.util.function.BiFunction
import javax.net.ssl.{SSLParameters, SSLContextSpi, SSLSession, SSLEngineResult, SSLEngine}

import scala.scalanative.unsafe.{Ptr, Zone, toCString}

import snhttp.experimental.openssl.ssl

/// An **Client** SSLEngine is created by calling `SSLContext.createSSLEngine(host, port)`.
///
/// Any configuration parameters should be set before making the first call to
/// `wrap()`, `unwrap()`, or `beginHandshake()`
///
/// Implementation Notes:
///
/// 1. SSLEngine will map to `SSL` struct related APIs in OpenSSL library.
/// 2. Inspired from Conscrypt's SSLEngine implementation
///    <https://github.com/google/conscrypt/blob/master/common/src/main/java/org/conscrypt/ConscryptEngine.java>
///
/// TODO:
///
/// Remove EngineState and leverage SSL_state() to get current state of SSL object.
class ClientSSLEngineImpl protected[ssl] (ctxSpi: SSLContextSpiImpl, host: String, port: Int)
    extends SSLEngine(host, port):

  val sslContextSession = ctxSpi.clientSessionContext
  val sslSocketFactory = ctxSpi.sslSocketFactory

  // SSL Object
  @volatile protected[ssl] var maybeSSLPtr: Option[Ptr[ssl.SSL]] = None
  // Active session
  @volatile protected[ssl] var maybeSession: SSLSession = null
  // Engine state
  @volatile protected[ssl] var state: EngineState = EngineState.STATE_NEW

  // Socket BIOs

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
        || this.shutdownReceived
    )
      && this.pendingReadableBytes == 0

  /**
   * Calling `ssl.SSL_shutdown` to close outbound with notices:
   *
   *   1. A close_notify shutdown alert message is sent/received
   *   2. Closes the write direction of the connection; the read direction is closed by the peer
   *   3. `SSL_shutdown()` does not affect an underlying network connection such as a TCP
   *      connection, which remains open.
   */
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
        || this.shutdownSent
    )
      && this.pendingWrittenBytes == 0

  // Implementation Notes:
  //
  //  Unlike SSLSocket.getSession()
  //

  def getSession(): SSLSession =
    state match
      case _ if state.value < EngineState.STATE_HANDSHAKE_COMPLETED.value => SSLNullSessionImpl()
      case EngineState.STATE_CLOSED                                       => SSLNullSessionImpl()
      case _                                                              => maybeSession

  override def getHandshakeSession(): SSLSession =
    state match
      case EngineState.STATE_HANDSHAKE_STARTED => maybeSession
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

    // Prepare and init SSL Object
    initSSL()

    // TODO:
    // For clients, offer to resume a previously cached session to avoid the
    // full TLS handshake.
    // if (getUseClientMode()) {
    //   val cachedClientSession = ???
    //   if (cachedClientSession != null) ctx.resumeFrom(cachedClientSession)
    // }

    try {
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

  /**
   * Getter and Setter methods for various SSL parameters
   *
   * Setter methods should only be called before the handshake tarted.
   */

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

  /**
   * Initialize SSL object for current SSLEngine and set necessary parameters.
   */
  private def initSSL(): Unit = {
    if (maybeSSLPtr.isDefined)
      throw new IllegalStateException(
        "SSL object has already been initialized, cannot initialize again",
      )

    val ptr = ssl.SSL_new(sslContextSession.ptr)
    if (ptr == null)
      throw new RuntimeException("SSL_new returned null pointer, failed to create the SSL object")
    maybeSSLPtr = Some(ptr)

    // Set SNI Hostname
    if host != null && host.nonEmpty
    then
      Zone {
        val hostPtr = toCString(host)
        val setHostRet = ssl.SSL_set1_host(ptr, hostPtr)
        if (setHostRet != 1)
          throw new RuntimeException(
            s"Failed to set hostname to ${host}, SSL_set1_host returned ${setHostRet}",
          )
        val setSNIRet = ssl.SSL_set_tlsext_host_name(ptr, hostPtr)
        if (setSNIRet != 1)
          throw new RuntimeException(
            s"Failed to set SNI hostname to ${host}, SSL_set_tlsext_host_name returned ${setSNIRet}",
          )
      }

    // Set the SSL to Client mode
    if (getUseClientMode()) ssl.SSL_set_connect_state(ptr)
  }

  private def handshakeStarted =
    state.value < EngineState.STATE_HANDSHAKE_STARTED.value

  private def transitionTo(newState: EngineState): Unit =
    state = newState

  private def throwIfHandshakeStarted(): Unit =
    if (handshakeStarted)
      throw new IllegalStateException(
        "Cannot change SSLEngine parameters after handshake has started",
      )

  private def throwIfHandshakeNotStarted(): Unit =
    if (maybeSSLPtr.isEmpty)
      throw new IllegalStateException(
        "Cannot get SSL object before handshake starts",
      )

  private def freeIfDone(): Unit =
    if (isInboundDone() && isOutboundDone())
      closeAllAndFreeResources()

  private def closeAllAndFreeResources(): Unit =
    transitionTo(EngineState.STATE_CLOSED)

    if maybeSSLPtr.isDefined
    then
      val ptr = maybeSSLPtr.get
      ssl.SSL_free(ptr)
      maybeSSLPtr = None
    else
      throw new IllegalStateException(
        "SSL object is already freed or was never initialized",
      )

    // if (ssl != null) {
    //     ssl.close();
    // }
    // if (networkBio != null) {
    //     networkBio.close();
    // }

  private def doHandshake(): Unit =
    val ptrConnRet = ssl.SSL_connect(maybeSSLPtr.get)

  private def pendingReadableBytes: Int =
    ???

  private def pendingWrittenBytes: Int =
    ???

  private def shutdownSent: Boolean =
    throwIfHandshakeNotStarted()
    ssl.SSL_get_shutdown(maybeSSLPtr.get) == ssl.SSL_SHUTDOWN.SENT.value

  private def shutdownReceived: Boolean =
    throwIfHandshakeNotStarted()
    ssl.SSL_get_shutdown(maybeSSLPtr.get) == ssl.SSL_SHUTDOWN.RECEIVED.value

object ClientSSLEngineImpl:

  def apply(
      ctxSpi: SSLContextSpiImpl,
      host: String,
      port: Int,
  ): ClientSSLEngineImpl =
    new ClientSSLEngineImpl(ctxSpi, host, port)

end ClientSSLEngineImpl

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
