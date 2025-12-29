package snhttp.jdk.net.http.internal

import java.net.http.{HttpRequest, HttpHeaders, HttpResponse}
import java.net.http.HttpClient.{Version, Redirect}
import java.net.http.HttpResponse.{BodyHandler, BodySubscribers, ResponseInfo}
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.Map as JMap
import java.util.List as JList
import java.util.concurrent.atomic.AtomicBoolean

import scala.scalanative.unsafe.{
  Ptr,
  Zone,
  toCString,
  fromCStringSlice,
  stackalloc,
  alloc,
  Tag,
  CLong,
  CSize,
  CString,
  CStruct2,
}

import snhttp.utils.PointerFinalizer
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
}

import snhttp.jdk.net.http.{HttpClientImpl, HttpResponseImpl}
import java.net.http.HttpRequest.BodyPublishers

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

  private[snhttp] val ptr = libcurl.easyInit()
  if (ptr == null)
    throw new RuntimeException("Failed to initialize CURL easy handle")
  // val _ = PointerFinalizer(
  //   this,
  //   ptr,
  //   _ptr => libcurl.easyCleanup(_ptr),
  // )

  /**
   * When `CurlSlist`(alias `curl_slist`) option is passed to `curl_easy_setopt`, libcurl does not
   * copy the entire list so you **must** keep it around until you no longer use this _handle_ for a
   * transfer before you call `curl_slist_free_all` on the list.
   */
  private var slistPtr: Ptr[CurlSlist] = null

  // val _ = PointerFinalizer(
  //   this,
  //   slistPtr,
  //   _slist => libcurl.curl_slist_free_all(_slist),
  // )

  private val _started = new AtomicBoolean(false)
  private val _shutdown = new AtomicBoolean(false)

  given zone: Zone = Zone.open()

  val respBody: Ptr[CurlData] = alloc[CurlData]()

  init()

  /**
   * Setup options for this connection based on the `request` and `client` config.
   *
   * FIXME: error handling. catch libcurl error codes (now just `_` to ignore) and throw proper
   * exceptions.
   */
  private def init(): Unit = {
    try {
      val _ = libcurl.easySetopt(
        ptr,
        CurlOption.URL,
        toCString(request.uri().toString()),
      )

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
        .orElse(Duration.ofSeconds(30))
        .toMillis
      val _ = libcurl.easySetopt(
        ptr,
        CurlOption.TIMEOUT_MS,
        timeoutMs,
      )

      val connectTimeoutMs = client.builder._connectTimeout.map(_.toMillis).orElse(0L)
      val _ = libcurl.easySetopt(
        ptr,
        CurlOption.CONNECTTIMEOUT_MS,
        connectTimeoutMs,
      )

      val followRedirects = client.builder._redirect match
        case Redirect.NEVER  => CurlFollow.DISABLED
        case Redirect.ALWAYS => CurlFollow.ALL
        case Redirect.NORMAL => CurlFollow.OBEYCODE
      val _ = libcurl.easySetopt(
        ptr,
        CurlOption.FOLLOWLOCATION,
        followRedirects,
      )

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

      }
      val _ = libcurl.easySetopt(ptr, CurlOption.HTTPHEADER, slistPtr)

      /**
       * TLS options
       */
      val scheme = request.uri().getScheme().toLowerCase().strip()

      if !scheme.endsWith("s")
      then // no TLS
        val _ = libcurl.easySetopt(ptr, CurlOption.USE_SSL, CurlUseSsl.NONE)
      else { // with TLS
        val _ = libcurl.easySetopt(ptr, CurlOption.USE_SSL, CurlUseSsl.ALL)

        // TODO: https://curl.se/libcurl/c/CURLINFO_TLS_SSL_PTR.html
        // Register SSL context ptr to set up custom SSL context

        // ()
      }

      /**
       * Set body handler
       */
      val _ = libcurl.easySetopt(
        ptr,
        CurlOption.WRITEFUNCTION,
        ???,
      )
      val _ = libcurl.easySetopt(
        ptr,
        CurlOption.WRITEDATA,
        respBody,
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
        val _ = libcurl.easySetopt(ptr, CurlOption.POSTFIELDS, toCString(data))
      }
      // NOTES:
      // `CURLOPT_POSTFIELDS` implied POST,
      // so postpone setting method until here.
      val _ = request.method() match
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

      /**
       * Finally add easy handle to multi handle
       */
      val code = libcurl.multiAddHandle(client.ptr, ptr)
      if (code != 0)
        throw new RuntimeException(
          s"Failed to add easy handle to multi handle: error code ${code}",
        )

    } catch {
      case e: Exception => throw e
    }
  }

  def close(): Unit = {
    if ptr != null
    then libcurl.easyCleanup(ptr)

    if slistPtr != null
    then
      libcurl.slistFreeAll(slistPtr)
      slistPtr = null

    zone.close()
  }

  def perform(): Unit =
    val stillRunningPtr = stackalloc[Int]()
    val _ = libcurl.multiPerform(client.ptr, stillRunningPtr)
    val exp = _started.weakCompareAndSet(false, true)
    if (!exp) throw new IllegalStateException("HTTP connection should be started")

  def pollUntilDone(): Unit = {
    val msgQPtr = stackalloc[Int]() // zero-initialized
    var msg: Ptr[CurlMsg] = null

    while {
      msg = libcurl.multiInfoRead(client.ptr, msgQPtr)
      // Stop
      // if no more messages
      // or if we have catched the message for curr connection (easyHandle)
      msg == null || ((!msg).easyHandle == ptr && (!msg).msg == CurlMsgCode.DONE)
    } do ()

    // We unconfidently assueme here that msg is not null and (!msg).easyHandle == conn.ptr
    if (msg == null || (!msg).easyHandle != ptr)
      throw new IllegalStateException(
        s"Failed to complete HTTP request: no DONE message received for the connection",
      )

    _shutdown.set(true)
  }

  def buildResponse(): HttpResponse[T] = {
    val respData = fromCStringSlice((!respBody)._1, (!respBody)._2)
    val info = CurlGetInfoHelper(ptr)
    val subscriber = CurlBodySubscriber(responseBodyHandler(info))
    val publisher = BodyPublishers.ofString(respData)

    publisher.subscribe(subscriber)
    val body = subscriber.getBody().toCompletableFuture().join()

    info.close()

    ???
    // HttpResponseImpl(
    //   request,
    //   info.version(),
    //   info.statusCode(),
    //   info.headers(),
    //   body,
    // ).asInstanceOf[HttpResponse[T]]
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
