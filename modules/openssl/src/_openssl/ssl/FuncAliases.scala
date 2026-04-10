package snhttp.experimental.openssl
package _openssl.ssl

import scala.scalanative.unsafe.*

import _root_.snhttp.experimental.openssl._openssl.ssl.Enumerations.{
  SSL_CTRL,
  SSL_SESS_CACHE,
  TLS_VERSION,
}
import _root_.snhttp.experimental.openssl._openssl.ssl.Structs.{SSL, SSL_CTX}
import _root_.snhttp.experimental.openssl._openssl.ssl.Functions

private[openssl] trait FuncAliases(funcs: Functions):

  def SSL_CTX_set_min_proto_version(ctx: Ptr[SSL_CTX], version: TLS_VERSION): Size =
    funcs.SSL_CTX_ctrl(ctx, SSL_CTRL.SET_MIN_PROTO_VERSION, version.value, null)

  def SSL_CTX_get_min_proto_version(ctx: Ptr[SSL_CTX]): Size =
    funcs.SSL_CTX_ctrl(ctx, SSL_CTRL.GET_MIN_PROTO_VERSION, 0, null)

  def SSL_CTX_set_max_proto_version(ctx: Ptr[SSL_CTX], version: TLS_VERSION): Size =
    funcs.SSL_CTX_ctrl(ctx, SSL_CTRL.SET_MAX_PROTO_VERSION, version.value, null)

  def SSL_CTX_get_max_proto_version(ctx: Ptr[SSL_CTX]): Size =
    funcs.SSL_CTX_ctrl(ctx, SSL_CTRL.GET_MAX_PROTO_VERSION, 0, null)

  def SSL_CTX_set_session_cache_mode(ctx: Ptr[SSL_CTX], mode: SSL_SESS_CACHE): Size =
    funcs.SSL_CTX_ctrl(ctx, SSL_CTRL.SET_SESS_CACHE_MODE, mode.value, null)

  def SSL_CTX_sess_set_cache_size(ctx: Ptr[SSL_CTX], size: Size): Size =
    funcs.SSL_CTX_ctrl(ctx, SSL_CTRL.SET_SESS_CACHE_SIZE, size, null)

  def SSL_set_tlsext_host_name(ssl: Ptr[SSL], name: CString): Size =
    funcs.SSL_ctrl(ssl, SSL_CTRL.SET_TLSEXT_HOSTNAME, 0, name)
