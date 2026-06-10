package snhttp.jdk.net.http.internal

import java.net.http.{HttpRequest, HttpHeaders, HttpResponse}
import java.net.http.HttpClient.{Version, Redirect}
import java.net.http.HttpResponse.{BodyHandler, BodySubscribers, ResponseInfo, BodySubscriber}
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.{Map as JMap, List as JList, Optional}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.SubmissionPublisher
import java.util.concurrent.locks.ReentrantLock

import scala.jdk.javaapi.CollectionConverters.asScala
import scala.scalanative.unsafe.{
  Ptr,
  Zone,
  toCString,
  fromCString,
  alloc,
  CSize,
  CStruct3,
  CFuncPtr4,
  CQuote,
  UnsafeRichLong,
  UnsafeRichInt,
  Tag,
}
import scala.scalanative.unsigned.{UnsignedRichInt, UnsignedRichLong}
import scala.scalanative.libc.stddef.NULL

import _root_.snhttp.experimental.curl.curl.{
  CurlErrCodeException,
  CurlException,
  CurlFollow,
  CurlUseSSL,
  CurlEasy,
  CurlOption,
  CurlHttpVersion,
  CurlMsgCode,
  CurlMultiErrCode,
  CurlMsg,
  CurlErrCode,
  CurlWriteCallback,
  CurlSlist,
  CurlWriteFuncRet,
}
import _root_.snhttp.experimental.curl.curl.CurlMultiCode.RichCurlMultiCode
import _root_.snhttp.jdk.net.http.{HttpClientImpl, HttpResponseImpl, ResponseInfoImpl}
import _root_.snhttp.jdk.net.http.internal.PropertyUtils
import _root_.snhttp.jdk.net.ssl.SSLContextImpl

type CurlRecvBuffer = CStruct3[
  AtomicBoolean, // flag for resp body received
  Function0[Unit], // resp body received callback
  SubmissionPublisher[JList[ByteBuffer]], // publisher for response body chunks
]

given Tag[CurlRecvBuffer] = Tag.materializeCStruct3Tag[
  AtomicBoolean,
  Function0[Unit],
  SubmissionPublisher[JList[ByteBuffer]],
]

extension (inline struct: CurlRecvBuffer)
  inline def flag: AtomicBoolean = struct._1
  inline def callback: Function0[Unit] = struct._2
  inline def publisher: SubmissionPublisher[JList[ByteBuffer]] = struct._3

/**
 * Represents a (virtual) connection to a web server, majorly based on libcurl's easy handle.
 *
 *   - For HTTP/1.1, multiple connections may be created
 *   - For HTTP/2, a connection corresponds to a multiplexed connection.
 */
