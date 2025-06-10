package java.net.http

import java.io.InputStream
import java.net.URI
import java.nio.ByteBuffer
import java.util.List as JList
import java.util.{Optional, Collections, Map as JMap}
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.function.Function
import scala.jdk.CollectionConverters.*

object HttpResponseMocks {

  class MockHttpResponse[T](
      private val _statusCode: Int,
      private val _body: T,
      private val _headers: HttpHeaders = MockHttpHeaders.empty,
      private val _request: HttpRequest = MockHttpRequest.default,
      private val _previousResponse: Optional[HttpResponse[T]] = Optional.empty(),
      private val _uri: URI = URI.create("http://example.com"),
      private val _version: HttpClient.Version = HttpClient.Version.HTTP_1_1,
  ) extends HttpResponse[T] {
    override def statusCode(): Int = _statusCode
    override def connectionLabel(): Optional[String] = Optional.empty()
    override def request(): HttpRequest = _request
    override def previousResponse(): Optional[HttpResponse[T]] = _previousResponse
    override def headers(): HttpHeaders = _headers
    override def body(): T = _body
    override def uri(): URI = _uri
    override def version(): HttpClient.Version = _version
  }

  object MockHttpHeaders {
    val empty: HttpHeaders = HttpHeaders.of(Collections.emptyMap(), null)

    def of(headers: Map[String, String]): HttpHeaders = {
      val javaMap: JMap[String, JList[String]] = headers.map {
        case (k, v) =>
          k -> JList.of(v)
      }.asJava
      HttpHeaders.of(javaMap, null)
    }
  }

  object MockHttpRequest {
    class MockHttpRequestImpl extends HttpRequest {
      override def method(): String = "GET"
      override def uri(): URI = URI.create("http://example.com")
      override def headers(): HttpHeaders = MockHttpHeaders.empty
      override def bodyPublisher(): Optional[HttpRequest.BodyPublisher] = Optional.empty()
      override def timeout(): Optional[java.time.Duration] = Optional.empty()
      override def expectContinue(): Boolean = false
      override def version(): Optional[HttpClient.Version] = Optional.empty()
    }

    val default: HttpRequest = new MockHttpRequestImpl()
  }

  class TestSubscriber[T] extends Subscriber[T] {
    private var subscription: Subscription = _
    private val items = new java.util.ArrayList[T]()
    private var completed = false
    private var error: Throwable = _

    override def onSubscribe(s: Subscription): Unit = {
      subscription = s
      s.request(Long.MaxValue)
    }

    override def onNext(item: T): Unit =
      items.add(item)

    override def onError(throwable: Throwable): Unit =
      error = throwable

    override def onComplete(): Unit =
      completed = true

    def getItems: JList[T] = items
    def isCompleted: Boolean = completed
    def getError: Throwable = error
  }

  class TestResponseInfo(
      private val _statusCode: Int = 200,
      private val _headers: HttpHeaders = MockHttpHeaders.empty,
      private val _version: HttpClient.Version = HttpClient.Version.HTTP_1_1,
  ) extends HttpResponse.ResponseInfo {
    override def statusCode(): Int = _statusCode
    override def headers(): HttpHeaders = _headers
    override def version(): HttpClient.Version = _version
  }
}
