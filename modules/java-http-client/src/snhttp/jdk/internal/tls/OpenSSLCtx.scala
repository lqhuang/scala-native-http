package snhttp.jdk.internal.tls

import java.net.InetAddress

import java.security.PrivateKey
import javax.net.ssl.SSLSession

trait OpenSSLCtx:

  def initialize(
      host: String,
      privateKey: PrivateKey,
  ): Unit =
    ???

  def shutdownReceived: Boolean =
    // (SSL_get_shutdown(ctxPtr) & SSL_RECEIVED_SHUTDOWN) != 0
    ???

  def shutdownSent: Boolean =
    // (SSL_get_shutdown(ctxPtr) & SSL_SENT_SHUTDOWN) != 0
    ???

  def pendingReadableBytes: Int =
    ???

  def pendingWrittenBytes: Int =
    ???

  def maxSealOverhead: Int =
    ???

  def resumeFrom(session: SSLSession): Unit =
    ???
