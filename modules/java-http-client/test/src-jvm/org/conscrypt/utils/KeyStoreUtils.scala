// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/testing/src/main/java/org/conscrypt/java/security/KeyStoreUtils.java

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

package org.conscrypt.utils

import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.CRLReason
import org.bouncycastle.asn1.x509.ExtendedKeyUsage
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.GeneralNames
import org.bouncycastle.asn1.x509.GeneralSubtree
import org.bouncycastle.asn1.x509.KeyPurposeId
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.asn1.x509.NameConstraints
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
import org.bouncycastle.cert.ocsp.BasicOCSPResp
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder
import org.bouncycastle.cert.ocsp.CertificateID
import org.bouncycastle.cert.ocsp.CertificateStatus
import org.bouncycastle.cert.ocsp.OCSPResp
import org.bouncycastle.cert.ocsp.OCSPRespBuilder
import org.bouncycastle.cert.ocsp.RevokedStatus
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.DigestCalculatorProvider
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.PrintStream
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStore.PasswordProtection
import java.security.KeyStore.PrivateKeyEntry
import java.security.KeyStore.TrustedCertificateEntry
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.Principal
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.UnrecoverableEntryException
import java.security.UnrecoverableKeyException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.AlgorithmParameterSpec
import java.util.ArrayList
import java.util.Collections
import java.util.Date
import java.util.List as JList
import javax.crypto.spec.DHParameterSpec
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.security.auth.x500.X500Principal

import scala.collection.JavaConverters.*
import scala.util.boundary

import utest.assert

import _root_.org.conscrypt.utils.{MockKeyManager, TrustManagerUtils}

/**
 * KeyStoreUtils is a convenience class for other tests that want a canned KeyStore with a variety
 * of key pairs.
 *
 * Creating a key store is relatively slow, so a singleton instance is accessible via
 * KeyStoreUtils.get().
 */
object KeyStoreUtils {

  /** Size of DSA keys to generate for testing. */
  private val DSA_KEY_SIZE_BITS = 1024

  /** Size of EC keys to generate for testing. */
  private val EC_KEY_SIZE_BITS = 256

  /** Size of RSA keys to generate for testing. */
  private val RSA_KEY_SIZE_BITS = 1024

  // Generated with: openssl dhparam -C 1024
  private val DH_PARAMS_P = new BigInteger(
    1,
    Array[Byte](
      0xa2.toByte,
      0x31.toByte,
      0xb4.toByte,
      0xb3.toByte,
      0x6d.toByte,
      0x9b.toByte,
      0x7e.toByte,
      0xf4.toByte,
      0xe7.toByte,
      0x21.toByte,
      0x51.toByte,
      0x40.toByte,
      0xeb.toByte,
      0xc6.toByte,
      0xb6.toByte,
      0xd6.toByte,
      0x54.toByte,
      0x56.toByte,
      0x72.toByte,
      0xbe.toByte,
      0x43.toByte,
      0x18.toByte,
      0x30.toByte,
      0x5c.toByte,
      0x15.toByte,
      0x5a.toByte,
      0xf9.toByte,
      0x19.toByte,
      0x62.toByte,
      0xad.toByte,
      0xf4.toByte,
      0x29.toByte,
      0xcb.toByte,
      0xc6.toByte,
      0xf6.toByte,
      0x64.toByte,
      0x0b.toByte,
      0x9d.toByte,
      0x23.toByte,
      0x80.toByte,
      0xf9.toByte,
      0x5b.toByte,
      0x1c.toByte,
      0x1c.toByte,
      0x6a.toByte,
      0xb4.toByte,
      0xea.toByte,
      0xb9.toByte,
      0x80.toByte,
      0x98.toByte,
      0x8b.toByte,
      0xaf.toByte,
      0x15.toByte,
      0xa8.toByte,
      0x5c.toByte,
      0xc4.toByte,
      0xb0.toByte,
      0x41.toByte,
      0x29.toByte,
      0x66.toByte,
      0x9f.toByte,
      0x9f.toByte,
      0x1f.toByte,
      0x88.toByte,
      0x50.toByte,
      0x97.toByte,
      0x38.toByte,
      0x0b.toByte,
      0x01.toByte,
      0x16.toByte,
      0xd6.toByte,
      0x84.toByte,
      0x1d.toByte,
      0x48.toByte,
      0x6f.toByte,
      0x7c.toByte,
      0x06.toByte,
      0x8c.toByte,
      0x6e.toByte,
      0x68.toByte,
      0xcd.toByte,
      0x38.toByte,
      0xe6.toByte,
      0x22.toByte,
      0x30.toByte,
      0x61.toByte,
      0x37.toByte,
      0x02.toByte,
      0x3d.toByte,
      0x47.toByte,
      0x62.toByte,
      0xce.toByte,
      0xb9.toByte,
      0x1a.toByte,
      0x69.toByte,
      0x9d.toByte,
      0xa1.toByte,
      0x9f.toByte,
      0x10.toByte,
      0xa1.toByte,
      0xaa.toByte,
      0x70.toByte,
      0xf7.toByte,
      0x27.toByte,
      0x9c.toByte,
      0xd4.toByte,
      0xa5.toByte,
      0x15.toByte,
      0xe2.toByte,
      0x15.toByte,
      0x0c.toByte,
      0x20.toByte,
      0x90.toByte,
      0x08.toByte,
      0xb6.toByte,
      0xf5.toByte,
      0xdf.toByte,
      0x1c.toByte,
      0xcb.toByte,
      0x82.toByte,
      0x6d.toByte,
      0xc0.toByte,
      0xe1.toByte,
      0xbd.toByte,
      0xcc.toByte,
      0x4a.toByte,
      0x76.toByte,
      0xe3.toByte,
    ),
  )

