package snhttp.experimental.curl
package _libcurl

import scala.scalanative.unsafe.name
import scala.scalanative.libc.stdint.int64_t

private[curl] object System:

  /**
   * =curl_off_t
   *
   * For any given platform/compiler `curl_off_t` MUST be typedef'ed to a 64-bit
   * wide signed integral data type. The width of this data type must remain
   * constant and independent of any possible large file support settings.
   *
   * As a general rule, `curl_off_t` shall not be mapped to `off_t`. This rule shall
   * only be violated if `off_t` is the only 64-bit data type available and the
   * size of `off_t` is independent of large file support settings. Keep your
   * build on the safe side avoiding an `off_t` gating. If you have a 64-bit
   * `off_t` then take for sure that another 64-bit data type exists, dig deeper
   * and you will find it.
   */
  @name("curl_off_t")
  type CurlOff = int64_t
