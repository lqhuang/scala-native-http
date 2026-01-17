package snhttp.jdk.internal

import java.nio.ByteBuffer
import java.util.function.BiPredicate

import scala.collection.immutable.TreeSet
import scala.util.Properties

import Ordering.comparatorToOrdering
import String.CASE_INSENSITIVE_ORDER

/**
 * To check semantics and default value of various JDK properties, see
 * https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/module-summary.html
 */
object PropertyUtils {

  val strCIOrdering: Ordering[String] = comparatorToOrdering(CASE_INSENSITIVE_ORDER)

  val DEFAULT_DISALLOWED_HEADERS: TreeSet[String] =
    TreeSet.from(
      Seq("connection", "content-length", "expect", "host", "upgrade"),
    )(using strCIOrdering)

  def getRestrictedHeaders(): TreeSet[String] =
    Properties
      .propOrNone("jdk.httpclient.allowRestrictedHeaders")
      .flatMap(opts =>
        val hs = opts.split(",").map(_.trim).toSeq
        if hs.isEmpty
        then None
        else Some(DEFAULT_DISALLOWED_HEADERS -- hs),
      )
      .getOrElse(DEFAULT_DISALLOWED_HEADERS)

  val restrictedHeaders = getRestrictedHeaders()
  def allowedHeadersPredicate(name: String, value: String): Boolean =
    !restrictedHeaders.contains(name.toLowerCase())

    /**
     * default buffer size to use for HTTP requests and responses.
     */
  final val DEFAULT_INTERNAL_BUFFER_SIZE: Int = 64 * 1024; // 64 KB
  val INTERNAL_BUFFER_SIZE =
    Properties
      .propOrNone("jdk.httpclient.bufsize")
      .flatMap(size => size.trim().toIntOption)
      .filter(s => s >= 16 * 1024 && s <= 2 * 1024 * 1024) // [16 KB, 2 MB] inclusive
      .getOrElse(DEFAULT_INTERNAL_BUFFER_SIZE)
  def newFixedByteBuffer() = ByteBuffer.allocate(INTERNAL_BUFFER_SIZE)

  // https://curl.se/libcurl/c/CURLOPT_BUFFERSIZE.html
  final val DEFAULT_RECEIVE_BUFFER_SIZE: Int = 64 * 1024; // 64 KB, default 16 kb in curl
  val RECEIVE_BUFFER_SIZE =
    Properties
      .propOrNone("jdk.httpclient.receiveBufferSize")
      .flatMap(size => size.trim().toIntOption)
      .filter(s => s >= 1 * 1024 && s <= 10 * 1024 * 1024) // [1 kB, 10 MB] inclusive
      .getOrElse(DEFAULT_RECEIVE_BUFFER_SIZE)

  // https://curl.se/libcurl/c/CURLOPT_UPLOAD_BUFFERSIZE.html
  final val DEFAULT_SEND_BUFFER_SIZE: Int = 64 * 1024; // default 64 kb in curl
  val SEND_BUFFER_SIZE =
    Properties
      .propOrNone("jdk.httpclient.sendBufferSize")
      .flatMap(size => size.trim().toIntOption)
      .filter(s => s >= 16 * 1024 && s <= 2 * 1024 * 1024) // [16 KB, 2 MB] inclusive
      .getOrElse(DEFAULT_SEND_BUFFER_SIZE)

  final val DEFAULT_SESSION_CACHE_SIZE = 20480
  val SESSION_CACHE_SIZE: Int =
    Properties
      .propOrNone("jdk.httpclient.sessionCacheSize")
      .flatMap(size => size.trim().toIntOption)
      .getOrElse(20480)

}