  // generator of 2
  private val DH_PARAMS_G = BigInteger.valueOf(2)

  @volatile private var ROOT_CA: KeyStoreUtils = _
  @volatile private var INTERMEDIATE_CA: KeyStoreUtils = _
  @volatile private var INTERMEDIATE_CA_2: KeyStoreUtils = _
  @volatile private var INTERMEDIATE_CA_EC: KeyStoreUtils = _

  @volatile private var SERVER: KeyStoreUtils = _
  @volatile private var SERVER_HOSTNAME: KeyStoreUtils = _
  @volatile private var CLIENT: KeyStoreUtils = _
  @volatile private var CLIENT_CERTIFICATE: KeyStoreUtils = _
  @volatile private var CLIENT_EC_RSA_CERTIFICATE: KeyStoreUtils = _
  @volatile private var CLIENT_EC_EC_CERTIFICATE: KeyStoreUtils = _

  @volatile private var CLIENT_2: KeyStoreUtils = _

  if (!classOf[BouncyCastleProvider].getName().startsWith("com.android")) {
    // If we run outside of the Android system, we need to make sure
    // that the BouncyCastleProvider's static field keyInfoConverters
    // is initialized. This happens in the default constructor only.
    new BouncyCastleProvider()
  }

  private val LOCAL_HOST_ADDRESS: Array[Byte] = Array(127, 0, 0, 1)
  private val LOCAL_HOST_NAME = "localhost"
  private val LOCAL_HOST_NAME_IPV6 = "ip6-localhost"
  val CERT_HOSTNAME = "example.com"

  def createKeyManagers(keyStore: KeyStore, storePassword: Array[Char]): Array[KeyManager] =
    try {
      val kmfa = KeyManagerFactory.getDefaultAlgorithm()
      val kmf = KeyManagerFactory.getInstance(kmfa)
      kmf.init(keyStore, storePassword)
      MockKeyManager.wrap(kmf.getKeyManagers())
    } catch {
      case e: Exception => throw new RuntimeException(e)
    }

  def createTrustManagers(keyStore: KeyStore): Array[TrustManager] =
    try {
      val tmfa = TrustManagerFactory.getDefaultAlgorithm()
      val tmf = TrustManagerFactory.getInstance(tmfa)
      tmf.init(keyStore)
      TrustManagerUtils.wrap(tmf.getTrustManagers())
    } catch {
      case e: Exception => throw new RuntimeException(e)
    }

