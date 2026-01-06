import snhttp.experimental.libcurl.{
  CurlOption,
  easyInit,
  easySetopt,
  easyPerform,
  CurlErrCode,
  easyCleanup,
}

import scalanative.unsafe.{CFuncPtr4, CSize, Ptr, stackalloc, CString, CQuote}
import scala.scalanative.libc.string
import scala.scalanative.libc.stdio

import utest.{TestSuite, Tests, test, assert}

object LibcurlTest extends TestSuite:
  val tests = Tests:

    test("writedata callback function should be called on data received") {
      val write_data_callback = CFuncPtr4.fromScalaFunction {
        (ptr: Ptr[Byte], size: CSize, nmemb: CSize, userdata: Ptr[Byte]) =>
          val chunk = stackalloc[Byte](nmemb)
          val _ = string.strncpy(chunk, ptr, nmemb)
          val _ = stdio.printf(c"Chunk: %s\n", chunk)
          nmemb * size
      }
      val curl = easyInit()
      assert(curl != null)

      val _ = easySetopt(curl, CurlOption.URL, c"https://httpbin.org/get")
      val _ = easySetopt(curl, CurlOption.WRITEFUNCTION, write_data_callback)

      val res = easyPerform(curl)
      assert(res == CurlErrCode.OK)
      easyCleanup(curl)

    }
