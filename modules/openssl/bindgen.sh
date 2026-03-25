#!/bin/bash

set -eu -o pipefail

PACKAGE=snhttp.experimental.openssl
INCLUDE_DIR=/usr/include

OSSL_HEADERS=(
  asn1
  bio
  pkcs12
  ssl
  x509
  x509v3
)

for header in "${OSSL_HEADERS[@]}"; do
  output="src/_openssl/${header}/"
  mkdir -p "${output}"
  sn-bindgen --package "${PACKAGE}._openssl" --header "${INCLUDE_DIR}/openssl/${header}.h" \
    --scala --multi-file --flavour scala-native05 \
    --out "${output}"
done
