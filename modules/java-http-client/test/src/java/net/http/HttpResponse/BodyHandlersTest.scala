package snhttp.java.net.http

import java.io.InputStream
import java.net.http.HttpClient.Version
import java.net.http.HttpResponse.BodyHandlers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.nio.file.StandardOpenOption
import java.util.{Optional, ArrayList}
import java.util.{List as JList, Map as JMap}
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.function.{Consumer, Function}

import snhttp.jdk.net.http.ResponseInfoImpl
import snhttp.jdk.testkits.{MockBodySubscriber, MockByteBufSubscriber, MockSubscription}
import snhttp.jdk.testkits.HttpResponseUtils.{createHeaders, createResponseInfo}

import utest.{TestSuite, Tests, test, assert, assertThrows}
import snhttp.jdk.testkits.MockStringSubscriber

class BodyHandlersTest extends TestSuite:

  def tests = Tests:

    // ================================== //
    // Test BodyHandlers.fromSubscriber() //
    // ================================== //

    test("fromSubscriber(Subscriber) should create handler returning Void") {
      val mockSubscriber = MockBodySubscriber[JList[ByteBuffer]]()
      val handler = BodyHandlers.fromSubscriber(mockSubscriber)
      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      subscriber.onComplete()

      assert(mockSubscriber.completed)

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == null)

      val receivedBytes = mockSubscriber.concatReceived()
      assert(new String(receivedBytes, StandardCharsets.UTF_8) == "test")
    }

    test("fromSubscriber(S, Function) should apply finisher") {
      val mockSubscriber = MockBodySubscriber[JList[ByteBuffer]]()
      val finisher: Function[MockBodySubscriber[JList[ByteBuffer]], String] =
        s => s"received ${s.received.size} buffers"

      val handler = BodyHandlers.fromSubscriber(mockSubscriber, finisher)
      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "received 1 buffers")
    }

    test("fromSubscriber finisher exception should complete exceptionally") {
      val mockSubscriber = MockBodySubscriber[JList[ByteBuffer]]()
      val faultyFinisher: Function[MockBodySubscriber[JList[ByteBuffer]], String] =
        _ => throw new RuntimeException("Finisher error")

      val handler = BodyHandlers.fromSubscriber(mockSubscriber, faultyFinisher)
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      subscriber.onComplete()

      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
    }

    // ====================================== //
    // Test BodyHandlers.fromLineSubscriber() //
    // ====================================== //

    test("fromLineSubscriber(Subscriber) should create handler using charset from headers") {
      val subscriber = MockStringSubscriber()
      val handler = BodyHandlers.fromLineSubscriber(subscriber)
      val responseInfo =
        createResponseInfo(Map("Content-Type" -> "text/plain; charset=iso-8859-1"))
      val bodySubscriber = handler(responseInfo)

      bodySubscriber.onSubscribe(MockSubscription())
      bodySubscriber.onComplete()

      val result = bodySubscriber.getBody().toCompletableFuture().get()
      assert(result == null) // 1-arg returns Void
    }

    test("fromLineSubscriber should accept null lineSeparator") {
      val subscriber = MockStringSubscriber()
      val handler = BodyHandlers.fromLineSubscriber(
        subscriber,
        ((_: Subscriber[? >: String]) => "done"): Function[Subscriber[? >: String], String],
        null,
      )
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val bodySubscriber = handler(responseInfo)
      // Should not throw
    }

    test("fromLineSubscriber should reject empty lineSeparator") {
      val subscriber = MockStringSubscriber()
      assertThrows[IllegalArgumentException] {
        BodyHandlers.fromLineSubscriber(
          subscriber,
          ((_: Subscriber[? >: String]) => "done"): Function[Subscriber[? >: String], String],
          "",
        ): Unit
      }
    }

    test("fromLineSubscriber should accept non-empty lineSeparator") {
      val subscriber = MockStringSubscriber()
      val handler = BodyHandlers.fromLineSubscriber(
        subscriber,
        ((_: Subscriber[? >: String]) => "done"): Function[Subscriber[? >: String], String],
        "\n",
      )
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val bodySubscriber = handler(responseInfo)
      // Should not throw
    }

    // ============================== //
    // Test BodyHandlers.discarding() //
    // ============================== //

    test("discarding should create handler that ignores body") {
      val handler = BodyHandlers.discarding()
      val responseInfo =
        ResponseInfoImpl(204, Version.HTTP_1_1, createHeaders(Map.empty))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("ignored".getBytes())))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == null)
    }

    // ============================= //
    // Test BodyHandlers.replacing() //
    // ============================= //

    test("replacing should create handler returning given value") {
      val handler = BodyHandlers.replacing("replacement")
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("ignored".getBytes())))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "replacement")
    }

    test("replacing should accept null value") {
      val handler = BodyHandlers.replacing[String](null)
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == null)
    }

    // ============================ //
    // Test BodyHandlers.ofString() //
    // ============================ //

    test("ofString() should default to UTF-8 when no Content-Type") {
      val handler = BodyHandlers.ofString()
      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "Hello")
    }

    test("ofString() should use charset from Content-Type header") {
      val handler = BodyHandlers.ofString()
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello UTF-8".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "Hello UTF-8")
    }

    test("ofString(Charset) should use given charset regardless of headers") {
      val handler = BodyHandlers.ofString(StandardCharsets.ISO_8859_1)
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)

      val bytes = Array[Byte](0x63, 0x61, 0x66, 0xe9.toByte) // "café" in ISO-8859-1
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "caf\u00e9")
    }

    // ========================== //
    // Test BodyHandlers.ofFile() //
    // ========================== //

    test("ofFile should create handler that writes body to file") {
      val tempFile = Files.createTempFile("handler-test", ".txt")
      try {
        val handler = BodyHandlers.ofFile(tempFile)
        val subscriber = handler(createResponseInfo())

        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(JList.of(ByteBuffer.wrap("content".getBytes(StandardCharsets.UTF_8))))
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture().get()
        assert(resultPath == tempFile)
        val fileContent = Files.readString(tempFile, StandardCharsets.UTF_8)
        assert(fileContent == "content")
      } finally Files.deleteIfExists(tempFile): Unit
    }

    test("ofFile should reject DELETE_ON_CLOSE option") {
      val tempFile = Files.createTempFile("handler-test", ".txt")
      try
        assertThrows[IllegalArgumentException] {
          BodyHandlers.ofFile(tempFile, StandardOpenOption.DELETE_ON_CLOSE): Unit
        }
      finally
        Files.deleteIfExists(tempFile): Unit
    }

    test("ofFile should reject READ option") {
      val tempFile = Files.createTempFile("handler-test", ".txt")
      try
        assertThrows[IllegalArgumentException] {
          BodyHandlers.ofFile(tempFile, StandardOpenOption.READ): Unit
        }
      finally
        Files.deleteIfExists(tempFile): Unit
    }

    // ================================== //
    // Test BodyHandlers.ofFileDownload() //
    // ================================== //

    // test("ofFileDownload should extract filename from Content-Disposition") {
    //   val tempDir = Files.createTempDirectory("test-downloads")
    //   try {
    //     val handler =
    //       BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    //     val responseInfo = createResponseInfo(
    //       Map("Content-Disposition" -> "attachment; filename=\"document.pdf\""),
    //     )
    //     val subscriber = handler(responseInfo)

    //     subscriber.onSubscribe(MockSubscription())
    //     subscriber.onNext(JList.of(ByteBuffer.wrap("pdf content".getBytes())))
    //     subscriber.onComplete()

    //     val resultPath = subscriber.getBody().toCompletableFuture().get()
    //     assert(resultPath.getFileName.toString == "document.pdf")
    //     assert(Files.exists(resultPath))
    //     Files.deleteIfExists(resultPath): Unit
    //   } finally Files.deleteIfExists(tempDir): Unit
    // }

    // test("ofFileDownload should use fallback filename when no Content-Disposition") {
    //   val tempDir = Files.createTempDirectory("test-downloads")
    //   try {
    //     val handler =
    //       BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    //     val responseInfo = createResponseInfo()
    //     val subscriber = handler(responseInfo)

    //     subscriber.onSubscribe(MockSubscription())
    //     subscriber.onNext(JList.of(ByteBuffer.wrap("data".getBytes())))
    //     subscriber.onComplete()

    //     val resultPath = subscriber.getBody().toCompletableFuture().get()
    //     assert(Files.exists(resultPath))
    //     Files.deleteIfExists(resultPath): Unit
    //   } finally Files.deleteIfExists(tempDir): Unit
    // }

    test("ofFileDownload should reject DELETE_ON_CLOSE option") {
      val tempDir = Files.createTempDirectory("test-downloads")
      try
        assertThrows[IllegalArgumentException] {
          BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.DELETE_ON_CLOSE): Unit
        }
      finally
        Files.deleteIfExists(tempDir): Unit
    }

    test("ofFileDownload should reject READ option") {
      val tempDir = Files.createTempDirectory("test-downloads")
      try
        assertThrows[IllegalArgumentException] {
          BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.READ): Unit
        }
      finally
        Files.deleteIfExists(tempDir): Unit
    }

    // ================================= //
    // Test BodyHandlers.ofInputStream() //
    // ================================= //

    test("ofInputStream should create handler returning InputStream") {
      val handler = BodyHandlers.ofInputStream()
      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())

      val inputStream = subscriber.getBody().toCompletableFuture().get()
      assert(inputStream != null)
      inputStream.close()
    }

    // =========================== //
    // Test BodyHandlers.ofLines() //
    // =========================== //

    test("ofLines should create handler using charset from Content-Type") {
      val handler = BodyHandlers.ofLines()
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())

      val stream = subscriber.getBody().toCompletableFuture().get()
      assert(stream != null)
      stream.close()
    }

    // ======================================= //
    // Test BodyHandlers.ofByteArrayConsumer() //
    // ======================================= //

    test("ofByteArrayConsumer should create handler feeding chunks to consumer") {
      val consumedData = new ArrayList[Array[Byte]]()
      val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
        if (opt.isPresent) consumedData.add(opt.get()): Unit
      }

      val handler = BodyHandlers.ofByteArrayConsumer(consumer)
      val subscriber = handler(createResponseInfo())

      val data = "consumer test".getBytes(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(data)))
      subscriber.onComplete()

      subscriber.getBody().toCompletableFuture().get()
      assert(consumedData.size() == 1)
      assert(consumedData.get(0).sameElements(data))
    }

    // =============================== //
    // Test BodyHandlers.ofByteArray() //
    // =============================== //

    test("ofByteArray should create handler accumulating bytes") {
      val handler = BodyHandlers.ofByteArray()
      val subscriber = handler(createResponseInfo())

      val data = "byte array test".getBytes(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(data)))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result.sameElements(data))
    }

    // ================================ //
    // Test BodyHandlers.ofPublisher()  //
    // ================================ //

    test("ofPublisher should create handler returning Publisher") {
      val handler = BodyHandlers.ofPublisher()
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())

      val publisher = subscriber.getBody().toCompletableFuture().get()
      assert(publisher != null)
    }

    // ============================== //
    // Test BodyHandlers.buffering()  //
    // ============================== //

    test("buffering should create buffering handler") {
      val downstreamHandler = BodyHandlers.ofString()
      val handler = BodyHandlers.buffering(downstreamHandler, 1024)
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "test")
    }

    test("buffering should reject zero buffer size") {
      val downstreamHandler = BodyHandlers.ofString()
      assertThrows[IllegalArgumentException] {
        BodyHandlers.buffering(downstreamHandler, 0): Unit
      }
    }

    test("buffering should reject negative buffer size") {
      val downstreamHandler = BodyHandlers.ofString()
      assertThrows[IllegalArgumentException] {
        BodyHandlers.buffering(downstreamHandler, -1): Unit
      }
    }

    // // ============================== //
    // // Test BodyHandlers.limiting()   //
    // // ============================== //

    // test("limiting should create limiting handler") {
    //   val downstreamHandler = BodyHandlers.ofString()
    //   val handler = BodyHandlers.limiting(downstreamHandler, 1024L)
    //   val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
    //   val subscriber = handler(responseInfo)

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture().get()
    //   assert(result == "Hello")
    // }

    // test("limiting should accept zero capacity") {
    //   val downstreamHandler = BodyHandlers.ofString()
    //   val handler = BodyHandlers.limiting(downstreamHandler, 0L)
    //   val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
    //   val subscriber = handler(responseInfo)

    //   subscriber.onSubscribe(MockSubscription())
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture().get()
    //   assert(result == "")
    // }

    // test("limiting should reject negative capacity") {
    //   val downstreamHandler = BodyHandlers.ofString()
    //   assertThrows[IllegalArgumentException] {
    //     BodyHandlers.limiting(downstreamHandler, -1L): Unit
    //   }
    // }

end BodyHandlersTest
