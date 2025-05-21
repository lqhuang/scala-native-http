package java.net.http

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.OpenOption
import java.nio.file.{Files, Path, Paths}
import java.nio.file.StandardOpenOption.{READ, DELETE_ON_CLOSE, CREATE, WRITE}
import java.net.URI
import java.util.{List, Optional}
import java.util.Objects.requireNonNull
import java.util.concurrent.{CompletableFuture, CompletionStage, ConcurrentMap}
import java.util.concurrent.Flow.{Subscriber, Publisher, Subscription}
import java.util.function.{Function, Consumer}
import java.util.stream.Stream
import javax.net.ssl.SSLSession

import snhttp.internal.Utils.charsetFrom
import snhttp.jdk.ResponseSubscribers
import snhttp.jdk.ResponseBodyHandlers.{
  PathBodyHandler,
  FileDownloadBodyHandler,
  PushPromisesHandlerWithMap,
}

/// @since 11
trait HttpResponse[T] {
  def statusCode(): Int

  /// @since 25
  def connectionLabel(): Optional[String]

  def request(): HttpRequest

  def previousResponse(): Optional[HttpResponse[T]]

  def headers(): HttpHeaders

  def body(): T

  def sslSession(): Optional[SSLSession]

  def uri(): URI

  def version(): HttpClient.Version

}

object HttpResponse {

  trait ResponseInfo {
    def statusCode(): Int

    def headers(): HttpHeaders

    def version(): HttpClient.Version
  }

  /// @since 11
  @FunctionalInterface
  trait BodyHandler[T] {
    def apply(responseInfo: ResponseInfo): BodySubscriber[T]
  }

  /// @since 11
  object BodyHandlers {
    def fromSubscriber(subscriber: Subscriber[? >: List[ByteBuffer]]): BodyHandler[Void] = {
      requireNonNull(subscriber)
      return responseInfo => BodySubscribers.fromSubscriber(subscriber, (_: Any) => null)
    }

    def fromSubscriber[S <: Subscriber[? >: List[ByteBuffer]], T](
        subscriber: S,
        finisher: Function[? >: S, ? <: T],
    ): BodyHandler[T] = {
      requireNonNull(subscriber)
      requireNonNull(finisher)
      return responseInfo => BodySubscribers.fromSubscriber(subscriber, finisher)
    }

    def fromLineSubscriber(subscriber: Subscriber[? >: String]): BodyHandler[Void] = {
      requireNonNull(subscriber)
      return responseInfo =>
        BodySubscribers.fromLineSubscriber(
          subscriber,
          (_: Any) => null,
          charsetFrom(responseInfo.headers()),
          null,
        )
    }

    def fromLineSubscriber[S <: Subscriber[? >: String], T](
        subscriber: S,
        finisher: Function[? >: S, ? <: T],
        lineSeparator: String,
    ): BodyHandler[T] = {
      requireNonNull(subscriber)
      requireNonNull(finisher)
      require(lineSeparator == null || lineSeparator.nonEmpty, "empty line separator")
      return responseInfo =>
        BodySubscribers.fromLineSubscriber(
          subscriber,
          finisher,
          charsetFrom(responseInfo.headers()),
          lineSeparator,
        )
    }

    def discarding(): BodyHandler[Void] = _ => BodySubscribers.discarding()

    def replacing[U](value: U): BodyHandler[U] = _ => BodySubscribers.replacing(value)

    def ofString(charset: Charset): BodyHandler[String] = {
      requireNonNull(charset)
      return _ => BodySubscribers.ofString(charset)
    }

    def ofFile(file: Path, openOptions: OpenOption*): BodyHandler[Path] = {
      requireNonNull(file)
      require(
        !openOptions.contains(DELETE_ON_CLOSE) && !openOptions.contains(READ),
        s"invalid openOptions: $openOptions",
      )

      val opts = List.of(openOptions*)
      return PathBodyHandler.create(file, opts)
    }

    def ofFile(file: Path): BodyHandler[Path] = ofFile(file, CREATE, WRITE)

    def ofFileDownload(directory: Path, openOptions: OpenOption*): BodyHandler[Path] = {
      requireNonNull(directory)
      require(!openOptions.contains(DELETE_ON_CLOSE), s"invalid openOptions: ${openOptions}")
      val opts = List.of(openOptions*)
      return FileDownloadBodyHandler.create(directory, opts)
    }

    def ofInputStream(): BodyHandler[InputStream] =
      _ => BodySubscribers.ofInputStream()

    def ofLines(): BodyHandler[Stream[String]] =
      ri => BodySubscribers.ofLines(charsetFrom(ri.headers()))

    def ofByteArrayConsumer(consumer: Consumer[Optional[Array[Byte]]]): BodyHandler[Void] =
      _ => BodySubscribers.ofByteArrayConsumer(consumer)

    def ofByteArray(): BodyHandler[Array[Byte]] = _ => BodySubscribers.ofByteArray()

    def ofString(): BodyHandler[String] = ri => BodySubscribers.ofString(charsetFrom(ri.headers()))

