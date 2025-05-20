package java.net.http

import java.net.URI
import java.time.Duration
import java.util.function.BiPredicate
import java.io.Closeable

trait HttpRequest extends Closeable {
  import HttpRequest.BodyPublisher

  def method(): String

  def bodyPublisher(): Option[HttpRequest.BodyPublisher]

  def timeout(): Option[Duration]
}

object HttpRequest {

  trait Builder {
    def uri(uri: URI): Builder

    def expectContinue(enable: Boolean): Builder

    def version(version: HttpClient.Version): Builder

    def header(name: String, value: String): Builder

    def headers(headers: String*): Builder

    def timeout(duration: Duration): Builder

    def setHeader(name: String, value: String): Builder

    def GET(): Builder

    def POST(bodyPublisher: BodyPublisher): Builder

    def PUT(bodyPublisher: BodyPublisher): Builder

    def DELETE(): Builder

    def method(method: String, bodyPublisher: BodyPublisher): Builder

    def build(): HttpRequest

    def copy(): Builder
  }

  trait BodyPublisher

  trait BodyPublishers {
    def noBody(): BodyPublisher
  }

  def newBuilder(): Builder = ???

  def newBuilder(uri: URI): Builder = ???

  def newBuilder(request: HttpRequest, filter: BiPredicate[String, String]): Builder = ???

}
