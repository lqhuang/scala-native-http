package java.net

import java.util.{List as JList}

// @since 1.6
trait CookieStore:
  
  def add(uri: URI, cookie: HttpCookie): Unit

  def get(uri: URI): JList[HttpCookie]

  def getCookies(): JList[HttpCookie]

  def getURIs(): JList[URI]

  def remove(uri: URI, cookie: HttpCookie): Boolean

  def removeAll(): Boolean
