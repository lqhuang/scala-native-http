package java.net

import java.io.IOException

abstract class URLStreamHandler:

  def openConnection(url: URL): URLConnection

  def openConnection(url: URL, proxy: Proxy): URLConnection = ???

  def parseURL(url: URL, spec: String, start: Int, limit: Int): Unit = ???

  def getDefaultPort(): Int = ???

  def sameFile(url1: URL, url2: URL): Boolean = ???

  def getHostAddress(url: URL): InetAddress = ???

  def hostsEqual(url1: URL, url2: URL): Boolean = ???

  def toExternalForm(u: URL): String = ???

  def setURL(
      url: URL,
      protocol: String,
      host: String,
      port: Int,
      authority: String,
      userInfo: String,
      path: String,
      query: String,
      ref: String,
  ): Unit = ???

  @deprecated(
    "Use [[setURL(URL, String, String, int, String, String, String, String)]];",
    since = "1.5",
  )
  def setURL(
      u: URL,
      protocol: String,
      host: String,
      port: Int,
      file: String,
      ref: String,
  ): Unit = throw new UnsupportedOperationException("deprecated")

  // true if the two urls are considered equal,
  // i.e. they refer to the same fragment in the same file.
  override def equals(other: Any): Boolean = ???

  override def hashCode(): Int = ???
