package snhttp.test.java.net.http

import java.io.IOException
import java.net.http.HttpResponse.BodyHandlers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.List as JList
import java.util.concurrent.ExecutionException

import _root_.snhttp.test.jdk.net.http.MockSubscription
import _root_.snhttp.test.jdk.net.http.HttpClientTestUtils.createResponseInfo

import utest.{TestSuite, Tests, test, assert, assertThrows}

class BodyHandlersLimitingTest extends TestSuite:

  def tests = Tests:

    test("limiting should create limiting handler") {
      val downstreamHandler = BodyHandlers.ofString()
      val handler = BodyHandlers.limiting(downstreamHandler, 1024L)
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "Hello")
    }

    test("limiting should accept zero capacity with no data") {
      val downstreamHandler = BodyHandlers.ofString()
      val handler = BodyHandlers.limiting(downstreamHandler, 0L)
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "")
    }

    test("limiting should reject negative capacity") {
      val downstreamHandler = BodyHandlers.ofString()
      val _ = assertThrows[IllegalArgumentException] {
        BodyHandlers.limiting(downstreamHandler, -1L): Unit
      }
    }

    test("limiting should pass data at exact capacity boundary") {
      val downstreamHandler = BodyHandlers.ofString()
      val handler = BodyHandlers.limiting(downstreamHandler, 5L)
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))),
      ) // 5 == 5
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "Hello")
    }

    test("limiting should error when data exceeds capacity") {
      val downstreamHandler = BodyHandlers.ofString()
      val handler = BodyHandlers.limiting(downstreamHandler, 3L)
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))),
      ) // 5 > 3

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[IOException])
    }

    test("limiting should error when multiple buffers in one onNext together exceed capacity") {
      val downstreamHandler = BodyHandlers.ofByteArray()
      val handler = BodyHandlers.limiting(downstreamHandler, 4L)
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      // two buffers: "He" (2 bytes) + "llo" (3 bytes) = 5 > 4
      subscriber.onNext(
        JList.of(
          ByteBuffer.wrap("He".getBytes(StandardCharsets.UTF_8)),
          ByteBuffer.wrap("llo".getBytes(StandardCharsets.UTF_8)),
        ),
      )

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[IOException])
    }
