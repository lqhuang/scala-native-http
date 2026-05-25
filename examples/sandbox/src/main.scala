import javax.net.ssl.SSLContext

@main
def main(): Unit = {
  val sslContext: SSLContext = SSLContext.getDefault()
  println("Hello, world!")
}
