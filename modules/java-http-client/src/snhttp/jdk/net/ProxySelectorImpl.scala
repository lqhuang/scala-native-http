package snhttp.jdk.net

import java.io.IOException
import java.net.{InetSocketAddress, SocketAddress, URI, Proxy, ProxySelector}
import java.util.List as JList
import java.util.Objects.requireNonNull

import snhttp.jdk.internal.PropertyUtils

class SystemProxySelectorImpl() extends ProxySelector:

  def connectFailed(uri: URI, address: SocketAddress, exc: IOException): Unit =
    require(uri != null)
    require(address != null)
    require(exc != null)
    throw new IOException(
      s"Failed to connect to proxy ${address} failed for URI ${uri}",
      exc,
    )

  def select(uri: URI): JList[Proxy] = {
    require(uri != null, "URI must not be null")
    val scheme = uri.getScheme()
    if (scheme == null) throw new IllegalArgumentException("URI scheme is null")
    val trimed = scheme.toLowerCase().trim()

    // TODO: implement system proxy detection
    JList.of(Proxy.NO_PROXY)
  }

end SystemProxySelectorImpl

class StaticProxySelectorImpl(address: InetSocketAddress) extends ProxySelector:

  private val proxy =
    if address == null
    then Proxy.NO_PROXY
    else new Proxy(Proxy.Type.HTTP, address)

  def connectFailed(uri: URI, address: SocketAddress, exc: IOException): Unit =
    require(uri != null)
    require(address != null)
    require(exc != null)
    throw new IOException(
      s"Failed to connect to proxy ${address} failed for URI ${uri}",
      exc,
    )

  def select(uri: URI): JList[Proxy] = {
    require(uri != null, "URI must not be null")
    val scheme = uri.getScheme()
    if (scheme == null) throw new IllegalArgumentException("URI scheme is null")
    val trimed = scheme.toLowerCase().trim()

    if trimed == "http" || trimed == "https"
    then JList.of(proxy)
    else JList.of(Proxy.NO_PROXY)
  }

end StaticProxySelectorImpl
