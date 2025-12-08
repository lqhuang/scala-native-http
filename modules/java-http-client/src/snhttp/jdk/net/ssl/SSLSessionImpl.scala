package snhttp.jdk.net.ssl

import java.nio.ByteBuffer
import java.security.Principal
import java.security.cert.Certificate
import javax.net.ssl.{SSLSessionContext, SSLSession}
import javax.net.ssl.SSLPeerUnverifiedException

import snhttp.experimental.openssl.libssl

import snhttp.core.utils.PtrFinalizer

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
  PtrFinalizer(this, ptr, x => libssl.SSL_SESSION_free(x))

  def getId(): Array[Byte] =
    ???

  def getSessionContext(): SSLSessionContext =
    _sessContext

  def getCreationTime(): Long =
    ???

  def getLastAccessedTime(): Long = ???

  def invalidate(): Unit =
    ???

  def isValid(): Boolean =
    ???

  def putValue(name: String, value: AnyRef): Unit =
    ???

  def getValue(name: String): AnyRef =
    ???

  def removeValue(name: String): Unit =
    ???

  def getValueNames(): Array[String] =
    ???

  def getPeerCertificates(): Array[Certificate] =
    _peerCerts

  def getLocalCertificates(): Array[Certificate] =
    ???

  def getPeerPrincipal(): Principal =
    ???

  def getLocalPrincipal(): Principal =
    ???

  def getCipherSuite(): String =
    _cipherSuite

  def getProtocol(): String =
    _protocol

  def getPeerHost(): String =
    _host

  def getPeerPort(): Int =
    _port

  def getPacketBufferSize(): Int =
    ???

  def getApplicationBufferSize(): Int =
    ???

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
