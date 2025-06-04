import java.net.{InetSocketAddress, Proxy, SocketAddress}

import munit.FunSuite

class ProxyTest extends FunSuite {

  test("NO_PROXY should be of type DIRECT") {
    assertEquals(Proxy.NO_PROXY.`type`(), Proxy.Type.DIRECT)
    assertEquals(Proxy.NO_PROXY.address(), null)
  }

  test("create HTTP proxy with valid socket address") {
    val address = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    val proxy = Proxy(Proxy.Type.HTTP, address)

    assertEquals(proxy.`type`(), Proxy.Type.HTTP)
    assertEquals(proxy.address(), address)
  }

  test("create SOCKS proxy with valid socket address") {
    val address = InetSocketAddress.createUnresolved("socks.example.com", 1080)
    val proxy = Proxy(Proxy.Type.SOCKS, address)

    assertEquals(proxy.`type`(), Proxy.Type.SOCKS)
    assertEquals(proxy.address(), address)
  }

  test("throw exception when creating DIRECT proxy with address") {
    val address = InetSocketAddress.createUnresolved("example.com", 8080)

    intercept[IllegalArgumentException] {
      Proxy(Proxy.Type.DIRECT, address)
    }
  }

  test("throw exception when creating proxy with non-InetSocketAddress") {
    val invalidAddress = new SocketAddress() {}

    intercept[IllegalArgumentException] {
      Proxy(Proxy.Type.HTTP, invalidAddress)
    }
  }

  test("toString should return 'DIRECT' for NO_PROXY") {
    assertEquals(Proxy.NO_PROXY.toString, "DIRECT")
  }

  test("toString should return type @ address for proxied connections") {
    val address = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    val proxy = Proxy(Proxy.Type.HTTP, address)

    assertEquals(proxy.toString, s"HTTP @ $address")
  }

  test("equals should work correctly") {
    val address1 = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    val address2 = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    val address3 = InetSocketAddress.createUnresolved("other.example.com", 8080)

    val proxy1 = Proxy(Proxy.Type.HTTP, address1)
    val proxy2 = Proxy(Proxy.Type.HTTP, address2)
    val proxy3 = Proxy(Proxy.Type.HTTP, address3)
    val proxy4 = Proxy(Proxy.Type.SOCKS, address1)

    assertEquals(proxy1, proxy2)
    assertNotEquals(proxy1, proxy3)
    assertNotEquals(proxy1, proxy4)
    assertNotEquals(proxy1, null)

    assertEquals(Proxy.NO_PROXY, Proxy.NO_PROXY)
  }

  test("hashCode should be consistent with equals") {
    val address1 = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    val address2 = InetSocketAddress.createUnresolved("proxy.example.com", 8080)

    val proxy1 = Proxy(Proxy.Type.HTTP, address1)
    val proxy2 = Proxy(Proxy.Type.HTTP, address2)

    assertEquals(proxy1.hashCode(), proxy2.hashCode())

    assertEquals(Proxy.NO_PROXY.hashCode(), Proxy.Type.DIRECT.hashCode())
  }

  test("different proxy types should have different hash codes") {
    val address = InetSocketAddress.createUnresolved("proxy.example.com", 8080)
    val httpProxy = Proxy(Proxy.Type.HTTP, address)
    val socksProxy = Proxy(Proxy.Type.SOCKS, address)

    assertNotEquals(httpProxy.hashCode(), socksProxy.hashCode())
  }
}
