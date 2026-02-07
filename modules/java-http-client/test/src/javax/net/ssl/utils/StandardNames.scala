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

// Ported from https://github.com/google/conscrypt/blob/7cbb302ebea3f96f848eda83e0cf19e03ec1c6f5/testing/src/main/java/org/conscrypt/java/security/StandardNames.java

package org.conscrypt.javax.net.ssl

import scala.collection.mutable.{HashSet, HashMap, TreeSet, LinkedHashSet}
import scala.jdk.javaapi.CollectionConverters.asScala

import utest.assert

/**
 * This class defines expected string names for protocols, key types, client and server auth types,
 * cipher suites.
 *
 * Initially based on "Appendix A: Standard Names" of <a
 * href="http://java.sun.com/j2se/1.5.0/docs/guide/security/jsse/JSSERefGuide.html#AppA"> Java
 * &trade; Secure Socket Extension (JSSE) Reference Guide for the Java &trade; 2 Platform Standard
 * Edition 5 </a>.
 *
 * Updated based on the <a
 * href="http://download.java.net/jdk8/docs/technotes/guides/security/SunProviders.html"> Java
 * &trade; Cryptography Architecture Oracle Providers Documentation for Java &trade; Platform
 * Standard Edition 7 </a>. See also the <a
 * href="http://download.java.net/jdk8/docs/technotes/guides/security/StandardNames.html"> Java
 * &trade; Cryptography Architecture Standard Algorithm Name Documentation </a>.
 *
 * Further updates based on the <a
 * href=http://java.sun.com/javase/6/docs/technotes/guides/security/p11guide.html"> Java &trade;
 * PKCS#11 Reference Guide </a>.
 */
object StandardNames {
  val IS_RI: Boolean = !("Dalvik Core Library" == System.getProperty("java.specification.name"))
  val JSSE_PROVIDER_NAME: String =
    if (IS_RI) "Conscrypt"
    else "AndroidOpenSSL"
  val KEY_MANAGER_FACTORY_DEFAULT: String =
    if (IS_RI) "SunX509"
    else "PKIX"
  val TRUST_MANAGER_FACTORY_DEFAULT = "PKIX"
  val KEY_STORE_ALGORITHM: String =
    if (IS_RI) "JKS"
    else "BKS"
  val IS_15_OR_UP: Boolean = majorVersionFromJavaSpecificationVersion >= 15

  private def majorVersionFromJavaSpecificationVersion = majorVersion(
    System.getProperty("java.specification.version", "1.6"),
  )

  private def majorVersion(javaSpecVersion: String) = {
    val components = javaSpecVersion.split("\\.", -1)

    val version = new Array[Int](components.length)
    for (i <- 0 until components.length)
      version(i) = components(i).toInt

    if version(0) == 1
    then {
      assert(version(1) >= 6)
      version(1)
    } else version(0)
  }

  /**
   * RFC 5746's Signaling Cipher Suite Value to indicate a request for secure renegotiation
   */
  val CIPHER_SUITE_SECURE_RENEGOTIATION = "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"

  /**
   * From https://tools.ietf.org/html/draft-ietf-tls-downgrade-scsv-00 it is a signaling cipher
   * suite value (SCSV) to indicate that this request is a protocol fallback (e.g., TLS 1.0 -> SSL
   * 3.0) because the server didn't respond to the first request.
   */
  val CIPHER_SUITE_FALLBACK = "TLS_FALLBACK_SCSV"
  private val CIPHER_MODES = HashMap[String, HashSet[String]]()
  private val CIPHER_PADDINGS = HashMap[String, HashSet[String]]()
  private val SSL_CONTEXT_PROTOCOLS_ENABLED = HashMap[String, Array[String]]()

  private def provideCipherModes(algorithm: String, newModes: Array[String]): Unit =
    CIPHER_MODES.get(algorithm) match
      case None    => CIPHER_MODES.put(algorithm, HashSet[String](newModes*)): Unit
      case Some(m) => m.addAll(Seq(newModes*)): Unit

