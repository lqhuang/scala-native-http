import java.net.http.HttpHeaders
import java.util.List as JList
import java.util.Map as JMap
import java.util.TreeMap
import java.util.function.BiPredicate

import utest.{Tests, test, assert, assertThrows}

class HttpHeadersTest extends utest.TestSuite {

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

  val tests = Tests {

    test("HttpHeaders.of returns NO_HEADERS for empty header map") {
      val headerMap: JMap[String, JList[String]] = JMap.of()
      val headers = HttpHeaders.of(headerMap, accpetAllFilter)
      assert(headers.map().size() == 0)
    }

    test("HttpHeaders.of throws exception for null headerMap") {
      assertThrows[NullPointerException] {
        HttpHeaders.of(null, accpetAllFilter)
      }
    }

    test("HttpHeaders.of throws exception for null filter") {
      val headerMap = JMap.of("Host", JList.of("example.com"))
      assertThrows[NullPointerException] {
        HttpHeaders.of(headerMap, null)
      }
    }

    test("HttpHeaders.of throws exception for null header values") {
      val headerMap = new TreeMap[String, JList[String]]()
      headerMap.put("Host", null)
      assertThrows[NullPointerException] {
        HttpHeaders.of(headerMap, accpetAllFilter)
      }
    }

    test("HttpHeaders.of throws exception for empty header key") {
      val headerMap = JMap.of("", JList.of("value"))
      assertThrows[IllegalArgumentException] {
        HttpHeaders.of(headerMap, accpetAllFilter)
      }
    }

    test("HttpHeaders.of throws exception for whitespace-only header key") {
      val headerMap = JMap.of("    ", JList.of("value"))
      assertThrows[IllegalArgumentException] {
        HttpHeaders.of(headerMap, accpetAllFilter)
      }
    }

    test("HttpHeaders.of throws exception for duplicate header names (case insensitive)") {
      val headerMap = new TreeMap[String, JList[String]]()
      headerMap.put("Host", JList.of("example.com"))
      headerMap.put("HOST", JList.of("different.com"))

      assertThrows[IllegalArgumentException] {
        HttpHeaders.of(headerMap, accpetAllFilter)
      }
    }

    test(
      "HttpHeaders.of throws exception for whitespace and duplicate header names (case insensitive)",
    ) {
      val headerMap = new TreeMap[String, JList[String]]()
      headerMap.put("Host  ", JList.of("example.com"))
      headerMap.put("HOST", JList.of("different.com"))

      assertThrows[IllegalArgumentException] {
        HttpHeaders.of(headerMap, accpetAllFilter)
      }
    }

    test("HttpHeaders.of throws exception for empty header values list") {
      val headerMap = JMap.of("Host", JList.of[String]())
      assertThrows[IllegalArgumentException] {
        HttpHeaders.of(headerMap, accpetAllFilter)
      }
    }

    test("HttpHeaders.of throws exception for header values list with empty value") {
      val headerMap = JMap.of("Host", JList.of[String]("   "))
      assertThrows[IllegalArgumentException] {
        HttpHeaders.of(headerMap, accpetAllFilter)
      }
    }

    test("HttpHeaders.of throws exception for header values list with any empty value") {
      val headerMap = JMap.of("Host", JList.of[String]("localhost", "   "))
      assertThrows[IllegalArgumentException] {
        HttpHeaders.of(headerMap, accpetAllFilter)
      }
    }

    test("HttpHeaders.of trims header keys and values and") {
      val headerMap = JMap.of("  Host  ", JList.of("  example.com  "))
      val headers = HttpHeaders.of(headerMap, accpetAllFilter)
      val value = headers.firstValue("Host")
      assert(value.get() == "example.com")
    }

    test("HttpHeaders.of retains case for header keys and values") {
      val headerMap = JMap.of("Host", JList.of("eXample.com"))
      val headers = HttpHeaders.of(headerMap, accpetAllFilter)
      assert(headers.map().size() == 1)
      assert(headers.firstValue("Host").get() == "eXample.com")
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
      assert(headers.map().size() == 1)
      val values = headers.allValues("Accept")
      assert(values.get(0) == "text/html")
    }

    test("HttpHeaders.of filters header values based on predicate") {
      val headerMap = JMap.of("Accept", JList.of("text/html", "application/json"))
      val filter: BiPredicate[String, String] = (_, value) => value.contains("json")
      val headers = HttpHeaders.of(headerMap, filter)
      val values = headers.allValues("Accept")
      assert(values.size() == 1)
      assert(values.get(0) == "application/json")
    }

    test("HttpHeaders.of headers can construct when all values are filtered out") {
      val headerMap =
        JMap.of(
          "Accept",
          JList.of("text/html"),
          "Content-Type",
          JList.of("application/xml", "text/plain"),
        )
      val filter: BiPredicate[String, String] = (_, value) => false
      val headers = HttpHeaders.of(headerMap, filter)
      assert(headers.map().size() == 0)
    }

    /**
     * TODO: add test cases for system properties `java.net.http.HttpHeaders.allowDuplicateHeaders`
     */

    test("hashCode is consistent with equals") {
      val headerMap = JMap.of("Host", JList.of("example.com"))
      val headers1 = HttpHeaders.of(headerMap, accpetAllFilter)
      val headers2 = HttpHeaders.of(headerMap, accpetAllFilter)
      assert(headers1.hashCode() == headers2.hashCode())
    }

    test("case insensitive header name access") {
      val headerMap = JMap.of("Content-Type", JList.of("application/json"))
      val headers = HttpHeaders.of(headerMap, accpetAllFilter)
      assert(headers.firstValue("content-type").get() == "application/json")
      assert(headers.firstValue("CONTENT-TYPE").get() == "application/json")
    }

    test("allValues returns all values for existing header") {
      val headerMap = JMap.of("Content-Type", JList.of("application/json", "charset=utf-8"))
      val headers = HttpHeaders.of(headerMap, accpetAllFilter)
      val values = headers.allValues("Content-Type")
      assert(values.size() == 2)
      assert(values.get(0) == "application/json")
      assert(values.get(1) == "charset=utf-8")
    }

    test("firstValue returns empty optional for non-existent header") {
      val value = exampleHeaders.firstValue("non-existent")
      assert(value.isPresent, false)
    }

    test("firstValue returns first value for existing header") {
      val headerMap = JMap.of("Accept", JList.of("text/html", "application/xml"))
      val headers = HttpHeaders.of(headerMap, accpetAllFilter)
      val value = headers.firstValue("Accept")
      assert(value.isPresent == true)
      assert(value.get() == "text/html")
    }

    test("firstValueAsLong parses numeric header value") {
      val headerMap = JMap.of("Content-Length", JList.of("1234"))
      val headers = HttpHeaders.of(headerMap, accpetAllFilter)
      val value = headers.firstValueAsLong("Content-Length")
      assert(value.isPresent == true)
      assert(value.getAsLong == 1234L)
    }

    test("firstValueAsLong throws exception for non-numeric value") {
      val headerMap = JMap.of("Content-Type", JList.of("text/plain"))
      val headers = HttpHeaders.of(headerMap, accpetAllFilter)
      assertThrows[NumberFormatException] {
        headers.firstValueAsLong("Content-Type")
      }
    }

    test("equals returns true for identical headers") {
      val headerMap = JMap.of("Host", JList.of("example.com"))
      val headers1 = HttpHeaders.of(headerMap, accpetAllFilter)
      val headers2 = HttpHeaders.of(headerMap, accpetAllFilter)
      assert(headers1 == headers2)
    }

    test("equals returns false for different headers") {
      val headerMap1 = JMap.of("Host", JList.of("example.com"))
      val headerMap2 = JMap.of("Host", JList.of("different.com"))

      val headers1 = HttpHeaders.of(headerMap1, accpetAllFilter)
      val headers2 = HttpHeaders.of(headerMap2, accpetAllFilter)
      assert(headers1 != headers2)
    }
  }
}
