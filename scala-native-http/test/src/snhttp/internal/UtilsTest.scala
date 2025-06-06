import snhttp.internal.Utils
import java.net.http.HttpHeaders
import java.util.List as JList
import java.util.Map as JMap
import java.nio.charset.{Charset, StandardCharsets}
import java.util.function.BiPredicate

class UtilsTest extends munit.FunSuite {

  val acceptAllFilter: BiPredicate[String, String] = (_, _) => true

  test("charsetFrom returns UTF-8 for missing Content-Type header") {
    val headers = HttpHeaders.of(JMap.of(), acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_8)
  }

  test("charsetFrom returns UTF-8 for Content-Type without charset") {
    val headerMap = JMap.of("Content-Type", JList.of("application/json"))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_8)
  }

  test("charsetFrom parses charset from Content-Type header") {
    val headerMap = JMap.of("Content-Type", JList.of("text/html; charset=iso-8859-1"))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, Charset.forName("iso-8859-1"))
  }

  test("charsetFrom handles UTF-8 charset") {
    val headerMap = JMap.of("Content-Type", JList.of("application/json; charset=utf-8"))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_8)
  }

  test("charsetFrom handles charset with extra spaces") {
    val headerMap = JMap.of("Content-Type", JList.of("text/plain;  charset=utf-16  "))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_16)
  }

  test("charsetFrom handles multiple parameters with charset") {
    val headerMap =
      JMap.of("Content-Type", JList.of("text/html; boundary=something; charset=utf-16"))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_16)
  }

  test("charsetFrom handles case-insensitive charset parameter") {
    val headerMap = JMap.of("Content-Type", JList.of("text/html; CHARSET=utf-8"))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_8)
  }

  test("charsetFrom returns UTF-8 for malformed charset parameter") {
    val headerMap = JMap.of("Content-Type", JList.of("text/html; charset="))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_8)
  }

  test("charsetFrom returns UTF-8 for empty Content-Type") {
    val headerMap = JMap.of("Content-Type", JList.of(""))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    // TODO:
    // We cannot build HttpHeaders with an empty Content-Type directly
    // but what if we receive such a header?
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_8)
  }

  test("charsetFrom returns UTF-8 for unqualified charset name") {
    val headerMap = JMap.of("Content-Type", JList.of("text/html; charset=non-existent-charset"))
    val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    val charset = Utils.charsetFrom(headers)
    assertEquals(charset, StandardCharsets.UTF_8)
  }

  // test("charsetFrom handles quoted charset values") {
  //   val headerMap = JMap.of("Content-Type", JList.of("text/html; charset=\"utf-8\""))
  //   val headers = HttpHeaders.of(headerMap, acceptAllFilter)
  //   // Note: This test may fail with current implementation if quotes aren't handled
  //   // The charset name would be "utf-8" including quotes, which might not be valid
  //   intercept[Exception] {
  //     Utils.charsetFrom(headers)
  //   }
  // }

}
