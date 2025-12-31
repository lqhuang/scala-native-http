import java.net.http.HttpResponse.BodySubscribers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.{ArrayList, Optional}
import java.util.List as JList
import java.util.concurrent.Flow.Subscription
import java.util.function.{Consumer, Function}

// import snhttp.testkits.MockSubscriber
import utest.{Tests, test, assert, assertThrows}

class BodySubscribersTest extends utest.TestSuite {

  val tests = Tests {

    // ===================================== //
    // Test BodySubscribers.fromSubscriber() //
    // ===================================== //

    // test("BodySubscribers.fromSubscriber should work with custom subscriber") {
    //   val ts = new MockSubscriber[JList[ByteBuffer]]()
    //   val subscriber = BodySubscribers.fromSubscriber(
    //     ts,
    //     (_: MockSubscriber[JList[ByteBuffer]]) => "custom result",
    //   )

    //   val testData = "Test data"
    //   val bytes = testData.getBytes(StandardCharsets.UTF_8)
    //   val buffer = ByteBuffer.wrap(bytes)

    //   subscriber.onSubscribe(new Subscription {
    //     override def request(n: Long): Unit = ()
    //     override def cancel(): Unit = ()
    //   })
    //   subscriber.onNext(JList.of(buffer))
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture.get()
    //   assert(result, "custom result")

    // }

    // ========================================= //
    // Test BodySubscribers.fromLineSubscriber() //
    // ========================================= //

    // test("BodySubscribers.fromLineSubscriber should handle line processing") {
    //   val lineSubscriber = new MockSubscriber[String]()
    //   val subscriber = BodySubscribers.fromLineSubscriber(
    //     lineSubscriber,
    //     (_: MockSubscriber[String]) => "line processing complete",
    //     StandardCharsets.UTF_8,
    //     "\n",
    //   )

    //   val testData = "Line 1\nLine 2\n"
    //   val bytes = testData.getBytes(StandardCharsets.UTF_8)

    //   subscriber.onSubscribe(new Subscription {
    //     override def request(n: Long): Unit = ()
    //     override def cancel(): Unit = ()
    //   })
    //   subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    //   subscriber.onComplete()

    //   val result = subscriber.getBody().toCompletableFuture.get()
    //   assert(result, "line processing complete")
    // }

    // =============================== //
    // Test BodySubscribers.ofString() //
    // =============================== //

    test("BodySubscribers.ofString should handle empty data") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)

      subscriber.onSubscribe(new Subscription {
        override def request(n: Long): Unit = ()
        override def cancel(): Unit = ()
      })
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == "")
    }

    test("BodySubscribers.ofString should handle errors") {
      val subscriber = BodySubscribers.ofString(StandardCharsets.UTF_8)
      val testError = new RuntimeException("Test error")

      subscriber.onSubscribe(new Subscription {
        override def request(n: Long): Unit = ()
        override def cancel(): Unit = ()
      })
      subscriber.onError(testError)

      assertThrows[RuntimeException] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

    test("BodySubscribers.ofString should handle subscription cancellation") {
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
      assert(result == testData)
    }

    // ================================== //
    // Test BodySubscribers.ofByteArray() //
    // ================================== //

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
      assert(result.sameElements(expected))
    }

    test("BodySubscribers.ofByteArray should be joined multiple buffers correctly") {
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
      assert(result.sameElements(expected))
    }

    test("BodySubscribers.ofByteArray should handle many small chunks efficiently") {
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

      assert(result.length == chunkSize * numChunks)
      assert(endTime - startTime < 2000) // Should complete within 2 seconds
    }

    test("BodySubscribers.ofByteArray should handle very large data efficiently") {
      val subscriber = BodySubscribers.ofByteArray()
      val largeDataSize = 1024 * 1024 // 1MB
      val largeData = new Array[Byte](largeDataSize)
      val largeDataBuffer = ByteBuffer.wrap(largeData)

      subscriber.onSubscribe(new Subscription {
        override def request(n: Long): Unit = ()
        override def cancel(): Unit = ()
      })

      val startTime = System.currentTimeMillis()
      subscriber.onNext(JList.of(largeDataBuffer))
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      val endTime = System.currentTimeMillis()

      assert(result.length == largeDataSize)
      assert(endTime - startTime < 1000) // Should complete within 1 second
    }

    // ============================= //
    // Test BodySubscribers.ofFile() //
    // ============================= //

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
        assert(resultPath == tempFile)

        val fileContent = Files.readString(tempFile, StandardCharsets.UTF_8)
        assert(fileContent == testData)
      } finally Files.deleteIfExists(tempFile): Unit
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
        assert(fileContent == "Part1Part2")
      } finally Files.deleteIfExists(tempFile): Unit
    }

    // ========================================== //
    // Test BodySubscribers.ofByteArrayConsumer() //
    // ========================================== //

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

      assertThrows[Exception]:
        val result = subscriber.getBody().toCompletableFuture.get()
    }

    test("BodySubscribers.ofByteArrayConsumer should call consumer") {
      val consumedData = new ArrayList[Array[Byte]]()
      val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
        if (opt.isPresent)
          consumedData.add(opt.get()): Unit
      }

      val subscriber = BodySubscribers.ofByteArrayConsumer(consumer)
      val testData = "Consumer test"
      val expectedBytes = testData.getBytes(StandardCharsets.UTF_8)

      subscriber.onSubscribe(new Subscription {
        override def request(n: Long): Unit = ()
        override def cancel(): Unit = ()
      })
      subscriber.onNext(JList.of(ByteBuffer.wrap(expectedBytes)))
      subscriber.onComplete()

      subscriber.getBody().toCompletableFuture.get()
      assert(consumedData.size() == 1)
      assert(consumedData.get(0).sameElements(expectedBytes))
    }

