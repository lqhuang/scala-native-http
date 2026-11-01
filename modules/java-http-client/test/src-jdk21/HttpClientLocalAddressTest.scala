package snhttp.test.java.net.http

import java.net.InetAddress
import java.net.http.HttpClient
import java.net.http.HttpClient.{Redirect, Version}
import java.time.Duration
import java.util.Optional

import utest.{TestSuite, Tests, test, assert}

class HttpClientLocalAddressTest extends TestSuite:
  def tests = Tests:
    test("HttpClient builder should configure local address") {
      val localAddr = InetAddress.getLoopbackAddress()
      val client = HttpClient.newBuilder().localAddress(localAddr).build()
      try assert(client != null)
      finally ClientUtils.closeClient(client)
    }

    test("HttpClient should handle various local address configurations") {
      val loopback = InetAddress.getLoopbackAddress()
      val anyLocal = InetAddress.getByName("0.0.0.0")

      val client1 = HttpClient.newBuilder().localAddress(loopback).build()
      val client2 = HttpClient.newBuilder().localAddress(anyLocal).build()

      try
        assert(client1 != null)
        assert(client2 != null)
      finally
        ClientUtils.closeClient(client1)
        ClientUtils.closeClient(client2)
    }

    test("HttpClient builder should chain local address with other options") {
      val client = HttpClient
        .newBuilder()
        .version(Version.HTTP_2)
        .followRedirects(Redirect.ALWAYS)
        .connectTimeout(Duration.ofSeconds(10))
        .priority(100)
        .localAddress(InetAddress.getLoopbackAddress())
        .build()

      try
        assert(client.version() == Version.HTTP_2)
        assert(client.followRedirects() == Redirect.ALWAYS)
        assert(client.connectTimeout() == Optional.of(Duration.ofSeconds(10)))
        assert(client.executor() == Optional.empty())
      finally ClientUtils.closeClient(client)
    }
