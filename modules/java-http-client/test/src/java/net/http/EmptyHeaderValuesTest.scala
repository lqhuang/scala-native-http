package snhttp.test.java.net.http

import java.net.http.HttpHeaders
import java.util.{List as JList, Map as JMap}
import utest.{TestSuite, Tests, test, assert}

class EmptyHeaderValuesTest extends TestSuite:
  val tests = Tests:
    test("empty header value is retained") {
      val headers = HttpHeaders.of(JMap.of("X-Empty", JList.of("")), (_, _) => true)
      assert(headers.firstValue("X-Empty").isPresent())
      assert(headers.firstValue("X-Empty").get() == "")
    }
    test("whitespace header value becomes an empty value") {
      val headers = HttpHeaders.of(JMap.of("X-Empty", JList.of("  \t ")), (_, _) => true)
      assert(headers.allValues("X-Empty") == JList.of(""))
    }
    test("empty header value list is omitted") {
      val headers = HttpHeaders.of(JMap.of("X-Empty", JList.of[String]()), (_, _) => true)
      assert(headers.map().isEmpty())
    }
    test("filter can reject empty values while retaining other values") {
      val headers = HttpHeaders.of(
        JMap.of("X-Values", JList.of("", "  value  ", " ")),
        (_, value) => !value.isEmpty(),
      )
      assert(headers.allValues("X-Values") == JList.of("value"))
    }
