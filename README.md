# Scala Native HTTP

Scala Native implementation for `java.net.http`.

This project is forked from [lolgab/scala-native-http-client-async](https://github.com/lolgab/scala-native-http-client-async) and restarted due to [com-lihaoyi/requests-scala#156](https://github.com/com-lihaoyi/requests-scala/issues/156) and [scala-native/scala-native#4104](https://github.com/scala-native/scala-native/issues/4104)

Still working in progress, any kind of help is welcome and appreciated.

`java.net.http` provides Provides high-level client interfaces to HTTP (versions 1.1 and 2) and low-level client interfaces to WebSocket. There are four main types defined:

- [x] `HttpClient`
- [x] `HttpRequest`
- [x] `HttpResponse`
- [ ] `WebSocket`

The development of first three types is the milestone in current stage.

There are also other auxiliary types are enforced by the Java Networking API and are missing in Scala Native core, such as:

1. Vendored by dependencies
   - [x] `java.time.Duration`
     - Provided by [cquiroz/scala-java-time](https://github.com/cquiroz/scala-java-time)
   - [x] `java.util.Locale`
     - Provided by [cquiroz/scala-java-locales](https://github.com/cquiroz/scala-java-locales)
   - [ ] `javax.net.ssl.SSLContext`
     - No implementation yet
     - May try to implement it in the future on upstream project [lolgab/scala-native-crypto](https://github.com/lolgab/scala-native-crypto)
2. Will try to include in this package
   - [x] `java.net.Proxy`
   - [x] `java.net.ProxySelector`
   - [ ] `java.net.Authenticator`
   - [ ] `java.net.CookieHandler`

## Development

This project is developed in Scala 3 and Scala Native 0.5.7 with mill build tool.

## Known Issues

Not all HTTP Client properties listed in [Java Networking Docs](https://docs.oracle.com/en/java/javase/24/core/java-networking.html#GUID-86B96A42-74FE-4E7D-8E60-D64A03862083) are supported, but we may try to implement them in the future. Currently supported properties are:

- [x] `jdk.httpclient.allowRestrictedHeaders`
- [ ] `jdk.httpclient.auth.retrylimit`
- [x] `jdk.httpclient.bufsize`
- [ ] `jdk.httpclient.connectionPoolSize`
- [ ] `jdk.httpclient.connectionWindowSize`
- [ ] `jdk.httpclient.disableRetryConnect`
- [ ] `jdk.httpclient.enableAllMethodRetry`
- [ ] `jdk.httpclient.enablepush`
- [ ] `jdk.httpclient.hpack.maxheadertablesize`
- [ ] `jdk.httpclient.HttpClient.log`
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

## Future

I'm thinking `curl` is probably not the minimal component for HTTP Client, so I may play to create serveral other bindings for Scala Native HTTP libraries, such as:

- [ ] `scala-native-brotli` for [google/brotli](https://github.com/google/brotli)
- [nghttp2/nghttp2](https://github.com/nghttp2/nghttp2)
- [ngtcp2/nghttp3](https://github.com/ngtcp2/nghttp3)
- [ada-url/ada](https://github.com/ada-url/ada)

That's should provide a more flexible , extensible HTTP Client for Scala Native with composability.
