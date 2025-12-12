/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.net.ssl

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ReadOnlyBufferException
import java.util.List as JList
import java.util.Objects.requireNonNull
import java.util.function.BiFunction
import javax.net.ssl.{SSLParameters, SSLContextSpi, SSLSession, SSLEngineResult, SSLEngine}
import javax.net.ssl.SSLException
import javax.net.ssl.SSLEngineResult.HandshakeStatus

import scala.scalanative.unsafe.{Ptr, Zone, toCString}

import snhttp.experimental.openssl.{ssl, bio}
import snhttp.experimental.openssl.ssl_internal.enumerations.OSSL_HANDSHAKE_STATE

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

  import EngineState.*

  val sslContextSession = ctxSpi.clientSessionContext
  val sslSocketFactory = ctxSpi.sslSocketFactory

  // Engine state
  @volatile protected[ssl] var state: EngineState = STATE_NEW

  /**
   * Client/Server mode
   *
   *   - None => not set yet
   *   - Some(true) => client mode
   *   - Some(false) => server mode
   */
  @volatile protected[ssl] var _useClientMode: Option[Boolean] = None

  // SSL Object
  @volatile protected[ssl] var maybeSSLPtr: Option[Ptr[ssl.SSL]] = None
  private def _sslptr: Ptr[ssl.SSL] =
    if (maybeSSLPtr.isEmpty) throw new IllegalStateException("SSL object is not initialized yet")
    maybeSSLPtr.get

  // Underlying socket BIO
  @volatile protected[ssl] var maybeSocket: Option[ClientSSLSocketImpl] = None
  private def _ssl_socket: ClientSSLSocketImpl =
    throwIfNoAttatchedBIO()
    maybeSocket.get

  // Active session
  @volatile protected[ssl] var maybeSession: SSLSession = null

  /**
   * Major methods
   */

  /**
   * Throws:
   *
   *   - SSLException - A problem was encountered while processing the data that caused the
   *     SSLEngine to abort. See the class description for more information on engine closure.
   *   - IndexOutOfBoundsException - if the preconditions on the offset and length parameters do not
   *     hold.
   *   - ReadOnlyBufferException - if the dst buffer is read-only.
   *   - IllegalArgumentException - if either srcs or dst is null, or if any element in the srcs
   *     subsequence specified is null.
   *   - IllegalStateException - if the client/server mode has not yet been set.
   */
  def wrap(
      srcs: Array[ByteBuffer],
      offset: Int,
      length: Int,
      dst: ByteBuffer,
  ): SSLEngineResult = {
    checkArrayBuffer(srcs, offset, length, "srcs")
    if (dst == null) throw new IllegalArgumentException("dst is null")
    if (dst.isReadOnly()) throw new ReadOnlyBufferException()

    // check engine state
    state match
      case STATE_NEW =>
        throw new IllegalStateException("SSLEngine: Client/Server mode has not yet been set")
      case STATE_MODE_SET =>
        beginHandshake()
      case STATE_CLOSED_OUTBOUND => // Outbound is closed, cannot write more data
        return SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), 0, 0)
      case STATE_CLOSED =>
        return SSLEngineResult(SSLEngineResult.Status.CLOSED, getHandshakeStatus(), 0, 0)
      case _ => // continue
        ()

    // check handshake status
    getHandshakeStatus() match
      case HandshakeStatus.NEED_UNWRAP =>
        return SSLEngineResult(SSLEngineResult.Status.OK, HandshakeStatus.NEED_UNWRAP, 0, 0)
      case _ => // continue to wrap
        ()

    // Perform wrap operation
    dst.remaining() match
      case 0 =>
        SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, getHandshakeStatus(), 0, 0)
      case _ =>
        ???

    ???
    // TODO
  }

  /**
   * Throws:
   *
   *   - SSLException - A problem was encountered while processing the data that caused the
   *     SSLEngine to abort. See the class description for more information on engine closure.
   *   - IndexOutOfBoundsException - if the preconditions on the offset and length parameters do not
   *     hold.
   *   - ReadOnlyBufferException - if the src buffer is read-only.
   *   - IllegalArgumentException - if either srcs or dst is null, or if any element in the srcs
   *     subsequence specified is null.
   *   - IllegalStateException - if the client/server mode has not yet been set.
   */
  def unwrap(
      src: ByteBuffer,
      dsts: Array[ByteBuffer],
      offset: Int,
      length: Int,
  ): SSLEngineResult = {
    if (src == null) throw new IllegalArgumentException("src is null")
    if (src.isReadOnly()) throw new ReadOnlyBufferException()
    checkArrayBuffer(dsts, offset, length, "dsts")

    ???
  }

  // Not really necessary but included for completeness
  def getDelegatedTask(): Runnable =
    throw new UnsupportedOperationException(
      "Hence getDelegatedTask isn't enforced for full features, it's not implemented yet.",
    )

  def closeInbound(): Unit = {
    state match
      case STATE_CLOSED_INBOUND | STATE_CLOSED => return ()
      case _                                   => ()

    if handshakeStarted
    then {
      state match
        case STATE_CLOSED_OUTBOUND => transitionTo(STATE_CLOSED)
        case _                     => transitionTo(STATE_CLOSED_INBOUND)
      freeIfDone()
    } else { // Clean up
      closeAllAndFreeResources()
    }
  }

  def isInboundDone(): Boolean =
    (
      state == STATE_CLOSED
        || state == STATE_CLOSED_INBOUND
        || this.shutdownReceived
    )
      && _ssl_socket.pendingReadableBytes == 0

  /**
   * Calling `ssl.SSL_shutdown` to close outbound with notices:
   *
   *   1. A close_notify shutdown alert message is sent/received
   *   2. Closes the write direction of the connection; the read direction is closed by the peer
   *   3. `SSL_shutdown()` does not affect an underlying network connection such as a TCP
   *      connection, which remains open.
   */
  def closeOutbound(): Unit = {
    if (state == STATE_CLOSED || state == STATE_CLOSED_OUTBOUND)
      return ()

    if handshakeStarted
    then {
      state match
        case STATE_CLOSED_INBOUND => transitionTo(STATE_CLOSED)
        case _                    => transitionTo(STATE_CLOSED_OUTBOUND)
      freeIfDone()
    } else { // Clean up
      closeAllAndFreeResources()
    }
  }

  def isOutboundDone(): Boolean =
    (
      state == STATE_CLOSED
        || state == STATE_CLOSED_OUTBOUND
        || this.shutdownSent
    )
      && _ssl_socket.pendingWrittenBytes == 0

  def getSession(): SSLSession =
    state match
      case _ if state.code < STATE_HANDSHAKE_COMPLETED.code => SSLNullSessionImpl()
      case STATE_CLOSED                                     => SSLNullSessionImpl()
      case _                                                => maybeSession

  override def getHandshakeSession(): SSLSession =
    state match
      case STATE_HANDSHAKE_STARTED => maybeSession
      case _                       => null

  /**
   * Implements Notes:
   *
   * Unlike the `SSLSocket#startHandshake()` method, this method does not block until handshaking is
   * completed.
   */
  def beginHandshake(): Unit = {
    state match
      case STATE_NEW =>
        throw new IllegalStateException("SSLEngine: Client/Server mode not set before handshake")
      case STATE_CLOSED_INBOUND | STATE_CLOSED_OUTBOUND | STATE_CLOSED =>
        throw new IllegalStateException("SSLEngine: Engine has already been closed")
      // handshake has been already started, just return
      case STATE_HANDSHAKE_STARTED | STATE_HANDSHAKE_COMPLETED | STATE_READY_HANDSHAKE_CUT_THROUGH |
          STATE_READY =>
        return ()
      case STATE_MODE_SET => // Go to next step
        ()

    transitionTo(STATE_HANDSHAKE_STARTED)
    var releaseResources = true

    try {
      // Prepare and init SSL Object with non-blocking BIO
      initSSL()

      // TODO:
      // For clients, offer to resume a previously cached session to avoid the
      // full TLS handshake.

      // Perform handshake
      doHandshake()

      // If we reach here, the handshake was triggered, no need to release resources
      releaseResources = false
    } catch {
      case e: IOException =>
        closeAllAndFreeResources()
        throw new SSLException("Failed to complete SSL handshake", e)
    } finally // release resources if handshake failed
      if (releaseResources) closeAllAndFreeResources()
  }

  /**
   * Getter and Setter methods for various SSL parameters
   *
   * Setter methods should only be called before the handshake tarted.
   */

  def setUseClientMode(mode: Boolean): Unit =
    throwIfHandshakeStarted()
    _useClientMode = Some(mode)
    transitionTo(STATE_MODE_SET)

  /**
   * Implementation Note:
   *
   * The JDK SunJSSE provider implementation returns false unless setUseClientMode(boolean) is used
   * to change the mode to true.
   */
  def getUseClientMode(): Boolean =
    _useClientMode match
      case Some(mode) => mode
      case None       => false // Default to false if not set yet

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

  def getHandshakeStatus(): HandshakeStatus = {
    // do quick state check
    state match
      case STATE_NEW | STATE_MODE_SET | STATE_READY_HANDSHAKE_CUT_THROUGH | STATE_READY |
          STATE_CLOSED_INBOUND | STATE_CLOSED_OUTBOUND | STATE_CLOSED =>
        return HandshakeStatus.NOT_HANDSHAKING
      case _ => ()

    // Query libssl SSL object state to determine current handshake status
    val _ret = ssl.SSL_do_handshake(_sslptr)
    val _err_code = ssl.SSL_get_error(_sslptr, _ret)

    _err_code match
      case ssl.SSL_ERROR.WANT_READ   => HandshakeStatus.NEED_UNWRAP
      case ssl.SSL_ERROR.WANT_WRITE  => HandshakeStatus.NEED_WRAP
      case ssl.SSL_ERROR.NONE        => HandshakeStatus.FINISHED
      case ssl.SSL_ERROR.ZERO_RETURN => HandshakeStatus.NOT_HANDSHAKING // ??? not sure
      case _                         => HandshakeStatus.NEED_TASK // ??? not sure
  }

  def setNeedClientAuth(need: Boolean): Unit =
    ???

  def getNeedClientAuth(): Boolean =
    ???

  def setWantClientAuth(want: Boolean): Unit =
    ???

  def getWantClientAuth(): Boolean =
    ???

  def setEnableSessionCreation(flag: Boolean): Unit =
    throwIfHandshakeStarted()
    ???

  def getEnableSessionCreation(): Boolean =
    ???

  override def getSSLParameters(): SSLParameters =
    ???

  override def setSSLParameters(params: SSLParameters): Unit =
    throwIfHandshakeStarted()
    ???

  override def getApplicationProtocol(): String =
    ???

  override def getHandshakeApplicationProtocol(): String =
    ???

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

  ///
  /// Private helper methods
  ///

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
      throw new Error("SSL_new returned null pointer, failed to create the SSL object")
    maybeSSLPtr = Some(ptr)

    // Set SNI Hostname
    if (host != null && host.nonEmpty)
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

    // Create SSLSocket and attach to SSL object
    val sslSocket = sslSocketFactory.createSocket(host, port)
    ssl.SSL_set_bio(ptr, sslSocket.ptr, sslSocket.ptr)
    maybeSocket = Some(sslSocket)
  }

  private def transitionTo(newState: EngineState): Unit =
    state = newState

  private def freeIfDone(): Unit =
    if (isInboundDone() && isOutboundDone())
      closeAllAndFreeResources()

  private def closeAllAndFreeResources(): Unit = {
    transitionTo(STATE_CLOSED)

    if maybeSSLPtr.isDefined
    then
      ssl.SSL_free(_sslptr)
      maybeSSLPtr = None
    else
      throw new IllegalStateException(
        "SSL object is already freed or was never initialized",
      )

    // SSLPtr will help to free the underlying BIOs, socket will be closed in its own close method
    if (maybeSocket.isDefined) {
      val socket = maybeSocket.get
      socket.close()
      maybeSocket = None
    }
  }

  private def doHandshake(): Unit =
    val _ret = ssl.SSL_connect(_sslptr)
    // in non-blocking mode, so we will get return immediately
    // here, we handle fatal error only
    if (_ret < 0)
      val _err = ssl.SSL_get_error(_sslptr, _ret)
      // TODO:
      // fatal error codes can be also retrieved from ERR_get_error library call as well
      // https://docs.openssl.org/master/man3/ERR_GET_LIB/
      throw new IOException(
        s"SSL_connect got fatal error with ret code: ${_ret} and error code: ${_err.name}(${_err.value})",
      )

  /**
   * State check helpers
   */

  private def handshakeStarted =
    state.code < STATE_HANDSHAKE_STARTED.code

  private def shutdownSent: Boolean =
    throwIfHandshakeNotStarted()
    ssl.SSL_get_shutdown(_sslptr) == ssl.SSL_SHUTDOWN.SENT.value

  private def shutdownReceived: Boolean =
    throwIfHandshakeNotStarted()
    ssl.SSL_get_shutdown(_sslptr) == ssl.SSL_SHUTDOWN.RECEIVED.value

  /**
   * Preconditions / contracts / validations
   */

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

  private def throwIfNoAttatchedBIO(): Unit =
    if (maybeSocket.isEmpty)
      throw new IllegalStateException(
        "Cannot get underlying BIO for SSL, SSL object not initialized properly or already freed",
      )

  private def checkArrayBuffer(
      bufs: Array[ByteBuffer],
      offset: Int,
      length: Int,
      dir: "srcs" | "dsts",
  ): Unit = {
    if (bufs == null)
      throw new IllegalArgumentException(s"${dir} is null")
    bufs.foreach(buf =>
      if (buf == null) throw new IllegalArgumentException(s"One of the ${dir} buffers is null"),
    )
    if (offset < 0 || length < 0 || length < (bufs.length - offset))
      throw new IndexOutOfBoundsException(
        s"Invalid offset ${offset} and length ${length} for ${dir} of length ${bufs.length}",
      )
  }

