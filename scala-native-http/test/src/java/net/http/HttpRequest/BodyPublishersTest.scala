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

import snhttp.jdk.PullPublisher
import snhttp.testkits.MockSubscriber

class BodyPublishersTest extends munit.FunSuite {

  test("ofByteArray publishes correct bytes and contentLength") {
    val arr = Array[Byte](1, 2, 3, 4, 5)
    val publisher = BodyPublishers.ofByteArray(arr)
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr.toList)
    assertEquals(publisher.contentLength(), arr.length.toLong)
  }

  test("ofByteArray with offset/length publishes correct slice") {
    val arr = Array[Byte](10, 20, 30, 40, 50)
    val publisher = BodyPublishers.ofByteArray(arr, 1, 3)
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received =
      subscriber.received.flatMap(buf => buf.array().slice(buf.position(), buf.limit()))
    assertEquals(received, arr.slice(1, 4).toList)
    assertEquals(publisher.contentLength(), 3L)
  }

  // test("ofByteArrays publishes all arrays as ByteBuffers") {
  //   val arrays = Seq(Array[Byte](1, 2), Array[Byte](3, 4, 5))
  //   val publisher = BodyPublishers.ofByteArrays(arrays)
  //   val subscriber = new MockSubscriber[ByteBuffer]()
  //   publisher.subscribe(subscriber)
  //   subscriber.subscription.request(10)
  //   assert(subscriber.waitForCompletion(1000))
  //   val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
  //   assertEquals(received, arrays.flatten)
  // }

  test("ofInputStream publishes bytes from InputStream") {
    val arr = Array[Byte](42, 43, 44)
    val is = new ByteArrayInputStream(arr)
    val publisher = BodyPublishers.ofInputStream(() => is)
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr.toList)
  }

  test("ofInputStream with null InputStream delivers error") {
    val publisher = BodyPublishers.ofInputStream(() => null)
    val subscriber = new MockSubscriber[ByteBuffer]()
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
    val publisher = BodyPublishers.ofFile(tmp)
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr.toList)
    assertEquals(publisher.contentLength(), arr.length.toLong)
    Files.deleteIfExists(tmp)
  }

  test("ofFile with missing file delivers error") {
    val tmp = Path.of("/tmp/nonexistent-file-" + System.nanoTime())
    val publisher = BodyPublishers.ofFile(tmp)
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(1)
    assert(subscriber.waitForError(1000))
    assert(subscriber.hasError.get())
  }

  test("noBody publisher completes immediately with empty buffer") {
    val publisher = BodyPublishers.noBody()
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(1)
    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received.size, 1)
    assertEquals(subscriber.received.head.remaining(), 0)
    assertEquals(publisher.contentLength(), 0L)
  }

  test("concat with multiple publishers delivers all in order") {
    val arr1 = Array[Byte](1, 2)
    val arr2 = Array[Byte](3, 4, 5)
    val pub1 = BodyPublishers.ofByteArray(arr1)
    val pub2 = BodyPublishers.ofByteArray(arr2)
    val publisher = BodyPublishers.concat(pub1, pub2)
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr1.toList ++ arr2.toList)
    assertEquals(publisher.contentLength(), (arr1.length + arr2.length).toLong)
  }

  test("concat with no publishers is noBody") {
    val publisher = BodyPublishers.concat()
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(1)
    assert(subscriber.waitForCompletion(1000))
    assertEquals(subscriber.received.size, 1)
    assertEquals(subscriber.received.head.remaining(), 0)
    assertEquals(publisher.contentLength(), 0L)
  }

  test("concat with one publisher is identity") {
    val arr = Array[Byte](7, 8, 9)
    val pub = BodyPublishers.ofByteArray(arr)
    val publisher = BodyPublishers.concat(pub)
    val subscriber = new MockSubscriber[ByteBuffer]()
    publisher.subscribe(subscriber)
    subscriber.subscription.request(10)
    assert(subscriber.waitForCompletion(1000))
    val received = subscriber.received.flatMap(buf => buf.array().take(buf.limit()))
    assertEquals(received, arr.toList)
    assertEquals(publisher.contentLength(), arr.length.toLong)
  }

}
