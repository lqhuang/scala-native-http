package java.net

// @since 1.6
trait CookiePolicy:
  def shouldAccept(uri: URI, cookie: HttpCookie): Boolean

object CookiePolicy:
  val ACCEPT_ALL: CookiePolicy = new CookiePolicy:
    override def shouldAccept(uri: URI, cookie: HttpCookie): Boolean = true

  val ACCEPT_NONE: CookiePolicy = new CookiePolicy:
    override def shouldAccept(uri: URI, cookie: HttpCookie): Boolean = false

  val ACCEPT_ORIGINAL_SERVER: CookiePolicy = new CookiePolicy:
    override def shouldAccept(uri: URI, cookie: HttpCookie): Boolean =
      if uri == null || cookie == null then return false
      val host = uri.getHost()
      if host == null then return false
      val domain = cookie.getDomain()
      if domain == null || domain.isEmpty then false
      else HttpCookie.domainMatches(domain, host)
