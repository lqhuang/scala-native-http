package snhttp.experimental.openssl
package _openssl.bio

import scala.annotation.targetName
import scala.scalanative.unsafe.*

object Unions:

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_ADDR = CArray[Byte, Nat._0]
  object BIO_ADDR:
    given _tag: Tag[BIO_ADDR] = Tag.CArray[CChar, Nat._0](Tag.Byte, Tag.Nat0)

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_sock_info_u = CArray[Byte, Nat._8]
  object BIO_sock_info_u:
    given _tag: Tag[BIO_sock_info_u] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

    def apply()(using Zone): Ptr[BIO_sock_info_u] =
      val ___ptr = alloc[BIO_sock_info_u](1)
      ___ptr

    @targetName("apply_addr")
    def apply(addr: Ptr[BIO_ADDR])(using Zone): Ptr[BIO_sock_info_u] =
      val ___ptr = alloc[BIO_sock_info_u](1)
      val un = !___ptr
      un.at(0).asInstanceOf[Ptr[Ptr[BIO_ADDR]]].update(0, addr)
      ___ptr

    extension (struct: BIO_sock_info_u)
      inline def addr: Ptr[BIO_ADDR] = !struct.at(0).asInstanceOf[Ptr[Ptr[BIO_ADDR]]]
      inline def addr_=(value: Ptr[BIO_ADDR]): Unit =
        !struct.at(0).asInstanceOf[Ptr[Ptr[BIO_ADDR]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type bio_addr_st = CArray[Byte, Nat._0]
  object bio_addr_st:
    given _tag: Tag[bio_addr_st] = Tag.CArray[CChar, Nat._0](Tag.Byte, Tag.Nat0)
