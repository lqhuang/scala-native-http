package java.net

import java.io.IOException
import java.net.{InetSocketAddress, SocketAddress, URI, Proxy}
import java.util.List as JList
import java.util.Objects.requireNonNull

abstract class ProxySelector:
  def select(uri: URI): JList[Proxy]

  def connectFailed(uri: URI, sa: SocketAddress, ioe: IOException): Unit

object ProxySelector {

  @volatile private var defaultSelector: ProxySelector = null

  def getDefault(): ProxySelector =
    defaultSelector

  def setDefault(ps: ProxySelector): Unit =
    defaultSelector = ps

  /**
   * Returns a `ProxySelector` which uses the given proxy address for all HTTP and HTTPS requests.
   * If proxyAddress is null then proxying is disabled.
   */
  def of(proxyAddress: InetSocketAddress): ProxySelector =
    // If `proxyAddress` is null then proxying is disabled.
    new ProxySelectorImpl(proxyAddress)
}

end ProxySelector

private class ProxySelectorImpl(address: InetSocketAddress) extends ProxySelector:

  private val proxy =
    if address == null
    then Proxy.NO_PROXY
    else
      // TODO: how to decide between HTTP and SOCKS proxy?
      new Proxy(Proxy.Type.HTTP, address)

  def connectFailed(uri: URI, address: SocketAddress, exc: IOException): Unit =
    require(uri == null)
    require(address == null)
    require(exc == null)
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

end ProxySelectorImpl
