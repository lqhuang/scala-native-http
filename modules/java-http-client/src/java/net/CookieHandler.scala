package java.net

import java.util.{List as JList, Map as JMap}

// Refs:
// 1. https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/CookieHandler.html
//
// @since 1.5
abstract class CookieHandler:

  def get(uri: URI, requestHeaders: JMap[String, JList[String]]): JMap[String, JList[String]]

  def put(uri: URI, responseHeaders: JMap[String, JList[String]]): Unit

object CookieHandler:
  @volatile private var defaultHandler: CookieHandler = null

  def getDefault(): CookieHandler = defaultHandler

  def setDefault(cHandler: CookieHandler): Unit =
    defaultHandler = cHandler
