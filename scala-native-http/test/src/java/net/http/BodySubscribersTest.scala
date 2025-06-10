package java.net.http

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.List as JList
import java.util.{ArrayList, Optional}
import java.util.concurrent.Flow.Subscription
import java.util.function.Consumer
import java.net.http.HttpResponse.BodySubscribers

import scala.util.Using

class BodySubscribersTest extends munit.FunSuite {

  test("ConsumerSubscriber should handle byte array consumption") {
    val consumedData = new ArrayList[Array[Byte]]()
    val consumer: Consumer[Optional[Array[Byte]]] = { opt =>
      if (opt.isPresent) consumedData.add(opt.get())
    }

    val subscriber = new ResponseSubscribers.ConsumerSubscriber(consumer)
    val testData = "Test data for consumer"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, null)
    assertEquals(consumedData.size(), 1)
    assert(java.util.Arrays.equals(consumedData.get(0), bytes))
  }

  test("PathSubscriber should write to file with custom options") {
    val tempFile = Files.createTempFile("path-test", ".txt")
    try {
      val options = JList.of(
        java.nio.file.StandardOpenOption.CREATE,
        java.nio.file.StandardOpenOption.WRITE,
        java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
      )
      val subscriber = ResponseSubscribers.PathSubscriber.create(tempFile, options)

      val testData = "Path subscriber test data"
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

  test("ByteArraySubscriber should handle multiple chunks") {
    val finisher = (bytes: Array[Byte]) => new String(bytes, StandardCharsets.UTF_8)
    val subscriber = new ResponseSubscribers.ByteArraySubscriber(finisher)

    val chunk1 = "Hello, ".getBytes(StandardCharsets.UTF_8)
    val chunk2 = "World!".getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(chunk1)))
    subscriber.onNext(JList.of(ByteBuffer.wrap(chunk2)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, "Hello, World!")
  }

  test("InputStreamSubscriber should handle incremental reading") {
    val subscriber = new ResponseSubscribers.InputStreamSubscriber()
    val testData = "Incremental reading test data"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val inputStream = subscriber.getBody().toCompletableFuture.get()
    Using.resource(inputStream) { stream =>
      val buffer = new Array[Byte](5)
      val firstRead = stream.read(buffer)
      assertEquals(firstRead, 5)
      assertEquals(new String(buffer, 0, firstRead, StandardCharsets.UTF_8), "Incre")

      val remainingBytes = stream.readAllBytes()
      val remaining = new String(remainingBytes, StandardCharsets.UTF_8)
      assertEquals(remaining, "mental reading test data")
    }
  }

  test("NullSubscriber should handle replacement values") {
    val replacementValue = "Custom replacement"
    val subscriber = new ResponseSubscribers.NullSubscriber(Optional.of(replacementValue))

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap("ignored data".getBytes())))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, replacementValue)
  }

  test("MappingSubscriber should transform results correctly") {
    val upstreamSubscriber = new ResponseSubscribers.ByteArraySubscriber(identity)
    val mapper = (bytes: Array[Byte]) => bytes.length
    val subscriber = new ResponseSubscribers.MappingSubscriber(upstreamSubscriber, mapper, true)

    val testData = "Test data for mapping"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    assertEquals(result, bytes.length)
  }

  test("PublishingBodySubscriber should create functional publisher") {
    val subscriber = new ResponseSubscribers.PublishingBodySubscriber()
    val testData = "Publisher data"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    val publisher = subscriber.getBody().toCompletableFuture.get()
    assertNotEquals(publisher, null)

    // Test that publisher can be subscribed to
    var receivedData: JList[ByteBuffer] = null
    var completed = false

    publisher.subscribe(new java.util.concurrent.Flow.Subscriber[JList[ByteBuffer]] {
      override def onSubscribe(s: Subscription): Unit = s.request(1)
      override def onNext(item: JList[ByteBuffer]): Unit = receivedData = item
      override def onError(throwable: Throwable): Unit = ()
      override def onComplete(): Unit = completed = true
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(bytes)))
    subscriber.onComplete()

    assertNotEquals(receivedData, null)
    assert(completed)
  }

  test("BufferingSubscriber should accumulate before forwarding") {
    val downstreamSubscriber = new ResponseSubscribers.ByteArraySubscriber(identity)
    val bufferSize = 15
    val subscriber = new ResponseSubscribers.BufferingSubscriber(downstreamSubscriber, bufferSize)

    val smallChunk = "Hello".getBytes(StandardCharsets.UTF_8) // 5 bytes
    val mediumChunk = " World".getBytes(StandardCharsets.UTF_8) // 6 bytes
    val largeChunk = "!".getBytes(StandardCharsets.UTF_8) // 1 byte

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    // Should buffer these (total: 11 bytes < 15)
    subscriber.onNext(JList.of(ByteBuffer.wrap(smallChunk)))
    subscriber.onNext(JList.of(ByteBuffer.wrap(mediumChunk)))
    subscriber.onNext(JList.of(ByteBuffer.wrap(largeChunk)))
    subscriber.onComplete()

    val result = subscriber.getBody().toCompletableFuture.get()
    val expected = smallChunk ++ mediumChunk ++ largeChunk
    assert(java.util.Arrays.equals(result, expected))
  }

  test("LimitingSubscriber should enforce byte limits") {
    val downstreamSubscriber = new ResponseSubscribers.ByteArraySubscriber(identity)
    val capacity = 10L
    val subscriber = new ResponseSubscribers.LimitingSubscriber(downstreamSubscriber, capacity)

    val smallData = "small".getBytes(StandardCharsets.UTF_8) // 5 bytes
    val largeData = "large data".getBytes(StandardCharsets.UTF_8) // 10 bytes - total would be 15

    subscriber.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    subscriber.onNext(JList.of(ByteBuffer.wrap(smallData))) // 5 bytes total

    // This should trigger the capacity limit
    intercept[Exception] {
      subscriber.onNext(JList.of(ByteBuffer.wrap(largeData))) // Would be 15 bytes total
    }
  }

  test("SubscriberAdapter should handle custom subscriber with finisher") {
    class CountingSubscriber extends java.util.concurrent.Flow.Subscriber[JList[ByteBuffer]] {
      private var itemCount = 0
      private var completed = false

      override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)
      override def onNext(item: JList[ByteBuffer]): Unit = itemCount += item.size()
      override def onError(throwable: Throwable): Unit = ()
      override def onComplete(): Unit = completed = true

      def getCount: Int = itemCount
      def isCompleted: Boolean = completed
    }

    val countingSubscriber = new CountingSubscriber()
    val finisher = (cs: CountingSubscriber) => s"Received ${cs.getCount} buffers"
    val adapter = new ResponseSubscribers.SubscriberAdapter(countingSubscriber, finisher)

    adapter.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    adapter.onNext(JList.of(ByteBuffer.wrap("test1".getBytes())))
    adapter.onNext(
      JList.of(ByteBuffer.wrap("test2".getBytes()), ByteBuffer.wrap("test3".getBytes())),
    )
    adapter.onComplete()

    val result = adapter.getBody().toCompletableFuture.get()
    assertEquals(result, "Received 3 buffers")
    assert(countingSubscriber.isCompleted)
  }

  test("LineSubscriberAdapter should handle line-by-line processing") {
    class LineCollector extends java.util.concurrent.Flow.Subscriber[String] {
      private val lines = new ArrayList[String]()
      private var completed = false

      override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)
      override def onNext(line: String): Unit = lines.add(line)
      override def onError(throwable: Throwable): Unit = ()
      override def onComplete(): Unit = completed = true

      def getLines: JList[String] = lines
      def isCompleted: Boolean = completed
    }

    val lineCollector = new LineCollector()
    val finisher = (lc: LineCollector) => lc.getLines.size()
    val adapter = new ResponseSubscribers.LineSubscriberAdapter(
      lineCollector,
      finisher,
      StandardCharsets.UTF_8,
      "\n",
    )

    val testData = "First line\nSecond line\nThird line"
    val bytes = testData.getBytes(StandardCharsets.UTF_8)

    adapter.onSubscribe(new Subscription {
      override def request(n: Long): Unit = ()
      override def cancel(): Unit = ()
    })

    adapter.onNext(JList.of(ByteBuffer.wrap(bytes)))
    adapter.onComplete()

    val result = adapter.getBody().toCompletableFuture.get()
    assertEquals(result, 3)
    assert(lineCollector.isCompleted)
    assertEquals(lineCollector.getLines.get(0), "First line")
    assertEquals(lineCollector.getLines.get(1), "Second line")
    assertEquals(lineCollector.getLines.get(2), "Third line")
  }
}
