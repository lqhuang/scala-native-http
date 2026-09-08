package snhttp.test.java.net.http

import java.net.URI
import java.net.http.HttpRequest
import java.time.Duration
import utest.{TestSuite, Tests, test, assert}

class CopyRequestBodyTest extends TestSuite:
  val tests = Tests:
    test("copy preserves absence of a body for GET HEAD and DELETE") {
      Seq("GET", "HEAD", "DELETE").foreach { method =>
        val builder = HttpRequest.newBuilder(URI.create("https://example.com/"))
        val original = (method match {
          case "HEAD" => builder.HEAD()
          case "DELETE" => builder.DELETE()
          case _ => builder.GET()
        }).build()
        val copy = HttpRequest.newBuilder(original, (_, _) => true).build()
        assert(!copy.bodyPublisher().isPresent())
        assert(copy.method() == method)
      }
    }
    test("copy preserves explicit empty publisher") {
      val publisher = HttpRequest.BodyPublishers.noBody()
      val original = HttpRequest.newBuilder(URI.create("https://example.com/"))
        .method("GET", publisher).build()
      val copy = HttpRequest.newBuilder(original, (_, _) => true).build()
      assert(copy.bodyPublisher().get() eq publisher)
    }
    test("copy preserves POST body and metadata and filters headers") {
      val publisher = HttpRequest.BodyPublishers.ofString("payload")
      val original = HttpRequest.newBuilder(URI.create("https://example.com/path"))
        .POST(publisher).timeout(Duration.ofSeconds(3)).expectContinue(true)
        .header("X-Keep", "yes").header("X-Drop", "no").build()
      val copy = HttpRequest.newBuilder(original, (name, _) => name != "X-Drop").build()
      assert(copy.bodyPublisher().get() eq publisher)
      assert(copy.method() == "POST", copy.uri() == original.uri())
      assert(copy.timeout() == original.timeout(), copy.expectContinue())
      assert(copy.headers().firstValue("X-Keep").get() == "yes")
      assert(copy.headers().firstValue("X-Drop").isEmpty())
    }
