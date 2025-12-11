# OpenSSL ssl binding

```sh
mkdir -p ssl_internal/
sn-bindgen --package snhttp.experimental.openssl.ssl_internal --header /usr/include/openssl/ssl.h --scala --c --clang-include /usr/include --multi-file --out ssl_internal/

mkdir -p bio_internal/
sn-bindgen --package snhttp.experimental.openssl.bio_internal --header /usr/include/openssl/bio.h --scala --c --multi-file --out bio_internal/

mkdir -p err_internal/
sn-bindgen --package snhttp.experimental.openssl.err_internal --header /usr/include/openssl/err.h --scala --c --multi-file --out err_internal/
```
