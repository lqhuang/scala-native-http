/// Inspired and modified from
/// <https://github.com/lolgab/scala-native-crypto/blob/main/scala-native-crypto/src/java/com/github/lolgab/scalanativecrypto/internal/CtxFinalizer.scala>
package snhttp.utils

import java.lang.ref.Cleaner

import scala.scalanative.meta.LinktimeInfo
import scala.scalanative.unsafe.Ptr
import java.lang.ref.Cleaner.Cleanable

object PointerCleaner:
  private val cleaner: Cleaner = Cleaner.create()

  def register[T](
      owner: AnyRef,
      ptr: Ptr[T],
      cleanFunction: Ptr[T] => Unit,
  ): Cleanable =
    cleaner.register(
      owner,
      new Runnable {
        override def run(): Unit = if (ptr != null) cleanFunction(ptr)
      },
    )

end PointerCleaner
