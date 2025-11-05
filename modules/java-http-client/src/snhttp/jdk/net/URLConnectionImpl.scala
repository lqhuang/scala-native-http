package snhttp.jdk.net

import java.io.{IOException, InputStream, OutputStream}
import java.net.{URL, URLConnection, ContentHandlerFactory, FileNameMap}
import java.util.{List as JList, Map as JMap}

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URLConnection.html
class URLConnectionImpl protected (private val url: URL) extends URLConnection(url) {

  def getURL(): URL = url

  def getContentLength(): Int = ???

  def getContentLengthLong(): Long = ???

  def getContentType(): String = ???

  def getContentEncoding(): String = ???

  def getExpiration(): Long = ???

  def getDate(): Long = ???

  def getLastModified(): Long = ???

  def getInputStream(): InputStream = ???

  def getOutputStream(): OutputStream = ???

  def getHeaderField(name: String): String = ???

  def getHeaderFieldKey(n: Int): String = ???

  def getHeaderField(n: Int): String = ???

  def getHeaderFieldInt(name: String, default: Int): Int = ???

  def getHeaderFieldLong(name: String, default: Long): Long = ???

  def getHeaderFieldDate(name: String, default: Long): Long = ???

  def getHeaderFields(): JMap[String, JList[String]] = ???

  def getRequestProperties(): JMap[String, JList[String]] = ???

  def getRequestProperty(key: String): String = ???

  def setRequestProperty(key: String, value: String): Unit = ???

  def addRequestProperty(key: String, value: String): Unit = ???

  def getDoInput(): Boolean = ???

  def setDoInput(doInput: Boolean): Unit = ???

  def getDoOutput(): Boolean = ???

  def setDoOutput(doOutput: Boolean): Unit = ???

  def getAllowUserInteraction(): Boolean = ???

  def setAllowUserInteraction(allowUserInteraction: Boolean): Unit = ???

  def getUseCaches(): Boolean = ???

  def setUseCaches(useCaches: Boolean): Unit = ???

  def getIfModifiedSince(): Long = ???

  def setIfModifiedSince(ifModifiedSince: Long): Unit = ???

  def getDefaultUseCaches(): Boolean = ???

  def getDefaultUseCaches(protocol: String): Boolean = ???

  def setDefaultUseCaches(defaultUseCaches: Boolean): Unit = ???

  def setDefaultUseCaches(protocol: String, defaultVal: Boolean): Unit = ???

  def getConnectTimeout(): Int = ???

  def setConnectTimeout(timeout: Int): Unit = ???

  def getReadTimeout(): Int = ???

  def setReadTimeout(timeout: Int): Unit = ???

  def connect(): Unit = ???

  def getContent(): AnyRef = ???

  def getContent(classes: Array[Class[_]]): AnyRef = ???

  override def toString(): String = ???
}
