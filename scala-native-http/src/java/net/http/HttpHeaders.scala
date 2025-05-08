package java.net.http

import java.{util => ju}

import java.util.{Locale, Optional, OptionalLong}

import java.util.function.BiPredicate
import scala.jdk.CollectionConverters._
import scala.collection.immutable.{TreeMap, TreeSet}

/**
 * A read-only view of a set of HTTP headers.
 *
 * An `HttpHeaders` is not typically created directly, but rather returned from an
 * [[HttpRequest.headers()]] or an [[HttpResponse.headers()]]. Specific HTTP headers can be set for
 * a request through one of the request builder's [[HttpRequest.Builder.header]] methods.
 *
 * The methods of this class (that accept a String header name), and the Map returned by the
 * [[map()]] method, operate without regard to case when retrieving the header value(s).
 *
 * An HTTP header name may appear more than once in the HTTP protocol. As such, headers are
 * represented as a name and a list of values. Each occurrence of a header value is added verbatim,
 * to the appropriate header name list, without interpreting its value.
 *
 * `HttpHeaders` instances are immutable.
 *
 * @since 11
 */
final class HttpHeaders private (private val headers: Map[String, ju.List[String]]) {

  /**
   * Returns an Optional containing the first header string value of the given named header.
   *
   * @param name
   *   the header name
   * @return
   *   an Optional[String] containing the first named header string value, if present
   */
  def firstValue(name: String): Optional[String] =
    Optional.ofNullable(headers.headOption.flatMap(xs => xs._2).orNull)

  /**
   * Returns an OptionalLong containing the first header string value parsed as Long.
   *
   * @param name
   *   the header name
   * @return
   *   an OptionalLong
   * @throws NumberFormatException
   *   if value found but doesn't parse as Long
   */
  def firstValueAsLong(name: String): OptionalLong = {
    val value = firstValue(name)
    if (value.isPresent) OptionalLong.of(value.get.toLong)
    else OptionalLong.empty()
  }

  /**
   * Returns an unmodifiable List of all header string values for the given name.
   *
   * @param name
   *   the header name
   * @return
   *   a List of header string values
   */
  def allValues(name: String): ju.List[String] =
    Option(headers.get(name)).getOrElse(ju.List.of())

  /**
   * Returns an unmodifiable multi Map view of these HttpHeaders.
   *
   * @return
   *   the Map
   */
  def map(): ju.Map[String, ju.List[String]] = headers.asJava

  override def equals(obj: Any): Boolean = obj match {
    case other: HttpHeaders => this.map().equals(other.map())
    case _                  => false
  }

  override def hashCode(): Int =
    headers.map {
      case (key, value) =>
        val keyHash = key.toLowerCase(Locale.ROOT).hashCode
        val valueHash = value.hashCode
        keyHash ^ valueHash
    }.sum

  override def toString: String =
    s"${super.toString} { ${map()} }"
}

object HttpHeaders {
  private val NoHeaders = new HttpHeaders(Map.empty)

  /**
   * Creates HttpHeaders from a map with header name validation and filtering.
   *
   * @param headerMap
   *   the map containing header names and values
   * @param filter
   *   a filter for header name-value pairs
   * @return
   *   an HttpHeaders instance
   */
  def of(
      headerMap: ju.Map[String, ju.List[String]],
      filter: BiPredicate[String, String],
  ): HttpHeaders = {
    if (headerMap == null || filter == null) throw new NullPointerException()

    val headers = TreeMap.empty[String, ju.List[String]](
      Ordering.comparatorToOrdering(String.CASE_INSENSITIVE_ORDER),
    )
    val notAdded =
      TreeSet.empty[String](Ordering.comparatorToOrdering(String.CASE_INSENSITIVE_ORDER))
    val tempList = new java.util.ArrayList[String]()

    headerMap.asScala.foreach {
      case (key, values) =>
        val headerName = Option(key)
          .map(_.trim)
          .filter(_.nonEmpty)
          .getOrElse(throw new IllegalArgumentException("empty key"))

        val headerValues = Option(values).getOrElse(throw new NullPointerException())

        headerValues.forEach { value =>
          val headerValue = Option(value)
            .map(_.trim)
            .getOrElse(throw new NullPointerException())

          if (filter.test(headerName, headerValue)) {
            tempList.add(headerValue)
          }
        }

        if (tempList.isEmpty) {
          if (
            headers.contains(headerName) || notAdded.contains(headerName.toLowerCase(Locale.ROOT))
          )
            throw new IllegalArgumentException(s"duplicate key: $headerName")
          notAdded += headerName.toLowerCase(Locale.ROOT)
        } else if (headers.contains(headerName)) {
          throw new IllegalArgumentException(s"duplicate key: $headerName")
        } else {
          headers += (headerName -> ju.List.copyOf(tempList))
        }
        tempList.clear()
    }

    if (headers.isEmpty) NoHeaders
    else new HttpHeaders(headers)
  }
}
