#!/bin/bash

# Generates PBE PKCS12 keystores with PBKDF2 and different MAC digest algorithms.

set -eux -o pipefail

WORKING_DIR=$(dirname "$(realpath "$0")")
echo "Current working dir is ${WORKING_DIR}"

NAME=test-trust

md=sha512
pbe=aes-256-cbc
SUBJECT="/CN=Test CA/O=Test"

CERT_FILE="${WORKING_DIR}/${NAME}.cert.pem"
PKEY_FILE="${WORKING_DIR}/${NAME}.pkey.pem"
PKCS_FILE="${WORKING_DIR}/${NAME}.p12"

PKCS12_PASSWORD="test-password"

# Create test private key and self signed ca
openssl req \
  -x509 -newkey rsa:8192 \
  -nodes \
  -days 36500 -subj "${SUBJECT}" \
  -keyout "${PKEY_FILE}" \
  -out "${CERT_FILE}"

# Wrap up content in pkcs12
openssl pkcs12 \
  -export \
  -certpbe "${pbe}" -keypbe "${pbe}" \
  -macalg "${md}" \
  -name "${NAME}" \
  -in "${CERT_FILE}" -inkey "${PKEY_FILE}" \
  -passout "pass:${PKCS12_PASSWORD}" -out "${PKCS_FILE}"
