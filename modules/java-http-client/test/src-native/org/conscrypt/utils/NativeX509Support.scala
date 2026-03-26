package org.conscrypt.utils

import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.net.InetAddress
import java.security.{PrivateKey, PublicKey}
import java.security.cert.{CertificateFactory, X509Certificate}
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.{Date, List as JList}
import javax.security.auth.x500.X500Principal
import java.lang.ref.Cleaner

import scala.collection.JavaConverters.*
import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

import snhttp.experimental.openssl.libcrypto

final class DEROctetString(private val octets: Array[Byte]):
  def getOctets(): Array[Byte] = octets.clone()

object GeneralName:
  final val dNSName = 2
  final val iPAddress = 7

final class GeneralName(val tagNo: Int, private val value: Any):
  def toExtensionValue: String =
    tagNo match
      case GeneralName.dNSName => s"DNS:${value.asInstanceOf[String]}"
      case GeneralName.iPAddress =>
        val bytes = value match
          case octets: DEROctetString => octets.getOctets()
          case raw: Array[Byte]       => raw.clone()
          case other =>
            throw new IllegalArgumentException(s"Unsupported iPAddress value: ${other.getClass}")
        s"IP:${InetAddress.getByAddress(bytes).getHostAddress}"
      case other => throw new IllegalArgumentException(s"Unsupported GeneralName tag: $other")

final class GeneralSubtree(val generalName: GeneralName)

final class KeyPurposeId(val value: String)

object KeyPurposeId:
  val anyExtendedKeyUsage = new KeyPurposeId("anyExtendedKeyUsage")
  val id_kp_clientAuth = new KeyPurposeId("clientAuth")
  val id_kp_serverAuth = new KeyPurposeId("serverAuth")
  val id_kp_codeSigning = new KeyPurposeId("codeSigning")

// Lightweight replacements for BouncyCastle OCSP types.
// Full OCSP generation would require <openssl/ocsp.h> bindings.
sealed abstract class CertificateStatus

final class RevokedStatus(val revocationTime: Date, val reasonCode: Int) extends CertificateStatus

object CertificateStatus:
  case object Good extends CertificateStatus

final class OCSPResp(val encoded: Array[Byte]):
  def getStatus(): Int = OCSPResp.SUCCESSFUL

object OCSPResp:
  final val SUCCESSFUL = 0

