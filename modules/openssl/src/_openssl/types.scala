package snhttp.experimental.openssl
package _openssl.types

import scala.scalanative.unsafe.*

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type BIGNUM = CStruct0

object BIGNUM:
  given _tag: Tag[BIGNUM] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type DSA = CStruct0

object DSA:
  given _tag: Tag[DSA] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type RSA = CStruct0

object RSA:
  given _tag: Tag[RSA] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type EC_KEY = CStruct0

object EC_KEY:
  given _tag: Tag[EC_KEY] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type EVP_CIPHER = CStruct0

object EVP_CIPHER:
  given _tag: Tag[EVP_CIPHER] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type EVP_CIPHER_CTX = CStruct0

object EVP_CIPHER_CTX:
  given _tag: Tag[EVP_CIPHER_CTX] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type EVP_MD = CStruct0

object EVP_MD:
  given _tag: Tag[EVP_MD] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type EVP_MD_CTX = CStruct0

object EVP_MD_CTX:
  given _tag: Tag[EVP_MD_CTX] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type EVP_PKEY = CStruct0

object EVP_PKEY:
  given _tag: Tag[EVP_PKEY] = Tag.materializeCStruct0Tag

/**
 * [bindgen] header: /usr/include/openssl/types.h
 */
opaque type OSSL_LIB_CTX = CStruct0

object OSSL_LIB_CTX:
  given _tag: Tag[OSSL_LIB_CTX] = Tag.materializeCStruct0Tag
