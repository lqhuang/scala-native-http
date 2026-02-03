import java.util.concurrent.atomic.AtomicBoolean

import scala.scalanative.unsafe.{CStruct3, CFuncPtr4, CSize, Ptr, stackalloc, alloc, Zone, CQuote}
import scala.scalanative.unsigned.UnsignedRichInt
import scala.scalanative.libc.string.memcpy
import scala.scalanative.libc.stddef.size_t
import scala.util.Using

import snhttp.experimental.libcurl
import snhttp.experimental.libcurl.{
  CurlOption,
  CurlErrCode,
  Curl,
  CurlInfo,
  CurlHttpVersion,
  CurlData,
  CurlWriteCallback,
}
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

    test("writedata callback function should be called on data received") {
      val writeData = alloc[CurlData]()
      (!writeData)._1 = alloc[Byte](8192)
      (!writeData)._2 = 0.toUSize

      val writeDataCallback: CurlWriteCallback = CFuncPtr4.fromScalaFunction {
        (ptr: Ptr[Byte], size: CSize, nmemb: CSize, data: Ptr[?]) =>
          val userdata = data.asInstanceOf[Ptr[CurlData]]
          val total = size * nmemb

          val processed: CSize =
            if total >= 8192.toUSize
            then
              val _ = memcpy((!userdata)._1, ptr, 8192.toUSize)
              (!userdata)._2 = 8192.toUSize
              8192.toUSize
            else
              val _ = memcpy((!userdata)._1, ptr, total)
              (!userdata)._2 = total
              total

          processed
      }

      Using(libcurl.easyInit()) { curl =>
        assert(curl != null)

        val _ = libcurl.easySetopt(curl, CurlOption.URL, c"http://httpbin.org/get")
        val _ = libcurl.easySetopt(curl, CurlOption.WRITEDATA, writeData)
        val _ = libcurl.easySetopt(curl, CurlOption.WRITEFUNCTION, writeDataCallback)
        val res = libcurl.easyPerform(curl)

        assert((!writeData)._2 > 0.toUInt)

        if (res != CurlErrCode.OK)
          println(s"Failed with code ${res} (str err: ${res.getname})")
        assert(res == CurlErrCode.OK)
      }
    }

    test("writedata supports Scala functions") {

      /** Used by CURLOPT_WRITEDATA and more */
      type CurlData = CStruct3[
        /** memory */
        Ptr[Byte],
        /** size */
        size_t,
        /** scala function */
        Function0[Unit],
      ]

      val flag = new AtomicBoolean()
      def customFunction(): Unit =
        flag.getAndSet(true): Unit

      val writeData = alloc[CurlData]()

      (!writeData)._1 = alloc[Byte](8192)
      (!writeData)._2 = 0.toUSize
      (!writeData)._3 = customFunction

      val writeDataCallback: CurlWriteCallback = CFuncPtr4.fromScalaFunction {
        (ptr: Ptr[Byte], size: CSize, nmemb: CSize, data: Ptr[?]) =>
          val userdata = data.asInstanceOf[Ptr[CurlData]]
          val total = size * nmemb

          val processed: CSize =
            if total >= 8192.toUSize
            then
              val _ = memcpy((!userdata)._1, ptr, 8192.toUSize)
              (!userdata)._2 = 8192.toUSize
              8192.toUSize
            else
              val _ = memcpy((!userdata)._1, ptr, total)
              (!userdata)._2 = total
              total

          val _ = (!userdata)._3()

          processed
      }

      Using(libcurl.easyInit()) { curl =>
        assert(curl != null)

        val _ = libcurl.easySetopt(curl, CurlOption.URL, c"http://httpbin.org/get")
        val _ = libcurl.easySetopt(curl, CurlOption.WRITEDATA, writeData)
        val _ = libcurl.easySetopt(curl, CurlOption.WRITEFUNCTION, writeDataCallback)
        val res = libcurl.easyPerform(curl)

        assert((!writeData)._2 > 0.toUInt)
        assert(flag.get() == true)

        if (res != CurlErrCode.OK)
          println(s"Failed with code ${res} (str err: ${res.getname})")
        assert(res == CurlErrCode.OK)
      }
    }
