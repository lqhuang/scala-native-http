/*
 * Copyright 2016 The Android Open Source Project
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

// Ported from https://github.com/google/conscrypt/blob/5b46bc69b6ee129b79c719cd8130a5fb4823a75a/common/src/test/java/org/conscrypt/javax/net/ssl/SNIHostNameTest.java

package org.conscrypt.javax.net.ssl

import java.util
import javax.net.ssl.SNIHostName
import javax.net.ssl.StandardConstants

import utest.{Tests, test, assert, assertThrows, TestSuite}

class SSLParametersTest extends TestSuite:

  val tests = Tests:

    def test_byteArray_Constructor(): Unit = {
      val idnEncoded: Array[Byte] = Array[Int](
        0xe4, 0xbb, 0x96, 0xe4, 0xbb, 0xac, 0xe4, 0xb8, 0xba, 0xe4, 0xbb, 0x80, 0xe4, 0xb9, 0x88,
        0xe4, 0xb8, 0x8d, 0xe8, 0xaf, 0xb4, 0xe4, 0xb8, 0xad, 0xe6, 0x96, 0x87,
      ).map(_.toByte)
      val hostName = new SNIHostName(idnEncoded)

      assert("xn--ihqwcrb4cv8a8dqg056pqjye" == hostName.getAsciiName())
      assert(StandardConstants.SNI_HOST_NAME == hostName.getType())
      assert(new String(idnEncoded) == new String(hostName.getEncoded()))
    }
