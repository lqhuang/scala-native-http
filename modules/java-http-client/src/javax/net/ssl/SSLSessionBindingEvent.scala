package javax.net.ssl

import java.util.EventObject

class SSLSessionBindingEvent(session: SSLSession, name: String) extends EventObject(session) {
  def getSession(): SSLSession = session

  def getName(): String = name
}
