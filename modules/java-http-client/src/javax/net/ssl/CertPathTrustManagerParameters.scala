package javax.net.ssl

import java.security.cert.CertPathParameters
import java.util.Objects.requireNonNull

class CertPathTrustManagerParameters(parameters: CertPathParameters)
    extends ManagerFactoryParameters:

  def getParameters(): CertPathParameters =
    requireNonNull(parameters)
    parameters
