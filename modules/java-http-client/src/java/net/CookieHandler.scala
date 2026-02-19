package java.net

import java.util.{Collections, List as JList, Map as JMap}

// @since 1.5
abstract class CookieHandler:

  def get(uri: URI, requestHeaders: JMap[String, JList[String]]): JMap[String, JList[String]]

  def put(uri: URI, responseHeaders: JMap[String, JList[String]]): Unit

object CookieHandler:
  @volatile private var defaultHandler: CookieHandler = null

  def getDefault(): CookieHandler = defaultHandler

  def setDefault(cHandler: CookieHandler): Unit =
    defaultHandler = cHandler
