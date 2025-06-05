package snhttp.jdk

import java.util.concurrent.Flow
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ListBuffer

class PullPublisherTest extends munit.FunSuite {

  // test("publish empty iterable should complete immediately") {
  //   val publisher = PullPublisher(List.empty[String])
  //   val subscriber = new TestSubscriber[String]()

  //   publisher.subscribe(subscriber)

  //   assert(subscriber.waitForCompletion(1000))
  //   assertEquals(subscriber.received, List.empty)
  //   assert(subscriber.completed.get())
  //   assert(!subscriber.hasError.get())
  // }

  // test("publish single item should deliver and complete") {
  //   val items = List("hello")
  //   val publisher = PullPublisher(items)
  //   val subscriber = new TestSubscriber[String]()

  //   publisher.subscribe(subscriber)
  //   subscriber.subscription.request(1)

  //   assert(subscriber.waitForCompletion(1000))
  //   assertEquals(subscriber.received, items)
  //   assert(subscriber.completed.get())
  //   assert(!subscriber.hasError.get())
  // }

  // test("publish multiple items should deliver all and complete") {
  //   val items = List("a", "b", "c", "d", "e")
  //   val publisher = PullPublisher(items)
  //   val subscriber = new TestSubscriber[String]()

  //   publisher.subscribe(subscriber)
  //   subscriber.subscription.request(10)

  //   assert(subscriber.waitForCompletion(1000))
  //   assertEquals(subscriber.received, items)
  //   assert(subscriber.completed.get())
  //   assert(!subscriber.hasError.get())
  // }

  // test("request more than available should deliver all and complete") {
  //   val items = List(1, 2, 3)
  //   val publisher = PullPublisher(items)
  //   val subscriber = new TestSubscriber[Int]()

  //   publisher.subscribe(subscriber)
  //   subscriber.subscription.request(100)

  //   assert(subscriber.waitForCompletion(1000))
  //   assertEquals(subscriber.received, items)
  //   assert(subscriber.completed.get())
  //   assert(!subscriber.hasError.get())
  // }

  // test("backpressure should work with demand") {
  //   val items = List(1, 2, 3, 4, 5)
  //   val publisher = PullPublisher(items)
  //   val subscriber = new TestSubscriber[Int]()

  //   publisher.subscribe(subscriber)

  //   // Request 2 items
  //   subscriber.subscription.request(2)
  //   Thread.sleep(100)
  //   assertEquals(subscriber.received.size, 2)
  //   assertEquals(subscriber.received, List(1, 2))
  //   assert(!subscriber.completed.get())

  //   // Request 2 more items
  //   subscriber.subscription.request(2)
  //   Thread.sleep(100)
  //   assertEquals(subscriber.received.size, 4)
  //   assertEquals(subscriber.received, List(1, 2, 3, 4))
  //   assert(!subscriber.completed.get())

  //   // Request last item
  //   subscriber.subscription.request(1)
  //   assert(subscriber.waitForCompletion(1000))
  //   assertEquals(subscriber.received, items)
  //   assert(subscriber.completed.get())
  // }

  // test("zero or negative request should cause error") {
  //   val items = List(1, 2, 3)
  //   val publisher = PullPublisher(items)
  //   val subscriber = new TestSubscriber[Int]()

  //   publisher.subscribe(subscriber)
  //   subscriber.subscription.request(0)

  //   assert(subscriber.waitForError(1000))
  //   assert(subscriber.hasError.get())
  //   assert(!subscriber.completed.get())
  //   assert(subscriber.error.isInstanceOf[IllegalArgumentException])
  // }

  // test("negative request should cause error") {
  //   val items = List(1, 2, 3)
  //   val publisher = PullPublisher(items)
  //   val subscriber = new TestSubscriber[Int]()

  //   publisher.subscribe(subscriber)
  //   subscriber.subscription.request(-5)

  //   assert(subscriber.waitForError(1000))
  //   assert(subscriber.hasError.get())
  //   assert(!subscriber.completed.get())
  //   assert(subscriber.error.isInstanceOf[IllegalArgumentException])
  // }

  // test("cancel should stop delivery") {
  //   val items = List(1, 2, 3, 4, 5)
  //   val publisher = PullPublisher(items)
  //   val subscriber = new TestSubscriber[Int]()

