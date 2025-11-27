package java.net

import scala.jdk.CollectionConverters._

class HttpCookieTests extends munit.FunSuite:

  test("parseSimple") {
    val header = "name=value"
    val cookies = HttpCookie.parse(header).asScala.toList
    assertEquals(cookies.head.getName(), "name")
    assertEquals(cookies.head.getValue(), "value")
  }

  test("parseWithAttributes") {
    val header = "name=value; Path=/; Secure"
    val cookies = HttpCookie.parse(header).asScala.toList
    val cookie = cookies.head
    assertEquals(cookie.getName(), "name")
    assertEquals(cookie.getValue(), "value")
    assertEquals(cookie.getPath(), "/")
    assert(cookie.getSecure())
  }

  test("parseMultipleCookies") {
    val header = "name1=value1, name2=value2"
    val cookies = HttpCookie.parse(header).asScala.toList
    assertEquals(cookies.length, 2)
    assertEquals(cookies(0).getName(), "name1")
    assertEquals(cookies(1).getName(), "name2")
  }

  test("parseEmpty") {
    val header = ""
    val cookies = HttpCookie.parse(header).asScala.toList
    assertEquals(cookies.isEmpty, true)
  }

  test("parseMultiline") {
    val header =
      "name=value; Path=/; Secure, " +
      "name2=value2; HttpOnly"
    val cookies = HttpCookie.parse(header).asScala.toList
    assertEquals(cookies.length, 2)
    assertEquals(cookies(0).getName(), "name")
    assertEquals(cookies(1).getName(), "name2")
  }
