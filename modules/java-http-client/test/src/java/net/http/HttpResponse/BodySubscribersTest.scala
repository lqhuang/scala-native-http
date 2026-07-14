package snhttp.test.java.net.http

import java.io.IOException
import java.net.http.HttpResponse.BodySubscribers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.nio.file.StandardOpenOption
import java.util.{ArrayList, Optional}
import java.util.List as JList
import java.util.concurrent.{CompletableFuture, CountDownLatch, ExecutionException, TimeUnit}
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.function.{Consumer, Function}

import utest.{TestSuite, Tests, test, assert, assertThrows}

import _root_.snhttp.test.jdk.net.http.{MockBodySubscriber, MockSubscription, MockSubscriber}
import _root_.snhttp.test.jdk.net.http.SPException

class BodySubscribersTest extends TestSuite:

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

      assert(mocker.completes == 1)
      assert(new String(mocker.concatReceived()) == "hello")

      assert(subscriber.getBody().toCompletableFuture().get() == null)
    }

    test("fromSubscriber(S, Function) should apply finisher after onComplete") {
      val mocker = MockBodySubscriber[String]()
      val finisher: Function[MockBodySubscriber[String], String] =
        s => new String(mocker.concatReceived(), StandardCharsets.UTF_8)

      val subscriber = BodySubscribers.fromSubscriber(mocker, finisher)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("hello world".getBytes())))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "hello world")
    }

    test("fromSubscriber finisher exception should complete body exceptionally") {
      val mocker = MockBodySubscriber[String]()
      val finisher: Function[MockBodySubscriber[String], String] =
        _ => throw new SPException("Finisher error")

      val subscriber = BodySubscribers.fromSubscriber(mocker, finisher)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      subscriber.onComplete()

      assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
    }

    test("fromSubscriber should forward onError to wrapped subscriber") {
      val mocker = MockBodySubscriber()
      val subscriber = BodySubscribers.fromSubscriber(mocker)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("test error"))

      assert(mocker.errors == 1)
      assert(mocker.lastError.isInstanceOf[SPException])

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
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

      assert(mocker.completes == 1)
      assert(mocker.concatReceived().isEmpty)
    }

    // ========================================= //
    // Test BodySubscribers.fromLineSubscriber() //
    // ========================================= //

    test("fromLineSubscriber(Subscriber) should forward lines and return Unit") {
      val mocker = MockSubscriber[String](request = true)
      val subscriber = BodySubscribers.fromLineSubscriber(mocker)

      subscriber.onSubscribe(MockSubscription())
      val sep = System.lineSeparator()
      val data = s"line1${sep}line2${sep}"
      subscriber.onNext(JList.of(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(mocker.completes == 1)
      assert(mocker.received.contains("line1"))
      assert(mocker.received.contains("line2"))
      assert(subscriber.getBody().toCompletableFuture().get() == null)
    }

    test("fromLineSubscriber with finisher should apply finisher after onComplete") {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        "\n",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("a\nb\nc".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == 3)
    }

    test("fromLineSubscriber should reject empty lineSeparator") {
      val mocker = MockSubscriber[String](request = true)
      assertThrows[IllegalArgumentException] {
        BodySubscribers.fromLineSubscriber(
          mocker,
          ((_: MockSubscriber[String]) => "done"): Function[MockSubscriber[String], String],
          StandardCharsets.UTF_8,
          "",
        ): Unit
      }
    }

    test("fromLineSubscriber should accept null lineSeparator") {
      val mocker = MockSubscriber[String](request = true)
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        ((_: MockSubscriber[String]) => "ok"): Function[MockSubscriber[String], String],
        StandardCharsets.UTF_8,
        null,
      )

      subscriber.onSubscribe(MockSubscription())
      val sep = System.lineSeparator()
      subscriber.onNext(JList.of(ByteBuffer.wrap(s"X${sep}Y".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "ok")
      assert(mocker.received.contains("X"))
      assert(mocker.received.contains("Y"))
    }

    test("fromLineSubscriber with custom separator should split correctly") {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
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

      assert(subscriber.getBody().toCompletableFuture().get() == 3)
      assert(mocker.received.sameElements(Seq("part1", "part2", "part3")))
    }

    test("fromLineSubscriber with custom separators should split correctly") {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
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

      assert(subscriber.getBody().toCompletableFuture().get() == 3)
      assert(mocker.received.sameElements(Seq("part1", "part2", "part3")))
    }

    test(
      "fromLineSubscriber with escape characters separators || should split correctly",
    ) {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        "||",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("part1||part2||part3".getBytes(StandardCharsets.UTF_8))),
      )
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == 3)
      assert(mocker.received.sameElements(Seq("part1", "part2", "part3")))
    }

    test("fromLineSubscriber should handle data split across chunks") {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
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

      assert(subscriber.getBody().toCompletableFuture().get() == 2)
      assert(mocker.received.sameElements(Seq("hello", "world")))
    }

    test("fromLineSubscriber should handle empty body") {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        "\n",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == 0)
    }

    test("fromLineSubscriber finisher exception should complete body exceptionally") {
      val mocker = MockSubscriber[String](request = true)
      val faultyFinisher: Function[MockSubscriber[String], String] =
        _ => throw new SPException("line finisher error")
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        faultyFinisher,
        StandardCharsets.UTF_8,
        "\n",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("data".getBytes(StandardCharsets.UTF_8))))

      assertThrows[SPException] {
        subscriber.onComplete()
      }
    }

    test("fromLineSubscriber 1-arg should use UTF-8 charset by default") {
      val mocker = MockSubscriber[String](request = true)
      val subscriber = BodySubscribers.fromLineSubscriber(mocker)

      subscriber.onSubscribe(MockSubscription())
      val text = "αβγ\nδεζ" // Greek letters, multi-byte in UTF-8
      subscriber.onNext(JList.of(ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(mocker.received.contains("αβγ"))
      assert(mocker.received.contains("δεζ"))
    }

    test("fromLineSubscriber should not emit trailing empty line when body ends with separator") {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        "\n",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("line1\nline2\n".getBytes(StandardCharsets.UTF_8))),
      )
      subscriber.onComplete()

      // Should have exactly 2 lines (no trailing empty line)
      assert(subscriber.getBody().toCompletableFuture().get() == 2)
      assert(mocker.received.sameElements(Seq("line1", "line2")))
    }

    test("fromLineSubscriber should emit last line without trailing separator") {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
      val subscriber = BodySubscribers.fromLineSubscriber(
        mocker,
        finisher,
        StandardCharsets.UTF_8,
        "\n",
      )

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("line1\nline2".getBytes(StandardCharsets.UTF_8))),
      )
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == 2)
      assert(mocker.received.sameElements(Seq("line1", "line2")))
    }

    // =============================== //
    // Test BodySubscribers.ofString() //
    // =============================== //

    test("ofString should handle empty body") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "")
    }

    test("ofString should decode single chunk") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello, World!".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "Hello, World!")
    }

    test("ofString should handle UTF-8 emoji/multibyte") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val text = "Hello, World! 🌍"
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == text)
    }

    test("ofString should decode with ISO-8859-1") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.ISO_8859_1)
      val bytes = Array[Byte](0x63, 0x61, 0x66, 0xe9.toByte) // "café" in ISO-8859-1
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "caf\u00e9")
    }

    test("ofString should accumulate multiple chunks") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello, ".getBytes(StandardCharsets.UTF_8))))
      subscriber.onNext(JList.of(ByteBuffer.wrap("World!".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "Hello, World!")
    }

    test("ofString should complete exceptionally on onError") {
      for (charset <- Seq(StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1)) {
        val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
        subscriber.onSubscribe(MockSubscription())
        subscriber.onError(new SPException("Test error"))

        val exc = assertThrows[ExecutionException] {
          subscriber.getBody().toCompletableFuture().get(): Unit
        }
        assert(exc.getCause().isInstanceOf[SPException])
      }
    }

    test("ofString subscription cancellation should succeed") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val sub = MockSubscription()

      subscriber.onSubscribe(sub)
      sub.cancel()
      assert(sub.cancelled == true)
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

      assert(subscriber.getBody().toCompletableFuture().get().sameElements(data))
    }

    test("ofByteArray should collect all bytes from multiple chunks") {
      val subscriber = BodySubscribers.ofByteArray()
      val data1 = "Hello, ".getBytes(StandardCharsets.UTF_8)
      val data2 = "World!".getBytes(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(data1)))
      subscriber.onNext(JList.of(ByteBuffer.wrap(data2)))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get().sameElements(data1 ++ data2))
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

      assert(subscriber.getBody().toCompletableFuture().get().sameElements(data1 ++ data2 ++ data3))
    }

    test("ofByteArray should handle empty body") {
      val subscriber = BodySubscribers.ofByteArray()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get().isEmpty)
    }

    test("ofByteArray should handle onError") {
      val subscriber = BodySubscribers.ofByteArray()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("Test error"))

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
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

      assert(subscriber.getBody().toCompletableFuture().get().length == chunkSize * numChunks)
    }

    test("ofByteArray should handle 1MB data") {
      val subscriber = BodySubscribers.ofByteArray()
      val size = 1024 * 1024
      val largeData = new Array[Byte](size)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(largeData)))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get().length == size)
    }

    test("ofByteArray should handle ByteBuffer with non-zero position") {
      val subscriber = BodySubscribers.ofByteArray()
      val raw = "XYZHello".getBytes(StandardCharsets.UTF_8)
      val buf = ByteBuffer.wrap(raw)
      buf.position(3) // skip "XYZ", leaving "Hello"

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(buf))
      subscriber.onComplete()

      assert(
        new String(subscriber.getBody().toCompletableFuture().get(), StandardCharsets.UTF_8)
          == "Hello",
      )
    }

    test("ofByteArray should handle empty ByteBuffer in the list") {
      val subscriber = BodySubscribers.ofByteArray()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(
          ByteBuffer.wrap(new Array[Byte](0)), // empty buffer
          ByteBuffer.wrap("data".getBytes(StandardCharsets.UTF_8)),
        ),
      )
      subscriber.onComplete()

      assert(
        subscriber
          .getBody()
          .toCompletableFuture()
          .get()
          .sameElements("data".getBytes(StandardCharsets.UTF_8)),
      )
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

        val resultPath = subscriber.getBody().toCompletableFuture().get()
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

        val resultPath = subscriber.getBody().toCompletableFuture().get()
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

        val resultPath = subscriber.getBody().toCompletableFuture().get()
        assert(resultPath == tempFile)
        assert(Files.size(tempFile) == 0)
      } finally Files.deleteIfExists(tempFile): Unit
    }

    test("ofFile should reject DELETE_ON_CLOSE option") {
      val tempFile = Files.createTempFile("file-option-test", ".txt")
      try
        assertThrows[IllegalArgumentException] {
          BodySubscribers.ofFile(tempFile, StandardOpenOption.DELETE_ON_CLOSE): Unit
        }
      finally Files.deleteIfExists(tempFile): Unit
    }

    test("ofFile should reject READ option") {
      val tempFile = Files.createTempFile("file-option-test", ".txt")
      try
        assertThrows[IllegalArgumentException] {
          BodySubscribers.ofFile(tempFile, StandardOpenOption.READ): Unit
        }
      finally Files.deleteIfExists(tempFile): Unit
    }

    test("ofFile should accept APPEND option") {
      val tempFile = Files.createTempFile("file-append-test", ".txt")
      try {
        // Should not throw at construction
        val subscriber = BodySubscribers.ofFile(tempFile, StandardOpenOption.APPEND)
        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(JList.of(ByteBuffer.wrap("appended".getBytes(StandardCharsets.UTF_8))))
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture().get()
        assert(resultPath == tempFile)
      } finally Files.deleteIfExists(tempFile): Unit
    }

    test("ofFile should create file if it does not exist") {
      val tempDir = Files.createTempDirectory("subscriber-create-test")
      val newFile = tempDir.resolve("new-file.txt")
      try {
        assert(!Files.exists(newFile))
        val subscriber = BodySubscribers.ofFile(newFile)
        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(JList.of(ByteBuffer.wrap("created".getBytes(StandardCharsets.UTF_8))))
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture().get()
        assert(Files.exists(newFile))
        assert(resultPath == newFile)
        assert(Files.readString(newFile, StandardCharsets.UTF_8) == "created")
      } finally {
        Files.deleteIfExists(newFile): Unit
        Files.deleteIfExists(tempDir): Unit
      }
    }

    test("ofFile(path) should be equivalent to ofFile(path, CREATE, WRITE)") {
      // Both forms should successfully write to a new file in the same directory
      val tempDir = Files.createTempDirectory("subscriber-equiv-test")
      val file1 = tempDir.resolve("file1.txt")
      val file2 = tempDir.resolve("file2.txt")
      try {
        val data = "equivalence test".getBytes(StandardCharsets.UTF_8)

        val s1 = BodySubscribers.ofFile(file1)
        s1.onSubscribe(MockSubscription())
        s1.onNext(JList.of(ByteBuffer.wrap(data)))
        s1.onComplete()
        s1.getBody().toCompletableFuture().get()

        val s2 = BodySubscribers.ofFile(file2, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        s2.onSubscribe(MockSubscription())
        s2.onNext(JList.of(ByteBuffer.wrap(data)))
        s2.onComplete()
        s2.getBody().toCompletableFuture().get()

        assert(
          Files.readAllBytes(file1).sameElements(Files.readAllBytes(file2)),
        )
      } finally {
        Files.deleteIfExists(file1): Unit
        Files.deleteIfExists(file2): Unit
        Files.deleteIfExists(tempDir): Unit
      }
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

      assert(subscriber.getBody().toCompletableFuture().get() == null)
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

      assert(subscriber.getBody().toCompletableFuture().get() == null)
      assert(receivedEmpty)
    }

    test("ofByteArrayConsumer should handle consumer exceptions") {
      val faultyConsumer: Consumer[Optional[Array[Byte]]] =
        _ => throw new SPException("Consumer error")

      val subscriber = BodySubscribers.ofByteArrayConsumer(faultyConsumer)
      subscriber.onSubscribe(MockSubscription())
      val _ = assertThrows[SPException] {
        subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      }
      val _ = assertThrows[SPException] {
        subscriber.onComplete(): Unit
      }
    }

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

      assert(subscriber.getBody().toCompletableFuture().get() == null)
      assert(consumedData.size() == 2)
    }

    test("ofByteArrayConsumer should handle multiple buffers in single onNext") {
      val consumedData = new ArrayList[Array[Byte]]()
      val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
        if (opt.isPresent) consumedData.add(opt.get()): Unit
      }

      val subscriber = BodySubscribers.ofByteArrayConsumer(consumer)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(
        JList.of(
          ByteBuffer.wrap("first".getBytes(StandardCharsets.UTF_8)),
          ByteBuffer.wrap("second".getBytes(StandardCharsets.UTF_8)),
        ),
      )
      subscriber.onComplete()

      subscriber.getBody().toCompletableFuture().get()
      // Should have received at least one non-empty call covering both buffers
      assert(consumedData.size() >= 1)
      val total = consumedData.stream().mapToInt(_.length).sum()
      assert(total == "firstsecond".length)
    }

    // ==================================== //
    // Test BodySubscribers.ofInputStream() //
    // ==================================== //

    test("ofInputStream should create subscriber and return InputStream") {
      val subscriber = BodySubscribers.ofInputStream()
      subscriber.onSubscribe(MockSubscription())

      // getBody() should complete immediately after subscription (streaming)
      val inputStream = subscriber.getBody().toCompletableFuture().get()
      assert(inputStream != null)
      inputStream.close()
    }

    test("ofInputStream should allow reading all available bytes") {
      for (
        charset <- Seq(
          StandardCharsets.UTF_8,
          StandardCharsets.UTF_16,
          StandardCharsets.US_ASCII,
          StandardCharsets.ISO_8859_1,
        )
      ) {
        val subscriber = BodySubscribers.ofInputStream()
        subscriber.onSubscribe(MockSubscription())

        val inputStream = subscriber.getBody().toCompletableFuture().get()
        val expected = "stream data"
        subscriber.onNext(JList.of(ByteBuffer.wrap(expected.getBytes(StandardCharsets.UTF_8))))
        subscriber.onComplete()

        val bytes = inputStream.readAllBytes()
        inputStream.close()
        assert(new String(bytes, StandardCharsets.UTF_8) == expected)
      }
    }

    test("ofInputStream should return -1 on read after all data consumed") {
      val subscriber = BodySubscribers.ofInputStream()
      subscriber.onSubscribe(MockSubscription())

      val inputStream = subscriber.getBody().toCompletableFuture().get()
      subscriber.onNext(JList.of(ByteBuffer.wrap("hi".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      inputStream.readAllBytes() // drain
      assert(inputStream.read() == -1)
      inputStream.close()
    }

    test("ofInputStream bulk read should wait for delayed data") {
      val subscriber = BodySubscribers.ofInputStream()
      subscriber.onSubscribe(MockSubscription())
      val inputStream = subscriber.getBody().toCompletableFuture().get()
      val readerStarted = new CountDownLatch(1)
      val readResult = new CompletableFuture[(Int, String)]()
      val reader = new Thread(() => {
        readerStarted.countDown()
        try {
          val bytes = new Array[Byte](32)
          val count = inputStream.read(bytes, 0, bytes.length)
          readResult.complete((count, new String(bytes, 0, count, StandardCharsets.UTF_8))): Unit
        } catch {
          case exc: Throwable => readResult.completeExceptionally(exc): Unit
        }
      })
      reader.setDaemon(true)

      reader.start()
      assert(readerStarted.await(5L, TimeUnit.SECONDS))
      Thread.sleep(50L)
      assert(!readResult.isDone())

      subscriber.onNext(JList.of(ByteBuffer.wrap("delayed data".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val (count, body) = readResult.get(5L, TimeUnit.SECONDS)
      reader.join(1000L)
      inputStream.close()
      assert(count == "delayed data".length)
      assert(body == "delayed data")
      assert(!reader.isAlive())
    }

    // ============================== //
    // Test BodySubscribers.ofLines() //
    // ============================== //

    test("ofLines should create subscriber") {
      val subscriber = BodySubscribers.ofLines(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())

      // getBody() should complete immediately (streaming)
      val stream = subscriber.getBody().toCompletableFuture().get()
      assert(stream != null)
      stream.close()
    }

    test("ofLines should deliver correct lines to stream") {
      val subscriber = BodySubscribers.ofLines(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())

      val stream = subscriber.getBody().toCompletableFuture().get()
      subscriber.onNext(
        JList.of(ByteBuffer.wrap("line1\nline2\nline3".getBytes(StandardCharsets.UTF_8))),
      )
      subscriber.onComplete()

      val collected = new ArrayList[String]()
      stream.forEach(collected.add(_): Unit)
      stream.close()
      assert(collected.size() == 3)
      assert(collected.get(0) == "line1")
      assert(collected.get(1) == "line2")
      assert(collected.get(2) == "line3")
    }

    test("ofLines should deliver empty stream for empty body") {
      val subscriber = BodySubscribers.ofLines(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())

      val stream = subscriber.getBody().toCompletableFuture().get()
      subscriber.onComplete()

      val collected = new ArrayList[String]()
      stream.forEach(collected.add(_): Unit)
      stream.close()
      assert(collected.size() == 0)
    }

    // ================================== //
    // Test BodySubscribers.ofPublisher() //
    // ================================== //

    test("ofPublisher should create publisher available after subscription") {
      val subscriber = BodySubscribers.ofPublisher()
      subscriber.onSubscribe(MockSubscription())

      val publisher = subscriber.getBody().toCompletableFuture().get()
      assert(publisher != null)
    }

    test("ofPublisher should forward data to downstream subscriber") {
      val subscriber = BodySubscribers.ofPublisher()
      subscriber.onSubscribe(MockSubscription())

      val publisher = subscriber.getBody().toCompletableFuture().get()
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

      assert(received.size() >= 1)
    }

    test("ofPublisher second subscription should receive error via onError") {
      val subscriber = BodySubscribers.ofPublisher()
      subscriber.onSubscribe(MockSubscription())

      val publisher = subscriber.getBody().toCompletableFuture().get()

      // First subscription
      publisher.subscribe(new Subscriber[JList[ByteBuffer]] {
        override def onSubscribe(s: Subscription): Unit = ()
        override def onNext(item: JList[ByteBuffer]): Unit = ()
        override def onError(t: Throwable): Unit = ()
        override def onComplete(): Unit = ()
      })

      // Second subscription must receive IllegalStateException via onError
      var secondSubError: Throwable = null
      publisher.subscribe(new Subscriber[JList[ByteBuffer]] {
        override def onSubscribe(s: Subscription): Unit = ()
        override def onNext(item: JList[ByteBuffer]): Unit = ()
        override def onError(t: Throwable): Unit = secondSubError = t
        override def onComplete(): Unit = ()
      })

      assert(secondSubError != null)
      assert(secondSubError.isInstanceOf[IllegalStateException])
    }

    // ================================ //
    // Test BodySubscribers.replacing() //
    // ================================ //

    test("replacing should discard body and return given value") {
      val subscriber = BodySubscribers.replacing("replaced")
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("ignored data".getBytes())))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "replaced")
    }

    test("replacing should accept null value") {
      val subscriber = BodySubscribers.replacing[String](null)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == null)
    }

    test("replacing should discard large body") {
      val subscriber = BodySubscribers.replacing(42)
      subscriber.onSubscribe(MockSubscription())
      for (_ <- 0 until 100)
        subscriber.onNext(JList.of(ByteBuffer.wrap(new Array[Byte](1024))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == 42)
    }

    test("replacing should return exactly the given value (reference equality for objects)") {
      val sentinel = new Object()
      val subscriber = BodySubscribers.replacing(sentinel)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() eq sentinel)
    }

    // ================================= //
    // Test BodySubscribers.discarding() //
    // ================================= //

    test("discarding should ignore all data and return null") {
      val subscriber = BodySubscribers.discarding()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("discarded".getBytes())))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == null)
    }

    test("discarding should handle empty body") {
      val subscriber = BodySubscribers.discarding()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == null)
    }

    test("discarding should handle onError") {
      val subscriber = BodySubscribers.discarding()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("error"))

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
    }

    test("discarding should return null specifically (Void)") {
      val subscriber = BodySubscribers.discarding()
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == null)
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

      assert(subscriber.getBody().toCompletableFuture().get().sameElements(exactData))
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

      val expected = "HiHello World!".getBytes(StandardCharsets.UTF_8)
      assert(subscriber.getBody().toCompletableFuture().get().sameElements(expected))
    }

    test("buffering should flush remaining data on onComplete") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.buffering(downstream, 100)

      subscriber.onSubscribe(MockSubscription())
      // Less than bufferSize, stays buffered until onComplete
      subscriber.onNext(JList.of(ByteBuffer.wrap("small".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(
        subscriber
          .getBody()
          .toCompletableFuture()
          .get()
          .sameElements("small".getBytes(StandardCharsets.UTF_8)),
      )
    }

    test("buffering should delegate getBody to downstream") {
      val downstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val subscriber = BodySubscribers.buffering(downstream, 1024)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "test")
    }

    test("buffering should forward onError to downstream") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.buffering(downstream, 10)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("error"))

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
    }

    test("buffering should guarantee exactly bufferSize bytes per onNext except final") {
      val batches = new ArrayList[Int]()
      val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
        if (opt.isPresent)
          batches.add(opt.get().length): Unit
      }
      val bufSize = 5
      val downstream = BodySubscribers.ofByteArrayConsumer(consumer)
      val subscriber = BodySubscribers.buffering(downstream, bufSize)

      subscriber.onSubscribe(MockSubscription())
      // Send 8 byte chunks (larger than bufSize) per time
      ("AAABBBCCCDDD" * 100).grouped(8).foreach { chunk =>
        subscriber.onNext(JList.of(ByteBuffer.wrap(chunk.getBytes(StandardCharsets.UTF_8))))
      }
      subscriber.onComplete()
      subscriber.getBody().toCompletableFuture().get()

      // All batches except for last one must be smaller than bufSize
      val nonFinal = batches.subList(0, Math.max(0, batches.size() - 1))
      nonFinal.forEach(b => assert(b <= bufSize): Unit): Unit
    }

    test("buffering should forward all bytes with no data loss") {
      val downstream = BodySubscribers.ofByteArray()
      val subscriber = BodySubscribers.buffering(downstream, 3)

      val chunks = List("a", "bb", "ccc", "dddd", "eeeee")
      subscriber.onSubscribe(MockSubscription())
      chunks.foreach { c =>
        subscriber.onNext(JList.of(ByteBuffer.wrap(c.getBytes(StandardCharsets.UTF_8))))
      }
      subscriber.onComplete()

      val expected = chunks.mkString.getBytes(StandardCharsets.UTF_8)
      assert(subscriber.getBody().toCompletableFuture().get().sameElements(expected))
    }

    // ============================== //
    // Test BodySubscribers.mapping() //
    // ============================== //

    test("mapping should transform upstream result") {
      val upstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val mapper: Function[String, Int] = _.length
      val subscriber = BodySubscribers.mapping(upstream, mapper)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == 5)
    }

    test("mapping should handle mapper exceptions") {
      val upstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val faultyMapper: Function[String, Int] = _ => throw new SPException("Mapper error")
      val subscriber = BodySubscribers.mapping(upstream, faultyMapper)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      subscriber.onComplete()

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
    }

    test("mapping should forward onError to upstream") {
      val upstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val mapper: Function[String, Int] = _.length
      val subscriber = BodySubscribers.mapping(upstream, mapper)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("upstream error"))

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
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

      assert(subscriber.getBody().toCompletableFuture().get() == 5.0)
    }

    test("mapping should propagate null from mapper") {
      val upstream = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val mapper: Function[String, String] = _ => null
      val subscriber = BodySubscribers.mapping(upstream, mapper)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("input".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == null)
    }

    test("mapping should compose with ofByteArray upstream") {
      val upstream = BodySubscribers.ofByteArray()
      val mapper: Function[Array[Byte], String] = bytes => new String(bytes, StandardCharsets.UTF_8)
      val subscriber = BodySubscribers.mapping(upstream, mapper)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("mapped".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get() == "mapped")
    }

    // ================================ //
    // Test BodySubscribers.limiting()  //
    // ================================ //

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
      assertThrows[IllegalArgumentException] {
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
