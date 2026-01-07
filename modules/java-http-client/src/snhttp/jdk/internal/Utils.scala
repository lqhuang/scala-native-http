package snhttp.jdk.internal

import java.nio.charset.{Charset, StandardCharsets}
import java.nio.charset.UnsupportedCharsetException
import java.net.http.HttpHeaders
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

object Utils {
  private val CHARSET_PATTERN = Pattern.compile(
    "(?:^|;)\\s*\\bcharset\\s*=\\s*([^\\s;]+)\\s*",
    Pattern.CASE_INSENSITIVE,
  )
  /// Get the `Charset` from `Content-Type` field. Defaults to `UTF_8`
  ///
  /// Reference for `Content-type` header:
  ///
  /// 1. https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type
  /// 2. https://datatracker.ietf.org/doc/html/rfc7231
  def charsetFrom(headers: HttpHeaders): Charset = {
    val contentType = headers.firstValue("Content-Type")

    if contentType.isEmpty()
    then StandardCharsets.UTF_8
    else {
      val matcher = CHARSET_PATTERN.matcher(contentType.get())

      if matcher.find()
      then
        val trimed = matcher.group(1).trim().stripPrefix("\"").stripSuffix("\"")
        val charsetName = if trimed.isEmpty then None else Some(trimed)
        try
          Charset.forName(charsetName.getOrElse("utf-8"))
        catch {
          case err: UnsupportedCharsetException => StandardCharsets.UTF_8
        }
      else // if no charset specified, default to UTF-8
        StandardCharsets.UTF_8
    }
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
