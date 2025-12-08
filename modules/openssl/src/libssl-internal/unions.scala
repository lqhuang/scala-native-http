package snhttp.experimental.openssl.libssl_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object unions:
  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_ADDR = CArray[Byte, Nat._0]
  object BIO_ADDR:
    given _tag: Tag[BIO_ADDR] = Tag.CArray[CChar, Nat._0](Tag.Byte, Tag.Nat0)
