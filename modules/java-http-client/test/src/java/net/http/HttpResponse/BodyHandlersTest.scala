package snhttp.test.java.net.http

import java.io.{InputStream, UncheckedIOException, IOException}
import java.net.http.HttpClient.Version
import java.net.http.HttpResponse.BodyHandlers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardOpenOption, FileVisitResult, SimpleFileVisitor}
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.attribute.{PosixFilePermissions, BasicFileAttributes}
import java.util.{Optional, ArrayList}
import java.util.{List as JList, Map as JMap}
import java.util.concurrent.ExecutionException
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.function.{Consumer, Function}

import _root_.snhttp.test.jdk.net.http.{
  MockBodySubscriber,
  MockSubscription,
  MockSubscriber,
  SPException,
}
import _root_.snhttp.test.jdk.net.http.HttpClientTestUtils.{
  ResponseInfoImpl,
  createHeaders,
  createResponseInfo,
}

import utest.{TestSuite, Tests, test, assert, assertThrows}

class BodyHandlersTest extends TestSuite:

  def rmdir(path: Path): Unit =
    try //
      Files.deleteIfExists(path): Unit
    catch
      case exc: DirectoryNotEmptyException =>
        Files.walkFileTree(
          path,
          new SimpleFileVisitor[Path] {
            override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
              Files.delete(file);
              return FileVisitResult.CONTINUE;
            }

            override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
              Files.delete(dir);
              return FileVisitResult.CONTINUE;
            }
          },
        ): Unit

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

      assert(mockSubscriber.completes == 1)

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == null)

      val receivedBytes = mockSubscriber.concatReceived()
      assert(new String(receivedBytes, StandardCharsets.UTF_8) == "test")
    }

    test("fromSubscriber(Subscriber) should forward multiple chunks") {
      val mockSubscriber = MockBodySubscriber[JList[ByteBuffer]]()
      val handler = BodyHandlers.fromSubscriber(mockSubscriber)
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("foo".getBytes())))
      subscriber.onNext(JList.of(ByteBuffer.wrap("bar".getBytes())))
      subscriber.onComplete()

      assert(new String(mockSubscriber.concatReceived(), StandardCharsets.UTF_8) == "foobar")
    }

    test("fromSubscriber(Subscriber) should forward onError to wrapped subscriber") {
      val mockSubscriber = MockBodySubscriber[JList[ByteBuffer]]()
      val handler = BodyHandlers.fromSubscriber(mockSubscriber)
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("upstream error"))

      assert(mockSubscriber.lastError != null)
      assert(mockSubscriber.lastError.isInstanceOf[SPException])
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
        _ => throw new SPException("Finisher error")

      val handler = BodyHandlers.fromSubscriber(mockSubscriber, faultyFinisher)
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
      subscriber.onComplete()

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
      assert(exc.getCause().getMessage() == "Finisher error")
    }

    // ====================================== //
    // Test BodyHandlers.fromLineSubscriber() //
    // ====================================== //

    test("fromLineSubscriber should create handler using charset from headers") {
      val subscriber = MockSubscriber[String](request = true)
      val handler = BodyHandlers.fromLineSubscriber(subscriber)
      val responseInfo =
        createResponseInfo(Map("Content-Type" -> "text/plain; charset=iso-8859-1"))
      val bodySubscriber = handler(responseInfo)

      bodySubscriber.onSubscribe(MockSubscription())
      bodySubscriber.onComplete()

      val result = bodySubscriber.getBody().toCompletableFuture().get()
      assert(result == null) // 1-arg returns Void
    }

    test("fromLineSubscriber should deliver lines to downstream subscriber") {
      val mocker = MockSubscriber[String](request = true)
      val finisher: Function[MockSubscriber[String], Int] = s => s.received.size
      val handler = BodyHandlers.fromLineSubscriber(mocker, finisher, "\n")
      val bodySubscriber = handler(createResponseInfo())

      bodySubscriber.onSubscribe(MockSubscription())
      bodySubscriber.onNext(
        JList.of(ByteBuffer.wrap("line1\nline2\nline3".getBytes(StandardCharsets.UTF_8))),
      )
      bodySubscriber.onComplete()

      val result = bodySubscriber.getBody().toCompletableFuture().get()
      println(s"Received lines: ${mocker.received}")
      assert(result == 3)
      assert(mocker.received.contains("line1"))
      assert(mocker.received.contains("line2"))
      assert(mocker.received.contains("line3"))
    }

    test("fromLineSubscriber should accept null lineSeparator") {
      val subscriber = MockSubscriber[String](request = true)
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
      val subscriber = MockSubscriber[String](request = true)
      assertThrows[IllegalArgumentException] {
        BodyHandlers.fromLineSubscriber(
          subscriber,
          ((_: Subscriber[? >: String]) => "done"): Function[Subscriber[? >: String], String],
          "",
        ): Unit
      }
    }

    test("fromLineSubscriber should accept non-empty lineSeparator") {
      val subscriber = MockSubscriber[String](request = true)
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

    test("discarding should handle multiple chunks") {
      val handler = BodyHandlers.discarding()
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      for (_ <- 0 until 10)
        subscriber.onNext(JList.of(ByteBuffer.wrap(new Array[Byte](1024))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == null)
    }

    test("discarding should forward onError and complete exceptionally") {
      val handler = BodyHandlers.discarding()
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("upstream error"))

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
      assert(exc.getCause().getMessage() == "upstream error")
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

    test("replacing should forward onError and complete exceptionally") {
      val handler = BodyHandlers.replacing("value")
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("upstream error"))

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
      assert(exc.getCause().getMessage() == "upstream error")
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

    test("ofString() should return empty string for empty body") {
      val handler = BodyHandlers.ofString()
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "")
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

    test("ofFile should write multiple chunks to file") {
      val tempFile = Files.createTempFile("handler-multi", ".txt")
      try {
        val handler = BodyHandlers.ofFile(tempFile)
        val subscriber = handler(createResponseInfo())

        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(JList.of(ByteBuffer.wrap("Hello, ".getBytes(StandardCharsets.UTF_8))))
        subscriber.onNext(JList.of(ByteBuffer.wrap("World!".getBytes(StandardCharsets.UTF_8))))
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture().get()
        val fileContent = Files.readString(resultPath, StandardCharsets.UTF_8)
        assert(fileContent == "Hello, World!")
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

    test("ofFile default should create file if it does not exist") {
      val tempDir = Files.createTempDirectory("handler-create-test")
      val newFile = tempDir.resolve("new-file.txt")
      try {
        assert(!Files.exists(newFile))
        val handler = BodyHandlers.ofFile(newFile)
        val subscriber = handler(createResponseInfo())

        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(
          JList.of(ByteBuffer.wrap("new content".getBytes(StandardCharsets.UTF_8))),
        )
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture().get()
        assert(Files.exists(newFile))
        assert(Files.readString(newFile, StandardCharsets.UTF_8) == "new content")
        assert(resultPath == newFile)
      } finally {
        Files.deleteIfExists(newFile): Unit
        Files.deleteIfExists(tempDir): Unit
      }
    }

    test("ofFile default should overwrite existing file content") {
      val tempFile = Files.createTempFile("handler-overwrite-test", ".txt")
      try {
        Files.writeString(tempFile, "original content")
        val handler = BodyHandlers.ofFile(tempFile)
        val subscriber = handler(createResponseInfo())

        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(JList.of(ByteBuffer.wrap("new".getBytes(StandardCharsets.UTF_8))))
        subscriber.onComplete()

        subscriber.getBody().toCompletableFuture().get()
        // WRITE without TRUNCATE_EXISTING writes from offset 0; old content may linger
        // At minimum the new bytes are at the start
        val content = Files.readString(tempFile, StandardCharsets.UTF_8)
        assert(content.startsWith("new"))
      } finally Files.deleteIfExists(tempFile): Unit
    }

    // ================================== //
    // Test BodyHandlers.ofFileDownload() //
    // ================================== //

    test("ofFileDownload should extract filename from Content-Disposition") {
      val tempDir = Files.createTempDirectory("test-downloads")
      try {
        val handler =
          BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        val responseInfo = createResponseInfo(
          Map("Content-Disposition" -> "attachment; filename=\"document.txt\""),
        )
        val subscriber = handler(responseInfo)

        subscriber.onSubscribe(MockSubscription())
        subscriber.onNext(
          JList.of(ByteBuffer.wrap("file content".getBytes(StandardCharsets.UTF_8))),
        )
        subscriber.onComplete()

        val resultPath = subscriber.getBody().toCompletableFuture().get()
        assert(resultPath.getFileName.toString == "document.txt")
        assert(Files.exists(resultPath))
        Files.deleteIfExists(resultPath): Unit
      } //
      finally //
        Files.deleteIfExists(tempDir): Unit
    }

    test("ofFileDownload should fail when Content-Disposition is missing") {
      val tempDir = Files.createTempDirectory("test-downloads")
      try {
        val handler =
          BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

        val exc = assertThrows[UncheckedIOException] {
          handler(createResponseInfo()): Unit // no Content-Disposition
        }
        assert(exc.getCause().isInstanceOf[IOException])

      } //
      finally //
        Files.deleteIfExists(tempDir): Unit
    }

    /*
     * Security: regardless of whether an implementation uses the final component or
     * rejects path traversal entirely, the resulting file MUST NOT escape the directory.
     *
     * This can pass on JVM ???
     */
    // test("ofFileDownload should reject path traversal in filename") {
    //   val tempDir = Files.createTempDirectory("test-downloads")
    //   try {
    //     val handler =
    //       BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    //     // Spec requires that filename components other than simple names (e.g. containing path separators) are rejected
    //     val responseInfo = createResponseInfo(
    //       Map("Content-Disposition" -> "attachment; filename=\"../evil.txt\""),
    //     )
    //     val subscriber = handler(responseInfo)

    //     subscriber.onSubscribe(MockSubscription())
    //     subscriber.onNext(JList.of(ByteBuffer.wrap("data".getBytes())))
    //     subscriber.onComplete()

    //     // assertThrows[DirectoryNotEmptyException] {
    //     subscriber.getBody().toCompletableFuture().get(): Unit
    //     // }
    //   } finally rmdir(tempDir): Unit
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

    test(
      "ofFileDownload should raise IOException If the destination directory cannot be written to",
    ) {
      val tempDir = Files.createTempDirectory(
        "test-downloads",
        PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("r-xr-xr-x")),
      )
      try
        assertThrows[IllegalArgumentException] {
          BodyHandlers.ofFileDownload(tempDir): Unit
        }
      finally
        Files.deleteIfExists(tempDir): Unit
    }

    test("ofFileDownload should throw IllegalArgumentException for non-existent directory") {
      val nonExistentDir = Path.of("/tmp/nonexistent-dir-snhttp-test-xyz")
      assertThrows[IllegalArgumentException] {
        BodyHandlers.ofFileDownload(nonExistentDir): Unit
      }
    }

    test("ofFileDownload should throw IllegalArgumentException when path is a regular file") {
      val tempFile = Files.createTempFile("not-a-dir-test", ".txt")
      try
        assertThrows[IllegalArgumentException] {
          BodyHandlers.ofFileDownload(tempFile): Unit
        }
      finally Files.deleteIfExists(tempFile): Unit
    }

    test("ofFileDownload should fail when Content-Disposition type is not attachment") {
      val tempDir = Files.createTempDirectory("test-downloads")
      try {
        val handler =
          BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        // "inline" is not the "attachment" type required by the spec
        val responseInfo = createResponseInfo(
          Map("Content-Disposition" -> "inline; filename=\"document.txt\""),
        )
        val exc = assertThrows[UncheckedIOException] {
          handler(responseInfo): Unit
        }
        assert(exc.getCause().isInstanceOf[IOException])
      } //
      finally //
        rmdir(tempDir): Unit
    }

    test("ofFileDownload should fail when Content-Disposition has no filename parameter") {
      val tempDir = Files.createTempDirectory("test-downloads")
      try {
        val handler =
          BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        val responseInfo = createResponseInfo(
          Map("Content-Disposition" -> "attachment"),
        )

        val exc = assertThrows[UncheckedIOException] {
          handler(responseInfo): Unit
        }
        assert(exc.getCause().isInstanceOf[IOException])

      } finally Files.deleteIfExists(tempDir): Unit
    }

    test("ofFileDownload should reject DELETE_ON_CLOSE open option") {
      val tempDir = Files.createTempDirectory("test-downloads")
      try
        assertThrows[IllegalArgumentException] {
          BodyHandlers.ofFileDownload(tempDir, StandardOpenOption.DELETE_ON_CLOSE): Unit
        }
      finally Files.deleteIfExists(tempDir): Unit
    }

    // // ================================= //
    // // Test BodyHandlers.ofInputStream() //
    // // ================================= //

    // test("ofInputStream should create handler returning InputStream") {
    //   val handler = BodyHandlers.ofInputStream()
    //   val responseInfo = createResponseInfo()
    //   val subscriber = handler(responseInfo)

    //   subscriber.onSubscribe(MockSubscription())

    //   val inputStream = subscriber.getBody().toCompletableFuture().get()
    //   assert(inputStream != null)
    //   inputStream.close()
    // }

    // test("ofInputStream should make data available to read") {
    //   val handler = BodyHandlers.ofInputStream()
    //   val subscriber = handler(createResponseInfo())

    //   subscriber.onSubscribe(MockSubscription())

    //   val inputStream = subscriber.getBody().toCompletableFuture().get()
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("stream data".getBytes(StandardCharsets.UTF_8))))
    //   subscriber.onComplete()

    //   val bytes = inputStream.readAllBytes()
    //   inputStream.close()
    //   assert(new String(bytes, StandardCharsets.UTF_8) == "stream data")
    // }

    // // =========================== //
    // // Test BodyHandlers.ofLines() //
    // // =========================== //

    // test("ofLines should create handler using charset from Content-Type") {
    //   val handler = BodyHandlers.ofLines()
    //   val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
    //   val subscriber = handler(responseInfo)

    //   subscriber.onSubscribe(MockSubscription())

    //   val stream = subscriber.getBody().toCompletableFuture().get()
    //   assert(stream != null)
    //   stream.close()
    // }

    // test("ofLines should stream body content as lines") {
    //   val handler = BodyHandlers.ofLines()
    //   val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
    //   val subscriber = handler(responseInfo)

    //   subscriber.onSubscribe(MockSubscription())
    //   val stream = subscriber.getBody().toCompletableFuture().get()
    //   subscriber.onNext(
    //     JList.of(ByteBuffer.wrap("line1\nline2\nline3".getBytes(StandardCharsets.UTF_8))),
    //   )
    //   subscriber.onComplete()

    //   val lines = stream.toArray()
    //   stream.close()
    //   assert(lines.length == 3)
    // }

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

    test("ofByteArrayConsumer should call consumer with Optional.empty on completion") {
      var receivedEmpty = false
      val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
        if (opt.isEmpty) receivedEmpty = true
      }

      val handler = BodyHandlers.ofByteArrayConsumer(consumer)
      val subscriber = handler(createResponseInfo())
      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      subscriber.getBody().toCompletableFuture().get()
      assert(receivedEmpty)
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

    test("ofByteArray should accumulate multiple chunks") {
      val handler = BodyHandlers.ofByteArray()
      val subscriber = handler(createResponseInfo())

      val data1 = "Hello, ".getBytes(StandardCharsets.UTF_8)
      val data2 = "World!".getBytes(StandardCharsets.UTF_8)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap(data1)))
      subscriber.onNext(JList.of(ByteBuffer.wrap(data2)))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result.sameElements(data1 ++ data2))
    }

    test("ofByteArray should handle empty body") {
      val handler = BodyHandlers.ofByteArray()
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result.isEmpty)
    }

    test("ofByteArray should forward onError and complete exceptionally") {
      val handler = BodyHandlers.ofByteArray()
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onError(new SPException("upstream error"))

      val exc = assertThrows[ExecutionException] {
        subscriber.getBody().toCompletableFuture().get(): Unit
      }
      assert(exc.getCause().isInstanceOf[SPException])
      assert(exc.getCause().getMessage() == "upstream error")
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

    test("ofPublisher should forward data to downstream subscriber") {
      val handler = BodyHandlers.ofPublisher()
      val subscriber = handler(createResponseInfo())
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

      subscriber.onNext(JList.of(ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      assert(received.size() >= 1)
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

    test("buffering should accumulate and deliver data across multiple chunks") {
      val downstreamHandler = BodyHandlers.ofByteArray()
      val handler = BodyHandlers.buffering(downstreamHandler, 1024)
      val subscriber = handler(createResponseInfo())

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("part1".getBytes(StandardCharsets.UTF_8))))
      subscriber.onNext(JList.of(ByteBuffer.wrap("part2".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result.sameElements("part1part2".getBytes(StandardCharsets.UTF_8)))
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

    // ============================== //
    // Test BodyHandlers.limiting()   //
    // ============================== //

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
      assertThrows[IllegalArgumentException] {
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

    // ============================================= //
    // Test BodyHandlers.ofString() charset edge     //
    // ============================================= //

    test("ofString() should fall back to UTF-8 when Content-Type charset is unsupported") {
      val handler = BodyHandlers.ofString()
      val responseInfo =
        createResponseInfo(Map("Content-Type" -> "text/plain; charset=unknown-charset-xyz"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "Hello")
    }

    test("ofString() should parse charset case-insensitively from Content-Type") {
      val handler = BodyHandlers.ofString()
      // Charset name in uppercase — must still be recognized
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=UTF-8"))
      val subscriber = handler(responseInfo)

      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8))))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture().get()
      assert(result == "Hello")
    }