  /**
   * Lazily create shared test certificates.
   */
  private def initCerts(): Unit = synchronized {
    if (ROOT_CA != null) return

    ROOT_CA = new Builder()
      .aliasPrefix("RootCA")
      .subject("CN=Test Root Certificate Authority")
      .ca(true)
      .certificateSerialNumber(BigInteger.valueOf(1))
      .build()

    INTERMEDIATE_CA_EC = new Builder()
      .aliasPrefix("IntermediateCA-EC")
      .keyAlgorithms(Seq("EC"))
      .subject("CN=Test Intermediate Certificate Authority ECDSA")
      .ca(true)
      .signer(ROOT_CA.getPrivateKey("RSA", "RSA"))
      .rootCa(ROOT_CA.getRootCertificate("RSA"))
      .certificateSerialNumber(BigInteger.valueOf(2))
      .build()

    INTERMEDIATE_CA = new Builder()
      .aliasPrefix("IntermediateCA")
      .subject("CN=Test Intermediate Certificate Authority")
      .ca(true)
      .signer(ROOT_CA.getPrivateKey("RSA", "RSA"))
      .rootCa(ROOT_CA.getRootCertificate("RSA"))
      .certificateSerialNumber(BigInteger.valueOf(2))
      .build()

    SERVER = new Builder()
      .aliasPrefix("server")
      .signer(INTERMEDIATE_CA.getPrivateKey("RSA", "RSA"))
      .rootCa(INTERMEDIATE_CA.getRootCertificate("RSA"))
      .addSubjectAltNameIpAddress(LOCAL_HOST_ADDRESS)
      .certificateSerialNumber(BigInteger.valueOf(3))
      .build()

    SERVER_HOSTNAME = new Builder()
      .aliasPrefix("server-hostname")
      .signer(INTERMEDIATE_CA.getPrivateKey("RSA", "RSA"))
      .rootCa(INTERMEDIATE_CA.getRootCertificate("RSA"))
      .addSubjectAltNameDnsName(CERT_HOSTNAME)
      .certificateSerialNumber(BigInteger.valueOf(4))
      .build()

    CLIENT = new KeyStoreUtils(createClient(INTERMEDIATE_CA.keyStore), null, null)

    CLIENT_EC_RSA_CERTIFICATE = new Builder()
      .aliasPrefix("client-ec")
      .keyAlgorithms(Seq("EC"))
      .subject("emailAddress=test-ec@user")
      .signer(INTERMEDIATE_CA.getPrivateKey("RSA", "RSA"))
      .rootCa(INTERMEDIATE_CA.getRootCertificate("RSA"))
      .build()

    CLIENT_EC_EC_CERTIFICATE = new Builder()
      .aliasPrefix("client-ec")
      .keyAlgorithms(Seq("EC"))
      .subject("emailAddress=test-ec@user")
      .signer(INTERMEDIATE_CA_EC.getPrivateKey("EC", "RSA"))
      .rootCa(INTERMEDIATE_CA_EC.getRootCertificate("RSA"))
      .build()

    CLIENT_CERTIFICATE = new Builder()
      .aliasPrefix("client")
      .subject("emailAddress=test@user")
      .signer(INTERMEDIATE_CA.getPrivateKey("RSA", "RSA"))
      .rootCa(INTERMEDIATE_CA.getRootCertificate("RSA"))
      .build()

    val rootCa2 = new Builder()
      .aliasPrefix("RootCA2")
      .subject("CN=Test Root Certificate Authority 2")
      .ca(true)
      .build()

    INTERMEDIATE_CA_2 = new Builder()
      .aliasPrefix("IntermediateCA")
      .subject("CN=Test Intermediate Certificate Authority")
      .ca(true)
      .signer(rootCa2.getPrivateKey("RSA", "RSA"))
      .rootCa(rootCa2.getRootCertificate("RSA"))
      .build()

    CLIENT_2 = new KeyStoreUtils(createClient(rootCa2.keyStore), null, null)
  }

  def getRootCa(): KeyStoreUtils = { initCerts(); ROOT_CA }
  def getIntermediateCa(): KeyStoreUtils = { initCerts(); INTERMEDIATE_CA }
  def getIntermediateCa2(): KeyStoreUtils = { initCerts(); INTERMEDIATE_CA_2 }
  def getServer(): KeyStoreUtils = { initCerts(); SERVER }
  def getServerHostname(): KeyStoreUtils = { initCerts(); SERVER_HOSTNAME }
  def getClient(): KeyStoreUtils = { initCerts(); CLIENT }
  def getClientCertificate(): KeyStoreUtils = { initCerts(); CLIENT_CERTIFICATE }
  def getClientEcRsaCertificate(): KeyStoreUtils = { initCerts(); CLIENT_EC_RSA_CERTIFICATE }
  def getClientEcEcCertificate(): KeyStoreUtils = { initCerts(); CLIENT_EC_EC_CERTIFICATE }
  def getClientCA2(): KeyStoreUtils = { initCerts(); CLIENT_2 }

  /**
   * Creates KeyStores containing the requested key types. Since key generation can be expensive,
   * most tests should reuse the RSA-only singleton instance returned by KeyStoreUtils.get.
   */
  class Builder {
    private var keyAlgorithms: Seq[String] = Seq("RSA")
    private var storePassword: Array[Char] = _
    private var keyPassword: Array[Char] = _
    private var aliasPrefix: String = _
    private var subject: X500Principal = _
    private var keyUsage: Int = 0
    private var ca: Boolean = false
    private var privateEntry: PrivateKeyEntry = _
    private var signer: PrivateKeyEntry = _
    private var rootCa: Certificate = _
    private val extendedKeyUsages = new ArrayList[KeyPurposeId]()
    private val criticalExtendedKeyUsages = new ArrayList[Boolean]()
    private val subjectAltNames = new ArrayList[GeneralName]()
    private val permittedNameConstraints = new ArrayList[GeneralSubtree]()
    private val excludedNameConstraints = new ArrayList[GeneralSubtree]()
    private var certificateSerialNumber: BigInteger = _

    /**
     * Sets the requested key types to generate and include. The default is RSA only.
     */
    def keyAlgorithms(algorithms: Seq[String]): Builder = {
      this.keyAlgorithms = algorithms
      this
    }

    /** A unique prefix to identify the key aliases */
    def aliasPrefix(prefix: String): Builder = {
      this.aliasPrefix = prefix
      this
    }

    /**
     * Sets the subject common name. The default is the local host's canonical name.
     */
    def subject(subject: X500Principal): Builder = {
      this.subject = subject
      this
    }

    def subject(commonName: String): Builder =
      subject(new X500Principal(commonName))

    /** {@link KeyUsage} bit mask for 2.5.29.15 extension */
    def keyUsage(usage: Int): Builder = {
      this.keyUsage = usage
      this
    }

