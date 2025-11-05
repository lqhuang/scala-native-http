import java.net.URI
import java.net.http.{HttpRequest, HttpResponse}
import java.net.http.HttpResponse.BodyHandlers
import java.util.concurrent.{CompletableFuture, ConcurrentHashMap}
import java.util.function.Function

import utest.{Tests, test, assert}

class PushPromiseHandlerTest extends utest.TestSuite {

  val tests = Tests {

    test("PushPromiseHandler should handle push promises") {
      val pushPromisesMap = new ConcurrentHashMap[
        HttpRequest,
        CompletableFuture[HttpResponse[String]],
      ]()
      val pushPromiseHandler: Function[
        HttpRequest,
        HttpResponse.BodyHandler[String],
      ] = _ => BodyHandlers.ofString()

      val handler = HttpResponse.PushPromiseHandler.of(pushPromiseHandler, pushPromisesMap)

      // Create mock requests
      val initiatingRequest = HttpRequest
        .newBuilder()
        .uri(URI.create("http://example.com"))
        .build()

      val pushPromiseRequest = HttpRequest
        .newBuilder()
        .uri(URI.create("http://example.com/resource"))
        .build()

      val acceptor: Function[
        HttpResponse.BodyHandler[String],
        CompletableFuture[HttpResponse[String]],
      ] = _ => CompletableFuture.completedFuture(null)

      // This should not throw
      handler.applyPushPromise(initiatingRequest, pushPromiseRequest, acceptor)

      // Verify the push promise was recorded
      assert(pushPromisesMap.containsKey(pushPromiseRequest))
    }

  }
}
