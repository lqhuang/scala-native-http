package snhttp.experimental.openssl._libssl

import scala.scalanative.unsafe.*

object func_aliases:

  import _root_.snhttp.experimental.openssl._libssl.functions.{SSL_CTX_ctrl, SSL_ctrl}
  import _root_.snhttp.experimental.openssl._libssl.enumerations.{
    SSL_CTRL,
    SSL_SESS_CACHE,
    TLS_VERSION,
  }
  import _root_.snhttp.experimental.openssl._libssl.structs.{SSL, SSL_CTX}

  def SSL_CTX_set_min_proto_version(ctx: Ptr[SSL_CTX], version: TLS_VERSION): Size =
    SSL_CTX_ctrl(ctx, SSL_CTRL.SET_MIN_PROTO_VERSION, version.value, null)

  def SSL_CTX_set_max_proto_version(ctx: Ptr[SSL_CTX], version: TLS_VERSION): Size =
    SSL_CTX_ctrl(ctx, SSL_CTRL.SET_MAX_PROTO_VERSION, version.value, null)

  def SSL_CTX_set_session_cache_mode(ctx: Ptr[SSL_CTX], mode: SSL_SESS_CACHE): Size =
    SSL_CTX_ctrl(ctx, SSL_CTRL.SET_SESS_CACHE_MODE, mode.value, null)

  def SSL_CTX_sess_set_cache_size(ctx: Ptr[SSL_CTX], size: Size): Size =
    SSL_CTX_ctrl(ctx, SSL_CTRL.SET_SESS_CACHE_SIZE, size, null)

  def SSL_set_tlsext_host_name(ssl: Ptr[SSL], name: CString): Size =
    SSL_ctrl(ssl, SSL_CTRL.SET_TLSEXT_HOSTNAME, 0, name)
