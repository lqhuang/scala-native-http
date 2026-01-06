import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpResponse.BodyHandlers
import java.util.concurrent.CompletableFuture
import javax.net.ssl.SSLContext

def runAsync(): Unit =
  val uri = URI.create("http://www.example.org")
  val client = HttpClient.newBuilder().build()
  val request = HttpRequest
    .newBuilder()
    .uri(uri)
    .GET()
    .build()

  println(s"Sending async request to ${uri} ...")
  val cf = client.sendAsync(request, BodyHandlers.ofString())

  println("Doing other work while waiting for the response...")
  Thread.sleep(1000)

  val resp = cf.get()
  println(s"Status: ${resp.statusCode()}")
  println(s"Body: ${resp.body()}")
  println("Async request completed.")

def runAsyncTLS(): Unit =
  val sslContext = SSLContext.getDefault()
  val uri = URI.create("https://www.example.org")
  val client = HttpClient
    .newBuilder()
    .sslContext(sslContext)
    .build()

  val request = HttpRequest
    .newBuilder()
    .uri(uri)
    .GET()
    .build()

  println(s"Sending async TLS request to ${uri} ...")
  val cf = client.sendAsync(request, BodyHandlers.ofString())
  val resp = cf.get()
  println(s"Status: ${resp.statusCode()}")
  println(s"Body: ${resp.statusCode()}")
  println("Async TLS request completed.")

def runConcurrentRequests(): Unit =
  val client = HttpClient.newBuilder().build()

  val uris = List(
    "http://www.example.org",
    "https://www.example.org",
    "http://www.example.com",
    "https://www.example.net",
  ).map(x => URI.create(x))

  val requests = uris
    .map(uri => HttpRequest.newBuilder().uri(uri).GET().build())
    .map { request =>
      println(s"Sending async request to ${request.uri()} ...")
      client
        .sendAsync(request, BodyHandlers.ofString())
        .thenApply(response =>
          println(s"Response from ${response.uri()}: ${response.statusCode()}"),
        )
    }

  val all = CompletableFuture.allOf(requests*).join()

@main def mainReqAsync(): Unit =
  runAsync()
  runAsyncTLS()
  runConcurrentRequests()
