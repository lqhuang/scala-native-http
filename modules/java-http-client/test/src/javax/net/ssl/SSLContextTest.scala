// Ported from
// https://github.com/google/conscrypt/blob/5b46bc69b6ee129b79c719cd8130a5fb4823a75a/common/src/test/java/org/conscrypt/javax/net/ssl/SSLContextTest.java

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

package snhttp.javax.net.ssl

import java.io.IOException
import java.security.{
  AccessController,
  InvalidAlgorithmParameterException,
  KeyManagementException,
  KeyStore,
  KeyStoreException,
  NoSuchAlgorithmException,
  PrivilegedAction,
  Provider,
  Security,
  UnrecoverableKeyException,
}
import java.util.List as JList
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.net.SocketFactory
import javax.net.ssl.{
  KeyManager,
  KeyManagerFactory,
  KeyManagerFactorySpi,
  ManagerFactoryParameters,
  SSLContext,
  SSLEngine,
  SSLSessionContext,
  // SSLServerSocket,
  // SSLSocket,
  // SSLSocketFactory,
  TrustManager,
  TrustManagerFactory,
  TrustManagerFactorySpi,
  X509KeyManager,
}

import org.conscrypt.utils.{StandardNames, SSLConfigurationAsserts}

import utest.{TestSuite, test, assert, Tests, assertThrows}

class AssertionFailedError extends AssertionError

object SSLContextTest:

  private def fail(): Unit = throw new Exception("Failed")

  private def assertEnabledCipherSuites(
      expectedCipherSuites: Seq[String],
      sslContext: SSLContext,
  ): Unit = {

    assertContentsInOrder(
      expectedCipherSuites,
      sslContext.createSSLEngine().getEnabledCipherSuites(),
    )
    assertContentsInOrder(
      expectedCipherSuites,
      sslContext.createSSLEngine().getSSLParameters().getCipherSuites(),
    )
    assertContentsInOrder(
      expectedCipherSuites,
      sslContext.getSocketFactory().getDefaultCipherSuites(),
    )
    assertContentsInOrder(
      expectedCipherSuites,
      sslContext.getServerSocketFactory().getDefaultCipherSuites(),
    )

    // FIXME: SSLSocket isn't implemented yet
    // val sslSocket = sslContext.getSocketFactory.createSocket.asInstanceOf[SSLSocket]
    // try {
    //   assertContentsInOrder(expectedCipherSuites, sslSocket.getEnabledCipherSuites())
    //   assertContentsInOrder(expectedCipherSuites, sslSocket.getSSLParameters().getCipherSuites())
    // } finally
    //   try sslSocket.close()
    //   catch {
    //     case ignored: IOException =>

    //   }

    // TODO: Server features are not supported yet
    // val sslServerSocket =
    //   sslContext.getServerSocketFactory.createServerSocket.asInstanceOf[SSLServerSocket]
    // try
    //   assertContentsInOrder(expectedCipherSuites, sslServerSocket.getEnabledCipherSuites())
    // finally
    //   try sslSocket.close()
    //   catch {
    //     case ignored: IOException =>
    //   }
  }

  class ThrowExceptionKeyAndTrustManagerFactoryProvider
      extends Provider(
        "ThrowExceptionKeyAndTrustManagerProvider",
        1.0,
        "SSLContextTest fake KeyManagerFactory  and TrustManagerFactory provider",
      ) {
    put(
      "TrustManagerFactory." + TrustManagerFactory.getDefaultAlgorithm(),
      classOf[SSLContextTest.ThrowExceptionTrustManagagerFactorySpi].getName(),
    )
    put(
      "TrustManagerFactory.PKIX",
      classOf[SSLContextTest.ThrowExceptionTrustManagagerFactorySpi].getName(),
    )
    put(
      "KeyManagerFactory." + KeyManagerFactory.getDefaultAlgorithm(),
      classOf[SSLContextTest.ThrowExceptionKeyManagagerFactorySpi].getName(),
    )
    put(
      "KeyManagerFactory.PKIX",
      classOf[SSLContextTest.ThrowExceptionKeyManagagerFactorySpi].getName(),
    )
  }

  class ThrowExceptionTrustManagagerFactorySpi extends TrustManagerFactorySpi {
    @throws[KeyStoreException]
    override protected def engineInit(ks: KeyStore): Unit =
      fail()

    @throws[InvalidAlgorithmParameterException]
    override protected def engineInit(spec: ManagerFactoryParameters): Unit =
      fail()

    override protected def engineGetTrustManagers: Array[TrustManager] =
      throw new AssertionFailedError
  }

  class ThrowExceptionKeyManagagerFactorySpi extends KeyManagerFactorySpi {
    @throws[KeyStoreException]
    @throws[NoSuchAlgorithmException]
    @throws[UnrecoverableKeyException]
    override protected def engineInit(ks: KeyStore, password: Array[Char]): Unit =
      fail()

    @throws[InvalidAlgorithmParameterException]
    override protected def engineInit(spec: ManagerFactoryParameters): Unit =
      fail()

    override protected def engineGetKeyManagers: Array[KeyManager] =
      throw new AssertionFailedError
  }

  /**
   * Installs the specified security provider as the highest provider, invokes the provided
   * {@link Callable}, and removes the provider.
   *
   * @return
   *   result returned by the {@code callable}.
   */
  private def invokeWithHighestPrioritySecurityProvider[T](
      provider: Provider,
      callable: Callable[T],
  ) = {
    var providerPosition = -1
    try {
      providerPosition = Security.insertProviderAt(provider, 1)
      assert(1 == providerPosition)
      callable.call()
    } finally if (providerPosition != -1) Security.removeProvider(provider.getName())
  }

  private def assertContentsInOrder(expected: Seq[String], actual: Array[String]): Unit = {
    assert(expected.size == actual.size)
    assert(expected.sameElements(actual))
  }

