package snhttp.java.net.http

import java.io.IOException
import java.net.http.HttpResponse.BodySubscribers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.{ArrayList, Optional}
import java.util.List as JList
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.function.{Consumer, Function}

import scala.collection.mutable.ListBuffer

import _root_.snhttp.java.net.http.utils.{
  MockBodySubscriber,
  MockSubscription,
  MockStringSubscriber,
  MockByteBufSubscriber,
}

import utest.{TestSuite, Tests, test, assert, assertThrows}

class BodySubscribersTest extends TestSuite:

  private def trackingSubscription(): (Subscription, () => Boolean) = {
    var cancelled = false
    val sub = new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = cancelled = true
    }
    (sub, () => cancelled)
  }

  val tests = Tests:

    // ===================================== //
    // Test BodySubscribers.fromSubscriber() //
    // ===================================== //

    test("fromSubscriber(Subscriber) should return BodySubscriber[Unit]") {
      val mocker = MockBodySubscriber()
      val subscriber = BodySubscribers.fromSubscriber(mocker)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("hello".getBytes())))
      subscriber.onComplete()

      assert(mocker.completed)
      assert(new String(mocker.concatReceived()) == "hello")
      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == null)
    }

    test("fromSubscriber(S, Function) should apply finisher after onComplete") {
      val mocker = MockBodySubscriber[String]()
      val finisher: Function[MockBodySubscriber[String], String] =
        s => new String(mocker.concatReceived(), StandardCharsets.UTF_8)

      val subscriber = BodySubscribers.fromSubscriber(mocker, finisher)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("hello world".getBytes())))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "hello world")
    }

    test("fromSubscriber finisher exception should complete body exceptionally") {
      val mocker = MockBodySubscriber[String]()
      val finisher: Function[MockBodySubscriber[String], String] =
        _ => throw new RuntimeException("Finisher error")

      val subscriber = BodySubscribers.fromSubscriber(mocker, finisher)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      subscriber.onComplete()

      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

    test("fromSubscriber should forward onError to wrapped subscriber") {
      val mocker = MockBodySubscriber()
      val subscriber = BodySubscribers.fromSubscriber(mocker)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new RuntimeException("test error"))

      assert(mocker.error.isDefined)
      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

    test("fromSubscriber should forward multiple chunks") {
      val mocker = MockBodySubscriber()
      val subscriber = BodySubscribers.fromSubscriber(mocker)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("a".getBytes())))
      subscriber.onNext(JList.of(ByteBuffer.wrap("b".getBytes())))
      subscriber.onNext(JList.of(ByteBuffer.wrap("c".getBytes())))
      subscriber.onComplete()

      assert(new String(mocker.concatReceived()) == "abc")
    }

    test("fromSubscriber with empty body should complete normally") {
      val mocker = MockBodySubscriber()
      val subscriber = BodySubscribers.fromSubscriber(mocker)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(mocker.completed)
      assert(mocker.concatReceived().isEmpty)
    }

    // ========================================= //
    // Test BodySubscribers.fromLineSubscriber() //
    // ========================================= //

    test("fromLineSubscriber(Subscriber) should forward lines and return Unit") {
      val mocker = MockStringSubscriber()
      val subscriber = BodySubscribers.fromLineSubscriber(mocker)

      subscriber.onSubscribe(MockSubscription())
      val sep = System.lineSeparator()
      val data = s"line1${sep}line2${sep}"
      subscriber.onNext(JList.of(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(mocker.completed)
      assert(mocker.received.contains("line1"))
      assert(mocker.received.contains("line2"))
      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == null)
    }

    test("fromLineSubscriber with finisher should apply finisher after onComplete") {
      val mocker = MockStringSubscriber()
      val finisher: Function[MockStringSubscriber, Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        "\n",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("a\nb\nc".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == 3)
    }

    test("fromLineSubscriber should reject empty lineSeparator") {
      val mocker = MockStringSubscriber()
      assertThrows[IllegalArgumentException] {
        BodySubscribers.fromLineSubscriber(
          mocker,
          ((_: MockStringSubscriber) => "done"): Function[MockStringSubscriber, String],
          StandardCharsets.UTF_8,
          "",
        ): Unit
      }
    }

    test("fromLineSubscriber should accept null lineSeparator") {
      val mocker = MockStringSubscriber()
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        ((_: MockStringSubscriber) => "ok"): Function[MockStringSubscriber, String],
        StandardCharsets.UTF_8,
        null,
      )

      subscriber.onSubscribe(MockSubscription())
      val sep = System.lineSeparator()
      subscriber.onNext(JList.of(ByteBuffer.wrap(s"X${sep}Y".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "ok")
      assert(mocker.received.contains("X"))
      assert(mocker.received.contains("Y"))
    }

    test("fromLineSubscriber with custom separator should split correctly") {
      val mocker = MockStringSubscriber()
      val finisher: Function[MockStringSubscriber, Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        ",",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("part1,part2,part3".getBytes(StandardCharsets.UTF_8))),
      )
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == 3)
      assert(mocker.received.toSeq == Seq("part1", "part2", "part3"))
    }

    test("fromLineSubscriber with custom separators should split correctly") {
      val mocker = MockStringSubscriber()
      val finisher: Function[MockStringSubscriber, Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        ",,",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("part1,,part2,,part3".getBytes(StandardCharsets.UTF_8))),
      )
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == 3)
      assert(mocker.received.toSeq == Seq("part1", "part2", "part3"))
    }

    // test("fromLineSubscriber with custom separators || should split correctly") {
    //   val mocker = MockStringSubscriber()
    //   val finisher: Function[MockStringSubscriber, Int] = s => s.received.size
    //   val subscriber = BodySubscribers.fromLineSubscriber(
    //     mocker,
    //     finisher,
    //     StandardCharsets.UTF_8,
    //     "||",
    //   )

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onNext(
    //     JList.of(ByteBuffer.wrap("part1||part2||part3".getBytes(StandardCharsets.UTF_8))),
    //   )
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture.get()
    //   assert(result == 3)
    //   assert(mocker.received.toSeq == Seq("part1", "part2", "part3"))
    // }

    test("fromLineSubscriber should handle data split across chunks") {
      val mocker = MockStringSubscriber()
      val finisher: Function[MockStringSubscriber, Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        "\n",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("hel".getBytes(StandardCharsets.UTF_8))))
      subscriber.onNext(JList.of(ByteBuffer.wrap("lo\nwor".getBytes(StandardCharsets.UTF_8))))
      subscriber.onNext(JList.of(ByteBuffer.wrap("ld".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == 2)
      assert(mocker.received.toSeq == Seq("hello", "world"))
    }

    test("fromLineSubscriber should handle empty body") {
      val mocker = MockStringSubscriber()
      val finisher: Function[MockStringSubscriber, Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        "\n",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == 0)
    }

    /*
     * doesn't pass on JVM
     */
    // test("fromLineSubscriber finisher exception should complete body exceptionally") {
    //   val mocker = MockStringSubscriber()
    //   val faultyFinisher: Function[MockStringSubscriber, String] =
    //     _ => throw new RuntimeException("line finisher error")
    //   val subscriber = BodySubscribers.fromLineSubscriber(
    //     mocker,
    //     faultyFinisher,
    //     StandardCharsets.UTF_8,
    //     "\n",
    //   )

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("data".getBytes(StandardCharsets.UTF_8))))
    //   subscriber.onComplete()

    //   assertThrows[Exception] {
    //     subscriber.getBody().toCompletableFuture.get(): Unit
    //   }
    // }

    // =============================== //
    // Test BodySubscribers.ofString() //
    // =============================== //

    test("ofString should handle empty body") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "")
    }

    test("ofString should decode single chunk") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello, World!".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "Hello, World!")
    }

    test("ofString should handle UTF-8 emoji/multibyte") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val text = "Hello, World! 🌍"
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == text)
    }

    test("ofString should decode with ISO-8859-1") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.ISO_8859_1)
      val bytes = Array[Byte](0x63, 0x61, 0x66, 0xe9.toByte) // "café" in ISO-8859-1
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "caf\u00e9")
    }

    test("ofString should accumulate multiple chunks") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello, ".getBytes(StandardCharsets.UTF_8))))
      subscriber.onNext(JList.of(ByteBuffer.wrap("World!".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "Hello, World!")
    }

    // test("ofString should handle onError") {
    //   val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onError(new RuntimeException("Test error"))

    //   assertThrows[RuntimeException] {
    //     subscriber.getBody().toCompletableFuture.get(): Unit
    //   }
    // }

    test("ofString subscription cancellation should succeed") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val (sub, wasCancelled) = trackingSubscription()

      subscriber.onSubscribe(sub)
      sub.cancel()
      assert(wasCancelled())
    }

    // ================================== //
    // Test BodySubscribers.ofByteArray() //
    // ================================== //

    test("ofByteArray should collect all bytes from single chunk") {
      val subscriber = BodySubscribers.ofByteArray()
      val data = "Hello, World!".getBytes(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(data)))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result.sameElements(data))
    }

    test("ofByteArray should collect all bytes from multiple chunks") {
      val subscriber = BodySubscribers.ofByteArray()
      val data1 = "Hello, ".getBytes(StandardCharsets.UTF_8)
      val data2 = "World!".getBytes(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(data1)))
      subscriber.onNext(JList.of(ByteBuffer.wrap(data2)))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result.sameElements(data1 ++ data2))
    }

    test("ofByteArray should join multiple buffers in single onNext") {
      val subscriber = BodySubscribers.ofByteArray()
      val data1 = "Part1".getBytes(StandardCharsets.UTF_8)
      val data2 = "Part2".getBytes(StandardCharsets.UTF_8)
      val data3 = "Part3".getBytes(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap(data1), ByteBuffer.wrap(data2), ByteBuffer.wrap(data3)),
      )
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result.sameElements(data1 ++ data2 ++ data3))
    }

    test("ofByteArray should handle empty body") {
      val subscriber = BodySubscribers.ofByteArray()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result.isEmpty)
    }

    test("ofByteArray should handle onError") {
      val subscriber = BodySubscribers.ofByteArray()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new RuntimeException("Test error"))

      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

    test("ofByteArray should handle many small chunks") {
      val subscriber = BodySubscribers.ofByteArray()
      val chunkSize = 10
      val numChunks = 1000
      subscriber.onSubscribe(MockSubscription())

      for (i <- 0 until numChunks) {
        val chunk = s"chunk-$i".padTo(chunkSize, ' ').getBytes(StandardCharsets.UTF_8)
        subscriber.onNext(JList.of(ByteBuffer.wrap(chunk)))
      }
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result.length == chunkSize * numChunks)
    }

    test("ofByteArray should handle 1MB data") {
      val subscriber = BodySubscribers.ofByteArray()
      val size = 1024 * 1024
      val largeData = new Array[Byte](size)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(largeData)))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result.length == size)
    }

    // ============================= //
    // Test BodySubscribers.ofFile() //
    // ============================= //

    test("ofFile should write body to file and return its path") {
      val tempFile = Files.createTempFile("test", ".txt")
      try {
        val subscriber = BodySubscribers.ofFile(tempFile)
        val testData = "File content test"
        val bytes = testData.getBytes(StandardCharsets.UTF_8)

        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture.get()
        assert(resultPath == tempFile)

        val fileContent = Files.readString(tempFile, StandardCharsets.UTF_8)
        assert(fileContent == testData)
      } finally Files.deleteIfExists(tempFile): Unit
    }

    test("ofFile should handle sequential writes") {
      val tempFile = Files.createTempFile("sequential-test", ".txt")
      try {
        val subscriber = BodySubscribers.ofFile(tempFile)
        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(JList.of(ByteBuffer.wrap("Part1".getBytes(StandardCharsets.UTF_8))))
        subscriber.onNext(JList.of(ByteBuffer.wrap("Part2".getBytes(StandardCharsets.UTF_8))))
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture.get()
        val fileContent = Files.readString(tempFile, StandardCharsets.UTF_8)
        assert(fileContent == "Part1Part2")
      } finally Files.deleteIfExists(tempFile): Unit
    }

    test("ofFile should handle empty body") {
      val tempFile = Files.createTempFile("empty-test", ".txt")
      try {
        val subscriber = BodySubscribers.ofFile(tempFile)
        subscriber.onSubscribe(MockSubscription())
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture.get()
        assert(resultPath == tempFile)
        assert(Files.size(tempFile) == 0)
      } finally Files.deleteIfExists(tempFile): Unit
    }

    // ========================================== //
    // Test BodySubscribers.ofByteArrayConsumer() //
    // ========================================== //

    test("ofByteArrayConsumer should call consumer with each chunk") {
      val consumedData = new ArrayList[Array[Byte]]()
      val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
        if (opt.isPresent) consumedData.add(opt.get()): Unit
      }

      val subscriber = BodySubscribers.ofByteArrayConsumer(consumer)
      val expectedBytes = "Consumer test".getBytes(StandardCharsets.UTF_8)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(expectedBytes)))
      subscriber.onComplete()

      subscriber.getBody().toCompletableFuture.get()
      assert(consumedData.size() == 1)
      assert(consumedData.get(0).sameElements(expectedBytes))
    }

    test("ofByteArrayConsumer should send Optional.empty on completion") {
      var receivedEmpty = false
      val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
        if (opt.isEmpty) receivedEmpty = true
      }

      val subscriber = BodySubscribers.ofByteArrayConsumer(consumer)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      subscriber.getBody().toCompletableFuture.get()
      assert(receivedEmpty)
    }

    /*
     * doesn't pass on JVM
     */
    // test("ofByteArrayConsumer should handle consumer exceptions") {
    //   val faultyConsumer: Consumer[Optional[Array[Byte]]] =
    //     _ => throw new RuntimeException("Consumer error")

    //   val subscriber = BodySubscribers.ofByteArrayConsumer(faultyConsumer)
    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
    //   subscriber.onComplete()

    //   assertThrows[Exception] {
    //     subscriber.getBody().toCompletableFuture.get(): Unit
    //   }
    // }

    test("ofByteArrayConsumer should handle multiple chunks") {
      val consumedData = new ArrayList[Array[Byte]]()
      val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
        if (opt.isPresent) consumedData.add(opt.get()): Unit
      }

      val subscriber = BodySubscribers.ofByteArrayConsumer(consumer)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("chunk1".getBytes())))
      subscriber.onNext(JList.of(ByteBuffer.wrap("chunk2".getBytes())))
      subscriber.onComplete()

      subscriber.getBody().toCompletableFuture.get()
      assert(consumedData.size() == 2)
    }

    // ==================================== //
    // Test BodySubscribers.ofInputStream() //
    // ==================================== //

    // test("ofInputStream should create subscriber and return InputStream") {
    //   val subscriber = BodySubscribers.ofInputStream()
    //   subscriber.onSubscribe(MockSubscription())

    //   // getBody() should complete immediately after subscription (streaming)
    //   val inputStream = subscriber.getBody().toCompletableFuture.get()
    //   assert(inputStream != null)
    //   inputStream.close()
    // }

    // ============================== //
    // Test BodySubscribers.ofLines() //
    // ============================== //

    test("ofLines should create subscriber") {
      val subscriber = BodySubscribers.ofLines(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())

      // getBody() should complete immediately (streaming)
      val stream = subscriber.getBody().toCompletableFuture.get()
      assert(stream != null)
      stream.close()
    }

    // ================================== //
    // Test BodySubscribers.ofPublisher() //
    // ================================== //

    test("ofPublisher should create publisher available after subscription") {
      val subscriber = BodySubscribers.ofPublisher()
      subscriber.onSubscribe(MockSubscription())

      val publisher = subscriber.getBody().toCompletableFuture.get()
      assert(publisher != null)
    }

    test("ofPublisher should forward data to downstream subscriber") {
      val subscriber = BodySubscribers.ofPublisher()
      subscriber.onSubscribe(MockSubscription())

      val publisher = subscriber.getBody().toCompletableFuture.get()
      val received = new ArrayList[ByteBuffer]()

      publisher.subscribe(new Subscriber[JList[ByteBuffer]] {
        override def onSubscribe(s: Subscription): Unit = ()
        override def onNext(item: JList[ByteBuffer]): Unit =
          item.forEach(bb => received.add(bb): Unit): Unit
        override def onError(t: Throwable): Unit = ()
        override def onComplete(): Unit = ()
      })

      subscriber.onNext(JList.of(ByteBuffer.wrap("hello".getBytes())))
      subscriber.onComplete()

      assert(received.size() == 1)
    }

    // ================================ //
    // Test BodySubscribers.replacing() //
    // ================================ //

    test("replacing should discard body and return given value") {
      val subscriber = BodySubscribers.replacing("replaced")
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("ignored data".getBytes())))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "replaced")
    }

    test("replacing should accept null value") {
      val subscriber = BodySubscribers.replacing[String](null)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == null)
    }

    test("replacing should discard large body") {
      val subscriber = BodySubscribers.replacing(42)
      subscriber.onSubscribe(MockSubscription())
      for (_ <- 0 until 100)
        subscriber.onNext(JList.of(ByteBuffer.wrap(new Array[Byte](1024))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == 42)
    }

    // ================================= //
    // Test BodySubscribers.discarding() //
    // ================================= //

    test("discarding should ignore all data and return null") {
      val subscriber = BodySubscribers.discarding()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("discarded".getBytes())))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == null)
    }

    test("discarding should handle empty body") {
      val subscriber = BodySubscribers.discarding()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == null)
    }

    test("discarding should handle onError") {
      val subscriber = BodySubscribers.discarding()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new RuntimeException("error"))

      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

    // ================================ //
    // Test BodySubscribers.buffering() //
    // ================================ //

    test("buffering should throw IllegalArgumentException if bufferSize is zero") {
      assertThrows[IllegalArgumentException] {
        BodySubscribers.buffering(BodySubscribers.ofByteArray(), 0): Unit
      }
    }

    test("buffering should throw IllegalArgumentException if bufferSize is negative") {
      assertThrows[IllegalArgumentException] {
        BodySubscribers.buffering(BodySubscribers.ofByteArray(), -1): Unit
      }
    }

    test("buffering should flush at exact buffer boundary") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.buffering(downstream, 10)

      val exactData = "1234567890".getBytes(StandardCharsets.UTF_8) // Exactly 10 bytes
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(exactData)))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result.sameElements(exactData))
    }

    test("buffering should accumulate and flush data correctly") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.buffering(downstream, 10)

      subscriber.onSubscribe(MockSubscription())
      // 2 bytes (buffered)
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hi".getBytes(StandardCharsets.UTF_8))))
      // 12 bytes total -> triggers flush (>= 10)
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("Hello World!".getBytes(StandardCharsets.UTF_8))),
      )
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      val expected = "HiHello World!".getBytes(StandardCharsets.UTF_8)
      assert(result.sameElements(expected))
    }

    test("buffering should flush remaining data on onComplete") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.buffering(downstream, 100)

      subscriber.onSubscribe(MockSubscription())
      // Less than bufferSize, stays buffered until onComplete
      subscriber.onNext(JList.of(ByteBuffer.wrap("small".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result.sameElements("small".getBytes(StandardCharsets.UTF_8)))
    }

    test("buffering should delegate getBody to downstream") {
      val downstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val subscriber = BodySubscribers.buffering(downstream, 1024)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "test")
    }

    test("buffering should forward onError to downstream") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.buffering(downstream, 10)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new RuntimeException("error"))

      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

    // ============================== //
    // Test BodySubscribers.mapping() //
    // ============================== //

    /*
     * doesn't pass on JVM
     */
    // test("mapping should transform upstream result") {
    //   val upstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
    //   val mapper: Function[String, Int] = _.length
    //   val subscriber = BodySubscribers.mapping(upstream, mapper)

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture.get()
    //   assert(result == 5)
    // }

    test("mapping should handle mapper exceptions") {
      val upstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val faultyMapper: Function[String, Int] = _ => throw new RuntimeException("Mapper error")
      val subscriber = BodySubscribers.mapping(upstream, faultyMapper)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      subscriber.onComplete()

      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

    test("mapping should forward onError to upstream") {
      val upstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val mapper: Function[String, Int] = _.length
      val subscriber = BodySubscribers.mapping(upstream, mapper)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new RuntimeException("upstream error"))

      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

    test("mapping should chain transformations") {
      val upstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val toLength: Function[String, Int] = _.length
      val toDouble: Function[Int, Double] = _.toDouble

      val step1 = BodySubscribers.mapping(upstream, toLength)
      val subscriber = BodySubscribers.mapping(step1, toDouble)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == 5.0)
    }

    // // ================================ //
    // // Test BodySubscribers.limiting()  //
    // // ================================ //

    // test("limiting should pass data within capacity") {
    //   val downstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
    //   val subscriber = BodySubscribers.limiting(downstream, 100L)

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture.get()
    //   assert(result == "Hello")
    // }

    // test("limiting should error when exceeding capacity") {
    //   val downstream = MockBodySubscriber[String]()
    //   val subscriber = BodySubscribers.limiting(downstream, 3L)

    //   val (sub, wasCancelled) = trackingSubscription()
    //   subscriber.onSubscribe(sub)
    //   subscriber.onNext(
    //     JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))),
    //   ) // 5 > 3

    //   assert(wasCancelled())
    //   assert(downstream.error.isDefined)
    //   assert(downstream.error.get.isInstanceOf[IOException])
    // }

    // test("limiting should reject negative capacity") {
    //   assertThrows[IllegalArgumentException] {
    //     BodySubscribers.limiting(BodySubscribers.ofString(StandardCharsets.UTF_8), -1L): Unit
    //   }
    // }

    // test("limiting should accept zero capacity with no data") {
    //   val downstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
    //   val subscriber = BodySubscribers.limiting(downstream, 0L)

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture.get()
    //   assert(result == "")
    // }

    // test("limiting should error when exceeding zero capacity") {
    //   val downstream = MockBodySubscriber[String]()
    //   val subscriber = BodySubscribers.limiting(downstream, 0L)

    //   val (sub, wasCancelled) = trackingSubscription()
    //   subscriber.onSubscribe(sub)
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("X".getBytes()))) // 1 > 0

    //   assert(wasCancelled())
    //   assert(downstream.error.isDefined)
    //   assert(downstream.error.get.isInstanceOf[IOException])
    // }

    // test("limiting should pass data at exact capacity boundary") {
    //   val downstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
    //   val subscriber = BodySubscribers.limiting(downstream, 5L)

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onNext(
    //     JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))),
    //   ) // 5 == 5
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture.get()
    //   assert(result == "Hello")
    // }

    // test("limiting should error on second chunk that exceeds cumulative capacity") {
    //   val downstream = MockBodySubscriber[String]()
    //   val subscriber = BodySubscribers.limiting(downstream, 5L)

    //   val (sub, wasCancelled) = trackingSubscription()
    //   subscriber.onSubscribe(sub)
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("Hi".getBytes()))) // 2 <= 5
    //   assert(!wasCancelled())
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("World".getBytes()))) // 2 + 5 = 7 > 5

    //   assert(wasCancelled())
    //   assert(downstream.error.isDefined)
    //   assert(downstream.error.get.isInstanceOf[IOException])
    // }

    // test("limiting should delegate getBody to downstream") {
    //   val downstream = BodySubscribers.ofByteArray()
    //   val subscriber = BodySubscribers.limiting(downstream, 100L)

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("data".getBytes())))
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture.get()
    //   assert(result.sameElements("data".getBytes()))
    // }

    // test("limiting should forward onError to downstream") {
    //   val downstream = MockBodySubscriber[String]()
    //   val subscriber = BodySubscribers.limiting(downstream, 100L)

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onError(new RuntimeException("upstream error"))

    //   assert(downstream.error.isDefined)
    // }