  //   publisher.subscribe(subscriber)
  //   subscriber.subscription.request(2)
  //   Thread.sleep(100)
  //   assertEquals(subscriber.received.size, 2)

  //   subscriber.subscription.cancel()
  //   subscriber.subscription.request(10)
  //   Thread.sleep(100)

  //   // Should not receive more items
  //   assertEquals(subscriber.received.size, 2)
  //   assert(!subscriber.completed.get())
  // }

  // test("publisher with error should deliver error") {
  //   val error = new RuntimeException("test error")
  //   val publisher = PullPublisher(List(1, 2, 3), error)
  //   val subscriber = new TestSubscriber[Int]()

  //   publisher.subscribe(subscriber)

  //   assert(subscriber.waitForError(1000))
  //   assert(subscriber.hasError.get())
  //   assertEquals(subscriber.error, error)
  //   assertEquals(subscriber.received.size, 0)
  // }

  // test("iterator exception should be propagated") {
  //   val failingIterable = new Iterable[String] {
  //     def iterator: Iterator[String] = new Iterator[String] {
  //       private var count = 0
  //       def hasNext: Boolean = count < 3
  //       def next(): String = {
  //         count += 1
  //         if count == 2 then throw new RuntimeException("iterator failure")
  //         else s"item$count"
  //       }
  //     }
  //   }

  //   val publisher = PullPublisher(failingIterable)
  //   val subscriber = new TestSubscriber[String]()

  //   publisher.subscribe(subscriber)
  //   subscriber.subscription.request(10)

  //   assert(subscriber.waitForError(1000))
  //   assert(subscriber.hasError.get())
  //   assertEquals(subscriber.received, List("item1"))
  //   assert(subscriber.error.getMessage.contains("iterator failure"))
  // }

  // test("multiple subscribers should get independent iterators") {
  //   val items = List(1, 2, 3)
  //   val publisher = PullPublisher(items)
  //   val subscriber1 = new TestSubscriber[Int]()
  //   val subscriber2 = new TestSubscriber[Int]()

  //   publisher.subscribe(subscriber1)
  //   publisher.subscribe(subscriber2)

  //   subscriber1.subscription.request(10)
  //   subscriber2.subscription.request(10)

  //   assert(subscriber1.waitForCompletion(1000))
  //   assert(subscriber2.waitForCompletion(1000))

  //   assertEquals(subscriber1.received, items)
  //   assertEquals(subscriber2.received, items)
  // }

  // test("concurrent requests should work correctly") {
  //   val items = (1 to 100).toList
  //   val publisher = PullPublisher(items)
  //   val subscriber = new TestSubscriber[Int]()

  //   publisher.subscribe(subscriber)

  //   // Multiple concurrent requests
  //   (1 to 10).foreach(_ => new Thread(() => subscriber.subscription.request(10)).start())

  //   assert(subscriber.waitForCompletion(2000))
  //   assertEquals(subscriber.received.size, 100)
  //   assertEquals(subscriber.received.sorted, items)
  // }

  // private class TestSubscriber[T] extends Flow.Subscriber[T] {
  //   val received = ListBuffer[T]()
  //   val completed = new AtomicBoolean(false)
  //   val hasError = new AtomicBoolean(false)
  //   var error: Throwable = null
  //   var subscription: Flow.Subscription = null
  //   private val completionLatch = new CountDownLatch(1)
  //   private val errorLatch = new CountDownLatch(1)

  //   override def onSubscribe(subscription: Flow.Subscription): Unit =
  //     this.subscription = subscription

  //   override def onNext(item: T): Unit = synchronized {
  //     received += item
  //   }

  //   override def onError(throwable: Throwable): Unit = {
  //     error = throwable
  //     hasError.set(true)
  //     errorLatch.countDown()
  //   }

  //   override def onComplete(): Unit = {
  //     completed.set(true)
  //     completionLatch.countDown()
  //   }

  //   def waitForCompletion(timeoutMs: Long): Boolean =
  //     completionLatch.await(timeoutMs, TimeUnit.MILLISECONDS)

  //   def waitForError(timeoutMs: Long): Boolean =
  //     errorLatch.await(timeoutMs, TimeUnit.MILLISECONDS)
  // }
}
