package snhttp.jdk.testkits

import java.net.http.{HttpClient, HttpHeaders}
import java.net.http.HttpResponse.{BodyHandlers, BodySubscribers, ResponseInfo}
import java.util.List as JList
import java.util.Map as JMap
import java.util.concurrent.Flow.{Subscriber, Subscription}

import snhttp.jdk.net.http.ResponseInfoImpl

object HttpResponseUtils:

  def createHeaders(map: Map[String, String]): HttpHeaders =
    val entries = map.map { case (k, v) => JMap.entry(k, JList.of(v)) }.toSeq
    val headerMap = JMap.ofEntries(entries*)
    HttpHeaders.of(headerMap, (_, _) => true)

  def createResponseInfo(map: Map[String, String] = Map.empty): ResponseInfo =
    ResponseInfoImpl(
      200,
      HttpClient.Version.HTTP_1_1,
      createHeaders(map),
    )
