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
