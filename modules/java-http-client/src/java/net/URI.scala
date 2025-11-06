package java.net

import java.io.Serializable

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URI.html
final class URI private () extends Comparable[URI] with Serializable {

  def this(str: String) = this()

  def this(
      scheme: String,
      userInfo: String,
      host: String,
      port: Int,
      path: String,
      query: String,
      fragment: String,
  ) = this()

  def this(scheme: String, authority: String, path: String, query: String, fragment: String) =
    this()

  def this(scheme: String, host: String, path: String, fragment: String) = this()

  def this(scheme: String, ssp: String, fragment: String) = this()

  def parseServerAuthority(): URI = ???

  def normalize(): URI = ???

  def resolve(uri: URI): URI = ???

  def resolve(str: String): URI = ???

  def relativize(uri: URI): URI = ???

  def toURL(): URL = ???

  def getScheme(): String = ???

  def isAbsolute(): Boolean = ???

  def isOpaque(): Boolean = ???

  def getRawSchemeSpecificPart(): String = ???

  def getSchemeSpecificPart(): String = ???

  def getRawAuthority(): String = ???

  def getAuthority(): String = ???

  def getUserInfo(): String = ???

  def getRawUserInfo(): String = ???

  def getHost(): String = ???

  def getPort(): Int = ???

  def getRawPath(): String = ???

  def getPath(): String = ???

  def getRawQuery(): String = ???

  def getQuery(): String = ???

  def getRawFragment(): String = ???

  def getFragment(): String = ???

  def toASCIIString(): String = ???

  def compareTo(that: URI): Int = ???

  override def equals(obj: Any): Boolean = ???

  override def hashCode(): Int = ???

  override def toString(): String = ???

}

object URI {
  def create(str: String): URI = ???
}
