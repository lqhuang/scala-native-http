package snhttp.jdk.net.http

import java.io.{IOException, UncheckedIOException}
import java.util.concurrent.{CompletableFuture, ConcurrentMap}
import java.net.URI
import java.net.http.HttpResponse
import java.net.http.HttpRequest
import java.net.http.HttpResponse.{BodyHandler, ResponseInfo, BodySubscriber, BodySubscribers}
import java.nio.file.{OpenOption, Files, Path, Paths}
import java.util.{List, Set}
import java.util.function.Function

import snhttp.jdk.Utils.filenameFrom

object BodyHandlersImpl {

  class PathBodyHandler(
      private val file: Path,
      private val openOptions: Seq[OpenOption],
  ) extends BodyHandler[Path] {
    override def apply(responseInfo: ResponseInfo): BodySubscriber[Path] =
      BodySubscribers.ofFile(file, openOptions*)
  }

  def ofFile(file: Path, openOptions: Seq[OpenOption]): PathBodyHandler =
    new PathBodyHandler(file, openOptions)

  class FileDownloadBodyHandler(
      private val directory: Path,
      private val openOptions: Seq[OpenOption],
  ) extends BodyHandler[Path] {
    override def apply(responseInfo: ResponseInfo): BodySubscriber[Path] = {
      val filename = filenameFrom(responseInfo.headers())
      val targetFile = directory.resolve(filename)
      BodySubscribers.ofFile(targetFile, openOptions*)
    }
  }

  def ofFileDownload(directory: Path, openOptions: OpenOption*): BodyHandler[Path] =
    new FileDownloadBodyHandler(directory, openOptions)

  /// Implements the `PushPromiseHandler` interface from `HttpResponse`
  case class PushPromisesHandlerWithMap[T](
      private val pushPromiseHandler: Function[HttpRequest, BodyHandler[T]],
      private val pushPromisesMap: ConcurrentMap[HttpRequest, CompletableFuture[HttpResponse[T]]],
  ) extends HttpResponse.PushPromiseHandler[T] {
    def applyPushPromise(
        initiatingRequest: HttpRequest,
        pushPromiseRequest: HttpRequest,
        acceptor: Function[BodyHandler[T], CompletableFuture[HttpResponse[T]]],
    ): Unit = {
      val bodyHandler = pushPromiseHandler.apply(pushPromiseRequest)
      val responseFuture = acceptor.apply(bodyHandler)
      pushPromisesMap.put(pushPromiseRequest, responseFuture)
    }
  }

}
