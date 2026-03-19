// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/common/src/main/java/org/conscrypt/OkHostnameVerifier.java

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.conscrypt.javax.net.ssl

import java.security.cert.CertificateParsingException
import java.security.cert.X509Certificate
import java.util.{ArrayList, Collections, Locale}
import java.util.List as JList
import java.util.regex.Pattern
import javax.net.ssl.SSLException
import javax.net.ssl.SSLSession

import scala.collection.JavaConverters.*

/**
 * This interface is used to implement hostname verification in Conscrypt. Unlike with
 * {@link javax.net.ssl.HostnameVerifier}, the hostname verifier is called whenever hostname
 * verification is needed, without any use of default rules.
 */
trait ConscryptHostnameVerifier:

  /**
   * Returns whether the given hostname is allowable given the peer's authentication information
   * from the given session.
   */
  def verify(certs: Array[X509Certificate], hostname: String, session: SSLSession): Boolean

end ConscryptHostnameVerifier

/**
 * A HostnameVerifier consistent with [RFC 2818](http://www.ietf.org/rfc/rfc2818.txt)
 */
final class OkHostnameVerifier private (strictWildcardMode: Boolean)
    extends ConscryptHostnameVerifier:

  import OkHostnameVerifier.*

  def verify(certs: Array[X509Certificate], host: String, session: SSLSession): Boolean =
    if certs.length > 0
    then verify(host, certs(0))
    else {
      try {
        val certificates = session.getPeerCertificates
        verify(host, certificates(0).asInstanceOf[X509Certificate])
      } catch {
        case e: SSLException => false
      }
    }

  def verify(host: String, certificate: X509Certificate): Boolean =
    if verifyAsIpAddress(host)
    then verifyIpAddress(host, certificate)
    else verifyHostName(host, certificate)

  /**
   * Returns true if {@code certificate} matches {@code ipAddress}.
   */
  private def verifyIpAddress(ipAddress: String, certificate: X509Certificate): Boolean = {
    for (each <- getSubjectAltNames(certificate, ALT_IPA_NAME).asScala)
      if (ipAddress.equalsIgnoreCase(each)) {
        return true
      }

    false
  }

  /**
   * Returns true if {@code certificate} matches {@code hostName}.
   */
  private def verifyHostName(hostNameParam: String, certificate: X509Certificate): Boolean = {
    val hostName = hostNameParam.toLowerCase(Locale.US)

    for (each <- getSubjectAltNames(certificate, ALT_DNS_NAME).asScala)
      if (verifyHostName(hostName, each)) {
        return true
      }

    false
  }

  /**
   * Returns {@code true} iff {@code hostName} matches the domain name {@code pattern}.
   *
   * @param hostNameParam
   *   lower-case host name.
   * @param patternParam
   *   domain name pattern from certificate. May be a wildcard pattern such as
   *   {@code *.android.com}.
   */
  private def verifyHostName(hostNameParam: String, patternParam: String): Boolean = {
    var hostName = hostNameParam
    var pattern = patternParam

    // Basic sanity checks
    // Check length == 0 instead of .isEmpty() to support Java 5.
    if (
      hostName == null
      || hostName.length() == 0
      || hostName.startsWith(".")
      || hostName.endsWith("..")
    ) {
      // Invalid domain name
      return false
    }
    if (
      pattern == null
      || pattern.length() == 0
      || pattern.startsWith(".")
      || pattern.endsWith("..")
    ) {
      // Invalid pattern/domain name
      return false
    }

    // Normalize hostName and pattern by turning them into absolute domain names if they are not
    // yet absolute. This is needed because server certificates do not normally contain absolute
    // names or patterns, but they should be treated as absolute. At the same time, any hostName
    // presented to this method should also be treated as absolute for the purposes of matching
    // to the server certificate.
    //   www.android.com  matches www.android.com
    //   www.android.com  matches www.android.com.
    //   www.android.com. matches www.android.com.
    //   www.android.com. matches www.android.com
    if (!hostName.endsWith(".")) {
      hostName += '.'
    }
    if (!pattern.endsWith(".")) {
      pattern += '.'
    }
    // hostName and pattern are now absolute domain names.

    pattern = pattern.toLowerCase(Locale.US)
    // hostName and pattern are now in lower case -- domain names are case-insensitive.

    if (!pattern.contains("*")) {
      // Not a wildcard pattern -- hostName and pattern must match exactly.
      return hostName.equals(pattern)
    }
    // Wildcard pattern

    // WILDCARD PATTERN RULES:
    // 1. Asterisk (*) is only permitted in the left-most domain name label and must be the
    //    only character in that label (i.e., must match the whole left-most label).
    //    For example, *.example.com is permitted, while *a.example.com, a*.example.com,
    //    a*b.example.com, a.*.example.com are not permitted.
    // 2. Asterisk (*) cannot match across domain name labels.
    //    For example, *.example.com matches test.example.com but does not match
    //    sub.test.example.com.
    // 3. Wildcard patterns for single-label domain names are not permitted.
    // 4. Android-added: if strictWildcardMode is true then wildcards matching top-level
    // domains,
    //    e.g. *.com, are not permitted.

    if (!pattern.startsWith("*.") || pattern.indexOf('*', 1) != -1) {
      // Asterisk (*) is only permitted in the left-most domain name label and must be the
      // only character in that label
      return false
    }

    // Optimization: check whether hostName is too short to match the pattern. hostName must be
    // at least as long as the pattern because asterisk must match the whole left-most label and
    // hostName starts with a non-empty label. Thus, asterisk has to match one or more
    // characters.
    if (hostName.length() < pattern.length()) {
      // hostName too short to match the pattern.
      return false
    }

    if (pattern == "*.") {
      // Wildcard pattern for single-label domain name -- not permitted.
      return false
    }

    // BEGIN Android-added: Disallow top-level wildcards in strict mode. http://b/144694112
    if (strictWildcardMode) {
      // By this point we know the pattern has been normalised and starts with a wildcard,
      // i.e. "*.domainpart."
      val domainPart = pattern.substring(2, pattern.length() - 1)
      // If the domain part contains no dots then this pattern will match top level domains.
      if (domainPart.indexOf('.') < 0) {
        return false
      }
    }
    // END Android-added: Disallow top-level wildcards in strict mode. http://b/144694112

    // hostName must end with the region of pattern following the asterisk.
    val suffix = pattern.substring(1)
    if (!hostName.endsWith(suffix)) {
      // hostName does not end with the suffix
      return false
    }

    // Check that asterisk did not match across domain name labels.
    val suffixStartIndexInHostName = hostName.length() - suffix.length()
    if (
      (suffixStartIndexInHostName > 0)
      && (hostName.lastIndexOf('.', suffixStartIndexInHostName - 1) != -1)
    ) {
      // Asterisk is matching across domain name labels -- not permitted.
      return false
    }

    // hostName matches pattern
    true
  }

