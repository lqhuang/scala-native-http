import java.net.http.{HttpRequest, HttpResponse}
import java.net.http.HttpResponse.PushPromiseHandler
import java.util.concurrent.Flow.{Subscriber, Subscription}
import java.util.concurrent.{CompletableFuture, ConcurrentHashMap}
import java.util.function.Function

class PushPromiseHandlerTest extends munit.FunSuite {

  test("PushPromiseHandler should handle push promises") {
    val pushPromisesMap = new ConcurrentHashMap[
      HttpRequest,
      CompletableFuture[HttpResponse[String]],
    ]()
    val pushPromiseHandler: Function[
      HttpRequest,
      HttpResponse.BodyHandler[String],
    ] = _ => HttpResponse.BodyHandlers.ofString()

    val handler = PushPromiseHandler.of(pushPromiseHandler, pushPromisesMap)

    // Create mock requests
    val initiatingRequest = HttpRequest
      .newBuilder()
      .uri(java.net.URI.create("http://example.com"))
      .build()

    val pushPromiseRequest = HttpRequest
      .newBuilder()
      .uri(java.net.URI.create("http://example.com/resource"))
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
