package snhttp.jdk.net.ssl

import java.net.InetAddress
import java.net.SocketException
import java.util.List as JList
import java.util.function.BiFunction
import java.util.Objects.requireNonNull
import javax.net.ssl.{SSLParameters, SSLSession, SSLSocket, HandshakeCompletedListener}

import scala.scalanative.posix.sys.socket

import snhttp.experimental.openssl.{ssl, bio}
import snhttp.experimental.openssl.ssl_internal.constants

/**
 * SSL Socket Implementation
 *
 * map to OpenSSL `BIO` with non-blocking mode
 */
class ClientSSLSocketImpl protected (
    sslParams: SSLParametersImpl,
    addr: InetAddress,
    port: Int,
    localAddress: InetAddress = null,
    localPort: Int = 0,
    autoClose: Boolean = true,
) extends SSLSocket:

  private val af_inet = addr.getAddress().length match
    case 4  => socket.AF_INET
    case 16 => socket.AF_INET6
    case _  => throw new IllegalArgumentException("Invalid IP address length")
  private val sock = socket.socket(af_inet, socket.SOCK_STREAM, 0)
  // socket.bind(
  //   sock,
  // )
  if (sock < 0)
    throw new RuntimeException("Failed to create socket")
  // TODO: Test socket can connect to the host:port else we need to recreate the socket

  protected[ssl] val ptr = bio.BIO_new_socket(sock, if autoClose then 1 else 0)
  if (ptr == null)
    throw new RuntimeException("Failed to create new BIO object")

  // Setting the socket to be nonblocking
  val _set_nbio_ret = bio.BIO_socket_nbio(sock, 1)
  if (_set_nbio_ret != 1)
    throw new RuntimeException("Failed to set socket to non-blocking mode")

  // little tunings on the socket options can be done here
  val _ = bio.BIO_set_conn_mode(ptr, bio.BIO_SOCK.REUSEADDR)
  val _ = bio.BIO_set_conn_mode(ptr, bio.BIO_SOCK.KEEPALIVE)
  val _ = bio.BIO_set_conn_mode(ptr, bio.BIO_SOCK.NONBLOCK)

  /// This method will initiate the initial handshake if necessary and then
  /// block until the handshake has been established.
  ///
  /// If an error occurs during the initial handshake, this method returns
  /// an invalid session object which reports an invalid cipher suite of
  /// "SSL_NULL_WITH_NULL_NULL".
  def getSession(): SSLSession =
    ???

  override def getHandshakeSession(): SSLSession =
    ???

  def addHandshakeCompletedListener(listener: HandshakeCompletedListener): Unit =
    ???

  def removeHandshakeCompletedListener(listener: HandshakeCompletedListener): Unit =
    ???

  def startHandshake(): Unit =
    ???

  override def close(): Unit =
    // If autoClose is false, we should close the underlying socket manually
    if (!autoClose)
      val ret = socket.shutdown(sock, socket.SHUT_RDWR)
      if (ret != 0)
        throw new SocketException(
          s"Failed to close the socket (fd=${sock}) properly, errno=${ret}",
        )

  /**
   * Beyond standard SSLSocket methods
   */

  protected[ssl] def pendingReadableBytes: Long =
    bio.BIO_ctrl_pending(ptr).toLong

  protected[ssl] def pendingWrittenBytes: Long =
    bio.BIO_ctrl_wpending(ptr).toLong

  protected[ssl] def requestReadBufferSize: Long =
    bio.BIO_ctrl_get_read_request(ptr).toLong

  protected[ssl] def guaranteeWriteBufferSize: Long =
    bio.BIO_ctrl_get_write_guarantee(ptr).toLong

  /**
   * Helpers
   */

  private def validateArrayOfStrings(arr: Array[String]): Unit =
    if (arr == null)
      throw new IllegalArgumentException()
    if (arr.filter(each => each == null || each.isEmpty()).length > 0)
      throw new IllegalArgumentException()

  /**
   * Meaningless for socket functions, keep here for signatrue compatibility
   */

  def setUseClientMode(mode: Boolean): Unit =
    ???

  def getUseClientMode(): Boolean =
    ???

  def getSupportedCipherSuites(): Array[String] =
    ???

  def getEnabledCipherSuites(): Array[String] =
    ???

  def setEnabledCipherSuites(suites: Array[String]): Unit =
    // validateArrayOfStrings(suites)
    ???

  def getSupportedProtocols(): Array[String] =
    ???

  def getEnabledProtocols(): Array[String] =
    ???

  def setEnabledProtocols(protocols: Array[String]): Unit =
    // validateArrayOfStrings(protocols)
    ???

  /// server side feature is not supported yet
  def setNeedClientAuth(need: Boolean): Unit =
    ???

  /// server side feature is not supported yet
  def getNeedClientAuth(): Boolean =
    ???

  /// server side feature is not supported yet
  def setWantClientAuth(want: Boolean): Unit =
    ???

  /// server side feature is not supported yet
  def getWantClientAuth(): Boolean =
    ???

  def setEnableSessionCreation(flag: Boolean): Unit =
    ???

  def getEnableSessionCreation(): Boolean =
    ???

  override def getSSLParameters(): SSLParameters =
    ???

  override def setSSLParameters(params: SSLParameters): Unit =
    ???

  override def getApplicationProtocol(): String =
    ???

  override def getHandshakeApplicationProtocol(): String =
    ???

  override def setHandshakeApplicationProtocolSelector(
      selector: BiFunction[SSLSocket, JList[String], String],
  ): Unit =
    ???

  override def getHandshakeApplicationProtocolSelector()
      : BiFunction[SSLSocket, JList[String], String] = ???

object ClientSSLSocketImpl:

  def apply(sslParams: SSLParametersImpl): ClientSSLSocketImpl =
    throw new NotImplementedError("Not implemented yet")

  def apply(sslParams: SSLParametersImpl, host: String, port: Int): ClientSSLSocketImpl =
    requireNonNull(host)
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    val addr = InetAddress.getByName(host)
    new ClientSSLSocketImpl(sslParams, addr, port)

  def apply(sslParams: SSLParametersImpl, address: InetAddress, port: Int): ClientSSLSocketImpl =
    requireNonNull(port)
    require(port > -1 && port <= 65535)
    new ClientSSLSocketImpl(sslParams, address, port)

  // def apply(
  //     sslParams: SSLParametersImpl,
  //     host: String,
  //     port: Int,
  //     localHost: InetAddress,
  //     localPort: Int,
  // ): ClientSSLSocketImpl =
  //   requireNonNull(host)
  //   requireNonNull(port)
  //   require(port > -1 && port <= 65535)
  //   val addr = InetAddress.getByName(host)
  //   new ClientSSLSocketImpl(sslParams, addr, port)

  // def apply(
  //     sslParams: SSLParametersImpl,
  //     address: InetAddress,
  //     port: Int,
  //     localAddress: InetAddress,
  //     localPort: Int,
  // ): ClientSSLSocketImpl =
  //   requireNonNull(port)
  //   requireNonNull(localPort)
  //   require(port > -1 && port <= 65535)
  //   require(localPort >= 0 && localPort <= 65535)
  //   new ClientSSLSocketImpl(sslParams, address, port)

end ClientSSLSocketImpl
