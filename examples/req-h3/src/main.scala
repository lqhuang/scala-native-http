import java.net.URI
import java.net.http.{HttpClient, HttpRequest}
import java.net.http.HttpClient.Version
import java.net.http.HttpResponse.BodyHandlers
import javax.net.ssl.SSLContext

import scala.util.Properties

object Main:

  def runHTTP3Request(): Unit = {
    val isNative = Properties.propOrEmpty("java.vm.name") == "Scala Native"
    val isH3Available = Properties.isJavaAtLeast(26) || isNative
    if (!isH3Available) {
      println(
        "HTTP/3 enum is not supported on this Java runtime. Please use at least Java 26 or later to run the HTTP/3 examples.",
      )
      return
    }

    for (each <- Version.values())
      println(s"Supported HTTP version: ${each}")

    val h3 = Version.valueOf("HTTP_3")
    val ctx = SSLContext.getDefault()
    val client = HttpClient.newBuilder().sslContext(ctx).version(h3).build()

    val request = HttpRequest
      .newBuilder()
      .uri(URI.create("https://www.http3check.net/"))
      .GET()
      .build()

    val response = client.send(request, BodyHandlers.ofString())
    println(s"Status: ${response.statusCode()}")
    println(s"Body: ${response.body()}")
    println(s"Status: ${response.headers()}")
  }

  // ------------------------------------------ //
  //   Main entry point                         //
  // ------------------------------------------ //

  def main(args: Array[String]): Unit =
    runHTTP3Request()
