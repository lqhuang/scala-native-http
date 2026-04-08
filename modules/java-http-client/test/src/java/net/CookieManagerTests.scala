package snhttp.java.net

import java.net.{CookieManager, CookiePolicy, HttpCookie, URI}
import java.util.{ArrayList, HashMap, List as JList}

import utest.{Tests, test, assert, assertThrows}

class CookieManagerTests extends utest.TestSuite:

  val tests = Tests {
    test("get returns empty map when no cookies") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")
      val requestHeaders = new HashMap[String, JList[String]]()

      val result = manager.get(uri, requestHeaders)
      assert(result.containsKey("Cookie"))
      assert(result.get("Cookie").isEmpty)
      assert(result.get("Cookie2") == null)
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

    test("put stores cookies from Set-Cookie2 header") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("session=abc123; Version=1; Path=/; Domain=example.com")
      responseHeaders.put("Set-Cookie2", setCookieValues)
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

    test("multiple cookies are returned as multiple Cookie header values") {
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

      val cookieHeader = result.get("Cookie")
      assert(cookieHeader.size() == 2)
      assert(cookieHeader.contains("cookie1=value1"))
      assert(cookieHeader.contains("cookie2=value2"))
    }

    test("get filters cookies by request path") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/app/page")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("root=v1; Path=/; Domain=example.com")
      setCookieValues.add("app=v2; Path=/app; Domain=example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val requestHeaders = new HashMap[String, JList[String]]()
      val result = manager.get(new URI("http://example.com/other"), requestHeaders)
      val cookieHeader = result.get("Cookie")

      assert(cookieHeader.size() == 1)
      assert(cookieHeader.get(0) == "root=v1")
    }

    test("get orders cookies by most specific path first") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/app/page")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("root=v1; Path=/; Domain=example.com")
      setCookieValues.add("app=v2; Path=/app; Domain=example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val requestHeaders = new HashMap[String, JList[String]]()
      val result = manager.get(uri, requestHeaders)
      val cookieHeader = result.get("Cookie")

      assert(cookieHeader.size() == 2)
      assert(cookieHeader.get(0) == "app=v2")
      assert(cookieHeader.get(1) == "root=v1")
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
      assert(cookies.get(0).getPath() == "/app/")
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
      setCookieValues.add("test=value; Domain=example.com")
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

      assert(result.containsKey("Cookie"))
      assert(result.get("Cookie").isEmpty)
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

      assert(result.containsKey("Cookie"))
      assert(result.get("Cookie").isEmpty)
    }

    // Phase 3: Null handling tests
    test("constructor with null store creates default store") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL)
      assert(manager.getCookieStore() != null)
    }

    test("constructor with null policy uses default policy") {
      val manager = new CookieManager(null, null)
      assert(manager.getCookieStore() != null)
    }

    test("get with null URI throws IllegalArgumentException") {
      val manager = new CookieManager()
      val requestHeaders = new HashMap[String, JList[String]]()
      assertThrows[IllegalArgumentException] {
        manager.get(null, requestHeaders): Unit
      }
    }

    test("get with null headers throws IllegalArgumentException") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/")
      assertThrows[IllegalArgumentException] {
        manager.get(uri, null): Unit
      }
    }

    test("put with null URI throws IllegalArgumentException") {
      val manager = new CookieManager()
      val responseHeaders = new HashMap[String, JList[String]]()
      assertThrows[IllegalArgumentException] {
        manager.put(null, responseHeaders): Unit
      }
    }

    test("put with null headers throws IllegalArgumentException") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/")
      assertThrows[IllegalArgumentException] {
        manager.put(uri, null): Unit
      }
    }

    test("setCookiePolicy with null keeps previous policy") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_NONE)
      // ACCEPT_NONE should reject all cookies
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)
      assert(manager.getCookieStore().getCookies().size() == 0)
      
      // Setting null should not change policy
      manager.setCookiePolicy(null)
      manager.put(uri, responseHeaders)
      // Should still reject (policy unchanged)
      assert(manager.getCookieStore().getCookies().size() == 0)
    }

    test("cross-domain cookie is accepted by ACCEPT_ALL") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value; Domain=attacker.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getDomain() == "attacker.com")
    }

    test("subdomain cookie is accepted with dot prefix") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL)
      val uri = new URI("http://www.example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      // Domain with leading dot allows subdomains (per RFC6265)
      setCookieValues.add("test=value; Domain=.example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
    }

    // Phase 4: Path derivation tests
    test("path defaults to directory of URI path") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/app/page")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getPath() == "/app/")
    }

    test("path defaults to root for root URI") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getPath() == "/")
    }

    test("path defaults to root for single-segment URI path") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/page")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getPath() == "/")
    }

    test("explicit path is preserved") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/app/page")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value; Path=/custom")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getPath() == "/custom")
    }

    // Phase 5: Policy integration tests
    test("ACCEPT_NONE blocks all cookies even with valid domain") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_NONE)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value; Domain=example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 0)
    }

    test("ACCEPT_ORIGINAL_SERVER only accepts matching domain") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("valid=value; Domain=example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getName() == "valid")
    }

    test("multiple cookies with different domains are filtered by policy") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("valid=value; Domain=example.com")
      setCookieValues.add("other=value; Domain=.example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      // Both should be accepted by ACCEPT_ORIGINAL_SERVER
      assert(cookies.size() == 2)
    }
  }