    /** true If the keys being created are for a CA */
    def ca(ca: Boolean): Builder = {
      this.ca = ca
      this
    }

    /** a private key entry to use for the generation of the certificate */
    def privateEntry(entry: PrivateKeyEntry): Builder = {
      this.privateEntry = entry
      this
    }

    /** a private key entry to be used for signing, otherwise self-sign */
    def signer(signer: PrivateKeyEntry): Builder = {
      this.signer = signer
      this
    }

    /** a root CA to include in the final store */
    def rootCa(root: Certificate): Builder = {
      this.rootCa = root
      this
    }

    def addExtendedKeyUsage(keyPurposeId: KeyPurposeId, critical: Boolean): Builder = {
      extendedKeyUsages.add(keyPurposeId)
      criticalExtendedKeyUsages.add(critical)
      this
    }

    def addSubjectAltName(generalName: GeneralName): Builder = {
      subjectAltNames.add(generalName)
      this
    }

    def addSubjectAltNameDnsName(dnsName: String): Builder =
      addSubjectAltName(new GeneralName(GeneralName.dNSName, dnsName))

    def addSubjectAltNameIpAddress(ipAddress: Array[Byte]): Builder =
      addSubjectAltName(new GeneralName(GeneralName.iPAddress, new DEROctetString(ipAddress)))

    private def addNameConstraint(permitted: Boolean, generalName: GeneralName): Builder = {
      if (permitted) permittedNameConstraints.add(new GeneralSubtree(generalName))
      else excludedNameConstraints.add(new GeneralSubtree(generalName))
      this
    }

    def addNameConstraint(permitted: Boolean, ipAddress: Array[Byte]): Builder =
      addNameConstraint(
        permitted,
        new GeneralName(GeneralName.iPAddress, new DEROctetString(ipAddress)),
      )

    def certificateSerialNumber(serial: BigInteger): Builder = {
      this.certificateSerialNumber = serial
      this
    }

    def build(): KeyStoreUtils =
      try {
        if (StandardNames.IS_RI) {
          if (storePassword == null)
            storePassword = "password".toCharArray()
          if (keyPassword == null)
            keyPassword = "password".toCharArray()
        }

        /*
         * This is not implemented for other key types because the logic
         * would be long to write and it's not needed currently.
         */
        if (
          privateEntry != null
          && (keyAlgorithms.length != 1 || "RSA" != keyAlgorithms(0))
        ) {
          throw new IllegalStateException("Only reusing an existing key is implemented for RSA")
        }

        val keyStore = createKeyStore()
        for (keyAlgorithm <- keyAlgorithms) {
          val publicAlias = aliasPrefix + "-public-" + keyAlgorithm
          val privateAlias = aliasPrefix + "-private-" + keyAlgorithm

          if (
            (keyAlgorithm == "EC_RSA" || keyAlgorithm == "DH_RSA")
            && signer == null
            && rootCa == null
          ) {
            createKeys(
              keyStore,
              keyAlgorithm,
              publicAlias,
              privateAlias,
              null,
              privateKey(keyStore, keyPassword, "RSA", "RSA"),
            )
          } else if (keyAlgorithm == "DH_DSA" && signer == null && rootCa == null) {
            createKeys(
              keyStore,
              keyAlgorithm,
              publicAlias,
              privateAlias,
              null,
              privateKey(keyStore, keyPassword, "DSA", "DSA"),
            )
          } else {
            createKeys(
              keyStore,
              keyAlgorithm,
              publicAlias,
              privateAlias,
              privateEntry,
              signer,
            )
          }
        }

        if (rootCa != null) {
          keyStore.setCertificateEntry(
            aliasPrefix + "-root-ca-" + rootCa.getPublicKey().getAlgorithm(),
            rootCa,
          )
        }
        new KeyStoreUtils(keyStore, storePassword, keyPassword)
      } catch {
        case e: Exception => throw new RuntimeException(e)
      }

