package snhttp.jdk

import java.net.URI
import java.time.Duration
import java.util.function.BiPredicate
import java.io.Closeable
import java.net.http.HttpRequest.{BodyPublisher, BodyPublishers}

import snhttp.constants.Method

private[net] class HttpRequestImpl extends HttpRequest {}

object HttpRequestImpl {

  class Builder extends HttpRequest.Builder {
    private var uri: URI = null
    private var headers: Map[String, List[String]] = Map.empty
    private var expectContinue: Boolean = false
    private var version: HttpClient.Version = null
    private var timeout: Duration = null
    private var method: String = Method.GET
    private var bodyPublisher: BodyPublisher = BodyPublishers.noBody()

    def uri(uri: URI): Builder = {
      this.uri = uri
      this
    }

    def expectContinue(enable: Boolean): Builder = {
      this.expectContinue = enable
      this
    }

    def version(version: HttpClient.Version): Builder = {
      this.version = version
      this
    }

    def header(name: String, value: String): Builder = {
      val values = headers.getOrElse(name, Nil)
      headers = headers.updated(name, values :+ value)
      this

      // def headers(headers: String*): Builder =
      //   if headers.length % 2 != 0 then
      //     throw new IllegalArgumentException("Headers must be supplied in name-value pairs")

      headers.grouped(2).foreach { pair =>
        header(pair(0), pair(1))
      }
      this
    }

    def timeout(duration: Duration): Builder = {
      val isValidTimeout = duration.isNegative || duration.isZero
      if (!isValidTimeout) throw new IllegalArgumentException("Duration must be positive")

      this.timeout = duration
      this
    }

    def setHeader(name: String, value: String): Builder = {
      headers = headers.updated(name, List(value))
      this
    }

    def GET(): Builder = {
      method = "GET"
      bodyPublisher = BodyPublishers.noBody()
      this
    }

    def POST(bodyPublisher: BodyPublisher): Builder = {
      method = "POST"
      this.bodyPublisher = bodyPublisher
      this
    }

    def PUT(bodyPublisher: BodyPublisher): Builder = {
      method = "PUT"
      this.bodyPublisher = bodyPublisher
      this
    }

    def DELETE(): Builder = {
      method = "DELETE"
      bodyPublisher = BodyPublishers.noBody()
      this
    }

    def method(method: String, bodyPublisher: BodyPublisher): Builder = {
      if (method == null || method.isEmpty)
        throw new IllegalArgumentException("Method must not be null or empty")

      this.method = method
      this.bodyPublisher = bodyPublisher
      this
    }

    def build(): HttpRequest = {
      if (uri == null) throw new IllegalStateException("URI must be set")

      new HttpRequestImpl(
        uri,
        method,
        headers,
        bodyPublisher,
        expectContinue,
        version,
        timeout,
      )
    }

    def copy(): Builder = {
      val newBuilder = new Builder()
      newBuilder.uri = this.uri
      newBuilder.headers = this.headers
      newBuilder.expectContinue = this.expectContinue
      newBuilder.version = this.version
      newBuilder.timeout = this.timeout
      newBuilder.method = this.method
      newBuilder.bodyPublisher = this.bodyPublisher
      newBuilder
    }
  }
}
