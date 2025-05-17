package snhttp.internal

import java.nio.charset.Charset
import java.net.http.HttpHeaders

object Utils {

  /** Get the `Charset` from `Content-Encoding` field. Defaults to `UTF_8` */
  def charsetFrom(headers: HttpHeaders): Charset = ???
  // {
  //   val contentType = headers
  //     .firstValue("Content-type")
  //     .orElse("text/html; charset=utf-8");
  //   int.i = contentType.indexOf(";");
  //   if (i >= 0) contentType = contentType.substring(i + 1);
  //   try {
  //     HeaderParser.parser = new HeaderParser(contentType);
  //     String.value = parser.findValue("charset");
  //     if (value == null) return StandardCharsets.UTF_8;
  //     return Charset.forName(value);
  //   } catch
  //     Throwable.x {
  //       Log.logTrace("Can't find charset in \"{0}\" ({1})", contentType, x);
  //       return StandardCharsets.UTF_8;
  //     }
  // }

}
