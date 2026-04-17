package snhttp.jdk.net

import java.net.{CookieStore, HttpCookie, URI}
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

final class InMemoryCookieStore extends CookieStore:

  private val uriIndex: JMap[URI, JList[HttpCookie]] = new HashMap()

  private val lock = new ReentrantLock()

  override def add(uri: URI, cookie: HttpCookie): Unit =
    Objects.requireNonNull(cookie, "cookie is null")
    lock.lock()
    try
      removeExpired()

      val effectiveUri = getEffectiveURI(uri)

      uriIndex.values().forEach((existing: JList[HttpCookie]) => existing.removeIf(c => cookie.equals(c)): Unit)

      if cookie.hasExpired() then return

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

      uriIndex.forEach((indexedUri: URI, cookies: JList[HttpCookie]) =>
        cookies.forEach((cookie: HttpCookie) =>
          if !result.contains(cookie) && matchesCookie(uri, indexedUri, cookie) then result.add(cookie): Unit
        )
      )

      Collections.unmodifiableList(result)
    finally lock.unlock()

  override def getCookies(): JList[HttpCookie] =
    lock.lock()
    try
      removeExpired()
      val result = new ArrayList[HttpCookie]()
      uriIndex.values().forEach((cookies: JList[HttpCookie]) =>
        cookies.forEach((cookie: HttpCookie) =>
          if !result.contains(cookie) then result.add(cookie): Unit
        )
      )
      Collections.unmodifiableList(result)
    finally lock.unlock()

  override def getURIs(): JList[URI] =
    lock.lock()
    try
      val result = new ArrayList[URI]()
      uriIndex.keySet().forEach((next: URI) =>
        if next != null then result.add(next): Unit
      )
      result
    finally lock.unlock()

  override def remove(uri: URI, cookie: HttpCookie): Boolean =
    Objects.requireNonNull(cookie, "cookie is null")
    lock.lock()
    try
      var removed = false
      uriIndex.values().forEach((list: JList[HttpCookie]) =>
        if list.remove(cookie) then removed = true
      )
      removed
    finally lock.unlock()

  override def removeAll(): Boolean =
    lock.lock()
    try
      val wasEmpty = uriIndex.isEmpty
      uriIndex.clear()
      !wasEmpty
    finally lock.unlock()

  private def removeExpired(): Unit =
    uriIndex.entrySet().removeIf((entry: java.util.Map.Entry[URI, JList[HttpCookie]]) =>
      val cookies = entry.getValue()
      cookies.removeIf(c => c.hasExpired()): Unit
      cookies.isEmpty
    ): Unit

  private def getEffectiveURI(uri: URI): URI =
    if uri != null then getEffectiveURIForRetrieval(uri)
    else null

  private def getEffectiveURIForRetrieval(uri: URI): URI =
    try
      val scheme = if uri.getScheme() != null then uri.getScheme().toLowerCase(Locale.ROOT) else "http"
      new URI(scheme, extractHost(uri), "/", null)
    catch case _: Exception => uri

  private def matchesCookie(uri: URI, indexedUri: URI, cookie: HttpCookie): Boolean = {
    val host = extractHost(uri)
    if host == null then return false

    val indexedHost = if indexedUri != null then extractHost(indexedUri) else null
    val domain = cookie.getDomain()
    val domainMatch =
      if domain == null then
        indexedHost != null && host.equalsIgnoreCase(indexedHost)
      else
        HttpCookie.domainMatches(domain, host) ||
          domain.equalsIgnoreCase(host) ||
          (indexedHost != null && host.equalsIgnoreCase(indexedHost))

    if !domainMatch then return false

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
