package java.net

import java.util.{
  ArrayList,
  Collections,
  HashMap,
  List as JList,
  Map as JMap,
}

import snhttp.jdk.net.InMemoryCookieStore

// @since 1.6
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
    if uri == null || requestHeaders == null then
      throw new IllegalArgumentException("uri and requestHeaders must not be null")

    val cookies = cookieStore.get(uri)
    val result = new HashMap[String, JList[String]]()
    val matchedCookies = new ArrayList[HttpCookie]()
    val cookieHeader = new ArrayList[String]()
    val scheme = uri.getScheme()
    val isSecure = scheme != null && scheme.equalsIgnoreCase("https")
    val requestPath =
      val path = uri.getPath()
      if path == null || path.isEmpty then "/" else path

    if cookies != null && !cookies.isEmpty then
      cookies.forEach { cookie =>
        if !cookie.hasExpired() then
          val cookiePath = cookie.getPath()
          val pathMatches = cookiePath != null && matchesRequestPath(requestPath, cookiePath)
          if pathMatches && (!cookie.getSecure() || isSecure) then matchedCookies.add(cookie): Unit
      }

    Collections.sort(matchedCookies, (left: HttpCookie, right: HttpCookie) =>
      compareCookiePath(left, right)
    )

    matchedCookies.forEach { cookie =>
      val value = cookie.getValue()
      val pair =
        if value != null then cookie.getName() + "=" + value
        else cookie.getName()
      cookieHeader.add(pair): Unit
    }

    result.put("Cookie", Collections.unmodifiableList(cookieHeader)): Unit
    Collections.unmodifiableMap(result)

  override def put(uri: URI, responseHeaders: JMap[String, JList[String]]): Unit =
    if uri == null || responseHeaders == null then
      throw new IllegalArgumentException("uri and responseHeaders must not be null")

    responseHeaders.forEach((headerName: String, headerValues: JList[String]) =>
      if headerName != null &&
        (headerName.equalsIgnoreCase("Set-Cookie") || headerName.equalsIgnoreCase("Set-Cookie2")) &&
        headerValues != null
      then
        headerValues.forEach((headerValue: String) =>
          if headerValue != null && !headerValue.isEmpty then
            try
              val cookies = HttpCookie.parse(headerValue)
              cookies.forEach((cookie: HttpCookie) =>
                if cookie.getDomain() == null then
                  cookie.setDomain(uri.getHost())

                if cookie.getPath() == null || cookie.getPath().isEmpty then
                  val path = uri.getPath()
                  val defaultPath =
                    if path == null || path.isEmpty then "/"
                    else
                      val lastSlash = path.lastIndexOf('/')
                      if lastSlash <= 0 then "/"
                      else path.substring(0, lastSlash + 1)
                  cookie.setPath(defaultPath)

                if cookiePolicy.shouldAccept(uri, cookie) then cookieStore.add(uri, cookie)
              )
            catch case _: IllegalArgumentException => ()
        )
    )

  private def compareCookiePath(left: HttpCookie, right: HttpCookie): Int =
    val leftPath = left.getPath()
    val rightPath = right.getPath()
    if leftPath == rightPath then 0
    else if leftPath != null && rightPath != null then
      if leftPath.startsWith(rightPath) then -1
      else if rightPath.startsWith(leftPath) then 1
      else 0
    else 0

  private def matchesRequestPath(requestPath: String, cookiePath: String): Boolean =
    if requestPath == cookiePath then true
    else if requestPath.startsWith(cookiePath) then
      if cookiePath.endsWith("/") then true
      else requestPath.length > cookiePath.length && requestPath.charAt(cookiePath.length) == '/'
    else false
