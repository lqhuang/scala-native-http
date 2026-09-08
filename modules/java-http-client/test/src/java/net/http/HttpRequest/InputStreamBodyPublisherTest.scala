package snhttp.test.java.net.http

import java.io.IOException
import java.net.http.HttpRequest.BodyPublishers
import java.nio.ByteBuffer
import java.util.concurrent.{ConcurrentLinkedQueue, Flow, ForkJoinPool, TimeUnit}

import scala.jdk.CollectionConverters.*

import utest.{TestSuite, Tests, assert, test}

class InputStreamBodyPublisherTest extends TestSuite:

  private class RecordingSubscriber(onSubscribeAction: Flow.Subscription => Unit = _ => ())
      extends Flow.Subscriber[ByteBuffer]:

    private val events = new ConcurrentLinkedQueue[String]()
    val errors = new ConcurrentLinkedQueue[Throwable]()
    @volatile var subscription: Flow.Subscription = _

    // Assertions in worker callbacks can hide an invalid second terminal signal.
    // Record every callback and assert on the test thread after queued work settles.
    override def onSubscribe(value: Flow.Subscription): Unit = {
      subscription = value
      events.add("subscribe"): Unit
      onSubscribeAction(value)
    }

    override def onNext(value: ByteBuffer): Unit =
      events.add("next"): Unit

    override def onError(error: Throwable): Unit = {
      errors.add(error): Unit
      events.add("error"): Unit
    }

    override def onComplete(): Unit =
      events.add("complete"): Unit

    def signals: List[String] = events.iterator().asScala.toList

  end RecordingSubscriber

  private def settleCallbacks(): Unit =
    assert(ForkJoinPool.commonPool().awaitQuiescence(5, TimeUnit.SECONDS))

  def tests = Tests:

    test("unavailable stream sends only an error with immediate demand") {
      val publisher = BodyPublishers.ofInputStream(() => null)
      val subscriber = new RecordingSubscriber(_.request(1))

      publisher.subscribe(subscriber)
      settleCallbacks()

      assert(subscriber.signals == List("subscribe", "error"))
      assert(subscriber.errors.peek().isInstanceOf[IOException])
    }

    test("unavailable stream ignores demand after its terminal error") {
      val publisher = BodyPublishers.ofInputStream(() => null)
      val subscriber = new RecordingSubscriber()

      publisher.subscribe(subscriber)
      settleCallbacks()
      assert(subscriber.signals == List("subscribe", "error"))

      subscriber.subscription.request(1)
      settleCallbacks()
      subscriber.subscription.request(0)
      settleCallbacks()

      assert(subscriber.signals == List("subscribe", "error"))
      assert(subscriber.errors.peek().isInstanceOf[IOException])
    }

    test("unavailable stream respects cancellation during onSubscribe") {
      val publisher = BodyPublishers.ofInputStream(() => null)
      val subscriber = new RecordingSubscriber(_.cancel())

      publisher.subscribe(subscriber)
      settleCallbacks()

      assert(subscriber.signals == List("subscribe"))
      assert(subscriber.errors.isEmpty())
    }

    test("unavailable stream does not send a second error after invalid demand") {
      val publisher = BodyPublishers.ofInputStream(() => null)
      val subscriber = new RecordingSubscriber(_.request(0))

      publisher.subscribe(subscriber)
      settleCallbacks()

      assert(subscriber.signals == List("subscribe", "error"))
      assert(subscriber.errors.peek().isInstanceOf[IllegalArgumentException])
    }

end InputStreamBodyPublisherTest
