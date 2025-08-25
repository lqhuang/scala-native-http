package javax.net.ssl

import java.util.Collection

abstract class SNIMatcher(private val nameType: Int) {

  if (nameType < 0) {
    throw new IllegalArgumentException(
      "Server name type cannot be less than zero",
    )
  } else if (nameType > 255) {
    throw new IllegalArgumentException(
      "Server name type cannot be greater than 255",
    )
  }

  final def getType(): Int = nameType

  def matches(serverName: SNIServerName): Boolean

}
