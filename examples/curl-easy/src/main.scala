import scala.scalanative.unsafe.{CStruct2, CSize, Ptr, Zone, CQuote}
import scala.scalanative.unsafe.{alloc, fromCString}
import scala.scalanative.unsigned.UnsignedRichInt
import scala.scalanative.libc.string.memcpy
import scala.scalanative.libc.stddef.size_t
import scala.util.Using

import snhttp.experimental.curl.curl.{CurlEasy, CurlOption, CurlErrCode, CurlWriteCallback}
import snhttp.experimental.curl.curl.CurlErrCode.RichCurlErrCode

object App:

  type CurlData = CStruct2[
    /** memory */
    Ptr[Byte],
    /** size */
    size_t,
  ]

  given zone: Zone = Zone.open()

  val writeDataCallback = CurlWriteCallback.fromScalaFunction {
    (payload: Ptr[Byte], size: CSize, nmemb: CSize, outstream: Ptr[?]) =>
      val userdata = outstream.asInstanceOf[Ptr[CurlData]]
      val recvSize = size * nmemb

      val processed: CSize =
        if recvSize >= 8192.toUSize
        then
          val _ = memcpy((!userdata)._1, payload, 8192.toUSize)
          (!userdata)._2 = 8192.toUSize
          8192.toUSize
        else
          val _ = memcpy((!userdata)._1, payload, recvSize)
          (!userdata)._2 = recvSize
          recvSize
      processed
  }

  @main
  def main(): Unit = {

    println("Curl example started")

    val writeData = alloc[CurlData]()
    (!writeData)._1 = alloc[Byte](4096)
    (!writeData)._2 = 0.toUSize

    Using.resource(CurlEasy()) { curl =>
      println(s"CurlEasy ${curl} initialized.")

      curl.setCStringOption(CurlOption.URL, c"http://httpbin.org/get")
      curl.setPtrOption(CurlOption.WRITEDATA, writeData)
      curl.setFuncPtrOption(CurlOption.WRITEFUNCTION, writeDataCallback.asFuncPtr)

      println("Performing curl request...")
      val perfRet = curl.perform()
      println(s"perform return code: ${perfRet} (${perfRet.getname})")

      val respCode = curl.info.responseCode
      println(s"Response body size received: ${respCode}")
    }

    zone.close()
  }
