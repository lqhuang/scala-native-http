package snhttp.test.java.net.http

import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect

object ClientUtils:

  // Before JDK 21, HttpClient has no explicit shutdown or close API.
  def closeClient(client: HttpClient): Unit = ()

  inline def withNewHttpClient[T](func: HttpClient => T): T =
    val client = HttpClient.newHttpClient()
    try func(client)
    finally closeClient(client)

  inline def withNewHttpClient[T](policy: Redirect)(func: HttpClient => T): T =
    val client = HttpClient.newBuilder().followRedirects(policy).build()
    try func(client)
    finally closeClient(client)
