package snhttp.test.javax.net.ssl

import java.net.Socket
import java.security.{PrivateKey, Principal, SecureRandom}
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, TrustManager, X509KeyManager, X509TrustManager}

import utest.{TestSuite, Tests, test, assert, assertThrows}

class SSLContextExtraTest extends TestSuite:

  val trustAllCerts = Array[TrustManager](new X509TrustManager() {
    def getAcceptedIssuers() = Array[X509Certificate]()
    def checkClientTrusted(chain: Array[X509Certificate], authType: String) = {}
    def checkServerTrusted(chain: Array[X509Certificate], authType: String) = {}
  })

  def tests: Tests = Tests:

    test("no verify") {
      val noVerifySSLContext = {
        // Install the all-trusting trust manager
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, trustAllCerts, new SecureRandom())
        sc
      }
    }

    test("verify key manager registration is unsupported") {
      val keyManager = new X509KeyManager() {
        def getClientAliases(keyType: String, issuers: Array[Principal]) = null
        def chooseClientAlias(
            keyType: Array[String],
            issuers: Array[Principal],
            socket: Socket,
        ) = null
        def getServerAliases(keyType: String, issuers: Array[Principal]) = null
        def chooseServerAlias(keyType: String, issuers: Array[Principal], socket: Socket) = null
        def getCertificateChain(alias: String) = Array.empty[X509Certificate]
        def getPrivateKey(alias: String): PrivateKey = null
      }

      val sc = SSLContext.getInstance("TLS")
      assertThrows[UnsupportedOperationException]:
        sc.init(Array(keyManager), null, new SecureRandom())
    }
