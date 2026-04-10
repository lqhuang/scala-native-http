import java.io.FileInputStream
import java.security.{SecureRandom, KeyStore}
import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManager, X509TrustManager}

import utest.{TestSuite, Tests, test, assert}

class CertTest:

  private lazy val trustAllCerts = Array[TrustManager](new X509TrustManager() {
    def getAcceptedIssuers = new Array[X509Certificate](0)

    def checkClientTrusted(chain: Array[X509Certificate], authType: String) = {}

    def checkServerTrusted(chain: Array[X509Certificate], authType: String) = {}
  })

  val path: String = sys.props.getOrElse("cert.path", "")
  val password: Option[String] = sys.props.get("cert.password")

  def tests: Tests = Tests:

    test("no verify") {
      val noVerifySSLContext = {
        // Install the all-trusting trust manager
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, new SecureRandom())
        sc
      }
    }

    test("verify") {
      val pass = password.map(_.toCharArray).getOrElse(Array.emptyCharArray)

      val keyManagers = {
        val ks = KeyStore.getInstance("PKCS12")
        ks.load(new FileInputStream(path), pass)
        val keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManager.init(ks, pass)
        keyManager.getKeyManagers()
      }

      val sc = SSLContext.getInstance("SSL")

      sc.init(keyManagers, null, new SecureRandom())
      sc
    }
