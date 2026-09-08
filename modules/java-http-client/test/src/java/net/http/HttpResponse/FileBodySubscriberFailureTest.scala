package snhttp.test.java.net.http

import java.io.IOException
import java.net.http.HttpResponse.BodySubscribers
import java.nio.ByteBuffer
import java.nio.file.{Files, Path}
import java.util.List as JList
import java.util.concurrent.{ExecutionException, TimeUnit}

import utest.{TestSuite, Tests, test, assert, assertThrows}

import _root_.snhttp.test.jdk.net.http.MockSubscription

class FileBodySubscriberFailureTest extends TestSuite:
  val tests = Tests:
    test("successful file output preserves all bytes across buffers") {
      val file = Files.createTempFile("body-file-success", ".bin")
      try
        val subscriber = BodySubscribers.ofFile(file)
        val subscription = MockSubscription()
        subscriber.onSubscribe(subscription)
        subscriber.onNext(JList.of(
          ByteBuffer.wrap(Array[Byte](1, 2)),
          ByteBuffer.allocate(0),
          ByteBuffer.wrap(Array[Byte](3, 4)),
        ))
        subscriber.onComplete()
        assert(subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS) == file)
        assert(Files.readAllBytes(file).toSeq == Seq[Byte](1, 2, 3, 4))
        assert(!subscription.cancelled)
      finally Files.delete(file)
    }

    test("file open failure cancels upstream and fails body") {
      val directory = Files.createTempDirectory("body-file-failure")
      try
        val subscriber = BodySubscribers.ofFile(directory.resolve("missing").resolve("body"))
        val subscription = MockSubscription()
        subscriber.onSubscribe(subscription)
        val failure = assertThrows[ExecutionException] {
          subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS): Unit
        }
        assert(failure.getCause().isInstanceOf[IOException])
        assert(subscription.cancelled)
        assert(subscription.received == 0)
      finally Files.delete(directory)
    }

    test("file write failure cancels upstream and fails body without throwing") {
      val fullDevice = Path.of("/dev/full")
      if Files.exists(fullDevice) then
        val subscriber = BodySubscribers.ofFile(fullDevice)
        val subscription = MockSubscription()
        subscriber.onSubscribe(subscription)
        subscriber.onNext(JList.of(ByteBuffer.wrap(Array[Byte](1, 2, 3))))
        val failure = assertThrows[ExecutionException] {
          subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS): Unit
        }
        assert(failure.getCause().isInstanceOf[IOException])
        assert(subscription.cancelled)
    }