private[utils] object NativeX509Support:

  private val cleaner = Cleaner.create()
  given zone: Zone = Zone.open()
  cleaner.register(this, () => zone.close())

  private val x509DateFormat =
    DateTimeFormatter.ofPattern("yyyyMMddHHmmss'Z'").withZone(ZoneOffset.UTC)

  def createCertificate(
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
      serialNumber: BigInteger,
      start: Date,
      end: Date,
  ): X509Certificate = {
    // X509_new_ex(null, null) is equivalent to the deprecated X509_new()
    val x509 = requireNonNull(libcrypto.X509_new_ex(null, null), "X509_new_ex")

    try
      checkPositive(libcrypto.X509_set_version(x509, 2), "X509_set_version")
      setSerialNumber(x509, serialNumber)
      setValidity(x509, start, end)

      val subjectName = decodeX509Name(subject)
      val issuerName = decodeX509Name(issuer)
      val publicKeyHandle = decodePublicKey(publicKey)
      val signingKeyHandle = decodePrivateKey(privateKey)

      try
        checkPositive(libcrypto.X509_set_subject_name(x509, subjectName), "X509_set_subject_name")
        checkPositive(libcrypto.X509_set_issuer_name(x509, issuerName), "X509_set_issuer_name")
        checkPositive(libcrypto.X509_set_pubkey(x509, publicKeyHandle), "X509_set_pubkey")

        if ca then addExtension(x509, "basicConstraints", critical = true, "CA:TRUE")

        val keyUsageValue = keyUsageString(keyUsage)
        if keyUsageValue.nonEmpty then
          addExtension(x509, "keyUsage", critical = true, keyUsageValue)

        val extendedKeyUsageValue = extendedKeyUsages.asScala.map(_.value).mkString(",")
        if extendedKeyUsageValue.nonEmpty then
          addExtension(
            x509,
            "extendedKeyUsage",
            criticalExtendedKeyUsages.asScala.exists(java.lang.Boolean.TRUE == _),
            extendedKeyUsageValue,
          )

        val subjectAltNameValue = subjectAltNames.asScala.map(_.toExtensionValue).mkString(",")
        if subjectAltNameValue.nonEmpty then
          addExtension(x509, "subjectAltName", critical = false, subjectAltNameValue)

        val nameConstraintsValue =
          buildNameConstraints(permittedNameConstraints, excludedNameConstraints)
        if nameConstraintsValue.nonEmpty then
          addExtension(x509, "nameConstraints", critical = true, nameConstraintsValue)

        checkPositive(
          libcrypto.X509_sign(x509, signingKeyHandle, libcrypto.EVP_sha256()),
          "X509_sign",
        )
        toJavaCertificate(x509)
      finally
        libcrypto.X509_NAME_free(subjectName)
        libcrypto.X509_NAME_free(issuerName)
        libcrypto.EVP_PKEY_free(publicKeyHandle)
        libcrypto.EVP_PKEY_free(signingKeyHandle)
    finally libcrypto.X509_free(x509)
  }

  private def addExtension(
      x509: Ptr[libcrypto.X509],
      name: String,
      critical: Boolean,
      value: String,
  )(using Zone): Unit =
    val nid = libcrypto.OBJ_txt2nid(toCString(name))
    if nid <= 0 then throw new IllegalArgumentException(s"Unknown extension name: $name")
    val ext = requireNonNull(
      libcrypto.X509V3_EXT_conf_nid(null, null, nid, toCString(value)),
      s"X509V3_EXT_conf_nid($name)",
    )
    try
      if critical then
        checkPositive(
          libcrypto.X509_EXTENSION_set_critical(ext, 1),
          s"X509_EXTENSION_set_critical($name)",
        )
      checkPositive(libcrypto.X509_add_ext(x509, ext, -1), s"X509_add_ext($name)")
    finally libcrypto.X509_EXTENSION_free(ext)

  private def buildNameConstraints(
      permitted: JList[GeneralSubtree],
      excluded: JList[GeneralSubtree],
  ): String =
    val permittedValue =
      permitted.asScala.map(tree => s"permitted;${tree.generalName.toExtensionValue}")
    val excludedValue =
      excluded.asScala.map(tree => s"excluded;${tree.generalName.toExtensionValue}")
    (permittedValue ++ excludedValue).mkString(",")

  private def checkPositive(code: Int, operation: String): Unit =
    if code <= 0 then throw new IllegalStateException(s"OpenSSL call failed: $operation")

  private def decodePrivateKey(privateKey: PrivateKey)(using Zone): Ptr[libcrypto.EVP_PKEY] =
    // Java PrivateKey.getEncoded() returns PKCS#8 DER (SubjectPrivateKeyInfo).
    val encoded = privateKey.getEncoded()
    if encoded == null || encoded.isEmpty then
      throw new IllegalArgumentException("Private key encoding is unavailable")
    val nativeBytes = alloc[Byte](encoded.length)
    var index = 0
    while index < encoded.length do
      nativeBytes(index) = encoded(index)
      index += 1
    val bio = requireNonNull(
      libcrypto.BIO_new_mem_buf(nativeBytes, encoded.length),
      "BIO_new_mem_buf(privateKey)",
    )
    try
      val p8 = requireNonNull(
        libcrypto.d2i_PKCS8_PRIV_KEY_INFO_bio(bio, null),
        "d2i_PKCS8_PRIV_KEY_INFO_bio",
      )
      try requireNonNull(libcrypto.EVP_PKCS82PKEY(p8), "EVP_PKCS82PKEY")
      finally libcrypto.PKCS8_PRIV_KEY_INFO_free(p8)
    finally libcrypto.BIO_free(bio): Unit

  private def decodePublicKey(publicKey: PublicKey)(using Zone): Ptr[libcrypto.EVP_PKEY] =
    // Java PublicKey.getEncoded() returns SubjectPublicKeyInfo DER.
    val encoded = publicKey.getEncoded()
    if encoded == null || encoded.isEmpty then
      throw new IllegalArgumentException("Public key encoding is unavailable")
    val nativeBytes = alloc[Byte](encoded.length)
    var index = 0
    while index < encoded.length do
      nativeBytes(index) = encoded(index)
      index += 1
    val bio = requireNonNull(
      libcrypto.BIO_new_mem_buf(nativeBytes, encoded.length),
      "BIO_new_mem_buf(publicKey)",
    )
    try requireNonNull(libcrypto.d2i_PUBKEY_bio(bio, null), "d2i_PUBKEY_bio")
    finally libcrypto.BIO_free(bio): Unit

  private def decodeX509Name(principal: X500Principal)(using Zone): Ptr[libcrypto.X509_NAME] =
    // X500Principal.getEncoded() returns the DER-encoded X.500 distinguished name,
    // which is exactly what d2i_X509_NAME expects.
    // Note: d2i_X509_NAME(a, pp, length) needs to be added to the libcrypto Scala Native bindings.
    val encoded = principal.getEncoded()
    val nativeBytes = alloc[CUnsignedChar](encoded.length)
    var index = 0
    while index < encoded.length do
      nativeBytes(index) = encoded(index).toUByte
      index += 1
    val input = alloc[Ptr[CUnsignedChar]](1)
    !input = nativeBytes
    requireNonNull(libcrypto.d2i_X509_NAME(null, input, encoded.length), "d2i_X509_NAME")

  private def formatDate(date: Date): CString =
    toCString(x509DateFormat.format(date.toInstant()))

  private def keyUsageString(usage: Int): String =
    val flags = Seq(
      0x80 -> "digitalSignature",
      0x40 -> "nonRepudiation",
      0x20 -> "keyEncipherment",
      0x10 -> "dataEncipherment",
      0x08 -> "keyAgreement",
      0x04 -> "keyCertSign",
      0x02 -> "cRLSign",
      0x01 -> "encipherOnly",
      0x8000 -> "decipherOnly",
    )
    flags.collect { case (bit, name) if (usage & bit) != 0 => name }.mkString(",")

  private def requireNonNull[T <: AnyRef](value: T, operation: String): T =
    if value == null then throw new IllegalStateException(s"OpenSSL call returned null: $operation")
    else value

  private def setSerialNumber(
      x509: Ptr[libcrypto.X509],
      serialNumber: BigInteger,
  )(using Zone): Unit =
    val serialBytes =
      val raw = serialNumber.toByteArray()
      if raw.nonEmpty && raw(0) == 0 then raw.drop(1) else raw
    val nativeBytes = alloc[CUnsignedChar](serialBytes.length)
    var index = 0
    while index < serialBytes.length do
      nativeBytes(index) = serialBytes(index).toUByte
      index += 1
    val serialBn =
      requireNonNull(libcrypto.BN_bin2bn(nativeBytes, serialBytes.length, null), "BN_bin2bn")
    try
      val asn1Serial = requireNonNull(
        libcrypto.BN_to_ASN1_INTEGER(serialBn, libcrypto.X509_get_serialNumber(x509)),
        "BN_to_ASN1_INTEGER",
      )
      if asn1Serial != libcrypto.X509_get_serialNumber(x509) then
        throw new IllegalStateException("Unexpected ASN1 serial allocation path")
    finally libcrypto.BN_free(serialBn)

  private def setValidity(
      x509: Ptr[libcrypto.X509],
      start: Date,
      end: Date,
  )(using Zone): Unit =
    checkPositive(
      libcrypto.ASN1_TIME_set_string_X509(libcrypto.X509_getm_notBefore(x509), formatDate(start)),
      "ASN1_TIME_set_string_X509(notBefore)",
    )
    checkPositive(
      libcrypto.ASN1_TIME_set_string_X509(libcrypto.X509_getm_notAfter(x509), formatDate(end)),
      "ASN1_TIME_set_string_X509(notAfter)",
    )

  private def toJavaCertificate(x509: Ptr[libcrypto.X509])(using Zone): X509Certificate =
    // Encode the X509 certificate to DER via a memory BIO, then read into a Java byte array.
    val bio = requireNonNull(libcrypto.BIO_new(libcrypto.BIO_s_mem()), "BIO_new(BIO_s_mem)")
    try
      checkPositive(libcrypto.i2d_X509_bio(bio, x509), "i2d_X509_bio")
      // Allocate a generous buffer; DER certificates are typically well under 16 KB.
      val maxCertSize = 65536
      val buf = alloc[Byte](maxCertSize)
      val bytesRead = libcrypto.BIO_read(bio, buf, maxCertSize)
      if bytesRead <= 0 then
        throw new IllegalStateException("Failed to read DER certificate from BIO")
      val bytes = new Array[Byte](bytesRead)
      var index = 0
      while index < bytesRead do
        bytes(index) = buf(index)
        index += 1
      CertificateFactory
        .getInstance("X.509")
        .generateCertificate(new ByteArrayInputStream(bytes))
        .asInstanceOf[X509Certificate]
    finally libcrypto.BIO_free(bio): Unit
