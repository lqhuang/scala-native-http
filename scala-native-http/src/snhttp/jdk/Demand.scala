package snhttp.jdk

import java.util.concurrent.atomic.AtomicLong

import scala.annotation.tailrec

final class Demand() {
  private[jdk] val ref = AtomicLong(0L)

  override def toString(): String = ref.get().toString

  def fulfilled: Boolean = ref.get() == 0L

  def reset(): Unit = ref.set(0L)

  def get(): Long = ref.get()

  def increase(n: Long): Boolean =
    require(n > 0, s"number $n can not be zero or negative")
    val prev = ref.getAndAccumulate(n, (p, i) => safeAdd(p, i))
    prev == 0

  def increaseIfFulfilled(): Boolean =
    ref.compareAndSet(0L, 1L)

  def decreaseAndGet(n: Long): Long =
    require(n > 0, s"number $n to decrease can not be zero or negative")

    @inline @tailrec def loop(): Long =
      val curr = ref.get()
      val willDec = curr.min(n)
      val after = safeSubtract(curr, willDec)

      if ref.compareAndSet(curr, after)
      then willDec
      else loop()

    loop()

  def decrease(): Boolean =
    decreaseAndGet(1L) == 1L

  def decreaseUntilFulfilled(): Boolean =
    @inline @tailrec def loop(): Boolean =
      if fulfilled
      then true
      else {
        decrease()
        loop()
      }

    loop()

  @inline private def safeAdd(a: Long, b: Long): Long =
    val r = a + b
    if ((a ^ r) & (b ^ r)) < 0
    then Long.MaxValue
    else r

  @inline private def safeSubtract(a: Long, b: Long): Long =
    val r = a - b
    if ((a ^ b) & (a ^ r)) < 0
    then Long.MinValue
    else r
}
object Demand {
  def apply(): Demand = new Demand()
}
