package javax.net.ssl

import java.util.Arrays
import java.util.Objects.requireNonNull

// ref: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/net/ssl/SNIServerName.html
abstract class SNIServerName(
    private val nameType: Int,
    private val encoded: Array[Byte],
) {

  require(nameType >= 0, "Server name type cannot be less than zero")
  require(nameType <= 255, "Server name type cannot be greater than 255")
  requireNonNull(encoded)

  private val encodedCopied: Array[Byte] = encoded.clone()

  final def getType(): Int = nameType

  final def getEncoded(): Array[Byte] = encodedCopied.clone()

  override def equals(other: Any): Boolean = {
    if (this.getClass != other.getClass) return false
    if (this eq other.asInstanceOf[AnyRef]) return true

    val that = other.asInstanceOf[SNIServerName]
    this.nameType == that.getType() && Arrays.equals(
      this.encodedCopied,
      that.encodedCopied,
    )
  }

  override def hashCode(): Int =
    nameType.hashCode() + Arrays.hashCode(encodedCopied)

  // format: "type=<name type>, value=<name value>"
  //
  // examples:
  //
  // - "type=(31), value=77:77:77:2E:65:78:61:6D:70:6C:65:2E:63:6E"
  // - "type=host_name (0), value=77:77:77:2E:65:78:61:6D:70:6C:65:2E:63:6E
  override def toString(): String =
    if (nameType == StandardConstants.SNI_HOST_NAME) {
      s"type=host_name (0), value=${hex(encodedCopied)}"
    } else {
      s"type=($nameType), value=${hex(encodedCopied)}"
    }

  private def hex(bytes: Array[Byte]): String =
    if (bytes.length == 0) "(empty)"
    else bytes.map(x => "%02X".format(x)).mkString(":")
}

case class SNIServerNameImpl(
    private val nameType: Int,
    private val encoded: Array[Byte],
) extends SNIServerName(nameType, encoded)
