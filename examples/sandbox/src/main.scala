import javax.net.ssl.SSLContext

@main
def main(): Unit = {
  SSLContext.getDefault()
  println("Hello, world!")
}
