# OpenSSL ssl binding

```sh
mkdir -p ssl/
sn-bindgen --package snhttp.experimental.openssl.ssl_internal --header /usr/include/openssl/ssl.h --scala --c --clang-include /usr/include --multi-file --out ssl/

mkdir -p bio/
sn-bindgen --package snhttp.experimental.openssl.bio_internal --header /usr/include/openssl/bio.h --scala --c --multi-file --out bio/

mkdir -p err/
sn-bindgen --package snhttp.experimental.openssl.bio_internal --header /usr/include/openssl/bio.h --scala --c --multi-file --out err/
```
