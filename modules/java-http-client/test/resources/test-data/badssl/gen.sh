#!/bin/bash

set -eux -o pipefail

WORKING_DIR=$(dirname "$(realpath "$0")")
echo "Current working dir is ${WORKING_DIR}"

BADSSL_CLIENT_PKCS12_FILE="${WORKING_DIR}/badssl.com-client.p12"
BADSSL_CLIENT_PASSWORD=badssl.com

if [[ ! -f "${BADSSL_CLIENT_PKCS12_FILE}" ]]; then
  echo "badssl.com PKCS12 file ${BADSSL_CLIENT_PKCS12_FILE} does not exist."
  echo "  Downloading from badssl.com now ..."
  curl -fsSL -o "${BADSSL_CLIENT_PKCS12_FILE}" https://badssl.com/certs/badssl.com-client.p12
fi

# No password protected PKCS12 file
#
# Using current badssl.com-client.p12 file as the CA source,
# generate a new password less protected PKCS12 as client cert
#
# Inspired by <https://github.com/com-lihaoyi/requests-scala/blob/master/requests/test/resources/badssl.com-client.md>

BADSSL_CLIENT_CRT_FILE="${WORKING_DIR}/badssl.com.client-cert.pem"
# extract client cert from badssl PKCS12 file
openssl pkcs12 -legacy \
  -clcerts -nokeys \
  -in "${BADSSL_CLIENT_PKCS12_FILE}" -passin "pass:${BADSSL_CLIENT_PASSWORD}" \
  -out "${BADSSL_CLIENT_CRT_FILE}"

BADSSL_CA_CERT_FILE="${WORKING_DIR}/badssl.com.ca-cert.pem"
# extract CA cert from badssl PKCS12 file
openssl pkcs12 -legacy \
  -cacerts -nokeys \
  -in "${BADSSL_CLIENT_PKCS12_FILE}" -passin "pass:${BADSSL_CLIENT_PASSWORD}" \
  -out "${BADSSL_CA_CERT_FILE}"

BADSSL_CLIENT_PRIVATEKEY_FILE="${WORKING_DIR}/badssl.com.private-key.pem"
# extract private key from badssl PKCS12 file and remove password protection
openssl pkcs12 -legacy \
  -nocerts \
  -in "${BADSSL_CLIENT_PKCS12_FILE}" -passin "pass:${BADSSL_CLIENT_PASSWORD}" \
  -passout "pass:" \
  -out "${BADSSL_CLIENT_PRIVATEKEY_FILE}"

BADSSL_CLIENT_PRIVATEKEY_FILE_NOPASSWORD="${WORKING_DIR}/badssl.com.pkey-nopass.key"
# Remove password protection from the private key
openssl rsa \
  -in "${BADSSL_CLIENT_PRIVATEKEY_FILE}" \
  -out "${BADSSL_CLIENT_PRIVATEKEY_FILE_NOPASSWORD}" \
  -passin pass:

BADSSL_CLIENT_PKCS12_FILE_NOPASSWORD="${WORKING_DIR}/badssl.com-client-nopass.p12"
# Generate a new password less protected PKCS12 file
openssl pkcs12 -legacy \
  -export \
  -CAfile "${BADSSL_CA_CERT_FILE}" \
  -inkey "${BADSSL_CLIENT_PRIVATEKEY_FILE_NOPASSWORD}" \
  -in "${BADSSL_CLIENT_CRT_FILE}" \
  -out "${BADSSL_CLIENT_PKCS12_FILE_NOPASSWORD}"

rm "${BADSSL_CLIENT_CRT_FILE}" "${BADSSL_CA_CERT_FILE}" "${BADSSL_CLIENT_PRIVATEKEY_FILE}" "${BADSSL_CLIENT_PRIVATEKEY_FILE_NOPASSWORD}"
