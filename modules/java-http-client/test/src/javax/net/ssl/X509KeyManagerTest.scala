/*
 * Copyright 2013 The Android Open Source Project
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

package snhttp.javax.net.ssl

import javax.net.ssl.{KeyManager, KeyManagerFactory, X509KeyManager}

import utest.{TestSuite, Tests, test}

import org.conscrypt.utils.KeyStoreUtils

class X509KeyManagerTest extends TestSuite:

  private def assertChooseClientAliasKeyType(
      clientKeyType: String,
      caKeyType: String,
      selectedKeyType: String,
      succeeds: Boolean,
  ): Unit = {
    val ca = new KeyStoreUtils.Builder().keyAlgorithms(Seq(caKeyType)).build()
    val client = new KeyStoreUtils.Builder()
      .keyAlgorithms(Seq(clientKeyType))
      .signer(ca.getPrivateKey(caKeyType, caKeyType))
      .build()

    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
    kmf.init(client.keyStore, client.keyPassword)

    val keyTypes = Array(selectedKeyType)
    val managers = kmf.getKeyManagers()
    for (manager <- managers)
      manager match {
        case km: X509KeyManager =>
          val alias = km.chooseClientAlias(keyTypes, null, null)
          if succeeds then assert(alias != null) else assert(alias == null)
        case _ => ()
      }
  }

  def tests: Tests = Tests:

    /**
     * Tests whether the key manager will select the right key when the CA is of one key type and
     * the client is of a possibly different key type.
     *
     * <p> There was a bug where EC was being interpreted as EC_EC and only accepting EC signatures
     * when it should accept any signature type.
     */
    test("Combinations for choosing client alias") {
      assertChooseClientAliasKeyType("RSA", "RSA", "RSA", succeeds = true)
      assertChooseClientAliasKeyType("RSA", "EC", "RSA", succeeds = true)
      assertChooseClientAliasKeyType("RSA", "EC", "EC", succeeds = false)

      assertChooseClientAliasKeyType("EC", "RSA", "EC_RSA", succeeds = true)
      assertChooseClientAliasKeyType("EC", "EC", "EC_RSA", succeeds = false)

      assertChooseClientAliasKeyType("EC", "EC", "EC_EC", succeeds = true)
      assertChooseClientAliasKeyType("EC", "RSA", "EC_EC", succeeds = false)

      assertChooseClientAliasKeyType("EC", "RSA", "RSA", succeeds = false)
    }
