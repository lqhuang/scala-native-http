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
