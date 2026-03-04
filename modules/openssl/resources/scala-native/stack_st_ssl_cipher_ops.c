#include "openssl/ssl.h"

int snhttp_ossl_sk_SSL_CIPHER_num(struct stack_st_SSL_CIPHER* stack) {
  return sk_SSL_CIPHER_num(stack);
}

const SSL_CIPHER* snhttp_ossl_sk_SSL_CIPHER_value(
  struct stack_st_SSL_CIPHER* stack,
  int i
) {
  return sk_SSL_CIPHER_value(stack, i);
}
