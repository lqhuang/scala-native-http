package snhttp.jdk.internal

import java.nio.charset.{Charset, StandardCharsets}
import java.net.http.HttpHeaders
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

object Utils {
  // def charsetFrom(headers: HttpHeaders): Charset = {
  //   val contentType = headers
  //     .firstValue("Content-Type")
  //     .orElse("text/html; charset=utf-8")

  //   val parts = contentType.split(";")
  //   if parts.isEmpty
  //   then StandardCharsets.UTF_8
  //   else {
  //     val charsetPart = parts
  //       .map(_.trim())
  //       .find(_.toLowerCase().startsWith("charset="))
  //       .getOrElse("charset=utf-8")

  //     val charsetName = charsetPart
  //       .split("=")
  //       .lastOption
  //       .map(_.trim())
  //       .getOrElse("utf-8")

  //     // TODO: Should we throw an exception if the charset is invalid?
  //     //       Or warn the user that the charset is not supported?
  //     //       For now, we return UTF-8 if the charset is invalid
  //     Try(Charset.forName(charsetName))
  //       .getOrElse(StandardCharsets.UTF_8)
  //   }
  // }

  private val CHARSET_PATTERN = Pattern.compile(
    ".*charset\\s*=\\s*([^\\s;]+).*",
    Pattern.CASE_INSENSITIVE,
  )
  /// Get the `Charset` from `Content-Type` field. Defaults to `UTF_8`
  ///
  /// Reference for `Content-type` header:
  ///   https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type
  def charsetFrom(headers: HttpHeaders): Charset = {
    val contentType = headers
      .firstValue("Content-Type")
      .orElse("text/html; charset=utf-8")
    val matcher = CHARSET_PATTERN.matcher(contentType)

    if matcher.matches()
    then
      val charsetName = Some(matcher.group(1).trim())
      // .replaceAll("^\"|\"$", "") // Remove "" quotes
      // .replaceAll("^'|'$", "") // Remove '' quotes
      Charset.forName(charsetName.getOrElse("utf-8"))
    else // if no charset specified, default to UTF-8
      StandardCharsets.UTF_8
  }

  // val DISPOSITION_TYPE = "attachment"
  // val PROHIBITED_PREFIX = Set(".", "..", "", "~", "|")
  // val FILENAME_PATTERN = Pattern.compile("filename\\s*=\\s*", CASE_INSENSITIVE)
  val QUOTED_FILENAME_PATTERN = Pattern.compile("\\s*\"([^\"]*)\"", CASE_INSENSITIVE)
  // Insprired from:
  //   - [sindresorhus/filenamify](https://github.com/sindresorhus/filenamify): Convert a string to a valid safe filename
  //   - https://labex.io/tutorials/nmap-how-to-sanitize-filenames-in-cybersecurity-419804
  val ILLEGAL_FILENAME_CHARACTERS =
    Set(
      '\\', '/', ':', '*', '?', '"', '<', '>', '|', '~', '#', '%', '&', '{', '}', '\n', '\t', '\r',
    )
  private def isAllowedInFilename(c: Char): Boolean = !ILLEGAL_FILENAME_CHARACTERS.contains(c)

  /**
   * Get the `filename` from `Content-Disposition` field.
   *
   * Reference for `Content-Disposition` header:
   *
   *   - https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition
   *
   * The HTTP Content-Disposition header indicates whether content should be displayed inline in the
   * browser as a web page or part of a web page or downloaded as an attachment locally.
   *
   * We assume that the `Content-Disposition` header is not `inline` here Well-formed
   * `Content-Disposition` header should look like:
   *
   * ```
   * Content-Disposition: attachment
   * Content-Disposition: attachment; filename="file name.jpg"
   * Content-Disposition: attachment; filename*=UTF-8''file%20name.jpg
   * ```
   *
   * TODO: current only supports `attachment; filename="file name.jpg"` like format.
   */
  def filenameFrom(headers: HttpHeaders, fallbackFilename: String = "downloaded-file"): String =

    val contentDisposition = headers
      .firstValue("Content-Disposition")
      .orElse(s"aattachment; filename=\"${fallbackFilename}\"")

    val maybeFilename = contentDisposition
      .split(";")
      .filter(_.toLowerCase.trim.startsWith("filename="))
      .headOption

    maybeFilename match
      case Some(unfilename) =>
        val matcher = QUOTED_FILENAME_PATTERN.matcher(unfilename)
        if matcher.matches() then {
          val valid = matcher.group(1).replaceAll("^\"|\"$", "").trim().translateEscapes()
          if valid.forall(isAllowedInFilename)
          then valid
          else fallbackFilename
        } // qualified filename not matches, return fallback
        else fallbackFilename
      case None => fallbackFilename

}
