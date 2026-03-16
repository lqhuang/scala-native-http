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
import snhttp.jdk.net.InMemoryCookieStore

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
class CookieManager(store: CookieStore, policy: CookiePolicy) extends CookieHandler:

  private val cookieStore: CookieStore =
    if store != null then store else new InMemoryCookieStore()

  private var cookiePolicy: CookiePolicy =
    if policy != null then policy else CookiePolicy.ACCEPT_ORIGINAL_SERVER

  def this() = this(null, null)

  def setCookiePolicy(cookiePolicy: CookiePolicy): Unit =
    if cookiePolicy != null then this.cookiePolicy = cookiePolicy

  def getCookieStore(): CookieStore = cookieStore

  override def get(uri: URI, requestHeaders: JMap[String, JList[String]]): JMap[String, JList[String]] =
    Objects.requireNonNull(uri, "uri is null")
    Objects.requireNonNull(requestHeaders, "requestHeaders is null")

    val cookies = cookieStore.get(uri)
    val result = new HashMap[String, JList[String]]()

    // Build the Cookie header value
    if cookies != null && !cookies.isEmpty then
      val cookiePairs = new ArrayList[String]()

      val it = cookies.iterator()
      while it.hasNext do
        val cookie = it.next()
        if !cookie.hasExpired() then
          val scheme = uri.getScheme()
          val isSecure = scheme != null && scheme.equalsIgnoreCase("https")
          if !cookie.getSecure() || isSecure then
            val value = cookie.getValue()
            val pair =
              if value != null then cookie.getName() + "=" + value
              else cookie.getName()
            cookiePairs.add(pair): Unit

      if !cookiePairs.isEmpty then
        val headerValue = String.join("; ", cookiePairs)
        val cookieHeader = new ArrayList[String]()
        cookieHeader.add(headerValue)
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
                  if cookie.getPath() == null || cookie.getPath().isEmpty then
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
