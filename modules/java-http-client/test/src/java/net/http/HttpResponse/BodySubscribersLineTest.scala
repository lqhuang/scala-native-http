package snhttp.test.java.net.http

import java.net.http.HttpResponse.BodySubscribers
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets.UTF_8
import java.util.List as JList
import java.util.concurrent.TimeUnit
import java.util.function.Function

import utest.{TestSuite, Tests, test, assert}

import _root_.snhttp.test.jdk.net.http.{MockSubscriber, MockSubscription}

class BodySubscribersLineTest extends TestSuite:

  private def checkLines(
      events: Seq[Seq[Array[Byte]]],
      separator: String,
      expected: Seq[String],
  ): Unit = {
    val downstream = MockSubscriber[String](request = true)
    val finisher: Function[MockSubscriber[String], Int] = _.received.size
    val subscriber = BodySubscribers.fromLineSubscriber(downstream, finisher, UTF_8, separator)

    subscriber.onSubscribe(MockSubscription())
    events.foreach { chunks =>
      val buffers = new java.util.ArrayList[ByteBuffer]()
      chunks.foreach(chunk => (buffers.add(ByteBuffer.wrap(chunk)): Unit))
      subscriber.onNext(buffers)
    }
    subscriber.onComplete()

    val count = subscriber.getBody().toCompletableFuture().get(5, TimeUnit.SECONDS)
    assert(count == expected.size)
    assert(downstream.received.toSeq == expected)
    assert(downstream.completes == 1)
    assert(downstream.errors == 0)
  }

  val tests = Tests:

    test("UTF-8 characters split between buffers in one onNext") {
      // Exercise every split, including inside two-, three-, and four-byte characters.
      val text = "a\u00a2\u20ac\ud83d\ude00z\nlast"
      val bytes = text.getBytes(UTF_8)
      for split <- 1 until bytes.length do
        checkLines(
          Seq(Seq(bytes.take(split), bytes.drop(split))),
          "\n",
          Seq("a\u00a2\u20ac\ud83d\ude00z", "last"),
        )
    }

    test("UTF-8 characters split between onNext calls") {
      val text = "a\u00a2\u20ac\ud83d\ude00z\nlast"
      val bytes = text.getBytes(UTF_8)
      for split <- 1 until bytes.length do
        checkLines(
          Seq(Seq(bytes.take(split)), Seq(Array.emptyByteArray), Seq(bytes.drop(split))),
          "\n",
          Seq("a\u00a2\u20ac\ud83d\ude00z", "last"),
        )
    }

    test("UTF-8 characters delivered one byte at a time") {
      val text = "\u00a2\u20ac\ud83d\ude00\n"
      checkLines(
        text.getBytes(UTF_8).toSeq.map(byte => Seq(Array(byte))),
        "\n",
        Seq("\u00a2\u20ac\ud83d\ude00"),
      )
    }

    test("null separator recognizes CR LF and CRLF") {
      checkLines(
        Seq(Seq("first\rsecond\nthird\r\n\nlast\r".getBytes(UTF_8))),
        null,
        Seq("first", "second", "third", "", "last"),
      )
    }

    test("one-argument overload recognizes CR LF and CRLF") {
      val downstream = MockSubscriber[String](request = true)
      val subscriber = BodySubscribers.fromLineSubscriber(downstream)
      subscriber.onSubscribe(MockSubscription())
      subscriber.onNext(JList.of(ByteBuffer.wrap("a\rb\nc\r\nd".getBytes(UTF_8))))
      subscriber.onComplete()

      assert(subscriber.getBody().toCompletableFuture().get(5, TimeUnit.SECONDS) == null)
      assert(downstream.received.toSeq == Seq("a", "b", "c", "d"))
      assert(downstream.completes == 1)
      assert(downstream.errors == 0)
    }

    test("default CRLF can span events without creating an extra line") {
      checkLines(
        Seq("first\r", "", "\nsecond\r", "third\n", "tail").map(s => Seq(s.getBytes(UTF_8))),
        null,
        Seq("first", "second", "third", "tail"),
      )
    }

    test("explicit LF separator preserves carriage returns") {
      checkLines(
        Seq("a\rb\nc\r", "\ntail").map(s => Seq(s.getBytes(UTF_8))),
        "\n",
        Seq("a\rb", "c\r", "tail"),
      )
    }

    test("incomplete UTF-8 sequence is replaced at end of input") {
      checkLines(
        Seq(Seq(Array(0xe2.toByte)), Seq(Array(0x82.toByte))),
        "\n",
        Seq("\ufffd"),
      )
    }

end BodySubscribersLineTest
