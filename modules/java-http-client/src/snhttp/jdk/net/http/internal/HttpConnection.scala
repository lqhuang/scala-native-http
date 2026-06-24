package snhttp.jdk.net.http.internal

import java.net.http.{HttpRequest, HttpHeaders, HttpResponse}
import java.net.ConnectException
import java.net.http.HttpClient.{Version, Redirect}
import java.net.http.HttpResponse.{BodyHandler, BodySubscribers, ResponseInfo, BodySubscriber}
import java.nio.ByteBuffer
import java.nio.channels.{ClosedChannelException, UnresolvedAddressException}
import java.nio.charset.StandardCharsets
import java.util.{Map as JMap, List as JList, Optional}
import java.util.concurrent.Flow.Subscription
import java.util.concurrent.{CompletableFuture, SubmissionPublisher, ConcurrentLinkedQueue}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock

import scala.jdk.javaapi.CollectionConverters.asScala
import scala.scalanative.libc.stddef.NULL
import scala.scalanative.unsafe.{
  Ptr,
  Zone,
  CSize,
  CStruct1,
  CStruct3,
  CFuncPtr0,
  CFuncPtr2,
  CFuncPtr4,
  UnsafeRichLong,
  UnsafeRichInt,
  Tag,
}
import scala.scalanative.unsafe.{toCString, fromCString, fromCStringSlice, alloc}
import scala.scalanative.unsafe.CQuote as c
import scala.scalanative.unsigned.{UnsignedRichInt, UnsignedRichLong}

