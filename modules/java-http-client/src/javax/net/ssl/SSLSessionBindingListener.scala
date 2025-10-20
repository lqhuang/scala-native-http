package javax.net.ssl

import java.util.EventListener

trait SSLSessionBindingListener extends EventListener {
  def valueBound(event: SSLSessionBindingEvent): Unit

  def valueUnbound(event: SSLSessionBindingEvent): Unit
}
