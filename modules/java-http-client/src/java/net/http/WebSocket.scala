package java.net.http

import java.net.URI
import java.nio.ByteBuffer
import java.time.Duration
import java.util.concurrent.{CompletableFuture, CompletionStage}

trait WebSocket {
  def sendText(message: String, last: Boolean): CompletableFuture[WebSocket]

  def sendBinary(data: ByteBuffer, last: Boolean): CompletableFuture[WebSocket]

  def sendPing(message: ByteBuffer): CompletableFuture[WebSocket]

  def sendPong(message: ByteBuffer): CompletableFuture[WebSocket]

  def sendClose(statusCode: Int, reason: String): CompletableFuture[WebSocket]

  def request(n: Long): Unit

  def getSubprotocol(): String

  def isOutputClosed(): Boolean

  def isInputClosed(): Boolean

  def abort(): Unit
}

object WebSocket {

  /** Message status code for normal closure */
  val NORMAL_CLOSURE = 1000

  trait Builder {
    def header(name: String, value: String): Builder

    def connectTimeout(timeout: Duration): Builder

    def subprotocols(mostPreferred: String, lesserPreferred: String*): Builder

    def buildAsync(uri: URI, listener: Listener): CompletableFuture[WebSocket]
  }

  trait Listener {
    def onOpen(webSocket: WebSocket): Unit

    def onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage[?]

    def onBinary(webSocket: WebSocket, data: ByteBuffer, last: Boolean): CompletionStage[?]

    def onPing(webSocket: WebSocket, message: ByteBuffer): CompletionStage[?]

    def onPong(webSocket: WebSocket, message: ByteBuffer): CompletionStage[?]

    def onClose(webSocket: WebSocket, statusCode: Int, reason: String): CompletionStage[?]

    def onError(webSocket: WebSocket, error: Throwable): Unit
  }

}
