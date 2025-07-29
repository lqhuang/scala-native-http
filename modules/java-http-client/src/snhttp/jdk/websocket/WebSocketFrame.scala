package snhttp.jdk.websocket

import java.nio.charset.StandardCharsets.UTF_8

import WebSocketOpcodes as Opcodes
import WebSocketCloseCodes as CloseCodes

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
