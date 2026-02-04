package snhttp.jdk.net.http.internal

import java.net.http.{HttpRequest, HttpHeaders, HttpResponse}
import java.net.http.HttpClient.{Version, Redirect}
import java.net.http.HttpResponse.{BodyHandler, BodySubscribers, ResponseInfo, BodySubscriber}
import java.net.http.HttpRequest.BodyPublishers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.Map as JMap
import java.util.List as JList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.SubmissionPublisher

import scala.scalanative.libc.stdio.printf

import scala.concurrent.duration.{Duration, DurationInt}
import scala.scalanative.unsafe.{
  Ptr,
  Zone,
  toCString,
  fromCString,
  alloc,
  CSize,
  CStruct3,
  CFuncPtr4,
  sizeof,
  UnsafeRichLong,
  UnsafeRichInt,
  CQuote,
  stackalloc,
}
import scala.scalanative.libc.stdlib.{malloc, free, calloc}
import scala.scalanative.libc.stdio.printf
import scala.scalanative.libc.string.memcpy
import scala.scalanative.unsigned.{UnsignedRichInt, UnsignedRichLong}

import _root_.snhttp.experimental.curl.{CurlErrCodeException, CurlOSException, CurlException}
import _root_.snhttp.experimental.curl.CurlInfo
import _root_.snhttp.experimental.libcurl
import _root_.snhttp.experimental.libcurl.CurlSlist
import _root_.snhttp.experimental.curl.{
  CurlInfo,
  CurlFollow,
  CurlUseSsl,
  CurlEasy,
  CurlOption,
  CurlHttpVersion,
  CurlMsgCode,
  CurlMsg,
  CurlErrCode,
  CurlWriteCallback,
  CurlDebugCallback,
  CurlInfoType,
  CurlData,
}
import _root_.snhttp.jdk.net.http.{HttpClientImpl, HttpResponseImpl, ResponseInfoImpl}
import _root_.snhttp.jdk.internal.PropertyUtils
import _root_.snhttp.utils.PointerCleaner
import scala.scalanative.unsafe.CChar
import scala.scalanative.unsigned.USize

type CurlRecvBuffer = CStruct3[
  /** request nsize bytes */
  Function2[Ptr[Byte], CSize, CSize],
  /** flag for resp body received */
  AtomicBoolean,
  /** resp body received callback */
  Function0[Unit],
]

