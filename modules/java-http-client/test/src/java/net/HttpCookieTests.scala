package snhttp.httpcookie

import java.net.HttpCookie
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.{ZoneOffset, ZonedDateTime}
import java.util.Locale

import scala.jdk.CollectionConverters._

class HttpCookieTests extends munit.FunSuite:

  private val rfc850Formatter =
    new DateTimeFormatterBuilder()
      .appendPattern("EEEE, dd-MMM-yy HH:mm:ss 'GMT'")
      .toFormatter(Locale.US)
      .withZone(ZoneOffset.UTC)

  private val asctimeFormatter =
    new DateTimeFormatterBuilder()
      .appendPattern("EEE MMM d HH:mm:ss yyyy")
      .toFormatter(Locale.US)
      .withZone(ZoneOffset.UTC)

  private def parseSingle(header: String): HttpCookie =
    val list = HttpCookie.parse(header).asScala.toList
    assertEquals(list.length, 1)
    list.head

  test("parses name and value from Set-Cookie header") {
    val cookie = parseSingle("Set-Cookie: session=abc123; Path=/app")
    assertEquals(cookie.getName(), "session")
    assertEquals(cookie.getValue(), "abc123")
    assertEquals(cookie.getPath(), "/app")
    assertEquals(cookie.getVersion(), 0)
  }

  test("secure and http-only flags toggle correctly") {
    val cookie = parseSingle("auth=token; Secure; HttpOnly")
    assertEquals(cookie.getSecure(), true)
    assertEquals(cookie.isHttpOnly(), true)
  }

  test("max-age takes precedence over expires") {
    val expiresAt = ZonedDateTime.of(2030, 6, 15, 10, 30, 0, 0, ZoneOffset.UTC)
    val header = s"prefs=dark; Max-Age=42; Expires=${expiresAt.format(DateTimeFormatter.RFC_1123_DATE_TIME)}"
    val cookie = parseSingle(header)
    assertEquals(cookie.getMaxAge(), 42L)
  }

  test("expires header in RFC1123 format derives delta seconds") {
    val expiresAt = ZonedDateTime.of(2024, 1, 1, 0, 2, 0, 0, ZoneOffset.UTC)
    val header = s"session=id; Expires=${expiresAt.format(DateTimeFormatter.RFC_1123_DATE_TIME)}"
    val before = System.currentTimeMillis()
    val cookie = parseSingle(header)
    val after = System.currentTimeMillis()
    val expiresMillis = expiresAt.toInstant.toEpochMilli
    val min = math.max(0L, (expiresMillis - after) / 1000L)
    val max = math.max(0L, (expiresMillis - before) / 1000L)
    assert(cookie.getMaxAge() >= min && cookie.getMaxAge() <= max)
  }

  test("expires header in RFC850 format derives delta seconds") {
    val expiresAt = ZonedDateTime.of(1994, 11, 6, 8, 49, 37, 0, ZoneOffset.UTC)
    val header = s"legacy=1; Expires=${expiresAt.format(rfc850Formatter)}"
    val before = System.currentTimeMillis()
    val cookie = parseSingle(header)
    val after = System.currentTimeMillis()
    val expiresMillis = expiresAt.toInstant.toEpochMilli
    val min = math.max(0L, (expiresMillis - after) / 1000L)
    val max = math.max(0L, (expiresMillis - before) / 1000L)
    assert(cookie.getMaxAge() >= min && cookie.getMaxAge() <= max)
  }

  test("two-digit RFC850 years below 70 map to 2000-based years") {
    val expiresAt = ZonedDateTime.of(2012, 5, 20, 4, 30, 0, 0, ZoneOffset.UTC)
    val header = s"future=1; Expires=${expiresAt.format(rfc850Formatter)}"
    val before = System.currentTimeMillis()
    val cookie = parseSingle(header)
    val after = System.currentTimeMillis()
    val expiresMillis = expiresAt.toInstant.toEpochMilli
    val min = math.max(0L, (expiresMillis - after) / 1000L)
    val max = math.max(0L, (expiresMillis - before) / 1000L)
    assert(cookie.getMaxAge() >= min && cookie.getMaxAge() <= max)
  }

  test("expires header in asctime format derives delta seconds") {
    val expiresAt = ZonedDateTime.of(2024, 1, 1, 0, 1, 0, 0, ZoneOffset.UTC)
    val header = s"state=ok; Expires=${expiresAt.format(asctimeFormatter)}"
    val before = System.currentTimeMillis()
    val cookie = parseSingle(header)
    val after = System.currentTimeMillis()
    val expiresMillis = expiresAt.toInstant.toEpochMilli
    val min = math.max(0L, (expiresMillis - after) / 1000L)
    val max = math.max(0L, (expiresMillis - before) / 1000L)
    assert(cookie.getMaxAge() >= min && cookie.getMaxAge() <= max)
  }

  test("invalid expires values mark cookie as expired") {
    val cookie = parseSingle("flag=1; Expires=not-a-date")
    assertEquals(cookie.getMaxAge(), 0L)
    assertEquals(cookie.hasExpired(), true)
  }

  test("version 0 cookies do not split on comma") {
    // Per JDK behavior: version 0 (Netscape/RFC 6265) cookies are not comma-separated.
    // The entire header is parsed as a single cookie.
    val header = "flavor=\"choco,cherry\"; Path=/"
    val cookies = HttpCookie.parse(header).asScala.toList
    assertEquals(cookies.length, 1)
    assertEquals(cookies.head.getName(), "flavor")
    assertEquals(cookies.head.getValue(), "choco,cherry")
    assertEquals(cookies.head.getPath(), "/")
  }

  test("whitespace is ignored around attributes") {
    val cookie = parseSingle(" name = value ; Path = /root ; Domain = Example.COM ")
    assertEquals(cookie.getName(), "name")
    assertEquals(cookie.getValue(), "value")
    assertEquals(cookie.getPath(), "/root")
    assertEquals(cookie.getDomain(), "example.com")
  }

  test("domain matching follows RFC rules") {
    assertEquals(HttpCookie.domainMatches(".example.com", "www.example.com"), true)
    assertEquals(HttpCookie.domainMatches(".example.com", "example.org"), false)
  }

  test("invalid cookie names throw") {
    intercept[IllegalArgumentException] {
      HttpCookie.parse("$bad=value")
    }
  }
