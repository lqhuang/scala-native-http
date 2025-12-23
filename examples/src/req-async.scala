import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpResponse.BodyHandlers
import javax.net.ssl.SSLContext

import scala.concurrent.Future
import java.util.concurrent.CompletableFuture

def runBasic(): Unit =
  val sslContext = SSLContext.getDefault()

  val client = HttpClient.newBuilder().sslContext(sslContext).build()

  val request = HttpRequest
    .newBuilder()
    .uri(URI.create("https://www.example.com"))
    .GET()
    .build()

  val cf = client.sendAsync(request, BodyHandlers.ofString())

  cf.thenApply(response =>
    println(s"Status: ${response.statusCode()}")
    println(s"Body: ${response.body()}"),
  ).join()

def runConcurrentRequests(): Unit =
  val client = HttpClient.newBuilder().build()

  val uris = List(
    "https://www.example.com",
    "https://www.example.org",
    "https://www.example.net",
  ).map(x => URI.create(x))

  val requests = uris
    .map(uri => HttpRequest.newBuilder().uri(uri).GET().build())
    .map(request =>
      client
        .sendAsync(request, BodyHandlers.ofString())
        .thenApply(response =>
          println(s"Response from ${response.uri()}: ${response.statusCode()}"),
        ),
    )

  val all = CompletableFuture.allOf(requests*).join()

@main def main(args: Array[String]): Unit =
  runBasic()
  runConcurrentRequests()
