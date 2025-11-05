package snhttp.jdk.net

import java.io.IOException
import java.io.{InputStream, OutputStream}
import java.net.{HttpURLConnection, URL, Authenticator}
import java.net.URLConnection
import java.util.{List as JList, Map as JMap}

class HttpURLConnectionImpl protected (private val url: URL, private val urlConn: URLConnection)
    extends HttpURLConnection(url) {

  def this(url: URL) = this(url, null)

  def setAuthenticator(auth: Authenticator): Unit = ???

  def getHeaderFieldKey(n: Int): String = ???

  def setFixedLengthStreamingMode(contentLength: Int): Unit = ???

  def setFixedLengthStreamingMode(contentLength: Long): Unit = ???

  def setChunkedStreamingMode(chunkLength: Int): Unit = ???

  def getHeaderField(n: Int): String = ???

  def setInstanceFollowRedirects(followRedirects: Boolean): Unit = ???

  def getInstanceFollowRedirects(): Boolean = ???

  def setRequestMethod(method: String): Unit = ???

  def getRequestMethod(): String = ???

  def getResponseCode(): Int = ???

  def getResponseMessage(): String = ???

  def disconnect(): Unit = ???

  def usingProxy(): Boolean = ???

  // @deprecated // Since JDK 25
  // def getPermission(): Permission = ???

  def getErrorStream(): InputStream = ???

  override def toString(): String = ???

  /**
   * From URLConnection
   */

  def connect(): Unit = urlConn.connect()

  def setConnectTimeout(timeout: Int): Unit = urlConn.setConnectTimeout(timeout)

  def getConnectTimeout(): Int = urlConn.getConnectTimeout()

  def setReadTimeout(timeout: Int): Unit = urlConn.setReadTimeout(timeout)

  def getReadTimeout(): Int = urlConn.getReadTimeout()

  def getURL(): URL = urlConn.getURL()

  def getContentLength(): Int = urlConn.getContentLength()

  def getContentLengthLong(): Long = urlConn.getContentLengthLong()

  def getContentType(): String = urlConn.getContentType()

  def getContentEncoding(): String = urlConn.getContentEncoding()

  def getExpiration(): Long = urlConn.getExpiration()

  def getDate(): Long = urlConn.getDate()

  def getLastModified(): Long = urlConn.getLastModified()

  def getHeaderField(name: String): String = urlConn.getHeaderField(name)

  def getHeaderFields(): JMap[String, JList[String]] = urlConn.getHeaderFields()

  def getHeaderFieldInt(name: String, default: Int): Int = urlConn.getHeaderFieldInt(name, default)

  def getHeaderFieldLong(name: String, default: Long): Long =
    urlConn.getHeaderFieldLong(name, default)

  def getHeaderFieldDate(name: String, default: Long): Long =
    urlConn.getHeaderFieldDate(name, default)

  // def getHeaderFieldKey(n: Int): String = urlConn.getHeaderFieldKey(n)

  // def getHeaderField(n: Int): String = urlConn.getHeaderField(n)

  def getContent(): AnyRef = urlConn.getContent()

  def getContent(classes: Array[Class[_]]): AnyRef = urlConn.getContent(classes)

  // def getPermission(): Permission

  def getInputStream(): InputStream =
    urlConn.getInputStream()

  def getOutputStream(): OutputStream =
    urlConn.getOutputStream()

  def setDoInput(doInput: Boolean): Unit =
    urlConn.setDoInput(doInput)

  def getDoInput(): Boolean =
    urlConn.getDoInput()

  def setDoOutput(doOutput: Boolean): Unit =
    urlConn.setDoOutput(doOutput)

  def getDoOutput(): Boolean =
    urlConn.getDoOutput()

  def setAllowUserInteraction(allowUserInteraction: Boolean): Unit =
    urlConn.setAllowUserInteraction(allowUserInteraction)

  def getAllowUserInteraction(): Boolean =
    urlConn.getAllowUserInteraction()

  def setUseCaches(useCaches: Boolean): Unit =
    urlConn.setUseCaches(useCaches)

  def getUseCaches(): Boolean =
    urlConn.getUseCaches()

  def setIfModifiedSince(ifModifiedSince: Long): Unit =
    urlConn.setIfModifiedSince(ifModifiedSince)

  def getIfModifiedSince(): Long =
    urlConn.getIfModifiedSince()

  def getDefaultUseCaches(): Boolean =
    urlConn.getDefaultUseCaches()

  def getDefaultUseCaches(protocol: String): Boolean =
    urlConn.getDefaultUseCaches(protocol)

  def setDefaultUseCaches(defaultVal: Boolean): Unit =
    urlConn.setDefaultUseCaches(defaultVal)

  def setDefaultUseCaches(protocol: String, defaultVal: Boolean): Unit =
    urlConn.setDefaultUseCaches(protocol, defaultVal)

  def setRequestProperty(key: String, value: String): Unit =
    urlConn.setRequestProperty(key, value)

  def addRequestProperty(key: String, value: String): Unit =
    urlConn.addRequestProperty(key, value)

  def getRequestProperty(key: String): String =
    urlConn.getRequestProperty(key)

  def getRequestProperties(): JMap[String, JList[String]] =
    urlConn.getRequestProperties()
}
