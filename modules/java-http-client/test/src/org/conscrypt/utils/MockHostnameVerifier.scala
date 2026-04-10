// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/testing/src/main/java/org/conscrypt/javax/net/ssl/TestHostnameVerifier.java

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

import java.security.cert.CertificateParsingException
import java.security.cert.X509Certificate
import java.util.{ArrayList, Collection, Collections, List as JList}

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLException
import javax.net.ssl.SSLSession

import scala.jdk.CollectionConverters.*

/**
 * This class implements the simplest possible HostnameVerifier.
 */
class MockHostnameVerifier extends HostnameVerifier:

  override def verify(hostname: String, sslSession: SSLSession): Boolean =
    try
      verify(hostname, sslSession.getPeerCertificates()(0).asInstanceOf[X509Certificate])
    catch {
      case exc: SSLException => false
    }

  private def verify(hostname: String, cert: X509Certificate): Boolean = {
    var isVerified = false

    for (certHost <- getHostnames(cert).asScala)
      if (certHost == hostname)
        isVerified = true

    isVerified
  }

  private final val DNS_NAME_TYPE: Int = 2

  private def getHostnames(cert: X509Certificate): JList[String] = {
    val result: ArrayList[String] = new ArrayList[String]()
    try {
      val altNamePairs: Collection[JList[?]] = cert.getSubjectAlternativeNames()
      if (altNamePairs != null) {
        for (altNamePair <- altNamePairs.asScala)
          // altNames are returned as effectively Pair<Integer, String> instances,
          // where the first member is the type of altName and the second is the name.
          if (altNamePair.get(0) == DNS_NAME_TYPE) {
            result.add(altNamePair.get(1).asInstanceOf[String]): Unit
          }
      }
      return result
    } catch {
      case exc: CertificateParsingException => Collections.emptyList()
    }
  }
