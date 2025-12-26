package javax.net.ssl

import java.nio.charset.StandardCharsets.US_ASCII
// import java.net.IDN // TODO: IDN implementation is missing
import java.util.Objects.requireNonNull
import java.util.regex.Pattern
import javax.net.ssl.StandardConstants

/**
 * Refs:
 *
 *   - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SNIHostName.html
 */
final class SNIHostName(private val hostname: String)
    extends SNIServerName(
      StandardConstants.SNI_HOST_NAME,
      requireNonNull(hostname).getBytes(US_ASCII),
    ):

  def this(encoded: Array[Byte]) =
    this(String(encoded, US_ASCII))

  /// Implementation notes from JDK docs:
  ///
  /// The hostname argument is illegal if it
  ///
  /// - hostname is empty,
  /// - hostname ends with a trailing dot,
  /// - hostname is not a valid Internationalized Domain Name (IDN) compliant with the RFC 3490 specification.
  if (hostname.isEmpty || hostname.endsWith("."))
    throw new IllegalArgumentException("hostname is empty or ends with a trailing dot")

  def getAsciiName(): String = new String(hostname.getBytes(US_ASCII), US_ASCII)

  override def equals(other: Any): Boolean =
    other match
      case that: SNIHostName => this.hostname.equalsIgnoreCase(that.hostname)
      case _                 => false

  override def hashCode(): Int =
    hostname.toLowerCase.hashCode()

  override def toString(): String =
    s"type=host_name (${StandardConstants.SNI_HOST_NAME}), value=${hostname}"

end SNIHostName

object SNIHostName:

  private class SNIHostNameMatcher(pattern: Pattern)
      extends SNIMatcher(StandardConstants.SNI_HOST_NAME):
    def matches(serverName: SNIServerName): Boolean =
      serverName match
        case sniHostName: SNIHostName =>
          pattern.matcher(sniHostName.getAsciiName()).matches()
        case _ =>
          throw new IllegalArgumentException(
            "serverName is not of the given server name type of hostname (0) type",
          )

  def createSNIMatcher(regex: String): SNIMatcher =
    requireNonNull(regex)
    val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
    new SNIHostNameMatcher(pattern)

end SNIHostName
