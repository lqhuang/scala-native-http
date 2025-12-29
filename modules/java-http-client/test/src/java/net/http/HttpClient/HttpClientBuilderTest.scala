import java.net.{Proxy, ProxySelector, URI, InetAddress}
import java.net.http.HttpClient
import java.net.http.HttpClient.{Redirect, Version}
import java.time.Duration
import java.util.Optional
import java.util.concurrent.Executors

import javax.net.ssl.SSLContext

import utest.{TestSuite, Tests, test, assert, assertThrows}

class HttpClientBuilderTest extends TestSuite:

  val tests = Tests:

    test("HttpClient.newBuilder() should create a builder") {
      val builder = HttpClient.newBuilder()
      assert(builder != null)

      val client = builder.build()
      assert(client != null)
    }

    test("HttpClient.newHttpClient() should create a default client") {
      val client = HttpClient.newHttpClient()

      assert(client != null)
      assert(client.version() == Version.HTTP_1_1)
      assert(client.followRedirects() == Redirect.NORMAL)
      assert(client.proxy() == Optional.empty())
      assert(client.connectTimeout() == Optional.empty())
      assert(client.executor() == Optional.empty())
    }

    test("HttpClient builder should configure connect timeout") {
      val timeout = Duration.ofSeconds(30)
      val client = HttpClient
        .newBuilder()
        .connectTimeout(timeout)
        .build()

      assert(client.connectTimeout() == Optional.of(timeout))
    }

    test("HttpClient builder should reject invalid connect timeout") {
      val builder = HttpClient.newBuilder()

      val _ = assertThrows[IllegalArgumentException] {
        builder.connectTimeout(Duration.ofSeconds(0)): Unit
      }

      val _ = assertThrows[IllegalArgumentException] {
        builder.connectTimeout(Duration.ofSeconds(-1)): Unit
      }

      val _ = assertThrows[NullPointerException] {
        builder.connectTimeout(null): Unit
      }
    }

    test("HttpClient builder should configure follow redirects") {
      val client1 = HttpClient
        .newBuilder()
        .followRedirects(Redirect.NEVER)
        .build()
      assert(client1.followRedirects() == Redirect.NEVER)

      val client2 = HttpClient
        .newBuilder()
        .followRedirects(Redirect.ALWAYS)
        .build()
      assert(client2.followRedirects() == Redirect.ALWAYS)

      val client3 = HttpClient
        .newBuilder()
        .followRedirects(Redirect.NORMAL)
        .build()
      assert(client3.followRedirects() == Redirect.NORMAL)
    }

    test("HttpClient builder should reject null redirect policy"):
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException]:
        builder.followRedirects(null): Unit

    test("HttpClient builder should configure HTTP version") {
      val client1 = HttpClient
        .newBuilder()
        .version(Version.HTTP_1_1)
        .build()
      assert(client1.version() == Version.HTTP_1_1)

      val client2 = HttpClient
        .newBuilder()
        .version(Version.HTTP_2)
        .build()
      assert(client2.version() == Version.HTTP_2)
    }

    test("HttpClient builder should reject null HTTP version"):
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException]:
        builder.version(null): Unit

    test("HttpClient builder should configure executor"):
      val executor = Executors.newCachedThreadPool()
      try {
        val client = HttpClient
          .newBuilder()
          .executor(executor)
          .build()
        assert(client.executor() == Optional.of(executor))
      } finally executor.shutdown()

    test("HttpClient builder should reject null executor") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException] {
        builder.executor(null): Unit
      }
    }

    test("HttpClient builder should configure priority") {
      val builder = HttpClient.newBuilder()

      // Valid priorities
      builder.priority(1)
      builder.priority(128)
      builder.priority(256)

      // Invalid priorities
      val _ = assertThrows[IllegalArgumentException]:
        builder.priority(0): Unit

      val _ = assertThrows[IllegalArgumentException]:
        builder.priority(257): Unit

      val _ = assertThrows[IllegalArgumentException]:
        builder.priority(-1): Unit
    }

    test("HttpClient builder should configure local address") {
      val localAddr = InetAddress.getLoopbackAddress()
      val client = HttpClient
        .newBuilder()
        .localAddress(localAddr)
        .build()

      // The client should be created successfully
      assert(client != null)
    }

    test("HttpClient builder should reject null local address"):
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException]:
        builder.localAddress(null): Unit

    test("HttpClient builder should chain method calls"):
      val executor = Executors.newSingleThreadExecutor()
      try {
        val localAddr = InetAddress.getLoopbackAddress()

        val client = HttpClient
          .newBuilder()
          .version(Version.HTTP_2)
          .followRedirects(Redirect.ALWAYS)
          .connectTimeout(Duration.ofSeconds(10))
          .executor(executor)
          .priority(100)
          .localAddress(localAddr)
          .build()

        assert(client.version() == Version.HTTP_2)
        assert(client.followRedirects() == Redirect.ALWAYS)
        assert(client.connectTimeout() == Optional.of(Duration.ofSeconds(10)))
        assert(client.executor() == Optional.of(executor))
      } finally executor.shutdown()

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

    test("HttpClient.Builder.NO_PROXY should be available") {
      val builder = HttpClient.newBuilder()
      val noProxy = HttpClient.Builder.NO_PROXY

      assert(noProxy != null)

      val exampleUri = URI.create("http://example.com")
      val defaultProxySelector = ProxySelector.getDefault()
      val client = builder.proxy(defaultProxySelector).build()

      assert(client.proxy().isPresent())
      assert(
        client
          .proxy()
          .get()
          .select(exampleUri)
          .toArray()
          .sameElements(
            noProxy.select(exampleUri).toArray(),
          ),
      )
    }
