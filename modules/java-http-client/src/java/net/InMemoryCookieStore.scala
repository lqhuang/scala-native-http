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
import java.util.concurrent.locks.ReentrantLock

// Refs:
// 1. https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/CookieStore.html (Interface)
//
// @since 1.6
private[net] class InMemoryCookieStore extends CookieStore:

  // Map from URI to list of cookies associated with that URI
  private val uriIndex: JMap[URI, JList[HttpCookie]] = new HashMap()

  // Lock for thread safety
  private val lock = new ReentrantLock()

  override def add(uri: URI, cookie: HttpCookie): Unit =
    Objects.requireNonNull(cookie, "cookie is null")
    lock.lock()
    try
      // Remove expired cookies first
      removeExpired()

      // Determine the effective URI for indexing
      val effectiveUri = getEffectiveURI(uri)

      // Remove any existing cookie with the same name, domain, and path
      val existingIt = uriIndex.values().iterator()
      while existingIt.hasNext do
        val existing = existingIt.next()
        existing.removeIf(c => cookie.equals(c)): Unit

      // Don't add cookies that are already expired
      if cookie.hasExpired() then return

      // Add the cookie
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
        val indexedUri = entry.getKey()
        val cookies = entry.getValue()

        val cookieIt = cookies.iterator()
        while cookieIt.hasNext do
          val cookie = cookieIt.next()
          if !result.contains(cookie) && matchesCookie(uri, indexedUri, cookie) then result.add(cookie): Unit

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
      while it.hasNext do
        val next = it.next()
        if next != null then result.add(next): Unit
      result
    finally lock.unlock()

  override def remove(uri: URI, cookie: HttpCookie): Boolean =
    Objects.requireNonNull(cookie, "cookie is null")
    lock.lock()
    try
      var removed = false
      val it = uriIndex.values().iterator()
      while it.hasNext do
        val list = it.next()
        if list.remove(cookie) then removed = true
      removed
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
    val uriIt = uriIndex.entrySet().iterator()
    while uriIt.hasNext do
      val entry = uriIt.next()
      val cookies = entry.getValue()
      cookies.removeIf(c => c.hasExpired()): Unit
      if cookies.isEmpty then uriIt.remove(): Unit

  /**
   * Get an effective URI for storing a cookie. Normalizes the URI to just scheme + host.
   */
  private def getEffectiveURI(uri: URI): URI =
    if uri != null then getEffectiveURIForRetrieval(uri)
    else null

  /**
   * Normalize URI for cookie storage/retrieval (scheme + host only).
   */
  private def getEffectiveURIForRetrieval(uri: URI): URI =
    try
      val scheme = if uri.getScheme() != null then uri.getScheme().toLowerCase(Locale.ROOT) else "http"
      new URI(scheme, extractHost(uri), "/", null)
    catch case _: Exception => uri

  /**
   * Check if a cookie matches a given URI based on domain and path.
   */
  private def matchesCookie(uri: URI, indexedUri: URI, cookie: HttpCookie): Boolean = {
    val host = extractHost(uri)
    if host == null then return false

    // Check domain
    val domain = cookie.getDomain()
    val domainMatch =
      if domain == null then
        val indexedHost = if indexedUri != null then extractHost(indexedUri) else null
        indexedHost != null && host.equalsIgnoreCase(indexedHost)
      else HttpCookie.domainMatches(domain, host) || domain.equalsIgnoreCase(host)

    if !domainMatch then return false

    // Check secure
    if cookie.getSecure() then
      val scheme = uri.getScheme()
      if scheme == null || !scheme.equalsIgnoreCase("https") then return false

    true
  }

  private def extractHost(uri: URI): String =
    val direct = uri.getHost()
    if direct != null then return normalizeHost(direct)

    val rawAuthority = uri.getRawAuthority()
    val authority =
      if rawAuthority != null && !rawAuthority.isEmpty then rawAuthority
      else
        val plainAuthority = uri.getAuthority()
        if plainAuthority != null && !plainAuthority.isEmpty then plainAuthority
        else parseAuthorityFromUriString(uri.toString())
    if authority == null || authority.isEmpty then return null

    val at = authority.lastIndexOf('@')
    val hostPort =
      if at >= 0 && at + 1 < authority.length then authority.substring(at + 1)
      else authority

    if hostPort.startsWith("[") then
      val end = hostPort.indexOf(']')
      if end > 0 then normalizeHost(hostPort.substring(0, end + 1))
      else hostPort
    else
      val colon = hostPort.lastIndexOf(':')
      val firstColon = hostPort.indexOf(':')
      val host =
        if colon > 0 && colon == firstColon then hostPort.substring(0, colon)
        else hostPort
      normalizeHost(host)

  private def normalizeHost(host: String): String =
    if host == null || host.isEmpty then host
    else if host.indexOf(':') >= 0 && !(host.startsWith("[") && host.endsWith("]")) then
      "[" + host + "]"
    else host

  private def parseAuthorityFromUriString(uriString: String): String =
    if uriString == null then null
    else
      val schemeSep = uriString.indexOf("://")
      if schemeSep < 0 then null
      else
        val start = schemeSep + 3
        if start >= uriString.length then null
        else
          val slash = uriString.indexOf('/', start)
          val end = if slash >= 0 then slash else uriString.length
          if end <= start then null
          else uriString.substring(start, end)
