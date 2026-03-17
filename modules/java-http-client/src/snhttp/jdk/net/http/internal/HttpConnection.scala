package snhttp.jdk.net.http.internal

import java.net.http.{HttpRequest, HttpHeaders, HttpResponse}
import java.net.http.HttpClient.{Version, Redirect}
import java.net.http.HttpResponse.{BodyHandler, BodySubscribers, ResponseInfo, BodySubscriber}
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.{Map as JMap, List as JList}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.SubmissionPublisher

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
  UnsafeRichLong,
  UnsafeRichInt,
  Tag,
}
import scala.scalanative.unsigned.{UnsignedRichInt, UnsignedRichLong}
import scala.scalanative.libc.stddef.NULL

import _root_.snhttp.experimental.curl.{
  CurlErrCodeException,
  CurlException,
  CurlFollow,
  CurlUseSsl,
  CurlEasy,
  CurlOption,
  CurlHttpVersion,
  CurlMsgCode,
  CurlMsg,
  CurlErrCode,
  CurlWriteCallback,
  CurlSlist,
  CurlWriteFuncRet,
}
import _root_.snhttp.jdk.net.http.{HttpClientImpl, HttpResponseImpl, ResponseInfoImpl}
import _root_.snhttp.jdk.internal.PropertyUtils

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
  inline def writeNotify: Function0[Unit] = struct._2
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
  private var curlMsg: Option[CurlMsg] = None

  /**
   * When `CurlSlist`(alias `curl_slist`) option is passed to `curl_easy_setopt`, libcurl does not
   * copy the entire list so you **must** keep it around until you no longer use this _handle_ for a
   * transfer before you call `curl_slist_free_all` on the list.
   */
  private var slist: Option[CurlSlist] = None

  private val respBodyReceived = new AtomicBoolean(false)
  private val respBodyPublisher = new SubmissionPublisher[JList[ByteBuffer]]()

  @volatile private var maybeRespInfo: Option[ResponseInfoImpl] = None
  @volatile private var maybeRespBodySubscriber: Option[BodySubscriber[T]] = None

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
  (!writeData)._2 = triggerForFirstPacket
  (!writeData)._3 = respBodyPublisher

  private val shutdown = new AtomicBoolean(false)

  init()

  def close(): Unit = {
    easy.cleanup()
    slist.map(_.freeAll()): Unit
    zone.close()
    shutdown.compareAndExchange(false, true): Unit
  }

  def assignCurlMsg(msg: CurlMsg): Unit =
    if (curlMsg.isDefined)
      throw new IllegalStateException("CurlMsg has already been assigned/done")
    curlMsg = Some(msg)
    // Connection is done, close the publisher to signal completion to subscribers.
    respBodyPublisher.close()

  inline def waitUntilDoneReceived(): Unit =
    while !client.runAndGetIsDone(500) do println("HttpConnection waiting for DONE message...")
    client.collectInfo()

  def buildResponse(): HttpResponse[T] =
    handleError()
    if (maybeRespInfo.isEmpty || maybeRespBodySubscriber.isEmpty)
      throw new IllegalStateException(
        s"ResponseInfo ${maybeRespInfo} or ResponseBodySubscriber ${responseBodyHandler} is not initialized",
      )
    val body = maybeRespBodySubscriber.get.getBody().toCompletableFuture().join()
    new HttpResponseImpl(request, maybeRespInfo.get, body)

  //
  // Private methods
  //

  /**
   * Setup options for this connection based on the `request` and `client` config.
   */
  private def init(): Unit = {
    easy.setCStringOption(CurlOption.URL, toCString(request.uri().toString()))

    val httpVersion = request.version()
    if (httpVersion.isPresent())
      val version = httpVersion.get() match
        case Version.HTTP_1_1 => CurlHttpVersion.VERSION_1_1
        case Version.HTTP_2   => CurlHttpVersion.VERSION_2_0
      easy.setCLongOption(CurlOption.HTTP_VERSION, version.value)

    // default to 30 seconds
    val timeoutMs = request.timeout().map(_.toMillis()).orElse(30 * 1000L)
    easy.setCLongOption(
      CurlOption.TIMEOUT_MS,
      timeoutMs.toSize,
    )

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
     *   4. curl_slist_append copies the string.
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
      slist = Some(_slist)
      easy.setSlistOption(CurlOption.HTTPHEADER, _slist)
    }

    /**
     * Set data write callback and data pointer
     */
    easy.setPtrOption(CurlOption.WRITEDATA, writeData)
    easy.setFuncPtrOption(CurlOption.WRITEFUNCTION, writeDataCallback)

    /**
     * TLS options
     */
    val scheme = request.uri().getScheme().toLowerCase().strip()
    if !scheme.endsWith("s")
    then // no TLS
      easy.setCLongOption(CurlOption.USE_SSL, CurlUseSsl.NONE.value)
    else // with TLS
      // TODO: Register SSL context ptr to set up custom SSL context
      // https://curl.se/libcurl/c/CURLINFO_TLS_SSL_PTR.html
      easy.setCLongOption(CurlOption.USE_SSL, CurlUseSsl.TRY.value)

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

    println("INFO: Curl options initialized successfully")
  }

  private def requireNonShutdown(): Unit =
    if (shutdown.get())
      throw new IllegalStateException("HttpConnection has been shutdown")

  private def handleError(): Unit =
    // TODO: If `CURLOPT_ERRORBUFFER` was set with `curl_easy_setopt` there can
    // be an error message stored in the error buffer when non-zero is returned.
    curlMsg match
      case None =>
        throw new IllegalStateException("Calling `handleError` before done")
      case Some(msg) =>
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
            )

  /**
   * When receiving response body for the first time, this callback will be invoked to assemble
   * necessary response info and initialize the `BodyHandler` to transform the response body.
   */
  private def triggerForFirstPacket(): Unit = {
    val version = easy.info.version match
      case CurlHttpVersion.VERSION_1_1 => Version.HTTP_1_1
      case CurlHttpVersion.VERSION_2_0 => Version.HTTP_2
      case _                           => Version.HTTP_1_1
    val statusCode = easy.info.responseCode
    val jmap = JMap.ofEntries(
      easy.info.headers.mapValues(xs => JList.of(xs.toSeq*)).map((k, v) => JMap.entry(k, v)).toSeq*,
    )
    val headers = HttpHeaders.of(jmap, (_, _) => true)

    synchronized {
      val respInfo = new ResponseInfoImpl(statusCode, version, headers)
      maybeRespInfo = Some(respInfo)
      val subscriber = responseBodyHandler(respInfo)
      maybeRespBodySubscriber = Some(subscriber)
      respBodyPublisher.subscribe(subscriber)
    }
  }

object HttpConnection:

  def apply[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
      client: HttpClientImpl,
  ): HttpConnection[T] =
    new HttpConnection[T](request, responseBodyHandler, client)

end HttpConnection
