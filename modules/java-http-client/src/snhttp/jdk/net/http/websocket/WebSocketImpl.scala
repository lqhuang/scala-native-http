package snhttp.jdk.net.http.websocket

import java.net.URI
import java.net.http.{HttpClient, WebSocket}
import java.nio.ByteBuffer
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.List as JList
import java.util.Objects.requireNonNull

import scala.collection.mutable.HashMap

class WebSocketBuilderImpl(client: HttpClient) extends WebSocket.Builder:
  private var _timeout: Duration = Duration.ofSeconds(30)
  private var _headers: HashMap[String, String] = HashMap.empty

  def header(name: String, value: String): WebSocket.Builder =
    requireNonNull(name, "name cannot be null")
    requireNonNull(value, "value cannot be null")
    _headers(name) = value
    this

  def subprotocols(mostPreferred: String, lesserPreferred: String*): WebSocket.Builder =
    requireNonNull(mostPreferred, "mostPreferred cannot be null")
    this

  def connectTimeout(timeout: Duration): WebSocket.Builder =
    this._timeout = requireNonNull(timeout, "timeout cannot be null")
    this

  override def buildAsync(uri: URI, listener: WebSocket.Listener): CompletableFuture[WebSocket] =
    requireNonNull(uri, "uri cannot be null")
    requireNonNull(listener, "listener cannot be null")
    val future = new CompletableFuture[WebSocket]()
    // In a real implementation, this would establish a WebSocket connection
    future.completeExceptionally(new UnsupportedOperationException("WebSocket not yet implemented"))
    future

class WebSocketImpl extends WebSocket with AutoCloseable:
  def sendText(data: CharSequence, last: Boolean): CompletableFuture[WebSocket] = ???

  def sendText(message: String, last: Boolean): CompletableFuture[WebSocket] = ???

  def sendBinary(data: ByteBuffer, last: Boolean): CompletableFuture[WebSocket] = ???

  def sendPing(message: ByteBuffer): CompletableFuture[WebSocket] = ???

  def sendPong(message: ByteBuffer): CompletableFuture[WebSocket] = ???

  def sendClose(statusCode: Int, reason: String): CompletableFuture[WebSocket] = ???

  def request(n: Long): Unit = ???

  def getSubprotocol(): String = ???

  def isOutputClosed(): Boolean = ???

  def isInputClosed(): Boolean = ???

  def abort(): Unit = ???

  def close(): Unit = ???
