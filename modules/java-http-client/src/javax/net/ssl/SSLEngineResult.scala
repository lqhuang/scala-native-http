package javax.net.ssl

import java.lang._Enum

/// Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLEngineResult.html
class SSLEngineResult(
    private val status: SSLEngineResult.Status,
    private val handshakeStatus: SSLEngineResult.HandshakeStatus,
    private val _bytesConsumed: Int,
    private val _bytesProduced: Int,
    private val _sequenceNumber: Long,
):

  require(status != null && handshakeStatus != null)
  require(_bytesConsumed >= 0 && _bytesProduced >= 0)

  def this(
      status: SSLEngineResult.Status,
      handshakeStatus: SSLEngineResult.HandshakeStatus,
      bytesConsumed: Int,
      bytesProduced: Int,
  ) =
    this(status, handshakeStatus, bytesConsumed, bytesProduced, -1L)

  final def getStatus(): SSLEngineResult.Status =
    status

  final def getHandshakeStatus(): SSLEngineResult.HandshakeStatus =
    handshakeStatus

  final def bytesConsumed(): Int =
    _bytesConsumed

  final def bytesProduced(): Int =
    _bytesProduced

  final def sequenceNumber(): Long =
    _sequenceNumber

  override def toString(): String = {
    val padding =
      if _sequenceNumber == -1
      then ""
      else s" sequenceNumber = ${_sequenceNumber}"

    s"Status = ${status} HandshakeStatus = ${handshakeStatus} bytesConsumed = ${_bytesConsumed} bytesProduced = ${_bytesProduced}" + padding
  }

object SSLEngineResult:

  def apply(
      status: SSLEngineResult.Status,
      handshakeStatus: SSLEngineResult.HandshakeStatus,
      bytesConsumed: Int,
      bytesProduced: Int,
      sequenceNumber: Long,
  ): SSLEngineResult =
    new SSLEngineResult(status, handshakeStatus, bytesConsumed, bytesProduced, sequenceNumber)

  def apply(
      status: SSLEngineResult.Status,
      handshakeStatus: SSLEngineResult.HandshakeStatus,
      bytesConsumed: Int,
      bytesProduced: Int,
  ): SSLEngineResult =
    new SSLEngineResult(status, handshakeStatus, bytesConsumed, bytesProduced)

  // scalafmt: { maxColumn = 150, align.preset = most }
  sealed class Status private (name: String, ordinal: Int) extends _Enum[Status](name, ordinal)
  object Status:
    final val BUFFER_UNDERFLOW = new Status("BUFFER_UNDERFLOW", 0)
    final val BUFFER_OVERFLOW  = new Status("BUFFER_OVERFLOW", 1)
    final val OK               = new Status("OK", 2)
    final val CLOSED           = new Status("CLOSED", 3)

    def values(): Array[Status] = Array(BUFFER_UNDERFLOW, BUFFER_OVERFLOW, OK, CLOSED)

    def valueOf(name: String): Status =
      name match
        case "BUFFER_UNDERFLOW" => BUFFER_UNDERFLOW
        case "BUFFER_OVERFLOW"  => BUFFER_OVERFLOW
        case "OK"               => OK
        case "CLOSED"           => CLOSED
        case _                  => throw new IllegalArgumentException(s"No enum const Status.${name}")
  end Status

  sealed class HandshakeStatus private (name: String, ordinal: Int) extends _Enum[HandshakeStatus](name, ordinal)
  object HandshakeStatus:
    final val NOT_HANDSHAKING   = new HandshakeStatus("NOT_HANDSHAKING", 0)
    final val FINISHED          = new HandshakeStatus("FINISHED", 1)
    final val NEED_TASK         = new HandshakeStatus("NEED_TASK", 2)
    final val NEED_WRAP         = new HandshakeStatus("NEED_WRAP", 3)
    final val NEED_UNWRAP       = new HandshakeStatus("NEED_UNWRAP", 4)
    final val NEED_UNWRAP_AGAIN = new HandshakeStatus("NEED_UNWRAP_AGAIN", 5)

    def values(): Array[HandshakeStatus] = Array(
      NOT_HANDSHAKING,
      FINISHED,
      NEED_TASK,
      NEED_WRAP,
      NEED_UNWRAP,
      NEED_UNWRAP_AGAIN,
    )

    def valueOf(name: String): HandshakeStatus =
      name match
        case "NOT_HANDSHAKING"   => NOT_HANDSHAKING
        case "FINISHED"          => FINISHED
        case "NEED_TASK"         => NEED_TASK
        case "NEED_WRAP"         => NEED_WRAP
        case "NEED_UNWRAP"       => NEED_UNWRAP
        case "NEED_UNWRAP_AGAIN" => NEED_UNWRAP_AGAIN
        case _                   => throw new IllegalArgumentException(s"No enum const HandshakeStatus.${name}")
  end HandshakeStatus
  // scalafmt: { maxColumn = 100, align.preset = more }

end SSLEngineResult
