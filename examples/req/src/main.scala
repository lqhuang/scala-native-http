import java.net.URI
import java.net.http.{HttpClient, HttpRequest}
import java.net.http.HttpResponse.BodyHandlers
import java.security.SecureRandom
import javax.net.ssl.SSLContext

object Main:

  def runReqBasic(): Unit = {
    val ctx = SSLContext.getDefault()
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest
      .newBuilder()
      .uri(URI.create("https://example.com/"))
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
    runReqBasic()
