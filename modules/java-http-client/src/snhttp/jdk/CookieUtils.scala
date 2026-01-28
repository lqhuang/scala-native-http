package snhttp.jdk

import java.io.IOException
import java.net.{CookieHandler, URI}
import java.net.http.{HttpHeaders, HttpRequest}
import java.util.{ArrayList, Collections, HashMap, List as JList, Map as JMap, TreeMap}

/**
 * Utility methods for cookie integration with HTTP requests and responses.
 *
 * This class provides the glue between CookieHandler and HttpClient:
 * - Retrieves cookies for outgoing requests
 * - Stores cookies from incoming responses
 */
private[jdk] object CookieUtils:

  /**
   * Get cookies to be sent with the request and merge them with existing request headers.
   *
   * @param cookieHandler
   *   the cookie handler to use (may be null)
   * @param request
   *   the HTTP request
   * @return
   *   a map of headers including any Cookie headers from the cookie handler
   */
  def getCookiesForRequest(
      cookieHandler: CookieHandler | Null,
      request: HttpRequest,
  ): JMap[String, JList[String]] =
    val result = new TreeMap[String, JList[String]](String.CASE_INSENSITIVE_ORDER)

    // Copy existing headers from request
    request.headers().map().forEach { (name, values) =>
      result.put(name, new ArrayList(values)): Unit
    }

    // Add cookies from cookie handler if present
    if cookieHandler != null then
      try
        val uri = request.uri()
        val cookieHeaders = cookieHandler.get(uri, result)
        if cookieHeaders != null then
          cookieHeaders.forEach { (name, values) =>
            if name != null && values != null && !values.isEmpty then
              // Merge cookie headers - append to existing or add new
              val existing = result.get(name)
              if existing != null then
                val merged = new ArrayList(existing)
                values.forEach(v => if v != null then merged.add(v): Unit)
                result.put(name, merged): Unit
              else result.put(name, new ArrayList(values)): Unit
          }
      catch
        case e: IOException =>
          // Log and continue without cookies if handler fails
          // In production, consider proper logging
          ()

    Collections.unmodifiableMap(result)

  /**
   * Store cookies from response headers.
   *
   * @param cookieHandler
   *   the cookie handler to use (may be null)
   * @param uri
   *   the URI the response came from
   * @param responseHeaders
   *   the response headers containing Set-Cookie headers
   */
  def storeCookiesFromResponse(
      cookieHandler: CookieHandler | Null,
      uri: URI,
      responseHeaders: HttpHeaders,
  ): Unit =
    if cookieHandler != null then
      try
        // Convert HttpHeaders to Map format expected by CookieHandler.put
        val headerMap = new HashMap[String, JList[String]]()
        responseHeaders.map().forEach { (name, values) =>
          headerMap.put(name, new ArrayList(values)): Unit
        }
        cookieHandler.put(uri, headerMap)
      catch
        case e: IOException =>
          // Log and continue if storing cookies fails
          // In production, consider proper logging
          ()

  /**
   * Store cookies from response headers using a simple Map.
   *
   * @param cookieHandler
   *   the cookie handler to use (may be null)
   * @param uri
   *   the URI the response came from
   * @param responseHeaders
   *   the response headers as a Map
   */
  def storeCookiesFromResponse(
      cookieHandler: CookieHandler | Null,
      uri: URI,
      responseHeaders: JMap[String, JList[String]],
  ): Unit =
    if cookieHandler != null then
      try cookieHandler.put(uri, responseHeaders)
      catch
        case e: IOException =>
          // Log and continue if storing cookies fails
          ()

  /**
   * Build a Cookie header string from a list of cookie values.
   *
   * @param cookies
   *   list of cookie strings
   * @return
   *   a single Cookie header value, or null if no cookies
   */
  def buildCookieHeader(cookies: JList[String]): String | Null =
    if cookies == null || cookies.isEmpty then null
    else
      val sb = new java.lang.StringBuilder()
      var first = true
      val it = cookies.iterator()
      while it.hasNext do
        val cookie = it.next()
        if cookie != null && !cookie.isEmpty then
          if !first then sb.append("; "): Unit
          first = false
          sb.append(cookie): Unit
      if sb.length > 0 then sb.toString else null
