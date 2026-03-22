// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/common/src/test/java/org/conscrypt/javax/net/ssl/KeyManagerFactoryTest.java

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

import java.io.{IOException, InputStream, OutputStream}
import java.security.{
  InvalidAlgorithmParameterException,
  Key,
  KeyStore,
  KeyStoreException,
  KeyStoreSpi,
  NoSuchAlgorithmException,
  PrivateKey,
  Provider,
  UnrecoverableKeyException,
}
import java.security.KeyStore.{PasswordProtection, PrivateKeyEntry}
import java.security.cert.{Certificate, CertificateException, X509Certificate}
import java.util.{Arrays, Date, Enumeration}

import javax.net.ssl.{
  KeyManager,
  KeyManagerFactory,
  KeyStoreBuilderParameters,
  ManagerFactoryParameters,
  X509ExtendedKeyManager,
  X509KeyManager,
}

import utest.{TestSuite, Tests, test, assert, assertThrows}

import org.conscrypt.utils.{StandardNames, KeyStoreUtils}

class KeyManagerFactoryTest extends TestSuite:

  // note the rare usage of DSA keys here in addition to RSA
  private val keyAlgorithms: Seq[String] = Seq(
    "RSA",
    "DSA",
    "EC",
    // "DH_RSA", // JVM -> DH_RSA KeyPairGenerator not available
    // "DH_DSA", // JVM -> DH_DSA KeyPairGenerator not available
    // "EC_RSA", // JVM -> EC_RSA KeyPairGenerator not available
  )

  private val mockKeyStore: KeyStoreUtils = new KeyStoreUtils.Builder()
    .keyAlgorithms(keyAlgorithms)
    .aliasPrefix("rsa-dsa-ec-dh")
    .build()

  private class UselessManagerFactoryParameters extends ManagerFactoryParameters

  private def supportsManagerFactoryParameters(algorithm: String): Boolean =
    // Only the "New" one supports ManagerFactoryParameters
    algorithm == "NewSunX509"

  private def keyTypes(algorithm: String): Array[String] =
    // Although the "New" one supports ManagerFactoryParameters,
    // it can't handle nulls in the key types array.
    if algorithm == "NewSunX509"
    then KEY_TYPES_WITH_EMPTY
    else KEY_TYPES_WITH_EMPTY_AND_NULL

  private def testKeyManagerFactory(kmf: KeyManagerFactory): Unit = {
    assert(kmf != null)
    assert(kmf.getAlgorithm() != null)
    assert(kmf.getProvider() != null)

    // before init
    try {
      kmf.getKeyManagers()
      throw new AssertionError("Expected IllegalStateException")
    } catch {
      case _: IllegalStateException => // Ignore
    }

    // init with null ManagerFactoryParameters
    val _ = assertThrows[InvalidAlgorithmParameterException] {
      kmf.init(null.asInstanceOf[ManagerFactoryParameters])
    }

    // init with useless ManagerFactoryParameters
    val _ = assertThrows[InvalidAlgorithmParameterException] {
      kmf.init(new UselessManagerFactoryParameters())
    }

    // init with KeyStoreBuilderParameters ManagerFactoryParameters
    val pp = new PasswordProtection(mockKeyStore.storePassword)
    val builder = KeyStore.Builder.newInstance(mockKeyStore.keyStore, pp)
    val ksbp = new KeyStoreBuilderParameters(builder)

    // TODO: doesn't pass on JVM, skip for now.
    // if (supportsManagerFactoryParameters(kmf.getAlgorithm())) {
    //   kmf.init(ksbp)
    //   testKeyManagerFactoryGetKeyManagers(kmf, empty = false)
    // } else {
    //   val _ = assertThrows[InvalidAlgorithmParameterException] {
    //     kmf.init(ksbp)
    //   }
    // }

    if (kmf.getAlgorithm() == "PAKE") {
      val _ = assertThrows[KeyStoreException] {
        kmf.init(null, null)
      }
      return // Functional testing is in PakeKeyManagerFactoryTest
    }

    // init with null for default behavior
    kmf.init(null.asInstanceOf[KeyStore], null)
    testKeyManagerFactoryGetKeyManagers(kmf, empty = true)

    // TODO: doesn't pass on JVM, skip for now.
    // init with specific key store and password
    // kmf.init(mockKeyStore.keyStore, mockKeyStore.storePassword)
    // testKeyManagerFactoryGetKeyManagers(kmf, empty = false)
  }

  private def testKeyManagerFactoryGetKeyManagers(kmf: KeyManagerFactory, empty: Boolean): Unit = {
    val keyManagers = kmf.getKeyManagers()
    assert(keyManagers != null)
    assert(keyManagers.length > 0)
    for (keyManager <- keyManagers) {
      assert(keyManager != null)
      keyManager match {
        case km: X509KeyManager =>
          testX509KeyManager(km, empty, kmf.getAlgorithm())
        case _ => ()
      }
    }
  }

  private val KEY_TYPES_ONLY: Array[String] =
    StandardNames.KEY_TYPES.toArray()
  private val KEY_TYPES_WITH_EMPTY: Array[String] =
    KEY_TYPES_ONLY ++ Array("")
  private val KEY_TYPES_WITH_EMPTY_AND_NULL: Array[String] =
    KEY_TYPES_ONLY ++ Array(null.asInstanceOf[String])

  private def testX509KeyManager(km: X509KeyManager, empty: Boolean, algorithm: String): Unit = {
    val kTypes = keyTypes(algorithm)

    for (keyType <- kTypes) {
      val aliases = km.getClientAliases(keyType, null)
      if (empty || keyType == null || keyType.isEmpty()) {
        // s"Expected null aliases for keyType=$keyType"
        assert(aliases == null)
      } else {
        // s"Expected non-null aliases for keyType=$keyType"
        assert(aliases != null)
        for (alias <- aliases)
          testX509KeyManagerAlias(km, alias, keyType, many = false, empty = empty)
      }
    }

    for (keyType <- kTypes) {
      val aliases = km.getServerAliases(keyType, null)
      if (empty || keyType == null || keyType.isEmpty()) {
        // s"Expected null aliases for keyType=$keyType"
        assert(aliases == null)
      } else {
        // s"Expected non-null aliases for keyType=$keyType"
        assert(aliases != null)
        for (alias <- aliases)
          testX509KeyManagerAlias(km, alias, keyType, many = false, empty = empty)
      }
    }

    val rotatedTypes = rotate(nonEmpty(kTypes))
    for (keyList <- rotatedTypes) {
      val alias = km.chooseClientAlias(keyList, null, null)
      testX509KeyManagerAlias(km, alias, null, many = true, empty = empty)
    }

    for (keyType <- kTypes) {
      val array = Array(keyType)
      val alias = km.chooseClientAlias(array, null, null)
      testX509KeyManagerAlias(km, alias, keyType, many = false, empty = empty)
    }
    for (keyType <- kTypes) {
      val alias = km.chooseServerAlias(keyType, null, null)
      testX509KeyManagerAlias(km, alias, keyType, many = false, empty = empty)
    }
    km match {
      case ekm: X509ExtendedKeyManager =>
        testX509ExtendedKeyManager(ekm, empty, algorithm)
      case _ => ()
    }
  }

  private def testX509ExtendedKeyManager(
      km: X509ExtendedKeyManager,
      empty: Boolean,
      algorithm: String,
  ): Unit = {
    val kTypes = keyTypes(algorithm)
    val rotatedTypes = rotate(nonEmpty(kTypes))
    for (keyList <- rotatedTypes) {
      val alias = km.chooseEngineClientAlias(keyList, null, null)
      testX509KeyManagerAlias(km, alias, null, many = true, empty = empty)
    }

    for (keyType <- kTypes) {
      val array = Array(keyType)
      val alias = km.chooseEngineClientAlias(array, null, null)
      testX509KeyManagerAlias(km, alias, keyType, many = false, empty = empty)
    }
    for (keyType <- kTypes) {
      val alias = km.chooseEngineServerAlias(keyType, null, null)
      testX509KeyManagerAlias(km, alias, keyType, many = false, empty = empty)
    }
  }

  // Filters null or empty values from a String array and returns a new array with the results.
  private def nonEmpty(input: Array[String]): Array[String] = {
    val buf = new Array[String](input.length)
    var size = 0
    for (keyType <- input)
      if (keyType != null && !keyType.isEmpty()) {
        buf(size) = keyType
        size += 1
      }
    Arrays.copyOfRange(buf, 0, size)
  }

  // Generates an array of arrays of all the rotational permutations of its input.
  private def rotate(input: Array[String]): Array[Array[String]] = {
    val size = input.length
    val result = Array.ofDim[String](size, size)
    for {
      i <- 0 until size
      j <- 0 until size
    }
      result(i)(j) = input((i + j) % size)
    result
  }

  private def testX509KeyManagerAlias(
      km: X509KeyManager,
      alias: String,
      keyType: String,
      many: Boolean,
      empty: Boolean,
  ): Unit = {
    if (empty || (!many && (keyType == null || keyType.isEmpty()))) {
      // s"Expected null alias for keyType=$keyType"
      assert(alias == null)
      // s"Expected null cert chain for keyType=$keyType"
      assert(km.getCertificateChain(alias) == null)
      // s"Expected null private key for keyType=$keyType"
      assert(km.getPrivateKey(alias) == null)
      return
    }

    assert(alias != null)
    val certificateChain = km.getCertificateChain(alias)
    val privateKey = km.getPrivateKey(alias)

    val keyAlgName = privateKey.getAlgorithm()

    val certificate = certificateChain(0)
    // s"keyType=$keyType: expected $keyAlgName == ${certificate.getPublicKey().getAlgorithm()}",
    assert(keyAlgName == certificate.getPublicKey().getAlgorithm())

    val sigAlgName = certificate.getSigAlgName()

    val privateKeyEntry = mockKeyStore.getPrivateKey(keyAlgName, sigAlgName)

    // s"keyType=$keyType: certificate chains differ",
    assert(
      privateKeyEntry.getCertificateChain().sameElements(certificateChain),
    )
    // s"keyType=$keyType: private keys differ"
    assert(privateKeyEntry.getPrivateKey() == privateKey)

    if (keyType != null) {
      assert(KeyStoreUtils.keyAlgorithm(keyType) == keyAlgName)

      // Skip this when we're given only "DH" or "EC" instead of "DH_DSA",
      // "EC_RSA", etc. since we don't know what the expected
      // algorithm was.
      if (keyType != "DH" && keyType != "EC") {
        // s"SigAlg: $sigAlgName, KeyType: $keyType"
        assert(sigAlgName.contains(KeyStoreUtils.signatureAlgorithm(keyType)))
      }
    }
  }

  private class NoGetEntryKeyStore(keyStore: KeyStore)
      extends KeyStore(
        new NoGetEntryKeyStoreSpi(keyStore),
        keyStore.getProvider(),
        keyStore.getType(),
      ) {
    load(null, null)
  }

  // Android Keystore's KeyStore doesn't support getEntry(), so we replicate that here
  // for testing by throwing UnsupportedOperationException and passing everything else through
  // to a working implementation.
  private class NoGetEntryKeyStoreSpi(keyStore: KeyStore) extends KeyStoreSpi {

    override def engineGetEntry(
        alias: String,
        protParam: KeyStore.ProtectionParameter,
    ): KeyStore.Entry =
      throw new UnsupportedOperationException()

    override def engineGetKey(s: String, chars: Array[Char]): Key =
      try keyStore.getKey(s, chars)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineGetCertificateChain(s: String): Array[Certificate] =
      try keyStore.getCertificateChain(s)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineGetCertificate(s: String): Certificate =
      try keyStore.getCertificate(s)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineGetCreationDate(s: String): Date =
      try keyStore.getCreationDate(s)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineSetKeyEntry(
        s: String,
        key: Key,
        chars: Array[Char],
        certificates: Array[Certificate],
    ): Unit =
      try keyStore.setKeyEntry(s, key, chars, certificates)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineSetKeyEntry(
        s: String,
        bytes: Array[Byte],
        certificates: Array[Certificate],
    ): Unit =
      try keyStore.setKeyEntry(s, bytes, certificates)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineSetCertificateEntry(s: String, certificate: Certificate): Unit =
      try keyStore.setCertificateEntry(s, certificate)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineDeleteEntry(s: String): Unit =
      try keyStore.deleteEntry(s)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineAliases(): Enumeration[String] =
      try keyStore.aliases()
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineContainsAlias(s: String): Boolean =
      try keyStore.containsAlias(s)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineSize(): Int =
      try keyStore.size()
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineIsKeyEntry(s: String): Boolean =
      try keyStore.isKeyEntry(s)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineIsCertificateEntry(s: String): Boolean =
      try keyStore.isCertificateEntry(s)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineGetCertificateAlias(certificate: Certificate): String =
      try keyStore.getCertificateAlias(certificate)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineStore(outputStream: OutputStream, chars: Array[Char]): Unit =
      try keyStore.store(outputStream, chars)
      catch { case e: KeyStoreException => throw new AssertionError(e) }

    override def engineLoad(inputStream: InputStream, chars: Array[Char]): Unit = {
      // Do nothing, the keystore is already loaded
    }
  }

  def tests: Tests = Tests:

    test("test_KeyManagerFactory_getInstance") {
      val kmf = KeyManagerFactory.getInstance(StandardNames.KEY_MANAGER_FACTORY_SUPPORTS_ALGORITHM)
      assert(kmf.getAlgorithm() == StandardNames.KEY_MANAGER_FACTORY_SUPPORTS_ALGORITHM)
      testKeyManagerFactory(kmf)

      // kmf = KeyManagerFactory.getInstance(algorithm, provider)
      // assert(algorithm == kmf.getAlgorithm())
      // assert(provider == kmf.getProvider())
      // testKeyManagerFactory(kmf)

      // kmf = KeyManagerFactory.getInstance(algorithm, provider.getName())
      // assert(algorithm == kmf.getAlgorithm())
      // assert(provider == kmf.getProvider())
      // testKeyManagerFactory(kmf)
    }

    // // The Conscrypt provider on OpenJDK doesn't provide the KeyManagerFactory, but we want
    // // to test it on OpenJDK anyway
    // test("test_KeyManagerFactory_Conscrypt") {
    //   val kmf = new KeyManagerFactory(
    //     new KeyManagerFactoryImpl(),
    //     TestUtils.getConscryptProvider(),
    //     KeyManagerFactory.getDefaultAlgorithm,
    //   ) {}
    //   testKeyManagerFactory(kmf)

    //   // Test that using a KeyStore that doesn't implement getEntry(), like Android Keystore
    //   // doesn't, still produces a functional KeyManager.
    //   kmf.init(new NoGetEntryKeyStore(mockKeyStore.keyStore), mockKeyStore.storePassword)
    //   testKeyManagerFactoryGetKeyManagers(kmf, empty = false)
    // }
