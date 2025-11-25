package java.net.http

import java.io.InputStream
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Files, OpenOption, Path, Paths}
import java.nio.file.StandardOpenOption.{READ, DELETE_ON_CLOSE, CREATE, WRITE}
import java.util.List as JList
import java.util.Optional
import java.util.Objects.requireNonNull
import java.util.concurrent.{CompletableFuture, CompletionStage, ConcurrentMap}
import java.util.concurrent.Flow.{Subscriber, Publisher, Subscription}
import java.util.function.{Function, Consumer}
import java.util.stream.Stream
import javax.net.ssl.SSLSession

import snhttp.jdk.internal.Utils.charsetFrom
import snhttp.jdk.net.http.{BodyHandlersImpl, BodySubscribersImpl}

/// @since 11
trait HttpResponse[T] {
  def statusCode(): Int

  /// @since 25
  def connectionLabel(): Optional[String]

  def request(): HttpRequest

  def previousResponse(): Optional[HttpResponse[T]]

  def headers(): HttpHeaders

  def body(): T

  // def sslSession(): Optional[SSLSession]

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
  abstract class BodyHandlers {}
  object BodyHandlers {

    def fromSubscriber(subscriber: Subscriber[? >: JList[ByteBuffer]]): BodyHandler[Void] =
      requireNonNull(subscriber)
      responseInfo => BodySubscribers.fromSubscriber(subscriber, _ => null)

    def fromSubscriber[S <: Subscriber[? >: JList[ByteBuffer]], T](
        subscriber: S,
        finisher: Function[? >: S, ? <: T],
    ): BodyHandler[T] =
      requireNonNull(subscriber)
      requireNonNull(finisher)
      _ => BodySubscribers.fromSubscriber(subscriber, finisher)

    def fromLineSubscriber(subscriber: Subscriber[? >: String]): BodyHandler[Void] =
      requireNonNull(subscriber)
      ri =>
        BodySubscribers.fromLineSubscriber(
          subscriber,
          _ => null,
          charsetFrom(ri.headers()),
          null,
        )

    def fromLineSubscriber[S <: Subscriber[? >: String], T](
        subscriber: S,
        finisher: Function[? >: S, ? <: T],
        lineSeparator: String,
    ): BodyHandler[T] =
      requireNonNull(subscriber)
      requireNonNull(finisher)
      require(
        lineSeparator != null && !lineSeparator.nonEmpty,
        "line separator cannot be null or empty",
      )
      return ri =>
        BodySubscribers.fromLineSubscriber(
          subscriber,
          finisher,
          charsetFrom(ri.headers()),
          lineSeparator,
        )

    def discarding(): BodyHandler[Void] =
      _ => BodySubscribers.discarding()

    def replacing[U](value: U): BodyHandler[U] =
      requireNonNull(value)
      _ => BodySubscribers.replacing(value)

    def ofString(charset: Charset): BodyHandler[String] =
      requireNonNull(charset)
      _ => BodySubscribers.ofString(charset)

    def ofFile(file: Path, openOptions: OpenOption*): BodyHandler[Path] =
      requireNonNull(file)
      require(
        !openOptions.contains(DELETE_ON_CLOSE) && !openOptions.contains(READ),
        s"invalid openOptions: $openOptions",
      )
      BodyHandlersImpl.ofFile(file, openOptions)

    def ofFile(file: Path): BodyHandler[Path] =
      ofFile(file, CREATE, WRITE)

    def ofFileDownload(directory: Path, openOptions: OpenOption*): BodyHandler[Path] = {
      requireNonNull(directory)
      try
        directory.toFile.getPath()
      catch {
        case uoe: UnsupportedOperationException =>
          throw new IllegalArgumentException(s"invalid path: $directory", uoe)
      }
      require(!Files.notExists(directory), s"non-existent directory: $directory")
      require(Files.isDirectory(directory), s"not a directory: $directory")
      require(Files.isWritable(directory), s"non-writable directory: $directory")

      require(!openOptions.contains(DELETE_ON_CLOSE), s"invalid openOptions: ${openOptions}")

      return BodyHandlersImpl.ofFileDownload(directory, openOptions*)
    }

    def ofInputStream(): BodyHandler[InputStream] =
      _ => BodySubscribers.ofInputStream()

