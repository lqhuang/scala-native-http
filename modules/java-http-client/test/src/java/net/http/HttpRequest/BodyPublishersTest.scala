package snhttp.test.java.net.http

import java.io.ByteArrayInputStream
import java.net.http.HttpRequest.BodyPublishers
import java.nio.ByteBuffer
import java.io.IOException
import java.nio.charset.StandardCharsets

import _root_.snhttp.test.jdk.net.http.MockSubscriber

import utest.{TestSuite, Tests, assert, test}
import java.net.http.HttpRequest.BodyPublisher

class BodyPublishersTest extends TestSuite:

  private class TrackingInputStream(bytes: Array[Byte]) extends ByteArrayInputStream(bytes):
    @volatile var closed = false

    override def close(): Unit = synchronized {
      closed = true
      super.close()
    }
  end TrackingInputStream

  private def flatten(buffers: Iterable[ByteBuffer]): Array[Byte] =
    buffers.iterator.flatMap { buffer =>
      val duplicate = buffer.duplicate()
      val bytes = new Array[Byte](duplicate.remaining())
      duplicate.get(bytes)
      bytes
    }.toArray

  def tests = Tests:

    test("noBody completes after positive demand") {
      val publisher = BodyPublishers.noBody()
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitComplete()
      assert(subscriber.received.isEmpty)
      assert(publisher.contentLength() == 0L)
    }

    test("ofString publishes correct bytes and contentLength") {
      val content = "Hello, World!"
      val expected = content.getBytes(StandardCharsets.UTF_16)
      val publisher = BodyPublishers.ofString(content, StandardCharsets.UTF_16)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(expected))
      assert(publisher.contentLength() == expected.length.toLong)
    }

    test("ofInputStream publishes bytes and closes the stream") {
      val expected = Array[Byte](42, 43, 44)
      val stream = TrackingInputStream(expected)
      val publisher = BodyPublishers.ofInputStream(() => stream)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(expected))
      assert(stream.closed)
    }

    test("ofInputStream with null InputStream delivers error") {
      val publisher = BodyPublishers.ofInputStream(() => null)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitError()
      assert(subscriber.completes == 0)
      assert(subscriber.errors == 1)
      assert(subscriber.lastError.isInstanceOf[IOException])
    }

    test("ofInputStream with unknown content length") {
      // TODO
    }

    test("concat with multiple publishers delivers all data in order") {
      val p1 = Array[Byte](1, 2)
      val p2 = Array[Byte](3, 4, 5, 6, 7)
      val p3 = Array[Byte](8, 9, 10)
      val seq = Seq(p1, p2, p3)
      val publisher = BodyPublishers.concat(
        seq.map(bytes => BodyPublishers.ofByteArray(bytes)): _*,
      )
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(p1 ++ p2 ++ p3))
      assert(publisher.contentLength() == seq.map(_.length).sum.toLong)
    }

    test("concat with multiple publishers delivers all data in order - split requests") {
      val p1 = Array[Byte](1, 2)
      val p2 = Array[Byte](3, 4, 5, 6, 7)
      val p3 = Array[Byte](8, 9, 10)
      val seq = Seq(p1, p2, p3)
      val publisher = BodyPublishers.concat(
        seq.map(bytes => BodyPublishers.ofByteArray(bytes)): _*,
      )
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(5)
      Thread.sleep(100) // wait for some data to be received
      subscriber.sub.request(5)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(p1 ++ p2 ++ p3))
      assert(publisher.contentLength() == seq.map(_.length).sum.toLong)
    }

    test("concat works with empty publishers") {
      val publisher = BodyPublishers.concat(Seq.empty: _*)
      val subscriber = MockSubscriber[ByteBuffer]()
      publisher.subscribe(subscriber)

      subscriber.sub.request(5)

      subscriber.awaitComplete()
      assert(subscriber.received.isEmpty)
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
      assert(publisher.contentLength() == 0)
    }
