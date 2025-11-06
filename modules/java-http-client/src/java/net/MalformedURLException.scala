package java.net

import java.io.IOException

class MalformedURLException(s: String) extends IOException(s):
  def this() = this(null)
