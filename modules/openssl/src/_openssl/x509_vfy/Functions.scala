package snhttp.experimental.openssl
package _openssl.x509_vfy

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.x509.Types.X509_VERIFY_PARAM
import _root_.snhttp.experimental.openssl._common.size_t

@extern
private[openssl] trait Functions:

  /**
   * [bindgen] header: /usr/include/openssl/x509_vfy.h
   */
  def X509_VERIFY_PARAM_set1_host(
      param: Ptr[X509_VERIFY_PARAM],
      name: CString | Null,
      namelen: size_t,
  ): CInt =
    extern
