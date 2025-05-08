object RequestBuilder {

  def buildGetRequest(uri: String, headers: Map[String, String] = Map()): HttpRequest =
    HttpRequest
      .newBuilder()
      .uri(URI.create(uri))
      .method("GET", HttpRequest.BodyPublishers.noBody())
      .headers(headers.toSeq: _*)
      .build()

  def buildPostRequest(
      uri: String,
      body: String,
      headers: Map[String, String] = Map(),
  ): HttpRequest =
    HttpRequest
      .newBuilder()
      .uri(URI.create(uri))
      .method("POST", HttpRequest.BodyPublishers.ofString(body))
      .headers(headers.toSeq: _*)
      .build()

  def buildPutRequest(
      uri: String,
      body: String,
      headers: Map[String, String] = Map(),
  ): HttpRequest =
    HttpRequest
      .newBuilder()
      .uri(URI.create(uri))
      .method("PUT", HttpRequest.BodyPublishers.ofString(body))
      .headers(headers.toSeq: _*)
      .build()

  def buildDeleteRequest(uri: String, headers: Map[String, String] = Map()): HttpRequest =
    HttpRequest
      .newBuilder()
      .uri(URI.create(uri))
      .method("DELETE", HttpRequest.BodyPublishers.noBody())
      .headers(headers.toSeq: _*)
      .build()
}
