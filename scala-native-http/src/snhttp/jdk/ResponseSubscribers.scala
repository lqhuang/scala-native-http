package snhttp.jdk

import java.io.IOException
import java.io.{InputStream, BufferedReader, InputStreamReader}
import java.net.http.HttpResponse.BodySubscriber
import java.nio.ByteBuffer
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.channels.FileChannel
import java.nio.file.{Path, OpenOption}
import java.util.{List, ArrayList, Optional}
import java.util.Objects.requireNonNull
import java.util.stream.Stream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.Flow
import java.util.concurrent.Flow.{Publisher, Subscription, Subscriber}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.{Consumer, Function}

object ResponseSubscribers {
  trait TrustedSubscriber[T] extends BodySubscriber[T] {
    def needsExecutor(): Boolean = false

    def needsExecutor(bs: BodySubscriber[?]): Boolean =
      if (bs.isInstanceOf[TrustedSubscriber[_]])
        bs.asInstanceOf[TrustedSubscriber[_]].needsExecutor()
      else true

    def getBody(): CompletionStage[T]
  }

  class ConsumerSubscriber(private val consumer: Consumer[Optional[Array[Byte]]])
      extends TrustedSubscriber[Void] {
    private var subscription: Subscription = null
    private val result = new CompletableFuture[Void]()
    private val subscribed = new AtomicBoolean()

    override def getBody(): CompletionStage[Void] = result

    override def onSubscribe(subscription: Subscription): Unit =
      if (!subscribed.compareAndSet(false, true)) {
        subscription.cancel()
      } else {
        this.subscription = subscription
        subscription.request(1)
      }

    override def onNext(items: List[ByteBuffer]): Unit = {
      requireNonNull(items)
      val it = items.iterator()
      while (it.hasNext) {
        val item = it.next()
        val buf = new Array[Byte](item.remaining())
        item.get(buf)
        consumer.accept(Optional.of(buf))
      }
      subscription.request(1)
    }

    override def onError(throwable: Throwable): Unit = {
      requireNonNull(throwable)
      result.completeExceptionally(throwable)
    }

    override def onComplete(): Unit = {
      consumer.accept(Optional.empty())
      result.complete(null)
    }
  }

  class PathSubscriber(private val file: Path, private val options: List[OpenOption])
      extends TrustedSubscriber[Path] {
    private val _options = options.toArray(new Array[OpenOption](options.size))
    private val result = new CompletableFuture[Path]()
    private val subscribed = new AtomicBoolean()

    @volatile private var subscription: Subscription = _
    @volatile private var out: FileChannel = _

    override def onSubscribe(subscription: Subscription): Unit = {
      requireNonNull(subscription)
      if (!subscribed.compareAndSet(false, true)) {
        subscription.cancel()
        return
      }
      this.subscription = subscription

      try
        out = FileChannel.open(file, _options*)
      catch {
        case exc: IOException =>
          result.completeExceptionally(exc)
          subscription.cancel()
          return
      }
      subscription.request(1)
    }

    override def onNext(items: List[ByteBuffer]): Unit = {
      try {
        val buffers = new Array[ByteBuffer](items.size)
        while ({
          out.write(buffers)
          buffers.exists(_.hasRemaining)
        }) ()
      } catch {
        case exc: IOException =>
          close()
          subscription.cancel()
          result.completeExceptionally(exc)
      }
      subscription.request(1)
    }

    override def onError(e: Throwable): Unit = {
      result.completeExceptionally(e)
      close()
    }

    override def onComplete(): Unit = {
      close()
      result.complete(file)
    }

    override def getBody(): CompletionStage[Path] = result

    private def close(): Unit =
      if (out != null) {
        try out.close()
        catch { case _: Throwable => () }
      }
  }
  object PathSubscriber {
    def create(file: Path, openOptions: List[OpenOption]): PathSubscriber =
      new PathSubscriber(file, openOptions)
  }

  class ByteArraySubscriber[T](finisher: Array[Byte] => T) extends TrustedSubscriber[T] {
    private val result = new CompletableFuture[T]()
    private val received = new ArrayList[ByteBuffer]()

    @volatile private var subscription: Subscription = _