object OkHostnameVerifier:

  // Android-changed: strict mode disallows top-level domain wildcards. b/144694112
  final val INSTANCE = new OkHostnameVerifier(false)
  final val STRICT_INSTANCE = new OkHostnameVerifier(true)

  /**
   * Quick and dirty pattern to differentiate IP addresses from hostnames. This is an approximation
   * of Android's private InetAddress#isNumeric API.
   *
   * <p>This matches IPv6 addresses as a hex string containing at least one colon, and possibly
   * including dots after the first colon. It matches IPv4 addresses as strings containing only
   * decimal digits and dots. This pattern matches strings like "a:.23" and "54" that are neither IP
   * addresses nor hostnames; they will be verified as IP addresses (which is a more strict
   * verification).
   */
  private val VERIFY_AS_IP_ADDRESS =
    Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)")

  def verifyAsIpAddress(host: String): Boolean =
    VERIFY_AS_IP_ADDRESS.matcher(host).matches()

  private val ALT_DNS_NAME = 2
  private val ALT_IPA_NAME = 7

  // It is necessary to declare this method in the companion object to allow static access.
  def allSubjectAltNames(certificate: X509Certificate): JList[String] = {
    val altIpaNames = getSubjectAltNames(certificate, ALT_IPA_NAME)
    val altDnsNames = getSubjectAltNames(certificate, ALT_DNS_NAME)
    val result = new ArrayList[String](altIpaNames.size() + altDnsNames.size())
    result.addAll(altIpaNames)
    result.addAll(altDnsNames)
    result
  }

  @SuppressWarnings(Array("MixedMutabilityReturnType"))
  private def getSubjectAltNames(certificate: X509Certificate, typeId: Int): JList[String] = {
    val result = new ArrayList[String]()
    try {
      val subjectAltNames = certificate.getSubjectAlternativeNames()
      if (subjectAltNames == null) {
        return Collections.emptyList()
      }

      for (subjectAltName <- subjectAltNames.asScala) {
        val entry = subjectAltName.asInstanceOf[JList[_]]
        if (entry != null && entry.size() >= 2) {
          val altNameType = entry.get(0).asInstanceOf[Integer]
          if (altNameType != null) {
            if (altNameType == typeId) {
              val altName = entry.get(1).asInstanceOf[String]
              if (altName != null) {
                result.add(altName): Unit
              }
            }
          }
        }
      }

      result
    } catch {
      case e: CertificateParsingException => Collections.emptyList()
    }
  }

end OkHostnameVerifier
