import java.net.http.{HttpClient, HttpHeaders}
import java.net.http.HttpResponse.{BodyHandlers, BodySubscribers, ResponseInfo}
import java.nio.ByteBuffer
import java.nio.file.{Files, Path}
import java.util.concurrent.Flow.Subscription
import java.util.List as JList
import java.util.Map as JMap
import java.util.function.Function

import snhttp.jdk.net.http.ResponseInfoImpl
// import snhttp.testkits.MockSubscriber

import utest.{Tests, test, assert}

// TODO: not implemented, tests are commented out for now

class BodyHandlersTest extends utest.TestSuite {

  private def createHeaders(map: Map[String, String]): HttpHeaders = {
    val entries = map.map { case (k, v) => JMap.entry(k, JList.of(v)) }.toSeq
    val headerMap = JMap.ofEntries(entries*)
    HttpHeaders.of(headerMap, (_, _) => true)
  }
  private def createResponseInfo(map: Map[String, String] = Map.empty): ResponseInfo =
    return ResponseInfoImpl(
      200,
      createHeaders(map),
      HttpClient.Version.HTTP_1_1,
    )

  val tests = Tests {

    // ================================== //
    // Test BodyHandlers.fromSubscriber() //
    // ================================== //

    // test("BodyHandlers.fromSubscriber should handle custom finisher exceptions") {
    //   val mockSubscriber = MockSubscriber[JList[ByteBuffer]]()
    //   val faultyFinisher: Function[MockSubscriber[JList[ByteBuffer]], String] =
    //     _ => throw new RuntimeException("Finisher error")

    //   val subscriber = BodySubscribers.fromSubscriber(mockSubscriber, faultyFinisher)
    //   subscriber.onSubscribe(new Subscription {
    //     override def request(n: Long): Unit = ()
    //     override def cancel(): Unit = ()
    //   })
    //   subscriber.onNext(JList.of(ByteBuffer.wrap("test".getBytes())))
    //   subscriber.onComplete()

    //   intercept[Exception] {
    //     subscriber.getBody().toCompletableFuture.get()
    //   }
    // }

    // ============================ //
    // Test BodyHandlers.ofString() //
    // ============================ //

    test("BodyHandlers.ofString should create string handler") {
      val handler = BodyHandlers.ofString()
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)
    }

    // ========================== //
    // Test BodyHandlers.ofFile() //
    // ========================== //

    test("BodyHandlers.ofFile should create file handler") {
      // Using.resource(Files.createTempFile("test", ".txt")) { tempFile =>
      //   val handler = BodyHandlers.ofFile(tempFile)
      //   val responseInfo = createResponseInfo()
      //   val subscriber = handler(responseInfo)
      // }(using deleteTempPath)
    }

    test(
      "BodyHandlers.ofFile should create path subscriber for file download",
    ) {
      // Using.resource(
      //   Files.createTempFile("test-download", ".txt"),
      // ) { tempFile =>
      //   val handler = BodyHandlers.ofFile(tempFile)
      //   val responseInfo = createResponseInfo()
      //   val subscriber = handler(responseInfo)
      // }(using deleteTempPath)
    }

    // ================================== //
    // Test BodyHandlers.ofFileDownload() //
    // ================================== //

    test(
      "BodySubscribers.ofFileDownload should extract filename from Content-Disposition",
    ) {
      // Using.resource(Files.createTempDirectory("test-downloads")) { tempDir =>
      //   val handler = BodyHandlers.ofFileDownload(tempDir)
      //   val responseInfo = createResponseInfo(
      //     Map(
      //       "Content-Disposition" -> "attachment; filename=\"document.pdf\"",
      //     ),
      //   )
      //   val subscriber = handler(responseInfo)
      // }(using deleteTempPath)
    }

    test(
      "BodySubscribers.ofFileDownload should use default filename when no Content-Disposition",
    ) {
      // Using.resource(Files.createTempDirectory("test-downloads")) { tempDir =>
      //   val handler = BodyHandlers.ofFileDownload(tempDir)
      //   val responseInfo = createResponseInfo()
      //   val subscriber = handler(responseInfo)
      // }(using deleteTempPath)
    }

    test(
      "BodySubscribers.ofFileDownload should handle complex Content-Disposition header",
    ) {
      // Using.resource(Files.createTempDirectory("test-downloads")) { tempDir =>
      //   val handler = BodyHandlers.ofFileDownload(tempDir)
      //   val responseInfo = createResponseInfo(
      //     Map(
      //       "Content-Disposition" -> "attachment; filename*=UTF-8''%e2%82%ac%20rates.txt; filename=\"fallback.txt\"",
      //     ),
      //   )
      // }(using deleteTempPath)
    }

    test(
      "BodySubscribers.ofFileDownload should handle empty filename",
    ) {
      // Using.resource(Files.createTempDirectory("test-downloads")) { tempDir =>
      //   val handler = BodyHandlers.ofFile(tempDir)
      //   val responseInfo = createResponseInfo(
      //     Map(
      //       "Content-Disposition" -> "attachment; filename=\"\"",
      //     ),
      //   )
      //   val subscriber = handler(responseInfo)
      // }(using deleteTempPath)
    }

    // ================================= //
    // Test BodyHandlers.ofInputStream() //
    // ================================= //

    test(
      "BodyHandlers.ofInputStream should create input stream handler",
    ) {
      val handler = BodyHandlers.ofInputStream()
      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)
    }

    // =========================== //
    // Test BodyHandlers.ofLines() //
    // =========================== //

    test("BodyHandlers.ofLines should create lines handler") {
      val handler = BodyHandlers.ofLines()
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)
    }

    // ======================================= //
    // Test BodyHandlers.ofByteArrayConsumer() //
    // ======================================= //

    // =============================== //
    // Test BodyHandlers.ofByteArray() //
    // =============================== //

    test("BodyHandlers.ofByteArray should create byte array handler") {
      val handler = BodyHandlers.ofByteArray()
      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)
    }

    // ============================== //
    // Test BodyHandlers.discarding() //
    // ============================== //

    test("BodyHandlers.discarding should create discarding handler") {
      val handler = BodyHandlers.discarding()
      val responseInfo =
        ResponseInfoImpl(204, createHeaders(Map.empty), HttpClient.Version.HTTP_1_1)
      val subscriber = handler(responseInfo)
    }

    test("BodyHandlers.buffering should create buffering handler") {
      val downstreamHandler = BodyHandlers.ofString()
      val bufferSize = 1024
      val handler = BodyHandlers.buffering(downstreamHandler, bufferSize)
      val responseInfo = createResponseInfo(Map("Content-Type" -> "text/plain; charset=utf-8"))
      val subscriber = handler(responseInfo)
    }

    // ============================= //
    // Test BodyHandlers.replacing() //
    // ============================= //

    test("BodyHandlers.replacing should create replacing handler") {
      val handler = BodyHandlers.replacing("replacement")
      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)
    }
  }
}
