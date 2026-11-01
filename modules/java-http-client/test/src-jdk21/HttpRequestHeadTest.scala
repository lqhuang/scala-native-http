package snhttp.test.java.net.http

import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

import utest.{TestSuite, Tests, test, assert}

class HttpRequestHeadTest extends TestSuite:
  def tests = Tests:
    test("should get response status code 200 when sending HEAD") {
      ClientUtils.withNewHttpClient { client =>
        val request = HttpRequest
          .newBuilder(URI.create("https://httpbingo.org/get"))
          .HEAD()
          .build()
        val response = client.send(request, BodyHandlers.ofString())

        assert(response.statusCode() == 200)
        assert(response.request() == request)
        assert(response.body() == "")
      }
    }
