import java.net.http.HttpHeaders
import java.nio.charset.{Charset, StandardCharsets}
import java.util.Collections
import java.util.List as JList
import java.util.Map as JMap
import java.util.function.BiPredicate

import snhttp.jdk.internal.Utils

import utest.{TestSuite, Tests, test, assert}

class UtilsTest extends TestSuite {

  private val acceptAllFilter: BiPredicate[String, String] = (_, _) => true
  private def createHeaders(contentType: String): HttpHeaders = {
    val headerMap = JMap.of("Content-Type", JList.of(contentType))
    HttpHeaders.of(headerMap, acceptAllFilter)
  }

  val tests = Tests {

    test("charsetFrom returns UTF-8 for missing Content-Type header") {
      Seq(
        JMap.of[String, JList[String]](),
        Collections.emptyMap[String, JList[String]](),
      ).foreach { headerMap =>
        val headers = HttpHeaders.of(headerMap, acceptAllFilter)
        val charset = Utils.charsetFrom(headers)
        assert(charset == StandardCharsets.UTF_8)
      }
    }

    test("charsetFrom should default to UTF-8 when no charset specified") {
      val headers = createHeaders("text/plain")
      val charset = Utils.charsetFrom(headers)
      assert(charset == StandardCharsets.UTF_8)
    }

    test("charsetFrom returns UTF-8 for Content-Type without charset") {
      val headers = createHeaders("application/json")
      val charset = Utils.charsetFrom(headers)
      assert(charset == StandardCharsets.UTF_8)
    }

    test("charsetFrom should extract UTF-8 from Content-Type header") {
      val headers = createHeaders("text/plain; charset=utf-8")
      val charset = Utils.charsetFrom(headers)
      assert(charset == StandardCharsets.UTF_8)
    }

    test("charsetFrom should extract ISO-8859-1 from Content-Type header") {
      val headers = createHeaders("text/html; charset=ISO-8859-1")
      val charset = Utils.charsetFrom(headers)
      assert(charset == StandardCharsets.ISO_8859_1)
    }

    test("charsetFrom should handle case-insensitive charset parameter") {
      Seq(
        "text/plain; charset=us-ascii",
        "text/plain; CHARSET=us-ascii",
        "text/plain; charset = US-ASCII",
        "text/plain; charset=US-ASCII; other=value",
        "text/plain; CHARSET=US-ASCII; other=value",
      ).foreach { contentType =>
        val headers = createHeaders(contentType)
        val charset = Utils.charsetFrom(headers)
        assert(charset == StandardCharsets.US_ASCII)
      }
    }

    test("charsetFrom should handle charset with spaces") {
      Seq(
        "text/plain; charset=utf-16",
        "text/plain;      charset = utf-16   ",
        "text/plain;charset = utf-16",
        "text/plain; charset =utf-16    ; other=value",
        "text/plain;charset=utf-16;other=value",
      ).foreach { contentType =>
        val headers = createHeaders(contentType)
        val charset = Utils.charsetFrom(headers)
        assert(charset == StandardCharsets.UTF_16)
      }
    }

    test("charsetFrom should handle multiple parameters") {
      val headers = createHeaders("text/plain; boundary=something; charset=us-ascii; other=value")
      val charset = Utils.charsetFrom(headers)
      assert(charset == StandardCharsets.US_ASCII)
    }

    test("charsetFrom should default to UTF-8 for invalid charset") {
      val headers = createHeaders("text/plain; charset=invalid-charset")
      val charset = Utils.charsetFrom(headers)
      assert(charset == StandardCharsets.UTF_8)
    }

    test("charsetFrom should handle malformed charset value") {
      Seq(
        "charset=",
        "text/plain; charset=",
        "charset=;utf-16",
        "text/plain; charset=;utf-16",
        "text/plain; charset=invalid;",
        "text/plain; charset=; invalid",
      ).foreach { contentType =>
        val headers = createHeaders(contentType)
        val charset = Utils.charsetFrom(headers)
        assert(charset == StandardCharsets.UTF_8)
      }
    }

    test("charsetFrom should handle charset at beginning of Content-Type") {
      val headers = createHeaders("charset=utf-16; text/plain")
      val charset = Utils.charsetFrom(headers)
      assert(charset == StandardCharsets.UTF_16)
    }

    test("charsetFrom should fetch first charset if multiple are present") {
      Seq(
        "text/plain; charset=utf-16; charset=iso-8859-1",
        "text/plain; timezone=utc; other=another; charset=utf-16; good; charset=iso-8859-1",
      ).foreach { contentType =>
        val headers = createHeaders(contentType)
        val charset = Utils.charsetFrom(headers)
        assert(charset == StandardCharsets.UTF_16)
      }
    }

    // test("charsetFrom returns UTF-8 for empty Content-Type") {
    //   val headerMap = JMap.of("Content-Type", JList.of(""))
    //   val headers = HttpHeaders.of(headerMap, acceptAllFilter)
    //   // TODO:
    //   // We cannot build HttpHeaders with an empty Content-Type directly
    //   // but what if we receive such a header?
    //   val charset = Utils.charsetFrom(headers)
    //   assert(charset == StandardCharsets.UTF_8)
    // }

    // test("charsetFrom handles quoted charset values") {
    //   val headers = createHeaders("application/json; charset=\"utf-8\"")
    //   // Note: This test may fail with current implementation if quotes aren't handled
    //   // The charset name would be "utf-8" including quotes, which might not be valid
    //   intercept[Exception] {
    //     Utils.charsetFrom(headers)
    //   }
    // }

    ///
    /// Tests for `filenameFrom` method
    ///

    // TODO: test malformed
    //
    // - "hello \"world\""
    // - "line1\nline2"
  }
}