    /**
     * Add newly generated keys of a given key type to an existing KeyStore. The PrivateKey will be
     * stored under the specified private alias name. The X509Certificate will be stored on the
     * public alias name and have the given subject distinguished name.
     *
     * If a CA is provided, it will be used to sign the generated certificate and OCSP responses.
     * Otherwise, the certificate will be self signed. The certificate will be valid for one day
     * before and one day after the time of creation.
     *
     * Based on:
     *   - org.bouncycastle.jce.provider.test.SigTest
     *   - org.bouncycastle.jce.provider.test.CertTest
     */
    private def createKeys(
        keyStore: KeyStore,
        keyAlgorithm: String,
        publicAlias: String,
        privateAlias: String,
        privateEntry: PrivateKeyEntry,
        signer: PrivateKeyEntry,
    ): KeyStore = {
      val caKey =
        if signer == null
        then null
        else signer.getPrivateKey()
      val caCert =
        if signer == null
        then null
        else signer.getCertificate().asInstanceOf[X509Certificate]
      val caCertChain =
        if signer == null
        then null
        else signer.getCertificateChain().asInstanceOf[Array[X509Certificate]]

      if (subject == null) {
        subject = localhost()
        addSubjectAltNameDnsName(LOCAL_HOST_NAME)
        addSubjectAltNameDnsName(LOCAL_HOST_NAME_IPV6)
      }

      var privateKey: PrivateKey = null
      var publicKey: PublicKey = null
      var x509c: X509Certificate = null
      var keyAlgo = keyAlgorithm
      if (publicAlias == null && privateAlias == null) {
        // don't want anything apparently
        privateKey = null
        x509c = null
      } else {
        if (privateEntry == null) {
          // 1a.) we make the keys
          var keySize: Int = -1
          var spec: AlgorithmParameterSpec = null
          if (keyAlgorithm == "RSA") {
            keySize = RSA_KEY_SIZE_BITS
          } else if (keyAlgorithm == "DH_RSA" || keyAlgorithm == "DH_DSA") {
            spec = new DHParameterSpec(DH_PARAMS_P, DH_PARAMS_G)
            keyAlgo = "DH"
          } else if (keyAlgorithm == "DSA") {
            keySize = DSA_KEY_SIZE_BITS
          } else if (keyAlgorithm == "EC") {
            keySize = EC_KEY_SIZE_BITS
          } else if (keyAlgorithm == "EC_RSA") {
            keySize = EC_KEY_SIZE_BITS
            keyAlgo = "EC"
          } else {
            throw new IllegalArgumentException("Unknown key algorithm " + keyAlgorithm)
          }

          val kpg = KeyPairGenerator.getInstance(keyAlgorithm)
          if (spec != null) {
            kpg.initialize(spec)
          } else {
            kpg.initialize(keySize)
          }

          val kp = kpg.generateKeyPair()
          privateKey = kp.getPrivate()
          publicKey = kp.getPublic()
        } else {
          // 1b.) we use the previous keys
          privateKey = privateEntry.getPrivateKey()
          publicKey = privateEntry.getCertificate().getPublicKey()
        }

        // 2.) use keys to make certificate
        val issuer: X500Principal =
          if caCert != null then caCert.getSubjectX500Principal() else subject
        val signingKey: PrivateKey = if caKey == null then privateKey else caKey
        x509c = createCertificate(
          publicKey,
          signingKey,
          subject,
          issuer,
          keyUsage,
          ca,
          extendedKeyUsages,
          criticalExtendedKeyUsages,
          subjectAltNames,
          permittedNameConstraints,
          excludedNameConstraints,
          certificateSerialNumber,
        )
      }

      var x509cc: Array[X509Certificate] = null
      if (privateAlias == null) {
        // don't need certificate chain
        x509cc = null
      } else if (caCertChain == null) {
        x509cc = Array[X509Certificate](x509c)
      } else {
        x509cc = Array.ofDim[X509Certificate](caCertChain.length + 1)
        x509cc(0) = x509c
        System.arraycopy(caCertChain, 0, x509cc, 1, caCertChain.length)
      }

      // 3.) put certificate and private key into the key store
      if (privateAlias != null) {
        keyStore.setKeyEntry(
          privateAlias,
          privateKey,
          keyPassword,
          x509cc.asInstanceOf[Array[Certificate]],
        )
      }
      if (publicAlias != null) {
        keyStore.setCertificateEntry(publicAlias, x509c)
      }

      keyStore
    }

    private def localhost(): X500Principal =
      new X500Principal("CN=" + LOCAL_HOST_NAME)
  }

  def createCa(publicKey: PublicKey, privateKey: PrivateKey, subjectStr: String): X509Certificate =
    try {
      val principal = new X500Principal(subjectStr)
      createCertificate(
        publicKey,
        privateKey,
        principal,
        principal,
        0,
        true,
        new ArrayList(),
        new ArrayList(),
        new ArrayList(),
        new ArrayList(),
        new ArrayList(),
        null,
      )
    } catch {
      case e: Exception => throw new RuntimeException(e)
    }

