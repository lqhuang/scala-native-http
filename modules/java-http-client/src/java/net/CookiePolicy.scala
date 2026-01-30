package java.net

import java.util.{List as JList}

/**
 * CookiePolicy implementations decide which cookies should be accepted and which should be
 * rejected. Three pre-defined policy implementations are provided, namely ACCEPT_ALL, ACCEPT_NONE
 * and ACCEPT_ORIGINAL_SERVER.
 *
 * @since 1.6
 */
trait CookiePolicy:
  /**
   * Will be called to see whether or not this cookie should be accepted.
   *
   * @param uri
   *   the URI to consult accept policy with
   * @param cookie
   *   the HttpCookie object in question
   * @return
   *   true if this cookie should be accepted; otherwise, false
   */
  def shouldAccept(uri: URI, cookie: HttpCookie): Boolean

object CookiePolicy:
  /**
   * One pre-defined policy which accepts all cookies.
   */
  val ACCEPT_ALL: CookiePolicy = new CookiePolicy:
    override def shouldAccept(uri: URI, cookie: HttpCookie): Boolean = true

  /**
   * One pre-defined policy which accepts no cookies.
   */
  val ACCEPT_NONE: CookiePolicy = new CookiePolicy:
    override def shouldAccept(uri: URI, cookie: HttpCookie): Boolean = false

  /**
   * One pre-defined policy which only accepts cookies from the original server.
   *
   * The cookie's domain must domain-match the host of the request URI, or the cookie must not have
   * a domain attribute (implying it came from the original server).
   */
  val ACCEPT_ORIGINAL_SERVER: CookiePolicy = new CookiePolicy:
    override def shouldAccept(uri: URI, cookie: HttpCookie): Boolean =
      if uri == null || cookie == null then return false
      val host = uri.getHost()
      if host == null then return false
      val domain = cookie.getDomain()
      if domain == null then true
      else HttpCookie.domainMatches(domain, host)
