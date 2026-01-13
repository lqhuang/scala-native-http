package snhttp.jdk.net.http.internal

import java.net.http.{HttpRequest, HttpHeaders, HttpResponse}
import java.net.http.HttpClient.{Version, Redirect}
import java.net.http.HttpResponse.{BodyHandler, BodySubscribers, ResponseInfo}
import java.net.http.HttpRequest.BodyPublishers
import java.nio.charset.StandardCharsets
import java.util.Map as JMap
import java.util.List as JList
import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.boundary
import scala.util.boundary.break
import scala.scalanative.unsafe.{
  Ptr,
  Zone,
  toCString,
  fromCString,
  alloc,
  CChar,
  CSize,
  CString,
  CStruct2,
  CFuncPtr4,
  sizeof,
  CQuote,
  UnsafeRichLong,
  UnsafeRichInt,
}
import scala.scalanative.libc.stdlib.{malloc, calloc, free, realloc}
import scala.scalanative.libc.string.memcpy
import scala.scalanative.libc.stdio.printf
import scala.scalanative.unsigned.{UnsignedRichInt, UnsignedRichLong}
import scala.util.Random

import _root_.snhttp.experimental.curl.{CurlErrCodeException, CurlOSException, CurlException}
import _root_.snhttp.experimental.curl.{CurlInfo, CurlEasy as CurlEasyHelper}
import _root_.snhttp.experimental.libcurl
import _root_.snhttp.experimental.libcurl.{
  CurlSlist,
  CurlFollow,
  CurlUseSsl,
  Curl,
  CurlOption,
  CurlHttpVersion,
  CurlMsgCode,
  CurlMsg,
  CurlHeader,
  CurlMulti,
  CurlMultiCode,
  CurlErrCode,
}
import _root_.snhttp.experimental.libcurl.CurlMultiCode.RichCurlMultiCode
import _root_.snhttp.experimental.libcurl.CurlErrCode.RichCurlErrCode
import _root_.snhttp.experimental.libcurl.CurlFollow.RichCurlFollow
import _root_.snhttp.jdk.net.http.{HttpClientImpl, HttpResponseImpl, ResponseInfoImpl}
import _root_.snhttp.utils.PointerFinalizer

type CurlData = CStruct2[
  /** memory */
  CString,
  /** size */
  CSize,
]

/**
 * Represents a (virtual) connection (pool) to a web server, majorly based on libcurl's easy handle.
 *
 *   - For HTTP/1.1, multiple connections may be created
 *   - For HTTP/2, a connection corresponds to a multiplexed connection.
 */
