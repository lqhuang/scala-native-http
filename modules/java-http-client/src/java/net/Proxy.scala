package java.net

import java.lang._Enum
import java.util.Objects.requireNonNull

class Proxy(private val proxyType: Proxy.Type, private val sa: SocketAddress):

  requireNonNull(proxyType)
  if proxyType != Proxy.Type.DIRECT && !sa.isInstanceOf[InetSocketAddress] then
    throw new IllegalArgumentException(s"type $proxyType is not compatible with address $sa")

  if proxyType == Proxy.Type.DIRECT && sa != null then
    throw new IllegalArgumentException("DIRECT proxies must have a null address")

  def `type`(): Proxy.Type = proxyType

  def address(): SocketAddress = sa

  override def toString(): String =
    if proxyType == Proxy.Type.DIRECT
    then "DIRECT"
    else s"${proxyType} @ ${sa}"

  override def equals(obj: Any): Boolean = obj match
    case other: Proxy =>
      other.`type`() == this.`type`() && other.address() == this.address()
    case _ => false

  override def hashCode(): Int =
    if sa == null
    then proxyType.hashCode()
    else proxyType.hashCode() + 31 * sa.hashCode()

object Proxy:

  sealed class Type private (name: String, ordinal: Int) extends _Enum[Type](name, ordinal)
  object Type:
    final val DIRECT: Type = new Type("DIRECT", 0)
    final val HTTP: Type = new Type("HTTP", 1)
    final val SOCKS: Type = new Type("SOCKS", 2)

    def values(): Array[Type] = Array(DIRECT, HTTP, SOCKS)

    def valueOf(name: String): Type =
      name match
        case "DIRECT" => DIRECT
        case "HTTP"   => HTTP
        case "SOCKS"  => SOCKS
        case _        => throw new IllegalArgumentException(s"No enum constant Proxy.Type.${name}")
  end Type

  val NO_PROXY: Proxy = new Proxy(Type.DIRECT, null)

end Proxy
