package snhttp.test.jdk.net.http.internal

import java.io.{ByteArrayInputStream, IOException}
import java.net.http.HttpRequest.{BodyPublisher, BodyPublishers}
import java.net.http.HttpResponse.BodySubscribers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.List as JList
import java.util.concurrent.Flow.Subscription

import utest.{TestSuite, Tests, assert, assertThrows, test}

import _root_.snhttp.jdk.net.http.internal.{
  CurlBodyUploader,
  DelegateSeekableInputStream,
  SeekableInputStream,
}

class SeekableInputStreamTest extends TestSuite:

  private val defaultMaxCachedBytes: Long = 4 * 1024 * 1024L

  private def genBytesFrom(value: String): Array[Byte] =
    value.getBytes(StandardCharsets.UTF_8)

  private def seekableBodyFrom(publisher: BodyPublisher): SeekableInputStream =
    val subscriber = CurlBodyUploader(maxCachedBytes = 4 * 1024 * 1024)
    publisher.subscribe(subscriber)
    subscriber.getBody().toCompletableFuture().get()

  def tests = Tests:

    test("seek should rewind to cached bytes and continue from delegate") {
      val stream = DelegateSeekableInputStream(
        defaultMaxCachedBytes,
        ByteArrayInputStream(genBytesFrom("abcdef")),
      )

      assert(stream.readNBytes(3).sameElements(genBytesFrom("abc")))
      assert(stream.position() == 3L)

      assert(stream.seek(0L))
      assert(stream.position() == 0L)
      assert(stream.readNBytes(2).sameElements(genBytesFrom("ab")))

      assert(stream.seek(3L))
      assert(stream.readNBytes(3).sameElements(genBytesFrom("def")))
      assert(stream.read() == -1)
      assert(stream.size() == 6L)
    }

    test("seek should read forward and preserve skipped bytes for later rewind") {
      val stream = DelegateSeekableInputStream(
        defaultMaxCachedBytes,
        ByteArrayInputStream(genBytesFrom("abcdef")),
      )

      assert(stream.seek(4L))
      assert(stream.position() == 4L)

      assert(stream.seek(1L))
      assert(stream.readNBytes(4).sameElements(genBytesFrom("bcde")))
    }

    test("CurlBodyUploader should expose a seekable input stream") {
      val subscriber = CurlBodyUploader(defaultMaxCachedBytes)
      subscriber.onSubscribe(new Subscription:
        override def request(n: Long): Unit = ()
        override def cancel(): Unit = ())
      subscriber.onNext(ByteBuffer.wrap(genBytesFrom("abcdef")))
      subscriber.onComplete()

      val stream = subscriber.getBody().toCompletableFuture().get()
      assert(stream.isSeekable())
      assert(stream.readNBytes(3).sameElements(genBytesFrom("abc")))

      assert(stream.seek(0L))
      assert(stream.readNBytes(6).sameElements(genBytesFrom("abcdef")))
    }

    test("CurlBodyUploader should expose seekable body from BodyPublishers.ofString") {
      val stream = seekableBodyFrom(BodyPublishers.ofString("abcdef"))

      assert(stream.readNBytes(4).sameElements(genBytesFrom("abcd")))
      assert(stream.seek(0L))
      assert(stream.readNBytes(6).sameElements(genBytesFrom("abcdef")))
      assert(stream.read() == -1)
      assert(stream.size() == 6L)
    }

    test("CurlBodyUploader should expose seekable body from BodyPublishers.ofByteArray slice") {
      val source = genBytesFrom("0123456789")
      val stream = seekableBodyFrom(BodyPublishers.ofByteArray(source, 2, 5))

      assert(stream.readNBytes(3).sameElements(genBytesFrom("234")))
      assert(stream.seek(1L))
      assert(stream.readNBytes(4).sameElements(genBytesFrom("3456")))
    }

    test("DelegateSeekableInputStream should rewind across BodyPublishers.ofByteArrays chunks") {
      val stream = seekableBodyFrom(
        BodyPublishers.ofByteArrays(
          JList.of(
            genBytesFrom("ab"),
            genBytesFrom("cd"),
            genBytesFrom("ef"),
          ),
        ),
      )

      assert(stream.readNBytes(5).sameElements(genBytesFrom("abcde")))
      assert(stream.seek(2L))
      assert(stream.readNBytes(4).sameElements(genBytesFrom("cdef")))
    }

    test("DelegateSeekableInputStream should keep reading when cache is exhausted") {
      val stream = DelegateSeekableInputStream(
        maxCachedBytes = 3L,
        ByteArrayInputStream(genBytesFrom("abcdef")),
      )

      assert(stream.readNBytes(6).sameElements(genBytesFrom("abcdef")))
      assert(!stream.isSeekable())
      assert(!stream.seek(0L))
    }

    test("CurlBodyUploader should expose seekable body from BodyPublishers.ofInputStream") {
      val stream = seekableBodyFrom(
        BodyPublishers.ofInputStream(() => ByteArrayInputStream(genBytesFrom("abcdef"))),
      )

      assert(stream.readNBytes(2).sameElements(genBytesFrom("ab")))
      assert(stream.seek(0L))
      assert(stream.readNBytes(6).sameElements(genBytesFrom("abcdef")))
    }

    test("CurlBodyUploader should expose seekable empty body from BodyPublishers.noBody") {
      val stream = seekableBodyFrom(BodyPublishers.noBody())

      assert(stream.read() == -1)
      assert(stream.position() == 0L)
      assert(stream.size() == 0L)
      assert(stream.seek(0L))
      assert(stream.read() == -1)
    }

    test("DelegateSeekableInputStream should reject seek when cache is disabled") {
      val stream = DelegateSeekableInputStream(
        maxCachedBytes = 0L,
        ByteArrayInputStream(genBytesFrom("abcdef")),
      )

      assert(!stream.isSeekable())
      assert(stream.readNBytes(3).sameElements(genBytesFrom("abc")))
      assert(!stream.seek(0L))
      assert(stream.readNBytes(3).sameElements(genBytesFrom("def")))
    }

    test("DelegateSeekableInputStream should reject invalid seek positions") {
      val stream = DelegateSeekableInputStream(
        defaultMaxCachedBytes,
        ByteArrayInputStream(genBytesFrom("abcdef")),
      )

      assert(!stream.seek(-1L))
      assert(stream.position() == 0L)
    }

    test("DelegateSeekableInputStream should reject reads after upstream close") {
      val stream = DelegateSeekableInputStream(
        defaultMaxCachedBytes,
        ByteArrayInputStream(genBytesFrom("abcdef")),
      )
      stream.close()
      val _ = assertThrows[IOException] {
        val _ = stream.read()
      }
      assert(!stream.seek(0L))
    }
