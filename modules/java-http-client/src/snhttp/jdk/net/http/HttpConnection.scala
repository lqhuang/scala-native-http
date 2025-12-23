package snhttp.jdk.net.http

import java.net.http.HttpRequest
import java.net.http.HttpClient.{Version, Redirect}
import java.time.Duration
import java.util.Map as JMap

import scala.scalanative.unsafe.{Ptr, Zone, toCString, stackalloc, Tag}

import snhttp.utils.PointerFinalizer
import snhttp.experimental.libcurl
import snhttp.experimental.libcurl.core.{CurlSlist, CurlFollow, CurlUseSsl}
import snhttp.experimental.libcurl.options.{CurlOption, CurlOptHttpVersion}

/**
 * Represents a connection to a web server, majorly based on libcurl's easy handle.
 */
private[http] case class HttpConnection(request: HttpRequest, client: HttpClientImpl):

  private[snhttp] val ptr = libcurl.easy.easyInit()
  if (ptr == null)
    throw new RuntimeException("Failed to initialize CURL easy handle")
  val _ = PointerFinalizer(
    this,
    ptr,
    _ptr => libcurl.easy.easyCleanup(_ptr),
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
    _slist => libcurl.core.curl_slist_free_all(_slist),
  )

  // given zone: Zone = Zone.open()
  // but how to close it properly ?_?

  Zone {

    /**
     * FIXME: error handling. catch libcurl error codes (now just `_` to ignore) and throw proper
     * exceptions.
     */
    try {
      val _ = libcurl.easy.easySetopt(
        ptr,
        CurlOption.URL,
        toCString(request.uri().toString()),
      )

      val httpVersion = request.version().orElse(Version.HTTP_2) match
        case Version.HTTP_1_1 => CurlOptHttpVersion.VERSION_1_1
        case Version.HTTP_2   => CurlOptHttpVersion.VERSION_2_0
      val _ = libcurl.easy.easySetopt(
        ptr,
        CurlOption.HTTP_VERSION,
        httpVersion,
      )

      val timeoutMs = request
        .timeout()
        .orElse(Duration.ofSeconds(30))
        .toMillis
      val _ = libcurl.easy.easySetopt(
        ptr,
        CurlOption.TIMEOUT_MS,
        timeoutMs,
      )

      val connectTimeoutMs = client.builder._connectTimeout match
        case Some(duration) => duration.toMillis
        case None           => 0L
      val _ = libcurl.easy.easySetopt(
        ptr,
        CurlOption.CONNECTTIMEOUT_MS,
        connectTimeoutMs,
      )

      val followRedirects = client.builder._redirect match
        case Redirect.NEVER  => CurlFollow.DISABLED
        case Redirect.ALWAYS => CurlFollow.ALL
        case Redirect.NORMAL => CurlFollow.OBEYCODE
      val _ = libcurl.easy.easySetopt(
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
        slistPtr = libcurl.core.curl_slist_append(slistPtr, toCString(header))

      }
      val _ = libcurl.easy.easySetopt(ptr, CurlOption.HTTPHEADER, slistPtr)

      /**
       * TLS options
       */
      val scheme = request.uri().getScheme().toLowerCase().strip()

      if !scheme.endsWith("s")
      then // no TLS
        val _ = libcurl.easy.easySetopt(ptr, CurlOption.USE_SSL, CurlUseSsl.NONE)
      else { // with TLS
        val _ = libcurl.easy.easySetopt(ptr, CurlOption.USE_SSL, CurlUseSsl.ALL)

        // TODO: https://curl.se/libcurl/c/CURLINFO_TLS_SSL_PTR.html

        // ()
      }

    } catch {
      case e: Exception => throw e
    }

  }

  def perform(): Unit =
    ???
