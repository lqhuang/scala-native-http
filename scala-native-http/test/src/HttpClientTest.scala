import java.net.{URI, InetSocketAddress}
import java.net.ProxySelector
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
// import javax.net.ssl.SSLContext

import java.util.concurrent.{Future, TimeUnit}
import java.util.concurrent.TimeoutException

class HttpClientSuite extends munit.FunSuite {

  // val emptyBodyHandler = HttpResponse.BodyHandlers.ofString()

  // test("HttpClient should send a GET request and receive a response") {
  //   val client = HttpClient.newBuilder().build()
  //   val request = HttpRequest.newBuilder(new URI("http://example.com")).GET().build()
  //   val resp = client.sendAsync(request, emptyBodyHandler).get()
  //   assertEquals(resp.statusCode, 200)
  //   assert(resp.body.nonEmpty)
  // }

  // test("HttpClient should handle timeouts correctly") {
  //   val client = HttpClient.newBuilder().build()
  //   val request = HttpRequest.newBuilder(new URI("http://example.com/timeout")).GET().build()

  //   val responseFuture = client.sendAsync(request, emptyBodyHandler).orTimeout(10, TimeUnit.SECONDS)

  //   intercept[TimeoutException] {
  //     responseFuture.get()
  //   }
  // }

  // test("HttpClient should handle SSL settings") {
  //   val client = HttpClient.newBuilder().sslContext(SSLContext.getDefault()).build()
  //   val request = HttpRequest.newBuilder(new URI("https://secure.example.com")).GET().build()
  //   val resp = client.sendAsync(request, emptyBodyHandler).get()
  //   assertEquals(resp.statusCode, 200)
  // }
}
