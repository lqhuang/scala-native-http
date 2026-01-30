package snhttp.jdk.net

import java.net.{CookieStore, HttpCookie, URI}
import java.util.{
  ArrayList,
  Collections,
  HashMap,
  Iterator as JIterator,
  List as JList,
  Locale,
  Map as JMap,
  Objects,
}
import java.util.concurrent.locks.ReentrantLock

/**
 * An in-memory implementation of CookieStore.
 *
 * Thread-safe: uses explicit locking for all operations.
 *
 * @since 1.6
 */
class InMemoryCookieStore extends CookieStore:

  // Map from URI to list of cookies associated with that URI
  private val uriIndex: JMap[URI, JList[HttpCookie]] = new HashMap()

  // Lock for thread safety
  private val lock = new ReentrantLock()

  override def add(uri: URI | Null, cookie: HttpCookie): Unit =
    Objects.requireNonNull(cookie, "cookie is null")
    lock.lock()
    try
      // Remove expired cookies first
      removeExpired()

      // Determine the effective URI for indexing
      val effectiveUri = getEffectiveURI(uri, cookie)

      // Remove any existing cookie with the same name, domain, and path
      if effectiveUri != null then
        val existing = uriIndex.get(effectiveUri)
        if existing != null then existing.removeIf(c => cookie.equals(c)): Unit

      // Don't add cookies that are already expired
      if cookie.hasExpired() then return

      // Add the cookie
      if effectiveUri != null then
        var list = uriIndex.get(effectiveUri)
        if list == null then
          list = new ArrayList[HttpCookie]()
          uriIndex.put(effectiveUri, list): Unit
        list.add(cookie): Unit
    finally lock.unlock()

  override def get(uri: URI): JList[HttpCookie] =
    Objects.requireNonNull(uri, "uri is null")
    lock.lock()
    try
      removeExpired()
      val result = new ArrayList[HttpCookie]()

      // Check all stored cookies for domain and path matching
      val it = uriIndex.entrySet().iterator()
      while it.hasNext do
        val entry = it.next()
        val cookies = entry.getValue()

        val cookieIt = cookies.iterator()
        while cookieIt.hasNext do
          val cookie = cookieIt.next()
          if !result.contains(cookie) && matchesCookie(uri, cookie) then result.add(cookie): Unit

      Collections.unmodifiableList(result)
    finally lock.unlock()

  override def getCookies(): JList[HttpCookie] =
    lock.lock()
    try
      removeExpired()
      val result = new ArrayList[HttpCookie]()
      val it = uriIndex.values().iterator()
      while it.hasNext do
        val cookies = it.next()
        val cookieIt = cookies.iterator()
        while cookieIt.hasNext do
          val cookie = cookieIt.next()
          if !result.contains(cookie) then result.add(cookie): Unit
      Collections.unmodifiableList(result)
    finally lock.unlock()

  override def getURIs(): JList[URI] =
    lock.lock()
    try
      val result = new ArrayList[URI]()
      val it = uriIndex.keySet().iterator()
      while it.hasNext do result.add(it.next()): Unit
      Collections.unmodifiableList(result)
    finally lock.unlock()

  override def remove(uri: URI | Null, cookie: HttpCookie): Boolean =
    Objects.requireNonNull(cookie, "cookie is null")
    lock.lock()
    try
      val effectiveUri = getEffectiveURI(uri, cookie)
      if effectiveUri == null then return false
      val list = uriIndex.get(effectiveUri)
      if list != null then list.remove(cookie)
      else false
    finally lock.unlock()

  override def removeAll(): Boolean =
    lock.lock()
    try
      val wasEmpty = uriIndex.isEmpty
      uriIndex.clear()
      !wasEmpty
    finally lock.unlock()

  /** Remove all expired cookies from the store. */
  private def removeExpired(): Unit =
    val uriIt = uriIndex.values().iterator()
    while uriIt.hasNext do
      val cookies = uriIt.next()
      cookies.removeIf(c => c.hasExpired()): Unit

  /**
   * Get an effective URI for storing a cookie. Normalizes the URI to just scheme + host.
   */
  private def getEffectiveURI(uri: URI | Null, cookie: HttpCookie): URI | Null =
    if uri != null then getEffectiveURIForRetrieval(uri)
    else
      // Use cookie's domain if URI is null
      val domain = cookie.getDomain()
      if domain != null then
        try new URI("http", domain, "/", null)
        catch case _: Exception => null
      else null

  /**
   * Normalize URI for cookie storage/retrieval (scheme + host only).
   */
  private def getEffectiveURIForRetrieval(uri: URI): URI =
    try
      val scheme = if uri.getScheme() != null then uri.getScheme().toLowerCase(Locale.ROOT) else "http"
      new URI(scheme, uri.getHost(), "/", null)
    catch case _: Exception => uri

  /**
   * Check if a cookie matches a given URI based on domain and path.
   */
  private def matchesCookie(uri: URI, cookie: HttpCookie): Boolean =
    val host = uri.getHost()
    if host == null then return false

    // Check domain
    val domain = cookie.getDomain()
    val domainMatch =
      if domain == null then true
      else HttpCookie.domainMatches(domain, host)

    if !domainMatch then return false

    // Check path
    val path = uri.getPath()
    val cookiePath = cookie.getPath()
    val pathMatch =
      if cookiePath == null || path == null then true
      else pathMatches(path, cookiePath)

    if !pathMatch then return false

    // Check secure
    if cookie.getSecure() then
      val scheme = uri.getScheme()
      if scheme == null || !scheme.equalsIgnoreCase("https") then return false

    true

  /**
   * Check if the request path matches the cookie path.
   *
   * Per RFC 6265:
   * - The cookie-path is a prefix of the request-path, and
   * - Either the cookie-path equals the request-path, or
   * - The cookie-path ends with "/", or
   * - The first character of the request-path that is not included in the cookie-path is a "/"
   */
  private def pathMatches(requestPath: String, cookiePath: String): Boolean =
    if requestPath == cookiePath then true
    else if requestPath.startsWith(cookiePath) then
      if cookiePath.endsWith("/") then true
      else if requestPath.length > cookiePath.length && requestPath.charAt(cookiePath.length) == '/' then true
      else false
    else false
