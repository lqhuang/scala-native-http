package java.net

import java.io.IOException
import java.net.{InetSocketAddress, SocketAddress, URI, Proxy}
import java.util.List as JList
import java.util.Objects.requireNonNull

import snhttp.jdk.net.{SystemProxySelectorImpl, StaticProxySelectorImpl}

abstract class ProxySelector:
  def select(uri: URI): JList[Proxy]

  def connectFailed(uri: URI, sa: SocketAddress, ioe: IOException): Unit

object ProxySelector:

  /**
   * The system-wide default proxy selector.
   */
  @volatile private var defaultSelector: ProxySelector =
    new SystemProxySelectorImpl()

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
    new StaticProxySelectorImpl(proxyAddress)

end ProxySelector
