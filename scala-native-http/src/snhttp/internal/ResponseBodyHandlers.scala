package snhttp.internal

import java.io.{IOException, UncheckedIOException}
import java.util.concurrent.{CompletableFuture, ConcurrentMap}
import java.net.URI
import java.net.http.HttpResponse
import java.net.http.HttpRequest
import java.net.http.HttpResponse.{BodyHandler, ResponseInfo, BodySubscriber}
import java.nio.file.{OpenOption, Files, Path, Paths}
import java.util.{List, Set}
import java.util.function.Function
import java.util.regex.{Matcher, Pattern}
import java.util.regex.Pattern.CASE_INSENSITIVE

import snhttp.internal.ResponseSubscribers.PathSubscriber

object ResponseBodyHandlers {

  /** A Path body handler. */
  class PathBodyHandler(
      private val file: Path,
      private val openOptions: List[OpenOption],
  ) extends BodyHandler[Path] {
    override def apply(responseInfo: ResponseInfo): BodySubscriber[Path] =
      return new PathSubscriber(file, openOptions);
  }
  object PathBodyHandler {
    def create(file: Path, openOptions: List[OpenOption]): PathBodyHandler =
      return new PathBodyHandler(file, openOptions);
  }

  class PushPromisesHandlerWithMap[T](
      pushPromiseHandler: Function[HttpRequest, BodyHandler[T]],
      pushPromisesMap: ConcurrentMap[HttpRequest, CompletableFuture[HttpResponse[T]]],
  ) extends HttpResponse.PushPromiseHandler[T] {
    import FileDownloadBodyHandler.*

    override def applyPushPromise(
        initiatingRequest: HttpRequest,
        pushRequest: HttpRequest,
        acceptor: BodyHandler[T] => CompletableFuture[HttpResponse[T]],
    ): Unit = ???
  }

  class FileDownloadBodyHandler(directory: Path, openOptions: List[OpenOption])
      extends BodyHandler[Path] {
    override def apply(responseInfo: ResponseInfo): BodySubscriber[Path] = ???
  }

  object FileDownloadBodyHandler {
    val DISPOSITION_TYPE = "attachment;"
    val FILENAME = Pattern.compile("filename\\s*=\\s*", CASE_INSENSITIVE)
    val PROHIBITED = List.of(".", "..", "", "~", "|")
    val NOT_ALLOWED_IN_TOKEN = Set.of('(', ')', '<', '>', '@', ',', ';', ':', '\\', '"', '/', '[',
      ']', '?', '=', '{', '}', ' ', '\t')

    /** Factory for creating FileDownloadBodyHandler. */
    def create(directory: Path, openOptions: List[OpenOption]): FileDownloadBodyHandler = {
      try
        directory.toFile.getPath()
      catch {
        case uoe: UnsupportedOperationException =>
          throw new IllegalArgumentException(s"invalid path: $directory", uoe)
      }

      if (Files.notExists(directory))
        throw new IllegalArgumentException(s"non-existent directory: $directory")
      if (!Files.isDirectory(directory))
        throw new IllegalArgumentException(s"not a directory: $directory")
      if (!Files.isWritable(directory))
        throw new IllegalArgumentException(s"non-writable directory: $directory")

      return new FileDownloadBodyHandler(directory, openOptions)
    }

    def allowedInToken(c: Char): Boolean = !NOT_ALLOWED_IN_TOKEN.contains(c) && isTokenText(c)

    def unchecked(rinfo: ResponseInfo, msg: String): UncheckedIOException =
      new UncheckedIOException(
        new IOException(s"$msg in response [${rinfo.statusCode()}, ${rinfo.headers()}]"),
      )

    def unchecked(msg: String): UncheckedIOException =
      new UncheckedIOException(new IOException(msg))

    def processFilename(src: String): String =
      if (src == "") src
      else if (src.charAt(0) == '"') processQuotedString(src.substring(1))
      else processToken(src)

    def isTokenText(c: Char): Boolean = c > 31 && c < 127

    def isQuotedStringText(c: Char): Boolean = c > 31

    def processQuotedString(src: String): String = ???

    def processToken(src: String): String = {
      var end = 0
      val len = src.length
      var whitespace = false
      var i = 0
      while (i < len) {
        val c = src.charAt(i)
        if (c == ';') {
          i = len
        } else if (c == ' ' || c == '\t') {
          whitespace = true
        } else {
          end += 1
          if (whitespace || !allowedInToken(c)) {
            val msg =
              if (whitespace) "whitespace must be followed by a semicolon"
              else s"$c is not allowed in a token"
            throw unchecked(msg)
          }
        }
        i += 1
      }
      src.substring(0, end)
    }
  }

}
