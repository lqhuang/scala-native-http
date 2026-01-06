import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpResponse.BodyHandlers
import javax.net.ssl.SSLContext

def runReqBasic(): Unit =
  val client = HttpClient.newBuilder().build()

  val request = HttpRequest
    .newBuilder()
    .uri(URI.create("http://www.example.com"))
    .GET()
    .build()
  val response = client.send(request, HttpResponse.BodyHandlers.ofString())

  println(s"Status: ${response.statusCode()}")
  println(s"Body: ${response.body()}")

def runCustomSSLContext(): Unit =
  val sslContext = SSLContext.getInstance("TLSv1.3")

  val sslParams = sslContext.getDefaultSSLParameters()
  sslParams.setProtocols(Array("TLSv1.3"))

  val client = HttpClient.newBuilder().sslContext(sslContext).sslParameters(sslParams).build()

  val request = HttpRequest
    .newBuilder()
    .uri(URI.create("https://www.example.com"))
    .GET()
    .build()

  val response = client.send(request, HttpResponse.BodyHandlers.ofString())

  println(s"Status: ${response.statusCode()}")
  println(s"Body: ${response.body()}")

@main def req(): Unit =
  runReqBasic()
  runCustomSSLContext()
