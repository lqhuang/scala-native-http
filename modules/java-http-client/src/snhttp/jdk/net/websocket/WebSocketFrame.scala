package snhttp.jdk.net.websocket

import java.nio.charset.StandardCharsets.UTF_8

object Opcodes {
  val CONTINUATION: Int = 0
  val TEXT: Int = 1
  val BINARY: Int = 2
  val CLOSE: Int = 8
  val PING: Int = 9
  val PONG: Int = 10
}
object CloseCodes {
  val NORMAL_CLOSURE: Int = 1000
  val GOING_AWAY: Int = 1001
  val UNSUPPORTED_DATA: Int = 1003

  val NO_STATUS_RCVD: Int = 1005
  val ABNORMAL_CLOSURE: Int = 1006

  val INVALID_PAYLOAD_DATA: Int = 1007
  val POLICY_VIOLATION: Int = 1008
  val MESSAGE_TOO_BIG: Int = 1009
  val UNSUPPORTED_EXTENSION: Int = 1010
  val INTERNAL_SERVER_ERROR: Int = 1011
  val TLS_HANDSHAKE_FAILURE: Int = 1015
}

trait AbstractWebSocketFrame:
  /**
   * Arbitrary "Application data", taking up the remainder of the frame after any "Extension data".
   * The length of the "Application data" is equal to the payload length minus the length of the
   * "Extension data".
   */
  def data: Seq[Byte]

  /**
   * Indicates that this is the final fragment in a message.
   */
  def fin: Boolean

  /**
   * Defines the interpretation of the "Payload data".
   */
  def opcode: Int

  final def length: Int = data.length.toInt

final case class Ping(data: Seq[Byte] = Seq.empty, fin: Boolean, opcode: Int = Opcodes.PING)
    extends AbstractWebSocketFrame

final case class Pong(data: Seq[Byte] = Seq.empty, fin: Boolean, opcode: Int = Opcodes.PONG)
    extends AbstractWebSocketFrame

final case class Close(
    data: Seq[Byte] = Seq.empty,
    fin: Boolean = true,
    opcode: Int = Opcodes.CLOSE,
) extends AbstractWebSocketFrame {
  def closeCode: Int =
    if (data.length > 0)
      (data(0) << 8 & 0xff00) | (data(1) & 0xff) // 16-bit unsigned
    else CloseCodes.NO_STATUS_RCVD

  def reason: String =
    if (data.length > 2)
      new String(data.drop(2).toArray, UTF_8)
    else ""
}
object Close {
  def apply(code: Int) = ???

  def apply(code: Int, reason: String) = ???
}

type ControlFrame = Ping | Pong | Close

final case class Text(
    data: Seq[Byte],
    fin: Boolean,
    opcode: Int = Opcodes.TEXT,
) extends AbstractWebSocketFrame
object Text {
  def apply(str: String, fin: Boolean): Text =
    Text(str.getBytes(UTF_8).toBuffer.toSeq, fin)
}

final case class Binary(data: Seq[Byte], fin: Boolean, opcode: Int = Opcodes.BINARY)
    extends AbstractWebSocketFrame

type DataFrame = Text | Binary

final case class Continuation(
    data: Seq[Byte],
    fin: Boolean,
    opcode: Int = Opcodes.CONTINUATION,
) extends AbstractWebSocketFrame

type WebSocketFrame = Continuation | DataFrame | ControlFrame
