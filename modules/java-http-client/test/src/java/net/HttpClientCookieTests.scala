package snhttp.cookie

import java.net.{CookieHandler, CookieManager, CookiePolicy}
import java.net.http.HttpClient

import utest.{Tests, test, assert, assertThrows}

class HttpClientCookieTests extends utest.TestSuite:

  val tests = Tests {
    test("HttpClient.Builder accepts CookieHandler") {
      val manager = new CookieManager()
      val client = HttpClient.newBuilder()
        .cookieHandler(manager)
        .build()

      assert(client.cookieHandler().isPresent)
      assert(client.cookieHandler().get() == manager)
    }

    test("HttpClient without CookieHandler has empty Optional") {
      val client = HttpClient.newHttpClient()
      assert(client.cookieHandler().isEmpty)
    }

    test("HttpClient.Builder rejects null CookieHandler") {
      assertThrows[NullPointerException] {
        HttpClient.newBuilder().cookieHandler(null): Unit
      }
    }

    test("CookieHandler can be retrieved after build") {
      val manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL)
      val client = HttpClient.newBuilder()
        .cookieHandler(manager)
        .build()

      val handler = client.cookieHandler().get()
      assert(handler.isInstanceOf[CookieManager])
    }
  }
