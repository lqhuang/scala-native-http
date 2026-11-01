package snhttp.test.java.net

import java.net.{URI, URL}

import utest.{TestSuite, Tests, test, assert}

class URLFactoryTest extends TestSuite:
  def tests = Tests:
    test("of") {
      val url = URL.of(
        URI.create("http://www.example.com:8080/path/to/resource?query=param#fragment"),
        null,
      )
      assert(url.getProtocol() == "http")
      assert(url.getHost() == "www.example.com")
      assert(url.getPort() == 8080)
      assert(url.getPath() == "/path/to/resource")
      assert(url.getQuery() == "query=param")
      assert(url.getRef() == "fragment")
    }
