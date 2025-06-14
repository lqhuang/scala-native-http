import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpRequest.BodyPublishers
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.io.{ByteArrayInputStream, IOException}
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.Flow
import scala.collection.mutable.ListBuffer

import snhttp.jdk.BodyPublishersImpl
import snhttp.jdk.PullPublisher
import snhttp.testkits.MockedSubscriber

class BodyPublishersTest extends munit.FunSuite {

  test("placeholder for PullPublisher tests") {}

  test("publish empty iterable should complete immediately") {
    val publisher = PullPublisher(List.empty[String])
    val subscriber = new MockedSubscriber[String]()

    publisher.subscribe(subscriber)

    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received, List.empty)
    assert(subscriber.completed.get())
    assert(!subscriber.hasError.get())
  }

  test("publish single item should deliver and complete") {
    val items = List("hello")
    val publisher = PullPublisher(items)
    val subscriber = new MockedSubscriber[String]()

    publisher.subscribe(subscriber)
    subscriber.subscription.request(1)

    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received, items)
    assert(subscriber.completed.get())
    assert(!subscriber.hasError.get())
  }

  test("publish multiple items should deliver all and complete") {
    val items = List("a", "b", "c", "d", "e")
    val publisher = PullPublisher(items)
    val subscriber = new MockedSubscriber[String]()

    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)

    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received, items)
    assert(subscriber.completed.get())
    assert(!subscriber.hasError.get())
  }

  test("request more than available should deliver all and complete") {
    val items = List(1, 2, 3)
    val publisher = PullPublisher(items)
    val subscriber = new MockedSubscriber[Int]()

    publisher.subscribe(subscriber)
    subscriber.subscription.request(100)

    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received, items)
    assert(subscriber.completed.get())
    assert(!subscriber.hasError.get())
  }

  test("backpressure should work with demand") {
    val items = List(1, 2, 3, 4, 5)
    val publisher = PullPublisher(items)
    val subscriber = new MockedSubscriber[Int]()

    publisher.subscribe(subscriber)

    // Request 2 items
    subscriber.subscription.request(2)
    Thread.sleep(100)
    assertEquals(subscriber.received.size, 2)
    assertEquals(subscriber.received, List(1, 2))
    assert(!subscriber.completed.get())

    // Request 2 more items
    subscriber.subscription.request(2)
    Thread.sleep(100)
    assertEquals(subscriber.received.size, 4)
    assertEquals(subscriber.received, List(1, 2, 3, 4))
    assert(!subscriber.completed.get())

    // Request last item
    subscriber.subscription.request(1)
    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received, items)
    assert(subscriber.completed.get())
  }

  test("zero or negative request should cause error") {
    val items = List(1, 2, 3)
    val publisher = PullPublisher(items)
    val subscriber = new MockedSubscriber[Int]()

    publisher.subscribe(subscriber)
    subscriber.subscription.request(0)

    assert(subscriber.waitForError(1000))
    assert(subscriber.hasError.get())
    assert(!subscriber.completed.get())
    assert(subscriber.error.isInstanceOf[IllegalArgumentException])
  }

  test("negative request should cause error") {
    val items = List(1, 2, 3)
    val publisher = PullPublisher(items)
    val subscriber = new MockedSubscriber[Int]()

    publisher.subscribe(subscriber)
    subscriber.subscription.request(-5)

    assert(subscriber.waitForError(1000))
    assert(subscriber.hasError.get())
    assert(!subscriber.completed.get())
    assert(subscriber.error.isInstanceOf[IllegalArgumentException])
  }

  test("cancel should stop delivery") {
    val items = List(1, 2, 3, 4, 5)
    val publisher = PullPublisher(items)
    val subscriber = new MockedSubscriber[Int]()

    publisher.subscribe(subscriber)
    subscriber.subscription.request(2)
    Thread.sleep(100)
    assertEquals(subscriber.received.size, 2)

    subscriber.subscription.cancel()
    subscriber.subscription.request(10)
    Thread.sleep(100)

    // Should not receive more items
    assertEquals(subscriber.received.size, 2)
    assert(!subscriber.completed.get())
  }

  test("publisher with error should deliver error") {
    val error = new RuntimeException("test error")
    val publisher = PullPublisher(List(1, 2, 3), error)
    val subscriber = new MockedSubscriber[Int]()

    publisher.subscribe(subscriber)

    assert(subscriber.waitForError(1000))
    assert(subscriber.hasError.get())
    assertEquals(subscriber.error, error)
    assertEquals(subscriber.received.size, 0)
  }

  test("iterator exception should be propagated") {
    val failingIterable = new Iterable[String] {
      def iterator: Iterator[String] = new Iterator[String] {
        private var count = 0
        def hasNext: Boolean = count < 3
        def next(): String = {
          count += 1
          if count == 2 then throw new RuntimeException("iterator failure")
          else s"item$count"
        }
      }
    }

    val publisher = PullPublisher(failingIterable)
    val subscriber = new MockedSubscriber[String]()

    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)

    assert(subscriber.waitForError(1000))
    assert(subscriber.hasError.get())
    assertEquals(subscriber.received, List("item1"))
    assert(subscriber.error.getMessage.contains("iterator failure"))
  }

  test("multiple subscribers should get independent iterators") {
    val items = List(1, 2, 3)
    val publisher = PullPublisher(items)
    val subscriber1 = new MockedSubscriber[Int]()
    val subscriber2 = new MockedSubscriber[Int]()

    publisher.subscribe(subscriber1)
    publisher.subscribe(subscriber2)

    subscriber1.subscription.request(10)
    subscriber2.subscription.request(10)

    assert(subscriber1.waitForCompletion(1000))
    assert(subscriber2.waitForCompletion(1000))

    assertEquals(subscriber1.received, items)
    assertEquals(subscriber2.received, items)
  }

  test("concurrent requests should work correctly") {
    val items = (1 to 100).toList
    val publisher = PullPublisher(items)
    val subscriber = new MockedSubscriber[Int]()

    publisher.subscribe(subscriber)

    // Multiple concurrent requests
    (1 to 10).foreach(_ => new Thread(() => subscriber.subscription.request(10)).start())

    assert(subscriber.waitForCompletion(2000))
    assertEquals(subscriber.received.size, 100)
    assertEquals(subscriber.received.sorted, items)
  }

  test("ofByteArray publishes correct bytes and contentLength") {
    val arr = Array[Byte](1, 2, 3, 4, 5)
    val publisher = BodyPublishersImpl.ofByteArray(arr)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr.toList)
    assertEquals(publisher.contentLength(), arr.length)
  }

  test("ofByteArray with offset/length publishes correct slice") {
    val arr = Array[Byte](10, 20, 30, 40, 50)
    val publisher = BodyPublishersImpl.ofByteArray(arr, 1, 3)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received =
      subscriber.received.flatMap(buf => buf.array().slice(buf.position(), buf.limit()))
    assertEquals(received, arr.slice(1, 4).toList)
    assertEquals(publisher.contentLength(), 3)
  }

  test("ofByteArrays publishes all arrays as ByteBuffers") {
    val arrays = List(Array[Byte](1, 2), Array[Byte](3, 4, 5))
    val publisher = BodyPublishersImpl.ofByteArrays(arrays)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arrays.flatten)
  }

  test("ofInputStream publishes bytes from InputStream") {
    val arr = Array[Byte](42, 43, 44)
    val is = new ByteArrayInputStream(arr)
    val publisher = BodyPublishersImpl.ofInputStream(() => is)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr.toList)
  }

  test("ofInputStream with null InputStream delivers error") {
    val publisher = BodyPublishersImpl.ofInputStream(() => null)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(1)
    assert(subscriber.waitForError(1000))
    assert(subscriber.hasError.get())
    assert(subscriber.error.isInstanceOf[IOException])
  }

  test("ofFile publishes file contents as ByteBuffers") {
    val tmp = Files.createTempFile("bodytest", ".bin")
    val arr = Array[Byte](99, 100, 101, 102)
    Files.write(tmp, arr)
    val publisher = BodyPublishersImpl.ofFile(tmp)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr.toList)
    assertEquals(publisher.contentLength(), arr.length)
    Files.deleteIfExists(tmp)
  }

  test("ofFile with missing file delivers error") {
    val tmp = Path.of("/tmp/nonexistent-file-" + System.nanoTime())
    val publisher = BodyPublishersImpl.ofFile(tmp)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(1)
    assert(subscriber.waitForError(1000))
    assert(subscriber.hasError.get())
  }

  test("noBody publisher completes immediately with empty buffer") {
    val publisher = BodyPublishersImpl.noBody()
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(1)
    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received.size, 1)
    assertEquals(subscriber.received.head.remaining(), 0)
    assertEquals(publisher.contentLength(), 0)
  }

  test("concat with multiple publishers delivers all in order") {
    val arr1 = Array[Byte](1, 2)
    val arr2 = Array[Byte](3, 4, 5)
    val pub1 = BodyPublishersImpl.ofByteArray(arr1)
    val pub2 = BodyPublishersImpl.ofByteArray(arr2)
    val publisher = BodyPublishersImpl.concat(pub1, pub2)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr1.toList ++ arr2.toList)
    assertEquals(publisher.contentLength(), arr1.length + arr2.length)
  }

  test("concat with no publishers is noBody") {
    val publisher = BodyPublishersImpl.concat()
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(1)
    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received.size, 1)
    assertEquals(subscriber.received.head.remaining(), 0)
    assertEquals(publisher.contentLength(), 0)
  }

  test("concat with one publisher is identity") {
    val arr = Array[Byte](7, 8, 9)
    val pub = BodyPublishersImpl.ofByteArray(arr)
    val publisher = BodyPublishersImpl.concat(pub)
    val subscriber = new MockedSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr.toList)
    assertEquals(publisher.contentLength(), arr.length)
  }

}
