import java.net.http.HttpClient

class WebSocketTest extends munit.FunSuite {
  test("HttpClient should create WebSocket builder") {
    val client = HttpClient.newHttpClient()
    val wsBuilder = client.newWebSocketBuilder()
    assertNotEquals(wsBuilder, null)
  }

  test("HttpClient should reject WebSocket builder creation after shutdown") {
    val client = HttpClient.newHttpClient()
    client.shutdown()

    intercept[IllegalStateException] {
      client.newWebSocketBuilder()
    }
  }
  test("HttpClient should handle WebSocket builder creation") {
    val client = HttpClient.newHttpClient()

    // Should create builder successfully
    val wsBuilder1 = client.newWebSocketBuilder()
    val wsBuilder2 = client.newWebSocketBuilder()

    assertNotEquals(wsBuilder1, null)
    assertNotEquals(wsBuilder2, null)

    // Should be different instances
    assert(wsBuilder1 ne wsBuilder2)
  }
}
