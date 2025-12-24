package java.net.http

import java.lang.Long as JLong
import java.util.Locale
import java.util.List as JList
import java.util.Map as JMap
import java.util.{Collections, Optional, OptionalLong, TreeMap, TreeSet}
import java.util.Objects.requireNonNull
import java.util.function.BiPredicate

/// @since 11
final class HttpHeaders private (headers: JMap[String, JList[String]]):

  def allValues(name: String): JList[String] =
    headers.getOrDefault(name.toLowerCase(Locale.ROOT), JList.of())

  def firstValue(name: String): Optional[String] =
    allValues(name).stream().findFirst()

  def firstValueAsLong(name: String): OptionalLong =
    allValues(name).stream().mapToLong(JLong.parseLong).findFirst()

  def map(): JMap[String, JList[String]] = Collections.unmodifiableMap(headers)

  /// Two HTTP headers are equal if each of their corresponding maps are equal.
  final override def equals(obj: Any): Boolean = obj match {
    case other: HttpHeaders => headers == other.map()
    case _                  => false
  }

  final override def hashCode(): Int =
    headers
      .entrySet()
      .stream()
      .mapToInt { entry =>
        val keyHash = entry.getKey.toLowerCase(Locale.ROOT).hashCode
        val valueHash = entry.getValue.hashCode
        keyHash ^ valueHash
      }
      .sum()

  override def toString: String = s"${super.toString()} { ${map()} }"

object HttpHeaders:

  val NO_HEADERS = new HttpHeaders(JMap.of())

  def of(
      headerMap: JMap[String, JList[String]],
      filter: BiPredicate[String, String],
  ): HttpHeaders = {
    requireNonNull(headerMap)
    requireNonNull(filter)

    val newHeaderMap = new TreeMap[String, JList[String]](String.CASE_INSENSITIVE_ORDER)

    headerMap.entrySet().forEach { entry =>
      val key = entry.getKey()
      val values = entry.getValue()

      requireNonNull(key)
      requireNonNull(values, s"values for key '${key}' can not be null")
      require(!values.isEmpty, s"empty values for key '${key}'")

      val headerKey = key.trim()
      require(!headerKey.isEmpty, "empty key")

      if (newHeaderMap.containsKey(headerKey))
        throw new IllegalArgumentException(s"duplicate header name: '${headerKey}'")

      val headerValues = values
        .stream()
        .map { s =>
          requireNonNull(s, s"header value can not be null")
          val trimedValue = requireNonNull(s).trim()
          require(!trimedValue.isEmpty, s"empty value for key '${headerKey}'")
          trimedValue
        }
        .filter { s =>
          val r = filter.test(headerKey, s)
          // println(s"filter header: ${headerKey} -> ${s}, result: ${r}")
          r
        }
        .toList()
      // TODO: bug? even if test returns false, the headerValues will not be empty
      // println(s"headerValues = ${headerValues.size()}")

      if (!headerValues.isEmpty) newHeaderMap.put(headerKey, headerValues): Unit
    }

    if newHeaderMap.isEmpty
    then NO_HEADERS
    else new HttpHeaders(newHeaderMap)
  }

end HttpHeaders
