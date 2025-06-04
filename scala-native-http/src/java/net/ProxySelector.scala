package java.net

import java.net.{InetSocketAddress, Proxy, URI}
import java.util.List
import java.util.Objects.requireNonNull
import java.io.IOException

abstract class ProxySelector {

  def select(uri: URI): List[Proxy]

  def connectFailed(uri: URI, sa: SocketAddress, ioe: IOException): Unit
}

private class StaticProxySelector(address: InetSocketAddress) extends ProxySelector {

  private val NO_PROXY_LIST = List.of(Proxy.NO_PROXY)

  private val list: List[Proxy] = {
    val p =
      if address == null then Proxy.NO_PROXY
      else new Proxy(Proxy.Type.HTTP, address)
    List.of(p)
  }

  def connectFailed(uri: URI, sa: SocketAddress, e: IOException): Unit =
    requireNonNull(uri, "uri can not be null")
    requireNonNull(sa, "socket address can not be null")
    requireNonNull(e, "exception can not be null")

  def select(uri: URI): List[Proxy] = {
    requireNonNull(uri, "uri can not be null")

    val scheme = uri.getScheme
    requireNonNull(scheme, "protocol can not be null")

    if scheme.toLowerCase == "http" || scheme.toLowerCase == "https"
    then list
    else NO_PROXY_LIST
  }
}

object ProxySelector {
  @volatile private var defaultSelector: Option[ProxySelector] = None

  def getDefault(): ProxySelector = defaultSelector.getOrElse(null)

  def setDefault(ps: ProxySelector): Unit =
    defaultSelector = Option(ps) match {
      case Some(selector) if selector != null => Some(selector)
      case _                                  => None
    }

  def of(proxyAddress: InetSocketAddress): ProxySelector = new StaticProxySelector(proxyAddress)
}
