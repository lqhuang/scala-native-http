package javax.net.ssl

/// Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLEngineResult.html
class SSLEngineResult(
    private val status: SSLEngineResult.Status,
    private val handshakeStatus: SSLEngineResult.HandshakeStatus,
    private val _bytesConsumed: Int,
    private val _bytesProduced: Int,
    private val _sequenceNumber: Long,
) {
  require(status != null && handshakeStatus != null)
  require(_bytesConsumed >= 0 && _bytesProduced >= 0)

  def this(
      status: SSLEngineResult.Status,
      handshakeStatus: SSLEngineResult.HandshakeStatus,
      bytesConsumed: Int,
      bytesProduced: Int,
  ) =
    this(status, handshakeStatus, bytesConsumed, bytesProduced, -1L)

  final def getStatus(): SSLEngineResult.Status = status

  final def getHandshakeStatus(): SSLEngineResult.HandshakeStatus = handshakeStatus

  final def bytesConsumed(): Int = _bytesConsumed

  final def bytesProduced(): Int = _bytesProduced

  final def sequenceNumber(): Long = _sequenceNumber

  override def toString(): String = {
    val padding =
      if _sequenceNumber == -1
      then ""
      else s" sequenceNumber = ${_sequenceNumber}"

    s"Status = ${status} HandshakeStatus = ${handshakeStatus} bytesConsumed = ${_bytesConsumed} bytesProduced = ${_bytesProduced}" + padding
  }

}

object SSLEngineResult {

  sealed abstract class Status
  object Status {
    case object BUFFER_UNDERFLOW extends Status
    case object BUFFER_OVERFLOW extends Status
    case object OK extends Status
    case object CLOSED extends Status
  }

  sealed abstract class HandshakeStatus
  object HandshakeStatus {
    case object NOT_HANDSHAKING extends HandshakeStatus
    case object FINISHED extends HandshakeStatus
    case object NEED_TASK extends HandshakeStatus
    case object NEED_WRAP extends HandshakeStatus
    case object NEED_UNWRAP extends HandshakeStatus
    case object NEED_UNWRAP_AGAIN extends HandshakeStatus
  }

}
