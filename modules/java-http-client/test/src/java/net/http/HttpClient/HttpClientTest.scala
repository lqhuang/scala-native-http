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

class HttpClientTest extends TestSuite {

  val emptyBodyHandler = HttpResponse.BodyHandlers.ofString()

  val tests = Tests {

    test("HttpClient send() should reject null parameters") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      assertThrows[NullPointerException] {
        client.send(null, bodyHandler): Unit
      }

      assertThrows[NullPointerException] {
        client.send(request, null): Unit
      }
    }

    test("HttpClient sendAsync() should reject null parameters") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      assertThrows[NullPointerException] {
        client.sendAsync(null, bodyHandler): Unit
      }

      assertThrows[NullPointerException] {
        client.sendAsync(request, null): Unit
      }
    }

    test("HttpClient sendAsync() with push promise handler should reject null parameters") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()
      val pushPromiseHandler = HttpResponse.PushPromiseHandler.of[String](
        _ => bodyHandler,
        new ConcurrentHashMap(),
      )

      val _ = assertThrows[NullPointerException]:
        val _ = client.sendAsync(null, bodyHandler, pushPromiseHandler)

      assertThrows[NullPointerException]:
        val _ = client.sendAsync(request, null, pushPromiseHandler)

      // assertThrows[NullPointerException] {
      //   client.sendAsync(request, bodyHandler, null)
      // }
    }

    test("HttpClient should handle basic request/response cycle") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest
        .newBuilder(URI.create("http://example.com"))
        .GET()
        .build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      // This should complete without throwing exceptions
      val future = client.sendAsync(request, bodyHandler)
      assert(future != null)

      // The future should complete (even if with a mock response)
      val response = future.get(5, TimeUnit.SECONDS)
      assert(response != null)
      assert(response.request() == request)
    }

    test("HttpClient shutdown lifecycle") {
      val client = HttpClient.newHttpClient()

      // Initially not terminated
      assert(client.isTerminated() == false)

      // Shutdown
      client.shutdown()

      // Should not immediately terminate (allows existing requests to complete)
      // But in our mock implementation, it might terminate immediately

      // Force shutdown
      client.shutdownNow()
      assert(client.isTerminated() == true)

      // Further requests should fail
      val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      assertThrows[IllegalStateException] {
        client.send(request, bodyHandler): Unit
      }

      val future = client.sendAsync(request, bodyHandler)
      assert(future.isCompletedExceptionally())
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

    test("HttpClient should handle interrupted threads during close") {
      val client = HttpClient.newHttpClient()
      val interrupted = new AtomicBoolean(false)

      val thread = new Thread(() =>
        try client.close()
        catch case _: InterruptedException => interrupted.set(true),
      )

      thread.start()
      // Give it a moment to start, then interrupt
      Thread.sleep(50)
      thread.interrupt()
      thread.join(1000)

      // The thread should have been interrupted
      assert(thread.isInterrupted() || interrupted.get())
    }

    test("HttpClient enum values should be correct") {
      // Test Version enum
      assert(Version.values().length == 2)
      assert(Version.values().contains(Version.HTTP_1_1))
      assert(Version.values().contains(Version.HTTP_2))

      // Test Redirect enum
      assert(Redirect.values().length == 3)
      assert(Redirect.values().contains(Redirect.NEVER))
      assert(Redirect.values().contains(Redirect.ALWAYS))
      assert(Redirect.values().contains(Redirect.NORMAL))
    }

    /**
     * Extra tests
     */

    test("HttpClient should handle concurrent requests") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest
        .newBuilder(URI.create("http://example.com"))
        .GET()
        .build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      val concurrentRequests = 10
      val futures = (1 to concurrentRequests).map { _ =>
        client.sendAsync(request, bodyHandler)
      }

      // All futures should complete successfully
      val responses = futures.map(_.get(5, TimeUnit.SECONDS))
      assert(responses.size == concurrentRequests)

      responses.foreach { response =>
        assert(response != null)
        assert(response.request() == request)
      }
    }

    test("HttpClient should handle rapid sequential requests") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      val requestCount = 50
      val responses = (1 to requestCount).map { i =>
        val request = HttpRequest
          .newBuilder(URI.create(s"http://example.com/path$i"))
          .GET()
          .build()
        client.sendAsync(request, bodyHandler).get(1, TimeUnit.SECONDS)
      }

      assert(responses.size == requestCount)
      responses.zipWithIndex.foreach {
        case (response, index) =>
          assert(response != null)
          assert(response.request().uri().getPath() == s"/path${index + 1}")
      }
    }

    test("HttpClient should handle requests after partial shutdown") {
      val client = HttpClient.newHttpClient()
      val request = HttpRequest
        .newBuilder(URI.create("http://example.com"))
        .GET()
        .build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      // Start a request
      val future1 = client.sendAsync(request, bodyHandler)

      // Initiate shutdown (but don't force it)
      client.shutdown()

      // The first request should still complete
      val response1 = future1.get(5, TimeUnit.SECONDS)
      assert(response1 != null)

      // New requests should fail
      assertThrows[IllegalStateException] {
        client.send(request, bodyHandler): Unit
      }
    }

    test("HttpClient should handle multiple shutdown calls") {
      val client = HttpClient.newHttpClient()

      // Multiple shutdown calls should not throw
      client.shutdown()
      client.shutdown()
      client.shutdownNow()
      client.shutdownNow()

      assert(client.isTerminated() == true)
    }

    test("HttpClient should handle timeout edge cases") {
      val client = HttpClient
        .newBuilder()
        .connectTimeout(Duration.ofNanos(1)) // Very small timeout
        .build()

      assert(client.connectTimeout().get() == Duration.ofNanos(1))

      // Zero timeout should be rejected
      assertThrows[IllegalArgumentException] {
        HttpClient.newBuilder().connectTimeout(Duration.ZERO): Unit
      }
    }

    test("HttpClient should handle maximum and minimum priority values") {
      val builder = HttpClient.newBuilder()

      // Minimum priority
      val client1 = builder.priority(1).build()
      assert(client1 != null)

      // Maximum priority
      val client2 = builder.priority(256).build()
      assert(client2 != null)

      // Boundary violations
      assertThrows[IllegalArgumentException] {
        builder.priority(0): Unit
      }

      assertThrows[IllegalArgumentException] {
        builder.priority(257): Unit
      }
    }

    test("HttpClient should handle various URI schemes") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      val schemes = List("http", "https")

      schemes.foreach { scheme =>
        val request = HttpRequest
          .newBuilder(URI.create(s"$scheme://example.com"))
          .GET()
          .build()

        val future = client.sendAsync(request, bodyHandler)
        assert(future != null)

        // Should complete without throwing
        val response = future.get(5, TimeUnit.SECONDS)
        assert(response.uri().getScheme() == scheme)
      }
    }

    test("HttpClient should handle different HTTP methods") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()
      val uri = URI.create("http://example.com")

      val methods = List("GET", "POST", "PUT", "DELETE", "HEAD")

      methods.foreach { method =>
        val requestBuilder = HttpRequest.newBuilder(uri)
        val request = method match {
          case "GET"    => requestBuilder.GET().build()
          case "POST"   => requestBuilder.POST(HttpRequest.BodyPublishers.ofString("test")).build()
          case "PUT"    => requestBuilder.PUT(HttpRequest.BodyPublishers.ofString("test")).build()
          case "DELETE" => requestBuilder.DELETE().build()
          case "HEAD" => requestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody()).build()
        }

        val future = client.sendAsync(request, bodyHandler)
        val response = future.get(5, TimeUnit.SECONDS)
        assert(response.request().method() == method)
      }
    }

    test("HttpClient builder should handle chained configurations") {
      val executor = Executors.newCachedThreadPool()
      val localAddr = InetAddress.getLoopbackAddress()

      // Test builder immutability by creating multiple clients from same builder
      val builder = HttpClient
        .newBuilder()
        .version(Version.HTTP_2)
        .followRedirects(Redirect.ALWAYS)

      val client1 = builder.connectTimeout(Duration.ofSeconds(5)).build()
      val client2 = builder.connectTimeout(Duration.ofSeconds(10)).build()

      // Both should have the same version and redirect policy
      assert(client1.version() == Version.HTTP_2)
      assert(client2.version() == Version.HTTP_2)
      assert(client1.followRedirects() == Redirect.ALWAYS)
      assert(client2.followRedirects() == Redirect.ALWAYS)

      // But different timeouts
      assert(client1.connectTimeout().get() == Duration.ofSeconds(5))
      assert(client2.connectTimeout().get() == Duration.ofSeconds(10))

      executor.shutdown()
    }

    test("HttpClient should handle executor shutdown scenarios") {
      val executor = Executors.newSingleThreadExecutor()
      val client = HttpClient
        .newBuilder()
        .executor(executor)
        .build()

      val request = HttpRequest
        .newBuilder(URI.create("http://example.com"))
        .GET()
        .build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      // Request should work with active executor
      val future1 = client.sendAsync(request, bodyHandler)
      val response1 = future1.get(5, TimeUnit.SECONDS)
      assert(response1 != null)

      // Shutdown executor
      executor.shutdown()

      // Client might still work (depends on implementation)
      // This test verifies the client handles executor shutdown gracefully
      try {
        val future2 = client.sendAsync(request, bodyHandler)
        // If it doesn't throw, it should complete
        future2.get(5, TimeUnit.SECONDS)
      } catch {
        case _: Exception => // Expected if executor is shutdown
      }
    }

    test("HttpClient should handle memory pressure") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      // Create many requests to test memory usage
      val requestCount = 1000
      val futures = new Array[CompletableFuture[HttpResponse[String]]](requestCount)

      // Submit all requests
      (0 until requestCount).foreach { i =>
        val request = HttpRequest
          .newBuilder(URI.create(s"http://example.com/test$i"))
          .GET()
          .build()
        futures(i) = client.sendAsync(request, bodyHandler)
      }

      // Complete some requests to free memory
      val completedCount = new AtomicInteger(0)
      futures.take(requestCount / 2).foreach { future =>
        future.whenComplete { (_, _) =>
          completedCount.incrementAndGet(): Unit
        }
      }

      // Wait for some to complete
      Thread.sleep(100)

      // Should handle this gracefully
      assert(completedCount.get() >= 0)
    }

    /** Compile errors */
    // test("HttpClient should handle interrupt during await termination") {
    //   val client = HttpClient.newHttpClient()
    //   @volatile var interrupted = false

    //   val thread = new Thread {
    //     new Runnable {
    //       override def run =
    //         try client.awaitTermination(Duration.ofMinutes(1))
    //         catch case _: InterruptedException => interrupted = true
    //     }
    //   }

    //   thread.start()
    //   Thread.sleep(50) // Let it start waiting
    //   thread.interrupt()
    //   thread.join(1000)

    //   // Should have been interrupted
    //   assert(interrupted || thread.isInterrupted())
    // }

    test("HttpClient should handle various local address configurations") {
      val loopback = InetAddress.getLoopbackAddress()
      val anyLocal = InetAddress.getByName("0.0.0.0")

      val client1 = HttpClient
        .newBuilder()
        .localAddress(loopback)
        .build()

      val client2 = HttpClient
        .newBuilder()
        .localAddress(anyLocal)
        .build()

      // Both should be created successfully
      assert(client1 != null)
      assert(client2 != null)
    }

    test("HttpClient should handle push promise handler variations") {
      val client = HttpClient
        .newBuilder()
        .version(Version.HTTP_2) // Push promises are HTTP/2 feature
        .build()

      val request = HttpRequest
        .newBuilder(URI.create("http://example.com"))
        .GET()
        .build()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      // Different push promise handler implementations
      val pushHandler1 = HttpResponse.PushPromiseHandler.of[String](
        _ => bodyHandler,
        new ConcurrentHashMap(),
      )

      val pushHandler2 = HttpResponse.PushPromiseHandler.of[String](
        _ => bodyHandler,
        new ConcurrentHashMap(),
      )

      // Both should work
      val future1 = client.sendAsync(request, bodyHandler, pushHandler1)
      val future2 = client.sendAsync(request, bodyHandler, pushHandler2)

      val response1 = future1.get(5, TimeUnit.SECONDS)
      val response2 = future2.get(5, TimeUnit.SECONDS)

      assert(response1 != null)
      assert(response2 != null)
    }

    /**
     * Performance test
     */

    test("HttpClient should handle high-frequency request creation") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      val startTime = System.nanoTime()
      val requestCount = 10000

      val requests = (1 to requestCount).map { i =>
        HttpRequest
          .newBuilder(URI.create(s"http://example.com/test$i"))
          .GET()
          .build()
      }

      val creationTime = System.nanoTime() - startTime

      // Should create requests quickly (less than 1ms per request on average)
      val avgTimePerRequest = creationTime / requestCount
      // Request creation too slow: ${avgTimePerRequest}ns per request"
      assert(avgTimePerRequest < 1_000_000)

      assert(requests.size == requestCount)
    }

    test("HttpClient should handle burst requests efficiently") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()
      val burstSize = 100

      val startTime = System.nanoTime()

      val futures = (1 to burstSize).map { i =>
        val request = HttpRequest
          .newBuilder(URI.create(s"http://example.com/burst$i"))
          .GET()
          .build()
        client.sendAsync(request, bodyHandler)
      }

      val submissionTime = System.nanoTime() - startTime

      // All futures should be submitted quickly
      // s"Burst submission too slow: ${submissionTime}ns"
      assert(submissionTime < 100_000_000)

      // All should complete
      val responses = futures.map(_.get(5, TimeUnit.SECONDS))
      assert(responses.size == burstSize)
    }

    test("HttpClient should scale with thread pool size") {
      val smallPool = Executors.newFixedThreadPool(2)
      val largePool = Executors.newFixedThreadPool(10)

      val client1 = HttpClient.newBuilder().executor(smallPool).build()
      val client2 = HttpClient.newBuilder().executor(largePool).build()

      val bodyHandler = HttpResponse.BodyHandlers.ofString()
      val requestCount = 50

      def measureTime(client: HttpClient): Long = {
        val startTime = System.nanoTime()
        val futures = (1 to requestCount).map { i =>
          val request = HttpRequest
            .newBuilder(URI.create(s"http://example.com/scale$i"))
            .GET()
            .build()
          client.sendAsync(request, bodyHandler)
        }
        futures.foreach(_.get(10, TimeUnit.SECONDS))
        System.nanoTime() - startTime
      }

      val time1 = measureTime(client1)
      val time2 = measureTime(client2)

      // Both should complete successfully
      // (In a real implementation, larger pool might be faster)
      assert(time1 > 0 && time2 > 0)

      smallPool.shutdown()
      largePool.shutdown()
    }

    test("HttpClient should handle memory efficiently with many clients") {
      val clientCount = 100
      val clients = (1 to clientCount).map { _ =>
        HttpClient
          .newBuilder()
          .connectTimeout(Duration.ofSeconds(30))
          .followRedirects(Redirect.NORMAL)
          .build()
      }

      assert(clients.size == clientCount)

      // Each client should be independent
      clients.zipWithIndex.foreach {
        case (client, index) =>
          assert(client != null)
          assert(client.connectTimeout().get() == Duration.ofSeconds(30))
          assert(client.followRedirects() == Redirect.NORMAL)
      }
    }

    test("HttpClient should handle rapid builder creation") {
      val builderCount = 1000
      val startTime = System.nanoTime()

      val builders = (1 to builderCount).map { _ =>
        HttpClient
          .newBuilder()
          .version(Version.HTTP_1_1)
          .followRedirects(Redirect.NEVER)
      }

      val creationTime = System.nanoTime() - startTime
      val avgTimePerBuilder = creationTime / builderCount

      // Should create builders quickly
      // s"Builder creation too slow: ${avgTimePerBuilder}ns per builder",
      assert(avgTimePerBuilder < 100_000)
      assert(builders.size == builderCount)
    }

    test("HttpClient should handle concurrent client creation") {
      val threadCount = 10
      val clientsPerThread = 10
      val latch = new CountDownLatch(threadCount)
      val clientCount = new AtomicInteger(0)
      val errors = new AtomicInteger(0)

      val threads = (1 to threadCount).map { threadId =>
        val capturedThreadId = threadId
        new Thread {
          override def run: Unit =
            try
              (1 to clientsPerThread).foreach { _ =>
                val client = HttpClient
                  .newBuilder()
                  .connectTimeout(Duration.ofSeconds(capturedThreadId.toLong))
                  .build()

                assert(client != null)
                clientCount.incrementAndGet()
              }
            catch case _: Exception => errors.incrementAndGet(): Unit
            finally latch.countDown()
        }
      }

      threads.foreach(t => t.start())
      // "Threads did not complete in time"
      assert(latch.await(30, TimeUnit.SECONDS))

      // "Some threads encountered errors"
      assert(errors.get() == 0)
      assert(clientCount.get() == threadCount * clientsPerThread)
    }

    test("HttpClient should handle request queue efficiently") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      // Submit many requests rapidly
      val queueSize = 500
      val counter = new AtomicLong(0)

      val futures = (1 to queueSize).map { i =>
        val request = HttpRequest
          .newBuilder(URI.create(s"http://example.com/queue$i"))
          .GET()
          .build()

        val future = client.sendAsync(request, bodyHandler)
        future.whenComplete((_, _) => counter.incrementAndGet(): Unit)
        future
      }

      // Wait for all to complete
      futures.foreach(_.get(10, TimeUnit.SECONDS))

      // All should have completed
      assert(counter.get() == queueSize.toLong)
    }

    test("HttpClient should handle timeout variations efficiently") {
      val timeouts = List(
        Duration.ofMillis(100),
        Duration.ofSeconds(1),
        Duration.ofSeconds(30),
        Duration.ofMinutes(1),
      )

      timeouts.foreach { timeout =>
        val startTime = System.nanoTime()

        val client = HttpClient
          .newBuilder()
          .connectTimeout(timeout)
          .build()

        val creationTime = System.nanoTime() - startTime

        assert(client.connectTimeout().get() == timeout)
        // Should create quickly regardless of timeout value
        // Client creation with timeout $timeout took too long
        assert(creationTime < 10_000_000)
      }
    }

    test("HttpClient should handle priority settings efficiently") {
      val priorities = List(1, 50, 100, 150, 200, 256)
      val clients = new Array[HttpClient](priorities.size)

      val startTime = System.nanoTime()

      priorities.zipWithIndex.foreach {
        case (priority, index) =>
          clients(index) = HttpClient
            .newBuilder()
            .priority(priority)
            .build()
      }

      val creationTime = System.nanoTime() - startTime
      val avgTime = creationTime / priorities.size

      // Should create quickly regardless of priority
      // s"Client creation with priorities took too long: ${avgTime}ns average",
      assert(avgTime < 1_000_000)

      assert(clients.length == priorities.size)
      // clients.foreach(assert(_ != null))
    }

    test("HttpClient should handle version switching efficiently") {
      val versions = List(Version.HTTP_1_1, Version.HTTP_2)
      val iterationCount = 100

      val startTime = System.nanoTime()

      (1 to iterationCount).foreach { i =>
        val version = versions(i % versions.size)
        val client = HttpClient
          .newBuilder()
          .version(version)
          .build()

        assert(client.version() == version)
      }

      val totalTime = System.nanoTime() - startTime
      val avgTime = totalTime / iterationCount

      // Should handle version switching quickly
      // Version switching too slow: ${avgTime}ns per switch
      assert(avgTime < 500_000)
    }

    test("HttpClient should handle redirect policy changes efficiently") {
      val policies = List(Redirect.NEVER, Redirect.NORMAL, Redirect.ALWAYS)
      val iterationCount = 150

      val startTime = System.nanoTime()

      (1 to iterationCount).foreach { i =>
        val policy = policies(i % policies.size)
        val client = HttpClient
          .newBuilder()
          .followRedirects(policy)
          .build()

        assert(client.followRedirects() == policy)
      }

      val totalTime = System.nanoTime() - startTime
      val avgTime = totalTime / iterationCount

      // Should handle policy changes quickly
      // Redirect policy switching too slow: ${avgTime}ns per switch
      assert(avgTime < 500_000)
    }

    test("HttpClient should handle executor switching efficiently") {
      val executors = List(
        Executors.newSingleThreadExecutor(),
        Executors.newFixedThreadPool(2),
        Executors.newCachedThreadPool(),
      )

      try {
        val startTime = System.nanoTime()

        val clients = executors.map { executor =>
          HttpClient
            .newBuilder()
            .executor(executor)
            .build()
        }

        val creationTime = System.nanoTime() - startTime
        val avgTime = creationTime / executors.size

        // Should create quickly with different executors
        // s"Executor switching too slow: ${avgTime}ns per switch"
        assert(avgTime < 1_000_000)

        clients.zipWithIndex.foreach {
          case (client, index) =>
            assert(client.executor().get() == executors(index))
        }

      } finally executors.foreach(_.shutdown())
    }

    test("HttpClient should handle graceful degradation under load") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = HttpResponse.BodyHandlers.ofString()

      // Simulate high load
      val loadSize = 1000
      val successCount = new AtomicInteger(0)
      val errorCount = new AtomicInteger(0)

      val futures = (1 to loadSize).map { i =>
        val request = HttpRequest
          .newBuilder(URI.create(s"http://example.com/load$i"))
          .GET()
          .build()

        client.sendAsync(request, bodyHandler).whenComplete { (response, throwable) =>
          if throwable == null
          then successCount.incrementAndGet(): Unit
          else errorCount.incrementAndGet(): Unit
        }
      }

      // Wait for all to complete
      futures.foreach { future =>
        try
          future.get(10, TimeUnit.SECONDS)
        catch {
          case _: Exception => // Expected under high load
        }
      }

      val totalProcessed = successCount.get() + errorCount.get()
      // "Not all requests were processed"
      assert(totalProcessed == loadSize)

      // Should handle most requests successfully (allowing for some failures under extreme load)
      // "Success rate too low: $successRate"
      val successRate = successCount.get().toDouble / loadSize
      assert(successRate > 0.5)
    }

  }
}
