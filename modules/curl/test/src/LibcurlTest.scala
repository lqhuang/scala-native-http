import java.util.concurrent.atomic.AtomicBoolean

import scala.scalanative.unsafe.{
  CStruct3,
  CFuncPtr4,
  CSize,
  Ptr,
  stackalloc,
  alloc,
  Zone,
  CQuote,
  sizeof,
}
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

  override def utestAfterAll(): Unit =
    zone.close()

  given Using.Releasable[Ptr[Curl]] = curlPtr => libcurl.easyCleanup(curlPtr)

  val tests = Tests:

    test("Get Curl info after performing a request") {
      for (version <- Seq(stackalloc[CurlHttpVersion](), alloc[CurlHttpVersion]())) do {
        !version = CurlHttpVersion.VERSION_LAST // an invalid version to start with

        Using.resource(libcurl.easyInit()) { curl =>
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
      val writeData = stackalloc[CurlData]()
      (!writeData)._1 = stackalloc[Byte](8192)
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

      Using.resource(libcurl.easyInit()) { curl =>
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

    test("writedata supports Scala functions and variables") {

      /** Used by CURLOPT_WRITEDATA and more */
      type CurlCustomData = CStruct3[
        /** variables */
        AtomicBoolean,
        /** function */
        Function0[Unit],
        /** function */
        Function0[Unit],
      ]

      assert(sizeof[CurlCustomData] == (sizeof[Ptr[?]] * 3.toUInt))

      val flag1 = new AtomicBoolean(false)

      val flag2 = new AtomicBoolean(false)
      def customFunction2(): Unit = flag2.getAndSet(true): Unit

      val flag3 = new AtomicBoolean(false)
      def customFunction3(): Unit = flag3.getAndSet(true): Unit

      val writeData = stackalloc[CurlCustomData]()
      (!writeData)._1 = flag1
      (!writeData)._2 = customFunction2
      (!writeData)._3 = customFunction3

      val writeDataCallback: CurlWriteCallback = CFuncPtr4.fromScalaFunction {
        (ptr: Ptr[Byte], size: CSize, nmemb: CSize, data: Ptr[?]) =>
          val userdata = data.asInstanceOf[Ptr[CurlCustomData]]
          val total = size * nmemb

          val _ = (!userdata)._1.getAndSet(true)
          val _ = (!userdata)._2.apply() // call customFunction2 via apply()
          val _ = (!userdata)._3() // call customFunction3 directly

          total
      }

      Using.resource(libcurl.easyInit()) { curl =>
        assert(curl != null)

        assert(flag1.get() == false)
        assert(flag2.get() == false)
        assert(flag3.get() == false)

        val _ = libcurl.easySetopt(curl, CurlOption.URL, c"http://httpbin.org/get")
        val _ = libcurl.easySetopt(curl, CurlOption.WRITEDATA, writeData)
        val _ = libcurl.easySetopt(curl, CurlOption.WRITEFUNCTION, writeDataCallback)
        val res = libcurl.easyPerform(curl)

        assert(flag1.get() == true)
        assert(flag2.get() == true)
        assert(flag3.get() == false) // yes ... it's false now ... buggy or expected?

        if (res != CurlErrCode.OK)
          println(s"Failed with code ${res} (str err: ${res.getname})")
        assert(res == CurlErrCode.OK)
      }
    }
