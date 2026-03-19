// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/common/src/test/java/org/conscrypt/TrustManagerImplTest.java

/*
 * Copyright (C) 2011 The Android Open Source Project
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
package snhttp.jdk.net.ssl

import java.io.IOException
import java.security.{KeyStore, Principal}
import java.security.cert.{Certificate, CertificateException, X509Certificate}
import java.util.{Arrays, List as JList}

import javax.net.ssl.{
  SSLParameters,
  SSLPeerUnverifiedException,
  SSLSession,
  SSLSessionContext,
  SSLSocket,
  X509TrustManager,
}

import utest.{TestSuite, Tests, test}

import org.conscrypt.utils.{FakeSSLSession, FakeSSLSocket, MockHostnameVerifier, KeyStoreUtils}

import _root_.snhttp.jdk.net.ssl.TrustManagerImpl

class TrustManagerImplTest extends TestSuite:

  private def trustManager(ca: X509Certificate): X509TrustManager = {
    val keyStore = KeyStoreUtils.createKeyStore()
    keyStore.setCertificateEntry("alias", ca)
    new TrustManagerImpl(keyStore)
  }

  private def assertValid(chain: Array[X509Certificate], tm: X509TrustManager): Unit =
    tm match {
      case tmi: TrustManagerImpl => tmi.checkServerTrusted(chain, "RSA")
      case _                     => ()
    }
    tm.checkServerTrusted(chain, "RSA")

  private def assertInvalid(chain: Array[X509Certificate], tm: X509TrustManager): Unit = {
    try {
      tm.checkClientTrusted(chain, "RSA")
      throw new AssertionError("Expected CertificateException")
    } catch {
      case _: CertificateException => // Expected.
    }
    try {
      tm.checkServerTrusted(chain, "RSA")
      throw new AssertionError("Expected CertificateException")
    } catch {
      case _: CertificateException => // Expected.
    }
  }

  def tests: Tests = Tests:
    /**
     * Ensure that our non-standard behavior of learning to trust new intermediate CAs does not
     * regress. http://b/3404902
     */
    test("testLearnIntermediate") {
      // TestUtils.assumeExtendedTrustManagerAvailable()

      // chain3 should be server/intermediate/root
      val pke: KeyStore.PrivateKeyEntry = KeyStoreUtils.getServer().getPrivateKey("RSA", "RSA")
      val chain3: Array[X509Certificate] =
        pke.getCertificateChain().asInstanceOf[Array[X509Certificate]]
      val root: X509Certificate = chain3(2)
      val intermediate: X509Certificate = chain3(1)
      val server: X509Certificate = chain3(0)
      val chain2 = Array[X509Certificate](server, intermediate)
      val chain1 = Array[X509Certificate](server)

      // Normal behavior
      assertValid(chain3, trustManager(root))
      assertValid(chain2, trustManager(root))
      assertInvalid(chain1, trustManager(root))
      assertValid(chain3, trustManager(intermediate))
      assertValid(chain2, trustManager(intermediate))
      assertValid(chain1, trustManager(intermediate))
      assertValid(chain3, trustManager(server))
      assertValid(chain2, trustManager(server))
      assertValid(chain1, trustManager(server))

      // non-standard behavior
      val tm: X509TrustManager = trustManager(root)
      // fail on short chain with only root trusted
      assertInvalid(chain1, tm)
      // succeed on longer chain, learn intermediate
      assertValid(chain2, tm)
      // now we can validate the short chain
      assertValid(chain1, tm)
    }

    // We should ignore duplicate cruft in the certificate chain
    // See https://code.google.com/p/android/issues/detail?id=52295 http://b/8313312
    test("testDuplicateInChain") {
      // TestUtils.assumeExtendedTrustManagerAvailable()

      // chain3 should be server/intermediate/root
      val pke: KeyStore.PrivateKeyEntry = KeyStoreUtils.getServer().getPrivateKey("RSA", "RSA")
      val chain3: Array[X509Certificate] =
        pke.getCertificateChain.asInstanceOf[Array[X509Certificate]]
      val root: X509Certificate = chain3(2)
      val intermediate: X509Certificate = chain3(1)
      val server: X509Certificate = chain3(0)

      val chain4 = Array[X509Certificate](server, intermediate, server, intermediate)
      assertValid(chain4, trustManager(root))
    }

  test("testGetFullChain") {
    // TestUtils.assumeExtendedTrustManagerAvailable()

    // build the trust manager
    val pke: KeyStore.PrivateKeyEntry = KeyStoreUtils.getServer().getPrivateKey("RSA", "RSA")
    val chain3: Array[X509Certificate] =
      pke.getCertificateChain.asInstanceOf[Array[X509Certificate]]
    val root: X509Certificate = chain3(2)
    val tm: X509TrustManager = trustManager(root)

    // build the chains we'll use for testing
    val intermediate: X509Certificate = chain3(1)
    val server: X509Certificate = chain3(0)
    val chain2 = Array[X509Certificate](server, intermediate)
    val chain1 = Array[X509Certificate](server)

    assert(tm.isInstanceOf[TrustManagerImpl])
    val tmi = tm.asInstanceOf[TrustManagerImpl]
    var certs = tmi.checkServerTrusted(chain2, "RSA", new FakeSSLSession("purple.com"))
    assert(Arrays.asList(chain3: _*) == certs)
    certs = tmi.checkServerTrusted(chain1, "RSA", new FakeSSLSession("purple.com"))
    assert(Arrays.asList(chain3: _*) == certs)
  }

  test("testHttpsEndpointIdentification") {
    // TestUtils.assumeExtendedTrustManagerAvailable()

    val pke: KeyStore.PrivateKeyEntry =
      KeyStoreUtils.getServerHostname().getPrivateKey("RSA", "RSA")
    val chain: Array[X509Certificate] = pke.getCertificateChain.asInstanceOf[Array[X509Certificate]]
    val root: X509Certificate = chain(2)
    val tmi: TrustManagerImpl = trustManager(root).asInstanceOf[TrustManagerImpl]

    val goodHostname = KeyStoreUtils.CERT_HOSTNAME
    val badHostname = "definitelywrong.nopenopenope"

    try {
      val params = new SSLParameters()

      // Without endpoint identification this should pass despite the mismatched hostname
      params.setEndpointIdentificationAlgorithm(null)

      var certs = tmi.getTrustedChainForServer(
        chain,
        "RSA",
        new FakeSSLSocket(new FakeSSLSession(badHostname, chain), params),
      )
      assert(Arrays.asList(chain: _*) == certs)

      // Turn on endpoint identification
      params.setEndpointIdentificationAlgorithm("HTTPS")

      try {
        tmi.getTrustedChainForServer(
          chain,
          "RSA",
          new FakeSSLSocket(new FakeSSLSession(badHostname, chain), params),
        )
        throw new AssertionError("Expected CertificateException")
      } catch {
        case _: CertificateException => // expected
      }

      certs = tmi.getTrustedChainForServer(
        chain,
        "RSA",
        new FakeSSLSocket(new FakeSSLSession(goodHostname, chain), params),
      )
      assert(Arrays.asList(chain: _*) == certs)

      // Override the global default hostname verifier with a Conscrypt-specific one that
      // always passes. Both scenarios should pass.
      Conscrypt.setHostnameVerifier(tmi, new ConscryptHostnameVerifier() {})

      certs = tmi.getTrustedChainForServer(
        chain,
        "RSA",
        new FakeSSLSocket(new FakeSSLSession(badHostname, chain), params),
      )
      assert(Arrays.asList(chain: _*) == certs)

      certs = tmi.getTrustedChainForServer(
        chain,
        "RSA",
        new FakeSSLSocket(new FakeSSLSession(goodHostname, chain), params),
      )
      assert(Arrays.asList(chain: _*) == certs)

      // Now set an instance-specific verifier on the trust manager. The bad hostname should
      // fail again.
      Conscrypt.setHostnameVerifier(
        tmi,
        Conscrypt.wrapHostnameVerifier(new MockHostnameVerifier()),
      )

      try {
        tmi.getTrustedChainForServer(
          chain,
          "RSA",
          new FakeSSLSocket(new FakeSSLSession(badHostname, chain), params),
        )
        throw new AssertionError("Expected CertificateException")
      } catch {
        case _: CertificateException => // expected
      }

      certs = tmi.getTrustedChainForServer(
        chain,
        "RSA",
        new FakeSSLSocket(new FakeSSLSession(goodHostname, chain), params),
      )
      assert(Arrays.asList(chain: _*) == certs)

      // Remove the instance-specific verifier, and both should pass again.
      Conscrypt.setHostnameVerifier(tmi, null)

      try {
        tmi.getTrustedChainForServer(
          chain,
          "RSA",
          new FakeSSLSocket(new FakeSSLSession(badHostname, chain), params),
        )
        throw new AssertionError("Expected CertificateException")
      } catch {
        case _: CertificateException => // expected
      }

      certs = tmi.getTrustedChainForServer(
        chain,
        "RSA",
        new FakeSSLSocket(new FakeSSLSession(goodHostname, chain), params),
      )
      assert(Arrays.asList(chain: _*) == certs)
    } finally Conscrypt.setDefaultHostnameVerifier(null)
  }
