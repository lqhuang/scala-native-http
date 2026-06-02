package snhttp.jdk.net.http.internal

import java.nio.file.Path
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.charset.UnsupportedCharsetException
import java.net.http.HttpHeaders
import java.util.Optional
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

object Utils:

  private final val CHARSET_PATTERN = Pattern.compile(
    "(?:^|;)\\s*\\bcharset\\s*=\\s*([^\\s;]+)\\s*",
    Pattern.CASE_INSENSITIVE,
  )

  /**
   * Get the `Charset` from `Content-Type` field. Defaults to `UTF_8`
   *
   * Reference for `Content-type` header:
   *
   *   1. https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type
   *   2. https://datatracker.ietf.org/doc/html/rfc7231
   */
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

  /**
   * Get the `filename` from `Content-Disposition` field.
   *
   * Reference for `Content-Disposition` header:
   *
   *   - https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition
   *   - https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/java/net/http/HttpResponse.BodyHandlers.html#ofFileDownload(java.nio.file.Path,java.nio.file.OpenOption...)
   *   - https://datatracker.ietf.org/doc/html/rfc6266
   *
   * The HTTP Content-Disposition header indicates whether content should be displayed inline in the
   * browser as a web page or part of a web page or downloaded as an attachment locally.
   *
   * Well-formed `Content-Disposition` header should look like:
   *
   * ```
   * Content-Disposition: attachment
   * Content-Disposition: attachment; filename="file name.jpg"
   * ```
   */
  def filenameFrom(
      headers: HttpHeaders,
  ): Optional[String] = {
    val maybeContentDisposition = headers.firstValue("Content-Disposition")

    maybeContentDisposition.flatMap { cd =>
      val parts = cd.split(";").map(_.trim())
      val isAttachment = parts.headOption.exists(_.equalsIgnoreCase("attachment"))
      val filename = parts.find(s => FILENAME_PARAM_PATTERN.matcher(s).find())

      if (!parts.headOption.exists(_.equalsIgnoreCase("attachment")) || filename.isEmpty)
        Optional.empty()
      else if (filename.isDefined) {
        parseFilename(filename.get)
      } //
      else
        Optional.empty()
    }
  }

  /*
   *  Helpers for `filenameFrom`
   */

  private final val FILENAME_PARAM_PATTERN =
    Pattern.compile("^filename\\s*=\\s*(.+?)\\s*$", Pattern.CASE_INSENSITIVE)

  private final val QUOTED_FILENAME_PATTERN =
    Pattern.compile("\"([^\"]+)\"", CASE_INSENSITIVE)

  private final val QUOTED_ILLEGAL_FILENAME_CHARACTERS: Set[Char] =
    Set('\'', '\n', '\t', '\r', '"', '\\')

  private final val UNQUOTE_ILLEGAL_FILENAME_CHARACTERS: Set[Char] =
    QUOTED_ILLEGAL_FILENAME_CHARACTERS ++ Set('?', '[', ']', '(', ')', '\\', '/', ' ')

  /**
   * Parse the `filename` parameter value, handling both quoted and unquoted tokens.
   */
  private def parseFilename(param: String): Optional[String] = {
    val m = FILENAME_PARAM_PATTERN.matcher(param)
    if !m.find()
    then //
      Optional.empty()
    else {
      val rawValue = m.group(1).trim().replaceAllLiterally("\\\\", "/")
      val quoted = QUOTED_FILENAME_PATTERN.matcher(rawValue)

      if (rawValue.isEmpty())
        Optional.empty()
      else if (quoted.find()) {
        Optional
          .ofNullable(Path.of(quoted.group(1).trim()).getFileName())
          .map(_.toString())
          .filter(name =>
            name != "." && name != ".." && !name.isEmpty()
              && !name.exists(QUOTED_ILLEGAL_FILENAME_CHARACTERS.contains(_)),
          )
      } //
      else {
        if rawValue == "." || rawValue == ".." || rawValue.isEmpty()
          || rawValue.exists(UNQUOTE_ILLEGAL_FILENAME_CHARACTERS.contains(_))
        then Optional.empty()
        else Optional.of(rawValue)
      }
    }
  }
