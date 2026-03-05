/** SPDX-License-Identifier: Apache-2.0 */
package snhttp.jdk.jsse.provider

import java.security.Provider
import java.util.Objects.requireNonNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.{List as JList, Map as JMap, Set as JSet}

class OpenSSLProvider(
    private val name: String = "scala-native-openssl",
    private val versionStr: String = "0.1",
    private val info: String =
      "Java Secure Socket Extension Provider for Scala Native using OpenSSL",
) extends Provider(name, versionStr, info):

  import OpenSSLProvider.ServiceKey

  private val initialized: AtomicBoolean = new AtomicBoolean(false)

  private val services: JMap[ServiceKey, ProvService] =
    new ConcurrentHashMap()

  setup()

  override def configure(configArg: String): Provider =
    throw new UnsupportedOperationException(
      "Dynamic configuration is not supported yet",
    )

  override def isConfigured(): Boolean =
    initialized.getAcquire()

  override def getName(): String =
    name

  override def getVersionStr(): String =
    versionStr

  override def getInfo(): String =
    info

  override def getService(
      svc: String,
      algorithm: String,
  ): Provider.Service = {
    if (!JSSEServiceType.names.contains(svc))
      throw new IllegalArgumentException(
        s"Unknown service: ${svc}, use one of ${JSSEServiceType.names.toArray.mkString(", ")}",
      )

    services.get(ServiceKey(svc, algorithm))
  }

  override def getServices(): JSet[Provider.Service] =
    JSet.of(
      services.values().toArray().map(_.asInstanceOf[Provider.Service]): _*,
    )

  override protected def putService(svc: Provider.Service): Unit =
    throw new UnsupportedOperationException(
      "Please use putProvService instead of putService to add services to the provider",
    )

  override protected def removeService(s: Provider.Service): Unit =
    ???

  private def putProvService(provSvc: ProvService): Unit =
    services.put(ServiceKey(provSvc.getType(), provSvc.getAlgorithm()), provSvc): Unit
    provSvc.getAliases().forEach { alias =>
      services.put(
        ServiceKey(provSvc.getType(), alias),
        provSvc,
      ): Unit
    }

  private def setup(): Unit =
    if (!initialized.compareAndExchange(false, true)) {
      for (algorithm <- Set("TLSv1.3", "TLSv1.2", "TLS", "Default"))
        putProvService(
          ProvService(
            this,
            "SSLContext",
            algorithm,
            "snhttp.jdk.net.ssl.SSLContextImpl",
            JList.of(),
            JMap.of(),
          ),
        )

    }

object OpenSSLProvider:

  lazy val defaultInstance = new OpenSSLProvider()

  private case class ServiceKey(svc: String, algorithm: String) {
    requireNonNull(svc)
    requireNonNull(algorithm)
    require(svc.nonEmpty && algorithm.nonEmpty)

    override def toString(): String = s"${svc}.${algorithm}"

    override def hashCode(): Int =
      31 * svc.toUpperCase().hashCode() + algorithm.toUpperCase().hashCode()

    override def equals(obj: Any): Boolean = {
      if (!obj.isInstanceOf[ServiceKey]) return false

      val other = obj.asInstanceOf[ServiceKey]
      svc.equalsIgnoreCase(other.svc) && algorithm.equalsIgnoreCase(other.algorithm)
    }
  }

end OpenSSLProvider
