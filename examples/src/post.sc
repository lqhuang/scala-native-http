import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpRequest.BodyPublishers

def runSimplePostRequest(): Unit =
  val request = HttpRequest
    .newBuilder()
    .uri(URI.create("https://postman-echo.com/post"))
    .headers("Content-Type", "text/plain; charset=UTF-8")
    .POST(BodyPublishers.ofString("Sample request body"))
    .build()
  val client = HttpClient.newBuilder().build()
  val response = client.send(request, HttpResponse.BodyHandlers.ofString())
  println(s"Status: ${response.statusCode()}")
  println(s"Response Body: ${response.body()}")

// ------------------------------------------ //
//   Main entry point                         //
// ------------------------------------------ //
runSimplePostRequest()