    def ofLines(): BodyHandler[Stream[String]] =
      ri => BodySubscribers.ofLines(charsetFrom(ri.headers()))

    def ofByteArrayConsumer(consumer: Consumer[Optional[Array[Byte]]]): BodyHandler[Void] =
      _ => BodySubscribers.ofByteArrayConsumer(consumer)

    def ofByteArray(): BodyHandler[Array[Byte]] = _ => BodySubscribers.ofByteArray()

    def ofString(): BodyHandler[String] = ri => BodySubscribers.ofString(charsetFrom(ri.headers()))

    def ofPublisher(): BodyHandler[Publisher[JList[ByteBuffer]]] = _ =>
      BodySubscribers.ofPublisher()

    def buffering[T](downstreamHandler: BodyHandler[T], bufferSize: Int): BodyHandler[T] =
      require(bufferSize > 0, "must be greater than 0")
      ri => BodySubscribers.buffering(downstreamHandler(ri), bufferSize)

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
      BodyHandlersImpl.PushPromisesHandlerWithMap(pushPromiseHandler, pushPromisesMap)
  }

  /// @since 11
  trait BodySubscriber[T] extends Subscriber[JList[ByteBuffer]] {
    def getBody(): CompletionStage[T]
  }

  abstract class BodySubscribers {}
  object BodySubscribers {
    def fromSubscriber(
        subscriber: Subscriber[? >: JList[ByteBuffer]],
    ): BodySubscriber[Void] =
      new BodySubscribersImpl.SubscriberAdapter(subscriber, _ => null)

    def fromSubscriber[S <: Subscriber[? >: JList[ByteBuffer]], T](
        subscriber: S,
        finisher: Function[? >: S, ? <: T],
    ): BodySubscriber[T] =
      new BodySubscribersImpl.SubscriberAdapter(subscriber, finisher)

    def fromLineSubscriber(subscriber: Subscriber[? >: String]): BodySubscriber[Void] =
      fromLineSubscriber(subscriber, (_: Any) => null, UTF_8, null)

    def fromLineSubscriber[S <: Subscriber[? >: String], T](
        subscriber: S,
        finisher: Function[? >: S, ? <: T],
        charset: Charset,
        lineSeparator: String,
    ): BodySubscriber[T] = BodySubscribersImpl.fromLineSubscriber(
      subscriber,
      finisher,
      charset,
      lineSeparator,
    )

    def ofString(charset: Charset): BodySubscriber[String] =
      BodySubscribersImpl.ofByteArray(bytes => new String(bytes, charset))

    def ofByteArray(): BodySubscriber[Array[Byte]] =
      BodySubscribersImpl.ofByteArray(identity)

    def ofFile(
        file: Path,
        openOptions: OpenOption*,
    ): BodySubscriber[Path] =
      BodySubscribersImpl.ofFile(file, openOptions)

    def ofFile(file: Path): BodySubscriber[Path] =
      ofFile(file, CREATE, WRITE)

    def ofByteArrayConsumer(
        consumer: Consumer[Optional[Array[Byte]]],
    ): BodySubscriber[Void] =
      BodySubscribersImpl.ofByteArrayConsumer(consumer)

    def ofInputStream(): BodySubscriber[InputStream] =
      BodySubscribersImpl.ofInputStream()

    def ofLines(charset: Charset): BodySubscriber[Stream[String]] =
      BodySubscribersImpl.ofLines(charset)

    def ofPublisher(): BodySubscriber[Publisher[JList[ByteBuffer]]] =
      BodySubscribersImpl.ofPublisher()

    def replacing[U](value: U): BodySubscriber[U] =
      BodySubscribersImpl.replacing(value)

    def discarding(): BodySubscriber[Void] =
      BodySubscribersImpl.discarding()

    def buffering[T](downstream: BodySubscriber[T], bufferSize: Int): BodySubscriber[T] = {
      require(bufferSize > 0, "must be greater than 0")
      BodySubscribersImpl.BufferingSubscriber(downstream, bufferSize)
    }

    def mapping[T, U](
        upstream: BodySubscriber[T],
        mapper: Function[? >: T, ? <: U],
    ): BodySubscriber[U] =
      BodySubscribersImpl.MappingSubscriber(upstream, mapper)
  }

}
