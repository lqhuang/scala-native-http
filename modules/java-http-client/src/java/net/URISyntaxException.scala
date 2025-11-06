package java.net

import java.util.Objects.requireNonNull

/// ## Refs
///
/// - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URISyntaxException.html
class URISyntaxException(input: String, reason: String, index: Int) extends Exception:

  requireNonNull(input)
  requireNonNull(reason)
  requireNonNull(index)
  require(index >= -1, "the error index cannot be less than -1")

  def this(input: String, reason: String) = this(input, reason, -1)

  def getInput(): String = input

  def getReason(): String = reason

  def getIndex(): Int = index

  // Returns a string describing the parse error. The resulting string consists
  // of the reason string followed by a colon character (':'), a space,
  // and the input string. If the error index is defined then the string
  // " at index " followed by the index, in decimal, is inserted after the
  // reason string and before the colon character.
  override def getMessage(): String = ???
