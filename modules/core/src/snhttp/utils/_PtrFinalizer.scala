package java.snhttp.utils

import java.lang.ref.{WeakReference, WeakReferenceRegistry}

import scala.scalanative.meta.LinktimeInfo
import scala.scalanative.unsafe.Ptr

/// Inspired and modified from
/// <https://github.com/lolgab/scala-native-crypto/blob/main/scala-native-crypto/src/java/com/github/lolgab/scalanativecrypto/internal/CtxFinalizer.scala>
final class PtrFinalizer[T](
    weakRef: WeakReference[?],
    private var ptr: Ptr[T],
    finalizationFunction: Ptr[T] => Unit,
):
  WeakReferenceRegistry.addHandler(weakRef, handler)

  def handler(): Unit =
    if (ptr != null)
      finalizationFunction(ptr)
      ptr = null.asInstanceOf[Ptr[T]]

object PtrFinalizer:

  def fromWeakRef[T](
      weakRef: WeakReference[?],
      ptr: Ptr[T],
      finalizationFunction: Ptr[T] => Unit,
  ): PtrFinalizer[T] =
    new PtrFinalizer(weakRef, ptr, finalizationFunction)

  def apply[T](
      owner: AnyRef,
      ptr: Ptr[T],
      finalizationFunction: Ptr[T] => Unit,
  ): PtrFinalizer[T] =
    if (owner == null)
      throw new NullPointerException("owner cannot be null")

    if (!LinktimeInfo.isWeakReferenceSupported)
      throw new RuntimeException(
        "Weak Reference style finalization is not supported. Consider using immix or commix GC, otherwise this will leak memory.",
      )

    val wr = new WeakReference(owner)
    new PtrFinalizer(wr, ptr, finalizationFunction)

end PtrFinalizer
