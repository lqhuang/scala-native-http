package java.net

// https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/FileNameMap.html
trait FileNameMap:
  def getContentTypeFor(fileName: String): String