private[http] final class HttpConnection[T](
    request: HttpRequest,
    responseBodyHandler: BodyHandler[T],
    client: HttpClientImpl,
) extends AutoCloseable:

  given zone: Zone = Zone.open()

  private[snhttp] val easy = CurlEasy()

  @volatile
  private var errMsg: Optional[CurlMsg] = Optional.empty()
  private val errMsgLock = new ReentrantLock()
  private val closed = new AtomicBoolean(false)

  /**
   * When `CurlSlist`(alias `curl_slist`) option is passed to `curl_easy_setopt`, libcurl does not
   * copy the entire list so you **must** keep it around until you no longer use this _handle_ for a
   * transfer before you call `curl_slist_free_all` on the list.
   */
  private var slist: Optional[CurlSlist] = Optional.empty()

  private val respBodyReceived = new AtomicBoolean(false)
  private val respBodyPublisher = new SubmissionPublisher[JList[ByteBuffer]]()

  private var maybeRespInfo: Optional[ResponseInfoImpl] = Optional.empty()
  private var maybeRespBodySubscriber: Optional[BodySubscriber[T]] = Optional.empty()

  /**
   * Callback function for writing response body data.
   *
   * Implementation notes:
   *
   * The main purpose of this function is to adapt the curl write data to our BodySubscriber.
   *
   * Refs:
   *
   *   1. [CURLOPT_WRITEFUNCTION](https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html)
   *   2. [libcurl example - getinmemory.c](https://curl.se/libcurl/c/getinmemory.html)
   */
  val writeDataCallback: CurlWriteCallback = CurlWriteCallback.fromScalaFunction {
    (payload: Ptr[Byte], nmemb: CSize, size: CSize, outstream: Ptr[?]) =>
      val userdata = outstream.asInstanceOf[Ptr[CurlRecvBuffer]]

      if (!(!userdata).flag.compareAndExchange(false, true))
        (!userdata).writeNotify.apply()

      // it's safe to cast UInt ot Int here
      // Curl guarantees that MAX_WRITE_SIZE won't exceed Int.MaxValue
      val ssize = size.toInt
      val bb = ByteBuffer.allocate(ssize)
      for i <- 0 until ssize do bb.put(!(payload + i))
      bb.flip()

      val offered = (!userdata).publisher.submit(JList.of(bb))
      size
  }

  val writeBufferSize = PropertyUtils.RECEIVE_BUFFER_SIZE // won't change after init

  val writeData = alloc[CurlRecvBuffer]()
  if (writeData == NULL)
    throw new CurlException("Failed to allocate memory for CurlData")
  (!writeData)._1 = respBodyReceived
  (!writeData)._2 = firstRespCallback
  (!writeData)._3 = respBodyPublisher

  init()

  def assignCurlErrMsg(msg: CurlMsg): Unit = {
    if (errMsg.isPresent() || closed.get())
      throw new IllegalStateException("CurlMsg has already been assigned/done")

    val lock = errMsgLock.lock()
    try errMsg = Optional.of(msg)
    finally errMsgLock.unlock()
  }

  def fetchResponse(): HttpResponse[T] = {
    blockUntilPerformDone()
    handleError()
    assert(respBodyReceived.get() == true)

    new HttpResponseImpl(
      request,
      maybeRespInfo.orElseThrow(() =>
        new IllegalStateException(s"${maybeRespInfo} is not initialized"),
      ),
      maybeRespBodySubscriber
        .orElseThrow(() =>
          new IllegalStateException(s"${maybeRespBodySubscriber} is not initialized"),
        )
        .getBody(),
    )
  }

  def close(): Unit =
    if (!closed.compareAndExchange(false, true)) {
      client.connections.remove(easy): Unit
      respBodyPublisher.close()
      easy.cleanup()
      slist.map(_.freeAll()): Unit
      zone.close()
    }

  //
  // Private methods
  //

  /**
   * Setup options for this connection based on the `request` and `client` config.
   */
  private def init(): Unit = {
    easy.setCStringOption(CurlOption.URL, toCString(request.uri().toString()))

    val httpVersion = request.version()
    if (httpVersion.isPresent()) {
      val h3 = Version.valueOf("HTTP_3") // FIXME: avoid compile error
      val version = httpVersion.get() match
        case Version.HTTP_1_1 => CurlHttpVersion.VERSION_1_1
        case Version.HTTP_2   => CurlHttpVersion.VERSION_2TLS
        case h3               => CurlHttpVersion.VERSION_3
      easy.setCLongOption(CurlOption.HTTP_VERSION, version.value)
    }

    // default to 30 seconds
    val timeoutMs = request.timeout().map(_.toMillis()).orElse(30 * 1000L)
    easy.setCLongOption(CurlOption.TIMEOUT_MS, timeoutMs.toSize)

    val connectTimeoutMs = client.builder._connectTimeout.map(_.toMillis).orElse(3 * 1000L)
    easy.setCLongOption(CurlOption.CONNECTTIMEOUT_MS, connectTimeoutMs.toSize)

    val followRedirects = client.builder._redirect match
      case Redirect.NEVER  => CurlFollow.DISABLED
      case Redirect.ALWAYS => CurlFollow.ALL
      case Redirect.NORMAL => CurlFollow.OBEYCODE
    easy.setCLongOption(CurlOption.FOLLOWLOCATION, followRedirects.value)

    /**
     * ref: https://curl.se/libcurl/c/curl_slist_append.html
     *
     * Notes from upstream libcurl documentation:
     *
     *   1. The existing list should be passed as the first argument and the new list is returned
     *      from this function.
     *   2. Pass in NULL in * the list argument to create a new list.
     *   3. The specified string has been appended when this function returns.
     *   4. `curl_slist_append` copies the string.
     */
    val headers = request.headers().map()
    if (!headers.isEmpty()) {
      val headerStrs = asScala(headers)
        .map { (key, values) =>
          asScala(values).map { v =>
            s"${key}: ${v}"
          }
        }
        .flatten
        .toSeq
        .map(toCString(_))

      val _slist = CurlSlist(headerStrs*)
      slist = Optional.of(_slist)
      easy.setSlistOption(CurlOption.HTTPHEADER, _slist)
    }

    /**
     * Set data write callback and data pointer
     */
    easy.setPtrOption(CurlOption.WRITEDATA, writeData)
    easy.setFuncPtrOption(CurlOption.WRITEFUNCTION, writeDataCallback)

    // /**
    //  * TLS options
    //  */
    // val scheme = request.uri().getScheme().toLowerCase().strip()
    // if !scheme.endsWith("s")
    // then // no TLS
    //   easy.setCLongOption(CurlOption.USE_SSL, CurlUseSSL.NONE.value)
    // else // with TLS
    //   // TODO: Register SSL context ptr to set up custom SSL context
    //   // https://curl.se/libcurl/c/CURLINFO_TLS_SSL_PTR.html
    //   easy.setCLongOption(CurlOption.USE_SSL, CurlUseSSL.TRY.value)

    if (client.builder._sslContext.isPresent()) {
      val ctx = {
        val ctx = client.builder._sslContext.get()
        if (!ctx.isInstanceOf[SSLContextImpl])
          throw new RuntimeException(s"Expected internal SSLContextImpl but got ${ctx.getClass()}")

        ctx.asInstanceOf[SSLContextImpl]
      }
      easy.setPtrOption(CurlOption.SSL_CTX_DATA, ctx.ref)
    }

    /**
     * set up request method and body for POST, PUT, etc.
     */
    if (request.bodyPublisher().isPresent()) {
      val bodyPublisher = request.bodyPublisher().get()
      val bodySubscriber = BodySubscribers.ofString(StandardCharsets.UTF_16)
      val postfieldSubscriber = CurlBodySubscriber(bodySubscriber)

      bodyPublisher.subscribe(postfieldSubscriber)
      val data = postfieldSubscriber.getBody().toCompletableFuture().join()

      easy.setCStringOption(CurlOption.POSTFIELDS, toCString(data))
    }
    // NOTES:
    // `CURLOPT_POSTFIELDS` implied POST,
    // so postpone setting method until here.
    val _method_ret = request.method() match
      case "GET" =>
        easy.setCIntOption(CurlOption.HTTPGET, 1)
      case "HEAD" =>
        easy.setCIntOption(CurlOption.NOBODY, 1)
      case "POST" =>
        easy.setCIntOption(CurlOption.POST, 1)
      case "CONNECT" =>
        easy.setCIntOption(CurlOption.CONNECT_ONLY, 1)
      case m @ ("PUT" | "DELETE" | "OPTIONS" | "TRACE" | "PATCH") =>
        easy.setCStringOption(CurlOption.CUSTOMREQUEST, toCString(m))
      case other =>
        throw new UnsupportedOperationException(
          s"HTTP method ${other} is not supported yet",
        )

    /**
     * TODO: set error buffer? https://curl.se/libcurl/c/CURLOPT_ERRORBUFFER.html
     */

    /**
     * Register connection to the client
     */
    client.connections.put(easy, this): Unit
    val ret = client.multi.addCurlEasy(easy)
    if (ret != CurlMultiErrCode.OK)
      throw new RuntimeException(
        s"CURLM add easy failed: error code ${ret} (${ret.getname})",
      )
  }

  private inline def requireNonShutdown(): Unit =
    if (closed.get())
      throw new IllegalStateException("HttpConnection has been Closed")

  private inline def blockUntilPerformDone(): Unit = {
    while !client.performUntilWakeupOrDone(3000) do ()
    client.collectInfo()
  }

  private inline def handleError(): Unit =
    // TODO: If `CURLOPT_ERRORBUFFER` was set with `curl_easy_setopt` there can
    // be an error message stored in the error buffer when non-zero is returned.
    errMsg.map(msg =>
      val code = msg.msg
      val err = msg.data
      code match
        case CurlMsgCode.DONE => // data is CurlErrCode
          val errCode = err.asInstanceOf[CurlErrCode]
          if (errCode != CurlErrCode.OK)
            throw new CurlErrCodeException(errCode)
        case _ => // data is CVoidPtr
          val errStr = fromCString(err.asInstanceOf[Ptr[Byte]])
          throw new CurlException(
            s"CURL message indicates error: code ${code}, data (recast to String) is ${errStr}",
          ),
    ): Unit

  /**
   * When receiving response body for the first time, this callback will be invoked to assemble
   * necessary response info and initialize the `BodyHandler` to transform the response body.
   */
  private def firstRespCallback(): Unit =
    if (!respBodyReceived.compareAndExchange(false, true)) {
      val version = easy.info.version match
        case CurlHttpVersion.VERSION_3   => Version.valueOf("HTTP_3")
        case CurlHttpVersion.VERSION_2_0 => Version.HTTP_2
        case CurlHttpVersion.VERSION_1_1 => Version.HTTP_1_1
        case _                           => Version.HTTP_1_1

      val statusCode = easy.info.responseCode
      val jmap = JMap.ofEntries(
        easy.info.headers
          .mapValues(xs => JList.of(xs.toSeq*))
          .map((k, v) => JMap.entry(k, v))
          .toSeq*,
      )
      val headers = HttpHeaders.of(jmap, (_, _) => true)

      val respInfo = new ResponseInfoImpl(statusCode, version, headers)
      val subscriber = responseBodyHandler(respInfo)
      respBodyPublisher.subscribe(subscriber)

      maybeRespInfo = Optional.of(respInfo)
      maybeRespBodySubscriber = Optional.of(subscriber)
    }
