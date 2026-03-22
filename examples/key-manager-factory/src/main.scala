import java.security.{KeyPair, KeyPairGenerator, KeyStore}
import javax.net.ssl.{KeyManagerFactory, X509KeyManager}

object Main {

  def testX509KeyManager(km: X509KeyManager, empty: Boolean, algorithm: String): Unit = {
    val kTypes = Array("RSA", "DSA", "DH_RSA", "DH_DSA", "EC", "EC_EC", "EC_RSA", "", null)

    for (keyType <- kTypes) {
      val aliases = km.getClientAliases(keyType, null)
      println(aliases)
      if (empty || keyType == null || keyType.isEmpty()) {
        // s"Expected null aliases for keyType=$keyType"
        assert(aliases == null)
      } else {
        // s"Expected non-null aliases for keyType=$keyType"
        assert(aliases != null)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val kmf = KeyManagerFactory.getInstance("PKIX")
    kmf.init(null, null)

    val keyManagers = kmf.getKeyManagers()
    assert(keyManagers != null)
    assert(keyManagers.length > 0)
    for (keyManager <- keyManagers) {
      assert(keyManager != null)
      keyManager match {
        case km: X509KeyManager =>
          testX509KeyManager(km, false, kmf.getAlgorithm())
        case _ => ()
      }
    }
  }

}
