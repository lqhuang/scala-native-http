import scala.scalanative.unsafe.{
  CStruct2,
  CSize,
  Ptr,
  stackalloc,
  alloc,
  Zone,
  CQuote,
  sizeof,
  CVoidPtr,
}
import scala.scalanative.unsigned.{USize, UnsignedRichInt}
import scala.scalanative.libc.stddef.NULL
import scala.scalanative.libc.string.memcpy
import scala.util.Using

import snhttp.experimental.curl.curl.{
  CurlEasy,
  CurlMulti,
  CurlOption,
  CurlErrCode,
  CurlMultiErrCode,
  CurlWriteCallback,
  CurlMsg,
}
import snhttp.experimental.curl.curl.CurlErrCode.RichCurlErrCode
import snhttp.experimental.curl.curl.CurlMultiErrCode.RichCurlMultiErrCode

object App:

  /** Used by CURLOPT_WRITEDATA and more */
  type CurlCustomData = CStruct2[
    /** variables to track data */
    Ptr[Byte],
    /** function */
    USize,
  ]

  given zone: Zone = Zone.open()

  val writeDataCallback = CurlWriteCallback.fromScalaFunction {
    (payload: Ptr[Byte], nmemb: CSize, size: CSize, outstream: Ptr[?]) =>
      val userdata = outstream.asInstanceOf[Ptr[CurlCustomData]]
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

    println("Curl multi example started")

    val writeData = stackalloc[CurlCustomData]()
    (!writeData)._1 = stackalloc[Byte](8192)
    (!writeData)._2 = 0.toUSize

    inline def pollAndGetIsErr(
        multi: CurlMulti,
        timeoutMs: Int,
    ): Boolean =
      val ret = multi.poll(null, 0.toUInt, timeoutMs, null)
      println(s"multi.poll ret = ${ret} (${ret.getname})")
      ret != CurlMultiErrCode.OK

    Using.resource(CurlMulti()) { multi =>
      Using.resources(CurlEasy(), CurlEasy()) { (curl1, curl2) =>
        curl1.setCStringOption(CurlOption.URL, c"http://httpbin.org/get")
        curl1.setPtrOption(CurlOption.WRITEDATA, writeData)
        curl1.setFuncPtrOption(CurlOption.WRITEFUNCTION, writeDataCallback.asFuncPtr)

        curl2.setCStringOption(CurlOption.URL, c"https://example.com/")

        multi.addCurlEasy(curl1)
        multi.addCurlEasy(curl2)

        val _runningCounter = stackalloc[Int]()
        while {
          val _ = multi.perform(_runningCounter)
          println(s"still running = ${!_runningCounter}")
          !_runningCounter != 0 && !pollAndGetIsErr(multi, 500)
        }
        do {}

        while {
          val msgCount = stackalloc[Int]()
          val msg = multi.infoRead(msgCount)

          if msg != NULL
          then //
            false // break loop
          else {
            val m = msg.asInstanceOf[CurlMsg]
            if (m.curl == curl1)
              println(s"Message is for curl1")
            else if (m.curl == curl2)
              println(s"Message is for curl2")
            else
              println(s"Message is for unknown curl handle")

            true // continue loop
          }
        }
        do ()

        val respCode = curl1.info.responseCode
        println(s"Response code of first curl handle is ${respCode}")

        val headers2 = curl2.info.headers
        println(s"Response headers of second curl handle are ${headers2}")

        multi.removeCurlEasy(curl1)
        multi.removeCurlEasy(curl2)
      }

    }

    zone.close()
  }
