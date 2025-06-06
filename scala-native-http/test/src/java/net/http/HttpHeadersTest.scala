import java.net.http.HttpHeaders
import java.util.{List as JList, Map as JMap, TreeMap}
import java.util.function.BiPredicate

class HttpHeadersTest extends munit.FunSuite {

  val accpetAllFilter: BiPredicate[String, String] = (_, _) => true
  val exampleHeaders: HttpHeaders = HttpHeaders.of(
    JMap.of(
      "Host",
      JList.of("example.com"),
      "Accept",
      JList.of("text/html", "application/json"),
      "Content-Type",
      JList.of("application/xml"),
    ),
    accpetAllFilter,
  )

  test("HttpHeaders.of returns NO_HEADERS for empty header map") {
    val headerMap: JMap[String, JList[String]] = JMap.of()
    val headers = HttpHeaders.of(headerMap, accpetAllFilter)
    assertEquals(headers.map().size(), 0)
  }

  test("HttpHeaders.of throws exception for null headerMap") {
    intercept[NullPointerException] {
      HttpHeaders.of(null, accpetAllFilter)
    }
  }

  test("HttpHeaders.of throws exception for null filter") {
    val headerMap = JMap.of("Host", JList.of("example.com"))
    intercept[NullPointerException] {
      HttpHeaders.of(headerMap, null)
    }
  }

  test("HttpHeaders.of throws exception for null header values") {
    val headerMap = new TreeMap[String, JList[String]]()
    headerMap.put("Host", null)
    intercept[NullPointerException] {
      HttpHeaders.of(headerMap, accpetAllFilter)
    }
  }

  test("HttpHeaders.of throws exception for empty header key") {
    val headerMap = JMap.of("", JList.of("value"))
    intercept[IllegalArgumentException] {
      HttpHeaders.of(headerMap, accpetAllFilter)
    }
  }

  test("HttpHeaders.of throws exception for whitespace-only header key") {
    val headerMap = JMap.of("    ", JList.of("value"))
    intercept[IllegalArgumentException] {
      HttpHeaders.of(headerMap, accpetAllFilter)
    }
  }

  test("HttpHeaders.of throws exception for duplicate header names (case insensitive)") {
    val headerMap = new TreeMap[String, JList[String]]()
    headerMap.put("Host", JList.of("example.com"))
    headerMap.put("HOST", JList.of("different.com"))

    intercept[IllegalArgumentException] {
      HttpHeaders.of(headerMap, accpetAllFilter)
    }
  }

  test(
    "HttpHeaders.of throws exception for whitespace and duplicate header names (case insensitive)",
  ) {
    val headerMap = new TreeMap[String, JList[String]]()
    headerMap.put("Host  ", JList.of("example.com"))
    headerMap.put("HOST", JList.of("different.com"))

    intercept[IllegalArgumentException] {
      HttpHeaders.of(headerMap, accpetAllFilter)
    }
  }

  test("HttpHeaders.of throws exception for empty header values list") {
    val headerMap = JMap.of("Host", JList.of[String]())
    intercept[IllegalArgumentException] {
      HttpHeaders.of(headerMap, accpetAllFilter)
    }
  }

  test("HttpHeaders.of throws exception for header values list with empty value") {
    val headerMap = JMap.of("Host", JList.of[String]("   "))
    intercept[IllegalArgumentException] {
      HttpHeaders.of(headerMap, accpetAllFilter)
    }
  }

  test("HttpHeaders.of throws exception for header values list with any empty value") {
    val headerMap = JMap.of("Host", JList.of[String]("localhost", "   "))
    intercept[IllegalArgumentException] {
      HttpHeaders.of(headerMap, accpetAllFilter)
    }
  }

  test("HttpHeaders.of trims header keys and values and") {
    val headerMap = JMap.of("  Host  ", JList.of("  example.com  "))
    val headers = HttpHeaders.of(headerMap, accpetAllFilter)
    val value = headers.firstValue("Host")
    assertEquals(value.get(), "example.com")
  }

  test("HttpHeaders.of retains case for header keys and values") {
    val headerMap = JMap.of("Host", JList.of("eXample.com"))
    val headers = HttpHeaders.of(headerMap, accpetAllFilter)
    assertEquals(headers.map().size(), 1)
    assertEquals(headers.firstValue("Host").get(), "eXample.com")
  }

