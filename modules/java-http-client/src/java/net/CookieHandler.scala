package java.net

import java.util.{Collections, List as JList, Map as JMap}

/**
 * A CookieHandler object provides a callback mechanism to hook up a HTTP state management policy
 * implementation into the HTTP protocol handler. The HTTP state management mechanism specifies a
 * way to create a stateful session with HTTP requests and responses.
 *
 * A system-wide CookieHandler to be used by the HTTP URL stream protocol handler can be registered
 * by doing a CookieHandler.setDefault(CookieHandler). The currently registered CookieHandler can be
 * retrieved by calling CookieHandler.getDefault().
 *
 * @since 1.5
 */
abstract class CookieHandler:

  /**
   * Gets all the applicable cookies from a cookie cache for the specified uri in the request
   * header.
   *
   * The URI passed as an argument specifies the intended use for the cookies and in particular the
   * scheme (http, https) should reflect whether the cookies will be sent over a secure channel.
   *
   * The requestHeaders is a Map from request header field names to lists of field values
   * representing the current request headers.
   *
   * @param uri
   *   a URI representing the intended use for the cookies
   * @param requestHeaders
   *   a Map from request header field names to lists of field values
   * @return
   *   an immutable map from state management headers, with field names "Cookie" or "Cookie2" to a
   *   list of cookies containing state information
   */
  def get(uri: URI, requestHeaders: JMap[String, JList[String]]): JMap[String, JList[String]]

  /**
   * Sets all the applicable cookies, examples are response header fields that are named Set-Cookie2,
   * present in the response headers into a cookie cache.
   *
   * @param uri
   *   a URI where the cookies come from
   * @param responseHeaders
   *   an immutable map from field names to lists of field values representing the response header
   *   fields returned
   */
  def put(uri: URI, responseHeaders: JMap[String, JList[String]]): Unit

object CookieHandler:
  @volatile private var defaultHandler: CookieHandler | Null = null

  /**
   * Gets the system-wide cookie handler.
   *
   * @return
   *   the system-wide cookie handler; A null return means there is no system-wide cookie handler
   *   currently set.
   */
  def getDefault(): CookieHandler | Null = defaultHandler

  /**
   * Sets (or unsets) the system-wide cookie handler.
   *
   * Note: non-standard CookieHandler implementations may not work correctly with certain security
   * manager implementations.
   *
   * @param cHandler
   *   The HTTP cookie handler, or null to unset.
   */
  def setDefault(cHandler: CookieHandler | Null): Unit =
    defaultHandler = cHandler
