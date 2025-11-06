package java.net

trait ContentHandlerFactory:
  def createContentHandler(mimeType: String): ContentHandler
