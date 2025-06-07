package snhttp.internal

import java.nio.charset.{Charset, StandardCharsets}
import java.net.http.HttpHeaders
import java.util.regex.Pattern

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
  /// Get the `Charset` from `Content-type` field. Defaults to `UTF_8`
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
}
