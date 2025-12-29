import java.net.InetSocketAddress
import java.net.Proxy

import utest.{TestSuite, Tests, test, assert, assertThrows}

class ProxyTest extends TestSuite:

  val tests = Tests:

    test("NO_PROXY should be of type DIRECT"):
      assert(Proxy.NO_PROXY.`type`() == Proxy.Type.DIRECT)
      assert(Proxy.NO_PROXY.address() == null)

    test("create HTTP proxy with valid socket address") {
      val address = InetSocketAddress.createUnresolved("localhost", 8080)
      val proxy = Proxy(Proxy.Type.HTTP, address)

      assert(proxy.`type`() == Proxy.Type.HTTP)
      assert(proxy.address() == address)
    }

    test("create SOCKS proxy with valid socket address") {
      val address = InetSocketAddress.createUnresolved("socks.example.com", 1080)
      val proxy = Proxy(Proxy.Type.SOCKS, address)

      assert(proxy.`type`() == Proxy.Type.SOCKS)
      assert(proxy.address() == address)
    }

    test("throw exception when creating DIRECT proxy with address") {
      val address = InetSocketAddress.createUnresolved("example.com", 8080)

      assertThrows[IllegalArgumentException] {
        Proxy(Proxy.Type.DIRECT, address): Unit
      }
    }

    test("toString should return 'DIRECT' for NO_PROXY"):
      assert(Proxy.NO_PROXY.toString() == "DIRECT")

    test("toString should return type @ address for proxied connections") {
      val address = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
      val proxy = Proxy(Proxy.Type.HTTP, address)

      assert(proxy.toString() == s"HTTP @ $address")
    }

    test("equals should work correctly") {
      val address1 = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
      val address2 = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
      val address3 = InetSocketAddress.createUnresolved("other.example.com", 8080)

      val proxy1 = Proxy(Proxy.Type.HTTP, address1)
      val proxy2 = Proxy(Proxy.Type.HTTP, address2)
      val proxy3 = Proxy(Proxy.Type.HTTP, address3)
      val proxy4 = Proxy(Proxy.Type.SOCKS, address1)
      val direct = Proxy(Proxy.Type.DIRECT, null)

      assert(proxy1 == proxy2)
      assert(proxy1 != proxy3)
      assert(proxy1 != proxy4)
      assert(proxy1 != null)

      assert(Proxy.NO_PROXY == Proxy.NO_PROXY)
      assert(Proxy.NO_PROXY == direct)
    }
