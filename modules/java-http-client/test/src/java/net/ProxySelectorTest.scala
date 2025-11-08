import java.net.{Proxy, ProxySelector}
import java.net.{URI, InetSocketAddress, SocketAddress}
import java.io.IOException
import java.util.List as JList

import utest.{Tests, test, assert, assertThrows}

class ProxySelectorTest extends utest.TestSuite {

  private class TestProxySelector extends ProxySelector {
    override def select(uri: URI): JList[Proxy] = JList.of(Proxy.NO_PROXY)
    override def connectFailed(uri: URI, sa: SocketAddress, ioe: IOException): Unit = {}
  }

  val tests = Tests {
    test("getDefault should return null when no system property is set") {
      val defaultSelector = ProxySelector.getDefault()
      assert(defaultSelector == null)
    }

    test("setDefault should set the system proxy selector") {
      val originalSelector = ProxySelector.getDefault()
      val customSelector = new TestProxySelector()

      ProxySelector.setDefault(customSelector)
      assert(ProxySelector.getDefault() == customSelector)

      ProxySelector.setDefault(originalSelector)
    }

    test("of with null address should create selector that returns NO_PROXY") {
      val selector = ProxySelector.of(null)
      val httpUri = new URI("http://example.com")
      val httpsUri = new URI("https://example.com")
      val ftpUri = new URI("ftp://example.com")

      val httpProxies = selector.select(httpUri)
      val httpsProxies = selector.select(httpsUri)
      val ftpProxies = selector.select(ftpUri)

      for proxy <- Seq(httpProxies, httpsProxies, ftpProxies) do {
        assert(proxy.size() == 1)
        assert(proxy.get(0) == Proxy.NO_PROXY)
      }

    }

    // // test("NullSelector will raise an exception on select") {
    // //   val url = new URL("http://127.0.0.1/");
    // //   ProxySelector.setDefault(null);
    // //   val con = url.openConnection();
    // //   con.setConnectTimeout(500);

    // //   intercept[IOException] {
    // //     con.connect()
    // //   }
    // // }

    // test("of with valid address should create selector for HTTP/HTTPS") {
    //   val proxyAddress = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    //   val selector = ProxySelector.of(proxyAddress)
    //   val expectedProxy = Proxy(Proxy.Type.HTTP, proxyAddress)

    //   val httpUri = new URI("http://example.com")
    //   val httpsUri = new URI("https://example.com")

    //   val httpProxies = selector.select(httpUri)
    //   val httpsProxies = selector.select(httpsUri)

    //   assert(httpProxies.size() == 1)
    //   assert(httpProxies.get(0) == expectedProxy)

    //   assert(httpsProxies.size() == 1)
    //   assert(httpsProxies.get(0) == expectedProxy)
    // }

    // test("static selector should return NO_PROXY for non-HTTP schemes") {
    //   val proxyAddress = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    //   val selector = ProxySelector.of(proxyAddress)

    //   val ftpUri = new URI("ftp://example.com")
    //   val socketUri = new URI("socket://example.com:1234")

    //   val ftpProxies = selector.select(ftpUri)
    //   val socketProxies = selector.select(socketUri)

    //   assert(ftpProxies.size(), 1)
    //   assert(ftpProxies.get(0), Proxy.NO_PROXY)

    //   assert(socketProxies.size(), 1)
    //   assert(socketProxies.get(0), Proxy.NO_PROXY)
    // }

    test("static selector select should throw on null URI") {
      val selector = ProxySelector.of(null)

      assertThrows[IllegalArgumentException] {
        selector.select(null): Unit
      }
    }

    test("static selector select should throw on URI without scheme") {
      val selector = ProxySelector.of(null)
      val uri = new URI("//example.com")

      assertThrows[IllegalArgumentException] {
        selector.select(uri): Unit
      }
    }

    // test("static selector connectFailed should throw on null arguments") {
    //   val selector = ProxySelector.of(null)
    //   val uri = new URI("http://example.com")
    //   val address = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    //   val exception = new IOException("Connection failed")

    //   intercept[IllegalArgumentException] {
    //     selector.connectFailed(null, address, exception)
    //   }
    //   intercept[IllegalArgumentException] {
    //     selector.connectFailed(uri, null, exception)
    //   }
    //   intercept[IllegalArgumentException] {
    //     selector.connectFailed(uri, address, null)
    //   }
    // }

    // test("static selector connectFailed should not throw with valid arguments") {
    //   val selector = ProxySelector.of(null)
    //   val uri = new URI("http://example.com")
    //   val address = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    //   val exception = new IOException("Connection failed")

    //   assertNoDiff(
    //     "", {
    //       selector.connectFailed(uri, address, exception)
    //       ""
    //     },
    //   )
    // }

    // test("case insensitive scheme matching") {
    //   val proxyAddress = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    //   val selector = ProxySelector.of(proxyAddress)
    //   val expectedProxy = Proxy(Proxy.Type.HTTP, proxyAddress)

    //   val httpUri = new URI("HTTP://example.com")
    //   val httpsUri = new URI("HTTPS://example.com")

    //   val httpProxies = selector.select(httpUri)
    //   val httpsProxies = selector.select(httpsUri)

    //   assert(httpProxies.size() == 1)
    //   assert(httpProxies.get(0) == expectedProxy)

    //   assert(httpsProxies.size() == 1)
    //   assert(httpsProxies.get(0) == expectedProxy)
    // }

    // test("-Djava.net.useSystemProxies=true") {
    //   val useSystemProxies = System.getProperty("java.net.useSystemProxies")
    //   val systemSelector = ProxySelector.getDefault()
    //   assert(systemSelector != null, "System proxy selector should not be null")
    //   val localURIs = List(
    //     "local",
    //     "localhost",
    //     "127.0.0.1",
    //   )
    //   /// TODO: unfinished test
    // }
  }
}
