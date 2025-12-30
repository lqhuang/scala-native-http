package snhttp.cookie

import java.net.{CookieHandler, CookieManager, CookiePolicy}
import java.net.http.HttpClient

class HttpClientCookieTests extends munit.FunSuite:

  test("HttpClient.Builder accepts CookieHandler") {
    val manager = new CookieManager()
    val client = HttpClient.newBuilder()
      .cookieHandler(manager)
      .build()

    assert(client.cookieHandler().isPresent)
    assertEquals(client.cookieHandler().get(), manager)
  }

  test("HttpClient without CookieHandler has empty Optional") {
    val client = HttpClient.newHttpClient()
    assert(client.cookieHandler().isEmpty)
  }

  test("HttpClient.Builder rejects null CookieHandler") {
    intercept[NullPointerException] {
      HttpClient.newBuilder().cookieHandler(null)
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
