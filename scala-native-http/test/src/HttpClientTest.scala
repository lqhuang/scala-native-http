import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationConversions
import scala.concurrent.ExecutionContext.Implicits.global

import munit.FunSuite

class HttpClientSuite extends FunSuite {

  test("HttpClient should send a GET request and receive a response") {
    val client = new HttpClient()
    val request = HttpRequest("GET", "http://example.com")

    val responseFuture: Future[HttpResponse] = client.sendAsync(request)

    val response = Await.result(responseFuture, 10.seconds)
    assertEquals(response.statusCode, 200)
    assert(response.body.nonEmpty)
  }

  test("HttpClient should handle timeouts correctly") {
    val client = new HttpClient()
    val request = HttpRequest("GET", "http://example.com/timeout")

    val responseFuture: Future[HttpResponse] = client.sendAsync(request)

    intercept[TimeoutException] {
      Await.result(responseFuture, 10.seconds)
    }
  }

  test("HttpClient should handle proxy settings") {
    val client = new HttpClient()
    client.setProxy("http://proxy.example.com:8080")
    val request = HttpRequest("GET", "http://example.com")

    val responseFuture: Future[HttpResponse] = client.sendAsync(request)

    val response = Await.result(responseFuture, 10.seconds)
    assertEquals(response.statusCode, 200)
  }

  test("HttpClient should handle SSL settings") {
    val client = new HttpClient()
    client.setSSLSettings( /* SSL settings here */ )
    val request = HttpRequest("GET", "https://secure.example.com")

    val responseFuture: Future[HttpResponse] = client.sendAsync(request)

    val response = Await.result(responseFuture, 10.seconds)
    assertEquals(response.statusCode, 200)
  }
}
