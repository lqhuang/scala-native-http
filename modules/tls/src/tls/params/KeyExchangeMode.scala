package snhttp.experimental.tls.params

enum KeyExchangeMode(val code: Short, val name: String):
  case PSK_KE extends KeyExchangeMode(0, "psk_ke")
  case PSK_DHE_KE extends KeyExchangeMode(1, "psk_dhe_ke")
