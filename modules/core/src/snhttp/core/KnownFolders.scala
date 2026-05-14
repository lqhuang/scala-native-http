package snhttp.core

object KnownFolders:

  val CA_CERTIFICATES =
    if (Platform.isLinux)
      "/usr/local/share/ca-certificates/ca-certificates.crt"
    else if (Platform.isMacOS)
      "/etc/ssl/certs/cert.pem"
    else if (Platform.isWindows) {
      // TODO: verify in a real machine
      // Ref: https://learn.microsoft.com/en-us/windows/win32/seccrypto/system-store-locations
      ???
    } else
      throw new RuntimeException(
        s"Unsupported platform: ${Platform.OS_NAME}. Please specify the path to CA certificates using the `javax.net.ssl.trustStore` system property.",
      )