  private def provideCipherPaddings(algorithm: String, newPaddings: Array[String]): Unit =
    CIPHER_PADDINGS.get(algorithm) match
      case None           => CIPHER_PADDINGS.put(algorithm, HashSet[String](newPaddings*)): Unit
      case Some(paddings) => paddings.addAll(Seq(newPaddings*))

  @SuppressWarnings(Array("EnumOrdinal"))
  private def provideSslContextEnabledProtocols(
      algorithm: String,
      minimum: StandardNames.TLSVersion,
      maximum: StandardNames.TLSVersion,
  ): Unit = {
    if (minimum.ordinal > maximum.ordinal)
      throw new RuntimeException("TLS version: minimum > maximum")
    val versionsLength = maximum.ordinal - minimum.ordinal + 1
    val versionNames = new Array[String](versionsLength)
    for (i <- 0 until versionsLength)
      versionNames(i) = TLSVersion.values(i + minimum.ordinal).name

    SSL_CONTEXT_PROTOCOLS_ENABLED.put(algorithm, versionNames): Unit
  }

  val SSL_CONTEXT_PROTOCOLS_DEFAULT = "Default"
  val SSL_CONTEXT_PROTOCOLS =
    Set[String](
      SSL_CONTEXT_PROTOCOLS_DEFAULT,
      "TLS",
      "TLSv1",
      "TLSv1.1",
      "TLSv1.2",
      "TLSv1.3",
    )
  val SSL_CONTEXT_PROTOCOLS_WITH_DEFAULT_CONFIG =
    Set[String](SSL_CONTEXT_PROTOCOLS_DEFAULT, "TLS", "TLSv1.3")
  // Deprecated TLS protocols... May or may not be present or enabled.
  val SSL_CONTEXT_PROTOCOLS_DEPRECATED = Set[String]()
  val KEY_TYPES = Set[String]("RSA", "DSA", "DH_RSA", "DH_DSA", "EC", "EC_EC", "EC_RSA")
  val SSL_SOCKET_PROTOCOLS = Set[String]("TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3")

  private enum TLSVersion(val name: String) {
    case SSLv3 extends TLSVersion("SSLv3")
    case TLSv1 extends TLSVersion("TLSv1")
    case TLSv11 extends TLSVersion("TLSv1.1")
    case TLSv12 extends TLSVersion("TLSv1.2")
    case TLSv13 extends TLSVersion("TLSv1.3")
  }

  /**
   * Valid values for X509TrustManager.checkClientTrusted authType, either the algorithm of the
   * public key or UNKNOWN.
   */
  val CLIENT_AUTH_TYPES = Set[String]("RSA", "DSA", "EC", "UNKNOWN")

  /**
   * Valid values for X509TrustManager.checkServerTrusted authType, either key exchange algorithm
   * part of the cipher suite, UNKNOWN, or GENERIC (for TLS 1.3 cipher suites that don't imply a
   * specific key exchange method).
   */
  val SERVER_AUTH_TYPES = Set[String](
    "DHE_DSS",
    "DHE_DSS_EXPORT",
    "DHE_RSA",
    "DHE_RSA_EXPORT",
    "DH_DSS_EXPORT",
    "DH_RSA_EXPORT",
    "DH_anon",
    "DH_anon_EXPORT",
    "KRB5",
    "KRB5_EXPORT",
    "RSA",
    "RSA_EXPORT",
    "RSA_EXPORT1024",
    "ECDH_ECDSA",
    "ECDH_RSA",
    "ECDHE_ECDSA",
    "ECDHE_RSA",
    "UNKNOWN",
    "GENERIC",
  )
  val CIPHER_SUITE_INVALID = "SSL_NULL_WITH_NULL_NULL"
  private val CIPHER_SUITES = LinkedHashSet[String]()
  private def addOpenSsl(cipherSuite: String): Unit =
    CIPHER_SUITES.add(cipherSuite): Unit

  /**
   * Cipher suites that are not negotiated when TLSv1.2 is selected on the RI.
   */
  val CIPHER_SUITES_OBSOLETE_TLS12: Seq[String] = Seq(
    "SSL_RSA_WITH_DES_CBC_SHA",
    "SSL_DHE_RSA_WITH_DES_CBC_SHA",
    "SSL_DHE_DSS_WITH_DES_CBC_SHA",
    "SSL_DH_anon_WITH_DES_CBC_SHA",
    "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
    "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5",
    "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
    "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
    "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA",
    "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA",
  )

