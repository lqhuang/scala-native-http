package java.net

import java.util.Objects.requireNonNull

class Proxy(private val proxyType: Proxy.Type, private val sa: Option[SocketAddress]) {
  def `type`(): Proxy.Type = proxyType

  def address(): SocketAddress = sa match {
    case Some(address) => address
    case None          => null
  }

  override def toString: String =
    if proxyType == Proxy.Type.DIRECT
    then "DIRECT"
    else s"${proxyType} @ ${address()}"

  override def equals(obj: Any): Boolean = obj match {
    case other: Proxy =>
      other.`type`() == this.`type`() && other.address() == this.address()
    case _ => false
  }

  override def hashCode(): Int = sa match {
    case Some(sa) => proxyType.hashCode() + sa.hashCode()
    case None     => proxyType.hashCode()
  }
}

object Proxy {
  enum Type extends Enum[Type] {
    case DIRECT, HTTP, SOCKS
  }

  val NO_PROXY: Proxy = new Proxy(Type.DIRECT, None)

  def apply(proxyType: Type, sa: SocketAddress): Proxy = {
    requireNonNull(proxyType, "proxy type can not be null")
    requireNonNull(sa, "socket address can not be null")

    if proxyType == Type.DIRECT || !sa.isInstanceOf[InetSocketAddress] then
      throw new IllegalArgumentException(s"type $proxyType is not compatible with address $sa")

    new Proxy(proxyType, Some(sa))
  }
}