  @SuppressWarnings(Array("JavaUtilDate"))
  private def createCertificate(
      publicKey: PublicKey,
      privateKey: PrivateKey,
      subject: X500Principal,
      issuer: X500Principal,
      keyUsage: Int,
      ca: Boolean,
      extendedKeyUsages: JList[KeyPurposeId],
      criticalExtendedKeyUsages: JList[Boolean],
      subjectAltNames: JList[GeneralName],
      permittedNameConstraints: JList[GeneralSubtree],
      excludedNameConstraints: JList[GeneralSubtree],
      serialNumberParam: BigInteger,
  ): X509Certificate = {
    // Note that there is no way to programmatically make a
    // Certificate using java.* or javax.* APIs. The
    // CertificateFactory interface assumes you want to read
    // in a stream of bytes, typically the X.509 factory would
    // allow ASN.1 DER encoded bytes and optionally some PEM
    // formats. Here we use Bouncy Castle's
    // X509V3CertificateGenerator and related classes.

    val millisPerDay = 24L * 60 * 60 * 1000
    val now = System.currentTimeMillis()
    val start = new Date(now - millisPerDay)
    val end = new Date(now + millisPerDay)

    val keyAlgorithm = privateKey.getAlgorithm()
    val signatureAlgorithm = keyAlgorithm match {
      case "RSA" | "EC_RSA" => "sha256WithRSA"
      case "DSA"            => "sha256WithDSA"
      case "EC"             => "sha256WithECDSA"
      case _ => throw new IllegalArgumentException("Unknown key algorithm " + keyAlgorithm)
    }

    val serialNumber = if (serialNumberParam == null) {
      val serialBytes = new Array[Byte](16)
      new SecureRandom().nextBytes(serialBytes)
      new BigInteger(1, serialBytes)
    } else serialNumberParam

    val x509cg = new X509v3CertificateBuilder(
      X500Name.getInstance(issuer.getEncoded()),
      serialNumber,
      start,
      end,
      X500Name.getInstance(subject.getEncoded()),
      SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()),
    )

    if (keyUsage != 0) x509cg.addExtension(Extension.keyUsage, true, new KeyUsage(keyUsage))
    if (ca) x509cg.addExtension(Extension.basicConstraints, true, new BasicConstraints(true))

    for (i <- 0 until extendedKeyUsages.size()) {
      val keyPurposeId = extendedKeyUsages.get(i)
      val critical = criticalExtendedKeyUsages.get(i)
      x509cg.addExtension(Extension.extendedKeyUsage, critical, new ExtendedKeyUsage(keyPurposeId))
    }

    if (!subjectAltNames.isEmpty) {
      x509cg.addExtension(
        Extension.subjectAlternativeName,
        false,
        new GeneralNames(subjectAltNames.toArray(new Array[GeneralName](0))).getEncoded(),
      )
    }

    if (!permittedNameConstraints.isEmpty || !excludedNameConstraints.isEmpty) {
      x509cg.addExtension(
        Extension.nameConstraints,
        true,
        new NameConstraints(
          permittedNameConstraints.toArray(new Array[GeneralSubtree](0)),
          excludedNameConstraints.toArray(new Array[GeneralSubtree](0)),
        ),
      )
    }

    val x509holder = x509cg.build(new JcaContentSignerBuilder(signatureAlgorithm).build(privateKey))
    val certFactory = CertificateFactory.getInstance("X.509")
    var x509c = certFactory
      .generateCertificate(new ByteArrayInputStream(x509holder.getEncoded()))
      .asInstanceOf[X509Certificate]

