// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/common/src/test/java/org/conscrypt/javax/net/ssl/TrustManagerFactoryTest.java

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

import java.security.InvalidAlgorithmParameterException
import java.security.{KeyStore, Provider}
import java.security.KeyStore.PrivateKeyEntry
import java.security.cert.{
  CertificateException,
  PKIXBuilderParameters,
  PKIXParameters,
  X509CertSelector,
  X509Certificate,
}

import javax.net.ssl.{
  CertPathTrustManagerParameters,
  ManagerFactoryParameters,
  TrustManager,
  TrustManagerFactory,
  X509TrustManager,
}

import utest.{Tests, test, assert, assertThrows, TestSuite}

// import org.bouncycastle.asn1.x509.KeyPurposeId
// import org.conscrypt.Spake2PlusTrustManager
import org.conscrypt.utils.{StandardNames, KeyStoreUtils}

class TrustManagerFactoryTest extends TestSuite:

  private val KEY_TYPES: Seq[String] = Seq(
    "RSA",
    "DSA",
    "EC",
    // "EC_RSA" // JVM -> EC_RSA KeyPairGenerator not available
  )
  private var TEST_KEY_STORE: KeyStoreUtils = null

  // note the rare usage of DSA keys here in addition to RSA
  private def getTestKeyStore(): KeyStoreUtils = {
    if TEST_KEY_STORE == null then
      TEST_KEY_STORE = new KeyStoreUtils.Builder()
        .keyAlgorithms(KEY_TYPES)
        .aliasPrefix("rsa-dsa-ec")
        .build()
    TEST_KEY_STORE
  }

  private def supportsManagerFactoryParameters(tmf: TrustManagerFactory): Boolean =
    tmf.getAlgorithm() == "PKIX"

  private class UselessManagerFactoryParameters extends ManagerFactoryParameters

  private def test_TrustManagerFactory(tmf: TrustManagerFactory): Unit = {
    assert(tmf != null)
    assert(tmf.getAlgorithm() != null)
    assert(tmf.getProvider() != null)

    // before init
    if !(tmf.getAlgorithm() == "PAKE")
    then {
      try
        tmf.getTrustManagers()
        assert(false)
      catch
        case expected: IllegalStateException => // Ignored.
      // init with null ManagerFactoryParameters
      try
        tmf.init(null.asInstanceOf[ManagerFactoryParameters])
        assert(false)
      catch
        case expected: InvalidAlgorithmParameterException => // Ignored.
    } else {
      tmf.init(null.asInstanceOf[ManagerFactoryParameters])
      test_TrustManagerFactory_getTrustManagers(tmf)
    }

    // init with useless ManagerFactoryParameters
    try {
      tmf.init(new UselessManagerFactoryParameters())
      assert(false)
    } catch {
      case expected: InvalidAlgorithmParameterException => // Ignored.
    }

    // init with PKIXParameters ManagerFactoryParameters
    try {
      val pp = new PKIXParameters(getTestKeyStore().keyStore)
      val cptmp = new CertPathTrustManagerParameters(pp)
      tmf.init(cptmp)
      assert(false)
    } catch {
      case expected: InvalidAlgorithmParameterException => // Ignored.
    }

    // init with PKIXBuilderParameters ManagerFactoryParameters
    val xcs = new X509CertSelector()
    val pbp = new PKIXBuilderParameters(getTestKeyStore().keyStore, xcs)
    val cptmp = new CertPathTrustManagerParameters(pbp)

    if supportsManagerFactoryParameters(tmf)
    then {
      tmf.init(cptmp)
      test_TrustManagerFactory_getTrustManagers(tmf)
    } else {
      try
        tmf.init(cptmp)
        assert(false)
      catch
        case expected: InvalidAlgorithmParameterException => // Ignored.
    }

    // init with null for default KeyStore
    tmf.init(null.asInstanceOf[KeyStore])
    test_TrustManagerFactory_getTrustManagers(tmf)

    // init with specific key store
    if (!(tmf.getAlgorithm() == "PAKE")) {
      tmf.init(getTestKeyStore().keyStore)
      test_TrustManagerFactory_getTrustManagers(tmf)
    }
  }

  private def test_TrustManagerFactory_getTrustManagers(tmf: TrustManagerFactory): Unit = {
    val trustManagers = tmf.getTrustManagers()
    assert(trustManagers != null)
    assert(trustManagers.length > 0)
    for trustManager <- trustManagers do
      assert(trustManager != null)
      if trustManager.isInstanceOf[X509TrustManager] then
        test_X509TrustManager(tmf.getProvider(), trustManager.asInstanceOf[X509TrustManager])
    //   if trustManager.isInstanceOf[Spake2PlusTrustManager] then
    //     test_pakeTrustManager(trustManager.asInstanceOf[Spake2PlusTrustManager])
  }

  // private def test_pakeTrustManager(tm: Spake2PlusTrustManager): Unit =
  //   tm.checkClientTrusted()
  //   tm.checkServerTrusted()

  private def test_X509TrustManager(p: Provider, tm: X509TrustManager): Unit =
    for keyType <- KEY_TYPES do {
      val issuers = tm.getAcceptedIssuers()
      assert(issuers != null)
      assert(issuers.length > 1)
      assert(issuers ne tm.getAcceptedIssuers())
      val defaultTrustManager =
        // RI de-duplicates certs from TrustedCertificateEntry and PrivateKeyEntry
        // `&& !Conscrypt.isConscrypt(p)` is removed here
        issuers.length > (if StandardNames.IS_RI then 1 else 2) * KEY_TYPES.length

      val keyAlgName = KeyStoreUtils.keyAlgorithm(keyType)
      val sigAlgName = KeyStoreUtils.signatureAlgorithm(keyType)
      val pke = getTestKeyStore().getPrivateKey(keyAlgName, sigAlgName)
      val chain = pke.getCertificateChain().asInstanceOf[Array[X509Certificate]]
      if defaultTrustManager
      then {
        try
          tm.checkClientTrusted(chain, keyType)
          assert(false)
        catch
          case expected: CertificateException => // Ignored.
        try
          tm.checkServerTrusted(chain, keyType)
          assert(false)
        catch
          case expected: CertificateException => // Ignored.
      } else {
        tm.checkClientTrusted(chain, keyType)
        tm.checkServerTrusted(chain, keyType)
      }
    }

  private def test_TrustManagerFactory_extendedKeyUsage(
      //   keyPurposeId: KeyPurposeId,
      critical: Boolean,
      client: Boolean,
      server: Boolean,
  ): Unit = {
    val algorithm = "RSA"
    val intermediateCa = KeyStoreUtils.getIntermediateCa()
    val leaf = new KeyStoreUtils.Builder()
      .keyAlgorithms(Seq(algorithm))
      .aliasPrefix("criticalCodeSigning")
      .signer(intermediateCa.getPrivateKey("RSA", "RSA"))
      .rootCa(intermediateCa.getRootCertificate("RSA"))
      //   .addExtendedKeyUsage(keyPurposeId, critical)
      .build()

    // leaf.dump("test_TrustManagerFactory_criticalCodeSigning")
    val privateKeyEntry = leaf.getPrivateKey(algorithm, algorithm)
    val chain = privateKeyEntry.getCertificateChain().asInstanceOf[Array[X509Certificate]]

    val rootCa = KeyStoreUtils.getRootCa()
    val trustManager = rootCa.trustManagers(0).asInstanceOf[X509TrustManager]
    try
      trustManager.checkClientTrusted(chain, algorithm)
      assert(client)
    catch case e: Exception => assert(!client)
    try
      trustManager.checkServerTrusted(chain, algorithm)
      assert(server)
    catch case e: Exception => assert(!server)
  }

  def tests = Tests:

    test("test_TrustManagerFactory_getInstance") {
      val tmf =
        TrustManagerFactory.getInstance(StandardNames.TRUST_MANAGER_FACTORY_SUPPORTS_ALGORITHM)
      assert(tmf.getAlgorithm() == StandardNames.TRUST_MANAGER_FACTORY_SUPPORTS_ALGORITHM)
      test_TrustManagerFactory(tmf)

      //   tmf = TrustManagerFactory.getInstance(algorithm, provider)
      //   assert(algorithm == tmf.getAlgorithm())
      //   assert(provider == tmf.getProvider())
      //   test_TrustManagerFactory(tmf)

      //   tmf = TrustManagerFactory.getInstance(algorithm, provider.getName())
      //   assert(algorithm == tmf.getAlgorithm())
      //   assert(provider == tmf.getProvider())
      //   test_TrustManagerFactory(tmf)

    }

    test("test_TrustManagerFactory_intermediate") {
      // chain should be server/intermediate/root
      val pke = KeyStoreUtils.getServer().getPrivateKey("RSA", "RSA")
      val chain = pke.getCertificateChain().asInstanceOf[Array[X509Certificate]]
      assert(chain.length == 3)

      // keyStore should contain only the intermediate CA so we can
      // test proper validation even if there are extra certs after
      // the trusted one (in this case the original root is "extra")
      val keyStore = KeyStoreUtils.createKeyStore()
      keyStore.setCertificateEntry("alias", chain(1))

      val algorithm = TrustManagerFactory.getDefaultAlgorithm()
      val tmf = TrustManagerFactory.getInstance(algorithm)
      tmf.init(keyStore)
      val trustManagers = tmf.getTrustManagers()
      for trustManager <- trustManagers do
        if (trustManager.isInstanceOf[X509TrustManager]) {
          val tm = trustManager.asInstanceOf[X509TrustManager]
          tm.checkClientTrusted(chain, "RSA")
          tm.checkServerTrusted(chain, "RSA")
        }

    }

    test("test_TrustManagerFactory_keyOnly") {
      // create a KeyStore containing only a private key with chain.
      // unlike PKIXParameters(KeyStore), the cert chain of the key should be trusted.
      val ks = KeyStoreUtils.createKeyStore()
      val pke = getTestKeyStore().getPrivateKey("RSA", "RSA")
      ks.setKeyEntry("key", pke.getPrivateKey(), "pw".toCharArray(), pke.getCertificateChain())

      val algorithm = TrustManagerFactory.getDefaultAlgorithm()
      val tmf = TrustManagerFactory.getInstance(algorithm)
      tmf.init(ks)
      val trustManager = tmf.getTrustManagers()(0).asInstanceOf[X509TrustManager]
      trustManager.checkServerTrusted(
        pke.getCertificateChain().asInstanceOf[Array[X509Certificate]],
        "RSA",
      )
    }

    // test("test_TrustManagerFactory_extendedKeyUsage") {
    //   // anyExtendedKeyUsage should work for client or server
    //   test_TrustManagerFactory_extendedKeyUsage(KeyPurposeId.anyExtendedKeyUsage, false, true, true)
    //   test_TrustManagerFactory_extendedKeyUsage(KeyPurposeId.anyExtendedKeyUsage, true, true, true)

    //   // critical clientAuth should work for client
    //   test_TrustManagerFactory_extendedKeyUsage(KeyPurposeId.id_kp_clientAuth, false, true, false)
    //   test_TrustManagerFactory_extendedKeyUsage(KeyPurposeId.id_kp_clientAuth, true, true, false)

    //   // critical serverAuth should work for server
    //   test_TrustManagerFactory_extendedKeyUsage(KeyPurposeId.id_kp_serverAuth, false, false, true)
    //   test_TrustManagerFactory_extendedKeyUsage(KeyPurposeId.id_kp_serverAuth, true, false, true)

    //   // codeSigning should not work
    //   test_TrustManagerFactory_extendedKeyUsage(KeyPurposeId.id_kp_codeSigning, false, false, false)
    //   test_TrustManagerFactory_extendedKeyUsage(KeyPurposeId.id_kp_codeSigning, true, false, false)
    // }
