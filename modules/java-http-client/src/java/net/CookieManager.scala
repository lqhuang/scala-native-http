package java.net

import java.util.{
  ArrayList,
  Collections,
  HashMap,
  List as JList,
  Locale,
  Map as JMap,
  Objects,
}

/**
 * CookieManager provides a concrete implementation of CookieHandler, which separates the storage of
 * cookies from the policy surrounding accepting and rejecting cookies. A CookieManager is
 * initialized with a CookieStore which manages storage, and a CookiePolicy object, which makes
 * policy decisions on cookie acceptance/rejection.
 *
 * The HTTP cookie management in java.net package looks like:
 *
 * {{{
 *                  use
 * CookieHandler <------- HttpURLConnection
 *       ^
 *       | impl
 *       |
 * CookieManager -------> CookiePolicy
 *       |                    use
 *       | use
 *       |
 *       V
 *    CookieStore
 * }}}
 *
 * @since 1.6
 */
class CookieManager(store: CookieStore | Null, policy: CookiePolicy | Null) extends CookieHandler:

  private val cookieStore: CookieStore =
    if store != null then store else new InMemoryCookieStore()

  private var cookiePolicy: CookiePolicy =
    if policy != null then policy else CookiePolicy.ACCEPT_ORIGINAL_SERVER

  /**
   * Create a new cookie manager with default cookie store and accept policy.
   */
  def this() = this(null, null)

  /**
   * To set the cookie policy of this cookie manager.
   *
   * A instance of CookieManager will have cookie policy ACCEPT_ORIGINAL_SERVER by default. Users
   * always can call this method to set another cookie policy.
   *
   * @param cookiePolicy
   *   the cookie policy. Can be null, in which case the current policy will not be changed.
   */
  def setCookiePolicy(cookiePolicy: CookiePolicy | Null): Unit =
    if cookiePolicy != null then this.cookiePolicy = cookiePolicy

  /**
   * To retrieve current cookie store.
   *
   * @return
   *   the cookie store currently used by cookie manager.
   */
  def getCookieStore(): CookieStore = cookieStore

  override def get(uri: URI, requestHeaders: JMap[String, JList[String]]): JMap[String, JList[String]] =
    Objects.requireNonNull(uri, "uri is null")
    Objects.requireNonNull(requestHeaders, "requestHeaders is null")

    val cookies = cookieStore.get(uri)
    val result = new HashMap[String, JList[String]]()

    // Build the Cookie header value
    if cookies != null && !cookies.isEmpty then
      val cookieHeader = new ArrayList[String]()
      val sb = new java.lang.StringBuilder()
      var first = true

      val it = cookies.iterator()
      while it.hasNext do
        val cookie = it.next()
        // Skip expired cookies
        if !cookie.hasExpired() then
          // Skip HttpOnly cookies for non-HTTP requests (though URI doesn't indicate this)
          // Skip Secure cookies for non-HTTPS
          val scheme = uri.getScheme()
          val isSecure = scheme != null && scheme.equalsIgnoreCase("https")
          if cookie.getSecure() && !isSecure then
            // Skip secure cookies for non-secure requests
            ()
          else
            if !first then sb.append("; "): Unit
            first = false
            sb.append(cookie.getName())
            val value = cookie.getValue()
            if value != null then sb.append("=").append(value): Unit

      if sb.length > 0 then
        cookieHeader.add(sb.toString())
        result.put("Cookie", Collections.unmodifiableList(cookieHeader)): Unit

    Collections.unmodifiableMap(result)

  override def put(uri: URI, responseHeaders: JMap[String, JList[String]]): Unit =
    Objects.requireNonNull(uri, "uri is null")
    Objects.requireNonNull(responseHeaders, "responseHeaders is null")

    // Process Set-Cookie and Set-Cookie2 headers
    val it = responseHeaders.entrySet().iterator()
    while it.hasNext do
      val entry = it.next()
      val headerName = entry.getKey()
      if headerName != null &&
        (headerName.equalsIgnoreCase("Set-Cookie") || headerName.equalsIgnoreCase("Set-Cookie2"))
      then
        val headerValues = entry.getValue()
        if headerValues != null then
          val valueIt = headerValues.iterator()
          while valueIt.hasNext do
            val headerValue = valueIt.next()
            if headerValue != null && !headerValue.isEmpty then
              try
                val cookies = HttpCookie.parse(headerValue)
                val cookieIt = cookies.iterator()
                while cookieIt.hasNext do
                  val cookie = cookieIt.next()

                  // Apply default domain if not set
                  if cookie.getDomain() == null then cookie.setDomain(uri.getHost())

                  // Apply default path if not set
                  if cookie.getPath() == null then
                    val path = uri.getPath()
                    val defaultPath =
                      if path == null || path.isEmpty then "/"
                      else
                        val lastSlash = path.lastIndexOf('/')
                        if lastSlash <= 0 then "/"
                        else path.substring(0, lastSlash)
                    cookie.setPath(defaultPath)

                  // Check policy
                  if cookiePolicy.shouldAccept(uri, cookie) then cookieStore.add(uri, cookie)
              catch case _: IllegalArgumentException => () // Ignore malformed cookies
