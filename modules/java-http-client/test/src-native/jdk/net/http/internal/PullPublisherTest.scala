package snhttp.test.jdk.net.http.internal

import java.util.concurrent.ThreadLocalRandom
import scala.concurrent.duration.DurationInt

import utest.{Tests, TestSuite, assert, test}

import _root_.snhttp.jdk.net.http.internal.PullPublisher
import _root_.snhttp.test.jdk.net.http.MockSubscriber

class PullPublisherTest extends TestSuite:

  def tests = Tests:

    test("Construction with empty iterable should not throw and complete immediately") {
      val publisher = PullPublisher(Iterator.empty[String])
      val subscriber = MockSubscriber[String]()
      publisher.subscribe(subscriber)

      subscriber.sub.request(1)
      subscriber.awaitComplete()
      assert(subscriber.received.isEmpty)
      assert(subscriber.nexts == 0)
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
    }

    test("request more than available should deliver all and complete") {
      val items = List(1, 2, 3)
      val publisher = PullPublisher(items.iterator)
      val subscriber = new MockSubscriber[Int]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(100)

      subscriber.awaitComplete()
      assert(subscriber.received.sameElements(items))
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
    }

    test("subscriptions reuse the shared worker pool") {
      def consumeOne(): Unit = {
        val publisher = PullPublisher(Iterator.single(1))
        val subscriber = MockSubscriber[Int]()
        publisher.subscribe(subscriber)
        subscriber.sub.request(1)
        subscriber.awaitComplete()
        assert(subscriber.received.sameElements(Seq(1)))
      }

      consumeOne() // initialize the shared executor before measuring
      val initialThreadCount = Thread.activeCount()

      (1 to 40).foreach(_ => consumeOne())

      val threadGrowth = Thread.activeCount() - initialThreadCount
      assert(threadGrowth < 8)
    }

    test("publishes staged demand in order and completes once exhausted") {
      val items = Seq(1, 2, 3, 4, 5)
      val publisher = PullPublisher(items.iterator)
      val subscriber = MockSubscriber[Int]()
      publisher.subscribe(subscriber)

      subscriber.sub.request(2)
      Thread.sleep(50L)
      assert(subscriber.received.sameElements(Seq(1, 2)))
      assert(subscriber.completes == 0)
      assert(subscriber.errors == 0)

      subscriber.sub.request(3)
      subscriber.awaitComplete()
      assert(subscriber.received.sameElements(items))
      assert(subscriber.errors == 0)
      assert(subscriber.completes == 1)
    }

    test("backpressure should work with demand") {
      val items = List(1, 2, 3, 4, 5)
      val publisher = PullPublisher(items.iterator.iterator)
      val subscriber = new MockSubscriber[Int]()

      publisher.subscribe(subscriber)

      // Request 2 items
      subscriber.sub.request(2)
      Thread.sleep(100L)

      assert(subscriber.received.sameElements(List(1, 2)))
      assert(subscriber.completes == 0)

      // Request 2 more items
      subscriber.sub.request(2)
      Thread.sleep(100L)
      assert(subscriber.received.sameElements(List(1, 2, 3, 4)))
      assert(subscriber.completes == 0)

      // Request last item
      subscriber.sub.request(2)
      subscriber.awaitComplete()
      assert(subscriber.received.sameElements(items))
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
    }

    test("cancel should stop delivery") {
      val items = Range(0, 100).toList
      val publisher = PullPublisher(items.iterator)
      val subscriber = new MockSubscriber[Int]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(2)
      Thread.sleep(100)
      assert(subscriber.received.sameElements(List(0, 1)))

      subscriber.sub.cancel()
      subscriber.sub.request(10)

      Thread.sleep(100)

      // Should not receive more items
      assert(subscriber.received.sameElements(List(0, 1)))
      assert(subscriber.completes == 0)
    }

    test("concurrent requests should work correctly") {
      val items = (1 to 1000).toList
      val publisher = PullPublisher(items.iterator)
      val subscriber = new MockSubscriber[Int]()

      publisher.subscribe(subscriber)

      // Multiple concurrent requests
      (1 to 10).foreach(_ => new Thread(() => subscriber.sub.request(100)).start())

      subscriber.awaitComplete()
      assert(subscriber.received.sameElements(items))
      assert(subscriber.completes == 1)
      assert(subscriber.errors == 0)
    }

    test("concurrent cancellations should work correctly") {
      @volatile
      var counter = 0

      val closeHandler = () =>
        synchronized {
          counter += 1
        }

      val items = (1 to 1000).toList
      val publisher = PullPublisher(items.iterator, closeHandler)
      val subscriber = new MockSubscriber[Int]()
      publisher.subscribe(subscriber)

      (1 to 10)
        .foreach { _ =>
          val thread = new Thread(() => {
            subscriber.sub.request(100)
            if (ThreadLocalRandom.current().nextBoolean())
              subscriber.sub.cancel()
          })
          thread.start()
          thread.join()
        }

      assert(counter == 1)
    }

    test(s"non-positive request terminates with IllegalArgumentException") {
      for (n <- Seq(0, -1, -100)) {
        val publisher = PullPublisher(Iterator(1, 2, 3))
        val subscriber = MockSubscriber[Int]()

        publisher.subscribe(subscriber)
        subscriber.sub.request(n)

        subscriber.awaitComplete()
        assert(subscriber.errors == 1)
        assert(subscriber.completes == 0)
        assert(subscriber.received.isEmpty)
        assert(subscriber.lastError.isInstanceOf[IllegalArgumentException])
      }
    }

    test("iterator failure is propagated after already emitted items") {
      val publisher = PullPublisher(
        new Iterator[String] {
          private var count = 0
          override def hasNext: Boolean =
            count < 2

          override def next(): String =
            count += 1
            if count == 2
            then throw new RuntimeException("iterator failure")
            else s"item${count}"
        },
      )
      val subscriber = MockSubscriber[String]()

      publisher.subscribe(subscriber)
      subscriber.sub.request(10)

      subscriber.awaitComplete()
      assert(subscriber.received.toList == List("item1"))
      assert(subscriber.errors == 1)
      assert(subscriber.lastError.getMessage().contains("iterator failure"))
    }

    test("PullPublisher can be subscribed only once") {
      val publisher = PullPublisher(Iterator(1, 2, 3))

      val subscriber1 = MockSubscriber[Int]()
      val subscriber2 = MockSubscriber[Int]()

      publisher.subscribe(subscriber1)
      publisher.subscribe(subscriber2)

      subscriber1.sub.request(10)
      subscriber1.awaitComplete()
      assert(subscriber1.received.sameElements(Iterator(1, 2, 3)))
      assert(subscriber1.errors == 0)
      assert(subscriber1.completes == 1)

      subscriber2.awaitError()
      assert(subscriber2.errors == 1)
      assert(subscriber2.lastError.isInstanceOf[IllegalStateException])
      assert(subscriber2.received.isEmpty)
    }
