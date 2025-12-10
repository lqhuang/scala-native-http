package snhttp.jdk.net.ssl

import java.nio.ByteBuffer
import java.security.Principal
import java.security.cert.Certificate
import javax.net.ssl.{SSLSessionContext, SSLSession}
import javax.net.ssl.SSLPeerUnverifiedException

import scala.scalanative.posix
import scala.scalanative.unsafe.{CChar, Ptr, alloc, fromCString}
import scala.scalanative.unsigned.UInt

import snhttp.utils.PointerFinalizer
import snhttp.experimental.openssl.libssl
import snhttp.experimental.openssl.libssl_internal.enumerations.TLS_VERSION
import scala.scalanative.unsafe.Zone

/// Implementation Notes:
///
/// map to OpenSSL `SSL_SESSION` features
class SSLSessionImpl(
    _sessContext: SSLSessionContext,
    // _id: Array[Byte],
    // _host: String,
    // _port: Int,
    // _cipherSuite: String,
    // _protocol: String,
    // _creationTime: Long,
    // _peerCerts: Array[Certificate],
) extends SSLSession:

  private val ptr = libssl.SSL_SESSION_new()
  PointerFinalizer(this, ptr, _ptr => libssl.SSL_SESSION_free(_ptr)): Unit

  def getId(): Array[Byte] =
    Zone {
      val lenPtr = alloc[UInt]()
      val ubytePtr = libssl.SSL_SESSION_get_id(ptr, lenPtr)
      val length = (!lenPtr).toInt

      val buffer = ByteBuffer.allocate(length)
      for i <- 0 until length do
        val byte = !(ubytePtr + i)
        buffer.put(i, byte.toByte)

      buffer.array()
    }

  def getSessionContext(): SSLSessionContext =
    _sessContext

  /// Return session creation time in milliseconds since epoch
  def getCreationTime(): Long =
    libssl.SSL_SESSION_get_time_ex(ptr).toLong * 1000L

  def getLastAccessedTime(): Long =
    ???

  def invalidate(): Unit =
    ???

  def isValid(): Boolean =
    posix.time.clock().toLong * 1000L
      < (getCreationTime() + getTimeout().toLong * 1000L)

  def putValue(name: String, value: AnyRef): Unit =
    ???

  def getValue(name: String): AnyRef =
    ???

  def removeValue(name: String): Unit =
    ???

  def getValueNames(): Array[String] =
    ???

  def getPeerCertificates(): Array[Certificate] =
    val x509Ptr = libssl.SSL_SESSION_get0_peer(ptr)
    ???

  def getLocalCertificates(): Array[Certificate] =
    ???

  def getPeerPrincipal(): Principal =
    ???

  def getLocalPrincipal(): Principal =
    ???

  def getCipherSuite(): String =
    val csPtr = libssl.SSL_SESSION_get0_cipher(ptr)
    if csPtr == null
    then null
    else libssl.SSL_CIPHER_standard_name(csPtr).toString()

  def getProtocol(): String =
    val ret = libssl.SSL_SESSION_get_protocol_version(ptr).toInt

    if ret == TLS_VERSION.TLS1_3.value then "TLSv1.3"
    else if ret == TLS_VERSION.TLS1_2.value then "TLSv1.2"
    else
      throw new RuntimeException(
        s"Get unsupported TLS version from SSLSession: ${ret} (openssl code value)",
      )

  def getPeerHost(): String =
    val sni = libssl.SSL_SESSION_get0_hostname(ptr)
    if sni == null
    then null
    else sni.toString()

  def getPeerPort(): Int =
    ???

  def getPacketBufferSize(): Int =
    ???

  def getApplicationBufferSize(): Int =
    ???

  /**
   * Extra methods beyond the SSLSession interface
   */

  /// Return session timeout in milliseconds
  def getTimeout(): Int =
    libssl.SSL_SESSION_get_timeout(ptr).toInt * 1000

  def isResumable(): Boolean =
    // `SSL_SESSION_is_resumable()` returns 1 if the session is resumable or 0 otherwise.
    libssl.SSL_SESSION_is_resumable(ptr) == 1

  def reused(): Boolean =
    // The following return values can occur:
    // 0: A new session was negotiated.
    // 1: A session was reused.
    libssl.SSL_SESSION_is_resumable(ptr) == 1

  def hasTicket(): Boolean =
    // returns 1 if there is a Session Ticket associated with this session, and 0 otherwise.
    libssl.SSL_SESSION_has_ticket(ptr) == 1

  /// Return session ticket lifetime hint in milliseconds
  def getTicketLifetimeHint(): Long =
    libssl.SSL_SESSION_get_ticket_lifetime_hint(ptr).toLong * 1000L

final protected class SSLNullSessionImpl extends SSLSession:
  private val INVALID_CIPHER_SUITE = "SSL_NULL_WITH_NULL_NULL"
  private val INVALID_PROTOCOL = "NONE"
  private val creationTime = System.currentTimeMillis()
  private val lastAccessedTime = creationTime

  def getId(): Array[Byte] =
    Array.emptyByteArray

  def getSessionContext(): SSLSessionContext =
    null

  def getCreationTime(): Long =
    creationTime

  def getLastAccessedTime(): Long =
    lastAccessedTime

  def invalidate(): Unit =
    ()

  def isValid(): Boolean =
    false

  def putValue(name: String, value: AnyRef): Unit =
    ()

  def getValue(name: String): AnyRef =
    throw new UnsupportedOperationException()

  def removeValue(name: String): Unit =
    throw new UnsupportedOperationException()

  def getValueNames(): Array[String] =
    throw new UnsupportedOperationException()

  def getPeerCertificates(): Array[Certificate] =
    throw new SSLPeerUnverifiedException("No peer certificate")

  def getLocalCertificates(): Array[Certificate] =
    throw new SSLPeerUnverifiedException("No local certificate")

  def getPeerPrincipal(): Principal =
    throw new SSLPeerUnverifiedException("No peer principal")

  def getLocalPrincipal(): Principal =
    throw new SSLPeerUnverifiedException("No local principal")

  def getCipherSuite(): String =
    INVALID_CIPHER_SUITE

  def getProtocol(): String =
    INVALID_PROTOCOL

  def getPeerHost(): String =
    null

  def getPeerPort(): Int =
    -1

  def getPacketBufferSize(): Int =
    0

  def getApplicationBufferSize(): Int =
    0
