package snhttp.jdk.net.http

import java.io.{IOException, UncheckedIOException}
import java.net.URI
import java.net.http.{HttpResponse, HttpRequest}
import java.net.http.HttpResponse.{BodyHandler, ResponseInfo, BodySubscriber}
import java.nio.file.{OpenOption, Path}
import java.util.concurrent.{CompletableFuture, ConcurrentMap}
import java.util.function.Function

import snhttp.jdk.net.http.internal.Utils.filenameFrom

object BodyHandlersImpl:

  def ofFile(file: Path, openOptions: Array[OpenOption]): PathBodyHandler =
    new PathBodyHandler(file, openOptions*)

  def ofFileDownload(directory: Path, openOptions: Array[OpenOption]): BodyHandler[Path] =
    new FileDownloadBodyHandler(directory, openOptions*)

  /// Implements the `PushPromiseHandler` interface from `HttpResponse`
  case class PushPromisesHandlerWithMap[T](
      pushPromiseHandler: Function[HttpRequest, BodyHandler[T]],
      pushPromisesMap: ConcurrentMap[HttpRequest, CompletableFuture[HttpResponse[T]]],
  ) extends HttpResponse.PushPromiseHandler[T]:

    def applyPushPromise(
        initiatingRequest: HttpRequest,
        pushPromiseRequest: HttpRequest,
        acceptor: Function[BodyHandler[T], CompletableFuture[HttpResponse[T]]],
    ): Unit =
      val bodyHandler = pushPromiseHandler.apply(pushPromiseRequest)
      val responseFuture = acceptor.apply(bodyHandler)
      pushPromisesMap.put(pushPromiseRequest, responseFuture): Unit

  end PushPromisesHandlerWithMap

end BodyHandlersImpl

private[snhttp] class PathBodyHandler(
    file: Path,
    openOptions: OpenOption*,
) extends BodyHandler[Path]:

  override def apply(responseInfo: ResponseInfo): BodySubscriber[Path] =
    new PathBodySubscriber(file, openOptions*)

end PathBodyHandler

private[snhttp] class FileDownloadBodyHandler(
    directory: Path,
    openOptions: OpenOption*,
) extends BodyHandler[Path]:

  /**
   * Notes from JDK docs:
   *
   * The Content-Disposition header must specify the attachment type and must also contain a
   * filename parameter.
   */
  override def apply(responseInfo: ResponseInfo): BodySubscriber[Path] =
    filenameFrom(responseInfo.headers())
      .map { name =>
        val targetFile = directory.resolve(name)
        new PathBodySubscriber(targetFile, openOptions*)
      }
      .orElseThrow(() =>
        new UncheckedIOException(new IOException("Bad Content-Disposition header")),
      )

end FileDownloadBodyHandler
