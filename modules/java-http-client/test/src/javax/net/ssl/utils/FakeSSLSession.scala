/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.conscrypt.javax.net.ssl

import java.security.cert.{Certificate, X509Certificate}
import java.security.Principal
import javax.net.ssl.{SSLSession, SSLSocket, SSLParameters, SSLSessionContext}
import javax.net.ssl.SSLPeerUnverifiedException

class FakeSSLSession(val hostname: String, val peerCerts: Array[X509Certificate])
    extends SSLSession {

  def this(hostname: String) = this(hostname, null)

  override def getApplicationBufferSize(): Int =
    throw new UnsupportedOperationException()
  override def getCipherSuite(): String =
    throw new UnsupportedOperationException()
  override def getCreationTime(): Long =
    throw new UnsupportedOperationException()
  override def getId(): Array[Byte] =
    throw new UnsupportedOperationException()
  override def getLastAccessedTime(): Long =
    throw new UnsupportedOperationException()
  override def getLocalCertificates(): Array[Certificate] =
    throw new UnsupportedOperationException()
  override def getLocalPrincipal(): Principal =
    throw new UnsupportedOperationException()
  override def getPacketBufferSize(): Int =
    throw new UnsupportedOperationException()
  override def getPeerCertificateChain(): Array[javax.security.cert.X509Certificate] =
    throw new UnsupportedOperationException()
  override def getPeerCertificates(): Array[Certificate] =
    if peerCerts == null
    then throw new SSLPeerUnverifiedException("Null peerCerts")
    else peerCerts.clone().asInstanceOf[Array[Certificate]]
  override def getPeerHost(): String =
    hostname
  override def getPeerPort(): Int =
    throw new UnsupportedOperationException()
  override def getPeerPrincipal(): Principal =
    throw new UnsupportedOperationException()
  override def getProtocol(): String =
    throw new UnsupportedOperationException()
  override def getSessionContext(): SSLSessionContext =
    throw new UnsupportedOperationException()
  override def getValue(name: String): AnyRef =
    throw new UnsupportedOperationException()
  override def getValueNames(): Array[String] =
    throw new UnsupportedOperationException()
  override def invalidate(): Unit =
    throw new UnsupportedOperationException()
  override def isValid(): Boolean =
    throw new UnsupportedOperationException()
  override def putValue(name: String, value: AnyRef): Unit =
    throw new UnsupportedOperationException()
  override def removeValue(name: String): Unit =
    throw new UnsupportedOperationException()
}

class FakeSSLSocket(session: SSLSession, parameters: SSLParameters) extends SSLSocket {
  override def getSSLParameters(): SSLParameters =
    parameters
  override def getSupportedCipherSuites(): Array[String] =
    throw new UnsupportedOperationException()
  override def getEnabledCipherSuites(): Array[String] =
    throw new UnsupportedOperationException()
  override def setEnabledCipherSuites(strings: Array[String]): Unit =
    throw new UnsupportedOperationException()
  override def getSupportedProtocols(): Array[String] =
    throw new UnsupportedOperationException()
  override def getEnabledProtocols(): Array[String] =
    throw new UnsupportedOperationException()
  override def setEnabledProtocols(strings: Array[String]): Unit =
    throw new UnsupportedOperationException()
  override def getSession(): SSLSession =
    session
  override def getHandshakeSession(): SSLSession =
    session
  // override def addHandshakeCompletedListener(l: HandshakeCompletedListener): Unit =
  //   throw new UnsupportedOperationException()
  // override def removeHandshakeCompletedListener(l: HandshakeCompletedListener): Unit =
  //   throw new UnsupportedOperationException()
  override def startHandshake(): Unit =
    throw new UnsupportedOperationException()
  override def setUseClientMode(b: Boolean): Unit =
    throw new UnsupportedOperationException()
  override def getUseClientMode(): Boolean =
    throw new UnsupportedOperationException()
  override def setNeedClientAuth(b: Boolean): Unit =
    throw new UnsupportedOperationException()
  override def getNeedClientAuth(): Boolean =
    throw new UnsupportedOperationException()
  override def setWantClientAuth(b: Boolean): Unit =
    throw new UnsupportedOperationException()
  override def getWantClientAuth(): Boolean =
    throw new UnsupportedOperationException()
  override def setEnableSessionCreation(b: Boolean): Unit =
    throw new UnsupportedOperationException()
  override def getEnableSessionCreation(): Boolean =
    throw new UnsupportedOperationException()
}
