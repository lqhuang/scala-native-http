// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/testing/src/main/java/org/conscrypt/javax/net/ssl/TestKeyManager.java

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.conscrypt.utils

import java.io.PrintStream
import java.net.Socket
import java.security.{Principal, PrivateKey}
import java.security.cert.X509Certificate

import javax.net.ssl.{KeyManager, SSLEngine, X509ExtendedKeyManager}

/**
 * MockKeyManager is a simple proxy class that wraps an existing X509ExtendedKeyManager to provide
 * debug logging and recording of values.
 */
final class MockKeyManager(keyManager: X509ExtendedKeyManager) extends X509ExtendedKeyManager:

  import MockKeyManager.out

  out.println("MockKeyManager.<init> keyManager=" + keyManager)

  override def chooseClientAlias(
      keyTypes: Array[String],
      issuers: Array[Principal],
      socket: Socket,
  ): String = {
    out.print("MockKeyManager.chooseClientAlias")
    out.print(" | keyTypes: ")
    for (keyType <- keyTypes) {
      out.print(keyType)
      out.print(' ')
    }

    dumpIssuers(issuers)
    dumpSocket(socket)
    assertKeyTypes(keyTypes)

    dumpAlias(keyManager.chooseClientAlias(keyTypes, issuers, socket))
  }

  private def assertKeyTypes(keyTypes: Array[String]): Unit =
    for (keyType <- keyTypes) assertKeyType(keyType)

  private def assertKeyType(keyType: String): Unit =
    if (!StandardNames.KEY_TYPES.contains(keyType))
      throw new AssertionError("Unexpected key type " + keyType)

  override def chooseServerAlias(
      keyType: String,
      issuers: Array[Principal],
      socket: Socket,
  ): String = {
    out.print("MockKeyManager.chooseServerAlias")
    out.print(" | keyType: ")
    out.print(keyType)
    out.print(' ')
    dumpIssuers(issuers)
    dumpSocket(socket)

    assertKeyType(keyType)

    dumpAlias(keyManager.chooseServerAlias(keyType, issuers, socket))
  }

  private def dumpSocket(socket: Socket): Unit = {
    out.print(" | socket: ")
    out.print(String.valueOf(socket))
  }

  private def dumpIssuers(issuers: Array[Principal]): Unit = {
    out.print(" | issuers: ")
    if (issuers == null) {
      out.print("null")
      return
    }
    for (issuer <- issuers) {
      out.print(issuer)
      out.print(' ')
    }
  }

  private def dumpAlias(alias: String): String = {
    out.print(" => ")
    out.println(alias)
    alias
  }

  override def getCertificateChain(alias: String): Array[X509Certificate] = {
    out.print("MockKeyManager.getCertificateChain")
    out.print(" | alias: ")
    out.print(alias)
    dumpCerts(keyManager.getCertificateChain(alias))
  }

  private def dumpCerts(certs: Array[X509Certificate]): Array[X509Certificate] = {
    out.print(" => ")
    for (cert <- certs) {
      out.print(cert.getSubjectDN)
      out.print(' ')
    }
    out.println()
    certs
  }

  override def getClientAliases(keyType: String, issuers: Array[Principal]): Array[String] = {
    out.print("MockKeyManager.getClientAliases")
    out.print(" | keyType: ")
    out.print(keyType)
    dumpIssuers(issuers)
    assertKeyType(keyType)
    dumpAliases(keyManager.getClientAliases(keyType, issuers))
  }

  override def getServerAliases(keyType: String, issuers: Array[Principal]): Array[String] = {
    out.print("MockKeyManager.getServerAliases")
    out.print(" | keyType: ")
    out.print(keyType)
    dumpIssuers(issuers)
    assertKeyType(keyType)
    dumpAliases(keyManager.getServerAliases(keyType, issuers))
  }

  private def dumpAliases(aliases: Array[String]): Array[String] = {
    out.print(" => ")
    for (alias <- aliases) {
      out.print(alias)
      out.print(' ')
    }
    out.println()
    aliases
  }

  override def getPrivateKey(alias: String): PrivateKey = {
    out.print("MockKeyManager.getPrivateKey")
    out.print(" | alias: ")
    out.print(alias)
    val pk = keyManager.getPrivateKey(alias)
    out.print(" => ")
    out.println(String.valueOf(pk))
    pk
  }

  override def chooseEngineClientAlias(
      keyTypes: Array[String],
      issuers: Array[Principal],
      e: SSLEngine,
  ): String = {
    out.print("MockKeyManager.chooseEngineClientAlias")
    out.print(" | keyTypes: ")
    for (keyType <- keyTypes) {
      out.print(keyType)
      out.print(' ')
    }
    dumpIssuers(issuers)
    dumpEngine(e)
    assertKeyTypes(keyTypes)
    dumpAlias(keyManager.chooseEngineClientAlias(keyTypes, issuers, e))
  }

  override def chooseEngineServerAlias(
      keyType: String,
      issuers: Array[Principal],
      e: SSLEngine,
  ): String = {
    out.print("MockKeyManager.chooseEngineServerAlias")
    out.print(" | keyType: ")
    out.print(keyType)
    out.print(' ')
    dumpIssuers(issuers)
    dumpEngine(e)
    assertKeyType(keyType)
    dumpAlias(keyManager.chooseEngineServerAlias(keyType, issuers, e))
  }

  private def dumpEngine(engine: SSLEngine): Unit = {
    out.print(" | engine: ")
    out.print(String.valueOf(engine))
  }

object MockKeyManager:

  private val LOG = false
  private val out: PrintStream = if (LOG) System.out else new NullPrintStream()

  def wrap(keyManagers: Array[KeyManager]): Array[KeyManager] = {
    val result = keyManagers.clone()
    for (i <- result.indices)
      result(i) = wrap(result(i))
    result
  }

  def wrap(keyManager: KeyManager): KeyManager = keyManager match {
    case ekm: X509ExtendedKeyManager => new MockKeyManager(ekm)
    case _                           => keyManager
  }

end MockKeyManager
