import java.lang.Thread
import java.net.{URI, InetAddress, InetSocketAddress}
import java.net.{Proxy, ProxySelector}
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpClient.{Redirect, Version}
import java.time.Duration
import java.util.{List as JList, Optional}
import java.util.concurrent.{
  ConcurrentHashMap,
  Executors,
  Future,
  TimeUnit,
  CompletableFuture,
  CountDownLatch,
}
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong}
import javax.net.ssl.SSLContext

import utest.{TestSuite, Tests, test, assert, assertThrows}

class HttpClientTest extends TestSuite:

  val emptyBodyHandler = HttpResponse.BodyHandlers.ofString()

  val tests = Tests:

    test("HttpClient send() should reject null parameters") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      val _ = assertThrows[NullPointerException]:
        client.send(null, bodyHandler): Unit

      assertThrows[NullPointerException]:
        client.send(request, null): Unit
    }

    test("HttpClient sendAsync() should reject null parameters") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      val _ = assertThrows[NullPointerException] {
        client.sendAsync(null, bodyHandler): Unit
      }

      assertThrows[NullPointerException] {
        client.sendAsync(request, null): Unit
      }
    }

    test("HttpClient sendAsync() should reject null parameters") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()
      val pushPromiseHandler = HttpResponse.PushPromiseHandler.of[String](
        _ => bodyHandler,
        new ConcurrentHashMap(),
      )

      val _ = assertThrows[NullPointerException]:
        val _ = client.sendAsync(null, bodyHandler, pushPromiseHandler)

      val _ = assertThrows[NullPointerException]:
        val _ = client.sendAsync(request, null, pushPromiseHandler)
    }

    // test("HttpClient should handle basic request/response cycle") {
    //   val client = HttpClient.newHttpClient()
    //   val request = HttpRequest
    //     .newBuilder(URI.create("http://example.com"))
    //     .GET()
    //     .build()
    //   val bodyHandler = HttpResponse.BodyHandlers.ofString()

    //   // This should complete without throwing exceptions
    //   val future = client.sendAsync(request, bodyHandler)
    //   assert(future != null)

    //   // The future should complete (even if with a mock response)
    //   val response = future.get(5, TimeUnit.SECONDS)
    //   assert(response != null)
    //   assert(response.request() == request)
    // }

    // test("HttpClient should handle various URI schemes") {
    //   val client = HttpClient.newHttpClient()
    //   val bodyHandler = HttpResponse.BodyHandlers.ofString()

    //   val schemes = List("http", "https")

    //   schemes.foreach { scheme =>
    //     val request = HttpRequest
    //       .newBuilder(URI.create(s"${scheme}://example.com"))
    //       .GET()
    //       .build()

    //     val future = client.sendAsync(request, bodyHandler)
    //     val response = future.get(5, TimeUnit.SECONDS)
    //     assert(response.uri().getScheme() == scheme)
    //   }
    // }

    // test("HttpClient should handle different HTTP methods") {
    //   val client = HttpClient.newHttpClient()
    //   val bodyHandler = HttpResponse.BodyHandlers.ofString()
    //   val uri = URI.create("http://example.com")

    //   val methods = List("GET", "POST", "PUT", "DELETE", "HEAD")

    //   methods.foreach { method =>
    //     val requestBuilder = HttpRequest.newBuilder(uri)
    //     val request = method match {
    //       case "GET"    => requestBuilder.GET().build()
    //       case "POST"   => requestBuilder.POST(HttpRequest.BodyPublishers.ofString("test")).build()
    //       case "PUT"    => requestBuilder.PUT(HttpRequest.BodyPublishers.ofString("test")).build()
    //       case "DELETE" => requestBuilder.DELETE().build()
    //       case "HEAD" => requestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody()).build()
    //     }

    //     val future = client.sendAsync(request, bodyHandler)
    //     val response = future.get(5, TimeUnit.SECONDS)
    //     assert(response.request().method() == method)
    //   }
    // }

    test("HttpClient shutdown lifecycle") {
      val client = HttpClient.newHttpClient()

      // Initially not terminated
      assert(client.isTerminated() == false)

      // Shutdown
      client.shutdown()

      // Further requests should fail
      val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      val _ = assertThrows[IllegalStateException] {
        client.send(request, bodyHandler): Unit
      }

      val _ = assertThrows[IllegalStateException] {
        client.sendAsync(request, bodyHandler): Unit
      }
    }

    test("HttpClient awaitTermination should respect timeout") {
      val client = HttpClient.newHttpClient()

      // Should return false immediately since not shutdown
      val result1 = client.awaitTermination(Duration.ofMillis(100))
      assert(result1, false)

      // Shutdown and wait
      client.shutdownNow()
      val result2 = client.awaitTermination(Duration.ofSeconds(1))
      assert(result2, true)
    }

    test("HttpClient awaitTermination should reject null duration") {
      val client = HttpClient.newHttpClient()
      assertThrows[NullPointerException] {
        client.awaitTermination(null): Unit
      }
    }

    test("HttpClient close() should shutdown gracefully") {
      val client = HttpClient.newHttpClient()

      // This should not throw
      client.close()

      // Should be terminated after close
      assert(client.isTerminated() == true)
    }

    // test("HttpClient should handle interrupted threads during close") {
    //   val client = HttpClient.newHttpClient()
    //   val interrupted = new AtomicBoolean(false)

    //   val thread = new Thread(() =>
    //     try client.close()
    //     catch case _: InterruptedException => interrupted.set(true),
    //   )

    //   thread.start()
    //   // Give it a moment to start, then interrupt
    //   Thread.sleep(50)
    //   thread.interrupt()
    //   thread.join(1000)

    //   // The thread should have been interrupted
    //   assert(thread.isInterrupted() || interrupted.get())
    // }
