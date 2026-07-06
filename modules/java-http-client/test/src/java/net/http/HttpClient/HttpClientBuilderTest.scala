package snhttp.test.java.net.http

import java.net.{
  Authenticator,
  CookieManager,
  InetAddress,
  PasswordAuthentication,
  ProxySelector,
  URI,
}
import java.net.http.HttpClient
import java.net.http.HttpClient.{Redirect, Version}
import java.time.Duration
import java.util.Optional
import java.util.concurrent.Executors
import javax.net.ssl.{SSLContext, SSLParameters}

import scala.util.Properties

import utest.{TestSuite, Tests, test, assert, assertThrows}

class HttpClientBuilderTest extends TestSuite:

  def tests = Tests:

    test("HttpClient.newBuilder() should create a builder") {
      val builder = HttpClient.newBuilder()
      assert(builder != null)

      val client = builder.build()
      assert(client != null)
    }

    /*
     * doesn't pass on JVM
     */
    // test("HttpClient.newHttpClient() should create a default client") {
    //   val client = HttpClient.newHttpClient()

    //   assert(client != null)
    //   assert(client.version() == Version.HTTP_1_1)
    //   assert(client.followRedirects() == Redirect.NORMAL)
    //   assert(client.proxy() == Optional.empty())
    //   assert(client.connectTimeout() == Optional.empty())
    //   assert(client.executor() == Optional.empty())
    // }

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

    test("HttpClient builder should reject null redirect policy") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException]:
        builder.followRedirects(null): Unit
    }

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

    test("HttpClient builder should reject null HTTP version") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException] {
        builder.version(null): Unit
      }
    }

    // test("HttpClient builder should configure executor") {
    //   val executor = Executors.newCachedThreadPool()
    //   try {
    //     val client = HttpClient
    //       .newBuilder()
    //       .executor(executor)
    //       .build()
    //     assert(client.executor() == Optional.of(executor))
    //   } finally executor.shutdown()
    // }

    test("HttpClient builder should reject null executor") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException] {
        builder.executor(null): Unit
      }
    }

    test("HttpClient builder should configure proxy selector") {
      val proxy = HttpClient.Builder.NO_PROXY
      val client = HttpClient.newBuilder().proxy(proxy).build()

      assert(client.proxy() == Optional.of(proxy))
    }

    test("HttpClient builder should reject null proxy selector") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException] {
        builder.proxy(null): Unit
      }
    }

    test("HttpClient builder should configure authenticator") {
      val authenticator = new Authenticator:
        override protected def getPasswordAuthentication(): PasswordAuthentication =
          PasswordAuthentication("user", "password".toCharArray())

      val client = HttpClient.newBuilder().authenticator(authenticator).build()

      assert(client.authenticator() == Optional.of(authenticator))
    }

    test("HttpClient builder should reject null authenticator") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException] {
        builder.authenticator(null): Unit
      }
    }

    test("HttpClient builder should configure SSL context") {
      val context = SSLContext.getDefault()
      val client = HttpClient.newBuilder().sslContext(context).build()

      assert(client.sslContext() == context)
    }

    test("HttpClient builder should reject null SSL context") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException] {
        builder.sslContext(null): Unit
      }
    }

    test("HttpClient builder should defensively copy SSL parameters") {
      val params = SSLParameters(null, Array("TLSv1.2"))

      val client = HttpClient.newBuilder().sslParameters(params).build()
      params.setProtocols(Array("TLSv1.3"))

      val firstRead = client.sslParameters()
      val secondRead = client.sslParameters()
      firstRead.setProtocols(Array("TLSv1.1"))

      assert(secondRead.getProtocols().sameElements(Seq("TLSv1.2")))
      assert(client.sslParameters().getProtocols().sameElements(Seq("TLSv1.2")))
    }

    test("HttpClient builder should reject null SSL parameters") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException] {
        builder.sslParameters(null): Unit
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

    /*
     * doesn't pass on JVM
     */
    // test("HttpClient builder should reject null local address") {
    //   val builder = HttpClient.newBuilder()
    //   assertThrows[NullPointerException] {
    //     builder.localAddress(null): Unit
    //   }
    // }

    test("HttpClient builder should chain method calls") {
      val localAddr = InetAddress.getLoopbackAddress()
      val client = HttpClient
        .newBuilder()
        .version(Version.HTTP_2)
        .followRedirects(Redirect.ALWAYS)
        .connectTimeout(Duration.ofSeconds(10))
        .priority(100)
        .localAddress(localAddr)
        .build()

      assert(client.version() == Version.HTTP_2)
      assert(client.followRedirects() == Redirect.ALWAYS)
      assert(client.connectTimeout() == Optional.of(Duration.ofSeconds(10)))
      assert(client.executor() == Optional.empty())
    }

    test("HttpClient enum values should be correct") {
      val isNative = Properties.propOrEmpty("java.vm.name") == "Scala Native"
      val isH3Available = Properties.isJavaAtLeast(26) || isNative

      // Test Version enum
      assert(Version.values().contains(Version.HTTP_1_1))
      assert(Version.values().contains(Version.HTTP_2))

      if isH3Available
      then
        assert(Version.values().length == 3)
        assert(Version.values().contains(Version.valueOf("HTTP_3")))
      else //
        assert(Version.values().length == 2)

      // Test Redirect enum
      assert(Redirect.values().length == 3)
      assert(Redirect.values().contains(Redirect.NEVER))
      assert(Redirect.values().contains(Redirect.ALWAYS))
      assert(Redirect.values().contains(Redirect.NORMAL))
    }

    test("HttpClient.Builder.NO_PROXY should be available") {
      val noProxy = HttpClient.Builder.NO_PROXY

      assert(noProxy != null)

      val exampleUri = URI.create("http://example.com")
      val client = HttpClient.newBuilder().proxy(noProxy).build()

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

    test("HttpClient builder should configure cookie handler") {
      val manager = new CookieManager()
      val client = HttpClient.newBuilder().cookieHandler(manager).build()

      assert(client.cookieHandler().isPresent())
      assert(client.cookieHandler().get() == manager)
    }

    test("HttpClient builder should reject null cookie handler") {
      val builder = HttpClient.newBuilder()
      assertThrows[NullPointerException] {
        builder.cookieHandler(null): Unit
      }
    }

    test("HttpClient builder without cookie handler should keep empty optional") {
      val client = HttpClient.newBuilder().build()
      assert(client.cookieHandler() == Optional.empty())
    }
