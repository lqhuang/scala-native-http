package snhttp.core

type Method =
  "GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "CONNECT" | "OPTIONS" | "TRACE" | "PATCH"

object Method {
  val METHODS = Set[Method](
    "GET",
    "HEAD",
    "POST",
    "PUT",
    "DELETE",
    "CONNECT",
    "OPTIONS",
    "TRACE",
    "PATCH",
  )

  inline def apply(value: Method | String): Option[Method] =
    val striped = value.trim().toUpperCase()
    if METHODS.contains(striped.asInstanceOf[Method])
    then Some(striped.asInstanceOf[Method])
    else None
}