    if (StandardNames.IS_RI) {
      val cf = CertificateFactory.getInstance("X.509")
      x509c = cf
        .generateCertificate(new ByteArrayInputStream(x509c.getEncoded()))
        .asInstanceOf[X509Certificate]
    }
    x509c
  }

  /**
   * Return the key algorithm for a possible compound algorithm identifier containing an underscore.
   * If not underscore is present, the argument is returned unmodified. However for an algorithm
   * such as EC_RSA, return EC.
   */
  def keyAlgorithm(algorithm: String): String = {
    val index = algorithm.indexOf('_')
    if (index == -1) algorithm else algorithm.substring(0, index)
  }

  /**
   * Return the signature algorithm for a possible compound algorithm identifier containing an
   * underscore. If not underscore is present, the argument is returned unmodified. However for an
   * algorithm such as EC_RSA, return RSA.
   */
  def signatureAlgorithm(algorithm: String): String = {
    val index = algorithm.indexOf('_')
    if (index == -1) algorithm else algorithm.substring(index + 1)
  }

  /**
   * Create an empty KeyStore
   */
  def createKeyStore(): KeyStore =
    try {
      val keyStore = KeyStore.getInstance(StandardNames.KEY_STORE_ALGORITHM)
      keyStore.load(null, null)
      keyStore
    } catch {
      case e: Exception => throw new RuntimeException(e)
    }

  /**
   * Return the only private key in a keystore for the given algorithms. Throws
   * IllegalStateException if there are are more or less than one.
   */
  def privateKey(
      keyStore: KeyStore,
      keyPassword: Array[Char],
      keyAlgorithm: String,
      signatureAlgorithm: String,
  ): PrivateKeyEntry =
    try {
      var found: PrivateKeyEntry = null
      val password = new PasswordProtection(keyPassword)
      for (alias <- Collections.list(keyStore.aliases()).asScala)
        if (keyStore.entryInstanceOf(alias, classOf[PrivateKeyEntry])) {
          val privateKey = keyStore.getEntry(alias, password).asInstanceOf[PrivateKeyEntry]
          if (privateKey.getPrivateKey().getAlgorithm() == keyAlgorithm) {
            val certificate = privateKey.getCertificate().asInstanceOf[X509Certificate]
            if (certificate.getSigAlgName().contains(signatureAlgorithm)) {
              if (found != null) {
                throw new IllegalStateException(
                  s"KeyStore has more than one private key for keyAlgorithm: $keyAlgorithm signatureAlgorithm: $signatureAlgorithm",
                )
              }
              found = privateKey
            }
          }
        }
      if (found == null) {
        throw new IllegalStateException(
          s"KeyStore contained no private key for keyAlgorithm: $keyAlgorithm signatureAlgorithm: $signatureAlgorithm",
        )
      }
      found
    } catch {
      case e: Exception =>
        throw new RuntimeException(
          s"Problem getting key for $keyAlgorithm and signature $signatureAlgorithm",
          e,
        )
    }

  /**
   * Return the issuing CA certificate of the given certificate. Throws IllegalStateException if
   * there are are more or less than one.
   */
  def issuer(keyStore: KeyStore, c: Certificate): Certificate = {
    if (!c.isInstanceOf[X509Certificate]) {
      throw new IllegalStateException("issuer requires an X509Certificate, found " + c)
    }
    val cert = c.asInstanceOf[X509Certificate]
    var found: Certificate = null
    for (alias <- Collections.list(keyStore.aliases()).asScala)
      if (keyStore.entryInstanceOf(alias, classOf[TrustedCertificateEntry])) {
        val certificateEntry = keyStore.getEntry(alias, null).asInstanceOf[TrustedCertificateEntry]
        val certificate = certificateEntry.getTrustedCertificate()
        if (certificate.isInstanceOf[X509Certificate]) {
          val x = certificate.asInstanceOf[X509Certificate]
          if (cert.getIssuerDN() == x.getSubjectDN()) {
            if (found != null) {
              throw new IllegalStateException(s"KeyStore has more than one issuing CA for $cert")
            }
            found = certificate
          }
        }
      }
    if (found == null)
      throw new IllegalStateException("KeyStore contained no issuing CA for " + cert)
    found
  }

  /**
   * Return the only self-signed root certificate in a TestKeyStore for the given algorithm. Throws
   * IllegalStateException if there are are more or less than one.
   */
  @SuppressWarnings(Array("JavaUtilDate"))
  private def generateOCSPResponse(
      server: PrivateKeyEntry,
      issuer: PrivateKeyEntry,
      status: CertificateStatus,
  ): OCSPResp =
    try {
      val serverCertJca = server.getCertificate().asInstanceOf[X509Certificate]
      val caCertJca = issuer.getCertificate().asInstanceOf[X509Certificate]
      val caCert = new JcaX509CertificateHolder(caCertJca)
      val digCalcProv = new BcDigestCalculatorProvider()
      val basicBuilder = new BasicOCSPRespBuilder(
        SubjectPublicKeyInfo.getInstance(caCertJca.getPublicKey().getEncoded()),
        digCalcProv.get(CertificateID.HASH_SHA1),
      )
      val certId = new CertificateID(
        digCalcProv.get(CertificateID.HASH_SHA1),
        caCert,
        serverCertJca.getSerialNumber(),
      )
      basicBuilder.addResponse(certId, status)
      val resp = basicBuilder.build(
        new JcaContentSignerBuilder("SHA256withRSA").build(issuer.getPrivateKey()),
        null,
        new Date(),
      )
      new OCSPRespBuilder().build(OCSPRespBuilder.SUCCESSFUL, resp)
    } catch {
      case e: Exception => throw new CertificateException("cannot generate OCSP response", e)
    }

  /**
   * Return the only self-signed root certificate in a keystore for the given algorithm. Throws
   * IllegalStateException if there are are more or less than one.
   */
  @SuppressWarnings(Array("JavaUtilDate"))
  def rootCertificate(keyStore: KeyStore, algorithm: String): X509Certificate =
    try {
      var found: X509Certificate = null
      for (alias <- Collections.list(keyStore.aliases()).asScala)
        if (keyStore.entryInstanceOf(alias, classOf[TrustedCertificateEntry])) {
          val certificateEntry =
            keyStore.getEntry(alias, null).asInstanceOf[TrustedCertificateEntry]
          val certificate = certificateEntry.getTrustedCertificate()
          if (
            certificate.getPublicKey().getAlgorithm() == algorithm && certificate
              .isInstanceOf[X509Certificate]
          ) {
            val x = certificate.asInstanceOf[X509Certificate]
            if (x.getIssuerDN() == x.getSubjectDN()) {
              if (found != null)
                throw new IllegalStateException(
                  s"KeyStore has more than one root CA for $algorithm",
                )
              found = x
            }
          }
        }
      if (found == null)
        throw new IllegalStateException("KeyStore contained no root CA for " + algorithm)
      found
    } catch {
      case e: Exception => throw new RuntimeException(e)
    }

  /**
   * Finds an entry in the keystore by the given alias.
   */
  def entryByAlias(keyStore: KeyStore, alias: String): KeyStore.Entry =
    try
      keyStore.getEntry(alias, null)
    catch {
      case e @ (_: NoSuchAlgorithmException | _: KeyStoreException |
          _: UnrecoverableEntryException) =>
        throw new RuntimeException(e)
    }

  /**
   * Create a client key store that only contains self-signed certificates but no private keys
   */
  def createClient(caKeyStore: KeyStore): KeyStore = {
    val clientKeyStore = createKeyStore()
    copySelfSignedCertificates(clientKeyStore, caKeyStore)
    clientKeyStore
  }

  /**
   * Copy self-signed certificates from one key store to another. Returns true if successful, false
   * if no match found.
   */
  def copySelfSignedCertificates(dst: KeyStore, src: KeyStore): Boolean =
    try {
      var copied = false
      for (alias <- Collections.list(src.aliases()).asScala)
        if (src.isCertificateEntry(alias)) {
          val cert = src.getCertificate(alias).asInstanceOf[X509Certificate]
          if (cert.getSubjectDN() == cert.getIssuerDN()) {
            dst.setCertificateEntry(alias, cert)
            copied = true
          }
        }
      copied
    } catch {
      case e: Exception => throw new RuntimeException(e)
    }

  /**
   * Copy named certificates from one key store to another. Returns true if successful, false if no
   * match found.
   */
  def copyCertificate(subject: Principal, dst: KeyStore, src: KeyStore): Boolean =
    boundary {
      for (alias <- Collections.list(src.aliases()).asScala)
        if (src.isCertificateEntry(alias)) {
          val cert = src.getCertificate(alias).asInstanceOf[X509Certificate]
          if (cert.getSubjectDN() == subject) {
            dst.setCertificateEntry(alias, cert)
            boundary.break(true)
          }
        }
      false
    }

  /**
   * Dump a key store for debugging.
   */
  def dump(context: String, keyStore: KeyStore, keyPassword: Array[Char]): Unit = {
    val out = System.out
    out.println("context=" + context)
    out.println("\tkeyStore=" + keyStore)
    out.println("\tkeyStore.type=" + keyStore.getType())
    out.println("\tkeyStore.provider=" + keyStore.getProvider())
    out.println("\tkeyPassword=" + (if (keyPassword == null) null else new String(keyPassword)))
    out.println("\tsize=" + keyStore.size())
    for (alias <- Collections.list(keyStore.aliases()).asScala) {
      out.println("alias=" + alias)
      out.println("\tcreationDate=" + keyStore.getCreationDate(alias))
      if (keyStore.isCertificateEntry(alias)) {
        out.println("\tcertificate:")
        out.println("==========================================")
        out.println(keyStore.getCertificate(alias))
        out.println("==========================================")
      } else if (keyStore.isKeyEntry(alias)) {
        out.println("\tkey:")
        out.println("==========================================")
        var keyStr = ""
        try
          keyStr = "Key retrieved using password\n" + keyStore.getKey(alias, keyPassword)
        catch {
          case _: UnrecoverableKeyException =>
            try
              keyStr = "Key retrieved without password\n" + keyStore.getKey(alias, null)
            catch {
              case _: UnrecoverableKeyException => keyStr = "Key could not be retrieved"
            }
        }
        out.println(keyStr)
        out.println("==========================================")
        val chain = keyStore.getCertificateChain(alias)
        if (chain == null) {
          out.println("No certificate chain associated with key")
          out.println("==========================================")
        } else {
          for (i <- chain.indices) {
            out.println("Certificate chain element #" + i)
            out.println(chain(i))
            out.println("==========================================")
          }
        }
      } else {
        out.println("\tunknown entry type")
      }
    }
  }

  /*
   * Note chain is Object[] to support both
   * java.security.cert.X509Certificate and
   * javax.security.cert.X509Certificate
   */
  def assertChainLength(chain: Array[AnyRef]): Unit =
    assert(3 == chain.length)
}

