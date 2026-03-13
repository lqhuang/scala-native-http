package snhttp.experimental.openssl._libssl

import _root_.scala.scalanative.unsafe.*

object unions:
  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_ADDR = CArray[Byte, Nat._0]
  object BIO_ADDR:
    given _tag: Tag[BIO_ADDR] = Tag.CArray[CChar, Nat._0](Tag.Byte, Tag.Nat0)