import _root_.snhttp.experimental.curl.curl
import _root_.snhttp.experimental.curl.libcurl
import _root_.snhttp.experimental.curl.curl.{CurlErrCodeException, CurlException}
import _root_.snhttp.experimental.curl.curl.{
  CurlEasy,
  CurlMsg,
  CurlSlist,
  CurlWriteFuncRet,
  CurlFollow,
  CurlUseSsl,
  CurlOption,
  CurlHttpVersion,
  CurlMsgCode,
  CurlErrCode,
  CurlMultiErrCode,
  CurlSocket,
  CurlReadCallback,
  CurlWriteCallback,
  CurlHeaderCallback,
}
import _root_.snhttp.experimental.curl.curl.CurlMultiErrCode.RichCurlMultiErrCode
import _root_.snhttp.experimental.curl.curl.CurlMsgData.asErrCode
import _root_.snhttp.jdk.net.http.{HttpClientImpl, HttpResponseImpl, ResponseInfoImpl}
import _root_.snhttp.jdk.net.http.internal.PropertyUtils
import _root_.snhttp.jdk.net.ssl.SSLContextImpl

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

  import HttpConnection.*

  given Zone = client.zone

  private[snhttp] val easy = CurlEasy()

  private val closed = AtomicBoolean(false)

  /**
   * When `CurlSlist`(alias `curl_slist`) option is passed to `curl_easy_setopt`, libcurl does not
   * copy the entire list so you **must** keep it around until you no longer use this _handle_ for a
   * transfer before you call `curl_slist_free_all` on the list.
   */
  private var slist: Optional[CurlSlist] = Optional.empty()

  private var respInfo: ResponseInfoImpl = _
  private var respBodySubscriber: BodySubscriber[T] = _
  private val respBodyPublisher: SubmissionPublisher[JList[ByteBuffer]] =
    new SubmissionPublisher[JList[ByteBuffer]]()
  private val respBodyReceived =
    AtomicBoolean(false)

  val response = new CompletableFuture[HttpResponse[T]]()

  /**
   * invoke when receiving header data, this callback will be invoked to assemble necessary response
   * info and initialize the `BodyHandler` to transform the response body.
   */
  private final def ensureResponseFutureCompleted() = {
    val version = easy.info.version match
      case CurlHttpVersion.VERSION_3   => Version.valueOf("HTTP_3")
      case CurlHttpVersion.VERSION_2_0 => Version.HTTP_2
      case CurlHttpVersion.VERSION_1_1 => Version.HTTP_1_1
      case _ => throw new IllegalStateException(s"Unexpected HTTP version: ${easy.info.version}")

    val statusCode = easy.info.responseCode
    val jmap = JMap.ofEntries(
      easy.info.headers
        .mapValues(xs => JList.of(xs.toSeq*))
        .map((k, v) => JMap.entry(k, v))
        .toSeq*,
    )
    val headers = HttpHeaders.of(jmap, (_, _) => true)
    val respInfo = new ResponseInfoImpl(statusCode, version, headers)
    val respBodySubscriber = responseBodyHandler(respInfo)
    respBodyPublisher.subscribe(respBodySubscriber)
    response.complete(HttpResponseImpl(request, respInfo, respBodySubscriber.getBody())): Unit
    ()
  }

  val writeData = alloc[CurlRecvBuffer]()
  if (writeData == NULL)
    throw new CurlException("Failed to allocate memory for CurlData")
  writeData.isBodyReceived = respBodyReceived
  writeData.ensureResponseFutureCompleted = ensureResponseFutureCompleted
  writeData.publisher = respBodyPublisher

  init()

  def assignCurlErrMsg(msg: CurlMsg): Unit = {
    if (closed.get())
      throw new IllegalStateException("CurlMsg has already been assigned/done")

    // TODO: If `CURLOPT_ERRORBUFFER` was set with `curl_easy_setopt` there can
    // be an error message stored in the error buffer when non-zero is returned.
    val code = msg.msg
    val err = msg.data

    code match
      case CurlMsgCode.DONE => // data is CurlErrCode
        val errCode = err.asErrCode
        if errCode != CurlErrCode.OK
        then {
          val exc = throwStructuredException(errCode)
          response.completeExceptionally(exc): Unit
          respBodyPublisher.closeExceptionally(exc)
        } //
        else {
          if (!response.isDone())
            ensureResponseFutureCompleted()
          respBodyPublisher.close()
        }
      case _ => // data is CVoidPtr
        val errStr = fromCString(err.asInstanceOf[Ptr[Byte]])
        val exc = new CurlException(
          s"CURL message indicates error: code ${code}, data (recast to String) is ${errStr}",
        )
        respBodyPublisher.closeExceptionally(exc)
        response.completeExceptionally(exc): Unit
  }

  def close(): Unit =
    if (!closed.compareAndExchange(false, true)) {
      client.connections.remove(easy): Unit
      client.multi.removeCurlEasy(easy): Unit
      slist.map(_.freeAll()): Unit
      easy.cleanup()
    }

  /*
   * Private methods
   */

  /**
   * Setup options for this connection based on the `request` and `client` config.
   */
  private def init(): Unit = {
    // easy.setCLongOption(CurlOption.VERBOSE, 1.toSize)

    easy.setCStringOption(CurlOption.USERAGENT, c"sn-java-http-client/0.0.0")
    easy.setCStringOption(CurlOption.URL, toCString(request.uri().toString()))

    val httpVersion = request.version()
    if (httpVersion.isPresent()) {
      val h3 = Version.valueOf("HTTP_3")
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
    easy.setFuncPtrOption(CurlOption.WRITEFUNCTION, writeDataCallback.asFuncPtr)

    // /**
    //  * TLS options
    //  */
    // val scheme = request.uri().getScheme().toLowerCase().strip()
    // if !scheme.endsWith("s")
    // then // no TLS
    //   easy.setCLongOption(CurlOption.USE_SSL, CurlUseSsl.NONE.value)
    // else // with TLS
    //   // TODO: Register SSL context ptr to set up custom SSL context
    //   // https://curl.se/libcurl/c/CURLINFO_TLS_SSL_PTR.html
    //   easy.setCLongOption(CurlOption.USE_SSL, CurlUseSsl.TRY.value)

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
        easy.setCLongOption(CurlOption.HTTPGET, 1.toSize)
      case "HEAD" =>
        easy.setCLongOption(CurlOption.NOBODY, 1.toSize)
      case "POST" =>
        easy.setCLongOption(CurlOption.POST, 1.toSize)
      case "CONNECT" =>
        easy.setCLongOption(CurlOption.CONNECT_ONLY, 1.toSize)
      case m @ ("PUT" | "DELETE" | "OPTIONS" | "TRACE" | "PATCH") =>
        easy.setCStringOption(CurlOption.CUSTOMREQUEST, toCString(m))
      case other =>
        throw new UnsupportedOperationException(
          s"HTTP method ${other} is not supported yet",
        )

    /**
     * TODO: set error buffer? https://curl.se/libcurl/c/CURLOPT_ERRORBUFFER.html
     */

  }

  private inline def requireNonShutdown(): Unit =
    if (closed.get())
      throw new IllegalStateException("HttpConnection has been Closed")

private[http] object HttpConnection:

  final inline def throwStructuredException(err: CurlErrCode): Throwable =
    if (err == CurlErrCode.COULDNT_RESOLVE_HOST)
      // cannot set UnresolvedAddressException() as cause ???
      ConnectException(s"Failed to resolve host address: ${curl.getStrError(err)}")
    else if (err == CurlErrCode.COULDNT_CONNECT)
      // ClosedChannelException
      ConnectException(s"Unreachable host or port: ${curl.getStrError(err)}")
    else
      CurlErrCodeException(err)

  type CurlRecvBuffer = CStruct3[
    AtomicBoolean, // Ptr[atomic_bool] // flag for resp body received
    Function0[Unit], // resp body received callback
    SubmissionPublisher[JList[ByteBuffer]], // publisher for response body chunks
  ]
  given Tag[CurlRecvBuffer] = Tag.materializeCStruct3Tag[
    AtomicBoolean,
    Function0[Unit],
    SubmissionPublisher[JList[ByteBuffer]],
  ]
  // scalafmt: { maxColumn = 150 }
  extension (inline struct: CurlRecvBuffer)
    inline def isBodyReceived: AtomicBoolean = struct._1
    inline def isBodyReceived_=(value: AtomicBoolean): Unit = !struct.at1 = value
    inline def ensureResponseFutureCompleted: Function0[Unit] = struct._2
    inline def ensureResponseFutureCompleted_=(value: Function0[Unit]): Unit = !struct.at2 = value
    inline def publisher: SubmissionPublisher[JList[ByteBuffer]] = struct._3
    inline def publisher_=(value: SubmissionPublisher[JList[ByteBuffer]]): Unit = !struct.at3 = value
  // scalafmt: { maxColumn = 120 }

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
  final val writeDataCallback = CurlWriteCallback.fromScalaFunction {
    (payload: Ptr[Byte], size: CSize, nmemb: CSize, userdata: Ptr[?]) =>
      val writedata = userdata.asInstanceOf[Ptr[CurlRecvBuffer]]

      // only invoke on the first time receiving body data,
      // which means header has been received and processed
      if (!writedata.isBodyReceived.compareAndExchange(false, true))
        writedata.ensureResponseFutureCompleted()

      // it's safe to cast UInt ot Int here
      // Curl guarantees that MAX_WRITE_SIZE won't exceed Int.MaxValue
      val ssize = (size * nmemb).toInt
      val bb = ByteBuffer.allocate(ssize)
      for i <- 0 until ssize do bb.put(!(payload + i))
      bb.flip()

      val offered = writedata.publisher.submit(JList.of(bb))
      size * nmemb
  }

  final val readDataCallback = CurlReadCallback.fromScalaFunction {
    (buffer: Ptr[Byte], size: CSize, nmemb: CSize, userdata: Ptr[?]) =>
      ???
  }

end HttpConnection
