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

    for url <- Seq(
        "https://www.http3check.net/",
        "https://cloudflare-quic.com/",
      )
    do {
      val request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build()
      val response = client.send(request, BodyHandlers.ofString())

      println(s"== Request to <${url}> completed")
      println(s"Status: ${response.statusCode()}")
      println(s"Headers: ${response.headers()}")
      println(s"Body: ${response.body()}")
    }
  }

  // ------------------------------------------ //
  //   Main entry point                         //
  // ------------------------------------------ //

  def main(args: Array[String]): Unit =
    runHTTP3Request()
