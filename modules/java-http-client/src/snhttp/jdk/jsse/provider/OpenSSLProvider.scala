package snhttp.jdk.jsse.provider

import java.security.Provider
import java.util.Objects.requireNonNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.{List as JList, Map as JMap, Set as JSet}

class OpenSSLProvider(
    private val name: String = "scala-native-ssl",
    private val versionStr: String = "0.1",
    private val info: String = "Java Cryptography Provider for Scala Native using OpenSSL",
) extends Provider(name, versionStr, info):

  private val initialized: AtomicBoolean = new AtomicBoolean(false)

  private val services: JMap[OpenSSLProvider.ServiceKey, Provider.Service] =
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
    if (!JSSEServiceType.contains(svc))
      throw new IllegalArgumentException(
        s"Unknown service: ${svc}, use one of ${JSSEServiceType.values.mkString(", ")}",
      )

    services.get(
      OpenSSLProvider.ServiceKey(svc, algorithm),
    )
  }

  override def getServices(): JSet[Provider.Service] =
    JSet.of(
      services.values().toArray().map(_.asInstanceOf[Provider.Service]): _*,
    )

  override protected def putService(svc: Provider.Service): Unit =
    services.put(
      OpenSSLProvider.ServiceKey(svc.getType(), svc.getAlgorithm()),
      svc,
    ): Unit

  // private def putAliasService(svc: Provider.Service, alias: String): Unit =
  //   services.put(
  //     OpenSSLProvider
  //       .ServiceKey(svc.getType(), alias, Some(svc.getAlgorithm())),
  //     svc,
  //   ): Unit

  override protected def removeService(s: Provider.Service): Unit =
    services.remove(
      OpenSSLProvider.ServiceKey(s.getType(), s.getAlgorithm()),
    ): Unit

  private def setup(): Unit =
    if (!initialized.compareAndExchange(false, true)) {
      putService(
        ProvService(
          this,
          JSSEServiceType.SSLContext,
          "Default",
          "snhttp.jdk.net.ssl.SSLContextSpiImpl",
          JList.of(),
          JMap.of(),
        ),
      )
      putService(
        ProvService(
          this,
          JSSEServiceType.SSLContext,
          "TLSv1.3",
          "snhttp.jdk.net.ssl.SSLContextSpiImpl",
          JList.of(),
          JMap.of(),
        ),
      )
      putService(
        ProvService(
          this,
          JSSEServiceType.SSLContext,
          "TLSv1.2",
          "snhttp.jdk.net.ssl.SSLContextSpiImpl",
          JList.of(),
          JMap.of(),
        ),
      )
      putService(
        ProvService(
          this,
          JSSEServiceType.SSLContext,
          "TLS",
          "snhttp.jdk.net.ssl.SSLContextSpiImpl",
          JList.of(),
          JMap.of(),
        ),
      )

    }

object OpenSSLProvider:

  lazy val defaultInstance = new OpenSSLProvider()

  private case class ServiceKey(
      svc: String,
      algorithm: String,
      origAlgorithm: Option[String] = None,
  ) {
    requireNonNull(svc)
    requireNonNull(algorithm)
    require(svc.nonEmpty && algorithm.nonEmpty)
    require(
      origAlgorithm.isEmpty || origAlgorithm.get.nonEmpty,
      "origAlgorithm name for an aliases, if provided, must be non-empty",
    )

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
