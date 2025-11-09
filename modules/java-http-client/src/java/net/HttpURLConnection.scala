package java.net

import java.io.{IOException, InputStream}

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/HttpURLConnection.html
abstract class HttpURLConnection(url: URL) extends URLConnection(url) {

  def setAuthenticator(auth: Authenticator): Unit

  // override def getHeaderFieldKey(x: Int): String

  def setFixedLengthStreamingMode(contentLength: Int): Unit

  def setFixedLengthStreamingMode(contentLength: Long): Unit

  def setChunkedStreamingMode(chunkLength: Int): Unit

  // override def getHeaderField(n: Int): String

  def setInstanceFollowRedirects(followRedirects: Boolean): Unit

  def getInstanceFollowRedirects(): Boolean

  def setRequestMethod(method: String): Unit

  def getRequestMethod(): String

  def getResponseCode(): Int

  def getResponseMessage(): String

  def disconnect(): Unit

  def usingProxy(): Boolean

  def getErrorStream(): InputStream
}

object HttpURLConnection {
  // Magic numbers from
  // https://docs.oracle.com/en/java/javase/25/docs/api/constant-values.html#java.net
  final val HTTP_OK: Int = 200
  final val HTTP_CREATED: Int = 201
  final val HTTP_ACCEPTED: Int = 202
  final val HTTP_NOT_AUTHORITATIVE: Int = 203
  final val HTTP_NO_CONTENT: Int = 204
  final val HTTP_RESET: Int = 205
  final val HTTP_PARTIAL: Int = 206

  final val HTTP_MULT_CHOICE: Int = 300
  final val HTTP_MOVED_PERM: Int = 301
  final val HTTP_MOVED_TEMP: Int = 302
  final val HTTP_SEE_OTHER: Int = 303
  final val HTTP_NOT_MODIFIED: Int = 304
  final val HTTP_USE_PROXY: Int = 305

  final val HTTP_BAD_REQUEST: Int = 400
  final val HTTP_UNAUTHORIZED: Int = 401
  final val HTTP_PAYMENT_REQUIRED: Int = 402
  final val HTTP_FORBIDDEN: Int = 403
  final val HTTP_NOT_FOUND: Int = 404
  final val HTTP_BAD_METHOD: Int = 405
  final val HTTP_NOT_ACCEPTABLE: Int = 406
  final val HTTP_PROXY_AUTH: Int = 407
  final val HTTP_CLIENT_TIMEOUT: Int = 408
  final val HTTP_CONFLICT: Int = 409
  final val HTTP_GONE: Int = 410
  final val HTTP_LENGTH_REQUIRED: Int = 411
  final val HTTP_PRECON_FAILED: Int = 412
  final val HTTP_ENTITY_TOO_LARGE: Int = 413
  final val HTTP_REQ_TOO_LONG: Int = 414
  final val HTTP_UNSUPPORTED_TYPE: Int = 415

  final val HTTP_SERVER_ERROR: Int = 500
  final val HTTP_INTERNAL_ERROR: Int = 500
  final val HTTP_NOT_IMPLEMENTED: Int = 501
  final val HTTP_BAD_GATEWAY: Int = 502
  final val HTTP_UNAVAILABLE: Int = 503
  final val HTTP_GATEWAY_TIMEOUT: Int = 504
  final val HTTP_VERSION: Int = 505

  @volatile var followRedirects: Boolean = true

  def setFollowRedirects(set: Boolean): Unit =
    followRedirects = set

  def getFollowRedirects(): Boolean = followRedirects
}
