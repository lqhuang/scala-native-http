// Ported from
// https://github.com/google/conscrypt/blob/097b6517252b50271bbe5ff1f5e0066863f797b7/common/src/test/java/org/conscrypt/HostnameVerifierTest.java

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License") you may not use this file except in compliance with the License.
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

package snhttp.javax.net.ssl

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.security.cert.{Certificate, CertificateFactory, X509Certificate}
import java.util.{Collection, Arrays}

import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSession
import javax.security.auth.x500.X500Principal

import org.conscrypt.utils.{FakeSSLSession => FFakeSSLSession, OkHostnameVerifier}

import utest.{Tests, test, assert, assertThrows, TestSuite}

/**
 * Tests for our hostname verifier. Most of these tests are from AOSP, which itself includes tests
 * from the Apache HTTP Client test suite.
 */

class HostnameVerifierTest extends TestSuite:

  private def makeCertificate(cert: String): X509Certificate =
    CertificateFactory
      .getInstance("X.509")
      .generateCertificate(new ByteArrayInputStream(cert.getBytes(UTF_8)))
      .asInstanceOf[X509Certificate]

  private def newSession(cert: String): SSLSession =
    new FakeSSLSession(Array(makeCertificate(cert)))

  final class FakeSSLSession(certificates: Array[Certificate]) extends FFakeSSLSession("FakeHost") {
    override def getPeerCertificates(): Array[Certificate] = {
      if (certificates.length == 0)
        throw new SSLPeerUnverifiedException("peer not authenticated")
      certificates
    }
  }

  // BEGIN Android-changed: Run tests for both default and strict verifiers.
  // http://b/144694112

  // Both verifiers should behave the same in all tests except for
  // subjectAltNameWithToplevelWildcard(), and that test is not parameterized for
  // clarity.
  val verifiers: Seq[OkHostnameVerifier] = Seq(
    OkHostnameVerifier.INSTANCE,
    OkHostnameVerifier.STRICT_INSTANCE,
  )
  // END Android-changed: Run tests for both default and strict verifiers.
  // http://b/144694112

  def tests: Tests = Tests:

    test("verify()") {
      val session = new FakeSSLSession(Array())
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers)
        assert(verifier.verify(certs, "localhost", session) == false)
    }

    test("verifyCn()") {
      // CN=foo.com
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIERjCCAy6gAwIBAgIJAIz+EYMBU6aQMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE1MzE0MVoXDTI4MTEwNTE1MzE0MVowgaQx\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIEwhNYXJ5bGFuZDEUMBIGA1UEBxMLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoTDmh0dHBjb21wb25lbnRzMRowGAYDVQQLExF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczEQMA4GA1UEAxMHZm9vLmNvbTElMCMGCSqGSIb3DQEJARYWanVs\n"
          + "aXVzZGF2aWVzQGdtYWlsLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n"
          + "ggEBAMhjr5aCPoyp0R1iroWAfnEyBMGYWoCidH96yGPFjYLowez5aYKY1IOKTY2B\n"
          + "lYho4O84X244QrZTRl8kQbYtxnGh4gSCD+Z8gjZ/gMvLUlhqOb+WXPAUHMB39GRy\n"
          + "zerA/ZtrlUqf+lKo0uWcocxeRc771KN8cPH3nHZ0rV0Hx4ZAZy6U4xxObe4rtSVY\n"
          + "07hNKXAb2odnVqgzcYiDkLV8ilvEmoNWMWrp8UBqkTcpEhYhCYp3cTkgJwMSuqv8\n"
          + "BqnGd87xQU3FVZI4tbtkB+KzjD9zz8QCDJAfDjZHR03KNQ5mxOgXwxwKw6lGMaiV\n"
          + "JTxpTKqym93whYk93l3ocEe55c0CAwEAAaN7MHkwCQYDVR0TBAIwADAsBglghkgB\n"
          + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE\n"
          + "FJ8Ud78/OrbKOIJCSBYs2tDLXofYMB8GA1UdIwQYMBaAFHua2o+QmU5S0qzbswNS\n"
          + "yoemDT4NMA0GCSqGSIb3DQEBBQUAA4IBAQC3jRmEya6sQCkmieULcvx8zz1euCk9\n"
          + "fSez7BEtki8+dmfMXe3K7sH0lI8f4jJR0rbSCjpmCQLYmzC3NxBKeJOW0RcjNBpO\n"
          + "c2JlGO9auXv2GDP4IYiXElLJ6VSqc8WvDikv0JmCCWm0Zga+bZbR/EWN5DeEtFdF\n"
          + "815CLpJZNcYwiYwGy/CVQ7w2TnXlG+mraZOz+owr+cL6J/ZesbdEWfjoS1+cUEhE\n"
          + "HwlNrAu8jlZ2UqSgskSWlhYdMTAP9CPHiUv9N7FcT58Itv/I4fKREINQYjDpvQcx\n"
          + "SaTYb9dr5sB4WLNglk7zxDtM80H518VvihTcP7FHL+Gn6g4j5fkI98+S\n"
          + "-----END CERTIFICATE-----\n",
      )
      // Android-changed: Ignore common name in hostname verification.
      // http://b/70278814
      // assert(verifier.verify("foo.com", session) == true)
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "foo.com", session) == false)
        assert(verifier.verify(certs, "a.foo.com", session) == false)
        assert(verifier.verify(certs, "bar.com", session) == false)
      }
    }

    test("verifyNonAsciiCn()") {
      // CN=&#x82b1;&#x5b50;.co.jp
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIESzCCAzOgAwIBAgIJAIz+EYMBU6aTMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE1NDIxNVoXDTI4MTEwNTE1NDIxNVowgakx\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIDAhNYXJ5bGFuZDEUMBIGA1UEBwwLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoMDmh0dHBjb21wb25lbnRzMRowGAYDVQQLDBF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczEVMBMGA1UEAwwM6Iqx5a2QLmNvLmpwMSUwIwYJKoZIhvcNAQkB\n"
          + "FhZqdWxpdXNkYXZpZXNAZ21haWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A\n"
          + "MIIBCgKCAQEAyGOvloI+jKnRHWKuhYB+cTIEwZhagKJ0f3rIY8WNgujB7PlpgpjU\n"
          + "g4pNjYGViGjg7zhfbjhCtlNGXyRBti3GcaHiBIIP5nyCNn+Ay8tSWGo5v5Zc8BQc\n"
          + "wHf0ZHLN6sD9m2uVSp/6UqjS5ZyhzF5FzvvUo3xw8fecdnStXQfHhkBnLpTjHE5t\n"
          + "7iu1JVjTuE0pcBvah2dWqDNxiIOQtXyKW8Sag1YxaunxQGqRNykSFiEJindxOSAn\n"
          + "AxK6q/wGqcZ3zvFBTcVVkji1u2QH4rOMP3PPxAIMkB8ONkdHTco1DmbE6BfDHArD\n"
          + "qUYxqJUlPGlMqrKb3fCFiT3eXehwR7nlzQIDAQABo3sweTAJBgNVHRMEAjAAMCwG\n"
          + "CWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNV\n"
          + "HQ4EFgQUnxR3vz86tso4gkJIFiza0Mteh9gwHwYDVR0jBBgwFoAUe5raj5CZTlLS\n"
          + "rNuzA1LKh6YNPg0wDQYJKoZIhvcNAQEFBQADggEBALJ27i3okV/KvlDp6KMID3gd\n"
          + "ITl68PyItzzx+SquF8gahMh016NX73z/oVZoVUNdftla8wPUB1GwIkAnGkhQ9LHK\n"
          + "spBdbRiCj0gMmLCsX8SrjFvr7cYb2cK6J/fJe92l1tg/7Y4o7V/s4JBe/cy9U9w8\n"
          + "a0ctuDmEBCgC784JMDtT67klRfr/2LlqWhlOEq7pUFxRLbhpquaAHSOjmIcWnVpw\n"
          + "9BsO7qe46hidgn39hKh1WjKK2VcL/3YRsC4wUi0PBtFW6ScMCuMhgIRXSPU55Rae\n"
          + "UIlOdPjjr1SUNWGId1rD7W16Scpwnknn310FNxFMHVI0GTGFkNdkilNCFJcIoRA=\n"
          + "-----END CERTIFICATE-----\n",
      )
      // Android-changed: Ignore common name in hostname verification.
      // http://b/70278814
      // assert(verifier.verify("\u82b1\u5b50.co.jp", session) == true)
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "\u82b1\u5b50.co.jp", session) == false)
        assert(verifier.verify(certs, "a.\u82b1\u5b50.co.jp", session) == false)
      }
    }

    test("verifySubjectAlt()") {
      // CN=foo.com, subjectAlt=bar.com
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIEXDCCA0SgAwIBAgIJAIz+EYMBU6aRMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE1MzYyOVoXDTI4MTEwNTE1MzYyOVowgaQx\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIEwhNYXJ5bGFuZDEUMBIGA1UEBxMLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoTDmh0dHBjb21wb25lbnRzMRowGAYDVQQLExF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczEQMA4GA1UEAxMHZm9vLmNvbTElMCMGCSqGSIb3DQEJARYWanVs\n"
          + "aXVzZGF2aWVzQGdtYWlsLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n"
          + "ggEBAMhjr5aCPoyp0R1iroWAfnEyBMGYWoCidH96yGPFjYLowez5aYKY1IOKTY2B\n"
          + "lYho4O84X244QrZTRl8kQbYtxnGh4gSCD+Z8gjZ/gMvLUlhqOb+WXPAUHMB39GRy\n"
          + "zerA/ZtrlUqf+lKo0uWcocxeRc771KN8cPH3nHZ0rV0Hx4ZAZy6U4xxObe4rtSVY\n"
          + "07hNKXAb2odnVqgzcYiDkLV8ilvEmoNWMWrp8UBqkTcpEhYhCYp3cTkgJwMSuqv8\n"
          + "BqnGd87xQU3FVZI4tbtkB+KzjD9zz8QCDJAfDjZHR03KNQ5mxOgXwxwKw6lGMaiV\n"
          + "JTxpTKqym93whYk93l3ocEe55c0CAwEAAaOBkDCBjTAJBgNVHRMEAjAAMCwGCWCG\n"
          + "SAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4E\n"
          + "FgQUnxR3vz86tso4gkJIFiza0Mteh9gwHwYDVR0jBBgwFoAUe5raj5CZTlLSrNuz\n"
          + "A1LKh6YNPg0wEgYDVR0RBAswCYIHYmFyLmNvbTANBgkqhkiG9w0BAQUFAAOCAQEA\n"
          + "dQyprNZBmVnvuVWjV42sey/PTfkYShJwy1j0/jcFZR/ypZUovpiHGDO1DgL3Y3IP\n"
          + "zVQ26uhUsSw6G0gGRiaBDe/0LUclXZoJzXX1qpS55OadxW73brziS0sxRgGrZE/d\n"
          + "3g5kkio6IED47OP6wYnlmZ7EKP9cqjWwlnvHnnUcZ2SscoLNYs9rN9ccp8tuq2by\n"
          + "88OyhKwGjJfhOudqfTNZcDzRHx4Fzm7UsVaycVw4uDmhEHJrAsmMPpj/+XRK9/42\n"
          + "2xq+8bc6HojdtbCyug/fvBZvZqQXSmU8m8IVcMmWMz0ZQO8ee3QkBHMZfCy7P/kr\n"
          + "VbWx/uETImUu+NZg22ewEw==\n"
          + "-----END CERTIFICATE-----\n",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "foo.com", session) == false)
        assert(verifier.verify(certs, "a.foo.com", session) == false)
        assert(verifier.verify(certs, "bar.com", session) == true)
        assert(verifier.verify(certs, "a.bar.com", session) == false)
      }
    }

    /**
     * Ignored due to incompatibilities between Android and Java on how non-ASCII subject alt names
     * are parsed. Android fails to parse these, which means we fall back to the CN. The RI does
     * parse them, so the CN is unused.
     */
    test("verifyNonAsciiSubjectAlt()") {
      // CN=foo.com, subjectAlt=bar.com, subjectAlt=&#x82b1;&#x5b50;.co.jp
      // (hanako.co.jp in kanji)
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIEajCCA1KgAwIBAgIJAIz+EYMBU6aSMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE1MzgxM1oXDTI4MTEwNTE1MzgxM1owgaQx\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIEwhNYXJ5bGFuZDEUMBIGA1UEBxMLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoTDmh0dHBjb21wb25lbnRzMRowGAYDVQQLExF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczEQMA4GA1UEAxMHZm9vLmNvbTElMCMGCSqGSIb3DQEJARYWanVs\n"
          + "aXVzZGF2aWVzQGdtYWlsLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n"
          + "ggEBAMhjr5aCPoyp0R1iroWAfnEyBMGYWoCidH96yGPFjYLowez5aYKY1IOKTY2B\n"
          + "lYho4O84X244QrZTRl8kQbYtxnGh4gSCD+Z8gjZ/gMvLUlhqOb+WXPAUHMB39GRy\n"
          + "zerA/ZtrlUqf+lKo0uWcocxeRc771KN8cPH3nHZ0rV0Hx4ZAZy6U4xxObe4rtSVY\n"
          + "07hNKXAb2odnVqgzcYiDkLV8ilvEmoNWMWrp8UBqkTcpEhYhCYp3cTkgJwMSuqv8\n"
          + "BqnGd87xQU3FVZI4tbtkB+KzjD9zz8QCDJAfDjZHR03KNQ5mxOgXwxwKw6lGMaiV\n"
          + "JTxpTKqym93whYk93l3ocEe55c0CAwEAAaOBnjCBmzAJBgNVHRMEAjAAMCwGCWCG\n"
          + "SAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4E\n"
          + "FgQUnxR3vz86tso4gkJIFiza0Mteh9gwHwYDVR0jBBgwFoAUe5raj5CZTlLSrNuz\n"
          + "A1LKh6YNPg0wIAYDVR0RBBkwF4IHYmFyLmNvbYIM6Iqx5a2QLmNvLmpwMA0GCSqG\n"
          + "SIb3DQEBBQUAA4IBAQBeZs7ZIYyKtdnVxVvdLgwySEPOE4pBSXii7XYv0Q9QUvG/\n"
          + "++gFGQh89HhABzA1mVUjH5dJTQqSLFvRfqTHqLpxSxSWqMHnvRM4cPBkIRp/XlMK\n"
          + "PlXadYtJLPTgpbgvulA1ickC9EwlNYWnowZ4uxnfsMghW4HskBqaV+PnQ8Zvy3L0\n"
          + "12c7Cg4mKKS5pb1HdRuiD2opZ+Hc77gRQLvtWNS8jQvd/iTbh6fuvTKfAOFoXw22\n"
          + "sWIKHYrmhCIRshUNohGXv50m2o+1w9oWmQ6Dkq7lCjfXfUB4wIbggJjpyEtbNqBt\n"
          + "j4MC2x5rfsLKKqToKmNE7pFEgqwe8//Aar1b+Qj+\n"
          + "-----END CERTIFICATE-----\n",
      )

      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "foo.com", session) == false) // Android will be true here
        assert(verifier.verify(certs, "a.foo.com", session) == false)
        // these checks test alternative subjects. The test data contains an
        // alternative subject starting with a japanese kanji character. This is
        // not supported by Android because the underlying implementation from
        // harmony follows the definition from rfc 1034 page 10 for alternative
        // subject names. This causes the code to drop all alternative subjects.
        // assert(verifier.verify("bar.com", session) == true)
        // assert(verifier.verify("a.bar.com", session) == false)
        // assert(verifier.verify("a.\u82b1\u5b50.co.jp", session) == false)
      }
    }

    test("verifySubjectAltOnly()") {
      // subjectAlt=foo.com
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIESjCCAzKgAwIBAgIJAIz+EYMBU6aYMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE2MjYxMFoXDTI4MTEwNTE2MjYxMFowgZIx\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIDAhNYXJ5bGFuZDEUMBIGA1UEBwwLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoMDmh0dHBjb21wb25lbnRzMRowGAYDVQQLDBF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczElMCMGCSqGSIb3DQEJARYWanVsaXVzZGF2aWVzQGdtYWlsLmNv\n"
          + "bTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMhjr5aCPoyp0R1iroWA\n"
          + "fnEyBMGYWoCidH96yGPFjYLowez5aYKY1IOKTY2BlYho4O84X244QrZTRl8kQbYt\n"
          + "xnGh4gSCD+Z8gjZ/gMvLUlhqOb+WXPAUHMB39GRyzerA/ZtrlUqf+lKo0uWcocxe\n"
          + "Rc771KN8cPH3nHZ0rV0Hx4ZAZy6U4xxObe4rtSVY07hNKXAb2odnVqgzcYiDkLV8\n"
          + "ilvEmoNWMWrp8UBqkTcpEhYhCYp3cTkgJwMSuqv8BqnGd87xQU3FVZI4tbtkB+Kz\n"
          + "jD9zz8QCDJAfDjZHR03KNQ5mxOgXwxwKw6lGMaiVJTxpTKqym93whYk93l3ocEe5\n"
          + "5c0CAwEAAaOBkDCBjTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NM\n"
          + "IEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUnxR3vz86tso4gkJIFiza\n"
          + "0Mteh9gwHwYDVR0jBBgwFoAUe5raj5CZTlLSrNuzA1LKh6YNPg0wEgYDVR0RBAsw\n"
          + "CYIHZm9vLmNvbTANBgkqhkiG9w0BAQUFAAOCAQEAjl78oMjzFdsMy6F1sGg/IkO8\n"
          + "tF5yUgPgFYrs41yzAca7IQu6G9qtFDJz/7ehh/9HoG+oqCCIHPuIOmS7Sd0wnkyJ\n"
          + "Y7Y04jVXIb3a6f6AgBkEFP1nOT0z6kjT7vkA5LJ2y3MiDcXuRNMSta5PYVnrX8aZ\n"
          + "yiqVUNi40peuZ2R8mAUSBvWgD7z2qWhF8YgDb7wWaFjg53I36vWKn90ZEti3wNCw\n"
          + "qAVqixM+J0qJmQStgAc53i2aTMvAQu3A3snvH/PHTBo+5UL72n9S1kZyNCsVf1Qo\n"
          + "n8jKTiRriEM+fMFlcgQP284EBFzYHyCXFb9O/hMjK2+6mY9euMB1U1aFFzM/Bg==\n"
          + "-----END CERTIFICATE-----\n",
      )

      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "foo.com", session) == true)
        assert(verifier.verify(certs, "a.foo.com", session) == false)
        assert(verifier.verify(certs, "foo.com", session) == true)
        assert(verifier.verify(certs, "a.foo.com", session) == false)
      }
    }

    test("verifyMultipleCn()") {
      // CN=foo.com, CN=bar.com, CN=&#x82b1;&#x5b50;.co.jp
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIEbzCCA1egAwIBAgIJAIz+EYMBU6aXMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE2MTk0NVoXDTI4MTEwNTE2MTk0NVowgc0x\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIDAhNYXJ5bGFuZDEUMBIGA1UEBwwLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoMDmh0dHBjb21wb25lbnRzMRowGAYDVQQLDBF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczEQMA4GA1UEAwwHZm9vLmNvbTEQMA4GA1UEAwwHYmFyLmNvbTEV\n"
          + "MBMGA1UEAwwM6Iqx5a2QLmNvLmpwMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyGOv\n"
          + "loI+jKnRHWKuhYB+cTIEwZhagKJ0f3rIY8WNgujB7PlpgpjUg4pNjYGViGjg7zhf\n"
          + "bjhCtlNGXyRBti3GcaHiBIIP5nyCNn+Ay8tSWGo5v5Zc8BQcwHf0ZHLN6sD9m2uV\n"
          + "Sp/6UqjS5ZyhzF5FzvvUo3xw8fecdnStXQfHhkBnLpTjHE5t7iu1JVjTuE0pcBva\n"
          + "h2dWqDNxiIOQtXyKW8Sag1YxaunxQGqRNykSFiEJindxOSAnAxK6q/wGqcZ3zvFB\n"
          + "TcVVkji1u2QH4rOMP3PPxAIMkB8ONkdHTco1DmbE6BfDHArDqUYxqJUlPGlMqrKb\n"
          + "3fCFiT3eXehwR7nlzQIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQf\n"
          + "Fh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUnxR3vz86\n"
          + "tso4gkJIFiza0Mteh9gwHwYDVR0jBBgwFoAUe5raj5CZTlLSrNuzA1LKh6YNPg0w\n"
          + "DQYJKoZIhvcNAQEFBQADggEBAGuZb8ai1NO2j4v3y9TLZvd5s0vh5/TE7n7RX+8U\n"
          + "y37OL5k7x9nt0mM1TyAKxlCcY+9h6frue8MemZIILSIvMrtzccqNz0V1WKgA+Orf\n"
          + "uUrabmn+CxHF5gpy6g1Qs2IjVYWA5f7FROn/J+Ad8gJYc1azOWCLQqSyfpNRLSvY\n"
          + "EriQFEV63XvkJ8JrG62b+2OT2lqT4OO07gSPetppdlSa8NBSKP6Aro9RIX1ZjUZQ\n"
          + "SpQFCfo02NO0uNRDPUdJx2huycdNb+AXHaO7eXevDLJ+QnqImIzxWiY6zLOdzjjI\n"
          + "VBMkLHmnP7SjGSQ3XA4ByrQOxfOUTyLyE7NuemhHppuQPxE=\n"
          + "-----END CERTIFICATE-----\n",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "foo.com", session) == false)
        assert(verifier.verify(certs, "a.foo.com", session) == false)
        assert(verifier.verify(certs, "bar.com", session) == false)
        assert(verifier.verify(certs, "a.bar.com", session) == false)
        // Android-changed: Ignore common name in hostname verification.
        // http://b/70278814
        // assert(verifier.verify("\u82b1\u5b50.co.jp", session) == true)
        assert(verifier.verify(certs, "\u82b1\u5b50.co.jp", session) == false)
        assert(verifier.verify(certs, "a.\u82b1\u5b50.co.jp", session) == false)
      }
    }

    test("verifyWilcardCn()") {
      // CN=*.foo.com
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIESDCCAzCgAwIBAgIJAIz+EYMBU6aUMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE2MTU1NVoXDTI4MTEwNTE2MTU1NVowgaYx\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIEwhNYXJ5bGFuZDEUMBIGA1UEBxMLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoTDmh0dHBjb21wb25lbnRzMRowGAYDVQQLExF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczESMBAGA1UEAxQJKi5mb28uY29tMSUwIwYJKoZIhvcNAQkBFhZq\n"
          + "dWxpdXNkYXZpZXNAZ21haWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n"
          + "CgKCAQEAyGOvloI+jKnRHWKuhYB+cTIEwZhagKJ0f3rIY8WNgujB7PlpgpjUg4pN\n"
          + "jYGViGjg7zhfbjhCtlNGXyRBti3GcaHiBIIP5nyCNn+Ay8tSWGo5v5Zc8BQcwHf0\n"
          + "ZHLN6sD9m2uVSp/6UqjS5ZyhzF5FzvvUo3xw8fecdnStXQfHhkBnLpTjHE5t7iu1\n"
          + "JVjTuE0pcBvah2dWqDNxiIOQtXyKW8Sag1YxaunxQGqRNykSFiEJindxOSAnAxK6\n"
          + "q/wGqcZ3zvFBTcVVkji1u2QH4rOMP3PPxAIMkB8ONkdHTco1DmbE6BfDHArDqUYx\n"
          + "qJUlPGlMqrKb3fCFiT3eXehwR7nlzQIDAQABo3sweTAJBgNVHRMEAjAAMCwGCWCG\n"
          + "SAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4E\n"
          + "FgQUnxR3vz86tso4gkJIFiza0Mteh9gwHwYDVR0jBBgwFoAUe5raj5CZTlLSrNuz\n"
          + "A1LKh6YNPg0wDQYJKoZIhvcNAQEFBQADggEBAH0ipG6J561UKUfgkeW7GvYwW98B\n"
          + "N1ZooWX+JEEZK7+Pf/96d3Ij0rw9ACfN4bpfnCq0VUNZVSYB+GthQ2zYuz7tf/UY\n"
          + "A6nxVgR/IjG69BmsBl92uFO7JTNtHztuiPqBn59pt+vNx4yPvno7zmxsfI7jv0ww\n"
          + "yfs+0FNm7FwdsC1k47GBSOaGw38kuIVWqXSAbL4EX9GkryGGOKGNh0qvAENCdRSB\n"
          + "G9Z6tyMbmfRY+dLSh3a9JwoEcBUso6EWYBakLbq4nG/nvYdYvG9ehrnLVwZFL82e\n"
          + "l3Q/RK95bnA6cuRClGusLad0e6bjkBzx/VQ3VarDEpAkTLUGVAa0CLXtnyc=\n"
          + "-----END CERTIFICATE-----\n",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "foo.com", session) == false)
        // Android-changed: Ignore common name in hostname verification.
        // http://b/70278814
        // assert(verifier.verify("www.foo.com", session) == true)
        assert(verifier.verify(certs, "www.foo.com", session) == false)
        // Android-changed: Ignore common name in hostname verification.
        // http://b/70278814
        // assert(verifier.verify("\u82b1\u5b50.foo.com", session) == true)
        assert(verifier.verify(certs, "\u82b1\u5b50.foo.com", session) == false)
        assert(verifier.verify(certs, "a.b.foo.com", session) == false)
      }
    }

    test("verifyWilcardCnOnTld()") {
      // It's the CA's responsibility to not issue broad-matching certificates!
      // CN=*.co.jp
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIERjCCAy6gAwIBAgIJAIz+EYMBU6aVMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE2MTYzMFoXDTI4MTEwNTE2MTYzMFowgaQx\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIEwhNYXJ5bGFuZDEUMBIGA1UEBxMLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoTDmh0dHBjb21wb25lbnRzMRowGAYDVQQLExF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczEQMA4GA1UEAxQHKi5jby5qcDElMCMGCSqGSIb3DQEJARYWanVs\n"
          + "aXVzZGF2aWVzQGdtYWlsLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n"
          + "ggEBAMhjr5aCPoyp0R1iroWAfnEyBMGYWoCidH96yGPFjYLowez5aYKY1IOKTY2B\n"
          + "lYho4O84X244QrZTRl8kQbYtxnGh4gSCD+Z8gjZ/gMvLUlhqOb+WXPAUHMB39GRy\n"
          + "zerA/ZtrlUqf+lKo0uWcocxeRc771KN8cPH3nHZ0rV0Hx4ZAZy6U4xxObe4rtSVY\n"
          + "07hNKXAb2odnVqgzcYiDkLV8ilvEmoNWMWrp8UBqkTcpEhYhCYp3cTkgJwMSuqv8\n"
          + "BqnGd87xQU3FVZI4tbtkB+KzjD9zz8QCDJAfDjZHR03KNQ5mxOgXwxwKw6lGMaiV\n"
          + "JTxpTKqym93whYk93l3ocEe55c0CAwEAAaN7MHkwCQYDVR0TBAIwADAsBglghkgB\n"
          + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE\n"
          + "FJ8Ud78/OrbKOIJCSBYs2tDLXofYMB8GA1UdIwQYMBaAFHua2o+QmU5S0qzbswNS\n"
          + "yoemDT4NMA0GCSqGSIb3DQEBBQUAA4IBAQA0sWglVlMx2zNGvUqFC73XtREwii53\n"
          + "CfMM6mtf2+f3k/d8KXhLNySrg8RRlN11zgmpPaLtbdTLrmG4UdAHHYr8O4y2BBmE\n"
          + "1cxNfGxxechgF8HX10QV4dkyzp6Z1cfwvCeMrT5G/V1pejago0ayXx+GPLbWlNeZ\n"
          + "S+Kl0m3p+QplXujtwG5fYcIpaGpiYraBLx3Tadih39QN65CnAh/zRDhLCUzKyt9l\n"
          + "UGPLEUDzRHMPHLnSqT1n5UU5UDRytbjJPXzF+l/+WZIsanefWLsxnkgAuZe/oMMF\n"
          + "EJMryEzOjg4Tfuc5qM0EXoPcQ/JlheaxZ40p2IyHqbsWV4MRYuFH4bkM\n"
          + "-----END CERTIFICATE-----\n",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        // Android-changed: Ignore common name in hostname verification.
        // http://b/70278814
        // assert(verifier.verify("foo.co.jp", session) == true)
        assert(verifier.verify(certs, "foo.co.jp", session) == false)
        // Android-changed: Ignore common name in hostname verification.
        // http://b/70278814
        // assert(verifier.verify("\u82b1\u5b50.co.jp", session) == true)
        assert(verifier.verify(certs, "\u82b1\u5b50.co.jp", session) == false)
      }
    }

    /**
     * Ignored due to incompatibilities between Android and Java on how non-ASCII subject alt names
     * are parsed. Android fails to parse these, which means we fall back to the CN. The RI does
     * parse them, so the CN is unused.
     */
    test("testWilcardNonAsciiSubjectAlt() ") {
      // CN=*.foo.com, subjectAlt=*.bar.com, subjectAlt=*.&#x82b1;&#x5b50;.co.jp
      // (*.hanako.co.jp in kanji)
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIEcDCCA1igAwIBAgIJAIz+EYMBU6aWMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYD\n"
          + "VQQGEwJDQTELMAkGA1UECBMCQkMxEjAQBgNVBAcTCVZhbmNvdXZlcjEWMBQGA1UE\n"
          + "ChMNd3d3LmN1Y2JjLmNvbTEUMBIGA1UECxQLY29tbW9uc19zc2wxHTAbBgNVBAMU\n"
          + "FGRlbW9faW50ZXJtZWRpYXRlX2NhMSUwIwYJKoZIhvcNAQkBFhZqdWxpdXNkYXZp\n"
          + "ZXNAZ21haWwuY29tMB4XDTA2MTIxMTE2MTczMVoXDTI4MTEwNTE2MTczMVowgaYx\n"
          + "CzAJBgNVBAYTAlVTMREwDwYDVQQIEwhNYXJ5bGFuZDEUMBIGA1UEBxMLRm9yZXN0\n"
          + "IEhpbGwxFzAVBgNVBAoTDmh0dHBjb21wb25lbnRzMRowGAYDVQQLExF0ZXN0IGNl\n"
          + "cnRpZmljYXRlczESMBAGA1UEAxQJKi5mb28uY29tMSUwIwYJKoZIhvcNAQkBFhZq\n"
          + "dWxpdXNkYXZpZXNAZ21haWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n"
          + "CgKCAQEAyGOvloI+jKnRHWKuhYB+cTIEwZhagKJ0f3rIY8WNgujB7PlpgpjUg4pN\n"
          + "jYGViGjg7zhfbjhCtlNGXyRBti3GcaHiBIIP5nyCNn+Ay8tSWGo5v5Zc8BQcwHf0\n"
          + "ZHLN6sD9m2uVSp/6UqjS5ZyhzF5FzvvUo3xw8fecdnStXQfHhkBnLpTjHE5t7iu1\n"
          + "JVjTuE0pcBvah2dWqDNxiIOQtXyKW8Sag1YxaunxQGqRNykSFiEJindxOSAnAxK6\n"
          + "q/wGqcZ3zvFBTcVVkji1u2QH4rOMP3PPxAIMkB8ONkdHTco1DmbE6BfDHArDqUYx\n"
          + "qJUlPGlMqrKb3fCFiT3eXehwR7nlzQIDAQABo4GiMIGfMAkGA1UdEwQCMAAwLAYJ\n"
          + "YIZIAYb4QgENBB8WHU9wZW5TU0wgR2VuZXJhdGVkIENlcnRpZmljYXRlMB0GA1Ud\n"
          + "DgQWBBSfFHe/Pzq2yjiCQkgWLNrQy16H2DAfBgNVHSMEGDAWgBR7mtqPkJlOUtKs\n"
          + "27MDUsqHpg0+DTAkBgNVHREEHTAbggkqLmJhci5jb22CDiou6Iqx5a2QLmNvLmpw\n"
          + "MA0GCSqGSIb3DQEBBQUAA4IBAQBobWC+D5/lx6YhX64CwZ26XLjxaE0S415ajbBq\n"
          + "DK7lz+Rg7zOE3GsTAMi+ldUYnhyz0wDiXB8UwKXl0SDToB2Z4GOgqQjAqoMmrP0u\n"
          + "WB6Y6dpkfd1qDRUzI120zPYgSdsXjHW9q2H77iV238hqIU7qCvEz+lfqqWEY504z\n"
          + "hYNlknbUnR525ItosEVwXFBJTkZ3Yw8gg02c19yi8TAh5Li3Ad8XQmmSJMWBV4XK\n"
          + "qFr0AIZKBlg6NZZFf/0dP9zcKhzSriW27bY0XfzA6GSiRDXrDjgXq6baRT6YwgIg\n"
          + "pgJsDbJtZfHnV1nd3M6zOtQPm1TIQpNmMMMd/DPrGcUQerD3\n"
          + "-----END CERTIFICATE-----\n",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        // try the foo.com variations
        assert(verifier.verify(certs, "foo.com", session) == false) // Android will be true here
        assert(verifier.verify(certs, "www.foo.com", session) == false) // Android will be true here
        assert(
          verifier.verify(certs, "\u82b1\u5b50.foo.com", session) == false,
        ) // Android will be true here
        assert(verifier.verify(certs, "a.b.foo.com", session) == false)
        // these checks test alternative subjects. The test data contains an
        // alternative subject starting with a japanese kanji character. This is
        // not supported by Android because the underlying implementation from
        // harmony follows the definition from rfc 1034 page 10 for alternative
        // subject names. This causes the code to drop all alternative subjects.
        // assert(verifier.verify("bar.com", session) == false)
        // assert(verifier.verify("www.bar.com", session) == true)
        // assert(verifier.verify("\u82b1\u5b50.bar.com", session) == true)
        // assert(verifier.verify("a.b.bar.com", session) == true)
      }
    }

    test("subjectAltUsesLocalDomainAndIp()") {
      // cat cert.cnf
      // [req]
      // distinguished_name=distinguished_name
      // req_extensions=req_extensions
      // x509_extensions=x509_extensions
      // [distinguished_name]
      // [req_extensions]
      // [x509_extensions]
      // subjectAltName=DNS:localhost.localdomain,DNS:localhost,IP:127.0.0.1
      //
      // $ openssl req -x509 -nodes -days 36500 -subj '/CN=localhost' -config ./cert.cnf \
      //     -newkey rsa:512 -out cert.pem
      val certificate: X509Certificate = makeCertificate(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIBWDCCAQKgAwIBAgIJANS1EtICX2AZMA0GCSqGSIb3DQEBBQUAMBQxEjAQBgNV\n"
          + "BAMTCWxvY2FsaG9zdDAgFw0xMjAxMDIxOTA4NThaGA8yMTExMTIwOTE5MDg1OFow\n"
          + "FDESMBAGA1UEAxMJbG9jYWxob3N0MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAPpt\n"
          + "atK8r4/hf4hSIs0os/BSlQLbRBaK9AfBReM4QdAklcQqe6CHsStKfI8pp0zs7Ptg\n"
          + "PmMdpbttL0O7mUboBC8CAwEAAaM1MDMwMQYDVR0RBCowKIIVbG9jYWxob3N0Lmxv\n"
          + "Y2FsZG9tYWlugglsb2NhbGhvc3SHBH8AAAEwDQYJKoZIhvcNAQEFBQADQQD0ntfL\n"
          + "DCzOCv9Ma6Lv5o5jcYWVxvBSTsnt22hsJpWD1K7iY9lbkLwl0ivn73pG2evsAn9G\n"
          + "X8YKH52fnHsCrhSD\n"
          + "-----END CERTIFICATE-----",
      )
      assert(new X500Principal("CN=localhost") == certificate.getSubjectX500Principal())

      val session: FakeSSLSession = new FakeSSLSession(Array(certificate))
      val certs: Array[X509Certificate] = Array()

      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "localhost", session) == true)
        assert(verifier.verify(certs, "localhost.localdomain", session) == true)
        assert(verifier.verify(certs, "local.host", session) == false)

        assert(verifier.verify(certs, "127.0.0.1", session) == true)
        assert(verifier.verify(certs, "127.0.0.2", session) == false)
      }
    }

    test("wildcardsCannotMatchIpAddresses()") {
      // openssl req -x509 -nodes -days 36500 -subj '/CN=*.0.0.1' -newkey rsa:512 -out cert.pem
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIBkjCCATygAwIBAgIJAMdemqOwd/BEMA0GCSqGSIb3DQEBBQUAMBIxEDAOBgNV\n"
          + "BAMUByouMC4wLjEwIBcNMTAxMjIwMTY0NDI1WhgPMjExMDExMjYxNjQ0MjVaMBIx\n"
          + "EDAOBgNVBAMUByouMC4wLjEwXDANBgkqhkiG9w0BAQEFAANLADBIAkEAqY8c9Qrt\n"
          + "YPWCvb7lclI+aDHM6fgbJcHsS9Zg8nUOh5dWrS7AgeA25wyaokFl4plBbbHQe2j+\n"
          + "cCjsRiJIcQo9HwIDAQABo3MwcTAdBgNVHQ4EFgQUJ436TZPJvwCBKklZZqIvt1Yt\n"
          + "JjEwQgYDVR0jBDswOYAUJ436TZPJvwCBKklZZqIvt1YtJjGhFqQUMBIxEDAOBgNV\n"
          + "BAMUByouMC4wLjGCCQDHXpqjsHfwRDAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEB\n"
          + "BQUAA0EAk9i88xdjWoewqvE+iMC9tD2obMchgFDaHH0ogxxiRaIKeEly3g0uGxIt\n"
          + "fl2WRY8hb4x+zRrwsFaLEpdEvqcjOQ==\n"
          + "-----END CERTIFICATE-----",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers)
        assert(verifier.verify(certs, "127.0.0.1", session) == false)
    }

    /**
     * Earlier implementations of Android's hostname verifier required that wildcard names wouldn't
     * match "*.com" or similar. This was a nonstandard check that we've since dropped. It is the
     * CA's responsibility to not hand out certificates that match so broadly.
     */
    test("wildcardsDoesNotNeedTwoDots()") {
      // openssl req -x509 -nodes -days 36500 -subj '/CN=*.com' -newkey rsa:512 -out cert.pem
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIBjDCCATagAwIBAgIJAOVulXCSu6HuMA0GCSqGSIb3DQEBBQUAMBAxDjAMBgNV\n"
          + "BAMUBSouY29tMCAXDTEwMTIyMDE2NDkzOFoYDzIxMTAxMTI2MTY0OTM4WjAQMQ4w\n"
          + "DAYDVQQDFAUqLmNvbTBcMA0GCSqGSIb3DQEBAQUAA0sAMEgCQQDJd8xqni+h7Iaz\n"
          + "ypItivs9kPuiJUqVz+SuJ1C05SFc3PmlRCvwSIfhyD67fHcbMdl+A/LrIjhhKZJe\n"
          + "1joO0+pFAgMBAAGjcTBvMB0GA1UdDgQWBBS4Iuzf5w8JdCp+EtBfdFNudf6+YzBA\n"
          + "BgNVHSMEOTA3gBS4Iuzf5w8JdCp+EtBfdFNudf6+Y6EUpBIwEDEOMAwGA1UEAxQF\n"
          + "Ki5jb22CCQDlbpVwkruh7jAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBQUAA0EA\n"
          + "U6LFxmZr31lFyis2/T68PpjAppc0DpNQuA2m/Y7oTHBDi55Fw6HVHCw3lucuWZ5d\n"
          + "qUYo4ES548JdpQtcLrW2sA==\n"
          + "-----END CERTIFICATE-----",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers)
        // Android-changed: Ignore common name in hostname verification.
        // http://b/70278814
        // assert(verifier.verify("google.com", session) == true)
        assert(verifier.verify(certs, "google.com", session) == false)
    }

    test("subjectAltName()") {
      // $ cat ./cert.cnf
      // [req]
      // distinguished_name=distinguished_name
      // req_extensions=req_extensions
      // x509_extensions=x509_extensions
      // [distinguished_name]
      // [req_extensions]
      // [x509_extensions]
      // subjectAltName=DNS:bar.com,DNS:baz.com
      //
      // $ openssl req -x509 -nodes -days 36500 -subj '/CN=foo.com' -config ./cert.cnf \
      //     -newkey rsa:512 -out cert.pem
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIBPTCB6KADAgECAgkA7zoHaaqNGHQwDQYJKoZIhvcNAQEFBQAwEjEQMA4GA1UE\n"
          + "AxMHZm9vLmNvbTAgFw0xMDEyMjAxODM5MzZaGA8yMTEwMTEyNjE4MzkzNlowEjEQ\n"
          + "MA4GA1UEAxMHZm9vLmNvbTBcMA0GCSqGSIb3DQEBAQUAA0sAMEgCQQC+gmoSxF+8\n"
          + "hbV+rgRQqHIJd50216OWQJbU3BvdlPbca779NYO4+UZWTFdBM8BdQqs3H4B5Agvp\n"
          + "y7HeSff1F7XRAgMBAAGjHzAdMBsGA1UdEQQUMBKCB2Jhci5jb22CB2Jhei5jb20w\n"
          + "DQYJKoZIhvcNAQEFBQADQQBXpZZPOY2Dy1lGG81JTr8L4or9jpKacD7n51eS8iqI\n"
          + "oTznPNuXHU5bFN0AAGX2ij47f/EahqTpo5RdS95P4sVm\n"
          + "-----END CERTIFICATE-----",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "foo.com", session) == false)
        assert(verifier.verify(certs, "bar.com", session) == true)
        assert(verifier.verify(certs, "baz.com", session) == true)
        assert(verifier.verify(certs, "a.foo.com", session) == false)
        assert(verifier.verify(certs, "quux.com", session) == false)
      }
    }

    test("subjectAltNameWithWildcard()") {
      // $ cat ./cert.cnf
      // [req]
      // distinguished_name=distinguished_name
      // req_extensions=req_extensions
      // x509_extensions=x509_extensions
      // [distinguished_name]
      // [req_extensions]
      // [x509_extensions]
      // subjectAltName=DNS:bar.com,DNS:*.baz.com
      //
      // $ openssl req -x509 -nodes -days 36500 -subj '/CN=foo.com' -config ./cert.cnf \
      //     -newkey rsa:512 -out cert.pem
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIBPzCB6qADAgECAgkAnv/7Jv5r7pMwDQYJKoZIhvcNAQEFBQAwEjEQMA4GA1UE\n"
          + "AxMHZm9vLmNvbTAgFw0xMDEyMjAxODQ2MDFaGA8yMTEwMTEyNjE4NDYwMVowEjEQ\n"
          + "MA4GA1UEAxMHZm9vLmNvbTBcMA0GCSqGSIb3DQEBAQUAA0sAMEgCQQDAz2YXnyog\n"
          + "YdYLSFr/OEgSumtwqtZKJTB4wqTW/eKbBCEzxnyUMxWZIqUGu353PzwfOuWp2re3\n"
          + "nvVV+QDYQlh9AgMBAAGjITAfMB0GA1UdEQQWMBSCB2Jhci5jb22CCSouYmF6LmNv\n"
          + "bTANBgkqhkiG9w0BAQUFAANBAB8yrSl8zqy07i0SNYx2B/FnvQY734pxioaqFWfO\n"
          + "Bqo1ZZl/9aPHEWIwBrxYNVB0SGu/kkbt/vxqOjzzrkXukmI=\n"
          + "-----END CERTIFICATE-----",
      )
      val certs: Array[X509Certificate] = Array()
      for (verifier <- verifiers) {
        assert(verifier.verify(certs, "foo.com", session) == false)
        assert(verifier.verify(certs, "bar.com", session) == true)
        assert(verifier.verify(certs, "a.baz.com", session) == true)
        assert(verifier.verify(certs, "baz.com", session) == false)
        assert(verifier.verify(certs, "a.foo.com", session) == false)
        assert(verifier.verify(certs, "a.bar.com", session) == false)
        assert(verifier.verify(certs, "quux.com", session) == false)
      }
    }

    // BEGIN Android-added: Verify behaviour with top level wildcard SAN.
    test("subjectAltNameWithToplevelWildcard()") {
      // Default OkHostnameVerifier instance should allow SANs which
      // have wildcards for top-level domains.  The strict instance should not.
      //
      // Certificate generated using:-
      //     openssl req -x509 -nodes -days 36500 -subj "/CN=Google Inc" \
      //         -addext "subjectAltName=DNS:*.com" -newkey rsa:512
      val session: SSLSession = newSession(
        "-----BEGIN CERTIFICATE-----\n"
          + "MIIBlTCCAT+gAwIBAgIUe1RB6C61ZW/SEQpKiywSEJOEOUMwDQYJKoZIhvcNAQEL\n"
          + "BQAwFTETMBEGA1UEAwwKR29vZ2xlIEluYzAgFw0xOTExMjExMjE1NTBaGA8yMTE5\n"
          + "MTAyODEyMTU1MFowFTETMBEGA1UEAwwKR29vZ2xlIEluYzBcMA0GCSqGSIb3DQEB\n"
          + "AQUAA0sAMEgCQQCu24jT8hktpvnmcde4dqC6e7G5F4cNNLUFnTi3Ay9BzPH1r7sN\n"
          + "v2lHTIQLKSlvjxa48mpeRBlOjDQigv7c+rfRAgMBAAGjZTBjMB0GA1UdDgQWBBQd\n"
          + "myvYKfluxb0+kNEJoh1ZER2wUTAfBgNVHSMEGDAWgBQdmyvYKfluxb0+kNEJoh1Z\n"
          + "ER2wUTAPBgNVHRMBAf8EBTADAQH/MBAGA1UdEQQJMAeCBSouY29tMA0GCSqGSIb3\n"
          + "DQEBCwUAA0EAK710g2hQpXSmpbOQH4dHG61fkVDtM/kR/4/R61vDDqVkgOuyHqXl\n"
          + "GUZFKHMeOZ8peQLT8b+5ik6pIO7Vu2pF6w==\n"
          + "-----END CERTIFICATE-----\n",
      )
      val certs: Array[X509Certificate] = Array()
      assert(OkHostnameVerifier.INSTANCE.verify(certs, "google.com", session) == true)
      assert(OkHostnameVerifier.STRICT_INSTANCE.verify(certs, "google.com", session) == false)
    }
    // END Android-added: Verify behaviour with top level wildcard SAN.
    // http://b/144694112

    test("verifyAsIpAddress()") {
      // IPv4
      assert(OkHostnameVerifier.verifyAsIpAddress("127.0.0.1") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("1.2.3.4") == true)

      // IPv6
      assert(OkHostnameVerifier.verifyAsIpAddress("::1") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("2001:db8::1") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("::192.168.0.1") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("::ffff:192.168.0.1") == true)
      assert(
        OkHostnameVerifier.verifyAsIpAddress("FEDC:BA98:7654:3210:FEDC:BA98:7654:3210") == true,
      )
      assert(OkHostnameVerifier.verifyAsIpAddress("1080:0:0:0:8:800:200C:417A") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("1080::8:800:200C:417A") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("FF01::101") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("0:0:0:0:0:0:13.1.68.3") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("0:0:0:0:0:FFFF:129.144.52.38") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("::13.1.68.3") == true)
      assert(OkHostnameVerifier.verifyAsIpAddress("::FFFF:129.144.52.38") == true)

      // Hostnames
      assert(OkHostnameVerifier.verifyAsIpAddress("go") == false)
      assert(OkHostnameVerifier.verifyAsIpAddress("localhost") == false)
      assert(OkHostnameVerifier.verifyAsIpAddress("squareup.com") == false)
      assert(OkHostnameVerifier.verifyAsIpAddress("www.nintendo.co.jp") == false)
    }