object ClientSSLEngineImpl:

  def apply(
      ctxSpi: SSLContextSpiImpl,
      host: String,
      port: Int,
  ): ClientSSLEngineImpl =
    new ClientSSLEngineImpl(ctxSpi, host, port)

end ClientSSLEngineImpl

// scalafmt: { maxColumn = 150, align.preset = most }
/** States for SSL engines. */
protected[ssl] enum EngineState(val code: Int):

  // The engine is constructed, but the initial handshake hasn't been started
  case STATE_NEW extends EngineState(0)
  // The client/server mode of the engine has been set.
  case STATE_MODE_SET extends EngineState(1)
  // The handshake has been started
  case STATE_HANDSHAKE_STARTED extends EngineState(2)
  // Listeners of the handshake have been notified of completion but
  // the handshake call hasn't returned.
  case STATE_HANDSHAKE_COMPLETED extends EngineState(3)
  // The handshake call returned but the listeners have not yet been notified.
  // This is expected behaviour in cut-through mode, where SSL_do_handshake
  // returns before the handshake is complete.
  // We can now start writing data to the socket.
  case STATE_READY_HANDSHAKE_CUT_THROUGH extends EngineState(4)
  // The handshake call has returned and the listeners have been notified.
  // Ready to begin writing data.
  case STATE_READY extends EngineState(5)
  // The inbound direction of the engine has been closed.
  case STATE_CLOSED_INBOUND extends EngineState(6)
  // The outbound direction of the engine has been closed.
  case STATE_CLOSED_OUTBOUND extends EngineState(7)
  // The engine has been closed.
  case STATE_CLOSED extends EngineState(8)

end EngineState
// scalafmt: { maxColumn = 100, align.preset = more }
