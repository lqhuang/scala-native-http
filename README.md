# Scala Native HTTP

`java.net.http` Scala Native implementation based on cURL.

This project is forked from [lolgab/scala-native-http-client-async](https://github.com/lolgab/scala-native-http-client-async) and restarted due to [com-lihaoyi/requests-scala#156](https://github.com/com-lihaoyi/requests-scala/issues/156) and [scala-native/scala-native#4104](https://github.com/scala-native/scala-native/issues/4104)

Still working in progress, any kind of help is welcome and appreciated.

`java.net.http` provides Provides high-level client interfaces to HTTP (versions 1.1 and 2) and low-level client interfaces to WebSocket. There are four main types defined:

- `HttpClient`
- `HttpRequest`
- `HttpResponse`
- `WebSocket`

The development of first three types is the milestone in current stage.

## Development

This project is developed in Scala 3 and Scala Native 0.5.7 with mill build tool.

## Notes

This package also implement missing required classes in Scala Native

- `java.net.Proxy`
- `java.net.ProxySelector`

We may port them to Scala Native core in the future.

## Known Issues

Not all HTTP Client properties listed in [Java Networking Docs](https://docs.oracle.com/en/java/javase/24/core/java-networking.html#GUID-86B96A42-74FE-4E7D-8E60-D64A03862083) are supported, but we may try to implement them in the future. Currently supported properties are:

- [ ] `jdk.httpclient.allowRestrictedHeaders`
- [ ] `jdk.httpclient.auth.retrylimit`
- [ ] `jdk.httpclient.bufsize`
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
