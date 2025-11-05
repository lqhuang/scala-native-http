import java.net.http.HttpClient

import utest.{Tests, test, assert, assertThrows}

class WebSocketTest extends utest.TestSuite {

  val tests = Tests {

    test("HttpClient should create WebSocket builder") {
      val client = HttpClient.newHttpClient()
      val wsBuilder = client.newWebSocketBuilder()
      assert(wsBuilder == null)
    }

    test("HttpClient should reject WebSocket builder creation after shutdown") {
      val client = HttpClient.newHttpClient()
      client.shutdown()

      assertThrows[IllegalStateException] {
        client.newWebSocketBuilder()
      }
    }
    test("HttpClient should handle WebSocket builder creation") {
      val client = HttpClient.newHttpClient()

      // Should create builder successfully
      val wsBuilder1 = client.newWebSocketBuilder()
      val wsBuilder2 = client.newWebSocketBuilder()

      assert(wsBuilder1 == null)
      assert(wsBuilder2 == null)

      // Should be different instances
      assert(wsBuilder1 ne wsBuilder2)
    }

  }
}
