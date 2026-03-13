import scala.scalanative.unsafe.{Ptr, Zone, CQuote, fromCString}
import scala.scalanative.unsigned.{USize, UnsignedRichInt}

import snhttp.experimental.openssl.libssl

object App:

  given zone: Zone = Zone.open()

  @main
  def main(): Unit = {
    val ssl = libssl.SSL_CTX_new(libssl.TLS_method())

    try {
      val stackOfCiphers = libssl.SSL_CTX_get_ciphers(ssl)

      val numCiphers = libssl.snhttp_ossl_sk_SSL_CIPHER_num(stackOfCiphers)

      println(s"Number of ciphers: ${numCiphers}")

      0.until(numCiphers).foreach { i =>
        val cipher = libssl.snhttp_ossl_sk_SSL_CIPHER_value(stackOfCiphers, i)
        println(s"Cipher ${i}: ${fromCString(libssl.SSL_CIPHER_get_name(cipher))}")
      }

    } finally libssl.SSL_CTX_free(ssl)

    zone.close()
  }
