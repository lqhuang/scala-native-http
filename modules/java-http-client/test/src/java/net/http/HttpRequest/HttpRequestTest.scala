import java.net.URI
import java.net.http.{HttpClient, HttpHeaders, HttpRequest}
import java.net.http.HttpRequest.BodyPublishers
import java.time.Duration
import java.util.Optional
import java.util.List as JList
import java.util.Map as JMap
import java.util.function.BiPredicate

import utest.{Tests, test, assert, assertThrows}

class HttpRequestTest extends utest.TestSuite {

  val endpoint = "http://localhost"
  val acceptAllFilter: BiPredicate[String, String] = (_, _) => true

  val tests = Tests {

    test("newBuilder / construct by URI arg with default inner state") {
      val request = HttpRequest.newBuilder(new URI(endpoint)).build()

      assert(request.uri() == new URI(endpoint))
      assert(request.method() == "GET")
      assert(request.headers().map().size() == 0)
      assert(request.expectContinue() == false)
      assert(request.timeout() == Optional.empty[Duration])
      assert(request.version() == Optional.empty[HttpClient.Version])
      assert(request.bodyPublisher() == Optional.empty[HttpRequest.BodyPublisher])
    }

    test("newBuilder / construct by URI method") {
      val request = HttpRequest.newBuilder().uri(new URI(endpoint)).build()

      assert(request.uri() == new URI(endpoint))
      assert(request.method() == "GET")
      assert(request.headers().map().size() == 0)
      assert(request.expectContinue() == false)
      assert(request.timeout() == Optional.empty[Duration]())
      assert(request.version() == Optional.empty[HttpClient.Version]())
      assert(request.bodyPublisher() == Optional.empty[HttpRequest.BodyPublisher]())
    }

    test("newBuilder / fail to construct without URI") {
      assertThrows[IllegalArgumentException] {
        val request = HttpRequest.newBuilder().build()
      }
    }

    test("newBuilder / timeout") {
      val request =
        HttpRequest
          .newBuilder(new URI(endpoint))
          .timeout(Duration.ofSeconds(10))
          .build()
      assert(request.timeout() == Optional.of(Duration.ofSeconds(10)))
    }

    test("newBuilder / timeout can not be zero or negative") {
      Seq(-100, -1, 0).foreach { timeout =>
        assertThrows[IllegalArgumentException] {
          HttpRequest
            .newBuilder(new URI(endpoint))
            .timeout(Duration.ofSeconds(timeout))
            .build()
        }
      }
    }

    test("newBuilder / expectContinue") {
      val request =
        HttpRequest
          .newBuilder(new URI(endpoint))
          .expectContinue(true)
          .build()
      assert(request.expectContinue(), true)
    }

    test("newBuilder / version") {
      val request =
        HttpRequest
          .newBuilder(new URI(endpoint))
          .version(HttpClient.Version.HTTP_1_1)
          .build()
      assert(request.version() == Optional.of(HttpClient.Version.HTTP_1_1))
    }

    /**
     * FIXME: One of them will hang out on the tests
     */

    // test("newBuilder / header method should add header") {
    //   val request =
    //     HttpRequest
    //       .newBuilder(new URI(endpoint))
    //       .header("Foo", "bar")
    //       .header("Accept", "text/html")
    //       .build()
    //   val expectedHeaders = HttpHeaders.of(
    //     JMap.of(
    //       "Foo",
    //       JList.of("bar"),
    //       "Accept",
    //       JList.of("text/html"),
    //     ),
    //     acceptAllFilter,
    //   )
    //   assert(
    //     request.headers() == expectedHeaders
    //   )
    // }

    // test("newBuilder / header method should append to existing header") {
    //   val request =
    //     HttpRequest
    //       .newBuilder(new URI(endpoint))
    //       .header("Foo", "bar") // should be case-insensitive
    //       .header("foo", "baz") // should append to existing header
    //       .build()
    //   val expectedHeaders = HttpHeaders.of(
    //     JMap.of("foo", JList.of("bar", "baz")),
    //     acceptAllFilter,
    //   )
    //   assert(
    //     request.headers() == expectedHeaders,
    //   )
    // }

    // test("newBuilder / setHeader should replace existing header") {
    //   val request =
    //     HttpRequest
    //       .newBuilder(new URI(endpoint))
    //       .header("Foo", "bar") // should be case-insensitive
    //       .setHeader("foo", "baz") // should replace existing header
    //       .build()
    //   val expectedHeaders =
    //     HttpHeaders.of(JMap.of("foo", JList.of("baz")), acceptAllFilter)
    //   assert(request.headers() == expectedHeaders)
    // }

    /**
     * TODO:
     *
     *   1. add test cases for system properties `java.net.http.HttpHeaders.allowDuplicateHeaders`
     *   2. add test cases for copy Builder and modify it
     *   3. add test cases for `headers` method
     *   4. add test cases for `equals` method
     *   5. add test cases for `hashCode` method
     */

    // test("newBuilder / method") {
    //   val request = HttpRequest
    //     .newBuilder()
    //     .uri(new URI(endpoint))
    //     .method("GET", BodyPublishers.noBody())
    //     .build()
    // }

    // test("newBuilder / POST request") {
    //   val request = HttpRequest
    //     .newBuilder()
    //     .uri(new URI("http://httpbin.org/post"))
    //     .POST(BodyPublishers.ofString("hello"))
    //     .build()
    // }

    // test("newBuilder / POST with header") {
    //   val text = "Some text to send in the body"
    //   HttpRequest
    //     .newBuilder()
    //     .uri(new URI("http://httpbin.org/post"))
    //     .POST(BodyPublishers.ofString(text))
    //     .header("Content-Type", "text/plain")
    //     .build()
    // }

    // test("newBuilder / PUT request") {
    //   val request =
    //     HttpRequest
    //       .newBuilder()
    //       .uri(new URI("http://httpbin.org/put"))
    //       .build()
    // }
  }
}
