package java.net

import java.util.Objects.requireNonNull

class Proxy(private val proxyType: Proxy.Type, private val sa: SocketAddress) {
  def `type`(): Proxy.Type = proxyType

  def address(): SocketAddress = sa

  override def toString(): String =
    if proxyType == Proxy.Type.DIRECT
    then "DIRECT"
    else s"${proxyType} @ ${sa}"

  override def equals(obj: Any): Boolean = obj match {
    case other: Proxy =>
      other.`type`() == this.`type`() && other.address() == this.address()
    case _ => false
  }

  override def hashCode(): Int =
    if sa == null
    then proxyType.hashCode()
    else proxyType.hashCode() + 31 * sa.hashCode()
}

object Proxy {
  enum Type extends Enum[Type] {
    case DIRECT, HTTP, SOCKS
  }

  val NO_PROXY: Proxy = new Proxy(Type.DIRECT, null)

  def apply(proxyType: Type, sa: SocketAddress): Proxy = {
    requireNonNull(proxyType)

    if proxyType != Type.DIRECT && !sa.isInstanceOf[InetSocketAddress] then
      throw new IllegalArgumentException(s"type $proxyType is not compatible with address $sa")

    new Proxy(proxyType, sa)
  }
}