extension (inline struct: CurlRecvBuffer)
  inline def bufferReader: Function2[Ptr[Byte], CSize, CSize] = struct._1
  inline def flag: AtomicBoolean = struct._2
  inline def firstReceCallback: Function0[Unit] = struct._3

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

  private[snhttp] var ptr = libcurl.easyInit()
  if (ptr == null)
    throw new CurlException("Failed to initialize curl easy handle")
  private[snhttp] val easy = CurlEasy(ptr)
  val _ = PointerCleaner.register(
    this,
    easy.ptr,
    _ptr => libcurl.easyCleanup(_ptr),
  )

  private var curlMsg: Option[CurlMsg] = None

  /**
   * When `CurlSlist`(alias `curl_slist`) option is passed to `curl_easy_setopt`, libcurl does not
   * copy the entire list so you **must** keep it around until you no longer use this _handle_ for a
   * transfer before you call `curl_slist_free_all` on the list.
   */
  private var slistPtr: Ptr[CurlSlist] = null
  val _ = PointerCleaner.register(
    this,
    slistPtr,
    _slist => libcurl.slistFreeAll(_slist),
  )

  given zone: Zone = Zone.open()

  private val respBodyReceived = new AtomicBoolean(false)
  private val respBodyPublisher = SubmissionPublisher[JList[ByteBuffer]]()

  @volatile private var maybeRespInfo: Option[ResponseInfoImpl] = None
  @volatile private var maybeRespBodySubscriber: Option[BodySubscriber[T]] = None

  val writeBufferSize = PropertyUtils.RECEIVE_BUFFER_SIZE // won't change after init
  val writeData: Ptr[CurlRecvBuffer] = alloc[CurlRecvBuffer]()
  if (writeData == null)
    throw new CurlException("Failed to allocate memory for CurlData")
  (!writeData)._1 = offerResponseBodyData
  (!writeData)._2 = respBodyReceived
  (!writeData)._3 = callbackWhenReceivingBodyFirstTime

  private val shutdown = new AtomicBoolean(false)

  init()

  def close(): Unit = {
    if (ptr != null)
      libcurl.easyCleanup(ptr)
      ptr = null

    if (slistPtr != null)
      libcurl.slistFreeAll(slistPtr)
      slistPtr = null

    zone.close()

    if (shutdown.compareAndExchange(false, true) != false)
      throw new IllegalStateException("HttpConnection has already been shutdown")
  }

  def assignCurlMsg(msg: CurlMsg): Unit =
    if (curlMsg.isDefined)
      throw new IllegalStateException("CurlMsg has already been assigned/done")
    curlMsg = Some(msg)

  def waitUntilDoneReceived(): Unit =
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
  private def init(): Unit =
    try {
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
      val headers = request.headers().map().entrySet().stream().forEach { entry =>
        val key = entry.getKey()
        val value = entry.getValue()

        /**
         * ref: https://curl.se/libcurl/c/CURLOPT_HTTPHEADER.html
         *
         * If you provide a header without content (no data on the right side of the colon) as in
         * `Accept:`, the internally used header is removed. To forcibly add a header without
         * content (nothing after the colon), use the form name `;` (using a trailing semicolon).
         *
         * Examples:
         *
         *   1. Add header with content => "Accept: application/json"
         *   2. remove this header => "Accept:"
         *   3. Add header with no content => "Accept;"
         */
        val header =
          if value.isEmpty
          then s"${key};"
          else s"${key}: ${value}"

        slistPtr = libcurl.slistAppend(slistPtr, toCString(header))
      }
      easy.setSlistOption(CurlOption.HTTPHEADER, slistPtr)

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
       * Set data write callback and data pointer
       */
      val _ = libcurl.easySetopt(ptr, CurlOption.WRITEDATA, writeData)
      val _ = libcurl.easySetopt(ptr, CurlOption.WRITEFUNCTION, writeDataCallback)

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
       * Finally add easy handle to multi handle
       */
      client.registerConnection(this)
      println("INFO: Curl options initialized successfully")
    } catch {
      case exc: Exception =>
        close()
        throw exc
    }

  // private def isDone: Boolean =
  //   (curlMsg.isDefined && curlMsg.get.msg == CurlMsgCode.DONE) || !client.isRunning

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
  private def callbackWhenReceivingBodyFirstTime(): Unit = {
    println("!!!!! HttpConnection: receiving response body for the first time")
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

  private def offerResponseBodyData(
      contents: Ptr[Byte],
      size: CSize,
  ): CSize = {
    println("!!!!! HttpConnection: offering response body data to BodySubscriber")
    val ssize = size.toInt
    val bb = ByteBuffer.allocate(ssize)

    for i <- 0 until ssize do
      val b = !(contents + i)
      bb.put(i, b)
    bb.flip()

    val offered = respBodyPublisher.submit(JList.of(bb))
    size
  }

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
  val writeDataCallback: CurlWriteCallback = CFuncPtr4.fromScalaFunction {
    (contents: Ptr[Byte], size: CSize, nmemb: CSize, outstream: Ptr[?]) =>
      val userdata = outstream.asInstanceOf[Ptr[CurlRecvBuffer]]

      if (!(!userdata).flag.compareAndExchange(false, true))
        val _ = printf(c"!!!!! HttpConnection: first time receiving response body")
        (!userdata).firstReceCallback.apply()

      val processed = (!userdata).bufferReader.apply(contents, size * nmemb)
      processed
  }

end HttpConnection

object HttpConnection:

  def apply[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
      client: HttpClientImpl,
  ): HttpConnection[T] =
    new HttpConnection[T](request, responseBodyHandler, client)

end HttpConnection
