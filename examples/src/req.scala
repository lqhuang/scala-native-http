import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import javax.net.ssl.SSLContext

def runBasic(): Unit =
  val sslContext = SSLContext.getDefault()

  val client = HttpClient.newBuilder().sslContext(sslContext).build()

  val request = HttpRequest
    .newBuilder()
    .uri(URI.create("https://www.example.com"))
    .GET()
    .build()

  val response = client.send(request, HttpResponse.BodyHandlers.ofString())

  println(s"Status: ${response.statusCode()}")
  println(s"Body: ${response.body()}")

def runCustomSSLContext(): Unit =
  val sslContext = SSLContext.getInstance("TLSv1.3")
  sslContext.init(null, null, null)

  val sslParams = sslContext.getDefaultSSLParameters()
  sslParams.setProtocols(Array("TLSv1.3"))

  val client = HttpClient.newBuilder().sslContext(sslContext).sslParameters().build()

  val request = HttpRequest
    .newBuilder()
    .uri(URI.create("https://www.example.com"))
    .GET()
    .build()

  val response = client.send(request, HttpResponse.BodyHandlers.ofString())

  println(s"Status: ${response.statusCode()}")
  println(s"Body: ${response.body()}")

@main def main(args: Array[String]): Unit =
  runBasic()
  runCustomSSLContext()
