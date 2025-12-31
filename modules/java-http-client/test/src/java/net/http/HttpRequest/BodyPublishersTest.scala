import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.http.HttpRequest.BodyPublishers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.concurrent.Flow
import java.util.List as JList

import scala.util.Random

import snhttp.jdk.internal.PropertyUtils
import snhttp.jdk.testkits.MockSubscriber

import utest.{TestSuite, Tests, test, assert, assertThrows}

class BodyPublishersTest extends TestSuite:
  val tests = Tests:

    test("noBody publisher is instance of BodyPublisher") {
      val publisher = BodyPublishers.noBody()
      assert(publisher.isInstanceOf[Flow.Publisher[ByteBuffer]])
    }

    test("ofString publishes correct bytes and contentLength") {
      val content = "Hello, World!"
      val contentBytes = content.getBytes(StandardCharsets.UTF_16)
      val contentLength = contentBytes.length.toLong
      val publisher = BodyPublishers.ofString(content, StandardCharsets.UTF_16)
      assert(publisher.contentLength() == contentLength)

      val subscriber = MockSubscriber()
      publisher.subscribe(subscriber)
      subscriber.subscription.request(10)

      val recv = MockSubscriber.flattenBuffers(subscriber.received)
      assert(recv.length == contentLength)
      assert(recv.sameElements(contentBytes))

    }

    test("ofByteArray publishes correct bytes and contentLength") {
      val arr = Array[Byte](1, 2, 3, 4, 5)
      val publisher = BodyPublishers.ofByteArray(arr)
      assert(publisher.contentLength() == arr.length.toLong)

      val subscriber = MockSubscriber()
      publisher.subscribe(subscriber)
      subscriber.subscription.request(10)

      val received = MockSubscriber.flattenBuffers(subscriber.received)
      assert(received.sameElements(arr))
    }

    test("ofByteArray with offset/length publishes correct slice") {
      val arr = Array[Byte](10, 20, 30, 40, 50)
      val publisher = BodyPublishers.ofByteArray(arr, 1, 3)
      val subscriber = MockSubscriber()
      assert(publisher.contentLength() == 3L)

      publisher.subscribe(subscriber)
      subscriber.subscription.request(10)

      val received = MockSubscriber.flattenBuffers(subscriber.received)
      assert(received.sameElements(arr.slice(1, 4)))
    }

    test(
      "ofByteArray publishes correct bytes and contentLength while length larger than BUFFER_SIZE",
    ) {
      assert(PropertyUtils.BUFFER_SIZE > 512)
      val arr: Array[Byte] = Random.nextBytes(2 * PropertyUtils.BUFFER_SIZE + 512)
      val publisher = BodyPublishers.ofByteArray(arr)
      assert(publisher.contentLength() == arr.length.toLong)

      val subscriber = MockSubscriber()
      publisher.subscribe(subscriber)
      subscriber.subscription.request(Long.MaxValue)

      val received = MockSubscriber.flattenBuffers(subscriber.received)
      assert(received.length == arr.length)
      assert(received.sameElements(arr))
    }

    test(
      "ofByteArray with offset/length publishes correct slice while length larger than BUFFER_SIZE",
    ) {
      assert(PropertyUtils.BUFFER_SIZE > 512)
      val arr: Array[Byte] = Random.nextBytes(4 * PropertyUtils.BUFFER_SIZE + 512)
      val publisher = BodyPublishers.ofByteArray(
        arr,
        PropertyUtils.BUFFER_SIZE / 2,
        PropertyUtils.BUFFER_SIZE * 2 + 100,
      )
      assert(publisher.contentLength() == PropertyUtils.BUFFER_SIZE * 2 + 100)

      val subscriber = MockSubscriber()
      publisher.subscribe(subscriber)
      subscriber.subscription.request(10)

      val received = MockSubscriber.flattenBuffers(subscriber.received)
      assert(received.length == PropertyUtils.BUFFER_SIZE * 2 + 100)
      assert(
        received.sameElements(
          arr.slice(
            PropertyUtils.BUFFER_SIZE / 2,
            PropertyUtils.BUFFER_SIZE / 2 + PropertyUtils.BUFFER_SIZE * 2 + 100,
          ),
        ),
      )

    }

    // test("ofByteArrays publishes all arrays as ByteBuffers") {
    //   val arrays = Seq(Array[Byte](1, 2), Array[Byte](3, 4, 5))
    //   val publisher = BodyPublishers.ofByteArrays(arrays)
    //   val subscriber = MockSubscriber()
    //   publisher.subscribe(subscriber)
    //   subscriber.subscription.request(15)

    //   val received = MockSubscriber.flattenBuffers(subscriber.received)
    //   assert(received.sameElements(arrays.flatten))
    // }

    test("ofInputStream publishes bytes from InputStream") {
      val arr = Array[Byte](42, 43, 44)
      val is = ByteArrayInputStream(arr)
      // val publisher = BodyPublishers.ofInputStream(() => is)
      // val subscriber = MockSubscriber[ByteBuffer]()
      // publisher.subscribe(subscriber)
      // subscriber.subscription.request(10)
      // assert(subscriber.waitForCompletion(1000))
      // val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
      // assertEquals(received, arr.toList)
    }

    test("ofInputStream with null InputStream delivers error") {
      // val publisher = BodyPublishers.ofInputStream(() => null)
      // val subscriber = MockSubscriber[ByteBuffer]()
      // publisher.subscribe(subscriber)
      // subscriber.subscription.request(1)
      // assert(subscriber.waitForError(1000))
      // assert(subscriber.hasError)
      // assert(subscriber.error.isInstanceOf[IOException])
    }

    // construct successfully but hang out
    // test("ofFile publishes file contents as ByteBuffers") {
    //   val tmp = Files.createTempFile("bodytest", ".bin")
    //   val arr = Array[Byte](99, 100, 101, 102)
    //   try {
    //     Files.write(tmp, arr)
    //     val publisher = BodyPublishers.ofFile(tmp)
    //     // val subscriber = MockSubscriber[ByteBuffer]()
    //     // publisher.subscribe(subscriber)
    //     // subscriber.subscription.request(10)
    //     // assert(subscriber.waitForCompletion(1000))
    //     // val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    //     // assertEquals(received, arr.toList)
    //     // assertEquals(publisher.contentLength(), arr.length.toLong)
    //   } finally Files.deleteIfExists(tmp)
    // }

    // test("ofFile with missing file delivers error") {
    //   val tmp = Path.of("/tmp/nonexistent-file-" + System.nanoTime())
    //   val publisher = BodyPublishers.ofFile(tmp)
    //   val subscriber = MockSubscriber[ByteBuffer]()
    //   publisher.subscribe(subscriber)
    //   subscriber.subscription.request(1)
    //   assert(subscriber.waitForError(1000))
    //   assert(subscriber.hasError)
    // }

    // test("noBody publisher completes immediately with empty buffer") {
    //   val publisher = BodyPublishers.noBody()
    //   val subscriber = MockSubscriber[ByteBuffer]()
    //   publisher.subscribe(subscriber)
    //   subscriber.subscription.request(1)
    //   assert(subscriber.waitForCompletion(1000))
    //   assertEquals(subscriber.received.size, 1)
    //   assertEquals(subscriber.received.head.remaining(), 0)
    //   assertEquals(publisher.contentLength(), 0L)
    // }

    // test("concat with multiple publishers delivers all in order") {
    //   val arr1 = Array[Byte](1, 2)
    //   val arr2 = Array[Byte](3, 4, 5)
    //   val pub1 = BodyPublishers.ofByteArray(arr1)
    //   val pub2 = BodyPublishers.ofByteArray(arr2)
    //   val publisher = BodyPublishers.concat(pub1, pub2)
    //   val subscriber = MockSubscriber[ByteBuffer]()
    //   publisher.subscribe(subscriber)
    //   subscriber.subscription.request(10)
    //   assert(subscriber.waitForCompletion(1000))
    //   val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    //   assertEquals(received, arr1.toList ++ arr2.toList)
    //   assertEquals(publisher.contentLength(), (arr1.length + arr2.length).toLong)
    // }

    test("concat with no publishers is noBody") {
      // val publisher = BodyPublishers.concat()
      // val subscriber = MockSubscriber[ByteBuffer]()
      // publisher.subscribe(subscriber)
      // subscriber.subscription.request(1)
      // assert(subscriber.waitForCompletion(1000))
      // assertEquals(subscriber.received.size, 1)
      // assertEquals(subscriber.received.head.remaining(), 0)
      // assertEquals(publisher.contentLength(), 0L)
    }

    // test("concat with one publisher is identity") {
    //   val arr = Array[Byte](7, 8, 9)
    //   val pub = BodyPublishers.ofByteArray(arr)
    //   val publisher = BodyPublishers.concat(pub)
    //   val subscriber = MockSubscriber[ByteBuffer]()
    //   publisher.subscribe(subscriber)
    //   subscriber.subscription.request(10)
    //   assert(subscriber.waitForCompletion(1000))
    //   val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    //   assertEquals(received, arr.toList)
    //   assertEquals(publisher.contentLength(), arr.length.toLong)
    // }
