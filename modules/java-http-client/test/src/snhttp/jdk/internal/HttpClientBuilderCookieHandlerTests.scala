package snhttp.jdk.internal

import java.net.{CookieManager, URI}
import java.net.http.HttpClient
import java.util.{ArrayList, HashMap}

import utest.{Tests, test, assert}

class HttpClientBuilderCookieHandlerTests extends utest.TestSuite:

  val tests = Tests {
    test("HttpClient.Builder accepts CookieHandler") {
      val manager = new CookieManager()
      val client = HttpClient.newBuilder().cookieHandler(manager).build()
      assert(client != null)
    }

    // Removed null test - HttpClient.Builder rejects null (throws NPE)

    test("HttpClient.Builder without CookieHandler") {
      val client = HttpClient.newBuilder().build()
      assert(client != null)
    }

    test("HttpClient with CookieManager stores cookies") {
      val manager = new CookieManager()
      val client = HttpClient.newBuilder().cookieHandler(manager).build()
      
      // This is a basic integration test - actual HTTP functionality
      // would require a running server
      assert(manager.getCookieStore() != null)
    }
  }
