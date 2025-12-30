package snhttp.cookie

import java.net.{CookieHandler, CookieManager, CookiePolicy, CookieStore, HttpCookie, URI}
import java.util.{ArrayList, Collections, HashMap, List as JList, Map as JMap}

import scala.jdk.CollectionConverters._

class CookieStoreTests extends munit.FunSuite:

  private def createStore(): CookieStore =
    new CookieManager().getCookieStore()

  test("add and retrieve cookie by URI") {
    val store = createStore()
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("session", "abc123")
    cookie.setDomain("example.com")
    cookie.setPath("/")

    store.add(uri, cookie)
    val retrieved = store.get(uri)

    assertEquals(retrieved.size(), 1)
    assertEquals(retrieved.get(0).getName(), "session")
    assertEquals(retrieved.get(0).getValue(), "abc123")
  }

  test("get returns empty list for unknown URI") {
    val store = createStore()
    val uri = new URI("http://unknown.com/path")
    val retrieved = store.get(uri)
    assertEquals(retrieved.size(), 0)
  }

  test("getCookies returns all cookies") {
    val store = createStore()
    val uri1 = new URI("http://example.com/path")
    val uri2 = new URI("http://other.com/path")
    val cookie1 = new HttpCookie("c1", "v1")
    val cookie2 = new HttpCookie("c2", "v2")
    cookie1.setDomain("example.com")
    cookie2.setDomain("other.com")

    store.add(uri1, cookie1)
    store.add(uri2, cookie2)

    val all = store.getCookies()
    assertEquals(all.size(), 2)
  }

  test("getURIs returns all URIs") {
    val store = createStore()
    val uri1 = new URI("http://example.com/path")
    val uri2 = new URI("http://other.com/path")
    val cookie1 = new HttpCookie("c1", "v1")
    val cookie2 = new HttpCookie("c2", "v2")
    cookie1.setDomain("example.com")
    cookie2.setDomain("other.com")

    store.add(uri1, cookie1)
    store.add(uri2, cookie2)

    val uris = store.getURIs()
    assertEquals(uris.size(), 2)
  }

  test("remove cookie") {
    val store = createStore()
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("session", "abc123")
    cookie.setDomain("example.com")
    cookie.setPath("/")

    store.add(uri, cookie)
    assertEquals(store.getCookies().size(), 1)

    val removed = store.remove(uri, cookie)
    assertEquals(removed, true)
    assertEquals(store.getCookies().size(), 0)
  }

  test("removeAll clears store") {
    val store = createStore()
    val uri = new URI("http://example.com/path")
    val cookie1 = new HttpCookie("c1", "v1")
    val cookie2 = new HttpCookie("c2", "v2")

    store.add(uri, cookie1)
    store.add(uri, cookie2)
    assertEquals(store.getCookies().size(), 2)

    val cleared = store.removeAll()
    assertEquals(cleared, true)
    assertEquals(store.getCookies().size(), 0)
  }

  test("expired cookies are not returned") {
    val store = createStore()
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("expired", "value")
    cookie.setMaxAge(0) // Immediately expired

    store.add(uri, cookie)
    val retrieved = store.get(uri)
    assertEquals(retrieved.size(), 0)
  }

  test("session cookies are returned") {
    val store = createStore()
    val uri = new URI("http://example.com/path")
    val cookie = new HttpCookie("session", "value")
    cookie.setMaxAge(-1) // Session cookie

    store.add(uri, cookie)
    val retrieved = store.get(uri)
    assertEquals(retrieved.size(), 1)
  }

  test("domain matching retrieves cookies") {
    val store = createStore()
    val storeUri = new URI("http://example.com/")
    val cookie = new HttpCookie("test", "value")
    cookie.setDomain(".example.com")
    cookie.setPath("/")

    store.add(storeUri, cookie)

    val retrieveUri = new URI("http://www.example.com/page")
    val retrieved = store.get(retrieveUri)
    assertEquals(retrieved.size(), 1)
  }

  test("path matching retrieves cookies") {
    val store = createStore()
    val uri = new URI("http://example.com/app")
    val cookie = new HttpCookie("test", "value")
    cookie.setDomain("example.com")
    cookie.setPath("/app")

    store.add(uri, cookie)

    val matchUri = new URI("http://example.com/app/page")
    val retrieved = store.get(matchUri)
    assertEquals(retrieved.size(), 1)

    val noMatchUri = new URI("http://example.com/other")
    val notRetrieved = store.get(noMatchUri)
    assertEquals(notRetrieved.size(), 0)
  }

  test("secure cookies only match HTTPS") {
    val store = createStore()
    val uri = new URI("https://example.com/")
    val cookie = new HttpCookie("secure", "value")
    cookie.setDomain("example.com")
    cookie.setPath("/")
    cookie.setSecure(true)

    store.add(uri, cookie)

    val httpsUri = new URI("https://example.com/page")
    val httpsRetrieved = store.get(httpsUri)
    assertEquals(httpsRetrieved.size(), 1)

    val httpUri = new URI("http://example.com/page")
    val httpRetrieved = store.get(httpUri)
    assertEquals(httpRetrieved.size(), 0)
  }

  test("replacing cookie with same name/domain/path") {
    val store = createStore()
    val uri = new URI("http://example.com/")
    val cookie1 = new HttpCookie("test", "value1")
    cookie1.setDomain("example.com")
    cookie1.setPath("/")

    val cookie2 = new HttpCookie("test", "value2")
    cookie2.setDomain("example.com")
    cookie2.setPath("/")

    store.add(uri, cookie1)
    store.add(uri, cookie2)

    val retrieved = store.get(uri)
    assertEquals(retrieved.size(), 1)
    assertEquals(retrieved.get(0).getValue(), "value2")
  }
