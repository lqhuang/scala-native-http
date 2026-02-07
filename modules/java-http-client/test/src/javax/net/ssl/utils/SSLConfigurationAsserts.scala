/*
 * Copyright (C) 2013 The Android Open Source Project
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

// Ported from https://github.com/google/conscrypt/blob/7cbb302ebea3f96f848eda83e0cf19e03ec1c6f5/testing/src/main/java/org/conscrypt/javax/net/ssl/SSLConfigurationAsserts.java

package org.conscrypt.javax.net.ssl

import java.io.IOException
import java.util.List as JList
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.SSLParameters
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLServerSocketFactory
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

import utest.assert

/**
 * Assertions about the configuration of TLS/SSL primitives.
 */
object SSLConfigurationAsserts {

  /**
   * Asserts that the provided {@link SSLContext} has the expected default configuration, and that
   * {@link SSLSocketFactory}, {@link SSLServerSocketFactory}, {@link SSLSocket},
   * {@link SSLServerSocket} and {@link SSLEngine} instances created from the context match the
   * configuration.
   */
  def assertSSLContextDefaultConfiguration(sslContext: SSLContext): Unit = {
    val defaultParameters = sslContext.getDefaultSSLParameters()
    StandardNames.assertSSLContextEnabledProtocols(
      sslContext.getProtocol(),
      defaultParameters.getProtocols(),
    )
    StandardNames.assertDefaultCipherSuites(defaultParameters.getCipherSuites())
    assert(defaultParameters.getWantClientAuth() == false)
    assert(defaultParameters.getNeedClientAuth() == false)
    val supportedParameters = sslContext.getSupportedSSLParameters()
    StandardNames.assertSupportedCipherSuites(supportedParameters.getCipherSuites())
    StandardNames.assertSupportedProtocols(supportedParameters.getProtocols())
    assert(supportedParameters.getWantClientAuth() == false)
    assert(supportedParameters.getNeedClientAuth() == false)
    assertContainsAll(supportedParameters.getCipherSuites(), defaultParameters.getCipherSuites())
    assertContainsAll(supportedParameters.getProtocols(), defaultParameters.getProtocols())
    assertSSLSocketFactoryConfigSameAsSSLContext(sslContext.getSocketFactory(), sslContext)
    assertSSLServerSocketFactoryConfigSameAsSSLContext(
      sslContext.getServerSocketFactory,
      sslContext,
    )
    val sslEngine = sslContext.createSSLEngine()
    assert(sslEngine.getUseClientMode() == false)
    assertSSLEngineConfigSameAsSSLContext(sslEngine, sslContext)
  }

  /**
   * Asserts that the provided {@link SSLSocketFactory} has the expected default configuration and
   * that {@link SSLSocket} instances created by the factory match the configuration.
   */
  @throws[Exception]
  def assertSSLSocketFactoryDefaultConfiguration(sslSocketFactory: SSLSocketFactory): Unit =
    assertSSLSocketFactoryConfigSameAsSSLContext(sslSocketFactory, SSLContext.getDefault())

  /**
   * Asserts that {@link SSLSocketFactory}'s configuration matches {@code SSLContext}'s
   * configuration, and that {@link SSLSocket} instances obtained from the factory match this
   * configuration as well.
   */
  @throws[IOException]
  private def assertSSLSocketFactoryConfigSameAsSSLContext(
      sslSocketFactory: SSLSocketFactory,
      sslContext: SSLContext,
  ): Unit = {
    assertCipherSuitesEqual(
      sslContext.getDefaultSSLParameters().getCipherSuites(),
      sslSocketFactory.getDefaultCipherSuites(),
    )
    assertCipherSuitesEqual(
      sslContext.getSupportedSSLParameters().getCipherSuites(),
      sslSocketFactory.getSupportedCipherSuites(),
    )
    val sslSocket = sslSocketFactory.createSocket.asInstanceOf[SSLSocket]
    try {
      assert(sslSocket.getUseClientMode() == true)
      assert(sslSocket.getEnableSessionCreation() == true)
      assertSSLSocketConfigSameAsSSLContext(sslSocket, sslContext)
    } finally sslSocket.close()
  }