  test("HttpHeaders.of filters header keys based on predicate") {
    val headerMap: JMap[String, JList[String]] = JMap.of(
      "Accept",
      JList.of("text/html", "application/json"),
      "Content-Type",
      JList.of("application/xml"),
    )
    val filter: BiPredicate[String, String] = (key, _) => key.toLowerCase().contains("accept")
    val headers = HttpHeaders.of(headerMap, filter)
    assertEquals(headers.map().size(), 1)
    val values = headers.allValues("Accept")
    assertEquals(values.get(0), "text/html")
  }

  test("HttpHeaders.of filters header values based on predicate") {
    val headerMap = JMap.of("Accept", JList.of("text/html", "application/json"))
    val filter: BiPredicate[String, String] = (_, value) => value.contains("json")
    val headers = HttpHeaders.of(headerMap, filter)
    val values = headers.allValues("Accept")
    assertEquals(values.size(), 1)
    assertEquals(values.get(0), "application/json")
  }

  test("HttpHeaders.of headers can not construct when all values are filtered out") {
    val headerMap = JMap.of("Accept", JList.of("text/html"))
    val filter: BiPredicate[String, String] = (_, value) => false

    intercept[IllegalArgumentException] {
      HttpHeaders.of(headerMap, filter)
    }

  }

  /**
   * TODO: add test cases for system properties `java.net.http.HttpHeaders.allowDuplicateHeaders`
   */

  test("hashCode is consistent with equals") {
    val headerMap = JMap.of("Host", JList.of("example.com"))
    val headers1 = HttpHeaders.of(headerMap, accpetAllFilter)
    val headers2 = HttpHeaders.of(headerMap, accpetAllFilter)
    assertEquals(headers1.hashCode(), headers2.hashCode())
  }

  test("case insensitive header name access") {
    val headerMap = JMap.of("Content-Type", JList.of("application/json"))
    val headers = HttpHeaders.of(headerMap, accpetAllFilter)
    assertEquals(headers.firstValue("content-type").get(), "application/json")
    assertEquals(headers.firstValue("CONTENT-TYPE").get(), "application/json")
  }

  test("allValues returns all values for existing header") {
    val headerMap = JMap.of("Content-Type", JList.of("application/json", "charset=utf-8"))
    val headers = HttpHeaders.of(headerMap, accpetAllFilter)
    val values = headers.allValues("Content-Type")
    assertEquals(values.size(), 2)
    assertEquals(values.get(0), "application/json")
    assertEquals(values.get(1), "charset=utf-8")
  }

  test("firstValue returns empty optional for non-existent header") {
    val value = exampleHeaders.firstValue("non-existent")
    assertEquals(value.isPresent, false)
  }

  test("firstValue returns first value for existing header") {
    val headerMap = JMap.of("Accept", JList.of("text/html", "application/xml"))
    val headers = HttpHeaders.of(headerMap, accpetAllFilter)
    val value = headers.firstValue("Accept")
    assertEquals(value.isPresent, true)
    assertEquals(value.get(), "text/html")
  }

  test("firstValueAsLong parses numeric header value") {
    val headerMap = JMap.of("Content-Length", JList.of("1234"))
    val headers = HttpHeaders.of(headerMap, accpetAllFilter)
    val value = headers.firstValueAsLong("Content-Length")
    assertEquals(value.isPresent, true)
    assertEquals(value.getAsLong, 1234L)
  }

  test("firstValueAsLong throws exception for non-numeric value") {
    val headerMap = JMap.of("Content-Type", JList.of("text/plain"))
    val headers = HttpHeaders.of(headerMap, accpetAllFilter)
    intercept[NumberFormatException] {
      headers.firstValueAsLong("Content-Type")
    }
  }

  test("equals returns true for identical headers") {
    val headerMap = JMap.of("Host", JList.of("example.com"))
    val headers1 = HttpHeaders.of(headerMap, accpetAllFilter)
    val headers2 = HttpHeaders.of(headerMap, accpetAllFilter)
    assertEquals(headers1, headers2)
  }

  test("equals returns false for different headers") {
    val headerMap1 = JMap.of("Host", JList.of("example.com"))
    val headerMap2 = JMap.of("Host", JList.of("different.com"))

    val headers1 = HttpHeaders.of(headerMap1, accpetAllFilter)
    val headers2 = HttpHeaders.of(headerMap2, accpetAllFilter)
    assertNotEquals(headers1, headers2)
  }

}
