package snhttp.cookie

import java.net.{CookiePolicy, HttpCookie, URI}

class CookiePolicyTests extends munit.FunSuite:

  test("ACCEPT_ALL accepts any cookie") {
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("test", "value")
    cookie.setDomain("other.com")

    assertEquals(CookiePolicy.ACCEPT_ALL.shouldAccept(uri, cookie), true)
  }

  test("ACCEPT_NONE rejects any cookie") {
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("test", "value")
    cookie.setDomain("example.com")

    assertEquals(CookiePolicy.ACCEPT_NONE.shouldAccept(uri, cookie), false)
  }

  test("ACCEPT_ORIGINAL_SERVER accepts cookie with matching domain") {
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("test", "value")
    cookie.setDomain("example.com")

    assertEquals(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie), true)
  }

  test("ACCEPT_ORIGINAL_SERVER accepts cookie with no domain") {
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("test", "value")
    // No domain set

    assertEquals(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie), true)
  }

  test("ACCEPT_ORIGINAL_SERVER rejects cookie with non-matching domain") {
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("test", "value")
    cookie.setDomain("other.com")

    assertEquals(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie), false)
  }

  test("ACCEPT_ORIGINAL_SERVER accepts subdomain match") {
    val uri = new URI("http://www.example.com/path")
    val cookie = new HttpCookie("test", "value")
    cookie.setDomain(".example.com")

    assertEquals(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, cookie), true)
  }

  test("ACCEPT_ORIGINAL_SERVER handles null uri") {
    val cookie = new HttpCookie("test", "value")
    assertEquals(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(null, cookie), false)
  }

  test("ACCEPT_ORIGINAL_SERVER handles null cookie") {
    val uri = new URI("http://example.com/path")
    assertEquals(CookiePolicy.ACCEPT_ORIGINAL_SERVER.shouldAccept(uri, null), false)
  }
