package snhttp.java.net

import java.net.{CookieManager, CookiePolicy, HttpCookie, URI}
import java.util.{ArrayList, HashMap, List as JList}

import utest.{Tests, test, assert}

class CookiePolicyTests extends utest.TestSuite:

  val tests = Tests {
    test("ACCEPT_ALL accepts any cookie") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("other.com")

      assert(CookiePolicy.ACCEPT_ALL.shouldAccept(uri, cookie) == true)
    }

    test("ACCEPT_ALL accepts null inputs") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")
      assert(CookiePolicy.ACCEPT_ALL.shouldAccept(null, cookie) == true)
      assert(CookiePolicy.ACCEPT_ALL.shouldAccept(uri, null) == true)
    }

    test("ACCEPT_NONE rejects any cookie") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("example.com")

      assert(CookiePolicy.ACCEPT_NONE.shouldAccept(uri, cookie) == false)
    }

    test("ACCEPT_NONE rejects null inputs") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")
      assert(CookiePolicy.ACCEPT_NONE.shouldAccept(null, cookie) == false)
      assert(CookiePolicy.ACCEPT_NONE.shouldAccept(uri, null) == false)
    }

    test("ACCEPT_ORIGINAL_SERVER accepts matching domain") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("example.com")

      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie) == true)
    }

    test("ACCEPT_ORIGINAL_SERVER is case-insensitive for domain") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("EXAMPLE.COM")

      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie) == true)
    }

    test("ACCEPT_ORIGINAL_SERVER rejects cookie with no domain") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")

      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie) == false)
    }

    test("ACCEPT_ORIGINAL_SERVER rejects cross-domain cookie") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("other.com")

      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie) == false)
    }

    test("ACCEPT_ORIGINAL_SERVER accepts subdomain with dot-prefixed domain") {
      val uri = new URI("http://www.example.com/path")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain(".example.com")

      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie) == true)
    }

    test("ACCEPT_ORIGINAL_SERVER rejects parent domain without dot on subdomain host") {
      val uri = new URI("http://www.example.com/path")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("example.com")

      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie) == false)
    }

    test("ACCEPT_ORIGINAL_SERVER accepts matching IPv4 and rejects non-matching IPv4") {
      val uri = new URI("http://192.168.0.1/path")
      val matchCookie = new HttpCookie("test", "value")
      matchCookie.setDomain("192.168.0.1")
      val mismatchCookie = new HttpCookie("test", "value")
      mismatchCookie.setDomain("192.168.0.2")

      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, matchCookie) == true)
      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, mismatchCookie) == false)
    }

    test("ACCEPT_ORIGINAL_SERVER rejects invalid domain") {
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("invalid")

      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie) == false)
    }

    test("ACCEPT_ORIGINAL_SERVER handles null uri") {
      val cookie = new HttpCookie("test", "value")
      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(null, cookie) == false)
    }

    test("ACCEPT_ORIGINAL_SERVER handles null cookie") {
      val uri = new URI("http://example.com/path")
      assert(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, null) == false)
    }

    test("CookieManager integration with ACCEPT_ALL") {
      val manager = new CookieManager()
      manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val values = new ArrayList[String]()
      values.add("same=value; Domain=example.com")
      values.add("other=value; Domain=other.com")
      responseHeaders.put("Set-Cookie", values)

      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 2)
    }

    test("CookieManager integration with ACCEPT_NONE") {
      val manager = new CookieManager()
      manager.setCookiePolicy(CookiePolicy.ACCEPT_NONE)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val values = new ArrayList[String]()
      values.add("same=value; Domain=example.com")
      values.add("other=value; Domain=other.com")
      responseHeaders.put("Set-Cookie", values)

      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 0)
    }

    test("CookieManager integration with ACCEPT_ORIGINAL_SERVER") {
      val manager = new CookieManager()
      manager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
      val uri = new URI("http://example.com/path")
      val responseHeaders = new HashMap[String, JList[String]]()
      val values = new ArrayList[String]()
      values.add("same=value; Domain=example.com")
      values.add("other=value; Domain=other.com")
      responseHeaders.put("Set-Cookie", values)

      manager.put(uri, responseHeaders)

      val cookies = manager.getCookieStore().getCookies()
      assert(cookies.size() == 1)
      assert(cookies.get(0).getName() == "same")
      assert(cookies.get(0).getDomain() == "example.com")
    }
  }
