import scala.scalanative.unsafe.{Zone, CQuote, toCString, fromCString}

import snhttp.experimental.openssl.ssl

import utest.{TestSuite, Tests, test, assert}

object LibcurlTest extends TestSuite:

  given zone: Zone = Zone.open()

  override def utestAfterAll(): Unit =
    zone.close()

  def tests = Tests:

    test("OPENSSL_cipher_name") {
      val rfcCipherName = c"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"
      val osslCipherName =
        fromCString(ssl.OPENSSL_cipher_name(rfcCipherName))
      assert(osslCipherName == "ECDHE-RSA-AES256-GCM-SHA384")
    }
