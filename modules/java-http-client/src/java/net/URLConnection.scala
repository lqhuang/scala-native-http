package java.net

import java.io.{IOException, InputStream, OutputStream}
import java.security.Permission
import java.util.{List as JList, Map as JMap}

import scala.collection.mutable.Map

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URLConnection.html
abstract class URLConnection(url: URL) {

  def connect(): Unit

  def setConnectTimeout(timeout: Int): Unit

  def getConnectTimeout(): Int

  def setReadTimeout(timeout: Int): Unit

  def getReadTimeout(): Int

  def getURL(): URL

  def getContentLength(): Int

  def getContentLengthLong(): Long

  def getContentType(): String

  def getContentEncoding(): String

  def getExpiration(): Long = ???

  def getDate(): Long = ???

  def getLastModified(): Long = ???

  def getHeaderField(name: String): String = ???

  def getHeaderFields(): JMap[String, JList[String]] = ???

  def getHeaderFieldInt(name: String, default: Int): Int = ???

  def getHeaderFieldLong(name: String, default: Long): Long = ???

  def getHeaderFieldDate(name: String, default: Long): Long = ???

  def getHeaderFieldKey(n: Int): String = ???

  def getHeaderField(n: Int): String = ???

  def getContent(): Any

  def getContent(classes: Array[Class[_]]): Any

  @deprecated(since = "JDK 25")
  def getPermission(): Permission = throw new NotImplementedError("deprecated")

  def getInputStream(): InputStream

  def getOutputStream(): OutputStream

  override def toString(): String

  def setDoInput(doInput: Boolean): Unit

  def getDoInput(): Boolean

  def setDoOutput(doOutput: Boolean): Unit

  def getDoOutput(): Boolean

  def setAllowUserInteraction(allowUserInteraction: Boolean): Unit

  def getAllowUserInteraction(): Boolean

  def setUseCaches(useCaches: Boolean): Unit

  def getUseCaches(): Boolean

  def setIfModifiedSince(ifModifiedSince: Long): Unit = ???

  def getIfModifiedSince(): Long = ???

  def getDefaultUseCaches(): Boolean

  def setDefaultUseCaches(defaultVal: Boolean): Unit

  def setRequestProperty(key: String, value: String): Unit

  def addRequestProperty(key: String, value: String): Unit

  def getRequestProperty(key: String): String

  def getRequestProperties(): JMap[String, JList[String]]
}

object URLConnection {

  @volatile private var allowUserInteraction: Boolean = true
  private val defaultUseCachesMap: Map[String, Boolean] = Map()

  def getFileNameMap(): FileNameMap = ???

  def setFileNameMap(map: FileNameMap): Unit = ???

  def setDefaultAllowUserInteraction(defaultAllowUserInteraction: Boolean): Unit =
    allowUserInteraction = defaultAllowUserInteraction

  /// This default is "sticky", being a part of the static state of all URLConnections.
  /// This flag applies to the next, and all following `URLConnections` that are created.
  def getDefaultAllowUserInteraction(): Boolean =
    allowUserInteraction

  def getDefaultUseCaches(protocol: String): Boolean =
    defaultUseCachesMap.getOrElse(protocol, false)

  // The protocol name is case insensitive
  def setDefaultUseCaches(protocol: String, defaultVal: Boolean): Unit =
    defaultUseCachesMap(protocol) = defaultVal

  def setContentHandlerFactory(factory: ContentHandlerFactory): Unit =
    throw new UnsupportedOperationException()

  def guessContentTypeFromName(fname: String): String = ???

  def guessContentTypeFromStream(is: InputStream): String = ???

  @deprecated()
  def setDefaultRequestProperty(key: String, value: String): Unit =
    throw new NotImplementedError("deprecated")

  @deprecated()
  def getDefaultRequestProperty(key: String): String =
    throw new NotImplementedError("deprecated")
}
