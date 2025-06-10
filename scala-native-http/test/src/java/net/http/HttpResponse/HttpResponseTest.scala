import java.io.{InputStream, ByteArrayInputStream}
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.net.http.HttpResponse.{BodyHandlers, BodySubscribers}
import java.net.http.{HttpClient, HttpHeaders, HttpRequest, HttpResponse}
import java.nio.file.{Files, Path, Paths}
import java.util.List as JList
import java.util.{ArrayList, Optional, Collections}
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Flow.{Publisher, Subscriber, Subscription}
import java.util.function.{Consumer, Function}
import java.util.stream.Stream

import scala.util.Using

class HttpResponseTest extends munit.FunSuite {

  test("HttpResponse should return correct status code, body, and headers") {}

}
