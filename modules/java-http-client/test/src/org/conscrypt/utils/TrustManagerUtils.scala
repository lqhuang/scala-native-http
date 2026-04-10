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

import java.io.PrintStream
import java.net.Socket
import java.security.cert.{CertificateException, X509Certificate}
import javax.net.ssl.{SSLEngine, TrustManager, X509ExtendedTrustManager, X509TrustManager}

import utest.assert

/**
 * TrustManager is a simple proxy class that wraps an existing X509ExtendedTrustManager to provide
 * debug logging and recording of values.
 */
object TrustManagerUtils:
  private val LOG = false
  private val out: PrintStream = if (LOG) System.out else new NullPrintStream()
  private val EXTENDED_TRUST_MANAGER_CLASS: Class[?] = classOf[X509ExtendedTrustManager]

  def wrap(trustManagers: Array[TrustManager]): Array[TrustManager] = {
    val result = trustManagers.clone()
    for (i <- result.indices)
      result(i) = wrap(result(i))
    result
  }

  def wrap(trustManager: TrustManager): TrustManager =
    if (
      EXTENDED_TRUST_MANAGER_CLASS != null
      && EXTENDED_TRUST_MANAGER_CLASS.isInstance(trustManager)
    ) {
      new ExtendedTMWrapper(trustManager.asInstanceOf[X509ExtendedTrustManager])
    } else if (trustManager.isInstanceOf[X509TrustManager]) {
      new TMWrapper(trustManager.asInstanceOf[X509TrustManager])
    } else {
      trustManager
    }

  private def assertClientAuthType(authType: String): Unit =
    // "Unexpected client auth type " + authType
    assert(StandardNames.CLIENT_AUTH_TYPES.contains(authType))

  private def assertServerAuthType(authType: String): Unit =
    // "Unexpected server auth type " + authType
    assert(StandardNames.SERVER_AUTH_TYPES.contains(authType))

  private final class TMWrapper(trustManager: X509TrustManager) extends X509TrustManager {
    out.println("TrustManager.<init> trustManager=" + trustManager)

    def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {
      out.print(
        "TrustManager.checkClientTrusted " + "chain=" + chain.length + " " + "authType=" + authType + " ",
      )
      try {
        assertClientAuthType(authType)
        trustManager.checkClientTrusted(chain, authType)
        out.println("OK")
      } catch {
        case e: CertificateException =>
          e.printStackTrace(out)
          throw e
      }
    }

    def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {
      out.print(
        "TrustManager.checkServerTrusted " + "chain=" + chain.length + " " + "authType=" + authType + " ",
      )
      try {
        assertServerAuthType(authType)
        trustManager.checkServerTrusted(chain, authType)
        out.println("OK")
      } catch {
        case e: CertificateException =>
          e.printStackTrace(out)
          throw e
      }
    }

    /**
     * Returns the list of certificate issuer authorities which are trusted for authentication of
     * peers.
     *
     * @return
     *   the list of certificate issuer authorities which are trusted for authentication of peers.
     */
    def getAcceptedIssuers: Array[X509Certificate] = {
      val result = trustManager.getAcceptedIssuers()
      out.print("TrustManager.getAcceptedIssuers result=" + result.length)
      result
    }
  }

  private final class ExtendedTMWrapper(extendedTrustManager: X509ExtendedTrustManager)
      extends X509ExtendedTrustManager {
    out.println("TrustManager.<init> extendedTrustManager=" + extendedTrustManager)

    private val trustManager: X509TrustManager = extendedTrustManager

    def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {
      out.print(
        "TrustManager.checkClientTrusted " + "chain=" + chain.length + " " + "authType=" + authType + " ",
      )
      try {
        assertClientAuthType(authType)
        trustManager.checkClientTrusted(chain, authType)
        out.println("OK")
      } catch {
        case e: CertificateException =>
          e.printStackTrace(out)
          throw e
      }
    }

    def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {
      out.print(
        "TrustManager.checkServerTrusted " + "chain=" + chain.length + " " + "authType=" + authType + " ",
      )
      try {
        assertServerAuthType(authType)
        trustManager.checkServerTrusted(chain, authType)
        out.println("OK")
      } catch {
        case e: CertificateException =>
          e.printStackTrace(out)
          throw e
      }
    }

    def checkClientTrusted(
        chain: Array[X509Certificate],
        authType: String,
        socket: Socket,
    ): Unit = {
      if (extendedTrustManager == null) {
        out.print("(fallback to X509TrustManager) ")
        checkClientTrusted(chain, authType)
        return
      }
      out.print(
        "TrustManager.checkClientTrusted " + "chain=" + chain.length + " " + "authType=" + authType + " " + "socket=" + socket + " ",
      )
      try {
        assertClientAuthType(authType)
        extendedTrustManager.checkClientTrusted(chain, authType, socket)
        out.println("OK")
      } catch {
        case e: CertificateException =>
          e.printStackTrace(out)
          throw e
      }
    }

    def checkClientTrusted(
        chain: Array[X509Certificate],
        authType: String,
        engine: SSLEngine,
    ): Unit = {
      if (extendedTrustManager == null) {
        out.print("(fallback to X509TrustManager) ")
        checkClientTrusted(chain, authType)
        return
      }
      out.print(
        "TrustManager.checkClientTrusted " + "chain=" + chain.length + " " + "authType=" + authType + " " + "engine=" + engine + " ",
      )
      try {
        assertClientAuthType(authType)
        extendedTrustManager.checkClientTrusted(chain, authType, engine)
        out.println("OK")
      } catch {
        case e: CertificateException =>
          e.printStackTrace(out)
          throw e
      }
    }

    def checkServerTrusted(
        chain: Array[X509Certificate],
        authType: String,
        socket: Socket,
    ): Unit = {
      if (extendedTrustManager == null) {
        out.print("(fallback to X509TrustManager) ")
        checkServerTrusted(chain, authType)
        return
      }
      out.print(
        "TrustManager.checkServerTrusted " + "chain=" + chain.length + " " + "authType=" + authType + " " + "socket=" + socket.toString + " ",
      )
      try {
        assertServerAuthType(authType)
        extendedTrustManager.checkServerTrusted(chain, authType, socket)
        out.println("OK")
      } catch {
        case e: CertificateException =>
          e.printStackTrace(out)
          throw e
      }
    }

    def checkServerTrusted(
        chain: Array[X509Certificate],
        authType: String,
        engine: SSLEngine,
    ): Unit = {
      if (extendedTrustManager == null) {
        out.print("(fallback to X509TrustManager) ")
        checkServerTrusted(chain, authType)
        return
      }
      out.print(
        "TrustManager.checkServerTrusted " + "chain=" + chain.length + " " + "authType=" + authType + " " + "engine=" + engine.toString + " ",
      )
      try {
        assertServerAuthType(authType)
        extendedTrustManager.checkServerTrusted(chain, authType, engine)
        out.println("OK")
      } catch {
        case e: CertificateException =>
          e.printStackTrace(out)
          throw e
      }
    }

    /**
     * Returns the list of certificate issuer authorities which are trusted for authentication of
     * peers.
     *
     * @return
     *   the list of certificate issuer authorities which are trusted for authentication of peers.
     */
    def getAcceptedIssuers: Array[X509Certificate] = {
      val result = trustManager.getAcceptedIssuers
      out.print("TrustManager.getAcceptedIssuers result=" + result.length)
      result
    }
  }