  /**
   * Asserts that the provided {@link SSLSocket} has the expected default configuration.
   */
  @throws[Exception]
  def assertSSLSocketDefaultConfiguration(sslSocket: SSLSocket): Unit = {
    assert(sslSocket.getUseClientMode() == true)
    assert(sslSocket.getEnableSessionCreation() == true)
    assertSSLSocketConfigSameAsSSLContext(sslSocket, SSLContext.getDefault())
  }

  /**
   * Asserts that {@link SSLSocket}'s configuration matches {@code SSLContext's} configuration.
   */
  private def assertSSLSocketConfigSameAsSSLContext(
      sslSocket: SSLSocket,
      sslContext: SSLContext,
  ): Unit = {
    assertSSLParametersEqual(sslSocket.getSSLParameters(), sslContext.getDefaultSSLParameters())
    assertCipherSuitesEqual(
      sslSocket.getEnabledCipherSuites(),
      sslContext.getDefaultSSLParameters().getCipherSuites(),
    )
    assertProtocolsEqual(
      sslSocket.getEnabledProtocols(),
      sslContext.getDefaultSSLParameters().getProtocols(),
    )
    assertCipherSuitesEqual(
      sslSocket.getSupportedCipherSuites(),
      sslContext.getSupportedSSLParameters().getCipherSuites(),
    )
    assertProtocolsEqual(
      sslSocket.getSupportedProtocols,
      sslContext.getSupportedSSLParameters().getProtocols(),
    )
  }

  /**
   * Asserts that the provided {@link SSLServerSocketFactory} has the expected default
   * configuration, and that {@link SSLServerSocket} instances created by the factory match the
   * configuration.
   */
  @throws[Exception]
  def assertSSLServerSocketFactoryDefaultConfiguration(
      sslServerSocketFactory: SSLServerSocketFactory,
  ): Unit =
    assertSSLServerSocketFactoryConfigSameAsSSLContext(
      sslServerSocketFactory,
      SSLContext.getDefault(),
    )

  /**
   * Asserts that {@link SSLServerSocketFactory}'s configuration matches {@code SSLContext}'s
   * configuration, and that {@link SSLServerSocket} instances obtained from the factory match this
   * configuration as well.
   */
  @throws[IOException]
  private def assertSSLServerSocketFactoryConfigSameAsSSLContext(
      sslServerSocketFactory: SSLServerSocketFactory,
      sslContext: SSLContext,
  ): Unit = {
    assertCipherSuitesEqual(
      sslContext.getDefaultSSLParameters().getCipherSuites(),
      sslServerSocketFactory.getDefaultCipherSuites(),
    )
    assertCipherSuitesEqual(
      sslContext.getSupportedSSLParameters().getCipherSuites(),
      sslServerSocketFactory.getSupportedCipherSuites(),
    )
    val sslServerSocket = sslServerSocketFactory.createServerSocket.asInstanceOf[SSLServerSocket]
    try {
      assert(sslServerSocket.getUseClientMode() == false)
      assert(sslServerSocket.getEnableSessionCreation() == true)
      assertSSLServerSocketConfigSameAsSSLContext(sslServerSocket, sslContext)
    } finally sslServerSocket.close()
  }

  /**
   * Asserts that the provided {@link SSLServerSocket} has the expected default configuration.
   */
  @throws[Exception]
  def assertSSLServerSocketDefaultConfiguration(sslServerSocket: SSLServerSocket): Unit = {
    assert(sslServerSocket.getUseClientMode() == false)
    assert(sslServerSocket.getEnableSessionCreation() == true)
    assertSSLServerSocketConfigSameAsSSLContext(sslServerSocket, SSLContext.getDefault())
    // TODO: Check SSLParameters when supported by SSLServerSocket API
  }

