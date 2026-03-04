import scala.scalanative.unsafe.{Zone, CQuote, fromCString, Ptr}

import snhttp.experimental.openssl.libssl

import utest.{TestSuite, Tests, test, assert}

object LibcurlTest extends TestSuite:

  given zone: Zone = Zone.open()

  override def utestAfterAll(): Unit =
    zone.close()

  def tests = Tests:

    test("OPENSSL_cipher_name") {
      val rfcCipherName = c"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"
      val osslCipherName =
        fromCString(libssl.OPENSSL_cipher_name(rfcCipherName))
      assert(osslCipherName == "ECDHE-RSA-AES256-GCM-SHA384")
    }

    test("StackOfCiphers") {
      val ssl = libssl.SSL_CTX_new(libssl.TLS_method())
      try {
        val stackOfCiphers = libssl.SSL_CTX_get_ciphers(ssl)
        val numCiphers = libssl.snhttp_ossl_sk_SSL_CIPHER_num(stackOfCiphers)
        assert(numCiphers > 0)

        0.until(numCiphers).foreach { i =>
          val cipher = libssl.snhttp_ossl_sk_SSL_CIPHER_value(stackOfCiphers, i)
          val cipherName = fromCString(libssl.SSL_CIPHER_get_name(cipher))
          assert(cipherName.nonEmpty)
        }
      }
      finally libssl.SSL_CTX_free(ssl)

    }
