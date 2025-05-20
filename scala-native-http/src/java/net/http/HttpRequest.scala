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

// class HttpRequest private (handle: Ptr[Byte]) {
//   import CurlImpl._
//   import CApi._
//   import CApiOps._
//   private[snhttp] var callback: Response => Unit = null
//   private[snhttp] val memory: Ptr[Memory] =
//     malloc(sizeof[Memory]).asInstanceOf[Ptr[Memory]]
//   private[snhttp] var headersList: Ptr[CurlSList] = null
//   memory._1 = malloc(0.toULong)
//   memory._2 = 0.toULong
//   curl_easy_setopt(
//     handle,
//     CURLOPT_WRITEDATA,
//     memory.asInstanceOf[Ptr[Byte]],
//   )
//   curl_easy_setopt(
//     handle,
//     CURLOPT_WRITEFUNCTION,
//     writeMemoryCallback,
//   )
//   HandleUtils.setData(handle, this)
//   def method(value: Method): Request = Zone { implicit z =>
//     curl_easy_setopt(
//       handle,
//       CURLOPT_CUSTOMREQUEST,
//       toCString(value.name),
//     )
//     this
//   }
//   def url(value: String): Request = {
//     Zone { implicit z =>
//       curl_easy_setopt(handle, CURLOPT_URL, toCString(value))
//     }
//     this
//   }
//   def body(value: String): Request = Zone { implicit z =>
//     curl_easy_setopt(
//       handle,
//       CURLOPT_COPYPOSTFIELDS,
//       toCString(value),
//     )
//     this
//   }
//   def header(value: String): Request = Zone { implicit z =>
//     headersList = curl_slist_append(headersList, toCString(value))
//     this
//   }
//   private def setCallback(value: Response => Unit): Request = {
//     this.callback = value
//     this
//   }
//   private def perform(): Unit = {
//     curl_easy_setopt(
//       handle,
//       CURLOPT_HTTPHEADER,
//       headersList.asInstanceOf[Ptr[Byte]],
//     )
//     curl_multi_add_handle(curlHandle, handle)
//   }
//   def future(): Future[Response] = {
//     val p = Promise[Response]()
//     setCallback(p.success(_))
//     perform()
//     p.future
//   }
//   def callback(value: Response => Unit): Unit = {
//     setCallback(value)
//     perform()
//   }
// }
