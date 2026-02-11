import java.net.URI
import java.nio.ByteBuffer
import java.net.http.{HttpClient, HttpHeaders, HttpRequest, HttpResponse}
import java.util.List as JList

import utest.{Tests, test, assert, TestSuite}

class HttpResponseTest extends TestSuite:

  def tests = Tests:
    test("HttpResponse should return correct status code, body, and headers") {}
