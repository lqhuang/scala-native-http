import java.net.URI
import java.net.http.{HttpClient, HttpRequest}
import java.net.http.HttpResponse.BodyHandlers
import java.security.SecureRandom
import javax.net.ssl.SSLContext

object Main:

  def runCustomSSLContext(): Unit = {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, null, null)

    val sslParams = sslContext.getDefaultSSLParameters()
    sslParams.setProtocols(Array("TLSv1.3"))

    val client = HttpClient.newBuilder().sslContext(sslContext).sslParameters(sslParams).build()

    val request = HttpRequest
      .newBuilder()
      .uri(URI.create("https://www.example.com"))
      .GET()
      .build()

    val response = client.send(request, BodyHandlers.ofString())
    println(s"Status: ${response.statusCode()}")
    println(s"Body: ${response.body()}")
  }

  def runCustomSSLContext2(): Unit = {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, null, null)

    val sslParams = sslContext.getDefaultSSLParameters()
    sslParams.setProtocols(Array("TLSv1.3"))

    val client = HttpClient.newBuilder().sslContext(sslContext).sslParameters(sslParams).build()

    val request = HttpRequest
      .newBuilder()
      .uri(URI.create("https://www.example.com"))
      .GET()
      .build()

    val response = client.send(request, BodyHandlers.ofString())
    println(s"Status: ${response.statusCode()}")
    println(s"Body: ${response.body()}")
  }

  // ------------------------------------------ //
  //   Main entry point                         //
  // ------------------------------------------ //

  def main(args: Array[String]): Unit =
    runCustomSSLContext()