  /**
   * Asserts that {@link SSLServerSocket}'s configuration matches {@code SSLContext's}
   * configuration.
   */
  private def assertSSLServerSocketConfigSameAsSSLContext(
      sslServerSocket: SSLServerSocket,
      sslContext: SSLContext,
  ): Unit = {
    assertCipherSuitesEqual(
      sslServerSocket.getEnabledCipherSuites(),
      sslContext.getDefaultSSLParameters().getCipherSuites(),
    )
    assertProtocolsEqual(
      sslServerSocket.getEnabledProtocols(),
      sslContext.getDefaultSSLParameters().getProtocols(),
    )
    assertCipherSuitesEqual(
      sslServerSocket.getSupportedCipherSuites(),
      sslContext.getSupportedSSLParameters().getCipherSuites(),
    )
    assertProtocolsEqual(
      sslServerSocket.getSupportedProtocols,
      sslContext.getSupportedSSLParameters().getProtocols(),
    )
    assert(
      sslServerSocket.getNeedClientAuth() ==
        sslContext.getDefaultSSLParameters().getNeedClientAuth(),
    )
    assert(
      sslServerSocket.getWantClientAuth() ==
        sslContext.getDefaultSSLParameters().getWantClientAuth(),
    )
  }

  /**
   * Asserts that the provided {@link SSLEngine} has the expected default configuration.
   */
  @throws[Exception]
  def assertSSLEngineDefaultConfiguration(sslEngine: SSLEngine): Unit = {
    assert(sslEngine.getUseClientMode() == false)
    assert(sslEngine.getEnableSessionCreation() == true)
    assertSSLEngineConfigSameAsSSLContext(sslEngine, SSLContext.getDefault())
  }

  /**
   * Asserts that {@link SSLEngine}'s configuration matches {@code SSLContext's} configuration.
   */
  private def assertSSLEngineConfigSameAsSSLContext(
      sslEngine: SSLEngine,
      sslContext: SSLContext,
  ): Unit = {
    assertSSLParametersEqual(sslEngine.getSSLParameters(), sslContext.getDefaultSSLParameters())
    assertCipherSuitesEqual(
      sslEngine.getEnabledCipherSuites(),
      sslContext.getDefaultSSLParameters().getCipherSuites(),
    )
    assertProtocolsEqual(
      sslEngine.getEnabledProtocols(),
      sslContext.getDefaultSSLParameters().getProtocols(),
    )
    assertCipherSuitesEqual(
      sslEngine.getSupportedCipherSuites(),
      sslContext.getSupportedSSLParameters().getCipherSuites(),
    )
    assertProtocolsEqual(
      sslEngine.getSupportedProtocols,
      sslContext.getSupportedSSLParameters().getProtocols(),
    )
  }

  private def assertSSLParametersEqual(expected: SSLParameters, actual: SSLParameters): Unit = {
    assertCipherSuitesEqual(expected.getCipherSuites(), actual.getCipherSuites())
    assertProtocolsEqual(expected.getProtocols(), actual.getProtocols)
    assert(expected.getNeedClientAuth() == actual.getNeedClientAuth())
    assert(expected.getWantClientAuth() == actual.getWantClientAuth())
  }

  private def assertCipherSuitesEqual(expected: Array[String], actual: Array[String]): Unit =
    assert(expected.sameElements(actual))

  private def assertProtocolsEqual(expected: Array[String], actual: Array[String]): Unit =
    // IMPLEMENTATION NOTE: The order of protocols versions does not matter. Similarly, it only
    // matters whether a protocol version is present or absent in the array. These arrays are
    // supposed to represent sets of protocol versions. Thus, we treat them as such.
    assert(Set(expected) == Set(actual))

  /**
   * Asserts that the {@code container} contains all the {@code elements}.
   */
  private def assertContainsAll(
      container: Array[String],
      elements: Array[String],
  ): Unit = {
    val elementsNotInContainer = Set(elements*)
    assert(Set() == elementsNotInContainer.removedAll(container))
  }
}
