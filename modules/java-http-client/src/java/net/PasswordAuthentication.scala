package java.net

/**
 * The class `PasswordAuthentication` is a data holder that is used by [[java.net.Authenticator]].
 *
 * Reference:
 *
 *   - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/PasswordAuthentication.html
 */
final class PasswordAuthentication(username: String, password: Array[Char]):

  lazy val _password: Array[Char] = password.clone()

  def getUserName(): String = username

  def getPassword(): Array[Char] = _password.clone()