    override def onSubscribe(subscription: Subscription): Unit = {
      if (this.subscription != null) {
        subscription.cancel()
        return
      }
      this.subscription = subscription
      subscription.request(Long.MaxValue)
    }

    override def onNext(items: List[ByteBuffer]): Unit = {
      requireNonNull(items)
      require(!items.isEmpty()) // TODO: pending to be rewriten
      received.addAll(items)
    }

    override def onError(throwable: Throwable): Unit = {
      received.clear()
      result.completeExceptionally(throwable)
    }

    private def join(bytes: List[ByteBuffer]): Array[Byte] = ???
    // val size = bytes.stream().mapToInt(_.remaining()).sum()
    // val res = new Array[Byte](size)
    // var from = 0
    // val it = bytes.iterator()
    // while (it.hasNext) {
    //   val b = it.next()
    //   val l = b.remaining()
    //   b.get(res, from, l)
    //   from += l
    // }
    // res

    override def onComplete(): Unit =
      try {
        result.complete(finisher(join(received)))
        received.clear()
      } catch {
        case e: IllegalArgumentException =>
          result.completeExceptionally(e)
      }

    override def getBody(): CompletionStage[T] = result
  }

  class InputStreamSubscriber extends InputStream with TrustedSubscriber[InputStream] {

    override def read(): Int = ???

    override def onSubscribe(subscription: Subscription): Unit = ???

    override def onNext(item: List[ByteBuffer]): Unit = ???

    override def onComplete(): Unit = ???

    override def onError(throwable: Throwable): Unit = ???

    override def getBody(): CompletionStage[InputStream] = ???

  }

  class NullSubscriber[T](private val result: Optional[T]) extends TrustedSubscriber[T] {
    private val cf = new CompletableFuture[T]()
    private val subscribed = new AtomicBoolean()

    override def onSubscribe(subscription: Subscription): Unit =
      if (!subscribed.compareAndSet(false, true)) subscription.cancel()
      else subscription.request(Long.MaxValue)

    override def onNext(items: List[ByteBuffer]): Unit = ()

    override def onError(throwable: Throwable): Unit = cf.completeExceptionally(throwable)

    override def onComplete(): Unit =
      if (result.isPresent()) cf.complete(result.get()) else cf.complete(null.asInstanceOf[T])

    override def getBody(): CompletionStage[T] = cf
  }

  class MappingSubscriber[T, U](
      private val upstream: BodySubscriber[T],
      private val mapper: Function[? >: T, ? <: U],
      private val trusted: Boolean = false,
  ) extends TrustedSubscriber[U] {
    def this(
        upstream: BodySubscriber[T],
        mapper: Function[? >: T, ? <: U],
    ) = this(upstream, mapper, false)

    override def needsExecutor(): Boolean = !trusted || super.needsExecutor(upstream)

    override def getBody(): CompletionStage[U] = upstream.getBody().thenApply(mapper)

    override def onSubscribe(subscription: Subscription): Unit =
      upstream.onSubscribe(subscription)

    override def onNext(item: List[ByteBuffer]): Unit = upstream.onNext(item)

    override def onError(throwable: Throwable): Unit = upstream.onError(throwable)

    override def onComplete(): Unit = upstream.onComplete()
  }

  class PublishingBodySubscriber extends TrustedSubscriber[Publisher[List[ByteBuffer]]] {

    override def onSubscribe(subscription: Subscription): Unit = ???

    override def onNext(item: List[ByteBuffer]): Unit = ???

    override def onComplete(): Unit = ???

    override def onError(throwable: Throwable): Unit = ???

    override def getBody(): CompletionStage[Publisher[List[ByteBuffer]]] = ???

  }

  class BufferingSubscriber[T](
      private val downstreamSubscriber: BodySubscriber[T],
      private val bufferSize: Int,
  ) extends TrustedSubscriber[T] {

    override def onSubscribe(subscription: Subscription): Unit = ???

    override def onNext(item: List[ByteBuffer]): Unit = ???

    override def onComplete(): Unit = ???

    override def onError(throwable: Throwable): Unit = ???

    override def getBody(): CompletionStage[T] = ???
  }

