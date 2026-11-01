package snhttp.test.java.net.http

import java.io.IOException
import java.net.http.HttpResponse.BodySubscribers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.List as JList

import _root_.snhttp.test.jdk.net.http.{MockBodySubscriber, MockSubscription, SPException}

import utest.{TestSuite, Tests, test, assert, assertThrows}

class BodySubscribersLimitingTest extends TestSuite:

  val tests = Tests:

    test("limiting should pass data within capacity") {
      val downstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val subscriber = BodySubscribers.limiting(downstream, 100L)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "Hello")
    }

    test("limiting should error when exceeding capacity") {
      val downstream = MockBodySubscriber[String]()
      val subscriber = BodySubscribers.limiting(downstream, 3L)

      val sub = MockSubscription()
      subscriber.onSubscribe(sub)
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))),
      ) // 5 > 3

      assert(sub.cancelled == true)
      assert(downstream.errors == 1)
      assert(downstream.lastError.isInstanceOf[IOException])
    }

    test("limiting should reject negative capacity") {
      val _ = assertThrows[IllegalArgumentException] {
        BodySubscribers.limiting(BodySubscribers.ofString(StandardCharsets.UTF_8), -1L): Unit
      }
    }

    test("limiting should accept zero capacity with no data") {
      val downstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val subscriber = BodySubscribers.limiting(downstream, 0L)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "")
    }

    test("limiting should error when exceeding zero capacity") {
      val downstream = MockBodySubscriber[String]()
      val subscriber = BodySubscribers.limiting(downstream, 0L)

      val sub = MockSubscription()
      subscriber.onSubscribe(sub)
      subscriber.onNext(JList.of(ByteBuffer.wrap("X".getBytes()))) // 1 > 0

      assert(sub.cancelled == true)
      assert(downstream.errors == 1)
      assert(downstream.lastError.isInstanceOf[IOException])
    }

    test("limiting should pass data at exact capacity boundary") {
      val downstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val subscriber = BodySubscribers.limiting(downstream, 5L)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))),
      ) // 5 == 5
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "Hello")
    }

    test("limiting should error on second chunk that exceeds cumulative capacity") {
      val downstream = MockBodySubscriber[String]()
      val subscriber = BodySubscribers.limiting(downstream, 5L)

      val sub = MockSubscription()
      subscriber.onSubscribe(sub)
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hi".getBytes()))) // 2 <= 5
      assert(sub.cancelled == false)
      subscriber.onNext(JList.of(ByteBuffer.wrap("World".getBytes()))) // 2 + 5 = 7 > 5

      assert(sub.cancelled == true)
      assert(downstream.errors == 1)
      assert(downstream.lastError.isInstanceOf[IOException])
    }

    test("limiting should delegate getBody to downstream") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.limiting(downstream, 100L)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("data".getBytes())))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get().sameElements("data".getBytes()))
    }

    test("limiting should forward onError to downstream") {
      val downstream = MockBodySubscriber[String]()
      val subscriber = BodySubscribers.limiting(downstream, 100L)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("upstream error"))

      assert(downstream.errors == 1)
      assert(downstream.lastError.isInstanceOf[SPException])
    }

    test("limiting should handle multiple buffers in single onNext within capacity") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.limiting(downstream, 10L)

      subscriber.onSubscribe(MockSubscription())
      // Two buffers: "Hi" (2) + "Bye" (3) = 5 <= 10
      subscriber.onNext(
        JList.of(
          ByteBuffer.wrap("Hi".getBytes(StandardCharsets.UTF_8)),
          ByteBuffer.wrap("Bye".getBytes(StandardCharsets.UTF_8)),
        ),
      )
      subscriber.onComplete()

      assert(
        subscriber
          .getBody()
          .toCompletableFuture()
          .get()
          .sameElements("HiBye".getBytes(StandardCharsets.UTF_8)),
      )
    }

    test("limiting should error when multiple buffers in single onNext together exceed capacity") {
      val downstream = MockBodySubscriber[String]()
      val subscriber = BodySubscribers.limiting(downstream, 4L)

      val sub = MockSubscription()
      subscriber.onSubscribe(sub)
      // Two buffers: "He" (2) + "llo" (3) = 5 > 4
      subscriber.onNext(
        JList.of(
          ByteBuffer.wrap("He".getBytes(StandardCharsets.UTF_8)),
          ByteBuffer.wrap("llo".getBytes(StandardCharsets.UTF_8)),
        ),
      )

      assert(sub.cancelled == true)
      assert(downstream.errors == 1)
      assert(downstream.lastError.isInstanceOf[IOException])
    }
