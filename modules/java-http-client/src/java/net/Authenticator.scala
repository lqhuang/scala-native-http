package java.net

import java.lang._Enum

/**
 * Reference:
 *
 *   - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/Authenticator.html
 */
abstract class Authenticator:

  final protected def getRequestingHost(): String = ???

  final protected def getRequestingSite(): InetAddress = ???

  final protected def getRequestingPort(): Int = ???

  final protected def getRequestingProtocol(): String = ???

  final protected def getRequestingPrompt(): String = ???

  final protected def getRequestingScheme(): String = ???

  protected def getPasswordAuthentication(): PasswordAuthentication

  protected def getRequestingURL(): URL

  protected def getRequestorType(): Authenticator.RequestorType

  def requestPasswordAuthenticationInstance(
      host: String,
      addr: InetAddress,
      port: Int,
      protocol: String,
      prompt: String,
      scheme: String,
      url: URL,
      reqType: Authenticator.RequestorType,
  ): PasswordAuthentication

object Authenticator:

  /** The type of the entity requesting authentication. */
  sealed class RequestorType private (name: String, ordinal: Int)
      extends _Enum[RequestorType](name, ordinal)
  object RequestorType:
    final val PROXY: RequestorType = new RequestorType("PROXY", 0)
    final val SERVER: RequestorType = new RequestorType("SERVER", 1)

    def values(): Array[RequestorType] = Array(PROXY, SERVER)

    def valueOf(name: String): RequestorType =
      name match
        case "PROXY"  => PROXY
        case "SERVER" => SERVER
        case _ =>
          throw new IllegalArgumentException(s"No enum constant RequestorType.${name}")
  end RequestorType

  // Since Java 9
  def setDefault(a: Authenticator): Unit = ???

  // Since Java 9
  def getDefault(): Authenticator = ???

  def requestPasswordAuthentication(
      addr: InetAddress,
      port: Int,
      protocol: String,
      prompt: String,
      scheme: String,
  ): PasswordAuthentication = ???

  def requestPasswordAuthentication(
      host: String,
      addr: InetAddress,
      port: Int,
      protocol: String,
      prompt: String,
      scheme: String,
  ): PasswordAuthentication = ???

  def requestPasswordAuthentication(
      host: String,
      addr: InetAddress,
      port: Int,
      protocol: String,
      prompt: String,
      scheme: String,
      url: URL,
      reqType: Authenticator.RequestorType,
  ): PasswordAuthentication = ???

  def requestPasswordAuthentication(
      authenticator: Authenticator,
      host: String,
      addr: InetAddress,
      port: Int,
      protocol: String,
      prompt: String,
      scheme: String,
      url: URL,
      reqType: Authenticator.RequestorType,
  ): PasswordAuthentication = ???

end Authenticator