  /**
   * Cipher suites that are only supported with TLS 1.3.
   */
  val CIPHER_SUITES_TLS13: Seq[String] = Seq(
    "TLS_AES_128_GCM_SHA256",
    "TLS_AES_256_GCM_SHA384",
    "TLS_CHACHA20_POLY1305_SHA256",
  )
  // NOTE: This list needs to be kept in sync with Javadoc of javax.net.ssl.SSLSocket and
  // javax.net.ssl.SSLEngine.
  private val CIPHER_SUITES_AES_HARDWARE = Seq(
    "TLS_AES_128_GCM_SHA256",
    "TLS_AES_256_GCM_SHA384",
    "TLS_CHACHA20_POLY1305_SHA256",
    "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
    "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
    "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
    "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
    "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
    "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
    "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
    "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
    "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
    "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
    "TLS_RSA_WITH_AES_128_GCM_SHA256",
    "TLS_RSA_WITH_AES_256_GCM_SHA384",
    "TLS_RSA_WITH_AES_128_CBC_SHA",
    "TLS_RSA_WITH_AES_256_CBC_SHA",
    CIPHER_SUITE_SECURE_RENEGOTIATION,
  )
  // NOTE: This list needs to be kept in sync with Javadoc of javax.net.ssl.SSLSocket and
  // javax.net.ssl.SSLEngine.
  private val CIPHER_SUITES_SOFTWARE = Seq(
    "TLS_AES_128_GCM_SHA256",
    "TLS_AES_256_GCM_SHA384",
    "TLS_CHACHA20_POLY1305_SHA256",
    "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
    "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
    "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
    "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256",
    "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
    "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
    "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
    "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
    "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
    "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
    "TLS_RSA_WITH_AES_128_GCM_SHA256",
    "TLS_RSA_WITH_AES_256_GCM_SHA384",
    "TLS_RSA_WITH_AES_128_CBC_SHA",
    "TLS_RSA_WITH_AES_256_CBC_SHA",
    CIPHER_SUITE_SECURE_RENEGOTIATION,
  )
  // NOTE: This list needs to be kept in sync with Javadoc of javax.net.ssl.SSLSocket and
  // javax.net.ssl.SSLEngine.
  val CIPHER_SUITES_DEFAULT: Seq[String] = CIPHER_SUITES_SOFTWARE
  // if CpuFeatures.isAESHardwareAccelerated
  // then CIPHER_SUITES_AES_HARDWARE
  // else CIPHER_SUITES_SOFTWARE

  // NOTE: This list needs to be kept in sync with Javadoc of javax.net.ssl.SSLSocket and
  // javax.net.ssl.SSLEngine.
  val CIPHER_SUITES_DEFAULT_PSK: Seq[String] = Seq(
    "TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256",
    "TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA",
    "TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA",
    "TLS_PSK_WITH_AES_128_CBC_SHA",
    "TLS_PSK_WITH_AES_256_CBC_SHA",
  )
  // Should be updated to match BoringSSL's defaults when they change.
  // https://boringssl.googlesource.com/boringssl/+/main/ssl/extensions.cc#215
  private val ELLIPTIC_CURVES_DEFAULT =
    Seq("x25519 (29)", "secp256r1 (23)", "secp384r1 (24)")

  /**
   * Asserts that the cipher suites array is non-null and that it all of its contents are cipher
   * suites known to this implementation. As a convenience, returns any unenabled cipher suites in a
   * test for those that want to verify separately that all cipher suites were included.
   */
  private def assertValidCipherSuites(expected: Set[String], cipherSuites: Array[String]) = {
    assert(cipherSuites != null)
    assert(cipherSuites.length != 0)
    // Make sure all cipherSuites names are expected
    val remainingCipherSuites = HashSet[String](expected.toSeq*)
    val unknownCipherSuites = HashSet[String]()
    for (cipherSuite <- cipherSuites) {
      val removed = remainingCipherSuites.remove(cipherSuite)
      if (!removed)
        unknownCipherSuites.add(cipherSuite): Unit
    }
    assert(unknownCipherSuites.isEmpty)
    remainingCipherSuites
  }

