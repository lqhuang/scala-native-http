package java.net.http

case class Response(code: Int, body: String)

class HttpResponse[T](val response: HttpResponse[T]) {

  def statusCode(): Int = response.statusCode()

  def headers(): Map[String, List[String]] =
    response.headers().map().asScala.toMap.view.mapValues(_.asScala.toList).toMap

  def body(): T = response.body()

  override def toString: String =
    s"HttpResponse(statusCode=${statusCode()}, headers=${headers()}, body=${body()})"
}
