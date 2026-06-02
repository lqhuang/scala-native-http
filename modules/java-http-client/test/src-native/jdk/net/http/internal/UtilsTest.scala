package snhttp.test.jdk.net.http.internal

import java.net.http.HttpHeaders
import java.nio.charset.{Charset, StandardCharsets}
import java.util.Collections
import java.util.{List as JList, Map as JMap}

import _root_.snhttp.jdk.net.http.internal.Utils

import utest.{TestSuite, Tests, test, assert}

class UtilsTest extends TestSuite:

  private def createHeaders(contentType: String): HttpHeaders = {
    val headerMap = JMap.of("Content-Type", JList.of(contentType))
    HttpHeaders.of(headerMap, (_, _) => true)
  }

  private def createDispositionHeaders(contentDisposition: String): HttpHeaders = {
    val headerMap = JMap.of("Content-Disposition", JList.of(contentDisposition))
    HttpHeaders.of(headerMap, (_, _) => true)
  }

  private val noDispositionHeaders: HttpHeaders =
    HttpHeaders.of(Collections.emptyMap[String, JList[String]](), (_, _) => true)

  def tests = Tests:

    test("charsetFrom returns UTF-8 for missing Content-Type header") {
      Seq(
        JMap.of[String, JList[String]](),
        Collections.emptyMap[String, JList[String]](),
      ).foreach { headerMap =>
        val headers = HttpHeaders.of(headerMap, (_, _) => true)
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

    test("charsetFrom should handle quoted charset parameter") {
      Seq(
        "text/html;charset=utf-16",
        "text/html;charset=UTF-16",
        "Text/HTML;Charset=\"utf-16\"",
        "text/html; charset=\"utf-16\"",
        "text/html; charset=   \"utf-16\"",
      ).foreach { contentType =>
        val headers = createHeaders(contentType)
        val charset = Utils.charsetFrom(headers)
        assert(charset == StandardCharsets.UTF_16)
      }
    }

    test("charsetFrom should handle case-insensitive charset parameter") {
      Seq(
        "text/plain; charset=us-ascii",
        "text/plain; CHARSET=us-ascii",
        "text/plain; charset = \"US-ASCII\"",
        "text/plain; charset=US-ASCII; other=value",
        "text/plain; CHARSET=US-ASCII; other=value",
      ).foreach { contentType =>
        val headers = createHeaders(contentType)
        val charset = Utils.charsetFrom(headers)
        assert(charset == StandardCharsets.US_ASCII)
      }
    }

    test("charsetFrom should only handle exact `charset` keyword") {
      Seq(
        "text/plain; _charset=us-ascii",
        "text/plain; hhh_charset=us-ascii",
        "text/plain; charset= 'us-ascii",
        "text/plain; god-charset=us-ascii",
        "text/plain; ad charset=us-ascii",
        "text/plain; CHARSETttttt=us-ascii",
        "text/plain; charsetaaa = US-ASCII",
        "text/plain; charsett = US-ASCII",
        "text/plain; ccccccharset=US-ASCII",
        "text/plain;-charset=us-ascii",
        "text/plain;    -charset=us-ascii",
        "text/plain; 'charset=us-ascii",
        "text/plain; '-charset=us-ascii",
        "text/plain;'charset=us-ascii",
        "text/plain; aa'charset=us-ascii",
      ).foreach { contentType =>
        val headers = createHeaders(contentType)
        val charset = Utils.charsetFrom(headers)
        assert(charset == StandardCharsets.UTF_8)
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

    /*
     * Tests for `filenameFrom` method
     */

    test("filenameFrom returns empty optional when Content-Disposition header is absent") {
      val filename = Utils.filenameFrom(noDispositionHeaders)
      assert(filename.isEmpty())
    }

    test("filenameFrom returns empty optional when no filename parameter is present") {
      Seq(
        "attachment",
        "attachment;",
        "inline",
        "form-data; name=field1",
        "form-data; name=field2;",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.isEmpty())
      }
    }

    test("filenameFrom returns empty optional when no attachment is present") {
      Seq(
        "filename = field1",
        "filename=field2;",
        "filename=field1; filename = field2;",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.isEmpty())
      }
    }

    test("filenameFrom extracts a simple unquoted") {
      Seq(
        ("attachment; filename = field1", "field1"),
        ("attachment; filename=field2;", "field2"),
        ("attachment; filename=field1; filename = field2;", "field1"),
      ).foreach { (disposition, expected) =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == expected)
      }
    }

    test("filenameFrom extracts a simple quoted filename") {
      Seq(
        ("attachment; filename=\"file.txt\"", "file.txt"),
        ("attachment; filename=\"report.pdf\"", "report.pdf"),
        ("attachment; filename=\"data.json\"", "data.json"),
        ("attachment; filename=\"archive.tar.gz\"", "archive.tar.gz"),
        ("attachment; filename=\"image.jpeg\"", "image.jpeg"),
      ).foreach { (disposition, expected) =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == expected)
      }
    }

    test("filenameFrom extracts a quoted filename that contains spaces") {
      Seq(
        ("attachment; filename=\"my file.txt\"", "my file.txt"),
        ("attachment; filename=\"annual report 2024.pdf\"", "annual report 2024.pdf"),
        ("attachment; filename=\"data export march.csv\"", "data export march.csv"),
        ("attachment; filename=\"data%20export%20march.csv\"", "data%20export%20march.csv"),
      ).foreach { (disposition, expected) =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == expected)
      }
    }

    test("filenameFrom extracts filename that contains dots") {
      Seq(
        ("attachment; filename=\"file.tar.gz\"", "file.tar.gz"),
        ("attachment; filename=\"v1.2.3.jar\"", "v1.2.3.jar"),
        ("attachment; filename=\"backup.2024.01.01.zip\"", "backup.2024.01.01.zip"),
      ).foreach { (disposition, expected) =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == expected)
      }
    }

    test("filenameFrom is case-insensitive for the filename parameter name") {
      Seq(
        "attachment; FILENAME=\"file.txt\"",
        "attachment; Filename=\"file.txt\"",
        "attachment; FileName=\"file.txt\"",
        "attachment; fIlEnAmE=\"file.txt\"",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == "file.txt")
      }
    }

    test("filenameFrom is case-sensitive for the filename name") {
      Seq(
        ("attachment; FILENAME=\"fIle.txt\"", "fIle.txt"),
        ("attachment; Filename=\"file.TXT\"", "file.TXT"),
        ("attachment; FileName=\"file.txt\"", "file.txt"),
        ("attachment; fIlEnAmE=\"FILE.txt\"", "FILE.txt"),
      ).foreach { (disposition, expected) =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == expected)
      }
    }

    test("filenameFrom handles whitespace around the equals sign in filename parameter") {
      Seq(
        "attachment; filename = \"file.txt\"",
        "attachment; filename=  \"file.txt\"",
        "attachment; filename  =  \"file.txt\"",
        "attachment;  filename = \"file.txt\"",
        "attachment;filename=\"file.txt\"",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == "file.txt")
      }
    }

    test("filenameFrom extracts filename when additional parameters are present") {
      Seq(
        "attachment; size=1024; filename=\"file.txt\"",
        "attachment; filename=\"file.txt\"; size=1024",
        "attachment; type=document; filename=\"file.txt\"; charset=utf-8",
        "attachment; creation-date=\"Mon, 01 Jan 2024 00:00:00 GMT\"; filename=\"file.txt\"",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == "file.txt")
      }
    }

    test("filenameFrom uses only the final path component for unix-style forward-slash paths") {
      Seq(
        ("attachment; filename=\"dir/file.txt\"", "file.txt"),
        ("attachment; filename=\"a/b/c/file.txt\"", "file.txt"),
        ("attachment; filename=\"/absolute/path/file.txt\"", "file.txt"),
        ("attachment; filename=\"../parent/file.txt\"", "file.txt"),
        ("attachment; filename=\"../../etc/passwd\"", "passwd"),
      ).foreach { (disposition, expected) =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == expected)
      }
    }

    test("filenameFrom uses only the final path component for windows-style backslash paths") {
      Seq(
        ("attachment; filename=\"dir\\\\file.txt\"", "file.txt"),
        ("attachment; filename=\"C:\\\\Users\\\\user\\\\file.txt\"", "file.txt"),
        ("attachment; filename=\"..\\\\secret.txt\"", "secret.txt"),
      ).foreach { (disposition, expected) =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == expected)
      }
    }

    test("filenameFrom returns first filename when multiple filename parameters are present") {
      // \n / \t / \r expand via translateEscapes in the filename value
      Seq(
        "attachment; filename = \"first.txt\"; filename = second.txt",
        "attachment; filename = first.txt; filename = second.txt",
        "attachment; filename = first.txt; filename = \"second.txt\"",
        "attachment; filenamE = first.txt; filename = \"second.txt\"",
        "attachment; FILENAME = first.txt; filename = \"second.txt\"",
        "attachment; filenamE = \"first.txt\"; filenaME = \"second.txt\"",
        "attachment; filenamE = \"first.txt\"; filenaME = \"second.txt\";",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == "first.txt")
      }
    }

    test(
      "filenameFrom returns expected filename",
    ) {
      Seq(
        ("attachment; filename=\"document.txt\"", "document.txt"),
        ("attachment; filename=document.txt", "document.txt"),
        ("attachment; filename=\"document with space.txt\"", "document with space.txt"),
        ("attachment; filename=\"document%20with%20space.txt\"", "document%20with%20space.txt"),
        ("attachment; filename=document%20with%20space.txt", "document%20with%20space.txt"),
        ("attachment; filename=\"dir/file.txt\"", "file.txt"),
        ("attachment; filename=\"a/b/c/file.txt\"", "file.txt"),
        ("attachment; filename=\"/absolute/path/file.txt\"", "file.txt"),
        ("attachment; filename=\"../parent/file.txt\"", "file.txt"),
        ("attachment; filename=\"../../etc/passwd\"", "passwd"),
        ("attachment; filename=\"..passwd\"", "..passwd"),
        ("attachment; filename=\"dir\\\\file.txt\"", "file.txt"),
        ("attachment; filename=\"C:\\\\Users\\\\user\\\\file.txt\"", "file.txt"),
        ("attachment; filename=\"..\\\\secret.txt\"", "secret.txt"),
        ("attachment; filename=\"fil?e.txt\"", "fil?e.txt"),
        ("attachment; filename=\"fil*e.txt\"", "fil*e.txt"),
        ("attachment; filename=\"fil:e.txt\"", "fil:e.txt"),
        ("attachment; filename=\"fil#e.txt\"", "fil#e.txt"),
        ("attachment; filename=\"fil<e.txt\"", "fil<e.txt"),
        ("attachment; filename=\"fil>e.txt\"", "fil>e.txt"),
        ("attachment; filename=\"fil~e.txt\"", "fil~e.txt"),
        ("attachment; filename=\"fil|e.txt\"", "fil|e.txt"),
        ("attachment; filename=\"fil%e.txt\"", "fil%e.txt"),
        ("attachment; filename=\"fil{e.txt\"", "fil{e.txt"),
        ("attachment; filename=\"fil}e.txt\"", "fil}e.txt"),
        ("attachment; filename=\"fil&e.txt\"", "fil&e.txt"),
        ("attachment; filename=\"fil\"e.txt\"", "fil"),
      ).foreach { (disposition, expected) =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.get() == expected)
      }
    }

    test(
      "filenameFrom returns empty optional when path traversal yields an empty or dangerous final component",
    ) {
      Seq(
        "attachment; filename=\"..\"", // bare parent-directory reference
        "attachment; filename=\"../\"", // trailing slash -> empty last component
        "attachment; filename=\"path/..\"", // parent-reference as last segment
        "attachment; filename=\"/\"", // root slash only
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.isEmpty())
      }
    }

    test("filenameFrom returns empty optional for empty filename") {
      Seq(
        "attachment; filename=\"\"",
        "attachment; filename=\"\";",
        "attachment; filename=\"   \";",
        "attachment; filename=\"./\";",
        "attachment; filename=\".\\\";",
        "attachment; filename=;",
        "attachment; filename=  ;",
        "attachment; filename =  ;",
        "attachment; filename=\"\t\"",
        "attachment; filename=\"\r\"",
        "attachment; filename=\"\n\";",
        "attachment; filename=\"\r\";",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.isEmpty())
      }
    }

    test("filenameFrom returns empty optional for filename with OS-reserved or illegal characters") {
      Seq(
        "attachment; filename=document.txt\"",
        "attachment; filename=\"document.txt",
        "attachment; filename=document no quotes with space.txt",
        "attachment; filename=./document.txt;",
        "attachment; filename=../document.txt",
        "attachment; filename=a/document.txt",
        "attachment; filename=\\document.txt",
        "attachment; filename=.\\document.txt",
        "attachment; filename=..\\document.txt",
        "attachment; filename=a\\document.txt",
        "attachment; filename=a?document.txt",
        "attachment; filename=a[document.txt",
        "attachment; filename=]document.txt",
        "attachment; filename=(document.txt",
        "attachment; filename=)document.txt",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.isEmpty())
      }
    }

    test("filenameFrom returns empty optional for filename containing control characters") {
      // \n / \t / \r expand via translateEscapes in the filename value
      Seq(
        "attachment; filename=\"file\\nname.txt\"",
        "attachment; filename=\"file\\tname.txt\"",
        "attachment; filename=\"file\\rname.txt\"",
        "attachment; filename=file\\nname.txt",
        "attachment; filename=file\\tname.txt",
        "attachment; filename=file\\rname.txt",
      ).foreach { disposition =>
        val headers = createDispositionHeaders(disposition)
        val filename = Utils.filenameFrom(headers)
        assert(filename.isEmpty())
      }
    }

    // test("filenameFrom extracts a percent-encoded filename from filename* parameter (RFC 5987)") {
    //   Seq(
    //     ("attachment; filename*=UTF-8''hello%20world.txt", "hello world.txt"),
    //     ("attachment; filename*=UTF-8''report%202024.pdf", "report 2024.pdf"),
    //     ("attachment; filename*=UTF-8''file.txt", "file.txt"),
    //     ("attachment; filename*=UTF-8''caf%C3%A9.txt", "café.txt"),
    //   ).foreach { (disposition, expected) =>
    //     val headers = createDispositionHeaders(disposition)
    //     val filename = Utils.filenameFrom(headers)
    //     assert(filename.get() == expected)
    //   }
    // }
