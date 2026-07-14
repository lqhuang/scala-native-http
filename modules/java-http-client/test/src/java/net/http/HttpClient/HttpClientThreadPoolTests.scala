package snhttp.test.java.net.http

import java.net.URI
import java.net.http.{HttpClient, HttpRequest}
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.HttpRequest.BodyPublishers
import java.time.Duration

import utest.{TestSuite, test, assert, Tests}

object HttpClientThreadPoolTests extends TestSuite:

  import ClientUtils.withNewHttpClient

  def runBenchmark(
      name: String,
      requestCount: Int = 100,
      maxThreadGrowth: Int = 20,
  )(makeRequest: (String, Int) => Unit): Unit =
    ServerUtils.usingEchoServer { port =>
      val url = s"http://localhost:${port}/echo"
      val initialThreadCount = Thread.activeCount()

      val start = System.currentTimeMillis()
      for (i <- 0 until requestCount) do makeRequest(url, i)
      val end = System.currentTimeMillis()

      println(s"${name}: ${requestCount} requests in ${end - start}ms")

      val finalThreadCount = Thread.activeCount()
      val threadGrowth = finalThreadCount - initialThreadCount
      assert(threadGrowth < maxThreadGrowth)
    }

  val tests = Tests:

    test("warmup") {
      // Uses the shared HttpClient from the default session (requests object)
      withNewHttpClient { client =>
        runBenchmark("warmup", 20) { (url, i) =>
          val request = HttpRequest
            .newBuilder()
            .uri(URI.create(url))
            .POST(BodyPublishers.ofString(s"request ${i}"))
            .build()

          client.send(request, BodyHandlers.ofString()): Unit
        }
      }
    }

    test("HttpClient should reuse a bounded set of worker threads") {
      withNewHttpClient { client =>
        runBenchmark("raw HttpClient (shared)") { (url, i) =>
          val request = HttpRequest
            .newBuilder()
            .uri(URI.create(url))
            .POST(BodyPublishers.ofString(s"request ${i}"))
            .build()

          client.send(request, BodyHandlers.ofString()): Unit
        }
      }
    }
