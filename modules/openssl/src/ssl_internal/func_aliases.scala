// scalafmt: { maxColumn = 200, align.preset = most }
package snhttp.experimental.openssl.ssl_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object func_aliases:

  import _root_.snhttp.experimental.openssl.ssl_internal.functions.*
  import _root_.snhttp.experimental.openssl.ssl_internal.enumerations.*
  import _root_.snhttp.experimental.openssl.ssl_internal.structs.*

  def SSL_CTX_set_min_proto_version(
      ctx: Ptr[SSL_CTX],
      version: TLS_VERSION,
  ): CLongInt =
    SSL_CTX_ctrl(
      ctx,
      SSL_CTRL.SET_MIN_PROTO_VERSION,
      version.value,
      null,
    )

  def SSL_CTX_set_max_proto_version(
      ctx: Ptr[SSL_CTX],
      version: TLS_VERSION,
  ): CLongInt =
    SSL_CTX_ctrl(
      ctx,
      SSL_CTRL.SET_MAX_PROTO_VERSION,
      version.value,
      null,
    )

  def SSL_CTX_set_session_cache_mode(
      ctx: Ptr[SSL_CTX],
      mode: SSL_SESS_CACHE,
  ): CLongInt =
    SSL_CTX_ctrl(
      ctx,
      SSL_CTRL.SET_SESS_CACHE_MODE,
      mode.value,
      null,
    )

  def SSL_CTX_sess_set_cache_size(
      ctx: Ptr[SSL_CTX],
      size: CLongInt,
  ): CLongInt =
    SSL_CTX_ctrl(
      ctx,
      SSL_CTRL.SET_SESS_CACHE_SIZE,
      size,
      null,
    )

  def SSL_set_tlsext_host_name(ssl: Ptr[SSL], name: CString): CLongInt =
    SSL_ctrl(
      ssl,
      SSL_CTRL.SET_TLSEXT_HOSTNAME,
      0,
      name,
    )
