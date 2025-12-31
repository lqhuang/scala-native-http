# Scala Native HTTP

![Scala 3 badge](https://img.shields.io/badge/Scala_3-DE3423?logo=scala)

Scala Native implementation for `java.net.http`.

This project is forked from [lolgab/scala-native-http-client-async](https://github.com/lolgab/scala-native-http-client-async) and restarted due to [com-lihaoyi/requests-scala#156](https://github.com/com-lihaoyi/requests-scala/issues/156) and [scala-native/scala-native#4104](https://github.com/scala-native/scala-native/issues/4104)

Still working in progress, any kind of help is welcome and appreciated.

`java.net.http` provides Provides high-level client interfaces to HTTP (versions 1.1 and 2) and low-level client interfaces to WebSocket. There are four main types defined:

- [x] `HttpClient`
- [x] `HttpRequest`
- [x] `HttpResponse`
- [ ] `WebSocket`

The development of first three types is the milestone in current stage.

Besides, TLS support is also a important part of HTTP Client, so we also implement `javax.net.ssl` related types as well.

- [x] `javax.net.ssl.SSLContext`

There are also other auxiliary types are enforced by the Java Networking API and are missing in Scala Native core, such as:

1. Vendored by dependencies
   - [x] `java.time.Duration`
     - Provided by [cquiroz/scala-java-time](https://github.com/cquiroz/scala-java-time)
   - [x] `java.util.Locale`
     - Provided by [cquiroz/scala-java-locales](https://github.com/cquiroz/scala-java-locales)
2. Will try to include in this package
   - [x] `java.net.Proxy`
   - [x] `java.net.ProxySelector`
   - [ ] `java.net.Authenticator`
   - [ ] `java.net.CookieHandler`

## Classes list for Java Http Client shims

Classes don't have checkbox mean they are implemented in Scala Native itself. Classes with checkbox and are checked mean they are implemented in this project.

- java.net
  - [ ] Authenticator
  - [ ] CacheRequest
  - [ ] CacheResponse
  - [ ] ContentHandler
  - [ ] ContentHandlerFactory
  - [ ] CookieHandler
  - [ ] CookieManager
  - [ ] CookiePolicy
  - [ ] CookieStore
  - [x] FileNameMap
  - [ ] HttpCookie
  - [x] HttpURLConnection
  - [ ] JarURLConnection
  - [ ] PasswordAuthentication
  - [x] Proxy
  - [x] ProxySelector
  - [ ] ResponseCache
  - [ ] SecureCacheResponse
  - URI
  - [x] URL
  - URLDecoder
  - URLEncoder
  - [x] URLConnection
  - [x] URLStreamHandler
  - [x] URLStreamHandlerFactory
  - Exceptions
    - [x] HttpRetryException
    - URISyntaxException (included in Scala Native, others in similar level are the same)
    - MalformedURLException
    - ProtocolException
    - UnknownHostException
    - UnknownServiceException
  - ssl
    - [x] SSLContext
    - [x] SSLParameters
- java.net.http
  - [x] HttpClient
  - [x] HttpHeaders
  - [x] HttpRequest
  - [x] HttpResponse
  - [ ] WebSocket
  - Exceptions
    - [x] HttpConnectTimeoutException
    - [x] HttpTimeoutException
    - [x] WebSocketHandshakeException

## Dev guides

This project is developed in Scala 3 and Scala Native 0.5.7 with mill build tool.

Please ensure you have `libcurl` installed.

- For Linux user with Debian / Ubuntu, the package name would be `libcurl4-openssl-dev`.
- For macOS user, install `curl` by Homebrew first.
- I haven't tested on other OS, you may need tune the `includeLibCurl` config in `build.mill` file.

In the current codebase, most implementations are layouted under `modules/java-http-client`, which will be published as `scala-native-java-http`. And the the binding for `curl` is included under `modules/java-http-client/src/snhttp/experimental/libcurl`. I will try to split to different sub modules in the future.

Serveral convenient recipes have been provided by `Makefile`, for example

- `make bsp`: Install `bsp` by `mill`
- `make install-mill-autocomp`: Run `mill.tabcomplete/install` and source the generated completions.
- `make fmt`: Run `scalafmt` over codebase
- `make compile`: Compile major codes
- `make test`: Run tests

Check `Makefile` for more.

## Known Issues

### Fixable (not supported yet)

- Client session resumption
- Custom SSL providers
- HTTP/1.1 connection pooling
- `java.net.IDN` is not implemented yet, so non-ASCII hostnames (ponycode) may not work as expected
  - influence `SNIHostName` then `SNIMatcher`
- Streaming responses

### Unfixable

### Properties support

Not all HTTP Client properties listed in

- [Java Networking Docs](https://docs.oracle.com/en/java/javase/24/core/java-networking.html#GUID-86B96A42-74FE-4E7D-8E60-D64A03862083)
- [Module `java.net.http` - System properties used by the `java.net.http` API](https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/module-summary.html)

are well supported, but we may try to implement them in the future. Currently supported properties are:

- [x] `java.net.useSystemProxies`
- [x] `jdk.httpclient.allowRestrictedHeaders`
- [ ] `jdk.httpclient.auth.retrylimit`
- [x] `jdk.httpclient.bufsize`
- [ ] `jdk.httpclient.connectionPoolSize`
- [ ] `jdk.httpclient.connectionWindowSize`
- [ ] `jdk.httpclient.disableRetryConnect`
- [ ] `jdk.httpclient.enableAllMethodRetry`
- [ ] `jdk.httpclient.enablepush`
- [ ] `jdk.httpclient.hpack.maxheadertablesize`
- `jdk.httpclient.HttpClient.log`
- [ ] `jdk.httpclient.keepalive.timeout`
- [ ] `jdk.httpclient.keepalive.timeout.h2`
- [ ] `jdk.httpclient.maxframesize`
- [ ] `jdk.httpclient.maxLiteralWithIndexing`
- [ ] `jdk.httpclient.maxNonFinalResponses`
- [ ] `jdk.httpclient.maxstreams`
- [ ] `jdk.httpclient.receiveBufferSize`
- [ ] `jdk.httpclient.redirects.retrylimit`
- [ ] `jdk.httpclient.sendBufferSize`
- [ ] `jdk.httpclient.websocket.writeBufferSize`
- [ ] `jdk.httpclient.windowsize`
- [ ] `jdk.internal.httpclient.disableHostnameVerification`
- [ ] `jdk.tls.client.SignatureSchemes`
- `jdk.tls.server.SignatureSchemes`
- [ ] `jdk.tls.namedGroups`

## Future

I'm thinking `curl` is probably not the **minimal** component for HTTP Client, so I may play to create serveral other bindings for Scala Native HTTP libraries, such as:

- [ ] `scala-native-brotli` package for [google/brotli](https://github.com/google/brotli)
- [ ] `scala-native-ada` package for [ada-url/ada](https://github.com/ada-url/ada)
- [ ] [nghttp2/nghttp2](https://github.com/nghttp2/nghttp2)
- [ ] [ngtcp2/nghttp3](https://github.com/ngtcp2/nghttp3)

That's should provide a more flexible , extensible HTTP Client for Scala Native with composability.
