package snhttp.jdk.websocket

/**
 * References:
 *
 *   1. [RFC 6455 - The WebSocket Protocol - 7.4. Status
 *      Codes](https://datatracker.ietf.org/doc/html/rfc6455#section-7.4)
 *   2. [WebSocket Close Code Number
 *      Registry](https://www.iana.org/assignments/websocket/websocket.xhtml#close-code-number)
 */
object WebSocketCloseCodes:
  val NORMAL_CLOSURE = 1000
  val NO_STATUS_RCVD = 1005

/**
 * Opcode: 4 bits. Defines the interpretation of the "Payload data".
 *
 * References:
 *
 *   1. [RFC 6455 - The WebSocket Protocol - 5.2. Base Framing Protocol
 *      Codes](https://datatracker.ietf.org/doc/html/rfc6455#section-5.2)
 *   2. [WebSocket Opcode
 *      Registry](https://www.iana.org/assignments/websocket/websocket.xhtml#opcode)
 */
object WebSocketOpcodes:
  val CONTINUATION = 0x0
  val TEXT = 0x1
  val BINARY = 0x2
  val CLOSE = 0x8
  val PING = 0x9
  val PONG = 0xa