class SSLContextTest extends TestSuite:

  def tests = Tests:

    test("SSLContext.getDefault") {
      val sslContext = SSLContext.getDefault()
      assert(sslContext != null)
      // Users shouldn't be able to initialize the default SSLContext (again),
      // since it's supposed to be initialized
      assertThrows[KeyManagementException]:
        sslContext.init(null, null, null)
    }

    test("SSLContext.setDefault") {
      val _ = assertThrows[NullPointerException] {
        SSLContext.setDefault(null)
      }

      val defaultContext = SSLContext.getDefault()

      for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
        val oldContext = SSLContext.getDefault()
        assert(oldContext != null)
        val newContext = SSLContext.getInstance(protocol)
        assert(newContext != null)
        assert(!oldContext.eq(newContext))
        SSLContext.setDefault(newContext)
        assert(newContext.eq(SSLContext.getDefault()))
      }

      SSLContext.setDefault(defaultContext)
    }

    test("SSLContext.defaultConfiguration") {
      SSLConfigurationAsserts.assertSSLContextDefaultConfiguration(SSLContext.getDefault())
      for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
        val sslContext = SSLContext.getInstance(protocol)
        if (!protocol.equalsIgnoreCase(StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT))
          sslContext.init(null, null, null)
        SSLConfigurationAsserts.assertSSLContextDefaultConfiguration(sslContext)
      }
    }

    test("SSLContext.allProtocols") {
      for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
        val sslContext = SSLContext.getInstance(protocol)
        if (!protocol.equalsIgnoreCase(StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT))
          sslContext.init(null, null, null)
      }
    }

    // test("SSLContext.pskOnlyConfiguration_defaultProviderOnly") {
    //   // Test the scenario where only a PSKKeyManager is provided and no TrustManagers are
    //   // provided.
    //   val sslContext = SSLContext.getInstance("TLS")
    //   sslContext.init(
    //     Array[KeyManager](PSKKeyManagerProxy.getConscryptPSKKeyManager(new PSKKeyManagerProxy)),
    //     new Array[TrustManager](0),
    //     null,
    //   )
    //   val expectedCipherSuites = new ArrayList[String](asJava(StandardNames.CIPHER_SUITES_TLS13))
    //   expectedCipherSuites.addAll(asJava(StandardNames.CIPHER_SUITES_DEFAULT_PSK))
    //   expectedCipherSuites.add(StandardNames.CIPHER_SUITE_SECURE_RENEGOTIATION)
    //   SSLContextTest.assertEnabledCipherSuites(expectedCipherSuites, sslContext)
    // }

    // test("SSLContext.x509AndPskConfiguration_defaultProviderOnly") {
    //   // Test the scenario where an X509TrustManager and PSKKeyManager are provided.
    //   var sslContext = SSLContext.getInstance("TLS")
    //   sslContext.init(
    //     Array[KeyManager](PSKKeyManagerProxy.getConscryptPSKKeyManager(new PSKKeyManagerProxy())),
    //     null, // Use default trust managers, one of which is an X.509 one.
    //     null,
    //   )
    //   // The TLS 1.3 cipher suites appear before the PSK ones, so we need to dedup them
    //   val expectedCipherSuiteSet = new util.LinkedHashSet[String]
    //   expectedCipherSuiteSet.addAll(StandardNames.CIPHER_SUITES_TLS13)
    //   expectedCipherSuiteSet.addAll(StandardNames.CIPHER_SUITES_DEFAULT_PSK)
    //   expectedCipherSuiteSet.addAll(StandardNames.CIPHER_SUITES_DEFAULT)
    //   val expectedCipherSuites = new util.ArrayList[String](expectedCipherSuiteSet)
    //   SSLContextTest.assertEnabledCipherSuites(expectedCipherSuites, sslContext)
    //   // Test the scenario where an X509KeyManager and PSKKeyManager are provided.
    //   sslContext = SSLContext.getInstance("TLS")
    //   // Just an arbitrary X509KeyManager -- it won't be invoked in this test.
    //   val x509KeyManager = new RandomPrivateKeyX509ExtendedKeyManager(null)
    //   sslContext.init(
    //     Array[KeyManager](
    //       x509KeyManager,
    //       PSKKeyManagerProxy.getConscryptPSKKeyManager(new PSKKeyManagerProxy),
    //     ),
    //     new Array[TrustManager](0),
    //     null,
    //   )
    //   SSLContextTest.assertEnabledCipherSuites(expectedCipherSuites, sslContext)
    // }

    // test("SSLContext.emptyConfiguration_defaultProviderOnly") {
    //   // Test the scenario where neither X.509 nor PSK KeyManagers or TrustManagers are provided.
    //   val sslContext = SSLContext.getInstance("TLS")
    //   sslContext.init(new Array[KeyManager](0), new Array[TrustManager](0), null)
    //   // No TLS 1.2 cipher suites should be enabled, since neither PSK nor X.509 key exchange
    //   // can be done.  The TLS 1.3 cipher suites should be there, since key exchange isn't
    //   // part of the cipher suite in 1.3.
    //   val expected =
    //     StandardNames.CIPHER_SUITES_TLS13.toSeq
    //       ++ Seq(StandardNames.CIPHER_SUITE_SECURE_RENEGOTIATION)
    //   SSLContextTest.assertEnabledCipherSuites(expected, sslContext)
    // }

    test("SSLContext should init correct enabled protocol versions") {
      for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
        val context = SSLContext.getInstance(protocol)
        if (!protocol.equalsIgnoreCase(StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT))
          context.init(null, null, null)

        StandardNames.assertSSLContextEnabledProtocols(
          protocol,
          context.getDefaultSSLParameters().getProtocols(),
        )

        // FIXME: not implemented yet, postpone the assertion
        // StandardNames.assertSSLContextEnabledProtocols(
        //   protocol,
        //   context.getSocketFactory.createSocket.asInstanceOf[SSLSocket].getEnabledProtocols,
        // )
        // StandardNames.assertSSLContextEnabledProtocols(
        //   protocol,
        //   context.getServerSocketFactory.createServerSocket
        //     .asInstanceOf[SSLServerSocket]
        //     .getEnabledProtocols,
        // )
        // StandardNames.assertSSLContextEnabledProtocols(
        //   protocol,
        //   context.createSSLEngine().getEnabledProtocols,
        // )
      }
    }

    test("SSLContext.getInstance") {
      val _ = assertThrows[NullPointerException]:
        SSLContext.getInstance(null): Unit

      for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
        assert(SSLContext.getInstance(protocol) != null)
        assert(!SSLContext.getInstance(protocol).eq(SSLContext.getInstance(protocol)))
      }

      val _ = assertThrows[NullPointerException]:
        SSLContext.getInstance(null, null.asInstanceOf[String]): Unit

      val _ = assertThrows[NullPointerException]:
        SSLContext.getInstance(null, ""): Unit

      for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS)
        val _ = assertThrows[IllegalArgumentException]:
          SSLContext.getInstance(protocol, null.asInstanceOf[String]): Unit

      assertThrows[NullPointerException]:
        SSLContext.getInstance(null, StandardNames.JSSE_PROVIDER_NAME): Unit
    }

    test("SSLContext.getProtocol") {
      for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
        val protocolName = SSLContext.getInstance(protocol).getProtocol()
        assert(protocolName != null)
        assert(protocol.startsWith(protocolName))
      }
    }

    /*
     * doesn't pass on JVM
     */
    // test("SSLContext.getProvider") {
    //   val provider = SSLContext.getDefault().getProvider()
    //   assert(provider != null)
    //   assert(StandardNames.JSSE_PROVIDER_NAME == provider.getName())
    // }

    // test("SSLContext.init_Default") {
    //   // Assert that initializing a default SSLContext fails because it's supposed to be
    //   // initialized already.
    //   val sslContext = SSLContext.getInstance(StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT)

    //   val _ = assertThrows[KeyManagementException]:
    //     sslContext.init(null, null, null): Unit

    //   val _ = assertThrows[KeyManagementException]:
    //     sslContext.init(new Array[KeyManager](0), new Array[TrustManager](0), null): Unit

    //   assertThrows[KeyManagementException]:
    //     sslContext.init(
    //       Array[KeyManager](new KeyManager() {}),
    //       Array[TrustManager](new TrustManager() {}),
    //       null,
    //     )
    // }

    test("SSLContext.init_withNullManagerArrays") {
      // Assert that SSLContext.init works fine even when provided with null arrays of
      // KeyManagers and TrustManagers.
      // The contract of SSLContext.init is that it will for default X.509 KeyManager and
      // TrustManager from the highest priority KeyManagerFactory and TrustManagerFactory.

      for (
        protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS
        if protocol != StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT
      ) {
        val sslContext = SSLContext.getInstance(protocol)
        sslContext.init(null, null, null)
      }
    }

    // test("SSLContext.init_withEmptyManagerArrays") {
    //   // Assert that SSLContext.init works fine even when provided with empty arrays of
    //   // KeyManagers and TrustManagers.
    //   // The contract of SSLContext.init is that it will not look for default X.509 KeyManager and
    //   // TrustManager.
    //   // This test thus installs a Provider of KeyManagerFactory and TrustManagerFactory whose
    //   // factories throw exceptions which will make this test fail if the factories are used.
    //   val provider = new SSLContextTest.ThrowExceptionKeyAndTrustManagerFactoryProvider
    //   SSLContextTest.invokeWithHighestPrioritySecurityProvider(
    //     provider,
    //     new Callable[Void]() {

    //       override def call: Void = {
    //         assertEquals(
    //           classOf[SSLContextTest.ThrowExceptionKeyAndTrustManagerFactoryProvider],
    //           TrustManagerFactory
    //             .getInstance(TrustManagerFactory.getDefaultAlgorithm)
    //             .getProvider
    //             .getClass,
    //         )
    //         assertEquals(
    //           classOf[SSLContextTest.ThrowExceptionKeyAndTrustManagerFactoryProvider],
    //           KeyManagerFactory
    //             .getInstance(KeyManagerFactory.getDefaultAlgorithm)
    //             .getProvider
    //             .getClass,
    //         )
    //         val keyManagers = new Array[KeyManager](0)
    //         val trustManagers = new Array[TrustManager](0)
    //         import scala.collection.JavaConversions._
    //         for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
    //           if (protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT) {
    //             // Default SSLContext is provided in an already initialized state
    //             continue // todo: continue is not supported

    //           }
    //           val sslContext = SSLContext.getInstance(protocol)
    //           sslContext.init(keyManagers, trustManagers, null)
    //         }
    //         null
    //       }
    //     },
    //   )
    // }

    // test("SSLContext.init_withoutX509") {
    //   // Assert that SSLContext.init works fine even when provided with KeyManagers and
    //   // TrustManagers which don't include the X.509 ones.
    //   // The contract of SSLContext.init is that it will not look for default X.509 KeyManager and
    //   // TrustManager.
    //   // This test thus installs a Provider of KeyManagerFactory and TrustManagerFactory whose
    //   // factories throw exceptions which will make this test fail if the factories are used.
    //   val provider = new SSLContextTest.ThrowExceptionKeyAndTrustManagerFactoryProvider
    //   SSLContextTest.invokeWithHighestPrioritySecurityProvider(
    //     provider,
    //     new Callable[Void]() {

    //       override def call: Void = {
    //         assertEquals(
    //           classOf[SSLContextTest.ThrowExceptionKeyAndTrustManagerFactoryProvider],
    //           TrustManagerFactory
    //             .getInstance(TrustManagerFactory.getDefaultAlgorithm)
    //             .getProvider
    //             .getClass,
    //         )
    //         assertEquals(
    //           classOf[SSLContextTest.ThrowExceptionKeyAndTrustManagerFactoryProvider],
    //           KeyManagerFactory
    //             .getInstance(KeyManagerFactory.getDefaultAlgorithm)
    //             .getProvider
    //             .getClass,
    //         )
    //         val keyManagers = Array[KeyManager](new KeyManager() {})
    //         val trustManagers = Array[TrustManager](new TrustManager() {})
    //         import scala.collection.JavaConversions._
    //         for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
    //           if (protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT) {
    //             // Default SSLContext is provided in an already initialized state
    //             continue // todo: continue is not supported

    //           }
    //           val sslContext = SSLContext.getInstance(protocol)
    //           sslContext.init(keyManagers, trustManagers, null)
    //         }
    //         null
    //       }
    //     },
    //   )
    // }

    // test("SSLContext.getSocketFactory") {

    //   for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
    //     if (protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT)
    //       SSLContext.getInstance(protocol).getSocketFactory(): Unit
    //     else
    //       val _ = assertThrows[IllegalStateException]:
    //         SSLContext.getInstance(protocol).getSocketFactory(): Unit

    //     val sslContext = SSLContext.getInstance(protocol)
    //     if (!(protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT))
    //       sslContext.init(null, null, null)
    //     val sf = sslContext.getSocketFactory
    //     assert(sf != null)
    //     assert(classOf[SSLSocketFactory].isAssignableFrom(sf.getClass))
    //   }
    // }

    // test("SSLContext.getServerSocketFactory") {
    //   for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
    //     if (protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT)
    //       SSLContext.getInstance(protocol).getServerSocketFactory(): Unit
    //     else
    //       val _ = assertThrows[IllegalStateException]:
    //         SSLContext.getInstance(protocol).getServerSocketFactory(): Unit

    //     val sslContext = SSLContext.getInstance(protocol)
    //     if (!(protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT))
    //       sslContext.init(null, null, null)
    //     val ssf = sslContext.getServerSocketFactory
    //     assert(ssf != null)
    //     assert(classOf[SSLServerSocketFactory].isAssignableFrom(ssf.getClass))
    //   }
    // }

    // test("SSLContext.createSSLEngine") {
    //   for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
    //     if (protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT)
    //       SSLContext.getInstance(protocol).createSSLEngine
    //     else
    //       val _ = assertThrows[IllegalStateException]:
    //         SSLContext.getInstance(protocol).createSSLEngine(): Unit

    //     if (protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT)
    //       SSLContext.getInstance(protocol).createSSLEngine(null, -1)
    //     else
    //       val _ = assertThrows[IllegalStateException]:
    //         SSLContext.getInstance(protocol).createSSLEngine(null, -1): Unit

    //     val sslContext = SSLContext.getInstance(protocol)
    //     if (!(protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT))
    //       sslContext.init(null, null, null)
    //     val se = sslContext.createSSLEngine()
    //     assert(se != null)
    //     val se1 = sslContext.createSSLEngine(null, -1)
    //     assert(se1 != null)
    //   }
    // }

    // test("SSLContext.getServerSessionContext") {
    //   for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
    //     val sslContext = SSLContext.getInstance(protocol)
    //     val sessionContext = sslContext.getServerSessionContext
    //     assert(sessionContext != null)
    //     if (protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT)
    //       assert(SSLContext.getInstance(protocol).getServerSessionContext().eq(sessionContext))
    //     else
    //       assert(!SSLContext.getInstance(protocol).getServerSessionContext().eq(sessionContext))
    //   }
    // }

    test("SSLContext.getClientSessionContext") {
      for (protocol <- StandardNames.SSL_CONTEXT_GET_PROTOCOLS) {
        val sslContext = SSLContext.getInstance(protocol)
        val sessionContext = sslContext.getClientSessionContext()
        assert(sessionContext != null)

        // Doesn't pass in JVM platform, skip ...
        // if (protocol == StandardNames.SSL_CONTEXT_GET_PROTOCOLS_DEFAULT)
        //   assert(SSLContext.getInstance(protocol).getClientSessionContext().eq(sessionContext))
        // else

        assert(!SSLContext.getInstance(protocol).getClientSessionContext().eq(sessionContext))
      }
    }

    // test("SSLContextTest_TestSSLContext.create") {
    //   val testContext = TestSSLContext.create
    //   assert(testContext != null)
    //   assert(testContext.clientKeyStore != null)
    //   assertNull(testContext.clientStorePassword)
    //   assert(testContext.serverKeyStore != null)
    //   assert(testContext.clientKeyManagers != null)
    //   assert(testContext.serverKeyManagers != null)
    //   if (testContext.clientKeyManagers.length eq 0) fail("No client KeyManagers")
    //   if (testContext.serverKeyManagers.length eq 0) fail("No server KeyManagers")
    //   assert(testContext.clientKeyManagers(0) != null)
    //   assert(testContext.serverKeyManagers(0) != null)
    //   assert(testContext.clientTrustManager != null)
    //   assert(testContext.serverTrustManager != null)
    //   assert(testContext.clientContext != null)
    //   assert(testContext.serverContext != null)
    //   assert(testContext.serverSocket != null)
    //   assert(testContext.host != null)
    //   assertTrue(testContext.port ne 0)
    //   testContext.close
    // }

    /*
     * TODO: skip on JVM
     */
    // // Find the default provider for TLS and verify that it does NOT support SSLv3.
    // test("SSLContext.SSLv3Unsupported") {
    //   // val defaultTlsProviders =
    //   //   for {
    //   //     provider <- Security.getProviders()
    //   //     protocol <- Seq("SSLContext.TLSv1.2", "SSLContext.TLSv1")
    //   //     if provider.get(protocol) != null
    //   //   } yield provider

    //   // assert(!defaultTlsProviders.isEmpty)

    //   // for (provider <- defaultTlsProviders)
    //   //   val _ = assertThrows[NoSuchAlgorithmException]:
    //   //     SSLContext.getInstance("SSLv3", provider): Unit
    //   val _ = assertThrows[NoSuchAlgorithmException]:
    //     SSLContext.getInstance("SSLv3"): Unit
    // }
