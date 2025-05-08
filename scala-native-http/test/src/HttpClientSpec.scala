package http.client

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HttpClientSpec extends AnyFlatSpec with Matchers {

  "HttpClient" should "send a GET request and receive a response" in {
    val client = new HttpClient()
    val request = HttpRequest("GET", "http://example.com")
    
    val responseFuture: Future[HttpResponse] = client.sendAsync(request)

    responseFuture.map { response =>
      response.statusCode shouldEqual 200
      response.body should not be empty
    }
  }

  it should "handle timeouts correctly" in {
    val client = new HttpClient()
    val request = HttpRequest("GET", "http://example.com/timeout")

    val responseFuture: Future[HttpResponse] = client.sendAsync(request)

    recoverToSucceededIf[TimeoutException] {
      responseFuture
    }
  }

  it should "handle proxy settings" in {
    val client = new HttpClient()
    client.setProxy("http://proxy.example.com:8080")
    val request = HttpRequest("GET", "http://example.com")

    val responseFuture: Future[HttpResponse] = client.sendAsync(request)

    responseFuture.map { response =>
      response.statusCode shouldEqual 200
    }
  }

  it should "handle SSL settings" in {
    val client = new HttpClient()
    client.setSSLSettings(/* SSL settings here */)
    val request = HttpRequest("GET", "https://secure.example.com")

    val responseFuture: Future[HttpResponse] = client.sendAsync(request)

    responseFuture.map { response =>
      response.statusCode shouldEqual 200
    }
  }
}