import java.net.URI
import java.net.http.{HttpClient, HttpRequest}
import java.net.http.HttpResponse.BodyHandlers
import java.security.SecureRandom
import java.security.cert.X509Certificate

import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManager, X509TrustManager}

object Main:

  def runDefaultSSLContext(): Unit = {
    val client = HttpClient.newBuilder().build()

    val reqBadSSL = HttpRequest.newBuilder(URI.create("https://expired.badssl.com")).build()
    try
      val response = client.send(reqBadSSL, BodyHandlers.ofString())
      throw new IllegalStateException("Expected SSLHandshakeException but request succeeded")
    catch
      case exc: SSLHandshakeException =>
        println(s"Expected SSLHandshakeException:\n  ${exc.getClass()}\n  ${exc.getMessage()}")

    val req = HttpRequest
      .newBuilder()
      .uri(URI.create("https://httpbingo.org/user-agent"))
      .GET()
      .build()

    val response = client.send(req, BodyHandlers.ofString())
    println(s"Status: ${response.statusCode()}")
    println(s"Body: ${response.body()}")
  }

  def runNoVerifySSLContext(): Unit = {
    val trustAllCerts = Array[TrustManager](new X509TrustManager() {
      def getAcceptedIssuers = new Array[X509Certificate](0)
      def checkClientTrusted(chain: Array[X509Certificate], authType: String) = {}
      def checkServerTrusted(chain: Array[X509Certificate], authType: String) = {}
    })
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom())

    val client = HttpClient.newBuilder().sslContext(sslContext).build()

    val request = HttpRequest
      .newBuilder()
      .uri(URI.create("https://expired.badssl.com"))
      .GET()
      .build()

    val response = client.send(request, BodyHandlers.ofString())
    println(s"Status: ${response.statusCode()}")
    println(s"Body: ${response.body()}")
  }

  def runCustomSSLContext(): Unit = {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, null, null)

    val sslParams = sslContext.getDefaultSSLParameters()
    sslParams.setProtocols(Array("TLSv1.3"))

    val client = HttpClient.newBuilder().sslContext(sslContext).sslParameters(sslParams).build()

    val request = HttpRequest
      .newBuilder()
      .uri(URI.create("https://check-tls.akamai.io/v1/tlsinfo.json"))
      .GET()
      .build()

    val response = client.send(request, BodyHandlers.ofString())
    println(s"Status: ${response.statusCode()}")
    println(s"Body: ${response.body()}")
  }

  // ------------------------------------------ //
  //   Main entry point                         //
  // ------------------------------------------ //

  def main(args: Array[String]): Unit =
    SSLContext.getDefault()
    runDefaultSSLContext()
    runNoVerifySSLContext()
    runCustomSSLContext()
