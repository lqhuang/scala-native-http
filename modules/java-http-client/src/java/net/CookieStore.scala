package java.net

import java.util.{List as JList}

// Refs:
// 1. https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/CookieStore.html
//
// @since 1.6
trait CookieStore:
  
  def add(uri: URI, cookie: HttpCookie): Unit

  def get(uri: URI): JList[HttpCookie]

  def getCookies(): JList[HttpCookie]

  def getURIs(): JList[URI]

  def remove(uri: URI, cookie: HttpCookie): Boolean

  def removeAll(): Boolean
