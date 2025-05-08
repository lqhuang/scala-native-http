package java.net.http

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._
import scala.collection.mutable
import scala.concurrent.duration._
import scala.scalanative.libc.stdlib._
import scala.scalanative.libc.string._
import scala.scalanative.runtime.{
  ByteArray,
  Intrinsics,
  LongArray,
  MemoryPool,
  fromRawPtr,
  toRawPtr,
}
import scala.scalanative.loop._
import snhttp._
import snhttp.internal._
import scala.annotation.tailrec
import scala.concurrent._

import java.net.URI

class HttpRequest private (
    val uri: URI,
    val method: String,
    val headers: Map[String, String],
    val body: Option[String],
) {

  def getUri: URI = uri

  def getMethod: String = method

  def getHeaders: Map[String, String] = headers

  def getBody: Option[String] = body

  override def toString: String =
    s"HttpRequest(method=$method, uri=$uri, headers=$headers, body=$body)"
}

object HttpRequest {
  def builder(): HttpRequestBuilder = new HttpRequestBuilder()

  class HttpRequestBuilder {
    private var uri: URI = _
    private var method: String = "GET"
    private var headers: Map[String, String] = Map()
    private var body: Option[String] = None

    def setUri(uri: URI): HttpRequestBuilder = {
      this.uri = uri
      this
    }

    def setMethod(method: String): HttpRequestBuilder = {
      this.method = method
      this
    }

    def addHeader(name: String, value: String): HttpRequestBuilder = {
      headers += (name -> value)
      this
    }

    def setBody(body: String): HttpRequestBuilder = {
      this.body = Some(body)
      this
    }

    def build(): HttpRequest = {
      if (uri == null) throw new IllegalArgumentException("URI must not be null")
      new HttpRequest(uri, method, headers, body)
    }
  }
}

class HttpRequest private (handle: Ptr[Byte]) {
  import CurlImpl._
  import CApi._
  import CApiOps._
  private[snhttp] var callback: Response => Unit = null
  private[snhttp] val memory: Ptr[Memory] =
    malloc(sizeof[Memory]).asInstanceOf[Ptr[Memory]]
  private[snhttp] var headersList: Ptr[CurlSList] = null
  memory._1 = malloc(0.toULong)
  memory._2 = 0.toULong
  curl_easy_setopt(
    handle,
    CURLOPT_WRITEDATA,
    memory.asInstanceOf[Ptr[Byte]],
  )
  curl_easy_setopt(
    handle,
    CURLOPT_WRITEFUNCTION,
    writeMemoryCallback,
  )
  HandleUtils.setData(handle, this)
  def method(value: Method): Request = Zone { implicit z =>
    curl_easy_setopt(
      handle,
      CURLOPT_CUSTOMREQUEST,
      toCString(value.name),
    )
    this
  }
  def url(value: String): Request = {
    Zone { implicit z =>
      curl_easy_setopt(handle, CURLOPT_URL, toCString(value))
    }
    this
  }
  def body(value: String): Request = Zone { implicit z =>
    curl_easy_setopt(
      handle,
      CURLOPT_COPYPOSTFIELDS,
      toCString(value),
    )
    this
  }
  def header(value: String): Request = Zone { implicit z =>
    headersList = curl_slist_append(headersList, toCString(value))
    this
  }
  private def setCallback(value: Response => Unit): Request = {
    this.callback = value
    this
  }
  private def perform(): Unit = {
    curl_easy_setopt(
      handle,
      CURLOPT_HTTPHEADER,
      headersList.asInstanceOf[Ptr[Byte]],
    )
    curl_multi_add_handle(curlHandle, handle)
  }
  def future(): Future[Response] = {
    val p = Promise[Response]()
    setCallback(p.success(_))
    perform()
    p.future
  }
  def callback(value: Response => Unit): Unit = {
    setCallback(value)
    perform()
  }
}

object HttpRequest {
  import CApi._
  def apply(): Request = new Request(curl_easy_init())
}
