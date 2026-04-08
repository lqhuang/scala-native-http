package snhttp.java.net

import java.net.{CookieManager, CookieStore, HttpCookie, URI}

import utest.{Tests, test, assert, assertThrows}

class CookieStoreTests extends utest.TestSuite:

  private def createStore(): CookieStore =
    new CookieManager().getCookieStore()

  val tests = Tests {
    test("add and retrieve cookie by URI") {
      val store = createStore()
      val uri = new URI("http://example.com/path")
      val cookie = new HttpCookie("session", "abc123")
      cookie.setDomain("example.com")
      cookie.setPath("/")

      store.add(uri, cookie)
      val retrieved = store.get(uri)

      assert(retrieved.size() == 1)
      assert(retrieved.get(0).getName() == "session")
      assert(retrieved.get(0).getValue() == "abc123")
    }

    test("add with null URI stores cookie without URI association") {
      val store = createStore()
      val cookie = new HttpCookie("session", "v")
      cookie.setDomain("example.com")
      cookie.setPath("/")

      store.add(null, cookie)

      assert(store.getCookies().size() == 1)
      assert(store.getURIs().size() == 0)
      assert(store.get(new URI("http://example.com/")).size() == 1)
      assert(store.get(new URI("http://other.com/")).size() == 0)
    }

    test("add with null URI and no domain stores non-retrievable cookie") {
      val store = createStore()
      val cookie = new HttpCookie("session", "v")
      cookie.setPath("/")

      store.add(null, cookie)

      assert(store.getCookies().size() == 1)
      assert(store.getURIs().size() == 0)
      assert(store.get(new URI("http://example.com/")).size() == 0)
    }

    test("add with null cookie throws NullPointerException") {
      val store = createStore()
      val uri = new URI("http://example.com/")
      assertThrows[NullPointerException] {
        store.add(uri, null): Unit
      }
    }

    test("add with null URI and null cookie throws NullPointerException") {
      val store = createStore()
      assertThrows[NullPointerException] {
        store.add(null, null): Unit
      }
    }

    test("get with null URI throws NullPointerException") {
      val store = createStore()
      assertThrows[NullPointerException] {
        store.get(null): Unit
      }
    }

    test("remove with null cookie throws NullPointerException") {
      val store = createStore()
      val uri = new URI("http://example.com/")
      assertThrows[NullPointerException] {
        store.remove(uri, null): Unit
      }
    }

    test("remove with null URI removes matching cookie") {
      val store = createStore()
      val uri = new URI("http://example.com/")
      val cookie = new HttpCookie("session", "abc123")
      cookie.setDomain("example.com")
      cookie.setPath("/")
      store.add(uri, cookie)

      assert(store.remove(null, cookie) == true)
      assert(store.getCookies().size() == 0)
    }

    test("remove ignores URI and removes matching cookie") {
      val store = createStore()
      val storedUri = new URI("http://example.com/")
      val otherUri = new URI("http://other.com/")
      val cookie = new HttpCookie("session", "abc123")
      cookie.setDomain("example.com")
      cookie.setPath("/")
      store.add(storedUri, cookie)

      assert(store.remove(otherUri, cookie) == true)
      assert(store.getCookies().size() == 0)
    }

    test("remove on non-existent cookie returns false") {
      val store = createStore()
      val uri = new URI("http://example.com/")
      val cookie = new HttpCookie("nonexistent", "value")
      assert(store.remove(uri, cookie) == false)
      assert(store.remove(null, cookie) == false)
    }

    test("getCookies returns unmodifiable list") {
      val store = createStore()
      val uri = new URI("http://example.com/")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("example.com")
      cookie.setPath("/")
      store.add(uri, cookie)

      val cookies = store.getCookies()
      assertThrows[UnsupportedOperationException] {
        cookies.add(new HttpCookie("new", "value")): Unit
      }
      assert(store.getCookies().size() == 1)
    }

    test("getURIs returns modifiable snapshot list") {
      val store = createStore()
      val uri = new URI("http://example.com/")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("example.com")
      cookie.setPath("/")
      store.add(uri, cookie)

      val uris = store.getURIs()
      assert(uris.size() == 1)
      uris.add(new URI("http://new.com/"))
      assert(uris.size() == 2)
      assert(store.getURIs().size() == 1)
    }

    test("empty store returns empty non-null cookie and URI lists") {
      val store = createStore()
      val cookies = store.getCookies()
      val uris = store.getURIs()
      assert(cookies != null)
      assert(cookies.size() == 0)
      assert(uris != null)
      assert(uris.size() == 0)
    }

    test("removeAll on empty store returns false") {
      val store = createStore()
      assert(store.removeAll() == false)
    }

    test("removeAll clears non-empty store and returns true") {
      val store = createStore()
      val uri = new URI("http://example.com/")
      val cookie = new HttpCookie("c", "1")
      cookie.setDomain("example.com")
      cookie.setPath("/")
      store.add(uri, cookie)
      assert(store.getCookies().size() == 1)

      assert(store.removeAll() == true)
      assert(store.getCookies().size() == 0)
      assert(store.getURIs().size() == 0)
    }

    test("different subdomains do not share host-only cookies") {
      val store = createStore()
      val cookie = new HttpCookie("sub", "1")
      cookie.setDomain("a.example.com")
      cookie.setPath("/")
      store.add(new URI("http://a.example.com/"), cookie)

      assert(store.get(new URI("http://a.example.com/")).size() == 1)
      assert(store.get(new URI("http://b.example.com/")).size() == 0)
    }

    test("dot-domain only matches one subdomain level in JDK behavior") {
      val store = createStore()
      val cookie = new HttpCookie("dot", "1")
      cookie.setDomain(".example.com")
      cookie.setPath("/")
      store.add(new URI("http://example.com/"), cookie)

      assert(store.get(new URI("http://www.example.com/")).size() == 1)
      assert(store.get(new URI("http://x.y.example.com/")).size() == 0)
    }

    test("localhost and .local domain behavior") {
      val store = createStore()
      val localhostCookie = new HttpCookie("lc", "1")
      localhostCookie.setDomain("localhost")
      localhostCookie.setPath("/")
      store.add(new URI("http://localhost/"), localhostCookie)

      val localCookie = new HttpCookie("loc", "1")
      localCookie.setDomain(".local")
      localCookie.setPath("/")
      store.add(new URI("http://machine/"), localCookie)

      assert(store.get(new URI("http://localhost/")).size() == 2)
      assert(store.get(new URI("http://machine/")).size() == 1)
    }

    test("domain retrieval is case-insensitive") {
      val store = createStore()
      val cookie = new HttpCookie("ci", "1")
      cookie.setDomain("EXAMPLE.COM")
      cookie.setPath("/")
      store.add(new URI("http://example.com/"), cookie)

      assert(store.get(new URI("http://example.com/")).size() == 1)
      assert(store.get(new URI("http://EXAMPLE.COM/")).size() == 1)
    }

    test("getCookies does not duplicate equal cookies across URIs") {
      val store = createStore()
      val cookie1 = new HttpCookie("dup", "1")
      cookie1.setDomain("example.com")
      cookie1.setPath("/")
      val cookie2 = new HttpCookie("dup", "1")
      cookie2.setDomain("example.com")
      cookie2.setPath("/")

      store.add(new URI("http://example.com/a"), cookie1)
      store.add(new URI("http://example.com/b"), cookie2)

      assert(store.getCookies().size() == 1)
    }

    test("IPv4 domain matching behavior") {
      val store = createStore()
      val cookie = new HttpCookie("ip4", "1")
      cookie.setDomain("192.168.1.10")
      cookie.setPath("/")
      store.add(new URI("http://192.168.1.10/"), cookie)

      assert(store.get(new URI("http://192.168.1.10/")).size() == 1)
      assert(store.get(new URI("http://192.168.1.11/")).size() == 0)
    }

    test("IPv6 domain matching behavior") {
      val store = createStore()
      val cookie = new HttpCookie("ip6", "1")
      cookie.setDomain("[2001:db8::1]")
      cookie.setPath("/")
      store.add(new URI("http://[2001:db8::1]/"), cookie)

      assert(store.get(new URI("http://[2001:db8::1]/")).size() == 1)
    }

    test("CookieStore.get does not filter by request path") {
      val store = createStore()
      val uri = new URI("http://example.com/app")
      val cookie = new HttpCookie("test", "value")
      cookie.setDomain("example.com")
      cookie.setPath("/app")
      store.add(uri, cookie)

      // NOTE: CookieStore does NOT enforce path matching (JDK behavior).
      // Path filtering is handled by CookieManager.
      val retrieved = store.get(new URI("http://example.com/app/page"))
      val notRetrieved = store.get(new URI("http://example.com/other"))
      assert(retrieved.size() == 1)
      assert(notRetrieved.size() == 1)
    }

    test("secure cookies only match HTTPS") {
      val store = createStore()
      val uri = new URI("https://example.com/")
      val cookie = new HttpCookie("secure", "value")
      cookie.setDomain("example.com")
      cookie.setPath("/")
      cookie.setSecure(true)

      store.add(uri, cookie)

      assert(store.get(new URI("https://example.com/page")).size() == 1)
      assert(store.get(new URI("http://example.com/page")).size() == 0)
    }

    test("replacing cookie with same name/domain/path keeps latest value") {
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
      assert(retrieved.size() == 1)
      assert(retrieved.get(0).getValue() == "value2")
    }
  }
