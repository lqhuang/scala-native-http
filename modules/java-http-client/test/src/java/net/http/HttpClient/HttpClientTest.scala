package snhttp.test.java.net.http

import java.io.IOException
import java.net.URI
import java.net.ConnectException
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpClient.{Redirect, Version}
import java.net.http.HttpResponse.{BodyHandlers, BodySubscribers, PushPromiseHandler}
import java.net.http.HttpRequest.BodyPublishers
import java.nio.charset.StandardCharsets
import java.nio.channels.{ClosedChannelException, UnresolvedAddressException}
import java.time.Duration
import java.util.Optional
import java.util.concurrent.ExecutionException
import java.util.concurrent.{TimeUnit, ConcurrentHashMap}
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ssl.SSLContext

import scala.util.Properties

import utest.{TestSuite, Tests, test, assert, assertThrows}

class HttpClientTest extends TestSuite:

  inline def httpbinEndpoint(path: String, secure: Boolean = true): URI =
    if secure
    then URI.create(s"https://httpbingo.org${path}")
    else URI.create(s"http://httpbingo.org${path}")

  inline def withNewHttpClient[T](func: HttpClient => T): T =
    val client = HttpClient.newHttpClient()
    try func(client)
    finally client.close()

  val isNative = Properties.propOrEmpty("java.vm.name") == "Scala Native"

  def tests = Tests:

    test("HttpClient.newHttpClient should expose documented defaults") {
      withNewHttpClient { client =>
        assert(client.version() == Version.HTTP_2)
        assert(client.followRedirects() == Redirect.NEVER)
        assert(client.cookieHandler().isEmpty())
        assert(client.connectTimeout().isEmpty())
        assert(client.authenticator().isEmpty())
        assert(client.executor().isEmpty())
        assert(client.proxy().isEmpty())
        assert(client.sslContext() == SSLContext.getDefault())
        assert(client.sslParameters() != null)
        assert(client.isTerminated() == false)
      }
    }

    test("HttpClient send() should reject null parameters") {
      withNewHttpClient { client =>
        val request = HttpRequest.newBuilder(URI.create("http://example.com/")).build()
        val bodyHandler = BodyHandlers.ofString()

        val _ = assertThrows[NullPointerException]:
          client.send(null, bodyHandler): Unit

        val _ = assertThrows[NullPointerException]:
          client.send(request, null): Unit
      }
    }

    test("HttpClient sendAsync() should reject null parameters") {
      withNewHttpClient { client =>
        val request = HttpRequest.newBuilder(URI.create("http://example.com/")).build()
        val bodyHandler = BodyHandlers.ofString()

        val _ = assertThrows[NullPointerException] {
          client.sendAsync(null, bodyHandler): Unit
        }

        assertThrows[NullPointerException] {
          client.sendAsync(request, null): Unit
        }
      }
    }

    test("HttpClient sendAsync() should reject null parameters") {
      withNewHttpClient { client =>
        val request = HttpRequest.newBuilder(URI.create("http://example.com")).build()
        val bodyHandler = BodyHandlers.ofString()
        val pushPromiseHandler = HttpResponse.PushPromiseHandler.of[String](
          _ => bodyHandler,
          new ConcurrentHashMap(),
        )

        val _ = assertThrows[NullPointerException]:
          val _ = client.sendAsync(null, bodyHandler, pushPromiseHandler)

        val _ = assertThrows[NullPointerException]:
          val _ = client.sendAsync(request, null, pushPromiseHandler)
      }
    }

    test("HttpClient should raise ConnectException for invalid host") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = BodyHandlers.ofString()

      test("send") {
        val request = HttpRequest
          .newBuilder(URI.create("http://nonexistent.invalid"))
          .GET()
          .build()
        assertThrows[ConnectException]:
          client.send(request, bodyHandler): Unit
      }

      test("sendAsync") {
        val request = HttpRequest
          .newBuilder(URI.create("http://nonexistent.invalid"))
          .GET()
          .build()
        val future = client.sendAsync(request, bodyHandler)
        val exc = assertThrows[ExecutionException]:
          future.get(): Unit
        assert(exc.getCause().isInstanceOf[ConnectException])
        // assert(exc.getCause().getCause().isInstanceOf[UnresolvedAddressException])
      }

    }

    test(
      "HttpClient should raise ConnectException for unreachable destination",
    ) {
      val client = HttpClient.newHttpClient()
      val bodyHandler = BodyHandlers.ofString()
      val uri = URI.create("http://127.0.0.1:10")

      test("send") {
        val request = HttpRequest
          .newBuilder(uri) // resolved but unreachable port
          .GET()
          .build()

        assertThrows[ConnectException]:
          client.send(request, bodyHandler): Unit
      }

      test("sendAsync") {
        val request = HttpRequest
          .newBuilder(uri)
          .GET()
          .build()
        val future = client.sendAsync(request, bodyHandler)
        val exc = assertThrows[ExecutionException]:
          future.get(): Unit
        assert(exc.getCause().isInstanceOf[ConnectException])
        // assert(exc.getCause().getCause().isInstanceOf[ClosedChannelException])
      }

    }

    test("HttpClient should handle basic request/response cycle") {
      withNewHttpClient { client =>
        val client = HttpClient.newHttpClient()
        val request = HttpRequest
          .newBuilder(URI.create("http://example.com"))
          .GET()
          .build()
        val bodyHandler = BodyHandlers.ofString()

        // This should complete without throwing exceptions
        val future = client.sendAsync(request, bodyHandler)
        assert(future != null)

        // The future should complete
        val response = future.get(5, TimeUnit.SECONDS)
        assert(response != null)
        assert(response.request() == request)
      }
    }

    test("HttpClient should handle various URI schemes") {
      val client = HttpClient.newHttpClient()
      val bodyHandler = BodyHandlers.ofString()

      val schemes = List("http", "https")

      schemes.foreach { scheme =>
        val request = HttpRequest
          .newBuilder(URI.create(s"${scheme}://example.com"))
          .GET()
          .build()

        val future = client.sendAsync(request, bodyHandler)
        val response = future.get(5, TimeUnit.SECONDS)
        assert(response.uri().getScheme() == scheme)
      }
    }

    test("HttpClient should handle different HTTP methods") {
      withNewHttpClient { client =>
        val schemes = Seq("http", "https")
        val methods = Seq("GET", "POST", "PUT", "DELETE", "HEAD")

        schemes.foreach { scheme =>
          methods.foreach { method =>
            val requestBuilder =
              HttpRequest.newBuilder(
                URI.create(s"${scheme}://httpbingo.org/${method.toLowerCase()}"),
              )
            val request = method match {
              case "GET"    => requestBuilder.GET().build()
              case "POST"   => requestBuilder.POST(BodyPublishers.ofString("test")).build()
              case "PUT"    => requestBuilder.PUT(BodyPublishers.ofString("test")).build()
              case "DELETE" => requestBuilder.DELETE().build()
              case "HEAD"   => requestBuilder.method("HEAD", BodyPublishers.noBody()).build()
            }
            val future = client.sendAsync(request, BodyHandlers.ofString())
            val response = future.get(3, TimeUnit.SECONDS)
            assert(response.request().method() == method)
          }
        }
      }
    }

    test("HttpClient shutdown lifecycle should reject later operations") {
      withNewHttpClient { client =>
        assert(client.isTerminated() == false)

        // Shutdown
        client.shutdown()

        // Further requests should fail
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

    test("HttpClient awaitTermination should report termination after shutdownNow") {
      withNewHttpClient { client =>

        client.sendAsync(
          HttpRequest.newBuilder(URI.create("https://example.org")).build(),
          BodyHandlers.ofString(),
        )

        val result1 = client.awaitTermination(Duration.ofMillis(50))
        assert(result1 == false)

        // Shutdown and wait
        client.shutdownNow()
        val result2 = client.awaitTermination(Duration.ofSeconds(1))
        assert(result2 == true)
      }
    }

    test("HttpClient awaitTermination should reject null and non-positive durations") {
      withNewHttpClient { client =>
        val _ = assertThrows[NullPointerException] {
          client.awaitTermination(null): Unit
        }
        assert(client.awaitTermination(Duration.ofSeconds(-1)) == false)
        assert(client.awaitTermination(Duration.ofSeconds(0)) == false)
      }
    }

    test("HttpClient close() should shutdown gracefully") {
      val client = HttpClient.newHttpClient()
      assert(client.isTerminated() == false)
      // This should not throw
      client.close()
      // Should be terminated after close
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
      // Give it a moment to start, then interrupt
      Thread.sleep(50)
      thread.interrupt()
      thread.join(1000)

      // The thread should have been interrupted
      assert(thread.isInterrupted() || interrupted)
    }

    test("HttpClient sendAsync with null push promise handler should match two-argument overload") {
      withNewHttpClient { client =>
        val request = HttpRequest.newBuilder(httpbinEndpoint("/")).build()
        val response = client
          .sendAsync(request, BodyHandlers.ofString(), null)
          .get(2, TimeUnit.SECONDS)
        assert(response.statusCode() == 200)
      }
    }

    test("HttpClient sendAsync() with push promise handler should reject null request or handler") {
      withNewHttpClient { client =>
        val request = HttpRequest.newBuilder(URI.create("http://localhost/")).build()
        val bodyHandler = BodyHandlers.ofString()
        val pushPromiseHandler = PushPromiseHandler.of[String](
          _ => bodyHandler,
          new ConcurrentHashMap(),
        )

        val _ = assertThrows[NullPointerException]:
          val _ = client.sendAsync(null, bodyHandler, pushPromiseHandler)

        val _ = assertThrows[NullPointerException]:
          val _ = client.sendAsync(request, null, pushPromiseHandler)
      }
    }

    // test("HttpClient should accept a push promise handler for HTTP/1.1 responses") {
    //   TestHttpServer.withRoutes { server =>
    //     val pushes = new AtomicInteger(0)
    //     server.enqueue(_ => TestResponse.ok("ok"))
    //     val request = HttpRequest.newBuilder(server.uri("/push-handler")).build()
    //     val pushPromiseHandler = PushPromiseHandler.of[String](
    //       _ => {
    //         pushes.incrementAndGet()
    //         BodyHandlers.ofString()
    //       },
    //       new ConcurrentHashMap(),
    //     )
    //   }

    //     val response = HttpClient
    //       .newHttpClient()
    //       .sendAsync(request, BodyHandlers.ofString(), pushPromiseHandler)
    //       .get(2, TimeUnit.SECONDS)

    //     assert(response.statusCode() == 200)
    //     assert(pushes.get() == 0)
    //   }
    // }

    test("Redirect.NEVER should return the redirect response without following it") {
      test("absolute redirect") {
        val client = HttpClient.newBuilder().followRedirects(Redirect.NEVER).build()
        Seq(true, false).foreach { secure =>
          val request =
            HttpRequest.newBuilder(httpbinEndpoint("/absolute-redirect/3", secure)).build()
          val response = client.send(request, BodyHandlers.ofString())
          assert(response.statusCode() == 302)
        }
        client.close()
      }
      test("relative redirect") {
        val client = HttpClient.newBuilder().followRedirects(Redirect.NEVER).build()
        Seq(true, false).foreach { secure =>
          val request =
            HttpRequest.newBuilder(httpbinEndpoint("/relative-redirect/3", secure)).build()
          val response = client.send(request, BodyHandlers.ofString())
          assert(response.statusCode() == 302)
        }
        client.close()
      }
      test("custom redirect code") {
        val client = HttpClient.newBuilder().followRedirects(Redirect.NEVER).build()
        Seq(
          301, 302, 303, 307, 308,
        ).foreach { status =>
          val request =
            HttpRequest
              .newBuilder(
                httpbinEndpoint(s"/redirect-to?url=www.example.org&status_code=${status}"),
              )
              .build()
          val response = client.send(request, BodyHandlers.ofString())
          assert(response.statusCode() == status)
        }

        client.close()
      }
    }

    test("Redirect.NORMAL should follow same-scheme redirects") {
      val client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build()

      test("http -> http") {
        val request =
          HttpRequest
            .newBuilder(
              httpbinEndpoint("/redirect-to?url=http://example.org&status_code=302", secure = false),
            )
            .build()
        val response = client.send(request, BodyHandlers.ofString())
        assert(response.statusCode() == 200)
        assert(response.uri().getHost() == "example.org")
        assert(response.uri().getScheme() == "http")
        assert(response.version() == Version.HTTP_1_1)
      }

      test("https -> https") {
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint("/redirect-to?url=https://example.org&status_code=302", secure = true),
          )
          .build()
        val response = client.send(request, BodyHandlers.ofString())
        assert(response.statusCode() == 200)
        assert(response.uri().getHost() == "example.org")
        assert(response.uri().getScheme() == "https")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())
      }

      test("http -> https") {
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint("/redirect-to?url=https://example.org&status_code=302", secure = false),
          )
          .build()
        val response = client.send(request, BodyHandlers.ofString())
        assert(response.statusCode() == 200)
        assert(response.uri().getHost() == "example.org")
        assert(response.uri().getScheme() == "https")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())
      }

      test("https -> http") {
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint("/redirect-to?http://example.org&status_code=302"),
          )
          .build()
        val response = client.send(request, BodyHandlers.ofString())
        // 400 Bad Request due to https -> http redirect not allowed
        assert(response.statusCode() == 400)
        assert(response.uri().getHost() == "httpbingo.org")
        assert(response.uri().getScheme() == "https")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())
      }
    }

    test("Redirect.ALWAYS allow different scheme redirects") {
      val client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build()

      test("http -> http") {
        val request =
          HttpRequest
            .newBuilder(
              httpbinEndpoint("/redirect-to?url=http://example.org&status_code=302", secure = false),
            )
            .build()
        val response = client.send(request, BodyHandlers.ofString())
        assert(response.statusCode() == 200)
        assert(response.uri().getHost() == "example.org")
        assert(response.uri().getScheme() == "http")
        assert(response.version() == Version.HTTP_1_1)
      }

      test("https -> https") {
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint("/redirect-to?url=https://example.org&status_code=302", secure = true),
          )
          .build()
        val response = client.send(request, BodyHandlers.ofString())
        assert(response.statusCode() == 200)
        assert(response.uri().getHost() == "example.org")
        assert(response.uri().getScheme() == "https")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())
      }

      test("http -> https") {
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint("/redirect-to?url=https://example.org&status_code=302", secure = false),
          )
          .build()
        val response = client.send(request, BodyHandlers.ofString())
        assert(response.statusCode() == 200)
        assert(response.uri().getHost() == "example.org")
        assert(response.uri().getScheme() == "https")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())
      }

      test("https -> http") {
        // XXX: ????? different behavior with documentation
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint("/redirect-to?http://example.org&status_code=302", secure = true),
          )
          .build()
        val response = client.send(request, BodyHandlers.ofString())
        assert(response.statusCode() == 400)
        assert(response.uri().getHost() == "httpbingo.org")
        assert(response.uri().getScheme() == "https")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())
      }

    }

    test("Redirect 307 and 308 should preserve method and body") {
      List(307, 308).foreach { status =>
        val client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build()
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint(s"/redirect-to?status_code=${status}&url=https://httpbingo.org/post"),
          )
          .POST(BodyPublishers.ofString("keep-me"))
          .build()
        val response = client.send(request, BodyHandlers.ofString())
        assert(response.statusCode() == 200)
        assert(response.uri().getPath() == "/post")

        val jsonBody = ujson.read(response.body())
        // a2VlcC1tZQ== -> base64("keep-me")
        assert(jsonBody("data").str == "data:application/octet-stream;base64,a2VlcC1tZQ==")
        client.close()
      }
    }

    test("Redirect STATUS Code 303/302/301 should switch POST to GET") {
      val client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build()
      Seq(301, 302, 303).foreach { status =>
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint(s"/redirect-to?status_code=${status}&url=https://httpbingo.org/get"),
          )
          .POST(BodyPublishers.ofString("do-not-repeat"))
          .build()
        val response = client.send(request, BodyHandlers.ofString())

        assert(response.statusCode() == 200)
        assert(response.uri().getPath() == "/get")
        assert(response.request().method() == "GET")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())
      }
    }

    test("STATUS Code 301/302 should not switch PUT to GET") {
      val client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build()

      Seq(301, 302).foreach { status =>
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint(s"/redirect-to?status_code=${status}&url=https://httpbingo.org/put"),
          )
          .PUT(BodyPublishers.ofString("do-not-repeat"))
          .build()
        val response = client.send(request, BodyHandlers.ofString())

        assert(response.uri().getPath() == "/put")
        assert(response.request().method() == "PUT")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())

        if isNative
        then //
          assert(response.statusCode() == 405)
        else //
          assert(response.statusCode() == 200)
      }

      client.close()
    }

    /**
     * Inconsistent behavior between JDK and Curl
     */
    test("STATUS Code 303 should change Every method except for HEAD to GET - PUT & DELETE") {
      val client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build()

      Seq("PUT", "DELETE").foreach { method =>
        val request = HttpRequest
          .newBuilder(
            httpbinEndpoint(
              s"/redirect-to?status_code=303&url=https://httpbingo.org/get",
            ),
          )
          .method(method, BodyPublishers.ofString("do-not-repeat"))
          .build()
        val response = client.send(request, BodyHandlers.ofString())

        assert(response.uri().getPath() == "/get")
        assert(response.version().ordinal() >= Version.HTTP_2.ordinal())

        if isNative
        then
          assert(response.statusCode() == 200) // why not 405 ???
          assert(response.request().method() == method) // wired ...
        else
          assert(response.statusCode() == 200)
          assert(response.request().method() == "GET")
      }

      client.close()
    }

    /**
     * Inconsistent behavior between JDK and Curl
     */
    test("STATUS Code 303 should not change HEAD to GET") {
      val client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build()

      val request = HttpRequest
        .newBuilder(
          httpbinEndpoint(
            s"/redirect-to?status_code=303&url=https://httpbingo.org/head",
          ),
        )
        .method("HEAD", BodyPublishers.noBody())
        .build()
      val headResponse = client.send(request, BodyHandlers.ofString())

      assert(headResponse.uri().getPath() == "/head")
      assert(headResponse.version().ordinal() >= Version.HTTP_2.ordinal())

      if isNative
      then
        assert(headResponse.statusCode() == 200)
        assert(headResponse.request().method() == "HEAD")
      else
        assert(headResponse.statusCode() == 405)
        assert(headResponse.request().method() == "GET") // wired ...

      client.close()
    }

    test("HttpClient should send GET and expose response metadata") {
      val client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build()
      val request = HttpRequest
        .newBuilder(httpbinEndpoint("/response-headers?X-Test=metadata"))
        .GET()
        .build()
      val response = client
        .send(request, BodyHandlers.ofString())

      assert(response.statusCode() == 200)
      assert(response.request() == request)
      assert(response.uri() == request.uri())
      assert(response.headers().firstValue("X-Test") == Optional.of("metadata"))
      // FIXME: `sslSession` not implemented yet
      // assert(response.sslSession().isPresent())
      // assert(response.connectionLabel().isPresent())

      client.close()
    }

    test("HttpClient should not send extra headers by default") {
      withNewHttpClient { client =>
        val request = HttpRequest
          .newBuilder(httpbinEndpoint("/headers"))
          .GET()
          .build()
        val response = client.send(request, BodyHandlers.ofString())

        assert(response.statusCode() == 200)

        val jsonBody = ujson.read(response.body())
        assert(jsonBody("headers")("User-Agent")(0).str.contains("/"))

        val headers = jsonBody("headers").obj
        assert(!headers.contains("Content-Type"))
        assert(!headers.contains("Accept"))
      }
    }

    test("HttpClient should send all configured Headers") {
      withNewHttpClient { client =>
        val request = HttpRequest
          .newBuilder(httpbinEndpoint("/headers"))
          .GET()
          .header("X-Test", "metadata")
          .header("X-Test-2", "metadata-2")
          .header("Content-Type", "application/json")
          .build()
        val response = client
          .send(request, BodyHandlers.ofString())

        assert(response.statusCode() == 200)

        val jsonBody = ujson.read(response.body())

        assert(jsonBody("headers")("X-Test")(0).str == "metadata")
        assert(jsonBody("headers")("X-Test-2")(0).str == "metadata-2")
        assert(jsonBody("headers")("Content-Type")(0).str == "application/json")
        assert(jsonBody("headers")("User-Agent")(0).str.contains("/"))
      }
    }
