package snhttp.test.java.net.http

import java.io.IOException
import java.net.URI
import java.net.http.{HttpClient, HttpRequest}
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import java.util.concurrent.ExecutionException

import utest.{TestSuite, Tests, test, assert, assertThrows}

class HttpClientLifecycleTest extends TestSuite:

  import ClientUtils.withNewHttpClient

  def tests = Tests:
    test("HttpClient should reject later operations after shutdown") {
      withNewHttpClient { client =>
        assert(client.isTerminated() == false)
        client.shutdown()

        val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
        val bodyHandler = BodyHandlers.ofString()

        val _ = assertThrows[IOException] {
          client.send(request, bodyHandler): Unit
        }

        val cf = client.sendAsync(request, bodyHandler)
        val exc = assertThrows[ExecutionException] {
          cf.get(): Unit
        }
        assert(exc.getCause().isInstanceOf[IOException])
      }
    }

    test("awaitTermination should report termination after shutdownNow") {
      withNewHttpClient { client =>
        client.sendAsync(
          HttpRequest.newBuilder(URI.create("https://example.org")).build(),
          BodyHandlers.ofString(),
        )

        val result1 = client.awaitTermination(Duration.ofMillis(50))
        assert(result1 == false)

        client.shutdownNow()
        val result2 = client.awaitTermination(Duration.ofSeconds(1))
        assert(result2 == true)
      }
    }

    test("awaitTermination should reject null and non-positive durations") {
      withNewHttpClient { client =>
        val _ = assertThrows[NullPointerException] {
          client.awaitTermination(null): Unit
        }
        assert(client.awaitTermination(Duration.ofSeconds(-1)) == false)
        assert(client.awaitTermination(Duration.ofSeconds(0)) == false)
      }
    }

    test("close() should shutdown gracefully") {
      val client = HttpClient.newHttpClient()
      assert(client.isTerminated() == false)
      client.close()
      assert(client.isTerminated() == true)
    }

    test("HttpClient should handle interrupted threads during close") {
      val client = HttpClient.newHttpClient()
      var interrupted = false

      val thread = new Thread(() =>
        try client.close()
        catch
          case _: InterruptedException =>
            interrupted = true,
      )

      thread.start()
      Thread.sleep(50)
      thread.interrupt()
      thread.join(1000)

      assert(thread.isInterrupted() || interrupted)
    }
