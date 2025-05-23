package java.net.http

import java.lang.Long as JLong
import java.util.{Locale, Optional, OptionalLong, List, Map}
import java.util.Objects.requireNonNull
import java.util.function.BiPredicate

import scala.collection.immutable.{TreeMap, TreeSet}
import scala.jdk.CollectionConverters.{MapHasAsScala, ListHasAsScala, MapHasAsJava, BufferHasAsJava}

import Ordering.comparatorToOrdering
import String.CASE_INSENSITIVE_ORDER

/// @since 11
final class HttpHeaders(private val headers: Map[String, List[String]]) {

  def allValues(name: String): List[String] =
    Option(headers.get(name)).getOrElse(List.of())

  def firstValue(name: String): Optional[String] =
    allValues(name).stream().findFirst()

  def firstValueAsLong(name: String): OptionalLong =
    allValues(name).stream().mapToLong(JLong.parseLong).findFirst()

  def map(): Map[String, List[String]] = headers

  /// Two HTTP headers are equal if each of their corresponding maps are equal.
  final override def equals(obj: Any): Boolean = obj match {
    case other: HttpHeaders => this.map() == other.map()
    case _                  => false
  }

  final override def hashCode(): Int =
    headers.asScala.map {
      case (key, value) =>
        val keyHash = key.toLowerCase(Locale.ROOT).hashCode
        val valueHash = value.hashCode
        keyHash ^ valueHash
    }.sum

  override def toString: String = s"${super.toString()} { ${map()} }"

}

object HttpHeaders {

  private val NO_HEADERS = new HttpHeaders(Map.of())

  val httpOrdering: Ordering[String] = comparatorToOrdering(CASE_INSENSITIVE_ORDER)

  def of(
      headerMap: Map[String, List[String]],
      filter: BiPredicate[String, String],
  ): HttpHeaders = {
    require(
      headerMap != null && filter != null,
      "headerMap and filter must not be null",
    )

    val headerNames = headerMap.asScala.keys.toSeq.map(_.trim()).filter(_.nonEmpty)
    require(!headerNames.isEmpty, "empty key")
    require(
      headerNames.distinct.size == headerNames.size,
      s"duplicate key: ${headerNames.mkString(",")}",
    )

    val newHeaderMap = headerMap.asScala.map {
      case (key, values) =>
        val headerName = key.trim() // tested in the previous step
        val headerValues = values.asScala
          .map(s => requireNonNull(s).trim())
          .filter(s => s.nonEmpty && filter.test(headerName, s))
        require(!headerValues.isEmpty, s"empty values")
        (headerName, headerValues.asJava)
    }

    if newHeaderMap.isEmpty
    then NO_HEADERS
    else new HttpHeaders(newHeaderMap.asJava)
  }

}