  class LimitingSubscriber[T](
      private val downstreamSubscriber: BodySubscriber[T],
      private val capacity: Long,
  ) extends TrustedSubscriber[T] {

    override def onSubscribe(subscription: Subscription): Unit = ???

    override def onNext(item: List[ByteBuffer]): Unit = ???

    override def onComplete(): Unit = ???

    override def onError(throwable: Throwable): Unit = ???

    override def getBody(): CompletionStage[T] = ???
  }

  final class SubscriberAdapter[S <: Subscriber[? >: List[ByteBuffer]], R](
      subscriber: S,
      finisher: Function[? >: S, ? <: R],
  ) extends TrustedSubscriber[R] {
    private val cf = new CompletableFuture[R]()
    private var subscription: Subscription = _

    override def onSubscribe(subscription: Subscription): Unit =
      if (this.subscription != null) subscription.cancel()
      else {
        this.subscription = subscription
        subscriber.onSubscribe(subscription)
      }

    override def onNext(item: List[ByteBuffer]): Unit =
      try subscriber.onNext(item)
      catch { case t: Throwable => subscription.cancel(); onError(t) }

    override def onError(throwable: Throwable): Unit =
      try subscriber.onError(throwable)
      finally cf.completeExceptionally(throwable)

    override def onComplete(): Unit =
      try subscriber.onComplete()
      finally
        try cf.completeAsync(() => finisher(subscriber))
        catch { case t: Throwable => cf.completeExceptionally(t) }

    override def getBody(): CompletionStage[R] = cf
  }

  final class LineSubscriberAdapter[S <: Subscriber[? >: String], R](
      subscriber: S,
      finisher: Function[? >: S, ? <: R],
      charset: Charset,
      eol: String,
  ) extends TrustedSubscriber[R] {
    private val cf = new CompletableFuture[R]()
    private var downstream: Subscription = _
    private val subscribed = new AtomicBoolean()

    override def onSubscribe(subscription: Subscription): Unit = ???

    override def onNext(item: List[ByteBuffer]): Unit = ???

    override def onComplete(): Unit = ???

    override def onError(throwable: Throwable): Unit = ???

    override def getBody(): CompletionStage[R] = ???
  }

  /// major public methods for ResponseSubscribers

  def createLineStream(): BodySubscriber[Stream[String]] = createLineStream(StandardCharsets.UTF_8)

  def createLineStream(charset: Charset): BodySubscriber[Stream[String]] = {
    requireNonNull(charset)

    val s = new InputStreamSubscriber()
    return new MappingSubscriber[InputStream, Stream[String]](
      s,
      (stream: InputStream) =>
        new BufferedReader(new InputStreamReader(stream, charset))
          .lines()
          .onClose(() => stream.close()),
      true,
    )
  }

  def createPublisher(): BodySubscriber[Publisher[List[ByteBuffer]]] =
    new PublishingBodySubscriber()

  def getBodyAsync[T](
      e: Executor,
      bs: BodySubscriber[T],
  ): CompletionStage[T] =
    if (
      bs.isInstanceOf[TrustedSubscriber[_]] && bs.asInstanceOf[TrustedSubscriber[_]].needsExecutor()
    )
      getBodyAsync(e, bs, new CompletableFuture[T]())
    else bs.getBody()

  def getBodyAsync[T](
      e: Executor,
      bs: BodySubscriber[T],
      cf: CompletableFuture[T],
  ): CompletionStage[T] =
    getBodyAsync(e, bs, cf, (t: Throwable) => cf.completeExceptionally(t))

  def getBodyAsync[T](
      e: Executor,
      bs: BodySubscriber[T],
      cf: CompletableFuture[T],
      errorHandler: Consumer[Throwable],
  ): CompletableFuture[T] = {
    // format: off
    try {
    // format: on
      e.execute { () =>
        try
          bs.getBody()
            .whenComplete((r: T, t: Throwable) =>
              if (t != null) cf.completeExceptionally(t)
              else cf.complete(r),
            )
        catch { case t: Throwable => errorHandler.accept(t) }
        return cf
      }
    } catch { case t: Throwable => errorHandler.accept(t) }
    return cf
  }
}
