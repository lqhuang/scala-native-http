package javax.net.ssl

import java.net.Socket
import java.security.cert.X509Certificate
import java.security.{Principal, PrivateKey}

trait X509KeyManager extends KeyManager:
  def getClientAliases(keyType: String, issuers: Array[Principal]): Array[String]

  def chooseClientAlias(
      keyType: Array[String],
      issuers: Array[Principal],
      socket: java.net.Socket,
  ): String

  def getServerAliases(keyType: String, issuers: Array[Principal]): Array[String]

  def chooseServerAlias(
      keyType: String,
      issuers: Array[Principal],
      socket: java.net.Socket,
  ): String

  def getCertificateChain(alias: String): Array[X509Certificate]

  def getPrivateKey(alias: String): PrivateKey
