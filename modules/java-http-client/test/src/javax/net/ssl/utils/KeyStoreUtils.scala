// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/testing/src/main/java/org/conscrypt/java/security/TestKeyStore.java

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

package org.conscrypt.javax.net.ssl

import java.security.KeyStore

object KeyStoreUtils:

  private final val CERT_HOSTNAME: String = "example.com"

  val ROOT_CA = ???
  val INTERMEDIATE_CA_EC = ???
  val INTERMEDIATE_CA = ???
  val SERVER = ???
  val SERVER_HOSTNAME = ???
  val CLIENT = ???
  val CLIENT_EC_RSA_CERTIFICATE = ???
  val CLIENT_EC_EC_CERTIFICATE = ???
  val CLIENT_CERTIFICATE = ???
  val rootCa2 = ???
  val INTERMEDIATE_CA_2 = ???
  val CLIENT_2 = ???

  /**
   * Create an empty KeyStore
   */
  def createKeyStore(): KeyStore = {
    val keyStore = KeyStore.getInstance(StandardNames.KEY_STORE_ALGORITHM)
    keyStore.load(null, null)
    keyStore
  }

  /**
   * Return a server keystore with a matched RSA certificate and private key as well as a CA
   * certificate.
   */
  def getServer(): KeyStore =
    ???

  /**
   * Return a server keystore with a matched RSA certificate with SAN hostname and private key as
   * well as a CA certificate.
   */
  def getServerHostname(): KeyStore =
    ???

  /**
   * Return the only private key in a TestKeyStore for the given algorithms. Throws
   * IllegalStateException if there are are more or less than one.
   */
  def getPrivateKey(
      keyAlgorithm: String,
      signatureAlgorithm: String,
  ): java.security.PrivateKey =
    ???
