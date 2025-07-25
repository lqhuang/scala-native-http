package snhttp

type MethodLiteral =
  "GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "CONNECT" | "OPTIONS" | "TRACE" | "PATCH"

enum Method(val value: String) {
  case GET extends Method("GET")
  case HEAD extends Method("HEAD")
  case POST extends Method("POST")
  case PUT extends Method("PUT")
  case DELETE extends Method("DELETE")
  case CONNECT extends Method("CONNECT")
  case OPTIONS extends Method("OPTIONS")
  case TRACE extends Method("TRACE")
  case PATCH extends Method("PATCH")

  override def toString: String = value
}

object Method {
  def apply(value: String): Option[Method] =
    values.find(_.value == value)
}