final private[http] class HttpConnection[T](
    request: HttpRequest,
    responseBodyHandler: BodyHandler[T],
    client: HttpClientImpl,
) extends AutoCloseable:

  private[snhttp] var ptr = libcurl.easyInit()
  if (ptr == null)
    throw new CurlException("Failed to initialize CURL easy handle")
  val _ = PointerFinalizer(
    this,
    ptr,
    _ptr => libcurl.easyCleanup(_ptr),
  )
  private val _easy = CurlEasyHelper(ptr)
  private var _curlMsg: Option[Ptr[CurlMsg]] = None

  /**
   * When `CurlSlist`(alias `curl_slist`) option is passed to `curl_easy_setopt`, libcurl does not
   * copy the entire list so you **must** keep it around until you no longer use this _handle_ for a
   * transfer before you call `curl_slist_free_all` on the list.
   */
  private var slistPtr: Ptr[CurlSlist] = null
  val _ = PointerFinalizer(
    this,
    slistPtr,
    _slist => libcurl.slistFreeAll(_slist),
  )

  private val _started = new AtomicBoolean(false)
  private val _shutdown = new AtomicBoolean(false)

  given zone: Zone = Zone.open()

  // val respBody: Ptr[CurlData] = alloc()
  // (!respBody)._1 = toCString(Random.nextString(1024 * 1024 * 1024))
  // (!respBody)._2 = 0.toUInt
  val respBody = malloc(sizeof[CurlData]).asInstanceOf[Ptr[CurlData]]
  (!respBody)._1 = calloc(4096.toUInt, sizeof[CChar])
  (!respBody)._2 = 0.toUInt

  val respHeader = malloc(sizeof[CurlData]).asInstanceOf[Ptr[CurlData]]
  (!respHeader)._1 = calloc(4096.toUInt, sizeof[CChar])
  (!respHeader)._2 = 0.toUInt

  init()

  def close(): Unit = {
    if (ptr != null)
      libcurl.easyCleanup(ptr)
      ptr = null

    if (slistPtr != null)
      libcurl.slistFreeAll(slistPtr)
      slistPtr = null

    zone.close()

    _shutdown.compareAndExchange(false, true): Unit
  }

  def assignCurlMsg(msgPtr: Ptr[CurlMsg]): Unit =
    if (assigned)
      throw new IllegalStateException("CurlMsg has already been assigned/done")
    _curlMsg = Some(msgPtr)

  def waitUntilDoneReceived(): Unit =
    while !done do client.performAndPoll(500)

  def buildResponse(): HttpResponse[T] = {
    handleError()

    val respData = fromCString((!respBody)._1, StandardCharsets.UTF_8)
    println(s"DEBUG: Response Body received: ${respData}")

    free((!respBody)._1)
    free(respBody)

    free((!respHeader)._1)
    free(respHeader)

    val version = _easy.info.version match
      case CurlHttpVersion.VERSION_1_1 => Version.HTTP_1_1
      case CurlHttpVersion.VERSION_2_0 => Version.HTTP_2
      case _                           => Version.HTTP_1_1
    val statusCode = _easy.info.responseCode
    val jmap = JMap.ofEntries(
      _easy.info.headers.mapValues(xs => JList.of(xs.toSeq*)).map((k, v) => JMap.entry(k, v)).toSeq*,
    )
    val headers = HttpHeaders.of(jmap, (_, _) => true)
    val ri = new ResponseInfoImpl(statusCode, version, headers)

    val subscriber = new CurlBodySubscriber(responseBodyHandler(ri))
    val publisher = BodyPublishers.ofString(respData, StandardCharsets.UTF_8)
    publisher.subscribe(subscriber)
    val body = subscriber.getBody().toCompletableFuture().join()

    new HttpResponseImpl(request, ri, body)
  }

  //
  // Private methods
  //

  /**
   * Setup options for this connection based on the `request` and `client` config.
   */
  private def init(): Unit =
    try {
      _easy.setStringOption(CurlOption.URL, request.uri().toString())

      val httpVersion = request.version()
      if (httpVersion.isPresent())
        val version = httpVersion.get() match
          case Version.HTTP_1_1 => CurlHttpVersion.VERSION_1_1
          case Version.HTTP_2   => CurlHttpVersion.VERSION_2_0
        _easy.setCLongOption(CurlOption.HTTP_VERSION, version.value)

      // default to 30 seconds
      val timeoutMs = request.timeout().map(_.toMillis()).orElse(30 * 1000L)
      _easy.setCLongOption(
        CurlOption.TIMEOUT_MS,
        timeoutMs.toSize,
      )

      val connectTimeoutMs = client.builder._connectTimeout.map(_.toMillis).orElse(3 * 1000L)
      _easy.setCLongOption(CurlOption.CONNECTTIMEOUT_MS, connectTimeoutMs.toSize)

      val followRedirects = client.builder._redirect match
        case Redirect.NEVER  => CurlFollow.DISABLED
        case Redirect.ALWAYS => CurlFollow.ALL
        case Redirect.NORMAL => CurlFollow.OBEYCODE
      val _follow_ret = _easy.setCLongOption(CurlOption.FOLLOWLOCATION, followRedirects.value)

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
      request.headers().map().entrySet().stream().forEach { entry =>
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
        // println(s"DEBUG: Appended header: ${header} to slistPtr: ${slistPtr}")
      }
      _easy.setSlistOption(CurlOption.HTTPHEADER, slistPtr)

      /**
       * TLS options
       */
      val scheme = request.uri().getScheme().toLowerCase().strip()
      if !scheme.endsWith("s")
      then // no TLS
        _easy.setCLongOption(CurlOption.USE_SSL, CurlUseSsl.NONE.value)
      else // with TLS
        // TODO: Register SSL context ptr to set up custom SSL context
        // https://curl.se/libcurl/c/CURLINFO_TLS_SSL_PTR.html
        _easy.setCLongOption(CurlOption.USE_SSL, CurlUseSsl.TRY.value)

      /**
       * Set data write callback and data pointer
       */
      import HttpConnection.writeDataFunction
      _easy.setFuncPtrOption(CurlOption.WRITEFUNCTION, writeDataFunction)
      _easy.setPtrOption(CurlOption.WRITEDATA, respBody)
      _easy.setPtrOption(CurlOption.HEADERDATA, respHeader)

      /**
       * set up request method and body for POST, PUT, etc.
       */
      if (request.bodyPublisher().isPresent()) {
        val bodyPublisher = request.bodyPublisher().get()
        val bodySubscriber = BodySubscribers.ofString(StandardCharsets.UTF_16)
        val postfieldSubscriber = CurlBodySubscriber(bodySubscriber)

        bodyPublisher.subscribe(postfieldSubscriber)
        val data = postfieldSubscriber.getBody().toCompletableFuture().join()

        _easy.setStringOption(CurlOption.POSTFIELDS, data)
      }
      // NOTES:
      // `CURLOPT_POSTFIELDS` implied POST,
      // so postpone setting method until here.
      val _method_ret = request.method() match
        case "GET" =>
          _easy.setCIntOption(CurlOption.HTTPGET, 1)
        case "HEAD" =>
          _easy.setCIntOption(CurlOption.NOBODY, 1)
        case "POST" =>
          _easy.setCIntOption(CurlOption.POST, 1)
        case "CONNECT" =>
          _easy.setCIntOption(CurlOption.CONNECT_ONLY, 1)
        case m @ ("PUT" | "DELETE" | "OPTIONS" | "TRACE" | "PATCH") =>
          _easy.setStringOption(CurlOption.CUSTOMREQUEST, m)
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
    } catch {
      case exc: Exception =>
        close()
        throw exc
    }
    println("INFO: Curl options initialized successfully")

  private def done: Boolean =
    assigned || !client.running

  private def assigned: Boolean =
    _curlMsg.isDefined

  private def requireNonShutdown(): Unit =
    if (_shutdown.get())
      throw new IllegalStateException("HttpConnection has been shutdown")

  private def handleError(): Unit =

    println(s"DEBUG: Handling errors for HttpConnection... ${_curlMsg}")
    _curlMsg match
      case None =>
        throw new IllegalStateException("Calling `handleError` before done")
      case Some(msgPtr) =>
        val code = (!msgPtr).msg
        val err = (!msgPtr).data
        code match
          case CurlMsgCode.DONE => // data is CurlErrCode
            val errCode = err.asInstanceOf[CurlErrCode]
            if (errCode != CurlErrCode.OK)
              throw new CurlErrCodeException(errCode)
          case _ => // data is CVoidPtr
            val errStr = fromCString(err.asInstanceOf[Ptr[CChar]])
            throw new CurlException(
              s"CURL message indicates error: code ${code}, data (recast to String) is ${errStr}",
            )

    // val osErrNo = _easy.info.osErrNo
    // if (osErrNo != 0)
    //   throw new CurlOSException(osErrNo)
    // val proxyError = _easy.info.proxyError
    // if (proxyError != 0)
    //   throw new CurlException(s"Proxy error occurred: code ${proxyError}")

end HttpConnection

object HttpConnection:

  def apply[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
      client: HttpClientImpl,
  ): HttpConnection[T] =
    new HttpConnection[T](request, responseBodyHandler, client)

  private val writeDataFunction: CFuncPtr4[Ptr[Byte], CSize, CSize, Ptr[CurlData], CSize] =
    CFuncPtr4.fromScalaFunction((ptr: Ptr[CChar], size: CSize, nmemb: CSize, data: Ptr[CurlData]) =>
      printf(c"DEBUG: In writeDataFunction callback..."): Unit
      val index: CSize = (!data)._2
      val increment: CSize = size * nmemb

      (!data)._2 = (!data)._2 + increment
      (!data)._1 = realloc((!data)._1, (!data)._2 + 1.toUInt)
      memcpy((!data)._1 + index, ptr, increment): Unit
      printf(c"DEBUG: Written %d bytes to response body buffer\n", increment.toInt): Unit
      !(!data)._1.+((!data)._2) = 0.toByte

      increment,
    )

end HttpConnection
