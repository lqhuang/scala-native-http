package snhttp.test.java.net.http

import java.io.{ByteArrayInputStream, IOException, UncheckedIOException, InputStream}
import java.net.http.HttpRequest.{BodyPublisher, BodyPublishers}
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.concurrent.Flow
import java.util.List as JList

import utest.{TestSuite, Tests, assert, assertThrows, test}

import _root_.snhttp.test.jdk.net.http.MockSubscriber

class BodyPublishersTest extends TestSuite:

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private class TrackingInputStream(bytes: Array[Byte]) extends ByteArrayInputStream(bytes):
    @volatile var closed = false

    override def close(): Unit = synchronized {
      closed = true
      super.close()
    }
  end TrackingInputStream

  /** Flatten a sequence of ByteBuffers into a plain byte array without consuming the originals. */
  private def flatten(buffers: Iterable[ByteBuffer]): Array[Byte] =
    buffers.iterator.flatMap { buffer =>
      val dup = buffer.duplicate()
      val bytes = new Array[Byte](dup.remaining())
      dup.get(bytes)
      bytes
    }.toArray

  // ---------------------------------------------------------------------------

  def tests = Tests:

    // =========================================================================
    // noBody
    // =========================================================================

    test("noBody - completes after positive demand with no items") {
      val publisher = BodyPublishers.noBody()
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitComplete()
      assert(subscriber.received.isEmpty)
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
    }

    test("noBody - contentLength is 0") {
      assert(BodyPublishers.noBody().contentLength() == 0L)
    }

    test("noBody - can be subscribed multiple times independently") {
      val publisher = BodyPublishers.noBody()
      val s1 = MockSubscriber[ByteBuffer]()
      val s2 = MockSubscriber[ByteBuffer]()

      publisher.subscribe(s1)
      s1.sub.request(1)
      publisher.subscribe(s2)
      s2.sub.request(1)

      s1.awaitComplete()
      s2.awaitComplete()
      assert(s1.completes == 1 && s1.received.isEmpty)
      assert(s2.completes == 1 && s2.received.isEmpty)
    }

    // =========================================================================
    // ofString(String) – default UTF-8
    // =========================================================================

    test("ofString - default charset encodes as UTF-8") {
      val content = "Hello, World!"
      val expected = content.getBytes(StandardCharsets.UTF_8)
      val publisher = BodyPublishers.ofString(content)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(expected))
      assert(publisher.contentLength() == expected.length.toLong)
    }

    test("ofString - empty string has zero contentLength") {
      val publisher = BodyPublishers.ofString("")
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitComplete()
      assert(publisher.contentLength() == 0L)
      assert(flatten(subscriber.received).isEmpty)
    }

    test("ofString - multi-byte UTF-8 characters are encoded correctly") {
      val content = "\u3053\u3093\u306b\u3061\u306f" // "こんにちは", 3 bytes/char in UTF-8
      val expected = content.getBytes(StandardCharsets.UTF_8)
      val publisher = BodyPublishers.ofString(content)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(expected))
      assert(publisher.contentLength() == expected.length.toLong)
    }

    // =========================================================================
    // ofString(String, Charset)
    // =========================================================================

    test("ofString - UTF-16 charset publishes correct bytes and contentLength") {
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

    test("ofString - ISO-8859-1 charset encodes Latin-1 correctly") {
      val content = "caf\u00e9" // "café"
      val expected = content.getBytes(StandardCharsets.ISO_8859_1)
      val publisher = BodyPublishers.ofString(content, StandardCharsets.ISO_8859_1)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(expected))
      assert(publisher.contentLength() == expected.length.toLong)
    }

    test("ofString - empty string with explicit charset has zero contentLength") {
      val publisher = BodyPublishers.ofString("", StandardCharsets.UTF_16)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitComplete()
      assert(publisher.contentLength() == 0L)
    }

    // =========================================================================
    // ofByteArray(byte[])
    // =========================================================================

    test("ofByteArray - publishes correct bytes and contentLength") {
      val data = Array[Byte](1, 2, 3, 4, 5)
      val publisher = BodyPublishers.ofByteArray(data)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(data))
      assert(publisher.contentLength() == data.length.toLong)
    }

    test("ofByteArray - empty array has zero contentLength and no items") {
      val publisher = BodyPublishers.ofByteArray(Array.empty[Byte])
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitComplete()
      assert(publisher.contentLength() == 0L)
    }

    // =========================================================================
    // ofByteArray(byte[], int offset, int length)
    // =========================================================================

    test("ofByteArray - offset and length selects correct sub-range") {
      val data = Array[Byte](10, 20, 30, 40, 50)
      val publisher = BodyPublishers.ofByteArray(data, 1, 3) // expects [20, 30, 40]
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(Array[Byte](20, 30, 40)))
      assert(publisher.contentLength() == 3L)
    }

    test("ofByteArray - offset=0 and full length covers entire array") {
      val data = Array[Byte](7, 8, 9)
      val publisher = BodyPublishers.ofByteArray(data, 0, data.length)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(data))
      assert(publisher.contentLength() == data.length.toLong)
    }

    test("ofByteArray - zero-length sub-range reports zero contentLength") {
      val publisher = BodyPublishers.ofByteArray(Array[Byte](1, 2, 3), 1, 0)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitComplete()
      assert(publisher.contentLength() == 0L)
    }

    test("ofByteArray - out-of-bounds range throws IndexOutOfBoundsException") {
      val data = Array[Byte](1, 2, 3)
      assertThrows[IndexOutOfBoundsException] {
        BodyPublishers.ofByteArray(data, 0, 10): Unit // length > array size
      }: Unit
      assertThrows[IndexOutOfBoundsException] {
        BodyPublishers.ofByteArray(data, -1, 2): Unit // negative offset
      }: Unit
      assertThrows[IndexOutOfBoundsException] {
        BodyPublishers.ofByteArray(data, 2, 2): Unit // offset + length > array size
      }: Unit
    }

    // =========================================================================
    // ofByteArrays(Iterable<byte[]>)
    // =========================================================================

    test("ofByteArrays - multiple arrays are published in order") {
      val a1 = Array[Byte](1, 2)
      val a2 = Array[Byte](3, 4, 5)
      val publisher = BodyPublishers.ofByteArrays(JList.of(a1, a2))
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(a1 ++ a2))
    }

    test("ofByteArrays - empty iterable completes immediately with no items") {
      val publisher = BodyPublishers.ofByteArrays(JList.of[Array[Byte]]())
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitComplete()
      assert(subscriber.received.isEmpty)
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
    }

    test("ofByteArrays - single array publishes all bytes") {
      val data = Array[Byte](42, 43, 44)
      val publisher = BodyPublishers.ofByteArrays(JList.of(data))
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(data))
    }

    // =========================================================================
    // ofInputStream(Supplier<InputStream>)
    // =========================================================================

    test("ofInputStream - publishes bytes and closes the stream") {
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

    test("ofInputStream - null InputStream delivered as IOException error") {
      val publisher = BodyPublishers.ofInputStream(() => null)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(1)

      subscriber.awaitError()
      assert(subscriber.completes == 0)
      assert(subscriber.errors == 1)
      assert(subscriber.lastError.isInstanceOf[IOException])
    }

    test("ofInputStream - contentLength is unknown (-1)") {
      val publisher =
        BodyPublishers.ofInputStream(() => new ByteArrayInputStream(Array[Byte](1, 2, 3)))
      assert(publisher.contentLength() == -1L)
    }

    test("ofInputStream - empty stream completes with no items") {
      val publisher =
        BodyPublishers.ofInputStream(() => new ByteArrayInputStream(Array.empty[Byte]))
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).isEmpty)
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
    }

    test("ofInputStream - IOException during read is delivered as onError") {
      val brokenStream = new InputStream {
        override def read(): Int =
          throw new IOException("simulated read failure")
        override def read(b: Array[Byte], off: Int, len: Int): Int =
          throw new IOException("simulated read failure")
      }
      val publisher = BodyPublishers.ofInputStream(() => brokenStream)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitError()
      assert(subscriber.completes == 0)
      assert(subscriber.errors == 1)
      assert(subscriber.lastError.isInstanceOf[UncheckedIOException])
      assert(subscriber.lastError.getCause().isInstanceOf[IOException])
    }

    // =========================================================================
    // ofFile(Path)
    // =========================================================================

    test("ofFile - publishes file content with correct contentLength") {
      val data = Array[Byte](10, 20, 30, 40, 50)
      val tmp = Files.createTempFile("body-publisher-test", ".bin")
      try {
        Files.write(tmp, data)
        val publisher = BodyPublishers.ofFile(tmp)
        val subscriber = MockSubscriber[ByteBuffer]()

        publisher.subscribe(subscriber)
        subscriber.sub.request(10)

        subscriber.awaitComplete()
        assert(flatten(subscriber.received).sameElements(data))
        assert(publisher.contentLength() == data.length.toLong)
      } //
      finally //
        Files.deleteIfExists(tmp): Unit
    }

    test("ofFile - empty file has zero contentLength") {
      val tmp = Files.createTempFile("body-publisher-empty", ".bin")
      try {
        val publisher = BodyPublishers.ofFile(tmp)
        val subscriber = MockSubscriber[ByteBuffer]()

        publisher.subscribe(subscriber)
        subscriber.sub.request(10)

        subscriber.awaitComplete()
        assert(publisher.contentLength() == 0L)
        assert(flatten(subscriber.received).isEmpty)
      } //
      finally //
        Files.deleteIfExists(tmp): Unit
    }

    test("ofFile - non-existent path throws FileNotFoundException") {
      assertThrows[java.io.FileNotFoundException] {
        BodyPublishers.ofFile(Path.of("/nonexistent/path/body-test-file.bin")): Unit
      }
    }

    // =========================================================================
    // fromPublisher(Flow.Publisher<ByteBuffer>)
    // =========================================================================

    test("fromPublisher - contentLength is unknown (-1)") {
      val publisher = BodyPublishers.fromPublisher(
        BodyPublishers.ofByteArray(Array[Byte](1, 2, 3)),
      )
      assert(publisher.contentLength() == -1L)
    }

    test("fromPublisher - forwards data from underlying publisher") {
      val data = Array[Byte](7, 8, 9)
      val publisher = BodyPublishers.fromPublisher(BodyPublishers.ofByteArray(data))
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(data))
    }

    // =========================================================================
    // fromPublisher(Flow.Publisher<ByteBuffer>, long contentLength)
    // =========================================================================

    test("fromPublisher with contentLength - reports given contentLength") {
      val publisher = BodyPublishers.fromPublisher(
        BodyPublishers.ofByteArray(Array[Byte](1, 2, 3)),
        3L,
      )
      assert(publisher.contentLength() == 3L)
    }

    test("fromPublisher with contentLength - forwards data correctly") {
      val data = Array[Byte](1, 2, 3)
      val publisher = BodyPublishers.fromPublisher(BodyPublishers.ofByteArray(data), 3L)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(data))
    }

    test("fromPublisher with contentLength - throws IllegalArgumentException for zero") {
      assertThrows[IllegalArgumentException] {
        BodyPublishers.fromPublisher(BodyPublishers.ofByteArray(Array.empty[Byte]), 0L): Unit
      }
    }

    test("fromPublisher with contentLength - throws IllegalArgumentException for negative") {
      assertThrows[IllegalArgumentException] {
        BodyPublishers.fromPublisher(
          BodyPublishers.ofByteArray(Array.empty[Byte]),
          -1L,
        ): Unit
      }
    }

    // =========================================================================
    // concat(BodyPublisher...)
    // =========================================================================

    test("concat - multiple publishers deliver all data in order") {
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

    test("concat - multiple publishers with split demand deliver all data in order") {
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
      Thread.sleep(100)
      subscriber.sub.request(5)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(p1 ++ p2 ++ p3))
      assert(publisher.contentLength() == seq.map(_.length).sum.toLong)
    }

    test("concat - empty sequence completes immediately") {
      val ps = new Array[BodyPublisher](0)
      val publisher = BodyPublishers.concat(ps: _*)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(5)

      subscriber.awaitComplete()
      assert(subscriber.received.isEmpty)
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
      assert(publisher.contentLength() == 0L)
    }

    test("concat - single publisher delivers correct content and contentLength") {
      val data = Array[Byte](1, 2, 3)
      val publisher = BodyPublishers.concat(BodyPublishers.ofByteArray(data))
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(flatten(subscriber.received).sameElements(data))
      assert(publisher.contentLength() == data.length.toLong)
    }

    test("concat - contentLength is unknown (-1) when any inner publisher is unknown") {
      val known = BodyPublishers.ofByteArray(Array[Byte](1, 2, 3))
      // ofInputStream always reports unknown length
      val unknown = BodyPublishers.ofInputStream(() => new ByteArrayInputStream(Array[Byte](4, 5)))
      val publisher = BodyPublishers.concat(known, unknown)
      assert(publisher.contentLength() == -1L)
    }

    test("concat - contentLength is unknown (-1) when sum overflows Long.MaxValue") {
      // Two publishers each claiming Long.MaxValue: their sum overflows → unknown
      val huge1 =
        BodyPublishers.fromPublisher(BodyPublishers.ofByteArray(Array.empty), Long.MaxValue)
      val huge2 =
        BodyPublishers.fromPublisher(BodyPublishers.ofByteArray(Array.empty), Long.MaxValue)
      val publisher = BodyPublishers.concat(huge1, huge2)
      assert(publisher.contentLength() == -1L)
    }

    test("concat - error from inner publisher is propagated as onError") {
      val brokenStream = new InputStream {
        override def read(): Int = throw new IOException("inner failure")
        override def read(b: Array[Byte], off: Int, len: Int): Int =
          throw new IOException("inner failure")
      }
      val good = BodyPublishers.ofByteArray(Array[Byte](1, 2))
      val broken = BodyPublishers.ofInputStream(() => brokenStream)
      val publisher = BodyPublishers.concat(good, broken)
      val subscriber = MockSubscriber[ByteBuffer]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(20)

      subscriber.awaitError()
      assert(subscriber.completes == 0)
      assert(subscriber.errors == 1)
    }
