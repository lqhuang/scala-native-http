# java.net Cookie Infrastructure

This directory contains a Scala Native implementation of the JDK `java.net` cookie APIs.

## Scope

The following types are implemented:

- `CookieHandler` — abstract base for cookie management
- `CookieManager` — concrete `CookieHandler` implementation
- `CookiePolicy` — acceptance policies (`ACCEPT_ALL`, `ACCEPT_NONE`, `ACCEPT_ORIGINAL_SERVER`)
- `CookieStore` — storage interface
- `InMemoryCookieStore` — thread-safe in-memory store (package-private)
- `HttpCookie` — cookie representation and parsing

## Integration

`HttpClient.Builder.cookieHandler(CookieHandler)` accepts any `CookieHandler`.  
`CookieUtils` (in `snhttp.jdk`) provides glue for request/response header processing.

## Semantics

- Parsing follows RFC 6265 (Netscape-style) and RFC 2965 (versioned cookies)
- Domain matching follows RFC 6265 § 5.1.3
- Path matching follows RFC 6265 § 5.1.4
- Expires parsing supports RFC 1123, RFC 850, and asctime formats
- Max-Age takes precedence over Expires

## Not Covered

- Persistent cookie storage
- Cookie jar serialization
- Public suffix list enforcement
- `HttpURLConnection` integration (separate module concern)

## Testing

Tests are located in `modules/java-http-client/test/src/snhttp/cookie/` and use utest.

### Test Results

```
$ ./mill modules.java-http-client.test snhttp.cookie.CookieManagerTests
+ snhttp.cookie.CookieManagerTests.get returns empty map when no cookies
+ snhttp.cookie.CookieManagerTests.put stores cookies from Set-Cookie header
+ snhttp.cookie.CookieManagerTests.get returns Cookie header for stored cookies
+ snhttp.cookie.CookieManagerTests.multiple cookies are joined with semicolon
+ snhttp.cookie.CookieManagerTests.put applies default domain from URI
+ snhttp.cookie.CookieManagerTests.put applies default path from URI
+ snhttp.cookie.CookieManagerTests.setCookiePolicy changes acceptance policy
+ snhttp.cookie.CookieManagerTests.ACCEPT_ALL policy accepts all cookies
+ snhttp.cookie.CookieManagerTests.malformed cookies are ignored
+ snhttp.cookie.CookieManagerTests.secure cookies are not sent over HTTP
+ snhttp.cookie.CookieManagerTests.expired cookies are not returned
+ snhttp.cookie.CookieManagerTests.CookieHandler.setDefault and getDefault
Tests: 12, Passed: 12, Failed: 0

$ ./mill modules.java-http-client.test snhttp.cookie.HttpClientCookieTests
+ snhttp.cookie.HttpClientCookieTests.HttpClient.Builder accepts CookieHandler
+ snhttp.cookie.HttpClientCookieTests.HttpClient without CookieHandler has empty Optional
+ snhttp.cookie.HttpClientCookieTests.HttpClient.Builder rejects null CookieHandler
+ snhttp.cookie.HttpClientCookieTests.CookieHandler can be retrieved after build
Tests: 4, Passed: 4, Failed: 0
```
