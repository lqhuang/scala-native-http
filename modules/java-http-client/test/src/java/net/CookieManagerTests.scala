package snhttp.cookie

import java.net.{CookieHandler, CookieManager, CookiePolicy, HttpCookie, URI}
import java.util.{ArrayList, HashMap, List as JList}

import utest.{Tests, test, assert}

class CookieManagerTests extends utest.TestSuite:

  val tests = Tests {
    test("get returns empty map when no cookies") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")
      val requestHeaders = new HashMap[String, JList[String]]()

      val result = manager.get(uri, requestHeaders)
      assert(result.isEmpty || !result.containsKey("Cookie"))
    }

    test("put stores cookies from Set-Cookie header") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("session=abc123; Path=/")
      responseHeaders.put("Set-Cookie", setCookieValues)

      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getName() == "session")
      assert(cookies.get(0).getValue() == "abc123")
    }

    test("get returns Cookie header for stored cookies") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("session=abc123; Path=/; Domain=example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val requestHeaders = new HashMap[String, JList[String]]()
      val result = manager.get(uri, requestHeaders)

      assert(result.containsKey("Cookie"))
      val cookieHeader = result.get("Cookie")
      assert(cookieHeader.size() == 1)
      assert(cookieHeader.get(0).contains("session=abc123"))
    }

    test("multiple cookies are joined with semicolon") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("cookie1=value1; Path=/; Domain=example.com")
      setCookieValues.add("cookie2=value2; Path=/; Domain=example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val requestHeaders = new HashMap[String, JList[String]]()
      val result = manager.get(uri, requestHeaders)

      val cookieHeader = result.get("Cookie").get(0)
      assert(cookieHeader.contains("cookie1=value1"))
      assert(cookieHeader.contains("cookie2=value2"))
      assert(cookieHeader.contains("; "))
    }

    test("put applies default domain from URI") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/app/page")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getDomain() == "example.com")
    }

    test("put applies default path from URI") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/app/page")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getPath() == "/app")
    }

    test("setCookiePolicy changes acceptance policy") {
      val manager = new CookieManager()
      manager.setCookiePolicy(CookiePolicy.ACCEPT_NONE)

      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 0)
    }

    test("ACCEPT_ALL policy accepts all cookies") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL)

      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value; Domain=other.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
    }

    test("malformed cookies are ignored") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("$invalid=value")
      setCookieValues.add("valid=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getName() == "valid")
    }

    test("secure cookies are not sent over HTTP") {
      val manager = new CookieManager()
      val httpsUri = new URI("https://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("secure=value; Secure; Domain=example.com; Path=/")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(httpsUri, responseHeaders)

      val httpUri = new URI("http://example.com/path")
      val requestHeaders = new HashMap[String, JList[String]]()
      val result = manager.get(httpUri, requestHeaders)

      assert(!result.containsKey("Cookie") || result.get("Cookie").isEmpty || result.get("Cookie").get(0).isEmpty)
    }

    test("expired cookies are not returned") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("expired=value; Max-Age=0; Domain=example.com; Path=/")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val requestHeaders = new HashMap[String, JList[String]]()
      val result = manager.get(uri, requestHeaders)

      assert(!result.containsKey("Cookie") || result.get("Cookie").isEmpty)
    }

    test("CookieHandler.setDefault and getDefault") {
      val originalHandler = CookieHandler.getDefault()
      try
        val manager = new CookieManager()
        CookieHandler.setDefault(manager)
        assert(CookieHandler.getDefault() == manager)

        CookieHandler.setDefault(null)
        assert(CookieHandler.getDefault() == null)
      finally CookieHandler.setDefault(originalHandler)
    }
  }
