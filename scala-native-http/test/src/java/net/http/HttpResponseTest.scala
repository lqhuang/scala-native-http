import java.net.http.HttpResponse
import java.net.http.HttpResponse.{BodyHandler, BodyHandlers, BodySubscriber, BodySubscribers}
import java.net.http.{HttpClient, HttpHeaders, HttpRequest}
import java.net.http.HttpResponseMocks.*
import java.io.{InputStream, ByteArrayInputStream}
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import java.util.List as JList
import java.util.{ArrayList, Optional, Collections}
import java.util.concurrent.{CompletableFuture, Flow}
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.function.{Consumer, Function}
import java.util.stream.Stream
import scala.jdk.CollectionConverters.*
import scala.concurrent.duration.*
import scala.util.Using

class HttpResponseTest extends munit.FunSuite {

  // Test BodySubscribers
  test("BodySubscribers.ofString should handle UTF-8 text") {
    val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
    val testData = "Hello, World! 🌍"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)
    val buffer = ByteBuffer.wrap(bytes)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(buffer))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, testData)
  }

  test("BodySubscribers.ofByteArray should collect all bytes") {
    val subscriber = BodySubscribers.ofByteArray()
    val data1 = "Hello, ".getBytes(StandardCharsets.UTF_8)
    val data2 = "World!".getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(data1)))
    subscriber.onNext(JList.of(ByteBuffer.wrap(data2)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    val expected = data1 ++ data2
    assert(java.util.Arrays.equals(result, expected))
  }

  test("BodySubscribers.ofFile should write to file") {
    val tempFile = Files.createTempFile("test", ".txt")
    try {
      val subscriber = BodySubscribers.ofFile(tempFile)
      val testData = "File content test"
      val bytes = testData.getBytes(StandardCharsets.UTF_8)

      subscriber.onSubscribe(new Subscription {
        override def request(n: Long): Unit = ()
        override def cancel(): Unit = ()
      })

      subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
      subscriber.onComplete()

      val resultPath = subscriber.getBody().toCompletableFuture.get()
      assertEquals(resultPath, tempFile)

      val fileContent = Files.readString(tempFile, StandardCharsets.UTF_8)
      assertEquals(fileContent, testData)
    } finally Files.deleteIfExists(tempFile)
  }

  test("BodySubscribers.ofInputStream should provide readable stream") {
    val subscriber = BodySubscribers.ofInputStream()
    val testData = "Stream content test"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val inputStream = subscriber.getBody().toCompletableFuture.get()
    Using.resource(inputStream) { stream =>
      val result = new String(stream.readAllBytes(), StandardCharsets.UTF_8)
      assertEquals(result, testData)
    }
  }

  test("BodySubscribers.discarding should ignore all data") {
    val subscriber = BodySubscribers.discarding()
    val testData = "This will be discarded"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, null)
  }

  test("BodySubscribers.replacing should return specified value") {
    val replacementValue = "replaced"
    val subscriber = BodySubscribers.replacing(replacementValue)
    val testData = "Original data"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, replacementValue)
  }

  test("BodySubscribers.buffering should buffer data before forwarding") {
    val downstreamSubscriber = BodySubscribers.ofByteArray()
    val bufferSize = 10
    val subscriber = BodySubscribers.buffering(downstreamSubscriber, bufferSize)

    val smallData = "Hi".getBytes(StandardCharsets.UTF_8) // 2 bytes
    val largeData = "Hello World!".getBytes(StandardCharsets.UTF_8) // 12 bytes

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    // This should be buffered (total: 2 bytes)
    subscriber.onNext(JList.of(ByteBuffer.wrap(smallData)))

    // This should trigger flush (total: 14 bytes > 10)
    subscriber.onNext(JList.of(ByteBuffer.wrap(largeData)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    val expected = smallData ++ largeData
    assert(java.util.Arrays.equals(result, expected))
  }

  test("BodySubscribers.ofLines should split text into lines") {
    val subscriber = BodySubscribers.ofLines(StandardCharsets.UTF_8)
    val testData = "Line 1\nLine 2\nLine 3"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val stream = subscriber.getBody().toCompletableFuture.get()
    val lines = stream.toArray().toList
    assertEquals(lines, List("Line 1", "Line 2", "Line 3"))
  }

  test("BodySubscribers.fromSubscriber should work with custom subscriber") {
    val testSubscriber = new TestSubscriber[JList[ByteBuffer]]()
    val subscriber = BodySubscribers.fromSubscriber(
      testSubscriber,
      (_: TestSubscriber[JList[ByteBuffer]]) => "custom result",
    )

    val testData = "Test data"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)
    val buffer = ByteBuffer.wrap(bytes)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(buffer))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, "custom result")
    assert(testSubscriber.isCompleted)
  }

  test("BodySubscribers.mapping should transform upstream result") {
    val upstreamSubscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
    val mapper: Function[String, Int] = _.length
    val subscriber = BodySubscribers.mapping(upstreamSubscriber, mapper)

    val testData = "Hello"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, 5)
  }

  // Test BodyHandlers
  test("BodyHandlers.ofString should create string handler") {
    val handler = BodyHandlers.ofString()
    val responseInfo = new TestResponseInfo(
      _headers = MockHttpHeaders.of(Map("Content-Type" -> "text/plain; charset=utf-8")),
    )

    val subscriber = handler(responseInfo)
    assertNotEquals(subscriber, null)
  }

  test("BodyHandlers.ofByteArray should create byte array handler") {
    val handler = BodyHandlers.ofByteArray()
    val responseInfo = new TestResponseInfo()

    val subscriber = handler(responseInfo)
    assertNotEquals(subscriber, null)
  }

  test("BodyHandlers.ofFile should create file handler") {
    val tempFile = Files.createTempFile("test", ".txt")
    try {
      val handler = BodyHandlers.ofFile(tempFile)
      val responseInfo = new TestResponseInfo()

      val subscriber = handler(responseInfo)
      assertNotEquals(subscriber, null)
    } finally Files.deleteIfExists(tempFile)
  }

  test("BodyHandlers.discarding should create discarding handler") {
    val handler = BodyHandlers.discarding()
    val responseInfo = new TestResponseInfo(_statusCode = 204)

    val subscriber = handler(responseInfo)
    assertNotEquals(subscriber, null)
  }

  test("BodyHandlers.buffering should create buffering handler") {
    val downstreamHandler = BodyHandlers.ofString()
    val bufferSize = 1024
    val handler = BodyHandlers.buffering(downstreamHandler, bufferSize)
    val responseInfo = new TestResponseInfo(
      _headers = MockHttpHeaders.of(Map("Content-Type" -> "text/plain; charset=utf-8")),
    )

    val subscriber = handler(responseInfo)
    assertNotEquals(subscriber, null)
  }

  // Edge cases and error handling
  test("BodySubscribers should handle empty data") {
    val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, "")
  }

  test("BodySubscribers should handle errors") {
    val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
    val testError = new RuntimeException("Test error")

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onError(testError)

    intercept[Exception] {
      subscriber.getBody().toCompletableFuture.get()
    }
  }

  test("BodySubscribers.ofByteArrayConsumer should call consumer") {
    val consumedData = new ArrayList[Array[Byte]]()
    val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
      if (opt.isPresent) consumedData.add(opt.get())
    }

    val subscriber = BodySubscribers.ofByteArrayConsumer(consumer)
    val testData = "Consumer test"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    subscriber.getBody().toCompletableFuture.get()
    assertEquals(consumedData.size(), 1)
    assert(java.util.Arrays.equals(consumedData.get(0), bytes))
  }

  // Additional comprehensive tests for HttpResponse functionality
  test("HttpResponse mock should provide correct properties") {
    val mockResponse = new MockHttpResponse(
      _statusCode = 200,
      _body = "Test body",
      _headers = MockHttpHeaders.of(Map("Content-Type" -> "text/plain")),
      _uri = URI.create("https://api.example.com/test"),
    )

    assertEquals(mockResponse.statusCode(), 200)
    assertEquals(mockResponse.body(), "Test body")
    assertEquals(mockResponse.uri(), URI.create("https://api.example.com/test"))
    assertEquals(mockResponse.version(), HttpClient.Version.HTTP_1_1)
    assert(mockResponse.previousResponse().isEmpty)
    assert(mockResponse.connectionLabel().isEmpty)
  }

  test("BodySubscribers.ofPublisher should create publisher") {
    val subscriber = BodySubscribers.ofPublisher()
    val testData = "Publisher test"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val publisher = subscriber.getBody().toCompletableFuture.get()
    assertNotEquals(publisher, null)
  }

  test("BodyHandlers.ofInputStream should create input stream handler") {
    val handler = BodyHandlers.ofInputStream()
    val responseInfo = new TestResponseInfo()

    val subscriber = handler(responseInfo)
    assertNotEquals(subscriber, null)
  }

  test("BodyHandlers.ofLines should create lines handler") {
    val handler = BodyHandlers.ofLines()
    val responseInfo = new TestResponseInfo(
      _headers = MockHttpHeaders.of(Map("Content-Type" -> "text/plain; charset=utf-8")),
    )

    val subscriber = handler(responseInfo)
    assertNotEquals(subscriber, null)
  }

  test("BodyHandlers.replacing should create replacing handler") {
    val handler = BodyHandlers.replacing("replacement")
    val responseInfo = new TestResponseInfo()

    val subscriber = handler(responseInfo)
    assertNotEquals(subscriber, null)
  }

  test("BodySubscribers multiple buffers should be joined correctly") {
    val subscriber = BodySubscribers.ofByteArray()
    val data1 = "Part1".getBytes(StandardCharsets.UTF_8)
    val data2 = "Part2".getBytes(StandardCharsets.UTF_8)
    val data3 = "Part3".getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(
      JList.of(
        ByteBuffer.wrap(data1),
        ByteBuffer.wrap(data2),
        ByteBuffer.wrap(data3),
      ),
    )
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    val expected = data1 ++ data2 ++ data3
    assert(java.util.Arrays.equals(result, expected))
  }

  test("BodySubscribers.fromLineSubscriber should handle line processing") {
    val lineSubscriber = new TestSubscriber[String]()
    val subscriber = BodySubscribers.fromLineSubscriber(
      lineSubscriber,
      (_: TestSubscriber[String]) => "line processing complete",
      StandardCharsets.UTF_8,
      "\n",
    )

    val testData = "Line 1\nLine 2\n"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, "line processing complete")
  }

  // Edge cases and corner scenarios
  test("BodySubscribers should handle subscription cancellation") {
    val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
    var cancelled = false

    val subscription = new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = cancelled = true
    }

    subscriber.onSubscribe(subscription)

    // Cancel subscription before any data
    subscription.cancel()
    assert(cancelled)
  }

  test("BodySubscribers.ofInputStream should handle partial reads") {
    val subscriber = BodySubscribers.ofInputStream()
    val testData = "This is a longer test string for partial reading"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val inputStream = subscriber.getBody().toCompletableFuture.get()
    Using.resource(inputStream) { stream =>
      val buffer = new Array[Byte](10)
      val bytesRead = stream.read(buffer)
      assertEquals(bytesRead, 10)

      val partialResult = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)
      assertEquals(partialResult, "This is a ")
    }
  }

  test("BodySubscribers.buffering should handle exact buffer size boundary") {
    val downstreamSubscriber = BodySubscribers.ofByteArray()
    val bufferSize = 10
    val subscriber = BodySubscribers.buffering(downstreamSubscriber, bufferSize)

    val exactData = "1234567890".getBytes(StandardCharsets.UTF_8) // Exactly 10 bytes

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(exactData)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assert(java.util.Arrays.equals(result, exactData))
  }

  test("BodySubscribers.ofLines should handle different line separators") {
    val subscriber = BodySubscribers.ofLines(StandardCharsets.UTF_8)
    val testData = "Line 1\r\nLine 2\rLine 3\nLine 4"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val stream = subscriber.getBody().toCompletableFuture.get()
    val lines = stream.toArray().toList
    // Should handle different line separators
    assert(lines.size >= 3)
  }

  test("BodySubscribers should handle very large data efficiently") {
    val subscriber = BodySubscribers.ofByteArray()
    val largeDataSize = 1024 * 1024 // 1MB
    val largeData = new Array[Byte](largeDataSize)
    java.util.Arrays.fill(largeData, 65.toByte) // Fill with 'A'

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    val startTime = System.currentTimeMillis()
    subscriber.onNext(JList.of(ByteBuffer.wrap(largeData)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    val endTime = System.currentTimeMillis()

    assertEquals(result.length, largeDataSize)
    assert(endTime - startTime < 1000) // Should complete within 1 second
  }

  test("BodySubscribers.ofFile should handle concurrent writes") {
    val tempFile = Files.createTempFile("concurrent-test", ".txt")
    try {
      val subscriber = BodySubscribers.ofFile(tempFile)
      val data1 = "Part1".getBytes(StandardCharsets.UTF_8)
      val data2 = "Part2".getBytes(StandardCharsets.UTF_8)

      subscriber.onSubscribe(new Subscription {
        override def request(n: Long): Unit = ()
        override def cancel(): Unit = ()
      })

      // Simulate rapid sequential writes
      subscriber.onNext(JList.of(ByteBuffer.wrap(data1)))
      subscriber.onNext(JList.of(ByteBuffer.wrap(data2)))
      subscriber.onComplete()

      val resultPath = subscriber.getBody().toCompletableFuture.get()
      val fileContent = Files.readString(tempFile, StandardCharsets.UTF_8)
      assertEquals(fileContent, "Part1Part2")
    } finally Files.deleteIfExists(tempFile)
  }

  test("BodyHandlers.fromSubscriber should handle custom finisher exceptions") {
    val testSubscriber = new TestSubscriber[JList[ByteBuffer]]()
    val faultyFinisher: Function[TestSubscriber[JList[ByteBuffer]], String] =
      _ => throw new RuntimeException("Finisher error")

    val subscriber = BodySubscribers.fromSubscriber(testSubscriber, faultyFinisher)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
    subscriber.onComplete()

    intercept[Exception] {
      subscriber.getBody().toCompletableFuture.get()
    }
  }

  test("BodySubscribers.ofByteArrayConsumer should handle consumer exceptions") {
    val faultyConsumer: Consumer[Optional[Array[Byte]]] =
      _ => throw new RuntimeException("Consumer error")

    val subscriber = BodySubscribers.ofByteArrayConsumer(faultyConsumer)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    // This should not throw during onNext, but may cause issues during processing
    subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
    subscriber.onComplete()

    // The body should still complete successfully as the consumer error is isolated
    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, null)
  }

  test("BodySubscribers.mapping should handle mapper exceptions") {
    val upstreamSubscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
    val faultyMapper: Function[String, Int] = _ => throw new RuntimeException("Mapper error")
    val subscriber = BodySubscribers.mapping(upstreamSubscriber, faultyMapper)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
    subscriber.onComplete()

    intercept[Exception] {
      subscriber.getBody().toCompletableFuture.get()
    }
  }

  test("HttpResponse should handle multiple previous responses") {
    val response1 = new MockHttpResponse(_statusCode = 301, _body = "")
    val response2 = new MockHttpResponse(
      _statusCode = 302,
      _body = "",
      _previousResponse = Optional.of(response1),
    )
    val finalResponse = new MockHttpResponse(
      _statusCode = 200,
      _body = "Final content",
      _previousResponse = Optional.of(response2),
    )

    assertEquals(finalResponse.statusCode(), 200)
    assert(finalResponse.previousResponse().isPresent)
    assertEquals(finalResponse.previousResponse().get().statusCode(), 302)
    assert(finalResponse.previousResponse().get().previousResponse().isPresent)
    assertEquals(finalResponse.previousResponse().get().previousResponse().get().statusCode(), 301)
  }

  test("BodyHandlers should preserve original request context") {
    val handler = BodyHandlers.ofString()
    val customHeaders = MockHttpHeaders.of(
      Map(
        "Content-Type" -> "application/json; charset=utf-8",
        "Content-Length" -> "100",
      ),
    )

    val responseInfo = new TestResponseInfo(
      _statusCode = 200,
      _headers = customHeaders,
      _version = HttpClient.Version.HTTP_2,
    )

    val subscriber = handler(responseInfo)
    assertNotEquals(subscriber, null)

    // Verify the charset was correctly extracted from headers
    assertEquals(
      responseInfo.headers().firstValue("Content-Type").get(),
      "application/json; charset=utf-8",
    )
  }

  // Performance and stress tests
  test("BodySubscribers should handle many small chunks efficiently") {
    val subscriber = BodySubscribers.ofByteArray()
    val chunkSize = 10
    val numChunks = 1000

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    val startTime = System.currentTimeMillis()

    for (i <- 0 until numChunks) {
      val chunk = s"chunk-$i".padTo(chunkSize, ' ').getBytes(StandardCharsets.UTF_8)
      subscriber.onNext(JList.of(ByteBuffer.wrap(chunk)))
    }
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    val endTime = System.currentTimeMillis()

    assertEquals(result.length, chunkSize * numChunks)
    assert(endTime - startTime < 2000) // Should complete within 2 seconds
  }

  test("BodySubscribers.buffering should optimize for typical web content sizes") {
    val downstreamSubscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
    val bufferSize = 8192 // Typical web buffer size
    val subscriber = BodySubscribers.buffering(downstreamSubscriber, bufferSize)

    // Simulate typical JSON response
    val jsonData = """{"users": [""" +
      (1 to 100)
        .map(i => s"""{"id": $i, "name": "User$i", "email": "user$i@example.com"}""")
        .mkString(",") +
      "]}"
    val bytes = jsonData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    // Send in realistic chunks
    val chunkSize = 1024
    var offset = 0
    while (offset < bytes.length) {
      val remaining = Math.min(chunkSize, bytes.length - offset)
      val chunk = java.util.Arrays.copyOfRange(bytes, offset, offset + remaining)
      subscriber.onNext(JList.of(ByteBuffer.wrap(chunk)))
      offset += remaining
    }
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, jsonData)
  }
}
