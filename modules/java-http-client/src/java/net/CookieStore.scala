package java.net

import java.util.{List as JList}

/**
 * A CookieStore object represents a storage for cookie. Can store and retrieve cookies.
 *
 * CookieManager will call CookieStore.add to save cookies for every incoming HTTP response, and
 * call CookieStore.get to retrieve cookie for every outgoing HTTP request. A CookieStore is
 * responsible for removing HttpCookie instances which have expired.
 *
 * @since 1.6
 */
trait CookieStore:
  /**
   * Adds one HTTP cookie to the store. This is called for every incoming HTTP response.
   *
   * A cookie to store may or may not be associated with a URI. If it is not associated with a URI,
   * the cookie's domain and path attribute will indicate where it comes from. If it is associated
   * with a URI and its domain and path attribute are not specified, given URI will indicate where
   * this cookie comes from.
   *
   * If a cookie corresponding to the given URI already exists, then it is replaced with the new
   * one.
   *
   * @param uri
   *   the uri this cookie associated with. if null, this cookie will not be associated with an
   *   URI
   * @param cookie
   *   the cookie to store
   * @throws NullPointerException
   *   if cookie is null
   */
  def add(uri: URI | Null, cookie: HttpCookie): Unit

  /**
   * Retrieve cookies associated with given URI, or whose domain matches the given URI. Only
   * cookies that have not expired are returned. This is called for every outgoing HTTP request.
   *
   * @param uri
   *   the uri associated with the cookies to be returned
   * @return
   *   an immutable list of HttpCookie, return empty list if no cookies match the given URI
   * @throws NullPointerException
   *   if uri is null
   */
  def get(uri: URI): JList[HttpCookie]

  /**
   * Get all not-expired cookies in cookie store.
   *
   * @return
   *   an immutable list of http cookies; return empty list if there's no http cookie in store
   */
  def getCookies(): JList[HttpCookie]

  /**
   * Get all URIs which identify the cookies in this cookie store.
   *
   * @return
   *   an immutable list of URIs; return empty list if no cookie in this cookie store is
   *   associated with a URI
   */
  def getURIs(): JList[URI]

  /**
   * Remove a cookie from store.
   *
   * @param uri
   *   the uri this cookie associated with. if null, the cookie to be removed is not associated
   *   with an URI when added; if not null, the cookie to be removed is associated with the given
   *   URI when added.
   * @param cookie
   *   the cookie to remove
   * @return
   *   true if this store contained the specified cookie
   * @throws NullPointerException
   *   if cookie is null
   */
  def remove(uri: URI | Null, cookie: HttpCookie): Boolean

  /**
   * Remove all cookies in this cookie store.
   *
   * @return
   *   true if this store changed as a result of the call
   */
  def removeAll(): Boolean
