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

// Ported from https://github.com/google/conscrypt/blob/5b46bc69b6ee129b79c719cd8130a5fb4823a75a/common/src/test/java/org/conscrypt/javax/net/ssl/SSLParametersTest.java

import java.util.Collections
import java.util.List as JList
import java.util.ArrayList
import javax.net.ssl.{SNIHostName, SNIMatcher, SNIServerName, SSLParameters}

import utest.{Tests, test, assert, assertThrows, TestSuite}

class SSLParametersTest extends TestSuite:

  val tests = Tests:

    test("SSLParameters - empty constructor") {
      val p = new SSLParameters()
      assert(p.getCipherSuites() == null)
      assert(p.getProtocols() == null)
      assert(p.getWantClientAuth() == false)
      assert(p.getNeedClientAuth() == false)
    }

    test("SSLParameters - cipherSuites constructor") {
      val cipherSuites = Array[String]("foo", null, "bar")

      val p = new SSLParameters(cipherSuites)

      assert(p.getCipherSuites() != null)
      assert(!cipherSuites.equals(p.getCipherSuites()))
      assert(cipherSuites.sameElements(p.getCipherSuites()))
      assert(p.getProtocols() == null)
      assert(p.getWantClientAuth() == false)
      assert(p.getNeedClientAuth() == false)
    }

    test("SSLParameters - cpherSuites Protocols constructor") {
      val cipherSuites = Array[String]("foo", null, "bar")
      val protocols = Array[String]("baz", null, "qux")
      val p = new SSLParameters(cipherSuites, protocols)

      assert(p.getCipherSuites() != null)
      assert(p.getProtocols() != null)

      assert(!cipherSuites.equals(p.getCipherSuites()))
      assert(!protocols.equals(p.getProtocols()))

      assert(cipherSuites.sameElements(p.getCipherSuites()))
      assert(protocols.sameElements(p.getProtocols()))

      assert(p.getWantClientAuth() == false)
      assert(p.getNeedClientAuth() == false)
    }

    test("SSLParameters - CipherSuites") {
      val p = new SSLParameters()
      assert(p.getCipherSuites() == null)

      // confirm clone on input
      val cipherSuites = Array[String]("fnord")
      val copy = cipherSuites.clone()
      p.setCipherSuites(copy)
      copy(0) = null
      assert(cipherSuites.sameElements(p.getCipherSuites()))

      // confirm clone on output
      assert(!p.getCipherSuites().equals(p.getCipherSuites()))
    }

    test("SSLParameters - Protocols") {
      val p = new SSLParameters()
      assert(p.getProtocols() == null)

      // confirm clone on input
      val protocols = Array[String]("fnord")
      val copy = protocols.clone()
      p.setProtocols(copy)
      copy(0) = null
      assert(protocols.sameElements(p.getProtocols()))

      // confirm clone on output
      assert(!p.getProtocols().equals(p.getProtocols()))
    }

    test("SSLParameters - ClientAuth") {
      val p = new SSLParameters()
      assert(p.getWantClientAuth() == false)
      assert(p.getNeedClientAuth() == false)

      // confirm turning one on by itself
      p.setWantClientAuth(true)
      assert(p.getWantClientAuth())
      assert(p.getNeedClientAuth() == false)

      // confirm turning setting on toggles the other
      p.setNeedClientAuth(true)
      assert(p.getWantClientAuth() == false)
      assert(p.getNeedClientAuth())

      // confirm toggling back
      p.setWantClientAuth(true)
      assert(p.getWantClientAuth())
      assert(p.getNeedClientAuth() == false)
    }

    test("SSLParameters - setServerName duplicated name should throw Exception") {
      val p = new SSLParameters()
      val dupeNames = new ArrayList[SNIServerName](2)
      dupeNames.add(new SNIHostName("www.example.com"))
      dupeNames.add(new SNIHostName("www.example.com"))

      assertThrows[IllegalArgumentException]:
        p.setServerNames(dupeNames)
    }

    test("SSLParameters - setServerNames set/get null should throw Exception") {
      val p = new SSLParameters()
      p.setServerNames(Collections.singletonList(new SNIHostName("www.example.com")))
      assert(p.getServerNames() != null)

      p.setServerNames(null)
      assert(p.getServerNames() == null)
    }

    test("SSLParameters - setServerNames set/get null or empty should throws Exception") {
      val p = new SSLParameters()
      p.setServerNames(JList.of[SNIServerName]())
      val actual = p.getServerNames()

      assert(actual != null)
      assert(actual.size() == 0)
    }

    test("SSLParameters - getServerNames should be unmodifiable") {
      val p = new SSLParameters()
      p.setServerNames(Collections.singletonList(new SNIHostName("www.example.com")))
      val actual = p.getServerNames()

      assertThrows[UnsupportedOperationException]:
        actual.add(new SNIHostName("www.foo.com")): Unit
    }

    test("SSLParameters - setSNIMatchers duplicated names should throws Exception") {
      val p = new SSLParameters()
      val dupeMatchers = new ArrayList[SNIMatcher]()

      dupeMatchers.add(SNIHostName.createSNIMatcher("www\\.example\\.com"))
      dupeMatchers.add(SNIHostName.createSNIMatcher("www\\.example\\.com"))

      assertThrows[IllegalArgumentException]:
        p.setSNIMatchers(dupeMatchers)
    }

    test("SSLParameters - setSNIMatchers set/get null should throw Exception") {
      val p = new SSLParameters()
      p.setSNIMatchers(
        Collections.singletonList(SNIHostName.createSNIMatcher("www\\.example\\.com")),
      )
      assert(p.getSNIMatchers() != null)

      p.setSNIMatchers(null)
      assert(p.getSNIMatchers() == null)
    }

    test("SSLParameters - setSNIMatchers set/get an empty result should throw Exception") {
      val p = new SSLParameters()
      p.setSNIMatchers(
        Collections.singletonList(SNIHostName.createSNIMatcher("www\\.example\\.com")),
      )
      assert(p.getSNIMatchers().size() == 1)

      p.setSNIMatchers(Collections.emptyList[SNIMatcher]())
      val actual = p.getSNIMatchers()
      assert(actual != null)
      assert(actual.size() == 0)
    }

    test("SSLParameters - getSNIMatchers return should be unmodifiable") {
      val p = new SSLParameters()
      p.setSNIMatchers(
        Collections.singletonList(SNIHostName.createSNIMatcher("www\\.example\\.com")),
      )
      val actual = p.getSNIMatchers()

      assertThrows[UnsupportedOperationException]:
        actual.add(SNIHostName.createSNIMatcher("www\\.google\\.com")): Unit
    }