  /**
   * After using assertValidCipherSuites on cipherSuites, assertSupportedCipherSuites additionally
   * verifies that all supported cipher suites where in the input array.
   */
  private def assertSupportedCipherSuites(
      expected: Set[String],
      cipherSuites: Array[String],
  ): Unit = {
    val remainingCipherSuites = assertValidCipherSuites(expected, cipherSuites)
    assert(remainingCipherSuites.isEmpty)
    assert(expected.size == cipherSuites.length)
  }

  /**
   * Asserts that the protocols array is non-null and that it all of its contents are protocols
   * known to this implementation. As a convenience, returns any unenabled protocols in a test for
   * those that want to verify separately that all protocols were included.
   */
  private def assertValidProtocols(expected: Set[String], protocols: Array[String]) = {
    assert(protocols != null)
    assert(protocols.length != 0)
    // Make sure all protocols names are expected
    val remainingProtocols = HashSet[String](expected.toSeq*)
    val unknownProtocols = HashSet[String]()

    for (protocol <- protocols) do
      if (!remainingProtocols.remove(protocol))
        unknownProtocols.add(protocol): Unit

    assert(unknownProtocols.isEmpty)
    remainingProtocols
  }

  /**
   * After using assertValidProtocols on protocols, assertSupportedProtocols additionally verifies
   * that all supported protocols where in the input array.
   */
  private def assertSupportedProtocols(valid: Set[String], protocols: Array[String]): Unit = {
    val remainingProtocols: HashSet[String] = assertValidProtocols(valid, protocols)
    // TODO(prb) Temporarily ignore TLSv1.x: See comment for assertSSLContextEnabledProtocols()
    remainingProtocols.subtractAll(SSL_CONTEXT_PROTOCOLS_DEPRECATED)
    assert(remainingProtocols.isEmpty)
  }

  /**
   * Asserts that the provided list of protocols matches the supported list of protocols.
   */
  def assertSupportedProtocols(protocols: Array[String]): Unit =
    assertSupportedProtocols(SSL_SOCKET_PROTOCOLS, protocols)

  /**
   * Assert that the provided list of cipher suites contains only the supported cipher suites.
   */
  def assertValidCipherSuites(cipherSuites: Array[String]): Unit =
    assertValidCipherSuites(CIPHER_SUITES.toSet, cipherSuites): Unit

  /**
   * Assert that the provided list of cipher suites matches the supported list.
   */
  def assertSupportedCipherSuites(cipherSuites: Array[String]): Unit =
    assertSupportedCipherSuites(CIPHER_SUITES.toSet, cipherSuites)

  /**
   * Assert cipher suites match the default list in content and priority order and contain only
   * cipher suites permitted by default.
   */
  def assertDefaultCipherSuites(cipherSuites: Array[String]): Unit = {
    assertValidCipherSuites(cipherSuites)
    val expected = TreeSet[String](CIPHER_SUITES_DEFAULT*)
    val actual = TreeSet[String](cipherSuites*)
    assert(expected == actual)
  }

  def assertDefaultEllipticCurves(curves: Array[String]): Unit =
    assert(ELLIPTIC_CURVES_DEFAULT.sameElements(curves))

  def assertSSLContextEnabledProtocols(version: String, protocols: Array[String]): Unit = {
    val expected =
      HashSet[String](SSL_CONTEXT_PROTOCOLS_ENABLED.getOrElse(version, Array[String]())*)
    val actual = HashSet[String](protocols*)
    // Ignore deprecated protocols, which are set earlier based
    // on Platform.isTlsV1Deprecated().
    expected.subtractAll(SSL_CONTEXT_PROTOCOLS_DEPRECATED)
    actual.subtractAll(SSL_CONTEXT_PROTOCOLS_DEPRECATED)
    assert(expected == actual)
  }

  /**
   * Get all supported mode names for the given cipher.
   */
  def getModesForCipher(cipher: String): Set[String] = CIPHER_MODES.get(cipher).get.toSet

