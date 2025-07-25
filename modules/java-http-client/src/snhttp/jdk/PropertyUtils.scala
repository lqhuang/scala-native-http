package snhttp.jdk

import java.nio.ByteBuffer
import java.util.function.BiPredicate

import scala.collection.immutable.TreeSet

import Ordering.comparatorToOrdering
import String.CASE_INSENSITIVE_ORDER

object PropertyUtils {

  val stringCI: Ordering[String] = comparatorToOrdering(CASE_INSENSITIVE_ORDER)

  val DEFAULT_DISALLOWED_HEADERS: TreeSet[String] = TreeSet.from(
    Seq("connection", "content-length", "expect", "host", "upgrade"),
  )(using stringCI)

  def getRestrictedHeaders(): TreeSet[String] =
    val allowRestrictedHeaders =
      System.getProperty("jdk.httpclient.allowRestrictedHeaders")
    if allowRestrictedHeaders == null
    then DEFAULT_DISALLOWED_HEADERS
    else {
      val hs = allowRestrictedHeaders.split(",").map(_.trim).toSeq
      if hs.isEmpty
      then DEFAULT_DISALLOWED_HEADERS
      else DEFAULT_DISALLOWED_HEADERS -- hs
    }

  val restrictedHeaders = getRestrictedHeaders()
  def allowedHeadersPredicate(name: String, value: String): Boolean =
    !restrictedHeaders.contains(name.toLowerCase())

    /**
     * default buffer size to use for HTTP requests and responses.
     */
  final val DEFAULT_BUFSIZE: Int = 64 * 1024; // 64 KB

  val BUFFER_SIZE =
    System
      .getProperty("jdk.httpclient.bufsize")
      .toIntOption
      .flatMap(size =>
        if size >= 16 * 1024 && size <= 2 * 1024 * 1024 // [16 KB, 2 MB] inclusive
        then Some(size)
        else None,
      )
      .getOrElse(DEFAULT_BUFSIZE)
  def newFixedByteBuffer() = ByteBuffer.allocate(BUFFER_SIZE)

}
