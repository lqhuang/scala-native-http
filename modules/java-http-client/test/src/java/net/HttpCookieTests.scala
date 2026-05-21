package snhttp.java.net

import java.net.HttpCookie
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.{ZoneOffset, ZonedDateTime}
import java.util.Locale

import scala.jdk.CollectionConverters._

import utest.{Tests, test, assert, assertThrows}

class HttpCookieTests extends utest.TestSuite:

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
    assert(list.length == 1)
    list.head

  val tests = Tests {
    test("parses name and value from Set-Cookie header") {
      val cookie = parseSingle("Set-Cookie: session=abc123; Path=/app")
      assert(cookie.getName() == "session")
      assert(cookie.getValue() == "abc123")
      assert(cookie.getPath() == "/app")
      assert(cookie.getVersion() == 0)
    }

    test("parses Set-Cookie2 header") {
      val cookie = parseSingle("Set-Cookie2: session=abc123; Version=1; Path=/app")
      assert(cookie.getName() == "session")
      assert(cookie.getValue() == "abc123")
      assert(cookie.getPath() == "/app")
      assert(cookie.getVersion() == 1)
    }

    test("parses lowercase set-cookie and set-cookie2 prefixes") {
      val c1 = parseSingle("set-cookie: a=1; Path=/")
      val c2 = parseSingle("set-cookie2: b=2; Version=1; Path=/")
      assert(c1.getName() == "a")
      assert(c2.getName() == "b")
      assert(c2.getVersion() == 1)
    }

    test("parses cookie header prefixes case-insensitively") {
      val c1 = parseSingle("sEt-CoOkIe: a=1; Path=/")
      val c2 = parseSingle("sEt-CoOkIe2: b=2; Version=1; Path=/")
      assert(c1.getName() == "a")
      assert(c2.getName() == "b")
      assert(c2.getVersion() == 1)
    }

    test("leading spaces before Set-Cookie prefix are rejected") {
      assertThrows[IllegalArgumentException] {
        HttpCookie.parse("       Set-Cookie: e=5"): Unit
      }
      assertThrows[IllegalArgumentException] {
        HttpCookie.parse(" Set-Cookie"): Unit
      }
    }

    test("secure and http-only flags toggle correctly") {
      val cookie = parseSingle("auth=token; Secure; HttpOnly")
      assert(cookie.getSecure() == true)
      assert(cookie.isHttpOnly() == true)
    }

    test("max-age takes precedence over expires") {
      val expiresAt = ZonedDateTime.of(2030, 6, 15, 10, 30, 0, 0, ZoneOffset.UTC)
      val header = s"prefs=dark; Max-Age=42; Expires=${expiresAt.format(DateTimeFormatter.RFC_1123_DATE_TIME)}"
      val cookie = parseSingle(header)
      assert(cookie.getMaxAge() == 42L)
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
      assert(cookie.getMaxAge() == 0L)
      assert(cookie.hasExpired() == true)
    }

    test("version 0 cookies do not split on comma") {
      val header = "flavor=\"choco,cherry\"; Path=/"
      val cookies = HttpCookie.parse(header).asScala.toList
      assert(cookies.length == 1)
      assert(cookies.head.getName() == "flavor")
      assert(cookies.head.getValue() == "choco,cherry")
      assert(cookies.head.getPath() == "/")
    }

    test("whitespace is ignored around attributes") {
      val cookie = parseSingle(" name = value ; Path = /root ; Domain = Example.COM ")
      assert(cookie.getName() == "name")
      assert(cookie.getValue() == "value")
      assert(cookie.getPath() == "/root")
      assert(cookie.getDomain() == "example.com")
    }

    test("domain matching follows RFC rules") {
      assert(HttpCookie.domainMatches(".example.com", "www.example.com") == true)
      assert(HttpCookie.domainMatches(".example.com", "example.org") == false)
    }

    test("invalid cookie names throw") {
      assertThrows[IllegalArgumentException] {
        HttpCookie.parse("$bad=value"): Unit
      }
    }

    test("constructor with null name throws") {
      assertThrows[NullPointerException] {
        new HttpCookie(null, "value"): Unit
      }
    }

    test("constructor with empty name throws") {
      assertThrows[IllegalArgumentException] {
        new HttpCookie("", "value"): Unit
      }
    }

    test("constructor with invalid name characters throws") {
      assertThrows[IllegalArgumentException] {
        new HttpCookie("cookie;name", "value"): Unit
      }
    }

    test("constructor with name starting with dollar throws") {
      assertThrows[IllegalArgumentException] {
        new HttpCookie("$cookie", "value"): Unit
      }
    }

    test("constructor allows null value") {
      val cookie = new HttpCookie("test", null)
      assert(cookie.getValue() == null)
    }

    test("domainMatches with IP addresses") {
      assert(HttpCookie.domainMatches("192.168.1.1", "192.168.1.1") == true)
      assert(HttpCookie.domainMatches("192.168.1.1", "192.168.1.2") == false)
    }

    test("domainMatches is case insensitive") {
      assert(HttpCookie.domainMatches("EXAMPLE.COM", "example.com") == true)
      assert(HttpCookie.domainMatches("example.com", "EXAMPLE.COM") == true)
    }

    test("domainMatches with null domain returns false") {
      assert(HttpCookie.domainMatches(null, "example.com") == false)
    }

    test("domainMatches with null host returns false") {
      assert(HttpCookie.domainMatches("example.com", null) == false)
    }

    test("setMaxAge and getMaxAge") {
      val cookie = new HttpCookie("test", "value")
      cookie.setMaxAge(3600)
      assert(cookie.getMaxAge() == 3600)
      
      cookie.setMaxAge(-1)
      assert(cookie.getMaxAge() == -1)
      
      cookie.setMaxAge(0)
      assert(cookie.getMaxAge() == 0)
    }

    test("hasExpired for expired cookie") {
      val cookie = new HttpCookie("test", "value")
      cookie.setMaxAge(0)
      assert(cookie.hasExpired() == true)
    }

    test("hasExpired for session cookie") {
      val cookie = new HttpCookie("test", "value")
      cookie.setMaxAge(-1)
      assert(cookie.hasExpired() == false)
    }

    test("setPath preserves exact slash form") {
      val cookie = new HttpCookie("test", "value")
      cookie.setPath(new java.net.URI("http://example.com/app/").getPath())
      assert(cookie.getPath() == "/app/")
      cookie.setPath(new java.net.URI("http://example.com/app").getPath())
      assert(cookie.getPath() == "/app")
    }

    test("clone creates independent copy") {
      val original = new HttpCookie("test", "value")
      original.setDomain("example.com")
      original.setPath("/")
      original.setSecure(true)

      val cloned = original.clone().asInstanceOf[HttpCookie]
      assert(cloned ne original)
      cloned.setValue("newvalue")
      cloned.setDomain("other.com")

      assert(original.getValue() == "value")
      assert(original.getDomain() == "example.com")
      assert(cloned.getValue() == "newvalue")
      assert(cloned.getDomain() == "other.com")
    }

    test("equals compares name domain and path") {
      val cookie1 = new HttpCookie("test", "value1")
      cookie1.setDomain("example.com")
      cookie1.setPath("/")
      
      val cookie2 = new HttpCookie("test", "value2")
      cookie2.setDomain("example.com")
      cookie2.setPath("/")
      
      val cookie3 = new HttpCookie("test", "value1")
      cookie3.setDomain("other.com")
      cookie3.setPath("/")
      
      assert(cookie1.equals(cookie2) == true)
      assert(cookie1.equals(cookie3) == false)
    }

    test("hashCode is consistent") {
      val cookie1 = new HttpCookie("test", "value")
      cookie1.setDomain("example.com")

      val cookie2 = new HttpCookie("test", "other")
      cookie2.setDomain("example.com")

      assert(cookie1.hashCode() == cookie2.hashCode())
    }

    test("toString matches JDK formatting for versions 0 and 1") {
      val cookie = new HttpCookie("session", "abc123")
      assert(cookie.toString() == "session=\"abc123\"")
      cookie.setVersion(0)
      assert(cookie.toString() == "session=abc123")
    }

    test("parse with null header throws NullPointerException") {
      assertThrows[NullPointerException] {
        HttpCookie.parse(null): Unit
      }
    }

    test("parse with empty string throws IllegalArgumentException") {
      assertThrows[IllegalArgumentException] {
        HttpCookie.parse(""): Unit
      }
    }

    test("parse with whitespace-only header throws IllegalArgumentException") {
      assertThrows[IllegalArgumentException] {
        HttpCookie.parse(" "): Unit
      }
      assertThrows[IllegalArgumentException] {
        HttpCookie.parse("   "): Unit
      }
    }

    test("attributes are case-insensitive") {
      val cookie = parseSingle("test=value; SECURE; HTTPONLY; PATH=/test; MAX-AGE=100")
      assert(cookie.getSecure() == true)
      assert(cookie.isHttpOnly() == true)
      assert(cookie.getPath() == "/test")
      assert(cookie.getMaxAge() == 100L)
    }

    test("setVersion with invalid version throws IllegalArgumentException") {
      val cookie = new HttpCookie("test", "value")
      assertThrows[IllegalArgumentException] {
        cookie.setVersion(2): Unit
      }
    }

    test("first occurrence of duplicate attributes wins") {
      val cookie = parseSingle("session=v; Domain=a.com; Domain=b.com; Path=/one; Path=/two")
      assert(cookie.getDomain() == "a.com")
      assert(cookie.getPath() == "/one")
    }

    test("clone preserves fields and remains independent") {
      val original = new HttpCookie("test", "value")
      original.setDomain("example.com")
      original.setPath("/app")
      original.setSecure(true)

      val cloned = original.clone().asInstanceOf[HttpCookie]
      assert(cloned.getName() == original.getName())
      assert(cloned.getValue() == original.getValue())
      assert(cloned.getDomain() == original.getDomain())
      assert(cloned.getPath() == original.getPath())
      assert(cloned.getSecure() == original.getSecure())

      cloned.setValue("modified")
      assert(original.getValue() == "value")
    }

    test("equals compares domain case-insensitively") {
      val cookie1 = new HttpCookie("test", "value")
      cookie1.setDomain("EXAMPLE.COM")

      val cookie2 = new HttpCookie("test", "value")
      cookie2.setDomain("example.com")

      assert(cookie1.equals(cookie2))
    }

    test("equals compares name case-insensitively") {
      val cookie1 = new HttpCookie("TEST", "value")
      cookie1.setDomain("example.com")

      val cookie2 = new HttpCookie("test", "value")
      cookie2.setDomain("example.com")

      assert(cookie1.equals(cookie2))
    }

    test("equals compares path case-sensitively") {
      val cookie1 = new HttpCookie("test", "value")
      cookie1.setDomain("example.com")
      cookie1.setPath("/App")

      val cookie2 = new HttpCookie("test", "value")
      cookie2.setDomain("example.com")
      cookie2.setPath("/app")

      assert(!cookie1.equals(cookie2))
    }
  }
