import scala.scalanative.unsafe.{
  CStruct2,
  CFuncPtr2,
  CFuncPtr4,
  CSize,
  Ptr,
  stackalloc,
  alloc,
  Zone,
  CQuote,
  sizeof,
  fromCString,
}
import scala.scalanative.unsigned.{USize, UnsignedRichInt}
import scala.scalanative.libc.string.memcpy
import scala.scalanative.libc.stddef.size_t
import scala.scalanative.libc.stdlib.realloc
import scala.util.Using

import snhttp.experimental.curl.{
  CurlEasy,
  CurlOption,
  CurlErrCode,
  CurlWriteCallback,
  CurlData,
  CurlWriteFuncRet,
}
import snhttp.experimental.curl.CurlErrCode.RichCurlErrCode

object App:

  given zone: Zone = Zone.open()

  val writeDataCallback: CurlWriteCallback = CFuncPtr4.fromScalaFunction {
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
      curl.setFuncPtrOption(CurlOption.WRITEFUNCTION, writeDataCallback)

      println("Performing curl request...")
      val perfRet = curl.perform()
      println(s"perform return code: ${perfRet} (${perfRet.getname})")

      val respCode = curl.info.responseCode
      println(s"Response body size received: ${respCode}")
    }

    zone.close()
  }
