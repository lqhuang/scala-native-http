package snhttp.test.javax.net.ssl

import java.io.FileInputStream
import java.security.{SecureRandom, KeyStore}
import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManager, X509TrustManager}

import utest.{TestSuite, Tests, test, assert, assertThrows}

class SSLContextExtraTest extends TestSuite:

  val trustAllCerts = Array[TrustManager](new X509TrustManager() {
    def getAcceptedIssuers() = Array[X509Certificate]()
    def checkClientTrusted(chain: Array[X509Certificate], authType: String) = {}
    def checkServerTrusted(chain: Array[X509Certificate], authType: String) = {}
  })

  val path: String = s"${sys.env("MILL_TEST_RESOURCE_DIR")}/test-data/pkcs12-ca/test-trust.p12"
  println(s"Using PKCS12 file at path: ${path}")
  val password: String = "test-password"

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
      val pass = password.toCharArray()
      val keyManagers = {
        val ks = KeyStore.getInstance("PKCS12")
        ks.load(new FileInputStream(path), pass)
        val keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManager.init(ks, pass)
        keyManager.getKeyManagers()
      }

      val sc = SSLContext.getInstance("TLS")
      assertThrows[UnsupportedOperationException]:
        sc.init(keyManagers, null, new SecureRandom())
    }
