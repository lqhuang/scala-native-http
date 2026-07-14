# Http Cookie

Design Document for Java Http Client Cookie support in Scala Native.

written by [Rishi Jat (@rishi-jat)](https://github.com/rishi-jat)

## Scope

The following types are implemented:

- `CookieHandler` - abstract base for cookie management
- `CookieManager` - concrete `CookieHandler` implementation
- `CookiePolicy` - acceptance policies (`ACCEPT_ALL`, `ACCEPT_NONE`, `ACCEPT_ORIGINAL_SERVER`)
- `CookieStore` - storage interface
- `InMemoryCookieStore` - thread-safe internal in-memory store (`snhttp.jdk.net`)
- `HttpCookie` - cookie representation and parsing

## Integration

`HttpClient.Builder.cookieHandler(CookieHandler)` accepts any `CookieHandler`.
`CookieManager` handles request/response cookie header processing.

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
