package snhttp.jdk.net.http

import java.nio.file.{OpenOption, Path, Paths}
import java.io.{File, InputStream}

object _TypeUtils {

  type BodyHandlers = String | Path | File | Array[Byte] | InputStream

}