  /**
   * Get all supported padding names for the given cipher.
   */
  def getPaddingsForCipher(cipher: String): Set[String] = CIPHER_PADDINGS.get(cipher).get.toSet

  // TODO: provideCipherModes and provideCipherPaddings for other Ciphers
  provideCipherModes("AES", Array[String]("CBC", "CFB", "CTR", "CTS", "ECB", "OFB"))
  provideCipherPaddings("AES", Array[String]("NoPadding", "PKCS5Padding"))
  // TODO: None?
  provideCipherModes("RSA", Array[String]("ECB"))
  // TODO: OAEPPadding
  provideCipherPaddings("RSA", Array[String]("NoPadding", "PKCS1Padding"))
  // Fixups for dalvik
  if (!IS_RI) provideCipherPaddings("AES", Array[String]("PKCS7Padding"))
  provideSslContextEnabledProtocols("TLS", TLSVersion.TLSv1, TLSVersion.TLSv13)
  provideSslContextEnabledProtocols("TLSv1", TLSVersion.TLSv1, TLSVersion.TLSv12)
  provideSslContextEnabledProtocols("TLSv1.1", TLSVersion.TLSv1, TLSVersion.TLSv12)
  provideSslContextEnabledProtocols("TLSv1.2", TLSVersion.TLSv1, TLSVersion.TLSv12)
  provideSslContextEnabledProtocols("TLSv1.3", TLSVersion.TLSv1, TLSVersion.TLSv13)
  provideSslContextEnabledProtocols("Default", TLSVersion.TLSv1, TLSVersion.TLSv13)

  // if (TestUtils.isTlsV1Deprecated) {
  //   SSL_CONTEXT_PROTOCOLS_DEPRECATED.add("TLSv1")
  //   SSL_CONTEXT_PROTOCOLS_DEPRECATED.add("TLSv1.1")
  // }
  // if (!TestUtils.isTlsV1Supported) {
  //   assert(TestUtils.isTlsV1Deprecated)
  //   SSL_CONTEXT_PROTOCOLS.removeAll(SSL_CONTEXT_PROTOCOLS_DEPRECATED)
  // }

  // if (IS_RI) {
  //   // DH_* are specified by standard names, but do not seem to be supported by RI
  //   KEY_TYPES.remove("DH_RSA")
  //   KEY_TYPES.remove("DH_DSA")
  // }

  addOpenSsl("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA")
  addOpenSsl("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA")
  addOpenSsl("TLS_RSA_WITH_AES_256_CBC_SHA")
  addOpenSsl("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA")
  addOpenSsl("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA")
  addOpenSsl("TLS_RSA_WITH_AES_128_CBC_SHA")
  // TLSv1.2 cipher suites
  addOpenSsl("TLS_RSA_WITH_AES_128_GCM_SHA256")
  addOpenSsl("TLS_RSA_WITH_AES_256_GCM_SHA384")
  addOpenSsl("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256")
  addOpenSsl("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384")
  addOpenSsl("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256")
  addOpenSsl("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384")
  addOpenSsl("TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256")
  addOpenSsl("TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256")
  // Pre-Shared Key (PSK) cipher suites
  addOpenSsl("TLS_PSK_WITH_AES_128_CBC_SHA")
  addOpenSsl("TLS_PSK_WITH_AES_256_CBC_SHA")
  addOpenSsl("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA")
  addOpenSsl("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA")
  addOpenSsl("TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256")
  // TLS 1.3 cipher suites
  addOpenSsl("TLS_AES_128_GCM_SHA256")
  addOpenSsl("TLS_AES_256_GCM_SHA384")
  addOpenSsl("TLS_CHACHA20_POLY1305_SHA256")
  // RFC 5746's Signaling Cipher Suite Value to indicate a request for secure renegotiation
  addOpenSsl(CIPHER_SUITE_SECURE_RENEGOTIATION)
  // From https://tools.ietf.org/html/draft-ietf-tls-downgrade-scsv-00 to indicate
  // TLS fallback request
  addOpenSsl(CIPHER_SUITE_FALLBACK)

}
