package snhttp.jdk.net

import java.net.{InetAddress, URL, URLConnection, URLStreamHandler}

class HttpURLStreamHandler extends URLStreamHandler:
  def openConnection(url: URL): URLConnection = ???

class HttpsURLStreamHandler extends URLStreamHandler:
  def openConnection(url: URL): URLConnection = ???

class FileURLStreamHandler extends URLStreamHandler:
  def openConnection(url: URL): URLConnection = ???
