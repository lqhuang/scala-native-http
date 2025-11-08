package java.net

import java.io.IOException
import java.net.{InetSocketAddress, SocketAddress, URI}
import java.net.Proxy
import java.util.List
import java.util.Objects.requireNonNull

import scala.collection.mutable.HashMap

abstract class ProxySelector {
  def select(uri: URI): List[Proxy]
  def connectFailed(uri: URI, sa: SocketAddress, ioe: IOException): Unit
}

private class StaticProxySelector(address: InetSocketAddress) extends ProxySelector {
  private val proxies = HashMap[Option[SocketAddress], Proxy](
    if address == null
    then None -> Proxy.NO_PROXY
    else Some(address) -> Proxy(Proxy.Type.HTTP, address),
  )

  def connectFailed(uri: URI, sa: SocketAddress, e: IOException): Unit =
    requireNonNull(uri)
    requireNonNull(sa)
    requireNonNull(e)
    ???

  def select(uri: URI): List[Proxy] =
    requireNonNull(uri)
    val scheme = uri.getScheme()
    requireNonNull(scheme)

    if scheme.toLowerCase == "http" || scheme.toLowerCase == "https"
    then List.of(proxies.get(Some(address)).getOrElse(Proxy.NO_PROXY))
    else List.of(Proxy.NO_PROXY)
}

object ProxySelector {
  @volatile private var defaultSelector: Option[ProxySelector] = None

  def getDefault(): ProxySelector = defaultSelector.getOrElse(null)

  def setDefault(ps: ProxySelector): Unit =
    defaultSelector = Option(ps) match {
      case Some(selector) if selector != null => Some(selector)
      case _                                  => None
    }

  def of(proxyAddress: InetSocketAddress): ProxySelector =
    // proxyAddress is nullable, so we can not use requireNonNull
    new StaticProxySelector(proxyAddress)
}
