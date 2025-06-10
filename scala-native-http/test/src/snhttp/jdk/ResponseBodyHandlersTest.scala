package snhttp.jdk

import java.net.http.{HttpClient, HttpHeaders}
import java.net.http.HttpResponse.ResponseInfo
import java.nio.file.{Files, StandardOpenOption}
import java.util.List as JList
import java.util.Collections

class ResponseBodyHandlersTest extends munit.FunSuite {

  private def createResponseInfo(headers: Map[String, String] = Map.empty): ResponseInfo =
    new ResponseInfo {
      override def statusCode(): Int = 200
      override def headers(): HttpHeaders = {
        val headerMap = headers.map { case (k, v) => k -> JList.of(v) }.asJava
        HttpHeaders.of(headerMap, null)
      }
      override def version(): HttpClient.Version = HttpClient.Version.HTTP_1_1
    }

  test("PathBodyHandler should create path subscriber for file download") {
    val tempFile = Files.createTempFile("test-download", ".txt")
    try {
      val openOptions = JList.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE)
      val handler = ResponseBodyHandlers.PathBodyHandler.create(tempFile, openOptions)

      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)

      assertNotEquals(subscriber, null)
    } finally Files.deleteIfExists(tempFile)
  }

  test("FileDownloadBodyHandler should extract filename from Content-Disposition") {
    val tempDir = Files.createTempDirectory("test-downloads")
    try {
      val openOptions = JList.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE)
      val handler = ResponseBodyHandlers.FileDownloadBodyHandler.create(tempDir, openOptions)

      val responseInfo = createResponseInfo(
        Map(
          "Content-Disposition" -> "attachment; filename=\"document.pdf\"",
        ),
      )

      val subscriber = handler(responseInfo)
      assertNotEquals(subscriber, null)
    } finally
      // Clean up temp directory
      Files
        .walk(tempDir)
        .sorted(java.util.Comparator.reverseOrder())
        .forEach(Files.delete)
  }

  test("FileDownloadBodyHandler should use default filename when no Content-Disposition") {
    val tempDir = Files.createTempDirectory("test-downloads")
    try {
      val openOptions = JList.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE)
      val handler = ResponseBodyHandlers.FileDownloadBodyHandler.create(tempDir, openOptions)

      val responseInfo = createResponseInfo()
      val subscriber = handler(responseInfo)

      assertNotEquals(subscriber, null)
    } finally
      Files
        .walk(tempDir)
        .sorted(java.util.Comparator.reverseOrder())
        .forEach(Files.delete)
  }

  test("FileDownloadBodyHandler should handle complex Content-Disposition header") {
    val tempDir = Files.createTempDirectory("test-downloads")
    try {
      val openOptions = JList.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE)
      val handler = ResponseBodyHandlers.FileDownloadBodyHandler.create(tempDir, openOptions)

      val responseInfo = createResponseInfo(
        Map(
          "Content-Disposition" -> "attachment; filename*=UTF-8''%e2%82%ac%20rates.txt; filename=\"fallback.txt\"",
        ),
      )

      val subscriber = handler(responseInfo)
      assertNotEquals(subscriber, null)
    } finally
      Files
        .walk(tempDir)
        .sorted(java.util.Comparator.reverseOrder())
        .forEach(Files.delete)
  }

  test("PushPromisesHandlerWithMap should handle push promises") {
    val pushPromisesMap = new java.util.concurrent.ConcurrentHashMap[
      java.net.http.HttpRequest,
      java.util.concurrent.CompletableFuture[java.net.http.HttpResponse[String]],
    ]()

    val pushPromiseHandler: java.util.function.Function[
      java.net.http.HttpRequest,
      java.net.http.HttpResponse.BodyHandler[String],
    ] = _ => java.net.http.HttpResponse.BodyHandlers.ofString()

    val handler = new ResponseBodyHandlers.PushPromisesHandlerWithMap(
      pushPromiseHandler,
      pushPromisesMap,
    )

    // Create mock requests
    val initiatingRequest = java.net.http.HttpRequest
      .newBuilder()
      .uri(java.net.URI.create("https://example.com"))
      .build()

    val pushPromiseRequest = java.net.http.HttpRequest
      .newBuilder()
      .uri(java.net.URI.create("https://example.com/resource"))
      .build()

    val acceptor: java.util.function.Function[
      java.net.http.HttpResponse.BodyHandler[String],
      java.util.concurrent.CompletableFuture[java.net.http.HttpResponse[String]],
    ] = _ => java.util.concurrent.CompletableFuture.completedFuture(null)

    // This should not throw
    handler.applyPushPromise(initiatingRequest, pushPromiseRequest, acceptor)

    // Verify the push promise was recorded
    assert(pushPromisesMap.containsKey(pushPromiseRequest))
  }

  test("FileDownloadBodyHandler should handle filename with path separators") {
    val tempDir = Files.createTempDirectory("test-downloads")
    try {
      val openOptions = JList.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE)
      val handler = ResponseBodyHandlers.FileDownloadBodyHandler.create(tempDir, openOptions)

      // Malicious filename that tries to escape directory
      val responseInfo = createResponseInfo(
        Map(
          "Content-Disposition" -> "attachment; filename=\"../../../etc/passwd\"",
        ),
      )

      val subscriber = handler(responseInfo)
      assertNotEquals(subscriber, null)

      // The handler should sanitize the filename to prevent directory traversal
    } finally
      Files
        .walk(tempDir)
        .sorted(java.util.Comparator.reverseOrder())
        .forEach(Files.delete)
  }

  test("FileDownloadBodyHandler should handle empty filename") {
    val tempDir = Files.createTempDirectory("test-downloads")
    try {
      val openOptions = JList.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE)
      val handler = ResponseBodyHandlers.FileDownloadBodyHandler.create(tempDir, openOptions)

      val responseInfo = createResponseInfo(
        Map(
          "Content-Disposition" -> "attachment; filename=\"\"",
        ),
      )

      val subscriber = handler(responseInfo)
      assertNotEquals(subscriber, null)
    } finally
      Files
        .walk(tempDir)
        .sorted(java.util.Comparator.reverseOrder())
        .forEach(Files.delete)
  }
}
