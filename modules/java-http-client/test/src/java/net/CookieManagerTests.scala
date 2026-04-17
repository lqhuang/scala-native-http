package snhttp.java.net

import java.net.{CookieManager, CookiePolicy, HttpCookie, URI}
import java.util.{ArrayList, HashMap, List as JList}

import scala.jdk.CollectionConverters._

import utest.{Tests, test, assert, assertThrows}

class CookieManagerTests extends utest.TestSuite:

  val tests = Tests {
    test("get returns empty Cookie header when no cookies") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")
      val requestHeaders = new HashMap[String, JList[String]]()

      val result = manager.get(uri, requestHeaders)
      assert(result.containsKey("Cookie"))
      val cookieHeader = result.get("Cookie")
      assert(cookieHeader != null)
      assert(cookieHeader.isEmpty)
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

    test("put treats Set-Cookie header case-insensitively") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("session=abc123; Path=/; Domain=example.com")
      responseHeaders.put("set-cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getName() == "session")
      assert(cookies.get(0).getValue() == "abc123")
    }

    test("put with empty Set-Cookie header value is no-op") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val values = new ArrayList[String]()
      values.add("")
      responseHeaders.put("Set-Cookie", values)
      manager.put(uri, responseHeaders)

      assert(manager.getCookieStore().getCookies().size() == 0)
    }

    test("put without Set-Cookie headers is no-op") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/path")

      val responseHeaders = new HashMap[String, JList[String]]()
      val values = new ArrayList[String]()
      values.add("v")
      responseHeaders.put("X-Test", values)
      manager.put(uri, responseHeaders)

      assert(manager.getCookieStore().getCookies().size() == 0)
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
      assert(result.get("Cookie2") == null)
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
      assert(result.get("Cookie2") == null)
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

    test("CookieManager enforces path matching") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/app")

      val cookie = new HttpCookie("k", "v")
      cookie.setPath("/app")
      manager.getCookieStore().add(uri, cookie)

      val stored = manager.getCookieStore().get(new URI("http://example.com/other"))
      assert(stored.size() == 1)

      val requestHeaders = new HashMap[String, JList[String]]()
      val result = manager.get(new URI("http://example.com/other"), requestHeaders)

      assert(result.containsKey("Cookie"))
      val cookieHeader = result.get("Cookie")
      assert(cookieHeader != null)
      assert(cookieHeader.isEmpty)
      assert(result.get("Cookie2") == null)
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

    test("get orders three cookie paths by descending specificity") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/app/deep/page")

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("root=v1; Path=/; Domain=example.com")
      setCookieValues.add("app=v2; Path=/app; Domain=example.com")
      setCookieValues.add("deep=v3; Path=/app/deep; Domain=example.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val result = manager.get(uri, new HashMap[String, JList[String]]())
      val cookieHeader = result.get("Cookie")
      assert(cookieHeader.size() == 3)
      assert(cookieHeader.get(0) == "deep=v3")
      assert(cookieHeader.get(1) == "app=v2")
      assert(cookieHeader.get(2) == "root=v1")
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

      val nestedHeaders = new HashMap[String, JList[String]]()
      val nestedValues = new ArrayList[String]()
      nestedValues.add("nested=value")
      nestedHeaders.put("Set-Cookie", nestedValues)
      manager.put(uri, nestedHeaders)

      val rootHeaders = new HashMap[String, JList[String]]()
      val rootValues = new ArrayList[String]()
      rootValues.add("root=value")
      rootHeaders.put("Set-Cookie", rootValues)
      manager.put(new URI("http://example.com/page"), rootHeaders)

      val cookies = manager.getCookieStore().getCookies().asScala.toList
      assert(cookies.size == 2)
      val nestedCookie = cookies.find(_.getName() == "nested").get
      val rootCookie = cookies.find(_.getName() == "root").get
      assert(nestedCookie.getPath() == "/app/")
      assert(rootCookie.getPath() == "/")
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

    test("constructor with null store and null policy uses defaults") {
      val manager = new CookieManager(null, null)
      val uri = new URI("http://example.com/path")
      assert(manager.getCookieStore() != null)

      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value; Domain=other.com")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 0)
    }

    test("constructor with custom store returns custom store") {
      val customStore = new CookieManager().getCookieStore()
      val manager = new CookieManager(customStore, null)
      assert(manager.getCookieStore() eq customStore)
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
      val cookieHeader = result.get("Cookie")
      assert(cookieHeader != null)
      assert(cookieHeader.isEmpty)
    }

    test("expired cookies are not returned") {
      val parsed = HttpCookie.parse("expired=value; Max-Age=0")
      assert(parsed.size() == 1)
      assert(parsed.get(0).hasExpired() == true)

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
      val cookieHeader = result.get("Cookie")
      assert(cookieHeader != null)
      assert(cookieHeader.isEmpty)
    }

    test("get validates null inputs") {
      val manager = new CookieManager()
      val requestHeaders = new HashMap[String, JList[String]]()
      val uri = new URI("http://example.com/")
      assertThrows[IllegalArgumentException] {
        manager.get(null, requestHeaders): Unit
      }
      assertThrows[IllegalArgumentException] {
        manager.get(uri, null): Unit
      }
      assertThrows[IllegalArgumentException] {
        manager.get(null, null): Unit
      }
    }

    test("put validates null inputs") {
      val manager = new CookieManager()
      val uri = new URI("http://example.com/")
      val responseHeaders = new HashMap[String, JList[String]]()
      assertThrows[IllegalArgumentException] {
        manager.put(null, responseHeaders): Unit
      }
      assertThrows[IllegalArgumentException] {
        manager.put(uri, null): Unit
      }
      assertThrows[IllegalArgumentException] {
        manager.put(null, null): Unit
      }
    }

    test("setCookiePolicy with null keeps previous policy") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_NONE)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value")
      responseHeaders.put("Set-Cookie", setCookieValues)
      manager.put(uri, responseHeaders)
      assert(manager.getCookieStore().getCookies().size() == 0)

      manager.setCookiePolicy(null)
      manager.put(uri, responseHeaders)
      assert(manager.getCookieStore().getCookies().size() == 0)
    }

    test("setCookiePolicy null does not change ACCEPT_ALL policy") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val setCookieValues = new ArrayList[String]()
      setCookieValues.add("test=value; Domain=other.com")
      responseHeaders.put("Set-Cookie", setCookieValues)

      manager.setCookiePolicy(null)
      manager.put(uri, responseHeaders)
      assert(manager.getCookieStore().getCookies().size() == 1)
    }
  }