final class KeyStoreUtils private (
    val keyStore: KeyStore,
    val storePassword: Array[Char],
    val keyPassword: Array[Char],
) {
  val keyManagers: Array[KeyManager] =
    KeyStoreUtils.createKeyManagers(keyStore, storePassword)
  val trustManagers: Array[TrustManager] =
    KeyStoreUtils.createTrustManagers(keyStore)
  val trustManager: TrustManager =
    trustManagers(0)

  def copy(): KeyStoreUtils =
    new KeyStoreUtils(keyStore, storePassword, keyPassword)

  def getPrivateKey(keyAlgorithm: String, signatureAlgorithm: String): PrivateKeyEntry =
    KeyStoreUtils.privateKey(keyStore, keyPassword, keyAlgorithm, signatureAlgorithm)

  def getIssuer(cert: Certificate): Certificate =
    KeyStoreUtils.issuer(keyStore, cert)

  def getRootCertificate(algorithm: String): X509Certificate =
    KeyStoreUtils.rootCertificate(keyStore, algorithm)

  def getEntryByAlias(alias: String): KeyStore.Entry =
    KeyStoreUtils.entryByAlias(keyStore, alias)

  def dump(context: String): Unit =
    KeyStoreUtils.dump(context, keyStore, keyPassword)
}
