package snhttp.test.java.net.http

import java.net.http.{HttpClient, HttpRequest, HttpResponse}

import java.net.http.HttpClient.{Redirect, Version}

object ClientUtils:

  inline def withNewHttpClient[T](func: HttpClient => T): T =
    val client = HttpClient.newHttpClient()
    try func(client)
    finally client.close()

  inline def withNewHttpClient[T](policy: Redirect)(func: HttpClient => T): T =
    val client = HttpClient.newBuilder().followRedirects(policy).build()
    try func(client)
    finally client.close()
