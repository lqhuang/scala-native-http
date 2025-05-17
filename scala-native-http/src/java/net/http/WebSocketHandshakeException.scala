package java.net.http;

import java.io.IOException;

/// @since 11
class WebSocketHandshakeException(
    @transient
    final private val response: HttpResponse,
) extends IOException {

  def this(response: HttpResponse) = this(response, null, null);

  def getResponse(): HttpResponse = response;

  override def initCause(cause: Throwable): Throwable = super.initCause(cause);

}

object WebSocketHandshakeException {
  final private val serialVersionUID: Long = 1L;
}
