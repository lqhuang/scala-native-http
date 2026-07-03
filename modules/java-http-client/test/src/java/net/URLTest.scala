package snhttp.test.java.net

import java.net.{URI, URL, MalformedURLException}

import utest.{TestSuite, Tests, test, assert, assertThrows}

class URLTest extends TestSuite:

  def tests: Tests = Tests {

    test("URL constructor") {
      val url = new URL("http://www.example.com:8080/path/to/resource?query=param#fragment")
      assert(url.getProtocol() == "http")
      assert(url.getHost() == "www.example.com")
      assert(url.getPort() == 8080)
      assert(url.getPath() == "/path/to/resource")
      assert(url.getQuery() == "query=param")
      assert(url.getRef() == "fragment")
    }

    test("URLWithDefaultPort") {
      val url = new URL("http://www.example.com/path/to/resource")
      assert(url.getProtocol() == "http")
      assert(url.getHost() == "www.example.com")
      assert(url.getPort() == -1)
      assert(url.getDefaultPort() == 80)
      assert(url.getPath() == "/path/to/resource")
    }

    test("URLWithHttpsDefaultPort") {
      val url = new URL("https://www.example.org/path/to/resource")
      assert(url.getProtocol() == "https")
      assert(url.getHost() == "www.example.org")
      assert(url.getPort() == -1)
      assert(url.getDefaultPort() == 443)
      assert(url.getPath() == "/path/to/resource")
    }

    test("toString") {
      val url = new URL("http://www.example.com:8080/path/to/resource?query=param#fragment")
      assert(url.toString() == "http://www.example.com:8080/path/to/resource?query=param#fragment")
    }

    test("MalformedURL") {
      assertThrows[MalformedURLException] {
        new URL("ht!tp://invalid-url"): Unit
      }
    }

    test("relativeURL") {
      val baseUrl = new URL("http://www.example.com/path/to/resource")
      val spec = "../another/resource"
      val relativeUrl = new URL(baseUrl, spec)

      assert(relativeUrl.toString() == "http://www.example.com/path/another/resource")
      assert(relativeUrl.toString() == baseUrl.toURI().resolve(spec).toString())
    }

    test("URLWithIPv6") {
      val url = new URL("http://[2001:db8::1]:8080/path/to/resource")
      assert(url.getProtocol() == "http")
      assert(url.getHost() == "[2001:db8::1]")
      assert(url.getPort() == 8080)
      assert(url.getPath() == "/path/to/resource")
    }

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

    test("equals") {
      val url1 = new URL("http://www.example.com:8080/path/to/resource?query=param#fragment")
      val url2 = new URL("http://www.example.com:8080/path/to/resource?query=param#fragment")
      val url3 = new URL("http://www.example.com:8080/path/to/resource?query=param#different")
      val url4 = new URL("https://www.example.com:8080/path/to/resource?query=param#different")

      assert(url1 == url2)
      assert(url1 != url3)
      assert(url3 != url4)
    }

  }
