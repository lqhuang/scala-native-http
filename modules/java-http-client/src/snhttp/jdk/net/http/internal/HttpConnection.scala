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
  fromCStringSlice,
  stackalloc,
  alloc,
  CChar,
  Tag,
  CLong,
  CSize,
  CString,
  CStruct2,
  CFuncPtr4,
  sizeof,
}
import scala.scalanative.libc.stdlib.{malloc, calloc, free, realloc}
import scala.scalanative.libc.string.memcpy
import scala.scalanative.unsigned.UnsignedRichInt
import scala.util.Random

import snhttp.experimental.libcurl
import snhttp.experimental.libcurl.{
  CurlSlist,
  CurlFollow,
  CurlUseSsl,
  CurlInfo,
  Curl,
  CurlOption,
  CurlHttpVersion,
  CurlMsgCode,
  CurlMsg,
  CurlHeader,
  CurlMulti,
  CurlMultiCode,
}
import snhttp.experimental.libcurl.CurlMultiCode.RichCurlMultiCode
import snhttp.experimental.libcurl.CurlErrCode.RichCurlErrCode
import snhttp.jdk.net.http.{HttpClientImpl, HttpResponseImpl}
import snhttp.utils.PointerFinalizer

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
private[http] class HttpConnection[T](
    request: HttpRequest,
    responseBodyHandler: BodyHandler[T],
    client: HttpClientImpl,
) extends AutoCloseable:

  private[snhttp] var ptr = libcurl.easyInit()
  if (ptr == null)
    throw new RuntimeException("Failed to initialize CURL easy handle")
  val _ = PointerFinalizer(
    this,
    ptr,
    _ptr => libcurl.easyCleanup(_ptr),
  )

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

  /**
   * Setup options for this connection based on the `request` and `client` config.
   *
   * FIXME: Improve error handling. Catch libcurl error codes (now just placeholder `_` to ignore)
   * and throw proper exceptions.
   */
  private def init(): Unit = {
    try {
      val _url_ret = libcurl.easySetopt(
        ptr,
        CurlOption.URL,
        toCString(request.uri().toString()),
      )
      // println(s"DEBUG: Set URL: ${_url_ret.getName}")

      /**
       * FIXME:
       *
       *   - [error] Found 1 unreachable symbols!
       *   - [error] Unknown type snhttp.experimental.libcurl.curl$.CurlHttpVersion
       */
      // val httpVersion = request.version().orElse(Version.HTTP_2) match
      //   case Version.HTTP_1_1 => CurlHttpVersion.VERSION_1_1
      //   case Version.HTTP_2   => CurlHttpVersion.VERSION_2_0
      // val _ = libcurl.easySetopt(
      //   ptr,
      //   CurlOption.HTTP_VERSION,
      //   httpVersion,
      // )

      val timeoutMs = request
        .timeout()
        .map(_.toMillis())
        .orElse(30 * 1000L) // default to 30 seconds
      val _timeout_ret = libcurl.easySetopt(
        ptr,
        CurlOption.TIMEOUT_MS,
        timeoutMs,
      )
      // println(s"DEBUG: Set timeout ${_timeout_ret.getName}")

      val connectTimeoutMs = client.builder._connectTimeout.map(_.toMillis).orElse(0L)
      val _ct_ret = libcurl.easySetopt(
        ptr,
        CurlOption.CONNECTTIMEOUT_MS,
        connectTimeoutMs,
      )
      // println(s"DEBUG: Set connect timeout: ${_ct_ret.getName}")

      val followRedirects = client.builder._redirect match
        case Redirect.NEVER  => CurlFollow.DISABLED
        case Redirect.ALWAYS => CurlFollow.ALL
        case Redirect.NORMAL => CurlFollow.OBEYCODE
        case null =>
          throw new IllegalArgumentException(
            s"Unsupported redirect policy: ${client.builder._redirect}",
          )

      val _follow_ret = libcurl.easySetopt(
        ptr,
        CurlOption.FOLLOWLOCATION,
        followRedirects,
      )
      // println(s"DEBUG: Set follow redirects: ${_follow_ret.getName}")

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
      val _header_ret = libcurl.easySetopt(ptr, CurlOption.HTTPHEADER, slistPtr)
      // println(s"DEBUG: Set headers: ${_header_ret.getName}")

      /**
       * TLS options
       */
      val scheme = request.uri().getScheme().toLowerCase().strip()
      if !scheme.endsWith("s")
      then // no TLS
        val _use_ssl_ret = libcurl.easySetopt(ptr, CurlOption.USE_SSL, CurlUseSsl.NONE)
        // println(s"DEBUG: Set USE_SSL to NONE, ${_use_ssl_ret.getName}")
      else { // with TLS
        val _use_ssl_ret = libcurl.easySetopt(ptr, CurlOption.USE_SSL, CurlUseSsl.TRY)
        // TODO: https://curl.se/libcurl/c/CURLINFO_TLS_SSL_PTR.html
        // Register SSL context ptr to set up custom SSL context
        // ()
        // println(s"DEBUG: Set USE_SSL to TRY, ${_use_ssl_ret.getName}")
      }

      /**
       * Set body handler
       */
      val _writefunction_ret = libcurl.easySetopt(
        ptr,
        CurlOption.WRITEFUNCTION,
        writeFunction,
      )
      val _writedata_ret = libcurl.easySetopt(
        ptr,
        CurlOption.WRITEDATA,
        respBody,
      )
      val _headerdata_ret = libcurl.easySetopt(
        ptr,
        CurlOption.HEADERDATA,
        respHeader,
      )

      /**
       * set up request method and body for POST, PUT, etc.
       */
      if (request.bodyPublisher().isPresent()) {
        val bodyPublisher = request.bodyPublisher().get()
        val bodySubscriber = BodySubscribers.ofString(StandardCharsets.UTF_16)
        val postfieldSubscriber = CurlBodySubscriber(bodySubscriber)

        bodyPublisher.subscribe(postfieldSubscriber)
        val data = postfieldSubscriber.getBody().toCompletableFuture().join()

        val _postfields_ret = libcurl.easySetopt(ptr, CurlOption.POSTFIELDS, toCString(data))
      }
      // NOTES:
      // `CURLOPT_POSTFIELDS` implied POST,
      // so postpone setting method until here.
      val _method_ret = request.method() match
        case "GET" =>
          libcurl.easySetopt(ptr, CurlOption.HTTPGET, 1)
        case "HEAD" =>
          libcurl.easySetopt(ptr, CurlOption.NOBODY, 1)
        case "POST" =>
          libcurl.easySetopt(ptr, CurlOption.POST, 1)
        case "CONNECT" =>
          libcurl.easySetopt(ptr, CurlOption.CONNECT_ONLY, 1)
        case m @ ("PUT" | "DELETE" | "OPTIONS" | "TRACE" | "PATCH") =>
          libcurl.easySetopt(ptr, CurlOption.CUSTOMREQUEST, toCString(m))
        case other =>
          throw new UnsupportedOperationException(
            s"HTTP method ${other} is not supported yet",
          )
      // println(s"DEBUG: Set HTTP method: ${_method_ret.getName}")

      /**
       * Finally add easy handle to multi handle
       */
      val code = libcurl.multiAddHandle(client.ptr, ptr)
      if (code != 0)
        throw new RuntimeException(
          s"Failed to add easy handle to multi handle: error code ${code}",
        )
      // println(s"DEBUG: Added easy handle to multi handle with code: ${code}")

    } catch {
      case e: Exception => throw e
    }

    // println("INFO: Curl options initialized successfully")
  }

  def close(): Unit = {
    if (ptr != null)
      libcurl.easyCleanup(ptr)
      ptr = null

    if (slistPtr != null)
      libcurl.slistFreeAll(slistPtr)
      slistPtr = null

    zone.close()
  }

  def perform(): Unit =
    // println("DEBUG: Performing CURL multi perform...")
    // val ret = libcurl.multiPerform(client.ptr, _runningCounterPtr)
    // println(ret.getName)
    // _started.weakCompareAndSet(false, true): Unit
    ()

  def pollUntilDone(timeout: Duration): Unit =
    require(timeout <= Int.MaxValue.milliseconds, "Timeout too large")
    val _runningCounterPtr: Ptr[Int] = stackalloc[Int]()

    while {
      val ret = libcurl.multiPerform(client.ptr, _runningCounterPtr)
      // println(s"DEBUG: CURL multi perform result: ${ret.getName}")
      // println(s"DEBUG: CURL still running counter result: ${!_runningCounterPtr}")

      if ret == CurlMultiCode.OK
      then
        val pollResult = libcurl.multiPoll(client.ptr, null, 0.toUInt, timeout.toMillis.toInt, null)
        // println(s"DEBUG: CURL multi poll result: ${pollResult.getName}")
        !(!_runningCounterPtr == 0 && pollResult == CurlMultiCode.OK)
      else
        throw new RuntimeException(
          s"CURL multi perform failed with code: ${ret}",
        )
    } do Thread.sleep(100)

  def waitUntilDoneReceived(): Unit =
    pollUntilDone(1000.milliseconds)
    // Zone {
    //   val msgQPtr = stackalloc[Int]() // zero-initialized
    //   var msg: Ptr[CurlMsg] = null
    //   while {
    //     msg = libcurl.multiInfoRead(client.ptr, msgQPtr)
    //     // Stop
    //     // if we have catched the message for curr connection (easyHandle)
    //     msg == null || !((!msg).easyHandle == ptr && (!msg).msg == CurlMsgCode.DONE)
    //     // msg == null ||
    //   } do Thread.sleep(100)

    //   // We unconfidently assueme here that msg is not null and (!msg).easyHandle == conn.ptr
    //   if (msg == null || (!msg).easyHandle != ptr)
    //     throw new IllegalStateException(
    //       s"Failed to complete HTTP request: no DONE message received for the connection",
    //     )
    // }

  def buildResponse(): HttpResponse[T] = {

    val respData = fromCString((!respBody)._1, StandardCharsets.UTF_8)
    println(s"DEBUG: Response Body received: ${respData}")

    free((!respBody)._1)
    free(respBody)

    free((!respHeader)._1)
    free(respHeader)

    val info = CurlGetInfoHelper(ptr)

    try {
      val subscriber = new CurlBodySubscriber(responseBodyHandler(info))
      val publisher = BodyPublishers.ofString(respData, StandardCharsets.UTF_8)
      publisher.subscribe(subscriber)

      val body = subscriber.getBody().toCompletableFuture().join()

      new HttpResponseImpl(
        request,
        info.version(),
        info.statusCode(),
        info.headers(),
        body,
      )
    } catch {
      case exc: Exception =>
        throw new RuntimeException(
          s"Failed to build HttpResponse: ${exc.getMessage()}",
          exc,
        )
    } finally info.close()
  }

  val writeFunction: CFuncPtr4[Ptr[Byte], CSize, CSize, Ptr[CurlData], CSize] = {
    (ptr: Ptr[CChar], size: CSize, nmemb: CSize, data: Ptr[CurlData]) =>
      // println(s"DEBUG: In writeFunction callback...")
      val index: CSize = (!data)._2
      val increment: CSize = size * nmemb

      (!data)._2 = (!data)._2 + increment
      (!data)._1 = realloc((!data)._1, (!data)._2 + 1.toUInt)
      memcpy((!data)._1 + index, ptr, increment): Unit
      !(!data)._1.+((!data)._2) = 0.toByte
      size * nmemb
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
