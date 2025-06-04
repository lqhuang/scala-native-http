import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers

// import scala.concurrent.ExecutionContext.Implicits.global

import munit.FunSuite

object HttpRequestTests extends FunSuite {

  test("newBuilder / constructor") {
    val request = HttpRequest.newBuilder(new URI("http://httpbin.org/get")).GET().build()

    assertEquals(request.method(), "GET")
    assertEquals(request.uri(), new URI("http://httpbin.org/get"))
    assertEquals(request.headers().map().size(), 0)
  }

  test("newBuilder / uri") {
    val request = HttpRequest.newBuilder().uri(new URI("http://httpbin.org/get")).GET().build()
  }

  test("newBuilder / method") {
    val request = HttpRequest
      .newBuilder()
      .uri(new URI("http://httpbin.org/get"))
      .method("GET", BodyPublishers.noBody())
      .build()

  }

  test("newBuilder / header") {
    val request =
      HttpRequest.newBuilder(new URI("http://httpbin.org/get")).header("Foo", "bar").build()
  }

  test("newBuilder / POST request") {
    val request = HttpRequest
      .newBuilder()
      .uri(new URI("http://httpbin.org/post"))
      .POST(BodyPublishers.ofString("hello"))
      .build()
  }

  test("newBuilder / POST with header") {
    val text = "Some text to send in the body"
    HttpRequest
      .newBuilder()
      .uri(new URI("http://httpbin.org/post"))
      .POST(BodyPublishers.ofString(text))
      .header("Content-Type", "text/plain")
      .build()
  }

  test("newBuilder / PUT request") {
    val request =
      HttpRequest
        .newBuilder()
        .uri(new URI("http://httpbin.org/put"))
        .build()
  }
}
