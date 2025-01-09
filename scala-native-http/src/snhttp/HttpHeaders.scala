package java.net.http

final class HttpHeaders private (headers: Map[String, String]) {
  def get(name: String): Option[String] = headers.get(name)
  def getAll(name: String): List[String] = headers.get(name).toList
  def add(name: String, value: String): HttpHeaders =
    new HttpHeaders(headers + (name -> value))
  def remove(name: String): HttpHeaders =
    new HttpHeaders(headers - name)
}