//   // ==================================== //
//   // Test BodySubscribers.ofInputStream() //
//   // ==================================== //

//   test(
//     "BodySubscribers.ofInputStream should provide readable stream".pending("FIXME: won't terminate"),
//   ) {
//     val subscriber = BodySubscribers.ofInputStream()
//     val testData = "Stream content test"
//     val bytes = testData.getBytes(StandardCharsets.UTF_8)

//     subscriber.onSubscribe(new Subscription {
//       override def request(n: Long): Unit = ()
//       override def cancel(): Unit = ()
//     })
//     subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
//     subscriber.onComplete()

//     val inputStream = subscriber.getBody().toCompletableFuture.get()
//     // Using.resource(inputStream) { stream =>
//     //   val result = new String(stream.readAllBytes(), StandardCharsets.UTF_8)
//     //   assert(result, testData)
//     // }
//   }

//   test(
//     "BodySubscribers.ofInputStream should handle partial reads".pending("FIXME: won't terminate"),
//   ) {
//     val subscriber = BodySubscribers.ofInputStream()
//     val testData = "This is a longer test string for partial reading"
//     val bytes = testData.getBytes(StandardCharsets.UTF_8)

//     subscriber.onSubscribe(new Subscription {
//       override def request(n: Long): Unit = ()
//       override def cancel(): Unit = ()
//     })
//     subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
//     subscriber.onComplete()

//     val inputStream = subscriber.getBody().toCompletableFuture.get()
//     // Using.resource(inputStream) { stream =>
//     //   val buffer = new Array[Byte](10)
//     //   val bytesRead = stream.read(buffer)
//     //   assert(bytesRead, 10)

//     //   val partialResult = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)
//     //   assert(partialResult, "This is a ")
//     // }
//   }

//   // ============================== //
//   // Test BodySubscribers.ofLines() //
//   // ============================== //

//   test("BodySubscribers.ofLines should split text into lines".pending("FIXME: won't terminate")) {
//     val subscriber = BodySubscribers.ofLines(StandardCharsets.UTF_8)
//     val testData = "Line 1\nLine 2\nLine 3"
//     val bytes = testData.getBytes(StandardCharsets.UTF_8)

//     subscriber.onSubscribe(new Subscription {
//       override def request(n: Long): Unit = ()
//       override def cancel(): Unit = ()
//     })
//     subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
//     subscriber.onComplete()

//     val stream = subscriber.getBody().toCompletableFuture.get()
//     val lines = stream.toList()
//     assert(lines, JList.of("Line 1", "Line 2", "Line 3"))
//   }

//   test(
//     "BodySubscribers.ofLines should handle different line separators".pending(
//       "FIXME: won't terminate",
//     ),
//   ) {
//     val subscriber = BodySubscribers.ofLines(StandardCharsets.UTF_8)
//     val testData = "Line 1\r\nLine 2\rLine 3\nLine 4"
//     val bytes = testData.getBytes(StandardCharsets.UTF_8)

//     subscriber.onSubscribe(new Subscription {
//       override def request(n: Long): Unit = ()
//       override def cancel(): Unit = ()
//     })
//     subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
//     subscriber.onComplete()

//     val stream = subscriber.getBody().toCompletableFuture.get()
//     val lines = stream.toList()
//     // Should handle different line separators
//     assert(lines.size >= 3)
//   }

    // ================================== //
    // Test BodySubscribers.ofPublisher() //
    // ================================== //

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
    }

    // ================================ //
    // Test BodySubscribers.replacing() //
    // ================================ //

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
      assert(result == replacementValue)
    }

    // ================================= //
    // Test BodySubscribers.discarding() //
    // ================================= //

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
      // assert(result, null)
    }

    // ================================ //
    // Test BodySubscribers.buffering() //
    // ================================ //

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
      assert(result.sameElements(exactData))
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
      assert(result.sameElements(expected))
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
        val remaining = chunkSize.min(bytes.length - offset)
        val chunk = Array.copyOf(bytes.slice(offset, offset + remaining), remaining)
        subscriber.onNext(JList.of(ByteBuffer.wrap(chunk)))
        offset += remaining
      }
      subscriber.onComplete()

      val result = subscriber.getBody().toCompletableFuture.get()
      assert(result == jsonData)
    }

    // ============================== //
    // Test BodySubscribers.mapping() //
    // ============================== //

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
      assert(result == 5)
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

      assertThrows[Exception] {
        subscriber.getBody().toCompletableFuture.get(): Unit
      }
    }

  }
}
