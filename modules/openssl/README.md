# OpenSSL ssl binding

```sh
mkdir -p src/_libssl/
sn-bindgen --package snhttp.experimental.openssl._libssl --header /usr/include/openssl/ssl.h --scala --clang-include /usr/include --multi-file --out src/_libssl/ --flavour scala-native05

mkdir -p src/_libbio/
sn-bindgen --package snhttp.experimental.openssl._libbio --header /usr/include/openssl/bio.h --scala --multi-file --out src/_libbio/ --flavour scala-native05

# mkdir -p err_internal/
# sn-bindgen --package snhttp.experimental.openssl.err_internal --header /usr/include/openssl/err.h --scala --multi-file --out err_internal/

mkdir -p src/_libasn1/
sn-bindgen --package snhttp.experimental.openssl._libasn1 --header /usr/include/openssl/asn1.h --scala --multi-file --out src/_libasn1/ --flavour scala-native05

mkdir -p src/_libasn1t/
sn-bindgen --package snhttp.experimental.openssl._libasn1t --header /usr/include/openssl/asn1.h --scala --multi-file --out src/_libasn1t/ --flavour scala-native05
```
