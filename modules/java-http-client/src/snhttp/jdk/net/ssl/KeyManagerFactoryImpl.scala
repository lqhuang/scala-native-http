package snhttp.jdk.net.ssl

import java.security.{KeyStore, Provider}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.{Collections, ArrayList}
import javax.net.ssl.{ManagerFactoryParameters, KeyManagerFactory, KeyManagerFactorySpi, KeyManager}

import com.github.lolgab.scalanativecrypto.crypto.OpenSSLKeyStore

private[snhttp] class KeyManagerFactorySpiImpl extends KeyManagerFactorySpi:

  private var _km: X509KeyManagerImpl = _
  private val _initialized = new AtomicBoolean(false)

  def engineInit(ks: KeyStore, password: Array[Char]): Unit = {
    require(ks != null, "KeyStore must not be null")

    if !_initialized.compareAndExchange(false, true)
    then {
      if ks.isInstanceOf[OpenSSLKeyStore]
      then //
        _km = X509KeyManagerImpl(ks.asInstanceOf[OpenSSLKeyStore], password)
      else
        throw new IllegalArgumentException(
          s"Unsupported KeyStore Type: ${ks.getClass()}. Only OpenSSLKeyStore is supported.",
        )
    } //
    else //
      throw new IllegalStateException("KeyManagerFactory is already initialized")
  }

  def engineInit(spec: ManagerFactoryParameters): Unit =
    ???

  def engineGetKeyManagers(): Array[KeyManager] =
    if _initialized.get()
    then Array(_km)
    else throw new IllegalStateException("KeyManagerFactory is not initialized")

private[snhttp] class KeyManagerFactoryImpl(provider: Provider, algorithm: String)
    extends KeyManagerFactory(KeyManagerFactorySpiImpl(), provider, algorithm)