    def ofPublisher(): BodyHandler[Publisher[List[ByteBuffer]]] = _ => BodySubscribers.ofPublisher()

    def buffering[T](downstreamHandler: BodyHandler[T], bufferSize: Int): BodyHandler[T] = {
      require(bufferSize > 0, "must be greater than 0")
      ri => BodySubscribers.buffering(downstreamHandler(ri), bufferSize)
    }

    def limiting[T](downstreamHandler: BodyHandler[T], capacity: Long): BodyHandler[T] = {
      require(capacity >= 0, s"capacity must not be negative: $capacity")
      ri => { val dsSub = downstreamHandler(ri); BodySubscribers.limiting(dsSub, capacity) }
    }
  }

  trait PushPromiseHandler[T] {
    def applyPushPromise(
        initiatingRequest: HttpRequest,
        pushPromiseRequest: HttpRequest,
        acceptor: Function[BodyHandler[T], CompletableFuture[HttpResponse[T]]],
    ): Unit
  }
  object PushPromiseHandler {
    def of[T](
        pushPromiseHandler: Function[HttpRequest, BodyHandler[T]],
        pushPromisesMap: ConcurrentMap[HttpRequest, CompletableFuture[HttpResponse[T]]],
    ): PushPromiseHandler[T] =
      return new PushPromisesHandlerWithMap(pushPromiseHandler, pushPromisesMap)
  }

  /// @since 11
  trait BodySubscriber[T] extends Subscriber[List[ByteBuffer]] {
    def getBody(): CompletionStage[T]
  }

  object BodySubscribers {
    def fromSubscriber(
        subscriber: Subscriber[? >: List[ByteBuffer]],
    ): BodySubscriber[Void] =
      new ResponseSubscribers.SubscriberAdapter(subscriber, (_: Any) => null)

    def fromSubscriber[S <: Subscriber[? >: List[ByteBuffer]], T](
        subscriber: S,
        finisher: Function[? >: S, ? <: T],
    ): BodySubscriber[T] =
      new ResponseSubscribers.SubscriberAdapter(subscriber, finisher)

    def fromLineSubscriber(subscriber: Subscriber[? >: String]): BodySubscriber[Void] =
      fromLineSubscriber(subscriber, (_: Any) => null, UTF_8, null)

    def fromLineSubscriber[S <: Subscriber[? >: String], T](
        subscriber: S,
        finisher: Function[? >: S, ? <: T],
        charset: Charset,
        lineSeparator: String,
    ): BodySubscriber[T] =
      new ResponseSubscribers.LineSubscriberAdapter(subscriber, finisher, charset, lineSeparator)

    def ofString(charset: Charset): BodySubscriber[String] = {
      requireNonNull(charset)
      new ResponseSubscribers.ByteArraySubscriber(bytes => new String(bytes, charset))
    }

    def ofByteArray(): BodySubscriber[Array[Byte]] =
      new ResponseSubscribers.ByteArraySubscriber(identity)

    def ofFile(
        file: Path,
        openOptions: OpenOption*,
    ): BodySubscriber[Path] = {
      require(
        !openOptions.contains(DELETE_ON_CLOSE) && !openOptions.contains(READ),
        s"invalid openOptions: ${openOptions}",
      )
      val opts = List.of(openOptions*)
      return ResponseSubscribers.PathSubscriber.create(file, opts)
    }

    def ofFile(file: Path): BodySubscriber[Path] =
      ofFile(file, CREATE, WRITE)

    def ofByteArrayConsumer(
        consumer: Consumer[Optional[Array[Byte]]],
    ): BodySubscriber[Void] =
      new ResponseSubscribers.ConsumerSubscriber(consumer)

    def ofInputStream(): BodySubscriber[InputStream] =
      new ResponseSubscribers.InputStreamSubscriber()

    def ofLines(
        charset: Charset,
    ): BodySubscriber[Stream[String]] =
      ResponseSubscribers.createLineStream(charset)

    def ofPublisher(): BodySubscriber[Publisher[List[ByteBuffer]]] =
      ResponseSubscribers.createPublisher()

    def replacing[U](value: U): BodySubscriber[U] =
      new ResponseSubscribers.NullSubscriber(Optional.ofNullable(value))

    def discarding(): BodySubscriber[Void] =
      new ResponseSubscribers.NullSubscriber(Optional.empty())

    def buffering[T](downstream: BodySubscriber[T], bufferSize: Int): BodySubscriber[T] = {
      require(bufferSize > 0, "must be greater than 0")
      new ResponseSubscribers.BufferingSubscriber(downstream, bufferSize)
    }

    def mapping[T, U](
        upstream: BodySubscriber[T],
        mapper: Function[? >: T, ? <: U],
    ): BodySubscriber[U] =
      new ResponseSubscribers.MappingSubscriber(upstream, mapper)

    def limiting[T](downstreamSubscriber: BodySubscriber[T], capacity: Long): BodySubscriber[T] = {
      require(capacity >= 0, s"capacity must not be negative: $capacity")
      new ResponseSubscribers.LimitingSubscriber(downstreamSubscriber, capacity)
    }
  }

}
