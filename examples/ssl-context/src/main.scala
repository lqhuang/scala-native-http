import java.io.FileInputStream
import java.security.{KeyStore, SecureRandom}
import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManager, X509TrustManager}

object Main {

  val trustAllCerts = Array[TrustManager](new X509TrustManager() {
    def getAcceptedIssuers() = Array[X509Certificate]()
    def checkClientTrusted(chain: Array[X509Certificate], authType: String) = {}
    def checkServerTrusted(chain: Array[X509Certificate], authType: String) = {}
  })

  def main(args: Array[String]): Unit = {
    val path: String = sys.env.getOrElse("cert.path", "")
    println(s"Using PKCS12 file at path: ${path}")
    val password: String = "test-password"

    val noVerifySSLContext = {
      // Install the all-trusting trust manager
      val sc = SSLContext.getInstance("TLS")
      sc.init(null, trustAllCerts, new SecureRandom())
      sc
    }

    val verifySSLContext = {
      val pass = password.toCharArray()
      val keyManagers = {
        val ks = KeyStore.getInstance("PKCS12")
        ks.load(new FileInputStream(path), pass)
        val keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManager.init(ks, pass)
        keyManager.getKeyManagers()
      }

      val sc = SSLContext.getInstance("TLS")
      sc.init(keyManagers, null, new SecureRandom())
      sc
    }

  }

}
