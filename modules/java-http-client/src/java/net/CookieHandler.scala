package java.net

import java.net.URI
import java.util.{Map as JMap, List as JList}

/**
 * Reference:
 *
 *   - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/CookieHandler.html
 */
abstract class CookieHandler:

  def get(uri: URI, requestHeaders: JMap[String, JList[String]]): JMap[String, JList[String]]

  def put(uri: URI, responseHeaders: JMap[String, JList[String]]): Unit

object CookieHandler:

  def getDefault(): CookieHandler = ???

  def setDefault(handler: CookieHandler): Unit = ???

end CookieHandler
