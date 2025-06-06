package snhttp.jdk

import java.util.function.BiPredicate

import scala.collection.immutable.TreeSet

import Ordering.comparatorToOrdering
import String.CASE_INSENSITIVE_ORDER

object PropertyUtils {

  val stringCI: Ordering[String] = comparatorToOrdering(CASE_INSENSITIVE_ORDER)

  val defaultDisallowedHeaders: TreeSet[String] = TreeSet.from(
    Seq("connection", "content-length", "expect", "host", "upgrade"),
  )(using stringCI)

  def getRestrictedHeaders(): TreeSet[String] = {
    val allowRestrictedHeaders =
      System.getProperty("jdk.httpclient.allowRestrictedHeaders")

    if allowRestrictedHeaders == null
    then defaultDisallowedHeaders
    else {
      val hs = allowRestrictedHeaders.split(",").map(_.trim).toSeq
      if hs.isEmpty
      then defaultDisallowedHeaders
      else defaultDisallowedHeaders -- hs
    }
  }

  val restrictedHeaders = getRestrictedHeaders()
  def allowedHeadersPredicate(name: String, value: String): Boolean =
    !restrictedHeaders.contains(name.toLowerCase())
}
