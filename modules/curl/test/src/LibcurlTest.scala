import scala.scalanative.unsafe.{
  CFuncPtr4,
  CSize,
  Ptr,
  stackalloc,
  alloc,
  CString,
  Zone,
  toCString,
  CQuote,
}
import scala.scalanative.libc.{string, stdio}
import scala.util.Using

import snhttp.experimental.libcurl
import snhttp.experimental.libcurl.{CurlOption, CurlErrCode, Curl, CurlInfo, CurlHttpVersion}
import snhttp.experimental.libcurl.CurlErrCode.RichCurlErrCode

import utest.{TestSuite, Tests, test, assert}

object LibcurlTest extends TestSuite:

  given zone: Zone = Zone.open()

  // val curlInitRet = libcurl.globalInit(libcurl.CurlGlobalFlag.DEFAULT)
  // try assert(curlInitRet == CurlErrCode.OK)
  // catch
  //   case e: Throwable =>
  //     println(
  //       s"Global init of Curl failed with code ${curlInitRet} (str err: ${curlInitRet.getname})",
  //     )
  //     libcurl.globalCleanup()
  //     throw e

  override def utestAfterAll(): Unit =
    zone.close()
    // libcurl.globalCleanup()

  given Using.Releasable[Ptr[Curl]] = curlPtr => libcurl.easyCleanup(curlPtr)

  val tests = Tests:

    test("writedata callback function should be called on data received") {
      val writeDataCallback = CFuncPtr4.fromScalaFunction {
        (ptr: Ptr[Byte], size: CSize, nmemb: CSize, userdata: Ptr[Byte]) =>
          val chunk = stackalloc[Byte](nmemb)
          val _ = string.strncpy(chunk, ptr, nmemb)
          // val _ = stdio.printf(c"Chunk: %s\n", chunk)
          nmemb * size
      }

      Using(libcurl.easyInit()) { curl =>
        assert(curl != null)

        val _ = libcurl.easySetopt(curl, CurlOption.URL, c"https://httpbin.org/get")
        val _ = libcurl.easySetopt(curl, CurlOption.WRITEFUNCTION, writeDataCallback)
        val res = libcurl.easyPerform(curl)

        if (res != CurlErrCode.OK)
          println(s"Failed with code ${res} (str err: ${res.getname})")

        assert(res == CurlErrCode.OK)
      }
    }

    test("Get Curl info after performing a request") {

      for (version <- Seq(stackalloc[CurlHttpVersion](), alloc[CurlHttpVersion]())) do {
        !version = CurlHttpVersion.VERSION_LAST // an invalid version to start with

        Using(libcurl.easyInit()) { curl =>
          assert(curl != null)

          val _ = libcurl.easySetopt(curl, CurlOption.URL, c"http://httpbin.org/get")
          val res = libcurl.easyPerform(curl)
          if (res != CurlErrCode.OK)
            println(s" Failed with code ${res} (str err: ${res.getname})")
          assert(res == CurlErrCode.OK)

          val ret = libcurl.easyGetInfo(curl, CurlInfo.HTTP_VERSION, version)
          if (ret != CurlErrCode.OK)
            println(s"Failed to get curl info with code ${ret} (str err: ${ret.getname})")
          assert(ret == CurlErrCode.OK)

          !version match
            case CurlHttpVersion.VERSION_1_1 => assert(!version == CurlHttpVersion.VERSION_1_1)
            case _                           => assert(false)
        }
      }
    }
